#!/usr/bin/env ruby

#
#  Tool to translate limited-dialect Java 1.5 into limited-dialect C# 2.0.
#
#  THIS IS A BIG STONKING HACK, AND NOT INTENDED AS A GENERAL-PURPOSE TRANSLATOR.
#


require 'ftools'


# scan a directory tree recursively, translating Java source and copying resources as we go
def scan_dir(from_dir, to_dir)
  Dir.foreach(from_dir) do |file|
    from_path = from_dir + File::SEPARATOR + file
    to_path = to_dir + File::SEPARATOR + file
    if file =~ /^\./
      next
    elsif FileTest.directory?(from_path)
      scan_dir(from_path, to_path) 
    elsif file =~ /\.java$/
      File.makedirs(to_dir)
      output_file = to_path.sub(/\.java$/, '.cs')
      print "Translating #{from_path} to #{output_file}\n"
      transmogrify(from_path, output_file)
    else
      File.makedirs(to_dir)
      print "Copying #{from_path} to #{to_path}\n"
      File.copy(from_path, to_path)
    end
  end
end

# translate Java type constraint syntax into C# syntax in method/class declarations
def chew_constraints(some_code, nuke_where_clause=false) 
  return some_code if some_code !~ /<((?:[^>]|<.*?>)+\sextends\s(?:[^>]|<.*?>)+)>/
  constraints = Hash.new
  some_code.sub!(/<((?:[^>]|<.*?>)+\sextends\s(?:[^<>]|<.*?>)+)>/) do |x|
    x.gsub(/\b(\w+)\s+extends\s+(\w+(?:\s*<(?:[^<>]|<.*?>)+>)?)/) do 
      constraints[$1] = $2
      $1
    end
  end
  return some_code if constraints.empty?
  where_clause = constraints.keys.collect{|x| "where #{x} : class, #{constraints[x]}"}.join(" ")
  nuke_where_clause ? some_code : some_code.sub(/([\{;]\s*$)/, " #{where_clause} \\1")
end

TYPE_MAP = {
	'boolean'   => 'bool',
	'Boolean'   => 'bool?',
	'Integer'   => 'int?',
	'Throwable' => 'Exception',
#	'Iterable'  => 'IEnumerable',
    'Class'     => 'Type',

    'List'      => 'ICompatibleList',
	'Map'       => 'ICompatibleHash',
	'ArrayList' => 'SimpleList',
	'HashMap'   => 'SimpleMap'
}
TYPE_REGEX = Regexp.new('\b(' + TYPE_MAP.keys.join('|') + ')\b')

# a poor simulacrum of a Java-to-C# translator
def transmogrify(java_path, cs_path)
  cs_lines = []
  namespace = nil
  imports = ['System', 'System.Collections.Generic', 'xcordion.util']
  is_override = false
  is_interface = false
  File.open(java_path) do |input|
    input.each_line do |java_line|
      java_line.gsub!(/[\015\012]+$/, '')
      java_line.gsub!(TYPE_REGEX) {|x| TYPE_MAP[$1]}
      java_line.gsub!(/\bsuper\./,          'base.')
      java_line.gsub!(/\bequals\s*\(/,      'Equals(')
      java_line.gsub!(/\btoString\s*\(/,    'ToString(')
      java_line.gsub!(/\btoLowerCase\s*\(/, 'ToLower(')
      java_line.gsub!(/\bindexOf\s*\(/,     'IndexOf(')
      java_line.gsub!(/\bgetClass\s*\(/,    'GetType(')
      java_line.gsub!(/\b(\w+)\.class\b/,   'typeof(\1)')

      if java_line =~ /^package\s+(.*?);/
        namespace = $1

      elsif java_line =~ /^import\s+(.*?)(?:\.[^\.]+)?;/
        package = $1
        imports << package if !imports.include?(package) && package =~ /^xcordion\./

      elsif java_line =~ /^(\s*)((?:(?:public|private|protected|abstract|final|static)\s*)*(?:class|interface|enum))\s+(\w+(?:<.*?>)?)\s*(?:extends\s*(.*?)\s*)?(?:implements\s*(.*?)\s*)?((?:\{.*)?$)/
        # class declaration
        tabs, what, class_name, superclass, superinterfaces, brace = $1, $2, $3, $4, $5, $6       
        supers = [superclass, superinterfaces].grep(/\w/).join(', ')
        supers = ": #{supers}" if supers =~ /\w/
        cs_lines << chew_constraints(tabs + "#{what} #{class_name} #{supers} #{brace}".strip.sub(/\s+/, ' '))
        is_interface = (what =~ /\binterface\b/)

      elsif java_line =~ /^(\s*)for\s*\(\s*(\w+(?:<.*?>)?)\s*(\w+)\s*:\s*(.*?)\s*\)(\s*(?:\{.*)?$)/
        # Java 1.5-style iteration over an Iterable - presume expression will be Enumerable in C#
        tabs, type, var, expression, brace = $1, $2, $3, $4, $5
        cs_lines << tabs + "foreach (#{type} #{var} in #{expression})#{brace}".strip.sub(/\s+/, ' ')
        
      elsif java_line =~ /^\s*(super|this)\((.*)\);\s*$/
        # invocation of other constructor - go back and patch previous line
        base_or_this, params = $1, $2
        base_or_this = 'base' if base_or_this == 'super'
        cs_lines.last.sub!(/(\{\s*$)/, ": #{base_or_this}(#{params}) \\1")
             
      elsif java_line =~ /^\s*\@(\w+)/
        # an annotation - don't emit this line to C#
        is_override = is_override || ($1 == 'Override')
              
      elsif java_line =~ /^(\s*)((?:(?:public|private|protected|abstract|final|static)\s+)*)(<.*?>\s+)?(\w.*?)\(\s*((?:(?:\w+(?:\s*<.*>)?\s+\w+\s*),\s*)*(?:\w+(?:\s*<.*>)?\s+\w+)?)\s*\)(\s*(?:[\{;].*)?$)/
        # method declaration
        tabs, modifiers, type_param, signature, params, brace = $1, $2, $3, $4, $5, $6

		if (brace !~ /\{/ && modifiers !~ /\w/ && type_param !~ /\w/) || signature =~ /\bfor\b/
			# false alarm! (regex has to be slightly overzealous to work on interfaces)
			cs_lines << java_line
		else
			# munge method modifiers to match C# method visibility...
			# all method decs are either static, private/final, abstract, virtual or override
			if !modifiers.sub!(/final/, '') && modifiers !~ /(static|private|abstract)/ && signature.strip =~ /\s/ && !is_interface
			  modifiers += is_override ? " override " : " virtual "
			end
			cs_lines << chew_constraints(tabs + "#{modifiers}#{signature}#{type_param}(#{params})#{brace}".strip.sub(/\s+/, ' '), is_override)
			is_override = false
		end

      else
      	java_line.gsub!(/\b(final\s+static|static\s+final)\b/, 'const')
      	java_line.gsub!(/\bfinal\b/, 'readonly')
      	java_line.gsub!(/\binstanceof\b/, 'is')
        cs_lines << java_line

      end
    end
  end
    
  crlf = "\015\012"  # ewww...
  File.open(cs_path, 'w', 0644) do |output|
    output.binmode
    output.print(imports.collect{|x| "using #{x};"}.join(crlf))
    output.print("#{crlf}#{crlf}#{crlf}namespace #{namespace} {#{crlf}\t")
    output.print(cs_lines.join("#{crlf}\t"))
    output.print("#{crlf}#{crlf}}#{crlf}")
  end
end


# main loop
scan_dir(ARGV[0], ARGV[1])
