/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extras;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Rojeru San CL
 */
public class EstiloTablaHeader implements TableCellRenderer{

    @Override
    public Component getTableCellRendererComponent(JTable jtable, Object value, boolean bln, boolean bln1, int row, int column) {
        JComponent jcomponent = null;
        
        if(value instanceof String ) {
            jcomponent = new JLabel((String) "   " + value);
            ((JLabel)jcomponent).setHorizontalAlignment(SwingConstants.CENTER );
            ((JLabel)jcomponent).setSize( 30, jcomponent.getWidth() );
            ((JLabel)jcomponent).setPreferredSize( new Dimension(3, jcomponent.getWidth()));
        } 
        
        jcomponent.setEnabled(true);        
        jcomponent.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 0, 1, new MyColor().COLOR_TABLE_BORDER_RENDER));
        jcomponent.setOpaque(true);
        jcomponent.setBackground( new MyColor().COLOR_TABLE_HEADER );
        jcomponent.setForeground(Color.WHITE);
        jcomponent.setFont(new Font("Tahoma", Font.BOLD, 14));
        
        return jcomponent;
    }
    
}
