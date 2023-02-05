package jwhile.graph;

import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Properties;

import org.junit.Test;
import org.neo4j.configuration.GraphDatabaseSettings;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.io.fs.FileUtils;

public class TestNeo4jLocal {

	private enum RelTypes implements RelationshipType {
		KNOWS
	}

	private static void registerShutdownHook(final DatabaseManagementService managementService) {
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running application).
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				managementService.shutdown();
			}
		});
	}

	@Test
	public void testLocalQuery() throws IOException {
		Properties props = new Properties();
		props.load(this.getClass().getResourceAsStream("/project.properties"));
		String myBasedir = (String) props.get("jwhile.basedir");
		String dirPath = myBasedir + File.separator + "src" + File.separator + "test" + File.separator + "resources"
				+ File.separator + "jwhile" + File.separator + "graph" + File.separator + "data" + File.separator
				+ "hw";
		Path directory = new File(dirPath).toPath();
		FileUtils.deleteDirectory(directory);
		DatabaseManagementService managementService = new DatabaseManagementServiceBuilder(directory)
				.setConfig(GraphDatabaseSettings.pagecache_memory, 512 * 8224L)
				.setConfig(GraphDatabaseSettings.transaction_timeout, Duration.ofSeconds(60))
				.setConfig(GraphDatabaseSettings.preallocate_logical_logs, true).build();
		GraphDatabaseService graphDb = managementService.database(DEFAULT_DATABASE_NAME);
		registerShutdownHook(managementService);
		try (Transaction tx = graphDb.beginTx()) {
			Node firstNode = tx.createNode();
			firstNode.setProperty("message", "Hello, ");
			Node secondNode = tx.createNode();
			secondNode.setProperty("message", "World!");

			Relationship relationship = firstNode.createRelationshipTo(secondNode, RelTypes.KNOWS);
			relationship.setProperty("message", "brave Neo4j ");
			tx.commit();
		}
		managementService.shutdown();
	}

}
