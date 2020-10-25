package ir.ac.sbu.parser;

import ir.ac.sbu.controller.MainController;
import ir.ac.sbu.exception.TableException;
import ir.ac.sbu.parser.builder.Action;
import ir.ac.sbu.parser.builder.LLCell;
import ir.ac.sbu.utility.CheckUtility;
import ir.ac.sbu.utility.DialogUtility;
import ir.ac.sbu.wagu.Block;
import ir.ac.sbu.wagu.Board;
import ir.ac.sbu.wagu.Table;
import javafx.util.Pair;
import ir.ac.sbu.model.EdgeModel;
import ir.ac.sbu.model.GraphModel;
import ir.ac.sbu.model.NodeModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LLParserGenerator {
    private static final int eofTokenId = 0;

    private List<GraphModel> graphs;
    private Set<String> tokens;
    private Set<String> variables;
    private Set<String> nullableVariables;
    private Set<Integer> nullableNodes;
    private Map<String, GraphModel> variableGraph;
    private List<NodeModel> allNodes;
    private List<EdgeModel> allEdges;
    private Map<String, Integer> tokenAsInt;
    private List<String> tokensSortedById;

    public LLParserGenerator(List<GraphModel> graphs) throws TableException {
        this.graphs = graphs;
        checkGraphs(graphs);

        tokens = graphs.stream()
                .flatMap(graph -> graph.getEdges().stream())
                .filter(edge -> !edge.isGraph())
                .map(EdgeModel::getToken)
                .collect(Collectors.toSet());

        variables = graphs.stream()
                .flatMap(graph -> graph.getEdges().stream())
                .filter(EdgeModel::isGraph)
                .map(EdgeModel::getToken).collect(Collectors.toSet());
        Set<String> graphNames = graphs.stream().map(GraphModel::getName).collect(Collectors.toSet());
        variables.addAll(graphNames);

        // check tokens and variables before continue
        checkTokens();

        variableGraph = graphs.stream().collect(Collectors.toMap(GraphModel::getName, Function.identity(), (o, v) -> o));
        allNodes = graphs.stream().flatMap(graphModel -> graphModel.getNodes().stream())
                .collect(Collectors.toList());
        allEdges = graphs.stream()
                .flatMap(graph -> graph.getEdges().stream())
                .collect(Collectors.toList());

        checkEdges();

        nullableNodes = getNullableNodes();
        nullableVariables = graphs.stream()
                .filter(graphModel -> nullableNodes.contains(graphModel.getStart().getId()))
                .map(GraphModel::getName)
                .collect(Collectors.toSet());
    }

    private void checkEdges() throws TableException {
        List<String> messages = new ArrayList<>();

        for (EdgeModel edge : allEdges) {
            try {
                CheckUtility.checkFunctionName(edge.getFunction());
            } catch (IllegalArgumentException e) {
                messages.add(e.getMessage());
            }
        }

        if (allEdges.stream()
                .filter(EdgeModel::isGraph)
                .map(EdgeModel::getToken)
                .anyMatch(token -> token.equals(MainController.mainGraphName))) {
            messages.add("Graph " + MainController.mainGraphName + " should not used in graphs.");
        }

        if (!messages.isEmpty()) {
            throw new TableException(messages);
        }
    }

    private void checkTokens() throws TableException {
        List<String> messages = new ArrayList<>();
        tokenAsInt = new HashMap<>();
        tokenAsInt.put("$", eofTokenId);
        int tokenUID = 1;
        for (String token : tokens) {
            try {
                CheckUtility.checkTokenName(token);
                tokenAsInt.put(token, tokenUID);
                tokenUID++;
            } catch (IllegalArgumentException e) {
                messages.add(e.getMessage());
            }
        }

        for (String variable : variables) {
            try {
                CheckUtility.checkGraphName(variable);
                if (tokenAsInt.put(variable, tokenUID) != null) {
                    messages.add(String.format("%s Should be either a token or a graph.", variable));
                }
                tokenUID++;
            } catch (IllegalArgumentException e) {
                messages.add(e.getMessage());
            }
        }

        tokensSortedById = tokenAsInt.keySet().stream()
                .sorted(Comparator.comparingInt(tokenAsInt::get))
                .collect(Collectors.toList());

        if (!messages.isEmpty()) {
            throw new TableException(messages);
        }
    }

    private void checkGraphs(List<GraphModel> graphs) throws TableException {
        List<String> messages = new ArrayList<>();
        Set<String> extractExpectedGraphs = graphs.stream().flatMap(graph -> graph.getEdges().stream()).
                filter(EdgeModel::isGraph).map(EdgeModel::getToken).collect(Collectors.toSet());
        List<String> givenGraphs = graphs.stream().map(GraphModel::getName).collect(Collectors.toList());
        extractExpectedGraphs.removeAll(givenGraphs);
        extractExpectedGraphs.forEach(s -> messages.add(String.format("Graph %s doesn't exist", s)));
        givenGraphs.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .filter(e -> e.getValue() > 1L)
                .map(Map.Entry::getKey)
                .forEach(s -> messages.add(String.format("Duplicate graph %s exist", s)));

        List<GraphModel> graphWithNoFinalNode = graphs.stream().
                filter(graph -> graph.getNodes().stream().noneMatch(NodeModel::isFinalNode))
                .collect(Collectors.toList());
        graphWithNoFinalNode.forEach(graph ->
                messages.add(String.format("Graph %s doesn't have final node", graph.getName())));
        List<GraphModel> graphWithNoStartNode = graphs.stream()
                .filter(graph -> graph.getStart() == null)
                .collect(Collectors.toList());
        graphWithNoStartNode.forEach(graph ->
                messages.add(String.format("Graph %s doesn't have start node", graph.getName())));

        if (!messages.isEmpty()) {
            throw new TableException(messages);
        }
    }

    private Pair<LLCell[][], Integer> buildTable() throws TableException {
        Map<Integer, Set<String>> totalFirstSets = getTotalFirstSets();
        Map<String, Set<String>> totalFollowSets = getTotalFollowSets(totalFirstSets);

        int totalTokensVariables = tokenAsInt.size();
        LLCell[][] table = new LLCell[allNodes.size()][totalTokensVariables];
        for (int i = 0; i < allNodes.size(); i++) {
            for (int j = 0; j < totalTokensVariables; j++) {
                table[i][j] = new LLCell(Action.ERROR, -1, "", "First");
            }
        }

        List<String> messages = new ArrayList<>();
        for (GraphModel graphModel : graphs) {
            List<NodeModel> finalNodes = graphModel.getNodes().stream().filter(NodeModel::isFinalNode)
                    .collect(Collectors.toList());
            for (NodeModel finalNode : finalNodes) {
                if (graphModel.getName().equals(MainController.mainGraphName)) {
                    // Set EOF as an accept state.
                    table[finalNode.getId()][eofTokenId] =
                            new LLCell(Action.ACCEPT, -1, "", "First");
                } else {
                    for (String follow : totalFollowSets.get(graphModel.getName())) {
                        table[finalNode.getId()][tokenAsInt.get(follow)] = new LLCell(Action.REDUCE,
                                tokenAsInt.get(graphModel.getName()), "", "Follow");
                    }
                }
            }
        }

        for (NodeModel node : allNodes) {
            int nodeID = node.getId();
            List<EdgeModel> adjacentList = node.getAdjacentList();
            List<EdgeModel> tokenEdges = adjacentList.stream().filter(x -> !x.isGraph()).collect(Collectors.toList());
            List<EdgeModel> graphEdges = adjacentList.stream().filter(EdgeModel::isGraph).collect(Collectors.toList());
            // Note: The list of tokens has a higher priority than the list of sub-graphs, and when moving
            //       on a graph, even if there is a token in the sub-graphs, the token on the edge is still
            //       selected to move. So we process list of tokens at first.
            for (EdgeModel edge : tokenEdges) {
                int tokenID = tokenAsInt.get(edge.getToken());
                LLCell cell = table[nodeID][tokenID];
                // Note: It is impossible to pre-set a 'PUSH_GOTO' or 'GOTO' value for a node because we
                //       are just starting to look at this node and we must have checked the tokens first.
                //       The possible values for it (except for the error, which is initially set for everyone)
                //       are only the values of 'REDUCE' and 'SHIFT'.
                //       In 'REDUCE', our priority is to move on the edge instead of going back to the top graph.
                //       But when we see 'SHIFT', it means we have seen a letter twice on the edge.
                if (cell.getAction() == Action.SHIFT) {
                    messages.add(String.format(
                            "At node %s: It is impossible to move with token \"%s\" to more than one node (%s and %s)",
                            nodeID, edge.getToken(), cell.getTarget(), edge.getEnd().getId()));
                }
                table[nodeID][tokenID] = new LLCell(Action.SHIFT, edge.getEnd().getId(), edge.getFunction(),
                        String.valueOf(edge.getEnd().getId()));
            }
            for (EdgeModel edge : graphEdges) {
                int tokenID = tokenAsInt.get(edge.getToken());
                int subGraphStartNodeId = variableGraph.get(edge.getToken()).getStart().getId();
                // Note: In all cases we will not return to the top graph if it is possible to move on the
                //       current graph or go to the sub-graphs. For this reason, the 'REDUCE' mode is not
                //       checked in the rest of the code of this section, and if necessary, it will be
                //       replaced with another {@link Action}.
                // Note: In this case, due to the edge type (sub-graph), the only possible value is 'GOTO'.
                if (table[nodeID][tokenID].getAction() == Action.GOTO) {
                    messages.add(String.format(
                            "At node %s: It is impossible to use same graph \"%s\" more than once",
                            nodeID, edge.getToken()));
                }
                table[nodeID][tokenID] = new LLCell(Action.GOTO, edge.getEnd().getId(),
                        edge.getFunction(), edge.getToken());

                // Note: We have to go to this sub-graph for the tokens that can be used
                //       in it (from the starting point). These tokens are first set of start node.
                Set<String> firstSet = totalFirstSets.get(subGraphStartNodeId);
                for (String firstToken : firstSet) {
                    int firstTokenId = tokenAsInt.get(firstToken);
                    LLCell cell = table[nodeID][firstTokenId];
                    // Note: At this point, the only values that can be set for it in previous iteration
                    //       are the following: 'SHIFT', 'PUSH_GOTO'
                    if (cell.getAction() == Action.SHIFT) {
                        // In this case, it means we have an edge with the same token. Because we decide
                        // to prefer moving on the current graph to going sub-graphs, we skip this token.
                        continue;
                    } else if (cell.getAction() == Action.PUSH_GOTO) {
                        messages.add(String.format(
                                "At node %s: It is impossible to move with token \"%s\" to more than one graph" +
                                        " (\"%s\" or \"%s\")",
                                nodeID, firstToken, cell.getHelperValue(), edge.getToken()));
                    }
                    table[nodeID][firstTokenId] = new LLCell(Action.PUSH_GOTO, subGraphStartNodeId, "",
                            edge.getToken());
                }

                // Check special case (When edge contains an nullable variable which means that we can
                // pass through it without consuming tokens)
                if (nullableVariables.contains(edge.getToken())) {
                    for (String follow : totalFollowSets.get(edge.getToken())) {
                        int followTokenId = tokenAsInt.get(follow);
                        LLCell cell = table[nodeID][followTokenId];
                        // Note: At this point, the only values that can be set for it in previous iteration
                        //       are the following: 'SHIFT', 'PUSH_GOTO'
                        if (cell.getAction() == Action.SHIFT) {
                            // In this case, it means we have an edge with the same token. (assume token X)
                            // I can cross a node without taking a token and then move with an X token.
                            // At the same time, we can move from the edge with the X token right now.
                            // Because our priority is to move on the current graph, we move from the
                            // edge with token X. In fact, the edge that does not consume any tokens is a
                            // sub-graph that we enter and return without consuming anything. But our intention
                            // is to move on the current graph before entering a sub-graph.
                            continue;
                        } else if (cell.getAction() == Action.PUSH_GOTO) {
                            messages.add(String.format(
                                    "At node %s: It is possible to move to node %s with edge \"%s\" without " +
                                            "consuming any token. So we can move with token \"%s\" to " +
                                            "graph \"%s\" or move with it on node \"%s\".",
                                    nodeID, edge.getEnd().getId(), edge.getToken(), follow,
                                    cell.getHelperValue(), edge.getEnd().getId()));
                        }
                        table[nodeID][followTokenId] = new LLCell(Action.PUSH_GOTO, subGraphStartNodeId, "",
                                edge.getToken());
                    }
                }
            }
        }

        if (!messages.isEmpty()) {
            throw new TableException(messages);
        }

        int startNode = variableGraph.get(MainController.mainGraphName).getStart().getId();
        return new Pair<>(table, startNode);
    }

    public void buildTable(File file) throws TableException {
        Pair<LLCell[][], Integer> tableInfo = buildTable();
        int startNode = tableInfo.getValue();
        LLCell[][] table = tableInfo.getKey();
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println(allNodes.size() + " " + table[0].length);
            writer.println(startNode);

            writer.println(String.join(CheckUtility.DELIMITER, tokensSortedById));
            for (LLCell[] cellOfNode : table) {
                writer.print(Arrays.stream(cellOfNode).map(LLCell::toString)
                        .collect(Collectors.joining(CheckUtility.DELIMITER)));
                writer.println();
            }
            writer.println();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void buildPrettyTable(File file) throws TableException {
        try (PrintWriter writer = new PrintWriter(file)) {
            String tableString = getTableString(30);
            writer.println(tableString);
        } catch (FileNotFoundException e) {
            DialogUtility.showErrorDialog("Unable to create table: " + e.getMessage());
        }
    }

    public void buildCSVTable(File file) throws TableException {
        try (PrintWriter writer = new PrintWriter(file)) {
            String tableString = getTableString(120);
            String blocksText = tableString + System.lineSeparator();
            Scanner scanner = new Scanner(blocksText);
            boolean printedInLine = false;
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                line = line.trim();
                if (line.equals("") || line.charAt(0) == '+')
                    continue;
                String[] items = line.split(" {3,}" + Pattern.quote("|") + " {3,}" + "|"
                        + Pattern.quote("|") + " {3,}" + "|" + " {3,}" + Pattern.quote("|"));
                if (items.length == 0)
                    continue;

                for (String item : items) {
                    String fixedItem = item.trim();
                    if (fixedItem.equals(""))
                        continue;
                    fixedItem = (fixedItem.contains(",") ? "\"" + fixedItem + "\"" : fixedItem);
                    if (printedInLine)
                        writer.write(",");
                    writer.write(fixedItem);
                    printedInLine = true;
                }
                writer.write(System.lineSeparator());
                printedInLine = false;
            }
            writer.write(System.lineSeparator());
        } catch (FileNotFoundException e) {
            DialogUtility.showErrorDialog("Unable to create table: " + e.getMessage());
        }
    }

    private String getTableString(int factor) throws TableException {
        Pair<LLCell[][], Integer> tableInfo = buildTable();
        LLCell[][] table = tableInfo.getKey();
        List<String> headersList = new ArrayList<>(tokensSortedById);
        headersList.add(0, "States");
        List<Integer> colAlignList = new ArrayList<>();
        for (int i = 0; i < headersList.size(); i++) {
            colAlignList.add(Block.DATA_CENTER);
        }
        List<List<String>> rowsList = BuildStringTable(table);
        BuildStringTable(table);

        Board board = new Board(headersList.size() * factor);
        Table ta = new Table(board, headersList.size() * factor, headersList, rowsList);
        ta.setColAlignsList(colAlignList);
        return board.setInitialBlock(ta.tableToBlocks()).build().getPreview();
    }

    private List<List<String>> BuildStringTable(LLCell[][] table) {
        int state = 0;
        List<List<String>> rowsList = new ArrayList<>();
        for (LLCell[] llCells : table) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(state++));
            for (LLCell cell : llCells) {
                String r = cell.getAction().toString();
                if (cell.getAction() == Action.REDUCE) {
                    for (Map.Entry<String, Integer> pair : tokenAsInt.entrySet()) {
                        if (pair.getValue() == cell.getTarget()) {
                            r += " " + pair.getKey();
                            break;
                        }
                    }

                }
                if (cell.getAction() == Action.GOTO || cell.getAction() == Action.PUSH_GOTO || cell.getAction() == Action.SHIFT)
                    r += " S" + cell.getTarget();
                if (cell.getAction() != Action.ERROR && cell.getAction() != Action.REDUCE)
                    r += " " + cell.getFunction();
                row.add(r);
            }
            rowsList.add(row);
        }
        return rowsList;
    }

    /**
     * a node is nullable if there is one out-edge like (A,B,C) (meaning A go with B to C)
     * and B is nullable and C is nullable too.
     *
     * @return all nullable nodes
     */
    private Set<Integer> getNullableNodes() {
        Set<Integer> nullableNodes = allNodes.stream()
                .filter(NodeModel::isFinalNode)
                .map(NodeModel::getId)
                .collect(Collectors.toSet());
        int lastSize;
        do {
            lastSize = nullableNodes.size();
            for (NodeModel nodeModel : allNodes) {
                for (EdgeModel edgeModel : nodeModel.getAdjacentList()) {
                    if (edgeModel.isGraph()) {
                        int idOfVariableStartNode = variableGraph.get(edgeModel.getToken()).getStart().getId();
                        if (nullableNodes.contains(idOfVariableStartNode) && nullableNodes.contains(edgeModel.getEnd().getId())) {
                            nullableNodes.add(edgeModel.getStart().getId());
                        }
                    }
                }
            }
        } while (nullableNodes.size() > lastSize);

        return nullableNodes;
    }

    private Map<Integer, Set<String>> getTotalFirstSets() throws TableException {
        Map<Integer, Set<String>> firsts = new HashMap<>();
        Set<Integer> visited = new HashSet<>();
        try {
            for (NodeModel nodeModel : allNodes) {
                calculateFirstSet(firsts, visited, nodeModel);
            }
            return firsts;
        } catch (TableException e) {
            // First element is message title and others are path in DFS search.
            throw new TableException(
                    e.getMessages().get(0) + String.join(" -> ", e.getMessages().subList(1, e.getMessages().size())));
        }
    }

    private Set<String> calculateFirstSet(Map<Integer, Set<String>> firstSets, Set<Integer> visited,
                                          NodeModel nodeModel) throws TableException {
        if (visited.contains(nodeModel.getId())) {
            Set<String> lastCalculatedFirstSet = firstSets.get(nodeModel.getId());
            if (lastCalculatedFirstSet == null) {
                // We use DFS to calculate first sets. If in DFS tree, node 'A' was children of itself directly
                // or indirectly, it means we can move in 'A' infinitely.
                throw new TableException(nodeModel.getGraph() + " graph calls itself recursively. Call stack: ");
            }
            return lastCalculatedFirstSet;
        }
        visited.add(nodeModel.getId());
        Set<String> firstOfCurrentNode = new HashSet<>();

        /*
        calculate first set
        if there is an edge from N1 to N2 with token (or variable) C:
        first{N1} += first{C} + (first{N2} | if C is a variable and C is nullable)
        if C is nullable and N1 == N2 (C is a loop), then first{N1} += first{C}
         */
        for (EdgeModel edgeModel : nodeModel.getAdjacentList()) {
            if (edgeModel.isGraph()) {
                try {
                    Set<String> firstSetOfGraph = calculateFirstSet(firstSets, visited,
                            variableGraph.get(edgeModel.getToken()).getStart());
                    firstOfCurrentNode.addAll(firstSetOfGraph);
                } catch (TableException e) {
                    // Add illegal graph to message list
                    e.getMessages().add(nodeModel.getGraph().toString());
                    throw e;
                }
                if (nullableVariables.contains(edgeModel.getToken()) &&
                        edgeModel.getEnd().getId() != nodeModel.getId()) {
                    Set<String> firstSetOfRestOfGraph = calculateFirstSet(firstSets, visited,
                            edgeModel.getEnd());
                    firstOfCurrentNode.addAll(firstSetOfRestOfGraph);
                }
            } else {
                firstOfCurrentNode.add(edgeModel.getToken());
            }
        }
        firstSets.put(nodeModel.getId(), firstOfCurrentNode);
        return firstOfCurrentNode;
    }

    private Map<String, Set<String>> getTotalFollowSets(Map<Integer, Set<String>> firstSets) {
        Map<String, Set<String>> followSet = new HashMap<>(); // map of (x, y) show that follow{x} = y
        Map<String, Set<String>> followContain = new HashMap<>(); // map of (x, y) show that follow{x} contains follow{y}
        for (String variable : variables) {
            followSet.put(variable, new HashSet<>());
            followContain.put(variable, new HashSet<>());
        }
        followSet.get(MainController.mainGraphName).add("$");

        /*
        calculate follow set with formula
        A -> BCDH
        follow{C} = First{DH} + (Follow{A} if DH is nullable)
        this is equal to (when we see nodes):
        A -> (node1) B (node2) C (node3) D (node4) H (graph model)
        follow{C} = First{node3} + (Follow{A} if node3 is nullable or node3 is final)
         */
        for (EdgeModel edgeModel : allEdges) {
            if (edgeModel.isGraph()) {
                NodeModel end = edgeModel.getEnd();
                String variable = edgeModel.getToken();
                String graphContainThisVariable = end.getGraph().getName();
                followSet.get(variable).addAll(firstSets.get(end.getId()));
                if (end.isFinalNode() || nullableNodes.contains(end.getId())) {
                    followContain.get(variable).add(graphContainThisVariable);
                }
            }
        }

        // iterate and add all follow of x to follow of y if followContain{x} contains y
        // do this until there is no change it follow sets so we calculated all follow sets
        boolean hasChange = true;
        while (hasChange) {
            hasChange = false;
            for (Map.Entry<String, Set<String>> entry : followContain.entrySet()) {
                List<String> followSetOfContainedFollows = entry.getValue().stream()
                        .map(followSet::get).flatMap(Collection::stream)
                        .collect(Collectors.toList());
                if (followSet.get(entry.getKey()).addAll(followSetOfContainedFollows)) {
                    hasChange = true;
                }
            }
        }

        return followSet;
    }
}
