package controller;

import extras.Conexion;
import extras.MyFormater;
import model.ModelClient;
import model.ModelPaymet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ControllerClient {

    public static Conexion mysql = new Conexion();
    public static Connection cn = mysql.getConnection();
    public static String sSQL = "";
    public static String sSQL2 = "";

    public static String StringTotal = "";
    public static int totalresgistros;
    public Double totalCredito = 0d;
    static Double limiteCredito = 0d;
    static Double adquirido = 0d;
    static Double abonado = 0d;
    static Double saldo_actual = 0d;
    public Double SALDO_ACTUAL_USUARIO = 0d;

    public DefaultTableModel mostrar(String buscar) {
        System.out.println("buscar: " + buscar);

        DefaultTableModel modelo;
        String[] titulos = {"ID", "NOMBRE", "DIRECCION", "TELEFONO", "LIMITE CREDITO", "SALDO ACTUAL", "ULTIMO PAGO"};
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
        totalresgistros = 0;
        totalCredito = 0d;
        limiteCredito = 0d;
        adquirido = 0d;
        abonado = 0d;
        saldo_actual = 0d;

        sSQL = "SELECT \n"
                + "c.idcliente AS IDCLIENTE,\n"
                + "c.nombre AS NOMBRE,\n"
                + "c.direccion AS DIRECCION,\n"
                + "c.telefono AS TELEFONO,\n"
                + "ROUND(IFNULL(\n"
                + "    		c.limiteCredito\n"
                + "      ,0)\n"
                + ",2) AS LIMITECREDITO,\n"
                + "\n"
                + "ROUND(IFNULL(\n"
                + "    		(SELECT \n"
                + "			ROUND((SUM((dt.cantidad) * dt.precioVenta) + (SUM((dt.cantidad) * dt.precioVenta) * (t.iva /100))),2)\n"
                + "			FROM detalleticket dt\n"
                + "			INNER JOIN ticket t\n"
                + "			ON dt.idticket = t.idticket\n"
                + "			WHERE t.idcliente = c.idcliente AND t.estado = 0 \n"
                + "			GROUP BY t.idcliente)\n"
                + "       ,0)\n"
                + ",2) AS GASTADO,\n"
                + "        \n"
                + "ROUND((IFNULL(\n"
                + "    		(SELECT SUM(p.monto)\n"
                + "			FROM cliente c2\n"
                + "			LEFT JOIN pago p\n"
                + "			ON c2.idcliente = p.idcliente\n"
                + "			WHERE c2.idcliente = c.idcliente\n"
                + "			GROUP BY c2.idcliente)\n"
                + "   	   ,0)\n"
                + "    )\n"
                + ",2) AS ABONADO,\n"
                + "\n"
                + "ROUND(IFNULL(\n"
                + "    		(c.limiteCredito - (SELECT \n"
                + "					ROUND((SUM((dt.cantidad) * dt.precioVenta) + (SUM((dt.cantidad) * dt.precioVenta) * (t.iva /100))),2)\n"
                + "					FROM detalleticket dt\n"
                + "					INNER JOIN ticket t\n"
                + "					ON dt.idticket = t.idticket\n"
                + "					WHERE t.idcliente= c.idcliente AND t.estado = 0\n"
                + "					GROUP BY t.idcliente) + ROUND((IFNULL(\n"
                + "                                    						(SELECT SUM(p.monto)\n"
                + "										FROM cliente c2\n"
                + "										LEFT JOIN pago p\n"
                + "										ON c2.idcliente = p.idcliente\n"
                + "										WHERE c2.idcliente = c.idcliente\n"
                + "										GROUP BY c2.idcliente)\n"
                + "                                                               ,0)\n"
                + "                                                              )\n"
                + "                                                         ,2)\n"
                + "            )\n"
                + "      ,c.limiteCredito)\n"
                + ",2) AS SALDO_ACTUAL,\n"
                + "\n"
                + "IFNULL(\n"
                + "      (select fecha from pago\n"
                + "        WHERE idcliente = c.idcliente AND monto <> 0\n"
                + "        ORDER BY idpago DESC LIMIT 1\n"
                + "      )\n"
                + ",'-') AS ULTIMO_PAGO\n"
                + "FROM cliente c\n"
                + "WHERE c.inactivo <> 1 AND (c.nombre regexp '^" + buscar + "$' OR c.nombre regexp '^" + buscar + ".') \n"
                + "GROUP BY c.idcliente\n"
                + "ORDER BY c.idcliente DESC";

        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            int c = 0;
            while (rs.next()) {
                if (!rs.getString("IDCLIENTE").equals("1")) {
                    registro[0] = rs.getString("IDCLIENTE");
                    registro[1] = rs.getString("NOMBRE");
                    registro[2] = rs.getString("DIRECCION");
                    registro[3] = rs.getString("TELEFONO");
                    registro[4] = MyFormater.formato(rs.getDouble("LIMITECREDITO")); //rs.getString("limiteCredito")
                    registro[5] = MyFormater.formato(rs.getDouble("SALDO_ACTUAL"));
                    registro[6] = rs.getString("ULTIMO_PAGO");
                    modelo.addRow(registro);

                    totalCredito += rs.getDouble("SALDO_ACTUAL");
                    totalresgistros += 1;

                    /*limiteCredito = rs.getDouble("limiteCredito");
                    adquirido = adquiridoCliente(rs.getString("idcliente"));
                    abonado = abonadoCliente(rs.getString("idcliente"));
                    saldo_actual = (limiteCredito - adquirido) + abonado;
                    registro[5] = MyFormater.formato(saldo_actual);
                    
                    registro[6] = (ultimoPago(rs.getString("idcliente")).equals("") ? "-" : ultimoPago(rs.getString("idcliente")));
                     */
                }
            }
            StringTotal = MyFormater.formato(totalCredito);

            return modelo;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error fcliente mostrar " + e);
            return null;

        }

    }

    private String ultimoPago(String idcliente) {
        sSQL = "select fecha from pago \n"
                + " WHERE idcliente = " + idcliente + " AND monto <> 0\n"
                + " ORDER BY idpago DESC LIMIT 1";
        String FECHA_ULTIMO_PAGO_CLIENTE = "";
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            while (rs.next()) {
                if (!rs.getString("fecha").equals("")) {
                    FECHA_ULTIMO_PAGO_CLIENTE = rs.getString("fecha");
                }

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en el  método ultimoPago: " + e);
        }
        return FECHA_ULTIMO_PAGO_CLIENTE;
    }

    public static Double adquiridoCliente(String idcliente) {
        sSQL = "SELECT\n"
                + "ROUND((SUM((dt.cantidad) * dt.precioVenta) + (SUM((dt.cantidad) * dt.precioVenta) * (t.iva /100))),2) ADQUIRIDO\n"
                + "FROM detalleticket dt\n"
                + "LEFT JOIN ticket t\n"
                + "ON dt.idticket = t.idticket\n"
                + "WHERE t.idcliente = " + idcliente + " AND t.estado = 0 \n"
                + "GROUP BY t.idcliente";
        Double ADQUIRIDO_CLIENTE = 0d;
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            while (rs.next()) {
                ADQUIRIDO_CLIENTE = rs.getDouble("ADQUIRIDO");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en el  método adquiridoCliente: " + e);
        }
        return ADQUIRIDO_CLIENTE;
    }

    private Double abonadoCliente(String idcliente) {
        sSQL = "SELECT c.nombre,\n"
                + "SUM(p.monto) pago\n"
                + "FROM cliente c\n"
                + "LEFT JOIN pago p\n"
                + "ON c.idcliente = p.idcliente\n"
                + "WHERE c.idcliente = " + idcliente + "\n"
                + "GROUP BY c.idcliente";
        Double ABONADO_CLIENTE = 0d;
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            while (rs.next()) {
                ABONADO_CLIENTE = rs.getDouble("pago");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en el  método abonadoCliente: " + e);
        }
        return ABONADO_CLIENTE;
    }

    public Double saldoActualCliente(String idcliente) {
        sSQL = "SELECT limiteCredito\n"
                + "FROM cliente\n"
                + "WHERE idcliente = " + idcliente + "\n";
        Double LIMITE_CREDITO = 0d;
        SALDO_ACTUAL_USUARIO = 0d;
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            while (rs.next()) {
                LIMITE_CREDITO = rs.getDouble("limiteCredito");
            }

            SALDO_ACTUAL_USUARIO = (LIMITE_CREDITO - adquiridoCliente(idcliente) + abonadoCliente(idcliente));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en el  método abonadoCliente: " + e);
        }
        return SALDO_ACTUAL_USUARIO;
    }

    public Double limiteCreditoCliente(String idcliente) {
        sSQL = "SELECT limiteCredito\n"
                + "FROM cliente\n"
                + "WHERE idcliente = " + idcliente + "\n";
        Double LIMITE_CREDITO = 0d;
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            while (rs.next()) {
                LIMITE_CREDITO = rs.getDouble("limiteCredito");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en el  método limiteCreditoCliente: " + e);
        }
        return LIMITE_CREDITO;
    }

    public boolean insertar(ModelClient dts, ModelPaymet dtspa) {
        sSQL = "insert into cliente (nombre,direccion,telefono,limiteCredito) "
                + "values(?,?,?,?)";

        //String s = "(select idcliente from cliente order by idcliente desc limit 1)";
        //sSQL2 = "insert into pago (idcliente,idempleado,monto) "
        //   + "values(" + s + ",?,?)";
        try {
            PreparedStatement pst = cn.prepareStatement(sSQL);
            //PreparedStatement pst2 = cn.prepareStatement(sSQL2);
            pst.setString(1, dts.getNombre());
            pst.setString(2, dts.getDireccion());
            pst.setString(3, dts.getTelefono());
            pst.setDouble(4, dts.getLimiteCredito());

            //pst2.setInt(1, dtspa.getIdempleado());
            //pst2.setDouble(2, dtspa.getMonto());
            int n = pst.executeUpdate();
            if (n != 0) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error fcliente insertar" + e);
            return false;
        }
    }

    public boolean editar(ModelClient dts) {
        sSQL = "update cliente set nombre=?,direccion=?,telefono=?,limiteCredito=?"
                + "where idcliente=?";

        try {
            PreparedStatement pst = cn.prepareStatement(sSQL);
            pst.setString(1, dts.getNombre());
            pst.setString(2, dts.getDireccion());
            pst.setString(3, dts.getTelefono());
            pst.setDouble(4, dts.getLimiteCredito());
            pst.setInt(5, dts.getIdcliente());

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

    public boolean eliminar(ModelClient dts) {
        boolean respuesta = false;
        sSQL = "UPDATE cliente SET inactivo = 1 WHERE idcliente = ?";

        try {
            PreparedStatement pst = cn.prepareStatement(sSQL);
            pst.setInt(1, dts.getIdcliente());

            int n = pst.executeUpdate();
            if (n != 0) {
                respuesta = true;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            respuesta = false;
        }
        return respuesta;
    }
}
