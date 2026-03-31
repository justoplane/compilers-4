package submit.ast;

public class ParenExpression implements Expression{

    private final Expression expr;

    public ParenExpression(Expression expr){
        this.expr = expr;
    }

    @Override
    public void toCminus(StringBuilder builder, String prefix) {
        builder.append(prefix).append("(");
        expr.toCminus(builder, "");
        builder.append(")");
    }
}
