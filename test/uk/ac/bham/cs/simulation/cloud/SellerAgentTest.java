package uk.ac.bham.cs.simulation.cloud;

import java.text.DecimalFormat;
import org.junit.Ignore;
import org.cloudbus.cloudsim.core.CloudSim;
import java.util.Calendar;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * This class belongs to the testing layer and contains methods to test
 * SellerAgent according to JUnit 4.5 specification.
 * @author Carlos Mera Gómez
 * @version 1.0, 01/08/2011
 */
public class SellerAgentTest
{

    public SellerAgentTest()
    {
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    @Before
    public void setUp() 
    {
         // First step: Initialize the CloudSim package. It should be called
        // before creating any entities.
        int num_user = 1;   // number of grid users
        Calendar calendar = Calendar.getInstance();
        boolean trace_flag = false;  // mean trace events

        // Initialize the CloudSim library
        CloudSim.init(num_user, calendar, trace_flag);
    }

    @After
    public void tearDown() 
    {
        CloudSim.abruptallyTerminate();
    }

    /**
     * Test of createSellerAgent method, of class SellerAgent.
     */
    @Test
    public void testCreateSellerAgent() throws Exception
    {
        System.out.println("createSellerAgent");
        SellerAgent result = SellerAgent.createSellerAgent("SellerAgent_A", 0.36, 0.30, 0.98, 250, SellerAgent.MEDIUM_REPUTATION, Region.NORTH);
        assertNotNull(result);
    }

    /**
     * Test of postOffer method, of class SellerAgent.
     */

    @Test
    public void testPostOffer() throws Exception
    {
        System.out.println("postOffer");
        long mi = 1;
        double referencePrice = 100.0;
        SellerAgent instance = SellerAgent.createSellerAgent("SellerAgent_A", 0.36, 0.30, 0.98, 250, SellerAgent.MEDIUM_REPUTATION, Region.NORTH);

        Double expResult = 1.36;
        Double result = instance.postOffer(mi, referencePrice);        
        assertEquals(expResult, result, 0.001);
    }

    /**
     * Test of makeBid method, of class SellerAgent.
     */
    //@Ignore
    @Test
    public void testMakeBid() throws Exception
    {
        System.out.println("makeBid");
        long mi = 1L;
        double referencePrice = 1.35;
        SellerAgent instance = SellerAgent.createSellerAgent("SellerAgent_A", 0.36, 0.30, 0.98, 250, SellerAgent.MEDIUM_REPUTATION, Region.NORTH);
        double expResult = 1.30;
        double result = instance.makeBid(mi, referencePrice);
        assertTrue(expResult<=result);
    }

   
}