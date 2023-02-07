package jwhile.antlr4.analysis.algorithms;

import java.util.List;
import java.util.Set;

import org.neo4j.driver.types.Node;

import jwhile.antlr4.cfg.entities.Program;
import jwhile.antlr4.cfg.persistence.ProgramPersistenceStore;

public abstract class Algorithm<T extends AnalysisInformation<? extends Comparable<?>>> {

	private Program program;
	private ProgramPersistenceStore store;
	private Set<T> cachedSuperset;

	public Algorithm(Program program, ProgramPersistenceStore store) {
		this.program = program;
		this.store = store;
	}

	public abstract ConcernType getConcernType();

	public abstract FlowType getFlowType();

	public abstract Set<T> gen(Node n);

	public abstract Set<T> kill(Node n);

	public abstract MeetOperator getMeetOperator();

	public abstract List<Node> getExtremeEdges();

	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
	}

	public ProgramPersistenceStore getStore() {
		return store;
	}

	public void setStore(ProgramPersistenceStore store) {
		this.store = store;
	}

	public abstract Set<T> getInitSet();

	public Set<T> findCachedSuperset() {
		if (cachedSuperset == null) {
			cachedSuperset = findSuperset();
		}
		return cachedSuperset;
	}

	public abstract Set<T> findSuperset();
}
