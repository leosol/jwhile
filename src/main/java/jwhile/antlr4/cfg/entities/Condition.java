package jwhile.antlr4.cfg.entities;

import java.util.HashSet;
import java.util.Set;

public class Condition extends Entity implements LabledEntity {

	private Label label;
	private String text;

	protected Condition(Label label, String text) {
		this.label = label;
		this.text = text;
	}

	@Override
	public Label getLabel() {
		return this.label;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setLabel(Label label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return getName() + " label: " + label + " text " + text;
	}

	@Override
	public Set<UsageEdge> getUsages() {
		Set<UsageEdge> usages = new HashSet<UsageEdge>();
		return usages;
	}
	
}
