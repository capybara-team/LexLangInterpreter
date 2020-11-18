/*
  Maxwell Souza 201435009
  Rodolpho Rossete 201435032
 */


package semantics;

import org.antlr.v4.runtime.ParserRuleContext;

public class LangException extends RuntimeException {

    public ParserRuleContext node = null;

    public LangException(String message) {
        super(message);
    }

    public LangException(String message, ParserRuleContext node) {
        super(message);
        this.node = node;
    }

    @Override
    public String getMessage() {
        String message = node == null ? "" : "Line " +
                node.getStart().getLine() + ':' +
                node.getStart().getCharPositionInLine() + ' ';
        return message + super.getMessage();
    }

    public String getMessage(ParserRuleContext node) {
        if (this.node == null)
            this.node = node;
        return getMessage();
    }
}
