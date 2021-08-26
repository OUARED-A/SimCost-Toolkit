package br.com.pucrio.inf.biobd.outertuning.bib.dispatcher.index;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Concept;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.FunctionConceptBase;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Property;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Relation;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Source;
import br.com.pucrio.inf.biobd.outertuning.bib.sgbd.SQL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.protege.owl.portability.query.Result;
import org.protege.owl.portability.query.ResultException;

/**
 *
 * @author Rafael
 */
public class Clausula extends FunctionConceptBase {

    @Override
    public ArrayList<Concept> getIndividualsForInstantiate(Source source) {
        return this.fcInstanciaClausula(source);
    }

    public ArrayList<Concept> fcInstanciaClausula(Source source) {
        this.list = new ArrayList<>();
        if (source.hasNotAnalyzed()) {
            try {
                Result allComandoDML = (Result) source.paramIn;
                SQL work = source.getWorkload();
                if (allComandoDML != null) {
                    while (allComandoDML.hasNext()) {
                        if (allComandoDML.getValue("?desc").toString().toLowerCase().equals(work.getSql().toLowerCase())) {
                            Concept dml = this.getConceptDML(work.getSql());
                            this.setSql(work.getSql());
                            ArrayList<Concept> clausesList = new ArrayList<>();
                            for (String clauseName : work.getNameClauses()) {
                                Concept clauseTemp = this.getClauseFromDML(clauseName, source);
                                if (clauseTemp != null) {
                                    clausesList.add(clauseTemp);
                                    this.list.add(clauseTemp);
                                }
                            }
                            dml.addRelation(new Relation(clausesList, "possui"));
                            this.list.add(dml);
                        }
                        allComandoDML.next();
                    }
                }
            } catch (ResultException ex) {
                Logger.getLogger(Clausula.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return list;
    }

    private String getNameClass(String nameClause) {
        switch (nameClause) {
            case "select":
                return "Select";
            case "from":
                return "From";
            case "where":
                return "Where";
            case "group by":
                return "GroupBy";
            case "order by":
                return "OrderBy";
            case "having":
                return "Having";
            case "limit":
                return "Limit";
            case "delete":
                return "Delete";
            case "update":
                return "Update";
            case "set":
                return "Set";
            case "insert":
                return "Insert";
            default:
                return "";
        }
    }

    private Concept getClauseFromDML(String clauseName, Source source) {
        String clause = source.getWorkload().getClauseFromSql(clauseName);
        if (!clause.isEmpty()) {
            Concept conceptClause = new Concept();
            conceptClause.setClassName(this.getNameClass(clauseName));
            for (Property param : source.paramOut) {
                if (!param.getDataType().equals("referencia")) {
                    Property paramCopy = new Property();
                    paramCopy.copy(param);
                    paramCopy.setValue(clause);
                    conceptClause.paramOut.add(paramCopy);
                }
            }
            ObjetoSGBD objetoSGBD = new ObjetoSGBD();
            conceptClause.addRelation(new Relation(objetoSGBD.getObjetoSGBDFromClausula(source, clause), "referencia"));
            return conceptClause;
        } else {
            return null;
        }
    }

    private Concept getConceptDML(String sql) {
        Concept dml = new Concept();
        dml.setClassName("Consulta");
        Property propertySql = new Property();
        propertySql.setDataType("temDescricao");
        propertySql.setValue(sql);
        dml.paramOut.add(propertySql);
        return dml;
    }

}
