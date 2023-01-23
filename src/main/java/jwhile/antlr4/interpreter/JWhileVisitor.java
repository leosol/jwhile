package jwhile.antlr4.interpreter;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jwhile.antlr4.generated.WhileBaseVisitor;
import jwhile.antlr4.generated.WhileParser.AExpContext;
import jwhile.antlr4.generated.WhileParser.AExpLeftHandContext;
import jwhile.antlr4.generated.WhileParser.AExpRightHandContext;
import jwhile.antlr4.generated.WhileParser.AssignmentContext;
import jwhile.antlr4.generated.WhileParser.BExpContext;
import jwhile.antlr4.generated.WhileParser.BExpLeftHandContext;
import jwhile.antlr4.generated.WhileParser.BExpRightHandContext;
import jwhile.antlr4.generated.WhileParser.ExpressionContext;
import jwhile.antlr4.generated.WhileParser.IfThenElseStatementContext;
import jwhile.antlr4.generated.WhileParser.IfThenStatementContext;
import jwhile.antlr4.generated.WhileParser.WhileStatementContext;

public class JWhileVisitor extends WhileBaseVisitor<Value> {

	private static Logger logger = LoggerFactory.getLogger(JWhileVisitor.class);
	private TreeMap<String, Value> memory = new TreeMap<String, Value>();

	private Value getMem(String variable) {
		if (!memory.containsKey(variable)) {
			throw new IllegalStateException("Variable " + variable + " is referenced before used");
		}
		return memory.get(variable);
	}

	private void setMem(String variable, Value v) {
		memory.put(variable, v);
	}

	public void dumpMem() {
		Set<Map.Entry<String, Value>> entries = memory.entrySet();
		for (Map.Entry<String, Value> entry : entries) {
			logger.info("MemoryDump: \t " + entry.getKey() + "=>" + entry.getValue());
		}
	}

	public Map<String, Value> getMem() {
		return this.memory;
	}

	@Override
	public Value visitAssignment(AssignmentContext ctx) {
		String variableName = ctx.Identifier().getText();
		Value v = visit(ctx.expression());
		setMem(variableName, v);
		return v;
	}

	@Override
	public Value visitExpression(ExpressionContext ctx) {
		if (ctx.Identifier() != null) {
			return getMem(ctx.Identifier().getText());
		} else if (ctx.IntegerLiteral() != null) {
			return new Value(Integer.parseInt(ctx.IntegerLiteral().getText()));
		} else if (ctx.BooleanLiteral() != null) {
			return new Value(Boolean.parseBoolean(ctx.BooleanLiteral().getText()));
		} else if (ctx.NullLiteral() != null) {
			return new Value(null);
		} else if (ctx.aExp() != null) {
			return visit(ctx.aExp());
		} else if (ctx.bExp() != null) {
			return visit(ctx.bExp());
		}
		return super.visitExpression(ctx);
	}

	@Override
	public Value visitAExp(AExpContext ctx) {
		Value leftValue = visit(ctx.aExpLeftHand());
		Value rightValue = visit(ctx.aExpRightHand());
		if (ctx.AddOperation() != null) {
			if (ctx.AddOperation().getText().equals("+")) {
				return new Value(leftValue.asInteger() + rightValue.asInteger());
			}
			if (ctx.AddOperation().getText().equals("-")) {
				return new Value(leftValue.asInteger() - rightValue.asInteger());
			}
		}
		if (ctx.MulOperation() != null) {
			if (ctx.MulOperation().getText().equals("*")) {
				return new Value(leftValue.asInteger() * rightValue.asInteger());
			}
			if (ctx.MulOperation().getText().equals("/")) {
				return new Value(leftValue.asInteger() / rightValue.asInteger());
			}
			if (ctx.MulOperation().getText().equals("%")) {
				return new Value(leftValue.asInteger() % rightValue.asInteger());
			}
		}

		return super.visitAExp(ctx);
	}

	@Override
	public Value visitBExp(BExpContext ctx) {
		if (ctx.aExpLeftHand() != null) {
			Value leftValue = visit(ctx.aExpLeftHand());
			Value rightValue = visit(ctx.aExpRightHand());
			if (ctx.ComparisonOperation() != null) {
				switch (ctx.ComparisonOperation().getText()) {
				case ">":
					return new Value(leftValue.asInteger() > rightValue.asInteger());
				case ">=":
					return new Value(leftValue.asInteger() >= rightValue.asInteger());
				case "<":
					return new Value(leftValue.asInteger() < rightValue.asInteger());
				case "<=":
					return new Value(leftValue.asInteger() <= rightValue.asInteger());
				}
			}
			if (ctx.EqualityOperation() != null) {
				switch (ctx.EqualityOperation().getText()) {
				case "==":
					return new Value(Value.isEqual(leftValue, rightValue));
				case "!=":
					return new Value(!Value.isEqual(leftValue, rightValue));
				}
			}
		}
		if (ctx.bExpRightHand() != null) {
			Value leftValue = visit(ctx.bExpLeftHand());
			Value rightValue = visit(ctx.bExpRightHand());
			if (ctx.EqualityOperation() != null) {
				switch (ctx.EqualityOperation().getText()) {
				case "==":
					return new Value(leftValue.asBoolean() == rightValue.asBoolean());
				case "!=":
					return new Value(leftValue.asBoolean() != rightValue.asBoolean());
				}
			}
			if (ctx.BinaryLogicalOperator() != null) {
				switch (ctx.BinaryLogicalOperator().getText()) {
				case "&&":
					return new Value(leftValue.asBoolean() && rightValue.asBoolean());
				case "||":
					return new Value(leftValue.asBoolean() || rightValue.asBoolean());
				}
			}
		} else {
			Value v = visit(ctx.bExpLeftHand());
			if (ctx.NotOperator() != null) {
				return new Value(!v.asBoolean());
			} else {
				return v;
			}
		}
		return super.visitBExp(ctx);
	}

	@Override
	public Value visitAExpLeftHand(AExpLeftHandContext ctx) {
		if (ctx.IntegerLiteral() != null) {
			return new Value(Integer.parseInt(ctx.IntegerLiteral().getText()));
		}
		if (ctx.Identifier() != null) {
			return new Value(getMem(ctx.Identifier().getText()).value);
		}
		return super.visitAExpLeftHand(ctx);
	}

	@Override
	public Value visitAExpRightHand(AExpRightHandContext ctx) {
		if (ctx.IntegerLiteral() != null) {
			return new Value(Integer.parseInt(ctx.IntegerLiteral().getText()));
		}
		if (ctx.Identifier() != null) {
			return new Value(getMem(ctx.Identifier().getText()).value);
		}
		if (ctx.aExp() != null) {
			return visit(ctx.aExp());
		}
		return super.visitAExpRightHand(ctx);
	}

	@Override
	public Value visitBExpLeftHand(BExpLeftHandContext ctx) {
		if (ctx.BooleanLiteral() != null) {
			return new Value(Boolean.parseBoolean(ctx.BooleanLiteral().getText()));
		}
		if (ctx.Identifier() != null) {
			return new Value(getMem(ctx.Identifier().getText()).value);
		}
		return super.visitBExpLeftHand(ctx);
	}

	@Override
	public Value visitBExpRightHand(BExpRightHandContext ctx) {
		if (ctx.BooleanLiteral() != null) {
			return new Value(Boolean.parseBoolean(ctx.BooleanLiteral().getText()));
		}
		if (ctx.Identifier() != null) {
			return new Value(getMem(ctx.Identifier().getText()).value);
		}
		if (ctx.bExp() != null) {
			return visit(ctx.bExp());
		}
		return super.visitBExpRightHand(ctx);
	}

	@Override
	public Value visitIfThenStatement(IfThenStatementContext ctx) {
		Value v = visit(ctx.expression());
		if (v.isTruthyValue()) {
			return visit(ctx.ifTrueStmts());
		}
		return new Value(0);
	}

	@Override
	public Value visitIfThenElseStatement(IfThenElseStatementContext ctx) {
		Value v = visit(ctx.expression());
		if (v.isTruthyValue()) {
			return visit(ctx.ifTrueStmts());
		} else {
			return visit(ctx.ifFalseStmts());
		}
	}

	@Override
	public Value visitWhileStatement(WhileStatementContext ctx) {
		Value v = new Value(0);
		while (visit(ctx.expression()).isTruthyValue()) {
			v = visit(ctx.stmts());
		}
		return v;
	}

}
