/*
 * Framework para apoiar a sintonia fina de banco de dados. PUC-RIO.
 * Ana Carolina Almeida [anacrl@gmail.com].
 * Rafael Pereira de Oliveira [rpoliveira@inf.puc-rio.br].
 * PUC-RIO - LABBIO - 2014.
 */
package br.com.pucrio.inf.biobd.outertuning.bib.sgbd;

import br.com.pucrio.inf.biobd.outertuning.bib.base.Log;
import br.com.pucrio.inf.biobd.outertuning.bib.configuration.Configuration;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 *
 * @author Rafael
 */
public class CaptorPlan {

    protected ConnectionSGBD conection;
    public Configuration config;
    public Log log;

    public CaptorPlan() {
        this.config = new Configuration();
        this.log = new Log(config);
        this.conection = new ConnectionSGBD();
    }

    public String getPlanExecution(String sql) {
        switch (config.getProperty("sgbd")) {
            case "postgresql":
                return (String) this.notYet();
            case "sqlserver":
                return getSQLServerPlan(sql);
            case "oracle":
                return this.getOraclePlan(sql);
            default:
                return null;
        }
    }

    public Plan getPlanExecution(String sql, Timestamp time) {
        switch (config.getProperty("sgbd")) {
            case "postgresql":
                return (Plan) this.notYet();
            case "sqlserver":
                return (Plan) this.notYet();
            case "oracle":
                return (new PlanOracle(this.getPlanExecution(sql), this.getIDEOraclePlan(sql), time));
            default:
                return (Plan) this.notYet();
        }
    }

    public String getEstimatedPlanExecution(String sql) {
        switch (config.getProperty("sgbd")) {
            case "postgresql":
                return (String) this.notYet();
            case "sqlserver":
                return getSQLServerPlan(sql);
            case "oracle":
                return this.getEstimatedOraclePlan(sql);
            default:
                return (String) this.notYet();
        }
    }

    public String getPlanExecutionIDE(String sql) {
        switch (config.getProperty("sgbd")) {
            case "postgresql":
                return (String) this.notYet();
            case "sqlserver":
                return (String) this.notYet();
            case "oracle":
                return this.getIDEOraclePlan(sql);
            default:
                return (String) this.notYet();
        }
    }

    private Object notYet() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private String getSQLServerPlan(String query) {
        String partitionedPlan = "";
        try {
            PreparedStatement preparedStatement = conection.prepareStatement(config.getProperty("getResultPlanQuerySQLServer"));
            preparedStatement.setString(1, query);
            ResultSet result = conection.executeQuery(preparedStatement);
            while (result.next()) {
                partitionedPlan += " " + result.getString(1);
            }
            if (partitionedPlan.isEmpty()) {
                partitionedPlan = this.getEstimatedSQLServerPlan(query);
            }
            result.close();
            preparedStatement.close();
        } catch (SQLException ex) {
            log.error(ex);
        }
        return partitionedPlan;
    }

    private String getEstimatedSQLServerPlan(String query) {
        String plan = "";
        try {
            Statement statement = conection.getStatement();
            statement.execute(config.getProperty("signature") + "SET SHOWPLAN_TEXT OFF");
            statement.execute(config.getProperty("signature") + "SET SHOWPLAN_XML ON");
            ResultSet resultset = statement.executeQuery(config.getProperty("signature") + " " + query);
            while (resultset.next()) {
                plan += " " + resultset.getString(1);
            }
            statement.execute(config.getProperty("signature") + "SET SHOWPLAN_XML OFF");
            statement.execute(config.getProperty("signature") + "SET SHOWPLAN_TEXT OFF");
            resultset.close();
            statement.close();
        } catch (SQLException ex) {
            log.msg(ex);
        }
        System.out.println(plan);
        return plan;
    }

    private String getOraclePlan(String query) {
        String plan = "";
        if (!query.isEmpty()) {
            try {
                Statement statement = conection.getStatement();
                statement.execute(config.getProperty("signature") + config.getProperty("getSqlCleanPlanExecutionForOracle"));
                statement.execute(config.getProperty("signature") + config.getProperty("getSqlGeneratePlanExecutionForOracle").replace("$QUERY$", query));
                query = query.replace("'", "''");
                statement.execute(config.getProperty("signature") + config.getProperty("getSqlUpdatePlanExecutionForOracle").replace("$QUERY$", query));
                ResultSet result = statement.executeQuery(config.getProperty("signature") + " " + config.getProperty("getSqlExtractPlanExecutionForOracle"));
                ResultSetMetaData rsmd = result.getMetaData();
                while (result.next()) {
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        plan += rsmd.getColumnName(i) + "=" + result.getString(i) + "\n";
                    }
                }
                if (plan.trim().isEmpty()) {
                    plan = this.getEstimatedOraclePlan(query);
                }
                result.close();
            } catch (SQLException ex) {
                log.msg(query);
                log.error(ex);
            }
        }
        return plan;
    }

    private String getIDEOraclePlan(String query) {
        String plan = "";
        if (!query.isEmpty()) {
            String queryExplain = "EXPLAIN PLAN SET STATEMENT_ID = 'dbx_ide' for " + query;
            String queryGetPlan = config.getProperty("getSqlExtractPlanExecutionForIDEoracle");
            try {
                conection.executeUpdate(queryExplain);
            } catch (Error ex) {
                log.msg("Query com erro:" + queryExplain);
                log.error(ex);
            }
            try {
                ResultSet result = conection.executeQuery(queryGetPlan);
                while (result.next()) {
                    plan += result.getString(1);
                }
                result.close();
            } catch (SQLException ex) {
                log.msg("Query com erro:" + queryGetPlan);
                log.error(ex);
            }
        }
        if (!plan.isEmpty()) {
            return this.processPlanForIDE(plan);
        } else {
            return "";
        }
    }

    private String processPlanForIDE(String plan) {
        String planResult = plan;
        if (planResult.contains("<body") && planResult.contains("</body>")) {
            planResult = planResult.substring(plan.indexOf("<body") + 24, plan.indexOf("</body>") + 7);
        }
        planResult = planResult.replace("<tbody>", "");
        planResult = planResult.replace("#CCCC99", "#e8e5df");
        planResult = planResult.replace("</tbody>", "");
        planResult = planResult.replace("bordercolor=\"#000000\"", "");
        planResult = planResult.replace("#F7F777", "#f5f5f5");
        return planResult;
    }

    private String getEstimatedOraclePlan(String query) {
        String partitionedPlan = "";
        if (!query.isEmpty()) {
            String queryExplain = "EXPLAIN PLAN SET STATEMENT_ID = 'dbx' for " + query;
            String queryGetPlan = " SELECT * FROM (SELECT * FROM plan_table CONNECT BY prior id = parent_id AND prior statement_id = statement_id START WITH id = 0 AND statement_id = 'dbx') p WHERE p.OPERATION = 'SELECT STATEMENT' AND ROWNUM <= 1";
            try {
                conection.executeUpdate(queryExplain);
            } catch (Error ex) {
                log.msg("Query com erro:" + queryExplain);
                log.error(ex);
            }
            try {
                ResultSet result = conection.executeQuery(queryGetPlan);
                ResultSetMetaData rsmd = result.getMetaData();
                while (result.next()) {
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        partitionedPlan += rsmd.getColumnName(i) + "=" + result.getString(i) + "\n";
                    }
                }
            } catch (SQLException ex) {
                log.msg("Query com erro:" + queryGetPlan);
                log.error(ex);
            }
        }
        return partitionedPlan;
    }

}
