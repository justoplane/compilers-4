package submit.ast;

import java.util.List;

public class FunDeclaration implements Declaration {

    private final VarType returnType;
    private final String id;
    private final List<Declaration> params;
    private final Statement body;

    public FunDeclaration(VarType returnType, String id, List<Declaration> params, Statement body){
        this.returnType = returnType;
        this.id = id;
        this.params = params;
        this.body = body;
    }

    @Override
    public void toCminus(StringBuilder builder, final String prefix) {
        builder.append(prefix);

        if (returnType == null) {
            builder.append("void ");
        } else {
            builder.append(returnType).append(" ");
        }

        builder.append(id).append("(");

        for (int i = 0; i < params.size(); i++){
            params.get(i).toCminus(builder, "");

            if (builder.charAt(builder.length() - 1) == '\n') {
                builder.deleteCharAt(builder.length() - 1);
            }
            if (builder.charAt(builder.length() - 1) == ';'){
                builder.deleteCharAt(builder.length() - 1);
            }

            if (i < params.size() - 1){
                builder.append(", ");
            }
        }

        builder.append(")\n");

        if (body != null){
            body.toCminus(builder, prefix);
        }



    }


}
