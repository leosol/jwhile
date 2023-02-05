package jwhile.antlr4.cfg.entities;

public abstract class UsageEdge extends Edge {

	private Entity from;
	private Entity to;
	private String qualifier;

	protected UsageEdge() {
		
	}
	
	protected UsageEdge(Entity from, Entity to) {
		this.from = from;
		this.to = to;
	}
	
	protected UsageEdge(Entity from, Entity to, String qualifier) {
		this.from = from;
		this.to = to;
		this.qualifier = qualifier;
	}

	public Entity getFrom() {
		return from;
	}

	public void setFrom(Entity from) {
		this.from = from;
	}

	public Entity getTo() {
		return to;
	}

	public void setTo(Entity to) {
		this.to = to;
	}

	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	@Override
	public String toString() {
		if (qualifier != null) {
			return getName() + " from: " + getFrom() + " to " + getTo() + " qualifier " + qualifier;
		}
		return getName() + " from: " + from + " to " + to;
	}

}
