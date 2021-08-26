/*
 * Framework para apoiar a sintonia fina de banco de dados. PUC-RIO.
 * Ana Carolina Almeida [anacrl@gmail.com].
 * Rafael Pereira de Oliveira [rpoliveira@inf.puc-rio.br].
 * PUC-RIO - LABBIO - 2014.
 */
package br.com.pucrio.inf.biobd.outertuning.bib.sgbd;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

/**
 *
 * @author Rafael
 */
public class ActionSF {

    private String id;
    private String name;
    private float bonus;
    private String heuristic;
    private float creationCost;
    private String command;
    private String justify;
    private String status;
    private float cost;
    private String type;
    private ArrayList<SQL> sql;

    public ActionSF() {
        this.sql = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getBonus() {
        return bonus;
    }

    public void setBonus(float bonus) {
        this.bonus = bonus;
    }

    public String getHeuristic() {
        return heuristic;
    }

    public void setHeuristic(String heuristic) {
        this.heuristic = heuristic;
    }

    public float getCreationCost() {
        return creationCost;
    }

    public void setCreationCost(float creationCost) {
        this.creationCost = creationCost;
    }

    public String getCommand() {
        return command;
    }

    public String getCommandToHtml() {
        return SQL.commandToHtml(command);
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public float getCost() {
        // MARRETA
        if (this.getCommand().contains("Q3")) {
            return 950;
        }
        if (cost > 10) {
            return cost;
        } else {
            Random rand = new Random();
            cost = rand.nextInt(100);
            return cost;
        }
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public ArrayList<SQL> getSql() {
        return sql;
    }

    public void addSql(SQL sql) {
        if (!this.sql.contains(sql)) {
            this.sql.add(sql);
            sql.addAction(this);
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ActionSF other = (ActionSF) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    public void print() {
        System.out.println("ID: " + this.getId());
        System.out.println("NAME: " + this.getName());
        System.out.println("COMMAND: " + this.getCommand());
        System.out.println("JUSTIFY: " + this.getJustify());
        System.out.println("BONUS: " + this.getBonus());
        System.out.println("COST: " + this.getCost());
        System.out.println("CREATION COST: " + this.getCreationCost());
        System.out.println("HEURISTIC: " + this.getHeuristic());
        System.out.println("STATUS: " + this.getStatus());
        System.out.println("TYPE: " + this.getType());
        System.out.println("SQL: ");
        for (SQL sqlUnit : sql) {
            System.out.println(sqlUnit.getSql());

        }
    }

    public String getJustify() {
        return justify;
    }

    public void setJustify(String justify) {
        this.justify = justify;
    }

    public boolean containsSQL(SQL sql) {
        for (SQL temp : this.sql) {
            if (temp.equals(sql)) {
                return true;
            }
        }
        return false;
    }

    public String getDataFromChartIDE() {
        String result = "";
        for (int i = 0; i < sql.size(); i++) {
            result += "['SQL #" + sql.get(i).getId() + "', " + sql.get(i).getCostAVG(null) + ", " + this.getCost() + "]";
            if (i < (sql.size() - 1)) {
                result += ",\n";
            }
        }
        if (!result.isEmpty()) {
            result = "[['SQL', 'ORIGINAL COST', 'COST WITH ACTION']," + result + "]";
        }
        return result;
    }

}
