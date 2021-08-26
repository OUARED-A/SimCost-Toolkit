/*
 * Framework para apoiar a sintonia fina de banco de dados. PUC-RIO.
 * Ana Carolina Almeida [anacrl@gmail.com].
 * Rafael Pereira de Oliveira [rpoliveira@inf.puc-rio.br].
 * PUC-RIO - LABBIO - 2014.
 */
package br.com.pucrio.inf.biobd.outertuning.bib.dispatcher.mv;

import br.com.pucrio.inf.biobd.outertuning.bib.sgbd.Column;
import br.com.pucrio.inf.biobd.outertuning.bib.sgbd.SQL;
import br.com.pucrio.inf.biobd.outertuning.bib.sgbd.Table;
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public class DefineView {

    private String select;
    private String from;
    private String where;
    private String groupBy;
    private String orderBy;

    public String getDdlCreateViewFromQuery(SQL query) {
        this.gerateClauseSelectForDDLView(query);
        this.gerateClauseFromForDDLView(query);
        this.gerateClauseWhereForDDLView(query);
        this.gerateClauseGroupByForDDLView(query);
        this.gerateClauseOrderByForDDLView(query);
        return this.getDdlCreateViewComplete();
    }

    private String getDdlCreateViewComplete() {
        return this.treatComma(this.select) + " "
                + treatComma(this.from) + " "
                + treatComma(this.where) + " "
                + treatComma(this.groupBy) + " "
                + treatComma(this.orderBy);
    }

    private String treatComma(String query) {
        query = query.trim();
        if (query.length() > 0 && query.charAt(query.length() - 1) == ',') {
            query = query.substring(0, query.length() - 1);
        }
        return query;
    }

    public void gerateClauseSelectForDDLView(SQL query) {
        this.select = query.getClauseFromSql("select").trim();
        String fields = ", ";
        if (!this.select.equals("select *")) {
            for (Table table : query.getTablesQuery()) {
                for (Column field : table.getFields()) {
                    if (query.getSql().toLowerCase().contains(field.getName()) && !select.toLowerCase().contains(field.getName())) {
                        if (!fields.equals(", ")) {
                            fields += ", ";
                        }
                        fields += field.getName();
                    }
                }
            }
        }
        this.select = query.getComents() + this.select + fields;
        this.groupBy = fields;
    }

    public void gerateClauseFromForDDLView(SQL query) {
        this.from = query.getClauseFromSql("from");
        for (Table table : query.getTablesQuery()) {
            if (!this.from.contains(table.getName())) {
                this.from += ", " + table.getName();
            }
        }
    }

    public void gerateClauseGroupByForDDLView(SQL query) {
        if (!this.groupBy.isEmpty() && query.existClause("group by")) {
            this.groupBy = query.getClauseFromSql("group by") + this.groupBy;
        } else if (this.hasForceClauseGroupBy()) {
            this.groupBy = " group by " + this.groupBy.substring(1);
        } else {
            this.groupBy = "";
        }
    }

    public void gerateClauseOrderByForDDLView(SQL query) {
        this.orderBy = query.getClauseFromSql("order by");
    }

    public boolean hasForceClauseGroupBy() {
        return !this.groupBy.trim().isEmpty() && !this.groupBy.trim().equals(",") && (this.select.contains(" sum(") || this.select.contains(" count("));
    }

    public void gerateClauseWhereForDDLView(SQL query) {
        String clause = query.getClauseFromSql("where");
        Combinacao combination = new Combinacao();
        ArrayList<String> lista = combination.dividirExpressaoPredicado(clause);
        this.where = "";
        for (String constrain : lista) {
            if (isConstrainValid(constrain) && !this.where.contains(constrain)) {
                if (!this.where.isEmpty()) {
                    this.where += " and ";
                }
                this.where += " " + constrain;
            }
        }
        if (!this.where.isEmpty()) {
            this.where = "where " + this.where;
        }
    }

    private boolean isConstrainValid(String constrain) {
        if (constrain.contains("'") || constrain.contains("\"")) {
            return false;
        }
        if (constrain.contains("<")) {
            return false;
        }
        if (constrain.contains(">")) {
            return false;
        }
        String[] words = constrain.split(" ");
        for (String word : words) {
            if (this.containNumber(word)) {
                return false;
            }
        }
        return true;
    }

    private boolean containNumber(String word) {
        if (word.contains("0")
                || word.contains("1")
                || word.contains("2")
                || word.contains("3")
                || word.contains("4")
                || word.contains("5")
                || word.contains("6")
                || word.contains("7")
                || word.contains("8")
                || word.contains("9")) {
            return true;
        }
        return false;
    }

}
