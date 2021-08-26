/*
 * Framework para apoiar a sintonia fina de banco de dados. PUC-RIO.
 * Ana Carolina Almeida [anacrl@gmail.com].
 * Rafael Pereira de Oliveira [rpoliveira@inf.puc-rio.br].
 * PUC-RIO - LABBIO - 2014.
 */
package br.com.pucrio.inf.biobd.outertuning.bib.ontology;

import br.com.pucrio.inf.biobd.outertuning.bib.configuration.Configuration;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParser;

/**
 *
 * @author Rafael
 */
public class SQWRLQuery {

    private Configuration config = null;

    public SQWRLQuery() {
        this.config = new Configuration();
    }

    public String getParameter(String parameter) {
        String param = this.config.getProperty(parameter);
        return this.replaceSimbolsOntology(param);
    }

    public String replaceSimbolsOntology(String parameter) {
        String param = parameter;
        param = param.replace("IMP_CHAR", " " + SWRLParser.IMP_CHAR + " ");
        param = param.replace("AND_CHAR", " " + SWRLParser.AND_CHAR + " ");
        return param;
    }

    public String getNameWhere() {
        return "Where(?dado) " + SWRLParser.IMP_CHAR
                + "  sqwrl:select(?dado)";
    }

    public String getAllRulesFromHeuristic(String heuristicName) {
        String query = this.getParameter("getAllRulesFromHeuristic");
        query = query.replace("*HEURISTIC*", heuristicName);
        return query;
    }

    public String getAllParametersFromPreConditions(String functionName) {
        String query = this.getParameter("getAllParametersFromPreConditions");
        query = query.replace("*NOMEFUNCAO*", functionName);
        return query;
    }

    public String getRuleFromJustify(String heuristic) {
        String query = this.getParameter("getJustify");
        query = query.replace("*HEURISTICA*", heuristic);
        return query;
    }

    public String getRuleFromJustifyDetails(String comando) {
        String query = this.getParameter("getJustifyQuery");
        query = query.replace("*ESTRUTURAACESSO*", comando);
        return query;
    }

}
