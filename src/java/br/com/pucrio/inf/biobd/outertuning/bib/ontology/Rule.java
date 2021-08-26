/*
 * Framework para apoiar a sintonia fina de banco de dados. PUC-RIO.
 * Ana Carolina Almeida [anacrl@gmail.com].
 * Rafael Pereira de Oliveira [rpoliveira@inf.puc-rio.br].
 * PUC-RIO - LABBIO - 2014.
 */
package br.com.pucrio.inf.biobd.outertuning.bib.ontology;

/**
 *
 * @author Rafael
 */
public class Rule {

    private String name;
    private String clause;
    private boolean active;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClause() {
        return clause;
    }

    public void setClause(String clause) {
        this.clause = clause;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Rule(String name, String rule) {
        this.name = name;
        this.clause = rule;
        this.active = true;
    }

    public String getNameSimple() {
        if (this.name.contains("#")) {
            return this.name.substring(this.name.indexOf("#"));
        } else {
            return this.name;
        }
    }

}
