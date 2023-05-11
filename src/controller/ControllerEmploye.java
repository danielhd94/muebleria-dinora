package controller;

import extras.Conexion;
import extras.AES;
import model.ModelEmploye;
import view.frmEnviarCorreo;
import view.FrmLogin;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Gilberto Hernandez
 */
public class ControllerEmploye {

    private Conexion mysql = new Conexion();
    private Connection cn = mysql.getConnection();
    private String sSQL = "";
    public Integer totalresgistros;

    public DefaultTableModel mostrar(String buscar) {

        DefaultTableModel modelo;
        String[] titulos = {"ID", "NOMBRE", "USUARIO", "PASSWORD", "EMAIL", "PRIVILEGIO"};
        String[] registro = new String[6];
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

        sSQL = "select * from empleado where inactivo = 0 AND nombre like '%" + buscar + "%' order by idempleado";

        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            while (rs.next()) {                
                if (!rs.getString("nombre").equals("RECUPERACION")) {
                    registro[0] = rs.getString("idempleado");
                    registro[1] = rs.getString("nombre");
                    registro[2] = rs.getString("usuario");
                    registro[3] = AES.decrypt(rs.getString("password"));
                    registro[4] = rs.getString("email");
                    registro[5] = rs.getString("privilegio");
                    totalresgistros += 1;
                    modelo.addRow(registro);
                }
            }

            return modelo;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            return null;

        }

    }

    public boolean insertar(ModelEmploye dts) {

        sSQL = "insert into empleado (nombre,usuario,password,email,privilegio)"
                + "values(?,?,?,?,?)";
        try {
            PreparedStatement pst = cn.prepareStatement(sSQL);
            pst.setString(1, dts.getNombre());
            pst.setString(2, dts.getUsuario());
            pst.setString(3, dts.getPassword());
            pst.setString(4, dts.getEmail());
            pst.setInt(5, dts.getPrivilegio());

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
    

    public boolean editar(ModelEmploye dts) {
        sSQL = "update empleado set nombre=?,usuario=?,password=?,email=?, privilegio=? "
                + "where idempleado=?";

        try {
            PreparedStatement pst = cn.prepareStatement(sSQL);
            pst.setString(1, dts.getNombre());
            pst.setString(2, dts.getUsuario());
            pst.setString(3, dts.getPassword());
            pst.setString(4, dts.getEmail());
            pst.setInt(5, dts.getPrivilegio());
            pst.setInt(6, dts.getIdempleado());
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
    
    public boolean verificarUsuario(String email) {
        sSQL = "select email from empleado where email = '"+email+"'";
        boolean respuesta = false;

        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            while (rs.next()) {
                if (!rs.getString("email").equals("")) {
                    respuesta = true;
                }

            }
        }catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            respuesta = false;

        }
        return respuesta;

    }

    public boolean eliminar(ModelEmploye dts) {
        sSQL = "UPDATE empleado SET inactivo = 1 WHERE idempleado = ?";

        try {
            PreparedStatement pst = cn.prepareStatement(sSQL);
            pst.setInt(1, dts.getIdempleado());

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

    public boolean validar(String user,String pass){
        sSQL = "select * from empleado where (usuario= ? or email = ? ) and password= ?";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean usuarioActivo = false;
        try {
            stmt = cn.prepareStatement(sSQL);
            stmt.setString(1, user);
            stmt.setString(2, user);
            stmt.setString(3, AES.encrypt(pass));
            //stmt.setString(3, AES.decrypt(pass));
            
            rs = stmt.executeQuery();

            while (rs.next()) {
                if (rs.getString("usuario").equals("admin") && rs.getString("password").equals("admin")) {
                    FrmLogin padre = null;
                    frmEnviarCorreo enviar = new frmEnviarCorreo(padre, true);
                    enviar.setVisible(true);
                } else {
                    if ((rs.getString("usuario").equals(user) || rs.getString("email").equals(user)) && rs.getString("password").equals(AES.encrypt(pass))) {
                        usuarioActivo = true;
                    }
                }

            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en el login: " + e);
            usuarioActivo = false;

        }
        return usuarioActivo;
    }
    
    public ModelEmploye getWorker(String user, String pass) {
        ModelEmploye empleado= new ModelEmploye();
        PreparedStatement stmt;
        sSQL = "select * from empleado where (usuario= ? or email = ? ) and password= ?";
        
        try {
            stmt = cn.prepareStatement(sSQL);
            stmt.setString(1, user);
            stmt.setString(2, user);
            //stmt.setString(3, AES.encrypt(pass));
            stmt.setString(3, AES.decrypt(pass));
            
            ResultSet rs=stmt.executeQuery();
            if(rs.next()){
                empleado.setIdempleado(rs.getInt("idempleado"));
                empleado.setNombre(rs.getString("nombre"));
                empleado.setUsuario(rs.getString("usuario"));
                empleado.setPassword(rs.getString("password"));
                empleado.setEmail(rs.getString("email"));
                empleado.setPrivilegio(rs.getInt("privilegio"));
                empleado.setInactivo(rs.getInt("inactivo"));
            }
            
        } catch (SQLException ex) {
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return empleado;
    }

    public boolean perteneceAdministrador(String user) {
        sSQL = "select privilegio from empleado where usuario = ?";
        boolean respuesta = false;
        PreparedStatement stmt;
        try {
            stmt = cn.prepareStatement(sSQL);
            stmt.setString(1, user);
            ResultSet rs=stmt.executeQuery();

            while (rs.next()) {
                if (rs.getString("privilegio").equals("1")) {
                    respuesta = true;
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            return respuesta;
        }
        return respuesta;
    }

    public boolean defaultuser(String usuario, String password) {
        sSQL = "select privilegio from empleado where usuario = '" + usuario + "' AND password = '" + password + "'";
        boolean respuesta = false;
        try {
            PreparedStatement pst = cn.prepareStatement(sSQL);
            ResultSet rs = pst.executeQuery(sSQL);

            while (rs.next()) {
                if (rs.getString("privilegio").equals("1")) {
                    respuesta = true;
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            return respuesta;
        }
        return respuesta;
    }

}
