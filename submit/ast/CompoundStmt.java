package submit.ast;
import java.util.List;

public class CompoundStmt implements Statement{

    private final List<VarDeclaration> varDecls;
    private final List<Statement> statements;

    public CompoundStmt(List<VarDeclaration> varDecls, List<Statement> statements){
        this.varDecls = varDecls;
        this.statements = statements;
    }


    @Override
    public void toCminus(StringBuilder builder, String prefix) {
        builder.append(prefix).append("{\n");

        String childPrefix = prefix + "  ";

        for (VarDeclaration varDecl : varDecls){
            varDecl.toCminus(builder, childPrefix);
        }

        for (Statement stmt : statements){
            stmt.toCminus(builder, childPrefix);
        }

        builder.append(prefix).append("}\n");
    }
}
