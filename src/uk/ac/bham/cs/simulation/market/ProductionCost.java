package uk.ac.bham.cs.simulation.market;

/**
 * This class calculates the production cost per million of instructions
 * @author  Carlos Mera Gómez
 * @version 1.0, 29/07/2011
 */
public class ProductionCost
{
    private static final Double COSTPERMI = 1.0;

    /**
     * 
     * @param mi
     * @return 
     */
    public static Double estimateCost(Long mi)
    {
        return COSTPERMI*mi;
    }
}
