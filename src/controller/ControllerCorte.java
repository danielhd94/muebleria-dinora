package controller;

import extras.Conexion;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

public class ControllerCorte {

    private Conexion mysql = new Conexion();
    private Connection cn = mysql.getConnection();
    private String sSQL = "", sSQL2 = "";

    public Double[] corte(String fecha, String nombreEm, int indexCombo) {

        //GENERAR CONSULTA Y MOSTRARLA EN LA TABLA
        if(indexCombo == 0){
            sSQL = "SELECT Date_format(fecha,'%Y-%M-%d %h:%i:%s %p') FECHA, \n"
                + "(   \n"
                + "    SELECT SUM(monto)\n"
                + "    FROM entradasalida\n"
                + "    WHERE tipo = 1 AND Date_format(fecha,'%Y-%m-%d') = '"+fecha+"' \n"
                + "    GROUP BY Date_format(fecha,'%Y-%m-%d')\n"
                + ") ENTRADAS,\n"
                + "(\n"
                + "    SELECT SUM(monto)\n"
                + "FROM entradasalida\n"
                + "WHERE tipo = 0 AND Date_format(fecha,'%Y-%m-%d') = '"+fecha+"'\n"
                + "    GROUP BY Date_format(fecha,'%Y-%m-%d')\n"
                + ") SALIDAS,\n"
                + "(\n"
                + "    SELECT \n"
                + "    ROUND((SUM((dt.cantidad) * dt.precioVenta) + (SUM((dt.cantidad) * dt.precioVenta) * (t.iva /100))),2)\n"
                + "	FROM detalleticket dt \n"
                + "	INNER JOIN ticket t \n"
                + "	ON dt.idticket = t.idticket\n"
                + "	WHERE t.idcliente = 1 AND Date_format(fecha,'%Y-%m-%d') = '"+fecha+"'\n"
                + "    GROUP BY Date_format(fecha,'%Y-%m-%d')\n"
                + "                \n"
                + ") VENTASEFECTIVO,\n"
                + "(\n"
                + "    SELECT SUM(monto)\n"
                + "	FROM pago\n"
                + "	WHERE Date_format(fecha,'%Y-%m-%d') = '"+fecha+"'\n"
                + "    GROUP BY Date_format(fecha,'%Y-%m-%d')\n"
                + "               \n"
                + ") PAGOSCLIENTES,\n"
                + "(\n"
                + "\n"
                + "   (\n"
                + "    (\n"
                + "    SELECT \n"
                + "    ROUND((SUM((dt.cantidad) * dt.precioVenta) + (SUM((dt.cantidad) * dt.precioVenta) * (t.iva /100))),2)\n"
                + "	FROM detalleticket dt \n"
                + "	INNER JOIN ticket t \n"
                + "	ON dt.idticket = t.idticket\n"
                + "	WHERE t.idcliente = 1 AND Date_format(fecha,'%Y-%m-%d') = '"+fecha+"'\n"
                + "        GROUP BY Date_format(fecha,'%Y-%m-%d')\n"
                + "                \n"
                + "	) - (\n"
                + "    		SELECT \n"
                + "    		ROUND((SUM(dt.cantidad * p.precioCosto)),2)\n"
                + "			FROM producto p\n"
                + "			INNER JOIN detalleticket dt\n"
                + "    		ON p.idproducto = dt.idproducto\n"
                + "			INNER JOIN ticket t \n"
                + "			ON dt.idticket = t.idticket\n"
                + "			WHERE t.idcliente = 1 AND Date_format(fecha,'%Y-%m-%d') = '"+fecha+"'\n"
                + "    		GROUP BY Date_format(t.fecha,'%Y-%m-%d')   \n"
                + "        \n"
                + "        )  \n"
                + "     ) - (\n"
                + "         SELECT \n"
                + "    ROUND((SUM(dt.cantidad * dt.precioVenta) * (t.iva /100)),2)\n"
                + "	FROM detalleticket dt \n"
                + "	INNER JOIN ticket t \n"
                + "	ON dt.idticket = t.idticket\n"
                + "	WHERE t.idcliente = 1 AND Date_format(fecha,'%Y-%m-%d') = '"+fecha+"'\n"
                + "    GROUP BY Date_format(fecha,'%Y-%m-%d')\n"
                + "     \n"
                + "     	 )\n"
                + "     \n"
                + "                \n"
                + ") GANANCIAS\n"
                + "FROM ticket t\n"
                + "INNER JOIN empleado e \n"
                + "ON t.idempleado = e.idempleado\n"
                + "WHERE Date_format(fecha,'%Y-%m-%d') = '"+fecha+"'\n"
                + "GROUP BY Date_format(fecha,'%Y-%m-%d')";
        }else{
        sSQL = "SELECT Date_format(fecha,'%Y-%M-%d %h:%i:%s %p') FECHA, \n"
                + "(   \n"
                + "    SELECT SUM(monto)\n"
                + "    FROM entradasalida\n"
                + "    WHERE tipo = 1 AND Date_format(fecha,'%Y-%m-%d') = '"+fecha+"' \n"
                + "    GROUP BY Date_format(fecha,'%Y-%m-%d')\n"
                + ") ENTRADAS,\n"
                + "(\n"
                + "    SELECT SUM(monto)\n"
                + "FROM entradasalida\n"
                + "WHERE tipo = 0 AND Date_format(fecha,'%Y-%m-%d') = '"+fecha+"'\n"
                + "    GROUP BY Date_format(fecha,'%Y-%m-%d')\n"
                + ") SALIDAS,\n"
                + "(\n"
                + "    SELECT \n"
                + "    ROUND((SUM((dt.cantidad) * dt.precioVenta) + (SUM((dt.cantidad) * dt.precioVenta) * (t.iva /100))),2)\n"
                + "	FROM detalleticket dt \n"
                + "	INNER JOIN ticket t \n"
                + "	ON dt.idticket = t.idticket\n"
                + "	WHERE t.idcliente = 1 AND Date_format(fecha,'%Y-%m-%d') = '"+fecha+"'\n"
                + "    GROUP BY Date_format(fecha,'%Y-%m-%d')\n"
                + "                \n"
                + ") VENTASEFECTIVO,\n"
                + "(\n"
                + "    SELECT SUM(monto)\n"
                + "	FROM pago\n"
                + "	WHERE Date_format(fecha,'%Y-%m-%d') = '"+fecha+"'\n"
                + "    GROUP BY Date_format(fecha,'%Y-%m-%d')\n"
                + "               \n"
                + ") PAGOSCLIENTES,\n"
                + "(\n"
                + "\n"
                + "   (\n"
                + "    (\n"
                + "    SELECT \n"
                + "    ROUND((SUM((dt.cantidad) * dt.precioVenta) + (SUM((dt.cantidad) * dt.precioVenta) * (t.iva /100))),2)\n"
                + "	FROM detalleticket dt \n"
                + "	INNER JOIN ticket t \n"
                + "	ON dt.idticket = t.idticket\n"
                + "	WHERE t.idcliente = 1 AND Date_format(fecha,'%Y-%m-%d') = '"+fecha+"'\n"
                + "        GROUP BY Date_format(fecha,'%Y-%m-%d')\n"
                + "                \n"
                + "	) - (\n"
                + "    		SELECT \n"
                + "    		ROUND((SUM(dt.cantidad * p.precioCosto)),2)\n"
                + "			FROM producto p\n"
                + "			INNER JOIN detalleticket dt\n"
                + "    		ON p.idproducto = dt.idproducto\n"
                + "			INNER JOIN ticket t \n"
                + "			ON dt.idticket = t.idticket\n"
                + "			WHERE t.idcliente = 1 AND Date_format(fecha,'%Y-%m-%d') = '"+fecha+"'\n"
                + "    		GROUP BY Date_format(t.fecha,'%Y-%m-%d')   \n"
                + "        \n"
                + "        )  \n"
                + "     ) - (\n"
                + "         SELECT \n"
                + "    ROUND((SUM(dt.cantidad * dt.precioVenta) * (t.iva /100)),2)\n"
                + "	FROM detalleticket dt \n"
                + "	INNER JOIN ticket t \n"
                + "	ON dt.idticket = t.idticket\n"
                + "	WHERE t.idcliente = 1 AND Date_format(fecha,'%Y-%m-%d') = '"+fecha+"'\n"
                + "    GROUP BY Date_format(fecha,'%Y-%m-%d')\n"
                + "     \n"
                + "     	 )\n"
                + "     \n"
                + "                \n"
                + ") GANANCIAS\n"
                + "FROM ticket t\n"
                + "INNER JOIN empleado e \n"
                + "ON t.idempleado = e.idempleado\n"
                + "WHERE Date_format(fecha,'%Y-%m-%d') = '"+fecha+"' AND e.nombre = '"+nombreEm+"'\n"
                + "GROUP BY Date_format(fecha,'%Y-%m-%d')";
        }

        Double[] registro = new Double[7];
        double totalES = 0d;
        double ventasTotales = 0d;
        double ganancias = 0d;

        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            while (rs.next()) {
                registro[0] = rs.getDouble("ENTRADAS");
                registro[1] = rs.getDouble("SALIDAS");
                registro[2] = rs.getDouble("VENTASEFECTIVO");
                registro[3] = rs.getDouble("PAGOSCLIENTES");

                /*for (int i = 0; i < 4; i++) {
                    if (registro[i] == null) {
                        registro[i] = 0d;
                    }
                }
                    */

                //totalES = (Double.parseDouble(registro[0]) + Double.parseDouble(registro[2])) - Double.parseDouble(registro[1]);
                //ventasTotales = Double.parseDouble(registro[2]) + Double.parseDouble(registro[3]);
                totalES = ((registro[0] + registro[2]) - registro[1]);
                ventasTotales = registro[2] + registro[3];

                registro[4] = totalES;
                registro[5] = ventasTotales;
                registro[6] = rs.getDouble("GANANCIAS");
                /*if (registro[6] == null) {
                    registro[6] = 0d;
                }
                */

            }
        } catch (SQLException er) {
            JOptionPane.showMessageDialog(null, er);
        }
        return registro;
    }
}
