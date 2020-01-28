package uk.ac.bham.cs.simulation.tool.gui;

/**
 * This class implements is the Mediator to decouple components interaction
 * @author  Carlos Mera Gómez
 * @version 1.0, 11/08/2011
 */
public class Mediator
{
    private SellersPane     sellersPane;
    private TreePane        treePane;
    private BuyersPane      buyersPane;
    
    /**
     * Constructor
     */
    public Mediator()
    {

    }

    /**
     * @return the sellersPane
     */
    public SellersPane getSellersPane()
    {
        return sellersPane;
    }

    /**
     * @param sellersPane the sellersPane to set
     */
    public void setSellersPane(SellersPane sellersPane)
    {
        this.sellersPane = sellersPane;
    }

    /**
     * @return the treePane
     */
    public TreePane getTreePane()
    {
        return treePane;
    }

    /**
     * @param treePane the treePane to set
     */
    public void setTreePane(TreePane treePane)
    {
        this.treePane = treePane;
    }

    /**
     * @return the buyersPane
     */
    public BuyersPane getBuyersPane()
    {
        return buyersPane;
    }

    /**
     * @param buyersPane the buyersPane to set
     */
    public void setBuyersPane(BuyersPane buyersPane)
    {
        this.buyersPane = buyersPane;
    }

    public void addSellerAgentTree()
    {
        treePane.addSellerNode();
    }

    public void addGroupOfJobTree()
    {
        treePane.addJobNode();
    }

    public void deleteSellerAgentTree()
    {
        treePane.removeSellerNode();
    }

    public void deleteGroupOfJobTree()
    {
        treePane.removeJobNode();
    }

}
