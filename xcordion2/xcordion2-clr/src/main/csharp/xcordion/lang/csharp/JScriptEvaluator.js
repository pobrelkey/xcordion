package robordion.lang.jscript
{
   class Evaluator
   {
      public function Eval(__expression : String) : Object 
      { 
         return eval(__expression); 
      }
   }
}
