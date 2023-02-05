package jwhile.antlr4.cfg.entities;

import java.util.List;

public interface FlowEntity {
	
	public Label getInitLabel();
	
	public List<Label> getFinalLabels();
	
	public List<FlowEdge> flow();

}
