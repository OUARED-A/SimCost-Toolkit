/*
 * Framework para apoiar a sintonia fina de banco de dados. PUC-RIO.
 * Ana Carolina Almeida [anacrl@gmail.com].
 * Rafael Pereira de Oliveira [rpoliveira@inf.puc-rio.br].
 * PUC-RIO - LABBIO - 2014.
 */
package br.com.pucrio.inf.biobd.outertuning.bib.ontology;

import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public class Relation {

    private ArrayList<Concept> concept;
    private String nameRelation;
    private boolean unique = true;

    public boolean isUnique() {
        return unique;
    }

    public boolean isNotUnique() {
        return !unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public String getNameRelation() {
        return nameRelation;
    }

    public void setNameRelation(String nameRelation) {
        this.nameRelation = nameRelation;
    }

    public Relation(ArrayList<Concept> concept, String nameRelation) {
        this.concept = concept;
        this.nameRelation = nameRelation;
    }

    public Relation(ArrayList<Concept> concept, String nameRelation, boolean isUnique) {
        this.setUnique(isUnique);
        this.concept = concept;
        this.nameRelation = nameRelation;
    }

    public ArrayList<Concept> getConcept() {
        return concept;
    }

    public void setConcept(ArrayList<Concept> concept) {
        this.concept = concept;
    }

}
