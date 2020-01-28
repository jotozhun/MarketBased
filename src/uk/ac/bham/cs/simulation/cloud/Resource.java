package uk.ac.bham.cs.simulation.cloud;

import java.util.List;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmScheduler;
import org.cloudbus.cloudsim.provisioners.BwProvisioner;
import org.cloudbus.cloudsim.provisioners.RamProvisioner;

/**
 * Resource models a resource in a IaaS layer
 * @author  Carlos Mera Gómez
 * @version 1.0, 01/07/2011
 */
public class Resource extends Host
{
    public static final double MINIMUM_PROBABILITY = 0;
    public static final double DEFAULT_PROBABILITY = 0.5;
    public static final double MAXIMUM_PROBABILITY = 1;
    public static final double LOWER_BOUNDARY_TO_SUCCEED = 0.7;
    private Integer CPUCapacity;
    private Integer memoryAvailable;
    private double probabilityToSucceed;

    /**
     * 
     * @param id
     * @param ramProvisioner
     * @param bwProvisioner
     * @param storage
     * @param peList
     * @param vmScheduler
     */
    public Resource(int id, RamProvisioner ramProvisioner, BwProvisioner bwProvisioner,
                    long storage, List<? extends Pe> peList, VmScheduler vmScheduler)
    {
        super(id, ramProvisioner, bwProvisioner,
                storage, peList, vmScheduler);
        probabilityToSucceed = DEFAULT_PROBABILITY;
    }

    /**
     * Allocates PEs and memory to a new VM in the Host.
     * @param vm Vm being started
     * @return $true if the VM could be started in the host; $false otherwise
     * @pre $none
     * @post $none
     */
    @Override
    public boolean vmCreate(Vm vm)
    {
        /*int Min = probabilityToSucceed;
        int Max = MAXIMUM_BOUNDARY;
        int result = Min + (int)(Math.random() * ((Max - Min) + 1));
        if(result<LOWER_BOUNDARY_TO_SUCCEED)*/
        if(!succeed(probabilityToSucceed))
        {
            Log.printLine("Allocation of VM #" + vm.getId() + " to Host #" + getId() + " failed by Availability");
            ((SellerAgent)this.getDatacenter()).decreaseReputation();
            return false;
        }

        if (!getRamProvisioner().allocateRamForVm(vm, vm.getCurrentRequestedRam()))
        {
            Log.printLine("Allocation of VM #" + vm.getId() + " to Host #" + getId() + " failed by RAM");
            return false;
        }

        if (!getBwProvisioner().allocateBwForVm(vm, vm.getCurrentRequestedBw()))
        {
            Log.printLine("Allocation of VM #" + vm.getId() + " to Host #" + getId() + " failed by BW");
            getRamProvisioner().deallocateRamForVm(vm);
            return false;
        }

        if (!getVmScheduler().allocatePesForVm(vm, vm.getCurrentRequestedMips()))
        {
            Log.printLine("[VmScheduler.vmCreate] Allocation of VM #" + vm.getId() + " to Host #" + getId() + " failed by MIPS");
            getRamProvisioner().deallocateRamForVm(vm);
            getBwProvisioner().deallocateBwForVm(vm);
            return false;
        }

        getVmList().add(vm);
        vm.setHost(this);
        /*if(this.getDatacenter().getId()==2)
        {
            System.out.println("CloudSim.clock():"+CloudSim.clock());
            CloudSim.pause(this.getDatacenter().getId(), CloudSim.clock()+1);
        }*/
            
        /*if()//if there is a delay and it is a high priotity job
        {
            //decrease the reputation
        }
        else // there is no delay or there is a delay but the job doesn't have high priority
        {
            // increase reputation
        }*/
        ((SellerAgent)this.getDatacenter()).increaseReputation();
        return true;
    }


    /**
     * @return the CPUCapacity
     */
    public Integer getCPUCapacity()
    {
        return CPUCapacity;
    }

    /**
     * @param CPUCapacity the CPUCapacity to set
     */
    public void setCPUCapacity(Integer CPUCapacity)
    {
        this.CPUCapacity = CPUCapacity;
    }

    /**
     * @return the memoryAvailable
     */
    public Integer getMemoryAvailable()
    {
        return memoryAvailable;
    }

    /**
     * @param memoryAvailable the memoryAvailable to set
     */
    public void setMemoryAvailable(Integer memoryAvailable)
    {
        this.memoryAvailable = memoryAvailable;
    }

    /**
     * @return the probabilityToSucceed
     */
    public double getProbabilityToSucceed()
    {
        return probabilityToSucceed;
    }

    /**
     * @param probabilityToSucceed the probabilityToSucceed to set
     */
    public void setProbabilityToSucceed(Double probabilityToSucceed)
    {
        if(probabilityToSucceed>= MINIMUM_PROBABILITY && probabilityToSucceed <= MAXIMUM_PROBABILITY)
        {
            this.probabilityToSucceed = probabilityToSucceed;
        }
    }

    /**
     * Returns a random boolean value with probability p
     * of being true and probability 1-p of being false
     * p should be in the range 0.0 - 1.0
     * Modified from http://www.refactory.org/s/generate_random_boolean_values_with_probability/view/latest
     */
    protected boolean succeed(double p)
    {
        return (Math.random() < p);
    }

    /**
     * 
     * @param p
     * @return
     */
    protected boolean isGoingToSucceed2(double p)
    {
        double Min = probabilityToSucceed;
        double Max = MAXIMUM_PROBABILITY;
        double result = Min + (int)(Math.random() * ((Max - Min) + 1));
        return result < LOWER_BOUNDARY_TO_SUCCEED;
    }

}
