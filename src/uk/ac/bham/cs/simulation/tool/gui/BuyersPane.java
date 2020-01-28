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
import org.cloudbus.cloudsim.BuyerAgent;
import uk.ac.bham.cs.simulation.cloud.Region;
import uk.ac.bham.cs.simulation.cloud.SLA;
import uk.ac.bham.cs.simulation.cloud.SLA.Priority;
import uk.ac.bham.cs.simulation.cloud.SLA.Reliability;
import uk.ac.bham.cs.simulation.tool.Iteration;
import uk.ac.bham.cs.simulation.tool.Util;

/**
 * This class build the panel with the list of scheduled jobs
 * @author  Carlos Mera Gómez
 * @version 1.0, 02/08/2011
 */
public class BuyersPane extends JPanel implements ActionListener, ListSelectionListener
{
    private JTable          tblBuyerAgents;
    private JTextField      txtIdIteration, txtNumberOfUsers, txtScheduledTime, txtMiPerCloudlet,
                            txtMipsPerVm, txtMinimumReputation, txtMaximumProfit;
    private JComboBox       cmbSlaRegion, cmbSlaPriority, cmbSlaReliability;
    private Vector<Vector>  data;
    private MyTableModel    model;
    private Integer         selectedRow =-1;
    private JPanel          detailPane, buttonsPane;
    private Integer         nextNumber;
    private JButton         btnSave, btnDelete;
    private Mediator        mediator;
    
    /**
     * 
     * @param mediator 
     */
    public BuyersPane(Mediator mediator)
    {
        super(new BorderLayout());
        this.mediator = mediator;
        init();
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Group of Jobs"), BorderFactory.createEmptyBorder(5,5,5,5)));
        model = new MyTableModel(data,makeHeader());
	makeDefaultData();
        tblBuyerAgents = new JTable(model);
        tblBuyerAgents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	ListSelectionModel lsm = tblBuyerAgents.getSelectionModel();
	lsm.addListSelectionListener(this);
        tblBuyerAgents.setPreferredScrollableViewportSize(new Dimension(800,400));
        tblBuyerAgents.getColumnModel().getColumn(0).setPreferredWidth(20);
        tblBuyerAgents.getColumnModel().getColumn(1).setPreferredWidth(50);
        tblBuyerAgents.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblBuyerAgents.getColumnModel().getColumn(4).setPreferredWidth(60);
        tblBuyerAgents.getColumnModel().getColumn(5).setPreferredWidth(100);
        tblBuyerAgents.getColumnModel().getColumn(7).setPreferredWidth(35);
        tblBuyerAgents.getColumnModel().getColumn(8).setPreferredWidth(50);
        JScrollPane scroll = new JScrollPane(tblBuyerAgents);
        scroll.setPreferredSize(new Dimension(100,200));
        add(scroll, BorderLayout.NORTH);
    }
    
    /**
     * 
     */
    private void init()
    {
        data = new Vector<Vector>();
        nextNumber = 1;
        initFields();
    }
    
    /**
     * 
     */
    private void initFields()
    {
        txtIdIteration          = new JTextField(10);
        txtIdIteration.setDocument(new NumberPlainDocument(1, 1000, 0));
        txtNumberOfUsers        = new JTextField(10);
        txtNumberOfUsers.setDocument(new NumberPlainDocument(1, 1000, 0));
        txtScheduledTime        = new JTextField(10);
        txtScheduledTime.setDocument(new NumberPlainDocument(0, 1000000, 2));
        txtMiPerCloudlet        = new JTextField(10);
        txtMiPerCloudlet.setDocument(new NumberPlainDocument(0, 1000000, 3));
        txtMipsPerVm            = new JTextField(10);
        txtMipsPerVm.setDocument(new NumberPlainDocument(0, 1000000, 3));
        txtMinimumReputation    = new JTextField(10);
        txtMinimumReputation.setDocument(new NumberPlainDocument(0, 1000000, 3));
        txtMaximumProfit      = new JTextField(10);
        txtMaximumProfit.setDocument(new NumberPlainDocument(0, 1, 3));
        Region[] items          = {Region.NORTH, Region.SOUTH, Region.EAST, Region.WEST};
        cmbSlaRegion            = new JComboBox(items);
        Reliability[] values    = {Reliability.LOW, Reliability.MEDIUM, Reliability.HIGH};
        cmbSlaReliability       = new JComboBox(values);
        Priority[] priorities   = {Priority.LOW, Priority.MEDIUM, Priority.HIGH};
        cmbSlaPriority          = new JComboBox(priorities);
    }

    /**
     * 
     */
    private void deleteData()
    {
        if (selectedRow>=0 && model.getRowCount()>1 )
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
            tblBuyerAgents.getSelectionModel().setLeadSelectionIndex(selectedRow);
            
        }
        else if(model.getRowCount()==1)
        {
            JOptionPane.showMessageDialog(this, "At least one Group of Jobs is required to start simulation", "Invalid Action", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void enableButtons(boolean enable)
    {
        btnSave.setEnabled(enable);
        btnDelete.setEnabled(enable);
    }

    private void updateTableModel()
    {
        if(selectedRow>=0)
        {
           model.setValueAt(txtIdIteration.getText(), selectedRow, 0);
           model.setValueAt(txtNumberOfUsers.getText(), selectedRow, 1);
           model.setValueAt(txtScheduledTime.getText(), selectedRow, 2);
           model.setValueAt(txtMiPerCloudlet.getText(), selectedRow, 3);
           model.setValueAt(txtMipsPerVm.getText(), selectedRow, 4);
           model.setValueAt(txtMinimumReputation.getText(), selectedRow, 5);
           model.setValueAt(txtMaximumProfit.getText(), selectedRow, 6);
           model.setValueAt(cmbSlaRegion.getSelectedItem(), selectedRow, 7);
           model.setValueAt(cmbSlaPriority.getSelectedItem(), selectedRow, 8);
           model.setValueAt(cmbSlaReliability.getSelectedItem(), selectedRow, 9);                 
        }
    }
    
    /**
     * 
     */
    private void updateFormModel()
    {
        if(selectedRow>=0)
        {
            txtIdIteration.setText(model.getValueAt(selectedRow, 0).toString());
            txtNumberOfUsers.setText(model.getValueAt(selectedRow, 1).toString());
            txtScheduledTime.setText(model.getValueAt(selectedRow, 2).toString());
            txtMiPerCloudlet.setText(model.getValueAt(selectedRow, 3).toString());
            txtMipsPerVm.setText(model.getValueAt(selectedRow, 4).toString());
            txtMinimumReputation.setText(model.getValueAt(selectedRow, 5).toString());
            txtMaximumProfit.setText(model.getValueAt(selectedRow, 6).toString());
            
            cmbSlaRegion.setSelectedItem(model.getValueAt(selectedRow,7));           
            cmbSlaReliability.setSelectedItem(model.getValueAt(selectedRow,9));            
            cmbSlaPriority.setSelectedItem(model.getValueAt(selectedRow,8));          
        }
    }
    
    /**
     * 
     */
    private void resetFormModel()
    {
       txtIdIteration.setText("");
       txtNumberOfUsers.setText("");
       txtScheduledTime.setText("");
       txtMiPerCloudlet.setText("");
       txtMipsPerVm.setText("");
       txtMinimumReputation.setText("");
       txtMaximumProfit.setText("");
       cmbSlaRegion.setSelectedItem(Region.NORTH);
       cmbSlaReliability.setSelectedItem(Reliability.HIGH);
       cmbSlaPriority.setSelectedItem(Priority.MEDIUM);
    }
    
    /**
     * 
     */
    private void selectAndShowNewData()
    {
        tblBuyerAgents.getSelectionModel().setLeadSelectionIndex(model.getRowCount()-1);
        tblBuyerAgents.scrollRectToVisible(tblBuyerAgents.getCellRect(selectedRow,0,true));
        if(model.getRowCount()==1)
        {
            enableButtons(true);
        }
    }
    
    /**
     * 
     * @return 
     */
    private boolean isFormComplete()
    {
        boolean complete = false;
        if(txtIdIteration.getText().length()==0)
        {
            JOptionPane.showMessageDialog(this, "Group Id must have a value", "Validation error", JOptionPane.ERROR_MESSAGE);
        }
        else if (txtNumberOfUsers.getText().length()==0)
        {
            JOptionPane.showMessageDialog(this, "# of Users must have a value", "Validation error", JOptionPane.ERROR_MESSAGE);
        }
        else if (txtScheduledTime.getText().length()==0)
        {
            JOptionPane.showMessageDialog(this, "Scheduled time must have a value", "Validation error", JOptionPane.ERROR_MESSAGE);
        }
        else if (txtMiPerCloudlet.getText().length()==0)
        {
            JOptionPane.showMessageDialog(this, "Mi Per Job must have a value", "Validation error", JOptionPane.ERROR_MESSAGE);
        }
        else if (txtMipsPerVm.getText().length()==0)
        {
            JOptionPane.showMessageDialog(this, "Mips Per VM must have a value", "Validation error", JOptionPane.ERROR_MESSAGE);
        }
        else if (txtMinimumReputation.getText().length()==0)
        {
            JOptionPane.showMessageDialog(this, "Minimum Reputation must have a value", "Validation error", JOptionPane.ERROR_MESSAGE);
        }
        else if (txtMaximumProfit.getText().length()==0)
        {
            JOptionPane.showMessageDialog(this, "Maximum Profit must have a value", "Validation error", JOptionPane.ERROR_MESSAGE);
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
    private Vector<String> makeHeader()
    {
	Vector<String> header = new Vector<String>();
        header.add("Id");
	header.add("# of Users");
        header.add("Scheduled Time");
        header.add("Mi Per Job");
        header.add("Mips Per VM");
        header.add("Minimum Reputation");
        header.add("Maximum Profit");
        header.add("Region");  
        header.add("SLA Priority");
        header.add("SLA Reliability");                     
        return header;
    }
    
    /**
     * 
     */
    protected void makeNewData()
    {       
        Vector row = new Vector();
        row.add(""+nextNumber++);
        row.add("3");
        row.add("0.0");
        row.add("2000");
        row.add("500");
        row.add("20");
        row.add("0.45");
        row.add(Region.NORTH);
        row.add(Priority.HIGH);
        row.add(Reliability.MEDIUM);
        model.insertRow(model.getRowCount(),row);
    }
    
    /**
     * 
     */
    private void makeDefaultData()
    {
        Vector row = new Vector();
        row.add(""+nextNumber++);
        row.add("2");
        row.add("0.0");
        row.add("1000");
        row.add("250");
        row.add("1");
        row.add("0.4");
        row.add(Region.NORTH);
        row.add(Priority.MEDIUM);
        row.add(Reliability.HIGH);
        
        model.insertRow(model.getRowCount(),row);
    }
    
    /**
     * 
     * @return 
     */
    private JPanel makeDetailPane()
    {
        JPanel detailPane = new JPanel(new GridBagLayout());
        detailPane.setBorder(BorderFactory.createTitledBorder("Scheduled Jobs Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0,0,3,40);
        detailPane.add(new JLabel("Group Id *"), gbc);
        gbc.gridx=1;
        gbc.gridy=0;

        detailPane.add(txtIdIteration, gbc);
        gbc.gridx=2;
        gbc.gridy=0;
        detailPane.add(new JLabel("# of Users *"), gbc);
        gbc.gridx=3;
        gbc.gridy=0;
        detailPane.add(txtNumberOfUsers, gbc);
        gbc.gridx=0;
        gbc.gridy=1;
        detailPane.add(new JLabel("Scheduled Time *"), gbc);
        gbc.gridx=1;
        gbc.gridy=1;
        detailPane.add(txtScheduledTime, gbc);
        gbc.gridx=2;
        gbc.gridy=1;
        detailPane.add(new JLabel("Mi Per Job *"), gbc);
        gbc.gridx=3;
        gbc.gridy=1;
        detailPane.add(txtMiPerCloudlet, gbc);
        gbc.gridx=0;
        gbc.gridy=2;
        detailPane.add(new JLabel("Mips Per VM *"), gbc);
        gbc.gridx=1;
        gbc.gridy=2;
        detailPane.add(txtMipsPerVm, gbc);
        gbc.gridx=2;
        gbc.gridy=2;
        detailPane.add(new JLabel("Minimum Reputation *"), gbc);
        gbc.gridx=3;
        gbc.gridy=2;
        detailPane.add(txtMinimumReputation, gbc);
        gbc.gridx=0;
        gbc.gridy=3;
        detailPane.add(new JLabel("Maximum Profit *"), gbc);
        gbc.gridx=1;
        gbc.gridy=3;
        detailPane.add(txtMaximumProfit, gbc);
        gbc.gridx=2;
        gbc.gridy=3;
        detailPane.add(new JLabel("Region *"), gbc);
        gbc.gridx=3;
        gbc.gridy=3;
        detailPane.add(cmbSlaRegion, gbc);
        gbc.gridx=0;
        gbc.gridy=4;
        detailPane.add(new JLabel("SLA Priority *"), gbc);
        gbc.gridx=1;
        gbc.gridy=4;
        detailPane.add(cmbSlaPriority, gbc);
        gbc.gridx=2;
        gbc.gridy=4;
        detailPane.add(new JLabel("SLA Reliability *"), gbc);
        gbc.gridx=3;
        gbc.gridy=4;
        detailPane.add(cmbSlaReliability, gbc);
        
        return detailPane;
    }
    
    /**
     * 
     * @return 
     */
    private JPanel makeButtonsPane()
    {
        JPanel buttonsPane = new JPanel();
        JButton btnAddEmpty = new JButton("Add Default One", Util.createImageIcon(this, "/images/crear.gif"));
        btnAddEmpty.setActionCommand("addEmpty");
        btnAddEmpty.addActionListener(this);
        JButton btnAddDefault = new JButton("Add Default Two", Util.createImageIcon(this, "/images/addGroup.gif"));
        btnAddDefault.setActionCommand("addDefault");
        btnAddDefault.addActionListener(this);
        btnSave = new JButton("Save", Util.createImageIcon(this, "/images/save-icon.png"));
        btnSave.setActionCommand("save");
        btnSave.addActionListener(this);
        btnDelete = new JButton("Delete", Util.createImageIcon(this, "/images/delete-trash.png"));
        btnDelete.setActionCommand("delete");
        btnDelete.addActionListener(this);
        buttonsPane.add(btnAddEmpty);
        buttonsPane.add(btnAddDefault);
        buttonsPane.add(btnSave);
        buttonsPane.add(btnDelete);
        return buttonsPane;
    }
    
    /**
     * 
     * @return
     * @throws Exception 
     */
    public List<BuyerAgent> createBuyerAgents() throws Exception
    {
        List<BuyerAgent> buyerAgents = new ArrayList<BuyerAgent>();
        int shift = 0;
        for(int i=0; i< model.getRowCount(); i++)
        {
            int numberOfUsers = new Integer(model.getValueAt(i, 1).toString());
            int maximumTimeForCompletion = 0;          
            Iteration iteration  = new Iteration(new Integer(model.getValueAt(i, 0).toString()),
                                                 numberOfUsers,
                                                 new Double(model.getValueAt(i, 2).toString()),
                                                 new Integer(model.getValueAt(i, 3).toString()),
                                                 new Integer(model.getValueAt(i, 4).toString()),
                                                 new Integer(model.getValueAt(i, 5).toString()),
                                                 new SLA((Priority)model.getValueAt(i, 8),
                                                         (Reliability)model.getValueAt(i, 9),
                                                          maximumTimeForCompletion,
                                                         (Region)model.getValueAt(i, 7)),
                                                 shift,
                                                 new Double(model.getValueAt(i, 6).toString()));
            iteration.initBuyerAgents();
            shift += numberOfUsers;
            buyerAgents.addAll(iteration.getBuyerAgents());
        }      
        return buyerAgents;
    }
    
    /**
     * 
     * @return 
     */
    public List<Iteration> createIterations() //throws Exception
    {
        List<Iteration> iterations = new ArrayList<Iteration>();
        int shift = 0;
        for(int i=0; i< model.getRowCount(); i++)
        {
            int numberOfUsers = new Integer(model.getValueAt(i, 1).toString());
            int maximumTimeForCompletion = 0;
            Iteration iteration  = new Iteration(new Integer(model.getValueAt(i, 0).toString()),
                                                 numberOfUsers,
                                                 new Double(model.getValueAt(i, 2).toString()),
                                                 new Integer(model.getValueAt(i, 3).toString()),
                                                 new Integer(model.getValueAt(i, 4).toString()),
                                                 new Integer(model.getValueAt(i, 5).toString()),
                                                 new SLA((Priority)model.getValueAt(i, 8),
                                                         (Reliability)model.getValueAt(i, 9),
                                                          maximumTimeForCompletion,
                                                         (Region)model.getValueAt(i, 7)),
                                                 shift,
                                                 new Double(model.getValueAt(i, 6).toString()));

            shift += numberOfUsers;
            iterations.add(iteration);
        }
        return iterations;
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
            mediator.addGroupOfJobTree();

        }
        else if ("addDefault".equals(e.getActionCommand()))
        {
            makeDefaultData();
            selectAndShowNewData();
            mediator.addGroupOfJobTree();
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
            mediator.deleteGroupOfJobTree();
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
        if (!lsm.isSelectionEmpty() && lsm == tblBuyerAgents.getSelectionModel())
        {
            //selectedRowOld = selectedRow;
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
     * @param groupOfJobs 
     */
    public void importScenario(Iteration[] groupOfJobs)
    {
        while(model.getRowCount()>0)
        {
            model.removeRow(model.getRowCount()-1);
        }

        for (int i = 0; i < groupOfJobs.length; i++)
        {
            Iteration groupOfJob = groupOfJobs[i];
            importData(groupOfJob);
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
     * @param iteration 
     */
    private void importData(Iteration iteration)
    {
        Vector row = new Vector();
        row.add(iteration.getId().toString());
        row.add(iteration.getNumberOfUsers().toString());
        row.add(iteration.getScheduledTime().toString());
        row.add(iteration.getMiPerCloudlet().toString());
        row.add(iteration.getMipsPerVm().toString());
        row.add(iteration.getMinimumReputation().toString());
        row.add(iteration.getMaximumProfit().toString());
        row.add(iteration.getSla().getRegion());
        row.add(iteration.getSla().getPriority());
        row.add(iteration.getSla().getReliability());

        model.insertRow(model.getRowCount(),row);
    }

}
