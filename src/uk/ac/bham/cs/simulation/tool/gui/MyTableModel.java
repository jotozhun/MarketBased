package uk.ac.bham.cs.simulation.tool.gui;

import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 * This class is for customization in our table models
 * @author  Carlos Mera Gómez
 * @version 1.0, 03/08/2011
 */
public class MyTableModel extends DefaultTableModel
{

    public MyTableModel(Vector dataVector,Vector columnIdentifiers)
    {
        super(dataVector,columnIdentifiers);
    }

    public Class getColumnClass(int c)
    {
        return getValueAt(0, c).getClass();
    }

    public boolean isCellEditable(int row, int col)
    {
        return false;
    }

}