/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
 */
package br.com.pucrio.inf.biobd.outertuning.bib.sgbd;

import br.com.pucrio.inf.biobd.outertuning.bib.base.Log;
import br.com.pucrio.inf.biobd.outertuning.bib.configuration.Configuration;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author Rafael
 */
public final class CaptorWorkload {

    private final CopyOnWriteArrayList<SQL> capturedSQL;
    private final ArrayList<String> badSQL;
    private final ArrayList<SQL> lastcapturedSQL;
    private ConnectionSGBD conection;
    private final Schema schema;
    public final Configuration config;
    public final Log log;
    private final CaptorPlan captorPlan;

    public CaptorWorkload(CopyOnWriteArrayList<SQL> capturedQueryList) {
        this.config = new Configuration();
        this.log = new Log(this.config);
        this.capturedSQL = capturedQueryList;
        this.lastcapturedSQL = new ArrayList<>();
        this.badSQL = new ArrayList<>();
        this.schema = new Schema();
        this.readSchemaDataBase();
        this.captorPlan = new CaptorPlan();
    }

    public ConnectionSGBD getConnection() {
        if (this.conection == null || this.conection.isClosed()) {
            this.conection = new ConnectionSGBD();
        }
        return this.conection;
    }

    private void readSchemaDataBase() {
        try {
            log.title("Reading schema database");
            String query = config.getProperty("getSqlTableNames" + config.getProperty("sgbd"));
            ResultSet schemaResult = getConnection().executeQuery(query);
            if (schemaResult != null) {
                while (schemaResult.next()) {
                    Table currentTable = new Table();
                    currentTable.setSchema(schemaResult.getString(1));
                    currentTable.setName(schemaResult.getString(2));
                    currentTable.setNumberRows(schemaResult.getLong(3));
                    switch (config.getProperty("sgbd")) {
                        case "oracle":
                            long pagesize = Integer.valueOf(config.getProperty("pagesize" + config.getProperty("sgbd")));
                            long numPage = schemaResult.getLong(4) / pagesize;
                            currentTable.setNumberPages(numPage);
                            break;
                        default:
                            currentTable.setNumberPages(schemaResult.getLong(4));
                    }
                    currentTable.setFields(this.getColumns(currentTable.getSchema(), currentTable.getName()));
                    log.msg("Table: " + currentTable.getName());
                    log.msg("Fields: " + currentTable.getFieldsString());
                    schema.tables.add(currentTable);
                }
                schemaResult.close();
            }
            log.endTitle();
        } catch (SQLException e) {
            log.error(e);
        }
    }

    public Schema getSchemaDataBase() {
        return this.schema;
    }

    private ArrayList<Column> getColumns(String schema, String tableName) {
        ArrayList<Column> result = new ArrayList<>();
        try {
            String sql = config.getProperty("getSqlDetailsColumns" + config.getProperty("sgbd"));
            sql = sql.replace("$schema$", schema);
            sql = sql.replace("$table$", tableName);
            PreparedStatement preparedStatement = getConnection().prepareStatement(sql);
            ResultSet fields = getConnection().executeQuery(preparedStatement);
            if (fields != null) {
                while (fields.next()) {
                    Column currentColumn = new Column();
                    currentColumn.setOrder(fields.getInt("ordernum"));
                    currentColumn.setName(fields.getString("columnname"));
                    currentColumn.setTable(fields.getString("tablename"));
                    currentColumn.setNotNull(fields.getBoolean("isnull"));
                    currentColumn.setType(fields.getString("typefield"));
                    currentColumn.setDomainRestriction(fields.getString("domainrestriction"));
                    currentColumn.setPrimaryKey(fields.getBoolean("primarykey"));
                    currentColumn.setUniqueKey(fields.getBoolean("uniquekey"));
                    if (fields.getBoolean("foreignkey")) {
                        currentColumn.setForeignKey(this.getForeignKeyColumn(fields));
                    }
                    result.add(currentColumn);
                }
                fields.close();
                preparedStatement.close();
            }
        } catch (SQLException e) {
            log.error(e);
        }
        return result;
    }

    private Column getForeignKeyColumn(ResultSet field) {
        Column foreignColumn = new Column();
        try {
            foreignColumn.setOrder(field.getInt(9));
            foreignColumn.setName(field.getString(10));
            foreignColumn.setTable(field.getString(11));
            foreignColumn.setType(field.getString(12));
            foreignColumn.setDomainRestriction("");
            foreignColumn.setNotNull(true);
        } catch (SQLException ex) {
            log.error(ex);
        }
        return foreignColumn;
    }

    public void verifyDatabase() {
        try {
            String query = config.getProperty("signature") + config.getProperty("getSqlClauseToCaptureCurrentQueries" + config.getProperty("sgbd"));
            PreparedStatement preparedStatement = getConnection().prepareStatement(query);
            ResultSet queriesResult = getConnection().executeQuery(preparedStatement);
            this.lastcapturedSQL.clear();
            while (queriesResult.next()) {
                String currentSQL = queriesResult.getString("sql");
                if (this.isQueryValid(currentSQL) && queriesResult.getTimestamp("start_time") != null) {
                    SQL sql = new SQL();
                    sql.setPid(queriesResult.getString("pid"));
                    sql.setDatabase(queriesResult.getString("database_name"));
                    sql.setSql(currentSQL);
                    sql.setSchemaDataBase(this.schema);
                    sql.addExecution(captorPlan.getPlanExecution(sql.getSql(), queriesResult.getTimestamp("start_time")));
                    SQL temp = this.processAndgetCapturedSQLFromHistory(sql);
                    if (temp != null) {
                        this.lastcapturedSQL.add(temp);
                    }
                }
            }
            queriesResult.close();
            preparedStatement.close();
        } catch (SQLException e) {
            log.error(e);
        }
    }

    private SQL processAndgetCapturedSQLFromHistory(SQL sql) {
        for (SQL workload : capturedSQL) {
            if (sql.getSql().equals(workload.getSql())) {
                if (workload.isAreadyCaptured(sql)) {
                    return null;
                } else {
                    workload.setPid(sql.getPid());
                    workload.setExecutions(sql.getExecutions());
                    workload.setWaitAnalysis(true);
                    return workload;
                }
            }
        }
        sql.setSchemaDataBase(this.schema);
        sql.setId(this.capturedSQL.size() + 1);
        log.msg("CATCH: " + sql.getSql());
        this.capturedSQL.add(sql);
        return sql;
    }

    private boolean isQueryValid(String sql) {
        boolean isValid = true;
        if (this.badSQL.contains(sql)) {
            isValid = false;
        } else if ((sql.isEmpty())
                || (this.isQueryGeneratedByOuterTuning(sql))
                || (this.isSQLGeneratedBySGBD(sql))
                || (captorPlan.getEstimatedPlanExecution(sql).isEmpty())) {
            isValid = false;
            this.badSQL.add(sql);
        }
        return isValid;
    }

    private boolean isSQLGeneratedBySGBD(String query) {
        boolean isCommand = false;
        String[] wordsBySGBD = config.getProperty("wordsBySGBD").split(";");
        for (String word : wordsBySGBD) {
            if (query.toLowerCase(Locale.getDefault()).contains(word)) {
                isCommand = true;
                break;
            }
        }
        return isCommand;
    }

    private boolean isQueryGeneratedByOuterTuning(String query) {
        return query.toLowerCase().contains(config.getProperty("signature").toLowerCase());
    }

    public Iterable<SQL> getCapturedSQL() {
        return this.capturedSQL;
    }

    public Iterable<SQL> getLastcapturedSQL() {
        return this.lastcapturedSQL;
    }

    public SQL getSqlCaptured(String sqlCommand) {
        for (SQL sql : capturedSQL) {
            if (sql.getSql().toLowerCase().contains(sqlCommand.toLowerCase())) {
                return sql;
            }
        }
        return null;
    }

}
