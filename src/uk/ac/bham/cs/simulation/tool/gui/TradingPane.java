package uk.ac.bham.cs.simulation.tool.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import uk.ac.bham.cs.simulation.market.Bargaining;
import uk.ac.bham.cs.simulation.market.MarketMechanism;
import uk.ac.bham.cs.simulation.market.PostedOffer;
import uk.ac.bham.cs.simulation.market.ReverseAuction;

/**
 * This class displays the trading pane in the GUI
 * @author  Carlos Mera Gómez
 * @version 1.0, 02/08/2011
 */
public class TradingPane extends JPanel implements FocusListener
{
    private static final String DEFAULT_WEIGHT = "0.3";
    private JTextField      txtWeightForReputationPostedOffer, txtWeightForReputationReverseAuction, txtWeightForReputationBargaining;
    private JRadioButton    rdbPostedOffer, rdbReverseAuction, rdbBargaining, rdbBoth;
    
    /**
     * 
     */
    public TradingPane()
    {
        super(new GridBagLayout());
        init();
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Trading Mechanism"), BorderFactory.createEmptyBorder(5,5,5,5)));
        makeTradePane();
    }
    
    /**
     * 
     */
    private void init()
    {
        initFields();
    }
    
    /**
     * 
     */
    private void initFields()
    {        
        rdbPostedOffer      = new JRadioButton("Posted Offer");
        rdbPostedOffer.setMnemonic(KeyEvent.VK_P);
        rdbReverseAuction   = new JRadioButton("Reverse Auction");
        rdbReverseAuction.setMnemonic(KeyEvent.VK_R);
        rdbBargaining = new JRadioButton("Bargaining");
        rdbBargaining.setMnemonic(KeyEvent.VK_C); //Ni idea porque puse esto
        rdbBoth             = new JRadioButton("Both");
        rdbBoth.setMnemonic(KeyEvent.VK_B);
        ButtonGroup group = new ButtonGroup();
        group.add(rdbPostedOffer);
        group.add(rdbReverseAuction);
        group.add(rdbBargaining);
        group.add(rdbBoth);
        rdbBoth.setSelected(true);     

        txtWeightForReputationPostedOffer = new JTextField(5);
        txtWeightForReputationPostedOffer.setDocument(new NumberPlainDocument(0, 1, 2));
        txtWeightForReputationPostedOffer.setText(DEFAULT_WEIGHT);
        txtWeightForReputationPostedOffer.addFocusListener(this);

        txtWeightForReputationReverseAuction = new JTextField(5);
        txtWeightForReputationReverseAuction.setDocument(new NumberPlainDocument(0, 1, 2));
        txtWeightForReputationReverseAuction.setText(DEFAULT_WEIGHT);
        
        txtWeightForReputationBargaining = new JTextField(5);
        txtWeightForReputationBargaining.setDocument(new NumberPlainDocument(0, 1, 2));
        txtWeightForReputationBargaining.setText(DEFAULT_WEIGHT);
        
        
    }
    
    /**
     * 
     */
    private void makeTradePane()
    {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0,0,3,40);
        add(rdbPostedOffer, gbc);
        gbc.gridx=1;
        gbc.gridy=0;
        add(new JLabel("Weight for Reputation in Posted Offer *"), gbc);
        gbc.gridx=2;
        gbc.gridy=0;
        add(txtWeightForReputationPostedOffer, gbc);
        gbc.gridx=0;
        gbc.gridy=1;
        add(rdbReverseAuction, gbc);
        gbc.gridx=1;
        gbc.gridy=1;
        add(new JLabel("Weight for Reputation in Reverse Auction *"), gbc);
        gbc.gridx=2;
        gbc.gridy=1;
        add(txtWeightForReputationReverseAuction, gbc);
        gbc.gridx=0;
        gbc.gridy=2;
        //Bargaining
        add(rdbBargaining, gbc);
        gbc.gridx=1;
        gbc.gridy=2;
        add(new JLabel("Weight for Reputation in Bargaining *"), gbc);
        gbc.gridx=2;
        gbc.gridy=2;
        add(txtWeightForReputationBargaining, gbc);
        gbc.gridx=0;
        gbc.gridy=3;
        //
        add(rdbBoth, gbc);

    }
    
    /**
     * 
     * @return 
     */
    public List<MarketMechanism> createMarketMechanism()
    {
        List<MarketMechanism> marketMechanisms = new ArrayList<MarketMechanism>();
        if(rdbPostedOffer.isSelected())
        {
            marketMechanisms.add(new PostedOffer(new Double(txtWeightForReputationPostedOffer.getText())));
        }
        else if(rdbReverseAuction.isSelected())
        {
            marketMechanisms.add(new ReverseAuction(new Double(txtWeightForReputationReverseAuction.getText())));
        }
        else if(rdbBargaining.isSelected())
        {
            marketMechanisms.add(new Bargaining(new Double(txtWeightForReputationBargaining.getText())));
        }
        else if(rdbBoth.isSelected())
        {
            marketMechanisms.add(new PostedOffer(new Double(txtWeightForReputationPostedOffer.getText())));
            marketMechanisms.add(new ReverseAuction(new Double(txtWeightForReputationReverseAuction.getText())));
        }
        return marketMechanisms;
    }

    /**
     * 
     * @param marketMechanisms 
     */
    public void importScenario(MarketMechanism[] marketMechanisms)
    {
        boolean markPostedOffer = false;
        boolean markReverseAuction = false;
        boolean markBargaining = false;
        for (int i = 0; i < marketMechanisms.length; i++)
        {
            if(PostedOffer.NAME.equals(marketMechanisms[i].getName()))
            {
                txtWeightForReputationPostedOffer.setText(marketMechanisms[i].getWeightForReputation().toString());
                markPostedOffer = true;
            }
            else if(ReverseAuction.NAME.equals(marketMechanisms[i].getName()))
            {
                txtWeightForReputationReverseAuction.setText(marketMechanisms[i].getWeightForReputation().toString());
                markReverseAuction = true;
            }
            else if(Bargaining.NAME.equals(marketMechanisms[i].getName()))
            {
                txtWeightForReputationBargaining.setText(marketMechanisms[i].getWeightForReputation().toString());
                markBargaining = true;
            }
        }
        if(markPostedOffer && markReverseAuction)
        {
            rdbBoth.setSelected(true);
        }
        else
        {
            rdbPostedOffer.setSelected(markPostedOffer);
            rdbReverseAuction.setSelected(markReverseAuction);
        }

    }
    
    /**
     * 
     * @param e 
     */
    public void focusGained(FocusEvent e)
    {
        return;
    }
    
    /**
     * 
     * @param e 
     */
    public void focusLost(FocusEvent e)
    {
        if(e.getSource()==txtWeightForReputationPostedOffer && txtWeightForReputationPostedOffer.getText().isEmpty())
        {
            txtWeightForReputationPostedOffer.setText(DEFAULT_WEIGHT);
        }
        else if(e.getSource() == txtWeightForReputationReverseAuction && txtWeightForReputationReverseAuction.getText().isEmpty())
        {
            txtWeightForReputationReverseAuction.setText(DEFAULT_WEIGHT);
        }
        else if(e.getSource() == txtWeightForReputationBargaining && txtWeightForReputationBargaining.getText().isEmpty())
        {
            txtWeightForReputationBargaining.setText(DEFAULT_WEIGHT);
        }
    }

}
