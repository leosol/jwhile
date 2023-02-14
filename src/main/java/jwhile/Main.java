package jwhile;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jwhile.antlr4.analysis.Framework;
import jwhile.antlr4.analysis.algorithms.AnalysisInformation;
import jwhile.antlr4.analysis.algorithms.impl.AvailableExpressions;
import jwhile.antlr4.analysis.algorithms.impl.LiveVariablesAnalysis;
import jwhile.antlr4.analysis.algorithms.impl.ReachingDefinitions;
import jwhile.antlr4.analysis.algorithms.impl.VeryBusyExpressions;
import jwhile.antlr4.cfg.entities.Label;
import jwhile.antlr4.cfg.entities.Program;
import jwhile.antlr4.cfg.persistence.impl.Neo4jProgramPersistenceStore;
import jwhile.antlr4.cfg.visitors.JWhileProgramParser;
import jwhile.antlr4.generated.WhileLexer;
import jwhile.antlr4.generated.WhileParser;
import jwhile.antlr4.interpreter.JWhileVisitor;
import jwhile.antlr4.interpreter.Value;
import jwhile.antlr4.utils.StandardErrorListener;

@SuppressWarnings("deprecation")
public class Main {

	private static Logger logger = LoggerFactory.getLogger(Main.class);
	private boolean SHOW_GUI = true;
	private Neo4jProgramPersistenceStore store;

	public Main() {
		String url = "neo4j://localhost:7687";
		String user = "neo4j";
		String password = "0123456789";
		this.store = new Neo4jProgramPersistenceStore(url, user, password);
		this.store.openSession();
	}

	public void showAstInGui(WhileParser parser, ParseTree tree) throws InterruptedException {
		// show AST in GUI
		JFrame frame = new JFrame("Antlr AST");
		JPanel panel = new JPanel();
		TreeViewer viewer = new TreeViewer(Arrays.asList(parser.getRuleNames()), tree);
		viewer.setScale(1.5); // Scale a little
		panel.add(viewer);
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);

		Thread t = new Thread() {
			public void run() {
				synchronized (Main.this) {
					while (frame.isVisible())
						try {
							Main.this.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					System.out.println("Working now");
				}
			}
		};
		t.start();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				synchronized (Main.this) {
					frame.setVisible(false);
					Main.this.notify();
				}
			}

		});
		t.join();
	}

	public Program persistAvailableExpressionsProgram() throws IOException, InterruptedException {
		this.store.clearDatabase();
		// @formatter:off
		String[] simpleStatements = {
				"x=a+b; y=a*b; while(y>a+b){a=a+1; x=a+b;};"};
		// @formatter:on

		logger.info("------------------ basic assiggnments -------------------");
		logger.info(simpleStatements[0]);
		ANTLRInputStream input = new ANTLRInputStream(simpleStatements[0]);
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
		visitor.visit(tree);
		Program prg = visitor.getProgram();
		prg.setAnalyst("JUnit Analyst");
		store.persistNewProgram(prg);
		logger.info("Finished Flow Visitor");
		return prg;
	}

	public void analyzeAvailableExpressions() throws IOException, InterruptedException {
		Program program = this.persistAvailableExpressionsProgram();
		AvailableExpressions algorithm = new AvailableExpressions(program, store);
		Framework<AnalysisInformation<String>> framework = new Framework<AnalysisInformation<String>>(
				program.getProgramId(), algorithm, store);
		framework.analyzeProgram();
	}

	public Program persistLiveVariablesAnalysisProgram() throws IOException, InterruptedException {
		this.store.clearDatabase();
		// @formatter:off
		String[] simpleStatements = {
				"x=2;y=4;x=1;if(y>x){z=y;}else{z=y*y;};x=z;"};
		// @formatter:on

		logger.info("------------------ basic assiggnments -------------------");
		logger.info(simpleStatements[0]);
		ANTLRInputStream input = new ANTLRInputStream(simpleStatements[0]);
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
		visitor.visit(tree);
		Program prg = visitor.getProgram();
		prg.setAnalyst("JUnit Analyst");
		store.persistNewProgram(prg);
		logger.info("Finished Flow Visitor");
		return prg;
	}

	public void analyzeLiveVariablesProgram() throws IOException, InterruptedException {
		Program program = this.persistLiveVariablesAnalysisProgram();
		LiveVariablesAnalysis algorithm = new LiveVariablesAnalysis(program, store);
		Framework<AnalysisInformation<String>> framework = new Framework<AnalysisInformation<String>>(
				program.getProgramId(), algorithm, store);
		framework.analyzeProgram();
	}

	public Program persistReachingDefinitionsProgram() throws IOException, InterruptedException {
		this.store.clearDatabase();
		// @formatter:off
		String[] simpleStatements = {
				"x=5;y=1;while(x>1){y=x*y;x=x-1;};"};
		// @formatter:on

		logger.info("------------------ basic assiggnments -------------------");
		logger.info(simpleStatements[0]);
		ANTLRInputStream input = new ANTLRInputStream(simpleStatements[0]);
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
		visitor.visit(tree);
		Program prg = visitor.getProgram();
		prg.setAnalyst("JUnit Analyst");
		store.persistNewProgram(prg);
		logger.info("Finished Flow Visitor");
		return prg;
	}

	public void analyzeReachingDefinitionsProgram() throws IOException, InterruptedException {
		Program program = this.persistReachingDefinitionsProgram();
		ReachingDefinitions algorithm = new ReachingDefinitions(program, store);
		Framework<AnalysisInformation<Pair<Label, String>>> framework = new Framework<AnalysisInformation<Pair<Label, String>>>(
				program.getProgramId(), algorithm, store);
		framework.analyzeProgram();
	}

	public Program persistVeryBusyExpressionsProgram() throws IOException, InterruptedException {
		this.store.clearDatabase();
		// @formatter:off
		String[] simpleStatements = {
				"if(a>b){x=b-a;y=a-b;}else{y=b-a;x=a-b;};"};
		// @formatter:on

		logger.info("------------------ basic assiggnments -------------------");
		logger.info(simpleStatements[0]);
		ANTLRInputStream input = new ANTLRInputStream(simpleStatements[0]);
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
		visitor.visit(tree);
		Program prg = visitor.getProgram();
		prg.setAnalyst("JUnit Analyst");
		store.persistNewProgram(prg);
		logger.info("Finished Flow Visitor");
		return prg;
	}

	public void analyzeVeryBusyExpressions() throws IOException, InterruptedException {
		Program program = this.persistVeryBusyExpressionsProgram();
		VeryBusyExpressions algorithm = new VeryBusyExpressions(program, store);
		Framework<AnalysisInformation<String>> framework = new Framework<AnalysisInformation<String>>(
				program.getProgramId(), algorithm, store);
		framework.analyzeProgram();
	}

	public void interpretExampleProgram() throws IOException, InterruptedException {
		// @formatter:off
		String[] simpleStatements = {
				"a = 0; b = 10; while(a<b){a = a +1;};"};
		List<Map<String, Value>> expected = new ArrayList<Map<String, Value>>();
		expected.add(new HashMap<String, Value>() {
			private static final long serialVersionUID = 1L;
		{
		    put("a", new Value(10));
		    put("b", new Value(10));
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
			if (SHOW_GUI) {
				showAstInGui(parser, tree);
			}
			JWhileVisitor visitor = new JWhileVisitor();
			Value v = visitor.visit(tree);
			logger.info("Test value: ", v);
			visitor.dumpMem();
			boolean memOkay = expected.get(i).equals(visitor.getMem());
			logger.info("Is memory as expected: " + memOkay);
		}
	}

	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException {
		Main m = new Main();
		System.out.println("Choose one option:");
		System.out.println("(1) Interpret example program");
		System.out.println("(2) Run Available Expressions Test");
		System.out.println("(3) Run Reaching Definitions Test");
		System.out.println("(4) Run Live Variables Test Test");
		System.out.println("(5) Run Very Busy Expressions Test");
		Scanner sc = new Scanner(System.in);
		String input = sc.nextLine();
		switch (input) {
		case ("1"):
			m.interpretExampleProgram();
			break;
		case ("2"):
			m.analyzeAvailableExpressions();
			break;
		case ("3"):
			m.analyzeReachingDefinitionsProgram();
			break;
		case ("4"):
			m.analyzeLiveVariablesProgram();
			break;
		case ("5"):
			m.analyzeVeryBusyExpressions();
			break;
		}
		if ("1".equals(input)) {
			m.interpretExampleProgram();
		}
		sc.close();
	}

}
