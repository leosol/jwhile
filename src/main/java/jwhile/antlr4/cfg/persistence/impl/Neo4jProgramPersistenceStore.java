package jwhile.antlr4.cfg.persistence.impl;

import static org.neo4j.driver.Values.parameters;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.javatuples.Triplet;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Logging;
import org.neo4j.driver.Query;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.Value;
import org.neo4j.driver.util.Pair;

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
import jwhile.antlr4.cfg.entities.Program;
import jwhile.antlr4.cfg.entities.SkipStmt;
import jwhile.antlr4.cfg.entities.TrivialAExp;
import jwhile.antlr4.cfg.entities.TrivialBExp;
import jwhile.antlr4.cfg.entities.UsageEdge;
import jwhile.antlr4.cfg.entities.WhileStmt;
import jwhile.antlr4.cfg.persistence.ProgramPersistenceStore;
import jwhile.antlr4.cfg.util.EntitySwitcher;

public class Neo4jProgramPersistenceStore implements ProgramPersistenceStore {

	private String url;
	private String user;
	private String password;
	private Driver driver;
	private Session session;

	public Neo4jProgramPersistenceStore(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
	}

	@Override
	public void openSession() {
		Config config = Config.builder().withLogging(Logging.console(Level.ALL)).build();
		driver = GraphDatabase.driver(url, AuthTokens.basic(user, password), config);
		session = driver.session();
	}

	@Override
	public void closeSession() {
		session.close();
		driver.close();
	}

	@Override
	public void persistNewProgram(Program program) {
		Triplet<List<FlowEdge>, Set<Entity>, Set<UsageEdge>> triplet = program.getProgramData();
		this.createProgram(program);
		Set<Entity> entities = triplet.getValue1();
		for (Entity entity : entities) {
			this.createEntity(entity);
		}
		List<FlowEdge> flows = triplet.getValue0();
		for (FlowEdge flowEdge : flows) {
			this.createEntity(flowEdge);
		}
		Set<UsageEdge> usages = triplet.getValue2();
		for (UsageEdge usageEdge : usages) {
			this.createEntity(usageEdge);
		}
	}

	public void createProgram(Program program) {
		Transaction t0 = session.beginTransaction();
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:Program) ON n.programId");
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:Program) ON n.analyst");
		t0.commit();
		t0.close();
		Transaction t = session.beginTransaction();
		Query q1 = new Query("CREATE (n:Program) SET n.programId=$programId, n.analyst=$analyst return n",
				parameters("programId", program.getProgramId(), "analyst", program.getAnalyst()));
		Result res = t.run(q1);
		boolean okay = false;
		if (res.hasNext()) {
			Record record = res.next();
			List<Pair<String, Value>> values = record.fields();
			Pair<String, Value> item = values.get(0);
			if ("n".equals(item.key())) {
				okay = true;
			}
		}
		if (!okay) {
			throw new IllegalStateException("Error running query");
		}
		t.commit();
		t.close();
	}

	public void createEntity(Entity entity) {
		new EntitySwitcher(entity) {
			@Override
			public void caseSkipStmt(SkipStmt stmt) {
				Neo4jProgramPersistenceStore.this.createSkipStmt(stmt);
			}

			@Override
			public void caseLiteral(Literal l) {
				Neo4jProgramPersistenceStore.this.createLiteral(l);
			}

			@Override
			public void caseAssignmentStmt(AssignmentStmt stmt) {
				Neo4jProgramPersistenceStore.this.createAssignmentStmt(stmt);
			}

			@Override
			public void caseIfStmt(IfStmt stmt) {
//				Neo4jProgramPersistenceStore.this.createIfStmt(stmt);
			}

			@Override
			public void caseWhileStmt(WhileStmt stmt) {
//				Neo4jProgramPersistenceStore.this.createWhileStmt(stmt);
			}

			@Override
			public void caseTrivialAExp(TrivialAExp t) {
				Neo4jProgramPersistenceStore.this.createTrivialAExp(t);
			}

			@Override
			public void caseNonTrivialAExp(NonTrivialAExp nt) {
				Neo4jProgramPersistenceStore.this.createNonTrivialAExp(nt);
			}

			@Override
			public void caseTrivialBExp(TrivialBExp t) {
				Neo4jProgramPersistenceStore.this.createTrivialBExp(t);
			}

			@Override
			public void caseNonTrivialComparisonExpression(NonTrivialComparisonExpression nt) {
				Neo4jProgramPersistenceStore.this.createNonTrivialComparisonExpression(nt);
			}

			@Override
			public void caseNonTrivialBooleanExpression(NonTrivialBooleanExpression entity) {
				super.caseNonTrivialBooleanExpression(entity);
			}

			@Override
			public void caseIdentifier(Identifier i) {
				Neo4jProgramPersistenceStore.this.createIdentifier(i);
			}

			@Override
			public void caseLiteralUsageEdge(LiteralUsageEdge entity) {
				Neo4jProgramPersistenceStore.this.createLiteralUsageEdge(entity);
			}

			@Override
			public void caseIdentifierUsageEdge(IdentifierUsageEdge entity) {
				Neo4jProgramPersistenceStore.this.createIdentifierUsageEdge(entity);
			}

			@Override
			public void caseExpressionUsageEdge(ExpressionUsageEdge entity) {
				Neo4jProgramPersistenceStore.this.createExpressionUsageEdge(entity);
			}

			@Override
			public void caseAssignmentEdge(AssignmentEdge entity) {
				Neo4jProgramPersistenceStore.this.createAssignmentEdge(entity);
			}

			@Override
			public void caseFlowEdge(FlowEdge f) {
				Neo4jProgramPersistenceStore.this.createFlowEdge(f);
			}

			@Override
			public void caseCondition(Condition c) {
				Neo4jProgramPersistenceStore.this.createCondition(c);
			}
		};
	}

	public void createCondition(Condition c) {
		Transaction t0 = session.beginTransaction();
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:Condition) ON n.programId");
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:Condition) ON n.intLabel");
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:Condition) ON n.contextLabel");
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:Condition) ON n.text");
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:Condition) ON n.id");
		t0.commit();
		t0.close();
		Transaction t = session.beginTransaction();
		Query q1 = new Query(
				"CREATE (n:Condition) SET n.programId=$programId, n.intLabel=$intLabel, n.contextLabel=$contextLabel, n.text=$text, n.id=$id return n",
				parameters("programId", c.getProgram().getProgramId(), "intLabel", c.getLabel().getIntLabel(),
						"contextLabel", c.getLabel().getContextLabel(), "text", c.getText(), "id", c.getId()));
		Result res = t.run(q1);
		boolean okay = false;
		if (res.hasNext()) {
			Record record = res.next();
			List<Pair<String, Value>> values = record.fields();
			Pair<String, Value> item = values.get(0);
			if ("n".equals(item.key())) {
				okay = true;
			}
		}
		if (!okay) {
			throw new IllegalStateException("Error running query");
		}
		t.commit();
		t.close();
	}

	public void createIdentifier(Identifier i) {
		Transaction t0 = session.beginTransaction();
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:Identifier) ON n.programId");
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:Identifier) ON n.text");
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:Identifier) ON n.id");
		t0.commit();
		t0.close();
		Transaction t = session.beginTransaction();
		Query q1 = new Query("CREATE (n:Identifier) SET n.programId=$programId, n.text=$text, n.id=$id return n",
				parameters("programId", i.getProgram().getProgramId(), "text", i.getText(), "id", i.getId()));
		Result res = t.run(q1);
		boolean okay = false;
		if (res.hasNext()) {
			Record record = res.next();
			List<Pair<String, Value>> values = record.fields();
			Pair<String, Value> item = values.get(0);
			if ("n".equals(item.key())) {
				okay = true;
			}
		}
		if (!okay) {
			throw new IllegalStateException("Error running query");
		}
		t.commit();
		t.close();
	}

	public void createNonTrivialBooleanExpression(NonTrivialBooleanExpression nt) {
		Transaction t0 = session.beginTransaction();
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:NonTrivialBooleanExpression) ON n.programId");
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:NonTrivialBooleanExpression) ON n.text");
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:NonTrivialBooleanExpression) ON n.operation");
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:NonTrivialBooleanExpression) ON n.id");
		t0.commit();
		t0.close();
		Transaction t = session.beginTransaction();
		Query q1 = new Query(
				"CREATE (n:NonTrivialBooleanExpression) SET n.programId=$programId, n.operation=$operation, n.text=$text, n.id=$id return n",
				parameters("programId", nt.getProgram().getProgramId(), "operation", nt.getOperation(), "text",
						nt.getText(), "id", nt.getId()));
		Result res = t.run(q1);
		boolean okay = false;
		if (res.hasNext()) {
			Record record = res.next();
			List<Pair<String, Value>> values = record.fields();
			Pair<String, Value> item = values.get(0);
			if ("n".equals(item.key())) {
				okay = true;
			}
		}
		if (!okay) {
			throw new IllegalStateException("Error running query");
		}
		t.commit();
		t.close();
	}

	public void createNonTrivialComparisonExpression(NonTrivialComparisonExpression nt) {
		Transaction t0 = session.beginTransaction();
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:NonTrivialComparisonExpression) ON n.programId");
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:NonTrivialComparisonExpression) ON n.text");
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:NonTrivialComparisonExpression) ON n.operation");
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:NonTrivialComparisonExpression) ON n.id");

		t0.commit();
		t0.close();
		Transaction t = session.beginTransaction();
		Query q1 = new Query(
				"CREATE (n:NonTrivialComparisonExpression) SET n.programId=$programId, n.operation=$operation, n.text=$text, n.id=$id return n",
				parameters("programId", nt.getProgram().getProgramId(), "operation", nt.getOperation(), "text",
						nt.getText(), "id", nt.getId()));
		Result res = t.run(q1);
		boolean okay = false;
		if (res.hasNext()) {
			Record record = res.next();
			List<Pair<String, Value>> values = record.fields();
			Pair<String, Value> item = values.get(0);
			if ("n".equals(item.key())) {
				okay = true;
			}
		}
		if (!okay) {
			throw new IllegalStateException("Error running query");
		}
		t.commit();
		t.close();
	}

	public void createTrivialBExp(TrivialBExp exp) {
		Transaction t0 = session.beginTransaction();
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:TrivialBExp) ON n.programId");
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:TrivialBExp) ON n.text");
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:TrivialBExp) ON n.id");
		t0.commit();
		t0.close();
		Transaction t = session.beginTransaction();
		Query q1 = new Query("CREATE (n:TrivialBExp) SET n.programId=$programId, n.text=$text, n.id=$id return n",
				parameters("text", exp.getText(), "id", exp.getId()));
		Result res = t.run(q1);
		boolean okay = false;
		if (res.hasNext()) {
			Record record = res.next();
			List<Pair<String, Value>> values = record.fields();
			Pair<String, Value> item = values.get(0);
			if ("n".equals(item.key())) {
				okay = true;
			}
		}
		if (!okay) {
			throw new IllegalStateException("Error running query");
		}
		t.commit();
		t.close();
	}

	public void createNonTrivialAExp(NonTrivialAExp nt) {
		Transaction t0 = session.beginTransaction();
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:NonTrivialAExp) ON n.programId");
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:NonTrivialAExp) ON n.text");
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:NonTrivialAExp) ON n.operation");
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:NonTrivialAExp) ON n.id");
		t0.commit();
		t0.close();
		Transaction t = session.beginTransaction();
		Query q1 = new Query(
				"CREATE (n:NonTrivialAExp) SET n.programId=$programId, n.operation=$operation, n.text=$text, n.id=$id return n",
				parameters("programId", nt.getProgram().getProgramId(), "operation", nt.getOperation(), "text",
						nt.getText(), "id", nt.getId()));
		Result res = t.run(q1);
		boolean okay = false;
		if (res.hasNext()) {
			Record record = res.next();
			List<Pair<String, Value>> values = record.fields();
			Pair<String, Value> item = values.get(0);
			if ("n".equals(item.key())) {
				okay = true;
			}
		}
		if (!okay) {
			throw new IllegalStateException("Error running query");
		}
		t.commit();
		t.close();
	}

	public void createTrivialAExp(TrivialAExp exp) {
		Transaction t0 = session.beginTransaction();
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:TrivialAExp) ON n.programId");
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:TrivialAExp) ON n.text");
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:TrivialAExp) ON n.id");
		t0.commit();
		t0.close();
		Transaction t = session.beginTransaction();
		Query q1 = new Query("CREATE (n:TrivialAExp) SET n.programId=$programId, n.text=$text, n.id=$id return n",
				parameters("programId", exp.getProgram().getProgramId(), "text", exp.getText(), "id", exp.getId()));
		Result res = t.run(q1);
		boolean okay = false;
		if (res.hasNext()) {
			Record record = res.next();
			List<Pair<String, Value>> values = record.fields();
			Pair<String, Value> item = values.get(0);
			if ("n".equals(item.key())) {
				okay = true;
			}
		}
		if (!okay) {
			throw new IllegalStateException("Error running query");
		}
		t.commit();
		t.close();
	}

	public void createAssignmentStmt(AssignmentStmt stmt) {
		Transaction t0 = session.beginTransaction();
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:AssignmentStmt) ON n.programId");
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:AssignmentStmt) ON n.intLabel");
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:AssignmentStmt) ON n.contextLabel");
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:AssignmentStmt) ON n.text");
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:AssignmentStmt) ON n.id");
		t0.commit();
		t0.close();
		Transaction t = session.beginTransaction();
		Query q1 = new Query(
				"CREATE (n:AssignmentStmt) SET n.programId=$programId,n.intLabel=$intLabel, n.contextLabel=$contextLabel, n.text=$text, n.id=$id return n",
				parameters("programId", stmt.getProgram().getProgramId(), "intLabel", stmt.getLabel().getIntLabel(),
						"contextLabel", stmt.getLabel().getContextLabel(), "text", stmt.getText(), "id", stmt.getId()));
		Result res = t.run(q1);
		boolean okay = false;
		if (res.hasNext()) {
			Record record = res.next();
			List<Pair<String, Value>> values = record.fields();
			Pair<String, Value> item = values.get(0);
			if ("n".equals(item.key())) {
				okay = true;
			}
		}
		if (!okay) {
			throw new IllegalStateException("Error running query");
		}
		t.commit();
		t.close();
	}

	public void createLiteral(Literal l) {
		Transaction t0 = session.beginTransaction();
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:Literal) ON n.programId");
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:Literal) ON n.text");
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:Literal) ON n.id");
		t0.commit();
		t0.close();
		Transaction t = session.beginTransaction();
		Query q1 = new Query(
				"CREATE (n:Literal) SET n.programId=$programId, n.text=$text, n.type=$type, n.id=$id return n",
				parameters("programId", l.getProgram().getProgramId(), "text", l.getText(), "type",
						l.getType().toString(), "id", l.getId()));
		Result res = t.run(q1);
		boolean okay = false;
		if (res.hasNext()) {
			Record record = res.next();
			List<Pair<String, Value>> values = record.fields();
			Pair<String, Value> item = values.get(0);
			if ("n".equals(item.key())) {
				okay = true;
			}
		}
		if (!okay) {
			throw new IllegalStateException("Error running query");
		}
		t.commit();
		t.close();
	}

	public void createSkipStmt(SkipStmt stmt) {
		Transaction t0 = session.beginTransaction();
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:SkipStmt) ON n.programId");
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:SkipStmt) ON n.intLabel");
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:SkipStmt) ON n.contextLabel");
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:SkipStmt) ON n.text");
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:SkipStmt) ON n.id");
		t0.commit();
		t0.close();
		Transaction t = session.beginTransaction();
		Query q1 = new Query(
				"CREATE (n:SkipStmt) SET n.programId=$programId,n.intLabel=$intLabel, n.contextLabel=$contextLabel, n.text=$text, n.id=$id return n",
				parameters("programId", stmt.getProgram().getProgramId(), "intLabel", stmt.getLabel().getIntLabel(),
						"contextLabel", stmt.getLabel().getContextLabel(), "text", stmt.getText(), "id", stmt.getId()));
		Result res = t.run(q1);
		boolean okay = false;
		if (res.hasNext()) {
			Record record = res.next();
			List<Pair<String, Value>> values = record.fields();
			Pair<String, Value> item = values.get(0);
			if ("n".equals(item.key())) {
				okay = true;
			}
		}
		if (!okay) {
			throw new IllegalStateException("Error running query");
		}
		t.commit();
		t.close();
	}

	public void createFlowEdge(FlowEdge f) {
		Transaction t = session.beginTransaction();
		Query q1 = new Query("MATCH (f), (t) WHERE f.programId=$programId AND f.programId=t.programId "
				+ "AND f.intLabel=$intLabelFrom AND t.intLabel=$intLabelTo" + " CREATE (f)-[r:FlowEdge]->(t) RETURN r",
				parameters("programId", f.getProgram().getProgramId(), "intLabelFrom", f.getFrom().getIntLabel(),
						"intLabelTo", f.getTo().getIntLabel()));
		Result res = t.run(q1);
		boolean okay = false;
		if (res.hasNext()) {
			Record record = res.next();
			List<Pair<String, Value>> values = record.fields();
			Pair<String, Value> item = values.get(0);
			if ("r".equals(item.key())) {
				okay = true;
			}
		}
		if (!okay) {
			throw new IllegalStateException("Error running query");
		}
		t.commit();
		t.close();
	}

	public void createAssignmentEdge(AssignmentEdge edge) {
		Transaction t = session.beginTransaction();
		Query q1 = new Query(
				"MATCH (f), (t) WHERE f.programId=$programId AND f.programId=t.programId "
						+ "AND f.id=$fromId AND t.id=$toId" + " CREATE (f)-[r:AssignmentEdge]->(t) RETURN r",
				parameters("programId", edge.getProgram().getProgramId(), "fromId", edge.getFrom().getId(), "toId",
						edge.getTo().getId()));
		Result res = t.run(q1);
		boolean okay = false;
		if (res.hasNext()) {
			Record record = res.next();
			List<Pair<String, Value>> values = record.fields();
			Pair<String, Value> item = values.get(0);
			if ("r".equals(item.key())) {
				okay = true;
			}
		}
		if (!okay) {
			throw new IllegalStateException("Error running query");
		}
		t.commit();
		t.close();
	}

	public void createExpressionUsageEdge(ExpressionUsageEdge edge) {
		Transaction t = session.beginTransaction();
		String qualifierStr = "";
		if (edge.getQualifier() != null) {
			qualifierStr = "{qualifier: '" + edge.getQualifier() + "'}";
		}
		Query q1 = new Query(
				"MATCH (f), (t) WHERE f.programId=$programId AND f.programId=t.programId "
						+ "AND f.id=$fromId AND t.id=$toId" + " CREATE (f)-[r:ExpressionUsageEdge " + qualifierStr
						+ " ]->(t) RETURN r",
				parameters("programId", edge.getProgram().getProgramId(), "fromId", edge.getFrom().getId(), "toId",
						edge.getTo().getId()));
		Result res = t.run(q1);
		boolean okay = false;
		if (res.hasNext()) {
			Record record = res.next();
			List<Pair<String, Value>> values = record.fields();
			Pair<String, Value> item = values.get(0);
			if ("r".equals(item.key())) {
				okay = true;
			}
		}
		if (!okay) {
			throw new IllegalStateException("Error running query");
		}
		t.commit();
		t.close();
	}

	public void createIdentifierUsageEdge(IdentifierUsageEdge edge) {
		Transaction t = session.beginTransaction();
		Query q1 = new Query(
				"MATCH (f), (t) WHERE f.programId=$programId AND f.programId=t.programId "
						+ "AND f.id=$fromId AND t.id=$toId" + " CREATE (f)-[r:IdentifierUsageEdge]->(t) RETURN r",
				parameters("programId", edge.getProgram().getProgramId(), "fromId", edge.getFrom().getId(), "toId",
						edge.getTo().getId()));
		Result res = t.run(q1);
		boolean okay = false;
		if (res.hasNext()) {
			Record record = res.next();
			List<Pair<String, Value>> values = record.fields();
			Pair<String, Value> item = values.get(0);
			if ("r".equals(item.key())) {
				okay = true;
			}
		}
		if (!okay) {
			throw new IllegalStateException("Error running query");
		}
		t.commit();
		t.close();
	}

	public void createLiteralUsageEdge(LiteralUsageEdge edge) {
		Transaction t = session.beginTransaction();
		Query q1 = new Query(
				"MATCH (f), (t) WHERE f.programId=$programId AND f.programId=t.programId "
						+ "AND f.id=$fromId AND t.id=$toId" + " CREATE (f)-[r:LiteralUsageEdge]->(t) RETURN r",
				parameters("programId", edge.getProgram().getProgramId(), "fromId", edge.getFrom().getId(), "toId",
						edge.getTo().getId()));
		Result res = t.run(q1);
		boolean okay = false;
		if (res.hasNext()) {
			Record record = res.next();
			List<Pair<String, Value>> values = record.fields();
			Pair<String, Value> item = values.get(0);
			if ("r".equals(item.key())) {
				okay = true;
			}
		}
		if (!okay) {
			throw new IllegalStateException("Error running query");
		}
		t.commit();
		t.close();
	}
}
