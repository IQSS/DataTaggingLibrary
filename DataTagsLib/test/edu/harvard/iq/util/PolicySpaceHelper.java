package edu.harvard.iq.util;

import java.util.Arrays;
import java.util.List;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 *
 * @author michael
 */
public class PolicySpaceHelper {
    
    public static List<String> toPath(String spaceRef) {
        return Arrays.stream(spaceRef.split("/")).collect(toList());
    }
    
    public static String fromPath( List<String> path ) {
        return path.stream().collect(joining("/"));
    }
}
