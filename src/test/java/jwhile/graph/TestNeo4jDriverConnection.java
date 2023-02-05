package jwhile.graph;

import static org.junit.Assert.assertTrue;
import static org.neo4j.driver.Values.parameters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Test;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Query;
import org.neo4j.driver.Session;

public class TestNeo4jDriverConnection {

	@Test
	public void testLocalQuery() {
		Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "0123456789"));
		try (Session session = driver.session()) {
			boolean b = session.executeWrite(tx -> {
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
				LocalDateTime now = LocalDateTime.now();
				Query q0 = new Query("MATCH (n) WHERE n:SystemTests OR n:TestExecution DETACH DELETE n");
				tx.run(q0);
				Query q1 = new Query("CREATE (g:SystemTests) SET g.dtCreation=$dt_creation", parameters("dt_creation", dtf.format(now)));
				tx.run(q1);
				Query q2 = new Query("CREATE (e:TestExecution) SET e.dtCreation=$dt_creation", parameters("dt_creation", dtf.format(now)));
				tx.run(q2);
				Query q3 = new Query("MATCH (g:SystemTests), (e:TestExecution) CREATE (g)-[r:RELTYPE {name: 'contains'}]->(e)");
				tx.run(q3);
				return true;
			});
			assertTrue(b);
		}
		driver.close();
	}

}
