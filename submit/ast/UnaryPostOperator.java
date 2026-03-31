package submit.ast;

public class UnaryPostOperator implements Expression{

    private final Expression expr;
    private final String op;

    public UnaryPostOperator(Expression expr, String op){
        this.expr = expr;
        this.op = op;
    }

    @Override
    public void toCminus(StringBuilder builder, String prefix) {
        expr.toCminus(builder, prefix);
        builder.append(op);
    }
}
