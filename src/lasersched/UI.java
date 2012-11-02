/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lasersched;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
//import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
//import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultRowSorter;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import lasersched.TableFilterDemo.MyTableModel;

/**
 *
 * @author dlaub
 */
public class UI extends javax.swing.JFrame  {

    /**
     * Creates new form UI
     */
    public static ArrayList modifiedRows = new ArrayList();
    
    public static JTable viewTable;
    
    public static int newRow = 0;
    
    public UI() throws SQLException, ClassNotFoundException {
        
        List<Image> icons = new ArrayList<Image>();
        
        icons.add(new ImageIcon("M:\\LASER\\FMI-LOGO-Vector(Dave's)16x16.gif").getImage());
        icons.add(new ImageIcon("M:\\LASER\\FMI-LOGO-Vector(Dave's)32x32.gif").getImage());
        icons.add(new ImageIcon("M:\\LASER\\FMI-LOGO-Vector(Dave's)64x64.gif").getImage());
        
        initComponents();
        this.setIconImages(icons);
        
        LaserSched.buildTable();
        
        viewTable = LaserSched.createAlternating(LaserSched.jTable2.getModel());
                
        viewTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        viewTable.getTableHeader().setReorderingAllowed(false);
        
        TableColumn jobNum  = viewTable.getColumnModel().getColumn(0);
        TableColumn client  = viewTable.getColumnModel().getColumn(1);
        TableColumn jobName  = viewTable.getColumnModel().getColumn(2);
        TableColumn mailDate  = viewTable.getColumnModel().getColumn(3);
        TableColumn type  = viewTable.getColumnModel().getColumn(4);
        TableColumn jobStatus  = viewTable.getColumnModel().getColumn(5);
        TableColumn notes  = viewTable.getColumnModel().getColumn(6);
        TableColumn programmer  = viewTable.getColumnModel().getColumn(7);
        TableColumn soDate  = viewTable.getColumnModel().getColumn(8);
        TableColumn approvDate  = viewTable.getColumnModel().getColumn(9);
        TableColumn prodDate  = viewTable.getColumnModel().getColumn(10);
        TableColumn platform  = viewTable.getColumnModel().getColumn(11);
        TableColumn csr  = viewTable.getColumnModel().getColumn(12);
        TableColumn printer  = viewTable.getColumnModel().getColumn(13);
        TableColumn data  = viewTable.getColumnModel().getColumn(14);
        TableColumn id  = viewTable.getColumnModel().getColumn(15);
        
        jobNum.setPreferredWidth(50);
        client.setPreferredWidth(150);
        jobName.setPreferredWidth(200);
        mailDate.setPreferredWidth(80);
        type.setPreferredWidth(50);
        jobStatus.setPreferredWidth(110);
        programmer.setPreferredWidth(80);
        soDate.setPreferredWidth(80);
        approvDate.setPreferredWidth(80);
        prodDate.setPreferredWidth(80);
        platform.setPreferredWidth(70);
        csr.setPreferredWidth(70);
        printer.setPreferredWidth(70);
        data.setPreferredWidth(75);
        notes.setPreferredWidth(275);
        id.setPreferredWidth(60);
        
        jScrollPane1.setViewportView(viewTable);
        
        autoScroll(jScrollPane1);
        this.setExtendedState(MAXIMIZED_BOTH);
        
        
        this.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                System.out.println("Close Event Occured");
                int n = 3;
                if(LaserSched.tableChangedFlag){
                    Object[] options = {"Yes", "No", "Cancel"};
                    JFrame frame = new JFrame();
                    n = JOptionPane.showOptionDialog(frame, "Would you like to save changes?",
                            "Save",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[2]);
                    if(n == 0){
                        try {
                            LaserSched.buildUpdateQuery(LaserSched.getTableData(LaserSched.jTable2));
                        } catch (SQLException | ClassNotFoundException ex) {
                            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        System.exit(0);
                    }
                    else if(n == 1){
                        System.exit(0);
                    }
                    else if(n == 2){
                        
                    }
                    
                }
                if(n == 3){
                    System.exit(0);
                }
            }
        });
        
        Action action = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                TableCellListener tcl = (TableCellListener)e.getSource();
                int column = tcl.getColumn();
                int row = tcl.getRow();
                String oldValue = tcl.getOldValue().toString();
                String newValue = tcl.getNewValue().toString();
                Object objOldValue = tcl.getOldValue();
                Object objNewValue = tcl.getNewValue();
                System.out.println("Row   : " + tcl.getRow());
                System.out.println("Column: " + tcl.getColumn());
                System.out.println("Old   : " + tcl.getOldValue());
                System.out.println("New   : " + tcl.getNewValue());
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                
                if(objNewValue instanceof Date){
                    Object date = objNewValue;
                    date = f.format(date);
                    TableModel m = viewTable.getModel();
                    m.setValueAt(date, tcl.getRow(), tcl.getColumn());
                }
                
                if(!(oldValue.equals(newValue))){
                    System.out.println("Table Has Been Modified");
                    LaserSched.tableChangedFlag = true;
                    modifiedRows.add(viewTable.getValueAt(row, 15));
                    try {
                        LaserSched.buildUpdateQuery(LaserSched.getTableData(viewTable));
                    } catch (SQLException | ClassNotFoundException ex) {
                        Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if(column == 5 && newValue.contains("Approved")){
                    System.out.println("Job changed to approved");
                    String jobNum = LaserSched.jTable2.getModel().getValueAt(row, 0).toString();
                    String clientName = LaserSched.jTable2.getModel().getValueAt(row, 1).toString();
                    String jobName = LaserSched.jTable2.getModel().getValueAt(row, 2).toString();
                    String personApproving = System.getProperty("user.name");
                    String address = "davelaub25@gmail.com";
                    String subject = jobNum + " " + clientName + " " + jobName + " Approved";
                    String text = "The job status of " + jobNum + " " + clientName + " " + jobName + " has just been changed to approved by " + personApproving + ".\n\n";
                    Email sender = new Email();
                    try {
                        sender.sendMail(address, subject, text);
                    } catch (Exception ex) {
                        Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
    TableCellListener tcl = new TableCellListener(viewTable, action);
    
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        updateTable = new javax.swing.JButton();
        insertRow = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        printButton = new javax.swing.JButton();
        refreshJButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("fmi Laser Status Center");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setName("jframe"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        updateTable.setText("Update Table");
        updateTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateTableActionPerformed(evt);
            }
        });

        insertRow.setText("Insert Row");
        insertRow.setToolTipText("");
        insertRow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertRowActionPerformed(evt);
            }
        });

        jButton1.setText("Delete Selected Row");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        printButton.setText("Print");
        printButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printButtonActionPerformed(evt);
            }
        });

        refreshJButton.setText("Refresh");
        refreshJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshJButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(printButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(refreshJButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1346, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(updateTable)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(insertRow, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(insertRow)
                    .addComponent(updateTable)
                    .addComponent(jButton1)
                    .addComponent(printButton)
                    .addComponent(refreshJButton)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void updateTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateTableActionPerformed
        try {
            LaserSched.buildUpdateQuery(LaserSched.getTableData(viewTable));
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_updateTableActionPerformed

    private void insertRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertRowActionPerformed
        DefaultTableModel table1Model = (DefaultTableModel)viewTable.getModel();
        try { 
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
        }
        int id = 0;
        try {
            LaserSched.connection = DriverManager.getConnection("jdbc:mysql://davelaub.com:3306/dlaub25_lasersched","dlaub25_fmi","admin");
            ResultSet rs1 = LaserSched.connection.createStatement().executeQuery("SELECT MAX(id) FROM main");
            rs1.next();
            id = rs1.getInt(1) + 1;        // Adding 1 to get to the next unused ID to use as a temp ID
            newRow++;
        } catch (SQLException ex) {
            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
        }
        table1Model.insertRow(viewTable.getRowCount(),new Object[]{null,null,null,null,null,null,null,null,null,null,null,null,null,null,"",(id)});
        LaserSched.newRows.add(id);
    }//GEN-LAST:event_insertRowActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        ((DefaultTableModel)viewTable.getModel()).removeRow(viewTable.getSelectedRow());
    }//GEN-LAST:event_jButton1ActionPerformed

    private void printButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printButtonActionPerformed
        DefaultTableModel printModel = (DefaultTableModel) UI.viewTable.getModel();
        JTable printTable = new JTable(printModel);
        printTable.setAutoCreateColumnsFromModel(false);
        for (int i = 0; i < printTable.getModel().getColumnCount(); i++) {
            System.out.println(printTable.getModel().getColumnClass(i));
        }
        printTable.removeColumn(printTable.getColumnModel().getColumn(10));
        printTable.removeColumn(printTable.getColumnModel().getColumn(10));
        printTable.removeColumn(printTable.getColumnModel().getColumn(10));
        printTable.removeColumn(printTable.getColumnModel().getColumn(10));
        printTable.removeColumn(printTable.getColumnModel().getColumn(11));
        printTable.setPreferredScrollableViewportSize(printTable.getPreferredSize());
        
        
        
        Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.HOUR_OF_DAY);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        // get start of this week in milliseconds
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        RowFilter<MyTableModel, Object> filter = null;
        Date weekStart = new Date(cal.getTimeInMillis());
        System.out.println(weekStart);
        List<RowFilter<Object,Object>> filters = new ArrayList<RowFilter<Object,Object>>(1);
        
        filters.add(RowFilter.dateFilter(RowFilter.ComparisonType.AFTER, weekStart, 3));
        filters.add(RowFilter.regexFilter("Approved", 5));
        filters.add(RowFilter.regexFilter("Setting Up", 5));
        filters.add(RowFilter.regexFilter("Need Approval", 5));
        filters.add(RowFilter.regexFilter("On Hold", 5));
        filters.add(RowFilter.regexFilter("Out to Laser", 5));
        filters.add(RowFilter.regexFilter("Partially Printed", 5));
        filter = RowFilter.orFilter(filters);
                        
        printTable.setAutoCreateRowSorter(true);
        DefaultRowSorter sorter = ((DefaultRowSorter)printTable.getRowSorter());
        sorter.setRowFilter(filter);
        ArrayList list = new ArrayList();
        list.add( new RowSorter.SortKey(1, SortOrder.ASCENDING) );
        sorter.setSortKeys(list);
        sorter.sort();
        TableColumn jobNum  = printTable.getColumnModel().getColumn(0);
        TableColumn client  = printTable.getColumnModel().getColumn(1);
        TableColumn jobName  = printTable.getColumnModel().getColumn(2);
        TableColumn mailDate  = printTable.getColumnModel().getColumn(3);
        TableColumn type  = printTable.getColumnModel().getColumn(4);
        TableColumn jobStatus  = printTable.getColumnModel().getColumn(5);
        TableColumn notes  = printTable.getColumnModel().getColumn(6);
        TableColumn programmer  = printTable.getColumnModel().getColumn(7);
        TableColumn soDate  = printTable.getColumnModel().getColumn(8);
        TableColumn approvDate  = printTable.getColumnModel().getColumn(9);
        TableColumn data  = printTable.getColumnModel().getColumn(10);
        
        jobNum.setPreferredWidth(100);
        client.setPreferredWidth(150);
        jobName.setPreferredWidth(250);
        mailDate.setPreferredWidth(200);
        type.setPreferredWidth(120);
        jobStatus.setPreferredWidth(240);
        programmer.setPreferredWidth(120);
        soDate.setPreferredWidth(200);
        approvDate.setPreferredWidth(200);
        data.setPreferredWidth(150);
        notes.setPreferredWidth(600);
        
        JScrollPane printPane = new JScrollPane(printTable);
        JFrame frame = new JFrame("TableDemo");
        frame.setPreferredSize(new Dimension(1000, 800));
        frame.setContentPane(printPane);
        frame.pack();
        frame.setVisible(true);
        PrintUtilities.printComponent(printPane);
    }//GEN-LAST:event_printButtonActionPerformed

    private void refreshJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshJButtonActionPerformed
        LaserSched.buildTable();
        try {
            viewTable = LaserSched.createAlternating(LaserSched.jTable2.getModel());
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
        }
        jScrollPane1.setViewportView(viewTable);
        TableColumn jobNum  = viewTable.getColumnModel().getColumn(0);
        TableColumn client  = viewTable.getColumnModel().getColumn(1);
        TableColumn jobName  = viewTable.getColumnModel().getColumn(2);
        TableColumn mailDate  = viewTable.getColumnModel().getColumn(3);
        TableColumn type  = viewTable.getColumnModel().getColumn(4);
        TableColumn jobStatus  = viewTable.getColumnModel().getColumn(5);
        TableColumn notes  = viewTable.getColumnModel().getColumn(6);
        TableColumn programmer  = viewTable.getColumnModel().getColumn(7);
        TableColumn soDate  = viewTable.getColumnModel().getColumn(8);
        TableColumn approvDate  = viewTable.getColumnModel().getColumn(9);
        TableColumn prodDate  = viewTable.getColumnModel().getColumn(10);
        TableColumn platform  = viewTable.getColumnModel().getColumn(11);
        TableColumn csr  = viewTable.getColumnModel().getColumn(12);
        TableColumn printer  = viewTable.getColumnModel().getColumn(13);
        TableColumn data  = viewTable.getColumnModel().getColumn(14);
        TableColumn id  = viewTable.getColumnModel().getColumn(15);
        autoScroll(jScrollPane1);
        jobNum.setPreferredWidth(50);
        client.setPreferredWidth(150);
        jobName.setPreferredWidth(200);
        mailDate.setPreferredWidth(80);
        type.setPreferredWidth(50);
        jobStatus.setPreferredWidth(110);
        programmer.setPreferredWidth(80);
        soDate.setPreferredWidth(80);
        approvDate.setPreferredWidth(80);
        prodDate.setPreferredWidth(80);
        platform.setPreferredWidth(70);
        csr.setPreferredWidth(70);
        printer.setPreferredWidth(70);
        data.setPreferredWidth(75);
        notes.setPreferredWidth(275);
        id.setPreferredWidth(60);
    }//GEN-LAST:event_refreshJButtonActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowOpened

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws SQLException {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new UI().setVisible(true);
                } catch (SQLException | ClassNotFoundException ex) {
                    Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
    }
////////////////////////////////////////////////////////////////////////////////    
   public TableCellRenderer dateCellRenderer = new DefaultTableCellRenderer() {

    SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");

       @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if( value instanceof Date) {
            // Use SimpleDateFormat class to get a formatted String from Date object.
            String strDate = new SimpleDateFormat("MM/dd/yyyy").format((Date)value);
            // Sorting algorithm will work with model value. So you dont need to worry
            // about the renderer's display value. 
            this.setText( strDate );
        }
        return super.getTableCellRendererComponent(table, value, isSelected,
                hasFocus, row, column);
    }
};
////////////////////////////////////////////////////////////////////////////////
public void autoScroll(JScrollPane sPane){
    sPane.validate();
    JScrollBar vScroll = sPane.getVerticalScrollBar();
    vScroll.setValue(vScroll.getMaximum());
}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton insertRow;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton printButton;
    private javax.swing.JButton refreshJButton;
    private javax.swing.JButton updateTable;
    // End of variables declaration//GEN-END:variables
}
