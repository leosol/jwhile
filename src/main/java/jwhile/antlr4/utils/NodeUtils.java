package jwhile.antlr4.utils;

import java.util.LinkedList;
import java.util.List;

import org.neo4j.driver.types.Node;

public class NodeUtils {

	public static List<String> nodesToTextList(List<Node> nodes) {
		List<String> out = new LinkedList<String>();
		if (nodes == null) {
			return out;
		}
		for (Node node : nodes) {
			out.add(node.get("text").asString());
		}
		return out;
	}

}
