/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.datatags.externaltexts;

import edu.harvard.iq.datatags.cli.commands.CreateLocalizationCommand;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.parser.decisiongraph.AstNodeIdProvider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author mor
 */
public class FsLocalizationIO {

    public static final String LOCALIZATION_DIRECTORY_NAME = "languages";
    public static final String ANSWERS_FILENAME = "answers.txt";
    public static final String SPACE_DATA_FILENAME = "space.md";
    public static final String NODE_DIRECTORY_NAME = "nodes";
    public static final String LOCALIZED_METADATA_FILENAME = "localized-model.xml";

    public static Map<String, Path> getNodesPath(Set<Node> nodes) {
        return nodes.stream().filter((node) -> !AstNodeIdProvider.isAutoId(node.getId())).collect(Collectors.toMap((node) -> node.getId(), (node) -> getNodePath(node.getId())));
    }

    public static Path getNodePath(String node) {
        String parent = node;
        if (node.startsWith("[")) {
            parent = node.substring(1, node.indexOf("]"));
        }
        parent = parent.endsWith(".dg") ? parent.substring(0, parent.length() - 3) : parent;
        return Paths.get(parent).resolve(node.substring(node.indexOf("]") + 1, node.length()) + ".md");
    }

    public static void createNodeLocalizationFile(Path nodesDir, Path node, String content) {
        if (!Files.exists(nodesDir.resolve(node.getParent()))) {
            try {
                Files.createDirectories(nodesDir.resolve(node.getParent()));
            } catch (IOException ex) {
                Logger.getLogger(CreateLocalizationCommand.class.getName()).log(Level.SEVERE, "Error creating directory for node localizatoin", ex);
            }
        }
        createFileWithContent(nodesDir.resolve(node), content);
    }

    private static void createFileWithContent(Path p, String c) {
        try {
            Files.write(p, c.getBytes());
        } catch (IOException ex) {
            Logger.getLogger(CreateLocalizationCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
