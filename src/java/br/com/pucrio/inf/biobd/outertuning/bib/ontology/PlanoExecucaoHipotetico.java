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
public class PlanoExecucaoHipotetico {

    public ArrayList<Concept> getPlanoExecucaoHipoteticoForRelation(SQL work) {
        Concept planoExecucao = new Concept();
        planoExecucao.setClassName("PlanoExecucaoHipotetico");

        Property temCustoExecucao = new Property();
        temCustoExecucao.setDataType("temCustoExecucao");
        temCustoExecucao.setValue((int) work.getLastPlan().getCost());
        planoExecucao.paramOut.add(temCustoExecucao);

        Property temDescricao = new Property();
        temDescricao.setDataType("temDescricao");
        temDescricao.setValue(work.getLastPlan().getPlan());
        planoExecucao.paramOut.add(temDescricao);

        Property temNumeroTuplasProcessadas = new Property();
        temNumeroTuplasProcessadas.setDataType("temNumeroTuplasProcessadas");
        temNumeroTuplasProcessadas.setValue((int) work.getLastPlan().getNumRow());
        planoExecucao.paramOut.add(temNumeroTuplasProcessadas);

        ArrayList<Concept> planoExecucaoList = new ArrayList<>();
        planoExecucaoList.add(planoExecucao);
        return planoExecucaoList;
    }

}
