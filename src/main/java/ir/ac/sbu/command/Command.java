package ir.ac.sbu.command;

public interface Command {
    void apply();

    void rollBack();
}
