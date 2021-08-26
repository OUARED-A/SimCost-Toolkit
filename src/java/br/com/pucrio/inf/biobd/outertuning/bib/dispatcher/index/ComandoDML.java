package br.com.pucrio.inf.biobd.outertuning.bib.dispatcher.index;

/*
 * Framework para apoiar a sintonia fina de banco de dados. PUC-RIO.
 * Ana Carolina Almeida [anacrl@gmail.com].
 * Rafael Pereira de Oliveira [rpoliveira@inf.puc-rio.br].
 * PUC-RIO - LABBIO - 2014.
 */
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Concept;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.FunctionConceptBase;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Property;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Source;
import br.com.pucrio.inf.biobd.outertuning.bib.sgbd.SQL;
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public class ComandoDML extends FunctionConceptBase {

    @Override
    public ArrayList<Concept> getIndividualsForInstantiate(Source source) {
        return this.fcInstanciaComandoDML(source);
    }

    public ArrayList<Concept> fcInstanciaComandoDML(Source source) {
        this.list = new ArrayList<>();
        if (source.hasNotAnalyzed()) {
            Concept dml = new Concept();
            dml.setClassName("Consulta");
            for (Property param : source.paramOut) {
                if (param.getDataType().equals("temDescricao")) {
                    SQL work = source.getWorkload();
                    param.setValue(work.getSql());
                    dml.paramOut.add(param);
                }
            }
            this.list.add(dml);
        }
        return list;
    }

}
