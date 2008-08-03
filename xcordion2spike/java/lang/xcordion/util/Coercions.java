package xcordion.util;

import ognl.OgnlException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.StringWriter;
import java.io.PrintWriter;

public class Coercions {

	public static Iterator toIterator(Object x) {
		if (x == null) {
			return Collections.EMPTY_SET.iterator();
		} else if (x instanceof Iterable) {
			return ((Iterable) x).iterator();
		} else if (x instanceof Object[]) {
			return Arrays.asList((Object[]) x).iterator();
		} else if (x instanceof Iterator) {
			return (Iterator) x;
		} else {
			return Collections.singletonList(x).iterator();
		}
	}

	public static <T> Iterable<T> toIterable(final Iterator<T> iterator) {
		return new Iterable<T>() {
			public Iterator<T> iterator() {
				return iterator;
			}
		};
	}
	
	public static Iterable toIterable(final Object x) {
		if (x == null) {
			return Collections.EMPTY_SET;
		} else if (x instanceof Iterable) {
			return (Iterable) x;
		} else if (x instanceof Object[]) {
			return Arrays.asList((Object[]) x);
		} else if (x instanceof Iterator) {
			return toIterable((Iterator) x);
		} else {
			return Collections.singletonList(x);
		}
	}

	public static Boolean toBoolean(Object x) {
		if (x == null || x instanceof Boolean) {
			return (Boolean) x;
        } else if (x.toString().equalsIgnoreCase("true")) {
            return true;
        } else if (x.toString().equalsIgnoreCase("false")) {
            return false;
        } else {
            return null;
		}
	}

    public static String toStackTrace(Throwable t) {
        if (t instanceof RuntimeException && t.getCause() instanceof OgnlException) {
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }

    public static String toExceptionMessage(Throwable e) {
        return e.getMessage();
    }

    private static final Pattern DASH_CHARACTER = Pattern.compile("-([a-z])");

    public static String camelCase(String s) {
        if (s.indexOf('-') == -1) {
            return s;
        }

        StringBuffer result = new StringBuffer();
        Matcher m = DASH_CHARACTER.matcher(s);
        while (m.find()) {
            m.appendReplacement(result, m.group(1).toUpperCase());
        }
        m.appendTail(result);
        return result.toString();
    }
}
