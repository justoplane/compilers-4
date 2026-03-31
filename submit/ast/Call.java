package submit.ast;

import java.util.List;

public class Call implements Expression{

    private final String id;
    private final List<Expression> args;

    public Call(String id, List<Expression> args){
        this.id = id;
        this.args = args;
    }


    @Override
    public void toCminus(StringBuilder builder, String prefix) {
        builder.append(prefix).append(id).append("(");

        for (int i = 0; i < args.size(); i++){
            args.get(i).toCminus(builder, "");

            if (i < args.size() - 1){
                builder.append(", ");
            }
        }

        builder.append(")");
    }
}
