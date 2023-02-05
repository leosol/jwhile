package jwhile.antlr4.cfg.entities;

public abstract class Stmt extends Entity implements FlowEntity {

	private String text;
	
	protected Stmt() {
		
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return getName()+": "+getText();
	}

}
