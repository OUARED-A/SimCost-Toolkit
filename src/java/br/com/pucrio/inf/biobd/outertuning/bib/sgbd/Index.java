/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
 */
package br.com.pucrio.inf.biobd.outertuning.bib.sgbd;

import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author Rafael
 */
public class Index {

    public ArrayList<Column> columns;
    private String typeColumn;
    private String hypotheticalPlan;
    private String tableName;
    private long creationCost;
    private String indexType;
    private boolean hasFilter;
    private String filterType; // theta ou equi
    private long numberOfRows; //número de linhas do seq scan para o qual o índice foi gerado
    private int cidId;//identificador do índice
    private String indexName;//Nome do índice

    public String getHypotheticalPlan() {
        return hypotheticalPlan;
    }

    public void setHypotheticalPlan(String hypotheticalPlan) {
        this.hypotheticalPlan = hypotheticalPlan;
    }

    public String getTypeColumn() {
        return typeColumn;
    }

    public void setTypeColumn(String typeColumn) {
        this.typeColumn = typeColumn;
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName.trim();
    }

    /**
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName.trim();
    }

    /**
     * @return the creationCost
     */
    public long getCreationCost() {
        return creationCost;
    }

    /**
     * @param creationCost the creationCost to set
     */
    public void setCreationCost(long creationCost) {
        this.creationCost = creationCost;
    }

    /**
     * @return the IndexType
     */
    public String getIndexType() {
        return indexType;
    }

    /**
     * @param IndexType the IndexType to set
     */
    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    public Index() {
        this.columns = new ArrayList<>();
    }

    public String getName() {
        String indexName = tableName + "_" + indexType;
        for (int i = 0; i < columns.size(); i++) {
            indexName = indexName + "_" + columns.get(i).getName();
        }
        return indexName;

    }

    public String getSintaxe() {
        if (columns.size() > 0) {
            String columnsNames = "";
            String table = "";
            for (int i = 0; i < columns.size(); i++) {
                table = columns.get(i).getTable();
                if (i > 0) {
                    columnsNames += ",";
                }
                columnsNames += columns.get(i).getName();
            }
            return "DROP HYPOTHETICAL INDEX IF EXISTS " + this.getName() + "; CREATE HYPOTHETICAL INDEX " + this.getName() + " ON " + table + " (" + columnsNames + ");";
//            return "DROP INDEX IF EXISTS " + this.getName() + ";";
        }
        return "";
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.columns);
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
        final Index other = (Index) obj;
        if (!Objects.equals(this.columns, other.columns)) {
            return false;
        }
        return true;
    }

    /**
     * @return the hasFilter
     */
    public boolean getHasFilter() {
        return hasFilter;
    }

    /**
     * @param hasFilter the hasFilter to set
     */
    public void setHasFilter(boolean hasFilter) {
        this.hasFilter = hasFilter;
    }

    /**
     * @return the filterType
     */
    public String getFilterType() {
        return filterType;
    }

    /**
     * @param filterType the filterType to set
     */
    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    /**
     * @return the numberOfRows
     */
    public long getNumberOfRows() {
        return numberOfRows;
    }

    /**
     * @param numberOfRows the numberOfRows to set
     */
    public void setNumberOfRows(long numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    public void setColumns(ArrayList<Column> fields) {
        for (Column column : fields) {
            if (!this.containsColumns(column)) {
                this.columns.add(column);
            }
        }
    }

    private boolean containsColumns(Column field) {
        for (Column column : columns) {
            if (column.equals(field)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return the cidId
     */
    public int getCidId() {
        return cidId;
    }

    /**
     * @param cidId the cidId to set
     */
    public void setCidId(int cidId) {
        this.cidId = cidId;
    }

    /**
     * @return the indexName
     */
    public String getIndexName() {
        //return indexName;
        return this.getName();
    }

    /**
     * @param indexName the indexName to set
     */
    public void setIndexName(String indexName) {
        this.indexName = indexName.trim();
    }

}
