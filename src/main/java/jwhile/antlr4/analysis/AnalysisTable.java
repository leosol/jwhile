package jwhile.antlr4.analysis;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.neo4j.driver.types.Node;

import jwhile.antlr4.analysis.algorithms.AnalysisInformation;
import jwhile.antlr4.cfg.util.TablePrinter;

public class AnalysisTable<T extends AnalysisInformation<? extends Comparable<?>>> {

	private Comparator<Node> nodeComparator = new Comparator<Node>() {
		@Override
		public int compare(Node o1, Node o2) {
			Integer label1 = o1.get("intLabel").asInt();
			Integer label2 = o2.get("intLabel").asInt();
			return label1.compareTo(label2);
		}

	};
	private Map<Node, AnalysisTableEntry<T>> map = new TreeMap<Node, AnalysisTableEntry<T>>(nodeComparator);

	public AnalysisTable() {

	}

	public Map<Node, AnalysisTableEntry<T>> getMap() {
		return map;
	}

	public void setMap(Map<Node, AnalysisTableEntry<T>> map) {
		this.map = map;
	}

	public AnalysisTableEntry<T> getEntry(Node node) {
		AnalysisTableEntry<T> entry = this.map.get(node);
		if (entry == null) {
			entry = new AnalysisTableEntry<T>(node);
			this.map.put(node, entry);
		}
		return entry;
	}

	public void printTable() {
		List<List<String>> rows = new LinkedList<>();
		List<String> headers = Arrays.asList("Node", "GEN", "KILL", "ENTRY", "EXIT");
		rows.add(headers);
		Set<Entry<Node, AnalysisTableEntry<T>>> entries = this.map.entrySet();
		for (Entry<Node, AnalysisTableEntry<T>> entry : entries) {
			rows.add(Arrays.asList(entry.getKey().get("text").asString(), entry.getValue().getGenStr(),
					entry.getValue().getKillStr(), entry.getValue().getEntryStr(), entry.getValue().getExitStr()));
		}
		TablePrinter.formatAsTable(rows);
	}

	public void checkPoint() {
		Set<Entry<Node, AnalysisTableEntry<T>>> entries = this.map.entrySet();
		for (Entry<Node, AnalysisTableEntry<T>> entry : entries) {
			entry.getValue().checkPoint();
		}
	}

	public boolean isStable() {
		boolean stable = true;
		Set<Entry<Node, AnalysisTableEntry<T>>> entries = this.map.entrySet();
		for (Entry<Node, AnalysisTableEntry<T>> entry : entries) {
			boolean isEntryStable = entry.getValue().isStable();
			if (!isEntryStable) {
				stable = false;
				break;
			}
		}
		return stable;
	}

	public Set<T> getGen(Node node) {
		return this.getEntry(node).getGen();
	}

	public Set<T> getKill(Node node) {
		return this.getEntry(node).getKill();
	}

}
