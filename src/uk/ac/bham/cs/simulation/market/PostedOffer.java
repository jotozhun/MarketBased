package uk.ac.bham.cs.simulation.market;

import java.util.ArrayList;
import java.util.List;
import uk.ac.bham.cs.simulation.cloud.Region;
import uk.ac.bham.cs.simulation.cloud.SellerAgent;
import uk.ac.bham.cs.simulation.cloud.SLA;

/**
 * This class implements a poster offer mechanism for resource allocation
 * @author  Carlos Mera Gómez
 * @version 1.0, 17/07/2011
 */
public class PostedOffer implements MarketMechanism
{
    private static double reputationWeight;
    public static final String NAME = "Posted Offer";

    /**
     * 
     * @param weightForReputation 
     */
    public PostedOffer(Double weightForReputation)
    {
        initTradingMechanism(weightForReputation);
    }
    
    /**
     * 
     * @param weightForReputation 
     */
    private void initTradingMechanism(Double weightForReputation)
    {
        reputationWeight = weightForReputation;
    }

    /**
     * 
     * @param mi
     * @param referencePrice
     * @param minimumReputation
     * @param sla
     * @return 
     */
    public SellerAgent giveBestSellerAgent(long mi, double referencePrice, Integer minimumReputation, SLA sla)
    {
        SellerAgent bestSeller = giveBestSellerAgentPerRegion(mi, referencePrice, minimumReputation, 
                                                                sla, sla.getRegion(), new ArrayList<Integer>());               
        List<Region> otherRegions =  Region.getOtherRegions(sla.getRegion());
        int i = 0;
        while(bestSeller==null && i < otherRegions.size())
        {
            bestSeller = giveBestSellerAgentPerRegion(mi, referencePrice, minimumReputation, sla, otherRegions.get(i), new ArrayList<Integer>());
            i++;
        }
        return bestSeller;
    }

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
                                                List<Integer> datacenterRequestedIdsList, SLA sla)
    {
        SellerAgent bestSeller = giveBestSellerAgentPerRegion(mi, referencePrice, minimumReputation,
                                                                sla, sla.getRegion(), datacenterRequestedIdsList);

        List<Region> otherRegions =  Region.getOtherRegions(sla.getRegion());
        int i = 0;
        while(bestSeller==null && i < otherRegions.size())
        {
            bestSeller = giveBestSellerAgentPerRegion(mi, referencePrice, minimumReputation, sla, otherRegions.get(i), datacenterRequestedIdsList);
            i++;
        }        
        return bestSeller;
    }

    /**
     * 
     * @param bid
     * @param sellerAgent
     * @param sla
     * @return 
     */
    private static double ponderBidWithReputation(double bid, SellerAgent sellerAgent , SLA sla)
    {
        double increasingPriceFactor = 0;
        int reputation = sellerAgent.getReputation();
        if(sla.getPriority().equals(SLA.Priority.HIGH))
        {            
            increasingPriceFactor = reputationWeight * sla.getReliability().getValue() * sellerAgent.getFailedJobsRate() * (SellerAgent.MAXIMUM_REPUTATION - reputation)/SellerAgent.MAXIMUM_REPUTATION;
        }
        else if (sla.getPriority().equals(SLA.Priority.MEDIUM))
        {
            increasingPriceFactor = reputationWeight * sla.getReliability().getValue() * sellerAgent.getFailedJobsRate() * (SellerAgent.MAXIMUM_REPUTATION - reputation)/SellerAgent.MAXIMUM_REPUTATION;
        }
        else //LOW
        {
            increasingPriceFactor = reputationWeight * sla.getReliability().getValue() * sellerAgent.getFailedJobsRate() * (SellerAgent.MAXIMUM_REPUTATION - reputation)/SellerAgent.MAXIMUM_REPUTATION;
        }

        double ponderedBid =  bid * (1 + increasingPriceFactor);        
        return ponderedBid;
    }
    
    /**
     * 
     * @param mi
     * @param referencePrice
     * @param minimumReputation
     * @param sla
     * @param region
     * @param datacenterRequestedIdsList
     * @return 
     */
    private SellerAgent giveBestSellerAgentPerRegion(long mi, double referencePrice, Integer minimumReputation,
                                                    SLA sla, Region region, List<Integer> datacenterRequestedIdsList)
    {
        SellerAgent bestSeller = null;
        double lowestBid = -1;

        List<SellerAgent> observers = TradingMechanism.getSellerAgents(region);

        for (SellerAgent sellerAgent : observers)
        {
            if(sellerAgent.getReputation()>=minimumReputation && !datacenterRequestedIdsList.contains(sellerAgent.getId()))
            {                
                double bid = sellerAgent.makeBid(mi, referencePrice);
                double ponderedBid = ponderBidWithReputation(bid, sellerAgent, sla);
                if(bid>0 && lowestBid == -1)
                {
                    lowestBid = ponderedBid;
                    bestSeller = sellerAgent;
                }
                else if (bid>0 && (ponderedBid<lowestBid || (ponderedBid==lowestBid && Math.random()>0.5)) )
                {
                    lowestBid = ponderedBid;
                    bestSeller = sellerAgent;
                }
            }

        }
        return bestSeller;
    }
    
    /**
     * 
     * @return 
     */
    public String getName()
    {
        return NAME;
    }
    
    /**
     * 
     * @return 
     */
    public Double getWeightForReputation()
    {
        return reputationWeight;
    }

}