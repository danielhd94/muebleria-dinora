/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import extras.Conexion;
import model.ModelTicket;
import model.ModelTicketDetail;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ControlllerTicketDetail {
    private Conexion mysql = new Conexion();
    private Connection cn = mysql.getConnection();
    private String sSQL = "";
    
    public boolean insertar(ModelTicketDetail dts) {
        sSQL = "insert into detalleticket (idticket,idproducto,cantidad,precioVenta,devuelto,cantidadDevo) "
                    + "values(?,?,?,?,?,?)";

        try {
            PreparedStatement pst;
            pst = cn.prepareStatement(sSQL);
            pst.setInt(1, dts.getIdticket());
            pst.setInt(2, dts.getIdproducto());
            pst.setInt(3, dts.getCantidad());
            pst.setDouble(4, dts.getPrecioVenta());
            pst.setInt(5, dts.getDevuelto());
            pst.setInt(6, dts.getCantidadDevo());

            int n = pst.executeUpdate();
            if (n != 0) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
            return false;
        }
    }
}
