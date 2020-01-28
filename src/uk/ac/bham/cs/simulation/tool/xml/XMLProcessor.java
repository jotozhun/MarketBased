package uk.ac.bham.cs.simulation.tool.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import uk.ac.bham.cs.simulation.cloud.Region;
import uk.ac.bham.cs.simulation.cloud.SLA;
import uk.ac.bham.cs.simulation.cloud.SLA.Priority;
import uk.ac.bham.cs.simulation.cloud.SLA.Reliability;
import uk.ac.bham.cs.simulation.market.MarketMechanism;
import uk.ac.bham.cs.simulation.market.PostedOffer;
import uk.ac.bham.cs.simulation.market.ReverseAuction;
import uk.ac.bham.cs.simulation.tool.Iteration;
import uk.ac.bham.cs.simulation.tool.gui.DummySellerAgent;

/**
 * This class contains the method to export and import a scenario for simulations
 * @author Carlos Mera Gómez
 * @version 1.0, 10/08/2011
 */
public class XMLProcessor
{
    private File file = null;
    private static Document document = null;

    /**
     * Constructor
     * @param file
     */
    public XMLProcessor(File file)
    {
        this.file = file;
        open();
    }

    /**
     * Formats the scenario to be saved into a XML file
     * @param dummySellerAgentList
     * @param marketMechanismList
     * @param iterationList
     */
    public void saveScenario(List<DummySellerAgent> dummySellerAgentList,  List<MarketMechanism> marketMechanismList, List<Iteration> iterationList)
    {
        for (DummySellerAgent dummySellerAgent : dummySellerAgentList)
        {
            addSellerAgent(dummySellerAgent);
        }
        for (MarketMechanism marketMechanism : marketMechanismList)
        {
            addMarketMechanism(marketMechanism);
        }
        for (Iteration iteration : iterationList)
        {
            addIteration(iteration);
        }
        save();
    }

    /**
     * Adds a Seller Agent in the DOM tree structure of the file
     * @param dummySellerAgent
     */
    public void addSellerAgent(DummySellerAgent dummySellerAgent)
    {
        Element newElement = new Element("sellerAgent");      
        copyFromSellerAgentToElement(dummySellerAgent, newElement);
        document.getRootElement().getChild("sellers").addContent(newElement);              
    }

    /**
     * Adds a Market Mechanism in the DOM tree structure of the file
     * @param marketMechanism
     */
    public void addMarketMechanism(MarketMechanism marketMechanism)
    {
        Element newElement = new Element("marketMechanism");
        copyFromMarketMechanismToElement(marketMechanism, newElement);
        document.getRootElement().getChild("trading").addContent(newElement);
    }

    /**
     * Adds an Iteration of Group of Jobs in the DOM tree structure of the file
     * @param iteration
     */
    public void addIteration(Iteration iteration)
    {
        Element newElement = new Element("iteration");
        copyFromIterationToElement(iteration, newElement);
        document.getRootElement().getChild("iterations").addContent(newElement);
    }

    /**
     * Retrieves all the Seller Agents from the DOM tree structure
     * @return
     */
    public DummySellerAgent[] getAllDummySellerAgents()
    {

        Collection<Element> col = document.getRootElement().getChild("sellers").getChildren();
        DummySellerAgent[] allSellers = new DummySellerAgent[col.size()];
        int i=0;
        for (Iterator<Element> it = col.iterator(); it.hasNext();)
        {
            Element element = it.next();
            DummySellerAgent sellerAgent =  copyFromElementToSellerAgent(element);
            allSellers[i++] = sellerAgent;
        }
        return allSellers;
    }

    /**
     * Parses an element in the DOM tree structure to a SellerAgent
     * @param element
     * @return
     */
    private DummySellerAgent copyFromElementToSellerAgent(Element element) //throws Exception
    {
        DummySellerAgent sellerAgent = new DummySellerAgent(element.getChildText("name"),
                                                                Double.valueOf(element.getChildText("preferredProfit")),
                                                                Double.valueOf(element.getChildText("minimumProfit")),
                                                                Double.valueOf(element.getChildText("probabilityToSucceed")),
                                                                Integer.valueOf(element.getChildText("mipsPerCore")),
                                                                Integer.valueOf(element.getChildText("initialReputation")),
                                                                Region.valueOf(element.getChildText("region")));
        return sellerAgent;               
    }

    /**
     * Retrieves all the Iteration of groups oj jobs from the DOM tree structure
     * @return
     * @throws Exception
     */
    public Iteration[] getAllIterations() throws Exception
    {               
        Collection<Element> col = document.getRootElement().getChild("iterations").getChildren();
        Iteration[] allIterations = new Iteration[col.size()];
        int i=0;
        int shift = 0;
        for (Iterator<Element> it = col.iterator(); it.hasNext();)
        {
            Element element = it.next();                       
            Iteration iteration =  copyFromElementToIteration(element, shift);
            shift += iteration.getNumberOfUsers();
            allIterations[i++] = iteration;
        }        
        return allIterations;
    }

    /**
     * Retrieves all the Trading Mechanisms from the DOM tree structure
     * @return
     * @throws Exception
     */
    public MarketMechanism[] getAllTradingMechanisms() throws Exception
    {
        Collection<Element> col = document.getRootElement().getChild("trading").getChildren();
        MarketMechanism[] allMarketMechanisms = new MarketMechanism[col.size()];
        int i=0;
        for (Iterator<Element> it = col.iterator(); it.hasNext();)
        {
            Element element = it.next();
            MarketMechanism marketMechanism =  copyFromElementToMarketMechanism(element);
            allMarketMechanisms[i++] = marketMechanism;
        }
        return allMarketMechanisms;
    }

    /**
     * Parses an element in the DOM tree structure to an Iteration
     * @param element
     * @param shift
     * @return
     * @throws Exception
     */
    private Iteration copyFromElementToIteration(Element element, int shift) throws Exception
    {
        int maximumTimeForCompletion = 0;
        SLA sla = new SLA(Priority.valueOf(element.getChildText("priority")),
                            Reliability.valueOf(element.getChildText("reliability")),
                            maximumTimeForCompletion,
                            Region.valueOf(element.getChildText("region")));
        
        Iteration iteration = new Iteration(Integer.valueOf(element.getChildText("id")),
                                            Integer.valueOf(element.getChildText("numberOfUsers")),
                                            Double.valueOf(element.getChildText("scheduledTime")),
                                            Integer.valueOf(element.getChildText("miPerJob")),
                                            Integer.valueOf(element.getChildText("mipsPerVm")),
                                            Integer.valueOf(element.getChildText("minimumReputation")),
                                            sla,
                                            shift,
                                            Double.valueOf(element.getChildText("maximumProfit")));
       return iteration;
    }

    /**
     * Parses an element in the DOM tree structure to a MarketMechanism
     * @param element
     * @return
     * @throws Exception
     */
    private MarketMechanism copyFromElementToMarketMechanism(Element element) throws Exception
    {
       MarketMechanism marketMechanism = null;
       if(PostedOffer.NAME.equals(element.getChildText("type")))
       {
           marketMechanism = new PostedOffer(Double.valueOf(element.getChildText("weightForReputation")));
       }
       else if(ReverseAuction.NAME.equals(element.getChildText("type")))
       {
           marketMechanism = new ReverseAuction(Double.valueOf(element.getChildText("weightForReputation")));
       }
       
       return marketMechanism;
    }

    /**
     * Open the file for export or import operations
     */
    private void open()
    {
        SAXBuilder builder = new SAXBuilder();
        try
        {
            document = builder.build(file);
        }
        catch (JDOMException ex)
        {
            Logger.getLogger(XMLProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(XMLProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Save the scenario in the file
     */
    private void save()
    {        
        XMLOutputter XMLOut = new XMLOutputter();
        try
        {
            XMLOut.output(document, new FileOutputStream(file));
        }
        catch (IOException ex)
        {
            Logger.getLogger(XMLProcessor.class.getName()).log(Level.SEVERE, "Error saving document: ", ex);
        }
    }

    /**
     * Parses a SellerAgent to an element in the DOM tree structure
     * @param sellerAgent
     * @param newElement
     */
    private void copyFromSellerAgentToElement(DummySellerAgent sellerAgent, Element newElement)
    {
       newElement.addContent(new Element("name").setText(sellerAgent.getName()));
       newElement.addContent(new Element("initialReputation").setText(sellerAgent.getReputation().toString()));
       newElement.addContent(new Element("minimumProfit").setText(sellerAgent.getMinimumProfitPerMi().toString()));
       newElement.addContent(new Element("preferredProfit").setText(sellerAgent.getPreferredProfitPerMi().toString()));
       newElement.addContent(new Element("mipsPerCore").setText(sellerAgent.getMipsPerCore().toString()));
       newElement.addContent(new Element("probabilityToSucceed").setText(sellerAgent.getProbabilityToSucceed().toString()));
       newElement.addContent(new Element("region").setText(sellerAgent.getRegion().toString()));
    }

    /**
     * Parses an MarketMechanism to an element in the DOM tree structure
     * @param marketMechanism
     * @param newElement
     */
    private void copyFromMarketMechanismToElement(MarketMechanism marketMechanism, Element newElement)
    {
        newElement.addContent(new Element("type").setText(marketMechanism.getName()));
        newElement.addContent(new Element("weightForReputation").setText(marketMechanism.getWeightForReputation().toString()));
    }

    /**
     * Parses an Iteration of group of jobs to an element in the DOM tree structure
     * @param iteration
     * @param newElement
     */
    private void copyFromIterationToElement(Iteration iteration, Element newElement)
    {
        newElement.addContent(new Element("id").setText(iteration.getId().toString()));
        newElement.addContent(new Element("numberOfUsers").setText(iteration.getNumberOfUsers().toString()));
        newElement.addContent(new Element("scheduledTime").setText(iteration.getScheduledTime().toString()));
        newElement.addContent(new Element("miPerJob").setText(iteration.getMiPerCloudlet().toString()));
        newElement.addContent(new Element("mipsPerVm").setText(iteration.getMipsPerVm().toString()));
        newElement.addContent(new Element("minimumReputation").setText(iteration.getMinimumReputation().toString()));
        newElement.addContent(new Element("maximumProfit").setText(iteration.getMaximumProfit().toString()));
        newElement.addContent(new Element("region").setText(iteration.getSla().getRegion().toString()));
        newElement.addContent(new Element("priority").setText(iteration.getSla().getPriority().toString()));
        newElement.addContent(new Element("reliability").setText(iteration.getSla().getReliability().toString()));
    }

}
