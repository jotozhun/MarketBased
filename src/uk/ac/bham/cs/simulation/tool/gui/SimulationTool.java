package uk.ac.bham.cs.simulation.tool.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import org.cloudbus.cloudsim.BuyerAgent;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import uk.ac.bham.cs.simulation.cloud.SellerAgent;
import uk.ac.bham.cs.simulation.market.MarketMechanism;
import uk.ac.bham.cs.simulation.market.TradingMechanism;
import uk.ac.bham.cs.simulation.tool.Iteration;
import uk.ac.bham.cs.simulation.tool.Util;

/**
 * This is the main class of the simulator to start simulating the scenario
 * @author  Carlos Mera Gómez
 * @version 1.0, 02/08/2011
 */
public class SimulationTool extends JPanel
{
    private SellersPane     sellersPane;
    private TradingPane     tradingPane;
    private BuyersPane      buyersPane;
    private JEditorPane     editorPane;
    private ChartPane       chartPane;
    private JTabbedPane     tabbedPane;
    private Integer         simulations;
    private TreePane        treePane;
    private Mediator        mediator;
    private Map<String,List<SellerAgent>>   simulatedSellerAgents;
    private static JFrame          frame;

    /**
     * Constructor
     */
    public SimulationTool()
    {
        simulations = 0;
        simulatedSellerAgents = new HashMap<String, List<SellerAgent>>();
        mediator = new Mediator();
        setLayout(new BorderLayout());
        sellersPane = new SellersPane(mediator);
        tradingPane = new TradingPane();
        buyersPane  = new BuyersPane(mediator);
        treePane    = new TreePane();
        mediator.setSellersPane(sellersPane);
        mediator.setBuyersPane(buyersPane);
        mediator.setTreePane(treePane);
        tabbedPane = new JTabbedPane();
        tabbedPane.add("Sellers",sellersPane);
        tabbedPane.add("Trading",tradingPane);
        tabbedPane.add("Scheduled Jobs",buyersPane);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setOneTouchExpandable(true);
        splitPane.setLeftComponent(new JScrollPane(treePane));
        splitPane.setRightComponent(tabbedPane);
        add(new ToolBar(this),BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        setPreferredSize(new Dimension(950, 520));
    }

    /**
     * The main thread to run the GUI
     */
    private static void createAndShowGUI()
    {
        frame = new JFrame("Simulation Tool for Market-based Cloud Resource Allocation");
        SimulationTool simulationTool = new SimulationTool();
        frame.setJMenuBar(new MenuBar(simulationTool));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(Util.createImageIcon(frame, "/images/cloud.png").getImage());
        frame.add(simulationTool);
        frame.pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
	frame.setLocation((int) (d.getWidth() / 5) , 15);
        frame.setVisible(true);
    }

    /**
     * The main method
     * @param args
     */
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
		createAndShowGUI();
            }
        });
    }

    /**
     * Starts the simulation
     */
    public void simulate()
    {
        chartPane   = new ChartPane();
        JScrollPane chartScrollPane = new JScrollPane(chartPane);
        tabbedPane.add("Chart "+"["+(simulations)+"]",chartScrollPane);
        editorPane  = new JEditorPane();
        tabbedPane.add("Log "+"["+(simulations++)+"]",new JScrollPane(editorPane));
        simulatedSellerAgents.clear();

        OutputStream out = new ByteArrayOutputStream();
        Log.setOutput(out);        
        List<MarketMechanism> marketMechanismList = tradingPane.createMarketMechanism();
        for (MarketMechanism marketMechanism : marketMechanismList)
        {
            Log.printLine("=================Starting Simulation for "+marketMechanism.getName()+"===================");
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
                List<SellerAgent> sellerAgents = sellersPane.createSellerAgents();

                TradingMechanism.initTradingMechanism(marketMechanism);
                TradingMechanism.addSellerAgents(sellerAgents);

                //Third step: Create Broker
                //Fourth step: Create VMs and Cloudlets and send them to broker
                List<BuyerAgent> buyerAgents = buyersPane.createBuyerAgents();

                // Fifth step: Starts the simulation
                CloudSim.startSimulation();

                // Final step: Print results when simulation is over
                List<Cloudlet> cloudletReceivedList = new ArrayList<Cloudlet>();
                List<Cloudlet> cloudletList = new ArrayList<Cloudlet>();

                CloudSim.stopSimulation();

                for (BuyerAgent buyerAgent : buyerAgents)
                {
                    cloudletReceivedList.addAll(buyerAgent.getCloudletReceivedList());
                    cloudletList.addAll(buyerAgent.getCloudletList());
                }
                Util.printCloudletList(cloudletReceivedList, buyerAgents);
                if(cloudletList.size()>0)
                {
                    Util.printCloudletList(cloudletList, buyerAgents);
                }

                //Print the debt of each user to each datacenter
                Util.printSellerAgentInfo(TradingMechanism.getAllSellerAgents());

                Log.printLine("=================Simulation finished  for "+marketMechanism.getName()+"=====================\n");
                                
                chartPane.drawAllocationChart(sellerAgents, marketMechanism);
                chartPane.drawReputationChart(sellerAgents, marketMechanism);
                chartPane.drawViolationChart(sellerAgents, buyerAgents, marketMechanism);
                simulatedSellerAgents.put(marketMechanism.getName(), sellerAgents);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.printLine("Unwanted errors happen");
            }

        }
        editorPane.setText(out.toString());
        editorPane.setCaretPosition(0);
        tabbedPane.setSelectedComponent(chartScrollPane);
    }

    /**
     * Close the Chart and Log tabs from previous simulations
     */
    public void closeResultTabs()
    {
        if(tabbedPane.getTabCount()==3)
        {
            JOptionPane.showMessageDialog(this, "No Chart and Log tabs to close", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
        else
        {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure that you want to close Chart and Log tabs?","Confirmation",JOptionPane.YES_NO_OPTION);
            if(confirm == JOptionPane.YES_OPTION)
            {
                while(tabbedPane.getTabCount()>3)
                {
                    int i = tabbedPane.getTabCount();
                    tabbedPane.remove(i-1);
                }
            }
        }
    }

    /**
     * Repeat the simulation with the last sellers agents and last market mechanisms
     */
    public void continueSimulation()
    {        
        chartPane   = new ChartPane();
        JScrollPane chartScrollPane = new JScrollPane(chartPane);
        tabbedPane.add("Chart "+"["+(simulations)+"]",chartScrollPane);
        editorPane  = new JEditorPane();
        tabbedPane.add("Log "+"["+(simulations++)+"]",new JScrollPane(editorPane));

        OutputStream out = new ByteArrayOutputStream();
        Log.setOutput(out);
        List<MarketMechanism> marketMechanismList = tradingPane.createMarketMechanism();
        for (MarketMechanism marketMechanism : marketMechanismList)
        {
            if(simulatedSellerAgents.get(marketMechanism.getName())==null)
            {
                continue;
            }
            Log.printLine("=================Starting Simulation for "+marketMechanism.getName()+"===================");
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
                

                List<SellerAgent> sellerAgents = sellersPane.reCreateSellerAgents(simulatedSellerAgents.get(marketMechanism.getName()));

                TradingMechanism.initTradingMechanism(marketMechanism);
                TradingMechanism.addSellerAgents(sellerAgents);

                //Third step: Create Broker
                //Fourth step: Create VMs and Cloudlets and send them to broker
                List<BuyerAgent> buyerAgents = buyersPane.createBuyerAgents();

                // Fifth step: Starts the simulation
                CloudSim.startSimulation();

                // Final step: Print results when simulation is over
                List<Cloudlet> cloudletReceivedList = new ArrayList<Cloudlet>();
                List<Cloudlet> cloudletList = new ArrayList<Cloudlet>();

                CloudSim.stopSimulation();

                for (BuyerAgent buyerAgent : buyerAgents)
                {
                    cloudletReceivedList.addAll(buyerAgent.getCloudletReceivedList());
                    cloudletList.addAll(buyerAgent.getCloudletList());
                }
                Util.printCloudletList(cloudletReceivedList, buyerAgents);
                if(cloudletList.size()>0)
                {
                    Util.printCloudletList(cloudletList, buyerAgents);
                }

                //Print the debt of each user to each datacenter
                Util.printSellerAgentInfo(TradingMechanism.getAllSellerAgents());

                Log.printLine("=================Simulation finished  for "+marketMechanism.getName()+"=====================\n");

                chartPane.drawAllocationChart(sellerAgents, marketMechanism);
                chartPane.drawReputationChart(sellerAgents, marketMechanism);
                chartPane.drawViolationChart(sellerAgents, buyerAgents, marketMechanism);
                simulatedSellerAgents.put(marketMechanism.getName(), sellerAgents);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.printLine("Unwanted errors happen");
            }

        }
        editorPane.setText(out.toString());
        editorPane.setCaretPosition(0);
        tabbedPane.setSelectedComponent(chartScrollPane);
    }

    /**
     * Retrieves the current SellerAgents in the scenario to be exported to a file
     * @return
     */
    public List<DummySellerAgent> exportDummySellerAgents()
    {      
        List<DummySellerAgent>  dummySellerAgentList = sellersPane.createDummySellerAgents();
        return dummySellerAgentList;
    }

    /**
     * Retrieves the current Group of Jobs in the scenario to be exported to a file
     * @return
     */
    public List<Iteration> exportGroupOfJobs()
    {        
        List<Iteration> groupOfJobsList = buyersPane.createIterations();
        return groupOfJobsList;
    }

    /**
     * Retrieves the current Market Mechanisms in the scenario to be exported to a file
     * @return
     */
    public List<MarketMechanism> exportMarketMechanisms()
    {
        List<MarketMechanism> marketMechanismList = tradingPane.createMarketMechanism();
        return marketMechanismList;
    }

    /**
     * Imports SellerAgents, Groups of Jobs and MarketMechanisms in the simulator
     * @param sellerAgents
     * @param groupOfJobs
     * @param marketMechanisms
     */
    public void importScenario(DummySellerAgent[] sellerAgents, Iteration[] groupOfJobs, MarketMechanism[] marketMechanisms)
    {
        sellersPane.importScenario(sellerAgents);
        tradingPane.importScenario(marketMechanisms);
        buyersPane.importScenario(groupOfJobs);
        treePane.importScenario(sellerAgents, marketMechanisms, groupOfJobs);
    }

    /**
     * @return the frame
     */
    public JFrame getFrame()
    {
        return frame;
    }

}
