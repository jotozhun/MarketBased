package uk.ac.bham.cs.simulation.tool;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.cloudbus.cloudsim.BuyerAgent;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import uk.ac.bham.cs.simulation.cloud.SLA;

/**
 * This class represents an iteration of a Group of Jobs
 * @author  Carlos Mera Gómez
 * @version 1.0, 27/07/2011
 */
public class Iteration
{
    private Integer     numberOfUsers;
    private Double      scheduledTime;
    private Integer     miPerCloudlet;
    private Integer     mipsPerVm;
    private Integer     minimumReputation;
    private Integer     id;
    private Integer     shift;
    private Double      maximumProfit;
    private SLA         sla;
    private List<BuyerAgent> buyerAgents;

    /**
     * Constructor
     * @param id
     * @param numberOfUsers
     * @param scheduledTime
     * @param miPerCloudlet
     * @param mipsPerVm
     * @param minimumReputation
     * @param sla
     * @param shift
     * @param maximumProfit
     */
    public Iteration(Integer id, Integer numberOfUsers, Double scheduledTime, 
                     Integer miPerCloudlet, Integer mipsPerVm, Integer minimumReputation,
                     SLA sla, Integer shift, Double maximumProfit)
    {
        this.id            = id;
        this.numberOfUsers = numberOfUsers;
        this.scheduledTime = scheduledTime;
        this.miPerCloudlet = miPerCloudlet;
        this.mipsPerVm     = mipsPerVm;
        this.shift         = shift;
        this.minimumReputation = minimumReputation;
        this.sla           = sla;
        this.maximumProfit = maximumProfit;
    }

    /**
     * Initializes the Buyer Agents for this iteration
     * @throws Exception
     */
    public void initBuyerAgents() throws Exception
    {
        buyerAgents        = new ArrayList<BuyerAgent>();
        for(int i=0; i<numberOfUsers; i++)
        {
            BuyerAgent buyerAgent = new BuyerAgent("BuyerAgent_"+id+"."+i, sla, scheduledTime, maximumProfit);
            List<Vm> vms = createVM(buyerAgent.getId(), 1, shift+i, mipsPerVm);
            List<Cloudlet> cloudlets = createCloudlet(buyerAgent.getId(), 1, shift+i, miPerCloudlet);
            buyerAgent.submitVmList(vms);
            buyerAgent.submitCloudletList(cloudlets);
            buyerAgents.add(buyerAgent);
        }        
    }

    /**
     * Creates the VMs for this iteration
     * @param userId
     * @param vms
     * @param idShift
     * @param mips
     * @return
     */
    private List<Vm> createVM(int userId, int vms, int idShift, int mips)
    {
        //Creates a container to store VMs. This list is passed to the broker later
        LinkedList<Vm> list = new LinkedList<Vm>();

        //VM Parameters
        long size = 10000; //image size (MB)
        int ram = 512; //vm memory (MB)
        //int mips = 250;
        long bw = 1000;
        int pesNumber = 1; //number of cpus
        String vmm = "Xen"; //VMM name

        //create VMs
        Vm[] vm = new Vm[vms];

        for(int i=0;i<vms;i++)
        {
            vm[i] = new Vm(idShift + i, userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
            list.add(vm[i]);
        }

        return list;
    }

    /**
     * Creates the Jobs or Cloudlets for this Iteration
     * @param userId
     * @param cloudlets
     * @param idShift
     * @param length
     * @return
     */
    private List<Cloudlet> createCloudlet(int userId, int cloudlets, int idShift, long length)
    {
        // Creates a container to store Cloudlets
        LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();
        //cloudlet parameters
        //long length = 40000;
        long fileSize = 300;
        long outputSize = 300;
        int pesNumber = 1;
        UtilizationModel utilizationModel = new UtilizationModelFull();
        Cloudlet[] cloudlet = new Cloudlet[cloudlets];
        for(int i=0;i<cloudlets;i++)
        {
            cloudlet[i] = new Cloudlet(idShift + i, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            // setting the owner of these Cloudlets
            cloudlet[i].setUserId(userId); //job[i].setSla(sla);
            list.add(cloudlet[i]);
        }
        return list;
    }

    /**
     *
     * @return
     */
    public List<BuyerAgent> getBuyerAgents()
    {
        return buyerAgents;
    }

    /**
     *
     * @return
     */
    public Integer getNumberOfUsers()
    {
        return numberOfUsers;
    }

    /**
     *
     * @return
     */
    public Double getScheduledTime()
    {
        return scheduledTime;
    }

    /**
     *
     * @return
     */
    public Integer getMiPerCloudlet()
    {
        return miPerCloudlet;
    }

    /**
     *
     * @return
     */
    public Integer getMipsPerVm()
    {
        return mipsPerVm;
    }

    /**
     *
     * @return
     */
    public Integer getMinimumReputation()
    {
        return minimumReputation;
    }

    /**
     *
     * @return
     */
    public Integer getId()
    {
        return id;
    }

    /**
     *
     * @return
     */
    public Integer getShift()
    {
        return shift;
    }

    /**
     *
     * @return
     */
    public Double getMaximumProfit()
    {
        return maximumProfit;
    }

    /**
     * 
     * @return
     */
    public SLA getSla()
    {
        return sla;
    }

}
