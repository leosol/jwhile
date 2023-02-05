package jwhile.antlr4.cfg.entities;

public class Identifier extends Entity {

	private String text;

	protected Identifier(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return getName()+": " + getText();
	}

}
