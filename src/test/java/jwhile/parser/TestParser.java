package jwhile.parser;

import static org.junit.Assert.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Test;

import jwhile.antlr4.generated.WhileLexer;
import jwhile.antlr4.generated.WhileParser;
import jwhile.antlr4.utils.DebugListener;
import jwhile.antlr4.utils.StandardErrorListener;

public class TestParser {

	@Test
	@SuppressWarnings("deprecation")
	public void test() throws IOException, InterruptedException {
		URL basicW = getClass().getResource("/jwhile/parser/basic.w");
		ANTLRInputStream input = new ANTLRInputStream(basicW.openStream());
		WhileLexer lex = new WhileLexer(input);
		lex.removeErrorListeners();
		lex.addErrorListener(StandardErrorListener.INSTANCE);
		CommonTokenStream tokens = new CommonTokenStream(lex);
		WhileParser parser = new WhileParser(tokens);
		parser.removeErrorListeners();
		parser.addErrorListener(StandardErrorListener.INSTANCE);
		parser.addParseListener(DebugListener.getInstance());
		ParseTree tree = parser.program();

		// show AST in console
		System.out.println(tree.toStringTree(parser));

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
				synchronized (TestParser.this) {
					while (frame.isVisible())
						try {
							TestParser.this.wait();
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
				synchronized (TestParser.this) {
					frame.setVisible(false);
					TestParser.this.notify();
				}
			}

		});
		t.join();

//	    ParseTreeWalker walker = new ParseTreeWalker();
//	    walker.walk(DebugListener.getInstance(), tree);
//		System.out.println(tree);
	}

}
