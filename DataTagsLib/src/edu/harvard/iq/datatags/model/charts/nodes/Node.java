package edu.harvard.iq.datatags.model.charts.nodes;

import edu.harvard.iq.datatags.model.charts.ChartEntity;
import edu.harvard.iq.datatags.model.charts.FlowChart;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;

/**
 * An atomic part of the program - the equivalent of a line of code 
 * in a regular program.
 * 
 * @author michael
 */
public abstract class Node extends ChartEntity {
	
	public interface Visitor<R> {
		R visit( AskNode nd ) throws DataTagsRuntimeException;
		R visit( SetNode nd ) throws DataTagsRuntimeException;
		R visit( RejectNode nd ) throws DataTagsRuntimeException;
		R visit( CallNode nd ) throws DataTagsRuntimeException;
		R visit( TodoNode nd ) throws DataTagsRuntimeException;
		R visit( EndNode nd ) throws DataTagsRuntimeException;
	}
    
    public static abstract class VoidVisitor implements Visitor<Void> {

        @Override
        public Void visit(AskNode nd) throws DataTagsRuntimeException {
            visitImpl(nd);
            return null;
        }

        @Override
        public Void visit(SetNode nd) throws DataTagsRuntimeException {
            visitImpl(nd);
            return null;
        }

        @Override
        public Void visit(RejectNode nd) throws DataTagsRuntimeException {
            visitImpl(nd);
            return null;
        }

        @Override
        public Void visit(CallNode nd) throws DataTagsRuntimeException {
            visitImpl(nd);
            return null;
        }

        @Override
        public Void visit(TodoNode nd) throws DataTagsRuntimeException {
            visitImpl(nd);
            return null;
        }

        @Override
        public Void visit(EndNode nd) throws DataTagsRuntimeException {
            visitImpl(nd);
            return null;
        }

        public abstract void visitImpl( AskNode nd    ) throws DataTagsRuntimeException;
        public abstract void visitImpl( SetNode nd    ) throws DataTagsRuntimeException;
        public abstract void visitImpl( RejectNode nd ) throws DataTagsRuntimeException;
        public abstract void visitImpl( CallNode nd   ) throws DataTagsRuntimeException;
        public abstract void visitImpl( TodoNode nd   ) throws DataTagsRuntimeException;
        public abstract void visitImpl( EndNode nd    ) throws DataTagsRuntimeException;

    }
    
	private FlowChart chart;

	public Node(String id) {
		this( id, null );
	}

	public Node(String id, String title) {
		this( id, title, null, null );
	}

	public Node(String id, String title, String text, FlowChart chart) {
		super(id);
		this.title = title;
		this.info = text;
		this.chart = chart;
	}

	public abstract <R> R accept( Node.Visitor<R> vr ) throws DataTagsRuntimeException ;
	
	public FlowChart getChart() {
		return chart;
	}

	public void setChart(FlowChart chart) {
		this.chart = chart;
	}
	
}
