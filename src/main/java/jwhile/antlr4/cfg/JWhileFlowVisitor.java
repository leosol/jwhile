package jwhile.antlr4.cfg;

import java.util.List;

import jwhile.antlr4.cfg.store.BasicNode;
import jwhile.antlr4.cfg.store.CFGProject;
import jwhile.antlr4.cfg.store.FlowNode;
import jwhile.antlr4.cfg.store.LiteralNode;
import jwhile.antlr4.cfg.store.NodeHub;
import jwhile.antlr4.cfg.store.OperationNode;
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
import jwhile.antlr4.generated.WhileParser.ProgramContext;
import jwhile.antlr4.generated.WhileParser.StmtContext;
import jwhile.antlr4.generated.WhileParser.StmtsContext;
import jwhile.antlr4.generated.WhileParser.WhileStatementContext;
import jwhile.antlr4.interpreter.Value;

public class JWhileFlowVisitor extends WhileBaseVisitor<BasicNode> {

	private CFGProject cfgProject;

	public JWhileFlowVisitor(CFGProject cfgProject) {
		this.cfgProject = cfgProject;
	}

	@Override
	public BasicNode visitProgram(ProgramContext ctx) {
		FlowNode start = cfgProject.createProgramStart();
		BasicNode lastButNotLeast = (BasicNode) super.visitProgram(ctx);
		FlowNode end = cfgProject.createProgramEnd();
		if (lastButNotLeast instanceof NodeHub) {
			this.cfgProject.createFlowEdge(start, (FlowNode) ((NodeHub) lastButNotLeast));
			this.cfgProject.createFlowEdge((FlowNode) ((NodeHub) lastButNotLeast), end);
		} else {
			this.cfgProject.createFlowEdge(start, (FlowNode) lastButNotLeast);
			this.cfgProject.createFlowEdge((FlowNode) lastButNotLeast, end);
		}
		return end;
	}

	@Override
	public BasicNode visitStmts(StmtsContext ctx) {
		List<StmtContext> stmts = ctx.stmt();
		FlowNode start = null;
		FlowNode beforeNode = null;
		FlowNode actualNode = null;
		for (StmtContext stmtContext : stmts) {
			actualNode = (FlowNode) visitStmt(stmtContext);
			if (start == null) {
				start = actualNode;
			}
			if (beforeNode != null) {
				this.cfgProject.createFlowEdge(beforeNode, actualNode);
			}
			boolean isConditional = (actualNode instanceof NodeHub && ((NodeHub) actualNode).isConditional());
			if (!isConditional) {
				beforeNode = actualNode;
			} else {
				beforeNode = NodeHub.join(beforeNode, actualNode);
			}
		}
		return new NodeHub(start, actualNode);
	}

	@Override
	public BasicNode visitAssignment(AssignmentContext ctx) {
		FlowNode assignment = this.cfgProject.createStmtFlowNode(ctx.getText());
		BasicNode identifier = this.cfgProject.createIdentifierNode(ctx.Identifier().getText());
		this.cfgProject.createAssignmentEdge(assignment, identifier);
		BasicNode v = visit(ctx.expression());
		if (v != null) {
			this.cfgProject.createUsageEdge(assignment, v);
		}

		return assignment;
	}

	@Override
	public BasicNode visitExpression(ExpressionContext ctx) {
		if (ctx.Identifier() != null) {
			BasicNode identifier = this.cfgProject.createIdentifierNode(ctx.Identifier().getText());
			return identifier;
		} else if (ctx.IntegerLiteral() != null) {
			LiteralNode idNode = this.cfgProject.createLiteralNode(ctx.IntegerLiteral().getText());
			return idNode;
		} else if (ctx.BooleanLiteral() != null) {
			LiteralNode idNode = this.cfgProject.createLiteralNode(ctx.BooleanLiteral().getText());
			return idNode;
		} else if (ctx.NullLiteral() != null) {
			LiteralNode idNode = this.cfgProject.createLiteralNode(ctx.NullLiteral().getText());
			return idNode;
		} else if (ctx.aExp() != null) {
			return visit(ctx.aExp());
		} else if (ctx.bExp() != null) {
			return visit(ctx.bExp());
		}
		return super.visitExpression(ctx);
	}

	@Override
	public BasicNode visitAExp(AExpContext ctx) {
		BasicNode leftValue = visit(ctx.aExpLeftHand());
		BasicNode rightValue = visit(ctx.aExpRightHand());
		OperationNode opNode = null;

		if (ctx.AddOperation() != null) {
			opNode = this.cfgProject.createOperationNode(ctx.getText());
		}
		if (ctx.MulOperation() != null) {
			opNode = this.cfgProject.createOperationNode(ctx.getText());
		}
		if (opNode != null) {
			if (leftValue != null) {
				this.cfgProject.createUsageEdge(opNode, leftValue);
			}
			if (rightValue != null) {
				this.cfgProject.createUsageEdge(opNode, rightValue);
			}
		}

		return opNode;
	}

	@Override
	public BasicNode visitAExpLeftHand(AExpLeftHandContext ctx) {
		if (ctx.IntegerLiteral() != null) {
			LiteralNode idNode = this.cfgProject.createLiteralNode(ctx.IntegerLiteral().getText());
			return idNode;
		}
		if (ctx.Identifier() != null) {
			BasicNode identifier = this.cfgProject.createIdentifierNode(ctx.Identifier().getText());
			return identifier;
		}
		return super.visitAExpLeftHand(ctx);
	}

	@Override
	public BasicNode visitAExpRightHand(AExpRightHandContext ctx) {
		if (ctx.IntegerLiteral() != null) {
			LiteralNode idNode = this.cfgProject.createLiteralNode(ctx.IntegerLiteral().getText());
			return idNode;
		}
		if (ctx.Identifier() != null) {
			BasicNode identifier = this.cfgProject.createIdentifierNode(ctx.Identifier().getText());
			return identifier;
		}
		if (ctx.aExp() != null) {
			return visit(ctx.aExp());
		}
		return super.visitAExpRightHand(ctx);
	}

	@Override
	public BasicNode visitBExp(BExpContext ctx) {
		if (ctx.aExpLeftHand() != null) {
			BasicNode leftValue = visit(ctx.aExpLeftHand());
			BasicNode rightValue = visit(ctx.aExpRightHand());
			OperationNode opNode = null;
			if (ctx.ComparisonOperation() != null) {
				opNode = this.cfgProject.createOperationNode(ctx.getText());
			}
			if (ctx.EqualityOperation() != null) {
				opNode = this.cfgProject.createOperationNode(ctx.getText());
			}
			if (opNode != null) {
				if (leftValue != null) {
					this.cfgProject.createUsageEdge(opNode, leftValue);
				}
				if (rightValue != null) {
					this.cfgProject.createUsageEdge(opNode, rightValue);
				}
			}
			return opNode;

		}
		if (ctx.bExpRightHand() != null) {
			BasicNode leftValue = visit(ctx.bExpLeftHand());
			BasicNode rightValue = visit(ctx.bExpRightHand());
			OperationNode opNode = null;
			if (ctx.EqualityOperation() != null) {
				opNode = this.cfgProject.createOperationNode(ctx.getText());
			}
			if (ctx.BinaryLogicalOperator() != null) {
				opNode = this.cfgProject.createOperationNode(ctx.getText());
			}
			if (opNode != null) {
				if (leftValue != null) {
					this.cfgProject.createUsageEdge(opNode, leftValue);
				}
				if (rightValue != null) {
					this.cfgProject.createUsageEdge(opNode, rightValue);
				}
			}
			return opNode;
		} else {
			BasicNode v = visit(ctx.bExpLeftHand());
			OperationNode opNode = null;
			if (ctx.NotOperator() != null) {
				opNode = this.cfgProject.createOperationNode(ctx.NotOperator().getText());
				this.cfgProject.createUsageEdge(opNode, v);
			} else {
				return v;
			}
		}
		return super.visitBExp(ctx);
	}

	@Override
	public BasicNode visitBExpLeftHand(BExpLeftHandContext ctx) {
		if (ctx.BooleanLiteral() != null) {
			LiteralNode idNode = this.cfgProject.createLiteralNode(ctx.BooleanLiteral().getText());
			return idNode;
		}
		if (ctx.Identifier() != null) {
			BasicNode identifier = this.cfgProject.createIdentifierNode(ctx.Identifier().getText());
			return identifier;
		}
		return super.visitBExpLeftHand(ctx);
	}

	@Override
	public BasicNode visitBExpRightHand(BExpRightHandContext ctx) {
		if (ctx.BooleanLiteral() != null) {
			LiteralNode idNode = this.cfgProject.createLiteralNode(ctx.BooleanLiteral().getText());
			return idNode;
		}
		if (ctx.Identifier() != null) {
			BasicNode identifier = this.cfgProject.createIdentifierNode(ctx.Identifier().getText());
			return identifier;
		}
		if (ctx.bExp() != null) {
			return visit(ctx.bExp());
		}
		return super.visitBExpRightHand(ctx);
	}

	@Override
	public BasicNode visitIfThenStatement(IfThenStatementContext ctx) {
		FlowNode ifThenStatement = this.cfgProject.createStmtFlowNode(ctx.getText());
		BasicNode expNode = visit(ctx.expression());
		this.cfgProject.createUsageEdge(ifThenStatement, expNode);
		NodeHub hub = null;
		if (ctx.ifTrueStmts() != null) {
			FlowNode trueStmts = (FlowNode) visit(ctx.ifTrueStmts());
			if (trueStmts != null && trueStmts instanceof NodeHub) {
				this.cfgProject.createFlowEdge(ifThenStatement, ((NodeHub) trueStmts));
				hub = new NodeHub(ifThenStatement, ((NodeHub) trueStmts).getEndPoints());
			} else {
				this.cfgProject.createFlowEdge(ifThenStatement, (FlowNode) trueStmts);
				hub = new NodeHub(ifThenStatement, trueStmts);
			}
		}
		hub.setConditional(true);
		return hub;
	}

	@Override
	public BasicNode visitIfThenElseStatement(IfThenElseStatementContext ctx) {
		FlowNode ifThenStatement = this.cfgProject.createStmtFlowNode(ctx.getText());
		BasicNode expNode = visit(ctx.expression());
		this.cfgProject.createUsageEdge(ifThenStatement, expNode);
		NodeHub hub = null, hubTrue = null, hubFalse = null;
		if (ctx.ifTrueStmts() != null) {
			FlowNode trueStmts = (FlowNode) visit(ctx.ifTrueStmts());
			if (trueStmts instanceof NodeHub) {
				this.cfgProject.createFlowEdge(ifThenStatement, ((NodeHub) trueStmts));
				hubTrue = new NodeHub(ifThenStatement, ((NodeHub) trueStmts).getEndPoints());
			} else {
				this.cfgProject.createFlowEdge(ifThenStatement, (FlowNode) trueStmts);
				hubTrue = new NodeHub(ifThenStatement, trueStmts);
			}
		}
		if (ctx.ifFalseStmts() != null) {
			FlowNode falseStmts = (FlowNode) visit(ctx.ifFalseStmts());
			if (falseStmts instanceof NodeHub) {
				this.cfgProject.createFlowEdge(ifThenStatement, ((NodeHub) falseStmts));
				hubFalse = new NodeHub(ifThenStatement, ((NodeHub) falseStmts).getEndPoints());
			} else {
				this.cfgProject.createFlowEdge(ifThenStatement, (FlowNode) falseStmts);
				hubFalse = new NodeHub(ifThenStatement, falseStmts);
			}
		}
		hub = NodeHub.join(hubTrue, hubFalse);
		return hub;
	}

	@Override
	public BasicNode visitWhileStatement(WhileStatementContext ctx) {
		FlowNode whileStmt = this.cfgProject.createStmtFlowNode(ctx.getText());
		BasicNode expNode = visit(ctx.expression());
		this.cfgProject.createUsageEdge(whileStmt, expNode);
		FlowNode nodeStmts = (FlowNode)visit(ctx.stmts());
		this.cfgProject.createFlowEdge(whileStmt, nodeStmts);
		this.cfgProject.createFlowEdge(nodeStmts, whileStmt);
		return whileStmt;
	}

}
