package br.com.pucrio.inf.biobd.outertuning.bib.base;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Rafael
 */
public class Interval {

    private final Date ini;
    private final Date end;

    public Interval(Date ini, Date end) {
        this.ini = ini;
        this.end = end;
    }

    public boolean isBetween(Date date) {
        return !(end == null || ini == null || date == null)
                && date.before(end)
                && date.after(ini);
    }

    public Date getIni() {
        return ini;
    }

    public String getIni(String format) {
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        return fmt.format(this.ini);
    }

    public String getEnd(String format) {
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        return fmt.format(this.end);
    }

    public Date getEnd() {
        return end;
    }

}
