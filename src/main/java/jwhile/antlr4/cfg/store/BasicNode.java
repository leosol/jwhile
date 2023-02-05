package jwhile.antlr4.cfg.store;

import org.neo4j.driver.types.Node;

public class BasicNode {
	private String label;
	private String text;
	private Node node;
	
	public BasicNode(String label, String text) {
		this.label = label;
		this.text = text;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

}
