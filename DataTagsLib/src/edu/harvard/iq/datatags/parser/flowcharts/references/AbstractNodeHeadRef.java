package edu.harvard.iq.datatags.parser.flowcharts.references;


/**
 * 
 * @author Michael Bar-Sinai
 */
public abstract class AbstractNodeHeadRef {
    private final String id;

    public AbstractNodeHeadRef( String anId ) {
        id = anId;
    }

    public String getId() {
        return id;
    }

}
