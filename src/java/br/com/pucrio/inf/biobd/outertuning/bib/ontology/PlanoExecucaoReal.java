/*
 * Framework para apoiar a sintonia fina de banco de dados. PUC-RIO.
 * Ana Carolina Almeida [anacrl@gmail.com].
 * Rafael Pereira de Oliveira [rpoliveira@inf.puc-rio.br].
 * PUC-RIO - LABBIO - 2014.
 */
package br.com.pucrio.inf.biobd.outertuning.bib.ontology;

import br.com.pucrio.inf.biobd.outertuning.bib.sgbd.SQL;
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public class PlanoExecucaoReal {

    public ArrayList<Concept> getPlanoExecucaoRealForRelation(SQL work) {
        Concept planoExecucao = new Concept();
        planoExecucao.setClassName("PlanoExecucaoReal");

        Property temCustoExecucao = new Property();
        temCustoExecucao.setDataType("temCustoExecucao");
        temCustoExecucao.setValue((int) work.getLastPlan().getCost());
        planoExecucao.paramOut.add(temCustoExecucao);

        Property temNumeroTuplasProcessadas = new Property();
        temNumeroTuplasProcessadas.setDataType("temNumeroTuplasProcessadas");
        temNumeroTuplasProcessadas.setValue((int) work.getLastPlan().getNumRow());
        planoExecucao.paramOut.add(temNumeroTuplasProcessadas);

        ArrayList<Concept> planoExecucaoList = new ArrayList<>();
        planoExecucaoList.add(planoExecucao);
        return planoExecucaoList;
    }

}
