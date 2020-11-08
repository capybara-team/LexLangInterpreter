package lexlang;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;

public class LexLangInterpreter extends LexLangBaseVisitor<Value> {

    FunctionScope memory = new FunctionScope();

    /**
     * Run a program
     */
    public Value run(ParseTree prog) {
        return this.visit(prog);
    }

    // Memory management

    private void startFunction() {
        this.memory = new FunctionScope(memory);
    }

    private void endFunction() {
        memory = memory.getParent();
    }

    private String getVariableName(LexLangParser.IdentifierValueContext id) {
        return id.getText();
    }

    private String getVariableName(LexLangParser.ArrayValueContext id) {
        return this.getVariableName(id.lvalue()) + '[' + this.visit(id.exp()).getInt() + ']';
    }

    private String getVariableName(LexLangParser.ObjectValueContext id) {
        return getVariableName(id.lvalue()) + '.' + id.ID().getText();
    }

    private String getVariableName(LexLangParser.LvalueContext id) {

        if (id instanceof LexLangParser.IdentifierValueContext) {
            return getVariableName((LexLangParser.IdentifierValueContext) id);
        }

        if (id instanceof LexLangParser.ArrayValueContext) {
            return getVariableName((LexLangParser.ArrayValueContext) id);
        }

        if (id instanceof LexLangParser.ObjectValueContext) {
            return getVariableName((LexLangParser.ObjectValueContext) id);
        }

        throw new RuntimeException("Unregognized identifier: " + id.getText());
    }


    @Override
    public Value visitProg(LexLangParser.ProgContext ctx) {
        return super.visitProg(ctx);
    }

    @Override
    public Value visitFunc(LexLangParser.FuncContext ctx) {
        return super.visitFunc(ctx);
    }

    // variables

    @Override
    public Value visitAttrCmd(LexLangParser.AttrCmdContext ctx) {
        String name = this.getVariableName(ctx.lvalue());
        Value val = visit(ctx.exp());
        this.memory.setVariable(name, val);
        return val;
    }

    @Override
    public Value visitReadVarPexp(LexLangParser.ReadVarPexpContext ctx) {
        String name = getVariableName(ctx.lvalue());
        return memory.getVariable(name);
    }

    @Override
    public Value visitIdentifierValue(LexLangParser.IdentifierValueContext ctx) {
        return super.visitIdentifierValue(ctx);
    }

    // while

    @Override
    public Value visitIterateCmd(LexLangParser.IterateCmdContext ctx) {

        while (visit(ctx.exp()).getBool()) {
            memory.pushScope();
            visit(ctx.cmd());
            memory.popScope();
        }

        return Value.VOID;
    }


    // if

    @Override
    public Value visitIfCmd(LexLangParser.IfCmdContext ctx) {
        Value result = visit(ctx.exp());
        if (result.getBool()) {
            memory.pushScope();
            visit(ctx.cmd());
            memory.popScope();
        }
        return Value.VOID;
    }

    @Override
    public Value visitElseCmd(LexLangParser.ElseCmdContext ctx) {
        Value result = visit(ctx.exp());
        if (result.getBool()) {
            memory.pushScope();
            visit(ctx.cmd(0));
            memory.popScope();
        } else {
            memory.pushScope();
            visit(ctx.cmd(1));
            memory.popScope();
        }
        return Value.VOID;
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
        if (ctx.op.getType() == LexLangParser.EQUALS)
            return new Value(v1.equals(v2));
        return new Value(!v1.equals(v2));
    }

    // Math

    @Override
    public Value visitAddAexp(LexLangParser.AddAexpContext ctx) {
        Value v1 = this.visit(ctx.aexp()),
                v2 = this.visit(ctx.mexp());
        if (ctx.op.getType() == LexLangParser.PLUS)
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
        if (value.isInt())
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
        letter = letter.substring(1, letter.length() - 1);
        switch (letter) {
            case "\\n":
                letter = "\n";
                break;
            case "\\t":
                letter = "\t";
                break;
            case "\\\\":
                letter = "\\";
                break;
            case "\\'":
                letter = "'";
                break;
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
    // debugging
//    @Override
//    public Value visitChildren(RuleNode node) {
//        System.out.println(node.getClass());
//        System.out.println(node.getText());
//        return super.visitChildren(node);
//    }

}
