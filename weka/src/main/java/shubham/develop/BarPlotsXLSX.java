/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shubham.develop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.jfree.data.category.DefaultCategoryDataset; 
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.plot.PlotOrientation;
import java.util.Iterator;
/**
 *
 * @author Shubham Kumar
 */
public class BarPlotsXLSX {
    
    private String m_Source;
    private String m_Destination;
    public BarPlotsXLSX(String src,String dest) {
        m_Source = src;
        m_Destination = dest;
        
    }
    public void plot() {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(new File(m_Source));
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            ArrayList<String> labels = new ArrayList<>();
            int i = 0;
            while(rowIterator.hasNext()) {
                //Read Rows from Excel document
                Row row = rowIterator.next();
                //Read cells in Rows and get chart data
                Iterator<Cell> cellIterator = row.cellIterator();
                DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                int j = 0;
                while(cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    if(j == 0) {
                        j++;
                        continue;
                    }
                    if(i == 0){
                        labels.add(cell.getStringCellValue());
                    } else {
                        double val = Double.parseDouble(cell.getStringCellValue());
                        dataset.addValue(val,"Scores",labels.get(j - 1));
                    }
                    j++;
                }
                i++;      
                if(i == 1) {
                    continue;
                }
                /* Create a logical chart object with the chart data collected */
                JFreeChart BarChartObject=ChartFactory.createBarChart("Class vs Scores","Class","Scores",dataset,PlotOrientation.VERTICAL,true,true,false);  
                /* Dimensions of the bar chart */               
                int width=640; /* Width of the chart */
                int height=480; /* Height of the chart */               
                /* We don't want to create an intermediate file. So, we create a byte array output stream 
                and byte array input stream
                And we pass the chart data directly to input stream through this */             
                /* Write chart as PNG to Output Stream */
                ByteArrayOutputStream chart_out = new ByteArrayOutputStream();          
                ChartUtilities.writeChartAsPNG(chart_out,BarChartObject,width,height);
                /* We can now read the byte data from output stream and stamp the chart to Excel worksheet */
                int my_picture_id = workbook.addPicture(chart_out.toByteArray(), Workbook.PICTURE_TYPE_PNG);
                /* we close the output stream as we don't need this anymore */
                chart_out.close();
                /* Create the drawing container */
                XSSFDrawing drawing = sheet.createDrawingPatriarch();
                /* Create an anchor point */
                ClientAnchor my_anchor = new XSSFClientAnchor();
                /* Define top left corner, and we can resize picture suitable from there */
                my_anchor.setCol1(0);
                my_anchor.setRow1(10*i);
                /* Invoke createPicture and pass the anchor point and ID */
                XSSFPicture  my_picture = drawing.createPicture(my_anchor, my_picture_id);
                /* Call resize method, which resizes the image */
                my_picture.resize();              
            } 
            FileOutputStream out = new FileOutputStream(new File(m_Source));
            workbook.write(out);
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BarPlotsXLSX.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BarPlotsXLSX.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fileInputStream.close();
            } catch (Exception ex) {
                Logger.getLogger(BarPlotsXLSX.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
