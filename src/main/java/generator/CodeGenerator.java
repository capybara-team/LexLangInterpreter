/*
  Maxwell Souza 201435009
  Rodolpho Rossete 201435032
 */


package generator;

import lexlang.FunctionScope;
import lexlang.LexLangBaseVisitor;
import lexlang.LexLangParser;
import lexlang.Scope;
import org.antlr.v4.runtime.ParserRuleContext;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import semantics.*;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class CodeGenerator extends LexLangBaseVisitor<Object> {
    private final HashMap<LexLangParser.ExpsContext, FunctionDeclaration> functionCalls;
    private final HashMap<ParserRuleContext, Scope> variablesDeclared;
    FunctionScope memory = new FunctionScope();

    STGroup t;

    Scanner reader = new Scanner(new InputStreamReader(System.in));

    HashMap<String, DataDeclaration> dataTypes;
    FunctionManager functionManager;

    Boolean returnCalled = false;
    private int itCounter = -1;
    private int fCounter = -1;

    public CodeGenerator(SemanticAnalyzer analyzer, STGroup templates) {
        this.t = templates;

        this.functionManager = analyzer.getFuncManager();
        this.dataTypes = analyzer.getDataTypes();
        this.functionCalls = analyzer.getFunctionCalls();
        this.variablesDeclared = analyzer.getVariablesDeclared();
    }


    public String run() {
        ST template = t.getInstanceOf("program");

        template.add("data", parseData());
        template.add("funcs", parseFunctions());

        return template.render();
    }

    FunctionDeclaration getFunction(String id, LexLangParser.ExpsContext exps) {
        return exps == null ? functionManager.getFunction(id) : functionCalls.get(exps);
    }

    List<ST> parseFunctions() {
        List<ST> functionsTemplate = new ArrayList<>();
        for (String fName : functionManager.getFunctions().keySet())
            for (FunctionDeclaration f : functionManager.getFunctions(fName)) {

                // handle args
                List<ST> argsTemplate = new ArrayList<>(f.getArguments().size());
                for (FunctionDeclaration.FunctionArgument argument : f.getArguments()) {
                    ST arg = t.getInstanceOf("param");
                    arg.add("name", argument.name);
                    arg.add("type", visit(argument.type));
                    argsTemplate.add(arg);
                }

                ST funcTemplate = t.getInstanceOf("func");
                funcTemplate.add("name", f.getId());
                funcTemplate.add("type", f.getReturnTypes().size() == 0 ? "void" : "Object[]");
                funcTemplate.add("cmd", getContextDeclaration(f.getCommands()));
                funcTemplate.add("params", argsTemplate);
                functionsTemplate.add(funcTemplate);
            }
        return functionsTemplate;
    }

    private ST getContextDeclaration(ParserRuleContext ctx) {
        ST commands = t.getInstanceOf("contextDecl").add("cmd", visit(ctx));
        if (!variablesDeclared.containsKey(ctx))
            return commands;

        List<ST> declarations = new ArrayList<>();
        for (String var : variablesDeclared.get(ctx).getMemory().keySet()) {
            ST decl = t.getInstanceOf("param");
            decl.add("name", var);
            decl.add("type", parseType((Type) variablesDeclared.get(ctx).getMemory().get(var)));
            declarations.add(decl);
        }
        return commands.add("decl", declarations);
    }


    private List<ST> parseData() {
        List<ST> declarationTemplates = new ArrayList<>(dataTypes.size());
        for (DataDeclaration dataDeclaration : dataTypes.values()) {
            ST dataTemplate = t.getInstanceOf("data");
            dataTemplate.add("name", dataDeclaration.getId());
            List<ST> dataDeclarations = new ArrayList<>(dataDeclaration.getTypes().size());
            for (DataDeclaration.DataType type : dataDeclaration.getTypes()) {
                ST typeString = t.getInstanceOf("decl");
                typeString.add("type", ((Template) visit(type.type)).getText());
                typeString.add("id", type.name);
                dataDeclarations.add(typeString);
            }
            dataTemplate.add("decl", dataDeclarations);
            declarationTemplates.add(dataTemplate);
        }
        return declarationTemplates;
    }

    private String parseType(Type typeObj) {
        if (typeObj.getType() == DefaultTypes.NULL)
            return "null";

        String type;
        if (typeObj.getType() == DefaultTypes.Int)
            type = "int";
        else if (typeObj.getType() == DefaultTypes.Bool)
            type = "boolean";
        else if (typeObj.getType() == DefaultTypes.Char)
            type = "char";
        else if (typeObj.getType() == DefaultTypes.Float)
            type = "float";
        else
            type = typeObj.getDataType().getId();

        for (int i = 0; i < typeObj.getDepth(); i++) {
            type += "[]";
        }

        return type;
    }

    @Override
    public Template visitArrayType(LexLangParser.ArrayTypeContext ctx) {
        return new Template(visit(ctx.type()) + "[]");
    }

    @Override
    public Template visitBtype(LexLangParser.BtypeContext ctx) {
        switch (ctx.typeName.getType()) {
            case LexLangParser.INT:
                return new Template("int");
            case LexLangParser.CHAR:
                return new Template("char");
            case LexLangParser.BOOL:
                return new Template("boolean");
            case LexLangParser.FLOAT:
                return new Template("float");
            default:
                return new Template(ctx.getText());
        }
    }

    private List<Object> visitList(List<? extends ParserRuleContext> exp) {
        List<Object> visited = new ArrayList<>();
        for (ParserRuleContext context : exp)
            visited.add(visit(context));
        return visited;
    }


    public ST visit(List<LexLangParser.CmdContext> ctx) {
        List<ST> cmds = new ArrayList<>();
        for (LexLangParser.CmdContext context : ctx) {
            cmds.add((ST) visit(context));
        }
        return t.getInstanceOf("multipleCmds").add("cmds", cmds);
    }

    @Override
    public ST visitFuncCmds(LexLangParser.FuncCmdsContext ctx) {
        return visit(ctx.cmd());
    }

    @Override
    public ST visitMultipleCommands(LexLangParser.MultipleCommandsContext ctx) {
        return visit(ctx.cmd());
    }

    @Override
    public ST visitClosureCmd(LexLangParser.ClosureCmdContext ctx) {
        return t.getInstanceOf("closure")
                .add("cmds", getContextDeclaration(ctx.cmds()));
    }

    @Override
    public ST visitIfCmd(LexLangParser.IfCmdContext ctx) {
        return t.getInstanceOf("if")
                .add("expr", visit(ctx.exp()))
                .add("cmd", getContextDeclaration(ctx.cmd()));
    }

    @Override
    public ST visitElseCmd(LexLangParser.ElseCmdContext ctx) {
        return t.getInstanceOf("if")
                .add("expr", visit(ctx.exp()))
                .add("cmd", getContextDeclaration(ctx.cmd(0)))
                .add("els", getContextDeclaration(ctx.cmd(1)));
    }

    @Override
    public ST visitIterateCmd(LexLangParser.IterateCmdContext ctx) {
        itCounter++;
        return t.getInstanceOf("iterate")
                .add("counter", itCounter)
                .add("expr", visit(ctx.exp()))
                .add("cmd", getContextDeclaration(ctx.cmd()));
    }

    @Override
    public ST visitPrintCmd(LexLangParser.PrintCmdContext ctx) {
        return t.getInstanceOf("print").add("exp", visit(ctx.exp()));
    }

    @Override
    public ST visitReadCmd(LexLangParser.ReadCmdContext ctx) {
        return t.getInstanceOf("read").add("lvalue", visit(ctx.lvalue()));
    }

    @Override
    public ST visitAttrCmd(LexLangParser.AttrCmdContext ctx) {
        return t.getInstanceOf("attr")
                .add("lvalue", visit(ctx.lvalue()))
                .add("exp", visit(ctx.exp()));
    }

    @Override
    public ST visitFuncCmd(LexLangParser.FuncCmdContext ctx) {
        FunctionDeclaration func = getFunction(ctx.ID().getText(), ctx.exps());
        ST template = t.getInstanceOf("funcCmd");
        fCounter++;
        if (ctx.lvalue().size() > 0) {
            List<ST> attributions = new ArrayList<>();
            List<Object> variables = visitList(ctx.lvalue());
            for (int i = 0; i < variables.size(); i++)
                attributions.add(
                        t.getInstanceOf("funcCmdAttr")
                                .add("lvalue", variables.get(i))
                                .add("type", visit(func.getReturnTypes().get(i)))
                                .add("counter", fCounter)
                                .add("index", i)

                );
            template.add("attr", attributions);
        }

        if (ctx.exps() != null)
            template.add("exps", visitList(ctx.exps().exp()));

        return template
                .add("id", ctx.ID().getText())
                .add("counter", fCounter);
    }

    @Override
    public ST visitReturnCmd(LexLangParser.ReturnCmdContext ctx) {
        return t.getInstanceOf("returnCmd")
                .add("values", visitList(ctx.exp()));
    }

    // lvalue handler
    @Override
    public Object visitIdentifierValue(LexLangParser.IdentifierValueContext ctx) {
        return ctx.getText();
    }

    @Override
    public Object visitArrayValue(LexLangParser.ArrayValueContext ctx) {
        return ctx.getText();
    }

    @Override
    public Object visitObjectValue(LexLangParser.ObjectValueContext ctx) {
        return ctx.getText();
    }

    // exps

    @Override
    public Object visitFuncCallPexp(LexLangParser.FuncCallPexpContext ctx) {
        FunctionDeclaration f = getFunction(ctx.ID().getText(), ctx.exps());
        ST template = t.getInstanceOf("funcCallPexp");
        int access = Integer.parseInt(ctx.exp().getText());
        if (ctx.exps() != null)
            template.add("exps", visitList(ctx.exps().exp()));
        return template
                .add("id", ctx.ID().getText())
                .add("type", visit(f.getReturnTypes().get(access)))
                .add("exp", access);
    }

    @Override
    public Object visitInstancePexp(LexLangParser.InstancePexpContext ctx) {
        ST template = t.getInstanceOf("instancePexp");
        String type = String.valueOf(visit(ctx.type()));
        if (ctx.exp() != null) {
            Object exp = visit(ctx.exp());
            template.add("exp", exp);
            int arrayStart = type.indexOf('[');
            if (arrayStart > -1) {
                template.add("depth", type.substring(arrayStart));
                type = type.substring(0, arrayStart);
            }
        } else type +="()";
        return template.add("type", type);
    }

    @Override
    public Object visitAndExp(LexLangParser.AndExpContext ctx) {
        return t.getInstanceOf("andExp")
                .add("lExp", visit(ctx.exp(0)))
                .add("rExp", visit(ctx.exp(1)));
    }

    @Override
    public Object visitLessThanRexp(LexLangParser.LessThanRexpContext ctx) {
        return t.getInstanceOf("lessThanRexp")
                .add("lExp", visit(ctx.aexp(0)))
                .add("rExp", visit(ctx.aexp(1)));
    }

    @Override
    public Object visitCompareRexp(LexLangParser.CompareRexpContext ctx) {
        return t.getInstanceOf(ctx.op.getType() == LexLangParser.EQUALS ? "eqRexp" : "neqRexp")
                .add("lExp", visit(ctx.rexp()))
                .add("rExp", visit(ctx.aexp()));
    }

    @Override
    public Object visitAddAexp(LexLangParser.AddAexpContext ctx) {
        return t.getInstanceOf(ctx.op.getType() == LexLangParser.PLUS ? "plusAexp" : "subAexp")
                .add("lExp", visit(ctx.aexp()))
                .add("rExp", visit(ctx.mexp()));
    }

    @Override
    public Object visitMultiplyMexp(LexLangParser.MultiplyMexpContext ctx) {
        String tName;
        if (ctx.op.getType() == LexLangParser.MULTIPLY)
            tName = "multMexp";
        else if (ctx.op.getType() == LexLangParser.DIVIDE)
            tName = "divMexp";
        else
            tName = "modMexp";
        return t.getInstanceOf(tName)
                .add("lExp", visit(ctx.mexp()))
                .add("rExp", visit(ctx.sexp()));
    }

    @Override
    public Object visitNotSexp(LexLangParser.NotSexpContext ctx) {
        return t.getInstanceOf("notSexp").add("exp", visit(ctx.sexp()));
    }

    @Override
    public Object visitNegativeSexp(LexLangParser.NegativeSexpContext ctx) {
        return t.getInstanceOf("negativeSexp").add("exp", visit(ctx.sexp()));
    }


    @Override
    public Object visitClosurePexp(LexLangParser.ClosurePexpContext ctx) {
        return t.getInstanceOf("closurePexp").add("exp", visit(ctx.exp()));
    }

    // literals
    @Override
    public Object visitBoolSexp(LexLangParser.BoolSexpContext ctx) {
        return ctx.getText();
    }

    @Override
    public Object visitNullSexp(LexLangParser.NullSexpContext ctx) {
        return ctx.getText();
    }

    @Override
    public Object visitIntSexp(LexLangParser.IntSexpContext ctx) {
        return ctx.getText();
    }

    @Override
    public Object visitFloatSexp(LexLangParser.FloatSexpContext ctx) {
        return t.getInstanceOf("floatSexp").add("exp", ctx.getText());
    }

    @Override
    public Object visitCharSexp(LexLangParser.CharSexpContext ctx) {
        return t.getInstanceOf("charSexp").add("exp", ctx.getText().substring(1, ctx.getText().length() - 1));
    }

}
