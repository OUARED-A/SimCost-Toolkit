/*
 * Framework para apoiar a sintonia fina de banco de dados. PUC-RIO.
 * Ana Carolina Almeida [anacrl@gmail.com].
 * Rafael Pereira de Oliveira [rpoliveira@inf.puc-rio.br].
 * PUC-RIO - LABBIO - 2014.
 */
package br.com.pucrio.inf.biobd.outertuning.bib.sgbd;

import java.util.concurrent.CopyOnWriteArrayList;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Rafael
 */
public class CaptorWorkloadTest {

    private CaptorWorkload captor;
    private CopyOnWriteArrayList<SQL> lastSQLCaptured;

    @Before
    public void reset() {
        this.lastSQLCaptured = new CopyOnWriteArrayList<>();
        captor = new CaptorWorkload(lastSQLCaptured);
    }

    @Test
    public void testCaptor() {
        captor.verifyDatabase();
        for (SQL sql : lastSQLCaptured) {
            sql.debug();
        }
        System.out.println(this.lastSQLCaptured.size());
        assertTrue(this.lastSQLCaptured.size() > 0);
    }

    @Test
    public void testDuration() {
    }
}
