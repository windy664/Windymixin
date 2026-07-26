package com.github.ginirohikocha.core.command;

import java.util.function.Consumer;

/**
 * ChaUI 依赖的 CommandLine — stub。
 */
public class CommandLine {

    public enum Type {
        PLAYER,
        OP_OR_CONSOLE
    }

    private final Type type;

    public CommandLine(Type type) {
        this.type = type;
    }

    public CommandLine addConstant(String constant) {
        return this;
    }

    public CommandLine addVariable(String variable) {
        return this;
    }

    public CommandLine addArg(String arg) {
        return this;
    }

    public void finish(Consumer<CommandWrapper> handler) {
        // no-op
    }
}
