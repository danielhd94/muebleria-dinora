package controller;

import extras.Conexion;
import model.ModelMovClient;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ControllerMovClient {
    private Conexion mysql = new Conexion();
    private Connection cn = mysql.getConnection();
    private String sSQL = "";
    
    public boolean insertar(ModelMovClient dts) {
        sSQL = "insert into movimientocliente (idcliente,tipo,monto) "
                    + "values(?,?,?)";

        try {
            PreparedStatement pst;
            pst = cn.prepareStatement(sSQL);
            pst.setInt(1, dts.getIdcliente());
            pst.setInt(2, dts.getTipo());
            pst.setDouble(3, dts.getMonto());

            int n = pst.executeUpdate();
            if (n != 0) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error en insertar movimientocleinte datos"+ex);
            return false;
        }
    }
    
}
