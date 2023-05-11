/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import extras.Conexion;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author Developer
 */
public class fMeses {

    public static Conexion mysql = new Conexion();
    public static Connection cn = mysql.getConnection();
    public static String sSQL = "";
    public Integer totalresgistros;

    public static ArrayList<String> cargarMeses(String buscar) {
        ArrayList<String> registro = new ArrayList<String>();

        sSQL = "SELECT t.idempleado,\n"
                + "t.idcliente, \n"
                + "t.idticket FOLIO,\n"
                + "SUM(dt.cantidad) ARTS,\n"
                + "DATE_FORMAT(t.fecha, '%d-%W-%Y') FECHA, \n"
                + "DATE_FORMAT(t.fecha, '%M' ) MES,\n"
                + "(SUM((dt.cantidad) * dt.precioVenta))  TOTAL \n"
                + "FROM detalleticket dt \n"
                + "INNER JOIN ticket t \n"
                + "ON t.idticket = dt.idticket \n"
                + "WHERE t.idcliente = " + buscar + " AND t.estado <> 1 AND t.estado <> 2 \n"
                + "GROUP BY t.idticket \n"
                + "ORDER BY DATE_FORMAT(t.fecha, '%d-%W-%Y' ) ASC";
        String mes = "";

        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            int i = 0;
            while (rs.next()) {
                mes = rs.getString("MES");
                if (!registro.contains(mes)) {
                    registro.add(i, mes);
                    i++;
                }

            }
            i = 0;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            return null;

        }
        return registro;
    }

    public static ArrayList<String> todosMeses(String buscar) {
        ArrayList<String> registro = new ArrayList<String>();

        sSQL = "SELECT t.idempleado,\n"
                + "t.idcliente, \n"
                + "t.idticket FOLIO,\n"
                + "SUM(dt.cantidad) ARTS,\n"
                + "DATE_FORMAT(t.fecha, '%d-%W-%Y') FECHA, \n"
                + "DATE_FORMAT(t.fecha, '%M' ) MES,\n"
                + "(SUM((dt.cantidad) * dt.precioVenta))  TOTAL \n"
                + "FROM detalleticket dt\n"
                + "INNER JOIN ticket t \n"
                + "ON t.idticket = dt.idticket \n"
                + "WHERE t.idcliente = " + buscar + " AND t.estado <> 1 AND t.estado <> 2 \n"
                + "GROUP BY t.idticket \n"
                + "ORDER BY DATE_FORMAT(t.fecha, '%d-%W-%Y' ) ASC";
        String mes = "";

        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            int i = 0;
            while (rs.next()) {
                mes = rs.getString("MES");
                registro.add(i, mes);

            }
            i = 0;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            return null;

        }
        return registro;
    }

    public static ArrayList<String> obtenerfechas(String buscar) {
        ArrayList<String> registro = new ArrayList<String>();

        sSQL = "SELECT t.idempleado,\n"
                + "t.idcliente, \n"
                + "t.idticket FOLIO,\n"
                + "SUM(dt.cantidad) ARTS,\n"
                + "t.fecha FECHA, \n" //%d-%W-%Y DATE_FORMAT(t.fecha, '%h:%i:%s') HORA
                + "DATE_FORMAT(t.fecha, '%M' ) MES,\n"
                + "(SUM((dt.cantidad) * dt.precioVenta))  TOTAL \n"
                + "FROM detalleticket dt\n"
                + "INNER JOIN ticket t \n"
                + "ON t.idticket = dt.idticket \n"
                + "WHERE t.idcliente = " + buscar + " AND t.estado <> 1 AND t.estado <> 2 \n"
                + "GROUP BY t.idticket \n"
                + "ORDER BY DATE_FORMAT(t.fecha, '%d-%W-%Y') ASC"; //ORDER BY DATE_FORMAT(t.fecha, '%d-%W-%Y') ASC";
        String fecha = "", idcliente = "";

        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            int i = 0;
            while (rs.next()) {
                //idcliente = rs.getString("idcliente");
                fecha = rs.getString("FECHA");
                //fecha = fecha +"/"+ idcliente;
                registro.add(i, fecha);
                    i++;

            }
            i = 0;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            return null;

        }
        return registro;
    }
}
