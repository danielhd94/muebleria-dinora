/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extras;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Rojeru San CL
 */
public class EstiloTablaRenderer extends DefaultTableCellRenderer {

    private Component componente;
    private String StringTotal = "";

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        componente = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); //To change body of generated methods, choose Tools | Templates.       
        //this.setHorizontalAlignment(0); //ESTO LO ALINEA EN EL CENTRO
        this.setBorder(null);
        this.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new MyColor().COLOR_TABLE_BORDER_RENDER));
        
        
        if (row % 2 == 0) {
            componente.setForeground(new Color(31, 25, 26)); //31,25,26
            componente.setBackground(new Color(255, 255, 255));
        } else {
            componente.setForeground(new Color(31, 25, 26)); //31,25,26
            componente.setBackground(new Color(255, 255, 255));
        }
        if (isSelected) {
            componente.setForeground(Color.white);
            componente.setBackground(new MyColor().COLOR_TABLE_RENDER_SELECTED);  //[51,153,255] 32, 178, 170
            componente.setFont(new Font("Tahoma", Font.BOLD, 15));
        }

        return componente;

    }

}
