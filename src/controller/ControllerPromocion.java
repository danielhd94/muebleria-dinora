package controller;

import extras.Conexion;
import model.ModelPromocion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class ControllerPromocion {

    private Conexion mysql = new Conexion();
    private Connection cn = mysql.getConnection();
    private String sSQL = "";
    public Integer totalresgistros;

    public DefaultTableModel mostrar(String buscar) {

        DefaultTableModel modelo;
        String[] titulos = {"ID", "N° Serie", "NOMBRE", "DESDE", "HASTA", "PRECIO PROMOCION"};
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
        sSQL = "select * from promocion where nombre like '%" + buscar + "%' order by idpromocion";
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            int c = 0;
            while (rs.next()) {
                registro[0] = rs.getString("idpromocion");
                registro[1] = rs.getString("idproducto");
                registro[2] = rs.getString("nombre");
                registro[3] = rs.getString("desde");
                registro[4] = rs.getString("hasta");
                registro[5] = rs.getString("preciopromocion");
                totalresgistros += 1;
                modelo.addRow(registro);

            }
            return modelo;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            return null;

        }

    }

    public boolean insertar(ModelPromocion dts) {
        sSQL = "insert into promocion (idproducto,nombre,desde,hasta,preciopromocion) "
                + "values(?,?,?,?,?)";

        try {
            PreparedStatement pst = cn.prepareStatement(sSQL);
            pst.setInt(1, dts.getIdproducto());
            pst.setString(2, dts.getNombre());
            pst.setInt(3, dts.getDesde());
            pst.setInt(4, dts.getHasta());
            pst.setDouble(5, dts.getPreciopromocion());

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

    public boolean eliminar(ModelPromocion dts) {
        sSQL = "delete from promocion where idpromocion=?";

        try {
            PreparedStatement pst = cn.prepareStatement(sSQL);
            pst.setInt(1, dts.getIdpromocion());

            int n = pst.executeUpdate();
            if (n != 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            return false;
        }

    }

}
