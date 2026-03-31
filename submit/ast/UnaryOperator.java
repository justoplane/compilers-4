package submit.ast;

public class UnaryOperator implements Expression{

    private final String op;
    private final Expression expr;

    public UnaryOperator(String op, Expression expr){
        this.op = op;
        this.expr = expr;
    }

    @Override
    public void toCminus(StringBuilder builder, String prefix) {
        builder.append(prefix).append(op);
        expr.toCminus(builder, "");
    }
}
