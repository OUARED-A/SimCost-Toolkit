/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.pucrio.inf.biobd.outertuning.bib.base;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Rafael
 */
public class IntervalList {

    public ArrayList<Interval> getIntervals(String stepSize) {
        return this.generateIntervals(stepSize, new Date());
    }

    public ArrayList<Interval> getIntervals(String stepSize, Date startingDate) {
        return this.generateIntervals(stepSize, startingDate);
    }

    private ArrayList<Interval> generateIntervals(String stepSize, Date startingDate) {
        Date end;
        Date ini = getDateIni(stepSize, startingDate);
        int step = this.getStep(stepSize);
        ArrayList<Interval> intervals = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            end = ini;
            ini = new Date(end.getTime() - step);
            intervals.add(new Interval(ini, end));
        }
        return intervals;
    }

    private int getStep(String step) {
        switch (step) {
            case "24h":
                return 86400000;
            case "12h":
                return 43200000;
            case "6h":
                return 21600000;
            case "1h":
                return 3600000;
            case "30min":
                return 1800000;
            case "10min":
                return 600000;
            case "5min":
                return 300000;
            case "1min":
                return 60000;
        }
        return 0;
    }

    private Date getDateIni(String step, Date startingDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startingDate);
        switch (step) {
            case "24h":
                cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                break;
            case "12h":
                int time = cal.get(Calendar.HOUR_OF_DAY);
                if (time >= 12) {
                    cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                } else {
                    cal.set(Calendar.HOUR_OF_DAY, 12);
                    cal.set(Calendar.MINUTE, 0);
                }
                break;
            case "6h":
                cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + 1);
                cal.set(Calendar.MINUTE, 0);
                break;
            case "1h":
                cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + 1);
                cal.set(Calendar.MINUTE, 0);
                break;
            case "30min":
                if (cal.get(Calendar.MINUTE) > 30) {
                    cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + 1);
                    cal.set(Calendar.MINUTE, 0);
                } else {
                    cal.set(Calendar.MINUTE, 30);
                }
                break;
            case "10min":
                if (cal.get(Calendar.MINUTE) % 10 != 0) {
                    cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + (10 - cal.get(Calendar.MINUTE) % 10));
                } else {
                    cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + 10);
                }
                break;
            case "5min":
                if (cal.get(Calendar.MINUTE) % 5 != 0) {
                    cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + (5 - cal.get(Calendar.MINUTE) % 5));
                } else {
                    cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + 5);
                }
                break;
            case "1min":
                cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + 1);
                break;
        }
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

}
