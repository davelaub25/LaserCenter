/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lasersched;

import com.toedter.calendar.JDateChooser;
import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author geovanny
 */
public class JDateChooserCellEditor extends AbstractCellEditor implements
    TableCellEditor {

  private static final long serialVersionUID = 917881575221755609L;

  private JDateChooser dateChooser = new JDateChooser();

  public Component getTableCellEditorComponent(JTable table, Object value,
      boolean isSelected, int row, int column) {

    Date date = null;
    if (value instanceof Date)
      date = (Date) value;

    dateChooser.setDate(date);
    dateChooser.setDateFormatString("yyyy-MM-dd");
    return dateChooser;
  }

  public Object getCellEditorValue() {
    SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
    dateChooser.setDateFormatString("yyyy-MM-dd");
    Object d = new Date();
    d = (Date)dateChooser.getDate();
    d = f.format(d);
    return d;
  }
}