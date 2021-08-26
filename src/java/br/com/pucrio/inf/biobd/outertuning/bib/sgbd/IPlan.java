/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
 */
package br.com.pucrio.inf.biobd.outertuning.bib.sgbd;

import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public interface IPlan {

    long getCost();

    long getNumRow();

    long getRowSize();

    ArrayList<SeqScan> getSeqScanOperations();

    float getDuration();

    String getPlanToViewHtml();

    void debug();

}
