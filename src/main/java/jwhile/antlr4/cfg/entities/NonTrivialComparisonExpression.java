package jwhile.antlr4.cfg.entities;

public class NonTrivialComparisonExpression extends NonTrivialBExp {

	protected NonTrivialComparisonExpression(String operation, Exp left, Exp right, String text) {
		super(operation, left, right, text);
	}

}
