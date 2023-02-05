package jwhile.antlr4.cfg.store;

import org.neo4j.driver.types.Relationship;

public class BasicEdge {
	private BasicNode from;
	private BasicNode to;
	private Relationship relationship;

	public BasicEdge(BasicNode from, BasicNode to) {
		this.from = from;
		this.to = to;
	}

	public BasicNode getFrom() {
		return from;
	}

	public void setFrom(BasicNode from) {
		this.from = from;
	}

	public BasicNode getTo() {
		return to;
	}

	public void setTo(BasicNode to) {
		this.to = to;
	}

	public Relationship getRelationship() {
		return relationship;
	}

	public void setRelationship(Relationship relationship) {
		this.relationship = relationship;
	}
}
