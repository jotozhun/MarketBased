package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.lists.VmList;
import uk.ac.bham.cs.simulation.cloud.SLA;
import uk.ac.bham.cs.simulation.cloud.SellerAgent;
import uk.ac.bham.cs.simulation.market.ProductionCost;
import uk.ac.bham.cs.simulation.market.TradingMechanism;

/**
 * This class represents a Buyer Agent who acts on behalf of the user to trade jobs allocation in the Cloud
 * @author  Carlos Mera Gómez
 * @version 1.0, 27/07/2011
 */
public class BuyerAgent extends DatacenterBroker
{    
    private Double  startTime;
    private long    totalMi;
    private Integer minimumReputation;
    private SLA     sla;
    private Double  maximumProfit;

    /**
     * Created a new DatacenterBroker object
     * @param name 	name to be associated with this entity (as required by Sim_entity class from simjava package)
     * @throws Exception the exception
     *
     * @pre name != null
     * @post $none
     */
    public BuyerAgent(String name, SLA sla, Double startTime, Double maximumProfit) throws Exception
    {
        super(name);
        this.sla        = sla;
        this.startTime  = startTime;
        totalMi         = 0;
        minimumReputation = 1;
        this.maximumProfit = maximumProfit;
    }

    /* (non-Javadoc)
     * @see cloudsim.core.SimEntity#startEntity()
     */
    @Override
    public void startEntity()
    {
        Log.printLine(getName() + " is starting...");
        //schedule(getId(), 0, CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST);
        schedule(getId(), startTime, CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST);
    }

/**
     * Process the return of a request for the characteristics of a PowerDatacenter.
     *
     * @param ev a SimEvent object
     *
     * @pre ev != $null
     * @post $none
     */
    @Override
    protected void processResourceCharacteristics(SimEvent ev)
    {
        DatacenterCharacteristics characteristics = (DatacenterCharacteristics) ev.getData();
        getDatacenterCharacteristicsList().put(characteristics.getId(), characteristics);

        if (getDatacenterCharacteristicsList().size() == getDatacenterIdsList().size())
        {
            setDatacenterRequestedIdsList(new ArrayList<Integer>());

            //OK  SellerAgent bestSellerAgent = PostedOffer.giveBestSellerAgent(totalMi, calculateReferencePrice(totalMi), minimumReputation, sla);
            SellerAgent bestSellerAgent = TradingMechanism.getMarketMechanism().giveBestSellerAgent(totalMi, calculateReferencePrice(totalMi), minimumReputation, sla);
            if (bestSellerAgent!=null)
            {
                createVmsInDatacenter(bestSellerAgent.getId()/* getDatacenterIdsList().get(1)*/);
            }
        }
    }

    /**
     * Process the ack received due to a request for VM creation.
     *
     * @param ev a SimEvent object
     *
     * @pre ev != null
     * @post $none
     */
    @Override
    protected void processVmCreate(SimEvent ev)
    {
        int[] data = (int[]) ev.getData();
        int datacenterId = data[0];
        int vmId = data[1];
        int result = data[2];

        if (result == CloudSimTags.TRUE)
        {
            getVmsToDatacentersMap().put(vmId, datacenterId);
            getVmsCreatedList().add(VmList.getById(getVmList(), vmId));
            Log.printLine(CloudSim.clock()+": "+getName()+ ": VM #"+vmId+" has been created in Datacenter #" + datacenterId + ", Host #" + VmList.getById(getVmsCreatedList(), vmId).getHost().getId());
        }
        else
        {
            Log.printLine(CloudSim.clock()+": "+getName()+ ": Creation of VM #"+vmId+" failed in Datacenter #" + datacenterId);
        }

        incrementVmsAcks();

        if (getVmsCreatedList().size() == getVmList().size() - getVmsDestroyed())
        { // all the requested VMs have been created
            submitCloudlets();
        }
        else
        {
            if (getVmsRequested() == getVmsAcks())
            { // all the acks received, but some VMs were not created
                // find id of the next datacenter that has not been tried
                //OK  SellerAgent bestSellerAgent = PostedOffer.giveBestSellerAgent(totalMi, calculateReferencePrice(totalMi), minimumReputation,getDatacenterRequestedIdsList(), sla );
                SellerAgent bestSellerAgent = TradingMechanism.getMarketMechanism().giveBestSellerAgent(totalMi, calculateReferencePrice(totalMi), minimumReputation,getDatacenterRequestedIdsList(), sla );
                if(bestSellerAgent!=null)
                {
                    createVmsInDatacenter(bestSellerAgent.getId());
                    return;
                }
                /*
                for (int nextDatacenterId : getDatacenterIdsList())
                {
                    if (!getDatacenterRequestedIdsList().contains(nextDatacenterId))
                    {
                        createVmsInDatacenter(nextDatacenterId);
                        return;
                    }
                }*/

                //all datacenters already queried
                if (getVmsCreatedList().size() > 0)
                { //if some vm were created
                    submitCloudlets();
                }
                else
                { //no vms created. abort
                    Log.printLine(CloudSim.clock() + ": " + getName() + ": none of the required VMs could be created. Aborting");
                    finishExecution();
                }
            }
        }
    }

    /**
     * Send an internal event communicating the end of the simulation.
     *
     * @pre $none
     * @post $none
     */
    private void finishExecution()
    {
        sendNow(getId(), CloudSimTags.END_OF_SIMULATION);
    }

    /**
     * This method is used to send to the broker the list of
     * cloudlets.
     *
     * @param list the list
     *
     * @pre list !=null
     * @post $none
     */
    @Override
    public void submitCloudletList(List<? extends Cloudlet> list)
    {
        getCloudletList().addAll(list);
        for (Cloudlet cloudlet : list)
        {
            totalMi += cloudlet.getCloudletLength();
        }
    }

    /* (non-Javadoc)
     * @see cloudsim.core.SimEntity#shutdownEntity()
     */
    @Override
    public void shutdownEntity()
    {
        Log.printLine(getName() + " is shutting down...");
    }

    /**
     * @return the totalMi
     */
    public long getTotalMi()
    {
        return totalMi;
    }

    /**
     * @param totalMi the totalMi to set
     */
    public void setTotalMi(Integer totalMi)
    {
        this.totalMi = totalMi;
    }

    /**
     * @return the minimumReputation
     */
    public Integer getMinimumReputation()
    {
        return minimumReputation;
    }

    /**
     * @param minimumReputation the minimumReputation to set
     */
    public void setMinimumReputation(Integer minimumReputation)
    {
        this.minimumReputation = minimumReputation;
    }

    private double calculateReferencePrice(Long mi)
    {
        double price = ProductionCost.estimateCost(mi)*(1+maximumProfit);
        return price;
    }

    /**
     * @return the sla
     */
    public SLA getSla()
    {
        return sla;
    }

    /**
     * @param sla the sla to set
     */
    public void setSla(SLA sla)
    {
        this.sla = sla;
    }

    /**
     *
     * @return
     */
    public Integer getNumberOfTries()
    {
        return getDatacenterRequestedIdsList().size();
    }

    /**
     *
     * @return
     */
    public List<Integer> getFailedSellerAgentIdList()
    {
        return getDatacenterRequestedIdsList();
    }

    /**
     *
     * @return
     */
    public List<Integer> getAllSellerAgentIdList()
    {
        return getDatacenterIdsList();
    }

}
