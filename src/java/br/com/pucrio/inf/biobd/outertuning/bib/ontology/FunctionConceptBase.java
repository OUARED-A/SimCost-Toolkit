package br.com.pucrio.inf.biobd.outertuning.bib.ontology;

/*
 * Framework para apoiar a sintonia fina de banco de dados. PUC-RIO.
 * Ana Carolina Almeida [anacrl@gmail.com].
 * Rafael Pereira de Oliveira [rpoliveira@inf.puc-rio.br].
 * PUC-RIO - LABBIO - 2014.
 */
import br.com.pucrio.inf.biobd.outertuning.bib.base.Log;
import br.com.pucrio.inf.biobd.outertuning.bib.configuration.Configuration;
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public abstract class FunctionConceptBase {

    protected String sql;
    protected ArrayList<Concept> list;
    protected SQWRLQuery sqwrl;
    protected Log log;
    protected Configuration config;

    protected String getSql() {
        return sql;
    }

    protected void setSql(String sql) {
        this.sql = sql;
    }

    public FunctionConceptBase() {
        this.sqwrl = new SQWRLQuery();
        this.config = new Configuration();
        this.log = new Log(config);

    }

    abstract public ArrayList<Concept> getIndividualsForInstantiate(Source source);

}
