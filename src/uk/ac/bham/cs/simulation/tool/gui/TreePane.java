package uk.ac.bham.cs.simulation.tool.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import uk.ac.bham.cs.simulation.market.MarketMechanism;
import uk.ac.bham.cs.simulation.tool.Iteration;
import uk.ac.bham.cs.simulation.tool.Util;

/**
 * This class builds the tree of the simulator
 * @author Carlos Mera Gómez
 * @version 1.0, 02/08/2011
 */
public class TreePane extends JTree
{    
    private DefaultTreeModel        model    = null;
    private DefaultMutableTreeNode  root, sellerRoot, tradingRoot, jobRoot;
    
    /**
     * 
     */
    public TreePane()
    {
        super();
        init();
    }
    
    /**
     * 
     */
    private void init()
    {        
        //Toolkit toolKit = Toolkit.getDefaultToolkit();
        root = new DefaultMutableTreeNode("Scenario");
        model = new DefaultTreeModel(root, true);
        setModel(model);
        addMainChildren();
        addSellerNode();
        addJobNode();
        addTradingNode("Posted Offer");
        addTradingNode("Reverse Auction");
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);	
	CustomRenderer customRenderer = new CustomRenderer();
	setCellRenderer(customRenderer);
        ToolTipManager.sharedInstance().registerComponent(this);
        Font f = customRenderer.getFont();
	customRenderer.setFont(f.deriveFont(10f));
        setShowsRootHandles(true);
        expandRow(0);
        setPreferredSize(new Dimension(160, 200));
    }   
    
    /**
     * 
     */
    public void addMainChildren()
    {
        sellerRoot = new DefaultMutableTreeNode("Sellers",true);
        model.insertNodeInto(sellerRoot, root, root.getChildCount());       
        tradingRoot = new DefaultMutableTreeNode("Trading",true);
        model.insertNodeInto(tradingRoot, root, root.getChildCount());
        jobRoot = new DefaultMutableTreeNode("Scheduled Jobs",true);
        model.insertNodeInto(jobRoot, root, root.getChildCount());
        scrollPathToVisible(new TreePath(root.getPath()));
    }
    
    /**
     * 
     */
    public void addSellerNode()
    {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("Seller Agent",false);
        //sellerRoot.add(node);
        model.insertNodeInto(node, sellerRoot, sellerRoot.getChildCount());
        scrollPathToVisible(new TreePath(node.getPath()));
    }
    
    /**
     * 
     */
    public void addJobNode()
    {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("Group of Jobs",false);
        //jobRoot.add(node);
        model.insertNodeInto(node, jobRoot, jobRoot.getChildCount());
        scrollPathToVisible(new TreePath(node.getPath()));
    }
    
    /**
     * 
     * @param type 
     */
    public void addTradingNode(String type)
    {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(type,false);
        //tradingRoot.add(node);
        model.insertNodeInto(node, tradingRoot, tradingRoot.getChildCount());
        scrollPathToVisible(new TreePath(node.getPath()));
        model.nodeStructureChanged(tradingRoot);
    }
    
    /**
     * 
     */
    public void removeSellerNode()
    { 
        if(sellerRoot.getChildCount()>1)
        {
            sellerRoot.remove(0);
        }
        model.nodeStructureChanged(sellerRoot);
    }
    
    /**
     * 
     */
    public void removeJobNode()
    {
        if(jobRoot.getChildCount()>1)
        {
            jobRoot.remove(0);
        }
        model.nodeStructureChanged(jobRoot);
    }
  
    private class CustomRenderer extends DefaultTreeCellRenderer
    {
	ImageIcon sellersIcon, tradingIcon, jobsIcon, sellerIcon, jobIcon, postedOfferIcon, reverseAuctionIcon, rootIcon;
	public CustomRenderer()
	{
            super();
            createIcons();
	}

        private void createIcons()
        {
            sellersIcon         = Util.createImageIcon(this, "/images/administracion.gif");
            tradingIcon         = Util.createImageIcon(this, "/images/find.gif");
            jobsIcon            = Util.createImageIcon(this, "/images/tareas.gif");
            sellerIcon          = Util.createImageIcon(this, "/images/seller.gif");
            jobIcon             = Util.createImageIcon(this, "/images/job.gif");
            postedOfferIcon     = Util.createImageIcon(this, "/images/campo.gif");
            reverseAuctionIcon  = Util.createImageIcon(this, "/images/funcion.gif");
            rootIcon            = Util.createImageIcon(this, "/images/computer.gif");
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree,Object value,boolean sel,boolean expanded,boolean leaf,int row,boolean hasFocus)
        {
            super.getTreeCellRendererComponent(tree, value, sel,expanded, leaf, row,hasFocus);
            if(leaf & value.toString().equals("Seller Agent")) //instanceof DefaultMutableTreeNode)
            {
                setIcon(sellerIcon);
                setToolTipText("Seller Agent");
            }
            else if (leaf & value.toString().equals("Group of Jobs")) //instanceof DefaultMutableTreeNode)
            {
                setIcon(jobIcon);
                setToolTipText("Group of Jobs");
            }
            else if (leaf & value.toString().equals("Posted Offer")) //instanceof DefaultMutableTreeNode)
            {
                setIcon(postedOfferIcon);
                setToolTipText("Posted Offer");
            }
            else if (leaf & value.toString().equals("Reverse Auction")) //instanceof DefaultMutableTreeNode)
            {
                setIcon(reverseAuctionIcon);
                setToolTipText("Reverse Auction");
            }
            else if(value.toString().equals("Sellers")) //instanceof DefaultMutableTreeNode)
            {
                setIcon(sellersIcon);
                setToolTipText("Sellers");
            }
           
            else if(value.toString().equals("Trading"))
            {
                setIcon(tradingIcon);
                setToolTipText("Trading");
            }
            else if(value.toString().equals("Scheduled Jobs"))
            {
                setIcon(jobsIcon);
                setToolTipText("Scheduled Jobs");
            }
            else if (value==root)
            {
                setIcon(rootIcon);
                setToolTipText("Scenario");
            }
            return this;
        }
    }

    public void importScenario(DummySellerAgent[] sellerAgents, MarketMechanism[] marketMechanisms, Iteration[] groupOfJobs)
    {
        removeAllSellerAgentNodes();
        for (int i = 0; i < sellerAgents.length; i++)
        {
            addSellerNode();
        }

        /*removeAllMarketMechanismNodes();
        for (int i = 0; i < marketMechanisms.length; i++)
        {
            addTradingNode(marketMechanisms[i].getName());
        }*/

        removeAllJobNodes();
        for (int i = 0; i < groupOfJobs.length; i++)
        {
            addJobNode();
        }

    }

    public void removeAllSellerAgentNodes()
    {
        while(sellerRoot.getChildCount()>0)
        {
           DefaultMutableTreeNode node = (DefaultMutableTreeNode)sellerRoot.getChildAt(sellerRoot.getChildCount()-1);
           model.removeNodeFromParent(node);
        }        
    }

    /*public void removeAllMarketMechanismNodes()
    {
        while(tradingRoot.getChildCount()>0)
        {
           DefaultMutableTreeNode node = (DefaultMutableTreeNode)tradingRoot.getChildAt(tradingRoot.getChildCount()-1);
           model.removeNodeFromParent(node);
        }
    }*/

    public void removeAllJobNodes()
    {
        while(jobRoot.getChildCount()>0)
        {
           DefaultMutableTreeNode node = (DefaultMutableTreeNode)jobRoot.getChildAt(jobRoot.getChildCount()-1);
           model.removeNodeFromParent(node);
        }
    }

}