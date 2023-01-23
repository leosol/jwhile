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
public class TestIfThenElse extends ShowGuiTest{
	private static Logger logger = LoggerFactory.getLogger(TestAExp.class);
	private boolean SHOW_GUI = false;
	
	@Test
	public void testIfThen() throws IOException, InterruptedException {
		// @formatter:off
		String[] simpleStatements = {
				"a = 0; b = 0; if(true){a=1; b=1;};",
				"a = 0; b = 0; if(false){a=1; b=1;};"};
		List<Map<String, Value>> expected = new ArrayList<Map<String, Value>>();
		expected.add(new HashMap<String, Value>() {{
		    put("a", new Value(1));
		    put("b", new Value(1));
		}});
		expected.add(new HashMap<String, Value>() {{
		    put("a", new Value(0));
		    put("b", new Value(0));
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
	public void testIfThenElse() throws IOException, InterruptedException {
		// @formatter:off
		String[] simpleStatements = {
				"a = 0; b = 0; if(true){a=1; b=1;} else {a=2; b=2;};",
				"a = 0; b = 0; if(false){a=1; b=1;} else {a=2; b=2;};"};
		List<Map<String, Value>> expected = new ArrayList<Map<String, Value>>();
		expected.add(new HashMap<String, Value>() {{
		    put("a", new Value(1));
		    put("b", new Value(1));
		}});
		expected.add(new HashMap<String, Value>() {{
		    put("a", new Value(2));
		    put("b", new Value(2));
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
