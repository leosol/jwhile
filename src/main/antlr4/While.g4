/** 
https://raw.githubusercontent.com/antlr/codebuff/master/grammars/org/antlr/codebuff/Java8.g4 
https://tomassetti.me/antlr-mega-tutorial/
**/
grammar While;

program: 	stmts+;

stmts
    :stmt+';' (stmt+';')*
    ;

stmt
	:	assignment
	|	ifThenStatement
	|	ifThenElseStatement
	|	whileStatement
	|	print
	|	read
	;
		
assignment: Identifier '=' expression;

ifThenStatement
	:	'if' '(' expression ')' '{' ifTrueStmts '}' 
	;
	
ifThenElseStatement
	:	'if' '(' expression ')' '{' ifTrueStmts '}' 'else' '{' ifFalseStmts '}' 
	;
	
whileStatement
	:	'while' '(' expression ')' '{' stmts '}'
	;
	
ifTrueStmts: stmts;

ifFalseStmts: stmts;
	
print: 
	'print' (Literal | Identifier);

read: 
	'read' (Identifier);
	
expression
	:   (IntegerLiteral|BooleanLiteral|NullLiteral|Identifier)
	|	aExp
	|	bExp
	;
	
aExp
	:	aExpLeftHand (AddOperation|MulOperation) aExpRightHand
	;
	
bExp
	: 	aExpLeftHand (ComparisonOperation|EqualityOperation) aExpRightHand
	|	bExpLeftHand (EqualityOperation|BinaryLogicalOperator) bExpRightHand
	|	bExpLeftHand
	| 	NotOperator bExpLeftHand
	;
	
aExpLeftHand: (IntegerLiteral|Identifier);

aExpRightHand: (IntegerLiteral|Identifier|aExp);

bExpLeftHand: (BooleanLiteral|Identifier);

bExpRightHand: (BooleanLiteral|Identifier|bExp);
	
AddOperation: ('+'|'-');
	
MulOperation: ('*'|'/'|'%');

EqualityOperation: ('=='|'!=');

NotOperator: '!';

BinaryLogicalOperator: ('&&'|'||');

ComparisonOperation: ('<'|'>'|'<='|'>=');
	
equalityExpression
	:aExp ('=='|'!=') aExp
	|aExp ('=='|'!=') BooleanLiteral;
	
relationalExpression
	:	aExp '<' aExp
	|	aExp '>' aExp
	|	aExp '<=' aExp
	|	aExp '>=' aExp
	;
		
IntegerLiteral
	:	DecimalIntegerLiteral
	;
	
BooleanLiteral
	:	'true'
	|	'false'
	;	

NullLiteral
	:	'null'
	;
	
fragment
DecimalIntegerLiteral
	:	DecimalNumeral
	;	
	
fragment
DecimalNumeral
	:	'0'
	|	NonZeroDigit (Digits?)
	;
	
fragment
Digits
	:	Digit (Digit)?
	;
		
fragment
Digit
	:	'0'
	|	NonZeroDigit
	;
		
fragment
NonZeroDigit
	:	[1-9]
	;
		
Identifier
	:	JavaLetter
	;

fragment
JavaLetter
	:	[a-zA-Z$_]
	;

LBR :  '(' ;
RBR :  ')' ;
MLP :  '*' ;
DIV :  '/' ;
PWR :  '^' ;

LSS :  '<'  ;
LSQ :  '<=' ;
GRT :  '>'  ;
GRQ :  '>=' ;
EQL :  '==' ;
NEQ :  '!=' ;
AND :  '&&' ;
OR  :  '||' ;
NOT :  '!'  ;
			
//
// Whitespace and comments
//

WS  :  [ \t\r\n\u000C]+ -> channel(HIDDEN)
    ;

COMMENT
    :   '/*' .*? '*/' -> channel(HIDDEN)
    ;

LINE_COMMENT
    :   '//' ~[\r\n]* -> channel(HIDDEN)
    ;