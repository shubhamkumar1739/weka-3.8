/*
 * Class to convert the classification data in xlsx format
 */
package weka.core.converters;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Shubham Kumar
 */
public class XLSXConverter {

    private int m_Columns;
    private InputStream m_FileInputStream;
    private XSSFWorkbook m_Workbook;
    private XSSFSheet m_Sheet;
    private String m_Filename;
    private int m_LastRowNumber;
    private String m_FilePath = System.getenv("SystemDrive");
    /**
     * Constructor takes number of rows as input
     */
    public XLSXConverter(int columns,String name) {
        
        
        m_Columns = columns;
        m_Filename = name;
        m_LastRowNumber = -1;
        File file = new File(m_FilePath+"/Users/DELL/Documents/Weka/Ouputs/"+m_Filename+".xls");
        m_Workbook = new XSSFWorkbook();
        m_Sheet = m_Workbook.createSheet(name);
    }
    
    public void addRowToSheet(ArrayList<String> list) throws Exception
    {
        if(list.size() != m_Columns){
            throw new Exception("The size of rows don't match : "+m_Columns+" & "+list.size());
        }
        
        Row row = m_Sheet.createRow(++m_LastRowNumber);
        for(int i = 0; i < list.size(); i++){
            Cell cell = row.createCell(i);
            cell.setCellValue(list.get(i));
        }
    }
    public void write()
    {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(m_FilePath+"/Users/DELL/Documents/Weka/Ouputs/"+m_Filename+".xlsx");
            m_Workbook.write(fileOutputStream);
            fileOutputStream.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(XLSXConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XLSXConverter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(XLSXConverter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
