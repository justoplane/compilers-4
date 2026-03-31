package submit.ast;

public class ExpressionStmt implements Statement{

    private final Expression expr;

    public ExpressionStmt(Expression expr){
        this.expr = expr;
    }


    @Override
    public void toCminus(StringBuilder builder, String prefix) {
        if (expr != null){
            expr.toCminus(builder, prefix);
        } else {
            builder.append(prefix);
        }
        builder.append(";\n");
    }
}
