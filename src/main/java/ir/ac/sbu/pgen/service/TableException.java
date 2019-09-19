package pgen.service;

import java.util.List;

/**
 * Created by Pouya Payandeh on 1/24/2017.
 */
public class TableException extends Exception {
    private final List<Message> msg;

    TableException(List<Message> msg) {
        this.msg = msg;
    }

    public List<Message> getMsg() {
        return msg;
    }
}
