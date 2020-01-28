package uk.ac.bham.cs.simulation.market;

import java.util.ArrayList;
import java.util.List;
import uk.ac.bham.cs.simulation.cloud.Region;
import uk.ac.bham.cs.simulation.cloud.SLA;
import uk.ac.bham.cs.simulation.cloud.SellerAgent;

/**
 * This class implements a reverse auction for resource allocation
 * @author  Carlos Mera Gómez
 * @version 1.0, 30/07/2011
 * @modified by Joel Torres
 * @
 */
public class ReverseAuction implements MarketMechanism
{    
    private double reputationWeight;
    public static final String NAME = "Reverse Auction";
    
    /**
     * 
     * @param weightForReputation 
     */
    public ReverseAuction(Double weightForReputation)
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
        SellerAgent bestSeller = giveBestSellerAgentPerRegion(mi, referencePrice, minimumReputation, sla, sla.getRegion(), new ArrayList<Integer>());        
        // try in other regions
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
     * @param sla
     * @param region
     * @param datacenterRequestedIdsList
     * @return 
     */
    private SellerAgent giveBestSellerAgentPerRegion(long mi, double referencePrice, Integer minimumReputation, SLA sla, Region region, List<Integer> datacenterRequestedIdsList)
    {
        SellerAgent bestSeller = null;
        double lowestPonderedBid = -1;
        int iteration = 0;        
        SellerAgent previousSeller = null;
        SellerAgent currentSeller = null;
        Double currentReferencePrice = referencePrice;

        do
        {
            iteration++;
            List<SellerAgent> observers = TradingMechanism.getSellerAgents(region);
            for (SellerAgent sellerAgent : observers)
            {
                if(sellerAgent.getReputation() >= minimumReputation && !datacenterRequestedIdsList.contains(sellerAgent.getId()) )
                {
                    
                    
                    if(iteration > 1 && Math.random() > 0.75 && sellerAgent != currentSeller) //Oferta bajando el 2% de lo que ofrece
                    {
                        sellerAgent.setPreferredProfitPerMi(sellerAgent.getPreferredProfitPerMi() - 0.02);
                        sellerAgent.setMinimumProfitPerMi(sellerAgent.getMinimumProfitPerMi() - 0.02);
                    
                    }
                    double bid = sellerAgent.makeBid(mi, currentReferencePrice);
                    //double ponderedBid = ponderBidWithReputation(bid, sellerAgent.getReputation(), sla);
                    double ponderedBid = ponderBidWithReputation(bid, sellerAgent, sla);
                    if(bid>0 && (lowestPonderedBid == -1 ||  ponderedBid<lowestPonderedBid ||
                                                            (ponderedBid==lowestPonderedBid && Math.random()>0.5) ))
                    {
                        //lowestBid = bid;
                        lowestPonderedBid = ponderedBid;
                        previousSeller = currentSeller;
                        currentSeller = sellerAgent;
                        currentReferencePrice = bid;
                    }                
                }
            }                       
        }while (iteration<3 && currentSeller!=null && currentSeller!=previousSeller);
        bestSeller = currentSeller;
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
        SellerAgent bestSeller = giveBestSellerAgentPerRegion(mi, referencePrice, minimumReputation, sla, sla.getRegion(), datacenterRequestedIdsList);
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
    public double ponderBidWithReputation(double bid, SellerAgent sellerAgent, SLA sla)
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
