package controller;

import extras.Conexion;
import model.ModelConfiguration;
import java.awt.Image;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class ControllerConfiguration {
    private static Conexion mysql = new Conexion();
    private static Connection cn = mysql.getConnection();
    private static String sSQL = "";
    private PreparedStatement ps;
    
    //private String UPDATE = "UPDATE imagen SET nombre = ? WHERE id = 1";
    private String UPDATE = "UPDATE configuracion SET ruta_logo = ? WHERE idconfiguracion = 1";
    public String ruta_guardar = "./src/Imagenes/logo/";
    
    private String nombre;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
   
    //==================================================================================
    
    public boolean isUpdate(ControllerConfiguration dts){
        try {
            String sql = dts.UPDATE;
            
            ps = cn.prepareStatement(sql);
            ps.setString(1, dts.getNombre());
            ps.executeUpdate();
            
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return false;
    }
    
    public String getNombreImagen(){
        String nombre = "";
        try {
            String SQL = "SELECT ruta_logo nombre FROM configuracion WHERE idconfiguracion = 1";
            
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            
            if(rs.next()){
                nombre = rs.getString("nombre");
            }
            return nombre;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return nombre;
    }
    
    public Icon ajustarImagen(String rutaImagen, JLabel lblImagen, Image img){
        img = new ImageIcon(rutaImagen).getImage();
        ImageIcon img2 = new ImageIcon(img.getScaledInstance(lblImagen.getWidth(), lblImagen.getHeight(), Image.SCALE_SMOOTH));
        
        return img2;
    }
    
    public void copyImagen(String origen, String destino){
        Path DE = Paths.get(origen);
        Path A = Paths.get(destino);
        
        CopyOption[] opciones = new CopyOption[]{
            StandardCopyOption.REPLACE_EXISTING,
            StandardCopyOption.COPY_ATTRIBUTES
        };
        
        try {
            Files.copy(DE, A, opciones);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
