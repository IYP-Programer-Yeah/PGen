package ir.ac.sbu.command;

import ir.ac.sbu.controller.RefreshableController;

import java.util.ArrayDeque;
import java.util.Deque;

public class CommandManager {
    private static CommandManager instance;
    private Deque<Command> commands;
    private Deque<Command> redoCommands;
    private RefreshableController controller;

    private CommandManager(RefreshableController controller) {
        this.controller = controller;
        commands = new ArrayDeque<>();
        redoCommands = new ArrayDeque<>();
    }

    public static CommandManager getInstance() {
        return instance;
    }

    public static void init(RefreshableController controller) {
        instance = new CommandManager(controller);
    }

    public void applyCommand(Command command) {
        command.apply();
        commands.push(command);
        controller.refresh();
        redoCommands.clear();
    }

    public void rollBack() {
        if (commands.size() > 0) {
            Command undoCmd = commands.pop();
            undoCmd.rollBack();
            redoCommands.push(undoCmd);
            controller.refresh();
        }
    }

    public void redoCommand() {
        if (redoCommands.size() > 0) {
            Command cmd = redoCommands.pop();
            cmd.apply();
            commands.push(cmd);
            controller.refresh();
        }
    }
}
