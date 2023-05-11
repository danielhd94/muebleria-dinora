/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import extras.Conexion;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Gilberto Hernandez
 */
public class fReporteIn {

    private Conexion mysql = new Conexion();
    private Connection cn = mysql.getConnection();
    private String sSQL = "";
    public Integer totalresgistros;

    public DefaultTableModel mostrar(String categoria, boolean op) {

        DefaultTableModel modelo;
        String[] titulos = {"ID", "NO. SERIE", "DESCRIPCIÓN", "PRECIO COSTO", "PRECIO VENTA", "PRECIO MAYOREO", "CATEGORIA", "EXISTENCIA", "INV. MÍNIMO"};
        String[] registro = new String[9];
        totalresgistros = 0;
        modelo = new DefaultTableModel(null, titulos) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 0) {
                    return true;
                } else {
                    return false;
                }
            }

        };
        if (op) {  
            sSQL = "SELECT p.idproducto, p.noserie, p.descripcion, p.precioCosto, p.precioVenta, p.precioMayoreo, c.nombre, p.existencia, p.cantidadMinima "
                + "FROM producto p "
                + "INNER JOIN categoria c "
                + "ON c.idcategoria = p.idcategoria "
                + "WHERE p.inactivo <> 1 AND c.nombre = '" + categoria + "' "
                + "ORDER BY p.idproducto DESC";
        }else{
            sSQL = "SELECT p.idproducto, p.noserie, p.descripcion, p.precioCosto, p.precioVenta, p.precioMayoreo, c.nombre, p.existencia, p.cantidadMinima "
                + "FROM producto p "
                + "INNER JOIN categoria c "
                + "ON c.idcategoria = p.idcategoria "
                + "WHERE p.inactivo <> 1 "
                + "ORDER BY p.idproducto DESC";
        }

        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            while (rs.next()) {
                registro[0] = rs.getString("idproducto");
                registro[1] = rs.getString("noserie");
                registro[2] = rs.getString("descripcion");
                registro[3] = rs.getString("precioCosto");
                registro[4] = rs.getString("precioVenta");
                registro[5] = rs.getString("precioMayoreo");
                registro[6] = rs.getString("nombre");
                registro[7] = rs.getString("existencia");
                registro[8] = rs.getString("cantidadMinima");
                //totalresgistros += 1;
                modelo.addRow(registro);
            }

            return modelo;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            return null;

        }
    }

}
