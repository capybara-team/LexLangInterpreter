/**
 Maxwell Souza 201435009 Rodolpho Rossete 201435032
 */

grammar LexLang;

// Fragment helpers
fragment DIGIT: [0-9]; // normal digit
fragment CHAR_TYPES: // possible digits
	~('\\')
	| '\\n'
	| '\\t'
	| '\\\\'
	| '\\\''
    ;

// Syntax
prog: (data)* (func)*;
data: 'data' ID '{' (decl)* '}';
decl: ID '::' type ';';
func: ID '(' params? ')' (':' type (',' type)*)? '{' funcCmds '}';
funcCmds: (cmd)*;
params: ID '::' type (',' ID '::' type)*;
type: type '[' ']' # arrayType | btype # btypeCall;
btype: INT | CHAR | BOOL | FLOAT | ID;

cmd:
	'{' (cmd)* '}'												# closureCmd
	| IF '(' exp ')' cmd										# ifCmd
	| IF '(' exp ')' cmd ELSE cmd								# elseCmd
	| ITERATE '(' exp ')' cmd									# iterateCmd
	| READ lvalue ';'											# readCmd
	| PRINT exp ';'												# printCmd
	| RETURN exp (',' exp)* ';'									# returnCmd
	| lvalue SET exp ';'										# attrCmd
	| ID '(' (exps)? ')' ('<' lvalue (',' lvalue)* '>')? ';'	# funcCmd
    ;
exp: exp AND exp # andExp | rexp # rexpCall;
rexp:
	aexp LESS_THAN aexp	                    # lessThanRexp
	| rexp op=(EQUALS | NOTEQ) aexp	        # compareRexp
	| aexp				                    # aexpCall
    ;
aexp:
	aexp op=(PLUS | MINUS) mexp             # addAexp
	| mexp                                  # mexpCall
    ;
mexp:
	mexp op=(MULTIPLY | DIVIDE | MOD) sexp  # multiplyMexp
	| sexp				                    # sexpCall
    ;
sexp:
	NOT sexp		                        # notSexp
	| MINUS sexp	                        # negativeSexp
	| (TRUE | FALSE)                        # boolSexp
	| NULL			                        # nullSexp
	| INT_NUM		                        # intSexp
	| FLOAT_NUM		                        # floatSexp
	| CHAR_VAL		                        # charSexp
	| pexp			                        # pexpCall
    ;
pexp:
	lvalue							        # readVarPexp
	| '(' exp ')'						    # closurePexp
	| 'new' type ( '[' exp ']')?		    # instancePexp
	| ID '(' exps? ')' ('[' exp ']')?	    # funcCallPexp
    ;
lvalue:
	ID						                # identifierValue
	| lvalue '[' exp ']'	                # arrayValue
	| lvalue DOT ID			                # objectValue
    ;
exps: exp (',' exp)*;

/* LEXICON */

WS: [ \t\r\n]+ -> skip;
COMMENT: '{-' .*? '-}' -> skip;
LINE_COMMENT: '--' ~('\r' | '\n')* ('\r' | '\n') -> skip;

/* keywords */
IF: 'if';
ELSE: 'else';
ITERATE: 'iterate';
READ: 'read';
PRINT: 'print';
RETURN: 'return';
DATA: 'data';

/* types */
INT: 'Int';
CHAR: 'Char';
BOOL: 'Bool';
FLOAT: 'Float';
NULL: 'null';
TRUE: 'true';
FALSE: 'false';

/* identifiers */
ID: [a-zA-Z][a-zA-Z0-9]*;

/* literals */
INT_NUM: '0' | [1-9][0-9]*;
FLOAT_NUM: ('0' | [1-9][0-9]*) '.' DIGIT+;

CHAR_VAL: '\'' CHAR_TYPES '\'';
//STRING : '"' .*? '"' ; // multiline string? STRING : '"' ~('\r' | '\n')* '"' ; // TODO: corrigir
// aspas

/* operators */
SET: '=';
EQUALS: '==';
NOTEQ: '!=';
NOT: '!';
PLUS: '+';
MINUS: '-';
MULTIPLY: '*';
DIVIDE: '/';
MOD: '%';

/*Logic*/
LESS_THAN: '<';
BIGGER_THAN: '>';
AND: '&&';
DOT: '.';
COMMA: ',';

ERROR_CHAR: .; // For identify errors

// Unused tokens

//PAR_OPEN : '(' ; PAR_CLOSE : ')' ; BRACE_OPEN : '{' ; BRACE_CLOSE : '}' ; BRACKET_OPEN : '[' ;
// BRACKET_CLOSE : ']' ; TYPEDEF : '::'; RETURNDEF : ':' ; SEMICOL: ';' ;