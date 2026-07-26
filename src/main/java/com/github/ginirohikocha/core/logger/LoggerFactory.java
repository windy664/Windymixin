package com.github.ginirohikocha.core.logger;

/**
 * ChaUI 依赖的 LoggerFactory — Windymixin 提供的 stub。
 */
public class LoggerFactory {
    public static Logger getLogger(String name) {
        return new Logger(name);
    }
}
