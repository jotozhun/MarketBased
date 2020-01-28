/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.bham.cs.simulation.market;

import java.util.ArrayList;
import java.util.List;
import uk.ac.bham.cs.simulation.cloud.Region;
import uk.ac.bham.cs.simulation.cloud.SLA;
import uk.ac.bham.cs.simulation.cloud.SellerAgent;

/**
 *
 * @author Jopoe
 */
public class Bargaining implements MarketMechanism{
    private static double reputationWeight;
    public static final String NAME = "Bargaining";
    
    
    public Bargaining(Double weightForReputation)
    {
        initTradingMechanism(weightForReputation);
    }
    
    private void initTradingMechanism(Double weightForReputation)
    {
        reputationWeight = weightForReputation;
    }
    
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
                    bargainSellerAndBuyer(sellerAgent, currentSeller);
                    
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
    
    public void bargainSellerAndBuyer(SellerAgent sellerAgent, SellerAgent currentSeller){
        boolean isBuyerInterested = Math.random() > 0.9 && sellerAgent != currentSeller;
        boolean SellerAccepts = Math.random() > 0.5;
        boolean sellerProposeAndBuyerAccepts = Math.random() > 0.5;
        if(isBuyerInterested) //Oferta bajando el 2% de lo que ofrece
        {
            if(SellerAccepts){
                sellerAgent.setPreferredProfitPerMi(sellerAgent.getPreferredProfitPerMi() - 0.015);
                sellerAgent.setMinimumProfitPerMi(sellerAgent.getMinimumProfitPerMi() - 0.015);
            }else if(sellerProposeAndBuyerAccepts){
                sellerAgent.setPreferredProfitPerMi(sellerAgent.getPreferredProfitPerMi() - 0.02);
                sellerAgent.setMinimumProfitPerMi(sellerAgent.getMinimumProfitPerMi() + 0.02);
            }
        }
    }

    

    @Override
    public Double getWeightForReputation() {
        return reputationWeight;
    }

    @Override
    public String getName() {
        return NAME;
    }
    
    
}
