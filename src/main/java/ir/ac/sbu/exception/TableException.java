package ir.ac.sbu.exception;

import java.util.List;

public class TableException extends Exception {
    private final List<String> messages;

    public TableException(List<String> messages) {
        this.messages = messages;
    }

    public List<String> getMessages() {
        return messages;
    }
}
