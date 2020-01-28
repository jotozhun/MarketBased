package uk.ac.bham.cs.simulation.tool;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.ImageIcon;
import org.cloudbus.cloudsim.BuyerAgent;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import uk.ac.bham.cs.simulation.cloud.SellerAgent;

/**
 * This class implements static methods to perform common or generic tasks
 * @author  Carlos Mera Gómez
 * @version 1.0, 28/07/2011
 */
public class Util
{

    /**
     * Prints the Cloudlet objects
     * @param list  list of Cloudlets
     */
    public static void printCloudletList(List<Cloudlet> list, List<BuyerAgent> buyerAgents)
    {
        int size = list.size();        
        Cloudlet job;

        String indent = "\t";//"    ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
                        "Data center ID" + indent + "VM ID" + indent +
                        "Time" + indent +
                        "Start Time" + indent + "Finish Time"+ indent +
                        "# of Tries" + indent + "User ID" );
        /*Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
                        "Data center ID" + indent + "VM ID" + indent + indent +
                        "Time" + indent +
                        "Start Time" + indent + "Finish Time"+ indent +
                        "# of Tries" + indent + indent + "User ID" );*/

        DecimalFormat dft = new DecimalFormat("###.##");
        for (int i = 0; i < size; i++)
        {
            job = list.get(i);
            Log.print(job.getCloudletId() + indent);
            /*Log.print(indent + job.getCloudletId() + indent + indent);*/

            //if (job.getCloudletStatus() == Cloudlet.SUCCESS || job.getCloudletStatus() == Cloudlet.CREATED)
            //{
            Log.print(job.getCloudletStatusString());
            //Log.print("SUCCESS");
            int tries = 0;
            for(BuyerAgent buyerAgent : buyerAgents)
            {
                if(buyerAgent.getId() == job.getUserId())
                {
                    tries = buyerAgent.getNumberOfTries();
                    break;
                }
            }

            /*Log.printLine( indent + indent +indent +indent + indent + job.getResourceId() + indent + indent + indent + job.getVmId() +
                            indent + indent + indent + dft.format(job.getActualCPUTime()) +
                            indent + indent + dft.format(job.getExecStartTime())+ indent + indent + indent + dft.format(job.getFinishTime()) +
                            indent + indent + indent + indent + tries +
                            indent + indent + indent + indent + job.getUserId());*/

            Log.printLine( indent + job.getResourceId() + indent +  job.getVmId() +
                            indent + dft.format(job.getActualCPUTime()) +
                            indent +  dft.format(job.getExecStartTime())+ indent +  dft.format(job.getFinishTime()) +
                            indent +  tries +
                            indent +  job.getUserId());
            //}
        }
    }

    /**
     * Prints the SellerAgent's information after the simulation
     * @param sellerAgents
     */
    public static void printSellerAgentInfo(List<SellerAgent> sellerAgents)
    {
        for (SellerAgent sellerAgent : sellerAgents)
        {
            Log.printLine(sellerAgent.getName() +" reputation is "+sellerAgent.getReputation());
            sellerAgent.printDebts();
        }
    }

    /**
     * Creates an Image Icon from a specified resource in the filesystem
     * @param object
     * @param name
     * @return
     */
    public static ImageIcon createImageIcon(Object object, String name)
    {
        ImageIcon imageIcon = null;
        URL imageURL =  object.getClass().getResource(name);
        if(imageURL!=null)
        {
            imageIcon = new ImageIcon(imageURL);
        }
        return imageIcon;
    }

}
