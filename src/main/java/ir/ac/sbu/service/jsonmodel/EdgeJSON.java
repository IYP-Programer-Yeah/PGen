package ir.ac.sbu.service.jsonmodel;

import ir.ac.sbu.model.EdgeModel;

public class EdgeJSON {
    private int start;
    private int end;
    private String token;
    private String func;
    private double anchorX;
    private double anchorY;
    private boolean isGraph;
    private boolean isGlobal;

    public EdgeJSON() {
    }

    public EdgeJSON(EdgeModel model) {
        start = model.getStart().getId();
        end = model.getEnd().getId();
        token = model.getToken();
        func = model.getFunction();
        anchorX = model.getAnchorX();
        anchorY = model.getAnchorY();
        isGraph = model.isGraph();
        isGlobal = model.isGlobal();
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getToken() {
        return token;
    }

    public String getFunc() {
        return func;
    }

    public double getAnchorX() {
        return anchorX;
    }

    public double getAnchorY() {
        return anchorY;
    }

    public boolean isGraph() {
        return isGraph;
    }

    public boolean isGlobal() {
        return isGlobal;
    }
}
