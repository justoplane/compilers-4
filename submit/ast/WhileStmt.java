package submit.ast;

public class WhileStmt implements Statement{

    private final Expression condition;
    private final Statement body;

    public WhileStmt(Expression condition, Statement body){
        this.condition = condition;
        this.body = body;
    }


    @Override
    public void toCminus(StringBuilder builder, String prefix) {
        builder.append(prefix).append("while (");
        condition.toCminus(builder, "");
        builder.append(")\n");

        if (body instanceof CompoundStmt) {
            body.toCminus(builder, prefix);
        } else {
            body.toCminus(builder, prefix + " ");
        }
    }
}
