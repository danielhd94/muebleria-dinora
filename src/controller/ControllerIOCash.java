package controller;

import extras.Conexion;
import model.ModelIOCash;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.JOptionPane;


public class ControllerIOCash {
    private Conexion mysql = new Conexion();
    private Connection cn = mysql.getConnection();
    private String sSQL = "";
    
    public boolean insertar(ModelIOCash entsal) {
        
        sSQL = "insert into entradasalida (identsal,idempleado,monto,descripcion,tipo,fecha) "
                + "values(NULL,?,?,?,?,CURRENT_TIMESTAMP)";
        
        try {
            PreparedStatement pst = cn.prepareStatement(sSQL);
            pst.setInt(1, entsal.getIdEmpleado());
            pst.setDouble(2, entsal.getMonto());
            pst.setString(3, entsal.getDescripcion());
            pst.setInt(4, entsal.getTipo());

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
    
}
