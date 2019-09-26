package ir.ac.sbu.service;

import ir.ac.sbu.model.EdgeModel;
import ir.ac.sbu.model.GraphModel;
import ir.ac.sbu.utility.DialogUtility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ExportService {
    private Path selectedDirectory;

    public ExportService(Path selectedDirectory) {
        this.selectedDirectory = selectedDirectory;
    }

    public void exportGraphs(List<GraphModel> graphs) {
        try {
            Files.createDirectories(selectedDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String CLASS_TEMPLATE = "public class %s\n{%s}\n";
        String FUNCTION_TEMPLATE = "public static void %s()\n{\n}";
        for (GraphModel graphModel : graphs) {
            StringBuilder funcs = new StringBuilder();
            for (EdgeModel edgeModel : graphModel.getEdges()) {
                if (!edgeModel.getFunction().equals("")) {
                    if (!edgeModel.isGlobal())
                        funcs.append(String.format(FUNCTION_TEMPLATE, edgeModel.getFunction()));
                }
            }
            String output = String.format(CLASS_TEMPLATE, graphModel.getName(), funcs.toString());
            makeFile(output, graphModel.getName());

        }
        List<String> globalfunc = graphs.stream().flatMap(graphModel -> graphModel.getEdges().stream())
                .filter(EdgeModel::isGlobal).map(EdgeModel::getFunction).collect(Collectors.toList());
        StringBuilder gfuncs = new StringBuilder();
        for (String func : globalfunc) {
            if (!func.equals("")) {
                gfuncs.append(String.format(FUNCTION_TEMPLATE, func));
            }
        }
        String output = String.format(CLASS_TEMPLATE, "Global", gfuncs.toString());
        makeFile(output, "Global");
    }

    private void makeFile(String data, String fileName) {
        try (PrintWriter writer = new PrintWriter(selectedDirectory.toString() + "/" + fileName + ".java")) {
            writer.print(data);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
