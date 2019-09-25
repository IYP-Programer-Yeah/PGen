package ir.ac.sbu.service;

public class Message {
    public final static int ERROR = 0;
    public final static int INFO = 1;
    int type;
    String message;

    public Message(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
