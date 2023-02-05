package jwhile.antlr4.cfg.entities;

import java.util.HashSet;
import java.util.Set;

public class TrivialBExp extends BExp {

	private Identifier identifier;
	private Literal literal;

	protected TrivialBExp(Identifier identifier) {
		this(identifier, null);
	}

	protected TrivialBExp(Literal literal) {
		this(null, literal);
	}

	protected TrivialBExp(Identifier identifier, Literal literal) {
		if (identifier == null && literal == null) {
			throw new IllegalArgumentException("Either identifier or literal mus be present");
		}
		this.identifier = identifier;
		this.literal = literal;
	}

	public Identifier getIdentifier() {
		return identifier;
	}

	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}

	public Literal getLiteral() {
		return literal;
	}

	public void setLiteral(Literal literal) {
		this.literal = literal;
	}

	@Override
	public Set<UsageEdge> getUsages() {
		Set<UsageEdge> usages = new HashSet<UsageEdge>();
		if (identifier != null) {
			usages.add(this.getProgram().getEntityFactory().getUsageEdge(this, identifier, IdentifierUsageEdge.class));
		}
		if (literal != null) {
			usages.add(this.getProgram().getEntityFactory().getUsageEdge(this, literal, LiteralUsageEdge.class));
		}
		return usages;
	}

	public String getText() {
		if (identifier != null)
			return identifier.getText();
		if (literal != null)
			return literal.getText();
		return "";
	}

	@Override
	public String toString() {
		Entity target = identifier == null ? literal : identifier;
		return getName() + " " + target;
	}
}
