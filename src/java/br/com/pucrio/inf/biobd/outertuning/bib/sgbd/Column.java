/*
 * Framework para apoiar a sintonia fina de banco de dados. PUC-RIO.
 * Ana Carolina Almeida [anacrl@gmail.com].
 * Rafael Pereira de Oliveira [rpoliveira@inf.puc-rio.br].
 * PUC-RIO - LABBIO - 2014.
 */
package br.com.pucrio.inf.biobd.outertuning.bib.sgbd;

import java.util.Objects;

/**
 *
 * @author Rafael
 */
public class Column {

    private String name;
    private Column foreignKey;
    private boolean primaryKey;
    private boolean uniqueKey;
    private String table;
    private boolean notNull;
    private String type;
    private int order;
    private String domainRestriction;

    public String getDomainRestriction() {
        if (domainRestriction == null || domainRestriction.isEmpty()) {
            return "null";
        } else {
            return domainRestriction;
        }
    }

    public void setDomainRestriction(String domainRestriction) {
        this.domainRestriction = domainRestriction;
    }

    public boolean isUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(boolean uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public String getCompleteName() {
        return (table + "." + name).toLowerCase().trim();
    }

    public void setName(String name) {
        this.name = name.trim().toLowerCase();
    }

    public Column getForeignKey() {
        return foreignKey;
    }

    public void setForeignKey(Column foreignKey) {
        this.foreignKey = foreignKey;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table.toLowerCase().trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        int hash = 5;
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
        final Column other = (Column) obj;
        if (!Objects.equals(this.name.toLowerCase(), other.name.toLowerCase())) {
            return false;
        }
        if (!Objects.equals(this.table.toLowerCase(), other.table.toLowerCase())) {
            return false;
        }
        return this.order == other.order;
    }

    public Object getValue(String dataType) {
        switch (dataType) {
            case "temNome":
                return this.getCompleteName();
            case "temOrdem":
                return this.getOrder();
            case "temTipo":
                return this.getType();
            case "temRestricaoDominio":
                return this.getDomainRestriction();
            default:
                return null;
        }
    }

}
