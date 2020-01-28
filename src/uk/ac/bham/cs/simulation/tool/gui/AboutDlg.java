package uk.ac.bham.cs.simulation.tool.gui;

import javax.swing.*;
import javax.swing.table.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import uk.ac.bham.cs.simulation.tool.Util;

/**
 * This class displays the about simulator dialog
 * @author  Carlos Mera Gómez
 * @version 1.0, 28/08/2011
 */
public class AboutDlg extends JDialog implements ActionListener
{
   private JTable      tblProperties;
   private JButton     btnClose;
   private JLabel      lblNombre, lblVersion, lblYear;
   
   /**
    * 
    * @param parent 
    */
   public AboutDlg(JFrame parent)
   {
      super(parent,"About Simulator OQPMM",true);
      setLocation(parent.getLocation().x+parent.getWidth()/2-150, parent.getLocation().y+parent.getHeight()/2-150);
      setSize(300,300);
      init();
   }   
   
   /**
    * 
    */
   private void init()
   {
      Vector data,header,row;
      header = new Vector();
      header.add("Name");
      header.add("Value");
      data = new Vector();
      row = new Vector();
      row.add("JDK Version ");
      row.add("1.6.0_25");
      data.add(row);
      row = new Vector();
      row.add("CloudSim Version");
      row.add("2.1.1");
      data.add(row);
      row = new Vector();
      row.add("IDE");
      row.add("Netbeans 6.9.1");
      data.add(row);
      row = new Vector();
      row.add("Autor");
      row.add("Carlos Mera Gómez");
      data.add(row);
      row = new Vector();
      row.add("Supervisor");
      row.add("Rami Bahsoon");
      data.add(row);
      row = new Vector();
      row.add("UML");
      row.add("Visual Paradigm 8.2");
      data.add(row);
      row = new Vector();
      row.add("Libraries");
      row.add("JFreeChart, JDom, JUnit");
      data.add(row);
      
      MyTableModel model = new MyTableModel(data,header);
      tblProperties = new JTable(model);
      
      JPanel northPane = new JPanel();
      JLabel lblImage = new JLabel(Util.createImageIcon(this, "/images/cloud.png"));
      lblImage.setAlignmentX(Component.CENTER_ALIGNMENT);
      lblNombre = new JLabel("University of Birmingham");
      lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
      lblVersion= new JLabel("Version 1.0");
      lblVersion.setAlignmentX(Component.CENTER_ALIGNMENT);   
      lblYear   = new JLabel("Year 2011 - MSc. ISS");
      lblYear.setAlignmentX(Component.CENTER_ALIGNMENT);
      
      northPane.setLayout(new BoxLayout(northPane, BoxLayout.Y_AXIS));
      northPane.add(lblImage);
      northPane.add(lblVersion);
      northPane.add(lblYear);
      northPane.add(lblNombre);
      JPanel pnlButtons = new JPanel();
      btnClose = new JButton("Close");
      btnClose.setActionCommand("close");
      btnClose.addActionListener(this);
      pnlButtons.add(btnClose);
      
      getContentPane().add(northPane,BorderLayout.NORTH);
      getContentPane().add(new JScrollPane(tblProperties),BorderLayout.CENTER );
      getContentPane().add(pnlButtons,BorderLayout.SOUTH);
      
   }
   
   /**
    * 
    * @param e 
    */
   public void actionPerformed(ActionEvent e)
   {
      if (e.getActionCommand().equals("close"))
      {
         dispose();            
      }      
   }

   class MyTableModel extends DefaultTableModel
   {
      /**
        * 
        * @param dataVector
        * @param columnIdentifiers 
        */
      public MyTableModel(Vector dataVector,Vector columnIdentifiers)
      {
         super(dataVector,columnIdentifiers);
      }  
      
      /**
       * 
       * @param c
       * @return 
       */
      public Class getColumnClass(int c)
      {
         return getValueAt(0, c).getClass();
      }
      
      /**
       * 
       * @param row
       * @param col
       * @return 
       */
      public boolean isCellEditable(int row, int col)
      {
         return (false);
      }
   }

}
