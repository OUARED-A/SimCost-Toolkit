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
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public class SGBD extends FunctionConceptBase {

    @Override
    public ArrayList<Concept> getIndividualsForInstantiate(Source source) {
        return this.fcInstanciaSGBD(source);
    }

    public ArrayList<Concept> fcInstanciaSGBD(Source source) {
        this.list = new ArrayList<>();
        if (source.hasNotAnalyzed()) {
            if (source.paramIn == null) {
                Concept sgbd = new Concept();
                sgbd.setClassName(source.getClassName());
                for (Property param : source.paramOut) {
                    String value = this.sqwrl.getParameter(source.getClassName() + param.getDataType());
                    param.setValue(this.convertTypeObject(param.getDataType(), value));
                    sgbd.paramOut.add(param);
                }
                this.list.add(sgbd);
            }
        }
        return this.list;
    }

    private Object convertTypeObject(String dataType, String value) {
        switch (dataType) {
            case "temValorFatorPreenchimento":
            case "temTamanhoPagina":
                return Float.valueOf(value);
        }
        return value;
    }

}
