package jwhile.antlr4.cfg.entities;

public class Label {

	private long intLabel;

	private String contextLabel;

	public Label(long intLabel, String contextLabel) {
		this.intLabel = intLabel;
		this.contextLabel = contextLabel;
	}

	public long getIntLabel() {
		return intLabel;
	}

	public String getContextLabel() {
		return contextLabel;
	}

	@Override
	public int hashCode() {
		return (this.contextLabel+this.intLabel).hashCode();
	}

	@Override
	public String toString() {
		return contextLabel;
	}

}
