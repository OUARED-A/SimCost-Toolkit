/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
 */
package br.com.pucrio.inf.biobd.outertuning.bib.sgbd;

/**
 *
 * @author josemariamonteiro
 */
public class Filter extends Column {

    //private Column column;
    private String filterType;// theta ou equi

    //public Filter(Column column, String filterType){
    //    this. = column;
    //    this.filterType = filterType;
    //}
    /**
     * @return the column
     */
    /*
     public Column getColumn() {
     return column;
     }
     */
    /*
     public Filter() {
     throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
     }
     */
    /**
     * @param column the column to set
     */
    /*
     public void setColumn(Column column) {
     this.column = column;
     }
     */
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

}
