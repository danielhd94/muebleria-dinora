package controller;

import extras.Conexion;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class fReporteMov {

    private Conexion mysql = new Conexion();
    private Connection cn = mysql.getConnection();
    private String sSQL = "";
    public Integer totalresgistros;

    public DefaultTableModel mostrar(String fecha, String caj_pro_cat, int indexCombo) {

        DefaultTableModel modelo;
        String[] titulos = {"ID","HORA", "DESCRIPCION", "HABÍA", "TIPO", "CANTIDAD", "CAJERO", "CATEGORIA"};
        String[] registro = new String[8];
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

        if (indexCombo == 0) {
            sSQL = "SELECT \n"
                    + "inv.idinvetario,\n"
                    + "Date_format(inv.fecha,'%h:%i %p') HORA,\n"
                    + "p.descripcion DESCRIPCION,\n"
                    + "inv.habia HABIA,\n"
                    + "inv.tipomovimiento TIPO,\n"
                    + "inv.cantidad CANTIDAD,\n"
                    + "em.nombre CAJERO,\n"
                    + "cat.nombre CATEGORIA\n"
                    + "FROM inventario inv\n"
                    + "INNER JOIN producto p\n"
                    + "ON inv.idproducto = p.idproducto\n"
                    + "INNER JOIN empleado em\n"
                    + "ON inv.idempleado = em.idempleado\n"
                    + "INNER JOIN categoria cat\n"
                    + "ON p.idcategoria = cat.idcategoria\n"
                    + "WHERE (Date_format(inv.fecha,'%Y-%m-%d') = '"+fecha + "') AND (em.nombre like '" + caj_pro_cat + "%' OR (p.descripcion regexp '^" + caj_pro_cat + "$' OR p.descripcion regexp '^" + caj_pro_cat + ".') OR (cat.nombre regexp '^" + caj_pro_cat + "$' OR cat.nombre regexp '^" + caj_pro_cat + ".')) "
                    + "ORDER BY Date_format(inv.fecha,'%h:%i %p') DESC";
        } else {
            sSQL = "SELECT \n"
                    + "inv.idinvetario,\n"
                    + "Date_format(inv.fecha,'%h:%i %p') HORA,\n"
                    + "p.descripcion DESCRIPCION,\n"
                    + "inv.habia HABIA,\n"
                    + "inv.tipomovimiento TIPO,\n"
                    + "inv.cantidad CANTIDAD,\n"
                    + "em.nombre CAJERO,\n"
                    + "cat.nombre CATEGORIA\n"
                    + "FROM inventario inv\n"
                    + "INNER JOIN producto p\n"
                    + "ON inv.idproducto = p.idproducto\n"
                    + "INNER JOIN empleado em\n"
                    + "ON inv.idempleado = em.idempleado\n"
                    + "INNER JOIN categoria cat\n"
                    + "ON p.idcategoria = cat.idcategoria\n"
                    + "WHERE (Date_format(inv.fecha,'%Y-%m-%d') = '"+fecha + "') AND (em.nombre like '" + caj_pro_cat + "%' OR (p.descripcion regexp '^" + caj_pro_cat + "$' OR p.descripcion regexp '^" + caj_pro_cat + ".') OR (cat.nombre regexp '^" + caj_pro_cat + "$' OR cat.nombre regexp '^" + caj_pro_cat + ".'))"
                    + "AND (inv.tipomovimiento = " + indexCombo + ") "
                    + "ORDER BY Date_format(inv.fecha,'%h:%i %p') DESC";

        }

        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            while (rs.next()) {
                registro[0] = rs.getString("idinvetario");
                registro[1] = rs.getString("HORA");
                registro[2] = rs.getString("DESCRIPCION");
                registro[3] = rs.getString("HABIA");
                registro[4] = tipo(rs.getString("TIPO"));
                registro[5] = rs.getString("CANTIDAD");
                registro[6] = rs.getString("CAJERO");
                registro[7] = rs.getString("CATEGORIA");
                modelo.addRow(registro);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            return null;
        }
        return modelo;
    }

    private String tipo(String tipo) {
        switch (tipo) {
            case "1":
                tipo = "ENTRADA";
                break;
            case "2":
                tipo = "SALIDA";
                break;
            case "3":
                tipo = "AJUSTE";
                break;
            case "4":
                tipo = "DEVOLUCION";
                break;
            default:
                break;

        }

        return tipo;
    }

}
