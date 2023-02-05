package jwhile.antlr4.cfg.entities;

import java.util.LinkedList;
import java.util.List;

public class WhileStmt extends Stmt implements FlowEntity {

	private Condition c;
	private Stmt body;

	protected WhileStmt(Condition c, Stmt body) {
		this.c = c;
		this.body = body;
	}

	public Condition getC() {
		return c;
	}

	public Stmt getBody() {
		return body;
	}

	@Override
	public Label getInitLabel() {
		return this.c.getLabel();
	}

	@Override
	public List<Label> getFinalLabels() {
		List<Label> finals = new LinkedList<Label>();
		finals.add(getInitLabel());
//		finals.addAll(body.getFinalLabels());
		return finals;
	}

	@Override
	public List<FlowEdge> flow() {
		List<FlowEdge> edges = new LinkedList<FlowEdge>();
		edges.addAll(this.getBody().flow());
		Label l = this.c.getLabel();
		edges.add(new FlowEdge(l, body.getInitLabel()));
		List<Label> finals = this.body.getFinalLabels();
		for (Label label : finals) {
			edges.add(new FlowEdge(label, l));
		}
		return edges;
	}

	@Override
	public String toString() {
		return getName() + " " + c + " " + body;
	}

}
