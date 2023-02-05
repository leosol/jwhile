package jwhile.antlr4.cfg.entities;

import java.util.HashSet;
import java.util.Set;

public abstract class Entity {

	private Program program;
	private Long id;

	protected Entity() {
		this.program = Program.getProgram();
		this.id = this.program.getNextEntityId();
	}

	public String getName() {
		return this.getClass().getSimpleName();
	}

	public Set<UsageEdge> getUsages() {
		return new HashSet<UsageEdge>();
	}

	@Override
	public String toString() {
		return getName() + "" + hashCode();
	}

	public Program getProgram() {
		return program;
	}

	public Long getId() {
		return id;
	}

}
