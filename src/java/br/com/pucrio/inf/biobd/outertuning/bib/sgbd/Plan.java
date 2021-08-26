/*
 * Framework para apoiar a sintonia fina de banco de dados. PUC-RIO.
 * Ana Carolina Almeida [anacrl@gmail.com].
 * Rafael Pereira de Oliveira [rpoliveira@inf.puc-rio.br].
 * PUC-RIO - LABBIO - 2014.
 */
package br.com.pucrio.inf.biobd.outertuning.bib.sgbd;

import java.util.Date;

/**
 *
 * @author Rafael
 */
public abstract class Plan implements IPlan {

    private String plan;
    private String planIDE;
    private Date dateExecution;

    public String getPlan() {
        return plan;
    }

    public Date getDateExecution() {
        return dateExecution;
    }

    public String getPlanIDE() {
        return planIDE;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public void setPlanIDE(String planIDE) {
        this.planIDE = planIDE;
    }

    public void setDateExecution(Date dateExecution) {
        this.dateExecution = dateExecution;
    }

    @Override
    public void debug() {
        System.out.println("***** DEBUG PLAN ORACLE ******");
        System.out.println("ROW SIZE: " + getRowSize());
        System.out.println("COST: " + getCost());
        System.out.println("NUMROW: " + getNumRow());
        System.out.println("DURATION: " + getDuration());
        System.out.println("PLAN: " + getPlan());
    }

}
