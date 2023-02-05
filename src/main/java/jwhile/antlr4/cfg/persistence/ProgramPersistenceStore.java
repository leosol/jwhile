package jwhile.antlr4.cfg.persistence;

import jwhile.antlr4.cfg.entities.Program;

public interface ProgramPersistenceStore {
	
	public void openSession();

	public void closeSession();
	
	public void persistNewProgram(Program program);

	

}
