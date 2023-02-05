package jwhile.antlr4.cfg.entities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.javatuples.Triplet;

public class Program {

	private static final ThreadLocal<Program> program = new ThreadLocal<Program>();

	private String programId;
	private String analyst;
	private EntityFactory entityFactory = new EntityFactory();
	private Long nextEntityId = 0L;
	private Stmt start = null;

	public Program(String programId) {
		this.programId = programId;
		setProgram(this);
	}

	public String getProgramId() {
		return programId;
	}

	public String getAnalyst() {
		return analyst;
	}

	public void setAnalyst(String analyst) {
		this.analyst = analyst;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public static void setProgram(Program prg) {
		program.set(prg);
	}

	public static Program getProgram() {
		return program.get();
	}

	public EntityFactory getEntityFactory() {
		return entityFactory;
	}

	public void setEntityFactory(EntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

	public Long getNextEntityId() {
		long actual = this.nextEntityId;
		this.nextEntityId = this.nextEntityId + 1;
		return actual;
	}

	public Stmt getStart() {
		return start;
	}

	public void setStart(Stmt start) {
		this.start = start;
	}

	public Triplet<List<FlowEdge>, Set<Entity>, Set<UsageEdge>> getProgramData() {
		List<FlowEdge> cfg = start.flow();
		Set<Entity> entities = new HashSet<Entity>();
		Set<UsageEdge> usageEdges = new HashSet<UsageEdge>();
		for (FlowEdge edge : cfg) {
			Entity from = this.entityFactory.getEntity(edge.getFrom());
			Entity to = this.entityFactory.getEntity(edge.getTo());
			entities.add(from);
			entities.add(to);
			usageEdges.addAll(from.getUsages());
			usageEdges.addAll(to.getUsages());
		}
		for (UsageEdge usageEdge : usageEdges) {
			entities.add(usageEdge.getFrom());
			entities.add(usageEdge.getTo());
		}
		return new Triplet<List<FlowEdge>, Set<Entity>, Set<UsageEdge>>(cfg, entities, usageEdges);
	}

}
