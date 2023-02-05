package jwhile.antlr4.cfg.store;

import static org.neo4j.driver.Values.parameters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;

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
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.neo4j.driver.util.Pair;

import org.neo4j.driver.Value;

public class CFGStoreNeo4jImpl implements CFGStore {

	private String url;
	private String user;
	private String password;
	private Driver driver;
	private Session session;

	public CFGStoreNeo4jImpl(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
	}

	public void openSession() {
		Config config = Config.builder().withLogging(Logging.console(Level.ALL)).build();
		driver = GraphDatabase.driver(url, AuthTokens.basic(user, password), config);
		session = driver.session();
	}

	public void closeSession() {
		session.close();
		driver.close();
	}

	@Override
	public CFGProject createCFGProject() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		LocalDateTime now = LocalDateTime.now();
		CFGProject project = new CFGProject("PRJ-" + dtf.format(now), this);
		createCFGProjectNode(project);
		return project;
	}

	private void createCFGProjectNode(CFGProject p) {
		Transaction t0 = session.beginTransaction();
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:CFGProject) ON n.name");
		t0.run("CREATE INDEX IF NOT EXISTS FOR (n:FlowNode) ON n.label");
		t0.commit();
		t0.close();
		Transaction t = session.beginTransaction();
		Query q1 = new Query("CREATE (n:CFGProject) SET n.name=$name return n", parameters("name", p.getProjectName()));
		Result res = t.run(q1);
		if (res.hasNext()) {
			Record record = res.next();
			List<Pair<String, Value>> values = record.fields();
			Pair<String, Value> item = values.get(0);
			if ("n".equals(item.key())) {
				Value v = item.value();
				Node n = (Node) v.asEntity();
				p.setNode(n);
			}
		}
		t.commit();
		t.close();
	}

	@Override
	public void createFlowNode(CFGProject prj, FlowNode node) {
		Transaction t = session.beginTransaction();
		Query q1 = new Query(
				"match (p {name:$prjName}) CREATE (n:FlowNode {label:$label, text:$text}) create(p)-[:contains]->(n) return n",
				parameters("prjName", prj.getProjectName(), "label", node.getLabel(), "text", node.getText()));
		Result res = t.run(q1);
		if (res.hasNext()) {
			Record record = res.next();
			List<Pair<String, Value>> values = record.fields();
			Pair<String, Value> item = values.get(0);
			if ("n".equals(item.key())) {
				Value v = item.value();
				node.setNode((Node) v.asEntity());
			}
		}
		t.commit();
		t.close();
	}

	@Override
	public void createFlowEdge(CFGProject prj, FlowEdge edge) {
		Transaction t = session.beginTransaction();
		Query q1 = new Query(
				"MATCH (f:FlowNode), (t:FlowNode) WHERE f.label=$fromLabel AND t.label=$toLabel CREATE (f)-[r:FlowEdge]->(t) RETURN r",
				parameters("fromLabel", edge.getFrom().getLabel(), "toLabel", edge.getTo().getLabel()));
		Result res = t.run(q1);
		if (res.hasNext()) {
			Record record = res.next();
			List<Pair<String, Value>> values = record.fields();
			Pair<String, Value> item = values.get(0);
			if ("r".equals(item.key())) {
				Value v = item.value();
				edge.setRelationship((Relationship) v.asEntity());
			}
		}
		t.commit();
		t.close();
	}

	@Override
	public void createLiteralNode(CFGProject prj, LiteralNode l) {
		Transaction t = session.beginTransaction();
		Query q1 = new Query(
				"match (p {name:$prjName}) CREATE (n:LiteralNode {label:$label, text:$text}) create(p)-[:contains]->(n) return n",
				parameters("prjName", prj.getProjectName(), "label", l.getLabel(), "text", l.getText()));
		Result res = t.run(q1);
		if (res.hasNext()) {
			Record record = res.next();
			List<Pair<String, Value>> values = record.fields();
			Pair<String, Value> item = values.get(0);
			if ("n".equals(item.key())) {
				Value v = item.value();
				l.setNode((Node) v.asEntity());
			}
		}
		t.commit();
		t.close();
	}

	@Override
	public void createUsageEdge(CFGProject prj, UsageEdge edge) {
		Transaction t = session.beginTransaction();
		Query q1 = new Query(
				"MATCH (f), (t) WHERE f.label=$fromLabel AND t.label=$toLabel CREATE (f)-[r:UsageEdge]->(t) RETURN r",
				parameters("fromLabel", edge.getFrom().getLabel(), "toLabel", edge.getTo().getLabel()));
		Result res = t.run(q1);
		if (res.hasNext()) {
			Record record = res.next();
			List<Pair<String, Value>> values = record.fields();
			Pair<String, Value> item = values.get(0);
			if ("r".equals(item.key())) {
				Value v = item.value();
				edge.setRelationship((Relationship) v.asEntity());
			}
		}
		t.commit();
		t.close();
	}
	
	@Override
	public void createAssignmentEdge(CFGProject cfgProject, UsageEdge edge) {
		Transaction t = session.beginTransaction();
		Query q1 = new Query(
				"MATCH (f), (t) WHERE f.label=$fromLabel AND t.label=$toLabel CREATE (f)-[r:AssignmentEdge]->(t) RETURN r",
				parameters("fromLabel", edge.getFrom().getLabel(), "toLabel", edge.getTo().getLabel()));
		Result res = t.run(q1);
		if (res.hasNext()) {
			Record record = res.next();
			List<Pair<String, Value>> values = record.fields();
			Pair<String, Value> item = values.get(0);
			if ("r".equals(item.key())) {
				Value v = item.value();
				edge.setRelationship((Relationship) v.asEntity());
			}
		}
		t.commit();
		t.close();
	}

	@Override
	public void createIdentifierNode(CFGProject prj, IdentifierNode n) {
		Transaction t = session.beginTransaction();
		Query q1 = new Query(
				"match (p {name:$prjName}) CREATE (n:IdentifierNode {label:$label, text:$text}) create(p)-[:contains]->(n) return n",
				parameters("prjName", prj.getProjectName(), "label", n.getLabel(), "text", n.getText()));
		Result res = t.run(q1);
		if (res.hasNext()) {
			Record record = res.next();
			List<Pair<String, Value>> values = record.fields();
			Pair<String, Value> item = values.get(0);
			if ("n".equals(item.key())) {
				Value v = item.value();
				n.setNode((Node) v.asEntity());
			}
		}
		t.commit();
		t.close();
	}

	@Override
	public void createOperationNode(CFGProject prj, OperationNode o) {
		Transaction t = session.beginTransaction();
		Query q1 = new Query(
				"match (p {name:$prjName}) CREATE (n:OperationNode {label:$label, text:$text}) create(p)-[:contains]->(n) return n",
				parameters("prjName", prj.getProjectName(), "label", o.getLabel(), "text", o.getText()));
		Result res = t.run(q1);
		if (res.hasNext()) {
			Record record = res.next();
			List<Pair<String, Value>> values = record.fields();
			Pair<String, Value> item = values.get(0);
			if ("n".equals(item.key())) {
				Value v = item.value();
				o.setNode((Node) v.asEntity());
			}
		}
		t.commit();
		t.close();
	}
	
	

}
