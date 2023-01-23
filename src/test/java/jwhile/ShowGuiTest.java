package jwhile;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.tree.ParseTree;

import jwhile.antlr4.generated.WhileParser;
import jwhile.parser.TestAssignments;

public class ShowGuiTest {
	
	
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
				synchronized (ShowGuiTest.this) {
					while (frame.isVisible())
						try {
							ShowGuiTest.this.wait();
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
				synchronized (ShowGuiTest.this) {
					frame.setVisible(false);
					ShowGuiTest.this.notify();
				}
			}

		});
		t.join();
	}
}
