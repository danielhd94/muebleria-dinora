
package extras;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class Resaltador2 extends JTable  {

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component component = super.prepareRenderer(renderer, row, column);
        component.setBackground(Color.WHITE);
        component.setForeground(Color.BLACK);
        String val = getValueAt(row, column).toString();
            if (val.equals("1")) {
                component.setBackground(Color.GREEN);                
                component.setForeground(Color.WHITE);
            }
        return component;
    }
        
}
