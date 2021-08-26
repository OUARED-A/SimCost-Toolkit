/*
 * Framework para apoiar a sintonia fina de banco de dados. PUC-RIO.
 * Ana Carolina Almeida [anacrl@gmail.com].
 * Rafael Pereira de Oliveira [rpoliveira@inf.puc-rio.br].
 * PUC-RIO - LABBIO - 2014.
 */
package br.com.pucrio.inf.biobd.outertuning.bib.ontology;

import br.com.pucrio.inf.biobd.outertuning.bib.sgbd.SQL;
import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author Rafael
 */
public class Source {

    private String classJavaName;
    private String className;
    private String library;
    private String heuristic;
    private String functionName;
    private SQL workload;
    private String preConditionSQWRL = "";
    private int order;

    public String getClassJavaName() {
        return classJavaName;
    }

    public void setClassJavaName(String classJavaName) {
        this.classJavaName = "br.com.pucrio.inf.biobd.outertuning." + classJavaName;
    }

    public Object paramIn = null;
    public ArrayList<Property> paramOut;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Source() {
        this.paramOut = new ArrayList<>();
    }

    public String getPreConditionSQWRL() {
        return preConditionSQWRL;
    }

    public void setPreConditionSQWRL(String preConditionSQWRL) {
        this.preConditionSQWRL = preConditionSQWRL;
    }

    public SQL getWorkload() {
        return workload;
    }

    public void setWorkload(SQL workload) {
        this.workload = workload;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getLibrary() {
        return library;
    }

    public void setLibrary(String library) {
        this.library = library;
    }

    public String getHeuristic() {
        return heuristic;
    }

    public void setHeuristic(String heuristic) {
        this.heuristic = heuristic;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public boolean hasNotAnalyzed() {
        return this.getWorkload().getCaptureCount() == 1;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.classJavaName);
        hash = 79 * hash + Objects.hashCode(this.className);
        hash = 79 * hash + Objects.hashCode(this.library);
        hash = 79 * hash + Objects.hashCode(this.functionName);
        hash = 79 * hash + this.order;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Source other = (Source) obj;
        if (!Objects.equals(this.classJavaName, other.classJavaName)) {
            return false;
        }
        if (!Objects.equals(this.className, other.className)) {
            return false;
        }
        if (!Objects.equals(this.library, other.library)) {
            return false;
        }
        if (!Objects.equals(this.functionName, other.functionName)) {
            return false;
        }
        if (this.order != other.order) {
            return false;
        }
        return true;
    }

}
