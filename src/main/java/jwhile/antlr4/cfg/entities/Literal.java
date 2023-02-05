package jwhile.antlr4.cfg.entities;

public class Literal extends Entity {

	private LiteralType type;
	private String text;

	protected Literal(LiteralType type, String text) {
		this.type = type;
		this.text = text;
	}

	@Override
	public String getName() {
		return type.name();
	}

	public LiteralType getType() {
		return type;
	}

	public void setType(LiteralType type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return getName() + " " + type + " " + text;
	}

}
