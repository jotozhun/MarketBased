package uk.ac.bham.cs.simulation.tool.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.text.NumberFormat;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import org.cloudbus.cloudsim.BuyerAgent;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.BorderArrangement;
import org.jfree.chart.block.LabelBlock;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import uk.ac.bham.cs.simulation.cloud.SellerAgent;
import uk.ac.bham.cs.simulation.market.MarketMechanism;

/**
 * This class builds the panel to display the charts of the simulation results
 * @author  Carlos Mera Gómez
 * @version 1.0, 07/08/2011
 */
public class ChartPane extends JPanel
{

    /**
     * Constructor
     */
    public ChartPane()
    {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    /**
     * Draws the allocation chart of the simulation results
     * @param sellerAgents
     * @param marketMechanism
     */
    public void drawAllocationChart(List<SellerAgent> sellerAgents, MarketMechanism marketMechanism)
    {
        // create a dataset...
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (SellerAgent sellerAgent : sellerAgents)
        {            
            dataset.setValue(sellerAgent.getName(), sellerAgent.getNumberOfAllocatedJobs());
        }      
        // create a chart...
        String bigTitle = "Allocated Jobs Per Seller Agent using "+marketMechanism.getName()+" - Pie Chart";
        JFreeChart chart = ChartFactory.createPieChart/*3D*/(bigTitle,
                                                            dataset,
                                                            true, // legend?
                                                            true, // tooltips?
                                                            false // URLs?
                                                            );

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.setNoDataMessage("No data available");
        plot.setCircular(true);
        plot.setLabelGap(0.02);

        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} = {2}",
                NumberFormat.getNumberInstance(),
                NumberFormat.getPercentInstance()));

        LegendTitle legend = createLegend(chart, marketMechanism.getName());
        chart.addSubtitle(legend);

        add(new ChartPanel(chart));
    }

    /**
     * Drwas the reputation chart with the simultation results
     * @param sellerAgents
     * @param marketMechanism
     */
    public void drawReputationChart(List<SellerAgent> sellerAgents, MarketMechanism marketMechanism)
    {
        CategoryDataset dataset = createCategoryDataset(sellerAgents);
        JFreeChart chart = createChart(dataset, 
                                        "Final Seller Agents' Reputation using "+marketMechanism.getName()+ "- Bar Chart",
                                        "Seller Agents",
                                        "Final Reputation");

        LegendTitle legend = createLegend(chart, marketMechanism.getName());
        chart.addSubtitle(legend);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

        add(chartPanel);
    }

    /**
     *
     * @param sellerAgents
     * @return
     */
    private CategoryDataset createCategoryDataset(List<SellerAgent> sellerAgents)
    {
        // column keys...
        String category = "";//Seller Agents";
        // create the dataset...
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (SellerAgent sellerAgent : sellerAgents)
        {
            // row keys...
            String serie = sellerAgent.getName();
            dataset.addValue(sellerAgent.getReputation(), serie , category);
        }
        return dataset;
    }

    /**
     *
     * @param dataset
     * @param title
     * @param domainAxisLabel
     * @param rangeAxisLabel
     * @return
     */
    private JFreeChart createChart(CategoryDataset dataset, String title, String domainAxisLabel, String rangeAxisLabel) {

        // create the chart...
        JFreeChart chart = ChartFactory.createBarChart(
            title,         // chart title
            domainAxisLabel,          // domain axis label
            rangeAxisLabel,           // range axis label
            dataset,                  // data
            PlotOrientation.VERTICAL,
            true,                     // include legend
            true,                     // tooltips?
            false                     // URLs?
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // set the background color for the chart...
        //chart.setBackgroundPaint(new Color(0xBBBBDD));

        // get a reference to the plot for further customisation...
        CategoryPlot plot = chart.getCategoryPlot();

        // set the range axis to display integers only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // disable bar outlines...
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setMaximumBarWidth(0.10);

        renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setItemLabelsVisible(true);
        ItemLabelPosition p = new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER, TextAnchor.CENTER, 45.0);
        renderer.setPositiveItemLabelPosition(p);

        // set up gradient paints for series...
        GradientPaint gp0 = new GradientPaint(
            0.0f, 0.0f, Color.blue,
            0.0f, 0.0f, Color.lightGray
        );
        GradientPaint gp1 = new GradientPaint(
            0.0f, 0.0f, Color.green,
            0.0f, 0.0f, Color.lightGray
        );
        renderer.setSeriesPaint(0, gp0);
        renderer.setSeriesPaint(1, gp1);

        // OPTIONAL CUSTOMISATION COMPLETED.

        return chart;
    }

    /**
     * 
     * @param chart
     * @param subTitle
     * @return
     */
    private LegendTitle createLegend(JFreeChart chart, String subTitle)
    {
        LegendTitle legend = new LegendTitle(chart.getPlot());

        BlockContainer wrapper = new BlockContainer(new BorderArrangement());
        wrapper.setBorder(new BlockBorder(1.0, 1.0, 1.0, 1.0));

        LabelBlock title = new LabelBlock("Seller Agents:", new Font("SansSerif", Font.BOLD, 12));
        title.setPadding(5, 5, 5, 5);
        wrapper.add(title, RectangleEdge.TOP);

        LabelBlock subtitle = new LabelBlock("Using: "+subTitle);
        subtitle.setPadding(8, 20, 2, 5);
        wrapper.add(subtitle, RectangleEdge.BOTTOM);

        BlockContainer items = legend.getItemContainer();
        items.setPadding(2, 10, 5, 2);
        wrapper.add(items);
        legend.setWrapper(wrapper);

        legend.setPosition(RectangleEdge.RIGHT);
        legend.setHorizontalAlignment(HorizontalAlignment.LEFT);

        return legend;
    }

     /**
     * Draws the SLA violation chart with the simultation results
     * @param sellerAgents
     * @param marketMechanism
     */
    public void drawViolationChart(List<SellerAgent> sellerAgents, List<BuyerAgent> buyerAgents, MarketMechanism marketMechanism)
    {

        CategoryDataset dataset = createViolationDataset(sellerAgents, buyerAgents);
        JFreeChart chart = createChart(dataset,
                                        "SLA Violations by Seller Agents using "+marketMechanism.getName()+ "- Bar Chart",
                                        "Seller Agents",
                                        "SLA Violations");

        LegendTitle legend = createLegend(chart, marketMechanism.getName());
        chart.addSubtitle(legend);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

        add(chartPanel);
    }

    /**
     *
     * @param sellerAgents
     * @return
     */
    private CategoryDataset createViolationDataset(List<SellerAgent> sellerAgents, List<BuyerAgent> buyerAgents)
    {
        // column keys...
        String category = "";//Seller Agents";
        // create the dataset...
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (SellerAgent sellerAgent : sellerAgents)
        {
            // row keys...
            String serie = sellerAgent.getName();
            int violations = 0;
            for (BuyerAgent buyerAgent : buyerAgents)
            {
                int endingPosition = buyerAgent.getCloudletList().isEmpty()?buyerAgent.getFailedSellerAgentIdList().size()-1:buyerAgent.getFailedSellerAgentIdList().size();
                if(buyerAgent.getFailedSellerAgentIdList().size()>1 &&  buyerAgent.getFailedSellerAgentIdList().subList(0,endingPosition).contains(sellerAgent.getId()))
                {
                    violations++;
                }
                //System.out.println(serie +" requested:"+buyerAgent.getFailedSellerAgentIdList());
            }
            dataset.addValue(violations, serie , category);
            //System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        }
        return dataset;
    }

}
