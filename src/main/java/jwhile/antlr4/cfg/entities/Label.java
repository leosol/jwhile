package jwhile.antlr4.cfg.entities;

import org.neo4j.driver.types.Node;

public class Label {

	private long intLabel;

	private String contextLabel;

	public Label(long intLabel, String contextLabel) {
		this.intLabel = intLabel;
		this.contextLabel = contextLabel;
	}

	public long getIntLabel() {
		return intLabel;
	}

	public String getContextLabel() {
		return contextLabel;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Label)) {
			return false;
		}
		return this.hashCode() == ((Label) obj).hashCode();
	}

	@Override
	public int hashCode() {
		return (this.contextLabel + this.intLabel).hashCode();
	}

	@Override
	public String toString() {
		return contextLabel;
	}

	public static Label fromNode(Node n) {
		long intLabel = n.get("intLabel").asLong();
		String contextLabel = n.get("contextLabel").asString();
		return new Label(intLabel, contextLabel);
	}

}
