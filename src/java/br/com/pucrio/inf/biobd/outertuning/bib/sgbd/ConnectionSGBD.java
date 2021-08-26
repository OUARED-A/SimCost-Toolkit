/*
 * Biblioteca de codigo fonte criada por Rafael Pereira
 * Proibido o uso sem autorizacao formal do autor
 *
 * rpoliveirati@gmail.com
 */
package br.com.pucrio.inf.biobd.outertuning.bib.sgbd;

import br.com.pucrio.inf.biobd.outertuning.bib.base.Log;
import br.com.pucrio.inf.biobd.outertuning.bib.configuration.Configuration;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionSGBD {

    private final Configuration config;
    private final Log log;

    public ConnectionSGBD() {
        this.config = new Configuration();
        this.log = new Log(this.config);
        connect();
    }

    protected static Connection connection = null;

    private void connect() {
        try {
            if (connection == null) {
                switch (config.getProperty("sgbd")) {
                    case "sqlserver":
                        connection = DriverManager.getConnection(config.getProperty("urlSGBD") + "databaseName=" + config.getProperty("databaseName") + ";", config.getProperty("userSGBD"), config.getProperty("pwdSGBD"));
                        break;
                    case "postgresql":
                        Class.forName("org.postgresql.Driver");
                        connection = DriverManager.getConnection(config.getProperty("urlSGBD") + config.getProperty("databaseName"), config.getProperty("userSGBD"), config.getProperty("pwdSGBD"));
                        break;
                    case "oracle":
                        Class.forName("oracle.jdbc.driver.OracleDriver");
                        connection = DriverManager.getConnection(config.getProperty("urlSGBD") + config.getProperty("databaseName"), config.getProperty("userSGBD"), config.getProperty("pwdSGBD"));
                        break;
                    default:
                        throw new UnsupportedOperationException("Atributo SGBD do arquivo de parametros (.properties) nao foi atribuido corretamente.");
                }
                log.msg("Conectado ao bd " + config.getProperty("urlSGBD") + ":" + config.getProperty("databaseName"));
                log.msg("Aplicação versão: " + config.getProperty("versao"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            log.error(e);
            System.exit(1);
        }
    }

    public void closeConnection() throws SQLException {
        ConnectionSGBD.connection.close();
    }

    public PreparedStatement prepareStatement(String query) {
        try {
            return ConnectionSGBD.connection.prepareStatement(config.getProperty("signature") + " " + query);
        } catch (SQLException e) {
            log.error(e);
            return null;
        }
    }

    public ResultSet executeQuery(String query) {
        try {
            Statement statement = ConnectionSGBD.connection.createStatement();
            statement.closeOnCompletion();
            return statement.executeQuery(config.getProperty("signature") + " " + query);
        } catch (SQLException e) {
            log.msg(query);
            log.msg(e);
            return null;
        }
    }

    public void executeUpdate(PreparedStatement prepared) {
        try {
            prepared.executeUpdate();
        } catch (SQLException e) {
            log.error(e);
        } finally {
            try {
                prepared.close();
            } catch (SQLException ex) {
                log.error(ex);
            }
        }
    }

    public void executeUpdate(String query) {
        PreparedStatement prepared = this.prepareStatement(query);
        try {
            prepared.executeUpdate();
        } catch (SQLException e) {
            log.msg("Query com erro:" + query);
            log.error(e);
        } finally {
            try {
                prepared.close();
            } catch (SQLException ex) {
                log.error(ex);
            }
        }
    }

    public ResultSet executeQuery(PreparedStatement prepared) {
        try {
            return prepared.executeQuery();
        } catch (SQLException e) {
            log.error(e);
            return null;
        }
    }

    public Statement getStatement() {
        try {
            Statement statement = ConnectionSGBD.connection.createStatement();
            statement.closeOnCompletion();
            return statement;
        } catch (SQLException ex) {
            log.error(ex);
            return null;
        }
    }

    public boolean isClosed() {
        try {
            return ConnectionSGBD.connection.isClosed();
        } catch (SQLException ex) {
            return true;
        }
    }
}
