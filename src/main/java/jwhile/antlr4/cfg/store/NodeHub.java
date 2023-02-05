package jwhile.antlr4.cfg.store;

import java.util.LinkedList;
import java.util.List;

import org.neo4j.driver.types.Node;

public class NodeHub extends FlowNode {

	private FlowNode startPoint;
	private List<FlowNode> endPoints = new LinkedList<FlowNode>();
	private boolean conditional = false;

	public NodeHub(FlowNode start, FlowNode... end) {
		super(null, null);
		startPoint = start;
		for (FlowNode basicNode : end) {
			endPoints.add(basicNode);
		}
	}

	public NodeHub(FlowNode start, List<FlowNode> endPoints) {
		super(null, null);
		this.startPoint = start;
		this.endPoints = endPoints;
	}

	public FlowNode getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(FlowNode startPoint) {
		this.startPoint = startPoint;
	}

	public List<FlowNode> getEndPoints() {
		return endPoints;
	}

	public void setEndPoints(List<FlowNode> endPoints) {
		this.endPoints = endPoints;
	}

	@Override
	public String getLabel() {
		throw new IllegalStateException();
	}

	@Override
	public void setLabel(String label) {
		throw new IllegalStateException();
	}

	@Override
	public String getText() {
		throw new IllegalStateException();
	}

	@Override
	public void setText(String text) {
		throw new IllegalStateException();
	}

	@Override
	public Node getNode() {
		throw new IllegalStateException();
	}

	@Override
	public void setNode(Node node) {
		throw new IllegalStateException();
	}

	public boolean isConditional() {
		return conditional;
	}

	public void setConditional(boolean conditional) {
		this.conditional = conditional;
	}

	public static NodeHub join(FlowNode a, FlowNode b) {
		FlowNode start, startA, startB;
		List<FlowNode> ends = new LinkedList<FlowNode>();
		if (a instanceof NodeHub) {
			startA = ((NodeHub) a).startPoint;
			ends.addAll(((NodeHub) a).getEndPoints());
		} else {
			startA = a;
			ends.add(a);
		}
		if (b instanceof NodeHub) {
			startB = ((NodeHub) b).startPoint;
			ends.addAll(((NodeHub) b).getEndPoints());
		} else {
			startB = b;
			ends.add(b);
		}
		start = null;
		if (startA != null) {
			start = startA;
		}
		if (startB != null) {
			start = startB;
		}
		NodeHub res = new NodeHub(start, ends);
		return res;
	}

}
