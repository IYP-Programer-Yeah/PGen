package ir.ac.sbu.service;

import ir.ac.sbu.model.EdgeModel;
import ir.ac.sbu.model.GraphModel;
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

        String CLASS_TEMPLATE = "public class %s { \n%s}\n";
        String FUNCTION_TEMPLATE = "public static void %s() {} \n";
        for (GraphModel graphModel : graphs) {
            StringBuilder functions = new StringBuilder();
            for (EdgeModel edgeModel : graphModel.getEdges()) {
                if (!edgeModel.getFunction().equals("") && !edgeModel.isGlobal()) {
                    functions.append(String.format(FUNCTION_TEMPLATE, edgeModel.getFunction()));
                }
            }
            String output = String.format(CLASS_TEMPLATE, graphModel.getName(), functions.toString().replaceAll("(?m)^", "\t"));
            makeFile(output, graphModel.getName());
        }

        List<String> globalFunctions = graphs.stream().flatMap(graphModel -> graphModel.getEdges().stream())
                .filter(EdgeModel::isGlobal).map(EdgeModel::getFunction).filter(x -> !x.equals("")).collect(Collectors.toList());
        StringBuilder globalFunctionsBuilder = new StringBuilder();
        for (String func : globalFunctions) {
            globalFunctionsBuilder.append(String.format(FUNCTION_TEMPLATE, func));
        }
        String output = String.format(CLASS_TEMPLATE, "Global", globalFunctionsBuilder.toString().replaceAll("(?m)^", "\t"));
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
