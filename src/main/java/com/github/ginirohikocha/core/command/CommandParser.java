package com.github.ginirohikocha.core.command;

/**
 * ChaUI 依赖的 CommandParser — stub。
 */
public class CommandParser {
    private final String name;

    public CommandParser(String name) {
        this.name = name;
    }

    public CommandLine newCommandLine(CommandLine.Type type) {
        return new CommandLine(type);
    }

    public void register() {
        // no-op
    }
}
