package jwhile.antlr4.cfg.util;

import jwhile.antlr4.cfg.entities.AssignmentEdge;
import jwhile.antlr4.cfg.entities.AssignmentStmt;
import jwhile.antlr4.cfg.entities.Condition;
import jwhile.antlr4.cfg.entities.Entity;
import jwhile.antlr4.cfg.entities.ExpressionUsageEdge;
import jwhile.antlr4.cfg.entities.FlowEdge;
import jwhile.antlr4.cfg.entities.Identifier;
import jwhile.antlr4.cfg.entities.IdentifierUsageEdge;
import jwhile.antlr4.cfg.entities.IfStmt;
import jwhile.antlr4.cfg.entities.Literal;
import jwhile.antlr4.cfg.entities.LiteralUsageEdge;
import jwhile.antlr4.cfg.entities.NonTrivialAExp;
import jwhile.antlr4.cfg.entities.NonTrivialBooleanExpression;
import jwhile.antlr4.cfg.entities.NonTrivialComparisonExpression;
import jwhile.antlr4.cfg.entities.SeqStmt;
import jwhile.antlr4.cfg.entities.SkipStmt;
import jwhile.antlr4.cfg.entities.TrivialAExp;
import jwhile.antlr4.cfg.entities.TrivialBExp;
import jwhile.antlr4.cfg.entities.WhileStmt;

public class EntitySwitcher {

	public EntitySwitcher(Entity entity) {
		if (entity == null) {
			this.caseNull();
			return;
		}
		if (entity instanceof Identifier) {
			this.caseIdentifier((Identifier) entity);
			return;
		}
		if (entity instanceof Literal) {
			this.caseLiteral((Literal) entity);
			return;
		}
		if (entity instanceof AssignmentStmt) {
			this.caseAssignmentStmt((AssignmentStmt) entity);
			return;
		}
		if (entity instanceof IfStmt) {
			this.caseIfStmt((IfStmt) entity);
			return;
		}
		if (entity instanceof SeqStmt) {
			this.caseSeqStmt((SeqStmt) entity);
			return;
		}
		if (entity instanceof WhileStmt) {
			this.caseWhileStmt((WhileStmt) entity);
			return;
		}
		if (entity instanceof TrivialAExp) {
			this.caseTrivialAExp((TrivialAExp) entity);
			return;
		}
		if (entity instanceof NonTrivialAExp) {
			this.caseNonTrivialAExp((NonTrivialAExp) entity);
			return;
		}
		if (entity instanceof TrivialBExp) {
			this.caseTrivialBExp((TrivialBExp) entity);
			return;
		}
		if (entity instanceof NonTrivialBooleanExpression) {
			this.caseNonTrivialBooleanExpression((NonTrivialBooleanExpression) entity);
			return;
		}
		if (entity instanceof NonTrivialComparisonExpression) {
			this.caseNonTrivialComparisonExpression((NonTrivialComparisonExpression) entity);
			return;
		}
		if (entity instanceof SkipStmt) {
			this.caseSkipStmt((SkipStmt) entity);
			return;
		}
		if (entity instanceof Condition) {
			this.caseCondition((Condition) entity);
			return;
		}
		if(entity instanceof FlowEdge ) {
			this.caseFlowEdge((FlowEdge) entity);
			return;
		}
		if(entity instanceof AssignmentEdge) {
			this.caseAssignmentEdge((AssignmentEdge) entity);
			return;
		}
		if(entity instanceof ExpressionUsageEdge) {
			this.caseExpressionUsageEdge((ExpressionUsageEdge) entity);
			return;
		}
		if(entity instanceof IdentifierUsageEdge) {
			this.caseIdentifierUsageEdge((IdentifierUsageEdge) entity);
			return;
		}
		if(entity instanceof LiteralUsageEdge) {
			this.caseLiteralUsageEdge((LiteralUsageEdge) entity);
			return;
		}
	}

	public void caseLiteralUsageEdge(LiteralUsageEdge entity) {
	}

	public void caseIdentifierUsageEdge(IdentifierUsageEdge entity) {
	}

	public void caseExpressionUsageEdge(ExpressionUsageEdge entity) {
	}

	public void caseAssignmentEdge(AssignmentEdge entity) {
	}

	public void caseNonTrivialComparisonExpression(NonTrivialComparisonExpression entity) {
	};

	public void caseNonTrivialBooleanExpression(NonTrivialBooleanExpression entity) {
	};

	public void caseFlowEdge(FlowEdge entity) {
	};

	public void caseCondition(Condition entity) {
	};

	public void caseSkipStmt(SkipStmt entity) {
	};

	public void caseNull() {
	};

	public void caseLiteral(Literal l) {
	};

	public void caseAssignmentStmt(AssignmentStmt a) {
	};

	public void caseIfStmt(IfStmt i) {
	};

	public void caseSeqStmt(SeqStmt seq) {
	};

	public void caseWhileStmt(WhileStmt w) {
	};

	public void caseTrivialAExp(TrivialAExp t) {
	};

	public void caseNonTrivialAExp(NonTrivialAExp c) {
	};

	public void caseTrivialBExp(TrivialBExp t) {
	};

	public void caseIdentifier(Identifier i) {
	};

}
