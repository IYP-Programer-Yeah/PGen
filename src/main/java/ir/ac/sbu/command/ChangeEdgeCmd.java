package ir.ac.sbu.command;

import ir.ac.sbu.model.EdgeModel;

public class ChangeEdgeCmd implements Command {
    private EdgeModel edgeModel;
    private String newToken;
    private String newFunc;
    private Boolean newGraph;
    private Boolean newGlobal;

    private String lastToken;
    private String lastFunc;
    private Boolean lastGraph;
    private Boolean lastGlobal;

    public ChangeEdgeCmd(EdgeModel edgeModel, String newToken, String newFunc, Boolean newGraph, Boolean newGlobal) {
        this.edgeModel = edgeModel;
        this.newToken = newToken;
        this.newFunc = newFunc;
        this.newGlobal = newGlobal;
        this.newGraph = newGraph;
    }

    @Override
    public void apply() {
        lastToken = edgeModel.getToken();
        lastFunc = edgeModel.getFunction();
        lastGraph = edgeModel.isGraph();
        lastGlobal = edgeModel.isGlobal();

        edgeModel.setFunction(newFunc);
        edgeModel.setToken(newToken);
        edgeModel.setGlobal(newGlobal);
        edgeModel.setGraph(newGraph);
    }

    @Override
    public void rollBack() {
        edgeModel.setFunction(lastFunc);
        edgeModel.setToken(lastToken);
        edgeModel.setGraph(lastGraph);
        edgeModel.setGlobal(lastGlobal);
    }
}
