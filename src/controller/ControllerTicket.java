package controller;

import extras.Conexion;
import model.ModelTicket;
import model.ModelProduct;
import view.FrmMain;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ControllerTicket {

    private static Conexion mysql = new Conexion();
    private static Connection cn = mysql.getConnection();
    private static String sSQL = "", sSQL2 = "";
    public static boolean nuevaVenta = true;
    public static double totalImporteTicket = 0d;
    public static double totalImpuestos = 0d;
    public static double total = 0d;
    int c = 0;

    int r = 0, r2 = 0;
    public boolean insertar(ModelTicket dts, ModelProduct dtsp, int cantidad) {
        String s = "(SELECT idticket FROM ticket ORDER BY idticket DESC LIMIT 1)";
        sSQL = "insert into ticket (idempleado,idcliente,iva,pagoCon) "
                + "values(?,?,?,?)";
        sSQL2 = "insert into detalleticket (idticket,idproducto,cantidad,precioVenta) "
                + "values(" + s + ",?,?,?)";

        try {
            if (nuevaVenta) {
                PreparedStatement pst;
                pst = cn.prepareStatement(sSQL);
                pst.setInt(1, dts.getIdEmpleado());
                pst.setInt(2, dts.getIdCliente());
                pst.setInt(3, dts.getIva());
                pst.setDouble(4, dts.getPagoCon());
                r = pst.executeUpdate();
                nuevaVenta = false;
            }

            PreparedStatement pst2;
            pst2 = cn.prepareStatement(sSQL2);
            
            pst2.setInt(1, dtsp.getIdproducto()); //idproducto
            pst2.setInt(2, cantidad); //cantidad
            pst2.setDouble(3, dtsp.getPrecioVenta()); //precioVenta

            if (r != 0) {
                r2 = pst2.executeUpdate();
                if (r2 != 0) {
                    return true;
                } else {
                    return false;
                }

            } else {
                return false;
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
            return false;
        }
    }

    public DefaultTableModel mostrar(String p_fecha_Desde, String p_fecha_Hasta) {
        DefaultTableModel modelo;
        String[] titulos = {"NOSERIE", "DESCRIPCION", "CANTIDAD", "PRECIOVENTA", "CATEGORIA"};
        //String[] registro = new String[5];
        Object[] registro = new Object[5];
        //totalRegistro = 0;
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

        String sSQL = "SELECT "
                + "p.noserie, "
                + "p.descripcion, "
                + "dt.cantidad, "
                + "dt.precioVenta, "
                + "c.nombre "
                + "FROM detalleticket dt "
                + "INNER JOIN producto p "
                + "ON dt.idproducto = p.idproducto "
                + "INNER JOIN ticket t "
                + "ON  dt.idticket = t.idticket "
                + "INNER JOIN categoria c "
                + "ON p.idcategoria = c.idcategoria "
                + "WHERE Date_format(t.fecha,'%Y-%m-%d') BETWEEN '" + p_fecha_Desde + "' AND '" + p_fecha_Hasta + "'";
        
        try {

            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            while (rs.next()) {
                registro[0] = rs.getString("noserie");
                registro[1] = rs.getString("descripcion");
                registro[2] = rs.getInt("cantidad");
                registro[3] = rs.getDouble("precioVenta");
                registro[4] = rs.getString("nombre");

                modelo.addRow(registro);
            }

        } catch (SQLException e) {
            //JOptionPane.showMessageDialog(null, e, "Advertencia", JOptionPane.PLAIN_MESSAGE, icono);
            return null;
        }
        return modelo;
    }

    public DefaultTableModel ticket(int idcliente, String fecha, int idticket) {
        DefaultTableModel modelo2;
        String[] titulos = {"IDTICKET", "DESCRIPCION", "PRECIO VENTA", "CANTIDAD", "IMPORTE","IDCLIENTE","IDEMPLEADO"};
        String[] registro = new String[7];
        modelo2 = new DefaultTableModel(null, titulos) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 0) {
                    return true;
                } else {
                    return false;
                }
            }

        };
        totalImporteTicket = 0d;
        totalImpuestos = 0d;
        total = 0d;

        //GENERAR CONSULTA Y MOSTRARLA EN LA TABLA
        sSQL = "SELECT t.idticket, p.descripcion,dt.precioVenta,dt.cantidad , (dt.cantidad * dt.precioVenta) Importe,t.idcliente,t.idempleado\n"
                + "FROM detalleticket dt\n"
                + "INNER JOIN producto p\n"
                + "ON dt.idproducto = p.idproducto\n"
                + "INNER JOIN ticket t\n"
                + "ON dt.idticket = t.idticket\n" //%d-%W-%Y  DATE_FORMAT(t.fecha, '%h:%i:%s')
                + "WHERE t.idcliente = " + idcliente + " AND t.fecha = '" + fecha + "' AND t.idticket = " + idticket;
        
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            while (rs.next()) {
                registro[0] = rs.getString("idticket");
                registro[1] = rs.getString("descripcion");
                registro[2] = rs.getString("precioVenta");
                registro[3] = rs.getString("cantidad");
                registro[4] = rs.getString("Importe");
                registro[5] = rs.getString("idcliente");
                registro[6] = rs.getString("idempleado");
                modelo2.addRow(registro);
                total += rs.getDouble("Importe");
                
            }
            totalImpuestos = (total * FrmMain.IMPUESTO/100);
            totalImporteTicket = total + totalImpuestos;
            
        } catch (SQLException er) {
            JOptionPane.showMessageDialog(null, er);
        }
        return modelo2;
    }

    public DefaultTableModel ventasCategoria(String fecha) {
        DefaultTableModel modelo2;
        String[] titulos = {"CATEGORIA", "TOTAL"};
        String[] registro = new String[2];
        modelo2 = new DefaultTableModel(null, titulos) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 0) {
                    return true;
                } else {
                    return false;
                }
            }

        };

        //GENERAR CONSULTA Y MOSTRARLA EN LA TABLA
        sSQL = "SELECT c.nombre CATEGORIA,"
                + " ROUND((SUM((dt.cantidad) * dt.precioVenta) + (SUM((dt.cantidad) * dt.precioVenta) * (t.iva /100))),2) TOTAL \n"
                + "FROM detalleticket dt \n"
                + "INNER JOIN ticket t \n"
                + "ON dt.idticket = t.idticket\n"
                + "INNER JOIN producto p \n"
                + "ON dt.idproducto = p.idproducto\n"
                + "INNER JOIN categoria c\n"
                + "ON p.idcategoria = c.idcategoria\n"
                + "WHERE t.idcliente = 1 AND Date_format(t.fecha,'%Y-%m-%d') = '" + fecha + "'\n"
                + "GROUP BY c.nombre";

        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            while (rs.next()) {
                registro[0] = rs.getString("CATEGORIA");
                registro[1] = rs.getString("TOTAL");
                modelo2.addRow(registro);

            }
        } catch (SQLException er) {
            JOptionPane.showMessageDialog(null, er);
        }
        return modelo2;
    }

    public static void cancelarTicket(String idticket) {
        String sSQL = "update ticket set estado = ? where idticket  = ?";

        try {
            //2 VENTA LIQUIDADA
            //1 VENTA CANCELADA
            //0 NO CANCELADA
            PreparedStatement pst = cn.prepareStatement(sSQL);
            pst.setInt(1, 1);
            pst.setInt(2, Integer.parseInt(idticket));
            int n = pst.executeUpdate();
            if (n > 0) {
            }

        } catch (SQLException ex) {
        }
    }

    public static void liquidarTicketCliente(int idcliente) {
        String sSQL = "update ticket set estado=? where idcliente  = ?";

        try {
            //1 VENTA CANCELADA
            //0 NO CANCELADA
            PreparedStatement pst = cn.prepareStatement(sSQL);
            pst.setInt(1, 2);
            pst.setInt(2, idcliente);
            int n = pst.executeUpdate();
            if (n > 0) {
            }

        } catch (SQLException ex) {
        }
    }

    public static boolean ticketLiquidadoTodoCliente(int idcliente) {
        sSQL = "SELECT t.estado\n"
                + "FROM ticket t\n"
                + "WHERE t.idcliente = " + idcliente;
        boolean respuesta = true;
        int count = 0;
        int estado = 0;
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            while (rs.next()) {
                if (rs.getInt("estado") != 2) {
                    respuesta = false;
                }
            }
        } catch (SQLException ex) {
        }

        return respuesta;
    }

    /*public static void imprimirComprobante(int nro){
 
    try{	    
	    String dir= "DIRECCION REPORTE";
	    JasperReport reporteJasper = JasperCompileManager.compileReport(dir);
	    Map parametro = new HashMap();
	    parametro.put("@nro", nro);
	    JasperPrint mostrarReporte = JasperFillManager.fillReport(reporteJasper,parametro, cn);
 
 
 
	// ESTABLECE DATOS DE IMPRESORAS
 
	       PrinterJob job = PrinterJob.getPrinterJob();
	       PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
	    int selectedService = 0;
	    for(int i = 0; i < services.length;i++){
	    if(services[i].getName().toUpperCase().contains('NOMBRE IMPRESORA')){
                selectedService = i;
	    }
 
	    }
 
	      job.setPrintService(services[selectedService]);
	       PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
	       MediaSizeName mediaSizeName = MediaSize.findMedia(4,4,MediaPrintableArea.INCH);
	      printRequestAttributeSet.add(mediaSizeName);
	      printRequestAttributeSet.add(new Copies(1));
	      JRPrintServiceExporter exporter;
	      exporter = new JRPrintServiceExporter();
	      exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE, services[selectedService]);
	    exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, services[selectedService].getAttributes());
	    exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
	    exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
	    exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
	    exporter.setParameter(JRExporterParameter.JASPER_PRINT, mostrarReporte);
	    exporter.exportReport();
    }catch(JRException ex){
        JOptionPane.showMessageDialog(null, "Error de JREEXEPCION: " + ex);
 
    } catch (PrinterException ex) {
        JOptionPane.showMessageDialog(null,"ERROR PRINTEREXCEPCION " + ex);
    }
 
}*/
}
