/*
 * Framework para apoiar a sintonia fina de banco de dados. PUC-RIO.
 * Ana Carolina Almeida [anacrl@gmail.com].
 * Rafael Pereira de Oliveira [rpoliveira@inf.puc-rio.br].
 * PUC-RIO - LABBIO - 2014.
 */
package br.com.pucrio.inf.biobd.outertuning.bib.dispatcher.index;

import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Concept;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Property;
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public class Token extends ObjetoSGBD {

    private String description;
    private String simbol;
    private String className;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSimbol() {
        return simbol;
    }

    public void setSimbol(String simbol) {
        this.simbol = simbol;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public ArrayList<Token> getTokens() {
        ArrayList<Token> tokens = new ArrayList<>();
        Token e = new Token();
        e.setSimbol(" and ");
        e.setDescription("and");
        e.setClassName("ConectorLogico");
        tokens.add(e);

        Token ou = new Token();
        ou.setSimbol(" or ");
        ou.setDescription("or");
        ou.setClassName("ConectorLogico");
        tokens.add(ou);

        Token menorQue = new Token();
        menorQue.setClassName("OperadorComparacao");
        menorQue.setDescription("menor que");
        menorQue.setSimbol("<");
        tokens.add(menorQue);

        Token menorOuIgualQue = new Token();
        menorOuIgualQue.setClassName("OperadorComparacao");
        menorOuIgualQue.setDescription("menor ou igual que");
        menorOuIgualQue.setSimbol("<=");
        tokens.add(menorOuIgualQue);

        Token maiorQue = new Token();
        maiorQue.setClassName("OperadorComparacao");
        maiorQue.setDescription("maior que");
        maiorQue.setSimbol(">");
        tokens.add(maiorQue);

        Token maiorOuIgualQue = new Token();
        maiorOuIgualQue.setClassName("OperadorComparacao");
        maiorOuIgualQue.setDescription("maior ou igual que");
        maiorOuIgualQue.setSimbol(">=");
        tokens.add(maiorOuIgualQue);

        Token igual = new Token();
        igual.setClassName("OperadorComparacao");
        igual.setDescription("igual a");
        igual.setSimbol("=");
        tokens.add(igual);

        Token like = new Token();
        like.setClassName("OperadorComparacao");
        like.setDescription("similar a");
        like.setSimbol("like");
        tokens.add(like);

        Token in = new Token();
        in.setClassName("OperadorComparacao");
        in.setDescription("no intervalo");
        in.setSimbol("in");
        tokens.add(in);

        Token between = new Token();
        between.setClassName("OperadorComparacao");
        between.setDescription("entre");
        between.setSimbol("between");
        tokens.add(between);

        Token diferente = new Token();
        diferente.setClassName("OperadorComparacao");
        diferente.setDescription("diferente de");
        diferente.setSimbol("<>");
        tokens.add(diferente);

        return tokens;
    }

    public Concept parseTokenToConcept() {
        Concept tokenConcept = new Concept();
        tokenConcept.setClassName(this.getClassName());
        if (this.getClassName().equals("OperadorComparacao")) {
            Property temSimbolo = new Property();
            temSimbolo.setDataType("temSimbolo");
            temSimbolo.setValue(this.getSimbol());
            tokenConcept.paramOut.add(temSimbolo);
        }
        Property propDescription = new Property();
        propDescription.setDataType("temDescricao");
        propDescription.setValue(this.getDescription());
        tokenConcept.paramOut.add(propDescription);
        return tokenConcept;
    }

    public ArrayList<Concept> getTokensFromClause(String clause) {
        ArrayList<Token> tokens = this.getTokens();
        ArrayList<Concept> tokensConcept = new ArrayList<>();
        for (Token token : tokens) {
            if (clause.toLowerCase().contains(token.getSimbol().toLowerCase())) {
                tokensConcept.add(token.parseTokenToConcept());
            }
        }
        return tokensConcept;
    }

}
