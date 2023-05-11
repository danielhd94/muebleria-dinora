package controller;

import extras.Conexion;
import model.ModelInventory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ControllerInventory {
    private Conexion mysql = new Conexion();
    private Connection cn = mysql.getConnection();
    private String sSQL = "";
    
    public boolean insertar(ModelInventory dts) {
        sSQL = "insert into inventario (idproducto,idempleado,habia,tipomovimiento,cantidad,fecha) "
                    + "values(?,?,?,?,?,CURRENT_TIMESTAMP)";

        try {
            PreparedStatement pst;
            pst = cn.prepareStatement(sSQL);
            pst.setInt(1, dts.getIdproducto());
            pst.setInt(2, dts.getIdempleado());
            pst.setInt(3, dts.getHabia());
            pst.setDouble(4, dts.getTipoMovimiento());
            pst.setInt(5, dts.getCantidad());

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
