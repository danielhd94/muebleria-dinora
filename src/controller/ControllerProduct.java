package controller;

import extras.Conexion;
import model.ModelProduct;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ControllerProduct {

    private Conexion mysql = new Conexion();
    private Connection cn = mysql.getConnection();
    private String sSQL = "";
    public Integer totalresgistros;

    public DefaultTableModel mostrar(String buscar) {

        DefaultTableModel modelo;
        String[] titulos = {"ID", "NO. SERIE", "DESCRIPCION", "PRECIO COSTO", "PRECIO VENTA", "PRECIO MAYOREO", "EXISTENCIA", "CATIDAD MINIMA", "CATEGORIA"};
        String[] registro = new String[9];
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

        sSQL = "SELECT p.idproducto,p.noserie,p.descripcion,p.precioCosto,p.precioVenta,p.precioMayoreo,p.existencia,p.cantidadMinima,c.nombre "
                + "FROM producto p "
                + "INNER JOIN categoria c "
                + "ON p.idcategoria = c.idcategoria "
                + "where p.inactivo <> 1 AND (p.noserie like '" + buscar + "%') "
                + "ORDER BY p.idproducto DESC";

        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            while (rs.next()) {
                registro[0] = rs.getString("idproducto");
                registro[1] = rs.getString("noserie");
                registro[2] = rs.getString("descripcion");
                registro[3] = rs.getString("precioCosto");
                registro[4] = rs.getString("precioVenta");
                registro[5] = rs.getString("precioMayoreo");
                registro[6] = rs.getString("existencia");
                registro[7] = rs.getString("cantidadMinima");
                registro[8] = rs.getString("nombre");
                totalresgistros += 1;
                modelo.addRow(registro);
            }

            return modelo;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            return null;

        }

    }

    public DefaultTableModel mostrarBusqueda(String buscar) {

        DefaultTableModel modelo;
        String[] titulos = {"ID", "Nº Serie", "DESCRIPCION", "PRECIO COSTO", "PRECIO VENTA", "PRECIO MAYOREO", "CATEGORIA", "EXISTENCIA", "CATIDAD MINIMA"};
        String[] registro = new String[9];
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

        if (buscar.equals("%")) {
            sSQL = "SELECT p.idproducto,p.noserie,p.descripcion,p.precioCosto,p.precioVenta,p.precioMayoreo,c.nombre,p.existencia,p.cantidadMinima "
                    + "FROM producto p "
                    + "INNER JOIN categoria c "
                    + "ON p.idcategoria = c.idcategoria ";
        } else {

            sSQL = "SELECT p.idproducto,p.noserie,p.descripcion,p.precioCosto,p.precioVenta,p.precioMayoreo,c.nombre,p.existencia,p.cantidadMinima "
                    + "FROM producto p "
                    + "INNER JOIN categoria c "
                    + "ON p.idcategoria = c.idcategoria "
                    + "WHERE p.descripcion regexp '^"+buscar+"$' OR p.descripcion regexp '^"+buscar+".' ORDER BY p.idproducto ASC";
        }

        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            while (rs.next()) {
                registro[0] = rs.getString("idproducto");
                registro[1] = rs.getString("noserie");
                registro[2] = rs.getString("descripcion");
                registro[3] = rs.getString("precioCosto");
                registro[4] = rs.getString("precioVenta");
                registro[5] = rs.getString("precioMayoreo");
                registro[6] = rs.getString("nombre");
                registro[7] = rs.getString("existencia");
                registro[8] = rs.getString("cantidadMinima");
                totalresgistros += 1;
                modelo.addRow(registro);
            }

            return modelo;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            return null;

        }

    }

    public DefaultTableModel bajosInventario() {
        DefaultTableModel modelo;
        String[] titulos = {"NOSERIE", "DESCRIPCION", "PRECIO VENTA", "INVENTARIO", "INV. MINIMO", "CATEGORIA"};
        String[] registro = new String[6];
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

        sSQL = "SELECT \n"
                + "p.noserie NOSERIE,\n"
                + "p.descripcion DESCRIPCION,\n"
                + "p.precioVenta PRECIOVENTA,\n"
                + "p.existencia INVENTARIO,\n"
                + "p.cantidadMinima INVMINIMO,\n"
                + "c.nombre CATEGORIA\n"
                + "FROM producto p\n"
                + "INNER JOIN categoria c\n"
                + "ON p.idcategoria = c.idcategoria\n"
                + "WHERE p.inactivo <> 1 AND p.inactivo <> 2 AND (p.existencia <= p.cantidadMinima)";
                //inactivo = 1 dado de baja
                //inactivo = 2 liquidado
                //inactivo = 0 disponible
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            while (rs.next()) {
                registro[0] = rs.getString("NOSERIE");
                registro[1] = rs.getString("DESCRIPCION");
                registro[2] = rs.getString("PRECIOVENTA");
                registro[3] = rs.getString("INVENTARIO");
                registro[4] = rs.getString("INVMINIMO");
                registro[5] = rs.getString("CATEGORIA");
                modelo.addRow(registro);
            }

            return modelo;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            return null;

        }

    }

    public boolean insertar(ModelProduct dts) {
        try {
            sSQL = "INSERT INTO producto (noserie,descripcion,precioCosto,precioVenta,precioMayoreo,existencia,cantidadMinima,idcategoria)"
                    + " VALUES(?,?,?,?,?,?,?,?)";
            //sSQL2 = "insert into categoria  (nombre) values (?)";

            PreparedStatement pst = cn.prepareStatement(sSQL);
            //PreparedStatement pst2 = cn.prepareStatement(sSQL2);

            pst.setString(1, dts.getNoserie());
            pst.setString(2, dts.getDescripcion());
            pst.setDouble(3, dts.getPrecioCosto());
            pst.setDouble(4, dts.getPrecioVenta());
            pst.setDouble(5, dts.getPrecioMayoreo());
            pst.setInt(6, dts.getExistencia());
            pst.setInt(7, dts.getCantidadMinima());
            pst.setInt(8, dts.getIdcategoria());

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

    public boolean editar(ModelProduct dts) {
        sSQL = "update producto set noserie=?,descripcion=?,precioCosto=?,precioVenta=?,precioMayoreo=?,existencia=?,cantidadMinima=?,idcategoria=?"
                + " where idproducto=?";

        try {
            PreparedStatement pst = cn.prepareStatement(sSQL);
            pst.setString(1, dts.getNoserie());
            pst.setString(2, dts.getDescripcion());
            pst.setDouble(3, dts.getPrecioCosto());
            pst.setDouble(4, dts.getPrecioVenta());
            pst.setDouble(5, dts.getPrecioMayoreo());
            pst.setInt(6, dts.getExistencia());
            pst.setInt(7, dts.getCantidadMinima());
            pst.setInt(8, dts.getIdcategoria());
            pst.setInt(9, dts.getIdproducto());
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

    public boolean eliminar(ModelProduct dts) {
        boolean respuesta = false;
         sSQL = "UPDATE producto SET inactivo = 1 WHERE idproducto = ?";
        
        try {
            PreparedStatement pst = cn.prepareStatement(sSQL);
            pst.setInt(1, dts.getIdproducto());
            
            int n = pst.executeUpdate();
            if (n != 0) {
                respuesta = true;
            } else {
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            return false;
        }
        return respuesta;
    }
    
    public boolean verificarSerieProducto(String serieProducto) {
        sSQL = "select noserie from producto where noserie = '"+serieProducto+"'";
        boolean respuesta = false;

        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            while (rs.next()) {
                if (!rs.getString("noserie").equals("")) {
                    respuesta = true;
                }

            }
        }catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            respuesta = false;

        }
        return respuesta;

    }

}
