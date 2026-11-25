grammar ItemSearchGrammar;

@header {
    package notkirill.workshopmanager.item.library.search.antlr;
}

// Parser Rules
query     : or_expr EOF ;
or_expr   : and_expr (OR and_expr)* ;
and_expr  : not_expr (AND? not_expr)* ;
not_expr  : NOT? primary ;
primary   : LPAREN or_expr RPAREN | criteria ;
criteria  : TAG? (comp_val | range_val | PHRASE | WORD) ;

comp_val  : op=(GT|GTE|LT|LTE|EQ) val=WORD ;
range_val : start=WORD? RANGE end=WORD? ;

// Lexer Rules
TAG       : ([a-zA-Z_][a-zA-Z0-9_]* '.')? [a-zA-Z_][a-zA-Z0-9_]* ':' ;
AND       : 'AND' | '&&' ;
OR        : 'OR' | '||' ;
NOT       : 'NOT' | '!' | '-' ;
GT : '>' ; GTE : '>=' ; LT : '<' ; LTE : '<=' ; EQ : '=' ; RANGE : '..' ;

PHRASE    : '"' ( '\\' . | ~["\\] )* '"' ;
fragment WORD_CHAR : ~[ \t\r\n()!":><=.-] ;
WORD : WORD_CHAR+ ( ('.' | '-') WORD_CHAR+ )* ;

WS        : [ \t\r\n]+ -> skip ;
LPAREN : '(' ;
RPAREN : ')' ;