package prototype.commodities;

public class Debug {
    public static boolean DEBUG = false;

    public static void println(String co, Object o) {
        if (DEBUG) {
            println(co + " " + o);
        }
    }
    public static void println(Object co, Object o) {
        if (DEBUG) {
            println(co.getClass().getName() + " " + o);
        }
    }
    public static void println(Object o) {
        if (DEBUG) {
            System.out.println(o);
        }
    }
    public static void print(String co, Object o) {
        if (DEBUG) {
            print(co + " " + o);
        }
    }
    public static void print(Object co, Object o) {
        if (DEBUG) {
            print(co.getClass().getName() + " " + o);
        }
    }
    public static void print(Object o) {
        if (DEBUG) {
            System.out.print(o);
        }
    }

    public static void println(boolean debug, String co, Object o) {
        if (DEBUG || debug) {
            println(debug, co + " " + o);
        }
    }
    public static void println(boolean debug, Object co, Object o) {
        if (DEBUG || debug) {
            println(debug, co.getClass().getName() + " " + o);
        }
    }
    public static void println(boolean debug, Object o) {
        if (DEBUG || debug) {
            System.out.println(o);
        }
    }
    public static void print(boolean debug, String co, Object o) {
        if (DEBUG || debug) {
            print(debug, co + " " + o);
        }
    }
    public static void print(boolean debug, Object co, Object o) {
        if (DEBUG || debug) {
            print(debug, co.getClass().getName() + " " + o);
        }
    }
    public static void print(boolean debug, Object o) {
        if (DEBUG || debug) {
            System.out.print(o);
        }
    }
}
