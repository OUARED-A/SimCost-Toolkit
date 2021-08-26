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
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public class Coluna extends FunctionConceptBase {

    @Override
    public ArrayList<Concept> getIndividualsForInstantiate(Source source) {
        return this.fcInstanciaColuna(source);
    }

    public ArrayList<Concept> fcInstanciaColuna(Source source) {
        this.list = new ArrayList<>();
        if (source.hasNotAnalyzed()) {
            this.instantiateColumns(source);
        }
        return this.list;
    }

    private void instantiateColumns(Source source) {
        SQL workload = source.getWorkload();
        for (Column columnQuery : workload.getFieldsQuery()) {
            this.list.add(this.parseColumnToConcept(columnQuery, source));
            if (columnQuery.getForeignKey() != null) {
                this.list.add(this.parseColumnToConcept(columnQuery.getForeignKey(), source));
            }
        }
    }

    private Concept parseColumnToConcept(Column column, Source source) {
        Concept columnConcept = new Concept();
        columnConcept.setClassName(source.getClassName());

        for (Property property : source.paramOut) {
            Property propCopy = new Property();
            propCopy.copy(property);
            propCopy.setValue(column.getValue(propCopy.getDataType()));
            columnConcept.paramOut.add(propCopy);
        }
        return columnConcept;
    }

}
