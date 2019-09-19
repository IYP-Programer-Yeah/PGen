package ir.ac.sbu.pgen.cmd;

import ir.ac.sbu.controller.RefreshableController;

import java.util.Stack;

/**
 * Created by Pouya Payandeh on 9/3/2016.
 */
public class CommandManager {
    private static CommandManager instance;
    Stack<Command> cmds;
    Stack<Command> redoCmds;
    RefreshableController controller;

    private CommandManager(RefreshableController controller) {
        this.controller = controller;
        cmds = new Stack<>();
        redoCmds = new Stack<>();
    }

    public static CommandManager getInstance() {
        return instance;
    }

    public static void init(RefreshableController controller) {
        instance = new CommandManager(controller);
    }

    public void applyCommand(Command command) {
        command.apply();
        cmds.push(command);
        controller.refresh();
        redoCmds.clear();
    }

    public void rollBack() {
        if (cmds.size() > 0) {
            Command undoCmd = cmds.pop();
            undoCmd.rollBack();
            redoCmds.push(undoCmd);
            controller.refresh();
        }
    }

    public void redoCommand() {
        if (redoCmds.size() > 0) {
            Command cmd = redoCmds.pop();
            cmd.apply();
            cmds.push(cmd);
            controller.refresh();
        }

    }
}
