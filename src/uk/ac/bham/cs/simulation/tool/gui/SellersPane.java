package uk.ac.bham.cs.simulation.tool.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import uk.ac.bham.cs.simulation.cloud.Region;
import uk.ac.bham.cs.simulation.cloud.SellerAgent;
import uk.ac.bham.cs.simulation.tool.Util;

/**
 * This panel displays the list of Seller Agents in the GUI
 * @author  Carlos Mera Gómez
 * @version 1.0, 02/08/2011
 */
public class SellersPane extends JPanel implements ActionListener, ListSelectionListener
{
    private JTable          tblSellerAgents;
    private JTextField      txtName, txtInitialReputation, txtMinimumProfit, txtPreferredProfit,
                            txtMipsPerCore, txtProbabilityToSucceed;
    private JComboBox       cmbRegion;
    private Vector<Vector>  data;
    private MyTableModel    model;
    private Integer         selectedRow = -1;
    private JPanel          detailPane, buttonsPane;
    private Integer         nextLetter;
    private JButton         btnSave, btnDelete;
    private Mediator        mediator;
    
    /**
     * 
     * @param mediator 
     */
    public SellersPane(Mediator mediator)
    {
        super(new BorderLayout());
        init();
        this.mediator = mediator;
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Seller Agents"), BorderFactory.createEmptyBorder(5,5,5,5)));
        model = new MyTableModel(data,makeHeader());
	makeDefaultData();
        makeTable();
        tblSellerAgents.setPreferredScrollableViewportSize(new Dimension(800,400));
        tblSellerAgents.getColumnModel().getColumn(5).setPreferredWidth(120);
        JScrollPane scroll = new JScrollPane(tblSellerAgents);
        scroll.setPreferredSize(new Dimension(100,200));
        add(scroll, BorderLayout.NORTH);
    }
    
    /**
     * 
     */
    private void makeTable()
    {
        tblSellerAgents = new JTable(model);
        tblSellerAgents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	ListSelectionModel lsm = tblSellerAgents.getSelectionModel();
	lsm.addListSelectionListener(this);
    }
    
    /**
     * 
     */
    private void init()
    {
        data = new Vector<Vector>();        
        nextLetter = 0;
        initFields();
    }
    
    /**
     * 
     * @return 
     */
    private Vector<String> makeHeader()
    {
	Vector<String> header = new Vector<String>();
        header.add("Name");
	header.add("Initial Reputation");
        header.add("Minimum Profit");
        header.add("Preferred Profit");
        header.add("Mips Per Core");
        header.add("Probability To Succeed");
        header.add("Region");
        return header;
    }
    
    /**
     * 
     */
    private void makeNewData()
    {        
        Vector row = new Vector();
        row.add("SellerAgent"+getLetter());
        row.add("25");
        row.add("0.30");
        row.add("0.45");
        row.add("500");
        row.add("0.98");
        row.add(Region.NORTH);
        model.insertRow(model.getRowCount(),row);
    }
    
    /**
     * 
     */
    private void makeDefaultData()
    {
        Vector row = new Vector();
        row.add("SellerAgent"+getLetter());
        row.add("25");
        row.add("0.25");
        row.add("0.40");
        row.add("250");
        row.add("0.95");
        row.add(Region.NORTH);
        model.insertRow(model.getRowCount(),row);
    }
    
    /**
     * 
     */
    private void deleteData()
    {
        if (selectedRow>=0 && model.getRowCount()>1)
        {
            model.removeRow(selectedRow);
            if(selectedRow==0&&model.getRowCount()>0)
            {
                selectedRow = 0;
            }
            else
            {
                selectedRow--;
            }
            updateFormModel();
            tblSellerAgents.getSelectionModel().setLeadSelectionIndex(selectedRow);            
        }
        else if(model.getRowCount()==1)
        {
            JOptionPane.showMessageDialog(this, "At least one Seller Agent is required to start simulation", "Invalid Action", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void enableButtons(boolean enable)
    {
        btnSave.setEnabled(enable);
        btnDelete.setEnabled(enable);
    }
    
    /**
     * 
     * @return 
     */
    private String getLetter()
    {
        String[] alphabet = {"A","B","C","D","F","G","H","I","J","K","L","M","N",
                            "O","P","Q","R","S","T","U","V","W", "X", "Y", "Z"};
        String letter =  alphabet[nextLetter++];
        if(nextLetter == alphabet.length)
        {
           nextLetter = 0;  
        }
        return letter;
    }
    
    /**
     * 
     */
    private void initFields()
    {
        txtName                 = new JTextField(15);
        txtName.setDocument(new TextPlainDocument(15, false));
        txtInitialReputation    = new JTextField(10);
        txtInitialReputation.setDocument(new NumberPlainDocument(SellerAgent.MINIMUM_REPUTATION, SellerAgent.MAXIMUM_REPUTATION, 0));
        txtMinimumProfit        = new JTextField(10);
        txtMinimumProfit.setDocument(new NumberPlainDocument(0, 1, 3));
        txtPreferredProfit      = new JTextField(10);
        txtPreferredProfit.setDocument(new NumberPlainDocument(0, 1, 3));
        txtMipsPerCore          = new JTextField(10);
        txtMipsPerCore.setDocument(new NumberPlainDocument(1, 1000000, 0));
        txtProbabilityToSucceed  = new JTextField(10);
        txtProbabilityToSucceed.setDocument(new NumberPlainDocument(0, 1, 3));
        Region[] items          = {Region.NORTH, Region.SOUTH, Region.EAST, Region.WEST};
        cmbRegion               = new JComboBox(items);
    }
    
    /**
     * 
     * @return 
     */
    private boolean isFormComplete()
    {
        boolean complete = false;
        if(txtName.getText().length()==0)
        {
            JOptionPane.showMessageDialog(this, "Name must have a value", "Validation error", JOptionPane.ERROR_MESSAGE);
        }
        else if (txtInitialReputation.getText().length()==0)
        {
            JOptionPane.showMessageDialog(this, "Initial Reputation must have a value", "Validation error", JOptionPane.ERROR_MESSAGE);
        }
        else if (txtMinimumProfit.getText().length()==0)
        {
            JOptionPane.showMessageDialog(this, "Minimum Profit must have a value", "Validation error", JOptionPane.ERROR_MESSAGE);
        }
        else if (txtPreferredProfit.getText().length()==0)
        {
            JOptionPane.showMessageDialog(this, "Preferred Profit must have a value", "Validation error", JOptionPane.ERROR_MESSAGE);
        }
        else if (txtMipsPerCore.getText().length()==0)
        {
            JOptionPane.showMessageDialog(this, "Mips Per Core must have a value", "Validation error", JOptionPane.ERROR_MESSAGE);
        }
        else if (txtProbabilityToSucceed.getText().length()==0)
        {
            JOptionPane.showMessageDialog(this, "Probability To Succeed must have a value", "Validation error", JOptionPane.ERROR_MESSAGE);
        }
        else
        {
            complete = true;
        }

        return complete;
    }
    
    /**
     * 
     * @return 
     */
    private JPanel makeDetailPane()
    {
        JPanel detailPane = new JPanel(new GridBagLayout());        
        detailPane.setBorder(BorderFactory.createTitledBorder("Seller Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0,0,3,40);
        detailPane.add(new JLabel("Name *"), gbc);
        gbc.gridx=1;
        gbc.gridy=0;
        
        detailPane.add(txtName, gbc);
        gbc.gridx=2;
        gbc.gridy=0;
        detailPane.add(new JLabel("Initial Reputation *"), gbc);
        gbc.gridx=3;
        gbc.gridy=0;
        detailPane.add(txtInitialReputation, gbc);
        gbc.gridx=0;
        gbc.gridy=1;
        detailPane.add(new JLabel("Minimum Profit *"), gbc);
        gbc.gridx=1;
        gbc.gridy=1;
        detailPane.add(txtMinimumProfit, gbc);
        gbc.gridx=2;
        gbc.gridy=1;
        detailPane.add(new JLabel("Preferred Profit *"), gbc);
        gbc.gridx=3;
        gbc.gridy=1;
        detailPane.add(txtPreferredProfit, gbc);
        gbc.gridx=0;
        gbc.gridy=2;
        detailPane.add(new JLabel("Mips Per Core *"), gbc);
        gbc.gridx=1;
        gbc.gridy=2;
        detailPane.add(txtMipsPerCore, gbc);
        gbc.gridx=2;
        gbc.gridy=2;
        detailPane.add(new JLabel("Probability To Suceed *"), gbc);
        gbc.gridx=3;
        gbc.gridy=2;
        detailPane.add(txtProbabilityToSucceed, gbc);
        gbc.gridx=0;
        gbc.gridy=3;
        detailPane.add(new JLabel("Region *"), gbc);
        gbc.gridx=1;
        gbc.gridy=3;
        detailPane.add(cmbRegion, gbc);
                
        return detailPane;
    }
    
    /**
     * 
     * @return 
     */
    private JPanel makeButtonsPane()
    {
        JPanel buttonsPane = new JPanel();
        JButton btnAddDefaultOne = new JButton("Add Default One", Util.createImageIcon(this, "/images/crear.gif"));
        btnAddDefaultOne.setActionCommand("addEmpty");
        btnAddDefaultOne.addActionListener(this);
        JButton btnAddDefault = new JButton("Add Default Two", Util.createImageIcon(this, "/images/addGroup.gif"));
        btnAddDefault.setActionCommand("addDefault");
        btnAddDefault.addActionListener(this);
        btnSave = new JButton("Save", Util.createImageIcon(this, "/images/save-icon.png"));
        btnSave.setActionCommand("save");
        btnSave.addActionListener(this);
        btnDelete = new JButton("Delete", Util.createImageIcon(this, "/images/delete-trash.png"));
        btnDelete.setActionCommand("delete");
        btnDelete.addActionListener(this);
        buttonsPane.add(btnAddDefaultOne);
        buttonsPane.add(btnAddDefault);
        buttonsPane.add(btnSave);
        buttonsPane.add(btnDelete);
        return buttonsPane;
    }
    
    /**
     * 
     */
    private void updateTableModel()
    {
        if(selectedRow>=0)
        {
           model.setValueAt(txtName.getText(), selectedRow, 0);
           model.setValueAt(txtInitialReputation.getText(), selectedRow, 1);
           model.setValueAt(txtMinimumProfit.getText(), selectedRow, 2);
           model.setValueAt(txtPreferredProfit.getText(), selectedRow, 3);
           model.setValueAt(txtMipsPerCore.getText(), selectedRow, 4); 
           model.setValueAt(txtProbabilityToSucceed.getText(), selectedRow, 5);
           model.setValueAt(cmbRegion.getSelectedItem(), selectedRow, 6);            
        }
    }
    
    /**
     * 
     */
    private void updateFormModel()
    {
        if(selectedRow>=0)
        {
           txtName.setText(model.getValueAt(selectedRow, 0).toString());
           txtInitialReputation.setText(model.getValueAt(selectedRow, 1).toString());
           txtMinimumProfit.setText(model.getValueAt(selectedRow, 2).toString());
           txtPreferredProfit.setText(model.getValueAt(selectedRow, 3).toString());
           txtMipsPerCore.setText(model.getValueAt(selectedRow, 4).toString());
           txtProbabilityToSucceed.setText(model.getValueAt(selectedRow, 5).toString());           
           cmbRegion.setSelectedItem(model.getValueAt(selectedRow,6));
        }
    }
    
    /**
     * 
     */
    private void resetFormModel()
    {
       txtName.setText("");
       txtInitialReputation.setText("");
       txtMinimumProfit.setText("");
       txtPreferredProfit.setText("");
       txtMipsPerCore.setText("");
       txtProbabilityToSucceed.setText("");
       cmbRegion.setSelectedItem(Region.NORTH);
    }
    
    /**
     * 
     */
    private void selectAndShowNewData()
    {
        tblSellerAgents.getSelectionModel().setLeadSelectionIndex(model.getRowCount()-1);
        tblSellerAgents.scrollRectToVisible(tblSellerAgents.getCellRect(selectedRow,0,true));
        if(model.getRowCount()==1)
        {
            enableButtons(true);
        }
    }
    
    /**
     * 
     * @return
     * @throws Exception 
     */
    public List<SellerAgent> createSellerAgents() throws Exception
    {
        List<SellerAgent> sellerAgents = new ArrayList<SellerAgent>();
        for(int i=0; i< model.getRowCount(); i++)
        {
            SellerAgent sellerAgent = SellerAgent.createSellerAgent(model.getValueAt(i, 0).toString(),
                                                                    new Double(model.getValueAt(i, 3).toString()),
                                                                    new Double(model.getValueAt(i, 2).toString()),
                                                                    new Double(model.getValueAt(i, 5).toString()),
                                                                    new Integer(model.getValueAt(i, 4).toString()),
                                                                    new Integer(model.getValueAt(i, 1).toString()),
                                                                    (Region)model.getValueAt(i, 6));
            sellerAgents.add(sellerAgent);
        }
        return sellerAgents;
    }
    
    /**
     * 
     * @return 
     */
    public List<DummySellerAgent> createDummySellerAgents()
    {
        List<DummySellerAgent> dummySellerAgents = new ArrayList<DummySellerAgent>();
        for(int i=0; i< model.getRowCount(); i++)
        {
            DummySellerAgent dummySellerAgent = new DummySellerAgent(model.getValueAt(i, 0).toString(),
                                                                    new Double(model.getValueAt(i, 3).toString()),
                                                                    new Double(model.getValueAt(i, 2).toString()),
                                                                    new Double(model.getValueAt(i, 5).toString()),
                                                                    new Integer(model.getValueAt(i, 4).toString()),
                                                                    new Integer(model.getValueAt(i, 1).toString()),
                                                                    (Region)model.getValueAt(i, 6));
            dummySellerAgents.add(dummySellerAgent);
        }
        return dummySellerAgents;
    }
    
    /**
     * 
     * @param previousSellerAgents
     * @return
     * @throws Exception 
     */
    public List<SellerAgent> reCreateSellerAgents(List<SellerAgent> previousSellerAgents) throws Exception
    {
        List<SellerAgent> sellerAgents = new ArrayList<SellerAgent>();
        for(SellerAgent previousSellerAgent : previousSellerAgents)
        {
            SellerAgent sellerAgent = SellerAgent.createSellerAgent(previousSellerAgent.getName(),
                                                                    previousSellerAgent.getPreferredProfitPerMi(),
                                                                    previousSellerAgent.getMinimumProfitPerMi(),
                                                                    previousSellerAgent.getProbabilityToSucceed(),
                                                                    previousSellerAgent.getMipsPerCore(),
                                                                    previousSellerAgent.getReputation(),
                                                                    previousSellerAgent.getRegion());
            sellerAgent.setSuccessfulJobs(previousSellerAgent.getSuccessfulJobs());
            sellerAgent.setFailedJobs(previousSellerAgent.getFailedJobs());
            sellerAgents.add(sellerAgent);
        }
        return sellerAgents;
    }
    
    /**
     * 
     * @param e 
     */
    public void actionPerformed(ActionEvent e)
    {
        if("addEmpty".equals(e.getActionCommand()))
        {
            makeNewData();
            selectAndShowNewData();
            mediator.addSellerAgentTree();
        }
        else if ("addDefault".equals(e.getActionCommand()))
        {
            makeDefaultData();
            selectAndShowNewData();
            mediator.addSellerAgentTree();
        }
        else if ("save".equals(e.getActionCommand()))
        {
            if(isFormComplete())
            {
                updateTableModel();
            }
        }
        else if("delete".equals(e.getActionCommand()))
        {
            deleteData();
            mediator.deleteSellerAgentTree();
        }

    }
    
    /**
     * 
     * @param e 
     */
    public void valueChanged(ListSelectionEvent e)
    {
        if (e.getValueIsAdjusting()) return;
        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
        if (!lsm.isSelectionEmpty() && lsm == tblSellerAgents.getSelectionModel())
        {
	    selectedRow = lsm.getMinSelectionIndex();
            if(detailPane==null)
            {
                detailPane = makeDetailPane();
                add(detailPane, BorderLayout.CENTER);
                buttonsPane = makeButtonsPane();
                add(buttonsPane, BorderLayout.SOUTH);
                validate();
                repaint();
            }
                
            updateFormModel();

        }
    }
    
    /**
     * 
     * @param sellerAgents 
     */
    public void importScenario(DummySellerAgent[] sellerAgents)
    {        
        while(model.getRowCount()>0)
        {
            model.removeRow(model.getRowCount()-1);
        }
                      
        for (int i = 0; i < sellerAgents.length; i++)
        {
            DummySellerAgent sellerAgent = sellerAgents[i];
            importData(sellerAgent);
        }
        selectedRow = -1;
        if(detailPane!=null)
        {
            remove(detailPane);
            remove(buttonsPane);
            validate();
            repaint();
            detailPane = null;
        }
    }
    
    /**
     * 
     * @param sellerAgent 
     */
    private void importData(DummySellerAgent sellerAgent)
    {
        Vector row = new Vector();
        row.add(sellerAgent.getName());
        row.add(sellerAgent.getReputation().toString());
        row.add(sellerAgent.getMinimumProfitPerMi().toString());
        row.add(sellerAgent.getPreferredProfitPerMi().toString());
        row.add(sellerAgent.getMipsPerCore().toString());
        row.add(sellerAgent.getProbabilityToSucceed().toString());
        row.add(sellerAgent.getRegion());
        model.insertRow(model.getRowCount(),row);
    }

}
