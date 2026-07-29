package com.github.ginirohikocha.core.command;

/**
 * ChaUI 依赖的 CommandParser — stub。
 */
public class CommandParser {
    private final String name;

    public CommandParser(String name) {
        this.name = name;
    }

    public CommandParser(String name, boolean flag) {
        this.name = name;
    }

    public CommandLine newCommandLine(CommandLine.Type type) {
        return new CommandLine(type);
    }

    public CommandLine newCommandLine(CommandLine.Type type, String name) {
        return new CommandLine(type);
    }

    public CommandLine newCommandLine() {
        return new CommandLine(CommandLine.Type.PLAYER);
    }

    public void register() {
        // no-op
    }
}
