/*
 * Outer-Tuning - Framework para apoiar a sintonia fina de banco de dados. PUC-RIO.
 * Rafael Pereira de Oliveira [rpoliveira@inf.puc-rio.br].
 * Ana Carolina Almeida [anacrl@gmail.com].
 * Sergio Lifschitz [sergio@inf.puc-rio.br].
 * PUC-RIO - BioBD.
 */
package br.com.pucrio.inf.biobd.outertuning.agents;

import br.com.pucrio.inf.biobd.outertuning.bib.base.Interval;
import br.com.pucrio.inf.biobd.outertuning.bib.base.IntervalList;
import br.com.pucrio.inf.biobd.outertuning.bib.ontology.Heuristic;
import br.com.pucrio.inf.biobd.outertuning.bib.sgbd.ActionSF;
import br.com.pucrio.inf.biobd.outertuning.bib.sgbd.Plan;
import br.com.pucrio.inf.biobd.outertuning.bib.sgbd.SQL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author Rafael
 */
public class CoordenatorAgent {

    private final CopyOnWriteArrayList<SQL> lastSQLCaptured;
    private Thread threadAgentTuning;
    public OuterTuningAgent OTAgent;
    public boolean running = false;

    public CoordenatorAgent() {
        this.lastSQLCaptured = new CopyOnWriteArrayList<>();
        this.OTAgent = new OuterTuningAgent();
    }

    public void startCaptureWorkload() {
        this.OTAgent.initialize(lastSQLCaptured);
        if (this.threadAgentTuning == null) {
            this.threadAgentTuning = new Thread(OTAgent);
            this.threadAgentTuning.start();
            this.running = true;
        }
    }

    public String capturedQueryByWindow(String windowSize) {
        String dataLine = "";
        ArrayList<SQL> sqlIn = new ArrayList<>();
        IntervalList list = new IntervalList();
        ArrayList<Interval> inter = list.getIntervals(windowSize);
        float duration;
        float cost;
        int count;
        for (int i = inter.size() - 1; i >= 0; i--) {
            dataLine += ",\n['" + inter.get(i).getIni(this.getMaskByWindowSize(windowSize)) + "'";

            Iterator<SQL> itrSQL = lastSQLCaptured.iterator();
            while (itrSQL.hasNext()) {
                SQL sql = itrSQL.next();
                duration = 0;
                cost = 0;
                count = 0;
                Iterator<Plan> itr = sql.getExecutions().iterator();
                while (itr.hasNext()) {
                    Plan execution = itr.next();
                    if (inter.get(i).isBetween(execution.getDateExecution()) && execution.getDuration() > 0) {
                        if (!sqlIn.contains(sql)) {
                            sqlIn.add(sql);
                        }
                        duration += execution.getDuration();
                        cost += execution.getCost();
                        count++;
                    }
                }
                if (count == 0) {
                    dataLine += ",0";
                } else {
                    dataLine += "," + duration;
                }
                dataLine += ",'<b>SQL #" + sql.getId() + "</b><br>  ";
                dataLine += "Execution(s): <b>" + count + "</b><br>";
                dataLine += "Total Time: <b>" + formatDecimalIDE(duration) + "s</b><br> ";
                dataLine += "Total cost: <b>" + formatDecimalIDE(cost) + "</b>'";
            }
            dataLine += "]";
        }

        String firstLine = "['TIME'";
        for (SQL sql : lastSQLCaptured) {
            firstLine += ",'SQL #" + sql.getId() + "'";
            firstLine += ",{type: 'string', role: 'tooltip', 'p': {'html': true}}";
        }

        if (sqlIn.isEmpty()) {
            dataLine = "";
            firstLine = "['TIME'";
            firstLine += ",'empty'";
            for (int i = inter.size() - 1; i >= 0; i--) {
                dataLine += ",\n['" + inter.get(i).getIni(this.getMaskByWindowSize(windowSize)) + "',0]";
            }
        }
        firstLine += "]";
        return firstLine + dataLine;
    }

    public ArrayList<SQL> getSQLbyId(int id) {
        ArrayList<SQL> sqlIn = new ArrayList<>();
        if (this.lastSQLCaptured.size() >= id) {
            sqlIn.add(this.lastSQLCaptured.get(id - 1));
        }
        return sqlIn;
    }

    public ArrayList<SQL> getSQLbyWindow(String windowSize, String windowSelected) {
        ArrayList<SQL> sqlIn = new ArrayList<>();
        Interval intervalSelected = this.getIntervalAsked(windowSize, windowSelected);
        Iterator<SQL> itrSQL = lastSQLCaptured.iterator();
        while (itrSQL.hasNext()) {
            SQL sql = itrSQL.next();
            for (Plan execution : sql.getExecutions()) {
                if (intervalSelected != null && intervalSelected.isBetween(execution.getDateExecution()) && execution.getDuration() > 0) {
                    if (!sqlIn.contains(sql)) {
                        sqlIn.add(sql);
                    }
                }
            }
        }
        return sqlIn;
    }

    public Interval getIntervalAsked(String windowSize, String windowSelected) {
        Interval intervalSelected = null;
        IntervalList list = new IntervalList();
        ArrayList<Interval> inter = list.getIntervals(windowSize);
        for (Interval interval : inter) {
            if (interval.getIni(this.getMaskByDate(windowSelected)).equals(windowSelected)) {
                intervalSelected = interval;
                break;
            }
        }
        return intervalSelected;
    }

    private String getMaskByDate(String windowSelected) {
        if (windowSelected.contains("/")) {
            return "dd/MM HH:mm";
        } else {
            return "HH:mm";
        }
    }

    private String getMaskByWindowSize(String windowSize) {
        if (windowSize.contains("h")) {
            return "dd/MM HH:mm";
        } else {
            return "HH:mm";
        }
    }

    public ArrayList<Heuristic> getHeuristicsFromOntology() {
        return this.OTAgent.getAllHeuristics();
    }

    public void setSelectedHeuristics(Heuristic heuristics) {
        this.OTAgent.selectedHeuristics.add(heuristics);
    }

    public CopyOnWriteArrayList<Heuristic> getSelectedHeuristics() {
        return this.OTAgent.selectedHeuristics;
    }

    public String formatDecimalIDE(double number) {
        DecimalFormat formatter = new DecimalFormat("###,###.##", new DecimalFormatSymbols(new Locale("pt", "BR")));
        return formatter.format(number);
    }

    public ActionSF getActionSFById(String actionID) {
        for (SQL sql : lastSQLCaptured) {
            for (ActionSF actionSF : sql.getActionsSF()) {
                if (actionSF.getId().equals(actionID)) {
                    return actionSF;
                }
            }
        }
        return null;
    }

    public String getActionsFromChart() {
        ArrayList<String> result = new ArrayList<>();
        for (SQL sql : lastSQLCaptured) {
            for (ActionSF actionSF : sql.getActionsSF()) {
                String actionTemp = "['" + actionSF.getId() + "', " + actionSF.getBonus() + ", " + actionSF.getCreationCost() + ", '" + actionSF.getType() + "', " + actionSF.getSql().size() + "]";
                if (!result.contains(actionTemp)) {
                    result.add(actionTemp);
                }
            }
        }
        String toChart = "";
        for (int i = 0; i < result.size(); i++) {
            toChart += result.get(i);
            if (i < (result.size() - 1)) {
                toChart += ",";
            }
        }
        toChart = "['ACTION_ID', 'Gain Expectancy', 'Creation Cost', 'Type', 'N. of SQL Serviced'], " + toChart;
        return toChart;
    }

    public boolean isRunning() {
        return this.running;
    }
}
