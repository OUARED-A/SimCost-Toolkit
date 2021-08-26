/*
 * Framework para apoiar a sintonia fina de banco de dados. PUC-RIO.
 * Ana Carolina Almeida [anacrl@gmail.com].
 * Rafael Pereira de Oliveira [rpoliveira@inf.puc-rio.br].
 * PUC-RIO - LABBIO - 2014.
 */
package br.com.pucrio.inf.biobd.outertuning.bib.dispatcher.index;

import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Concept;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.FunctionConceptBase;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Property;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Source;
import br.com.pucrio.inf.biobd.outertuning.bib.sgbd.Column;
import br.com.pucrio.inf.biobd.outertuning.bib.sgbd.SQL;
import br.com.pucrio.inf.biobd.outertuning.bib.sgbd.Table;
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public class ObjetoSGBD extends FunctionConceptBase {

    @Override
    public ArrayList<Concept> getIndividualsForInstantiate(Source source) {
        return null;
    }

    public ArrayList<Concept> getObjetoSGBDFromClausula(Source source, String clause) {
        this.list = new ArrayList<>();
        this.getColunasClausula(source, clause);
        this.getTabelasClausula(source, clause);
        return this.list;
    }

    public void getTabelasClausula(Source source, String clause) {
        SQL workload = source.getWorkload();

        for (Table queryTable : workload.getTablesQuery()) {
            if (clause.toLowerCase().contains(queryTable.getName().toLowerCase())) {
                this.list.add(this.parseTableToConceptFromClause(queryTable));
            }
        }
    }

    private Concept parseTableToConceptFromClause(Table queryTable) {
        Concept tabela = new Concept();
        tabela.setClassName("Tabela");
        Property propCopy = new Property();
        propCopy.setDataType("temNome");
        propCopy.setValue(queryTable.getValue(propCopy.getDataType()));
        tabela.paramOut.add(propCopy);
        return tabela;
    }

    public void getColunasClausula(Source source, String clause) {
        SQL workload = source.getWorkload();

        for (Table queryTable : workload.getTablesQuery()) {
            for (Column column : queryTable.getFields()) {
                if (clause.toLowerCase().contains(column.getName().toLowerCase())) {
                    this.list.add(this.parseColumnToConceptFromClause(column));
                }

            }
        }
    }

    private Concept parseColumnToConceptFromClause(Column queryTable) {
        Concept coluna = new Concept();
        coluna.setClassName("Tabela");
        Property propCopy = new Property();
        propCopy.setDataType("temNome");
        propCopy.setValue(queryTable.getValue(propCopy.getDataType()));
        coluna.paramOut.add(propCopy);
        return coluna;
    }

}
