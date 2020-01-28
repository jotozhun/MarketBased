package uk.ac.bham.cs.simulation.tool;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.cloudbus.cloudsim.BuyerAgent;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import uk.ac.bham.cs.simulation.cloud.Region;
import uk.ac.bham.cs.simulation.cloud.SLA;
import uk.ac.bham.cs.simulation.cloud.SLA.Reliability;
import uk.ac.bham.cs.simulation.cloud.SellerAgent;
import uk.ac.bham.cs.simulation.market.MarketMechanism;
import uk.ac.bham.cs.simulation.market.PostedOffer;
import uk.ac.bham.cs.simulation.market.ReverseAuction;
import uk.ac.bham.cs.simulation.market.TradingMechanism;

/**
 * This class starts the simulation
 * @author  Carlos Mera Gómez
 * @version 1.0, 01/07/2011
 */
public class Simulator
{
    
    public static void main(String[] args)
    {
        Log.printLine("Starting Simulator...");
        try
        {
            // First step: Initialize the CloudSim package. It should be called
            // before creating any entities.
            int num_user = 1;   // number of grid users
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;  // mean trace events

            // Initialize the CloudSim library
            CloudSim.init(num_user, calendar, trace_flag);

            // Second step: Create Datacenters
            //Datacenters are the resource providers in CloudSim. We need at list one of them to run a CloudSim simulation
            //String name, Integer preferredProfit,Integer probabilyToSucced, Integer mipsPerCore
            SellerAgent sellerAgentA = SellerAgent.createSellerAgent("SellerAgent_A", 0.36, 0.30, 0.98,
                                                                    250, SellerAgent.MEDIUM_REPUTATION,
                                                                    Region.NORTH);
            SellerAgent sellerAgentB = SellerAgent.createSellerAgent("SellerAgent_B", 0.35, 0.31, 0.80,
                                                                    250, SellerAgent.MEDIUM_REPUTATION,
                                                                    Region.NORTH);
            SellerAgent sellerAgentC = SellerAgent.createSellerAgent("SellerAgent_C", 0.34, 0.30, 0.60,
                                                                    250, SellerAgent.MEDIUM_REPUTATION,
                                                                    Region.NORTH);

            SellerAgent sellerAgentD = SellerAgent.createSellerAgent("SellerAgent_D", 0.34, 0.20, 0.95,
                                                                    250, SellerAgent.MEDIUM_REPUTATION,
                                                                    Region.EAST);

            MarketMechanism marketMechanism = new PostedOffer(0.3);  //new ReverseAuction(0.3); // 
            TradingMechanism.initTradingMechanism(marketMechanism);
            TradingMechanism.addSellerAgent(sellerAgentA);
            TradingMechanism.addSellerAgent(sellerAgentB);
            TradingMechanism.addSellerAgent(sellerAgentC);
            TradingMechanism.addSellerAgent(sellerAgentD);
                        
            //List<SellerAgent> allSellerAgents = TradingMechanism.getAllSellerAgents(); //new ArrayList<SellerAgent>();
            /*allSellerAgents.add(sellerAgentA);
            allSellerAgents.add(sellerAgentB);
            allSellerAgents.add(sellerAgentC);*/

            //PostedOffer.initTradingMechanism(0.3);//a number from 0.0 to 0.3

            //PostedOffer.addObserver(sellerAgentA);
            //PostedOffer.addObserver(sellerAgentB);
            //PostedOffer.addObserver(sellerAgentC);
            
            //Third step: Create Broker
            //Fourth step: Create VMs and Cloudlets and send them to broker
            //Integer id, Integer numberOfUsers, Double scheduledTime, Integer miPerCloudlet,
            //Integer mipsPerVm, Integer minimumReputation, SLA sla, Integer shift, Integer maximumProfit
            Iteration firstIteration  = new Iteration(1, 2, 0.0, 4000, 250, 1,
                                                    new SLA(SLA.Priority.HIGH, Reliability.HIGH, 0, Region.NORTH),
                                                    0, 0.4);
            firstIteration.initBuyerAgents();
            Iteration secondIteration = new Iteration(2, 2, 20.0, 4000, 250, 1,
                                                    new SLA(SLA.Priority.HIGH, Reliability.HIGH, 0, Region.NORTH),
                                                    2, 0.4);
            secondIteration.initBuyerAgents();
            Iteration thirdIteration = new Iteration(2, 2, 40.0, 4000, 250, 1,
                                                    new SLA(SLA.Priority.HIGH, Reliability.HIGH, 0, Region.NORTH),
                                                    4, 0.4);
            thirdIteration.initBuyerAgents();
            Iteration fourthIteration = new Iteration(2, 2, 60.0, 4000, 250, 1,
                                                    new SLA(SLA.Priority.HIGH, Reliability.HIGH, 0, Region.NORTH),
                                                    6, 0.24);
            fourthIteration.initBuyerAgents();

            List<BuyerAgent> allBuyerAgents = new ArrayList<BuyerAgent>();
            allBuyerAgents.addAll(firstIteration.getBuyerAgents());
            allBuyerAgents.addAll(secondIteration.getBuyerAgents());
            allBuyerAgents.addAll(thirdIteration.getBuyerAgents());
            allBuyerAgents.addAll(fourthIteration.getBuyerAgents());
            // Fifth step: Starts the simulation
            CloudSim.startSimulation();

            // Final step: Print results when simulation is over
            List<Cloudlet> cloudletReceivedList = new ArrayList<Cloudlet>();
            List<Cloudlet> cloudletList = new ArrayList<Cloudlet>();
            
            CloudSim.stopSimulation();
            
            for (BuyerAgent buyerAgent : allBuyerAgents)
            {                
                cloudletReceivedList.addAll(buyerAgent.getCloudletReceivedList());
                cloudletList.addAll(buyerAgent.getCloudletList());
            }
            Util.printCloudletList(cloudletReceivedList, allBuyerAgents);
            if(cloudletList.size()>0)
            {
                Util.printCloudletList(cloudletList, allBuyerAgents);  
            }
            
            //Print the debt of each user to each datacenter
            Util.printSellerAgentInfo(TradingMechanism.getAllSellerAgents()/*allSellerAgents*/);
            
            Log.printLine("CloudSimSimulation finished!");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.printLine("Unwanted errors happen");
        }
    }    
}
