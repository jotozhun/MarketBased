package uk.ac.bham.cs.simulation.market;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import uk.ac.bham.cs.simulation.cloud.Region;
import uk.ac.bham.cs.simulation.cloud.SellerAgent;

/**
 * This class controls the trading regions in the model
 * @author  Carlos Mera Gómez
 * @version 1.0, 30/07/2011
 */
public class TradingMechanism
{
    public static Map<Region, List<SellerAgent>> regions;
    private static MarketMechanism marketMechanism;
    
    /**
     * Constructor
     */
    public TradingMechanism()
    {
        
    }
    
    /**
     * 
     * @param theMarketMechanism 
     */
    public static void initTradingMechanism(MarketMechanism theMarketMechanism)
    {
        regions = new EnumMap<Region, List<SellerAgent>>(Region.class);
        marketMechanism = theMarketMechanism;
    }
    
    /**
     * 
     * @param region 
     */
    public static void createRegion(Region region)
    {
        regions.put(region, new ArrayList<SellerAgent>());
    }
    
    /**
     * 
     * @param region
     * @return 
     */
    public static List<SellerAgent> getSellerAgents(Region region)
    {
        List<SellerAgent> sellerAgentsPerRegion = regions.get(region);
        if(sellerAgentsPerRegion==null)
        {
            sellerAgentsPerRegion = new ArrayList<SellerAgent>();
        }
        return sellerAgentsPerRegion;
    }
    
    /**
     * 
     * @param sellerAgent 
     */
    public static void addSellerAgent(SellerAgent sellerAgent)
    {
        if(regions.get(sellerAgent.getRegion())==null)
        {
            createRegion(sellerAgent.getRegion());
        }
        regions.get(sellerAgent.getRegion()).add(sellerAgent);
    }
    
    /**
     * 
     * @param sellerAgents 
     */
    public static void addSellerAgents(List<SellerAgent> sellerAgents)
    {
        for (SellerAgent sellerAgent : sellerAgents)
        {
            addSellerAgent(sellerAgent);
        }
    }
    
    /**
     * 
     * @return 
     */
    public static MarketMechanism getMarketMechanism()
    {
        return marketMechanism;
    }
    
    /**
     * 
     * @return 
     */
    public static List<SellerAgent> getAllSellerAgents()
    {
        List<SellerAgent> allSellerAgents = new ArrayList<SellerAgent>();
        allSellerAgents.addAll(getSellerAgents(Region.NORTH));
        allSellerAgents.addAll(getSellerAgents(Region.SOUTH));
        allSellerAgents.addAll(getSellerAgents(Region.EAST));
        allSellerAgents.addAll(getSellerAgents(Region.WEST));
        return allSellerAgents;
    }
   
}
