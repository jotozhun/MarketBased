package uk.ac.bham.cs.simulation.tool.gui;

import javax.swing.text.*;
import java.awt.*;

public class NumberPlainDocument extends PlainDocument
{
    private Toolkit toolkit = Toolkit.getDefaultToolkit();
    private float 	min,max;
    private int   	dec;

    /**
     * Creates a PlainDocument to validate numbers
     * 
     *
     * @param min   the minimum value
     * @param max   the maximum value
     * @param dec   the maximum length of decimals allowed
     *
     * @pre min >= 0
     * @pre dec >= 0
     * @post $none
     */
    public NumberPlainDocument(float min, float max,int dec)
    {
        this.min = min;
        this.max = max;
        this.dec = dec;
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
        String text = getText(0,getLength());
        float  number;
        try
        {
            String chain;
            if (offs>=1 && offs<getLength())
            {
                chain = text.substring(0,offs)+str+text.substring(offs);               
            }
            else if (offs==0 && offs<getLength())
            {
                chain = str + text;
            }
            else
                chain = text + str;

            if (dec==0) 
                number = Integer.parseInt(chain);
            else	   
                number = Float.parseFloat(chain);

            if(number >= min && number <= max && isDecimalRange(chain))
            {
                super.insertString(offs, str, a);
            }
        }
        catch(NumberFormatException e)
        {
           toolkit.beep();
        }
    }
    
    /**
     * 
     * @param text
     * @return 
     */
    private boolean isDecimalRange(String text)
    {
        int index = text.indexOf(".");
        if(index !=-1 && index < text.length()-1)
            return(text.substring(index+1).length()<=dec);
        return true;
    }
}