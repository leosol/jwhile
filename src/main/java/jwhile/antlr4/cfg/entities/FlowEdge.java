package jwhile.antlr4.cfg.entities;

public class FlowEdge extends Edge {

	private Label from;
	private Label to;

	protected FlowEdge(Label from, Label to) {
		this.from = from;
		this.to = to;
	}

	public Label getFrom() {
		return from;
	}

	public void setFrom(Label from) {
		this.from = from;
	}

	public Label getTo() {
		return to;
	}

	public void setTo(Label to) {
		this.to = to;
	}

	@Override
	public String toString() {
		return getName()+" from: " + this.from + " to: " + this.to;
	}

}
