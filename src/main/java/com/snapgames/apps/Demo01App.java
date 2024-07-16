package com.snapgames.apps;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Main class for Project Demo01
 *
 * @author Frédéric Delorme frederic.delorme@gmail.com
 * @since 1.0.0
 */
public class Demo01App {
    private ResourceBundle messages = ResourceBundle.getBundle("i18n/messages");
    private Properties config = new Properties();

    private static boolean exit = false;
    private static int debug = 0;
    private static String debugFilter = "";
    private static String loggerFilter = "ERR,WARN,INFO";

    public Demo01App(){
        info("Initialization application %s (%s) %n- running on JDK %s %n- at %s %n- with classpath = %s%n",
                messages.getString("app.name"),
                messages.getString("app.version"),
                System.getProperty("java.version"),
                System.getProperty("java.home"),
                System.getProperty("java.class.path"));
    }

    public void run(String[] args) {
        init(args);
        loop();
        dispose();
    }

    private void init(String[] args) {
        List<String> lArgs = Arrays.asList(args);
        try {
            config.load(this.getClass().getResourceAsStream("/config.properties"));
            config.forEach((k, v) -> {
                info("Configuration|%s=%s", k, v);
            });
            exit = Boolean.parseBoolean(config.getProperty("app.exit"));
        } catch (IOException e) {
            info("Configuration|Unable to read configuration file: %s", e.getMessage());
        }
        lArgs.forEach(s -> {
            info(String.format("Configuration|Argument: %s", s));
        });
    }

    private void loop() {
        while (!exit) {
            // will loop until exit=true or CTRL+C
        }
    }

    private void dispose() {
        info("End of application ");
    }

    public static void main(String[] argc) {
        Demo01App app = new Demo01App();
        app.run(argc);
    }

    public static void log(String level, String message, Object... args) {
        if (loggerFilter.contains(level)) {
            String dateFormatted = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now());
            System.out.printf(dateFormatted + "|" + level + "|" + message + "%n", args);
        }
    }

    public static void info(String message, Object... args) {
        log("INFO", message, args);
    }

    public static void warn(String message, Object... args) {
        log("WARN", message, args);
    }

    public static void error(String message, Object... args) {
        log("ERR", message, args);
    }
}