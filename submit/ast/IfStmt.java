package submit.ast;

public class IfStmt implements Statement{

    private final Expression condition;
    private final Statement thenStmt;
    private final Statement elseStmt;

    public IfStmt(Expression condition, Statement thenStmt, Statement elseStmt){
        this.condition = condition;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
    }


    @Override
    public void toCminus(StringBuilder builder, String prefix) {
        builder.append(prefix).append("if (");
        condition.toCminus(builder, "");
        builder.append(")\n");

        if (thenStmt instanceof CompoundStmt) {
            thenStmt.toCminus(builder, prefix);
        } else {
            thenStmt.toCminus(builder, prefix + " ");
        }

        if (elseStmt != null){
            builder.append(prefix).append("else\n");

            if (elseStmt instanceof CompoundStmt) {
                elseStmt.toCminus(builder, prefix);
            } else {
                elseStmt.toCminus(builder, prefix + " ");
            }
        }
    }
}
