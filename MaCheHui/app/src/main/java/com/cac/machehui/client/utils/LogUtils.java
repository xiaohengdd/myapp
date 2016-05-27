package com.cac.machehui.client.utils;

import android.util.Log;

public class LogUtils {
    private static int log_level = 4;
    /**verbose级别*/
    private static int log_level_v = 0;
    /**debug级别*/
    private static int log_level_d = 1;
    /**info级别*/
    private static int log_level_i = 2;
    /**warn级别*/
    private static int log_level_w = 3;
    /**error级别*/
    private static int log_level_e = 4;

    /**
     * 打印异常信息
     *
     * @param clazz
     * @param message
     */
    public static void logError(Class clazz, Object message) {
        logError(clazz.toString(), message);
    }

    /**
     * 打印异常信息
     *
     * @param clazz
     * @param message
     */
    public static void logError(String tag, Object message) {
        if (log_level <= log_level_e) {
            Log.e(tag, "ERROR-->" + message);
        }
    }

    /**
     * 打印开发过程中的debug信息
     *
     * @param clazz
     * @param message
     */
    public static void logDebug(Class clazz, Object message) {
        logDebug(clazz.toString(),message);
    }
    /**
     * 打印开发过程中的debug信息
     *
     * @param tag
     * @param message
     */
    public static void logDebug(String tag, Object message) {

        if (log_level <= log_level_d) {
            Log.d(tag, "DEBUG-->" + message);
        }
    }

    /**
     * 打印开发过程中的verbose信息
     *
     * @param clazz
     * @param message
     */
    public static void logVerbose(Class clazz, Object message) {
        logVerbose(clazz.toString(),message);
    }
    /**
     * 打印开发过程中的info信息
     *
     * @param tag
     * @param message
     */
    public static void logVerbose(String tag, Object message) {

        if (log_level <= log_level_v) {
            Log.v(tag, "VERBOSE-->" + message);
        }
    }
    /**
     * 打印开发过程中的info信息
     *
     * @param clazz
     * @param message
     */
    public static void logInfo(Class clazz, Object message) {
        logInfo(clazz.toString(),message);
    }
    /**
     * 打印开发过程中的info信息
     *
     * @param tag
     * @param message
     */
    public static void logInfo(String tag, Object message) {

        if (log_level <= log_level_i) {
            Log.i(tag, "INFO-->" + message);
        }
    }
    /**
     * 打印开发过程中的warning信息
     *
     * @param clazz
     * @param message
     */
    public static void logWarn(Class clazz, Object message) {
        logWarn(clazz.toString(),message);
    }
    /**
     * 打印开发过程中的warning信息
     *
     * @param tag
     * @param message
     */
    public static void logWarn(String tag, Object message) {

        if (log_level <= log_level_w) {
            Log.w(tag, "WARNING-->" + message);
        }
    }
}
