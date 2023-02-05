package jwhile.antlr4.cfg.entities;

import java.util.LinkedList;
import java.util.List;

public class SkipStmt extends Stmt implements LabledEntity {

	private Label label;

	protected SkipStmt(Label label) {
		this(label, "skip");
	}

	protected SkipStmt(Label label, String text) {
		this.label = label;
		setText(text);
	}

	@Override
	public Label getLabel() {
		return label;
	}

	@Override
	public Label getInitLabel() {
		return this.label;
	}

	@Override
	public List<Label> getFinalLabels() {
		List<Label> finals = new LinkedList<Label>();
		finals.add(this.label);
		return finals;
	}

	@Override
	public List<FlowEdge> flow() {
		return new LinkedList<FlowEdge>();
	}

}
