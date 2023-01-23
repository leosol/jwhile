package jwhile.interpreter;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jwhile.ShowGuiTest;
import jwhile.antlr4.generated.WhileLexer;
import jwhile.antlr4.generated.WhileParser;
import jwhile.antlr4.interpreter.JWhileVisitor;
import jwhile.antlr4.interpreter.Value;
import jwhile.antlr4.utils.StandardErrorListener;

@SuppressWarnings(value = { "serial", "deprecation" })
public class TestBExp extends ShowGuiTest{
	private static Logger logger = LoggerFactory.getLogger(TestAExp.class);
	private boolean SHOW_GUI = false;
	
	@Test
	public void testBasicBoolean() throws IOException, InterruptedException {
		// @formatter:off
		String[] simpleStatements = {
				"a = true; b = a; c = a && a; d = a || false; e = false && a;"};
		List<Map<String, Value>> expected = new ArrayList<Map<String, Value>>();
		expected.add(new HashMap<String, Value>() {{
		    put("a", new Value(true));
		    put("b", new Value(true));
		    put("c", new Value(true));
		    put("d", new Value(true));
		    put("e", new Value(false));
		}});
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
			if(SHOW_GUI) {
				showAstInGui(parser, tree);
			}
			JWhileVisitor visitor = new JWhileVisitor();
	        Value v = visitor.visit(tree);	
	        logger.info("Test value: ", v);
	        visitor.dumpMem();
	        boolean memOkay = expected.get(i).equals(visitor.getMem());
			logger.info("Is memory as expected: " + memOkay);
			assertTrue(memOkay);
		}
	}
	
	@Test
	public void testBasicComparisons() throws IOException, InterruptedException {
		// @formatter:off
		String[] simpleStatements = {
				"a = 1 == 1; b = a == 1; c = 1 == a; d = 10 < 20; e = 4 >= 4;"};
		List<Map<String, Value>> expected = new ArrayList<Map<String, Value>>();
		expected.add(new HashMap<String, Value>() {{
		    put("a", new Value(true));
		    put("b", new Value(false));
		    put("c", new Value(false));
		    put("d", new Value(true));
		    put("e", new Value(true));
		}});
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
			if(SHOW_GUI) {
				showAstInGui(parser, tree);
			}
			JWhileVisitor visitor = new JWhileVisitor();
	        Value v = visitor.visit(tree);	
	        logger.info("Test value: ", v);
	        visitor.dumpMem();
	        boolean memOkay = expected.get(i).equals(visitor.getMem());
			logger.info("Is memory as expected: " + memOkay);
			assertTrue(memOkay);
		}
	}

}
