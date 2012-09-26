/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lasersched;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

/**
 *
 * @author Dave
 */
public class MyComboBoxEditor extends DefaultCellEditor {
    public MyComboBoxEditor(String[] items) {
        super(new JComboBox(items));
    }
}