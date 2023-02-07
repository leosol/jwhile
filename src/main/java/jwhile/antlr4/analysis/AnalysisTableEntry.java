package jwhile.antlr4.analysis;

import java.util.LinkedHashSet;
import java.util.Set;

import org.neo4j.driver.types.Node;

import jwhile.antlr4.analysis.algorithms.AnalysisInformation;

public class AnalysisTableEntry<T extends AnalysisInformation<? extends Comparable<?>>> {

	private Node node;
	private Set<T> gen = new LinkedHashSet<T>();
	private Set<T> kill = new LinkedHashSet<T>();
	private Set<T> entry = new LinkedHashSet<T>();
	private Set<T> exit = new LinkedHashSet<T>();
	private Set<T> previousEntry = new LinkedHashSet<T>();
	private Set<T> previousExit = new LinkedHashSet<T>();

	public AnalysisTableEntry(Node node) {
		this.node = node;
	}

	public Set<T> getGen() {
		return gen;
	}

	public void setGen(Set<T> gen) {
		this.gen = gen;
	}

	public Set<T> getKill() {
		return kill;
	}

	public void setKill(Set<T> kill) {
		this.kill = kill;
	}

	public Node getNode() {
		return node;
	}

	public Set<T> getEntry() {
		return entry;
	}

	public void setEntry(Set<T> entry) {
		this.entry = entry;
	}

	public Set<T> getExit() {
		return exit;
	}

	public void setExit(Set<T> exit) {
		this.exit = exit;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public String getGenStr() {
		StringBuffer sb = new StringBuffer("{");
		int pos = 0;
		for (T t : gen) {
			if (pos > 0) {
				sb.append(",");
			}
			sb.append(t.toString());
			pos = pos + 1;
		}
		sb.append("}");
		return sb.toString();
	}

	public String getKillStr() {
		StringBuffer sb = new StringBuffer("{");
		int pos = 0;
		for (T t : kill) {
			if (pos > 0) {
				sb.append(",");
			}
			sb.append(t.toString());
			pos = pos + 1;
		}
		sb.append("}");
		return sb.toString();
	}
	
	public void checkPoint() {
		this.previousEntry = new LinkedHashSet<T>();
		this.previousExit = new LinkedHashSet<T>();
		this.previousEntry.addAll(this.entry);
		this.previousExit.addAll(this.exit);
	}

	public boolean isStable() {
		boolean entryIsStable = false;
		boolean exitIsStable = false;
		if (this.previousEntry.size() == this.entry.size()) {
			if (this.previousEntry.equals(this.entry)) {
				entryIsStable = true;
			}
		}
		if (this.previousExit.size() == this.exit.size()) {
			if (this.previousExit.equals(this.exit)) {
				exitIsStable = true;
			}
		}
		return entryIsStable && exitIsStable;
	}

	public String getEntryStr() {
		StringBuffer sb = new StringBuffer("{");
		int pos = 0;
		for (T t : entry) {
			if (pos > 0) {
				sb.append(",");
			}
			sb.append(t.toString());
			pos = pos + 1;
		}
		sb.append("}");
		return sb.toString();
	}
	
	public String getExitStr() {
		StringBuffer sb = new StringBuffer("{");
		int pos = 0;
		for (T t : exit) {
			if (pos > 0) {
				sb.append(",");
			}
			sb.append(t.toString());
			pos = pos + 1;
		}
		sb.append("}");
		return sb.toString();
	}
}
