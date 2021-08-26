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
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Relation;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Source;
import br.com.pucrio.inf.biobd.outertuning.bib.sgbd.Column;
import br.com.pucrio.inf.biobd.outertuning.bib.sgbd.SQL;
import br.com.pucrio.inf.biobd.outertuning.bib.sgbd.Table;
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public class Tabela extends FunctionConceptBase {

    @Override
    public ArrayList<Concept> getIndividualsForInstantiate(Source source) {
        return this.fcInstanciaTabela(source);
    }

    public ArrayList<Concept> fcInstanciaTabela(Source source) {
        this.list = new ArrayList<>();
        if (source.hasNotAnalyzed()) {
            SQL workload = source.getWorkload();
            for (Table queryTable : workload.getTablesQuery()) {
                this.list.add(this.parseTableToConcept(queryTable, source));
            }
        }
        return this.list;
    }

    private Concept parseTableToConcept(Table table, Source source) {
        Concept tabela = new Concept();
        tabela.setClassName("Tabela");

        ArrayList<Concept> tableRelationHasColumn = this.instantiateColuns(table);
        tabela.addRelation(new Relation(tableRelationHasColumn, "possuiColuna"));

        ArrayList<Concept> tableRelationForeignKey = this.instantiateForeignKeys(table);
        tabela.addRelation(new Relation(tableRelationForeignKey, "temChaveEstrangeira"));

        ArrayList<Concept> tableRelationPrimaryKey = this.instantiatePrimaryKeys(table);
        tabela.addRelation(new Relation(tableRelationPrimaryKey, "temChavePrimaria"));

        for (Property property : source.paramOut) {
            Property propCopy = new Property();
            propCopy.copy(property);
            propCopy.setValue(table.getValue(propCopy.getDataType()));
            tabela.paramOut.add(propCopy);
        }
        return tabela;
    }

    private ArrayList<Concept> instantiateForeignKeys(Table table) {
        ArrayList<Concept> listForeignKey = new ArrayList<>();
        for (Column column : table.getFields()) {
            if (column.getForeignKey() != null) {
                listForeignKey.add(this.parseColumnToConcept(column));
            }
        }
        return listForeignKey;
    }

    private ArrayList<Concept> instantiateColuns(Table table) {
        ArrayList<Concept> listColumns = new ArrayList<>();
        for (Column column : table.getFields()) {
            listColumns.add(this.parseColumnToConcept(column));
        }
        return listColumns;
    }

    private ArrayList<Concept> instantiatePrimaryKeys(Table table) {
        ArrayList<Concept> listPrimaryKey = new ArrayList<>();
        for (Column column : table.getFields()) {
            if (column.isPrimaryKey()) {
                listPrimaryKey.add(this.parseColumnToConcept(column));
            }
        }
        return listPrimaryKey;
    }

    private Concept parseColumnToConcept(Column column) {
        Concept columnConcept = new Concept();
        columnConcept.setClassName("Coluna");

        Property nameColumn = new Property();
        nameColumn.setDataType("temNome");
        nameColumn.setValue(column.getName());
        columnConcept.paramOut.add(nameColumn);

        Property orderColumn = new Property();
        orderColumn.setDataType("temOrdem");
        orderColumn.setValue(column.getOrder());
        columnConcept.paramOut.add(orderColumn);

        Property typeColumn = new Property();
        typeColumn.setDataType("temTipo");
        typeColumn.setValue(column.getType());
        columnConcept.paramOut.add(typeColumn);

        Property domainRestriction = new Property();
        domainRestriction.setDataType("temRestricaoDominio");
        domainRestriction.setValue(column.getDomainRestriction());
        columnConcept.paramOut.add(domainRestriction);

        return columnConcept;
    }

}
