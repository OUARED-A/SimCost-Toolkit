/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
 */
package br.com.pucrio.inf.biobd.outertuning.bib.sgbd;

import br.com.pucrio.inf.biobd.outertuning.bib.configuration.Configuration;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

/**
 *
 * @author Rafael
 */
public class PlanOracle extends Plan {

    private Properties planProperties;
    private static Configuration config;

    public PlanOracle(String plan, String planIDE, Date dateExecution) {
        super.setPlan(plan);
        super.setDateExecution(dateExecution);
        super.setPlanIDE(planIDE);
        this.readPlan();
        if (config == null) {
            config = new Configuration();
        }
    }

    @Override
    public long getCost() {
        if (this.planProperties.getProperty("cost") != null) {
            return Long.valueOf(this.planProperties.getProperty("cost"));
        } else {
            return 0;
        }
    }

    @Override
    public long getNumRow() {
        if (this.planProperties.containsKey("cardinality")) {
            return Long.valueOf(this.planProperties.getProperty("cardinality"));
        } else {
            return 0;
        }
    }

    @Override
    public long getRowSize() {
        if (this.planProperties.getProperty("bytes") == null || this.planProperties.getProperty("bytes").equals("null")) {
            return 0;
        }
        long numRows = this.getNumRow();
        long totalSize = Long.valueOf(this.planProperties.getProperty("bytes")) * 8;
        if (totalSize > 0 && numRows > 0) {
            long total = totalSize / numRows;
            if (total <= 0) {
                total = Long.valueOf(this.planProperties.getProperty("bytes")) * 8;
            }
            return total;
        } else {
            return 0;
        }
    }

    @Override
    public ArrayList<SeqScan> getSeqScanOperations() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public float getDuration() {
        // MARRETADA
        if (this.planProperties.containsKey("time") && this.planProperties.getProperty("time").equals("null")) {
            Random rand = new Random();
            this.planProperties.setProperty("time", String.valueOf((rand.nextInt(15) + 1)));
        }
        if (this.planProperties.containsKey("time") && !this.planProperties.getProperty("time").equals("null")) {
            float elapsed = Float.valueOf(this.planProperties.getProperty("time"));
            if (elapsed > 1000000) {
                return elapsed / 1000000;
            }
            if (elapsed > 0) {
                return elapsed;
            }
        }
        return 0;
    }

    private void readPlan() {
        this.planProperties = new Properties();
        String lines[] = this.getPlan().split("\\r?\\n");
        for (String line : lines) {
            String properties[] = line.split("=");
            if (properties.length > 1) {
                try {
                    this.planProperties.put(properties[0].toLowerCase(), properties[1]);
                } catch (Exception e) {
                    System.out.println("Erro: " + line);
                }
            }
        }
    }

    @Override
    public String getPlanToViewHtml() {
        return this.getPlanIDE();
    }

    @Override
    public void debug() {
        super.debug();
        planProperties.entrySet().stream().forEach((e) -> {
            System.out.println(e.getKey() + ": " + e.getValue());
        });
        System.out.println("***** FIM DEBUG PLAN ORACLE ******");
    }

}
