package uk.ac.bham.cs.simulation.tool.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import uk.ac.bham.cs.simulation.tool.Util;
import uk.ac.bham.cs.simulation.tool.xml.XMLProcessor;

/**
 * This class implements the menu of the simulator
 * @author  Carlos Mera Gómez
 * @version 1.0, 02/08/2011
 */
public class MenuBar extends JMenuBar implements ActionListener
{

    private SimulationTool simulationTool;
    public static final String fileExtension = "oqpmm";

    /**
     * 
     * @param simulationTool 
     */
    public MenuBar(SimulationTool simulationTool)
    {
        init();
        this.simulationTool = simulationTool;
    }
    
    /**
     * 
     */
    private void init()
    {
        JMenu menuFile = new JMenu("File");
        menuFile.setMnemonic(KeyEvent.VK_F);
        add(menuFile);
        JMenuItem menuItemExport = new JMenuItem("Export...", KeyEvent.VK_E);
        menuItemExport.setIcon(Util.createImageIcon(this, "/images/export.gif"));
        menuItemExport.addActionListener(this);
        menuItemExport.setActionCommand("export");
        menuFile.add(menuItemExport);
        JMenuItem menuItemImport = new JMenuItem("Import...", KeyEvent.VK_I);
        menuItemImport.setIcon(Util.createImageIcon(this, "/images/import.gif"));
        menuItemImport.addActionListener(this);
        menuItemImport.setActionCommand("import");
        menuFile.add(menuItemImport);
        JMenuItem menuItemExit = new JMenuItem("Exit", KeyEvent.VK_X);
        menuItemExit.setIcon(Util.createImageIcon(this, "/images/salir.gif"));
        menuItemExit.addActionListener(this);
        menuItemExit.setActionCommand("exit");
        menuFile.add(menuItemExit);

        JMenu menu = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_H);
        menu.getAccessibleContext().setAccessibleDescription("Simulator Help");
        add(menu);
        JMenuItem menuItemAbout = new JMenuItem("About Simulator", KeyEvent.VK_A);
        menuItemAbout.setIcon(Util.createImageIcon(this, "/images/about.gif"));
        menuItemAbout.setActionCommand("about");
        menuItemAbout.addActionListener(this);
        menu.add(menuItemAbout);
    }
    
    /**
     * 
     */
    private void exportScenario()
    {
        JFileChooser fileChooser = new JFileChooser();
        //FileNameExtensionFilter filter = new FileNameExtensionFilter(fileExtension.toUpperCase()+" Files", fileExtension);
        MyFilter filter = new MyFilter();
        fileChooser.setFileFilter(filter);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int option = fileChooser.showSaveDialog(this.getParent());
        File file = fileChooser.getSelectedFile();
        if(option==JFileChooser.APPROVE_OPTION)
        {
            try
            {
                int choice = JOptionPane.YES_OPTION;
                if(!file.exists())
                {
                    if(!file.getAbsolutePath().endsWith("."+fileExtension))
                    {
                        file = new File(file.getAbsolutePath()+"."+fileExtension);
                    }
                    file.createNewFile();
                }
                else
                {
                    choice = JOptionPane.showConfirmDialog(this.getParent(), "This file already exists. Do you want to overwrite it?", "Confirmation", JOptionPane.YES_NO_OPTION);
                }
                if(choice == JOptionPane.YES_OPTION)
                {
                    PrintWriter out = new PrintWriter(file);
                    out.write("<?xml version='1.0' encoding='UTF-8'?><scenario><sellers></sellers><trading></trading><iterations></iterations></scenario>");
                    out.close();
                    XMLProcessor xmlProcessor = new XMLProcessor(file);
                    xmlProcessor.saveScenario(simulationTool.exportDummySellerAgents(),
                                              simulationTool.exportMarketMechanisms(),
                                              simulationTool.exportGroupOfJobs());
                    JOptionPane.showMessageDialog(this.getParent(), "Export Completed", "Information Message", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            catch(IOException ex)
            {
                JOptionPane.showMessageDialog(fileChooser, "Error saving the file:"+ex.toString(), "Error Message", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * 
     */
    private void importScenario()
    {
        JFileChooser fc = new JFileChooser(); 
        MyFilter filter = new MyFilter();
        fc.setFileFilter(filter);
        int choice = fc.showOpenDialog(this);
        if(choice == JFileChooser.APPROVE_OPTION)
        {
            File file = fc.getSelectedFile();            
            String extension = filter.getExtension(file);
            if (extension!=null && extension.equals(fileExtension))
            {
                XMLProcessor xmlProcessor = new XMLProcessor(file);
                try
                {
                    simulationTool.importScenario(xmlProcessor.getAllDummySellerAgents(),
                                                  xmlProcessor.getAllIterations(),
                                                  xmlProcessor.getAllTradingMechanisms());

                    JOptionPane.showMessageDialog(this.getParent(), "Import Completed", "Information Message", JOptionPane.INFORMATION_MESSAGE);                 
                }
                catch(Exception ex)
                {
                    JOptionPane.showMessageDialog(this.getParent(), "Error importing the file:"+ex.toString(), "Error Message", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
            else
            {
                 JOptionPane.showMessageDialog(this.getParent(),"You must choose files "+fileExtension.toUpperCase() ,"Warning Message",JOptionPane.WARNING_MESSAGE);
            }

        }
    }
    
    /**
     * 
     * @param e 
     */
    public void actionPerformed(ActionEvent e)
    {
        if("exit".equals(e.getActionCommand()))
        {
            int n = JOptionPane.showConfirmDialog(this.getParent(),"Are you sure to exit?","Exiting from Simulator",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
            if (n==JOptionPane.YES_OPTION)
            {
               System.exit(0);
            }       
        }
        else if ("export".equals(e.getActionCommand()))
        {
            exportScenario();
        }
        else if ("import".equals(e.getActionCommand()))
        {
            importScenario();
        }
        else if ("about".equals(e.getActionCommand()))
        {
            AboutDlg aboutDlg = new AboutDlg(simulationTool.getFrame());
            aboutDlg.setVisible(true);
        }
    }

    public class MyFilter extends FileFilter
    {
        public boolean accept(File f)
        {
            if (f.isDirectory())
            {
                return true;
            }
            String extension = getExtension(f);
            if (extension != null)
                return extension.equals(fileExtension);

            return false;
        }

        public String getDescription()
        {
            return "Files "+fileExtension.toUpperCase()+" (*."+fileExtension+")";
        }

        private String getExtension(File f)
        {
            String ext = null;
            String s = f.getName();
            int i = s.lastIndexOf('.');

            if (i > 0 &&  i < s.length() - 1)
            {
                ext = s.substring(i+1).toLowerCase();
            }
            return ext;
        }
    }


  }