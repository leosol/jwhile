package jwhile.cfg;

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
import jwhile.antlr4.cfg.JWhileFlowVisitor;
import jwhile.antlr4.cfg.store.CFGProject;
import jwhile.antlr4.cfg.store.CFGStore;
import jwhile.antlr4.cfg.store.CFGStoreNeo4jImpl;
import jwhile.antlr4.cfg.store.FlowEdge;
import jwhile.antlr4.cfg.store.FlowNode;
import jwhile.antlr4.generated.WhileLexer;
import jwhile.antlr4.generated.WhileParser;
import jwhile.antlr4.interpreter.JWhileVisitor;
import jwhile.antlr4.interpreter.Value;
import jwhile.antlr4.utils.StandardErrorListener;
import jwhile.interpreter.TestAExp;

public class TestCfg extends ShowGuiTest {
	
	private static Logger logger = LoggerFactory.getLogger(TestAExp.class);
	private boolean SHOW_GUI = true;
	private CFGStoreNeo4jImpl cfgStore;
	
	
	public TestCfg() {
		String url = "neo4j://localhost:7687";
		String user = "neo4j";
		String password = "0123456789";
		this.cfgStore = new CFGStoreNeo4jImpl(url, user, password);
		this.cfgStore.openSession();
	}

	@Test
	@Ignore
	public void testAddition() throws IOException, InterruptedException {
		// @formatter:off
		String[] simpleStatements = {
				"a = 1; b = a; c = a + a; d = a + 1; e = 1 + a; f = 1 + 2 + 3;"};
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
			JWhileFlowVisitor visitor = new JWhileFlowVisitor(this.cfgStore.createCFGProject());
			visitor.visit(tree);
			logger.info("Finished Flow Visitor");
		}
	}
	
	@Test
	@Ignore
	public void testBasicBoolean() throws IOException, InterruptedException {
		// @formatter:off
		String[] simpleStatements = {
				"a = true; b = a; c = a && a; d = a || false; e = false && a;"};
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
			JWhileFlowVisitor visitor = new JWhileFlowVisitor(this.cfgStore.createCFGProject());
			visitor.visit(tree);
			logger.info("Finished Flow Visitor");
		}
	}
	
	@Test
	@Ignore
	public void testBasicComparisons() throws IOException, InterruptedException {
		// @formatter:off
		String[] simpleStatements = {
				"a = 1 == 1; b = a == 1; c = 1 == a; d = 10 < 20; e = 4 >= 4;"};
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
			JWhileFlowVisitor visitor = new JWhileFlowVisitor(this.cfgStore.createCFGProject());
			visitor.visit(tree);
			logger.info("Finished Flow Visitor");
		}
	}
	
	@Test
	@Ignore
	public void testIfThenElse() throws IOException, InterruptedException {
		// @formatter:off
		String[] simpleStatements = {
				"a = 0; b = 0; if(true){a=1; b=1;}; if(false){a=1; b=1;} else {a=2; b=2;};"};
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
			JWhileFlowVisitor visitor = new JWhileFlowVisitor(this.cfgStore.createCFGProject());
			visitor.visit(tree);
			logger.info("Finished Flow Visitor");
		}
	}
	
	@Test
	public void testWhile() throws IOException, InterruptedException {
		// @formatter:off
		String[] simpleStatements = {
				"y = x; z = 1; while(y>1){z=z*y; y=y-1;}; y=0;"};
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
			JWhileFlowVisitor visitor = new JWhileFlowVisitor(this.cfgStore.createCFGProject());
			visitor.visit(tree);
			logger.info("Finished Flow Visitor");
		}
	}
}
