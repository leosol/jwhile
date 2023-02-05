package jwhile.antlr4.cfg.store;

public interface CFGStore {
	
	public CFGProject createCFGProject();

	public void createFlowNode(CFGProject prj, FlowNode start);
	
	public void createFlowEdge(CFGProject prj, FlowEdge edge);
	
	public void createLiteralNode(CFGProject prj, LiteralNode n);
	
	public void createUsageEdge(CFGProject prj, UsageEdge edge);

	public void createIdentifierNode(CFGProject prj, IdentifierNode n);

	public void createOperationNode(CFGProject cfgProject, OperationNode o);

	public void createAssignmentEdge(CFGProject cfgProject, UsageEdge edge);

}
