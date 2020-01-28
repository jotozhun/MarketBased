package uk.ac.bham.cs.simulation.cloud;

/**
 * This class represents the SLA document and contains the expected QoS
 * @author  Carlos Mera Gómez
 * @version 1.0, 05/07/2011
 */
public class SLA
{   
    public enum Priority {LOW, MEDIUM, HIGH}
    private Priority     priority;
    private Region       region;
    private Integer      maximumTimeForCompletion;
    private Reliability  reliability;
    
    /**
     * 
     * @param priority
     * @param reliability
     * @param maximumTimeForCompletion
     * @param region
     */
    public SLA(Priority priority, Reliability reliability, Integer maximumTimeForCompletion, Region region)
    {
        this.priority                   = priority;
        this.reliability                = reliability;
        this.maximumTimeForCompletion   = maximumTimeForCompletion;
        this.region                     = region;
    }

    /**
     * @return the priority
     */
    public Priority getPriority()
    {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(Priority priority)
    {
        this.priority = priority;
    }

    /**
     * @return the maximumTimeForCompletion
     */
    public Integer getMaximumTimeForCompletion()
    {
        return maximumTimeForCompletion;
    }

    /**
     * @param maximumTimeForCompletion the maximumTimeForCompletion to set
     */
    public void setMaximumTimeForCompletion(Integer maximumTimeForCompletion)
    {
        this.maximumTimeForCompletion = maximumTimeForCompletion;
    }

    /**
     * @return the reliability
     */
    public Reliability getReliability()
    {
        return reliability;
    }

    /**
     * @param reliability the reliability to set
     */
    public void setReliability(Reliability reliability)
    {
        this.reliability = reliability;
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

    /**
     * This class constains the levels of reliability
     */
    public enum Reliability
    {
        LOW(0.5/*1*/),
        MEDIUM(0.75/*3*/),
        HIGH(1.0/*5*/);
        private Double value;

        /**
         * Constructor
         * @param value
         */
        Reliability(Double value)
        {
            this.value = value;
        }

        /**
         * @return the value
         */
        public Double getValue()
        {
            return value;
        }

        /**
         * @param value the value to set
         */
        public void setValue(Double value)
        {
            this.value = value;
        }
    }

}
