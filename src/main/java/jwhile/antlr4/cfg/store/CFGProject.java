package jwhile.antlr4.cfg.store;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.neo4j.driver.types.Node;

public class CFGProject {

	private String projectName;

	private Map<String, Integer> labelCount = new HashMap<String, Integer>();
	private CFGStore cfgStore;
	private FlowNode start;
	private FlowNode end;
	private Node node;
	private Map<String, LiteralNode> literalsCache = new HashMap<String, LiteralNode>();
	private Map<String, IdentifierNode> identifiersCache = new HashMap<String, IdentifierNode>();

	public CFGProject(String projectName, CFGStore cfgStore) {
		this.projectName = projectName;
		this.cfgStore = cfgStore;
	}

	public String getNextLabel(String context) {
		Integer i = this.labelCount.get(context);
		if (i == null) {
			i = 0;
		} else {
			i = i + 1;
		}
		this.labelCount.put(context, i);
		return context + ":" + i;
	}

	public FlowNode createProgramStart() {
		FlowNode start = new FlowNode(getProgramStartLabel(), "START");
		cfgStore.createFlowNode(this, start);
		this.start = start;
		return start;
	}

	public FlowNode createProgramEnd() {
		FlowNode end = new FlowNode(getProgramEndLabel(), "END");
		cfgStore.createFlowNode(this, end);
		this.end = end;
		return end;
	}

	public FlowNode createStmtFlowNode(String text) {
		FlowNode n = new FlowNode(getNextStmtLabel(), text);
		cfgStore.createFlowNode(this, n);
		return n;
	}

	public OperationNode createOperationNode(String text) {
		OperationNode o = new OperationNode(getNextLabel("operation"), text);
		cfgStore.createOperationNode(this, o);
		return o;
	}

	public IdentifierNode createIdentifierNode(String text) {
		IdentifierNode existingNode = this.identifiersCache.get(text);
		if (existingNode == null) {
			IdentifierNode n = new IdentifierNode(getNextIdentifierLabel(), text);
			cfgStore.createIdentifierNode(this, n);
			this.identifiersCache.put(text, n);
			return n;
		}
		return existingNode;
	}

	public LiteralNode createLiteralNode(String text) {
		LiteralNode existingNode = this.literalsCache.get(text);
		if (existingNode == null) {
			LiteralNode n = new LiteralNode(getNextLiteralLabel(), text);
			cfgStore.createLiteralNode(this, n);
			this.literalsCache.put(text, n);
			return n;
		}
		return existingNode;
	}

	private void doCreateFlowEdge(FlowNode from, FlowNode to) {
		FlowEdge edge = new FlowEdge(from, to);
		cfgStore.createFlowEdge(this, edge);
	}

	public void createFlowEdge(FlowNode from, FlowNode to) {
		if (from instanceof NodeHub) {
			this.createFlowEdge((NodeHub) from, to);
			return;
		}
		if (to instanceof NodeHub) {
			this.createFlowEdge(from, (NodeHub) to);
			return;
		}
		doCreateFlowEdge(from, to);
	}

	public void createFlowEdge(NodeHub from, FlowNode to) {
		if (to instanceof NodeHub) {
			this.createFlowEdge(from, (NodeHub) to);
			return;
		}
		List<FlowNode> ends = from.getEndPoints();
		for (FlowNode basicNode : ends) {
			this.createFlowEdge(basicNode, to);
		}
	}

	public void createFlowEdge(FlowNode from, NodeHub to) {
		if (from instanceof NodeHub) {
			this.createFlowEdge((NodeHub) from, to);
			return;
		}
		createFlowEdge(from, to.getStartPoint());
	}

	public void createFlowEdge(NodeHub from, NodeHub to) {
		List<FlowNode> ends = from.getEndPoints();
		for (FlowNode basicNode : ends) {
			this.doCreateFlowEdge(basicNode, (FlowNode) to.getStartPoint());
		}
	}

	public UsageEdge createAssignmentEdge(FlowNode from, BasicNode to) {
		UsageEdge edge = new UsageEdge(from, to);
		cfgStore.createAssignmentEdge(this, edge);
		return edge;
	}
	
	public UsageEdge createUsageEdge(FlowNode from, BasicNode to) {
		UsageEdge edge = new UsageEdge(from, to);
		cfgStore.createUsageEdge(this, edge);
		return edge;
	}

	public UsageEdge createUsageEdge(OperationNode opNode, BasicNode leftValue) {
		UsageEdge edge = new UsageEdge(opNode, leftValue);
		cfgStore.createUsageEdge(this, edge);
		return edge;
	}

	public String getProgramStartLabel() {
		return "program:start";
	}

	public String getProgramEndLabel() {
		return "program:end";
	}

	public String getNextStmtLabel() {
		return getNextLabel("stmt");
	}

	public String getNextIdentifierLabel() {
		return getNextLabel("identifier");
	}

	public String getNextLiteralLabel() {
		return getNextLabel("literal");
	}

	public String getProjectName() {
		return projectName;
	}

	public FlowNode getStart() {
		return start;
	}

	public FlowNode getEnd() {
		return end;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

}
