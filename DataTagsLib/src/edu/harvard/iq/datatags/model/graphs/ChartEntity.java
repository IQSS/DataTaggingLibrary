package edu.harvard.iq.datatags.model.graphs;

/**
 * Base class for classes that take part in a chart set.
 * 
 * @author michael
 */
public abstract class ChartEntity {
	
	protected final String id;
	protected String info;
	protected String title;
	
	protected ChartEntity( String anId ) {
		id = anId;
	}
	
	public String getId() {
		return id;
	}

	public String getInfo() {
		return info;
	}

	public String getTitle() {
		return title;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	@Override
	public String toString() {
		String comps[] = getClass().getName().split("\\.");
        String toStringExtras = toStringExtras();
        if ( ! toStringExtras.isEmpty() ) {
            toStringExtras = " " + toStringExtras;
        }
		return String.format("[%s id:%s title:%s%s]",
				comps[comps.length-1], getId(), getTitle(), toStringExtras);
	}
	
	/**
	 * Override this if you want to use the default to string, 
	 * but add some of your subclass stuff.
	 * @return String to be included in the {@code toString()} result.
	 */
	protected String toStringExtras() {
		return "";
	}
	
}
