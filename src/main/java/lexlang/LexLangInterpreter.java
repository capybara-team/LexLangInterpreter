package lexlang;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;

import java.io.InputStreamReader;
import java.util.*;

public class LexLangInterpreter extends LexLangBaseVisitor<Value> {

    FunctionScope memory = new FunctionScope();

    Scanner reader = new Scanner(new InputStreamReader(System.in));

    HashMap<String, DataDeclaration> dataTypes = new HashMap<>();
    HashMap<String, FunctionDeclaration> functions = new HashMap<>();

    Boolean returnCalled = false;
    List<Value> returnValues = null; // TODO: Improve function n-uple usage.

    /**
     * Run a program
     */
    public Value run(ParseTree prog) {
        return this.visit(prog);
    }

    // Memory management

    private Value runFunction(String name, LexLangParser.ExpsContext exps) {
        if (!functions.containsKey(name))
            throw new RuntimeException("Function '" + name + "' not found");


        List<Value> args = new ArrayList<>();
        FunctionDeclaration func = functions.get(name);

        if (exps != null)
            for (LexLangParser.ExpContext expContext : exps.exp())
                args.add(visit(expContext));


        this.memory = new FunctionScope(memory);
        for (int i = 0; i < func.getArguments().size(); i++)
            memory.setVariable(func.getArguments().get(i).name, args.get(i));
        Value v = visit(func.getCommands());
        this.returnCalled = false;
        this.memory = memory.getParent();
        return v;
    }

    private Value runFunction(String name) {
        return runFunction(name, null);
    }

//    private String getVariableName(LexLangParser.IdentifierValueContext id) {
//        return id.getText();
//    }
//
//    private String getVariableName(LexLangParser.ArrayValueContext id) {
//        return this.getVariableName(id.lvalue()) + '[' + this.visit(id.exp()).getInt() + ']';
//    }
//
//    private String getVariableName(LexLangParser.ObjectValueContext id) {
//        return getVariableName(id.lvalue()) + '.' + id.ID().getText();
//    }
//
//    private String getVariableName(LexLangParser.LvalueContext id) {
//
//        if (id instanceof LexLangParser.IdentifierValueContext) {
//            return getVariableName((LexLangParser.IdentifierValueContext) id);
//        }
//
//        if (id instanceof LexLangParser.ArrayValueContext) {
//            return getVariableName((LexLangParser.ArrayValueContext) id);
//        }
//
//        if (id instanceof LexLangParser.ObjectValueContext) {
//            return getVariableName((LexLangParser.ObjectValueContext) id);
//        }
//
//        throw new RuntimeException("Unregognized identifier: " + id.getText());
//    }

    private Value resolveVariable(LexLangParser.LvalueContext ctx, Value set) {
        if (ctx instanceof LexLangParser.ObjectValueContext) {
            LexLangParser.ObjectValueContext rule = (LexLangParser.ObjectValueContext) ctx;
            String field = rule.ID().getText();
            Value obj = resolveVariable(rule.lvalue());
            if (set != null) return obj.getData().put(field, set);
            return obj.getData().get(field);
        }
        if (ctx instanceof LexLangParser.ArrayValueContext) {
            LexLangParser.ArrayValueContext rule = ((LexLangParser.ArrayValueContext) ctx);
            int i = visit(rule.exp()).getInt();
            Value arr = resolveVariable(rule.lvalue());
            if (set != null) return arr.getArray().set(i, set);
            return arr.getArray().get(i);
        }
        LexLangParser.IdentifierValueContext rule = (LexLangParser.IdentifierValueContext) ctx;
        if (set != null) return memory.setVariable(rule.ID(), set);
        return memory.getVariable(rule.ID());
    }

    private Value resolveVariable(LexLangParser.LvalueContext ctx) {
        return resolveVariable(ctx, null);
    }

    private Value resolveNumber(float result, Value v1, Value v2) {
        if (v1.getRawValue() instanceof Integer && v2.getRawValue() instanceof Integer)
            return new Value(((Float) result).intValue());
        return new Value(result);

    }

    private Value resolveNumber(float result, Value v1) {
        return resolveNumber(result, v1, new Value((Integer) 0));

    }

    @Override
    protected boolean shouldVisitNextChild(RuleNode node, Value currentResult) {
        return !this.returnCalled;
    }

    // Visitors

    @Override
    public Value visitProg(LexLangParser.ProgContext ctx) {
        this.visitChildren(ctx);
        return this.runFunction("main");
    }

    // Data
    @Override
    public Value visitData(LexLangParser.DataContext ctx) {
        String name = ctx.ID().getText();
        DataDeclaration d = new DataDeclaration(ctx);
        dataTypes.put(d.getId(), d);
        return Value.VOID;
    }

    @Override
    public Value visitInstancePexp(LexLangParser.InstancePexpContext ctx) {
        LexLangParser.TypeContext typeCtx = ctx.type();
        int depth = 0;
        // TODO: understand array to instance multiple (maybe only semantic)
        while (typeCtx instanceof LexLangParser.ArrayTypeContext) {
            depth++;
            typeCtx = ((LexLangParser.ArrayTypeContext) typeCtx).type();
        }
        String type = ((LexLangParser.BtypeCallContext) typeCtx).btype().getText();
        if (ctx.exp() != null) {
            depth++;
            int size = visit(ctx.exp()).getInt();
            return new Value(new ArrayValue(size, type));
        }
        if (List.of("Int", "Char", "Bool", "Float").contains(type))
            return new Value(null);
        return new Value(new Data(dataTypes.get(type)));
    }

    // functions
    @Override
    public Value visitFunc(LexLangParser.FuncContext ctx) {
        FunctionDeclaration f = new FunctionDeclaration(ctx);
        functions.put(f.getId(), f);
        return Value.VOID;
    }

    @Override
    public Value visitFuncCmd(LexLangParser.FuncCmdContext ctx) {
        String name = ctx.ID().getText();
        Value ret = runFunction(name, ctx.exps());
        for (int i = 0; i < ctx.lvalue().size(); i++) {
            resolveVariable(ctx.lvalue(i), returnValues.get(i));
        }
        returnValues = null;
        return ret;
    }

    @Override
    public Value visitFuncCallPexp(LexLangParser.FuncCallPexpContext ctx) {
        String name = ctx.ID().getText();
        Value result = runFunction(name, ctx.exps());
        if (ctx.exp() != null)
            result = returnValues.get(visit(ctx.exp()).getInt());
        returnValues = null;
        return result;
    }

    @Override
    public Value visitReturnCmd(LexLangParser.ReturnCmdContext ctx) {
        this.returnValues = new ArrayList<>();
        for (LexLangParser.ExpContext expContext : ctx.exp()) {
            this.returnValues.add(visit(expContext));
        }
        this.returnCalled = true;
        return this.returnValues.get(0);
    }

    // variables

    @Override
    public Value visitAttrCmd(LexLangParser.AttrCmdContext ctx) {
        return resolveVariable(ctx.lvalue(), visit(ctx.exp()));
    }

    @Override
    public Value visitReadVarPexp(LexLangParser.ReadVarPexpContext ctx) {
        return resolveVariable(ctx.lvalue());
    }

    @Override
    public Value visitIdentifierValue(LexLangParser.IdentifierValueContext ctx) {
        return super.visitIdentifierValue(ctx);
    }

    // while

    @Override
    public Value visitIterateCmd(LexLangParser.IterateCmdContext ctx) {

        Value partialResult = Value.VOID;
        while (visit(ctx.exp()).getBool()) {
            memory.pushScope();
            partialResult = visit(ctx.cmd());
            memory.popScope();
        }

        return partialResult;
    }


    // if

    @Override
    public Value visitIfCmd(LexLangParser.IfCmdContext ctx) {
        Value result = visit(ctx.exp());
        if (result.getBool()) {
            memory.pushScope();
            result = visit(ctx.cmd());
            memory.popScope();
        }
        return result;
    }

    @Override
    public Value visitElseCmd(LexLangParser.ElseCmdContext ctx) {
        Value result = visit(ctx.exp());
        if (result.getBool()) {
            memory.pushScope();
            result = visit(ctx.cmd(0));
            memory.popScope();
        } else {
            memory.pushScope();
            result = visit(ctx.cmd(1));
            memory.popScope();
        }
        return result;
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
            return resolveNumber(v1.getFloat() + v2.getFloat(), v1, v2);
        return resolveNumber(v1.getFloat() - v2.getFloat(), v1, v2);
    }

    @Override
    public Value visitMultiplyMexp(LexLangParser.MultiplyMexpContext ctx) {
        Value v1 = this.visit(ctx.mexp()),
                v2 = this.visit(ctx.sexp());
        switch (ctx.op.getType()) {
            case LexLangParser.MULTIPLY:
                return resolveNumber(v1.getFloat() * v2.getFloat(), v1, v2);
            case LexLangParser.DIVIDE:
                return resolveNumber(v1.getFloat() / v2.getFloat(), v1, v2);
            case LexLangParser.MOD:
                return resolveNumber(v1.getFloat() % v2.getFloat(), v1, v2);
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

    @Override
    public Value visitNegativeSexp(LexLangParser.NegativeSexpContext ctx) {
        Value value = this.visit(ctx.sexp());
        float negative = value.getFloat() * -1;
        return resolveNumber(negative, value);
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

    @Override
    public Value visitIntSexp(LexLangParser.IntSexpContext ctx) {
        return new Value(Integer.valueOf(ctx.getText()));
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

    @Override
    public Value visitReadCmd(LexLangParser.ReadCmdContext ctx) {
        String response = reader.nextLine();
        return resolveVariable(ctx.lvalue(), new Value(Float.valueOf(response)));
    }

    // debugging
//    @Override
//    public Value visitChildren(RuleNode node) {
//        System.out.println(node.getClass());
//        System.out.println(node.getText());
//        return super.visitChildren(node);
//    }

}
