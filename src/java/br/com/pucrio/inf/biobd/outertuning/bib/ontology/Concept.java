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
public class Concept {

    private String className;
    private String name = "";
    private ArrayList<Relation> relation;
    public ArrayList<Property> paramOut;
    private Object aditionalParam;

    public Object getAditionalParam() {
        return aditionalParam;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAditionalParam(Object aditionalParam) {
        this.aditionalParam = aditionalParam;
    }

    public ArrayList<Relation> getRelation() {
        return relation;
    }

    public void setRelation(ArrayList<Relation> relation) {
        this.relation = relation;
    }

    public void addRelation(Relation newRelation) {
        this.relation.add(newRelation);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Concept() {
        this.relation = new ArrayList<>();
        this.paramOut = new ArrayList<>();
    }

    public String getMsgToPrint() {
        String msg = "CONCEITO: " + this.getClassName();

        for (Property param : this.paramOut) {
            if (param.getValue() != null) {
                msg += " " + param.getDataType() + ": " + param.getValue().toString() + ";";
            }
        }

        return msg;
    }

}
