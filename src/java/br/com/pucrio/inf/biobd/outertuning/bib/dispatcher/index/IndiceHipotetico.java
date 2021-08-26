/*
 * Framework para apoiar a sintonia fina de banco de dados. PUC-RIO.
 * Ana Carolina Almeida [anacrl@gmail.com].
 * Rafael Pereira de Oliveira [rpoliveira@inf.puc-rio.br].
 * PUC-RIO - LABBIO - 2014.
 */
package br.com.pucrio.inf.biobd.outertuning.bib.dispatcher.index;

import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Concept;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.FunctionConceptBase;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.PlanoExecucaoHipotetico;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Property;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Relation;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Source;
import br.com.pucrio.inf.biobd.outertuning.bib.sgbd.Column;
import br.com.pucrio.inf.biobd.outertuning.bib.sgbd.ConnectionSGBD;
import br.com.pucrio.inf.biobd.outertuning.bib.sgbd.Index;
import br.com.pucrio.inf.biobd.outertuning.bib.sgbd.SQL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public class IndiceHipotetico extends FunctionConceptBase {

    /* 1. EQ: colunas envolvidas em predicados de igualdade; */
    private ArrayList<Index> EQ;
    /* 2. O: colunas envolvidas em cláusulas ORDER BY, GROUP BY e predicados de junção; */
    private ArrayList<Index> O;
    /* 3. RANGE: colunas que aparecem em restrições de intervalos; */
    private ArrayList<Index> RANGE;
    /* 3. 4. SARG: colunas que aparecem em outros predicados indexáveis (por exemplo, like). Este uso de colunas remete ao conceito de predicados de busca (sargable predicates);*/
    private ArrayList<Index> SARG;
    /* 5. REF: demais colunas referenciadas no comando SQL. */
    private ArrayList<Index> REF;

    private SQL work;

    @Override
    public ArrayList<Concept> getIndividualsForInstantiate(Source source) {
        return this.fcInstanciaIndiceHipotetico(source);
    }

    public ArrayList<Concept> fcInstanciaIndiceHipotetico(Source source) {
        this.list = new ArrayList<>();
        if (source.hasNotAnalyzed()) {
            this.setWork(source.getWorkload());
            ArrayList<Index> indexAll = new ArrayList<>();
            ArrayList<Index> indexAllInverso = new ArrayList<>();
            indexAll.addAll(this.combineArrayIndex(this.getEQ(), this.getO(), "EQ_O"));
            indexAll.addAll(this.combineArrayIndex(indexAll, this.getRANGE(), "EQ_O_RANGE"));
            indexAll.addAll(this.combineArrayIndex(indexAll, this.getSARG(), "EQ_O_RANGE_SARG"));
            indexAllInverso.addAll(this.combineArrayIndex(this.getO(), this.getEQ(), "O_EQ"));
            indexAllInverso.addAll(this.combineArrayIndex(indexAll, this.getRANGE(), "O_EQ_RANGE"));
            indexAllInverso.addAll(this.combineArrayIndex(indexAll, this.getSARG(), "O_EQ_RANGE_SARG"));
            indexAll.addAll(indexAllInverso);
            indexAll.addAll(this.getEQ());
            indexAll.addAll(this.getO());
            indexAll.addAll(this.getRANGE());
            indexAll.addAll(this.getSARG());
            indexAll.addAll(this.getREF());
            ArrayList<Index> result = new ArrayList<>();
            for (Index index : indexAll) {
                if (index != null && !result.contains(index)) {
                    result.add(index);
                    this.list.add(this.parseIndexToConcept(index, source));
                }
            }
            SQL workTemp = new SQL();
            workTemp.setSql(work.getSql());
            ArrayList<Concept> planos = this.getPlanoExecucaoConcept(workTemp);
            for (Concept concept : this.list) {
                concept.addRelation(new Relation(planos, "produz"));
            }
        }
        return this.list;
    }

    private ArrayList<Concept> getPlanoExecucaoConcept(SQL workload) {
        PlanoExecucaoHipotetico plano = new PlanoExecucaoHipotetico();
        return plano.getPlanoExecucaoHipoteticoForRelation(workload);
    }

    private Concept parseIndexToConcept(Index index, Source source) {
        if (index != null) {
            this.createIndexOnDataBase(index);
            Concept indexConcept = new Concept();
            indexConcept.setClassName("IndiceHipotetico");
            for (Property param : source.paramOut) {
                Property paramCopy = new Property();
                switch (param.getDataType()) {
                    case "temDescricao":
                        paramCopy.copy(param);
                        paramCopy.setValue(index.getSintaxe());
                        indexConcept.paramOut.add(paramCopy);
                        break;
                    case "temNome":
                        paramCopy.copy(param);
                        paramCopy.setValue(index.getName());
                        indexConcept.paramOut.add(paramCopy);
                        break;
                }

            }
            indexConcept.addRelation(new Relation(this.getConceptColumn(index.columns), "atuaSobre"));
            return indexConcept;
        } else {
            return null;
        }
    }

    private void createIndexOnDataBase(Index index) {
        try {
            ConnectionSGBD driver = new ConnectionSGBD();
            PreparedStatement preparedStatement = driver.prepareStatement(index.getSintaxe());
            preparedStatement.execute();
        } catch (SQLException ex) {
            log.error(ex);
        }
    }

    private ArrayList<Index> combineArrayIndex(ArrayList<Index> a, ArrayList<Index> b, String name) {
        ArrayList<Index> resultOfMerge = new ArrayList<>();
        if (a.isEmpty()) {
            resultOfMerge.addAll(b);
        } else if (b.isEmpty()) {
            resultOfMerge.addAll(a);
        }
        for (Index out : a) {
            for (Index in : b) {
                if (out != null && in != null && !out.equals(in)) {
                    resultOfMerge.add(combineIndex(out, in, name));
                }
            }
        }
        return resultOfMerge;
    }

    private Index combineIndex(Index a, Index b, String type) {
        if (a != null && b != null) {
            Index merge = new Index();
            for (Column columnA : a.columns) {
                for (Column columnB : b.columns) {
                    if (columnA.getTable().equals(columnB.getTable()) && !columnA.equals(columnB)) {
                        merge.columns.add(columnA);
                        merge.columns.add(columnB);
                    }
                }
            }
            merge.setTypeColumn(type);
            return merge;
        } else {
            return null;

        }
    }

    public ArrayList<Index> getEQ() {
        if (this.EQ == null) {
            this.EQ = this.getIndex("EQ");
        }
        return EQ;
    }

    public ArrayList<Index> getO() {
        if (this.O == null) {
            this.O = this.getIndex("O");
        }
        return O;
    }

    public ArrayList<Index> getRANGE() {
        if (this.RANGE == null) {
            this.RANGE = this.getIndex("RANGE");
        }
        return RANGE;
    }

    public ArrayList<Index> getSARG() {
        if (this.SARG == null) {
            this.SARG = this.getIndex("SARG");
        }
        return SARG;
    }

    public ArrayList<Index> getREF() {
        if (this.REF == null) {
            this.REF = this.getIndex("REF");
        }
        return REF;
    }

    public void setWork(SQL work) {
        this.work = work;
    }

    private ArrayList<Index> getIndex(String typeColumn) {
        String clauses = this.getClauseFromExtractIndex(typeColumn);
        String[] words = clauses.split(" ");
        ArrayList<Index> indexes = new ArrayList<>();
        for (int i = 1; i < words.length - 1; i++) {
            if (this.validKeyWord(words[i], typeColumn)) {
                words[i] = words[i].replace(",", "");
                indexes.add(this.getIndexObj(words[i - 1], typeColumn));
                indexes.add(this.getIndexObj(words[i], typeColumn));
                indexes.add(this.getIndexObj(words[i + 1], typeColumn));
            }
        }
        return indexes;
    }

    private Index getIndexObj(String columnName, String typeColumn) {
        Column column = this.work.getFielFromQuery(columnName);
        if (column != null) {
            Index index = new Index();
            index.columns.add(column);
            index.setTypeColumn(typeColumn);
            return index;
        } else {
            return null;
        }

    }

    private String getClauseFromExtractIndex(String typeColumn) {
        switch (typeColumn) {
            case "EQ":
                return this.work.getClauseFromSql("where");
            case "O":
                String clauses = this.work.getClauseFromSql("order by");
                clauses += " " + this.work.getClauseFromSql("group by");
                return clauses;
            case "RANGE":
            case "SARG":
                return this.work.getClauseFromSql("where");
            case "REF":
                return this.work.getSql();
        }
        return null;
    }

    private boolean validKeyWord(String word, String typeColumn) {
        switch (typeColumn) {
            case "EQ":
                return word.equals("=");
            case "O":
            case "REF":
                return true;
            case "RANGE":
                return (word.equals(">") || word.equals(">=") || word.equals("<") || word.equals("<=") || word.equals("between"));
            case "SARG":
                return (word.equals("like"));
        }
        return false;
    }

    private ArrayList<Concept> getConceptColumn(ArrayList<Column> columns) {
        ArrayList<Concept> conceptsColumn = new ArrayList<>();

        for (Column column : columns) {
            Concept columnConcept = new Concept();
            columnConcept.setClassName("Coluna");
            Property propperty = new Property();
            propperty.setDataType("temNome");
            propperty.setValue(column.getCompleteName());
            columnConcept.paramOut.add(propperty);
            conceptsColumn.add(columnConcept);
        }
        return conceptsColumn;
    }

}
