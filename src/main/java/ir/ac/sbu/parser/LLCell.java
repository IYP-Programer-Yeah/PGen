package ir.ac.sbu.parser;

enum Action {
    ERROR, SHIFT, GOTO, PUSH_GOTO, REDUCE, ACCEPT
}

public class LLCell {
    private Action action;
    private int target;
    private String function;
    /**
     * does it come from follow set or first set
     */
    private String comeFrom;

    public LLCell(Action action, int target, String function) {
        this.action = action;
        this.target = target;
        this.function = function;
        if (function == null || function.equals(""))
            this.function = "NoSem";
        else
            this.function = "@" + function;
    }

    public LLCell(Action action, int target, String function, String comeFrom) {
        this(action, target, function);
        this.comeFrom = comeFrom;
    }

    @Override
    public String toString() {
        return String.format("%d %d %s", action.ordinal(), target, function);
    }

    public Action getAction() {
        return action;
    }

    public String getComeFrom() {
        return comeFrom;
    }

    public int getTarget() {
        return target;
    }

    public String getFunction() {
        return function;
    }
}
