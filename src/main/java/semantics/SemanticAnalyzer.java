/*
  Maxwell Souza 201435009
  Rodolpho Rossete 201435032
 */


package semantics;

import lexlang.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SemanticAnalyzer extends LexLangBaseVisitor<Value> {

    FunctionScope memory = new FunctionScope();
    HashMap<String, DataDeclaration> dataTypes = new HashMap<>();
    FunctionManager funcManager = new FunctionManager();
    HashMap<LexLangParser.ExpsContext, FunctionDeclaration> functionCalls = new HashMap<>();
    FunctionDeclaration currentFunction = null;
    HashMap<ParserRuleContext, Scope> variablesDeclared = new HashMap<ParserRuleContext, Scope>();

    public HashMap<ParserRuleContext, Scope> getVariablesDeclared() {
        return variablesDeclared;
    }

    public HashMap<String, DataDeclaration> getDataTypes() {
        return dataTypes;
    }

    public FunctionManager getFuncManager() {
        return funcManager;
    }

    public HashMap<LexLangParser.ExpsContext, FunctionDeclaration> getFunctionCalls() {
        return functionCalls;
    }

    /**
     * Run a program
     */
    public void run(ParseTree program) {
        this.visit(program);
    }

    // Memory management

    private boolean mathArguments(FunctionDeclaration function, List<Type> args) {
        if (function.getArguments().size() != args.size()) return false;
        for (int i = 0; i < function.getArguments().size(); i++) {
            Type argType = resolveType(function.getArguments().get(i).type);
            if (!argType.equals(args.get(i)))
                return false;
        }
        return true;
    }

    // Ok
    private FunctionDeclaration resolveFunction(String name, LexLangParser.ExpsContext exps) {
        List<Type> args = new ArrayList<>();
        FunctionDeclaration func = null;

        if (exps != null)
            for (LexLangParser.ExpContext expContext : exps.exp())
                args.add((Type) visit(expContext));

        List<FunctionDeclaration> functions = funcManager.getFunctions(name);

        for (FunctionDeclaration function : functions) {
            if (mathArguments(function, args)) {
                func = function;
                break;
            }
        }

        if (func == null)
            throw new LangException("Cannot find a function '" + name + "' with the following signature: " + args, exps);

        this.functionCalls.put(exps, func);
        return func;
    }

    public Type resolveType(LexLangParser.TypeContext typeCtx) {
        int depth = 0;
        while (typeCtx instanceof LexLangParser.ArrayTypeContext) {
            depth++;
            typeCtx = ((LexLangParser.ArrayTypeContext) typeCtx).type();
        }
        String type = ((LexLangParser.BtypeCallContext) typeCtx).btype().getText();
        switch (type) {
            case "Int":
                return new Type(DefaultTypes.Int, depth);
            case "Char":
                return new Type(DefaultTypes.Char, depth);
            case "Bool":
                return new Type(DefaultTypes.Bool, depth);
            case "Float":
                return new Type(DefaultTypes.Float, depth);
            default:
                if (!dataTypes.containsKey(type))
                    throw new LangException("Data type '" + type + "' not found", typeCtx);
                return new Type(DefaultTypes.DATA, depth, dataTypes.get(type));
        }
    }

    // TODO: clean and reduce
    private Value resolveVariable(LexLangParser.LvalueContext ctx, Type set) {
        if (ctx instanceof LexLangParser.ObjectValueContext) {
            LexLangParser.ObjectValueContext rule = (LexLangParser.ObjectValueContext) ctx;
            String field = rule.ID().getText();
            Type obj = (Type) resolveVariable(rule.lvalue());
            Validator.canAccess(obj, field);
            obj = resolveType(obj.getDataType().getType(field).type);
            if (set != null) Validator.canSet(obj, set);

            return obj;
        }
        if (ctx instanceof LexLangParser.ArrayValueContext) {
            LexLangParser.ArrayValueContext rule = ((LexLangParser.ArrayValueContext) ctx);
            Type i = (Type) visit(rule.exp());
            Validator.isInt(i);
            Type arr = (Type) resolveVariable(rule.lvalue());
            if (!arr.isArray())
                throw new LangException("Cannot access array index: Value of type '" + arr + "' isn't an array.", ctx);
            Type type = new Type(arr.getType(), arr.getDepth() - 1, arr.getDataType());
            if (set != null)
                Validator.canSet(type, set);

            return type;
        }

        LexLangParser.IdentifierValueContext rule = (LexLangParser.IdentifierValueContext) ctx;
        Type type = (Type) memory.getVariable(rule.ID());
        if (set != null) {
            if (type != null) Validator.canSet(type, set);
            memory.setVariable(rule.ID(), set);
        } else if (type == null) {
            throw new LangException("Cannot find variable: " + rule.ID().getText(), ctx);
        }
        return type;
    }

    private Value resolveVariable(LexLangParser.LvalueContext ctx) {
        return resolveVariable(ctx, null);
    }


    private Value visitScopedCommand(ParserRuleContext cmd) {
        Scope scope = memory.pushScope();
        Value eval = visit(cmd);
        memory.popScope();
        variablesDeclared.put(cmd, scope);

        return eval;
    }

    // Visitors


    @Override
    public Value visit(ParseTree tree) {
        try {
            return super.visit(tree);
        } catch (LangException e) {
            if (!(tree instanceof ParserRuleContext)) throw e;

            System.err.println(e.getMessage((ParserRuleContext) tree));
            System.exit(1);
            return null;
        }
    }

    @Override
    public Value visitProg(LexLangParser.ProgContext ctx) {
        this.visitChildren(ctx);

        for (String key : funcManager.getFunctions().keySet())
            for (FunctionDeclaration function : funcManager.getFunctions(key)) {
                this.currentFunction = function;
                memory.pushScope();
                for (FunctionDeclaration.FunctionArgument argument : function.getArguments())
                    if (memory.getVariable(argument.name) != null)
                        throw new LangException("Function '" + function + "' has multiple arguments '" + argument.name + "'", function.getCommands().getParent());
                    else
                        memory.setVariable(argument.name, resolveType(argument.type));

                Scope scope = memory.pushScope(); // local variables
                visit(function.getCommands());
                memory.popScope();
                memory.popScope();
                variablesDeclared.put(function.getCommands(), scope);

                // return validation
                if (function.getReturnTypes().size() > 0) {
                    boolean hasReturn = false;
                    for (LexLangParser.CmdContext context : function.getCommands().cmd()) {
                        if (context instanceof LexLangParser.ReturnCmdContext) {
                            hasReturn = true;
                            break;
                        }
                    }
                    if (!hasReturn)
                        throw new LangException("Function '" + function + "' should have at least 1 return statement at root.", function.getCommands());
                }

                this.currentFunction = null;
            }


        if (this.funcManager.getFunctions("main").size() > 1)
            throw new LangException("Multiple declarations of function 'main' found");

        FunctionDeclaration main = funcManager.getFunction("main");

        if (main.getArguments().size() > 0)
            throw new LangException("Function 'main' shouldn't have any arguments");

        if (main.getReturnTypes().size() > 0)
            throw new LangException("Function 'main' shouldn't have a return");

        return null;
    }

    // Data
    @Override
    public Value visitData(LexLangParser.DataContext ctx) {
        DataDeclaration d = new DataDeclaration(ctx);
        if (dataTypes.containsKey(d.getId()))
            throw new LangException("Data '" + d.getId() + "' was already declared", ctx);
        dataTypes.put(d.getId(), d);
        return null;
    }

    @Override
    public Value visitInstancePexp(LexLangParser.InstancePexpContext ctx) {
        Type value = resolveType(ctx.type());
        if (ctx.exp() != null) {
            value.setDepth(value.getDepth() + 1);
            Validator.isInt((Type) visit(ctx.exp()));
        } else if (value.getDepth() != 0)
            throw new LangException("Array was initialized without size", ctx);
        else if (value.getType() != DefaultTypes.DATA)
            throw new LangException("'new' command need to be used for initializing Arrays or data structures. Initialized '" + value + "'", ctx);
        return value;
    }

    // functions
    @Override
    public Value visitFunc(LexLangParser.FuncContext ctx) {
        funcManager.addFunction(ctx);
        return null;
    }

    @Override
    public Value visitFuncCmd(LexLangParser.FuncCmdContext ctx) {
        String name = ctx.ID().getText();
        FunctionDeclaration f = resolveFunction(name, ctx.exps());
        if (ctx.lvalue().size() > f.getReturnTypes().size())
            throw new LangException("Function '" + f.getId() + "' only returns " + f.getReturnTypes().size() + " values. Trying to get " + ctx.lvalue().size(), ctx);

        for (int i = 0; i < ctx.lvalue().size(); i++) {
            resolveVariable(ctx.lvalue(i), resolveType(f.getReturnTypes().get(i)));
        }
        return null;
    }

    @Override
    public Value visitFuncCallPexp(LexLangParser.FuncCallPexpContext ctx) {
        String name = ctx.ID().getText();
        FunctionDeclaration f = resolveFunction(name, ctx.exps());

        if (f.getReturnTypes().size() == 0)
            throw new LangException("Function '" + name + "' doesn't returns arguments", ctx);
        Validator.isInt((Type) visit(ctx.exp()));

        int returnIndex;
        try {
            returnIndex = Integer.parseInt(ctx.exp().getText());
        } catch (Exception e) {
            throw new LangException("Function return access should be a literal 'Int'. Found expression '" + ctx.exp().getText() + "'", ctx);
        }

        if (returnIndex >= f.getReturnTypes().size())
            throw new LangException("Function '" + f.getId() + "' only returns " + f.getReturnTypes().size() + " values. Trying to access index " + returnIndex, ctx);


        return resolveType(f.getReturnTypes().get(returnIndex));
    }

    @Override
    public Value visitReturnCmd(LexLangParser.ReturnCmdContext ctx) {
        if (currentFunction.getReturnTypes().size() != ctx.exp().size())
            throw new LangException("Function '" + currentFunction.getId() + "' only accepts " + currentFunction.getReturnTypes().size() + " returns, " + ctx.exp().size() + " was found.", ctx);
        for (int i = 0; i < this.currentFunction.getReturnTypes().size(); i++) {
            Type returnType = resolveType(this.currentFunction.getReturnTypes().get(i));
            Type returnedType = (Type) visit(ctx.exp(i));
            if (!returnType.equals(returnedType))
                throw new LangException("Return " + (i + 1) + " should be of type '" + returnType + "'. Returned '" + returnedType + "'", ctx);

        }
        return null;
    }

    // variables

    @Override
    public Value visitAttrCmd(LexLangParser.AttrCmdContext ctx) {
        return resolveVariable(ctx.lvalue(), (Type) visit(ctx.exp()));
    }

    @Override
    public Value visitReadVarPexp(LexLangParser.ReadVarPexpContext ctx) {
        return resolveVariable(ctx.lvalue());
    }

    // while

    @Override
    public Value visitIterateCmd(LexLangParser.IterateCmdContext ctx) {
        Validator.isInt((Type) visit(ctx.exp()));
        visitScopedCommand(ctx.cmd());
        return null;
    }


    // if

    @Override
    public Value visitIfCmd(LexLangParser.IfCmdContext ctx) {
        Validator.isBool((Type) visit(ctx.exp()));
        visitScopedCommand(ctx.cmd());
        return null;
    }

    @Override
    public Value visitElseCmd(LexLangParser.ElseCmdContext ctx) {
        Validator.isBool((Type) visit(ctx.exp()));
        visitScopedCommand(ctx.cmd(0));
        visitScopedCommand(ctx.cmd(1));
        return null;
    }

    @Override
    public Value visitClosureCmd(LexLangParser.ClosureCmdContext ctx) {
        return visitScopedCommand(ctx.cmds());
    }

    // Logic

    @Override
    public Value visitAndExp(LexLangParser.AndExpContext ctx) {
        Type v1 = (Type) this.visit(ctx.exp(0)), v2 = (Type) this.visit(ctx.exp(1));
        Validator.isBool(v1);
        Validator.compareTypes(v1, v2);
        return new Value(v1.getBool() && v2.getBool());
    }

    @Override
    public Value visitLessThanRexp(LexLangParser.LessThanRexpContext ctx) {
        Type v1 = (Type) this.visit(ctx.aexp(0)), v2 = (Type) this.visit(ctx.aexp(1));
        Validator.isNumber(v1);
        Validator.compareTypes(v1, v2);
        return new Type(DefaultTypes.Bool);
    }

    @Override
    public Value visitCompareRexp(LexLangParser.CompareRexpContext ctx) {
        Type v1 = (Type) this.visit(ctx.rexp()), v2 = (Type) this.visit(ctx.aexp());
        Validator.compareTypes(v1, v2);
        return new Type(DefaultTypes.Bool);
    }

    // Math

    @Override
    public Value visitAddAexp(LexLangParser.AddAexpContext ctx) {
        Type v1 = (Type) this.visit(ctx.aexp()), v2 = (Type) this.visit(ctx.mexp());
        Validator.isNumber(v1);
        Validator.compareTypes(v1, v2);
        return v1;
    }

    @Override
    public Value visitMultiplyMexp(LexLangParser.MultiplyMexpContext ctx) {
        Type v1 = (Type) this.visit(ctx.mexp()), v2 = (Type) this.visit(ctx.sexp());
        Validator.isNumber(v1);
        Validator.compareTypes(v1, v2);
        return v1;
    }

    @Override
    public Value visitNotSexp(LexLangParser.NotSexpContext ctx) {
        Type type = (Type) this.visit(ctx.sexp());
        Validator.isBool(type);
        return type;
    }

    @Override
    public Value visitNegativeSexp(LexLangParser.NegativeSexpContext ctx) {
        Type type = (Type) this.visit(ctx.sexp());
        Validator.isNumber(type);
        return type;
    }

    @Override
    public Value visitClosurePexp(LexLangParser.ClosurePexpContext ctx) {
        return this.visit(ctx.exp());
    }

    // primitives

    @Override
    public Value visitBoolSexp(LexLangParser.BoolSexpContext ctx) {
        return new Type(DefaultTypes.Bool);
    }

    @Override
    public Value visitNullSexp(LexLangParser.NullSexpContext ctx) {
        return new Type(DefaultTypes.NULL);
    }

    @Override
    public Value visitIntSexp(LexLangParser.IntSexpContext ctx) {
        return new Type(DefaultTypes.Int);
    }


    @Override
    public Value visitFloatSexp(LexLangParser.FloatSexpContext ctx) {
        return new Type(DefaultTypes.Float);
    }

    @Override
    public Value visitCharSexp(LexLangParser.CharSexpContext ctx) {
        return new Type(DefaultTypes.Char);
    }

    // Input/output

    @Override
    public Value visitPrintCmd(LexLangParser.PrintCmdContext ctx) {
        return visit(ctx.exp());
    }

    @Override
    public Value visitReadCmd(LexLangParser.ReadCmdContext ctx) {
        return resolveVariable(ctx.lvalue(), new Type(DefaultTypes.Int));
    }

    // debugging
//    @Override
//    public Value visitChildren(RuleNode node) {
//        System.out.println(node.getClass());
//        System.out.println(node.getText());
//        return super.visitChildren(node);
//    }

}
