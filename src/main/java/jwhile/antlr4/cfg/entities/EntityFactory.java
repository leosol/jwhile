package jwhile.antlr4.cfg.entities;

import java.util.HashMap;
import java.util.Map;

import org.javatuples.Triplet;

import jwhile.antlr4.cfg.util.LabelGenerator;

public class EntityFactory {

	private LabelGenerator labelGenerator = new LabelGenerator();
	private Map<Label, Entity> labelCatalog = new HashMap<Label, Entity>();
	private Map<String, Identifier> identifierCatalog = new HashMap<String, Identifier>();
	private Map<String, Literal> literalCatalog = new HashMap<String, Literal>();
	private Map<Triplet<Entity, Entity, String>, UsageEdge> usageEdgesCatalog = new HashMap<Triplet<Entity, Entity, String>, UsageEdge>();

	public UsageEdge getUsageEdge(Entity from, Entity to, String qualifier, Class<? extends UsageEdge> clazz) {
		Triplet<Entity, Entity, String> entry = new Triplet<Entity, Entity, String>(from, to, qualifier);
		UsageEdge edge = this.usageEdgesCatalog.get(entry);
		if (edge == null) {
			try {
				edge = (UsageEdge) clazz.getConstructor().newInstance();
				edge.setFrom(from);
				edge.setTo(to);
				edge.setQualifier(qualifier);
				this.usageEdgesCatalog.put(entry, edge);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return edge;
	}

	public UsageEdge getUsageEdge(Entity from, Entity to, Class<? extends UsageEdge> clazz) {
		return this.getUsageEdge(from, to, "", clazz);
	}

	public Entity getEntity(Label label) {
		Entity e = this.labelCatalog.get(label);
		if (e == null) {
			throw new NullPointerException("Entity for label " + label + " shouldnt be null");
		}
		return e;
	}

	public Literal getLiteral(String text, LiteralType type) {
		Literal literal = literalCatalog.get(text);
		if (literal == null) {
			literal = new Literal(type, text);
		}
		return literal;
	}

	public Identifier getIdentifier(String text) {
		Identifier identifier = identifierCatalog.get(text);
		if (identifier == null) {
			identifier = new Identifier(text);
		}
		return identifier;
	}

	public SkipStmt createSkipStmt(String text) {
		SkipStmt skip = new SkipStmt(labelGenerator.getNextLabel("skip"), text);
		this.labelCatalog.put(skip.getLabel(), skip);
		return skip;
	}

	public SeqStmt createSeqStmt(Stmt s1, Stmt s2) {
		return new SeqStmt(s1, s2);
	}

	public TrivialAExp createTrivialAExp(Identifier identifier) {
		return new TrivialAExp(identifier);
	}

	public TrivialAExp createTrivialAExp(Literal literal) {
		return new TrivialAExp(literal);
	}

	public TrivialBExp createTrivialBExp(Identifier identifier) {
		return new TrivialBExp(identifier);
	}

	public TrivialBExp createTrivialBExp(Literal literal) {
		return new TrivialBExp(literal);
	}

	public NonTrivialAExp createNonTrivialAExp(String operation, AExp leftValue, AExp rightValue, String text) {
		return new NonTrivialAExp(operation, leftValue, rightValue, text);
	}

	public NonTrivialComparisonExpression createNonTrivialComparisonExpression(String operation, Exp leftValue,
			Exp rightValue, String text) {
		return new NonTrivialComparisonExpression(operation, leftValue, rightValue, text);
	}

	public NonTrivialBooleanExpression createNonTrivialBooleanExpression(String operation, BExp leftValue,
			BExp rightValue, String text) {
		return new NonTrivialBooleanExpression(operation, leftValue, rightValue, text);
	}

	public AssignmentStmt createAssignmentStmt(Identifier identifier, Exp expression) {
		AssignmentStmt a = new AssignmentStmt(this.labelGenerator.getNextLabel("assignment"), identifier, expression);
		this.labelCatalog.put(a.getLabel(), a);
		return a;
	}
	
	public IfStmt createIfStmt(Exp expression, String expressionText, Stmt trueBranch, Stmt falseBranch) {
		Label label = this.labelGenerator.getNextLabel("condition");
		Condition c = new Condition(label, expression, expressionText);
		this.labelCatalog.put(label, c);
		IfStmt ifStmt = new IfStmt(c, trueBranch, falseBranch);
		return ifStmt;
	}

	public WhileStmt createWhileStmt(Exp expression, String expressionText, Stmt body) {
		Label label = this.labelGenerator.getNextLabel("condition");
		Condition c = new Condition(label, expression, expressionText);
		this.labelCatalog.put(label, c);
		WhileStmt res = new WhileStmt(c, body);
		return res;
	}

}
