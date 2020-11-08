package lexlang;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;

public class LexLangInterpreter extends LexLangBaseVisitor<Value> {

    /**
     * Run a program
     */
    public Value run(ParseTree prog) {
        return this.visit(prog);
    }

    @Override
    public Value visitProg(LexLangParser.ProgContext ctx) {
        return super.visitProg(ctx);
    }

    @Override
    public Value visitFunc(LexLangParser.FuncContext ctx) {
        return super.visitFunc(ctx);
    }

    // while

    @Override
    public Value visitIterateCmd(LexLangParser.IterateCmdContext ctx) {

        while (visit(ctx.exp()).getBool()) {
            visit(ctx.cmd());
        }

        return Value.VOID;
    }


    // if

    @Override
    public Value visitIfCmd(LexLangParser.IfCmdContext ctx) {
        Value result = visit(ctx.exp());
        if(result.getBool())
            visit(ctx.cmd());
        return  Value.VOID;
    }

    @Override
    public Value visitElseCmd(LexLangParser.ElseCmdContext ctx) {
        Value result = visit(ctx.exp());
        if(result.getBool())
            visit(ctx.cmd(0));
        else
            visit(ctx.cmd(1));
        return  Value.VOID;
    }

    // Logic

    @Override
    public Value visitAndExp(LexLangParser.AndExpContext ctx) {
        Value v1 = this.visit(ctx.exp(0)),
                v2 = this.visit(ctx.exp(1));
        return new Value(v1.getBool() && v2.getBool());
    }

    @Override
    public Value visitLessThanRexp(LexLangParser.LessThanRexpContext ctx) {
        Value v1 = this.visit(ctx.aexp(0)),
                v2 = this.visit(ctx.aexp(1));
        return new Value(v1.getFloat() < v2.getFloat());
    }

    @Override
    public Value visitCompareRexp(LexLangParser.CompareRexpContext ctx) {
        Value v1 = this.visit(ctx.rexp()),
                v2 = this.visit(ctx.aexp());
        if(ctx.op.getType() == LexLangParser.EQUALS)
            return new Value(v1.equals(v2));
        return new Value(!v1.equals(v2));
    }

    // Math

    @Override
    public Value visitAddAexp(LexLangParser.AddAexpContext ctx) {
        Value v1 = this.visit(ctx.aexp()),
                v2 = this.visit(ctx.mexp());
        if(ctx.op.getType() == LexLangParser.PLUS)
                return new Value(v1.getFloat() + v2.getFloat());
        return new Value(v1.getFloat() - v2.getFloat());
    }

    @Override
    public Value visitMultiplyMexp(LexLangParser.MultiplyMexpContext ctx) {
        Value v1 = this.visit(ctx.mexp()),
                v2 = this.visit(ctx.sexp());
        switch (ctx.op.getType()) {
            case LexLangParser.MULTIPLY:
                return new Value(v1.getFloat() * v2.getFloat());
            case LexLangParser.DIVIDE:
                return new Value(v1.getFloat() / v2.getFloat());
            case LexLangParser.MOD:
                return new Value(v1.getFloat() % v2.getFloat());
            default:
            throw new RuntimeException("unknown operator: " + ctx.op.getText());
        }
    }

    // TODO: check if any value can be negated (strings, numbers)
    @Override
    public Value visitNotSexp(LexLangParser.NotSexpContext ctx) {
        Value value = this.visit(ctx.sexp());
        return new Value(!value.getBool());
    }

    // TODO: improve usage of int and float
    @Override
    public Value visitNegativeSexp(LexLangParser.NegativeSexpContext ctx) {
        Value value = this.visit(ctx.sexp());
        float negative = (value.isInt() ? value.getInt() : value.getFloat()) * -1;
        if(value.isInt())
            return new Value(Math.round(negative));
        return new Value(negative);
    }

    @Override
    public Value visitClosurePexp(LexLangParser.ClosurePexpContext ctx) {
        return this.visit(ctx.exp());
    }

    // primitives


    @Override
    public Value visitBoolSexp(LexLangParser.BoolSexpContext ctx) {
        return new Value(Boolean.valueOf(ctx.getText()));
    }

    @Override
    public Value visitNullSexp(LexLangParser.NullSexpContext ctx) {
        return new Value(null);
    }

    @Override // FIXME: INT not FLOAT
    public Value visitIntSexp(LexLangParser.IntSexpContext ctx) {
        return new Value(Float.valueOf(ctx.getText()));
    }


    @Override
    public Value visitFloatSexp(LexLangParser.FloatSexpContext ctx) {
        return new Value(Float.valueOf(ctx.getText()));
    }

    @Override
    public Value visitCharSexp(LexLangParser.CharSexpContext ctx) {
        String letter = ctx.getText();
        letter = letter.substring(1, letter.length()-1);
        switch (letter){
            case "\\n":
                letter = "\n"; break;
            case "\\t":
                letter = "\t"; break;
            case "\\\\":
                letter = "\\"; break;
            case "\\'":
                letter = "'"; break;
        }
        return new Value(letter.charAt(0));
    }

    // Input/output

    @Override
    public Value visitPrintCmd(LexLangParser.PrintCmdContext ctx) {
        Value value = visit(ctx.exp());
        System.out.print(value);
        return value;
    }

    // TODO: read line and return (store? where)
    @Override
    public Value visitReadCmd(LexLangParser.ReadCmdContext ctx) {
        return new Value(0);
    }
    // debugging
//    @Override
//    public Value visitChildren(RuleNode node) {
//        System.out.println(node.getClass());
//        System.out.println(node.getText());
//        return super.visitChildren(node);
//    }

}
