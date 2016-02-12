package util;


import java.util.HashMap;
import java.util.Map;

public class logger {

    public static Map<String, LEVEL> control = new HashMap();
    public static LEVEL defaultLevel = LEVEL.DEBUG;
    private static String stringFormat = "[%s] %t35 %s";

    static {
        stringFormat = "[%s] %t35 %s";
    }

    public static LEVEL getLevel(String className) {
        LEVEL result = (control.get(className));
        if (result == null) {
            return defaultLevel;
        }
        return result;
    }

    public static boolean shouldOut(LEVEL level, String className1) {
        LEVEL classLevel = getLevel(className1);
        //in INFO, get DEBUG
        // in 0 , get 1
        return (classLevel.ordinal() >= level.ordinal());
    }

    private static String className;
    public static String lastMsg;
    public static String lassClass;

    public logger(Class inputName) {
        String temp = inputName.toString();
        this.className = inputName.getSimpleName();
    }

    public static logger getLogger(Class className) {
        return new logger(className);
    }

    public void debug(String msg) {
        record(className, msg);
        if (shouldOut(LEVEL.DEBUG, className)) {
//            Logger.debug(strings.format(stringFormat, className, msg));
        }
    }

    private static void record(String className, String msg) {
        lastMsg = msg;
        lassClass = className;
    }

    public void error(String msg) {
        record(className, msg);
        if (shouldOut(LEVEL.ERROR, className)) {
//            Logger.error(strings.format(stringFormat, className, msg));
        }
    }

    public void error(String msg, String... arg) {
        if (shouldOut(LEVEL.ERROR, className)) {
            error(strings.format(msg, arg));
        }
    }

    public void error(String msg, Object... arg) {
        if (shouldOut(LEVEL.ERROR, className)) {
            info(strings.format(msg, arg));
        }
    }

    public void info(String msg, Object... arg) {
        if (shouldOut(LEVEL.INFO, className)) {
            if (!msg.endsWith(" ")) {
                info(strings.format(msg + " ", arg));
            } else {
                info(strings.format(msg, arg));
            }
        }
    }

    public void debug(String msg, Object... arg) {
        if (shouldOut(LEVEL.DEBUG, className)) {
            debug(strings.format(msg, arg));
        }
    }

    public static void info(String msg) {
        record(className, msg);
        if (shouldOut(LEVEL.INFO, className)) {
//            Logger.info(" " + strings.format(stringFormat, className, msg));
        }
    }

    public void trace(String msg, Object... arg) {
        if (shouldOut(LEVEL.TRACE, className)) {
            debug(strings.format(msg, arg));
        }
    }


    public void warn(String msg) {
        record(className, msg);
        if (shouldOut(LEVEL.TRACE, className)) {
//            Logger.warn(" " + strings.format(stringFormat, className, msg));
        }
    }

    public void warn(String msg, Object... arg) {
        if (shouldOut(LEVEL.TRACE, className)) {
            warn(strings.format(msg, arg));
        }
    }

}