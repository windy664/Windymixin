package com.github.ginirohikocha.core.logger;

import java.util.logging.Level;

/**
 * ChaUI 依赖的 Logger — Windymixin 提供的 stub。
 */
public class Logger {
    private final String name;
    private boolean debug = false;

    public Logger(String name) {
        this.name = name;
    }

    public void info(String msg) {
        java.util.logging.Logger.getLogger(name).info(msg);
    }

    public void debug(String msg) {
        if (debug) {
            java.util.logging.Logger.getLogger(name).fine(msg);
        }
    }

    public void warning(String msg) {
        java.util.logging.Logger.getLogger(name).warning(msg);
    }

    public void severe(String msg) {
        java.util.logging.Logger.getLogger(name).severe(msg);
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isDebug() {
        return debug;
    }

    public void banner() {
        info("[ChaCore-stub] Running without remote verification (Windymixin)");
    }

    public void enabled() {
        info("[ChaCore-stub] Enabled");
    }

    public void disabled() {
        info("[ChaCore-stub] Disabled");
    }
}
