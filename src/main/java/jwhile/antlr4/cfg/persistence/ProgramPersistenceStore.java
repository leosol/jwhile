package jwhile.antlr4.cfg.persistence;

import java.util.List;

import org.javatuples.Pair;
import org.neo4j.driver.types.Node;

import jwhile.antlr4.cfg.entities.Program;

public interface ProgramPersistenceStore {

	public void openSession();

	public void closeSession();

	public void persistNewProgram(Program program);

	/**
	 * List of Pairs joind by flow edges
	 * 
	 * @param programId
	 * @return
	 */
	public List<Pair<Node, Node>> findFlow(String programId);

	/**
	 * List of pairs in reverse order
	 * 
	 * @param programId
	 * @return
	 */
	public List<Pair<Node, Node>> findFlowR(String programId);

	void clearDatabase();

	/**
	 * List of Pairs Source Node -> Identifier
	 * 
	 * @param programId
	 * @return
	 */
	public List<Pair<Node, Node>> findAssignments(String programId);

	/**
	 * List of Pairs Source Node -> NonTrivialExpression
	 * 
	 * @param programId
	 * @return
	 */
	public List<Pair<Node, Node>> findNonTrivialExpressions(String programId);

	/**
	 * List of NonTrivials of a node
	 * 
	 * @param programId
	 * @param nodeId
	 * @return
	 */
	public List<Node> findNonTrivalExpressions(String programId, Long nodeId);

	/**
	 * List of Trivials of a node
	 * 
	 * @param programId
	 * @param nodeId
	 * @return
	 */
	public List<Node> findTrivialExpressions(String programId, Long nodeId);

	/**
	 * List of NonTrivials that uses a variable
	 * 
	 * @param programId
	 * @param identifierText
	 * @return
	 */
	public List<Node> findNonTrivalExpressionsByIdentifier(String programId, String identifierText);

	/**
	 * List of Identifiers referenced by an Assignment node
	 * 
	 * @return List<Node> The identifier nodes
	 */
	public List<Node> findAssignmentsIdentifiers(String programId, Long nodeId);

	/**
	 * List of Identifiers somehow used by a non trivial expression
	 * 
	 * @param programId
	 * @param nodeId
	 * @return
	 */
	public List<Node> findNonTrivialExpressionIdentifiers(String programId, Long nodeId);

	/**
	 * List of NonTrivial Expressions that does not reference some identifier
	 * 
	 * @param programId
	 * @param nodeId
	 * @param identifier
	 * @return
	 */
	public List<Node> findNonTrivalExpressionsWithoutIdentifier(String programId, Long nodeId, String identifier);

	/**
	 * List of NonTrivial Expressions that does reference some identifier
	 * 
	 * @param programId
	 * @param nodeId
	 * @param identifier
	 * @return
	 */
	public List<Node> findNonTrivalExpressionsWithIdentifier(String programId, Long nodeId, String identifier);

	/**
	 * List of Nodes conected by FlowEdges
	 * 
	 * @param programId
	 * @param nodeId
	 * @param identifier
	 * @return
	 */
	public List<Node> findControlFlowNodes(String programId);

	/**
	 * List of All NonTrivials of the program with reference to the identifier
	 * 
	 * @param programId
	 * @param identifier
	 * @return
	 */
	public List<Node> findNonTrivalExpressionsWithIdentifier(String programId, String identifier);

	/**
	 * List of all identifiers found in Trivial and NonTrivial expressions
	 * 
	 * @param programId
	 * @param asLong
	 * @return
	 */
	public List<Node> findExpressionsIdentifiers(String programId, Long nodeId);

	/**
	 * List of Identifiers found in Trivials
	 * 
	 * @param programId
	 * @param nodeId
	 * @return
	 */
	public List<Node> findTrivialExpressionIdentifiers(String programId, Long nodeId);

	/**
	 * Returns the start node
	 * 
	 * @param programId
	 * @return
	 */
	public Node findProgramStart(String programId);

	/**
	 * Return the end node
	 * 
	 * @param programId
	 * @return
	 */
	public Node findProgramEnd(String programId);

}
