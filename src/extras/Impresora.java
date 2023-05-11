package extras;

import extras.MyFormater;
import view.FrmMain;
import java.awt.print.PrinterException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PrinterName;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;

public class Impresora {

    private static final Conexion mysql = new Conexion();
    private static final Connection cn = mysql.getConnection();
    private static String sSQL = "";

    public static void imprimirReporteInventario(String strCategoria) {
        Map p = new HashMap();
        JasperReport report;
        JasperPrint print;
        String archivo = new File("").getAbsolutePath() + "/src/Reportes/reporteInventario.jasper";
        p.put("str_categoria", strCategoria);
        try {
            //report = JasperCompileManager.compileReport(new File("").getAbsolutePath() + "/src/Reportes/reporteInventario.jrxml");
            report = (JasperReport) JRLoader.loadObjectFromFile(archivo);
            print = JasperFillManager.fillReport(report, p, cn);
            //JasperViewer vista = new JasperViewer(print, false);
            //vista.setTitle("Listado de Inventario");
            PrintReportToPrinter(print);//vista.setVisible(true);
        } catch (JRException e) {
            JOptionPane.showMessageDialog(null, "Error imprimirReporteInventario: " + e.getMessage());
        }
    }

    public static void imprimirListadoClientes(String nombreCliente) {
        Map p = new HashMap();
        JasperReport report;
        JasperPrint print;
        String archivo = new File("").getAbsolutePath() + "/src/Reportes/reporteCliente.jasper";
        try {

            report = (JasperReport) JRLoader.loadObjectFromFile(archivo);
            //p.put("nombreCliente", nombreCliente);
            print = JasperFillManager.fillReport(report, p, cn);
            //JasperViewer vista = new JasperViewer(print, false);
            //vista.setTitle("Listado de clientes");
            PrintReportToPrinter(print);//vista.setVisible(true);
        } catch (JRException e) {
            JOptionPane.showMessageDialog(null, "Error imprimirListadoClientes: " + e.getMessage());
        }
    }

    public static void imprimirArticulosVendidos(String cajero, String fecha_desde, String fecha_hasta) {
        Map p = new HashMap();
        JasperReport report;
        JasperPrint print;
        String archivo = new File("").getAbsolutePath() + "/src/Reportes/reporteVentas.jasper";
        p.put("f_desde", fecha_desde);
        p.put("f_hasta", fecha_hasta);
        p.put("cajero", cajero);
        try {
            report = (JasperReport) JRLoader.loadObjectFromFile(archivo);
            print = JasperFillManager.fillReport(report, p, cn);
            //JasperViewer vista = new JasperViewer(print, false);
            //vista.setTitle("Reporte de ventas");
            PrintReportToPrinter(print);//vista.setVisible(true);
        } catch (JRException e) {
            JOptionPane.showMessageDialog(null, "Error imprimirListadoClientes: " + e.getMessage());
        }
    }

    public static void imprimirTicket(String cajero, String encabezado, String mensaje, String lblFolio, String idticket, String totalTicket, int impuesto, Double pagoCon, String idcliente, String nombreCliente) throws PrinterException {
        Map p = new HashMap();
        JasperReport report;
        JasperPrint print;
        String archivo1 = new File("").getAbsolutePath() + "/src/Reportes/reporteTicket.jasper";
        String archivo2 = new File("").getAbsolutePath() + "/src/Reportes/reporteTicket_efectivo.jasper";

        //Double impuesto1 = Double.parseDouble("" + impuesto);
        int impuesto1 = ivaTicket(Integer.parseInt(idticket));
        Double total = Double.parseDouble(totalTicket);
        Double iva = (total * impuesto1) / (100 + impuesto1); //DESGLOSANDO IVA
        Double subTotal = total - iva;   //DESGLOSANDO SUBTOTAL
        Double suCambio = pagoCon - total;
        String str_iva = MyFormater.formato(iva);

        str_iva = str_iva.replace("$", "");
        int IDCLIENTE = Integer.parseInt(idcliente);
        if (IDCLIENTE == 1) {
            p.put("cajero", cajero);
            p.put("encabezado_ticket", encabezado);
            p.put("mensaje_ticket", mensaje);
            p.put("lblfolio", lblFolio);
            p.put("id_ticket", Integer.parseInt(idticket));
            p.put("subtotal_ticket", subTotal);
            p.put("iva_ticket", iva);
            p.put("total_tcket", total);
            p.put("pago_con", pagoCon);
            p.put("su_cambio", suCambio);
            
            try {
                report = (JasperReport) JRLoader.loadObjectFromFile(archivo1);
                print = JasperFillManager.fillReport(report, p, cn);
                PrintReportToPrinter(print);
            } catch (JRException e) {
                JOptionPane.showMessageDialog(null, "Error imprimirTicket: " + e.getMessage());
            }
        } else {
            p.put("cajero", cajero);
            p.put("encabezado_ticket", encabezado);
            p.put("mensaje_ticket", mensaje);
            p.put("lblfolio", lblFolio);
            p.put("id_ticket", Integer.parseInt(idticket));
            p.put("subtotal_ticket", subTotal);
            p.put("iva_ticket", iva);
            p.put("total_tcket", total);
            p.put("pago_con", pagoCon);
            p.put("su_cambio", suCambio);
            p.put("nombreCliente",nombreCliente);
            try {
                report = (JasperReport) JRLoader.loadObjectFromFile(archivo2);
                print = JasperFillManager.fillReport(report, p, cn);
                PrintReportToPrinter(print);
            } catch (JRException e) {
                JOptionPane.showMessageDialog(null, "Error imprimirTicket2: " + e.getMessage());
            }
        }
    }

    public static void imprimirCopiaTicketCredito(String cajero, String encabezado, String mensaje, String lblFolio, String idticket, String totalTicket, int impuesto, String nombreCliente) throws PrinterException {
        Map p = new HashMap();
        JasperReport report;
        JasperPrint print;
        String archivo = new File("").getAbsolutePath() + "/src/Reportes/reporteCopiaTicketCredito.jasper";

        int impuesto1 = ivaTicket(Integer.parseInt(idticket));
        Double subTotal = Double.parseDouble(totalTicket);
        Double iva = (subTotal * impuesto1 / 100);
        Double total = subTotal + iva;

        p.put("cajero", cajero);
        p.put("encabezado_ticket", encabezado);
        p.put("mensaje_ticket", mensaje);
        p.put("lblfolio", lblFolio);
        p.put("id_ticket", Integer.parseInt(idticket));
        p.put("subtotal_ticket", subTotal);
        p.put("iva_ticket", iva);
        p.put("total_tcket", total);
        p.put("nombre_cliente", nombreCliente);
        try {
            report = (JasperReport) JRLoader.loadObjectFromFile(archivo);
            print = JasperFillManager.fillReport(report, p, cn);
            PrintReportToPrinter(print);
        } catch (JRException e) {
            JOptionPane.showMessageDialog(null, "Error imprimirCopiaTicketCredito: " + e.getMessage());
        }
    }

    public static void imprimirUltimoTicket(String cajero, String encabezado, String mensaje, String lblFolio) {
        Map p = new HashMap();
        JasperReport report;
        JasperPrint print;
        int tipoPago = getIdUltimoCliente();
        if (tipoPago == 1) {
            String archivo = new File("").getAbsolutePath() + "/src/Reportes/reporteUltimoTicket.jasper";

            int impuesto2 = ivaTicket(idUltimoTicket());
            Double total = totalUlltimoTicket();
            Double iva = (total * impuesto2) / (100 + impuesto2); //DESGLOSANDO IVA
            Double subTotal = total - iva;   //DESGLOSANDO SUBTOTAL
            Double suCambio = pagoConUltimoTicket() - total;

            p.put("cajero", cajero);
            p.put("encabezado_ticket", encabezado);
            p.put("mensaje_ticket", mensaje);
            p.put("lblfolio", lblFolio);

            p.put("totalArts", getTotalArtsUltimoTicket());

            p.put("subtotal_ticket", myRound(MyFormater.formato(subTotal)));
            p.put("iva_ticket", myRound(MyFormater.formato(iva)));
            p.put("total_tcket", total);

            p.put("pago_con", pagoConUltimoTicket());
            p.put("su_cambio", suCambio);
            try {
                report = (JasperReport) JRLoader.loadObjectFromFile(archivo);
                print = JasperFillManager.fillReport(report, p, cn);
                PrintReportToPrinter(print);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error imprimirUltimoTicket efectivo " + e.getMessage());
            }
        } else {
            String archivo = new File("").getAbsolutePath() + "/src/Reportes/reporteUltimoTicketCredito.jasper";

            int impuesto2 = ivaTicket(idUltimoTicket());
            Double total = totalUlltimoTicket();
            Double iva = (total * impuesto2) / (100 + impuesto2); //DESGLOSANDO IVA
            Double subTotal = total - iva;   //DESGLOSANDO SUBTOTAL
            Double suCambio = pagoConUltimoTicket() - total;

            p.put("cajero", cajero);
            p.put("encabezado_ticket", encabezado);
            p.put("mensaje_ticket", mensaje);
            p.put("lblfolio", lblFolio);

            p.put("totalArts", getTotalArtsUltimoTicket());

            p.put("subtotal_ticket", myRound(MyFormater.formato(subTotal)));
            p.put("iva_ticket", myRound(MyFormater.formato(iva)));
            p.put("total_tcket", total);

            p.put("pago_con", pagoConUltimoTicket());
            p.put("su_cambio", suCambio);
            try {
                report = (JasperReport) JRLoader.loadObjectFromFile(archivo);
                print = JasperFillManager.fillReport(report, p, cn);
                PrintReportToPrinter(print);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error imprimirUltimoTicket credito " + e.getMessage());
            }
        }
    }

    public static void imprimirDatosMovimiento(int idinventario) throws PrinterException {
        Map p = new HashMap();
        JasperReport report;
        JasperPrint print;
        String archivo = new File("").getAbsolutePath() + "/src/Reportes/reporteMovimientoSeleccionado.jasper";
        try {
            String[] datos = datosMovimiento(idinventario);

            p.put("fecha_movimiento", datos[0]);
            p.put("tipo_movimiento", datos[3]);
            p.put("str_movimiento", strMovimiento(datos[3]));
            p.put("cant_movimiento", datos[4]);
            p.put("valor_habia", datos[2]);
            p.put("articulo_inv", datos[1]);
            System.out.println("fecha_movimiento: " + datos[0]);
            System.out.println("tipo_movimiento: " + datos[3]);
            System.out.println("str_movimiento: " + strMovimiento(datos[3]));
            System.out.println("cant_movimiento: " + datos[3]);
            System.out.println("valor_habia: " + datos[2]);
            System.out.println("articulo_inv: " + datos[1]);
            System.out.println("===================================================");

            report = (JasperReport) JRLoader.loadObjectFromFile(archivo);
            print = JasperFillManager.fillReport(report, p, cn);
            PrintReportToPrinter(print);
        } catch (JRException e) {
            JOptionPane.showMessageDialog(null, "Error imprimirDatosMovimiento: " + e.getMessage());
        }
    }

    public static void imprimirMovimientosTodos(String fecha, String parametro2, int indexCombo) throws PrinterException {
        Map p = new HashMap();
        JasperReport report;
        JasperPrint print;
        String archivo = new File("").getAbsolutePath() + "/src/Reportes/reporteMovimientoTodos.jasper";
        try {
            String todos = "";

            p.put("fecha_mov", fecha);
            p.put("param_mov", parametro2);
            p.put("combo_mov", indexCombo);

            report = (JasperReport) JRLoader.loadObjectFromFile(archivo);
            print = JasperFillManager.fillReport(report, p, cn);
            PrintReportToPrinter(print);
        } catch (JRException e) {
            JOptionPane.showMessageDialog(null, "Error imprimirMovimientosTodos: " + e.getMessage());
        }
    }

    public static String[] datosMovimiento(int idinventario) {
        sSQL = "SELECT \n"
                + "inv.idinvetario,\n"
                + "Date_format(inv.fecha,'%h:%i %p') HORA,\n"
                + "Date_format(inv.fecha,'%d-%M-%Y') FECHA,\n"
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
                + "WHERE inv.idinvetario = " + idinventario;
        String[] registro = new String[5];

        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            while (rs.next()) {
                registro[0] = rs.getString("FECHA") + "  " + rs.getString("HORA");//FECHA / HORA
                registro[1] = rs.getString("DESCRIPCION"); //ARTICULO
                registro[2] = rs.getString("HABIA"); //HABIA
                registro[3] = tipo1(rs.getString("TIPO"));//TIPO DE MOVIMIENTO
                registro[4] = rs.getString("CANTIDAD"); // CANTIDAD
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en datosMovimiento: " + e.getMessage());
            return null;
        }
        return registro;
    }

    public static String tipo1(String tipo) {
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

    public static String strMovimiento(String strTipo) {
        switch (strTipo) {
            case "ENTRADA":
                strTipo = "INGRESARON:";
                break;
            case "SALIDA":
                strTipo = "SE VENDIERON";
                break;
            case "AJUSTE":
                strTipo = "SE AJUSTO A:";
                break;
            case "DEVOLUCION":
                strTipo = "SE DEVOLVIERON";
                break;
            default:
                break;

        }

        return strTipo;
    }

    public static void imprimirEstadoCuentaCliente(String encabezado, String nombreCliente, String idcliente) throws PrinterException {
        Map p = new HashMap();
        JasperReport report;
        JasperPrint print;
        String archivo = new File("").getAbsolutePath() + "/src/Reportes/reporteEstadoCuentaCliente.jasper";

        //Double iva = (subTotal * impuesto1 / 100);
        String SALDO_PENDIENTE = "$" + getSaldoPendienteCliente(idcliente);
        p.put("encabezado", encabezado);
        p.put("nombre_cliente", nombreCliente);
        p.put("saldo_pendiente", SALDO_PENDIENTE);
        p.put("idcliente", idcliente);
        p.put("saldo_anterior", "0");

        try {
            report = (JasperReport) JRLoader.loadObjectFromFile(archivo);
            print = JasperFillManager.fillReport(report, p, cn);
            PrintReportToPrinter(print);
        } catch (JRException e) {
            JOptionPane.showMessageDialog(null, "Error imprimirEstadoCuentaCliente: " + e.getMessage());
        }
    }

    public static void imprimirLiquidacionCuentaCliente(String encabezado, String nombreCliente, String idcliente) throws PrinterException {
        Map p = new HashMap();
        JasperReport report;
        JasperPrint print;
        String archivo = new File("").getAbsolutePath() + "/src/Reportes/reporteLiquidacionCuentaCliente.jasper";

        //Double iva = (subTotal * impuesto1 / 100);
        String SALDO_PENDIENTE = "$" + getSaldoPendienteCliente(idcliente);
        p.put("encabezado", encabezado);
        p.put("nombre_cliente", nombreCliente);
        p.put("saldo_pendiente", "0");
        p.put("idcliente", idcliente);
        p.put("saldo_anterior", "0");

        try {
            report = (JasperReport) JRLoader.loadObjectFromFile(archivo);
            print = JasperFillManager.fillReport(report, p, cn);
            PrintReportToPrinter(print);
        } catch (JRException e) {
            JOptionPane.showMessageDialog(null, "Error imprimirEstadoCuentaCliente: " + e.getMessage());
        }
    }

    public static void imprimirAbonoCuentaCliente(String encabezado, String nombreCliente, String idcliente) throws PrinterException {
        Map p = new HashMap();
        JasperReport report;
        JasperPrint print;
        String archivo = new File("").getAbsolutePath() + "/src/Reportes/reporteReciboAbono.jasper";

        p.put("encabezado", encabezado);
        p.put("nombre_cliente", nombreCliente);
        p.put("idcliente", idcliente);

        try {
            report = (JasperReport) JRLoader.loadObjectFromFile(archivo);
            print = JasperFillManager.fillReport(report, p, cn);
            PrintReportToPrinter(print);
        } catch (JRException e) {
            JOptionPane.showMessageDialog(null, "Error imprimirEstadoCuentaCliente: " + e.getMessage());
        }
    }

    public static void PrintReportToPrinter(JasperPrint jasperPrint) throws JRException {

//Get the printers names 
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);

//Lets set the printer name based on the registered printers driver name (you can see the printer names in the services variable at debugging) 
        //String selectedPrinter = "Microsoft XPS Document Writer";
// String selectedPrinter = "\\\\S-BPPRINT\\HP Color LaserJet 4700"; // examlpe to network shared printer 
        String selectedPrinter = view.FrmMain.nombreImpresora;
        System.out.println("Number of print services: " + services.length);
        PrintService selectedService = null;

//Set the printing settings 
        PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
        printRequestAttributeSet.add(MediaSizeName.NA_LETTER);
        printRequestAttributeSet.add(new Copies(1));
        if (jasperPrint.getOrientationValue() == net.sf.jasperreports.engine.type.OrientationEnum.LANDSCAPE) {
            printRequestAttributeSet.add(OrientationRequested.LANDSCAPE);
        } else {
            printRequestAttributeSet.add(OrientationRequested.PORTRAIT);
        }
        PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
        printServiceAttributeSet.add(new PrinterName(selectedPrinter, null));

        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
        SimplePrintServiceExporterConfiguration configuration = new SimplePrintServiceExporterConfiguration();
        configuration.setPrintRequestAttributeSet(printRequestAttributeSet);
        configuration.setPrintServiceAttributeSet(printServiceAttributeSet);
        configuration.setDisplayPageDialog(false);
        configuration.setDisplayPrintDialog(false);

        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setConfiguration(configuration);

//Iterate through available printer, and once matched with our <selectedPrinter>, go ahead and print! 
        if (services != null && services.length != 0) {
            for (PrintService service : services) {
                String existingPrinter = service.getName();
                if (existingPrinter.equals(selectedPrinter)) {
                    selectedService = service;
                    break;
                }
            }
        }
        if (selectedService != null) {
            try {
                //Lets the printer do its magic! 
                exporter.exportReport();
            } catch (Exception e) {
                System.out.println("JasperReport Error: " + e.getMessage());
            }
        } else {
            System.out.println("JasperReport Error: Printer not found!");
        }
    }

    public static Double getSaldoPendienteCliente(String idcliente) {
        sSQL = "SELECT\n"
                + "ROUND(\n"
                + "    	(\n"
                + "    	  (SUM(monto)) - (\n"
                + "        \n"
                + "        					SELECT\n"
                + "							(SUM(monto))\n"
                + "							FROM\n"
                + "							movimientocliente\n"
                + "							WHERE tipo = 1 AND idcliente = " + idcliente + "\n"
                + "							GROUP BY idcliente\n"
                + "    			    	)\n"
                + "        ),2)SALDO_PENDIENTE\n"
                + "FROM\n"
                + "movimientocliente\n"
                + "WHERE tipo = 0 AND idcliente = " + idcliente + "\n"
                + "GROUP BY idcliente";
        Double SALDO_PENDIENTE_CLIENTE = 0d;
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            while (rs.next()) {
                SALDO_PENDIENTE_CLIENTE = rs.getDouble("SALDO_PENDIENTE");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en getSaldoPendienteCliente() " + e);
        }

        return SALDO_PENDIENTE_CLIENTE;
    }

    public static Integer getTotalArtsUltimoTicket() {
        sSQL = "SELECT\n"
                + "SUM(dt.cantidad) totalArtsUltimoTciket\n"
                + "FROM detalleticket dt\n"
                + "INNER JOIN ticket t\n"
                + "ON  dt.idticket = t.idticket \n"
                + "WHERE dt.idticket = (SELECT idticket FROM ticket ORDER BY idticket DESC LIMIT 1)";
        int totalArts = 0;
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            while (rs.next()) {
                totalArts = rs.getInt("totalArtsUltimoTciket");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al invocar metodo getTotalArtsUltimoTicket: " + e);
        }

        return totalArts;
    }

    public static Double totalUlltimoTicket() {
        sSQL = "SELECT\n"
                + "ROUND((SUM((dt.cantidad) * dt.precioVenta) + (SUM((dt.cantidad) * dt.precioVenta) * (t.iva /100))),2)  totalImporte "
                + "FROM detalleticket dt\n"
                + "INNER JOIN producto p\n"
                + "ON dt.idproducto = p.idproducto\n"
                + "INNER JOIN ticket t\n"
                + "ON dt.idticket = t.idticket\n"
                + "WHERE dt.idticket = (SELECT idticket FROM ticket ORDER BY idticket DESC LIMIT 1) "
                + "GROUP BY dt.idticket";
        Double totalImporteUltimoTicket = 0d;
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            while (rs.next()) {
                totalImporteUltimoTicket = rs.getDouble("totalImporte");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en totalUlltimoTicket: " + e);
        }

        return totalImporteUltimoTicket;
    }

    public static Double pagoConUltimoTicket() {
        sSQL = "SELECT\n"
                + "t.pagoCon "
                + "FROM ticket t\n"
                + "WHERE t.idticket = (SELECT idticket FROM ticket ORDER BY idticket DESC LIMIT 1)";
        Double pagoConUltimoTicket = 0d;
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            while (rs.next()) {
                pagoConUltimoTicket = rs.getDouble("pagoCon");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error pagoConUltimoTicket: " + e);
        }

        return pagoConUltimoTicket;
    }

    public static Integer ivaTicket(int idticket) {
        sSQL = "SELECT\n"
                + "t.iva "
                + "FROM ticket t\n"
                + "WHERE t.idticket = " + idticket;
        int ivaTicket = 0;
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            while (rs.next()) {
                ivaTicket = rs.getInt("iva");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error ivaTicket: " + e);
        }

        return ivaTicket;
    }

    public static Integer idUltimoTicket() {
        sSQL = "SELECT idticket FROM ticket ORDER BY idticket DESC LIMIT 1";
        int idUltimoTicket = 0;
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            while (rs.next()) {
                idUltimoTicket = rs.getInt("idticket");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error idUltimoTicket: " + e);
        }

        return idUltimoTicket;
    }

    public static Integer getIdUltimoCliente() {
        sSQL = "SELECT\n"
                + "cl.idcliente\n"
                + "FROM ticket t\n"
                + "INNER JOIN cliente cl\n"
                + "ON t.idcliente = cl.idcliente\n"
                + "WHERE t.idticket = (SELECT idticket FROM ticket ORDER BY idticket DESC LIMIT 1)";
        int idUltimoCliente = 0;
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            while (rs.next()) {
                idUltimoCliente = rs.getInt("idcliente");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error getIdUltimoIdCliente: " + e);
        }

        return idUltimoCliente;
    }

    public static Double myRound(String numero) {
        numero = numero.replace("$", "");
        numero = numero.replace(",", "");
        return Double.parseDouble(numero);
    }

    public static void imprimirCorte(String fecha_corte, String nombreCajero, Double[] datos) {
        Map p = new HashMap();
        JasperReport report;
        JasperPrint print;
        String archivo = new File("").getAbsolutePath() + "/src/Reportes/reporteCorte.jasper";

        for (int i = 0; i < datos.length; i++) {
            if (datos[i] == null) {
                datos[i] = 0d;
            }
            System.out.println("VALOR: " + datos[i] + "\nINDICE: " + i + "\n=============================");

        }
        //FECHA CORTE TIENE EL SIGUIENTE FORMATO: '%d-%m-%Y'

        String ENCABEZADO = FrmMain.confiEncabezadoTicket;
        String FECHA = "DEL " + fecha_corte;
        String REALIZADO = "REALIZADO: " + new Date().toString();
        String CAJERO = "CAJERO: " + nombreCajero;
        String VENTAS_EN_EL_DIA = ventasDelDia(fecha_corte) + " VENTAS EN EL DIA";
        String ENTRADA = datos[0] + "";      //ENTRADAS  
        String TOTAL_ENTRADAS = datos[0] + ""; //TOTAL ENTRADAS
        String VENTAS_EFECTIVO = datos[2] + ""; //VENTAS EN EFECTIVO
        String TOTAL_VENTAS_EFECTIVO = datos[2] + ""; // TOTAL DE VENTAS EN EFECTIVO
        String TOTAL_SALIDAS = datos[1] + "";  //SALIDAS

        String PAGOS_EFECTIVO = datos[3] + ""; //PAGOS DE CLIENTES
        //String TOTAL_CAJA = datos[4] + ""; //TOTAL DINERO
        Double TOTAL_CAJA = datos[0] + datos[3] - datos[1];
        String TOTAL_VENTAS_TOTALES = datos[5] + ""; //VENTAS TOTALES

        String TOTAL_IMPUESTOS = impuestosDelDia(fecha_corte) + "";
        String TOTAL_GANANCIAS_DE_DIA = datos[6] + "";

        p.put("encabezado", ENCABEZADO);
        p.put("FECHA", FECHA);
        p.put("fecha_Corte", fecha_corte);
        p.put("realizado", REALIZADO);
        p.put("cajero", CAJERO);
        p.put("ventas_en_el_dia", VENTAS_EN_EL_DIA);
        p.put("entrada", ENTRADA);
        p.put("total_entradas", TOTAL_ENTRADAS);
        p.put("ventas_con_efectivo", VENTAS_EFECTIVO);
        p.put("total_ventas_con_efectivo", TOTAL_VENTAS_EFECTIVO);

        p.put("total_salidas", TOTAL_SALIDAS);
        p.put("pagos_efectivo", PAGOS_EFECTIVO);
        p.put("total_caja", TOTAL_CAJA + "");
        p.put("total_ventas_totales", TOTAL_VENTAS_TOTALES);
        p.put("total_impuestos", TOTAL_IMPUESTOS);
        p.put("total_ganancias_del_dia", TOTAL_GANANCIAS_DE_DIA);

        String[] clienteInfo = pagosCredito(fecha_corte);
        p.put("nombre_cliente", clienteInfo[0]);
        p.put("monto_pago_cliente", clienteInfo[1]);

        String[] categoriaInfo = ventasCategoria(fecha_corte);
        p.put("nombre_categoria", categoriaInfo[0]);
        p.put("total_categoria", categoriaInfo[1]);
        try {
            report = (JasperReport) JRLoader.loadObjectFromFile(archivo);
            print = JasperFillManager.fillReport(report, p, cn);

            PrintReportToPrinter(print);
        } catch (JRException e) {
            JOptionPane.showMessageDialog(null, "Error imprimirCorte: " + e.getMessage());
        }

    }

    public static Integer ventasDelDia(String fecha) {
        sSQL = "select fecha, COUNT(fecha) noventas from ticket\n"
                + "WHERE DATE_FORMAT(fecha,'%d-%m-%Y') = '" + fecha + "'";
        int NO_VENTAS = 0;
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            while (rs.next()) {
                NO_VENTAS = rs.getInt("noventas");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en ventasDelDia: " + e);
        }
        return NO_VENTAS;
    }

    public static String[] pagosCredito(String fecha) {
        sSQL = "SELECT c.nombre,p.monto\n"
                + "FROM pago p\n"
                + "INNER JOIN cliente c\n"
                + "ON p.idcliente = c.idcliente\n"
                + "WHERE p.monto <> 0 AND Date_format(p.fecha,'%d-%m-%Y') = '" + fecha + "'";
        String[] pagos = new String[2];
        String nombre = "";
        String monto = "";
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            while (rs.next()) {
                nombre += rs.getString("nombre") + "\n";
                monto += rs.getString("monto") + "\n";
            }
            pagos[0] = nombre;
            pagos[1] = monto;
            System.out.println("CLIENTE: " + pagos[0] + " \nMONTO: " + pagos[1]);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error pagosCredito: " + e);
        }
        return pagos;
    }

    public static String[] ventasCategoria(String fecha) {
        sSQL = "SELECT c.nombre CATEGORIA,\n"
                + "ROUND((SUM((dt.cantidad) * dt.precioVenta) + (SUM((dt.cantidad) * dt.precioVenta) * (t.iva /100))),2) TOTAL\n"
                + "FROM detalleticket dt\n"
                + "INNER JOIN ticket t\n"
                + "ON dt.idticket = t.idticket\n"
                + "INNER JOIN producto p\n"
                + "ON dt.idproducto = p.idproducto\n"
                + "INNER JOIN categoria c\n"
                + "ON p.idcategoria = c.idcategoria\n"
                + "WHERE t.idcliente = 1 AND Date_format(t.fecha,'%d-%m-%Y') = '" + fecha + "'\n"
                + "GROUP BY c.nombre";
        String[] ventas = new String[2];
        String nombre_categoria = "";
        String monto_categoria = "";
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            while (rs.next()) {
                nombre_categoria += rs.getString("CATEGORIA") + "\n";
                monto_categoria += rs.getString("TOTAL") + "\n";
            }
            ventas[0] = nombre_categoria;
            ventas[1] = monto_categoria;
            System.out.println("CATEORIA: " + ventas[0] + " \nTOTAL: " + ventas[1]);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error ventasCategoria: " + e);
        }
        return ventas;
    }

    public static Double impuestosDelDia(String fecha) {
        sSQL = "SELECT \n"
                + "ROUND((SUM(dt.cantidad * dt.precioVenta) * (t.iva /100)),2) IMPUESTOS\n"
                + "FROM detalleticket dt\n"
                + "INNER JOIN ticket t\n"
                + "ON dt.idticket = t.idticket\n"
                + "WHERE t.idcliente = 1 AND Date_format(fecha,'%d-%m-%Y') = '" + fecha + "'\n"
                + "GROUP BY Date_format(fecha,'%d-%m-%Y')";
        Double impuestoDelDia = 0d;
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            while (rs.next()) {
                impuestoDelDia += rs.getDouble("IMPUESTOS");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error impuestosDelDia: " + e);
        }
        return impuestoDelDia;
    }
}
