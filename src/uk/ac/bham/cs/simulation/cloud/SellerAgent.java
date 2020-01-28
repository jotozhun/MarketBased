package uk.ac.bham.cs.simulation.cloud;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import uk.ac.bham.cs.simulation.market.ProductionCost;

/**
 * This class represents the SellerAgent which is the owner of datacenters in the Cloud
 * @author  Carlos Mera Gómez
 * @version 1.0, 12/07/2011
 */
public class SellerAgent extends Datacenter
{
    public static final Integer MAXIMUM_REPUTATION = 50;
    public static final Integer MINIMUM_REPUTATION = 1;
    public static final Integer MEDIUM_REPUTATION = 25;
    private Double      preferredProfitPerMi;
    private Double      minimumProfitPerMi;
    private Integer     reputation;
    private Region      region;
    private Integer     successfulJobs;
    private Integer     failedJobs;
    /**
     * Constructor
     * @param name
     * @param characteristics
     * @param vmAllocationPolicy
     * @param storageList
     * @param schedulingInterval
     * @param preferredProfitPerMi
     * @param minimumProfitPerMi
     * @param reputation
     * @param region
     * @throws Exception
     */
    private SellerAgent(String name, DatacenterCharacteristics characteristics,
                        VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList,
                        double schedulingInterval, Double preferredProfitPerMi, 
                        Double minimumProfitPerMi, Integer reputation, Region region) throws Exception
    {
        super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval);
        this.preferredProfitPerMi   = preferredProfitPerMi;
        this.reputation             = reputation;
        this.region                 = region;
        this.minimumProfitPerMi     = minimumProfitPerMi;
        successfulJobs              = 0;
        failedJobs                  = 0;
    }

    /**
     * 
     * @param name
     * @param preferredProfit
     * @param minimumProfit
     * @param probabilyToSucceed
     * @param mipsPerCore
     * @param initialReputation
     * @param theRegion
     * @return
     * @throws Exception
     */
    public static SellerAgent createSellerAgent(String name, Double preferredProfit, Double minimumProfit,
                                                Double probabilyToSucceed, Integer mipsPerCore,
                                                Integer initialReputation, Region theRegion) throws Exception
    {
        // Here are the steps needed to create a PowerDatacenter:
        // 1. We need to create a list to store one or more
        //    Machines
        List<Resource> resourceList = new ArrayList<Resource>();

        // 2. A Machine contains one or more PEs or CPUs/Cores. Therefore, should
        //    create a list to store these PEs before creating
        //    a Machine.
        List<Pe> peList1 = new ArrayList<Pe>();

        //int mips = 1000;

        // 3. Create PEs and add these into the list.
        //for a quad-core machine, a list of 4 PEs is required:
        peList1.add(new Pe(0, new PeProvisionerSimple(mipsPerCore))); // need to store Pe id and MIPS Rating
        peList1.add(new Pe(1, new PeProvisionerSimple(mipsPerCore)));
        peList1.add(new Pe(2, new PeProvisionerSimple(mipsPerCore)));
        peList1.add(new Pe(3, new PeProvisionerSimple(mipsPerCore)));

        //Another list, for a dual-core machine
        /*List<Pe> peList2 = new ArrayList<Pe>();

        peList2.add(new Pe(0, new PeProvisionerSimple(mips)));
        peList2.add(new Pe(1, new PeProvisionerSimple(mips)));*/

        //4. Create Hosts with its id and list of PEs and add them to the list of machines
        int hostId=0;
        int ram = /*2048*/16384; //host memory (MB)
        long storage = 1000000; //host storage
        int bw = 10000;

        Resource resource = new Resource(
                                            hostId,
                                            new RamProvisionerSimple(ram),
                                            new BwProvisionerSimple(bw),
                                            storage,
                                            peList1,
                                            new VmSchedulerTimeShared(peList1)
                                        );
        resource.setProbabilityToSucceed(probabilyToSucceed);

        resourceList.add(resource); // This is our first machine

        /*hostId++;

        resourceList.add(
                new Resource(
                        hostId,
                        new RamProvisionerSimple(ram),
                        new BwProvisionerSimple(bw),
                        storage,
                        peList2,
                        new VmSchedulerTimeShared(peList2)
                )
        );*/ // Second machine


        //To create a host with a space-shared allocation policy for PEs to VMs:
        //hostList.add(
    	//		new Host(
    	//			hostId,
    	//			new CpuProvisionerSimple(peList1),
    	//			new RamProvisionerSimple(ram),
    	//			new BwProvisionerSimple(bw),
    	//			storage,
    	//			new VmSchedulerSpaceShared(peList1)
    	//		)
    	//	);

		//To create a host with a oportunistic space-shared allocation policy for PEs to VMs:
		//hostList.add(
    	//		new Host(
    	//			hostId,
    	//			new CpuProvisionerSimple(peList1),
    	//			new RamProvisionerSimple(ram),
    	//			new BwProvisionerSimple(bw),
    	//			storage,
    	//			new VmSchedulerOportunisticSpaceShared(peList1)
    	//		)
    	//	);


        // 5. Create a DatacenterCharacteristics object that stores the
        //    properties of a data center: architecture, OS, list of
        //    Machines, allocation policy: time- or space-shared, time zone
        //    and its price (G$/Pe time unit).
        String arch = "x86";      // system architecture
        String os = "Linux";          // operating system
        String vmm = "Xen";
        double time_zone = 10.0;         // time zone this resource located
        double cost = 3.0;              // the cost of using processing in this resource
        double costPerMem = 0.05;		// the cost of using memory in this resource
        double costPerStorage = 0.1;	// the cost of using storage in this resource
        double costPerBw = 0.1;			// the cost of using bw in this resource
        LinkedList<Storage> storageList = new LinkedList<Storage>();	//we are not adding SAN devices by now

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
        arch, os, vmm, resourceList, time_zone, cost, costPerMem, costPerStorage, costPerBw);

        // 6. Finally, we need to create a PowerDatacenter object.
        SellerAgent sellerAgent = null;
        //try
        //{
        sellerAgent = new SellerAgent(name, characteristics, new VmAllocationPolicySimple(resourceList),
                                      storageList, 0, preferredProfit, minimumProfit, initialReputation, theRegion);
            //sellerAgent.setPreferredProfitPerMi(preferredProfit);
        /*}
        catch (Exception e)
        {
            e.printStackTrace();
        }*/
        return sellerAgent;
    }

    /**
     *
     * @param mi
     * @return
     */
    private double calculatePreferredPrice(long mi)
    {
        double price = ProductionCost.estimateCost(mi)*(1+preferredProfitPerMi);
        return price;
    }

    /**
     *
     * @param mi
     * @return
     */
    private double calculateMinimumPrice(long mi)
    {
        double price = ProductionCost.estimateCost(mi)*(1+minimumProfitPerMi);
        return price;
    }

    /**
     *
     * @param mi
     * @param referencePrice
     * @return
     */
    public double postOffer(long mi, double referencePrice)
    {
        double offeredPrice = -1;
        double preferredPrice = calculatePreferredPrice(mi);
        if(preferredPrice<=referencePrice)
        {
            offeredPrice = preferredPrice;
        }
        return offeredPrice;
    }

    /**
     *  
     * @param mi
     * @param referencePrice
     * @return
     */

    public double makeBid(long mi, double referencePrice)
    {
        double offeredPrice     = -1;
        double preferredPrice   = calculatePreferredPrice(mi);
        double minimumPrice     = calculateMinimumPrice(mi);
        if(preferredPrice <= referencePrice)
        {
            offeredPrice = preferredPrice;
        }
        else if(referencePrice == minimumPrice)
        {
            offeredPrice = minimumPrice;
        }
        else if(preferredPrice > referencePrice && minimumPrice < referencePrice)
        {
            double Min = minimumPrice;
            double Max = referencePrice;
            offeredPrice = Min + Math.random() * (Max - Min); // http://stackoverflow.com/questions/363681/java-generating-random-number-in-a-range
        }
        return offeredPrice;
    }

    /**
     * @return the reputation
     */
    public Integer getReputation()
    {
        return reputation;
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
     * Increase the current reputation
     */
    public void increaseReputation()
    {
        if(reputation < MAXIMUM_REPUTATION)
        {
            reputation += 1;
        }
        successfulJobs++;
    }

    /**
     * Decrease the currrent reputation
     */
    public void decreaseReputation()
    {
        if(reputation > MINIMUM_REPUTATION)
        {
            reputation -= 1;
        }
        failedJobs++;
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
     * 
     * @return the number of allocated jobs
     */
    public int getNumberOfAllocatedJobs()
    {
        return getDebts().keySet().size();
    }

    /**
     *
     * @return
     */
    public Double getMinimumProfitPerMi()
    {
        return minimumProfitPerMi;
    }

    public void setMinimumProfitPerMi(Double minimumProfitPerMi) {
        this.minimumProfitPerMi = minimumProfitPerMi;
    }

    /**
     * 
     * @return the probability to succeed at allocating jobs
     */
    public Double getProbabilityToSucceed()
    {
        Double probabilityToSuceed = 0.0;
        List<Resource> resources = getCharacteristics().getHostList();
        if(resources.size()>0)
        {
            probabilityToSuceed = resources.get(0).getProbabilityToSucceed();
        }
        return probabilityToSuceed;
    }

    /**
     * 
     * @return the mips per core
     */
    public Integer getMipsPerCore()
    {
        Integer mipsPerCore = 0;
        List<Resource> resources = getCharacteristics().getHostList();
        if(resources.size()>0)
        {
            mipsPerCore = resources.get(0).getPeList().get(0).getMips();
        }
        return mipsPerCore;
    }

    /**
     * @return the successfulJobs
     */
    public Integer getSuccessfulJobs()
    {
        return successfulJobs;
    }

    /**
     * @param successfulJobs the successfulJobs to set
     */
    public void setSuccessfulJobs(Integer successfulJobs)
    {
        this.successfulJobs = successfulJobs;
    }

    /**
     * @return the failedJobs
     */
    public Integer getFailedJobs()
    {
        return failedJobs;
    }

    /**
     * @param failedJobs the failedJobs to set
     */
    public void setFailedJobs(Integer failedJobs)
    {
        this.failedJobs = failedJobs;
    }

    /**
     *
     * @return the total of performed jobs
     */
    public Integer getTotalJobs()
    {
        return successfulJobs+failedJobs;
    }

    /**
     *
     * @return the rate of success
     */
    public Double getSucessfulJobsRate()
    {
        Double rate = 0.0;
        if(getTotalJobs()>0)
        {
            rate = getSuccessfulJobs().doubleValue() / getTotalJobs().doubleValue();
        }
        return rate;
    }

    /**
     *
     * @return the rate of failures
     */
    public Double getFailedJobsRate()
    {
        Double rate = 0.0;
        if(getTotalJobs()>0)
        {
            rate = getFailedJobs().doubleValue() / getTotalJobs().doubleValue();
        }
        return rate;
    }

}
