package jwhile.antlr4.cfg.entities;

public class NonTrivialBooleanExpression extends NonTrivialBExp {

	protected NonTrivialBooleanExpression(String operation, BExp left, BExp right, String text) {
		super(operation, left, right, text);
	}

}
