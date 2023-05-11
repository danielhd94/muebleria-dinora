
package extras;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class Resaltador extends DefaultTableCellRenderer  {
private Component componente = null;
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        /*if (row % 2 == 0) {
            setBackground( new Color(240, 240, 240));
            setForeground(new Color(87,204,243));  //87,204,243
        }else{
            setBackground(Color.WHITE);
            setForeground(new Color(87,204,243));
        }*/
        
        componente =  super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); //To change body of generated methods, choose Tools | Templates.
        if (table.getValueAt(row, 1).toString().equals("1")) {
            componente.setBackground(Color.GREEN);
            componente.setForeground(Color.WHITE);
        }
        return componente;
        //return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
