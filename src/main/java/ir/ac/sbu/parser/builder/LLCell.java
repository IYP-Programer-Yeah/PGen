package ir.ac.sbu.parser.builder;

public class LLCell {
    private Action action;
    private int target;
    private String function;
    /**
     * It will be only used for debugging.
     * For SHIFT, it represents target id
     * For GOTO or PUSH_GOTO, it represents graph name (variable name)
     */
    private String helperValue;

    public LLCell(Action action, int target, String function) {
        this.action = action;
        this.target = target;
        this.function = function;
        if (function == null || function.equals("")) {
            this.function = "NoSem";
        }
    }

    public LLCell(Action action, int target, String function, String helperValue) {
        this(action, target, function);
        this.helperValue = helperValue;
    }

    @Override
    public String toString() {
        return String.format("%d %d %s", action.ordinal(), target, function);
    }

    public Action getAction() {
        return action;
    }

    public String getHelperValue() {
        return helperValue;
    }

    public int getTarget() {
        return target;
    }

    public String getFunction() {
        return function;
    }
}
