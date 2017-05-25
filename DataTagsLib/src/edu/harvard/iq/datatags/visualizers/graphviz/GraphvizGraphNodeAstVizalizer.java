package edu.harvard.iq.datatags.visualizers.graphviz;

import edu.harvard.iq.datatags.parser.decisiongraph.AstNodeIdProvider;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstAnswerSubNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstAskNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstCallNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstConsiderNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstEndNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstRejectNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstSectionNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstSetNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstTermSubNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstTodoNode;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import static edu.harvard.iq.datatags.visualizers.graphviz.GvEdge.edge;
import static edu.harvard.iq.datatags.visualizers.graphviz.GvNode.node;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Visualizes the AST of a decision graph.
 *
 * @author Michael Bar-Sinai
 */
public class GraphvizGraphNodeAstVizalizer extends GraphvizVisualizer {

    private final List<? extends AstNode> nodeList;

    List<String> nodes = new LinkedList<>();
    List<String> edges = new LinkedList<>();

    interface AstNodeHandler<T extends AstNode> {

        void handle(T node, int depth);
    }

    private final Map<Class<?>, AstNodeHandler> handlers = new HashMap<>();

    int nextId = 0;

    public GraphvizGraphNodeAstVizalizer(List<? extends AstNode> aNodeList) {
        nodeList = aNodeList;
        addIds(nodeList, new AstNodeIdProvider());
        initMap();
    }

    @Override
    protected void printBody(BufferedWriter out) throws IOException {
        visualizeNodeList(nodeList, 0);
        for (String node : nodes) {
            out.write(node);
            out.newLine();
        }
        out.newLine();
        for (String edge : edges) {
            out.write(edge);
            out.newLine();
        }
    }

    void visualizeNodeList(List<? extends AstNode> list, int depth) {
        AstNode lastNode = null;
        for (AstNode curNode : list) {
            if (lastNode != null) {
                edges.add(edge(sanitizeId(lastNode.getId()), sanitizeId(curNode.getId()))
                        .color("#AAAABB")
                        .label("ast_next")
                        .constraint(depth != 0)
                        .style(GvEdge.Style.Dashed)
                        .gv());
            }

            handlers.get(curNode.getClass()).handle(curNode, depth + 1);

            lastNode = curNode;

        }
    }

    void writeTermNode(String termNodeId, AstTermSubNode node) {
        nodes.add(node(termNodeId)
                .label(nodeLabel(null, "term\\n" + node.getExplanation()))
                .fillColor("#BBBB22")
                .fontSize(9)
                .shape(GvNode.Shape.tab)
                .gv());
    }

    void writeAnswerNode(String nodeId, String ansNodeId, AstAnswerSubNode node, int depth) {
        nodes.add(node(ansNodeId).label(nodeLabel(null, "answer")).gv());
        if (!node.getSubGraph().isEmpty()) {
            edges.add(edge(ansNodeId, sanitizeId(node.getSubGraph().get(0).getId())).label("impl").gv());
        }
        visualizeNodeList(node.getSubGraph(), depth);
    }

    private void initMap() {

        setNodeTypeHandler(AstAskNode.class, (AstAskNode node, int depth) -> {
            final String gvNodeId = sanitizeId(node.getId());
            // consider node
            nodes.add(node(gvNodeId)
                    .label(nodeLabel(node.getId(), "consider\n"))
                    .fillColor("#BBBBFF")
                    .gv());
            // ask node
            nodes.add(node(gvNodeId)
                    .label(nodeLabel(node.getId(), "ask\n"))
                    .fillColor("#BBBBFF")
                    .gv());

            // text node
            edges.add(edge(gvNodeId, gvNodeId + "_TEXT").label("text").gv());
            nodes.add(node(gvNodeId + "_TEXT")
                    .label(nodeLabel(null, node.getTextNode().getText()))
                    .gv());

            // answers nodes
            node.getAnswers().forEach(a -> {
                String ansNodeId = gvNodeId + "_ans_" + sanitizeId(a.getAnswerText());
                edges.add(edge(gvNodeId, ansNodeId).label(a.getAnswerText()).gv());
                writeAnswerNode(gvNodeId, ansNodeId, a, depth);
            });

            if (node.getTerms() != null) {
                node.getTerms().forEach(tsn -> {
                    String termNodeId = gvNodeId + "_t_" + sanitizeId(tsn.getTerm());
                    edges.add(edge(gvNodeId, termNodeId).label(tsn.getTerm()).gv());
                    writeTermNode(termNodeId, tsn);
                });
            }
        });

        setNodeTypeHandler(AstCallNode.class, (AstCallNode node, int depth) -> {
            nodes.add(node(sanitizeId(node.getId()))
                    .shape(GvNode.Shape.cds)
                    .fillColor("#BBDDFF")
                    .label(nodeLabel(node.getId(), "call\n" + node.getCalleeId())).gv());
        });

        setNodeTypeHandler(AstEndNode.class, (AstEndNode node, int depth) -> {
            nodes.add(node(sanitizeId(node.getId()))
                    .label(nodeLabel(node.getId(), "end"))
                    .shape(GvNode.Shape.box)
                    .color("#000000")
                    .fillColor("#000000")
                    .fontColor("#FFFFFF")
                    .gv());
        });

        setNodeTypeHandler(AstSetNode.class, (AstSetNode node, int depth) -> {
            final StringBuilder sb = new StringBuilder();
            AstSetNode.Assignment.Visitor asgnmntPainter = new AstSetNode.Assignment.Visitor() {
                @Override
                public void visit(AstSetNode.AtomicAssignment aa) {
                    sb.append(aa.getSlot()).append("=").append(aa.getValue()).append(" ");
                }

                @Override
                public void visit(AstSetNode.AggregateAssignment aa) {
                    sb.append(aa.getSlot()).append("+={").append(aa.getValue()).append("}").append(" ");
                }
            };
            node.getAssignments().forEach(a -> a.accept(asgnmntPainter));

            final String nodeLabel = nodeLabel(node.getId(), "set\n" + sb.toString());
            nodes.add(node(sanitizeId(node.getId()))
                    .label(nodeLabel)
                    .shape(GvNode.Shape.box)
                    .gv());
        });

        setNodeTypeHandler(AstTodoNode.class, (AstTodoNode node, int depth) -> {
            nodes.add(node(sanitizeId(node.getId()))
                    .fillColor("#AAFFAA")
                    .shape(GvNode.Shape.note)
                    .label(nodeLabel(node.getId(), "todo\n" + node.getTodoText())).gv());
        });

        setNodeTypeHandler(AstRejectNode.class, (AstRejectNode node, int depth) -> {
            nodes.add(node(sanitizeId(node.getId()))
                    .fillColor("#FFAAAA")
                    .shape(GvNode.Shape.hexagon).label(nodeLabel(node.getId(), "reject\n" + node.getReason())).gv());
        });
        
        setNodeTypeHandler(AstSectionNode.class, (AstSectionNode node, int depth) -> {
            nodes.add(node(sanitizeId(node.getId()))
                    .fillColor("#FFAAAA")
                    .shape(GvNode.Shape.folder).label(nodeLabel(node.getId(), "section\n" + node.getInfo())).gv());
        });

    }

    private <T extends AstNode> void setNodeTypeHandler(Class<T> clazz, AstNodeHandler<T> hnd) {
        handlers.put(clazz, hnd);
    }

    String nodeLabel(String nodeId, String extras) {
        return wrap((nodeId == null || nodeId.startsWith("[#"))
                ? extras
                : (">" + nodeId + "<\n" + extras));
    }

    private void addIds(List<? extends AstNode> nodes, AstNodeIdProvider nodeIdProvider) {
        AstNode.NullVisitor idSupplier = new AstNode.NullVisitor() {

           

            @Override
            public void visitImpl(AstConsiderNode nd) throws DataTagsRuntimeException {
                if (nd.getId() == null) {
                    nd.setId(nodeIdProvider.nextId());
                }
                nd.getAnswers().forEach(ans -> addIds(ans.getSubGraph(), nodeIdProvider));
            }

            @Override
            public void visitImpl(AstAskNode nd) throws DataTagsRuntimeException {
                if (nd.getId() == null) {
                    nd.setId(nodeIdProvider.nextId());
                }
                nd.getAnswers().forEach(ans -> addIds(ans.getSubGraph(), nodeIdProvider));
            }

            @Override
            public void visitImpl(AstSetNode nd) throws DataTagsRuntimeException {
                if (nd.getId() == null) {
                    nd.setId(nodeIdProvider.nextId());
                }
            }

            @Override
            public void visitImpl(AstRejectNode nd) throws DataTagsRuntimeException {
                if (nd.getId() == null) {
                    nd.setId(nodeIdProvider.nextId());
                }
            }

            @Override
            public void visitImpl(AstCallNode nd) throws DataTagsRuntimeException {
                if (nd.getId() == null) {
                    nd.setId(nodeIdProvider.nextId());
                }
            }

            @Override
            public void visitImpl(AstTodoNode nd) throws DataTagsRuntimeException {
                if (nd.getId() == null) {
                    nd.setId(nodeIdProvider.nextId());
                }
            }

            @Override
            public void visitImpl(AstEndNode nd) throws DataTagsRuntimeException {
                if (nd.getId() == null) {
                    nd.setId(nodeIdProvider.nextId());
                }
            }
            
            @Override
            public void visitImpl(AstSectionNode nd) throws DataTagsRuntimeException {
                if (nd.getId() == null) {
                    nd.setId(nodeIdProvider.nextId());
                }
            }
        };

        nodes.forEach(n -> n.accept(idSupplier));
    }
}
