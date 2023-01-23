package jwhile.parser;

import static org.junit.Assert.assertEquals;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jwhile.ShowGuiTest;
import jwhile.antlr4.generated.WhileLexer;
import jwhile.antlr4.generated.WhileParser;
import jwhile.antlr4.utils.StandardErrorListener;

public class TestAssignments extends ShowGuiTest {

	private static Logger logger = LoggerFactory.getLogger(TestAssignments.class);

	@Test
	@SuppressWarnings("deprecation")
	public void testBasicAssignment() throws IOException, InterruptedException {
		boolean SHOW_GUI = true;
		// @formatter:off
		String[] assignments = {
				"a = b;", 
				"a = 1 + 2;", 
				"a = 1 + 2 + 3;", 
				"a = b + c;", 
				"a = 1 + a;", 
				"a = a + 1;"};
		String[] expected = { 
				"(program (stmts (stmt (assignment a = (expression (aExp b)))) ;))",
				"(program (stmts (stmt (assignment a = (expression (aExp (additiveExpression 1 + (aExp 2)))))) ;))",
				"(program (stmts (stmt (assignment a = (expression (aExp (additiveExpression 1 + (aExp (additiveExpression 2 + (aExp 3)))))))) ;))", 
				"(program (stmts (stmt (assignment a = (expression (aExp (additiveExpression b + (aExp c)))))) ;))",
				"(program (stmts (stmt (assignment a = (expression (aExp (additiveExpression 1 + (aExp a)))))) ;))",
				"(program (stmts (stmt (assignment a = (expression (aExp (additiveExpression a + (aExp 1)))))) ;))",
				""};
		// @formatter:on
		
		for (int i = 0; i < assignments.length; i++) {
			logger.info("------------------ basic assiggnments -------------------");
			logger.info(assignments[i]);
			logger.info(expected[i]);
			ANTLRInputStream input = new ANTLRInputStream(assignments[i]);
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
			assertEquals(expected[i], ast);
		}
	}

	

}
