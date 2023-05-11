package view.sales;

import extras.EstiloTablaHeader;
import extras.EstiloTablaRenderer;
import extras.MyColor;
import extras.MyFormater;
import extras.MyScrollbarUI;
import extras.Conexion;
import java.awt.print.PrinterException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import view.FrmMain;

public class FrmVentasDevoluciones2 extends javax.swing.JDialog {

    private Conexion mysql = new Conexion();
    private Connection cn = mysql.getConnection();
    private String sSQL = "";

    public static int IdempleadoU_Ventas_devoluciones = 0;
    public static String nombreEmpleado = "";
    public static String idcliente = "";

    String data[][] = {};
    boolean tablaTicketVenta = true;

    ArrayList<String> detalleTicket = null;

    //VARIABLE GLOBAL PARA IMPRIMIR TICEKT
    String idticket = "";
    String totalTicket = "";
    String pagoCon_ticket = "";

    public FrmVentasDevoluciones2(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);
        cargarTicket("");
        mostrarEmpleado();

        jPanel4.setVisible(false);
        jLabel7.setVisible(false);

        jScrollPane1.getViewport().setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.getVerticalScrollBar().setUI(new MyScrollbarUI());
        jScrollPane1.getHorizontalScrollBar().setUI(new MyScrollbarUI());

        dtVentaDia.getTableHeader().setDefaultRenderer(new EstiloTablaHeader());
        dtVentaDia.setDefaultRenderer(Object.class, new EstiloTablaRenderer());

    }

    public void cargarTicket(String parametro) {
        SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date fecha = new java.util.Date();
        //fecha = jDateChooser1.getDate();
        fecha = (java.util.Date) jDateChooser1.getDate();
        String p_fecha = FORMAT.format(fecha);
        mostrar(p_fecha, parametro);
    }

    void mostrar(String fecha, String ticket_nombreEm) {
        DefaultTableModel modelo;
        String cabeza[] = {"ide", "idc", "FOLIO", "ARTS", "FECHA", "HORA", "TOTAL", "ESTADO", "EMPLEADO", "CLIENTE", "PAGOCON"};

        modelo = new DefaultTableModel(data, cabeza) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 0) {
                    return true;
                } else {
                    return false;
                }
            }

        };

        sSQL = "SELECT "
                + "t.idempleado, "
                + "t.idcliente, "
                + "t.idticket FOLIO, "
                + "SUM(dt.cantidad) ARTS, "
                + "DATE_FORMAT(t.fecha, '%Y-%M-%d %h:%i:%s %p' ) FECHA, "
                + "DATE_FORMAT(t.fecha, '%h:%i %p' ) HORA, "
                + "ROUND((SUM((dt.cantidad) * dt.precioVenta) + (SUM((dt.cantidad) * dt.precioVenta) * (t.iva /100))),2)  TOTAL, "
                + "t.estado, "
                + "e.nombre NOMBRE_CAJERO, "
                + "c.nombre NOMBRE_CLIENTE, "
                + "t.pagoCon "
                + "FROM detalleticket dt "
                + "INNER JOIN ticket t "
                + "ON t.idticket = dt.idticket "
                + "INNER JOIN empleado e "
                + "ON t.idempleado = e.idempleado "
                + "INNER JOIN cliente c "
                + "ON c.idcliente = t.idcliente "
                + "WHERE Date_format(t.fecha,'%Y-%m-%d') = '" + fecha + "' AND (e.nombre = '" + ticket_nombreEm + "' OR dt.idticket like '" + ticket_nombreEm + "%')"
                + "GROUP BY t.idticket "
                + "ORDER BY t.idticket DESC";

        String[] registro = new String[11];
        String HM = "";

        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            while (rs.next()) {
                registro[0] = rs.getString("idempleado");
                registro[1] = rs.getString("idcliente");
                registro[2] = rs.getString("FOLIO");
                registro[3] = rs.getString("ARTS");
                registro[4] = rs.getString("FECHA");
                registro[5] = rs.getString("HORA");
                registro[6] = rs.getString("TOTAL");
                registro[7] = rs.getString("estado");
                registro[8] = rs.getString("NOMBRE_CAJERO");
                registro[9] = rs.getString("NOMBRE_CLIENTE");
                registro[10] = rs.getString("pagoCon");
                modelo.addRow(registro);

            }
            dtVentaDia.setModel(modelo);
            //dtVentaDia.setDefaultRenderer(Object.class, new Resaltador());
            ocultar_columnas();

            if (dtVentaDia.getRowCount() != 0) {
                dtVentaDia.changeSelection(0, 2, false, false);
                ticket();

            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al invocar metodo mostrar ticket: " + e);
        }

    }

    void ocultar_columnas() {
        for (int i = 0; i < dtVentaDia.getColumnCount(); i++) {
            if (i != 2 && i != 3 && i != 5 && i != 6) {
                dtVentaDia.getColumnModel().getColumn(i).setMaxWidth(1);
                dtVentaDia.getColumnModel().getColumn(i).setMinWidth(1);
                dtVentaDia.getColumnModel().getColumn(i).setPreferredWidth(1);
            }
        }
    }

    public Integer cantidadDetalleticket(String idticket) {
        sSQL = "SELECT cantidad \n"
                + "FROM detalleticket\n"
                + "WHERE iddetalleticket = " + idticket + "\n";
        int cantidad = 0;
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            while (rs.next()) {
                cantidad = rs.getInt("cantidad");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en cantidadDetalleticket: " + e);
        }
        return cantidad;
    }

    public ArrayList<ArrayList> cantidades(String idticket) {
        ArrayList<Integer> listaCantidad = new ArrayList<Integer>();
        ArrayList<Integer> listaIddetalleticket = new ArrayList<Integer>();
        ArrayList<ArrayList> datos = new ArrayList<ArrayList>();
        
        sSQL = "SELECT dt.iddetalleticket, dt.cantidad \n"
                + "FROM ticket t\n"
                + "INNER JOIN detalleticket dt\n"
                + "ON t.idticket = dt.idticket\n"
                + "INNER JOIN producto p\n"
                + "ON dt.idproducto = p.idproducto\n"
                + "WHERE t.idticket = " + idticket+"\n"
                + "ORDER BY p.descripcion ASC";

        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            while (rs.next()) {
                listaCantidad.add(rs.getInt("cantidad"));
                listaIddetalleticket.add(rs.getInt("iddetalleticket"));
            }
            datos.add(listaCantidad);
            datos.add(listaIddetalleticket);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en cantidades: " + e);
        }
        return datos;
    }

    public void devolver(String idticket, String iddetalleticket, String code) {
        //boolean respuesta = false;
        int ingresado = Integer.parseInt(code);
        //int cantidad = cantidadDetalleticket(iddetalleticket);
        ArrayList<Integer> cantidad = cantidades(idticket).get(0);
        ArrayList<Integer> iddetalle = cantidades(idticket).get(1);
        int can = 0;
        for (int i = 0; i < cantidad.size(); i++) {
            if (cantidad.get(i) <= ingresado || ingresado <= cantidad.get(i)) {
                System.out.println("IDDETALLE: "+iddetalle.get(i)+"\nCANTIDAD: "+cantidad.get(i)+"\nINGRESADO: "+ingresado);
                sSQL = "update detalleticket set cantidad = cantidad - ? where iddetalleticket = ?";

                
                try {
                    PreparedStatement pst = cn.prepareStatement(sSQL);
                    if(cantidad.get(i) <= ingresado){
                        can = cantidad.get(i);
                        pst.setInt(1, can);
                        pst.setInt(2, iddetalle.get(i));
                        int n = pst.executeUpdate();
                        ingresado -= cantidad.get(i);                        
                    }else{
                        can = ingresado;
                        pst.setInt(1, can);
                        pst.setInt(2, iddetalle.get(i));
                        int n = pst.executeUpdate();
                        break;
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private Integer getCantidadDetalleTicket(String idticket) {
        String sql = "update detalleticket set cantidad = ? where idticket  = " + idticket;

        /*try {
                PreparedStatement pst = cn.prepareStatement(sql);
                pst.setInt(1, cantidad);
                int n = pst.executeUpdate();
                if (n > 0) {
                    JOptionPane.showMessageDialog(rootPane, "Fue ajustado correctamente...");
                    //inhabilitarPro();
                }
                 
            } catch (SQLException ex) {
                ex.printStackTrace();(FrmVentasDevoluciones2.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
         */
        return 0;
    }


    public void ticket() {
        detalleTicket = new ArrayList<String>();
        int indexCount = 0;
        DefaultTableModel modelo2;
        String cabeza2[] = {"idticket", "iddetaleticket", "CANT.", "DESCRIPCIÓN", "PRECIO U.", "IMPORTE","IDPRODUCTO","IDEMPLEADO","EXISTENCIA"};
        modelo2 = new DefaultTableModel(data, cabeza2) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 0) {
                    return true;
                } else {
                    return false;
                }
            }

        };
        int fila = dtVentaDia.getSelectedRow();
        String idempleado = (dtVentaDia.getValueAt(fila, 0).toString());
        idcliente = (dtVentaDia.getValueAt(fila, 1).toString());
        idticket = (dtVentaDia.getValueAt(fila, 2).toString());
        String fecha = (dtVentaDia.getValueAt(fila, 4).toString());
        totalTicket = (dtVentaDia.getValueAt(fila, 6).toString());
        String estado = (dtVentaDia.getValueAt(fila, 7).toString());
        String nombreEmpleado = (dtVentaDia.getValueAt(fila, 8).toString());
        String nombreCliente = (dtVentaDia.getValueAt(fila, 9).toString());
        pagoCon_ticket = (dtVentaDia.getValueAt(fila, 10).toString());
        //jLabel3.setText(""+estado);
        if (estado.equals("1")) {
            ponerImagenCancelado();
            btnCalcelarVenta.setEnabled(false);
            btnDevolverProductoSelecionado.setEnabled(false);
        } else {
            quitarImagenCancelado();
            btnCalcelarVenta.setEnabled(true);
            btnDevolverProductoSelecionado.setEnabled(true);
        }
        //GENERAR CONSULTA Y MOSTRARLA EN LA TABLE DE TICKET PARA IMPRIMIR
        sSQL = "SELECT t.idticket,dt.iddetalleticket , dt.cantidad, p.descripcion,dt.precioVenta ,(dt.cantidad * dt.precioVenta) Importe, t.pagoCon,p.idproducto,t.idempleado,p.existencia\n"
                + "FROM detalleticket dt\n"
                + "INNER JOIN producto p\n"
                + "ON dt.idproducto = p.idproducto\n"
                + "INNER JOIN ticket t\n"
                + "ON dt.idticket = t.idticket\n"
                + "WHERE t.idticket = " + idticket
                + " ORDER BY p.descripcion ASC"; //p.descripcion
        String[] registro = new String[9];
        String pagocon = "";

        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            while (rs.next()) {
                registro[0] = rs.getString("idticket");
                registro[1] = rs.getString("iddetalleticket");
                registro[2] = rs.getString("cantidad");
                registro[3] = rs.getString("descripcion");
                registro[4] = rs.getString("precioVenta");
                registro[5] = rs.getString("Importe");
                registro[6] = rs.getString("idproducto");
                registro[7] = rs.getString("idempleado");
                registro[8] = rs.getString("existencia");                
                pagocon = rs.getString("pagoCon");
                modelo2.addRow(registro);
                detalleTicket.add(indexCount, rs.getString("cantidad"));
                indexCount += 1;
            }
            indexCount = 0;
            lblFolioTicket.setText(idticket);
            lblCajeroTicket.setText(nombreEmpleado);
            lblClienteTicket.setText(nombreCliente);
            lblFechaTicket.setText(fecha);
            dtTicketImprimir.setModel(modelo2);
            ocultar_columnasTicketImprimir();

            lblTotalTicket.setText(MyFormater.formato(Double.parseDouble(totalTicket)));

            if (!idcliente.equals("1")) {
                lblPagoTicket.setText("Crédito");
            } else {
                lblPagoTicket.setText(MyFormater.formato(Double.parseDouble(pagocon)));
            }

        } catch (SQLException er) {
            JOptionPane.showMessageDialog(null, er);
        }
    }

    private void ponerImagenCancelado() {
        jLayeredPane1.moveToFront(jLabel10);
    }

    private void quitarImagenCancelado() {
        jLayeredPane1.moveToBack(jLabel10);
    }

    void ocultar_columnasTicketImprimir() {
        for (int i = 0; i < dtTicketImprimir.getColumnCount(); i++) {
            if (i != 2 && i != 3 && i != 4 && i != 5) {
                dtTicketImprimir.getColumnModel().getColumn(i).setMaxWidth(1);
                dtTicketImprimir.getColumnModel().getColumn(i).setMinWidth(1);
                dtTicketImprimir.getColumnModel().getColumn(i).setPreferredWidth(1);
            }
        }

    }

    void mostrarEmpleado() {
        String sql1 = "select * from empleado ORDER BY nombre ASC";
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql1);

            while (rs.next()) {
                cbxEmpleado.addItem(rs.getString("nombre"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            //return null;
        }

    }

    public void cancelarVenta() {
        int fila = dtVentaDia.getSelectedRow();
        String idticket = (dtVentaDia.getValueAt(fila, 2).toString());
        sSQL = "update ticket set estado=? where idticket  = ?";

        try {
            //1 VENTA CANCELADA
            //0 NO CANCELADA
            PreparedStatement pst = cn.prepareStatement(sSQL);
            pst.setInt(1, 1);
            pst.setInt(2, Integer.parseInt(idticket));
            int n = pst.executeUpdate();
            if (n > 0) {
                //JOptionPane.showMessageDialog(rootPane, "Los ajustes fueron actualizados correctamente...");
                //AQUÍ INVOCAR EL MÉTODO PARA MOSTRAR LA IMAGEN
                ponerImagenCancelado();
                btnCalcelarVenta.setEnabled(false);
                btnDevolverProductoSelecionado.setEnabled(false);
                cargarTicket("");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void devolverProducto() {
        //if (tablaTicketVenta) {
        int fila = -1;
        if (dtTicketImprimir.getSelectedRow() == -1) {
            dtTicketImprimir.changeSelection(0, 0, false, false);
        }
        fila = dtTicketImprimir.getSelectedRow();

        //String idempleado = (dtTicketImprimir.getValueAt(fila, 0).toString());
        String idticket = (dtTicketImprimir.getValueAt(fila, 0).toString());
        String iddetalleticket = (dtTicketImprimir.getValueAt(fila, 1).toString());
        String cantidadArt = (dtVentaDia.getValueAt(fila, 3).toString());
        String idproducto = (dtTicketImprimir.getValueAt(fila, 6).toString());
        String idempleado = (dtTicketImprimir.getValueAt(fila, 7).toString());
        String existencia = (dtTicketImprimir.getValueAt(fila, 8).toString());

        //DEVOLUCIÓN DE ARTÍCULOS EN TICKET - VENTA
        String code = JOptionPane.showInputDialog(
                null,
                "Cantidad a devolver de los " + cantidadArt,
                "Devolución de Artículos",
                JOptionPane.INFORMATION_MESSAGE
        );

        if (cantidadArt.equals("0")) {
        } else {
            if (code.equals("")) {
                view.FrmMain.notificar2("Devolución cancelada");
            } else {
                if (code.equals("1") && cantidadArt.equals("1")) {
                    int op = JOptionPane.showConfirmDialog(null, "Al devolver el único de este ticket se cancelará esta venta. ¿Quiere cancelar esta venta?", "Cancelar Venta", JOptionPane.YES_NO_OPTION);
                    if (op == 0) {
                        //CANCELAR VENTA
                        cancelarVenta();
                        view.FrmMain.notificar2("Venta cancelada");
                    }
                } else {
                                         
                    //REGISTRAR MOVIMIENTO 
                    view.FrmMain.registrarMovimiento(Integer.parseInt(idproducto), Integer.parseInt(idempleado),Integer.parseInt(existencia) , 4, Integer.parseInt(code));
                    //AUMENTAR EXISTENCIA
                    aumentarExistencia(Integer.parseInt(code),Integer.parseInt(idproducto));
                    //DEVOLVER PRODUCTO   
                    devolver(idticket, iddetalleticket, code);
                    cargarTicket("");
                    view.FrmMain.notificar2("Artículo devuelto");

                }

            }
        }

    }
    
    public boolean aumentarExistencia(int cantidadDevuelta, int idproducto) {
        String sSQL = "UPDATE producto SET existencia = existencia + " + cantidadDevuelta
                + " WHERE idProducto = " + idproducto;

        try {
            PreparedStatement pst = cn.prepareStatement(sSQL);
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanelFrmVentasDevoluciones = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lblFechaTicket = new javax.swing.JLabel();
        lblFolioTicket = new javax.swing.JLabel();
        lblCajeroTicket = new javax.swing.JLabel();
        lblClienteTicket = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        lblTotalTicket = new javax.swing.JLabel();
        lblPagoTicket = new javax.swing.JLabel();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        dtTicketImprimir = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        btnDevolverProductoSelecionado = new principal.MaterialButton();
        btnCalcelarVenta = new principal.MaterialButton();
        btnImprimirCopiaTicket = new principal.MaterialButton();
        jLabel3 = new javax.swing.JLabel();
        txtBuscarTicket = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        dtVentaDia = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        cbxEmpleado = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        mainPanelFrmVentasDevoluciones.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel1.setText("VENTAS DEL DÍA");

        jPanel3.setBackground(new MyColor().COLOR_BACKGROUND_BLACK);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jLabel6.setFont(new java.awt.Font("Courier New", 1, 11)); // NOI18N
        jLabel6.setText("Cliente:");

        jLabel8.setFont(new java.awt.Font("Courier New", 1, 11)); // NOI18N
        jLabel8.setText("Folio:");

        jLabel9.setFont(new java.awt.Font("Courier New", 1, 11)); // NOI18N
        jLabel9.setText("Cajero:");

        lblFechaTicket.setFont(new java.awt.Font("Courier New", 1, 11)); // NOI18N
        lblFechaTicket.setText("-");

        lblFolioTicket.setFont(new java.awt.Font("Courier New", 0, 11)); // NOI18N
        lblFolioTicket.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblFolioTicket.setText("-");

        lblCajeroTicket.setFont(new java.awt.Font("Courier New", 0, 11)); // NOI18N
        lblCajeroTicket.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCajeroTicket.setText("-");

        lblClienteTicket.setFont(new java.awt.Font("Courier New", 0, 11)); // NOI18N
        lblClienteTicket.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblClienteTicket.setText("-");

        jLabel14.setFont(new java.awt.Font("Courier New", 1, 11)); // NOI18N
        jLabel14.setText("Total:");

        jLabel15.setFont(new java.awt.Font("Courier New", 1, 11)); // NOI18N
        jLabel15.setText("Pagó con:");

        lblTotalTicket.setFont(new java.awt.Font("Courier New", 0, 11)); // NOI18N
        lblTotalTicket.setText("$0.00");

        lblPagoTicket.setFont(new java.awt.Font("Courier New", 0, 11)); // NOI18N
        lblPagoTicket.setText("$0.00");

        jLayeredPane1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dtTicketImprimir.setFont(new java.awt.Font("Courier New", 0, 11)); // NOI18N
        dtTicketImprimir.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "CANT.", "DESCRIPCIÓN", "PRECIO U.", "IMPORTE"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        dtTicketImprimir.setGridColor(new java.awt.Color(255, 255, 255));
        dtTicketImprimir.setShowHorizontalLines(false);
        dtTicketImprimir.setShowVerticalLines(false);
        dtTicketImprimir.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dtTicketImprimirMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(dtTicketImprimir);

        jLayeredPane1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 280, 180));

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/pngcancelado.png"))); // NOI18N
        jLayeredPane1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 270, 180));

        btnDevolverProductoSelecionado.setBackground(new MyColor().COLOR_BUTTON);
        btnDevolverProductoSelecionado.setForeground(new java.awt.Color(255, 255, 255));
        btnDevolverProductoSelecionado.setText("devolver el producto selecconado");
        btnDevolverProductoSelecionado.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnDevolverProductoSelecionado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDevolverProductoSelecionadoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblClienteTicket, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblFolioTicket, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCajeroTicket, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(42, 42, 42))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFechaTicket, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblPagoTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTotalTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnDevolverProductoSelecionado, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFolioTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCajeroTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblClienteTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblFechaTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(btnDevolverProductoSelecionado, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTotalTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 30, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblPagoTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        btnCalcelarVenta.setBackground(new MyColor().COLOR_BUTTON);
        btnCalcelarVenta.setForeground(new java.awt.Color(255, 255, 255));
        btnCalcelarVenta.setText("cancelar venta");
        btnCalcelarVenta.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnCalcelarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalcelarVentaActionPerformed(evt);
            }
        });

        btnImprimirCopiaTicket.setBackground(new MyColor().COLOR_BUTTON);
        btnImprimirCopiaTicket.setForeground(new java.awt.Color(255, 255, 255));
        btnImprimirCopiaTicket.setText("imprimir copia");
        btnImprimirCopiaTicket.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnImprimirCopiaTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirCopiaTicketActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btnCalcelarVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnImprimirCopiaTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCalcelarVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnImprimirCopiaTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel3.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel3.setText("Puedes buscar por folio:");

        txtBuscarTicket.setForeground(new MyColor().COLOR_FOCUS_TEXTFIELD);
        txtBuscarTicket.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarTicketKeyReleased(evt);
            }
        });

        jButton6.setBackground(new java.awt.Color(255, 255, 255));
        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/buscar32G.png"))); // NOI18N
        jButton6.setBorder(null);
        jButton6.setBorderPainted(false);
        jButton6.setContentAreaFilled(false);
        jButton6.setFocusPainted(false);

        dtVentaDia.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "FOLIO", "ARTS", "HORA", "TOTAL"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        dtVentaDia.setGridColor(new java.awt.Color(255, 255, 255));
        dtVentaDia.setShowHorizontalLines(false);
        dtVentaDia.setShowVerticalLines(false);
        dtVentaDia.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dtVentaDiaMouseClicked(evt);
            }
        });
        dtVentaDia.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dtVentaDiaPropertyChange(evt);
            }
        });
        dtVentaDia.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                dtVentaDiaKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(dtVentaDia);

        jPanel1.setBackground(new  MyColor().COLOR_BACKGROUND_BLACK);

        jDateChooser1.setDate(new java.util.Date());
        jDateChooser1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jDateChooser1PropertyChange(evt);
            }
        });

        cbxEmpleado.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxEmpleadoItemStateChanged(evt);
            }
        });
        cbxEmpleado.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                cbxEmpleadoPropertyChange(evt);
            }
        });

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Del día:");

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Cajero:");

        jPanel4.setBackground(new java.awt.Color(153, 255, 153));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );

        jLabel7.setForeground(new MyColor().COLOR_JLABEL_BLACK);
        jLabel7.setText("Ventas a Crédito");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(62, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbxEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(78, 78, 78))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout mainPanelFrmVentasDevolucionesLayout = new javax.swing.GroupLayout(mainPanelFrmVentasDevoluciones);
        mainPanelFrmVentasDevoluciones.setLayout(mainPanelFrmVentasDevolucionesLayout);
        mainPanelFrmVentasDevolucionesLayout.setHorizontalGroup(
            mainPanelFrmVentasDevolucionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelFrmVentasDevolucionesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelFrmVentasDevolucionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelFrmVentasDevolucionesLayout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                        .addGap(3, 3, 3))
                    .addGroup(mainPanelFrmVentasDevolucionesLayout.createSequentialGroup()
                        .addGroup(mainPanelFrmVentasDevolucionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(mainPanelFrmVentasDevolucionesLayout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addComponent(jButton6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBuscarTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(mainPanelFrmVentasDevolucionesLayout.createSequentialGroup()
                .addGroup(mainPanelFrmVentasDevolucionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 439, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mainPanelFrmVentasDevolucionesLayout.setVerticalGroup(
            mainPanelFrmVentasDevolucionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelFrmVentasDevolucionesLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelFrmVentasDevolucionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBuscarTicket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanelFrmVentasDevoluciones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanelFrmVentasDevoluciones, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void dtTicketImprimirMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dtTicketImprimirMouseClicked
        tablaTicketVenta = false;
    }//GEN-LAST:event_dtTicketImprimirMouseClicked

    private void btnDevolverProductoSelecionadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDevolverProductoSelecionadoActionPerformed
        devolverProducto();
    }//GEN-LAST:event_btnDevolverProductoSelecionadoActionPerformed

    private void btnCalcelarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalcelarVentaActionPerformed
        int opcion = JOptionPane.showConfirmDialog(null, "¿¿Esta usted seguro que desea cancelar esta venta?", "Cancelar venta", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opcion == 0) {
            cancelarVenta();
        }
    }//GEN-LAST:event_btnCalcelarVentaActionPerformed

    private void btnImprimirCopiaTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirCopiaTicketActionPerformed
        //Presentacion.FrmMain.notificar("Ticket impreso");
        try {
            extras.Impresora.imprimirTicket(view.FrmMain.getNombreEmpleado(IdempleadoU_Ventas_devoluciones),
                    view.FrmMain.confiEncabezadoTicket,
                    view.FrmMain.confiMensajeAgradecieminto,
                    view.FrmMain.nombreFolio,
                    idticket,
                    totalTicket,
                    view.FrmMain.IMPUESTO,
                    Double.parseDouble(pagoCon_ticket),
                    idcliente,
                    FrmMain.getNombreCliente(Integer.parseInt(idcliente))
            );
            view.FrmMain.beep = false;
            view.FrmMain.notificar("Ticket impreso");
        } catch (PrinterException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btnImprimirCopiaTicketActionPerformed

    private void txtBuscarTicketKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarTicketKeyReleased
        cargarTicket(txtBuscarTicket.getText());
    }//GEN-LAST:event_txtBuscarTicketKeyReleased

    private void dtVentaDiaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dtVentaDiaMouseClicked
        //int fila = dtVentaDia.rowAtPoint(evt.getPoint());
        tablaTicketVenta = true;
        ticket();
    }//GEN-LAST:event_dtVentaDiaMouseClicked

    private void dtVentaDiaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_dtVentaDiaPropertyChange
        //ticket();
    }//GEN-LAST:event_dtVentaDiaPropertyChange

    private void dtVentaDiaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dtVentaDiaKeyPressed
        /*int seleccionarFila = dtVentaDia.getSelectedRow();
        if (evt.getKeyCode() == KeyEvent.VK_KP_UP) {
            seleccionarFila++;
            dtVentaDia.changeSelection(seleccionarFila, 1, false, false);
            ticket();
        }
        if (evt.getKeyCode() == KeyEvent.VK_KP_DOWN) {
            seleccionarFila--;
            dtVentaDia.changeSelection(seleccionarFila, 1, false, false);
            ticket();
        }*/
    }//GEN-LAST:event_dtVentaDiaKeyPressed

    private void jDateChooser1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jDateChooser1PropertyChange
        cargarTicket(nombreEmpleado);
    }//GEN-LAST:event_jDateChooser1PropertyChange

    private void cbxEmpleadoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxEmpleadoItemStateChanged
        nombreEmpleado = cbxEmpleado.getItemAt(cbxEmpleado.getSelectedIndex());
        cargarTicket(nombreEmpleado);
    }//GEN-LAST:event_cbxEmpleadoItemStateChanged

    private void cbxEmpleadoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_cbxEmpleadoPropertyChange
        //cargarTicket(cbxEmpleado.getSelectedItem().toString());
        //String name = cbxEmpleado.getSelectedItem().toString();
        //FrmInicio.displayMessage(name);
    }//GEN-LAST:event_cbxEmpleadoPropertyChange

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                FrmVentasDevoluciones2 dialog = new FrmVentasDevoluciones2(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private principal.MaterialButton btnCalcelarVenta;
    private principal.MaterialButton btnDevolverProductoSelecionado;
    private principal.MaterialButton btnImprimirCopiaTicket;
    public static javax.swing.JComboBox<String> cbxEmpleado;
    private javax.swing.JTable dtTicketImprimir;
    private javax.swing.JTable dtVentaDia;
    private javax.swing.JButton jButton6;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblCajeroTicket;
    private javax.swing.JLabel lblClienteTicket;
    private javax.swing.JLabel lblFechaTicket;
    private javax.swing.JLabel lblFolioTicket;
    private javax.swing.JLabel lblPagoTicket;
    private javax.swing.JLabel lblTotalTicket;
    public static javax.swing.JPanel mainPanelFrmVentasDevoluciones;
    private javax.swing.JTextField txtBuscarTicket;
    // End of variables declaration//GEN-END:variables
}
