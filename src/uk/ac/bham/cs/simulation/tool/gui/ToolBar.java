package uk.ac.bham.cs.simulation.tool.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JToolBar;
import uk.ac.bham.cs.simulation.tool.Util;

/**
 * This class builds the tool bar of the simulator
 * @author  Carlos Mera Gómez
 * @version 1.0, 02/08/2011
 */
public class ToolBar extends JToolBar implements ActionListener
{
    private JButton         btnContinue;
    private SimulationTool  simulationTool;

    /**
     * 
     * @param simulationTool 
     */
    public ToolBar(SimulationTool simulationTool)
    {
        super();
        init();
        this.simulationTool = simulationTool;
    }
    
    /**
     * 
     */
    private void init()
    {
        JButton btnSimulate = new JButton("Run Simulation", Util.createImageIcon(this, "/images/simulate.gif"));
        btnSimulate.setActionCommand("run");
        btnSimulate.addActionListener(this);
        JButton btnCloseResults = new JButton("Close Results", Util.createImageIcon(this, "/images/bs_chart.gif"));
        btnCloseResults.setActionCommand("close");
        btnCloseResults.addActionListener(this);
        btnContinue = new JButton("Continue Simulation", Util.createImageIcon(this, "/images/bs_design.gif"));
        btnContinue.setActionCommand("continue");
        btnContinue.addActionListener(this);
        btnContinue.setEnabled(false);
        add(btnSimulate);
        add(btnCloseResults);
        add(btnContinue);
    }
    
    /**
     * 
     * @param e 
     */
    public void actionPerformed(ActionEvent e)
    {
        if("run".equals(e.getActionCommand()))
        {
            simulationTool.simulate();
            btnContinue.setEnabled(true);
        }
        else if("close".equals(e.getActionCommand()))
        {           
            simulationTool.closeResultTabs();
        }
        else if("continue".equals(e.getActionCommand()))
        {
            simulationTool.continueSimulation();
        }
    }

}
