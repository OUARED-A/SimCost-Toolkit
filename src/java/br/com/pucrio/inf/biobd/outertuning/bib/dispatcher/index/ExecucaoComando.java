/*
 * Framework para apoiar a sintonia fina de banco de dados. PUC-RIO.
 * Ana Carolina Almeida [anacrl@gmail.com].
 * Rafael Pereira de Oliveira [rpoliveira@inf.puc-rio.br].
 * PUC-RIO - LABBIO - 2014.
 */
package br.com.pucrio.inf.biobd.outertuning.bib.dispatcher.index;

import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Concept;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.FunctionConceptBase;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.PlanoExecucaoReal;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Property;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Relation;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Source;
import br.com.pucrio.inf.biobd.outertuning.bib.sgbd.SQL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 *
 * @author Rafael
 */
public class ExecucaoComando extends FunctionConceptBase {

    @Override
    public ArrayList<Concept> getIndividualsForInstantiate(Source source) {
        return this.fcInstanciaExecucaoComando(source);
    }

    public ArrayList<Concept> fcInstanciaExecucaoComando(Source source) {
        this.list = new ArrayList<>();
        SQL work = source.getWorkload();
        Concept execucaoComando = new Concept();
        execucaoComando.setClassName(source.getClassName());
        for (Property param : source.paramOut) {
            switch (param.getDataType()) {
                case "temNumeroExecucao":
                    param.setValue((int) work.getCaptureCount());
                    break;
                case "temData":
                    param.setValue(this.getDate());
                    break;
                case "temHora":
                    param.setValue(this.getTime());
                    break;
            }
            execucaoComando.paramOut.add(param);
        }
        execucaoComando.addRelation(new Relation(this.getCommandDMLForRelation(work), "corresponde"));
        execucaoComando.addRelation(new Relation(this.getSGBDForRelation(), "executado"));
        PlanoExecucaoReal plano = new PlanoExecucaoReal();
        execucaoComando.addRelation(new Relation(plano.getPlanoExecucaoRealForRelation(work), "produz"));

        this.list.add(execucaoComando);
        return this.list;
    }

    private ArrayList<Concept> getCommandDMLForRelation(SQL work) {
        Concept comandoDmlConcept = new Concept();
        comandoDmlConcept.setClassName("Consulta");
        Property comandoDml = new Property();
        comandoDml.setDataType("temDescricao");
        comandoDml.setValue(work.getSql());
        comandoDmlConcept.paramOut.add(comandoDml);
        ArrayList<Concept> comandoDmlList = new ArrayList<>();
        comandoDmlList.add(comandoDmlConcept);
        return comandoDmlList;
    }

    private ArrayList<Concept> getSGBDForRelation() {
        Concept sgbd = new Concept();
        sgbd.setClassName("SGBD");
        Property temNome = new Property();
        temNome.setDataType("temNome");
        temNome.setValue(this.sqwrl.getParameter("SGBDtemNome"));
        sgbd.paramOut.add(temNome);
        ArrayList<Concept> sgbdList = new ArrayList<>();
        sgbdList.add(sgbd);
        return sgbdList;
    }

    private String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    private String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

}
