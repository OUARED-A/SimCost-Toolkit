/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.pucrio.inf.biobd.outertuning.bib.dispatcher.mv;

import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Concept;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.FunctionConceptBase;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Property;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Source;
import br.com.pucrio.inf.biobd.outertuning.bib.sgbd.SQL;
import java.util.ArrayList;
import org.protege.owl.portability.query.Result;
import org.protege.owl.portability.query.ResultException;

/**
 *
 * @author Rafael
 */
public class VisaoMaterializada extends FunctionConceptBase {

    @Override
    public ArrayList<Concept> getIndividualsForInstantiate(Source source) {
        return this.fcInstanciaVisaoMaterializada(source);
    }

    public ArrayList<Concept> fcInstanciaVisaoMaterializada(Source source) {
        this.list = new ArrayList<>();
        try {
            Result allComandoDML = (Result) source.paramIn;
            SQL work = source.getWorkload();
            if (allComandoDML != null) {
                while (allComandoDML.hasNext()) {
                    if (work.getSql().equals(allComandoDML.getValue("?sql").toString())) {
                        Concept VMH = new Concept();
                        VMH.setClassName("VisaoMaterializada");
                        String nomeVM = allComandoDML.getValue("?vm").toString();
                        float ganho = Float.valueOf(allComandoDML.getValue("?acumulado").toString()) + Float.valueOf(allComandoDML.getValue("?ganho").toString());

                        VMH.setName(nomeVM);
                        Property temGanho = new Property();
                        temGanho.setDataType("temValorBonusAcumulado");
                        temGanho.setValue(ganho);
                        temGanho.setReplace(true);
                        VMH.paramOut.add(temGanho);
                        this.list.add(VMH);
                    }
                    allComandoDML.next();
                }
            }

        } catch (ResultException ex) {
            System.out.println(ex);
            return new ArrayList<>();
        }
        return this.list;
    }

}
