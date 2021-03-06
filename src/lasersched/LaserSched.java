    /*
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lasersched;

import com.ezware.oxbow.swingbits.table.filter.TableRowFilterSupport;
import java.awt.Color;
import java.awt.Component;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
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
     public static ArrayList newRowsID = new ArrayList();
     
    /**
     * @param args the command line arguments
     */
         
    @SuppressWarnings("unchecked")
    public static void buildTable() /*throws SQLException*/ {
        try {
            Class.forName("com.mysql.jdbc.Driver"); 
            connection = DriverManager.getConnection("jdbc:mysql://10.10.10.14:3306/dlaub25_lasersched","dlaub25_fmi","admin"); 
            ResultSet rs1 = connection.createStatement().executeQuery("SELECT * FROM `main`");
            ResultSetMetaData md1 = rs1.getMetaData();
            System.out.println("Connection succeed!"); 
         
            DefaultTableModel model = new DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
                },
            new String [] {
                "Job #", "Client", "Job Name", "Mail Date", "Type", "Job Status", "Notes", "Programmer", "Sign Offs", "Approved", 
                "Production", "Platform", "CSR", "Printer", "Data", "ID"
                }
                
            ){
                Class[] types = new Class [] {  
                    //COL. TYPES ARE HERE!!!  
                    java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.util.Date.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.util.Date.class, java.util.Date.class, java.util.Date.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class 
                };  

                @Override  
                public Class getColumnClass(int columnIndex) {  
                    return types [columnIndex];  
                }
            };
            model.removeRow(0);
            jTable2 = new javax.swing.JTable(model);
            rs1.beforeFirst();
            int numberOfColumns = md1.getColumnCount();
            while (rs1.next()){
                Object [] rowData = new Object[numberOfColumns];
                for (int i = 0; i < rowData.length; ++i)
                {
                    //System.out.println(rs1.getString(i+1));
                    if(rs1.getString(i+1) == null){
                        rowData[i] = rs1.getObject(i+1);
                    }
                    else if( i == 9 || i == 10 ){
                        java.sql.Timestamp  sqlTimeStamp = (java.sql.Timestamp)rs1.getObject(i+1);
                        Date utilDate = new Date(sqlTimeStamp.getTime());
                        rowData[i] = utilDate;
                    }
                    else if( i == 3 || i == 8){
                        java.sql.Date  sqlDate = (java.sql.Date)rs1.getObject(i+1);
                        Date utilDate = new Date(sqlDate.getTime());
                        rowData[i] = utilDate;
                    }
                    else{
                        rowData[i] = rs1.getObject(i+1);
                    }
                }
                model.addRow(rowData);
                }
            /*JScrollPane printPane = new JScrollPane(jTable2);
             * JFrame frame = new JFrame("TableDemo");
             * frame.setContentPane(printPane);
             * frame.pack();
             * frame.setVisible(true);*/
            } catch (SQLException | ClassNotFoundException | NullPointerException  ex) {
                Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
                JFrame frame = new JFrame("User Error... Replace User");
                final Writer result = new StringWriter();
                final PrintWriter printWriter = new PrintWriter(result);
                ex.printStackTrace(printWriter);
                JOptionPane.showMessageDialog(frame,result.toString());
            } 
        }
    
    ////////////////////////////////////////////////////////////////////////////
    public static void buildUpdateQuery(Object[][] rowData) throws SQLException, ClassNotFoundException {
        System.out.println("New Update Query Started");
        
        DefaultTableModel dtm = (DefaultTableModel) UI.viewTable.getModel();
        
        int nRow = dtm.getRowCount(), nCol = dtm.getColumnCount();
        
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://10.10.10.14:3306/dlaub25_lasersched","dlaub25_fmi","admin"); 
        ResultSet rs1 = connection.createStatement().executeQuery("SELECT * FROM main");
        ResultSetMetaData md1 = rs1.getMetaData();
        for (int k = 0; k < newRows.size(); k++) {
            int rowNum = Integer.parseInt(newRows.get(k).toString())-1;
            String insertQuery = "INSERT INTO `dlaub25_lasersched`.`main` (`jobNum`, `client`, `jobName`, `mailDate`, `type`, `jobStatus`, `Notes`, `programmer`, `signOffs`, `approved`, `production`, `platform`, `csr`, `printer`, `data`, `id`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0);";
            PreparedStatement preparedStmtInsert = connection.prepareStatement(insertQuery);
            if(rowData[rowNum][0] != null && rowData[rowNum][1] != null && rowData[rowNum][2] != null && rowData[rowNum][3] != null && rowData[rowNum][4] != null && rowData[rowNum][5] != null && rowData[rowNum][7] != null && rowData[rowNum][11] != null && rowData[rowNum][12] != null && rowData[rowNum][14] != null){
                for( int i = 1 ; i <= (nCol - 1) ; i++){      //Using nCol - 1 to skip the ID field
                    if (rowData[rowNum][i-1] == null){
                        if( i == 3 || i == 8 || i == 9 || i == 10 ){
                            preparedStmtInsert.setNull(i, java.sql.Types.DATE);
                        }
                        if( i == 1 || i == 2 || i == 4 || i == 5 || i == 6 || i == 7 || i == 11 || i == 12 || i == 13 || i == 14 ){
                            preparedStmtInsert.setNull(i, java.sql.Types.VARCHAR);
                        }
                        if( i == 0 || i == 15 ){
                            preparedStmtInsert.setNull(i, java.sql.Types.INTEGER);
                        }
                    }
                    else{
                        preparedStmtInsert.setObject(i, rowData[rowNum][i-1].toString());
                    }
                }
                System.out.println("ONE ATTEMPT TO INSERT");
                preparedStmtInsert.executeUpdate();  
            }
        }
        for (int l = 0; l < UI.modifiedRow.size(); l++) {
            String updateQuery = "UPDATE main SET jobNum = ?, client = ?, jobName = ?, mailDate = ?, type = ?, jobStatus = ?, notes = ?, programmer = ?, signOffs = ?, approved = ?, production = ?, platform = ?, csr = ?, printer = ?, data = ? WHERE id = ?";
            PreparedStatement preparedStmtUpdate = connection.prepareStatement(updateQuery);
            int rowNum = Integer.parseInt(UI.modifiedRow.get(l).toString());
            for( int i = 1 ; i <= nCol ; i++){
                if (rowData[rowNum][i-1] == null){
                    if( i == 3 || i == 8 || i == 9 || i == 10 ){
                        preparedStmtUpdate.setNull(i, java.sql.Types.DATE);
                    }
                    if( i == 1 || i == 2 || i == 4 || i == 5 || i == 6 || i == 7 || i == 11 || i == 12 || i == 13 || i == 14 ){
                        preparedStmtUpdate.setNull(i, java.sql.Types.VARCHAR);
                    }
                    if( i == 0 || i == 15 ){
                        preparedStmtUpdate.setNull(i, java.sql.Types.INTEGER);
                    }
                }
                else{
                    preparedStmtUpdate.setString(i, rowData[rowNum][i-1].toString());
                }
            }
            System.out.println(preparedStmtUpdate.toString());
            preparedStmtUpdate.executeUpdate();
        }
        tableChangedFlag = false;
        connection.close();
        newRows.clear();
        newRowsID.clear();
        UI.modifiedRow.clear();
        UI.modifiedRowID.clear();
        System.out.println("Update Query Finished");
    }
    ////////////////////////////////////////////////////////////////////////////
    public static Object[][] getTableData (JTable table) {
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        int nRow = dtm.getRowCount(), nCol = dtm.getColumnCount();
        Object[][] tableData = new Object[nRow][nCol];
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0 ; i < nRow ; i++){
            for (int j = 0 ; j < nCol ; j++){
                Object value = UI.viewTable.getModel().getValueAt(i, j);
                if(value instanceof Date){
                    //System.out.println("Date Found: " + value);
                    value = f.format((Date)value);
                    //System.out.println("Formatted Version: " + value);
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
                        c.setForeground(Color.BLACK);
                        if ("Approved".equals(type)) c.setBackground(Color.YELLOW);
                        if ("Printed".equals(type)) c.setBackground(Color.BLUE);
                        if ("Need Approval".equals(type)) c.setBackground(Color.ORANGE);
                        if ("Need Approval".equals(type)) c.setForeground(Color.BLUE);
                        if ("Printed".equals(type)) c.setForeground(Color.WHITE);
                        if ("Out to Laser".equals(type)) c.setBackground(Color.GREEN);
                        if ("Printing".equals(type)) c.setBackground(Color.CYAN);
                        if ("Partially Printed".equals(type)) c.setBackground(Color.CYAN);
                        if ("On Hold".equals(type)) c.setForeground(Color.LIGHT_GRAY);
                        if ("On Hold".equals(type)) c.setBackground(Color.GRAY);
                        if ("Canceled".equals(type)) c.setForeground(Color.LIGHT_GRAY);
                        if ("Canceled".equals(type)) c.setBackground(Color.GRAY);
                        if ("Sign Offs due by:".equals(type)) c.setBackground(Color.PINK);
                        //
                        
                        return c;
                }
        };
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        TableCellRenderer dateRenderer = new FormatRenderer( f );
        TableColumn clientComboColumn1 = table.getColumnModel().getColumn(1);
        TableColumn clientComboColumn4 = table.getColumnModel().getColumn(4);
        TableColumn clientComboColumn5 = table.getColumnModel().getColumn(5);
        TableColumn clientComboColumn7 = table.getColumnModel().getColumn(7);
        TableColumn clientComboColumn11 = table.getColumnModel().getColumn(11);
        TableColumn clientComboColumn12 = table.getColumnModel().getColumn(12);
        TableColumn clientComboColumn13 = table.getColumnModel().getColumn(13);
        TableColumn clientComboColumn14 = table.getColumnModel().getColumn(14);
        TableColumn dateColumn3 = table.getColumnModel().getColumn(3);
        TableColumn dateColumn8 = table.getColumnModel().getColumn(8);
        TableColumn dateColumn9 = table.getColumnModel().getColumn(9);
        TableColumn dateColumn10 = table.getColumnModel().getColumn(10);

        String[][] values = buildComboBoxValues();

        clientComboColumn1.setCellEditor(new MyComboBoxEditor(values[0]));
        clientComboColumn4.setCellEditor(new MyComboBoxEditor(values[1]));
        clientComboColumn5.setCellEditor(new MyComboBoxEditor(values[2]));
        clientComboColumn7.setCellEditor(new MyComboBoxEditor(values[3]));
        clientComboColumn11.setCellEditor(new MyComboBoxEditor(values[4]));
        clientComboColumn12.setCellEditor(new MyComboBoxEditor(values[5]));
        clientComboColumn13.setCellEditor(new MyComboBoxEditor(values[6]));
        clientComboColumn14.setCellEditor(new MyComboBoxEditor(values[7]));
        dateColumn3.setCellEditor( new JDateChooserCellEditor());
        dateColumn8.setCellEditor( new JDateChooserCellEditor());
        dateColumn9.setCellEditor( new JDateChooserCellEditor());
        dateColumn10.setCellEditor( new JDateChooserCellEditor());
        dateColumn3.setCellRenderer(dateRenderer);
        dateColumn8.setCellRenderer(dateRenderer);
        dateColumn9.setCellRenderer(dateRenderer);
        dateColumn10.setCellRenderer(dateRenderer);
        
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
        connection = DriverManager.getConnection("jdbc:mysql://10.10.10.14:3306/dlaub25_lasersched","dlaub25_fmi","admin");
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
        connection = DriverManager.getConnection("jdbc:mysql://10.10.10.14:3306/dlaub25_lasersched", "dlaub25_fmi", "admin");
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
