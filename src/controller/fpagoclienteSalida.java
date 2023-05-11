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
public class fpagoclienteSalida {

    private Conexion mysql = new Conexion();
    private Connection cn = mysql.getConnection();
    private String sSQL = "";

    public DefaultTableModel mostrar(String fecha) {

        DefaultTableModel modelo;
        String[] titulos = {"NOMBRE DEL CLIENTE", "MONTO"};
        String[] registro = new String[2];
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

        sSQL = "SELECT c.nombre,p.monto \n"
                + "FROM pago p \n"
                + "INNER JOIN cliente c \n"
                + "ON p.idcliente = c.idcliente \n"
                + "WHERE p.monto <> 0 AND Date_format(p.fecha,'%Y-%m-%d') = '" + fecha + "'";

        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            while (rs.next()) {
                registro[0] = rs.getString("nombre");
                registro[1] = rs.getString("monto");
                modelo.addRow(registro);
            }
            return modelo;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            return null;

        }

    }

    public DefaultTableModel salidas(String fecha) {

        DefaultTableModel modelo;
        String[] titulos = {"NOMBRE DEL PROVEEDOR", "MONTO"};
        String[] registro = new String[2];
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

        sSQL = "SELECT descripcion,monto\n"
                + "FROM entradasalida\n"
                + "WHERE tipo = 0 AND Date_format(fecha,'%Y-%m-%d') = '" + fecha + "'";

        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            while (rs.next()) {
                registro[0] = rs.getString("descripcion");
                registro[1] = rs.getString("monto");
                modelo.addRow(registro);
            }
            return modelo;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            return null;

        }

    }

}
