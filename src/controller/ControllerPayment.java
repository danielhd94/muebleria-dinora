/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import extras.Conexion;
import model.ModelCategory;
import model.ModelPaymet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class ControllerPayment {    
    private Conexion mysql = new Conexion();
    private Connection cn = mysql.getConnection();
    private String sSQL = "";
    
    public DefaultTableModel mostrar(String buscar) {
        DefaultTableModel modelo;
        String[] titulos = {"ID","MONTO","FECHA"};
        String[] registro = new String[3];
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

        sSQL = "SELECT idpago, monto, fecha FROM pago where idcliente like '%" + buscar + "%' AND monto <> 0";
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            while (rs.next()) {
                registro[0] = rs.getString("idpago");
                registro[1] = rs.getString("monto");
                registro[2] = rs.getString("fecha");
                modelo.addRow(registro);
            }

            return modelo;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            return null;

        }

    }
    
    public boolean insertar(ModelPaymet dts) {

        sSQL = "insert into pago (idcliente,idempleado,monto,fecha)"
                + "values(?,?,?,CURRENT_TIMESTAMP)";
        try {
            PreparedStatement pst = cn.prepareStatement(sSQL);
            pst.setInt(1, dts.getIdcliente());
            pst.setInt(2, dts.getIdempleado());
            pst.setDouble(3, dts.getMonto());

            int n = pst.executeUpdate();
            if (n != 0) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            return false;
        }
    }
    
    public boolean eliminar(ModelPaymet dts) {
        boolean respuesta = false;
        sSQL = "DELETE FROM pago WHERE idcliente = ? ";
        
        try {PreparedStatement pst = cn.prepareStatement(sSQL);
            pst.setInt(1, dts.getIdcliente());

            
            int n = pst.executeUpdate();
            if (n != 0) {
                respuesta = true;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            return false;
        }
        return respuesta;
    }
    
    public boolean eliminarAbono(int idpago) {
        boolean respuesta = false;
        sSQL = "DELETE FROM pago WHERE idpago = ? ";
        
        try {PreparedStatement pst = cn.prepareStatement(sSQL);
            pst.setInt(1, idpago);

            
            int n = pst.executeUpdate();
            if (n != 0) {
                respuesta = true;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            return false;
        }
        return respuesta;
    }
}
