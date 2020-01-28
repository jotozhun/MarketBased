package uk.ac.bham.cs.simulation.market;

import java.util.List;
import uk.ac.bham.cs.simulation.cloud.SLA;
import uk.ac.bham.cs.simulation.cloud.SellerAgent;

/**
 * This interface defines the basic method for the market mechanisms
 * @author  Carlos Mera Gómez
 * @version 1.0, 30/07/2011
 */
public interface MarketMechanism
{
   /**
     * 
     * @param mi
     * @param referencePrice
     * @param minimumReputation
     * @param sla
     * @return 
     */
   public SellerAgent giveBestSellerAgent(long mi, double referencePrice, Integer minimumReputation, SLA sla);

   /**
    * 
    * @param mi
    * @param referencePrice
    * @param minimumReputation
    * @param datacenterRequestedIdsList
    * @param sla
    * @return 
    */
   public SellerAgent giveBestSellerAgent(long mi, double referencePrice, Integer minimumReputation,
                                                List<Integer> datacenterRequestedIdsList, SLA sla);

   /**
    * 
    * @return 
    */
   public String getName();
   
   /**
    * 
    * @return 
    */
   public Double getWeightForReputation();
    
}
