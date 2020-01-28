package uk.ac.bham.cs.simulation.tool.gui;

import uk.ac.bham.cs.simulation.cloud.Region;

/**
 * This class holds the SellerAgent's information fields for export and import
 * @author  Carlos Mera Gómez
 * @version 1.0, 15/08/2011
 */
public class DummySellerAgent
{
    private String  name;
    private Double  minimumProfitPerMi, preferredProfitPerMi, probabilityToSucceed;
    private Integer reputation, mipsPerCore;
    private Region  region;

    /**
     * constructor
     * @param the name
     * @param the minimum profit per mi
     * @param the preferred profit per mi
     * @param the probability to succeed at allocating resources
     * @param the initial reputation for the seller agent
     * @param the mips per core in the host of the datacenter
     * @param the SellerAgent's region 
     */
    public DummySellerAgent(String  name, Double  minimumProfitPerMi,
                            Double preferredProfitPerMi, Double probabilityToSucceed,
                            Integer reputation, Integer mipsPerCore, Region  region)
    {
        this.name                   =   name;
        this.minimumProfitPerMi     =   minimumProfitPerMi;
        this.preferredProfitPerMi   =   preferredProfitPerMi;
        this.probabilityToSucceed   =   probabilityToSucceed;
        this.reputation             =   reputation;
        this.mipsPerCore            =   mipsPerCore;
        this.region                 =   region;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the minimumProfitPerMi
     */
    public Double getMinimumProfitPerMi()
    {
        return minimumProfitPerMi;
    }

    /**
     * @param minimumProfitPerMi the minimumProfitPerMi to set
     */
    public void setMinimumProfitPerMi(Double minimumProfitPerMi)
    {
        this.minimumProfitPerMi = minimumProfitPerMi;
    }

    /**
     * @return the preferredProfitPerMi
     */
    public Double getPreferredProfitPerMi()
    {
        return preferredProfitPerMi;
    }

    /**
     * @param preferredProfitPerMi the preferredProfitPerMi to set
     */
    public void setPreferredProfitPerMi(Double preferredProfitPerMi)
    {
        this.preferredProfitPerMi = preferredProfitPerMi;
    }

    /**
     * @return the probabilityToSucceed
     */
    public Double getProbabilityToSucceed()
    {
        return probabilityToSucceed;
    }

    /**
     * @param probabilityToSucceed the probabilityToSucceed to set
     */
    public void setProbabilityToSucceed(Double probabilityToSucceed)
    {
        this.probabilityToSucceed = probabilityToSucceed;
    }

    /**
     * @return the reputation
     */
    public Integer getReputation()
    {
        return reputation;
    }

    /**
     * @param reputation the reputation to set
     */
    public void setReputation(Integer reputation)
    {
        this.reputation = reputation;
    }

    /**
     * @return the mipsPerCore
     */
    public Integer getMipsPerCore()
    {
        return mipsPerCore;
    }

    /**
     * @param mipsPerCore the mipsPerCore to set
     */
    public void setMipsPerCore(Integer mipsPerCore)
    {
        this.mipsPerCore = mipsPerCore;
    }

    /**
     * @return the region
     */
    public Region getRegion()
    {
        return region;
    }

    /**
     * @param region the region to set
     */
    public void setRegion(Region region)
    {
        this.region = region;
    }

}
