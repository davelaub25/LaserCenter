/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lasersched;

import com.ezware.oxbow.swingbits.table.filter.TableRowFilterSupport;
import com.standbysoft.component.date.swing.JDatePicker;
//import com.toedter.calendar.JDateChooserCellEditor;
import java.awt.Color;
import java.awt.Component;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.*;
import java.text.Format;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.*;

/**
 *
 * @author dlaub
 */
public class LaserSched extends JPanel{

     public static JTable jTable2;
     public static boolean tableChangedFlag = false;
     public static Connection connection;
     public static ArrayList newRows = new ArrayList();
     
    /**
     * @param args the command line arguments
     */
    
     
    @SuppressWarnings("unchecked")
    public static void buildTable() /*throws SQLException*/ {
        try {
            Class.forName("com.mysql.jdbc.Driver"); 
            connection = DriverManager.getConnection("jdbc:mysql://davelaub.com:3306/dlaub25_lasersched","dlaub25_fmi","admin"); 
            ResultSet rs1 = connection.createStatement().executeQuery("SELECT main.jobNum, client.client, main.jobName, main.mailDate, type.type, main.jobStatus, programmer.programmer, main.signOffs, main.approved, main.production, platform.platform, csr.csr, printer.printer, data.data, main.notes, main.id  FROM main, csr, client, type, programmer, platform, printer, data WHERE main.csr = csr.csr AND main.client = client.client AND main.type = type.type AND main.programmer = programmer.programmer AND main.platform = platform.platform AND main.printer = printer.printer AND main.data = data.data");
            ResultSetMetaData md1 = rs1.getMetaData();
            System.out.println("Connection succeed!"); 
         
            DefaultTableModel model = new DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
                },
            new String [] {
                "Job Number", "Client", "Job Name", "Mail Date", "Type", "Job Status", "Programmer", "Sign Offs", "Approved", 
                "Production", "Platform", "CSR", "Printer", "Data", "Notes", "ID"
                }
            );
            model.removeRow(0);
            jTable2 = new javax.swing.JTable(model);
            rs1.beforeFirst();
            int numberOfColumns = md1.getColumnCount();
            while (rs1.next()){
                Object [] rowData = new Object[numberOfColumns];
                for (int i = 0; i < rowData.length; ++i)
                {
                    rowData[i] = rs1.getObject(i+1);
                }
                model.addRow(rowData);
                }
        }
        catch (ClassNotFoundException | SQLException e) { 
            } 
        }
    
    ////////////////////////////////////////////////////////////////////////////
    public static void buildUpdateQuery(Object[][] rowData) throws SQLException, ClassNotFoundException {
        DefaultTableModel dtm = (DefaultTableModel) UI.viewTable.getModel();
        
        int nRow = dtm.getRowCount(), nCol = dtm.getColumnCount();
        
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://davelaub.com:3306/dlaub25_lasersched","dlaub25_fmi","admin"); 
        ResultSet rs1 = connection.createStatement().executeQuery("SELECT * FROM main");
        ResultSetMetaData md1 = rs1.getMetaData();

        for( int j = 1; j <= nRow ; j++ ){
            String updateQuery = "UPDATE main SET jobNum = ?, client = ?, jobName = ?, mailDate = ?, type = ?, jobStatus = ?, programmer = ?, signOffs = ?, approved = ?, production = ?, platform = ?, csr = ?, printer = ?, data = ?, notes = ? WHERE id = ?";
            String insertQuery = "INSERT INTO `dlaub25_lasersched`.`main` (`jobNum`, `client`, `jobName`, `mailDate`, `type`, `jobStatus`, `programmer`, `signOffs`, `approved`, `production`, `platform`, `csr`, `printer`, `data`, `Notes`, `id`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, null);";
            PreparedStatement preparedStmtUpdate = connection.prepareStatement(updateQuery);
            PreparedStatement preparedStmtInsert = connection.prepareStatement(insertQuery);
            
            if(newRows.contains(rowData[j-1][15])){
                for( int i = 1 ; i <= (nCol - 1) ; i++){      //Using nCol - 1 to skip the ID field
                    if (rowData[j-1][i-1] == null){
                        if( i == 3 || i == 7 || i == 8 || i == 9 ){
                            preparedStmtInsert.setNull(i, java.sql.Types.DATE);
                        }
                        if( i == 1 || i == 2 || i == 4 || i == 5 || i == 6 || i == 10 || i == 11 || i == 12 || i == 13 || i == 14 ){
                            preparedStmtInsert.setNull(i, java.sql.Types.VARCHAR);
                        }
                        if( i == 0 || i == 15 ){
                            preparedStmtInsert.setNull(i, java.sql.Types.INTEGER);
                        }
                    }
                    else{
                        preparedStmtInsert.setObject(i, rowData[j-1][i-1].toString());
                    }
                }
                preparedStmtInsert.executeUpdate();                
            }
            else{
                for( int i = 1 ; i <= nCol ; i++){         
                    if (rowData[j-1][i-1] == null){
                        if( i == 3 || i == 7 || i == 8 || i == 9 ){
                            preparedStmtUpdate.setNull(i, java.sql.Types.DATE);
                        }
                        if( i == 1 || i == 2 || i == 4 || i == 5 || i == 6 || i == 10 || i == 11 || i == 12 || i == 13 || i == 14 ){
                            preparedStmtUpdate.setNull(i, java.sql.Types.VARCHAR);
                        }
                        if( i == 0 || i == 15 ){
                            preparedStmtUpdate.setNull(i, java.sql.Types.INTEGER);
                        }
                    }
                    else{
                        preparedStmtUpdate.setString(i, rowData[j-1][i-1].toString());
                    }
                }
                preparedStmtUpdate.executeUpdate();
            }
        }
        tableChangedFlag = false;
        connection.close();
        newRows.clear();
    }
    ////////////////////////////////////////////////////////////////////////////
    public static Object[][] getTableData (JTable table) {
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        int nRow = dtm.getRowCount(), nCol = dtm.getColumnCount();
        Object[][] tableData = new Object[nRow][nCol];
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0 ; i < nRow ; i++){
            for (int j = 0 ; j < nCol ; j++){
                Object value = UI.viewTable.getValueAt(i, j);
                if(value instanceof Date){
                    System.out.println("Date Found: " + value);
                    value = f.format((Date)value);
                    System.out.println("Formatted Version: " + value);
                }
                tableData[i][j] = value;
            }
        }
        return tableData;
    };
    ////////////////////////////////////////////////////////////////////////////
    public static JTable createAlternating(TableModel model) throws ClassNotFoundException, SQLException
    {
        JTable table = new JTable( model )
        {
            @Override
                public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
                {
                        Component c = super.prepareRenderer(renderer, row, column);

                        
                        c.setBackground(getBackground());
                        int modelRow = convertRowIndexToModel(row);
                        String type = (String)getModel().getValueAt(modelRow, 5);
                        Object date = this.getValueAt(row, column);
                        if ("Approved".equals(type)) c.setBackground(Color.RED);
                        if ("Printed".equals(type)) c.setBackground(Color.GREEN);
                        if ("On Hold".equals(type)) c.setBackground(Color.GRAY);
                        c.setForeground(Color.BLACK);
                        
                        return c;
                }
        };
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        TableCellRenderer dateRenderer = new FormatRenderer( f );
        TableColumn clientComboColumn1 = table.getColumnModel().getColumn(1);
        TableColumn clientComboColumn4 = table.getColumnModel().getColumn(4);
        TableColumn clientComboColumn5 = table.getColumnModel().getColumn(5);
        TableColumn clientComboColumn6 = table.getColumnModel().getColumn(6);
        TableColumn clientComboColumn10 = table.getColumnModel().getColumn(10);
        TableColumn clientComboColumn11 = table.getColumnModel().getColumn(11);
        TableColumn clientComboColumn12 = table.getColumnModel().getColumn(12);
        TableColumn clientComboColumn13 = table.getColumnModel().getColumn(13);
        TableColumn dateColumn3 = table.getColumnModel().getColumn(3);
        TableColumn dateColumn7 = table.getColumnModel().getColumn(7);
        TableColumn dateColumn8 = table.getColumnModel().getColumn(8);
        TableColumn dateColumn9 = table.getColumnModel().getColumn(9);

        String[][] values = buildComboBoxValues();

        clientComboColumn1.setCellEditor(new MyComboBoxEditor(values[0]));
        clientComboColumn4.setCellEditor(new MyComboBoxEditor(values[1]));
        clientComboColumn5.setCellEditor(new MyComboBoxEditor(values[2]));
        clientComboColumn6.setCellEditor(new MyComboBoxEditor(values[3]));
        clientComboColumn10.setCellEditor(new MyComboBoxEditor(values[4]));
        clientComboColumn11.setCellEditor(new MyComboBoxEditor(values[5]));
        clientComboColumn12.setCellEditor(new MyComboBoxEditor(values[6]));
        clientComboColumn13.setCellEditor(new MyComboBoxEditor(values[7]));
        dateColumn3.setCellEditor( new JDateChooserCellEditor());
        dateColumn7.setCellEditor( new JDateChooserCellEditor());
        dateColumn8.setCellEditor( new JDateChooserCellEditor());
        dateColumn9.setCellEditor( new JDateChooserCellEditor());
        dateColumn3.setCellRenderer(dateRenderer);
        dateColumn7.setCellRenderer(dateRenderer);
        dateColumn8.setCellRenderer(dateRenderer);
        dateColumn9.setCellRenderer(dateRenderer);
        
        TableRowFilterSupport.forTable(table).searchable(true).apply();
        table.setRowHeight(30);
        
        return table;
    }
    ////////////////////////////////////////////////////////////////////////////
    public static boolean checkType(int i){
        if (i == 15){
            return true;
        }
        else {
            return false;
        }
    };
    ////////////////////////////////////////////////////////////////////////////
    public static int getIdValue(String s, int colNum) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver"); 
        connection = DriverManager.getConnection("jdbc:mysql://davelaub.com:3306/dlaub25_lasersched","dlaub25_fmi","admin");
        ResultSet rs1 = connection.createStatement().executeQuery("SELECT * FROM main");
        ResultSetMetaData md1 = rs1.getMetaData();
        String t = md1.getColumnName(colNum);
        PreparedStatement preparedStmt = connection.prepareStatement("SELECT " + t + ".id FROM " + t + " WHERE " + t + "." + t + " = ?");
        preparedStmt.setString(1, s);
        rs1 = preparedStmt.executeQuery();
        rs1.next();
        int i = rs1.getInt("id");
        connection.close();
        return i;
        
        };
    ////////////////////////////////////////////////////////////////////////////
    public static String[][] buildComboBoxValues() throws ClassNotFoundException, SQLException {
        String tableArray[] = {"client", "type", "jobStatus", "programmer", "platform", "csr",  "printer", "data",};
        String table;
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://davelaub.com:3306/dlaub25_lasersched", "dlaub25_fmi", "admin");
        ResultSet rs1;
        int i = tableArray.length;
        String[][] boxValues;
        boxValues = new String[i][20];
        for (int r = 0; r < 8; r++){
            table = tableArray[r];
            ArrayList tempValues = new ArrayList();
            PreparedStatement preparedStmt = connection.prepareStatement("SELECT " + table + "." + table + " FROM " + table);
            rs1 = preparedStmt.executeQuery();
            while(rs1.next()){
                int c = 0;
                //System.out.println(rs1.getString(table));
                String s = rs1.getString(table);
                tempValues.add(s);
                c++;
            }
            Object tempArray[] = tempValues.toArray();
            String stringArray[]=new String[tempArray.length];
            for (int j=0;j<stringArray.length;j++){
                stringArray[j]=tempArray[j].toString();
            }
            boxValues[r] = stringArray;
        }
        connection.close();
        return boxValues;
    }
    ////////////////////////////////////////////////////////////////////////////
    public void main(String[] args) {
        // TODO code application logic here
    }
}
