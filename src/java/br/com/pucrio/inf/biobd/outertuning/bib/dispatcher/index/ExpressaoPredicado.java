package br.com.pucrio.inf.biobd.outertuning.bib.dispatcher.index;

import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Concept;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.FunctionConceptBase;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Property;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Relation;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Source;
import br.com.pucrio.inf.biobd.outertuning.bib.sgbd.Column;
import br.com.pucrio.inf.biobd.outertuning.bib.sgbd.SQL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.protege.owl.portability.query.Result;
import org.protege.owl.portability.query.ResultException;

/*
 * Framework para apoiar a sintonia fina de banco de dados. PUC-RIO.
 * Ana Carolina Almeida [anacrl@gmail.com].
 * Rafael Pereira de Oliveira [rpoliveira@inf.puc-rio.br].
 * PUC-RIO - LABBIO - 2014.
 */
/**
 *
 * @author Rafael
 */
public class ExpressaoPredicado extends FunctionConceptBase {

    @Override
    public ArrayList<Concept> getIndividualsForInstantiate(Source source) {
        return this.fcInstanciaExpressaoPredicado(source);
    }

    public ArrayList<Concept> fcInstanciaExpressaoPredicado(Source source) {
        this.list = new ArrayList<>();
        if (source.hasNotAnalyzed()) {
            try {
                String where = this.getWhere(source);
                Concept whereConcept = this.parseWhereToConcept(where);
                ArrayList<Concept> relationsWhere = new ArrayList<>();
                relationsWhere.add(whereConcept);
                if (!where.isEmpty()) {
                    ArrayList<String> expressions = this.getExpressionsPredicate(where);
                    for (String expression : expressions) {
                        Concept expressaoPredicado = new Concept();
                        expressaoPredicado.setClassName(source.getClassName());

                        for (Property prop : source.paramOut) {
                            if (prop.getDataType().equals("temValorPredicadoLiteral")) {
                                Property propCopy = new Property();
                                propCopy.copy(prop);
                                propCopy.setValue(expression);
                                expressaoPredicado.paramOut.add(propCopy);
                            }
                        }
                        expressaoPredicado.addRelation(new Relation(this.getColumnsObjectExpression(source, expression), "temObjetoExpressao"));
                        expressaoPredicado.addRelation(new Relation(relationsWhere, "eExpressaoLogicaDe"));
                        Token tokens = new Token();
                        expressaoPredicado.addRelation(new Relation(tokens.getTokensFromClause(where), "temToken"));
                        this.list.add(expressaoPredicado);
                    }
                }
            } catch (Error ex) {
                Logger.getLogger(Clausula.class.getName()).log(Level.SEVERE, null, ex);

            }
        }
        return list;
    }

    private String getWhere(Source source) {
        try {
            Result allComandoDML = (Result) source.paramIn;
            if (allComandoDML != null) {
                SQL work = source.getWorkload();
                while (allComandoDML.hasNext()) {
                    if (work.getSql().toLowerCase().contains(allComandoDML.getValue("?descWhere").toString().toLowerCase())) {
                        return allComandoDML.getValue("?descWhere").toString().trim();
                    }
                    allComandoDML.next();
                }
            }
        } catch (ResultException ex) {
            System.out.println(ex);
        }
        return "";
    }

    private ArrayList<String> getExpressionsPredicate(String where) {
        Combinacao combination = new Combinacao();
        return combination.dividirExpressaoPredicado(where);
    }

    private ArrayList<Concept> getColumnsObjectExpression(Source source, String expression) {
        ArrayList<Concept> colunas = new ArrayList<>();
        SQL workload = source.getWorkload();
        for (Column columnQuery : workload.getFieldsQuery()) {
            String[] words = expression.split(" ");
            if (words[0].equals(columnQuery.getName())) {
                colunas.add(this.parseColumnToConcept(columnQuery));
            }
        }
        return colunas;
    }

    private Concept parseColumnToConcept(Column column) {
        Concept columnConcept = new Concept();
        columnConcept.setClassName("Coluna");
        Property propCopy = new Property();
        propCopy.setDataType("temDescricao");
        propCopy.setValue(column.getName());
        columnConcept.paramOut.add(propCopy);
        return columnConcept;
    }

    private Concept parseWhereToConcept(String where) {
        Concept whereConcept = new Concept();
        whereConcept.setClassName("Where");
        Property propCopy = new Property();
        propCopy.setDataType("temDescricao");
        propCopy.setValue(where);
        whereConcept.paramOut.add(propCopy);
        return whereConcept;
    }

}
