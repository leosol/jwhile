package jwhile.antlr4.visitors;

import java.io.IOException;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jwhile.ShowGuiTest;
import jwhile.antlr4.cfg.entities.Entity;
import jwhile.antlr4.cfg.entities.Program;
import jwhile.antlr4.cfg.persistence.impl.Neo4jProgramPersistenceStore;
import jwhile.antlr4.cfg.visitors.JWhileProgramParser;
import jwhile.antlr4.generated.WhileLexer;
import jwhile.antlr4.generated.WhileParser;
import jwhile.antlr4.utils.StandardErrorListener;
import jwhile.interpreter.TestAExp;

public class TestUsedForDevelopment extends ShowGuiTest {

	private static Logger logger = LoggerFactory.getLogger(TestAExp.class);
	private boolean SHOW_GUI = false;
	private Neo4jProgramPersistenceStore store;

	public TestUsedForDevelopment() {
		String url = "neo4j://localhost:7687";
		String user = "neo4j";
		String password = "0123456789";
		this.store = new Neo4jProgramPersistenceStore(url, user, password);
		this.store.openSession();
	}

	@Test
	public void testAddition() throws IOException, InterruptedException {
		// @formatter:off
		String[] simpleStatements = {
				"a = 1; b = a; c = a + a; d = a + 1; e = 1 + a; f = 1 + 2 + 3;",
				"a = true; b = a; c = a && a; d = a || false; e = false && a;", 
				"a = 1 == 1; b = a == 1; c = 1 == a; d = 10 < 20; e = 4 >= 4;",
				"a = 0; b = 0; if(true){a=1; b=1;}; if(false){a=1; b=1;} else {a=2; b=2;};",
				"y = x; z = 1; while(y>1){z=z*y; y=y-1;}; y=0;"
				};
		// @formatter:on

		for (int i = 0; i < simpleStatements.length; i++) {
			logger.info("------------------ basic assiggnments -------------------");
			logger.info(simpleStatements[i]);
			ANTLRInputStream input = new ANTLRInputStream(simpleStatements[i]);
			WhileLexer lex = new WhileLexer(input);
			lex.removeErrorListeners();
			lex.addErrorListener(StandardErrorListener.INSTANCE);
			CommonTokenStream tokens = new CommonTokenStream(lex);
			WhileParser parser = new WhileParser(tokens);
			parser.removeErrorListeners();
			parser.addErrorListener(StandardErrorListener.INSTANCE);
			ParseTree tree = parser.program();
			String ast = tree.toStringTree(parser);
			logger.info(ast);
			if (SHOW_GUI) {
				showAstInGui(parser, tree);
			}
			JWhileProgramParser visitor = new JWhileProgramParser();
			Entity visited = visitor.visit(tree);
			Program prg = visitor.getProgram();
			prg.setAnalyst("JUnit Analyst");
			store.persistNewProgram(prg);
			logger.info("Finished Flow Visitor");
		}
	}

}
