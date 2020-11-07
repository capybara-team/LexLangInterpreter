/**
Maxwell Souza       201435009
Rodolpho Rossete    201435032
*/

grammar LexLang;




fragment DIGIT: [0-9]; // normal digit
fragment CHAR_TYPES : ~('\\') | '\\n' | '\\t' | '\\\\' | '\\\''; // possible digits

// Syntax
prog : (data)* (func)*;
data : 'data' ID '{' (decl)* '}';
decl : ID '::' type ';';
func : ID '(' params? ')' (':' type (',' type)* )? '{' (cmd)* '}';
params : ID '::' type (',' ID '::' type)*;
type : type '[' ']'
| btype;
btype : INT
| CHAR
| BOOL
| FLOAT
| ID;
cmd : '{' (cmd)* '}'
| IF '(' exp ')' cmd
| IF '(' exp ')' cmd ELSE cmd
| ITERATE '(' exp ')' cmd
| READ lvalue ';'
| PRINT exp ';'
| RETURN exp (',' exp)* ';'
| lvalue SET exp ';'
| ID '(' (exps)? ')' ('<' lvalue (',' lvalue )* '>' )? ';';
exp : exp AND exp
| rexp;
rexp : aexp LESS_THAN aexp
| rexp EQUALS aexp
| rexp NOTEQ aexp
| aexp;
aexp : aexp PLUS mexp
| aexp MINUS mexp
| mexp;
mexp : mexp MULTIPLY sexp
| mexp DIVIDE sexp
| mexp MOD sexp
| sexp;
sexp : NOT sexp
| MINUS sexp
| TRUE
| FALSE
| NULL
| INT_NUM
| FLOAT_NUM
| CHAR_VAL
| pexp;
pexp : lvalue
| '(' exp ')'
| 'new' type ( '[' exp ']' )?
| ID '(' exps? ')' ('[' exp ']')?;
lvalue : ID
| lvalue '[' exp ']'
| lvalue DOT ID;
exps : exp  (',' exp)* ;

/* LEXICON */

WS : [ \t\r\n]+ -> skip ;
COMMENT: '{-' .*? '-}' -> skip;
LINE_COMMENT: '--' ~('\r' | '\n')* ('\r' | '\n') -> skip;


/* keywords */
IF:         'if'     ;
ELSE:       'else'   ;
ITERATE:    'iterate';
READ:       'read'   ;
PRINT:      'print'  ;
RETURN:     'return' ;
DATA:       'data'   ;

/* types */
INT:    'Int'   ;
CHAR:   'Char'  ;
BOOL:   'Bool'  ;
FLOAT:  'Float' ;
NULL:   'null'  ;
TRUE:   'true'  ;
FALSE:  'false' ;

/* identifiers */
ID : [a-zA-Z][a-zA-Z0-9]* ;

/* literals */
INT_NUM : '0' | [1-9][0-9]* ;
FLOAT_NUM : ('0' | [1-9][0-9]*) '.' DIGIT+ ;

CHAR_VAL : '\'' CHAR_TYPES '\'';
//STRING : '"' .*?  '"' ; // multiline string?
//STRING : '"' ~('\r' | '\n')*  '"' ; // TODO: corrigir aspas

/* operators */
SET:        '=' ;
EQUALS:     '==';
NOTEQ:      '!=';
NOT:        '!' ;
PLUS:       '+' ;
MINUS:      '-' ;
MULTIPLY:   '*' ;
DIVIDE:     '/' ;
MOD:        '%' ;

/*Logic*/
LESS_THAN :     '<' ;
BIGGER_THAN :   '>' ;
AND :           '&&';
DOT :           '.' ;
COMMA :         ',' ;

ERROR_CHAR : . ; // For identify errors

// Unused tokens

//PAR_OPEN :      '(' ;
//PAR_CLOSE :     ')' ;
//BRACE_OPEN :    '{' ;
//BRACE_CLOSE :   '}' ;
//BRACKET_OPEN :  '[' ;
//BRACKET_CLOSE : ']' ;
//TYPEDEF :       '::';
//RETURNDEF :     ':' ;
//SEMICOL:         ';' ;