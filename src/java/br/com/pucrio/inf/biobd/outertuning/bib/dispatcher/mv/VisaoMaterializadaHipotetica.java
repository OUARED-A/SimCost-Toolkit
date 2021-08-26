/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.pucrio.inf.biobd.outertuning.bib.dispatcher.mv;

import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Concept;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.FunctionConceptBase;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.PlanoExecucaoReal;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Property;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Relation;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Source;
import br.com.pucrio.inf.biobd.outertuning.bib.sgbd.CaptorPlan;
import br.com.pucrio.inf.biobd.outertuning.bib.sgbd.MaterializedView;
import br.com.pucrio.inf.biobd.outertuning.bib.sgbd.SQL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import org.protege.owl.portability.query.Result;
import org.protege.owl.portability.query.ResultException;

/**
 *
 * @author Rafael
 */
public class VisaoMaterializadaHipotetica extends FunctionConceptBase {

    @Override
    public ArrayList<Concept> getIndividualsForInstantiate(Source source) {
        return this.fcInstanciaVisaoMaterializadaHipotetica(source);
    }

    public ArrayList<Concept> fcInstanciaVisaoMaterializadaHipotetica(Source source) {
        this.list = new ArrayList<>();

        MaterializedView VMH_N = this.createMaterializedView(source.getWorkload(), false);
        Concept C_VMH_N = this.getConceptVisaoMaterializadaHipotetica(source, VMH_N);
        C_VMH_N.addRelation(new Relation(this.getPlanoExecucaoConcept(VMH_N), "produz", false));
        this.list.add(C_VMH_N);

        MaterializedView VMH_A = this.createMaterializedView(source.getWorkload(), true);
        Concept C_VMH_A = this.getConceptVisaoMaterializadaHipotetica(source, VMH_A);
        C_VMH_A.addRelation(new Relation(this.getPlanoExecucaoConcept(VMH_A), "produz", false));
        this.list.add(C_VMH_A);

        ArrayList<Concept> vmhList_N = new ArrayList<>();
        vmhList_N.add(C_VMH_N);

        ArrayList<Concept> vmhList_A = new ArrayList<>();
        vmhList_A.add(C_VMH_A);

        if (source.getWorkload().getCaptureCount() == 1) {
            Concept execucaoComando_N = this.getConceptExecucaoComando(source);
            execucaoComando_N.addRelation(new Relation(vmhList_N, "origina", false));
            execucaoComando_N.addRelation(new Relation(vmhList_A, "origina", false));
            this.list.add(execucaoComando_N);
        }

        return this.list;
    }

    private MaterializedView createMaterializedView(SQL work, boolean agrawal) {
        String definicaoView;
        MaterializedView view = new MaterializedView();
        view.setId(work.getId());
        view.setSql(work.getSql());
        if (agrawal) {
            DefineView defineVMH = new DefineView();
            definicaoView = defineVMH.getDdlCreateViewFromQuery(work);
            CaptorPlan captor = new CaptorPlan();
            Date date = new Date();
            view.addExecution(captor.getPlanExecution(definicaoView, new Timestamp(date.getTime())));
        } else {
            definicaoView = work.getSql();
            view.addExecution(work.getLastPlan());
        }
        view.setHypoMaterializedView(definicaoView);
        return view;
    }

    private ArrayList<Concept> getPlanoExecucaoConcept(MaterializedView workload) {
        PlanoExecucaoReal plano = new PlanoExecucaoReal();
        return plano.getPlanoExecucaoRealForRelation(workload);
    }

    private Concept getConceptVisaoMaterializadaHipotetica(Source source, MaterializedView workView) {
        Concept VMH = new Concept();
        VMH.setClassName("VisaoMaterializadaHipotetica");
        for (Property param : source.paramOut) {
            Property paramNew = new Property();
            paramNew.copy(param);
            switch (paramNew.getDataType()) {
                case "temDescricao":
                    paramNew.setValue(workView.getHypoMaterializedView());
                    VMH.paramOut.add(paramNew);
                    break;
                case "temNome":
                    String name = workView.getNameMaterializedView();
                    paramNew.setValue(name);
                    VMH.paramOut.add(paramNew);
                    VMH.setName(name);
                    break;
                case "temTamanhoTupla":
                    paramNew.setValue((int) workView.getLastPlan().getRowSize());
                    VMH.paramOut.add(paramNew);
                    break;
                case "temSituacao":
                    paramNew.setValue("coletando");
                    VMH.paramOut.add(paramNew);
                    break;
            }
            Property temGanho = new Property();
            temGanho.setDataType("temValorBonusAcumulado");
            temGanho.setValue(0);
            VMH.paramOut.add(temGanho);
        }
        return VMH;
    }

    private Concept getConceptExecucaoComando(Source source) {
        Concept execucaoComando = new Concept();
        execucaoComando.setClassName("ExecucaoComando");
        if (source.paramIn != null) {
            Result allComandoDML = (Result) source.paramIn;
            SQL work = source.getWorkload();
            try {
                while (allComandoDML.hasNext()) {
                    if (allComandoDML.getValue("?desc").toString().toLowerCase().equals(work.getSql().toLowerCase())) {
                        execucaoComando.setName(allComandoDML.getValue("?exec").toString());
                    }
                    allComandoDML.next();
                }
            } catch (ResultException ex) {
                System.out.println(ex);
            }
        }
        return execucaoComando;
    }

}
