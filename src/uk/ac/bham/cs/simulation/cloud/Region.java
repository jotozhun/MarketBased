package uk.ac.bham.cs.simulation.cloud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents the set of regions where Sellers Agents are
 * @author  Carlos Mera Gómez
 * @version 1.0, 30/07/2011
 */
public enum Region
{
    NORTH, SOUTH, EAST, WEST;

    /**
     * List all the available regions
     * @param region
     * @return
     */
    public static List<Region> getOtherRegions(Region region)
    {
        List<Region> regions = new ArrayList<Region>();        
        switch(region)
        {
            case NORTH:
                regions = Arrays.asList(SOUTH, EAST, WEST);
                break;
            case SOUTH:
                regions = Arrays.asList(NORTH, EAST, WEST);
                break;
            case EAST:
                regions = Arrays.asList(NORTH, SOUTH, WEST);
                break;
            case WEST:
                regions = Arrays.asList(NORTH, SOUTH, EAST);
                break;
        }
        return regions;
    }
    
}
