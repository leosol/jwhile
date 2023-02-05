package jwhile.antlr4.cfg.visitors;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import jwhile.antlr4.cfg.entities.AExp;
import jwhile.antlr4.cfg.entities.AssignmentStmt;
import jwhile.antlr4.cfg.entities.BExp;
import jwhile.antlr4.cfg.entities.Entity;
import jwhile.antlr4.cfg.entities.Exp;
import jwhile.antlr4.cfg.entities.Identifier;
import jwhile.antlr4.cfg.entities.Literal;
import jwhile.antlr4.cfg.entities.LiteralType;
import jwhile.antlr4.cfg.entities.Program;
import jwhile.antlr4.cfg.entities.SeqStmt;
import jwhile.antlr4.cfg.entities.SkipStmt;
import jwhile.antlr4.cfg.entities.Stmt;
import jwhile.antlr4.generated.WhileBaseVisitor;
import jwhile.antlr4.generated.WhileParser.AExpContext;
import jwhile.antlr4.generated.WhileParser.AExpLeftHandContext;
import jwhile.antlr4.generated.WhileParser.AExpRightHandContext;
import jwhile.antlr4.generated.WhileParser.AssignmentContext;
import jwhile.antlr4.generated.WhileParser.BExpContext;
import jwhile.antlr4.generated.WhileParser.BExpLeftHandContext;
import jwhile.antlr4.generated.WhileParser.BExpRightHandContext;
import jwhile.antlr4.generated.WhileParser.ExpressionContext;
import jwhile.antlr4.generated.WhileParser.ProgramContext;
import jwhile.antlr4.generated.WhileParser.StmtContext;
import jwhile.antlr4.generated.WhileParser.StmtsContext;

public class JWhileProgramParser extends WhileBaseVisitor<Entity> {

	private Program program;

	public JWhileProgramParser() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		LocalDateTime now = LocalDateTime.now();
		this.program = new Program("PRJ-" + dtf.format(now));
	}

	@Override
	public Entity visitProgram(ProgramContext ctx) {
		SkipStmt start = program.getEntityFactory().createSkipStmt("START");
		Stmt middle = (Stmt) super.visitProgram(ctx);
		SkipStmt end = program.getEntityFactory().createSkipStmt("END");
		SeqStmt sStartToMiddle = program.getEntityFactory().createSeqStmt(start, middle);
		SeqStmt middleToEnd = program.getEntityFactory().createSeqStmt(sStartToMiddle, end);
		program.setStart(middleToEnd);
		return middleToEnd;
	}

	@Override
	public Entity visitStmts(StmtsContext ctx) {
		List<StmtContext> stmts = ctx.stmt();
		Stmt previous = null;
		for (StmtContext stmtCtxts : stmts) {
			Stmt stmt = (Stmt) visitStmt(stmtCtxts);
			if (previous != null) {
				previous = program.getEntityFactory().createSeqStmt(previous, stmt);
			} else {
				previous = stmt;
			}
		}
		return previous;
	}

	@Override
	public Entity visitStmt(StmtContext ctx) {
		return super.visitStmt(ctx);
	}

	@Override
	public Entity visitAssignment(AssignmentContext ctx) {
		Identifier identifier = null;
		Exp expression = null;
		if (ctx.Identifier() != null) {
			identifier = program.getEntityFactory().getIdentifier(ctx.Identifier().getText());
		}
		if (ctx.expression() != null) {
			expression = (Exp) this.visit(ctx.expression());
		}
		AssignmentStmt assignmentStmt = program.getEntityFactory().createAssignmentStmt(identifier, expression);
		assignmentStmt.setText(ctx.getText());
		return assignmentStmt;
	}

	@Override
	public Entity visitExpression(ExpressionContext ctx) {
		if (ctx.Identifier() != null) {
			Identifier identifier = program.getEntityFactory().getIdentifier(ctx.Identifier().getText());
			return program.getEntityFactory().createTrivialAExp(identifier);
		} else if (ctx.IntegerLiteral() != null) {
			Literal literal = program.getEntityFactory().getLiteral(ctx.IntegerLiteral().getText(),
					LiteralType.IntegerLiteral);
			return program.getEntityFactory().createTrivialAExp(literal);
		} else if (ctx.BooleanLiteral() != null) {
			Literal literal = program.getEntityFactory().getLiteral(ctx.BooleanLiteral().getText(),
					LiteralType.BooleanLiteral);
			return program.getEntityFactory().createTrivialBExp(literal);
		} else if (ctx.NullLiteral() != null) {
			Literal literal = program.getEntityFactory().getLiteral(ctx.NullLiteral().getText(),
					LiteralType.NullLiteral);
			return program.getEntityFactory().createTrivialAExp(literal);
		} else if (ctx.aExp() != null) {
			return visit(ctx.aExp());
		} else if (ctx.bExp() != null) {
			return visit(ctx.bExp());
		}
		return super.visitExpression(ctx);
	}

	@Override
	public Entity visitAExp(AExpContext ctx) {
		AExp leftValue = (AExp) visit(ctx.aExpLeftHand());
		AExp rightValue = (AExp) visit(ctx.aExpRightHand());
		if (ctx.AddOperation() != null) {
			return program.getEntityFactory().createNonTrivialAExp(ctx.AddOperation().getText(), leftValue, rightValue,
					ctx.getText());
		}
		if (ctx.MulOperation() != null) {
			return program.getEntityFactory().createNonTrivialAExp(ctx.MulOperation().getText(), leftValue, rightValue,
					ctx.getText());
		}
		return super.visit(ctx);
	}

	@Override
	public Entity visitAExpLeftHand(AExpLeftHandContext ctx) {
		if (ctx.IntegerLiteral() != null) {
			Literal literal = program.getEntityFactory().getLiteral(ctx.IntegerLiteral().getText(),
					LiteralType.IntegerLiteral);
			return program.getEntityFactory().createTrivialAExp(literal);
		}
		if (ctx.Identifier() != null) {
			Identifier identifier = program.getEntityFactory().getIdentifier(ctx.Identifier().getText());
			return program.getEntityFactory().createTrivialAExp(identifier);
		}
		return super.visitAExpLeftHand(ctx);
	}

	@Override
	public Entity visitAExpRightHand(AExpRightHandContext ctx) {
		if (ctx.IntegerLiteral() != null) {
			Literal literal = program.getEntityFactory().getLiteral(ctx.IntegerLiteral().getText(),
					LiteralType.IntegerLiteral);
			return program.getEntityFactory().createTrivialAExp(literal);
		}
		if (ctx.Identifier() != null) {
			Identifier identifier = program.getEntityFactory().getIdentifier(ctx.Identifier().getText());
			return program.getEntityFactory().createTrivialAExp(identifier);
		}
		if (ctx.aExp() != null) {
			return visit(ctx.aExp());
		}
		return super.visitAExpRightHand(ctx);
	}

	@Override
	public Entity visitBExp(BExpContext ctx) {
		if (ctx.aExpLeftHand() != null) {
			AExp leftValue = (AExp) visit(ctx.aExpLeftHand());
			AExp rightValue = (AExp) visit(ctx.aExpRightHand());
			if (ctx.ComparisonOperation() != null) {
				return program.getEntityFactory().createNonTrivialComparisonExpression(
						ctx.ComparisonOperation().getText(), leftValue, rightValue, ctx.getText());
			}
			if (ctx.EqualityOperation() != null) {
				return program.getEntityFactory().createNonTrivialComparisonExpression(
						ctx.EqualityOperation().getText(), leftValue, rightValue, ctx.getText());
			}
		}
		if (ctx.bExpRightHand() != null) {
			BExp leftValue = (BExp) visit(ctx.bExpLeftHand());
			BExp rightValue = (BExp) visit(ctx.bExpRightHand());
			if (ctx.EqualityOperation() != null) {
				return program.getEntityFactory().createNonTrivialComparisonExpression(
						ctx.EqualityOperation().getText(), leftValue, rightValue, ctx.getText());
			}
			if (ctx.BinaryLogicalOperator() != null) {
				return program.getEntityFactory().createNonTrivialBooleanExpression(
						ctx.BinaryLogicalOperator().getText(), leftValue, rightValue, ctx.getText());
			}
		} else {
			BExp exp = (BExp) visit(ctx.bExpLeftHand());
			if (ctx.NotOperator() != null) {
				return program.getEntityFactory().createNonTrivialBooleanExpression(ctx.NotOperator().getText(), null,
						exp, ctx.getText());
			} else {
				return exp;
			}
		}
		return super.visitBExp(ctx);
	}

	@Override
	public Entity visitBExpLeftHand(BExpLeftHandContext ctx) {
		if (ctx.BooleanLiteral() != null) {
			Literal literal = program.getEntityFactory().getLiteral(ctx.BooleanLiteral().getText(),
					LiteralType.BooleanLiteral);
			return program.getEntityFactory().createTrivialBExp(literal);
		}
		if (ctx.Identifier() != null) {
			Identifier identifier = program.getEntityFactory().getIdentifier(ctx.Identifier().getText());
			return program.getEntityFactory().createTrivialBExp(identifier);
		}
		return super.visitBExpLeftHand(ctx);
	}

	@Override
	public Entity visitBExpRightHand(BExpRightHandContext ctx) {
		if (ctx.BooleanLiteral() != null) {
			Literal literal = program.getEntityFactory().getLiteral(ctx.BooleanLiteral().getText(),
					LiteralType.BooleanLiteral);
			return program.getEntityFactory().createTrivialBExp(literal);
		}
		if (ctx.Identifier() != null) {
			Identifier identifier = program.getEntityFactory().getIdentifier(ctx.Identifier().getText());
			return program.getEntityFactory().createTrivialBExp(identifier);
		}
		if (ctx.bExp() != null) {
			return visit(ctx.bExp());
		}
		return super.visitBExpRightHand(ctx);
	}

	public Program getProgram() {
		return program;
	}

}
