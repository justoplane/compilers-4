package submit;

import org.antlr.v4.codegen.model.decl.Decl;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import parser.CminusBaseVisitor;
import parser.CminusParser;
import submit.ast.*;

import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ASTVisitor extends CminusBaseVisitor<Node> {
    private final Logger LOGGER;
    private SymbolTable symbolTable;

    public ASTVisitor(Logger LOGGER) {
        this.LOGGER = LOGGER;
    }

    private VarType getVarType(CminusParser.TypeSpecifierContext ctx) {
        final String t = ctx.getText();
        return (t.equals("int")) ? VarType.INT : (t.equals("bool")) ? VarType.BOOL : VarType.CHAR;
    }

    @Override public Node visitProgram(CminusParser.ProgramContext ctx) {
        symbolTable = new SymbolTable();
        List<Declaration> decls = new ArrayList<>();
        for (CminusParser.DeclarationContext d : ctx.declaration()) {
            decls.add((Declaration) visitDeclaration(d));
        }
        return new Program(decls);
    }

    @Override public Node visitVarDeclaration(CminusParser.VarDeclarationContext ctx) {
        VarType type = getVarType(ctx.typeSpecifier());
        List<String> ids = new ArrayList<>();
        List<Integer> arraySizes = new ArrayList<>();

        for (CminusParser.VarDeclIdContext v : ctx.varDeclId()) {
            String id = v.ID().getText();
            ids.add(id);

            symbolTable.addSymbol(id, new SymbolInfo(id, type, false));

            if (v.NUMCONST() != null) {
                arraySizes.add(Integer.parseInt(v.NUMCONST().getText()));
            } else {
                arraySizes.add(-1);
            }
        }
        final boolean isStatic = false;
        return new VarDeclaration(type, ids, arraySizes, isStatic);
    }

    @Override public Node visitReturnStmt(CminusParser.ReturnStmtContext ctx) {
        if (ctx.expression() != null) {
            return new Return((Expression) visitExpression(ctx.expression()));
        }
        return new Return(null);
    }

    @Override public Node visitConstant(CminusParser.ConstantContext ctx) {
        final Node node;
        if (ctx.NUMCONST() != null) {
            node = new NumConstant(Integer.parseInt(ctx.NUMCONST().getText()));
        } else if (ctx.CHARCONST() != null) {
            node = new CharConstant(ctx.CHARCONST().getText().charAt(0));
        } else if (ctx.STRINGCONST() != null) {
            node = new StringConstant(ctx.STRINGCONST().getText());
        } else {
            node = new BoolConstant(ctx.getText().equals("true"));
        }
        return node;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitDeclaration(CminusParser.DeclarationContext ctx) {
        if (ctx.varDeclaration() != null){
            return visit(ctx.varDeclaration());
        } else if (ctx.funDeclaration() != null) {
            return visit(ctx.funDeclaration());
        }
        return null;
    }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitVarDeclId(CminusParser.VarDeclIdContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitFunDeclaration(CminusParser.FunDeclarationContext ctx) {
        VarType returnType = ctx.typeSpecifier() != null ? getVarType(ctx.typeSpecifier()) : null;
        String id = ctx.ID().getText();

        symbolTable.addSymbol(id, new SymbolInfo(id, returnType, true));

        symbolTable = symbolTable.createChild();

        List<Declaration> params = new ArrayList<>();
        for (CminusParser.ParamContext p : ctx.param()){
            params.add((Declaration) visit(p));
        }

        Statement body = (Statement) visit(ctx.statement());

        symbolTable = symbolTable.getParent();

        return new FunDeclaration(returnType, id, params, body);
    }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitTypeSpecifier(CminusParser.TypeSpecifierContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitParam(CminusParser.ParamContext ctx) {
        VarType type = getVarType(ctx.typeSpecifier());

        String id = ctx.paramId().ID().getText();

        boolean isArray = ctx.paramId().getText().contains("[]");

        symbolTable.addSymbol(id, new SymbolInfo(id, type, false));

        List<String> ids = new ArrayList<>();
        ids.add(id);
        List<Integer> arraySizes = new ArrayList<>();
        arraySizes.add(isArray ? 0 : -1);

        return new VarDeclaration(type, ids, arraySizes, false);

    }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitParamId(CminusParser.ParamIdContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitStatement(CminusParser.StatementContext ctx) {
        return visit(ctx.getChild(0));
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitCompoundStmt(CminusParser.CompoundStmtContext ctx) {

        symbolTable = symbolTable.createChild();

        List<VarDeclaration> varDecls = new ArrayList<>();
        for (CminusParser.VarDeclarationContext v : ctx.varDeclaration()){
            varDecls.add((VarDeclaration) visit(v));
        }

        List<Statement> statements = new ArrayList<>();
        for (CminusParser.StatementContext s : ctx.statement()){
            statements.add((Statement) visit(s));
        }

        symbolTable = symbolTable.getParent();

        return new CompoundStmt(varDecls, statements);

    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitExpressionStmt(CminusParser.ExpressionStmtContext ctx) {
        if (ctx.expression() != null){
            return new ExpressionStmt((Expression) visit(ctx.expression()));
        }
        return new ExpressionStmt(null);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitIfStmt(CminusParser.IfStmtContext ctx) {
        Expression condition = (Expression) visit(ctx.simpleExpression());
        Statement thenStmt = (Statement) visit(ctx.statement(0));
        Statement elseStmt = null;

        if (ctx.statement().size() > 1) {
            elseStmt = (Statement) visit(ctx.statement(1));
        }

        return new IfStmt(condition, thenStmt, elseStmt);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitWhileStmt(CminusParser.WhileStmtContext ctx) {
        Expression condition = (Expression) visit(ctx.simpleExpression());
        Statement body = (Statement) visit(ctx.statement());

        return new WhileStmt(condition, body);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitBreakStmt(CminusParser.BreakStmtContext ctx) {
        return new Break();
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitExpression(CminusParser.ExpressionContext ctx) {
        if (ctx.simpleExpression() != null){
            return visit(ctx.simpleExpression());
        }

        Mutable lhs = (Mutable) visit(ctx.mutable());

        if (ctx.getChildCount() == 2){
            String op = ctx.getChild(1).getText();
            return new UnaryPostOperator(lhs, op);
        }

        String op = ctx.getChild(1).getText();
        Expression rhs = (Expression) visit(ctx.expression());
        return new Assign(lhs, op, rhs);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitSimpleExpression(CminusParser.SimpleExpressionContext ctx) {
        return visit(ctx.orExpression());
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitOrExpression(CminusParser.OrExpressionContext ctx) {
        Expression lhs = (Expression) visit(ctx.andExpression(0));
        for (int i = 1; i < ctx.andExpression().size(); i++){
            Expression rhs = (Expression) visit(ctx.andExpression(i));
            lhs = new BinaryOperator(lhs, "||", rhs);
        }
        return lhs;
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitAndExpression(CminusParser.AndExpressionContext ctx) {
        Expression lhs = (Expression) visit(ctx.unaryRelExpression(0));
        for (int i = 1; i < ctx.unaryRelExpression().size(); i++){
            Expression rhs = (Expression) visit(ctx.unaryRelExpression(i));
            lhs = new BinaryOperator(lhs, "&&", rhs);
        }
        return lhs;
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitUnaryRelExpression(CminusParser.UnaryRelExpressionContext ctx) {
        Expression node = (Expression) visit(ctx.relExpression());

        for (int i = 0; i < ctx.BANG().size(); i++){
            node = new UnaryOperator("!", node);
        }
        return node;
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitRelExpression(CminusParser.RelExpressionContext ctx) {
        Expression lhs = (Expression) visit(ctx.sumExpression(0));
        for (int i = 1; i < ctx.sumExpression().size(); i++){
            Expression rhs = (Expression) visit(ctx.sumExpression(i));
            String op = ctx.relop(i - 1).getText();
            lhs = new BinaryOperator(lhs, op, rhs);
        }
        return lhs;
    }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitRelop(CminusParser.RelopContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitSumExpression(CminusParser.SumExpressionContext ctx) {
        Expression lhs = (Expression) visit(ctx.termExpression(0));
        for (int i = 1; i < ctx.termExpression().size(); i++){
            Expression rhs = (Expression) visit(ctx.termExpression(i));
            String op = ctx.sumop(i - 1).getText();
            lhs = new BinaryOperator(lhs, op, rhs);
        }
        return lhs;
    }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitSumop(CminusParser.SumopContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitTermExpression(CminusParser.TermExpressionContext ctx) {
        Expression lhs = (Expression) visit(ctx.unaryExpression(0));
        for (int i = 1; i < ctx.unaryExpression().size(); i++){
            Expression rhs = (Expression) visit(ctx.unaryExpression(i));
            String op = ctx.mulop(i - 1).getText();
            lhs = new BinaryOperator(lhs, op, rhs);
        }
        return lhs;
    }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitMulop(CminusParser.MulopContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitUnaryExpression(CminusParser.UnaryExpressionContext ctx) {
        Expression node = (Expression) visit(ctx.factor());

        for (int i = ctx.unaryop().size() - 1; i >= 0; i--){
            String op = ctx.unaryop(i).getText();
            node = new UnaryOperator(op, node);
        }
        return node;
    }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitUnaryop(CminusParser.UnaryopContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitFactor(CminusParser.FactorContext ctx) {
        return visit(ctx.getChild(0));
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitMutable(CminusParser.MutableContext ctx) {
        String id = ctx.ID().getText();
        Expression indexExpr = null;

        if (ctx.expression() != null){
            indexExpr = (Expression) visit(ctx.expression());
        }

        if (symbolTable.find(id) == null){
            LOGGER.warning("Undefined symbol on line " + ctx.getStart().getLine() + ": " + id);
        }

        return new Mutable(id, indexExpr);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitImmutable(CminusParser.ImmutableContext ctx) {
        if (ctx.expression() != null) {
            Expression innerExpr = (Expression) visit(ctx.expression());
            return new ParenExpression(innerExpr);
        }
        return visit(ctx.getChild(0));
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitCall(CminusParser.CallContext ctx) {
        String id = ctx.ID().getText();
        List<Expression> args = new ArrayList<>();

        for (CminusParser.ExpressionContext exprCtx : ctx.expression()){
            args.add((Expression) visit(exprCtx));
        }

        if (symbolTable.find(id) == null){
            LOGGER.warning("Undefined symbol on line " + ctx.getStart().getLine() + ": " + id);

        }

        return new Call(id, args);
    }

}
