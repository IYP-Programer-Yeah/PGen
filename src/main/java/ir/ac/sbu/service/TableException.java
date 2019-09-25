package ir.ac.sbu.service;

import java.util.List;

public class TableException extends Exception {
    private final List<Message> msg;

    TableException(List<Message> msg) {
        this.msg = msg;
    }

    public List<Message> getMsg() {
        return msg;
    }
}
