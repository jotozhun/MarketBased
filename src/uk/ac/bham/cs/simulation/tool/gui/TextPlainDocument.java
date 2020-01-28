package uk.ac.bham.cs.simulation.tool.gui;

import java.awt.Toolkit;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * This class validates the maximum length in a text fields and the uppercase or lowercase format
 * @author Carlos Mera Gómez
 * @version 1.0, 03/08/2011
 */
/* (non-Javadoc)
 * This class was used for ICW module
 */
public class TextPlainDocument extends PlainDocument
{
    int     length;
    boolean upperCase;
    
    /**
     * 
     * @param length
     * @param upperCase 
     */
    public TextPlainDocument(int length,boolean upperCase)
    {
        super();
        this.length     =   length;
        this.upperCase  =   upperCase;
    }
    
    /**
     * 
     * @param offs
     * @param str
     * @param a
     * @throws BadLocationException 
     */
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
    {
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        if (getLength()<=length-1)
        {
            if(upperCase)
            {
                str = str.toUpperCase();
            }            
            super.insertString(offs, str, a);            
        }
        else
        {
            toolkit.beep();
        }
    }
}
