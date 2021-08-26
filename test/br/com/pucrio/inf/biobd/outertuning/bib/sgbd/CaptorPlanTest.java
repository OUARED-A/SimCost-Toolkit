/*
 * Framework para apoiar a sintonia fina de banco de dados. PUC-RIO.
 * Ana Carolina Almeida [anacrl@gmail.com].
 * Rafael Pereira de Oliveira [rpoliveira@inf.puc-rio.br].
 * PUC-RIO - LABBIO - 2014.
 */
package br.com.pucrio.inf.biobd.outertuning.bib.sgbd;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Rafael
 */
public class CaptorPlanTest {

    private CaptorPlan captorPlan;
    private List<String> sqls;
    private Plan plan;

    @Before
    public void reset() {
        sqls = new ArrayList<>();
        sqls.add("/*TST*/ /* TPC-H/TPC-R Forecasting Revenue Change Query (Q6.3) test of a materialized view */ select sum(l_extendedprice * l_discount) as revenue from h_lineitem where l_shipdate >= date '1994-03-01' and l_discount between 0.03 - 0.01 and 0.03 + 0.01");
        sqls.add("/*TST*/ select sum(l_extendedprice * l_discount) as revenue, l_shipdate from h_lineitem  group by  l_shipdate ");
    }

    @Test
    public void testCardinality() {
        for (String sql : sqls) {
            this.captorPlan = new CaptorPlan();
            this.plan = captorPlan.getPlanExecution(sql, getNewTimestamp());
            if (plan.getNumRow() > 0) {
                assertTrue(true);
            } else {
                plan.debug();
                assertTrue(false);
            }
        }
    }

    @Test
    public void testDuration() {
        for (String sql : sqls) {
            this.captorPlan = new CaptorPlan();
            this.plan = captorPlan.getPlanExecution(sql, getNewTimestamp());
            if (plan.getDuration() >= 0) {
                assertTrue(true);
            } else {
                plan.debug();
                assertTrue(false);
            }
        }
    }

    private Timestamp getNewTimestamp() {
        Date date = new Date();
        long time = date.getTime();
        return new Timestamp(time);
    }
}
