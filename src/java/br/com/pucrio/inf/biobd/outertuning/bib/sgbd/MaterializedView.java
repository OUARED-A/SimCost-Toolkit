/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
 */
package br.com.pucrio.inf.biobd.outertuning.bib.sgbd;

import java.util.Date;

/**
 *
 * @author Rafael
 */
public class MaterializedView extends SQL {

    private Plan hypoPlan;
    private long hypoGain;
    private long hypoNumPages;
    private long hypoCreationCost;
    private long hypoGainAC;
    private String hypoMaterializedView;

    public String getHypoPlan() {
        if (this.hypoPlan != null) {
            return hypoPlan.getPlan();
        } else {
            return "";
        }
    }

    public void setHypoPlan(String hypoPlan, Date dateExecution) {
        switch (config.getProperty("sgbd")) {
            case "postgresql":
                this.hypoPlan = new PlanPostgreSQL(hypoPlan, hypoPlan, dateExecution);
                break;
            case "oracle":
                this.hypoPlan = new PlanOracle(hypoPlan, hypoPlan, dateExecution);
                break;
            default:
                erro();
        }
        this.setHypoNumPages();
        this.setHypoGain();
        this.setHypoGainAC();
        this.setHypoCreationCost();
    }

    public long getHypoGain() {
        return hypoGain;
    }

    public void setHypoGain() {
        this.hypoGain = (this.getLastPlan().getCost() - this.getHypoCost());
    }

    public long getHypoGainAC() {
        this.setHypoGainAC();
        return hypoGainAC;
    }

    public void setHypoGainAC() {
        this.hypoGainAC = this.hypoGain * this.getCaptureCount();
        if (this.hypoGainAC < 0) {
            this.hypoGainAC = -1;
        }
    }

    public void setHypoNumPages() {
        double fillfactory = Double.valueOf(config.getProperty("fillfactory" + config.getProperty("sgbd")));
        int pagesize = Integer.valueOf(config.getProperty("pagesize" + config.getProperty("sgbd")));
        this.hypoNumPages = (long) ((this.hypoPlan.getNumRow() * this.hypoPlan.getRowSize() * fillfactory) / pagesize);
        if (this.hypoNumPages < 1) {
            this.hypoNumPages = 1;
        }
    }

    public long getHypoCreationCost() {
        return hypoCreationCost;
    }

    public void setHypoCreationCost() {
        this.hypoCreationCost = (this.getHypoNumPages() * 2) + this.getLastPlan().getCost();
    }

    public long getHypoNumPages() {
        return hypoNumPages;
    }

    @Override
    public void debug() {
        super.debug();
        log.title("Hypothetical cost of a Materialized View " + this.getComents());
        log.msg("HypoGain: " + this.getHypoGain());
        log.msg("HypoGainAC: " + this.getHypoGainAC());
        log.msg("HypoNumPages: " + this.getHypoNumPages());
        log.msg("hypoNumRow: " + this.hypoPlan.getNumRow());
        log.msg("hypoSizeRow: " + this.hypoPlan.getRowSize());
        log.msg("hypoCost: " + this.getHypoCost());
        log.msg("Cost - hypoCost: " + (this.getLastPlan().getCost() - this.getHypoCost()));
        log.msg("HypoCreationCost: " + this.getHypoCreationCost());
        log.msg("Hypo Query MV: " + this.removerNl(this.getHypoMaterializedView()));
        log.endTitle();
    }

    public String getHypoMaterializedView() {
        String ddl = config.getProperty("getDDLCreateMV" + config.getProperty("sgbd"));
        ddl = ddl.replace("$nameMV$", this.getNameMaterializedView());
        ddl = ddl.replace("$sqlMV$", this.hypoMaterializedView);
        return ddl.trim();
    }

    public String getHypoMaterializedViewHTML() {
        return commandToHtml(getHypoMaterializedView());
    }

    private Object erro() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getNameMaterializedView() {
        return "OT_MV_" + String.valueOf(this.hypoMaterializedView.hashCode()).replace("-", "");
    }

    private long getHypoCost() {
        return this.getHypoNumPages();
    }

    public void setHypoMaterializedView(String hypoMaterializedView) {
        this.hypoMaterializedView = hypoMaterializedView;
    }

}
