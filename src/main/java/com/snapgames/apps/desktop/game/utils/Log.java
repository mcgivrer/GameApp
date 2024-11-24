package com.snapgames.apps.desktop.game.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A logging utility class that provides methods for logging messages at different levels
 * (DEBUG, INFO, WARN, ERROR) to the console.
 *
 * @author Frédéric Delorme
 * @since 1.0.2
 */
public class Log {

    /**
     * Filtering debug information output on console based on debug info level.
     */
    private static String loggerFilter = "ERROR,WARN,INFO";

    /**
     * Logs a message to the console at a specified logging level with optional arguments.
     *
     * @param level   the logging level (e.g., DEBUG, INFO, WARN, ERROR)
     * @param message the message to be logged
     * @param args    optional arguments to format the message
     */
    public static void log(String level, String message, Object... args) {
        if (loggerFilter.contains(level)) {
            String dateFormatted = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now());
            System.out.printf(dateFormatted + "|" + level + "|" + message + "%n", args);
        }
    }

    /**
     * Logs a message at the DEBUG level.
     *
     * @param message the message to be logged
     * @param args    optional arguments to format the message
     */
    public static void debug(String message, Object... args) {
        log("DEBUG", message, args);
    }

    /**
     * Logs a message at the INFO level.
     *
     * @param message the message to be logged
     * @param args    optional arguments to format the message
     */
    public static void info(String message, Object... args) {
        log("INFO", message, args);
    }

    /**
     * Logs a message at the WARN level.
     *
     * @param message the message to be logged
     * @param args    optional arguments to format the message
     */
    public static void warn(String message, Object... args) {
        log("WARN", message, args);
    }

    /**
     * Logs a message at the ERROR level.
     *
     * @param message the message to be logged
     * @param args    optional arguments to format the message
     */
    public static void error(String message, Object... args) {
        log("ERROR", message, args);
    }


    public static void setLoggerFilter(String filter) {
        loggerFilter = filter;
    }
}

