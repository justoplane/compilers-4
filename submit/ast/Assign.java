package submit.ast;

public class Assign implements Expression{

    private final Mutable lhs;
    private final String op;
    private final Expression rhs;

    public Assign(Mutable lhs, String op, Expression rhs){
        this.lhs = lhs;
        this.op = op;
        this.rhs = rhs;
    }

    @Override
    public void toCminus(StringBuilder builder, String prefix) {
        lhs.toCminus(builder, prefix);
        builder.append(" ").append(op).append(" ");
        rhs.toCminus(builder, "");
    }
}
