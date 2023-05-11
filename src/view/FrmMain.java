package view;

import com.sun.javafx.applet.Splash;
import controller.ControllerCategory;
import controller.ControllerClient;
import controller.ControllerConfiguration;
import controller.ControllerCorte;
import controller.ControllerEmploye;
import controller.ControllerInventory;
import controller.ControllerPayment;
import controller.ControllerProduct;
import controller.ControllerPromocion;
import controller.ControllerTicket;
import controller.fMeses;
import controller.fReporteIn;
import controller.fReporteMov;
import controller.fpagoclienteSalida;

import extras.AES;
import extras.EstiloTablaHeader;
import extras.EstiloTablaRenderer;
import static extras.Helper.playSound;
import static extras.Helper.ventana;
import extras.LongTask;
import extras.ModeloExcel;
import extras.MyColor;
import extras.MyFormater;
import extras.MyScrollbarUI;
import extras.Validacion;
import extras.Conexion;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import static java.awt.Frame.ICONIFIED;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterException;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import model.ModelCategory;
import model.ModelClient;
import model.ModelEmploye;
import model.ModelInventory;
import model.ModelPaymet;
import model.ModelProduct;
import model.ModelPromocion;
import view.clients.FrmAbono2;
import view.clients.FrmDetalleAbono2;
import view.products.FrmImportarProducto;
import view.sales.FrmBuscarProducto2;
import view.sales.FrmCobrar2;
import view.sales.FrmRegistroEntradasDinero;
import view.sales.FrmRegistroSalidasDinero2;
import view.sales.FrmVarios2;
import view.sales.FrmVentasDevoluciones2;


public class FrmMain extends javax.swing.JFrame implements Runnable {

    private int x;
    private int y;
    //VARIABLES GLOBALES EN FrmMain
    public static int IdempleadoU = 0;
    public static DefaultTableModel modelo;

    static JLabel msg;
    static int countTicket = 1;

    int Idcliente = 0;
    //VARIABLES GLOBALES PARA LAS FECHAS DE PRODUCTOS VENDIDO POR PERIODO
    String fecha_desde = "";
    String fecha_hasta = "";

    /*
        VARIABLES PARA EL HILO DE NITIFICAR
     */
    public final static int ONE_SECOND = 1000;
    public static Timer timer, timer2;
    private static LongTask task;
    static JPanel statusBar;

    public static boolean beep = true;
    //VARIABLES GLOBALES ARBOL
    public static DefaultMutableTreeNode carpetaRaiz = null;
    public static DefaultTreeModel modeloTree = null;
    public static ArrayList<String> meses, mesesTodo, fechas;

    //VARIABLES PARA IMPORTAR Y EXPORTAR
    static JFileChooser selecArchivo2 = new JFileChooser();
    static FileNameExtensionFilter F1 = new FileNameExtensionFilter("Excel (*.xls)", "xls");
    static FileNameExtensionFilter F2 = new FileNameExtensionFilter("Excel (*.xlsx)", "xlsx");
    static ModeloExcel modeloE = new ModeloExcel();
    static File archivo = null;

    //VARIABLE GLOBAL PARA RECARGAR ELIMINAR TICKET EN EDO DE CUENTA CLIENTE
    public static String IDCLIENTE_ELIMINAR_TIKET = "";

    //VARIABLES GLOBALES CONFIGURACION
    public static String nombreFolio = "";
    public static String rutaLogo = "";
    public static String confiEncabezadoTicket = "";
    public static String confiMensajeAgradecieminto = "";
    public static String activvadoImpuestos = "";
    public static String nombreImpresora = "";
    public static int IMPUESTO = 0;
    //VARIABLE GLOBAL SI/NO IMPRIMIR CORTE
    static boolean IMPRIMIRCORTE = false;

    private ArrayList<JTable> listOfTables;
    private ArrayList<JScrollPane> listOfScrollPane;

    public FrmMain() {
        initComponents();
        this.meses = new ArrayList<String>();
        this.mesesTodo = new ArrayList<String>();
        this.fechas = new ArrayList<String>();
        this.setIconImage(new ImageIcon(getClass().getResource("/Imagenes/logo_muebleria.jpg")).getImage());
        this.setExtendedState(this.getExtendedState() | FrmMain.MAXIMIZED_BOTH);
        this.setPreferredSize(null);
        this.mostrar("");
        this.mostrarCat("");
        this.mostrarPro("");
        this.mostrarEm("");
        this.cargarCategoria();
        this.mostrarEmpleado();
        this.mostrarTicket("", "");
        this.mostrarPromocion("");
        this.mostrarReporteInv("", 0);
        this.mostrarReporteMov("", "", 0);
        this.mostrarBajosInventario();
        this.mostrarTicketCredito();
        this.inhabilitar();
        this.inhabilitarCat();
        this.inhabilitarPro();
        this.inhabilitarEm();

        this.cargarEstado();
        this.cargarModeloVenta();
        this.inicializarSecciones();
        //cargar configuracion aqui
        listarImpresoras();
        recuperarConfi();

        this.titleSection.setText("VENTA DE PRODUCTOS - Ticket " + countTicket);
        jTabbedPane1.setTitleAt(countTicket - 1, "Ticket - " + countTicket);

        periodoFecha.setVisible(false);
        //btnCerrar.setVisible(false);
        lblSeleccionaUnCliente.setVisible(false);
        txtEnterProducto.requestFocus();

        jTree1.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
                TreePath paths = jTree1.getSelectionPath();
                Object rutas[] = paths.getPath();
                int longitud = rutas.length;
                if (longitud == 3) {
                    //PARÉMETROS
                    //FECHA
                    //IDCLIENTE
                    String fecha = "" + rutas[rutas.length - 1];
                    //mostrarTicketCredito(Idcliente, fecha);
                    DefaultTableModel modelo;
                    ControllerTicket func = new ControllerTicket();

                    modelo = func.ticket(Idcliente, fecha, conIdTicket(fecha));
                    dtClieCredito.setModel(modelo);
                    dtClieCredito.getColumnModel().getColumn(0).setMaxWidth(1);
                    dtClieCredito.getColumnModel().getColumn(0).setMinWidth(1);
                    dtClieCredito.getColumnModel().getColumn(0).setPreferredWidth(1);

                    dtClieCredito.getColumnModel().getColumn(5).setMaxWidth(1);
                    dtClieCredito.getColumnModel().getColumn(5).setMinWidth(1);
                    dtClieCredito.getColumnModel().getColumn(5).setPreferredWidth(1);

                    dtClieCredito.getColumnModel().getColumn(6).setMaxWidth(1);
                    dtClieCredito.getColumnModel().getColumn(6).setMinWidth(1);
                    dtClieCredito.getColumnModel().getColumn(6).setPreferredWidth(1);

                    //AL SELECCIONAR UN TICKET AUTOMATICAMENTE SE SELECCIONA
                    //UN REGISTRO DE LA TABLA
                    if (dtClieCredito.getRowCount() != 0) {
                        dtClieCredito.changeSelection(0, 0, false, false);
                    }

                    lblTotalImporteCredito.setText(MyFormater.formato(func.totalImporteTicket));
                    lblImpuestos.setText(MyFormater.formato(func.totalImpuestos));
                    IDCLIENTE_ELIMINAR_TIKET = "" + fechaTicketConIdCliente(fecha);
                    //JOptionPane.showMessageDialog(null,"IDCLIENTE_ELIMINAR_TIKET: "+IDCLIENTE_ELIMINAR_TIKET);
                    //jTextArea1.setText("IDCLEINTE: " + Idcliente + "\nFECHA:" + rutas[rutas.length - 1] + "\nIDTICKET:" + conIdTicket(fecha));
                }
            }
        });

        //lblLogotipo
        lblLogotipo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 1) {

                }
                if (e.getClickCount() == 2) {
                    setExtendedState(getExtendedState() | FrmMain.MAXIMIZED_BOTH);
                }
            }

        }
        );
        clickOnKey(btnVentasH, "F1 Ventas", KeyEvent.VK_F1);
        clickOnKey(btnClientesH, "F2 Clientes", KeyEvent.VK_F2);
        clickOnKey(btnProductosH, "F3 Productos", KeyEvent.VK_F3);
        clickOnKey(btnInventarioH, "F4 Inventario", KeyEvent.VK_F4);
        clickOnKey(btnBuscar, "F10 Buscar", KeyEvent.VK_F10);
        clickOnKey(btnMayoreo, "F11 Mayoreo", KeyEvent.VK_F11);
        clickOnKey(btnEntradas, "F7 Entradas", KeyEvent.VK_F7);
        clickOnKey(btnSalidas, "F8 Salidas", KeyEvent.VK_F8);
        clickOnKey(btnBorrarArt, "DEL Borrar Art.", KeyEvent.VK_DELETE);
        clickOnKey(btnNuevoTicket, "F6 Nuevo Ticket", KeyEvent.VK_F6);
        clickOnKey(btnCobrarVenta, "F12 - Cobrar", KeyEvent.VK_F12);

        //clickOnKey(btnAgregarProd, "Enter - Agregar producto", KeyEvent.VK_ENTER);
        //jLabel1.setVisible(false);
        //txtbuscarClientes.setVisible(false);
        dtInvReporte.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 1) {

                }
                if (e.getClickCount() == 2) {
                    abrirModificarProductor();
                }
            }

        }
        );

        initTables();

        org.jdesktop.swingx.border.DropShadowBorder dropShadowBorder1 = new org.jdesktop.swingx.border.DropShadowBorder();
        dropShadowBorder1.setShowBottomShadow(true);
        dropShadowBorder1.setShowRightShadow(true);
        jPanel12.setBorder(dropShadowBorder1);

        //LABEL EN LA SECCION DE INVENTARIO / AJUSTE
        lblIdProductoAjuste.setVisible(false);

        //BOTONES EN EL ESTADO DE CUENTA DE CLIENTES
        btnAbonarCliente.setEnabled(false);
        btnLiquidarAdeudo.setEnabled(false);

        //RADIO BUTTON EN APARTADO IMPUESTOS
        txtImpuesto.setVisible(false);

    }

    private void initTables() {
        listOfTables = new ArrayList<>(Arrays.asList(dtTicket, dtClieListado, dtClieCredito, dtProListado, dtProVentasPedido, dtProPromocion, dtProCategoria, dtInvBajos, dtInvReporte, dtInvHistorialMov, dtEmListado, dtVentasCategoria));
        listOfScrollPane = new ArrayList<>(Arrays.asList(jScrollPaneVentas, jScrollPaneListadoCliente, jScrollPaneEstadoCuenta, jScrollPaneListadoProducto, jScrollPaneVentasVendido, jScrollPanePromocion, jScrollPaneCategoria, jScrollPaneBajosInventario, jScrollPaneReporteInventario, jScrollPaneHistorialMov, jScrollPaneCajero, jScrollPaneVentasCategoria));

        for (int i = 0; i < listOfScrollPane.size(); i++) {
            listOfScrollPane.get(i).getViewport().setBackground(new java.awt.Color(255, 255, 255));
            listOfScrollPane.get(i).getVerticalScrollBar().setUI(new MyScrollbarUI());
            listOfScrollPane.get(i).getHorizontalScrollBar().setUI(new MyScrollbarUI());

            listOfTables.get(i).getTableHeader().setDefaultRenderer(new EstiloTablaHeader());
            listOfTables.get(i).setDefaultRenderer(Object.class, new EstiloTablaRenderer());
        }
    }

    private void listarImpresoras() {
        PrintService[] ps = PrintServiceLookup.lookupPrintServices(null, null);
        for (int i = 0; i < ps.length; i++) {
            comboImpresora.addItem(ps[i].getName());
        }
    }

    private void recuperarConfi() {
        String sSQL = "SELECT * FROM configuracion WHERE 1";
        String[] str_ticket = null;
        String[] str_impuesto = null;
        try {

            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            while (rs.next()) {
                nombreFolio = rs.getString("folio");
                //rutaLogo = rs.getString("ruta_logo");
                str_ticket = rs.getString("str_ticket").split("/");
                str_impuesto = rs.getString("impuesto").split("/");
                nombreImpresora = rs.getString("nombre_impresora");
            }

            //RECUPERANDO ENCABEZADO DEL TICKET
            txtEmpresaNombre.setText(str_ticket[0]);
            txtEmpresaCalle.setText(str_ticket[1]);
            txtEmpresaLocal.setText(str_ticket[2]);
            txtEmpresaSucursal.setText(str_ticket[3]);
            txtEmpresaTelefono.setText(str_ticket[4]);
            confiEncabezadoTicket = str_ticket[0] + "\n"
                    + str_ticket[1] + "\n"
                    + str_ticket[2] + "\n"
                    + str_ticket[3] + "\n"
                    + str_ticket[4];

            //RECUPERANDO MENSAJE DE AGRADECIMIENTO TICKET
            confiMensajeAgradecieminto = str_ticket[5];
            txtEmpresaAgradecimiento.setText(confiMensajeAgradecieminto);

            //RECUPERANDO INFO IMPUESTO
            activvadoImpuestos = str_impuesto[0];
            IMPUESTO = Integer.parseInt(str_impuesto[1]);

            if (activvadoImpuestos.equals("TRUE")) {
                rbImpuesto.setSelected(true);
                txtImpuesto.setVisible(true);
                txtImpuesto.setText("" + IMPUESTO);
            } else {
                rbImpuesto.setSelected(false);
                txtImpuesto.setVisible(false);
            }

            //RECUPERANDO NOMBRE FOLIO
            txtFolioConfiguracion.setText(nombreFolio);

            //RECUPERANDO RUTA LOGOTIPO
            //txtRutaLogotipo.setText(rutaLogo);
            cambiarLogo(); //rutaLogo

            //RECUPERANDO NOMBRE IPRESORA
            comboImpresora.setSelectedItem(nombreImpresora);

            System.out.println("NOMBRE FOLIO: " + nombreFolio);
            System.out.println("RUTA LOGO: " + rutaLogo);
            System.out.println("INFO IMPUESTO: " + activvadoImpuestos + "/" + IMPUESTO);
            System.out.println("ENCABEZADO TICKET: " + confiEncabezadoTicket);
            System.out.println("MENSAJE DE AGRADECIMIENTO: " + txtEmpresaAgradecimiento);
            System.out.println("NOMBRE IMPRESORA: " + nombreImpresora);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al recuper configuracion: " + e);
        }
        /*try {
            Object[] datos = Confi.cargarConfi();
            String strTicket_Encabezado = datos[2].toString();
            String strTicket_Mensaje = datos[3].toString();
            String strImpuestosActivado = datos[4].toString();
            String strImpresoraNombre = datos[5].toString();

            String[] infoEncabezadoTicket = strTicket_Encabezado.split("/");

            //RECUPERANDO ETIQUETA FOLIO
            txtFolioConfiguracion.setText(strFolio);
            nombreFolio = strFolio;

            //RECUPERANDO RUTA LOGO
            txtRutaLogotipo.setText(strRutaLogo);
            rutaLogo = strRutaLogo;
            cambiarLogo(strRutaLogo);

            //REUPERANDO ENCABEZADO DEL TICKET A IMPRMIR
            txtEmpresaNombre.setText(infoEncabezadoTicket[0]);
            txtEmpresaCalle.setText(infoEncabezadoTicket[1]);
            txtEmpresaLocal.setText(infoEncabezadoTicket[2]);
            txtEmpresaSucursal.setText(infoEncabezadoTicket[3]);
            txtEmpresaTelefono.setText(infoEncabezadoTicket[4]);
            confiEncabezadoTicket = infoEncabezadoTicket[0] + "\n"
                    + infoEncabezadoTicket[1] + "\n"
                    + infoEncabezadoTicket[2] + "\n"
                    + infoEncabezadoTicket[3] + "\n"
                    + infoEncabezadoTicket[4];

            //RECUPERANDO MENSAJE DEL TICKET
            txtEmpresaAgradecimiento.setText(strTicket_Mensaje);
            confiMensajeAgradecieminto = strTicket_Mensaje;

            //RECUPERANDO SI APLICA DESCUENTOS
            String[] infoImpuesto = strImpuestosActivado.split("/");
            if (infoImpuesto[0].equals("TRUE")) {
                activvadoImpuestos = infoImpuesto[0];
                rbImpuesto.setSelected(true);
                txtImpuesto.setVisible(true);
                txtImpuesto.setText("" + Integer.parseInt(infoImpuesto[1]));
                IMPUESTO = Integer.parseInt(infoImpuesto[1]);
            } else {
                rbImpuesto.setSelected(false);
                txtImpuesto.setVisible(false);
            }

            //RECUPERANDO LA IMPRESORA SELECCIONADA
            comboImpresora.setSelectedItem(strImpresoraNombre);
            nombreImpresora = strImpresoraNombre;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al recuper configuracion: " + ex);
        }
         */

    }

    public static Integer conIdTicket(String fecha) {
        String sSQL = "SELECT "
                + "t.idticket "
                + "FROM ticket t "
                + "WHERE t.fecha = '" + fecha + "'";

        String idticket = "";

        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            while (rs.next()) {
                idticket = rs.getString("idticket");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en el metodo conIdTicket: " + e);
        }
        return Integer.parseInt(idticket);
    }

    public static Integer fechaTicketConIdCliente(String fecha) {
        String sSQL = "SELECT c.idcliente \n"
                + "FROM ticket t \n"
                + "INNER JOIN cliente c \n"
                + "ON t.idcliente = c.idcliente\n"
                + "WHERE t.fecha = '" + fecha + "'\n"
                + "GROUP BY t.idcliente";

        String idcliente = "";

        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            while (rs.next()) {
                idcliente = rs.getString("idcliente");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en fechaTicketConIdCliente: " + e);
        }
        return Integer.parseInt(idcliente);
    }

    public static void clickOnKey(
            final AbstractButton button, String actionName, int key) {
        button.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(key, 0), actionName);
        //.put(KeyStroke.getKeyStroke(key, 0), actionName);

        button.getActionMap().put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button.doClick();
            }
        });
    }

    //Metodo Importar
    public static void importarTabla(JTable table) {
        AgregarFiltro2();
        if (selecArchivo2.showDialog(null, "Seleccionar archivo") == JFileChooser.APPROVE_OPTION) {
            archivo = selecArchivo2.getSelectedFile();
            if (archivo.getName().endsWith("xls") || archivo.getName().endsWith("xlsx")) {
                JOptionPane.showMessageDialog(null,
                        modeloE.Importar(archivo, table) + "\n Formato ." + archivo.getName().substring(archivo.getName().lastIndexOf(".") + 1),
                        "IMPORTAR EXCEL", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Elija un formato valido.");
            }
        }
    }

    public static void importarProductos() {
        AgregarFiltro2();
        if (selecArchivo2.showDialog(null, "Seleccionar archivo") == JFileChooser.APPROVE_OPTION) {
            archivo = selecArchivo2.getSelectedFile();
            if (archivo.getName().endsWith("xls") || archivo.getName().endsWith("xlsx")) {
                //jPanel1.setCursor(Cursor.getPredefinedCursor(Cursor.CURSOR_WAIT));
                JOptionPane.showMessageDialog(null,
                        modeloE.ImportarP(archivo) + "\n Formato ." + archivo.getName().substring(archivo.getName().lastIndexOf(".") + 1),
                        "IMPORTAR EXCEL", JOptionPane.INFORMATION_MESSAGE);
                //jPanel1.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            } else {
                JOptionPane.showMessageDialog(null, "Elija un formato valido.");
            }
        }
    }

    //Evento Exportar
    public static void exportarTabla(JTable tabla) {
        AgregarFiltro2();
        if (selecArchivo2.showDialog(null, "Exportar") == JFileChooser.APPROVE_OPTION) {
            String a = selecArchivo2.getSelectedFile().toString().concat(".xls");
            String b = selecArchivo2.getSelectedFile().toString().concat(".xlsx");
            File xls = new File(a);
            File xlsx = new File(b);
            String descripcion = selecArchivo2.getFileFilter().getDescription();
            if (descripcion.equals("Excel (*.xls)")) {
                JOptionPane.showMessageDialog(null, modeloE.Exportar(xls, tabla)); //EXTENCION .XLS PRIMERO
            } else {
                JOptionPane.showMessageDialog(null, modeloE.Exportar(xlsx, tabla)); //EXTENCION .XLSX SEGUNDO
            }

        }

    }

    // Metodo Agregar filtro 2
    public static void AgregarFiltro2() {
        selecArchivo2.setFileFilter(F1);
        selecArchivo2.setFileFilter(F2);
    }

    public static void createNodes(ArrayList<String> nombreMeses, ArrayList<String> nombreMeses2, ArrayList<String> fechas) {
        if (nombreMeses.size() == 0) {
            carpetaRaiz = new DefaultMutableTreeNode("SIN FECHAS");
        } else {
            carpetaRaiz = new DefaultMutableTreeNode("FECHAS");
        }
        modeloTree = new DefaultTreeModel(carpetaRaiz);
        jTree1.setModel(modeloTree);
        for (int i = 0; i < nombreMeses.size(); i++) {
            DefaultMutableTreeNode mes = new DefaultMutableTreeNode(nombreMes(nombreMeses.get(i)));
            carpetaRaiz.add(mes);

            for (int j = 0; j < nombreMeses2.size(); j++) {
                if (nombreMeses.get(i).equals(nombreMeses2.get(j))) { // nombreMeses.contains(nombreMeses2.get(j))
                    DefaultMutableTreeNode fecha = new DefaultMutableTreeNode(fechas.get(j));
                    mes.add(fecha);
                }
            }

        }

        jTree1.expandRow(0);
        //jTree1.setSelectionRow(1);
    }

    public static void cargarMeses(String buscar) {
        try {
            fMeses func = new fMeses();
            meses = func.cargarMeses(buscar);
            mesesTodo = func.todosMeses(buscar);
            fechas = func.obtenerfechas(buscar);
            //agregarNodos(meses);
            createNodes(meses, mesesTodo, fechas);
        } catch (Exception e) {
            //JOptionPane.showMessageDialog(rootPane, e);
        }

    }

    public static String nombreMes(String mes) {
        switch (mes) {
            case "1":
                mes = "ENERO";
                break;
            case "2":
                mes = "FEBRERO";
                break;
            case "3":
                mes = "MARZO";
                break;
            case "4":
                mes = "ABRIL";
                break;
            case "5":
                mes = "MAYO";
                break;
            case "6":
                mes = "JUNIO";
                break;
            case "7":
                mes = "JULIO";
                break;
            case "8":
                mes = "AGOSTO";
                break;
            case "9":
                mes = "SEPTIEMBRE";
                break;
            case "10":
                mes = "OCTUBRE";
                break;
            case "11":
                mes = "NOVIEMBRE";
                break;
            case "12":
                mes = "DICIEMBRE";
                break;
            default:
                break;
        }
        return mes;
    }

    private void inicializarSecciones() {
        btnNuevoTicket.setVisible(false);
        btnEliminarTicket.setVisible(false);
        //CAPAS - SECCIÓN PRINCIPAL
        this.VentasPanel.setVisible(true);
        this.ClientesPanel.setVisible(false);
        this.ProductosPanel.setVisible(false);
        this.InventarioPanel.setVisible(false);
        this.ConfiguracionPanel.setVisible(false);
        this.CortePanel.setVisible(false);

        //CAPAS - SECCIÓN INVENTARIO
        agregarInvPanel.setVisible(true);
        ajusteInvPanel.setVisible(false);
        bajoInvPanel.setVisible(false);
        reporteInvPanel.setVisible(false);
        reporteMovimientosPanel.setVisible(false);

        //CAPAS - SECCIÓN CONFIGURACIONES
        inicioConfiguracionesPanel.setVisible(true);
        opcionesHabilitadasPanel.setVisible(false);
        cajerosPanel.setVisible(false);
        baseDatosPanel.setVisible(false);
        folioTicketPanel.setVisible(false);
        logotipoPanel.setVisible(false);
        ticketPanel.setVisible(false);
        impuestosPanel.setVisible(false);
        impresoraTicketPanel.setVisible(false);
    }

    private void cargarModeloVenta() {
        String data[][] = {};
        String cabeza[] = {"IDPRODUCTO", "Nº SERIE", "DESCRIPCION DEL PRODUCTO", "PRECIO VENTA", "CANTIDAD", "IMPORTE", "EXISTENCIA"};
        boolean[] tableColums = {false, false, false, false, false, false, false};

        modelo = new DefaultTableModel(data, cabeza) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return tableColums[column];
            }

        };

        dtTicket.setModel(modelo);

        dtTicket.getColumnModel()
                .getColumn(0).setMaxWidth(1);
        dtTicket.getColumnModel()
                .getColumn(0).setMinWidth(1);
        dtTicket.getColumnModel()
                .getColumn(0).setPreferredWidth(1);

        btnVentasH.setEnabled(
                false);
        btnVentasH.setForeground(Color.GRAY);
    }

    private void cargarEstado() {
        //Three panels that are to added to the JFrame
        statusBar = new JPanel();
        JLabel welcomedate;
        JTabbedPane panel = new JTabbedPane();

        //Creating the StatusBar.
        //setLayout(new BorderLayout());//frame layout
        msg = new JLabel(" " + "Punto de venta...Teclee el Código del Producto.", JLabel.LEFT);
        msg.setForeground(Color.black);
        msg.setToolTipText("Muebllería Dinora");

        welcomedate = new JLabel();
        //welcomedate.setOpaque(true);//to set the color for jlabel
        //welcomedate.setBackground(Color.black);
        welcomedate.setForeground(Color.BLACK);

        statusBar.setLayout(new BorderLayout());
        statusBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        statusBar.setBackground(Color.WHITE);
        statusBar.add(msg, BorderLayout.WEST);
        statusBar.add(welcomedate, BorderLayout.EAST);
        mainPanel.add(statusBar);

        //display date time to status bar
        Timer timee = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Date now = new Date();
                String ss = DateFormat.getDateTimeInstance().format(now);
                welcomedate.setText(ss);
                welcomedate.setToolTipText("Bienvenido, hoy es " + ss);
            }
        });
        timee.start();
    }

    public Date sumarRestarDiasFecha(Date fecha, int dias) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha); // Configuramos la fecha que se recibe
        calendar.add(Calendar.DAY_OF_YEAR, dias);  // numero de días a añadir, o restar en caso de días<0
        return calendar.getTime(); // Devuelve el objeto Date con los nuevos días añadidos

    }

    public static class TimerListener implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            if (task.done()) {
                if (notificacion) {
                    pa.setVisible(false);
                    pa = null;
                    msgticket = null;
                    notificacion = false;
                    timer.stop();
                }

                if (notificacion2) {
                    pa2.setVisible(false);
                    pa2 = null;
                    msgticket2 = null;
                    notificacion2 = false;
                    timer.stop();
                }

                //CAMBIAR EL COLOR Y TEXTO AL ESTADO NORMAL
                if (notiDevo) {
                    obj.dispose();
                    notiDevo = false;
                    timer.stop();
                } else {
                    defaultMessage();
                    statusBar.setBackground(Color.WHITE);
                    timer.stop();
                }

            }
        }
    }

    public static void displayMessage(String ms) {
        msg.setText(ms);
        statusBar.setBackground(Color.YELLOW);
        task = new LongTask(100);
        //create a timer and a task
        timer = new Timer(ONE_SECOND, new TimerListener());

        task.go();
        timer.start();
    }

    public static void defaultMessage() {
        msg.setText("Punto de venta...Teclee el Código del Producto.");
    }

    private static Thread tiempo = null;
    static JPanel pa = null, pa2 = null;
    static JLabel msgticket = null, msgticket2 = null;
    static boolean notificacion = false, notificacion2 = false;

    public static void notificar(String msg) {
        if (notificacion == false) {
            pa = new JPanel(new BorderLayout());
            msgticket = new JLabel(msg);
            pa.setBounds(0, 0, 950, 100);
            pa.setBackground(new Color(135, 65, 155)); //color verde 102, 255, 102

            Dimension desktopSize = escritorio.getSize();
            Dimension FrameSize = pa.getSize();
            pa.setLocation((desktopSize.width - FrameSize.width) / 2, (desktopSize.height - FrameSize.height) / 2);

            //pa.setLocation(escritorio.getWidth() / 6, escritorio.getHeight() / 3);
            msgticket.setBounds(pa.getWidth() / 2, pa.getHeight() / 2, 200, 80);
            msgticket.setForeground(Color.WHITE);
            msgticket.setFont(new Font("Arial", Font.ITALIC, 40));
            msgticket.setSize(250, 80);
            msgticket.setHorizontalAlignment(JLabel.CENTER);
            msgticket.setVerticalAlignment(JLabel.CENTER);

            pa.add(msgticket, BorderLayout.CENTER);

            pa.setVisible(true);
            escritorio.add(pa);
            escritorio.moveToFront(pa);
            notificacion = true;

            task = new LongTask(100);
            //create a timer and a task
            timer = new Timer(ONE_SECOND, new TimerListener());

            task.go();
            timer.start();

        }

        //AWTUtilities.setWindowOpaque(this, false);
        /*tiempo = new Thread(this);
        tiempo.start();
         */
        if (beep) {
            playSound(new File("src/beep/beep-02.wav"));
        }

    }

    public static void notificar2(String msg) {
        if (notificacion == false) {
            pa = new JPanel(new BorderLayout());
            msgticket = new JLabel(msg);
            pa.setBounds(0, 0, 500, 100);
            pa.setBackground(new Color(135, 65, 155)); //color verde 102, 255, 102

            Dimension desktopSize = FrmVentasDevoluciones2.mainPanelFrmVentasDevoluciones.getSize();
            Dimension FrameSize = pa.getSize();
            pa.setLocation((desktopSize.width - FrameSize.width) / 2, (desktopSize.height - FrameSize.height) / 2);

            //pa.setLocation(escritorio.getWidth() / 6, escritorio.getHeight() / 3);
            msgticket.setBounds(pa.getWidth() / 2, pa.getHeight() / 2, 200, 80);
            msgticket.setForeground(Color.WHITE);
            msgticket.setFont(new Font("Arial", Font.ITALIC, 40));
            msgticket.setSize(250, 80);
            msgticket.setHorizontalAlignment(JLabel.CENTER);
            msgticket.setVerticalAlignment(JLabel.CENTER);

            pa.add(msgticket, BorderLayout.CENTER);

            pa.setVisible(true);
            FrmVentasDevoluciones2.mainPanelFrmVentasDevoluciones.add(pa);
            //FrmVentasDevoluciones2.mainPanelFrmVentasDevoluciones.//moveToFront(pa);
            notificacion = true;

            task = new LongTask(100);
            //create a timer and a task
            timer = new Timer(ONE_SECOND, new TimerListener());

            task.go();
            timer.start();

        }

        //AWTUtilities.setWindowOpaque(this, false);
        /*tiempo = new Thread(this);
        tiempo.start();
         */
        if (beep) {
            playSound(new File("src/beep/beep-02.wav"));
        }

    }

    public static void notificarNotFounf(String msg) {
        if (notificacion2 == false) {
            pa2 = new JPanel(new BorderLayout());
            msgticket2 = new JLabel(msg);
            pa2.setBounds(0, 0, 550, 250);
            pa2.setBackground(new Color(153, 102, 0));

            Dimension desktopSize = escritorio.getSize();
            Dimension FrameSize = pa2.getSize();
            pa2.setLocation((desktopSize.width - FrameSize.width) / 2, (desktopSize.height - FrameSize.height) / 2);

            //pa.setLocation(escritorio.getWidth() / 6, escritorio.getHeight() / 3);
            msgticket2.setBounds(pa2.getWidth() / 2, pa2.getHeight() / 2, 200, 80);
            msgticket2.setForeground(Color.WHITE);
            msgticket2.setFont(new Font("Arial", Font.ITALIC, 40));
            msgticket2.setSize(250, 80);
            msgticket2.setHorizontalAlignment(JLabel.CENTER);
            msgticket2.setVerticalAlignment(JLabel.CENTER);

            pa2.add(msgticket2, BorderLayout.CENTER);

            pa2.setVisible(true);
            escritorio.add(pa2);
            escritorio.moveToFront(pa2);
            notificacion2 = true;

            task = new LongTask(100);
            //create a timer and a task
            timer = new Timer(ONE_SECOND, new TimerListener());

            task.go();
            timer.start();

        }

        if (beep) {
            playSound(new File("src/beep/beep-02.wav"));
        }

    }

    public int numberTicket() {
        countTicket++;
        this.titleSection.setText("VENTA DE PRODUCTOS - Ticket " + countTicket);
        return countTicket;
    }

    static frmNotificacion obj = null;
    static boolean notiDevo = false;

    public static void notiDevo(String message) {
        if (notiDevo == false) {
            obj = new frmNotificacion();
            obj.msg = message;
            escritorio.add(obj);
            escritorio.moveToFront(obj);
            Dimension desktopSize = escritorio.getSize();
            Dimension FrameSize = obj.getSize();
            obj.setLocation((desktopSize.width - FrameSize.width) / 2, (desktopSize.height - FrameSize.height) / 2);
            obj.show();
            task = new LongTask(100);
            //create a timer and a task
            timer = new Timer(ONE_SECOND, new TimerListener());

            task.go();
            timer.start();
            notiDevo = true;
        }
    }

    private void activarV(String vista) {
        //PANELES
        this.VentasPanel.setVisible(false);
        this.ClientesPanel.setVisible(false);
        this.ProductosPanel.setVisible(false);
        this.InventarioPanel.setVisible(false);
        this.ConfiguracionPanel.setVisible(false);
        this.CortePanel.setVisible(false);

        //BOTONES
        btnVentasH.setEnabled(true);
        btnVentasH.setForeground(Color.WHITE);
        btnClientesH.setEnabled(true);
        btnClientesH.setForeground(Color.WHITE);
        btnProductosH.setEnabled(true);
        btnProductosH.setForeground(Color.WHITE);
        btnInventarioH.setEnabled(true);
        btnInventarioH.setForeground(Color.WHITE);
        btnConfiguracionH.setEnabled(true);
        btnConfiguracionH.setForeground(Color.WHITE);
        btnCorte.setEnabled(true);
        btnCorte.setForeground(Color.WHITE);

        guardarInfoConfiEmpresa();

        switch (vista) {
            case "ventas":
                VentasPanel.setVisible(true);
                btnVentasH.setEnabled(false);
                btnVentasH.setForeground(Color.BLACK);
                break;
            case "clientes":
                this.ClientesPanel.setVisible(true);
                btnClientesH.setEnabled(false);
                btnClientesH.setForeground(Color.BLACK);
                jTabbedPane4.setSelectedIndex(0);
                break;
            case "productos":
                this.ProductosPanel.setVisible(true);
                btnProductosH.setEnabled(false);
                btnProductosH.setForeground(Color.BLACK);
                jTabbedPane2.setSelectedIndex(0);
                break;
            case "inventario":
                this.InventarioPanel.setVisible(true);
                btnInventarioH.setEnabled(false);
                btnInventarioH.setForeground(Color.BLACK);
                activarI("agregar");
                break;
            case "configuracion":
                this.ConfiguracionPanel.setVisible(true);
                btnConfiguracionH.setEnabled(false);
                btnConfiguracionH.setForeground(Color.BLACK);
                activarC("inicio");
                break;
            case "corte":
                this.CortePanel.setVisible(true);
                btnCorte.setEnabled(false);
                btnCorte.setForeground(Color.BLACK);
                break;
            default:
                break;
        }
    }

    private void activarI(String vista) {
        agregarInvPanel.setVisible(false);
        ajusteInvPanel.setVisible(false);
        bajoInvPanel.setVisible(false);
        reporteInvPanel.setVisible(false);
        reporteMovimientosPanel.setVisible(false);

        this.btnAgregarInv.setEnabled(true);
        this.btnAjustesInv.setEnabled(true);
        this.btnProdInventarioBajos.setEnabled(true);
        this.btnReporteInv.setEnabled(true);
        this.btnReporteMovInv.setEnabled(true);

        this.btnAgregarInv.setOpaque(false);
        this.btnAjustesInv.setOpaque(false);
        this.btnProdInventarioBajos.setOpaque(false);
        this.btnReporteInv.setOpaque(false);
        this.btnReporteMovInv.setOpaque(false);

        this.btnAgregarInv.setBackground(new Color(52, 50, 51));
        this.btnAjustesInv.setBackground(new Color(52, 50, 51));
        this.btnProdInventarioBajos.setBackground(new Color(52, 50, 51));
        this.btnReporteInv.setBackground(new Color(52, 50, 51));
        this.btnReporteMovInv.setBackground(new Color(52, 50, 51));
        switch (vista) {
            case "agregar":
                this.agregarInvPanel.setVisible(true);
                this.btnAgregarInv.setEnabled(false);
                this.btnAgregarInv.setOpaque(true);
                this.btnAgregarInv.setBackground(new Color(102, 102, 102));
                break;
            case "ajuste":
                this.ajusteInvPanel.setVisible(true);
                this.btnAjustesInv.setEnabled(false);
                this.btnAjustesInv.setOpaque(true);
                this.btnAjustesInv.setBackground(new Color(102, 102, 102));
                break;
            case "bajo":
                this.bajoInvPanel.setVisible(true);
                this.btnProdInventarioBajos.setEnabled(false);
                this.btnProdInventarioBajos.setOpaque(true);
                this.btnProdInventarioBajos.setBackground(new Color(102, 102, 102));
                break;
            case "rInventario":
                this.reporteInvPanel.setVisible(true);
                this.btnReporteInv.setEnabled(false);
                this.btnReporteInv.setOpaque(true);
                this.btnReporteInv.setBackground(new Color(102, 102, 102));
                break;
            case "rMovimientos":
                this.reporteMovimientosPanel.setVisible(true);
                this.btnReporteMovInv.setEnabled(false);
                this.btnReporteMovInv.setOpaque(true);
                this.btnReporteMovInv.setBackground(new Color(102, 102, 102));
                break;
            default:
                break;
        }
    }

    private void activarC(String vista) {
        inicioConfiguracionesPanel.setVisible(false);
        opcionesHabilitadasPanel.setVisible(false);
        cajerosPanel.setVisible(false);
        baseDatosPanel.setVisible(false);
        folioTicketPanel.setVisible(false);
        logotipoPanel.setVisible(false);
        ticketPanel.setVisible(false);
        impuestosPanel.setVisible(false);
        impresoraTicketPanel.setVisible(false);
        switch (vista) {
            case "inicio":
                this.inicioConfiguracionesPanel.setVisible(true);
                break;
            case "opciones":
                this.opcionesHabilitadasPanel.setVisible(true);
                break;
            case "cajeros":
                this.cajerosPanel.setVisible(true);
                break;
            case "BD":
                this.baseDatosPanel.setVisible(true);
                break;
            case "folio":
                this.folioTicketPanel.setVisible(true);
                break;
            case "logotipo":
                this.logotipoPanel.setVisible(true);
                break;
            case "ticket":
                this.ticketPanel.setVisible(true);
                break;
            case "impuestos":
                this.impuestosPanel.setVisible(true);
                break;
            case "impresoraTicket":
                this.impresoraTicketPanel.setVisible(true);
                break;
            default:
                break;
        }
    }

    //=====================================================================================
    public static String accion = "guardar";

    public static void ocultar_columnas() {
        dtClieListado.getColumnModel().getColumn(0).setMaxWidth(1);
        dtClieListado.getColumnModel().getColumn(0).setMinWidth(1);
        dtClieListado.getColumnModel().getColumn(0).setPreferredWidth(1);
    }

    public static void ocultar_columnasCat() {
        dtProCategoria.getColumnModel().getColumn(0).setMaxWidth(0);
        dtProCategoria.getColumnModel().getColumn(0).setMinWidth(0);
        dtProCategoria.getColumnModel().getColumn(0).setPreferredWidth(0);
    }

    static void ocultar_columnasPro() {
        dtProListado.getColumnModel().getColumn(0).setMaxWidth(0);
        dtProListado.getColumnModel().getColumn(0).setMinWidth(0);
        dtProListado.getColumnModel().getColumn(0).setPreferredWidth(0);
    }

    void ocultar_columnasEm() {
        for (int i = 0; i < dtEmListado.getColumnCount(); i++) {
            if (i != 1) {
                dtEmListado.getColumnModel().getColumn(i).setMaxWidth(0);
                dtEmListado.getColumnModel().getColumn(i).setMinWidth(0);
                dtEmListado.getColumnModel().getColumn(i).setPreferredWidth(0);
            }
        }

    }

    void ocultar_columnasPromocion() {
        dtProPromocion.getColumnModel().getColumn(0).setMaxWidth(1);
        dtProPromocion.getColumnModel().getColumn(0).setMinWidth(1);
        dtProPromocion.getColumnModel().getColumn(0).setPreferredWidth(1);
    }

    public static void ocultar_columnasMovInv() {
        dtInvHistorialMov.getColumnModel().getColumn(0).setMaxWidth(1);
        dtInvHistorialMov.getColumnModel().getColumn(0).setMinWidth(1);
        dtInvHistorialMov.getColumnModel().getColumn(0).setPreferredWidth(1);
    }

    void inhabilitar() {
        txtidcliente.setVisible(false);
        txtnombreCliente.setEnabled(false);
        txtdireccionCliente.setEnabled(false);
        txttelefonoCliente.setEnabled(false);
        txtcreditoCliente.setEnabled(false);

        // btnnuevoCliente.setEnabled(false);
        btnguardarCliente.setEnabled(false);
        btncancelarCliente.setEnabled(false);
        btneliminarCliente.setEnabled(false);

        txtidcliente.setText("");
        txtnombreCliente.setText("");
        txtdireccionCliente.setText("");
        txttelefonoCliente.setText("");
        txtcreditoCliente.setText("");

    }

    void habilitar() {
        txtidcliente.setVisible(false);
        txtnombreCliente.setEnabled(true);
        txtdireccionCliente.setEnabled(true);
        txttelefonoCliente.setEnabled(true);
        txtcreditoCliente.setEnabled(true);

        //btnnuevoCliente.setEnabled(true);
        btnguardarCliente.setEnabled(true);
        btncancelarCliente.setEnabled(true);
        btneliminarCliente.setEnabled(true);

        txtidcliente.setText("");
        txtnombreCliente.setText("");
        txtdireccionCliente.setText("");
        txttelefonoCliente.setText("");
        txtcreditoCliente.setText("");
    }

    void inhabilitarCat() {
        txtidcategoria.setVisible(false);
        txtnombreCategoria.setEnabled(false);
        // btnnuevoCliente.setEnabled(false);
        btnguardarCategoria.setEnabled(false);
        btncancelarCategoria.setEnabled(false);
        btneliminarCategoria.setEnabled(false);

        txtidcategoria.setText("");
        txtnombreCategoria.setText("");
    }

    void habilitarCat() {
        txtidcategoria.setVisible(false);
        txtnombreCategoria.setEnabled(true);

        //btnnuevoCliente.setEnabled(true);
        btnguardarCategoria.setEnabled(true);
        btncancelarCategoria.setEnabled(true);
        btneliminarCategoria.setEnabled(true);

        txtidcategoria.setText("");
        txtnombreCategoria.setText("");
    }

    static void inhabilitarPro() {
        txtidproductos.setVisible(false);
        txtNoSeriePro.setEnabled(false);
        txtdescripcionPro.setEnabled(false);
        txtprecioCostoPro.setEnabled(false);
        txtprecioVentaPro.setEnabled(false);
        txtprecioMayoreoPro.setEnabled(false);
        cbocategoriaPro.setEnabled(false);
        txtcantidadActualPro.setEnabled(false);
        txtcantidadMinimoPro.setEnabled(false);

        // btnnuevoCliente.setEnabled(false);
        btnguardarProducto.setEnabled(false);
        //btnnuevoProducto.setEnabled(false);
        //btneliminarProducto.setEnabled(false);

        txtidproductos.setText("");
        txtNoSeriePro.setText("");
        txtdescripcionPro.setText("");
        txtprecioCostoPro.setText("");
        txtprecioVentaPro.setText("");
        txtprecioMayoreoPro.setText("");
        //cbocategoriaPro.setText("");
        txtcantidadActualPro.setText("");
        txtcantidadMinimoPro.setText("");

    }

    void habilitarPro() {
        txtidproductos.setVisible(false);
        txtNoSeriePro.setEnabled(true);
        txtdescripcionPro.setEnabled(true);
        txtprecioCostoPro.setEnabled(true);
        txtprecioVentaPro.setEnabled(true);
        txtprecioMayoreoPro.setEnabled(true);
        cbocategoriaPro.setEnabled(true);
        txtcantidadActualPro.setEnabled(true);
        txtcantidadMinimoPro.setEnabled(true);

        // btnnuevoCliente.setEnabled(false);
        btnguardarProducto.setEnabled(true);
        //btnnuevoProducto.setEnabled(true);
        //btneliminarProducto.setEnabled(true);

        txtidproductos.setText("");
        txtNoSeriePro.setText("");
        txtdescripcionPro.setText("");
        txtprecioCostoPro.setText("");
        txtprecioVentaPro.setText("");
        txtprecioMayoreoPro.setText("");
        //cbocategoriaPro.setText("");
        txtcantidadActualPro.setText("");
        txtcantidadMinimoPro.setText("");
    }

    void habilitarProEditar() {
        txtidproductos.setVisible(false);
        txtNoSeriePro.setEnabled(true);
        txtdescripcionPro.setEnabled(true);
        txtprecioCostoPro.setEnabled(true);
        txtprecioVentaPro.setEnabled(true);
        txtprecioMayoreoPro.setEnabled(true);
        cbocategoriaPro.setEnabled(true);
        txtcantidadActualPro.setEnabled(true);
        txtcantidadMinimoPro.setEnabled(true);

        // btnnuevoCliente.setEnabled(false);
        btnguardarProducto.setEnabled(true);
        btnguardarProducto.setText("Guardar Cambios");
        //btnnuevoProducto.setEnabled(true);
        //btneliminarProducto.setEnabled(true);
    }

    void inhabilitarEm() {
        txtidempleado.setVisible(false);
        txtnombreem.setEnabled(false);
        txtusuarioem.setEnabled(false);
        txtpasswordem.setEnabled(false);
        txtpasswordem.setEnabled(false);
        txtEmail.setEnabled(false);

        btnguardarem.setEnabled(false);
        btncancelarem.setEnabled(false);
        cboprivilegioem.setEnabled(false);

        txtidempleado.setText("");
        txtnombreem.setText("");
        txtusuarioem.setText("");
        txtpasswordem.setText("");
        txtEmail.setText("");
        txtpasswordem.setText("");

    }

    void habilitarEm() {
        txtidempleado.setVisible(false);
        txtnombreem.setEnabled(true);
        txtusuarioem.setEnabled(true);
        txtpasswordem.setEnabled(true);
        txtpasswordem.setEnabled(true);
        txtEmail.setEnabled(true);

        btnguardarem.setEnabled(true);
        btnguardarem.setEnabled(true);
        btncancelarem.setEnabled(true);
        cboprivilegioem.setEnabled(true);

        txtidempleado.setText("");
        txtnombreem.setText("");
        txtusuarioem.setText("");
        txtpasswordem.setText("");
        txtEmail.setText("");
        txtpasswordem.setText("");
    }

    public static void mostrar(String buscar) {
        try {
            DefaultTableModel modelo;
            ControllerClient func = new ControllerClient();
            modelo = func.mostrar(buscar);
            lblTotalRegistros.setText("" + func.totalresgistros);
            lblTotalRegistros1.setText("$" + func.StringTotal);

            dtClieListado.setModel(modelo);
            ocultar_columnas();
            //generarSaldosDisponibles();
            //gastado();
            //generarUltimosPagos();

            /*int totalSaldoActualClientes = 0;
            for (int i = 0; i < dtClieListado.getRowCount(); i++) {
                String saldo_actual_cliente = dtClieListado.getValueAt(i, 5).toString();
                totalSaldoActualClientes += Integer.parseInt(saldo_actual_cliente);
            }
            lblTotalRegistros.setText("" + Integer.toString(func.totalresgistros));
            lblTotalRegistros1.setText("" + totalSaldoActualClientes);
             */
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en el mostrar:" + e);
        }

    }

    public static void mostrarTicketCredito() {
        DefaultTableModel ticketCreModel = new ControllerTicket().ticket(0, "", 0);
        dtClieCredito.setModel(ticketCreModel);
        dtClieCredito.getColumnModel().getColumn(0).setMaxWidth(1);
        dtClieCredito.getColumnModel().getColumn(0).setMinWidth(1);
        dtClieCredito.getColumnModel().getColumn(0).setPreferredWidth(1);

        dtClieCredito.getColumnModel().getColumn(5).setMaxWidth(1);
        dtClieCredito.getColumnModel().getColumn(5).setMinWidth(1);
        dtClieCredito.getColumnModel().getColumn(5).setPreferredWidth(1);

        if (dtClieCredito.getRowCount() != 0) {
            dtClieCredito.changeSelection(0, 00, false, false);
        }
        /*int totalSaldoActualClientes = 0;
            for (int i = 0; i < dtClieCredito.getRowCount(); i++) {
                String saldo_actual_cliente = dtClieCredito.getValueAt(i, 5).toString();
                totalSaldoActualClientes += Integer.parseInt(saldo_actual_cliente);
            }
            lblTotalRegistros.setText("" + totalresgistros));
            lblTotalRegistros1.setText("" + totalSaldoActualClientes);*/
    }

    public static void mostrarCat(String buscar) {
        try {
            DefaultTableModel catModel;
            ControllerCategory func = new ControllerCategory();
            catModel = func.mostrar(buscar);

            dtProCategoria.setModel(catModel);
            ocultar_columnasCat();
            cargarCategoria();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    public static void mostrarPro(String buscar) {
        try {
            DefaultTableModel modelo;
            ControllerProduct func = new ControllerProduct();
            modelo = func.mostrar(buscar);

            dtProListado.setModel(modelo);
            //dtProListado.setDefaultRenderer(Object.class, new Resaltador());
            ocultar_columnasPro();
            lblTotalRegistros2.setText("" + Integer.toString(func.totalresgistros));
            mostrarReporteInv("", 0);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    public static void cargarCategoria() {
        String sql1 = "select * from categoria where inactiva = 0";
        ArrayList<String> categoriaIn = new ArrayList<String>();
        categoriaIn.add("Todos");
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql1);
            cbocategoriaPro.removeAllItems();
            cboCategoriaIn.removeAllItems();
            while (rs.next()) {
                cbocategoriaPro.addItem(rs.getString("nombre"));
                categoriaIn.add(rs.getString("nombre"));
                //cboCategoriaIn.addItem(rs.getString("nombre"));
            }
            for (String categoria : categoriaIn) {
                cboCategoriaIn.addItem("" + categoria);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            //return null;
        }

    }

    void mostrarEm(String buscar) {
        try {
            DefaultTableModel modelo;
            ControllerEmploye func = new ControllerEmploye();
            modelo = func.mostrar(buscar);

            dtEmListado.setModel(modelo);
            ocultar_columnasEm();
            //lblTotalRegistros2.setText("" + Integer.toString(func.totalresgistros));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, e);
        }

    }

    void mostrarTicket(String desde, String hasta) {
        try {
            DefaultTableModel modelo;
            ControllerTicket func = new ControllerTicket();
            modelo = func.mostrar(desde, hasta);
            dtProVentasPedido.setModel(modelo);
            if (modelo != null) {
                dtProVentasPedido.setModel(modelo);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, e);
        }

    }

    // Metodo mostrarPromocion
    void mostrarPromocion(String buscar) {
        try {
            DefaultTableModel modelo;
            ControllerPromocion func = new ControllerPromocion();
            modelo = func.mostrar(buscar);

            dtProPromocion.setModel(modelo);
            //dtProPromocion.setDefaultRenderer(Object.class, new Resaltador());
            ocultar_columnasPromocion();
            clearPromocion();
            //lblTotalRegistros.setText("" + Integer.toString(func.totalresgistros));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, e);
        }

    }

    // clear Promocion
    void clearPromocion() {
        txtnombrePromocion.setText("");
        txtnoseriePromocion.setText("");
        txtdesdePromocion.setText("");
        txthastaPromocion.setText("");
        txtprecioPromocion.setText("");
        lblPrecioNormal.setText("");
        lblPrecioCosto.setText("");
        lblDescripcionPromocion.setText("");
    }

    static DefaultTableModel modeloReporte = null;

    static void mostrarReporteInv(String categoria, int op) {
        try {
            //DefaultTableModel modelo = null;
            fReporteIn func = new fReporteIn();
            if (op != 0) {
                modeloReporte = func.mostrar(categoria, true);
            } else {
                modeloReporte = func.mostrar(categoria, false);

            }

            dtInvReporte.setModel(modeloReporte);
            //dtInvReporte.setDefaultRenderer(Object.class, new Resaltador());
            if (dtInvReporte.getRowCount() != 0) {
                dtInvReporte.changeSelection(0, 2, false, false);
            }

            int total = 0;
            double PC = 0, p = 0, e = 0;
            int filas = dtInvReporte.getRowCount();
            for (int i = 0; i < filas; i++) {
                total += Integer.parseInt(dtInvReporte.getValueAt(i, 7).toString());
                p = Double.parseDouble(dtInvReporte.getValueAt(i, 3).toString());
                e = Double.parseDouble(dtInvReporte.getValueAt(i, 7).toString());
                PC += p * e;
            }
            jLabel119.setText("" + total);
            jLabel118.setText(MyFormater.formato(PC));
            ocultar_columnasIn();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "" + e);
        }

    }

    static void mostrarReporteMov(String fecha, String ca_pro_ca, int indexCombo) {
        try {
            DefaultTableModel modeloReporteMov;
            fReporteMov func = new fReporteMov();
            modeloReporteMov = func.mostrar(fecha, ca_pro_ca, indexCombo);
            dtInvHistorialMov.setModel(modeloReporteMov);
            ocultar_columnasMovInv();
            if (dtInvHistorialMov.getRowCount() != 0) {
                dtInvHistorialMov.changeSelection(0, 0, false, false);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error mostrarReporteMov" + e.getMessage());
        }

    }

    public static void mostrarBajosInventario() {
        try {
            DefaultTableModel modelo;
            ControllerProduct func = new ControllerProduct();
            modelo = func.bajosInventario();

            dtInvBajos.setModel(modelo);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    static void mostrarVentasCategoria(String fecha) {
        try {
            DefaultTableModel modelo;
            ControllerTicket func = new ControllerTicket();
            modelo = func.ventasCategoria(fecha);

            dtVentasCategoria.setModel(modelo);
            //dtVentasCategoria.setDefaultRenderer(Object.class, new Resaltador());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    static void corteCaja(String fecha, String nombreEmpleado, int indexCombo) {
        try {
            ControllerCorte func2 = new ControllerCorte();
            Double[] datosCorteCaja = func2.corte(fecha, nombreEmpleado, indexCombo);

            boolean actividad = false;
            for (int i = 0; i < datosCorteCaja.length; i++) {
                if (datosCorteCaja[i] == null) {
                    datosCorteCaja[i] = 0d;
                }
                if (datosCorteCaja[i] != null) {
                    actividad = true;
                }
            }
            /*
                ÍNDICES
                0 ENTRADAS
                1 SALIDAS
                2 VENTASEFECTIVO
                3 PAGOCLIENTES
                4 TOTAL
                5 VENTAS TOTALES
                6 GANANCIAS
             */
            if (actividad) {
                actividad = false;
                lblEntradas.setText("$" + datosCorteCaja[0]);
                lblVentasEfectivo.setText("$" + datosCorteCaja[2]);
                lblVentasEfectivo2.setText("$" + datosCorteCaja[2]);
                lblEntradas2.setText("$" + datosCorteCaja[0]);
                lblSalidas.setText("$" + datosCorteCaja[1]);
                lblTotalDinero.setText("$" + datosCorteCaja[4]);
                lblPagosClientes.setText("$" + datosCorteCaja[3]);
                lblSalidas2.setText("$" + datosCorteCaja[1]);

                lblVentasTotales.setText("$" + datosCorteCaja[5]);
                lblGananciasDia.setText("$" + datosCorteCaja[6]);

                mostrarVentasCategoria(fecha);
                IMPRIMIRCORTE = true;
            } else {
                JOptionPane.showMessageDialog(null, "No se registraron movimientos de ventas, entradas, pagos, etc para el día, cajero y caja seleccionados.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    static void mostrarPagoCliente(String fecha) {
        try {
            DefaultTableModel modelo;
            fpagoclienteSalida func = new fpagoclienteSalida();
            modelo = func.mostrar(fecha);
            frmPagoClientes.dtPagoCliente.setModel(modelo);
            //frmPagoClientes.dtPagoCliente.setDefaultRenderer(Object.class, new Resaltador());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    static void mostrarSalidas(String fecha) {
        try {
            DefaultTableModel modelo;
            fpagoclienteSalida func = new fpagoclienteSalida();
            modelo = func.salidas(fecha);
            frmPagoProveedores.dtSalida.setModel(modelo);
            //frmSalida.dtSalida.setDefaultRenderer(Object.class, new Resaltador());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    void mostrarEmpleado() {
        String sql1 = "select * from empleado";
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql1);

            while (rs.next()) {
                cbxCajero.addItem(rs.getString("nombre"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            //return null;
        }

    }

//---------------------------------------------------------------------
//Metodo ocultar columnas
    static void ocultar_columnasIn() {
        dtInvReporte.getColumnModel().getColumn(0).setMaxWidth(1);
        dtInvReporte.getColumnModel().getColumn(0).setMinWidth(1);
        dtInvReporte.getColumnModel().getColumn(0).setPreferredWidth(1);

        dtInvReporte.getColumnModel().getColumn(5).setMaxWidth(1);
        dtInvReporte.getColumnModel().getColumn(5).setMinWidth(1);
        dtInvReporte.getColumnModel().getColumn(5).setPreferredWidth(1);

        dtInvReporte.getColumnModel().getColumn(6).setMaxWidth(1);
        dtInvReporte.getColumnModel().getColumn(6).setMinWidth(1);
        dtInvReporte.getColumnModel().getColumn(6).setPreferredWidth(1);
    }

    public static void registrarMovimiento(int idproducto, int idempleado, int habia, int tmovimiento, int cantidad) {
        ModelInventory dtsp = new ModelInventory();
        ControllerInventory func = new ControllerInventory();
        dtsp.setIdproducto(idproducto);
        dtsp.setIdempleado(idempleado);
        dtsp.setHabia(habia);
        dtsp.setTipoMovimiento(tmovimiento);
        dtsp.setCantidad(cantidad);
        func.insertar(dtsp);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        headerPanel = new javax.swing.JPanel();
        buttonsPanel4 = new javax.swing.JPanel();
        lblLogotipo = new javax.swing.JLabel();
        lblusuario = new javax.swing.JLabel();
        btnUsuario = new javax.swing.JButton();
        btnCerrar = new javax.swing.JButton();
        buttonsPanel = new javax.swing.JPanel();
        buttonsPanel1 = new javax.swing.JPanel();
        btnVentasH = new javax.swing.JButton();
        btnClientesH = new javax.swing.JButton();
        btnProductosH = new javax.swing.JButton();
        btnInventarioH = new javax.swing.JButton();
        btnConfiguracionH = new javax.swing.JButton();
        btnCorte = new javax.swing.JButton();
        btnCerrarSesion = new javax.swing.JButton();
        escritorio = new javax.swing.JDesktopPane();
        VentasPanel = new javax.swing.JPanel();
        TituloVentasPanel = new javax.swing.JPanel();
        txtEnterProducto = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        titleSection = new javax.swing.JLabel();
        btnAgregarProd = new principal.MaterialButton();
        buttonsPanel2 = new javax.swing.JPanel();
        buttonsPanel3 = new javax.swing.JPanel();
        btnBorrarArt = new javax.swing.JButton();
        btnINSVarios = new javax.swing.JButton();
        btnBuscar = new javax.swing.JButton();
        btnMayoreo = new javax.swing.JButton();
        btnEntradas = new javax.swing.JButton();
        btnSalidas = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPaneVentas = new javax.swing.JScrollPane();
        dtTicket = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        btnNuevoTicket = new javax.swing.JButton();
        btnEliminarTicket = new javax.swing.JButton();
        lblTotalCantProd = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        btnCobrarVenta = new principal.MaterialButtomRectangle();
        jPanel7 = new javax.swing.JPanel();
        lblTotalImporte = new javax.swing.JLabel();
        lblSubTotalImporte = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        lblTotalInfo = new javax.swing.JLabel();
        lblPagoConInfo = new javax.swing.JLabel();
        lblCambioInfo = new javax.swing.JLabel();
        jPanel29 = new javax.swing.JPanel();
        btnReimprimirUltimoTicket = new principal.MaterialButton();
        btnVentasDevoluciones = new principal.MaterialButton();
        btnHacerCorte2 = new principal.MaterialButton();
        ClientesPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        btnExportarClientes = new principal.MaterialButton();
        jScrollPaneListadoCliente = new javax.swing.JScrollPane();
        dtClieListado = new javax.swing.JTable();
        jPanel12 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        txtnombreCliente = new javax.swing.JTextField();
        txtdireccionCliente = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txttelefonoCliente = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txtcreditoCliente = new javax.swing.JTextField();
        txtidcliente = new javax.swing.JTextField();
        btnnuevoCliente = new principal.MaterialButton();
        btnguardarCliente = new principal.MaterialButton();
        btncancelarCliente = new principal.MaterialButton();
        jLabel1 = new javax.swing.JLabel();
        txtbuscarClientes = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        lblTotalRegistros = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        lblTotalRegistros1 = new javax.swing.JLabel();
        btneliminarCliente = new principal.MaterialButton();
        btnImprimirLista = new principal.MaterialButton();
        jPanel11 = new javax.swing.JPanel();
        jScrollPaneEstadoCuenta = new javax.swing.JScrollPane();
        dtClieCredito = new javax.swing.JTable();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        lblNombreCliente = new javax.swing.JLabel();
        lblSaldoActualCliente = new javax.swing.JLabel();
        lblLimiteCreditoCliente = new javax.swing.JLabel();
        lblTotalImporteCredito = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        btnAbonarCliente = new principal.MaterialButton();
        btnDetAbono = new principal.MaterialButton();
        btnLiquidarAdeudo = new principal.MaterialButton();
        btnImprimirEdoCuentaCompleto = new principal.MaterialButton();
        btnEliminarTicketCliente = new principal.MaterialButton();
        btnImprimirTicketTab = new principal.MaterialButton();
        lblSeleccionaUnCliente = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        lblImpuestos = new javax.swing.JLabel();
        ProductosPanel = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel9 = new javax.swing.JPanel();
        jScrollPaneListadoProducto = new javax.swing.JScrollPane();
        dtProListado = new javax.swing.JTable();
        jLabel24 = new javax.swing.JLabel();
        txtBuscarProductos = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        lblTotalRegistros2 = new javax.swing.JLabel();
        btnEliminarProducto = new principal.MaterialButton();
        btnImportarProducto = new principal.MaterialButton();
        jPanel10 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        txtNoSeriePro = new javax.swing.JTextField();
        txtdescripcionPro = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        txtprecioCostoPro = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        txtprecioVentaPro = new javax.swing.JTextField();
        txtprecioMayoreoPro = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        cbocategoriaPro = new javax.swing.JComboBox<String>();
        txtcantidadActualPro = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        txtcantidadMinimoPro = new javax.swing.JTextField();
        txtidproductos = new javax.swing.JTextField();
        btnnuevoProducto = new principal.MaterialButton();
        btnguardarProducto = new principal.MaterialButton();
        btnProCancelar = new principal.MaterialButton();
        jPanel13 = new javax.swing.JPanel();
        jScrollPaneVentasVendido = new javax.swing.JScrollPane();
        dtProVentasPedido = new javax.swing.JTable();
        jLabel103 = new javax.swing.JLabel();
        jLabel104 = new javax.swing.JLabel();
        cmbox = new javax.swing.JComboBox<String>();
        periodoFecha = new javax.swing.JPanel();
        fechachooser_hasta = new com.toedter.calendar.JDateChooser();
        jLabel101 = new javax.swing.JLabel();
        fechachooser_desde = new com.toedter.calendar.JDateChooser();
        jLabel100 = new javax.swing.JLabel();
        btnExportarVentasPedido = new principal.MaterialButton();
        btnImprimirProPedido = new principal.MaterialButton();
        jPanel14 = new javax.swing.JPanel();
        jLabel89 = new javax.swing.JLabel();
        txtnoseriePromocion = new javax.swing.JTextField();
        lblPrecioNormal = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        lblPrecioCosto = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        txtdesdePromocion = new javax.swing.JTextField();
        jScrollPanePromocion = new javax.swing.JScrollPane();
        dtProPromocion = new javax.swing.JTable();
        jLabel46 = new javax.swing.JLabel();
        txthastaPromocion = new javax.swing.JTextField();
        jLabel69 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        txtprecioPromocion = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        txtnombrePromocion = new javax.swing.JTextField();
        lblDescripcionPromocion = new javax.swing.JLabel();
        btnEliminarPromocionPro = new principal.MaterialButton();
        btnGuardarPromocion = new principal.MaterialButton();
        jPanel15 = new javax.swing.JPanel();
        jLabel90 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        txtBuscarCategoria = new javax.swing.JTextField();
        jScrollPaneCategoria = new javax.swing.JScrollPane();
        dtProCategoria = new javax.swing.JTable();
        jPanel16 = new javax.swing.JPanel();
        txtnombreCategoria = new javax.swing.JTextField();
        jLabel92 = new javax.swing.JLabel();
        btnnuevoCategoria = new principal.MaterialButton();
        btnguardarCategoria = new principal.MaterialButton();
        btncancelarCategoria = new principal.MaterialButton();
        txtidcategoria = new javax.swing.JTextField();
        btneliminarCategoria = new principal.MaterialButton();
        InventarioPanel = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        jLabel102 = new javax.swing.JLabel();
        buttonsPanel8 = new javax.swing.JPanel();
        buttonsPanel9 = new javax.swing.JPanel();
        btnAgregarInv = new javax.swing.JButton();
        btnAjustesInv = new javax.swing.JButton();
        btnProdInventarioBajos = new javax.swing.JButton();
        btnReporteInv = new javax.swing.JButton();
        btnReporteMovInv = new javax.swing.JButton();
        vistasInventario = new javax.swing.JLayeredPane();
        agregarInvPanel = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        jPanel27 = new javax.swing.JPanel();
        txtCodigoProAgregarInv = new javax.swing.JTextField();
        jLabel48 = new javax.swing.JLabel();
        lbldescripinventario = new javax.swing.JLabel();
        lblcantinventario = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel105 = new javax.swing.JLabel();
        txtinputinventario = new javax.swing.JTextField();
        jLabel47 = new javax.swing.JLabel();
        btninputinventario = new principal.MaterialButton();
        ajusteInvPanel = new javax.swing.JPanel();
        jLabel106 = new javax.swing.JLabel();
        jPanel28 = new javax.swing.JPanel();
        jLabel107 = new javax.swing.JLabel();
        txtCodigoProAjuste = new javax.swing.JTextField();
        jLabel108 = new javax.swing.JLabel();
        lbldesinventario2 = new javax.swing.JLabel();
        jLabel110 = new javax.swing.JLabel();
        lblExistenciaAjuste = new javax.swing.JLabel();
        jLabel112 = new javax.swing.JLabel();
        txtinputinventario2 = new javax.swing.JTextField();
        btnajusteinventario = new principal.MaterialButton();
        lblIdProductoAjuste = new javax.swing.JLabel();
        bajoInvPanel = new javax.swing.JPanel();
        jLabel113 = new javax.swing.JLabel();
        jLabel114 = new javax.swing.JLabel();
        jScrollPaneBajosInventario = new javax.swing.JScrollPane();
        dtInvBajos = new javax.swing.JTable();
        btnExportarExcelBajosInventario = new principal.MaterialButton();
        reporteInvPanel = new javax.swing.JPanel();
        jPanel21 = new javax.swing.JPanel();
        jLabel115 = new javax.swing.JLabel();
        jLabel116 = new javax.swing.JLabel();
        jLabel117 = new javax.swing.JLabel();
        jLabel118 = new javax.swing.JLabel();
        jLabel119 = new javax.swing.JLabel();
        jPanel22 = new javax.swing.JPanel();
        jLabel120 = new javax.swing.JLabel();
        cboCategoriaIn = new javax.swing.JComboBox<String>();
        btnImprimir = new principal.MaterialButton();
        btnExportar = new principal.MaterialButton();
        btnModificarProducto = new principal.MaterialButton();
        btnAgregarInventario = new principal.MaterialButton();
        jScrollPaneReporteInventario = new javax.swing.JScrollPane();
        dtInvReporte = new javax.swing.JTable();
        reporteMovimientosPanel = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        jLabel121 = new javax.swing.JLabel();
        jLabel122 = new javax.swing.JLabel();
        jDateChooser4 = new com.toedter.calendar.JDateChooser();
        jLabel123 = new javax.swing.JLabel();
        jLabel124 = new javax.swing.JLabel();
        jComboBox5 = new javax.swing.JComboBox<String>();
        jLabel125 = new javax.swing.JLabel();
        btnBuscar2 = new app.bolivia.swing.JCTextField();
        jScrollPaneHistorialMov = new javax.swing.JScrollPane();
        dtInvHistorialMov = new javax.swing.JTable();
        jPanel24 = new javax.swing.JPanel();
        btnImprimirMovimiento = new principal.MaterialButton();
        btnImprimirMovimiento1 = new principal.MaterialButton();
        ConfiguracionPanel = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        jLabel126 = new javax.swing.JLabel();
        buttonsPanel10 = new javax.swing.JPanel();
        buttonsPanel11 = new javax.swing.JPanel();
        btnMostrarOpciones = new principal.MaterialButton();
        vistasConfiguraciones = new javax.swing.JLayeredPane();
        inicioConfiguracionesPanel = new javax.swing.JPanel();
        personalizaciónFranjaPanel = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        btnTicket = new javax.swing.JButton();
        btnLogotipoPrograma = new javax.swing.JButton();
        btnImpuestos = new javax.swing.JButton();
        dispositivosFranjaPanel = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        btnImpresoraTicket = new javax.swing.JButton();
        generalFranjaPanel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        btnCajeros = new javax.swing.JButton();
        btnModificarFolio = new javax.swing.JButton();
        opcionesHabilitadasPanel = new javax.swing.JPanel();
        jLabel50 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();
        cajerosPanel = new javax.swing.JPanel();
        jLabel55 = new javax.swing.JLabel();
        txtbuscarem = new javax.swing.JTextField();
        jPanel20 = new javax.swing.JPanel();
        txtnombreem = new javax.swing.JTextField();
        jLabel127 = new javax.swing.JLabel();
        jLabel136 = new javax.swing.JLabel();
        txtusuarioem = new javax.swing.JTextField();
        txtpasswordem = new javax.swing.JTextField();
        jLabel140 = new javax.swing.JLabel();
        cboprivilegioem = new javax.swing.JComboBox<String>();
        jLabel141 = new javax.swing.JLabel();
        btnnuevoem = new principal.MaterialButton();
        btnguardarem = new principal.MaterialButton();
        btncancelarem = new principal.MaterialButton();
        jLabel142 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jScrollPaneCajero = new javax.swing.JScrollPane();
        dtEmListado = new javax.swing.JTable();
        jLabel134 = new javax.swing.JLabel();
        txtidempleado = new javax.swing.JTextField();
        btneliminarem = new principal.MaterialButton();
        folioTicketPanel = new javax.swing.JPanel();
        jLabel128 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        txtFolioConfiguracion = new javax.swing.JTextField();
        logotipoPanel = new javax.swing.JPanel();
        jLabel129 = new javax.swing.JLabel();
        jLabel137 = new javax.swing.JLabel();
        jLabel138 = new javax.swing.JLabel();
        txtRutaLogotipo = new javax.swing.JTextField();
        jLabel139 = new javax.swing.JLabel();
        btnExaminarLogotipo = new principal.MaterialButton();
        ticketPanel = new javax.swing.JPanel();
        jLabel130 = new javax.swing.JLabel();
        jPanel30 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel31 = new javax.swing.JPanel();
        txtEmpresaNombre = new javax.swing.JTextField();
        txtEmpresaCalle = new javax.swing.JTextField();
        txtEmpresaLocal = new javax.swing.JTextField();
        txtEmpresaSucursal = new javax.swing.JTextField();
        txtEmpresaTelefono = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jLabel76 = new javax.swing.JLabel();
        txtEmpresaAgradecimiento = new javax.swing.JTextField();
        impuestosPanel = new javax.swing.JPanel();
        jLabel131 = new javax.swing.JLabel();
        rbImpuesto = new javax.swing.JRadioButton();
        txtImpuesto = new javax.swing.JTextField();
        baseDatosPanel = new javax.swing.JPanel();
        jLabel132 = new javax.swing.JLabel();
        impresoraTicketPanel = new javax.swing.JPanel();
        jLabel133 = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        jLabel78 = new javax.swing.JLabel();
        comboImpresora = new org.jdesktop.swingx.JXComboBox();
        CortePanel = new javax.swing.JPanel();
        jLabel58 = new javax.swing.JLabel();
        buttonsPanel12 = new javax.swing.JPanel();
        buttonsPanel13 = new javax.swing.JPanel();
        btnImprimirCorte = new principal.MaterialButton();
        btnHacerCorteHoy = new principal.MaterialButton();
        MainCortetPanel = new javax.swing.JPanel();
        periodoPanel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        dateCorte = new com.toedter.calendar.JDateChooser();
        cbxCajero = new javax.swing.JComboBox<String>();
        btnHacerCorte = new principal.MaterialButton();
        ContenidoCortePanel = new javax.swing.JPanel();
        jPanel25 = new javax.swing.JPanel();
        EntradaEfectivoPanel = new javax.swing.JPanel();
        jLabel81 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        jSeparator7 = new javax.swing.JSeparator();
        lblEntradas = new javax.swing.JLabel();
        DineroCajaPanel = new javax.swing.JPanel();
        jLabel72 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel74 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel75 = new javax.swing.JLabel();
        lblTotalDinero = new javax.swing.JLabel();
        lblEntradas2 = new javax.swing.JLabel();
        lblSalidas = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        jLabel79 = new javax.swing.JLabel();
        lblVentasEfectivo = new javax.swing.JLabel();
        jPanel26 = new javax.swing.JPanel();
        PagosContadoPanel = new javax.swing.JPanel();
        jLabel88 = new javax.swing.JLabel();
        jLabel93 = new javax.swing.JLabel();
        jSeparator10 = new javax.swing.JSeparator();
        lblVentasEfectivo2 = new javax.swing.JLabel();
        jSeparator11 = new javax.swing.JSeparator();
        lblPagoClientes = new javax.swing.JLabel();
        lblPagosClientes = new javax.swing.JLabel();
        lblSalidas2 = new javax.swing.JLabel();
        lblSalidaEfectivo = new javax.swing.JLabel();
        jSeparator12 = new javax.swing.JSeparator();
        VentasDepatamentoPanel = new javax.swing.JPanel();
        jLabel65 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        jScrollPaneVentasCategoria = new javax.swing.JScrollPane();
        dtVentasCategoria = new javax.swing.JTable();
        VentasTotalesPanel = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        lblVentasTotales = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        lblGananciasDia = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        lblFechaCorte = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowDeiconified(java.awt.event.WindowEvent evt) {
                formWindowDeiconified(evt);
            }
        });

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setLayout(new javax.swing.BoxLayout(mainPanel, javax.swing.BoxLayout.Y_AXIS));

        headerPanel.setBackground(new java.awt.Color(244, 252, 250));
        headerPanel.setLayout(new javax.swing.BoxLayout(headerPanel, javax.swing.BoxLayout.Y_AXIS));

        buttonsPanel4.setBackground(new java.awt.Color(244, 252, 250));
        buttonsPanel4.setForeground(new java.awt.Color(255, 255, 255));
        buttonsPanel4.setPreferredSize(new java.awt.Dimension(1008, 80));

        lblLogotipo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblLogotipo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/logo_muebleria_4_header.png"))); // NOI18N
        lblLogotipo.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                lblLogotipoMouseDragged(evt);
            }
        });
        lblLogotipo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblLogotipoMousePressed(evt);
            }
        });

        lblusuario.setBackground(new java.awt.Color(87, 204, 243));
        lblusuario.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        lblusuario.setText("Le atiende");

        btnUsuario.setBackground(new MyColor().COLOR_BACKGROUND_BLACK);
        btnUsuario.setForeground(new java.awt.Color(255, 255, 255));
        btnUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/usuario32.png"))); // NOI18N
        btnUsuario.setText("nombre");
        btnUsuario.setBorderPainted(false);
        btnUsuario.setContentAreaFilled(false);
        btnUsuario.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnUsuario.setOpaque(true);
        btnUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUsuarioActionPerformed(evt);
            }
        });

        btnCerrar.setBackground(new java.awt.Color(51, 51, 51));
        btnCerrar.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnCerrar.setForeground(new java.awt.Color(102, 0, 153));
        btnCerrar.setText("-");
        btnCerrar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnCerrar.setBorderPainted(false);
        btnCerrar.setOpaque(false);
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout buttonsPanel4Layout = new javax.swing.GroupLayout(buttonsPanel4);
        buttonsPanel4.setLayout(buttonsPanel4Layout);
        buttonsPanel4Layout.setHorizontalGroup(
            buttonsPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonsPanel4Layout.createSequentialGroup()
                .addComponent(lblLogotipo, javax.swing.GroupLayout.DEFAULT_SIZE, 701, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblusuario)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCerrar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        buttonsPanel4Layout.setVerticalGroup(
            buttonsPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonsPanel4Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(buttonsPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblusuario)
                    .addComponent(btnUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCerrar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(buttonsPanel4Layout.createSequentialGroup()
                .addComponent(lblLogotipo, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        headerPanel.add(buttonsPanel4);

        buttonsPanel.setBackground(new java.awt.Color(87, 204, 243));
        buttonsPanel.setForeground(new java.awt.Color(255, 255, 255));
        buttonsPanel.setPreferredSize(new java.awt.Dimension(1008, 42));
        buttonsPanel.setLayout(new javax.swing.BoxLayout(buttonsPanel, javax.swing.BoxLayout.LINE_AXIS));

        buttonsPanel1.setBackground(new java.awt.Color(135, 65, 155));
        buttonsPanel1.setPreferredSize(new java.awt.Dimension(1008, 42));

        btnVentasH.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnVentasH.setForeground(new java.awt.Color(255, 255, 255));
        btnVentasH.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/ventas24Blanco.png"))); // NOI18N
        btnVentasH.setText("F1 Ventas");
        btnVentasH.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnVentasH.setBorderPainted(false);
        btnVentasH.setContentAreaFilled(false);
        btnVentasH.setFocusPainted(false);
        btnVentasH.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnVentasH.setMargin(new java.awt.Insets(0, 14, 0, 14));
        btnVentasH.setMaximumSize(new java.awt.Dimension(115, 40));
        btnVentasH.setMinimumSize(new java.awt.Dimension(115, 40));
        btnVentasH.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/btnCursor.png"))); // NOI18N
        btnVentasH.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnVentasHMouseEntered(evt);
            }
        });
        btnVentasH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVentasHActionPerformed(evt);
            }
        });

        btnClientesH.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnClientesH.setForeground(new java.awt.Color(255, 255, 255));
        btnClientesH.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/clientes32.png"))); // NOI18N
        btnClientesH.setText("F2 Clientes");
        btnClientesH.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnClientesH.setBorderPainted(false);
        btnClientesH.setContentAreaFilled(false);
        btnClientesH.setFocusPainted(false);
        btnClientesH.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnClientesH.setMaximumSize(new java.awt.Dimension(115, 40));
        btnClientesH.setMinimumSize(new java.awt.Dimension(115, 40));
        btnClientesH.setRequestFocusEnabled(false);
        btnClientesH.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/btnCursor.png"))); // NOI18N
        btnClientesH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClientesHActionPerformed(evt);
            }
        });

        btnProductosH.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnProductosH.setForeground(new java.awt.Color(255, 255, 255));
        btnProductosH.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/producto32.png"))); // NOI18N
        btnProductosH.setText("F3 Productos");
        btnProductosH.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnProductosH.setBorderPainted(false);
        btnProductosH.setContentAreaFilled(false);
        btnProductosH.setFocusPainted(false);
        btnProductosH.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnProductosH.setMaximumSize(new java.awt.Dimension(115, 40));
        btnProductosH.setMinimumSize(new java.awt.Dimension(115, 40));
        btnProductosH.setRequestFocusEnabled(false);
        btnProductosH.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/btnCursor.png"))); // NOI18N
        btnProductosH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProductosHActionPerformed(evt);
            }
        });

        btnInventarioH.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnInventarioH.setForeground(new java.awt.Color(255, 255, 255));
        btnInventarioH.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/inventario32.png"))); // NOI18N
        btnInventarioH.setText("F4 Inventario");
        btnInventarioH.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnInventarioH.setBorderPainted(false);
        btnInventarioH.setContentAreaFilled(false);
        btnInventarioH.setFocusPainted(false);
        btnInventarioH.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnInventarioH.setMaximumSize(new java.awt.Dimension(115, 40));
        btnInventarioH.setMinimumSize(new java.awt.Dimension(115, 40));
        btnInventarioH.setRequestFocusEnabled(false);
        btnInventarioH.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/btnCursor.png"))); // NOI18N
        btnInventarioH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInventarioHActionPerformed(evt);
            }
        });

        btnConfiguracionH.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnConfiguracionH.setForeground(new java.awt.Color(255, 255, 255));
        btnConfiguracionH.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/ajustes32.png"))); // NOI18N
        btnConfiguracionH.setText("Configuración");
        btnConfiguracionH.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnConfiguracionH.setBorderPainted(false);
        btnConfiguracionH.setContentAreaFilled(false);
        btnConfiguracionH.setFocusPainted(false);
        btnConfiguracionH.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnConfiguracionH.setMaximumSize(new java.awt.Dimension(115, 40));
        btnConfiguracionH.setMinimumSize(new java.awt.Dimension(115, 40));
        btnConfiguracionH.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/btnCursor.png"))); // NOI18N
        btnConfiguracionH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfiguracionHActionPerformed(evt);
            }
        });

        btnCorte.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnCorte.setForeground(new java.awt.Color(255, 255, 255));
        btnCorte.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/corte32.png"))); // NOI18N
        btnCorte.setText("Corte");
        btnCorte.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnCorte.setBorderPainted(false);
        btnCorte.setContentAreaFilled(false);
        btnCorte.setFocusPainted(false);
        btnCorte.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCorte.setMaximumSize(new java.awt.Dimension(115, 40));
        btnCorte.setMinimumSize(new java.awt.Dimension(115, 40));
        btnCorte.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/btnCursor.png"))); // NOI18N
        btnCorte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCorteActionPerformed(evt);
            }
        });

        btnCerrarSesion.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnCerrarSesion.setForeground(new java.awt.Color(255, 255, 255));
        btnCerrarSesion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/cierreSesion32.png"))); // NOI18N
        btnCerrarSesion.setText("Cerrar Sesión");
        btnCerrarSesion.setBorderPainted(false);
        btnCerrarSesion.setContentAreaFilled(false);
        btnCerrarSesion.setFocusPainted(false);
        btnCerrarSesion.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCerrarSesion.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnCerrarSesion.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/btnCursor.png"))); // NOI18N
        btnCerrarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarSesionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout buttonsPanel1Layout = new javax.swing.GroupLayout(buttonsPanel1);
        buttonsPanel1.setLayout(buttonsPanel1Layout);
        buttonsPanel1Layout.setHorizontalGroup(
            buttonsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonsPanel1Layout.createSequentialGroup()
                .addComponent(btnVentasH, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnClientesH, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnProductosH, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnInventarioH, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnConfiguracionH, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnCorte, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 169, Short.MAX_VALUE)
                .addComponent(btnCerrarSesion)
                .addContainerGap())
        );
        buttonsPanel1Layout.setVerticalGroup(
            buttonsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonsPanel1Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(buttonsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnClientesH, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnProductosH, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnInventarioH, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnConfiguracionH, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(buttonsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnCorte, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCerrarSesion))
                    .addComponent(btnVentasH, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        buttonsPanel.add(buttonsPanel1);

        headerPanel.add(buttonsPanel);

        mainPanel.add(headerPanel);

        escritorio.setPreferredSize(new java.awt.Dimension(1563, 922));

        VentasPanel.setBackground(new java.awt.Color(255, 255, 255));
        VentasPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        VentasPanel.setFocusable(false);
        VentasPanel.setLayout(new javax.swing.BoxLayout(VentasPanel, javax.swing.BoxLayout.Y_AXIS));

        TituloVentasPanel.setBackground(new java.awt.Color(255, 255, 255));

        txtEnterProducto.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtEnterProducto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtEnterProductoKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtEnterProductoKeyTyped(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(135, 65, 155));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Código del producto");

        titleSection.setBackground(new java.awt.Color(182, 210, 248));
        titleSection.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        titleSection.setForeground(new java.awt.Color(135, 65, 155));
        titleSection.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        titleSection.setText("VENTA DE PRODUCTOS");

        btnAgregarProd.setBackground(new MyColor().COLOR_BUTTON);
        btnAgregarProd.setForeground(new java.awt.Color(255, 255, 255));
        btnAgregarProd.setText("Enter - Agregar producto");
        btnAgregarProd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarProdActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout TituloVentasPanelLayout = new javax.swing.GroupLayout(TituloVentasPanel);
        TituloVentasPanel.setLayout(TituloVentasPanelLayout);
        TituloVentasPanelLayout.setHorizontalGroup(
            TituloVentasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TituloVentasPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleSection, javax.swing.GroupLayout.PREFERRED_SIZE, 576, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(975, Short.MAX_VALUE))
            .addGroup(TituloVentasPanelLayout.createSequentialGroup()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEnterProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAgregarProd, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        TituloVentasPanelLayout.setVerticalGroup(
            TituloVentasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TituloVentasPanelLayout.createSequentialGroup()
                .addComponent(titleSection)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(TituloVentasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtEnterProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAgregarProd, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );

        VentasPanel.add(TituloVentasPanel);

        buttonsPanel2.setBackground(new java.awt.Color(214, 237, 236));
        buttonsPanel2.setForeground(new java.awt.Color(255, 255, 255));
        buttonsPanel2.setPreferredSize(new java.awt.Dimension(1008, 42));

        buttonsPanel3.setBackground(new MyColor().COLOR_HEADER_BUTTON2);
        buttonsPanel3.setPreferredSize(new java.awt.Dimension(1008, 42));
        buttonsPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnBorrarArt.setForeground(new java.awt.Color(255, 255, 255));
        btnBorrarArt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/eliminar32G.png"))); // NOI18N
        btnBorrarArt.setText("DEL Borrar Art.");
        btnBorrarArt.setBorderPainted(false);
        btnBorrarArt.setContentAreaFilled(false);
        btnBorrarArt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarArtActionPerformed(evt);
            }
        });
        buttonsPanel3.add(btnBorrarArt, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 0, 150, 40));

        btnINSVarios.setForeground(new java.awt.Color(255, 255, 255));
        btnINSVarios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/varios32.png"))); // NOI18N
        btnINSVarios.setText("INS Varios");
        btnINSVarios.setBorderPainted(false);
        btnINSVarios.setContentAreaFilled(false);
        btnINSVarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnINSVariosActionPerformed(evt);
            }
        });
        buttonsPanel3.add(btnINSVarios, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 130, 40));

        btnBuscar.setForeground(new java.awt.Color(255, 255, 255));
        btnBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/buscar32G.png"))); // NOI18N
        btnBuscar.setText("F10 Buscar");
        btnBuscar.setBorderPainted(false);
        btnBuscar.setContentAreaFilled(false);
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });
        buttonsPanel3.add(btnBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 0, 130, 40));

        btnMayoreo.setForeground(new java.awt.Color(255, 255, 255));
        btnMayoreo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/mayoreo32G.png"))); // NOI18N
        btnMayoreo.setText("F11 Mayoreo");
        btnMayoreo.setBorderPainted(false);
        btnMayoreo.setContentAreaFilled(false);
        btnMayoreo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMayoreoActionPerformed(evt);
            }
        });
        buttonsPanel3.add(btnMayoreo, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 0, 140, 40));

        btnEntradas.setForeground(new java.awt.Color(255, 255, 255));
        btnEntradas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/entradas32G.png"))); // NOI18N
        btnEntradas.setText("F7 Entradas");
        btnEntradas.setBorderPainted(false);
        btnEntradas.setContentAreaFilled(false);
        btnEntradas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEntradasActionPerformed(evt);
            }
        });
        buttonsPanel3.add(btnEntradas, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 0, 140, 40));

        btnSalidas.setForeground(new java.awt.Color(255, 255, 255));
        btnSalidas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/salidas32G.png"))); // NOI18N
        btnSalidas.setText("F8 Salidas");
        btnSalidas.setBorderPainted(false);
        btnSalidas.setContentAreaFilled(false);
        btnSalidas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalidasActionPerformed(evt);
            }
        });
        buttonsPanel3.add(btnSalidas, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 0, 130, 40));

        javax.swing.GroupLayout buttonsPanel2Layout = new javax.swing.GroupLayout(buttonsPanel2);
        buttonsPanel2.setLayout(buttonsPanel2Layout);
        buttonsPanel2Layout.setHorizontalGroup(
            buttonsPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(buttonsPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        buttonsPanel2Layout.setVerticalGroup(
            buttonsPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonsPanel2Layout.createSequentialGroup()
                .addComponent(buttonsPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        VentasPanel.add(buttonsPanel2);

        jTabbedPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedPane1MouseClicked(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        dtTicket.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        dtTicket.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Código de barras", "Descripción del producto", "Precio de Venta", "Cantidad", "Importe", "Existencia"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        dtTicket.setGridColor(new java.awt.Color(255, 255, 255));
        dtTicket.setRowHeight(20);
        dtTicket.setSelectionBackground(new java.awt.Color(227, 244, 248));
        dtTicket.setSelectionForeground(new java.awt.Color(51, 153, 255));
        dtTicket.setShowHorizontalLines(false);
        dtTicket.setShowVerticalLines(false);
        dtTicket.getTableHeader().setReorderingAllowed(false);
        jScrollPaneVentas.setViewportView(dtTicket);
        if (dtTicket.getColumnModel().getColumnCount() > 0) {
            dtTicket.getColumnModel().getColumn(0).setMinWidth(200);
            dtTicket.getColumnModel().getColumn(0).setMaxWidth(300);
            dtTicket.getColumnModel().getColumn(1).setMinWidth(220);
            dtTicket.getColumnModel().getColumn(1).setMaxWidth(250);
            dtTicket.getColumnModel().getColumn(2).setMinWidth(100);
            dtTicket.getColumnModel().getColumn(2).setMaxWidth(150);
            dtTicket.getColumnModel().getColumn(3).setMinWidth(50);
            dtTicket.getColumnModel().getColumn(3).setMaxWidth(100);
            dtTicket.getColumnModel().getColumn(4).setMinWidth(100);
            dtTicket.getColumnModel().getColumn(4).setMaxWidth(150);
            dtTicket.getColumnModel().getColumn(5).setMinWidth(50);
            dtTicket.getColumnModel().getColumn(5).setMaxWidth(2000);
        }

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPaneVentas, javax.swing.GroupLayout.DEFAULT_SIZE, 1556, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPaneVentas, javax.swing.GroupLayout.DEFAULT_SIZE, 441, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Ticket", jPanel1);

        VentasPanel.add(jTabbedPane1);

        jPanel4.setBackground(new MyColor().COLOR_BACKGROUND_BLACK);

        jPanel6.setBackground(new MyColor().COLOR_BACKGROUND_BLACK);

        btnNuevoTicket.setBackground(new java.awt.Color(255, 255, 255));
        btnNuevoTicket.setText("F6 - Nuevo Ticket");
        btnNuevoTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoTicketActionPerformed(evt);
            }
        });

        btnEliminarTicket.setBackground(new java.awt.Color(255, 255, 255));
        btnEliminarTicket.setText("Eliminar Ticket");
        btnEliminarTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarTicketActionPerformed(evt);
            }
        });

        lblTotalCantProd.setBackground(new java.awt.Color(0, 0, 0));
        lblTotalCantProd.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblTotalCantProd.setForeground(new java.awt.Color(255, 255, 255));
        lblTotalCantProd.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalCantProd.setText("0");

        jLabel35.setBackground(new java.awt.Color(0, 0, 0));
        jLabel35.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(255, 255, 255));
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel35.setText("Productos en la venta actual.");

        btnCobrarVenta.setBackground(new MyColor().COLOR_BUTTON);
        btnCobrarVenta.setForeground(new java.awt.Color(255, 255, 255));
        btnCobrarVenta.setText("F12 - Cobrar");
        btnCobrarVenta.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnCobrarVenta.setFont(new java.awt.Font("Roboto Medium", 1, 18)); // NOI18N
        btnCobrarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCobrarVentaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(btnNuevoTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnEliminarTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(lblTotalCantProd, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 427, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 669, Short.MAX_VALUE)
                .addComponent(btnCobrarVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalCantProd)
                    .addComponent(jLabel35))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNuevoTicket)
                    .addComponent(btnEliminarTicket))
                .addGap(9, 9, 9))
            .addComponent(btnCobrarVenta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel7.setBackground(new java.awt.Color(244, 252, 250));

        lblTotalImporte.setBackground(new java.awt.Color(168, 168, 168));
        lblTotalImporte.setFont(new java.awt.Font("Dialog", 0, 48)); // NOI18N
        lblTotalImporte.setForeground(new java.awt.Color(135, 65, 155));
        lblTotalImporte.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotalImporte.setText("$0.00");

        lblSubTotalImporte.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        lblSubTotalImporte.setForeground(new java.awt.Color(135, 65, 155));
        lblSubTotalImporte.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSubTotalImporte.setText("Subtotal: $0.00");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblTotalImporte, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblSubTotalImporte, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(lblTotalImporte, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSubTotalImporte, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        VentasPanel.add(jPanel4);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setPreferredSize(new java.awt.Dimension(1561, 70));

        jLabel26.setBackground(new java.awt.Color(0, 0, 0));
        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(135, 65, 155));
        jLabel26.setText("Total:");

        jLabel27.setBackground(new java.awt.Color(0, 0, 0));
        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(135, 65, 155));
        jLabel27.setText("Pagó Con:");

        jLabel29.setBackground(new java.awt.Color(0, 0, 0));
        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(135, 65, 155));
        jLabel29.setText("Cambio:");

        lblTotalInfo.setBackground(new java.awt.Color(255, 255, 255));
        lblTotalInfo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblTotalInfo.setForeground(new java.awt.Color(135, 65, 155));
        lblTotalInfo.setText("$0.00");

        lblPagoConInfo.setBackground(new java.awt.Color(255, 255, 255));
        lblPagoConInfo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblPagoConInfo.setForeground(new java.awt.Color(135, 65, 155));
        lblPagoConInfo.setText("$0.00");

        lblCambioInfo.setBackground(new java.awt.Color(255, 255, 255));
        lblCambioInfo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblCambioInfo.setForeground(new java.awt.Color(135, 65, 155));
        lblCambioInfo.setText("$0.00");

        jPanel29.setBackground(new java.awt.Color(255, 255, 255));

        btnReimprimirUltimoTicket.setBackground(new MyColor().COLOR_BUTTON);
        btnReimprimirUltimoTicket.setForeground(new java.awt.Color(255, 255, 255));
        btnReimprimirUltimoTicket.setText("Reimprimir Último Ticket");
        btnReimprimirUltimoTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReimprimirUltimoTicketActionPerformed(evt);
            }
        });

        btnVentasDevoluciones.setBackground(new MyColor().COLOR_BUTTON);
        btnVentasDevoluciones.setForeground(new java.awt.Color(255, 255, 255));
        btnVentasDevoluciones.setText("Ventas del día y Devoluciones");
        btnVentasDevoluciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVentasDevolucionesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel29Layout = new javax.swing.GroupLayout(jPanel29);
        jPanel29.setLayout(jPanel29Layout);
        jPanel29Layout.setHorizontalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel29Layout.createSequentialGroup()
                .addContainerGap(119, Short.MAX_VALUE)
                .addComponent(btnReimprimirUltimoTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnVentasDevoluciones, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel29Layout.setVerticalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnReimprimirUltimoTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnVentasDevoluciones, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnHacerCorte2.setBackground(new MyColor().COLOR_BUTTON);
        btnHacerCorte2.setForeground(new java.awt.Color(255, 255, 255));
        btnHacerCorte2.setText("limpiar");
        btnHacerCorte2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHacerCorte2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addComponent(jLabel29))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(lblTotalInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(lblPagoConInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblCambioInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHacerCorte2, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel26)
                            .addComponent(jLabel27)
                            .addComponent(jLabel29))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblTotalInfo)
                            .addComponent(lblPagoConInfo)
                            .addComponent(lblCambioInfo)
                            .addComponent(btnHacerCorte2, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29))
        );

        VentasPanel.add(jPanel5);

        ClientesPanel.setBackground(new java.awt.Color(255, 255, 255));
        ClientesPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        ClientesPanel.setPreferredSize(new java.awt.Dimension(812, 582));
        ClientesPanel.setLayout(new javax.swing.BoxLayout(ClientesPanel, javax.swing.BoxLayout.Y_AXIS));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setBackground(new java.awt.Color(182, 210, 248));
        jLabel3.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(135, 65, 155));
        jLabel3.setText("   CRÉDITO A CLIENTES");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3)
        );

        ClientesPanel.add(jPanel2);

        jTabbedPane4.setForeground(new java.awt.Color(135, 65, 155));
        jTabbedPane4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTabbedPane4.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTabbedPane4PropertyChange(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        btnExportarClientes.setBackground(new MyColor().COLOR_BUTTON);
        btnExportarClientes.setForeground(new java.awt.Color(255, 255, 255));
        btnExportarClientes.setText("Exportar a Excel");
        btnExportarClientes.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnExportarClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarClientesActionPerformed(evt);
            }
        });

        dtClieListado.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        dtClieListado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Nombre", "Dirección", "Telefono", "Límite de Crédito", "Saldo Actual", "Último Pago"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        dtClieListado.setGridColor(new java.awt.Color(255, 255, 255));
        dtClieListado.setRowHeight(20);
        dtClieListado.setShowHorizontalLines(false);
        dtClieListado.setShowVerticalLines(false);
        dtClieListado.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dtClieListadoMouseClicked(evt);
            }
        });
        jScrollPaneListadoCliente.setViewportView(dtClieListado);
        if (dtClieListado.getColumnModel().getColumnCount() > 0) {
            dtClieListado.getColumnModel().getColumn(0).setMinWidth(200);
            dtClieListado.getColumnModel().getColumn(0).setMaxWidth(300);
            dtClieListado.getColumnModel().getColumn(1).setMinWidth(220);
            dtClieListado.getColumnModel().getColumn(1).setMaxWidth(250);
            dtClieListado.getColumnModel().getColumn(2).setMinWidth(70);
            dtClieListado.getColumnModel().getColumn(2).setMaxWidth(90);
            dtClieListado.getColumnModel().getColumn(3).setMinWidth(100);
            dtClieListado.getColumnModel().getColumn(3).setMaxWidth(150);
            dtClieListado.getColumnModel().getColumn(4).setMinWidth(50);
            dtClieListado.getColumnModel().getColumn(4).setMaxWidth(100);
            dtClieListado.getColumnModel().getColumn(5).setMinWidth(100);
            dtClieListado.getColumnModel().getColumn(5).setMaxWidth(150);
        }

        jPanel12.setBackground(new java.awt.Color(247, 246, 246));

        jLabel15.setForeground(new java.awt.Color(135, 65, 155));
        jLabel15.setText("Nombre Completo:");

        txtnombreCliente.setForeground(new java.awt.Color(135, 65, 155));
        txtnombreCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtnombreClienteKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtnombreClienteKeyTyped(evt);
            }
        });

        txtdireccionCliente.setForeground(new java.awt.Color(135, 65, 155));
        txtdireccionCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtdireccionClienteKeyPressed(evt);
            }
        });

        jLabel16.setForeground(new java.awt.Color(135, 65, 155));
        jLabel16.setText("Dirección:");

        jLabel17.setForeground(new java.awt.Color(135, 65, 155));
        jLabel17.setText("Teléfono:");

        txttelefonoCliente.setForeground(new java.awt.Color(135, 65, 155));
        txttelefonoCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txttelefonoClienteKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txttelefonoClienteKeyTyped(evt);
            }
        });

        jLabel18.setForeground(new java.awt.Color(135, 65, 155));
        jLabel18.setText("Límite de Credito");

        txtcreditoCliente.setForeground(new java.awt.Color(135, 65, 155));
        txtcreditoCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtcreditoClienteKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtcreditoClienteKeyTyped(evt);
            }
        });

        btnnuevoCliente.setBackground(new MyColor().COLOR_BUTTON);
        btnnuevoCliente.setForeground(new java.awt.Color(255, 255, 255));
        btnnuevoCliente.setText("Nuevo");
        btnnuevoCliente.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnnuevoCliente.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnnuevoCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnnuevoClienteActionPerformed(evt);
            }
        });

        btnguardarCliente.setBackground(new MyColor().COLOR_BUTTON);
        btnguardarCliente.setForeground(new java.awt.Color(255, 255, 255));
        btnguardarCliente.setText("Guardar");
        btnguardarCliente.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnguardarCliente.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnguardarCliente.setMaximumSize(new java.awt.Dimension(90, 24));
        btnguardarCliente.setMinimumSize(new java.awt.Dimension(90, 24));
        btnguardarCliente.setPreferredSize(new java.awt.Dimension(90, 24));
        btnguardarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnguardarClienteActionPerformed(evt);
            }
        });

        btncancelarCliente.setBackground(new MyColor().COLOR_BUTTON);
        btncancelarCliente.setForeground(new java.awt.Color(255, 255, 255));
        btncancelarCliente.setText("Cancelar");
        btncancelarCliente.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btncancelarCliente.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btncancelarCliente.setPreferredSize(new java.awt.Dimension(67, 24));
        btncancelarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncancelarClienteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtidcliente, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel17)
                        .addComponent(jLabel16)
                        .addComponent(jLabel15)
                        .addGroup(jPanel12Layout.createSequentialGroup()
                            .addComponent(jLabel18)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txtcreditoCliente, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE))
                        .addComponent(txtnombreCliente)
                        .addComponent(txtdireccionCliente)
                        .addComponent(txttelefonoCliente)))
                .addContainerGap(33, Short.MAX_VALUE))
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(btnnuevoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnguardarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(btncancelarCliente, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(txtidcliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtnombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtdireccionCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txttelefonoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(txtcreditoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnnuevoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnguardarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btncancelarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(156, Short.MAX_VALUE))
        );

        jLabel1.setForeground(new java.awt.Color(135, 65, 155));
        jLabel1.setText("Nombre:");

        txtbuscarClientes.setForeground(new java.awt.Color(135, 65, 155));
        txtbuscarClientes.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtbuscarClientesKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtbuscarClientesKeyTyped(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(135, 65, 155));
        jLabel6.setText("Total de registros:");

        lblTotalRegistros.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblTotalRegistros.setForeground(new java.awt.Color(135, 65, 155));
        lblTotalRegistros.setText("lblTotalRegistros");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(135, 65, 155));
        jLabel14.setText("Total de Créditos pendientes:");

        lblTotalRegistros1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblTotalRegistros1.setForeground(new java.awt.Color(135, 65, 155));
        lblTotalRegistros1.setText("lblTotalCreditosPendientes");

        btneliminarCliente.setBackground(new MyColor().COLOR_BUTTON);
        btneliminarCliente.setForeground(new java.awt.Color(255, 255, 255));
        btneliminarCliente.setText("Eliminar");
        btneliminarCliente.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btneliminarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btneliminarClienteActionPerformed(evt);
            }
        });

        btnImprimirLista.setBackground(new MyColor().COLOR_BUTTON);
        btnImprimirLista.setForeground(new java.awt.Color(255, 255, 255));
        btnImprimirLista.setText("IMPRIMIR LISTA");
        btnImprimirLista.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnImprimirLista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirListaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneListadoCliente)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtbuscarClientes)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnExportarClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btneliminarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnImprimirLista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblTotalRegistros, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblTotalRegistros1, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnExportarClientes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(txtbuscarClientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnImprimirLista, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btneliminarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPaneListadoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 424, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(lblTotalRegistros)
                    .addComponent(jLabel14)
                    .addComponent(lblTotalRegistros1))
                .addGap(22, 22, 22))
        );

        jTabbedPane4.addTab("LISTADO", jPanel3);

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));

        dtClieCredito.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        dtClieCredito.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "DESCRIPCIÓN", "PRECIO VENTA", "PRECIO COSTO", "IMPORTE"
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
        dtClieCredito.setGridColor(new java.awt.Color(255, 255, 255));
        dtClieCredito.setRowHeight(20);
        dtClieCredito.setShowHorizontalLines(false);
        dtClieCredito.setShowVerticalLines(false);
        dtClieCredito.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dtClieCreditoMouseClicked(evt);
            }
        });
        jScrollPaneEstadoCuenta.setViewportView(dtClieCredito);

        jLabel19.setForeground(new java.awt.Color(135, 65, 155));
        jLabel19.setText("Nombre:");

        jLabel20.setForeground(new java.awt.Color(135, 65, 155));
        jLabel20.setText("Saldo Actual:");

        jLabel21.setForeground(new java.awt.Color(135, 65, 155));
        jLabel21.setText("Límite de Crédito:");

        lblNombreCliente.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblNombreCliente.setText("lblNombreCliente");

        lblSaldoActualCliente.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblSaldoActualCliente.setText("lblSaldoActualCliente");

        lblLimiteCreditoCliente.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblLimiteCreditoCliente.setText("lblLimiteCreditoCliente");

        lblTotalImporteCredito.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblTotalImporteCredito.setForeground(new java.awt.Color(135, 65, 155));
        lblTotalImporteCredito.setText("$0.00");

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(135, 65, 155));
        jLabel23.setText("Total:");

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        jTree1.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane2.setViewportView(jTree1);

        btnAbonarCliente.setBackground(new MyColor().COLOR_BUTTON);
        btnAbonarCliente.setForeground(new java.awt.Color(255, 255, 255));
        btnAbonarCliente.setText("abonar");
        btnAbonarCliente.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnAbonarCliente.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnAbonarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbonarClienteActionPerformed(evt);
            }
        });

        btnDetAbono.setBackground(new MyColor().COLOR_BUTTON);
        btnDetAbono.setForeground(new java.awt.Color(255, 255, 255));
        btnDetAbono.setText("Detalle de Abonos");
        btnDetAbono.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnDetAbono.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnDetAbono.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDetAbonoActionPerformed(evt);
            }
        });

        btnLiquidarAdeudo.setBackground(new MyColor().COLOR_BUTTON);
        btnLiquidarAdeudo.setForeground(new java.awt.Color(255, 255, 255));
        btnLiquidarAdeudo.setText("Liquidar a Deudo");
        btnLiquidarAdeudo.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnLiquidarAdeudo.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnLiquidarAdeudo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLiquidarAdeudoActionPerformed(evt);
            }
        });

        btnImprimirEdoCuentaCompleto.setBackground(new MyColor().COLOR_BUTTON);
        btnImprimirEdoCuentaCompleto.setForeground(new java.awt.Color(255, 255, 255));
        btnImprimirEdoCuentaCompleto.setText("Imprimir Edo. Cuenta Completo");
        btnImprimirEdoCuentaCompleto.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnImprimirEdoCuentaCompleto.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnImprimirEdoCuentaCompleto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirEdoCuentaCompletoActionPerformed(evt);
            }
        });

        btnEliminarTicketCliente.setBackground(new MyColor().COLOR_BUTTON);
        btnEliminarTicketCliente.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminarTicketCliente.setText("Eliminar Ticket");
        btnEliminarTicketCliente.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnEliminarTicketCliente.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnEliminarTicketCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarTicketClienteActionPerformed(evt);
            }
        });

        btnImprimirTicketTab.setBackground(new MyColor().COLOR_BUTTON);
        btnImprimirTicketTab.setForeground(new java.awt.Color(255, 255, 255));
        btnImprimirTicketTab.setText("Imprimir Ticket");
        btnImprimirTicketTab.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnImprimirTicketTab.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnImprimirTicketTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirTicketTabActionPerformed(evt);
            }
        });

        lblSeleccionaUnCliente.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        lblSeleccionaUnCliente.setText("¡No hay cliente seleccionado!");

        jLabel80.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel80.setForeground(new java.awt.Color(135, 65, 155));
        jLabel80.setText("Impuestos:");

        lblImpuestos.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblImpuestos.setForeground(new java.awt.Color(135, 65, 155));
        lblImpuestos.setText("$0.00");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel80)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblImpuestos, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTotalImporteCredito, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEliminarTicketCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnImprimirTicketTab, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPaneEstadoCuenta)))
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblLimiteCreditoCliente))
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(btnAbonarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnLiquidarAdeudo, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnDetAbono, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addGap(18, 18, 18)
                                .addComponent(lblSaldoActualCliente)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnImprimirEdoCuentaCompleto, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(123, 123, 123)
                        .addComponent(lblSeleccionaUnCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel19)
                            .addComponent(lblNombreCliente)))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblSeleccionaUnCliente)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(lblSaldoActualCliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(lblLimiteCreditoCliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnLiquidarAdeudo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnAbonarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnDetAbono, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnImprimirEdoCuentaCompleto, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jScrollPaneEstadoCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblImpuestos)
                                    .addComponent(jLabel80))
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblTotalImporteCredito)
                                    .addComponent(jLabel23))
                                .addComponent(btnEliminarTicketCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnImprimirTicketTab, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 713, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane4.addTab("ESTADO DE CUENTA", jPanel11);

        ClientesPanel.add(jTabbedPane4);

        ProductosPanel.setBackground(new java.awt.Color(255, 255, 255));
        ProductosPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        ProductosPanel.setPreferredSize(new java.awt.Dimension(812, 582));
        ProductosPanel.setLayout(new javax.swing.BoxLayout(ProductosPanel, javax.swing.BoxLayout.Y_AXIS));

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));

        jLabel25.setBackground(new java.awt.Color(182, 210, 248));
        jLabel25.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(135, 65, 155));
        jLabel25.setText("   PRODUCTOS");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel25)
        );

        ProductosPanel.add(jPanel8);

        jTabbedPane2.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane2.setForeground(new java.awt.Color(135, 65, 155));
        jTabbedPane2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTabbedPane2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane2StateChanged(evt);
            }
        });
        jTabbedPane2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedPane2MouseClicked(evt);
            }
        });

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        dtProListado.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        dtProListado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Código de Barras", "Descripción", "Precio Costo", "Precio Venta", "Precio Mayoreo", "Categoría", "Cant. Actual", "Mínimo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        dtProListado.setGridColor(new java.awt.Color(255, 255, 255));
        dtProListado.setRowHeight(20);
        dtProListado.setSelectionBackground(new java.awt.Color(102, 204, 255));
        dtProListado.setSelectionForeground(new java.awt.Color(0, 0, 0));
        dtProListado.setShowHorizontalLines(false);
        dtProListado.setShowVerticalLines(false);
        dtProListado.setUpdateSelectionOnSort(false);
        dtProListado.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dtProListadoMouseClicked(evt);
            }
        });
        jScrollPaneListadoProducto.setViewportView(dtProListado);
        if (dtProListado.getColumnModel().getColumnCount() > 0) {
            dtProListado.getColumnModel().getColumn(0).setMinWidth(200);
            dtProListado.getColumnModel().getColumn(0).setMaxWidth(300);
            dtProListado.getColumnModel().getColumn(1).setMinWidth(220);
            dtProListado.getColumnModel().getColumn(1).setMaxWidth(250);
            dtProListado.getColumnModel().getColumn(2).setMinWidth(70);
            dtProListado.getColumnModel().getColumn(2).setMaxWidth(90);
            dtProListado.getColumnModel().getColumn(3).setMinWidth(100);
            dtProListado.getColumnModel().getColumn(3).setMaxWidth(150);
            dtProListado.getColumnModel().getColumn(4).setMinWidth(100);
            dtProListado.getColumnModel().getColumn(4).setMaxWidth(120);
            dtProListado.getColumnModel().getColumn(5).setMinWidth(130);
            dtProListado.getColumnModel().getColumn(5).setMaxWidth(150);
            dtProListado.getColumnModel().getColumn(6).setMinWidth(50);
            dtProListado.getColumnModel().getColumn(6).setMaxWidth(80);
            dtProListado.getColumnModel().getColumn(7).setMinWidth(50);
            dtProListado.getColumnModel().getColumn(7).setMaxWidth(1000);
        }

        jLabel24.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel24.setText("Código del Producto:");

        txtBuscarProductos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarProductosKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtBuscarProductosKeyTyped(evt);
            }
        });

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel28.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel28.setText("Total de registros:");

        lblTotalRegistros2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblTotalRegistros2.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        lblTotalRegistros2.setText("lblTotalRegistros");

        btnEliminarProducto.setBackground(new MyColor().COLOR_BUTTON);
        btnEliminarProducto.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminarProducto.setText("Eliminar");
        btnEliminarProducto.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnEliminarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarProductoActionPerformed(evt);
            }
        });

        btnImportarProducto.setBackground(new MyColor().COLOR_BUTTON);
        btnImportarProducto.setForeground(new java.awt.Color(255, 255, 255));
        btnImportarProducto.setText("importar");
        btnImportarProducto.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnImportarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportarProductoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(94, 94, 94)
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscarProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTotalRegistros2, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 146, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnImportarProducto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminarProducto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPaneListadoProducto, javax.swing.GroupLayout.DEFAULT_SIZE, 805, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(txtBuscarProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 388, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnImportarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel28)
                        .addComponent(lblTotalRegistros2)))
                .addContainerGap())
            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel9Layout.createSequentialGroup()
                    .addGap(91, 91, 91)
                    .addComponent(jScrollPaneListadoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(55, Short.MAX_VALUE)))
        );

        jTabbedPane2.addTab("Listado", jPanel9);

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(135, 65, 155));
        jLabel30.setText("Nº Serie");

        txtNoSeriePro.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNoSeriePro.setForeground(new java.awt.Color(135, 65, 155));
        txtNoSeriePro.setBorder(new javax.swing.border.LineBorder(new MyColor().COLOR_JLABEL_PRIMARY, 1, true));
        txtNoSeriePro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtNoSerieProKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNoSerieProKeyTyped(evt);
            }
        });

        txtdescripcionPro.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtdescripcionPro.setForeground(new java.awt.Color(135, 65, 155));
        txtdescripcionPro.setBorder(new javax.swing.border.LineBorder(new MyColor().COLOR_JLABEL_PRIMARY, 1, true));

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(135, 65, 155));
        jLabel31.setText("Descripción:");

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(135, 65, 155));
        jLabel32.setText("Precio Costo:");

        txtprecioCostoPro.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtprecioCostoPro.setForeground(new java.awt.Color(135, 65, 155));
        txtprecioCostoPro.setBorder(new javax.swing.border.LineBorder(new MyColor().COLOR_JLABEL_PRIMARY, 1, true));
        txtprecioCostoPro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtprecioCostoProKeyTyped(evt);
            }
        });

        jLabel36.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(135, 65, 155));
        jLabel36.setText("Precio Venta:");

        txtprecioVentaPro.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtprecioVentaPro.setForeground(new java.awt.Color(135, 65, 155));
        txtprecioVentaPro.setBorder(new javax.swing.border.LineBorder(new MyColor().COLOR_JLABEL_PRIMARY, 1, true));
        txtprecioVentaPro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtprecioVentaProKeyTyped(evt);
            }
        });

        txtprecioMayoreoPro.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtprecioMayoreoPro.setForeground(new java.awt.Color(135, 65, 155));
        txtprecioMayoreoPro.setBorder(new javax.swing.border.LineBorder(new MyColor().COLOR_JLABEL_PRIMARY, 1, true));
        txtprecioMayoreoPro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtprecioMayoreoProKeyTyped(evt);
            }
        });

        jLabel37.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(135, 65, 155));
        jLabel37.setText("Precio Mayoreo:");

        jLabel38.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(135, 65, 155));
        jLabel38.setText("Categoría:");

        cbocategoriaPro.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cbocategoriaPro.setForeground(new java.awt.Color(135, 65, 155));
        cbocategoriaPro.setBorder(new javax.swing.border.LineBorder(new MyColor().COLOR_JLABEL_PRIMARY, 1, true));

        txtcantidadActualPro.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtcantidadActualPro.setForeground(new java.awt.Color(135, 65, 155));
        txtcantidadActualPro.setBorder(new javax.swing.border.LineBorder(new MyColor().COLOR_JLABEL_PRIMARY, 1, true));
        txtcantidadActualPro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtcantidadActualProKeyTyped(evt);
            }
        });

        jLabel39.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(135, 65, 155));
        jLabel39.setText("Cantidad Actual:");

        jLabel41.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(135, 65, 155));
        jLabel41.setText("Mínimo:");

        txtcantidadMinimoPro.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtcantidadMinimoPro.setForeground(new java.awt.Color(135, 65, 155));
        txtcantidadMinimoPro.setBorder(new javax.swing.border.LineBorder(new MyColor().COLOR_JLABEL_PRIMARY, 1, true));
        txtcantidadMinimoPro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtcantidadMinimoProKeyTyped(evt);
            }
        });

        txtidproductos.setForeground(new java.awt.Color(135, 65, 155));

        btnnuevoProducto.setBackground(new MyColor().COLOR_BUTTON);
        btnnuevoProducto.setForeground(new java.awt.Color(255, 255, 255));
        btnnuevoProducto.setText("nuevo");
        btnnuevoProducto.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnnuevoProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnnuevoProductoActionPerformed(evt);
            }
        });

        btnguardarProducto.setBackground(new MyColor().COLOR_BUTTON);
        btnguardarProducto.setForeground(new java.awt.Color(255, 255, 255));
        btnguardarProducto.setText("Guardar");
        btnguardarProducto.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnguardarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnguardarProductoActionPerformed(evt);
            }
        });

        btnProCancelar.setBackground(new MyColor().COLOR_BUTTON);
        btnProCancelar.setForeground(new java.awt.Color(255, 255, 255));
        btnProCancelar.setText("Cancelar");
        btnProCancelar.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnProCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel36)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtprecioVentaPro, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel32)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtprecioCostoPro, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel31)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtdescripcionPro, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel10Layout.createSequentialGroup()
                                    .addComponent(jLabel39)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtcantidadActualPro, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel10Layout.createSequentialGroup()
                                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel37)
                                        .addComponent(jLabel38))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(cbocategoriaPro, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(txtprecioMayoreoPro, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel41)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtcantidadMinimoPro, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel30)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtidproductos, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtNoSeriePro, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(btnnuevoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnguardarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnProCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(440, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(txtidproductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(txtNoSeriePro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(txtdescripcionPro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(txtprecioCostoPro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36)
                    .addComponent(txtprecioVentaPro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel37)
                    .addComponent(txtprecioMayoreoPro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel38)
                    .addComponent(cbocategoriaPro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel39)
                    .addComponent(txtcantidadActualPro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel41)
                    .addComponent(txtcantidadMinimoPro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnnuevoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnguardarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnProCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(244, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Mantenimiento", jPanel10);

        jPanel13.setBackground(new java.awt.Color(255, 255, 255));

        dtProVentasPedido.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        dtProVentasPedido.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Código de Barras", "Descripción del Producto", "Precio Costo", "Precio Venta", "Categoría"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        dtProVentasPedido.setGridColor(new java.awt.Color(255, 255, 255));
        dtProVentasPedido.setRowHeight(20);
        dtProVentasPedido.setShowHorizontalLines(false);
        dtProVentasPedido.setShowVerticalLines(false);
        jScrollPaneVentasVendido.setViewportView(dtProVentasPedido);
        if (dtProVentasPedido.getColumnModel().getColumnCount() > 0) {
            dtProVentasPedido.getColumnModel().getColumn(0).setMinWidth(200);
            dtProVentasPedido.getColumnModel().getColumn(0).setMaxWidth(300);
            dtProVentasPedido.getColumnModel().getColumn(1).setMinWidth(220);
            dtProVentasPedido.getColumnModel().getColumn(1).setMaxWidth(250);
            dtProVentasPedido.getColumnModel().getColumn(2).setMinWidth(70);
            dtProVentasPedido.getColumnModel().getColumn(2).setMaxWidth(90);
            dtProVentasPedido.getColumnModel().getColumn(3).setMinWidth(100);
            dtProVentasPedido.getColumnModel().getColumn(3).setMaxWidth(150);
            dtProVentasPedido.getColumnModel().getColumn(4).setMinWidth(130);
            dtProVentasPedido.getColumnModel().getColumn(4).setMaxWidth(150);
        }

        jLabel103.setForeground(new java.awt.Color(135, 65, 155));
        jLabel103.setText("REPORTE DE PRODUCTOS VENDIDOS");

        jLabel104.setForeground(new java.awt.Color(135, 65, 155));
        jLabel104.setText("Mostrar Ventas de:");

        cmbox.setForeground(new java.awt.Color(135, 65, 155));
        cmbox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Hoy", "Ayer", "Esta semana", "La semana pasada", "Del mes", "De un periodo en particular" }));
        cmbox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmboxItemStateChanged(evt);
            }
        });

        periodoFecha.setBackground(new java.awt.Color(255, 255, 255));
        periodoFecha.setForeground(new java.awt.Color(135, 65, 155));

        fechachooser_hasta.setForeground(new java.awt.Color(135, 65, 155));
        fechachooser_hasta.setDate(new Date());
        fechachooser_hasta.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                fechachooser_hastaPropertyChange(evt);
            }
        });

        jLabel101.setForeground(new java.awt.Color(135, 65, 155));
        jLabel101.setText("Hasta:");

        fechachooser_desde.setForeground(new java.awt.Color(135, 65, 155));
        fechachooser_desde.setDate(new Date());
        fechachooser_desde.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                fechachooser_desdePropertyChange(evt);
            }
        });

        jLabel100.setForeground(new java.awt.Color(135, 65, 155));
        jLabel100.setText("Desde:");

        javax.swing.GroupLayout periodoFechaLayout = new javax.swing.GroupLayout(periodoFecha);
        periodoFecha.setLayout(periodoFechaLayout);
        periodoFechaLayout.setHorizontalGroup(
            periodoFechaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, periodoFechaLayout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addGroup(periodoFechaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel100)
                    .addComponent(fechachooser_desde, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(periodoFechaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fechachooser_hasta, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(periodoFechaLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel101)))
                .addContainerGap())
        );
        periodoFechaLayout.setVerticalGroup(
            periodoFechaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, periodoFechaLayout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addGroup(periodoFechaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(periodoFechaLayout.createSequentialGroup()
                        .addComponent(jLabel100)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fechachooser_desde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(periodoFechaLayout.createSequentialGroup()
                        .addComponent(jLabel101)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fechachooser_hasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        btnExportarVentasPedido.setBackground(new MyColor().COLOR_BUTTON);
        btnExportarVentasPedido.setForeground(new java.awt.Color(255, 255, 255));
        btnExportarVentasPedido.setText("Exportar");
        btnExportarVentasPedido.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnExportarVentasPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarVentasPedidoActionPerformed(evt);
            }
        });

        btnImprimirProPedido.setBackground(new MyColor().COLOR_BUTTON);
        btnImprimirProPedido.setForeground(new java.awt.Color(255, 255, 255));
        btnImprimirProPedido.setText("imprimir");
        btnImprimirProPedido.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnImprimirProPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirProPedidoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPaneVentasVendido)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel103)
                    .addComponent(jLabel104)
                    .addComponent(cmbox, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(periodoFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addComponent(btnExportarVentasPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnImprimirProPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel103)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel104)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(periodoFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnExportarVentasPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnImprimirProPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneVentasVendido, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Ventas por Pedido", jPanel13);

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));

        jLabel89.setForeground(new java.awt.Color(153, 153, 153));
        jLabel89.setText("Precio Costo:");

        txtnoseriePromocion.setForeground(new java.awt.Color(135, 65, 155));
        txtnoseriePromocion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtnoseriePromocionKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtnoseriePromocionKeyTyped(evt);
            }
        });

        lblPrecioNormal.setBackground(new java.awt.Color(255, 255, 255));
        lblPrecioNormal.setForeground(new java.awt.Color(153, 153, 153));
        lblPrecioNormal.setText("lblPrecioNormal");

        jLabel44.setForeground(new java.awt.Color(135, 65, 155));
        jLabel44.setText("No. Serie:");

        lblPrecioCosto.setBackground(new java.awt.Color(255, 255, 255));
        lblPrecioCosto.setForeground(new java.awt.Color(153, 153, 153));
        lblPrecioCosto.setText("lblPrecioCosto");

        jLabel45.setForeground(new java.awt.Color(135, 65, 155));
        jLabel45.setText("Cuando la Cantidad vaya:");

        txtdesdePromocion.setForeground(new java.awt.Color(135, 65, 155));
        txtdesdePromocion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtdesdePromocionKeyTyped(evt);
            }
        });

        dtProPromocion.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        dtProPromocion.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Código de Barras", "Descripción", "Precio Costo", "Precio Venta", "Precio Mayoreo", "Categoría", "Cant. Actual", "Mínimo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        dtProPromocion.setGridColor(new java.awt.Color(255, 255, 255));
        dtProPromocion.setRowHeight(20);
        dtProPromocion.setShowHorizontalLines(false);
        dtProPromocion.setShowVerticalLines(false);
        jScrollPanePromocion.setViewportView(dtProPromocion);
        if (dtProPromocion.getColumnModel().getColumnCount() > 0) {
            dtProPromocion.getColumnModel().getColumn(0).setMinWidth(200);
            dtProPromocion.getColumnModel().getColumn(0).setMaxWidth(300);
            dtProPromocion.getColumnModel().getColumn(1).setMinWidth(220);
            dtProPromocion.getColumnModel().getColumn(1).setMaxWidth(250);
            dtProPromocion.getColumnModel().getColumn(2).setMinWidth(70);
            dtProPromocion.getColumnModel().getColumn(2).setMaxWidth(90);
            dtProPromocion.getColumnModel().getColumn(3).setMinWidth(100);
            dtProPromocion.getColumnModel().getColumn(3).setMaxWidth(150);
            dtProPromocion.getColumnModel().getColumn(4).setMinWidth(100);
            dtProPromocion.getColumnModel().getColumn(4).setMaxWidth(120);
            dtProPromocion.getColumnModel().getColumn(5).setMinWidth(130);
            dtProPromocion.getColumnModel().getColumn(5).setMaxWidth(150);
            dtProPromocion.getColumnModel().getColumn(6).setMinWidth(50);
            dtProPromocion.getColumnModel().getColumn(6).setMaxWidth(80);
            dtProPromocion.getColumnModel().getColumn(7).setMinWidth(50);
            dtProPromocion.getColumnModel().getColumn(7).setMaxWidth(1000);
        }

        jLabel46.setForeground(new java.awt.Color(135, 65, 155));
        jLabel46.setText("Desde:");

        txthastaPromocion.setForeground(new java.awt.Color(135, 65, 155));
        txthastaPromocion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txthastaPromocionKeyTyped(evt);
            }
        });

        jLabel69.setForeground(new java.awt.Color(135, 65, 155));
        jLabel69.setText("Hasta:");

        jLabel70.setForeground(new java.awt.Color(135, 65, 155));
        jLabel70.setText("Usar Precio Unitario:");

        jLabel42.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(135, 65, 155));
        jLabel42.setText("NUEVA PROMOCIÓN");

        txtprecioPromocion.setForeground(new java.awt.Color(135, 65, 155));
        txtprecioPromocion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtprecioPromocionKeyTyped(evt);
            }
        });

        jLabel43.setForeground(new java.awt.Color(135, 65, 155));
        jLabel43.setText("Nombre:");

        jLabel71.setForeground(new java.awt.Color(153, 153, 153));
        jLabel71.setText("Precio Normal:");

        txtnombrePromocion.setForeground(new java.awt.Color(135, 65, 155));
        txtnombrePromocion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtnombrePromocionKeyTyped(evt);
            }
        });

        lblDescripcionPromocion.setBackground(new java.awt.Color(255, 255, 255));
        lblDescripcionPromocion.setForeground(new java.awt.Color(153, 153, 153));
        lblDescripcionPromocion.setText("lblDescripcionPromocion");

        btnEliminarPromocionPro.setBackground(new MyColor().COLOR_BUTTON);
        btnEliminarPromocionPro.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminarPromocionPro.setText("eliminar");
        btnEliminarPromocionPro.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnEliminarPromocionPro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarPromocionProActionPerformed(evt);
            }
        });

        btnGuardarPromocion.setBackground(new MyColor().COLOR_BUTTON);
        btnGuardarPromocion.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardarPromocion.setText("Guardar Nueva promocion");
        btnGuardarPromocion.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnGuardarPromocion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarPromocionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPanePromocion)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel14Layout.createSequentialGroup()
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel14Layout.createSequentialGroup()
                                        .addComponent(jLabel44)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtnoseriePromocion, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel14Layout.createSequentialGroup()
                                        .addComponent(jLabel43)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtnombrePromocion, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel14Layout.createSequentialGroup()
                                        .addComponent(jLabel45)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel46)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtdesdePromocion, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel69)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txthastaPromocion, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblDescripcionPromocion, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 309, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel14Layout.createSequentialGroup()
                                .addComponent(jLabel70)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtprecioPromocion, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel14Layout.createSequentialGroup()
                                        .addComponent(jLabel89)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblPrecioCosto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(jPanel14Layout.createSequentialGroup()
                                        .addComponent(jLabel71)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblPrecioNormal, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(91, 91, 91)
                        .addComponent(btnGuardarPromocion, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnEliminarPromocionPro, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel42)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel42)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel43)
                    .addComponent(txtnombrePromocion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDescripcionPromocion))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel44)
                    .addComponent(txtnoseriePromocion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtdesdePromocion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel46)
                        .addComponent(txthastaPromocion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel69))
                    .addComponent(jLabel45))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel70)
                    .addComponent(txtprecioPromocion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel71)
                    .addComponent(lblPrecioNormal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel89)
                    .addComponent(lblPrecioCosto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEliminarPromocionPro, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGuardarPromocion, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPanePromocion, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Promociones", jPanel14);

        jPanel15.setBackground(new java.awt.Color(255, 255, 255));

        jLabel90.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel90.setForeground(new java.awt.Color(135, 65, 155));
        jLabel90.setText("CATEGORÍAS");

        jLabel91.setForeground(new java.awt.Color(135, 65, 155));
        jLabel91.setText("Nombre:");

        txtBuscarCategoria.setForeground(new java.awt.Color(135, 65, 155));
        txtBuscarCategoria.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarCategoriaKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtBuscarCategoriaKeyTyped(evt);
            }
        });

        dtProCategoria.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        dtProCategoria.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Categorías"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        dtProCategoria.setGridColor(new java.awt.Color(255, 255, 255));
        dtProCategoria.setRowHeight(20);
        dtProCategoria.setShowHorizontalLines(false);
        dtProCategoria.setShowVerticalLines(false);
        dtProCategoria.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dtProCategoriaMouseClicked(evt);
            }
        });
        jScrollPaneCategoria.setViewportView(dtProCategoria);
        if (dtProCategoria.getColumnModel().getColumnCount() > 0) {
            dtProCategoria.getColumnModel().getColumn(0).setMinWidth(200);
            dtProCategoria.getColumnModel().getColumn(0).setMaxWidth(300);
        }

        jPanel16.setBackground(new java.awt.Color(247, 246, 246));

        txtnombreCategoria.setForeground(new java.awt.Color(135, 65, 155));
        txtnombreCategoria.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtnombreCategoriaKeyTyped(evt);
            }
        });

        jLabel92.setForeground(new java.awt.Color(135, 65, 155));
        jLabel92.setText("Nombre:");

        btnnuevoCategoria.setBackground(new MyColor().COLOR_BUTTON);
        btnnuevoCategoria.setForeground(new java.awt.Color(255, 255, 255));
        btnnuevoCategoria.setText("nuevo");
        btnnuevoCategoria.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnnuevoCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnnuevoCategoriaActionPerformed(evt);
            }
        });

        btnguardarCategoria.setBackground(new MyColor().COLOR_BUTTON);
        btnguardarCategoria.setForeground(new java.awt.Color(255, 255, 255));
        btnguardarCategoria.setText("guardar");
        btnguardarCategoria.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnguardarCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnguardarCategoriaActionPerformed(evt);
            }
        });

        btncancelarCategoria.setBackground(new MyColor().COLOR_BUTTON);
        btncancelarCategoria.setForeground(new java.awt.Color(255, 255, 255));
        btncancelarCategoria.setText("cancelar");
        btncancelarCategoria.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btncancelarCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncancelarCategoriaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jLabel92)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtnombreCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(btnnuevoCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnguardarCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btncancelarCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(79, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel92)
                    .addComponent(txtnombreCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnnuevoCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnguardarCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btncancelarCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtidcategoria.setForeground(new java.awt.Color(135, 65, 155));

        btneliminarCategoria.setBackground(new MyColor().COLOR_BUTTON);
        btneliminarCategoria.setForeground(new java.awt.Color(255, 255, 255));
        btneliminarCategoria.setText("eliminar");
        btneliminarCategoria.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btneliminarCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btneliminarCategoriaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jLabel90)
                        .addGap(18, 18, 18)
                        .addComponent(txtidcategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jLabel91)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscarCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPaneCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btneliminarCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(81, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel90)
                    .addComponent(txtidcategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel91)
                    .addComponent(txtBuscarCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btneliminarCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPaneCategoria, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE))
                .addGap(139, 139, 139))
        );

        jTabbedPane2.addTab("Categorías", jPanel15);

        ProductosPanel.add(jTabbedPane2);

        InventarioPanel.setBackground(new java.awt.Color(255, 255, 255));
        InventarioPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        InventarioPanel.setPreferredSize(new java.awt.Dimension(812, 582));
        InventarioPanel.setLayout(new javax.swing.BoxLayout(InventarioPanel, javax.swing.BoxLayout.Y_AXIS));

        jPanel18.setBackground(new java.awt.Color(255, 255, 255));

        jLabel102.setBackground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel102.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        jLabel102.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel102.setText("   INVENTARIO");

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addComponent(jLabel102, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel102)
        );

        InventarioPanel.add(jPanel18);

        buttonsPanel8.setBackground(new MyColor().COLOR_HEADER_BUTTON2);
        buttonsPanel8.setForeground(new java.awt.Color(255, 255, 255));
        buttonsPanel8.setPreferredSize(new java.awt.Dimension(1008, 42));

        buttonsPanel9.setBackground(new MyColor().COLOR_HEADER_BUTTON2);
        buttonsPanel9.setPreferredSize(new java.awt.Dimension(1008, 42));
        buttonsPanel9.setLayout(new javax.swing.BoxLayout(buttonsPanel9, javax.swing.BoxLayout.LINE_AXIS));

        btnAgregarInv.setBackground(new java.awt.Color(214, 237, 236));
        btnAgregarInv.setForeground(new java.awt.Color(255, 255, 255));
        btnAgregarInv.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/agregarInv32G.png"))); // NOI18N
        btnAgregarInv.setText("Agregar a Inv.");
        btnAgregarInv.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnAgregarInv.setBorderPainted(false);
        btnAgregarInv.setContentAreaFilled(false);
        btnAgregarInv.setFocusPainted(false);
        btnAgregarInv.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        btnAgregarInv.setMaximumSize(new java.awt.Dimension(120, 42));
        btnAgregarInv.setMinimumSize(new java.awt.Dimension(120, 42));
        btnAgregarInv.setPreferredSize(new java.awt.Dimension(120, 42));
        btnAgregarInv.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/btnCursor2.png"))); // NOI18N
        btnAgregarInv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarInvActionPerformed(evt);
            }
        });
        buttonsPanel9.add(btnAgregarInv);

        btnAjustesInv.setBackground(new java.awt.Color(214, 237, 236));
        btnAjustesInv.setForeground(new java.awt.Color(255, 255, 255));
        btnAjustesInv.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/editarInv32G.png"))); // NOI18N
        btnAjustesInv.setText("Ajustes");
        btnAjustesInv.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnAjustesInv.setBorderPainted(false);
        btnAjustesInv.setContentAreaFilled(false);
        btnAjustesInv.setFocusPainted(false);
        btnAjustesInv.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        btnAjustesInv.setMaximumSize(new java.awt.Dimension(90, 42));
        btnAjustesInv.setMinimumSize(new java.awt.Dimension(90, 42));
        btnAjustesInv.setPreferredSize(new java.awt.Dimension(90, 42));
        btnAjustesInv.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/btnCursor2.png"))); // NOI18N
        btnAjustesInv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAjustesInvActionPerformed(evt);
            }
        });
        buttonsPanel9.add(btnAjustesInv);

        btnProdInventarioBajos.setBackground(new java.awt.Color(214, 237, 236));
        btnProdInventarioBajos.setForeground(new java.awt.Color(255, 255, 255));
        btnProdInventarioBajos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/bajoInv32G.png"))); // NOI18N
        btnProdInventarioBajos.setText("Productos bajo en inventario");
        btnProdInventarioBajos.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnProdInventarioBajos.setBorderPainted(false);
        btnProdInventarioBajos.setContentAreaFilled(false);
        btnProdInventarioBajos.setFocusPainted(false);
        btnProdInventarioBajos.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        btnProdInventarioBajos.setMaximumSize(new java.awt.Dimension(180, 42));
        btnProdInventarioBajos.setMinimumSize(new java.awt.Dimension(180, 42));
        btnProdInventarioBajos.setPreferredSize(new java.awt.Dimension(180, 42));
        btnProdInventarioBajos.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/btnCursor2.png"))); // NOI18N
        btnProdInventarioBajos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProdInventarioBajosActionPerformed(evt);
            }
        });
        buttonsPanel9.add(btnProdInventarioBajos);

        btnReporteInv.setBackground(new java.awt.Color(214, 237, 236));
        btnReporteInv.setForeground(new java.awt.Color(255, 255, 255));
        btnReporteInv.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/reporteInv32G.png"))); // NOI18N
        btnReporteInv.setText("Reporte de Inventario");
        btnReporteInv.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnReporteInv.setBorderPainted(false);
        btnReporteInv.setContentAreaFilled(false);
        btnReporteInv.setFocusPainted(false);
        btnReporteInv.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        btnReporteInv.setMaximumSize(new java.awt.Dimension(152, 42));
        btnReporteInv.setMinimumSize(new java.awt.Dimension(152, 42));
        btnReporteInv.setPreferredSize(new java.awt.Dimension(152, 42));
        btnReporteInv.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/btnCursor2.png"))); // NOI18N
        btnReporteInv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReporteInvActionPerformed(evt);
            }
        });
        buttonsPanel9.add(btnReporteInv);

        btnReporteMovInv.setBackground(new java.awt.Color(214, 237, 236));
        btnReporteMovInv.setForeground(new java.awt.Color(255, 255, 255));
        btnReporteMovInv.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/reporteMoviInv32G.png"))); // NOI18N
        btnReporteMovInv.setText("Reporte de Movimientos");
        btnReporteMovInv.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnReporteMovInv.setBorderPainted(false);
        btnReporteMovInv.setContentAreaFilled(false);
        btnReporteMovInv.setFocusPainted(false);
        btnReporteMovInv.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        btnReporteMovInv.setMaximumSize(new java.awt.Dimension(162, 42));
        btnReporteMovInv.setMinimumSize(new java.awt.Dimension(162, 42));
        btnReporteMovInv.setPreferredSize(new java.awt.Dimension(162, 42));
        btnReporteMovInv.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/btnCursor2.png"))); // NOI18N
        btnReporteMovInv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReporteMovInvActionPerformed(evt);
            }
        });
        buttonsPanel9.add(btnReporteMovInv);

        javax.swing.GroupLayout buttonsPanel8Layout = new javax.swing.GroupLayout(buttonsPanel8);
        buttonsPanel8.setLayout(buttonsPanel8Layout);
        buttonsPanel8Layout.setHorizontalGroup(
            buttonsPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonsPanel8Layout.createSequentialGroup()
                .addComponent(buttonsPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 903, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        buttonsPanel8Layout.setVerticalGroup(
            buttonsPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(buttonsPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        InventarioPanel.add(buttonsPanel8);

        agregarInvPanel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel40.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel40.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel40.setText("AGREGAR INVENTARIO");

        jPanel27.setBackground(new java.awt.Color(255, 255, 255));

        txtCodigoProAgregarInv.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCodigoProAgregarInvKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCodigoProAgregarInvKeyTyped(evt);
            }
        });

        jLabel48.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel48.setText("Descripción:");

        lbldescripinventario.setText("-");

        lblcantinventario.setText("0");

        jLabel52.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel52.setText("Existencia");

        jLabel105.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel105.setText("Cantidad:");

        txtinputinventario.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtinputinventarioKeyTyped(evt);
            }
        });

        jLabel47.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel47.setText("Código del producto:");

        btninputinventario.setBackground(new MyColor().COLOR_BUTTON);
        btninputinventario.setForeground(new java.awt.Color(255, 255, 255));
        btninputinventario.setText("Agregar Cantidad al Inventario");
        btninputinventario.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btninputinventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btninputinventarioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel27Layout.createSequentialGroup()
                        .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel52)
                                .addComponent(jLabel47)
                                .addComponent(jLabel48))
                            .addComponent(jLabel105, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lbldescripinventario, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtCodigoProAgregarInv, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                            .addComponent(txtinputinventario, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblcantinventario, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 16, Short.MAX_VALUE))
                    .addGroup(jPanel27Layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(btninputinventario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel47)
                    .addComponent(txtCodigoProAgregarInv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel48)
                    .addComponent(lbldescripinventario))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel52)
                    .addComponent(lblcantinventario))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel105)
                    .addComponent(txtinputinventario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btninputinventario, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(124, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout agregarInvPanelLayout = new javax.swing.GroupLayout(agregarInvPanel);
        agregarInvPanel.setLayout(agregarInvPanelLayout);
        agregarInvPanelLayout.setHorizontalGroup(
            agregarInvPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(agregarInvPanelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(agregarInvPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40))
                .addContainerGap(517, Short.MAX_VALUE))
        );
        agregarInvPanelLayout.setVerticalGroup(
            agregarInvPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(agregarInvPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel40)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(201, Short.MAX_VALUE))
        );

        ajusteInvPanel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel106.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel106.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel106.setText("AJUSTAR DE INVENTARIO");

        jPanel28.setBackground(new java.awt.Color(255, 255, 255));

        jLabel107.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel107.setText("Código del producto:");

        txtCodigoProAjuste.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCodigoProAjusteKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCodigoProAjusteKeyTyped(evt);
            }
        });

        jLabel108.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel108.setText("Descripción:");

        lbldesinventario2.setText("-");

        jLabel110.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel110.setText("Existencia");

        lblExistenciaAjuste.setText("0");

        jLabel112.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel112.setText("Nueva Cantidad:");

        txtinputinventario2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtinputinventario2KeyTyped(evt);
            }
        });

        btnajusteinventario.setBackground(new MyColor().COLOR_BUTTON);
        btnajusteinventario.setForeground(new java.awt.Color(255, 255, 255));
        btnajusteinventario.setText("Ajustar el Inventario de este Producto");
        btnajusteinventario.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnajusteinventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnajusteinventarioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel28Layout = new javax.swing.GroupLayout(jPanel28);
        jPanel28.setLayout(jPanel28Layout);
        jPanel28Layout.setHorizontalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel28Layout.createSequentialGroup()
                        .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel110)
                                .addComponent(jLabel107)
                                .addComponent(jLabel108))
                            .addComponent(jLabel112, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblExistenciaAjuste, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtinputinventario2, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(lbldesinventario2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtCodigoProAjuste, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)))
                        .addGap(88, 88, 88))
                    .addGroup(jPanel28Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnajusteinventario, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblIdProductoAjuste, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel28Layout.setVerticalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel107)
                    .addComponent(txtCodigoProAjuste, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel108)
                    .addComponent(lbldesinventario2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel110)
                    .addComponent(lblExistenciaAjuste))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel112)
                    .addComponent(txtinputinventario2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnajusteinventario, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblIdProductoAjuste, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(67, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout ajusteInvPanelLayout = new javax.swing.GroupLayout(ajusteInvPanel);
        ajusteInvPanel.setLayout(ajusteInvPanelLayout);
        ajusteInvPanelLayout.setHorizontalGroup(
            ajusteInvPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ajusteInvPanelLayout.createSequentialGroup()
                .addGroup(ajusteInvPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ajusteInvPanelLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel106, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(ajusteInvPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel28, javax.swing.GroupLayout.PREFERRED_SIZE, 352, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(506, Short.MAX_VALUE))
        );
        ajusteInvPanelLayout.setVerticalGroup(
            ajusteInvPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ajusteInvPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel106)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(234, Short.MAX_VALUE))
        );

        jLabel113.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel113.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel113.setText("PRODUCTOS BAJOS EN INVENTARIO");

        jLabel114.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel114.setText("A continuación se muestra un listado con productos con inventario debajo de su mínimo:");

        dtInvBajos.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        dtInvBajos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Código", "Descripción del Producto", "Precio Venta", "Inventario", "Inv. Mínimo", "Categoría"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        dtInvBajos.setGridColor(new java.awt.Color(255, 255, 255));
        dtInvBajos.setRowHeight(20);
        dtInvBajos.setShowHorizontalLines(false);
        dtInvBajos.setShowVerticalLines(false);
        jScrollPaneBajosInventario.setViewportView(dtInvBajos);
        if (dtInvBajos.getColumnModel().getColumnCount() > 0) {
            dtInvBajos.getColumnModel().getColumn(0).setMinWidth(200);
            dtInvBajos.getColumnModel().getColumn(0).setMaxWidth(300);
            dtInvBajos.getColumnModel().getColumn(1).setMinWidth(220);
            dtInvBajos.getColumnModel().getColumn(1).setMaxWidth(250);
            dtInvBajos.getColumnModel().getColumn(2).setMinWidth(100);
            dtInvBajos.getColumnModel().getColumn(2).setMaxWidth(150);
            dtInvBajos.getColumnModel().getColumn(3).setMinWidth(130);
            dtInvBajos.getColumnModel().getColumn(3).setMaxWidth(150);
            dtInvBajos.getColumnModel().getColumn(4).setMinWidth(50);
            dtInvBajos.getColumnModel().getColumn(4).setMaxWidth(80);
            dtInvBajos.getColumnModel().getColumn(5).setMinWidth(100);
            dtInvBajos.getColumnModel().getColumn(5).setMaxWidth(2000);
        }

        btnExportarExcelBajosInventario.setBackground(new MyColor().COLOR_BUTTON);
        btnExportarExcelBajosInventario.setForeground(new java.awt.Color(255, 255, 255));
        btnExportarExcelBajosInventario.setText("Exportar a Excel");
        btnExportarExcelBajosInventario.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnExportarExcelBajosInventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarExcelBajosInventarioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout bajoInvPanelLayout = new javax.swing.GroupLayout(bajoInvPanel);
        bajoInvPanel.setLayout(bajoInvPanelLayout);
        bajoInvPanelLayout.setHorizontalGroup(
            bajoInvPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bajoInvPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(bajoInvPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel113, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel114))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnExportarExcelBajosInventario, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jScrollPaneBajosInventario, javax.swing.GroupLayout.DEFAULT_SIZE, 868, Short.MAX_VALUE)
        );
        bajoInvPanelLayout.setVerticalGroup(
            bajoInvPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bajoInvPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel113)
                .addGap(8, 8, 8)
                .addGroup(bajoInvPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel114)
                    .addComponent(btnExportarExcelBajosInventario, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(jScrollPaneBajosInventario, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE))
        );

        reporteInvPanel.setBackground(new java.awt.Color(255, 255, 255));
        reporteInvPanel.setLayout(new javax.swing.BoxLayout(reporteInvPanel, javax.swing.BoxLayout.Y_AXIS));

        jLabel115.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel115.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel115.setText("REPORTE DE INVENTARIO");

        jLabel116.setForeground(new MyColor().COLOR_JLABEL_BLACK);
        jLabel116.setText("Costo de inventario");

        jLabel117.setForeground(new MyColor().COLOR_JLABEL_BLACK);
        jLabel117.setText("Cantidad de productos en inventario");

        jLabel118.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel118.setForeground(new MyColor().COLOR_JLABEL_BLACK);
        jLabel118.setText("$0.00");

        jLabel119.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel119.setForeground(new MyColor().COLOR_JLABEL_BLACK);
        jLabel119.setText("0");

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel115, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel118, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel116))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel117)
                            .addComponent(jLabel119, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(426, Short.MAX_VALUE))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel115)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel116)
                    .addComponent(jLabel117))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel118)
                    .addComponent(jLabel119))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        reporteInvPanel.add(jPanel21);

        jLabel120.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel120.setText("Categoría:");

        cboCategoriaIn.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Todos" }));
        cboCategoriaIn.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboCategoriaInItemStateChanged(evt);
            }
        });

        btnImprimir.setBackground(new MyColor().COLOR_BUTTON);
        btnImprimir.setForeground(new java.awt.Color(255, 255, 255));
        btnImprimir.setText("imprimir");
        btnImprimir.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirActionPerformed(evt);
            }
        });

        btnExportar.setBackground(new MyColor().COLOR_BUTTON);
        btnExportar.setForeground(new java.awt.Color(255, 255, 255));
        btnExportar.setText("exportar");
        btnExportar.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnExportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarActionPerformed(evt);
            }
        });

        btnModificarProducto.setBackground(new MyColor().COLOR_BUTTON);
        btnModificarProducto.setForeground(new java.awt.Color(255, 255, 255));
        btnModificarProducto.setText("Modificar Producto");
        btnModificarProducto.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnModificarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarProductoActionPerformed(evt);
            }
        });

        btnAgregarInventario.setBackground(new MyColor().COLOR_BUTTON);
        btnAgregarInventario.setForeground(new java.awt.Color(255, 255, 255));
        btnAgregarInventario.setText("Agregar Inventario");
        btnAgregarInventario.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnAgregarInventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarInventarioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel120)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboCategoriaIn, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(btnAgregarInventario, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnModificarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnExportar, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanel22Layout.createSequentialGroup()
                            .addComponent(btnAgregarInventario, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                            .addGap(2, 2, 2))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel22Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel120)
                                .addComponent(cboCategoriaIn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel22Layout.createSequentialGroup()
                            .addComponent(btnModificarProducto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(1, 1, 1)))
                    .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExportar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        reporteInvPanel.add(jPanel22);

        dtInvReporte.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        dtInvReporte.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Código", "Descripción del Producto", "Costo", "Precio Venta", "Existencia", "Inv. Mínimo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        dtInvReporte.setGridColor(new java.awt.Color(255, 255, 255));
        dtInvReporte.setRowHeight(20);
        dtInvReporte.setShowHorizontalLines(false);
        dtInvReporte.setShowVerticalLines(false);
        dtInvReporte.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dtInvReporteMouseClicked(evt);
            }
        });
        jScrollPaneReporteInventario.setViewportView(dtInvReporte);
        if (dtInvReporte.getColumnModel().getColumnCount() > 0) {
            dtInvReporte.getColumnModel().getColumn(0).setMinWidth(200);
            dtInvReporte.getColumnModel().getColumn(0).setMaxWidth(300);
            dtInvReporte.getColumnModel().getColumn(1).setMinWidth(220);
            dtInvReporte.getColumnModel().getColumn(1).setMaxWidth(250);
            dtInvReporte.getColumnModel().getColumn(2).setMinWidth(100);
            dtInvReporte.getColumnModel().getColumn(2).setMaxWidth(150);
            dtInvReporte.getColumnModel().getColumn(3).setMinWidth(130);
            dtInvReporte.getColumnModel().getColumn(3).setMaxWidth(150);
            dtInvReporte.getColumnModel().getColumn(4).setMinWidth(50);
            dtInvReporte.getColumnModel().getColumn(4).setMaxWidth(80);
            dtInvReporte.getColumnModel().getColumn(5).setMinWidth(100);
            dtInvReporte.getColumnModel().getColumn(5).setMaxWidth(2000);
        }

        reporteInvPanel.add(jScrollPaneReporteInventario);

        reporteMovimientosPanel.setBackground(new java.awt.Color(255, 255, 255));
        reporteMovimientosPanel.setLayout(new javax.swing.BoxLayout(reporteMovimientosPanel, javax.swing.BoxLayout.Y_AXIS));

        jLabel121.setBackground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel121.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel121.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel121.setText("HISTORIAL DE MOVIMIENTOS DE INVENTARIO");

        jLabel122.setBackground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel122.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel122.setText("Del día:");

        jDateChooser4.setDate(new Date());
        jDateChooser4.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jDateChooser4PropertyChange(evt);
            }
        });

        jLabel123.setBackground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel123.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel123.setText("Buscar por:");

        jLabel124.setBackground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel124.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel124.setText("Movimientos:");

        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "- Todos -", "Entradas", "Salidas", "Ajustes", "Devoluciones" }));
        jComboBox5.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jComboBox5PropertyChange(evt);
            }
        });

        jLabel125.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/buscar32G.png"))); // NOI18N

        btnBuscar2.setPlaceholder("Cajero, Producto o Categoría");
        btnBuscar2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                btnBuscar2KeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel122)
                            .addComponent(jDateChooser4, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel23Layout.createSequentialGroup()
                                .addGap(46, 46, 46)
                                .addComponent(jLabel123))
                            .addGroup(jPanel23Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnBuscar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel125)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel124)))
                    .addComponent(jLabel121, javax.swing.GroupLayout.PREFERRED_SIZE, 493, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(144, Short.MAX_VALUE))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel121)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel122)
                            .addComponent(jLabel123))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jDateChooser4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnBuscar2, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel125))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel124)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        reporteMovimientosPanel.add(jPanel23);

        dtInvHistorialMov.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        dtInvHistorialMov.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Código", "Descripción del Producto", "Costo", "Precio Venta", "Existencia", "Inv. Mínimo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        dtInvHistorialMov.setGridColor(new java.awt.Color(255, 255, 255));
        dtInvHistorialMov.setRowHeight(20);
        dtInvHistorialMov.setShowHorizontalLines(false);
        dtInvHistorialMov.setShowVerticalLines(false);
        jScrollPaneHistorialMov.setViewportView(dtInvHistorialMov);
        if (dtInvHistorialMov.getColumnModel().getColumnCount() > 0) {
            dtInvHistorialMov.getColumnModel().getColumn(0).setMinWidth(200);
            dtInvHistorialMov.getColumnModel().getColumn(0).setMaxWidth(300);
            dtInvHistorialMov.getColumnModel().getColumn(1).setMinWidth(220);
            dtInvHistorialMov.getColumnModel().getColumn(1).setMaxWidth(250);
            dtInvHistorialMov.getColumnModel().getColumn(2).setMinWidth(100);
            dtInvHistorialMov.getColumnModel().getColumn(2).setMaxWidth(150);
            dtInvHistorialMov.getColumnModel().getColumn(3).setMinWidth(130);
            dtInvHistorialMov.getColumnModel().getColumn(3).setMaxWidth(150);
            dtInvHistorialMov.getColumnModel().getColumn(4).setMinWidth(50);
            dtInvHistorialMov.getColumnModel().getColumn(4).setMaxWidth(80);
            dtInvHistorialMov.getColumnModel().getColumn(5).setMinWidth(100);
            dtInvHistorialMov.getColumnModel().getColumn(5).setMaxWidth(2000);
        }

        reporteMovimientosPanel.add(jScrollPaneHistorialMov);

        btnImprimirMovimiento.setBackground(new MyColor().COLOR_BUTTON);
        btnImprimirMovimiento.setForeground(new java.awt.Color(255, 255, 255));
        btnImprimirMovimiento.setText("Imprimir Movimiento");
        btnImprimirMovimiento.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnImprimirMovimiento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirMovimientoActionPerformed(evt);
            }
        });

        btnImprimirMovimiento1.setBackground(new MyColor().COLOR_BUTTON);
        btnImprimirMovimiento1.setForeground(new java.awt.Color(255, 255, 255));
        btnImprimirMovimiento1.setText("Imprimir todos los movimientos en pantalla");
        btnImprimirMovimiento1.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnImprimirMovimiento1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirMovimiento1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnImprimirMovimiento, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 365, Short.MAX_VALUE)
                .addComponent(btnImprimirMovimiento1, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel24Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnImprimirMovimiento, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnImprimirMovimiento1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        reporteMovimientosPanel.add(jPanel24);

        javax.swing.GroupLayout vistasInventarioLayout = new javax.swing.GroupLayout(vistasInventario);
        vistasInventario.setLayout(vistasInventarioLayout);
        vistasInventarioLayout.setHorizontalGroup(
            vistasInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(agregarInvPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(vistasInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(ajusteInvPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(vistasInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(bajoInvPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(vistasInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(reporteInvPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 868, Short.MAX_VALUE))
            .addGroup(vistasInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(reporteMovimientosPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 868, Short.MAX_VALUE))
        );
        vistasInventarioLayout.setVerticalGroup(
            vistasInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(agregarInvPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(vistasInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(ajusteInvPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(vistasInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(bajoInvPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(vistasInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(reporteInvPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE))
            .addGroup(vistasInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(reporteMovimientosPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE))
        );
        vistasInventario.setLayer(agregarInvPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        vistasInventario.setLayer(ajusteInvPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        vistasInventario.setLayer(bajoInvPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        vistasInventario.setLayer(reporteInvPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        vistasInventario.setLayer(reporteMovimientosPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        InventarioPanel.add(vistasInventario);

        ConfiguracionPanel.setBackground(new java.awt.Color(255, 255, 255));
        ConfiguracionPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        ConfiguracionPanel.setPreferredSize(new java.awt.Dimension(812, 582));
        ConfiguracionPanel.setLayout(new javax.swing.BoxLayout(ConfiguracionPanel, javax.swing.BoxLayout.Y_AXIS));

        jPanel19.setBackground(new java.awt.Color(255, 255, 255));

        jLabel126.setBackground(new java.awt.Color(182, 210, 248));
        jLabel126.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        jLabel126.setForeground(new java.awt.Color(135, 65, 155));
        jLabel126.setText("   CONFIGURACIÓN");

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addComponent(jLabel126, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel126)
        );

        ConfiguracionPanel.add(jPanel19);

        buttonsPanel10.setBackground(new java.awt.Color(51, 51, 51));
        buttonsPanel10.setForeground(new java.awt.Color(255, 255, 255));
        buttonsPanel10.setPreferredSize(new java.awt.Dimension(1008, 42));

        buttonsPanel11.setBackground(new java.awt.Color(51, 51, 51));
        buttonsPanel11.setPreferredSize(new java.awt.Dimension(1008, 42));
        buttonsPanel11.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnMostrarOpciones.setBackground(new MyColor().COLOR_BUTTON);
        btnMostrarOpciones.setForeground(new java.awt.Color(255, 255, 255));
        btnMostrarOpciones.setText("Mostrar todas las opciones");
        btnMostrarOpciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMostrarOpcionesActionPerformed(evt);
            }
        });
        buttonsPanel11.add(btnMostrarOpciones, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 250, 40));

        javax.swing.GroupLayout buttonsPanel10Layout = new javax.swing.GroupLayout(buttonsPanel10);
        buttonsPanel10.setLayout(buttonsPanel10Layout);
        buttonsPanel10Layout.setHorizontalGroup(
            buttonsPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonsPanel10Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(buttonsPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 888, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        buttonsPanel10Layout.setVerticalGroup(
            buttonsPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(buttonsPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        ConfiguracionPanel.add(buttonsPanel10);

        inicioConfiguracionesPanel.setBackground(new java.awt.Color(255, 255, 255));

        personalizaciónFranjaPanel.setBackground(new java.awt.Color(255, 255, 255));
        personalizaciónFranjaPanel.setPreferredSize(new java.awt.Dimension(1541, 106));

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setText("Personalización");

        btnTicket.setForeground(new java.awt.Color(135, 65, 155));
        btnTicket.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/ticketB40.png"))); // NOI18N
        btnTicket.setText("Ticket");
        btnTicket.setBorderPainted(false);
        btnTicket.setContentAreaFilled(false);
        btnTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTicketActionPerformed(evt);
            }
        });

        btnLogotipoPrograma.setForeground(new java.awt.Color(135, 65, 155));
        btnLogotipoPrograma.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/iconLogoB30.png"))); // NOI18N
        btnLogotipoPrograma.setText("Logotivo del programa");
        btnLogotipoPrograma.setBorderPainted(false);
        btnLogotipoPrograma.setContentAreaFilled(false);
        btnLogotipoPrograma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogotipoProgramaActionPerformed(evt);
            }
        });

        btnImpuestos.setForeground(new java.awt.Color(135, 65, 155));
        btnImpuestos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/impuestosB30.png"))); // NOI18N
        btnImpuestos.setText("Impuestos");
        btnImpuestos.setBorderPainted(false);
        btnImpuestos.setContentAreaFilled(false);
        btnImpuestos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImpuestosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout personalizaciónFranjaPanelLayout = new javax.swing.GroupLayout(personalizaciónFranjaPanel);
        personalizaciónFranjaPanel.setLayout(personalizaciónFranjaPanelLayout);
        personalizaciónFranjaPanelLayout.setHorizontalGroup(
            personalizaciónFranjaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(personalizaciónFranjaPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(personalizaciónFranjaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addGroup(personalizaciónFranjaPanelLayout.createSequentialGroup()
                        .addComponent(btnLogotipoPrograma)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnTicket)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnImpuestos)))
                .addContainerGap(1120, Short.MAX_VALUE))
        );
        personalizaciónFranjaPanelLayout.setVerticalGroup(
            personalizaciónFranjaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(personalizaciónFranjaPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(personalizaciónFranjaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnImpuestos, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLogotipoPrograma, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(13, 13, 13))
        );

        dispositivosFranjaPanel.setBackground(new MyColor().COLOR_BACKGROUND_BAR_CONFIG);
        dispositivosFranjaPanel.setPreferredSize(new java.awt.Dimension(1541, 106));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setText("Dispositivos");

        btnImpresoraTicket.setForeground(new java.awt.Color(135, 65, 155));
        btnImpresoraTicket.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/impresoraB40.png"))); // NOI18N
        btnImpresoraTicket.setText("Impresora de Tickets");
        btnImpresoraTicket.setBorderPainted(false);
        btnImpresoraTicket.setContentAreaFilled(false);
        btnImpresoraTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImpresoraTicketActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout dispositivosFranjaPanelLayout = new javax.swing.GroupLayout(dispositivosFranjaPanel);
        dispositivosFranjaPanel.setLayout(dispositivosFranjaPanelLayout);
        dispositivosFranjaPanelLayout.setHorizontalGroup(
            dispositivosFranjaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dispositivosFranjaPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dispositivosFranjaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(btnImpresoraTicket))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        dispositivosFranjaPanelLayout.setVerticalGroup(
            dispositivosFranjaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dispositivosFranjaPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnImpresoraTicket, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(33, 33, 33))
        );

        generalFranjaPanel.setBackground(new MyColor().COLOR_BACKGROUND_BAR_CONFIG);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setText("General");

        btnCajeros.setForeground(new java.awt.Color(135, 65, 155));
        btnCajeros.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/cajerosB24.png"))); // NOI18N
        btnCajeros.setText("Cajeros");
        btnCajeros.setBorderPainted(false);
        btnCajeros.setContentAreaFilled(false);
        btnCajeros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCajerosActionPerformed(evt);
            }
        });

        btnModificarFolio.setForeground(new java.awt.Color(135, 65, 155));
        btnModificarFolio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/folioB24.png"))); // NOI18N
        btnModificarFolio.setText("Modifcar Folios");
        btnModificarFolio.setBorderPainted(false);
        btnModificarFolio.setContentAreaFilled(false);
        btnModificarFolio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarFolioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout generalFranjaPanelLayout = new javax.swing.GroupLayout(generalFranjaPanel);
        generalFranjaPanel.setLayout(generalFranjaPanelLayout);
        generalFranjaPanelLayout.setHorizontalGroup(
            generalFranjaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalFranjaPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(generalFranjaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addGroup(generalFranjaPanelLayout.createSequentialGroup()
                        .addComponent(btnCajeros)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnModificarFolio)))
                .addContainerGap(1293, Short.MAX_VALUE))
        );
        generalFranjaPanelLayout.setVerticalGroup(
            generalFranjaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalFranjaPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(generalFranjaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCajeros, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnModificarFolio, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );

        javax.swing.GroupLayout inicioConfiguracionesPanelLayout = new javax.swing.GroupLayout(inicioConfiguracionesPanel);
        inicioConfiguracionesPanel.setLayout(inicioConfiguracionesPanelLayout);
        inicioConfiguracionesPanelLayout.setHorizontalGroup(
            inicioConfiguracionesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inicioConfiguracionesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(inicioConfiguracionesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dispositivosFranjaPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1344, Short.MAX_VALUE)
                    .addComponent(personalizaciónFranjaPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1344, Short.MAX_VALUE)
                    .addComponent(generalFranjaPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        inicioConfiguracionesPanelLayout.setVerticalGroup(
            inicioConfiguracionesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inicioConfiguracionesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(generalFranjaPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(personalizaciónFranjaPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dispositivosFranjaPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(505, Short.MAX_VALUE))
        );

        opcionesHabilitadasPanel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel50.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel50.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel50.setText("OPCIONES HABILITADAS");

        jCheckBox1.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jCheckBox1.setText("Deseo ofrecer crédito a mis clientes");

        jLabel12.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel12.setText("Activa esta opción para dar de alta clientes y poder ofrecer ventas a crédito, recibir abonos y liquidar adeudos.");

        javax.swing.GroupLayout opcionesHabilitadasPanelLayout = new javax.swing.GroupLayout(opcionesHabilitadasPanel);
        opcionesHabilitadasPanel.setLayout(opcionesHabilitadasPanelLayout);
        opcionesHabilitadasPanelLayout.setHorizontalGroup(
            opcionesHabilitadasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(opcionesHabilitadasPanelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(opcionesHabilitadasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jCheckBox1)
                    .addComponent(jLabel50, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(1006, Short.MAX_VALUE))
        );
        opcionesHabilitadasPanelLayout.setVerticalGroup(
            opcionesHabilitadasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(opcionesHabilitadasPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel50)
                .addGap(29, 29, 29)
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addContainerGap(745, Short.MAX_VALUE))
        );

        cajerosPanel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel55.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel55.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel55.setText("CAJEROS");

        txtbuscarem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtbuscaremKeyReleased(evt);
            }
        });

        jPanel20.setBackground(new java.awt.Color(247, 246, 246));

        txtnombreem.setForeground(new MyColor().COLOR_FOCUS_TEXTFIELD);
        txtnombreem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtnombreemKeyTyped(evt);
            }
        });

        jLabel127.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel127.setText("Privilegio:");

        jLabel136.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel136.setText("Nombre Completo:");

        txtusuarioem.setForeground(new MyColor().COLOR_FOCUS_TEXTFIELD);

        txtpasswordem.setForeground(new MyColor().COLOR_FOCUS_TEXTFIELD);

        jLabel140.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel140.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel140.setText("Password:");

        cboprivilegioem.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cajero", "Administrador" }));

        jLabel141.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel141.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel141.setText("Usuario:");

        btnnuevoem.setBackground(new MyColor().COLOR_BUTTON);
        btnnuevoem.setForeground(new java.awt.Color(255, 255, 255));
        btnnuevoem.setText("nuevo");
        btnnuevoem.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnnuevoem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnnuevoemActionPerformed(evt);
            }
        });

        btnguardarem.setBackground(new MyColor().COLOR_BUTTON);
        btnguardarem.setForeground(new java.awt.Color(255, 255, 255));
        btnguardarem.setText("guardar");
        btnguardarem.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnguardarem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnguardaremActionPerformed(evt);
            }
        });

        btncancelarem.setBackground(new MyColor().COLOR_BUTTON);
        btncancelarem.setForeground(new java.awt.Color(255, 255, 255));
        btncancelarem.setText("cancelar");
        btncancelarem.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btncancelarem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncancelaremActionPerformed(evt);
            }
        });

        jLabel142.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel142.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel142.setText("Email:");

        txtEmail.setForeground(new MyColor().COLOR_FOCUS_TEXTFIELD);

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel136)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtnombreem, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(btnnuevoem, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnguardarem, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btncancelarem, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel20Layout.createSequentialGroup()
                                .addGap(49, 49, 49)
                                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel140)
                                    .addComponent(jLabel142, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel127, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel141, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtpasswordem, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
                            .addComponent(txtusuarioem)
                            .addComponent(txtEmail)
                            .addComponent(cboprivilegioem, 0, 311, Short.MAX_VALUE))))
                .addContainerGap(66, Short.MAX_VALUE))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtnombreem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel136))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtusuarioem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel141))
                .addGap(9, 9, 9)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel140)
                    .addComponent(txtpasswordem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel142)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboprivilegioem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel127))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnnuevoem, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnguardarem, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btncancelarem, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        dtEmListado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Nombre del Cajero"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        dtEmListado.setGridColor(new java.awt.Color(255, 255, 255));
        dtEmListado.setShowHorizontalLines(false);
        dtEmListado.setShowVerticalLines(false);
        dtEmListado.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dtEmListadoMouseClicked(evt);
            }
        });
        jScrollPaneCajero.setViewportView(dtEmListado);
        if (dtEmListado.getColumnModel().getColumnCount() > 0) {
            dtEmListado.getColumnModel().getColumn(0).setMinWidth(200);
            dtEmListado.getColumnModel().getColumn(0).setMaxWidth(300);
        }

        jLabel134.setText("Nombre:");

        btneliminarem.setBackground(new MyColor().COLOR_BUTTON);
        btneliminarem.setForeground(new java.awt.Color(255, 255, 255));
        btneliminarem.setText("eliminar");
        btneliminarem.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btneliminarem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btneliminaremActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout cajerosPanelLayout = new javax.swing.GroupLayout(cajerosPanel);
        cajerosPanel.setLayout(cajerosPanelLayout);
        cajerosPanelLayout.setHorizontalGroup(
            cajerosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cajerosPanelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 534, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(cajerosPanelLayout.createSequentialGroup()
                .addGroup(cajerosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPaneCajero, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(cajerosPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel134)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(cajerosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtidempleado, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtbuscarem, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cajerosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btneliminarem, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 816, Short.MAX_VALUE))
        );
        cajerosPanelLayout.setVerticalGroup(
            cajerosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cajerosPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel55)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtidempleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(cajerosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtbuscarem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel134)
                    .addComponent(btneliminarem, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(cajerosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPaneCajero, javax.swing.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE))
                .addGap(81, 81, 81))
        );

        folioTicketPanel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel128.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel128.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel128.setText("FOLIO DE TICKETS");

        jLabel56.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel56.setText("Puedes configurar aquí el manejo de los fondos de las ventas del programa.");

        jLabel57.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel57.setText("Nombre:");

        txtFolioConfiguracion.setForeground(new MyColor().COLOR_FOCUS_TEXTFIELD);
        txtFolioConfiguracion.setText("Folio:");

        javax.swing.GroupLayout folioTicketPanelLayout = new javax.swing.GroupLayout(folioTicketPanel);
        folioTicketPanel.setLayout(folioTicketPanelLayout);
        folioTicketPanelLayout.setHorizontalGroup(
            folioTicketPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(folioTicketPanelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(folioTicketPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel57)
                    .addComponent(jLabel128, javax.swing.GroupLayout.PREFERRED_SIZE, 534, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 407, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFolioConfiguracion, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(986, Short.MAX_VALUE))
        );
        folioTicketPanelLayout.setVerticalGroup(
            folioTicketPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(folioTicketPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel128)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel56)
                .addGap(18, 18, 18)
                .addComponent(jLabel57)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFolioConfiguracion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(708, Short.MAX_VALUE))
        );

        logotipoPanel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel129.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel129.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel129.setText("LOGOTIPO DEL PROGRAMA");

        jLabel137.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel137.setText("Puedes cambiar el logotipo del programa (que aparece en la barra superior) eligiendo la imagen con tu logotipo: ");

        jLabel138.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel138.setText("Imagen de Logotipo ");

        jLabel139.setForeground(new java.awt.Color(102, 102, 102));
        jLabel139.setText("(de 200 pixels de ancho por 72 pixels de alto) ");

        btnExaminarLogotipo.setBackground(new MyColor().COLOR_BUTTON);
        btnExaminarLogotipo.setForeground(new java.awt.Color(255, 255, 255));
        btnExaminarLogotipo.setText("examinar");
        btnExaminarLogotipo.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnExaminarLogotipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExaminarLogotipoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout logotipoPanelLayout = new javax.swing.GroupLayout(logotipoPanel);
        logotipoPanel.setLayout(logotipoPanelLayout);
        logotipoPanelLayout.setHorizontalGroup(
            logotipoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(logotipoPanelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(logotipoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel138)
                    .addComponent(jLabel129, javax.swing.GroupLayout.PREFERRED_SIZE, 534, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel137, javax.swing.GroupLayout.PREFERRED_SIZE, 624, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(logotipoPanelLayout.createSequentialGroup()
                        .addComponent(txtRutaLogotipo, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExaminarLogotipo, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel139, javax.swing.GroupLayout.PREFERRED_SIZE, 366, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(896, Short.MAX_VALUE))
        );
        logotipoPanelLayout.setVerticalGroup(
            logotipoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(logotipoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel129)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel137)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel138)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(logotipoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRutaLogotipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExaminarLogotipo, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel139)
                .addContainerGap(687, Short.MAX_VALUE))
        );

        ticketPanel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel130.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel130.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel130.setText("PERSONALIZACIÓN DEL TICKET DE VENTA");

        jPanel30.setBackground(new java.awt.Color(247, 246, 246));
        jPanel30.setBorder(new org.jdesktop.swingx.border.DropShadowBorder());

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/logo/logo_muebleria_ticket.png"))); // NOI18N

        jPanel31.setLayout(new javax.swing.BoxLayout(jPanel31, javax.swing.BoxLayout.PAGE_AXIS));

        txtEmpresaNombre.setFont(new java.awt.Font("Consolas", 0, 10)); // NOI18N
        txtEmpresaNombre.setForeground(new java.awt.Color(135, 65, 155));
        txtEmpresaNombre.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtEmpresaNombre.setMinimumSize(new java.awt.Dimension(6, 20));
        txtEmpresaNombre.setPreferredSize(new java.awt.Dimension(6, 20));
        jPanel31.add(txtEmpresaNombre);

        txtEmpresaCalle.setFont(new java.awt.Font("Consolas", 0, 10)); // NOI18N
        txtEmpresaCalle.setForeground(new java.awt.Color(135, 65, 155));
        txtEmpresaCalle.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtEmpresaCalle.setMinimumSize(new java.awt.Dimension(6, 20));
        txtEmpresaCalle.setPreferredSize(new java.awt.Dimension(6, 20));
        jPanel31.add(txtEmpresaCalle);

        txtEmpresaLocal.setFont(new java.awt.Font("Consolas", 0, 10)); // NOI18N
        txtEmpresaLocal.setForeground(new java.awt.Color(135, 65, 155));
        txtEmpresaLocal.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtEmpresaLocal.setMinimumSize(new java.awt.Dimension(6, 20));
        txtEmpresaLocal.setPreferredSize(new java.awt.Dimension(6, 20));
        jPanel31.add(txtEmpresaLocal);

        txtEmpresaSucursal.setFont(new java.awt.Font("Consolas", 0, 10)); // NOI18N
        txtEmpresaSucursal.setForeground(new java.awt.Color(135, 65, 155));
        txtEmpresaSucursal.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtEmpresaSucursal.setMinimumSize(new java.awt.Dimension(6, 20));
        txtEmpresaSucursal.setPreferredSize(new java.awt.Dimension(6, 20));
        jPanel31.add(txtEmpresaSucursal);

        txtEmpresaTelefono.setFont(new java.awt.Font("Consolas", 0, 10)); // NOI18N
        txtEmpresaTelefono.setForeground(new java.awt.Color(135, 65, 155));
        txtEmpresaTelefono.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtEmpresaTelefono.setMinimumSize(new java.awt.Dimension(6, 20));
        txtEmpresaTelefono.setPreferredSize(new java.awt.Dimension(6, 20));
        jPanel31.add(txtEmpresaTelefono);

        jLabel4.setText("06 de Enero de 2019   8:33 am");

        jLabel33.setText("CAJERO:");

        jLabel34.setText("FOLIO:");

        jLabel49.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel49.setText("ADMINISTRADOR");

        jLabel51.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel51.setText("94");

        jLabel53.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel53.setText("1                               Estufa                          $230.00");

        jLabel54.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel54.setText("---------------------------------------------------------------------");

        jLabel59.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel59.setText("Cant.                     Descripción                       Importe");

        jLabel60.setText("No. Artículos:  1");

        jLabel62.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel62.setText("IMPORTE: $230.00");

        jLabel64.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel64.setText("IVA: $230.00");

        jLabel66.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel66.setText("TOTA: $230.00");

        jLabel68.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel68.setText("PAGO CON: $230.00");

        jLabel76.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel76.setText("SU CAMBIO: $230.00");

        txtEmpresaAgradecimiento.setFont(new java.awt.Font("Calibri", 0, 10)); // NOI18N
        txtEmpresaAgradecimiento.setForeground(new java.awt.Color(135, 65, 155));
        txtEmpresaAgradecimiento.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtEmpresaAgradecimiento.setText("GRACIAS POR SU COMPRA");

        javax.swing.GroupLayout jPanel30Layout = new javax.swing.GroupLayout(jPanel30);
        jPanel30.setLayout(jPanel30Layout);
        jPanel30Layout.setHorizontalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtEmpresaAgradecimiento)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel30Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel34, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel33, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel51, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel49, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(9, 9, 9))
                    .addComponent(jLabel54, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
                    .addComponent(jLabel59, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel53, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel30Layout.createSequentialGroup()
                        .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel68, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel76, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel30Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel60, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel62, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel64, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel66, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
            .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel30Layout.createSequentialGroup()
                    .addGap(117, 117, 117)
                    .addComponent(jPanel31, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel30Layout.setVerticalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(jLabel49))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(jLabel51))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel59)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel54)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel53)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel60)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel62)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel64)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel66)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel68)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel76)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEmpresaAgradecimiento, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel30Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel31, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(290, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout ticketPanelLayout = new javax.swing.GroupLayout(ticketPanel);
        ticketPanel.setLayout(ticketPanelLayout);
        ticketPanelLayout.setHorizontalGroup(
            ticketPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ticketPanelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(ticketPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel130, javax.swing.GroupLayout.PREFERRED_SIZE, 534, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(986, Short.MAX_VALUE))
        );
        ticketPanelLayout.setVerticalGroup(
            ticketPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ticketPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel130)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(376, Short.MAX_VALUE))
        );

        impuestosPanel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel131.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel131.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel131.setText("IMPUESTOS");

        rbImpuesto.setText("Mis artículos manejan impuestos (IVA, etc).");
        rbImpuesto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbImpuestoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout impuestosPanelLayout = new javax.swing.GroupLayout(impuestosPanel);
        impuestosPanel.setLayout(impuestosPanelLayout);
        impuestosPanelLayout.setHorizontalGroup(
            impuestosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(impuestosPanelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(impuestosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel131, javax.swing.GroupLayout.PREFERRED_SIZE, 534, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rbImpuesto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtImpuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(986, Short.MAX_VALUE))
        );
        impuestosPanelLayout.setVerticalGroup(
            impuestosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(impuestosPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel131)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbImpuesto)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtImpuesto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(744, Short.MAX_VALUE))
        );

        baseDatosPanel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel132.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel132.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel132.setText("BASE DE DATOS");

        javax.swing.GroupLayout baseDatosPanelLayout = new javax.swing.GroupLayout(baseDatosPanel);
        baseDatosPanel.setLayout(baseDatosPanelLayout);
        baseDatosPanelLayout.setHorizontalGroup(
            baseDatosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(baseDatosPanelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel132, javax.swing.GroupLayout.PREFERRED_SIZE, 534, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(986, Short.MAX_VALUE))
        );
        baseDatosPanelLayout.setVerticalGroup(
            baseDatosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(baseDatosPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel132)
                .addContainerGap(791, Short.MAX_VALUE))
        );

        impresoraTicketPanel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel133.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel133.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel133.setText("IMPRESORA DE TICKETS");

        jLabel77.setText("Por favor elige la impresora de tickets entre las impresoras instaladas en tu sistema:");

        jLabel78.setText("Impresora de Tickets:");

        javax.swing.GroupLayout impresoraTicketPanelLayout = new javax.swing.GroupLayout(impresoraTicketPanel);
        impresoraTicketPanel.setLayout(impresoraTicketPanelLayout);
        impresoraTicketPanelLayout.setHorizontalGroup(
            impresoraTicketPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(impresoraTicketPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(impresoraTicketPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel133, javax.swing.GroupLayout.PREFERRED_SIZE, 534, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(impresoraTicketPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel78, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel77, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE))
                    .addComponent(comboImpresora, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(997, Short.MAX_VALUE))
        );
        impresoraTicketPanelLayout.setVerticalGroup(
            impresoraTicketPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(impresoraTicketPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel133)
                .addGap(4, 4, 4)
                .addComponent(jLabel77)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel78)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboImpresora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(733, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout vistasConfiguracionesLayout = new javax.swing.GroupLayout(vistasConfiguraciones);
        vistasConfiguraciones.setLayout(vistasConfiguracionesLayout);
        vistasConfiguracionesLayout.setHorizontalGroup(
            vistasConfiguracionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(inicioConfiguracionesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(vistasConfiguracionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(opcionesHabilitadasPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(vistasConfiguracionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(cajerosPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(vistasConfiguracionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(vistasConfiguracionesLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(folioTicketPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
            .addGroup(vistasConfiguracionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(vistasConfiguracionesLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(logotipoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
            .addGroup(vistasConfiguracionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(vistasConfiguracionesLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(ticketPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
            .addGroup(vistasConfiguracionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(vistasConfiguracionesLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(impuestosPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
            .addGroup(vistasConfiguracionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(vistasConfiguracionesLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(baseDatosPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
            .addGroup(vistasConfiguracionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(vistasConfiguracionesLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(impresoraTicketPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        vistasConfiguracionesLayout.setVerticalGroup(
            vistasConfiguracionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(inicioConfiguracionesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(vistasConfiguracionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(opcionesHabilitadasPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(vistasConfiguracionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(cajerosPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(vistasConfiguracionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(vistasConfiguracionesLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(folioTicketPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
            .addGroup(vistasConfiguracionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(vistasConfiguracionesLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(logotipoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
            .addGroup(vistasConfiguracionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(vistasConfiguracionesLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(ticketPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
            .addGroup(vistasConfiguracionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(vistasConfiguracionesLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(impuestosPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
            .addGroup(vistasConfiguracionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(vistasConfiguracionesLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(baseDatosPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
            .addGroup(vistasConfiguracionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(vistasConfiguracionesLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(impresoraTicketPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        vistasConfiguraciones.setLayer(inicioConfiguracionesPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        vistasConfiguraciones.setLayer(opcionesHabilitadasPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        vistasConfiguraciones.setLayer(cajerosPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        vistasConfiguraciones.setLayer(folioTicketPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        vistasConfiguraciones.setLayer(logotipoPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        vistasConfiguraciones.setLayer(ticketPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        vistasConfiguraciones.setLayer(impuestosPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        vistasConfiguraciones.setLayer(baseDatosPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        vistasConfiguraciones.setLayer(impresoraTicketPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        ConfiguracionPanel.add(vistasConfiguraciones);

        CortePanel.setBackground(new java.awt.Color(255, 255, 255));
        CortePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        CortePanel.setPreferredSize(new java.awt.Dimension(812, 582));

        jLabel58.setBackground(new java.awt.Color(182, 210, 248));
        jLabel58.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        jLabel58.setForeground(new java.awt.Color(135, 65, 155));
        jLabel58.setText("   CORTE");

        buttonsPanel12.setBackground(new java.awt.Color(51, 51, 51));
        buttonsPanel12.setForeground(new java.awt.Color(255, 255, 255));
        buttonsPanel12.setPreferredSize(new java.awt.Dimension(1008, 42));

        buttonsPanel13.setBackground(new java.awt.Color(51, 51, 51));
        buttonsPanel13.setPreferredSize(new java.awt.Dimension(1008, 42));
        buttonsPanel13.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnImprimirCorte.setBackground(new MyColor().COLOR_BUTTON);
        btnImprimirCorte.setForeground(new java.awt.Color(255, 255, 255));
        btnImprimirCorte.setText("IMPRIMIR CORTE");
        btnImprimirCorte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirCorteActionPerformed(evt);
            }
        });
        buttonsPanel13.add(btnImprimirCorte, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 0, 170, 40));

        btnHacerCorteHoy.setBackground(new MyColor().COLOR_BUTTON);
        btnHacerCorteHoy.setForeground(new java.awt.Color(255, 255, 255));
        btnHacerCorteHoy.setText("Hacer corte hoy");
        btnHacerCorteHoy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHacerCorteHoyActionPerformed(evt);
            }
        });
        buttonsPanel13.add(btnHacerCorteHoy, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 150, 40));

        javax.swing.GroupLayout buttonsPanel12Layout = new javax.swing.GroupLayout(buttonsPanel12);
        buttonsPanel12.setLayout(buttonsPanel12Layout);
        buttonsPanel12Layout.setHorizontalGroup(
            buttonsPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonsPanel12Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(buttonsPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, 888, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        buttonsPanel12Layout.setVerticalGroup(
            buttonsPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(buttonsPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        MainCortetPanel.setBackground(new java.awt.Color(248, 248, 253));
        MainCortetPanel.setLayout(new javax.swing.BoxLayout(MainCortetPanel, javax.swing.BoxLayout.Y_AXIS));

        periodoPanel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel8.setForeground(new java.awt.Color(135, 65, 155));
        jLabel8.setText("Día");

        jLabel10.setForeground(new java.awt.Color(135, 65, 155));
        jLabel10.setText("Del cajero:");

        dateCorte.setDate(new Date());

        cbxCajero.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "- Todos -" }));

        btnHacerCorte.setBackground(new MyColor().COLOR_BUTTON);
        btnHacerCorte.setForeground(new java.awt.Color(255, 255, 255));
        btnHacerCorte.setText("Hacer corte");
        btnHacerCorte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHacerCorteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout periodoPanelLayout = new javax.swing.GroupLayout(periodoPanel);
        periodoPanel.setLayout(periodoPanelLayout);
        periodoPanelLayout.setHorizontalGroup(
            periodoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(periodoPanelLayout.createSequentialGroup()
                .addGroup(periodoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(periodoPanelLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(periodoPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(dateCorte, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(33, 33, 33)
                .addGroup(periodoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(cbxCajero, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnHacerCorte, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(925, Short.MAX_VALUE))
        );
        periodoPanelLayout.setVerticalGroup(
            periodoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, periodoPanelLayout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(periodoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnHacerCorte, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(periodoPanelLayout.createSequentialGroup()
                        .addGroup(periodoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(periodoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dateCorte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbxCajero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(26, 26, 26))
        );

        MainCortetPanel.add(periodoPanel);

        ContenidoCortePanel.setBackground(new java.awt.Color(255, 255, 255));
        ContenidoCortePanel.setLayout(new javax.swing.BoxLayout(ContenidoCortePanel, javax.swing.BoxLayout.Y_AXIS));

        jPanel25.setBackground(new java.awt.Color(255, 255, 255));
        jPanel25.setPreferredSize(new java.awt.Dimension(606, 140));

        EntradaEfectivoPanel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel81.setBackground(new java.awt.Color(204, 204, 255));
        jLabel81.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel81.setForeground(new java.awt.Color(31, 25, 26));
        jLabel81.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel81.setText("Entradas en Efectivo");
        jLabel81.setToolTipText("Mostrar opciones para hacer el corte");
        jLabel81.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jLabel82.setBackground(new java.awt.Color(102, 153, 0));
        jLabel82.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel82.setForeground(new java.awt.Color(51, 51, 51));
        jLabel82.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel82.setText("Entrada de dinero (Cambio):");
        jLabel82.setToolTipText("Mostrar opciones para hacer el corte");
        jLabel82.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        lblEntradas.setBackground(new java.awt.Color(21, 239, 6));
        lblEntradas.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblEntradas.setForeground(new java.awt.Color(135, 65, 155));
        lblEntradas.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEntradas.setText("$0.00");
        lblEntradas.setToolTipText("Mostrar opciones para hacer el corte");
        lblEntradas.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        javax.swing.GroupLayout EntradaEfectivoPanelLayout = new javax.swing.GroupLayout(EntradaEfectivoPanel);
        EntradaEfectivoPanel.setLayout(EntradaEfectivoPanelLayout);
        EntradaEfectivoPanelLayout.setHorizontalGroup(
            EntradaEfectivoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EntradaEfectivoPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(EntradaEfectivoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel81, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(EntradaEfectivoPanelLayout.createSequentialGroup()
                        .addComponent(jLabel82, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblEntradas, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        EntradaEfectivoPanelLayout.setVerticalGroup(
            EntradaEfectivoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EntradaEfectivoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel81, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(EntradaEfectivoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel82, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEntradas, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        DineroCajaPanel.setBackground(new java.awt.Color(255, 255, 255));
        DineroCajaPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel72.setBackground(new java.awt.Color(204, 204, 255));
        jLabel72.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel72.setForeground(new java.awt.Color(31, 25, 26));
        jLabel72.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel72.setText("Dinero en caja");
        jLabel72.setToolTipText("Mostrar opciones para hacer el corte");
        jLabel72.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        DineroCajaPanel.add(jLabel72, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, 280, 20));

        jLabel73.setBackground(new java.awt.Color(102, 153, 0));
        jLabel73.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel73.setForeground(new java.awt.Color(51, 51, 51));
        jLabel73.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel73.setText("Entradas: +");
        jLabel73.setToolTipText("Mostrar opciones para hacer el corte");
        jLabel73.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        DineroCajaPanel.add(jLabel73, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 64, 160, 20));
        DineroCajaPanel.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 104, 270, 10));

        jLabel74.setBackground(new java.awt.Color(102, 153, 0));
        jLabel74.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel74.setForeground(new java.awt.Color(51, 51, 51));
        jLabel74.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel74.setText("Pago a Proveedores: -");
        jLabel74.setToolTipText("Mostrar opciones para hacer el corte");
        jLabel74.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        DineroCajaPanel.add(jLabel74, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 84, 130, 20));
        DineroCajaPanel.add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 270, 40));

        jLabel75.setBackground(new java.awt.Color(21, 239, 6));
        jLabel75.setForeground(new java.awt.Color(51, 51, 51));
        jLabel75.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel75.setText("Total:");
        jLabel75.setToolTipText("Mostrar opciones para hacer el corte");
        jLabel75.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        DineroCajaPanel.add(jLabel75, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 104, 60, 20));

        lblTotalDinero.setBackground(new java.awt.Color(21, 239, 6));
        lblTotalDinero.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblTotalDinero.setForeground(new java.awt.Color(135, 65, 155));
        lblTotalDinero.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalDinero.setText("$0.00");
        lblTotalDinero.setToolTipText("Mostrar opciones para hacer el corte");
        lblTotalDinero.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        DineroCajaPanel.add(lblTotalDinero, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 104, 60, 20));

        lblEntradas2.setBackground(new java.awt.Color(21, 239, 6));
        lblEntradas2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblEntradas2.setForeground(new java.awt.Color(135, 65, 155));
        lblEntradas2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEntradas2.setText("$0.00");
        lblEntradas2.setToolTipText("Mostrar opciones para hacer el corte");
        lblEntradas2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        DineroCajaPanel.add(lblEntradas2, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 64, 60, 20));

        lblSalidas.setBackground(new java.awt.Color(21, 239, 6));
        lblSalidas.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblSalidas.setForeground(new java.awt.Color(135, 65, 155));
        lblSalidas.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSalidas.setText("$0.00");
        lblSalidas.setToolTipText("Mostrar opciones para hacer el corte");
        lblSalidas.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        DineroCajaPanel.add(lblSalidas, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 84, 60, 20));
        DineroCajaPanel.add(jSeparator5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 85, 269, 10));

        jLabel79.setBackground(new java.awt.Color(102, 153, 0));
        jLabel79.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel79.setForeground(new java.awt.Color(51, 51, 51));
        jLabel79.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel79.setText("Ventas en efectivo: +");
        jLabel79.setToolTipText("Mostrar opciones para hacer el corte");
        jLabel79.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        DineroCajaPanel.add(jLabel79, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 37, 130, 20));

        lblVentasEfectivo.setBackground(new java.awt.Color(21, 239, 6));
        lblVentasEfectivo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblVentasEfectivo.setForeground(new java.awt.Color(135, 65, 155));
        lblVentasEfectivo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVentasEfectivo.setText("$0.00");
        lblVentasEfectivo.setToolTipText("Mostrar opciones para hacer el corte");
        lblVentasEfectivo.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        DineroCajaPanel.add(lblVentasEfectivo, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 38, 60, 20));

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(EntradaEfectivoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(DineroCajaPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(965, Short.MAX_VALUE))
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(DineroCajaPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(EntradaEfectivoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ContenidoCortePanel.add(jPanel25);

        jPanel26.setBackground(new java.awt.Color(255, 255, 255));

        PagosContadoPanel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel88.setBackground(new java.awt.Color(204, 204, 255));
        jLabel88.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel88.setForeground(new java.awt.Color(31, 25, 26));
        jLabel88.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel88.setText("Pagos de contado:");
        jLabel88.setToolTipText("Mostrar opciones para hacer el corte");
        jLabel88.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jLabel93.setBackground(new java.awt.Color(102, 153, 0));
        jLabel93.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel93.setForeground(new java.awt.Color(51, 51, 51));
        jLabel93.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel93.setText("Efectivo:");
        jLabel93.setToolTipText("Mostrar opciones para hacer el corte");
        jLabel93.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        lblVentasEfectivo2.setBackground(new java.awt.Color(21, 239, 6));
        lblVentasEfectivo2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblVentasEfectivo2.setForeground(new java.awt.Color(135, 65, 155));
        lblVentasEfectivo2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVentasEfectivo2.setText("$0.00");
        lblVentasEfectivo2.setToolTipText("Mostrar opciones para hacer el corte");
        lblVentasEfectivo2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        lblPagoClientes.setBackground(new java.awt.Color(102, 153, 0));
        lblPagoClientes.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblPagoClientes.setForeground(new java.awt.Color(51, 51, 51));
        lblPagoClientes.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblPagoClientes.setText("Pagos de Clientes:");
        lblPagoClientes.setToolTipText("Mostrar opciones para hacer el corte");
        lblPagoClientes.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblPagoClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblPagoClientesMouseClicked(evt);
            }
        });

        lblPagosClientes.setBackground(new java.awt.Color(21, 239, 6));
        lblPagosClientes.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblPagosClientes.setForeground(new java.awt.Color(135, 65, 155));
        lblPagosClientes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPagosClientes.setText("$0.00");
        lblPagosClientes.setToolTipText("Mostrar opciones para hacer el corte");
        lblPagosClientes.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        lblSalidas2.setBackground(new java.awt.Color(21, 239, 6));
        lblSalidas2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblSalidas2.setForeground(new java.awt.Color(135, 65, 155));
        lblSalidas2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSalidas2.setText("$0.00");
        lblSalidas2.setToolTipText("Mostrar opciones para hacer el corte");
        lblSalidas2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        lblSalidaEfectivo.setBackground(new java.awt.Color(102, 153, 0));
        lblSalidaEfectivo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblSalidaEfectivo.setForeground(new java.awt.Color(51, 51, 51));
        lblSalidaEfectivo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblSalidaEfectivo.setText("Pagos a proveedores:");
        lblSalidaEfectivo.setToolTipText("Mostrar opciones para hacer el corte");
        lblSalidaEfectivo.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblSalidaEfectivo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblSalidaEfectivoMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout PagosContadoPanelLayout = new javax.swing.GroupLayout(PagosContadoPanel);
        PagosContadoPanel.setLayout(PagosContadoPanelLayout);
        PagosContadoPanelLayout.setHorizontalGroup(
            PagosContadoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PagosContadoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PagosContadoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel88, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(PagosContadoPanelLayout.createSequentialGroup()
                        .addComponent(jLabel93, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addComponent(lblVentasEfectivo2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator10, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(PagosContadoPanelLayout.createSequentialGroup()
                        .addComponent(lblPagoClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addComponent(lblPagosClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator11, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(PagosContadoPanelLayout.createSequentialGroup()
                        .addComponent(lblSalidaEfectivo, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addComponent(lblSalidas2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator12, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PagosContadoPanelLayout.setVerticalGroup(
            PagosContadoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PagosContadoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel88)
                .addGap(1, 1, 1)
                .addGroup(PagosContadoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel93, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblVentasEfectivo2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jSeparator10, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addGroup(PagosContadoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPagoClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPagosClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jSeparator11, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PagosContadoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSalidaEfectivo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSalidas2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jSeparator12, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        VentasDepatamentoPanel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel65.setBackground(new java.awt.Color(204, 204, 255));
        jLabel65.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel65.setForeground(new java.awt.Color(31, 25, 26));
        jLabel65.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel65.setText("Ventas por Categoria");
        jLabel65.setToolTipText("Mostrar opciones para hacer el corte");
        jLabel65.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jPanel17.setBackground(new java.awt.Color(255, 255, 255));
        jPanel17.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel17.setForeground(new java.awt.Color(204, 204, 204));

        dtVentasCategoria.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        dtVentasCategoria.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CATEGORIA", "TOTAL"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        dtVentasCategoria.setRowHeight(20);
        dtVentasCategoria.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        dtVentasCategoria.setShowHorizontalLines(false);
        dtVentasCategoria.setShowVerticalLines(false);
        jScrollPaneVentasCategoria.setViewportView(dtVentasCategoria);

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPaneVentasCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPaneVentasCategoria, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout VentasDepatamentoPanelLayout = new javax.swing.GroupLayout(VentasDepatamentoPanel);
        VentasDepatamentoPanel.setLayout(VentasDepatamentoPanelLayout);
        VentasDepatamentoPanelLayout.setHorizontalGroup(
            VentasDepatamentoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(VentasDepatamentoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(VentasDepatamentoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(VentasDepatamentoPanelLayout.createSequentialGroup()
                        .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        VentasDepatamentoPanelLayout.setVerticalGroup(
            VentasDepatamentoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(VentasDepatamentoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PagosContadoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(VentasDepatamentoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel26Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PagosContadoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(VentasDepatamentoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        ContenidoCortePanel.add(jPanel26);

        VentasTotalesPanel.setBackground(new MyColor().COLOR_BACKGROUND_BLACK);

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Ventas Totales:");

        jLabel61.setForeground(new java.awt.Color(255, 255, 255));
        jLabel61.setText("(Pagos de Contado + Pagos de Clientes)");

        lblVentasTotales.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        lblVentasTotales.setForeground(new java.awt.Color(255, 255, 255));
        lblVentasTotales.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblVentasTotales.setText("$0.00");

        jLabel63.setForeground(new java.awt.Color(255, 255, 255));
        jLabel63.setText("(Precio de Venta - Precio de Costo - Impuesto) x Cantidad Vendida");

        lblGananciasDia.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        lblGananciasDia.setForeground(new java.awt.Color(255, 255, 255));
        lblGananciasDia.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblGananciasDia.setText("$0.00");

        jLabel67.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel67.setForeground(new java.awt.Color(255, 255, 255));
        jLabel67.setText("Ganancia del día:");

        lblFechaCorte.setForeground(new java.awt.Color(255, 255, 255));
        lblFechaCorte.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFechaCorte.setText("30 de Agosto 2018 5:06 pm");

        javax.swing.GroupLayout VentasTotalesPanelLayout = new javax.swing.GroupLayout(VentasTotalesPanel);
        VentasTotalesPanel.setLayout(VentasTotalesPanelLayout);
        VentasTotalesPanelLayout.setHorizontalGroup(
            VentasTotalesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(VentasTotalesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(VentasTotalesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(VentasTotalesPanelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel61, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(VentasTotalesPanelLayout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblVentasTotales, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(VentasTotalesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(VentasTotalesPanelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(VentasTotalesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblFechaCorte, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(VentasTotalesPanelLayout.createSequentialGroup()
                        .addComponent(jLabel67, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblGananciasDia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        VentasTotalesPanelLayout.setVerticalGroup(
            VentasTotalesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(VentasTotalesPanelLayout.createSequentialGroup()
                .addGroup(VentasTotalesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(VentasTotalesPanelLayout.createSequentialGroup()
                        .addGroup(VentasTotalesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(VentasTotalesPanelLayout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(VentasTotalesPanelLayout.createSequentialGroup()
                                .addComponent(lblVentasTotales, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(6, 6, 6)))
                        .addComponent(jLabel61))
                    .addGroup(VentasTotalesPanelLayout.createSequentialGroup()
                        .addGroup(VentasTotalesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblGananciasDia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel67))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel63)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblFechaCorte)
                .addContainerGap(49, Short.MAX_VALUE))
        );

        ContenidoCortePanel.add(VentasTotalesPanel);

        MainCortetPanel.add(ContenidoCortePanel);

        javax.swing.GroupLayout CortePanelLayout = new javax.swing.GroupLayout(CortePanel);
        CortePanel.setLayout(CortePanelLayout);
        CortePanelLayout.setHorizontalGroup(
            CortePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(buttonsPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, 1364, Short.MAX_VALUE)
            .addComponent(MainCortetPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel58, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        CortePanelLayout.setVerticalGroup(
            CortePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CortePanelLayout.createSequentialGroup()
                .addComponent(jLabel58)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonsPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(MainCortetPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout escritorioLayout = new javax.swing.GroupLayout(escritorio);
        escritorio.setLayout(escritorioLayout);
        escritorioLayout.setHorizontalGroup(
            escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1563, Short.MAX_VALUE)
            .addGroup(escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(VentasPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(ClientesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1563, Short.MAX_VALUE))
            .addGroup(escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(ProductosPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1563, Short.MAX_VALUE))
            .addGroup(escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(InventarioPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1563, Short.MAX_VALUE))
            .addGroup(escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(ConfiguracionPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1563, Short.MAX_VALUE))
            .addGroup(escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(CortePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1366, Short.MAX_VALUE))
        );
        escritorioLayout.setVerticalGroup(
            escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 922, Short.MAX_VALUE)
            .addGroup(escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(VentasPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 922, Short.MAX_VALUE))
            .addGroup(escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(ClientesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 922, Short.MAX_VALUE))
            .addGroup(escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(ProductosPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 922, Short.MAX_VALUE))
            .addGroup(escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(InventarioPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 922, Short.MAX_VALUE))
            .addGroup(escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(ConfiguracionPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 922, Short.MAX_VALUE))
            .addGroup(escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(CortePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE))
        );
        escritorio.setLayer(VentasPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        escritorio.setLayer(ClientesPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        escritorio.setLayer(ProductosPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        escritorio.setLayer(InventarioPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        escritorio.setLayer(ConfiguracionPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        escritorio.setLayer(CortePanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        mainPanel.add(escritorio);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1366, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 768, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClientesHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClientesHActionPerformed
        activarV("clientes");
    }//GEN-LAST:event_btnClientesHActionPerformed

    private void btnProductosHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductosHActionPerformed
        activarV("productos");
    }//GEN-LAST:event_btnProductosHActionPerformed

    private void btnInventarioHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInventarioHActionPerformed
        activarV("inventario");
    }//GEN-LAST:event_btnInventarioHActionPerformed

    private void btnCorteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCorteActionPerformed
        activarV("corte");
        SimpleDateFormat FORMAT1 = new SimpleDateFormat("dd");
        SimpleDateFormat FORMAT2 = new SimpleDateFormat("MMMMM yyyy h:mm a");

        java.util.Date hoy = new java.util.Date();
        String p_fecha_hoy = FORMAT1.format(hoy) + " de " + FORMAT2.format(hoy);

        lblFechaCorte.setText("" + p_fecha_hoy);
    }//GEN-LAST:event_btnCorteActionPerformed

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        /*int opcion = JOptionPane.showConfirmDialog(null, "Realmente desea salir del sistema?", "Confirmar salida", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opcion == 0) {
            //Cerrar Session
            System.exit(0);
        }*/
        setExtendedState(ICONIFIED);
    }//GEN-LAST:event_btnCerrarActionPerformed

    private void btnUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUsuarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnUsuarioActionPerformed

    private void btnConfiguracionHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfiguracionHActionPerformed
        activarV("configuracion");

    }//GEN-LAST:event_btnConfiguracionHActionPerformed

    private void btnBorrarArtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarArtActionPerformed
        int opcion = JOptionPane.showConfirmDialog(null, "¿Seguro que desea borrar el artículo?", "Confirmar", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opcion == 0) {
            DefaultTableModel modelo = (DefaultTableModel) this.dtTicket.getModel();
            if (modelo.getRowCount() > 0) {
                int fila = this.dtTicket.getSelectedRow();
                if (this.dtTicket.getSelectedRowCount() < 1) {
                    JOptionPane.showMessageDialog(null, "Seleciona un registro", "Confirmar", JOptionPane.ERROR_MESSAGE);
                } else {
                    modelo.removeRow(fila);
                }
            }
        }
        totalImporte();
    }//GEN-LAST:event_btnBorrarArtActionPerformed

    private void btnINSVariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnINSVariosActionPerformed
        FrmVarios2 varios = new FrmVarios2(this, true);
        varios.setVisible(true);

        /*frmVarios varios = new frmVarios();
        ventana(varios);
         */
    }//GEN-LAST:event_btnINSVariosActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        FrmBuscarProducto2 buscarP = new FrmBuscarProducto2(this, true);
        buscarP.setVisible(true);

        /* INTERNALFRAME
        frmBuscarProducto buscarP = new frmBuscarProducto();
        ventana(buscarP);
         */
    }//GEN-LAST:event_btnBuscarActionPerformed
    boolean aplicarMay = false;
    private void btnMayoreoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMayoreoActionPerformed
        // TODO add your handling code here:
        DefaultTableModel modelo = (DefaultTableModel) this.dtTicket.getModel();
        if (modelo.getRowCount() > 0) {
            int fila = this.dtTicket.getSelectedRow();
            String codigo = dtTicket.getValueAt(fila, 1).toString();
            int cantidad = Integer.parseInt(dtTicket.getValueAt(fila, 4).toString());
            if (this.dtTicket.getSelectedRowCount() < 1) {
                JOptionPane.showMessageDialog(null, "Seleciona un registro", "Confirmar", JOptionPane.ERROR_MESSAGE);
            } else {
                if (aplicarMay == false) {
                    aplicarMayoreo(fila, codigo, cantidad, true);
                    aplicarMay = true;
                    displayMessage("Se aplicó precio de mayoreo al producto");
                } else {
                    aplicarMayoreo(fila, codigo, cantidad, false);
                    aplicarMay = false;
                    displayMessage("Se quitó el precio de mayoreos");
                }

            }
        }
        totalImporte();
    }//GEN-LAST:event_btnMayoreoActionPerformed
    public void aplicarMayoreo(int fila, String codigo, int cantidad, boolean mayoreo) {
        Double precioMayoreo = 0d, precioVenta = 0d, importe = 0d;

        try {
            String sql = "SELECT precioVenta,precioMayoreo FROM producto WHERE noserie = " + codigo;
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                precioVenta = Double.parseDouble(rs.getString("precioVenta"));
                precioMayoreo = Double.parseDouble(rs.getString("precioMayoreo"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        if (mayoreo == true) {
            importe = precioMayoreo * cantidad;
            dtTicket.setValueAt(precioMayoreo, fila, 3);
            dtTicket.setValueAt(importe, fila, 5);
        } else {
            importe = precioVenta * cantidad;
            dtTicket.setValueAt(precioVenta, fila, 3);
            dtTicket.setValueAt(importe, fila, 5);
        }
    }

    private void btnEntradasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntradasActionPerformed
        FrmRegistroEntradasDinero entradaDinero = new FrmRegistroEntradasDinero(this, true);
        entradaDinero.Idempleado = IdempleadoU;
        entradaDinero.setVisible(true);

        /*frmRegistroEntradaCaja rentradas = new frmRegistroEntradaCaja();
        rentradas.Idempleado = IdempleadoU;
        ventana(rentradas);
         */
    }//GEN-LAST:event_btnEntradasActionPerformed

    private void btnSalidasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalidasActionPerformed
        FrmRegistroSalidasDinero2 salidaDinero = new FrmRegistroSalidasDinero2(this, true);
        salidaDinero.Idempleado = IdempleadoU;
        salidaDinero.setVisible(true);

        /*frmRegistroSalidasDinero rsalidas = new frmRegistroSalidasDinero();
        rsalidas.Idempleado = IdempleadoU;
        ventana(rsalidas);*/
    }//GEN-LAST:event_btnSalidasActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        JOptionPane.showConfirmDialog(null, "Realmente desea salir del sistema?", "Confirmar salida", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    }//GEN-LAST:event_formWindowClosing


    private void btnNuevoTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoTicketActionPerformed
        String nombreTicket = JOptionPane.showInternalInputDialog(escritorio, "¿Desea cobrar este ticket mas adelante?\nNombre:", "Nombre nuevo ticket", 0);
        if (nombreTicket == null || nombreTicket.equals("")) {
            return;
        } else {
            //FUTURAS CARACTERISTICAS
            /*JPanel nuevoPanel = new JPanel();
            JTable nuevaTabla = nuevaTabla();
            jScrollPaneVentas = new javax.swing.JScrollPane();
            jScrollPaneVentas.add(nuevaTabla);
            nuevoPanel.add(jScrollPaneVentas);
            jTabbedPane1.addTab("Ticket - " + numberTicket(), nuevoPanel);
            jTabbedPane1.setTitleAt(countTicket - 2, nombreTicket);
            jTabbedPane1.setSelectedIndex(countTicket - 1);*/
        }
    }//GEN-LAST:event_btnNuevoTicketActionPerformed

    private void reimprimirUltimoTicket() {
        beep = false;
        this.notificar("Ticket re-impreso");
    }

    private void abrirCobrarTicket() {
        if (modelo.getRowCount() == 0) {
            displayMessage("Ticket con CERO productos...Imposible cobrar.");
        } else {
            FrmCobrar2 fcobrar = new FrmCobrar2(this, true);
            fcobrar.IdEmpleado = IdempleadoU;
            fcobrar.recibirModelo(modelo);
            fcobrar.setVisible(true);
            /*
            frmCobrar fcobrar = new frmCobrar();
            fcobrar.IdEmpleado = IdempleadoU;
            fcobrar.recibirModelo(modelo);
            ventana(fcobrar);
             */
        }
    }

    public static void vaciarListaProductos() {
        int filas = modelo.getRowCount();
        for (int i = filas; i > 0; i--) {
            modelo.removeRow(modelo.getRowCount() - 1);
            lblTotalCantProd.setText("0");
            lblTotalImporte.setText("$0.00");
            lblSubTotalImporte.setText("$0.00");
        }
    }

    public static void vaciarListaCreditos() {
        DefaultTableModel modeloCredito = (DefaultTableModel) dtClieCredito.getModel();
        int filas = modeloCredito.getRowCount();
        for (int i = filas; i > 0; i--) {
            modeloCredito.removeRow(modeloCredito.getRowCount() - 1);
            lblImpuestos.setText("$0.00");
            lblTotalImporteCredito.setText("$0.00");
        }
    }

    private void abrirVentasDevoluciones() {
        try {
            FrmVentasDevoluciones2 fVentasDevoluciones = new FrmVentasDevoluciones2(this, true);
            fVentasDevoluciones.IdempleadoU_Ventas_devoluciones = IdempleadoU;
            fVentasDevoluciones.setVisible(true);
        } catch (Exception er) {
            JOptionPane.showMessageDialog(null, "Error abrirVentasDevoluciones2: " + er.getMessage());
            return;
        }
        /*
        frmVentasDevoluciones fVentasDevoluciones = new frmVentasDevoluciones();
        fVentasDevoluciones.IdempleadoU_Ventas_devoluciones = IdempleadoU;
        ventana(fVentasDevoluciones);
         */
    }

    private void btnAgregarInvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarInvActionPerformed
        activarI("agregar");
    }//GEN-LAST:event_btnAgregarInvActionPerformed

    private void btnAjustesInvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAjustesInvActionPerformed
        activarI("ajuste");
    }//GEN-LAST:event_btnAjustesInvActionPerformed

    private void btnProdInventarioBajosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProdInventarioBajosActionPerformed
        activarI("bajo");
        mostrarBajosInventario();
    }//GEN-LAST:event_btnProdInventarioBajosActionPerformed

    private void btnReporteInvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReporteInvActionPerformed
        activarI("rInventario");
        generarReporteMov();
    }//GEN-LAST:event_btnReporteInvActionPerformed

    private void btnReporteMovInvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReporteMovInvActionPerformed
        activarI("rMovimientos");
    }//GEN-LAST:event_btnReporteMovInvActionPerformed

    private void btnCajerosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCajerosActionPerformed
        activarC("cajeros");
    }//GEN-LAST:event_btnCajerosActionPerformed

    private void btnModificarFolioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarFolioActionPerformed
        activarC("folio");
    }//GEN-LAST:event_btnModificarFolioActionPerformed

    private void btnLogotipoProgramaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogotipoProgramaActionPerformed
        activarC("logotipo");
    }//GEN-LAST:event_btnLogotipoProgramaActionPerformed

    private void btnTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTicketActionPerformed
        activarC("ticket");
    }//GEN-LAST:event_btnTicketActionPerformed

    private void btnImpuestosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImpuestosActionPerformed
        activarC("impuestos");
    }//GEN-LAST:event_btnImpuestosActionPerformed

    private void btnImpresoraTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImpresoraTicketActionPerformed
        activarC("impresoraTicket");

    }//GEN-LAST:event_btnImpresoraTicketActionPerformed

    JFileChooser selecArchivo = new JFileChooser();
    int contAccion = 0;

    public void AgregarFiltro() {
        selecArchivo.setFileFilter(new FileNameExtensionFilter("JPG (*.jpg)", "jpg"));
        selecArchivo.setFileFilter(new FileNameExtensionFilter("PNG (*.png)", "png"));
    }

    private void examinarLogotipo() {
        contAccion++;
        if (contAccion == 1) {
            AgregarFiltro();
        }

        if (selecArchivo.showDialog(null, "Seleccionar imagen") == JFileChooser.APPROVE_OPTION) {
            archivo = selecArchivo.getSelectedFile();
            //String ruta = selecArchivo.getSelectedFile().getPath();
            ControllerConfiguration fc = new ControllerConfiguration();
            fc.setNombre(archivo.getName());

            if (fc.isUpdate(fc)) {
                fc.copyImagen(archivo.getPath(), fc.ruta_guardar + archivo.getName());
                cambiarLogo();
            }


            /*if (archivo.getName().endsWith("jpg") || archivo.getName().endsWith(".png") || archivo.getName().endsWith(".JPG") || archivo.getName().endsWith(".PNG")) {
                JOptionPane.showMessageDialog(null, cambiarLogo(),//ruta
                        "CAMBIAR LOGITIPO", JOptionPane.INFORMATION_MESSAGE);

            } else {
                JOptionPane.showMessageDialog(null, "Elija un formato valido.");
            }*/
        }
    }

    private void nuevoCliente() {
        habilitar();
        btnguardarCliente.setText("Guardar");
        accion = "guardar";
    }

    private void guardarCliente() {
        if (txtnombreCliente.getText().length() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Debes ingresar un nombre al cliente");
            txtnombreCliente.requestFocus();
            return;
        }

        if (txtdireccionCliente.getText().length() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Debes ingresar una descripcion al cliente");
            txtdireccionCliente.requestFocus();
            return;
        }
        if (txttelefonoCliente.getText().length() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Debes ingresar un telefono al cliente");
            txttelefonoCliente.requestFocus();
            return;
        }
        if (txtcreditoCliente.getText().length() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Debes ingresar un credito al cliente");
            txtcreditoCliente.requestFocus();
            return;
        }

        ModelClient dts = new ModelClient();
        ModelPaymet dtspa = new ModelPaymet();
        ControllerClient func = new ControllerClient();

        dts.setNombre(txtnombreCliente.getText());
        dts.setDireccion(txtdireccionCliente.getText());
        dts.setTelefono(txttelefonoCliente.getText());

        dtspa.setIdempleado(IdempleadoU);
        dtspa.setMonto(0);

        if (accion.equals("guardar")) {
            dts.setLimiteCredito(Double.parseDouble(txtcreditoCliente.getText()));
            if (func.insertar(dts, dtspa)) {
                JOptionPane.showMessageDialog(rootPane, "El cliente fue registrado satisfactoriamente");
                mostrar("");
                inhabilitar();
            }

        } else if (accion.equals("editar")) {
            String creditoCliente = txtcreditoCliente.getText();
            creditoCliente = creditoCliente.replace("$", "");
            creditoCliente = creditoCliente.replace(",", "");

            dts.setIdcliente(Integer.parseInt(txtidcliente.getText()));
            dts.setLimiteCredito(Double.parseDouble(creditoCliente)); //txtcreditoCliente.getText()

            if (func.editar(dts)) {
                JOptionPane.showMessageDialog(rootPane, "El cliente fue editado satisfactoriamente");
                mostrar("");
                inhabilitar();
            }

        }

    }

    public static double saldoDisponible(String idcliente) {
        //String sSQLID = "SELECT idcliente FROM cliente ORDER BY idcliente DESC LIMIT 1";

        String sSQL = "SELECT c.limiteCredito, p.monto "
                + "FROM cliente c "
                + "INNER JOIN pago p "
                + "ON p.idcliente = c.idcliente "
                + "WHERE c.idcliente = '" + idcliente + "'";

        String idc = "", limiC = "", mont = "";

        Double limiteCredito = 0d, monto = 0d, saldoA = 0d;

        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            // while (true) {
            while (rs.next()) {
                limiC = rs.getString("limiteCredito");
                mont = rs.getString("monto");
                //JOptionPane.showMessageDialog(null, "LIMITE: " + limiC + "  MONTO: " + mont);
            }

            if (!limiC.equals("") && !mont.equals("")) {
                limiteCredito = Double.parseDouble(limiC);
                monto = Double.parseDouble(mont);
                saldoA = limiteCredito - monto;
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error descubierto aqui: " + e);
            //return null;
        }
        return saldoA;
    }

    public static String ultimoPago(String idcliente) {
        //String sSQLID = "SELECT idcliente FROM cliente ORDER BY idcliente DESC LIMIT 1";

        String sSQL = "select idcliente, monto , fecha from pago \n"
                + "WHERE idcliente = " + idcliente + "\n"
                + "ORDER BY idpago DESC LIMIT 1";

        String monto = "";
        String fecha = "";
        //Double ultimoPago = 0d;

        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            // while (true) {
            while (rs.next()) {
                monto = rs.getString("monto");
                fecha = rs.getString("fecha");
            }

            if (monto.equals("0")) {
                fecha = "-";
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error método ultimo pago: " + e);
        }
        //return ultimoPago;
        return fecha;
    }

    static int count = 0;

    private void generarSaldosDisponibles() {
        String sSQL = "SELECT idcliente FROM cliente";
        Statement st;
        String id = "";
        try {
            st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            while (rs.next()) {
                id = rs.getString("idcliente");
                if (dtClieListado.getValueAt(count, 0).equals(id)) {
                    dtClieListado.setValueAt("" + saldoDisponible(id), count, 5);
                }
                count++;
            }
            count = 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public static void generarUltimosPagos() {
        String sSQL = "SELECT idcliente FROM cliente";
        Statement st;
        String id = "";
        try {
            st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            while (rs.next()) {
                id = rs.getString("idcliente");
                if (dtClieListado.getValueAt(count, 0).equals(id)) {
                    dtClieListado.setValueAt("" + ultimoPago(id), count, 6);
                }
                count++;
            }
            count = 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    private void eliminarCliente() {
        if (!txtidcliente.getText().equals("")) {
            int confirmacion = JOptionPane.showConfirmDialog(rootPane, "Estas seguro de eliminar el cliente", "Confirmación", 2);

            if (confirmacion == 0) {
                ControllerClient func = new ControllerClient();
                ModelClient dts = new ModelClient();
                dts.setIdcliente(Integer.parseInt(txtidcliente.getText()));
                if (func.eliminar(dts)) {
                    JOptionPane.showMessageDialog(null, "El cliente se ha dado de baja correctamente", "Eliminar cliente", 2);
                }
                mostrar("");
                inhabilitar();
            }
        }
    }
    private boolean dobleClicked = false;
    private void dtClieListadoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dtClieListadoMouseClicked
        // TODO add your handling code here:
        btnguardarCliente.setText("Guardar cambios");
        habilitar();
        btneliminarCliente.setEnabled(true);
        accion = "editar";
        lblSeleccionaUnCliente.setVisible(false);

        int fila = dtClieListado.getSelectedRow();
        txtidcliente.setText(dtClieListado.getValueAt(fila, 0).toString());
        txtnombreCliente.setText(dtClieListado.getValueAt(fila, 1).toString());
        txtdireccionCliente.setText(dtClieListado.getValueAt(fila, 2).toString());
        txttelefonoCliente.setText(dtClieListado.getValueAt(fila, 3).toString());
        txtcreditoCliente.setText(dtClieListado.getValueAt(fila, 4).toString());

        //displayMessage("");
        dtClieListado.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 1) {
                    dobleClicked = false;
                    lblSeleccionaUnCliente.setVisible(true);
                    Idcliente = 0;
                }
                if (e.getClickCount() == 2) {
                    dobleClicked = true;
                    jTabbedPane4.setSelectedIndex(1);
                    int fila = dtClieListado.getSelectedRow();
                    Idcliente = Integer.parseInt(dtClieListado.getValueAt(fila, 0).toString());
                    lblNombreCliente.setText(dtClieListado.getValueAt(fila, 1).toString());
                    lblLimiteCreditoCliente.setText(dtClieListado.getValueAt(fila, 4).toString());
                    lblSaldoActualCliente.setText(dtClieListado.getValueAt(fila, 5).toString());
                    //lblSaldoActualCliente.setText(MyFormater.formato(new ControllerClient().saldoActualCliente(String.valueOf(Idcliente))));
                    boolean LiquidadoTodo = new ControllerTicket().ticketLiquidadoTodoCliente(Idcliente);
                    if (LiquidadoTodo == true) {
                        habilitarBotonesAbonarLiquidar(false);
                    } else {
                        habilitarBotonesAbonarLiquidar(true);
                    }

                    cargarMeses("" + Idcliente);
                    vaciarListaCreditos();
                }
            }

        }
        );


    }//GEN-LAST:event_dtClieListadoMouseClicked

    private void habilitarBotonesAbonarLiquidar(boolean valor) {
        btnAbonarCliente.setEnabled(valor);
        btnLiquidarAdeudo.setEnabled(valor);
    }

    private void nuevaCategoria() {
        habilitarCat();
        btnguardarCategoria.setText("Guardar");
        accion = "guardar";
    }

    private void guardarCategoria() {
        if (txtnombreCategoria.getText().length() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Debes ingresar un nombre de la categoria");
            txtnombreCategoria.requestFocus();
            return;
        }
        ModelCategory dts = new ModelCategory();
        ControllerCategory func = new ControllerCategory();

        dts.setNombre(txtnombreCategoria.getText());

        if (accion.equals("guardar")) {
            if (func.insertar(dts)) {
                JOptionPane.showMessageDialog(rootPane, "La categoria fue registrada satisfactoriamente");
                mostrarCat("");
                inhabilitarCat();
            }

        } else if (accion.equals("editar")) {

            dts.setIdcategoria(Integer.parseInt(txtidcategoria.getText()));

            if (func.editar(dts)) {
                JOptionPane.showMessageDialog(rootPane, "La categoria fue editada satisfactoriamente");
                mostrarCat("");
                inhabilitarCat();
            }

        }
    }

    public static Integer registrarCategoria(String nombreCategoria) {
        ModelCategory dts = new ModelCategory();
        ControllerCategory func = new ControllerCategory();
        int idcategoria = 0;
        dts.setNombre(nombreCategoria);
        if (func.insertar(dts)) {
            mostrarCat("");
            idcategoria = getIdCategoria(nombreCategoria);
        }
        return idcategoria;
    }

    private void dtProCategoriaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dtProCategoriaMouseClicked
        btnguardarCategoria.setText("Guardar Cambios");
        habilitarCat();
        btneliminarCategoria.setEnabled(true);
        accion = "editar";
        int fila = dtProCategoria.getSelectedRow();
        txtidcategoria.setText(dtProCategoria.getValueAt(fila, 0).toString());
        txtnombreCategoria.setText(dtProCategoria.getValueAt(fila, 1).toString());

    }//GEN-LAST:event_dtProCategoriaMouseClicked

    private void nuevoProducto() {
        habilitarPro();
        btnguardarProducto.setText("Guardar");
        accion = "guardar";
    }

    int idproducto = 0;

    public static void guardarProducto() {
        if (txtNoSeriePro.getText().length() == 0) {
            JOptionPane.showMessageDialog(null, "Debes ingresar el número de serie del producto");
            txtNoSeriePro.requestFocus();
            return;
        }

        if (txtdescripcionPro.getText().length() == 0) {
            JOptionPane.showMessageDialog(null, "Debes ingresar una descripcion al producto");
            txtdescripcionPro.requestFocus();
            return;
        }
        if (txtprecioCostoPro.getText().length() == 0) {
            JOptionPane.showMessageDialog(null, "Debes ingresar un precio costo al producto");
            txtprecioCostoPro.requestFocus();
            return;
        }
        if (txtprecioVentaPro.getText().length() == 0) {
            JOptionPane.showMessageDialog(null, "Debes ingresar un precio venta al producto");
            txtprecioVentaPro.requestFocus();
            return;
        }

        if (txtprecioMayoreoPro.getText().length() == 0) {
            JOptionPane.showMessageDialog(null, "Debes ingresar un precio mayoreo al producto");
            txtprecioMayoreoPro.requestFocus();
            return;
        }
        if (txtcantidadActualPro.getText().length() == 0) {
            JOptionPane.showMessageDialog(null, "Debes ingresar una cantidad actual al producto");
            txtcantidadActualPro.requestFocus();
            return;
        }
        if (txtcantidadMinimoPro.getText().length() == 0) {
            JOptionPane.showMessageDialog(null, "Debes ingresar un catidad minimo al producto");
            txtcantidadMinimoPro.requestFocus();
            return;
        }

        if (accion.equals("guardar")) {
            ControllerProduct funcPro = new ControllerProduct();
            String SERIEPRODUCTO = txtNoSeriePro.getText();
            boolean yaExisteSerieProduccto = funcPro.verificarSerieProducto(SERIEPRODUCTO);
            if (yaExisteSerieProduccto) {
                JOptionPane.showMessageDialog(null, "Este producto con codigo " + SERIEPRODUCTO + ".\nYa está registrado.");
            } else {
                int seleccionado = cbocategoriaPro.getSelectedIndex();
                String nombreCategoria = cbocategoriaPro.getItemAt(seleccionado).toString();
                guardarProducto2(
                        txtNoSeriePro.getText(),
                        txtdescripcionPro.getText(),
                        txtprecioCostoPro.getText(),
                        txtprecioVentaPro.getText(),
                        txtprecioMayoreoPro.getText(),
                        txtcantidadActualPro.getText(),
                        txtcantidadMinimoPro.getText(),
                        nombreCategoria
                );
            }

        } else if (accion.equals("editar")) {
            editarProducto(
                    Integer.parseInt(txtidproductos.getText()),
                    txtNoSeriePro.getText(),
                    txtdescripcionPro.getText(),
                    txtprecioCostoPro.getText(),
                    txtprecioVentaPro.getText(),
                    txtprecioMayoreoPro.getText(),
                    cbocategoriaPro.getSelectedItem().toString(),
                    txtcantidadActualPro.getText(),
                    txtcantidadMinimoPro.getText()
            );
        }
    }

    public static void editarProducto(
            int idProducto,
            String noserie,
            String descripcion,
            String precioCosto,
            String precioVenta,
            String precioMayoreo,
            String nombreCategoria,
            String existencia,
            String cantidadMinima
    ) {

        ModelProduct dts = new ModelProduct();
        ControllerProduct func = new ControllerProduct();

        dts.setIdproducto(idProducto);
        dts.setNoserie(noserie);
        dts.setDescripcion(descripcion);
        dts.setPrecioCosto(Double.parseDouble(precioCosto));
        dts.setPrecioVenta(Double.parseDouble(precioVenta));
        dts.setPrecioMayoreo(Double.parseDouble(precioMayoreo));
        dts.setIdcategoria(getIdCategoria(nombreCategoria));
        dts.setExistencia(Integer.parseInt(existencia));
        dts.setCantidadMinima(Integer.parseInt(cantidadMinima));

        if (func.editar(dts)) {
            //JOptionPane.showMessageDialog(null, "El producto fue editado satisfactoriamente");
            mostrarPro("");
            inhabilitarPro();
        }

    }

    public static Integer getIdProducto(String noserie) {
        int idproducto = 0;
        try {
            String sql1 = "SELECT idproducto FROM producto WHERE noserie= '" + noserie + "'";

            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql1);
            while (rs.next()) {
                idproducto = rs.getInt("idproducto");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return idproducto;
    }

    public static Integer getIdUltimoProReg() {
        String sSQL = "SELECT idproducto FROM producto ORDER BY idproducto DESC LIMIT 1";
        int idc = 0;
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            while (rs.next()) {
                idc = rs.getInt("idproducto");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en getIdUltimoProReg: " + e.getMessage());
        }
        return idc;
    }

    public static int getIdCategoria(String nombreCategoria) {
        Statement st = null;
        ResultSet rs = null;
        int idcategoria = 0;
        String sql1 = "select idcategoria from categoria where nombre ='" + nombreCategoria + "'";

        try {
            st = cn.createStatement();
            rs = st.executeQuery(sql1);

            while (rs.next()) {
                idcategoria = rs.getInt("idcategoria");
            }
            if (idcategoria == 0) {
                idcategoria = registrarCategoria(nombreCategoria);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage() + "\nCausa: " + e.getCause() + "\nClase: " + e.getClass());
        }
        return idcategoria;

    }

    public static String getNombreEmpleado(int idempleado) {
        Statement st = null;
        ResultSet rs = null;
        String nombre = "";
        String sql1 = "select nombre from empleado where idempleado = " + idempleado;

        try {
            st = cn.createStatement();
            rs = st.executeQuery(sql1);

            while (rs.next()) {
                nombre = rs.getString("nombre");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage() + "\nCausa: " + e.getCause() + "\nClase: " + e.getClass());
        }
        return nombre;
    }

    public static String cliente(int idcliente) {
        Statement st = null;
        ResultSet rs = null;
        String nombre = "";
        String sql1 = "select nombre from cliente where idcliente = " + idcliente;

        try {
            st = cn.createStatement();
            rs = st.executeQuery(sql1);

            while (rs.next()) {
                nombre = rs.getString("nombre");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage() + "\nCausa: " + e.getCause() + "\nClase: " + e.getClass());
        }
        return nombre;
    }

    public static void guardar2domMetodo() {
        //=======================2DO MÉTODO========================
        String codigoBarras, descripcion, precioCosto, precioVenta, precioMayoreo, cantidadActual, cantidadMinimo;
        String sql3 = " ";
        codigoBarras = txtNoSeriePro.getText();
        descripcion = txtdescripcionPro.getText();
        precioCosto = txtprecioCostoPro.getText();
        precioVenta = txtprecioVentaPro.getText();
        precioMayoreo = txtprecioMayoreoPro.getText();
        cantidadActual = txtcantidadActualPro.getText();
        cantidadMinimo = txtcantidadMinimoPro.getText();
        sql3 = "INSERT INTO  producto (noserie,descripcion,precioCosto,precioVenta,precioMayoreo,existencia,cantidadMinima,idcategoria)"
                + "values(?,?,?,?,?,?,?,?)";

        try {
            PreparedStatement pst = cn.prepareStatement(sql3);
            pst.setString(1, codigoBarras);
            pst.setString(2, descripcion);
            pst.setString(3, precioCosto);
            pst.setString(4, precioVenta);
            pst.setString(5, precioMayoreo);
            pst.setString(6, cantidadActual);
            pst.setString(7, cantidadMinimo);

            int seleccionado = cbocategoriaPro.getSelectedIndex();
            String texto = (String) cbocategoriaPro.getItemAt(seleccionado);

            String sql1 = "select idcategoria from categoria where nombre ='" + texto + "'";
            int idc = 0, idp = 0, ex = 0;
            String sql2 = "SELECT idproducto, existencia FROM producto ORDER BY idproducto DESC LIMIT 1";

            try {
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql1);

                Statement st2 = cn.createStatement();
                ResultSet rs2 = st2.executeQuery(sql2);

                while (rs.next()) {
                    idc = rs.getInt("idcategoria");
                }

                while (rs2.next()) {
                    idp = rs2.getInt("idproducto");
                    ex = rs2.getInt("existencia");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);

            }

            pst.setInt(8, idc);

            int n = pst.executeUpdate();
            //REGISTRAR MOVIMIENTO DE INVENTARIO
            //1 ENTRADAS
            //2 SALIDAS
            //3 AJUSTES
            //4 DEVOLUCIONES
            registrarMovimiento(idp, IdempleadoU, ex, 1, 1);
            if (n > 0) {
                JOptionPane.showMessageDialog(null, "El producto fue registrado satisfactoriamente");
                inhabilitarPro();
                btnguardarProducto.setText("Guardar");
            }
            mostrarPro("");
        } catch (Exception e) {
        }
    }

    public static boolean updatePro = false;
    public static boolean updateOption = false;
    public static int opcionU = -1;

    public static void guardarProducto2(
            String noserie,
            String descripcion,
            String precioCosto,
            String precioVenta,
            String precioMayoreo,
            String cantidadActual,
            String cantidadMinimo,
            String nombreCategoria
    ) {
        String sql = "";

        if (verificarExistencia(noserie) == 0) {
            sql = "INSERT INTO  producto (noserie,descripcion,precioCosto,precioVenta,precioMayoreo,existencia,cantidadMinima,idcategoria)"
                    + "values(?,?,?,?,?,?,?,?)";

            try {
                PreparedStatement pst = cn.prepareStatement(sql);
                pst.setString(1, noserie);
                pst.setString(2, descripcion);
                pst.setString(3, precioCosto);
                pst.setString(4, precioVenta);
                pst.setString(5, precioMayoreo);
                pst.setString(6, cantidadActual);
                pst.setString(7, cantidadMinimo);
                pst.setInt(8, getIdCategoria(nombreCategoria));
                int n = pst.executeUpdate();

                //REGISTRAR MOVIMIENTO DE INVENTARIO
                //1 ENTRADAS
                //2 SALIDAS
                //3 AJUSTES
                //4 DEVOLUCIONES
                //Habia 0, porque es una entrada
                registrarMovimiento(getIdUltimoProReg(), IdempleadoU, 0, 1, Integer.parseInt(cantidadActual));
                inhabilitarPro();
                btnguardarProducto.setText("Guardar");
                mostrarPro("");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error en el metodo guardarProducto2(): " + e.getMessage());
            }
        } else {
            editarProducto(
                    getIdProducto(noserie), //IDRODUCTO
                    noserie,//NOSERIE
                    descripcion, //DESCRICIÓN
                    precioCosto, //PRECIO COSTO
                    precioVenta, //PRECIO VENTA
                    precioMayoreo, //PRECIO MAYOREO
                    nombreCategoria, ///NOMBRE CATEGORIA
                    cantidadActual, //EXISTENCIA
                    cantidadMinimo // INV. MINIMO
            );
        }
    }


    private void btnEliminarTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarTicketActionPerformed
        int numTab = jTabbedPane1.getTabCount();
        if (numTab != 1) {
            Component ct = jTabbedPane1.getSelectedComponent();
            if (ct.isEnabled()) {
                jTabbedPane1.remove(ct);
                numTab -= 1;
                countTicket -= 1;
            } else {
                jTabbedPane1.removeTabAt(countTicket - 1);
                countTicket -= 1;
            }
            boolean ct2 = jTabbedPane1.getSelectedComponent().isEnabled();
            String name = jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex());
            if (ct2) {
                this.titleSection.setText("VENTA DE PRODUCTOS - " + name);
            }
        } else {
            displayMessage("No se puede eliminar el ticket, es el único");
        }
    }//GEN-LAST:event_btnEliminarTicketActionPerformed

    private void jTabbedPane1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane1MouseClicked
        boolean ct2 = jTabbedPane1.getSelectedComponent().isEnabled();
        String name = jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex());
        if (ct2) {
            this.titleSection.setText("VENTA DE PRODUCTOS - " + name);
        }
    }//GEN-LAST:event_jTabbedPane1MouseClicked

    private void txtBuscarCategoriaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarCategoriaKeyReleased
        mostrarCat(txtBuscarCategoria.getText());
    }//GEN-LAST:event_txtBuscarCategoriaKeyReleased

    private void txtBuscarProductosKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarProductosKeyReleased
        mostrarPro(txtBuscarProductos.getText());
    }//GEN-LAST:event_txtBuscarProductosKeyReleased
    String idProducto;

    private void eliminarProducto() {
        if (!btnEliminarProducto.getText().equals("")) {
            int confirmacion = JOptionPane.showConfirmDialog(null, "Estas seguro de eliminar el producto", "Confirmacion", 2);

            if (confirmacion == 0) {
                ModelProduct dts = new ModelProduct();
                ControllerProduct func = new ControllerProduct();
                dts.setIdproducto(Integer.parseInt(idProducto));
                if (func.eliminar(dts)) {
                    JOptionPane.showMessageDialog(null, "El producto se ha dado de baja correctamente", "Eliminar producto", 2);
                }
                mostrarPro("");
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Debe seleccionar un registro para poder eliminar", "Eliminar producto", 2);
        }
    }
    private void dtProListadoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dtProListadoMouseClicked
        this.habilitarProEditar();
        accion = "editar";
        int fila = dtProListado.getSelectedRow();
        idProducto = (dtProListado.getValueAt(fila, 0).toString());
        dtProListado.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 1) {
                    //int fila = tablalistadoProductos.getSelectedRow();
                    //idProducto = (tablalistadoProductos.getValueAt(fila, 0).toString());
                }
                if (e.getClickCount() == 2) {
                    jTabbedPane2.setSelectedIndex(1);
                    int fila = dtProListado.getSelectedRow();
                    txtidproductos.setText(dtProListado.getValueAt(fila, 0).toString());
                    txtNoSeriePro.setText(dtProListado.getValueAt(fila, 1).toString());
                    txtdescripcionPro.setText(dtProListado.getValueAt(fila, 2).toString());
                    txtprecioCostoPro.setText(dtProListado.getValueAt(fila, 3).toString());
                    txtprecioVentaPro.setText(dtProListado.getValueAt(fila, 4).toString());
                    txtprecioMayoreoPro.setText(dtProListado.getValueAt(fila, 5).toString());
                    txtcantidadActualPro.setText(dtProListado.getValueAt(fila, 6).toString());
                    txtcantidadMinimoPro.setText(dtProListado.getValueAt(fila, 7).toString());
                    cbocategoriaPro.setSelectedItem(dtProListado.getValueAt(fila, 8).toString());
                }
            }

        }
        );
    }//GEN-LAST:event_dtProListadoMouseClicked

    private void txtNoSerieProKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNoSerieProKeyPressed

        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!this.txtNoSeriePro.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Ingrese el codigo de Barras al producto.");
            }

        }

    }//GEN-LAST:event_txtNoSerieProKeyPressed

    private void txtbuscarClientesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtbuscarClientesKeyReleased
        mostrar(txtbuscarClientes.getText());
    }//GEN-LAST:event_txtbuscarClientesKeyReleased

    private void jTabbedPane2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane2StateChanged
        reporteHoy();
    }//GEN-LAST:event_jTabbedPane2StateChanged

    private void jTabbedPane2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane2MouseClicked
        boolean ct2 = jTabbedPane2.getSelectedComponent().isEnabled();
        if (ct2) {
            //this.inhabilitarPro();
            //this.btnguardarProducto.setText("Guardar");
        }

    }//GEN-LAST:event_jTabbedPane2MouseClicked

    private void nuevoEmpleado() {
        habilitarEm();
        btnguardarem.setText("Guardar");
        accion = "guardar";
    }

    private void guardarEmpleado() {
        if (txtnombreem.getText().length() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Debes ingresar un nombre al empleado");
            txtnombreem.requestFocus();
            return;
        }

        if (txtusuarioem.getText().length() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Debes ingresar un usuario al empleado");
            txtusuarioem.requestFocus();
            return;
        }
        if (txtpasswordem.getText().length() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Debes ingresar una contraseña al empleado");
            txtpasswordem.requestFocus();
            return;
        }

        if (txtEmail.getText().length() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Debes ingresar un email al empleado");
            txtEmail.requestFocus();
            return;
        }

        try {
            ModelEmploye dts = new ModelEmploye();
            ControllerEmploye func = new ControllerEmploye();

            dts.setNombre(txtnombreem.getText());
            dts.setUsuario(txtusuarioem.getText());
            dts.setPassword(AES.encrypt(txtpasswordem.getText()));
            dts.setEmail(txtEmail.getText());
            int selec = cboprivilegioem.getSelectedIndex();
            dts.setPrivilegio(selec);
            //(String) cboprivilegioem.getItemAt(selec)

            if (accion.equals("guardar")) {
                String nameUser = txtusuarioem.getText();
                if (nameUser.equals("admin")) {
                    JOptionPane.showMessageDialog(null, "admin Está reservada para el sistema.\nPor favor ingrese otro para el usuario.");
                } else {
                    boolean yaExisteUsuario = func.verificarUsuario(txtEmail.getText());
                    if (yaExisteUsuario) {
                        JOptionPane.showMessageDialog(null, "Este email ya se encuentra registrado.\nPor favor ingrese otro email para el usuario.");
                    } else {
                        if (func.insertar(dts)) {
                            JOptionPane.showMessageDialog(rootPane, "El empleado fue registrado satisfactoriamente...");
                            mostrarEm("");
                            inhabilitarEm();
                        }
                    }
                }
            } else if (accion.equals("editar")) {
                dts.setIdempleado(Integer.parseInt(txtidempleado.getText()));
                if (func.editar(dts)) {
                    JOptionPane.showMessageDialog(rootPane, "El empleado fue editado satisfactoriamente...");
                    mostrarEm("");
                    inhabilitarEm();
                }

            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al guardar o editar empleado.");
        }

    }

    private void eliminarEmpleado() {
        if (!txtidempleado.getText().equals("")) {
            int confirmacion = JOptionPane.showConfirmDialog(rootPane, "Estas seguro de eliminar el empleado", "Confirmación", 2);

            if (confirmacion == 0) {
                ControllerEmploye func = new ControllerEmploye();
                ModelEmploye dts = new ModelEmploye();
                dts.setIdempleado(Integer.parseInt(txtidempleado.getText()));
                func.eliminar(dts);
                mostrarEm("");
                inhabilitarEm();
            }
        }
    }
    private void txtCodigoProAgregarInvKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoProAgregarInvKeyReleased
        // TODO add your handling code here:
        String buscar = txtCodigoProAgregarInv.getText();
        String sql = "select * from producto where noserie= '" + buscar + "'";

        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                lbldescripinventario.setText(rs.getString("descripcion"));
                lblcantinventario.setText(rs.getString("existencia"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            //return null;
        }

    }//GEN-LAST:event_txtCodigoProAgregarInvKeyReleased

    //private void agregarInv();
    private void txtCodigoProAjusteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoProAjusteKeyReleased
        // TODO add your handling code here:
        String noserie = txtCodigoProAjuste.getText();
        String sql = "select * from producto where noserie = '" + noserie + "'";
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                lbldesinventario2.setText(rs.getString("descripcion"));
                lblExistenciaAjuste.setText(rs.getString("existencia"));
                lblIdProductoAjuste.setText(rs.getString("idproducto"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }//GEN-LAST:event_txtCodigoProAjusteKeyReleased

    private void ajustarInventario() {
        if (txtCodigoProAjuste.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Ingrese no serie del producto");
        } else {
            String input = txtinputinventario2.getText();
            registrarMovimiento(
                    Integer.parseInt(lblIdProductoAjuste.getText()), //IDPRODUCTO
                    IdempleadoU, //EMPLEADO
                    Integer.parseInt(lblExistenciaAjuste.getText()), //EXISTENCIA ANTES DE AJUSTE
                    3, //TIPO MOVIMIENTO = AJUSTE.
                    Integer.parseInt(input) //CANTIDAD AJUSTADA
            );

            String sql = "update producto set existencia = ? where noserie  = " + txtCodigoProAjuste.getText();

            try {
                PreparedStatement pst = cn.prepareStatement(sql);
                pst.setString(1, input);
                int n = pst.executeUpdate();
                if (n > 0) {
                    JOptionPane.showMessageDialog(rootPane, "Fue ajustado correctamente.");
                    //inhabilitarPro();

                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            mostrarPro("");
            //txtcodinventario2.setText("");
            lbldesinventario2.setText("-");
            lblExistenciaAjuste.setText("0");
            txtinputinventario2.setText("");
        }
    }
    private void txtEnterProductoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEnterProductoKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            enterProducto(txtEnterProducto.getText(), "1");
        }
    }//GEN-LAST:event_txtEnterProductoKeyPressed

    private void abrirAbonar() {
        //CON JDIALOG
        FrmAbono2 dialogo = new FrmAbono2(this, true);
        dialogo.IDCLIENTE = Idcliente;
        dialogo.nombreCliente = lblNombreCliente.getText();
        dialogo.IDEMPLEADO = IdempleadoU;
        dialogo.setVisible(true);

        //CON JINTERNALFRAME
        /*frmAbono frmabonos = new frmAbono();
        frmabonos.IDCLIENTE = Idcliente;
        frmabonos.nombreCliente = lblNombreCliente.getText();
        frmabonos.IDEMPLEADO = IdempleadoU;
        ventana(frmabonos);*/
    }
    private void txtnombreClienteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtnombreClienteKeyTyped
        Validacion.soloLetras(evt);
    }//GEN-LAST:event_txtnombreClienteKeyTyped

    private void txttelefonoClienteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txttelefonoClienteKeyTyped
        Validacion.soloNumerosEnteros(evt);
    }//GEN-LAST:event_txttelefonoClienteKeyTyped

    private void txtcreditoClienteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtcreditoClienteKeyTyped
        Validacion.decimales(evt, txtcreditoCliente);
    }//GEN-LAST:event_txtcreditoClienteKeyTyped

    private void txtNoSerieProKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNoSerieProKeyTyped
        Validacion.letrasNumeros(evt);
    }//GEN-LAST:event_txtNoSerieProKeyTyped

    private void txtprecioCostoProKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtprecioCostoProKeyTyped
        Validacion.decimales(evt, txtprecioCostoPro);
    }//GEN-LAST:event_txtprecioCostoProKeyTyped

    private void txtprecioVentaProKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtprecioVentaProKeyTyped
        Validacion.decimales(evt, txtprecioVentaPro);
    }//GEN-LAST:event_txtprecioVentaProKeyTyped

    private void txtprecioMayoreoProKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtprecioMayoreoProKeyTyped
        Validacion.decimales(evt, txtprecioMayoreoPro);
    }//GEN-LAST:event_txtprecioMayoreoProKeyTyped

    private void txtcantidadActualProKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtcantidadActualProKeyTyped
        Validacion.soloNumerosEnteros(evt);
    }//GEN-LAST:event_txtcantidadActualProKeyTyped

    private void txtcantidadMinimoProKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtcantidadMinimoProKeyTyped
        Validacion.soloNumerosEnteros(evt);
    }//GEN-LAST:event_txtcantidadMinimoProKeyTyped

    private void txtnombreCategoriaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtnombreCategoriaKeyTyped
        Validacion.letrasNumeros(evt);
    }//GEN-LAST:event_txtnombreCategoriaKeyTyped

    private void txtCodigoProAgregarInvKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoProAgregarInvKeyTyped
        Validacion.letrasNumeros(evt);
    }//GEN-LAST:event_txtCodigoProAgregarInvKeyTyped

    private void txtinputinventarioKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtinputinventarioKeyTyped
        Validacion.soloNumerosEnteros(evt);
    }//GEN-LAST:event_txtinputinventarioKeyTyped

    private void txtCodigoProAjusteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoProAjusteKeyTyped
        Validacion.letrasNumeros(evt);
    }//GEN-LAST:event_txtCodigoProAjusteKeyTyped

    private void txtinputinventario2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtinputinventario2KeyTyped
        Validacion.soloNumerosEnteros(evt);
    }//GEN-LAST:event_txtinputinventario2KeyTyped

    private void abrirDetalleAbono() {
        //JDIALOG
        FrmDetalleAbono2 detAbono = new FrmDetalleAbono2(this, true);
        detAbono.mostrarAbonos(String.valueOf(Idcliente));
        detAbono.Idcliente = Idcliente;
        detAbono.setVisible(true);

        /*
        INTERNAL FRAME
        frmDetalleAbono detAbono = new frmDetalleAbono();
        detAbono.mostrarAbonos(String.valueOf(Idcliente));
        ventana(detAbono);
         */
    }

    private void reporteHoy() {
        SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        Calendar hoy = Calendar.getInstance();
        String DESDE = FORMAT.format(hoy.getTime());
        String HASTA = FORMAT.format(hoy.getTime());
        mostrarTicket(DESDE, HASTA);
        cmbox.setSelectedItem("Hoy");
    }

    private void artVendidosHoy() {
        SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        Calendar hoy = Calendar.getInstance();
        //String DESDE = FORMAT.format(hoy.getTime());
        //String HASTA = FORMAT.format(hoy.getTime());
        fecha_desde = FORMAT.format(hoy.getTime());
        fecha_hasta = FORMAT.format(hoy.getTime());

        mostrarTicket(fecha_desde, fecha_hasta);
    }

    private void artVendidosAyer() {
        SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        Calendar hoy = Calendar.getInstance();
        //String DESDE = FORMAT.format(hoy.getTime());
        //String HASTA = FORMAT.format(hoy.getTime());
        hoy.add(Calendar.DATE, -1);
        //String AYER = FORMAT.format(hoy.getTime());
        fecha_desde = FORMAT.format(hoy.getTime());
        fecha_hasta = FORMAT.format(hoy.getTime());

        mostrarTicket(fecha_desde, fecha_hasta);
    }

    private void artVendidosEstaSemana() {
        SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        // obtenemos la fecha
        Calendar fecha = Calendar.getInstance();
        // restamos una semana
        fecha.add(Calendar.WEEK_OF_YEAR, 0);
        // calculamos el primer dia de la semana anterior
        Calendar inicioSemanaAnterior = resetCalenderTime((Calendar) fecha.clone()); // clonamos la fecha
        inicioSemanaAnterior.set(Calendar.DAY_OF_WEEK, inicioSemanaAnterior.getActualMinimum(Calendar.DAY_OF_WEEK));
        // calculamos el ultimo dia del mes anterior
        Calendar finSemanaAnterior = resetCalenderTime2((Calendar) fecha.clone());
        finSemanaAnterior.set(Calendar.DAY_OF_WEEK, inicioSemanaAnterior.getActualMaximum(Calendar.DAY_OF_WEEK));
        //String ISP = FORMAT.format(inicioSemanaAnterior.getTime());
        //String FSP = FORMAT.format(finSemanaAnterior.getTime());

        fecha_desde = FORMAT.format(inicioSemanaAnterior.getTime());
        fecha_hasta = FORMAT.format(finSemanaAnterior.getTime());

        mostrarTicket(fecha_desde, fecha_hasta);
    }

    private void artVendidoSemanaPasada() {
        SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        // obtenemos la fecha
        Calendar fecha2 = Calendar.getInstance();
        // restamos una semana
        fecha2.add(Calendar.WEEK_OF_YEAR, -1);
        // calculamos el primer dia de la semana anterior
        Calendar inicioSemanaAnterior2 = resetCalenderTime((Calendar) fecha2.clone()); // clonamos la fecha
        inicioSemanaAnterior2.set(Calendar.DAY_OF_WEEK, inicioSemanaAnterior2.getActualMinimum(Calendar.DAY_OF_WEEK));
        // calculamos el ultimo dia del mes anterior
        Calendar finSemanaAnterior2 = resetCalenderTime2((Calendar) fecha2.clone());
        finSemanaAnterior2.set(Calendar.DAY_OF_WEEK, inicioSemanaAnterior2.getActualMaximum(Calendar.DAY_OF_WEEK));
        //String ISP2 = FORMAT.format(inicioSemanaAnterior2.getTime());
        //String FSP2 = FORMAT.format(finSemanaAnterior2.getTime());

        fecha_desde = FORMAT.format(inicioSemanaAnterior2.getTime());
        fecha_hasta = FORMAT.format(finSemanaAnterior2.getTime());
        mostrarTicket(fecha_desde, fecha_hasta);
        //JOptionPane.showMessageDialog(null, "DESDE: " + ISP2 + " HASTA:" + FSP2);

    }

    private void artVenidosDelMes() {
        SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        // obtenemos la fecha
        Calendar fecha3 = Calendar.getInstance();
        // restamos una semana
        fecha3.add(Calendar.DAY_OF_MONTH, 0);
        // calculamos el primer dia de la semana anterior
        Calendar inicioSemanaAnterior3 = resetCalenderTime((Calendar) fecha3.clone()); // clonamos la fecha
        inicioSemanaAnterior3.set(Calendar.DAY_OF_MONTH, inicioSemanaAnterior3.getActualMinimum(Calendar.DAY_OF_MONTH));
        // calculamos el ultimo dia del mes anterior
        Calendar finSemanaAnterior3 = resetCalenderTime2((Calendar) fecha3.clone());
        finSemanaAnterior3.set(Calendar.DAY_OF_MONTH, inicioSemanaAnterior3.getActualMaximum(Calendar.DAY_OF_MONTH));
        //String ISP3 = FORMAT.format(inicioSemanaAnterior3.getTime());
        //String FSP3 = FORMAT.format(finSemanaAnterior3.getTime());

        fecha_desde = FORMAT.format(inicioSemanaAnterior3.getTime());
        fecha_hasta = FORMAT.format(finSemanaAnterior3.getTime());
        mostrarTicket(fecha_desde, fecha_hasta);

    }

    public void periodoEspecifico() {
        SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date desde = new java.util.Date();
        desde = fechachooser_desde.getDate();
        //String p_fecha_Desde2 = FORMAT.format(desde);
        fecha_desde = FORMAT.format(desde);

        java.util.Date hasta = new java.util.Date();
        hasta = fechachooser_hasta.getDate();
        //String p_fecha_Hasta2 = FORMAT.format(hasta);
        fecha_hasta = FORMAT.format(hasta);

        mostrarTicket(fecha_desde, fecha_hasta);
    }

    private void cmboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmboxItemStateChanged
        periodoFecha.setVisible(false);
        int op = cmbox.getSelectedIndex();
        switch (op) {
            case 0: //HOY
                //mostrarTicket(DESDE, HASTA);
                artVendidosHoy();
                break;
            case 1: //AYER
                /*hoy.add(Calendar.DATE, -1);
                String AYER = FORMAT.format(hoy.getTime());
                mostrarTicket(AYER, AYER);
                 */
                artVendidosAyer();
                break;
            case 2: //ESTA SEMANA
                artVendidosEstaSemana();
                break;
            case 3: //LA SEMANA PASADA
                artVendidoSemanaPasada();
                break;
            case 4: //DEL MES
                artVenidosDelMes();
                break;
            case 5:  //FECHA EN PARTICULAR
                periodoFecha.setVisible(true);
                periodoEspecifico();
                break;
            default:

                break;
        }
    }//GEN-LAST:event_cmboxItemStateChanged

    public static Calendar resetCalenderTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static Calendar resetCalenderTime2(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar;
    }

    static boolean desde = false;
    private void fechachooser_desdePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_fechachooser_desdePropertyChange
        //JOptionPane.showMessageDialog(null, "Modificaste fecha");
        periodoEspecifico();
        desde = true;
    }//GEN-LAST:event_fechachooser_desdePropertyChange

    private void fechachooser_hastaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_fechachooser_hastaPropertyChange
        //periodoEspecifico();
        if (desde == true) {
            periodoEspecifico();
            desde = false;
        }
    }//GEN-LAST:event_fechachooser_hastaPropertyChange

    private void txtnoseriePromocionKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtnoseriePromocionKeyReleased
        String serie = txtnoseriePromocion.getText();
        String sql = "select * from producto where noserie= '" + serie + "'";
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                lblPrecioNormal.setText(rs.getString("precioVenta"));
                lblPrecioCosto.setText(rs.getString("precioCosto"));
                lblDescripcionPromocion.setText(rs.getString("descripcion"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            //return null;
        }
    }//GEN-LAST:event_txtnoseriePromocionKeyReleased

    private void guardarPromocion() {
        if (txtnombrePromocion.getText().length() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Debes ingresar un nombre ala promocion");
            txtnombrePromocion.requestFocus();
            return;
        }
        if (txtnoseriePromocion.getText().length() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Ingrese el Numero de serie de la promocion");
            txtnoseriePromocion.requestFocus();
            return;
        }
        if (txtdesdePromocion.getText().length() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Debes ingresar el inicio de la cantidad de la prmocion");
            txtdesdePromocion.requestFocus();
            return;
        }
        if (txthastaPromocion.getText().length() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Debes ingregar el fin de la cantidad de la promocion");
            txthastaPromocion.requestFocus();
            return;
        }
        if (txtprecioPromocion.getText().length() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Debes ingresar el precio Unitario de la promocion");
            txtprecioPromocion.requestFocus();
            return;
        }

        if (Integer.parseInt(txtdesdePromocion.getText()) > Integer.parseInt(txthastaPromocion.getText())) {
            JOptionPane.showMessageDialog(null, "Por favor verifique que el rango sea correcto y que el valor \"Desde\" sea menor que el valor de \"Hasta\". Ejemplo (5 a 15)", "Advertencia", JOptionPane.WARNING_MESSAGE);

        } else {
            if (Double.parseDouble(txtprecioPromocion.getText()) < Double.parseDouble(lblPrecioCosto.getText())) {
                int opcion = JOptionPane.showConfirmDialog(null, "El precio de promoción es MENOR que el precio de costo ¿Segurro que desea crear esta promoción?", "Confirmar", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (opcion == 0) {
                    ModelPromocion dts = new ModelPromocion();
                    ControllerPromocion func = new ControllerPromocion();
                    //dts.setIdproducto(idproducto);
                    dts.setIdproducto(Integer.parseInt(txtnoseriePromocion.getText()));
                    dts.setNombre(txtnombrePromocion.getText());
                    dts.setDesde(Integer.parseInt(txtdesdePromocion.getText()));
                    dts.setHasta(Integer.parseInt(txthastaPromocion.getText()));
                    dts.setPreciopromocion(Double.parseDouble(txtprecioPromocion.getText()));
                    func.insertar(dts);
                    mostrarPromocion("");
                }
            } else {
                ModelPromocion dts = new ModelPromocion();
                ControllerPromocion func = new ControllerPromocion();
                //dts.setIdproducto(idproducto);
                dts.setIdproducto(Integer.parseInt(txtnoseriePromocion.getText()));
                dts.setNombre(txtnombrePromocion.getText());
                dts.setDesde(Integer.parseInt(txtdesdePromocion.getText()));
                dts.setHasta(Integer.parseInt(txthastaPromocion.getText()));
                dts.setPreciopromocion(Double.parseDouble(txtprecioPromocion.getText()));
                func.insertar(dts);
                mostrarPromocion("");
            }
        }

    }
    private void cboCategoriaInItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboCategoriaInItemStateChanged
        String selected = (String) cboCategoriaIn.getSelectedItem();
        mostrarReporteInv(selected, cboCategoriaIn.getSelectedIndex());
    }//GEN-LAST:event_cboCategoriaInItemStateChanged

    int filaReporte = 0;
    private void dtInvReporteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dtInvReporteMouseClicked
        //filaReporte = dtInvReporte.getSelectedRow();
        //noserie = dtInvReporte.getValueAt(filaReporte, 1).toString();

    }//GEN-LAST:event_dtInvReporteMouseClicked

    private void abrirModificarProductor() {
        int filaSeleccionada = dtInvReporte.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "No hay registro seleccionado.");
        } else {
            FrmModificarProducto2 ModificarProducto = new FrmModificarProducto2(this, true);
            ModificarProducto.recibirNoserie(filaSeleccionada);
            ModificarProducto.setVisible(true);
        }

        /*frmModificarProducto ModificarProducto = new frmModificarProducto();
        ModificarProducto.recibirNoserie(noserie);
        ventana(ModificarProducto);
         */
    }

    private void agregarInventario() {
        int fila = dtInvReporte.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Por favor seleccione un registro para poder agregar inventario.");
        } else {
            String idproducto = (dtInvReporte.getValueAt(fila, 0).toString());
            String noserie = (dtInvReporte.getValueAt(fila, 1).toString());
            String descripcion = (dtInvReporte.getValueAt(fila, 2).toString());
            //DEVOLUCIÓN DE ARTÍCULOS EN TICKET - VENTA
            String cantidad = JOptionPane.showInputDialog(
                    null,
                    "Cantidad a Agregar;",
                    "Agregar Inventario " + descripcion,
                    JOptionPane.INFORMATION_MESSAGE
            );
            if (cantidad.equals("")) {
                JOptionPane.showMessageDialog(null, "Por favor escriba una cantidad válida.");
            } else {
                boolean res = agregarExistencia(Integer.parseInt(cantidad), Integer.parseInt(idproducto));
                if (res) {
                    mostrarReporteInv("", 0);
                    beep = false;
                    view.FrmMain.notificar("Se agregaron " + cantidad + " unidades.");
                    if (dtInvReporte.getValueAt(fila, 0).equals(idproducto)) {
                        dtInvReporte.changeSelection(fila, 0, false, false);
                    }
                }

            }
        }

    }

    private void jDateChooser4PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jDateChooser4PropertyChange
        generarReporteMov();
    }//GEN-LAST:event_jDateChooser4PropertyChange

    private void generarReporteMov() {
        SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date fecha = new java.util.Date();
        fecha = (java.util.Date) jDateChooser4.getDate();
        String p_fecha = FORMAT.format(fecha);
        mostrarReporteMov(p_fecha, btnBuscar2.getText(), jComboBox5.getSelectedIndex());
    }

    private void hacerCorteHoy() {
        SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date fecha = new java.util.Date();
        String p_fecha_hoy = FORMAT.format(fecha);
        dateCorte.setDate(new java.util.Date());
        corteCaja(p_fecha_hoy, cbxCajero.getSelectedItem().toString(), cbxCajero.getSelectedIndex());
    }

    private void hacerCorte() {
        SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date fecha = new java.util.Date();
        fecha = (java.util.Date) dateCorte.getDate();
        String p_fecha = FORMAT.format(fecha);
        corteCaja(p_fecha, cbxCajero.getSelectedItem().toString(), cbxCajero.getSelectedIndex());
    }
    private void lblPagoClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblPagoClientesMouseClicked
        // TODO add your handling code here:
        frmPagoClientes pagoCliente = new frmPagoClientes();
        ventana(pagoCliente);
        SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date hoy = new java.util.Date();
        hoy = dateCorte.getDate();
        String p_fecha_hoy = FORMAT.format(hoy);
        mostrarPagoCliente(p_fecha_hoy);

    }//GEN-LAST:event_lblPagoClientesMouseClicked

    private void lblSalidaEfectivoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSalidaEfectivoMouseClicked
        // TODO add your handling code here:
        frmPagoProveedores salidaEfectivo = new frmPagoProveedores();
        ventana(salidaEfectivo);
        SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date hoy = new java.util.Date();
        hoy = dateCorte.getDate();
        String p_fecha_hoy = FORMAT.format(hoy);
        mostrarSalidas(p_fecha_hoy);
    }//GEN-LAST:event_lblSalidaEfectivoMouseClicked

    private void btnVentasHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVentasHActionPerformed
        activarV("ventas");
        txtEnterProducto.requestFocus();
    }//GEN-LAST:event_btnVentasHActionPerformed

    private void btnCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarSesionActionPerformed
        int opcion = JOptionPane.showConfirmDialog(null, "¿Desea cerrar sesión?", "Confirmar cierre de sesión", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opcion == 0) {
            guardarInfoConfiEmpresa();
            this.dispose();
            new FrmLogin().setVisible(true);
        }
    }//GEN-LAST:event_btnCerrarSesionActionPerformed

    private void btnVentasHMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnVentasHMouseEntered

    }//GEN-LAST:event_btnVentasHMouseEntered

    private void jComboBox5PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jComboBox5PropertyChange
        generarReporteMov();
    }//GEN-LAST:event_jComboBox5PropertyChange

    private void txtEnterProductoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEnterProductoKeyTyped
        Validacion.letrasNumeros(evt);
    }//GEN-LAST:event_txtEnterProductoKeyTyped

    private void txtbuscarClientesKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtbuscarClientesKeyTyped
        Validacion.soloLetras(evt);
    }//GEN-LAST:event_txtbuscarClientesKeyTyped

    private void txtBuscarProductosKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarProductosKeyTyped
        Validacion.letrasNumeros(evt);
    }//GEN-LAST:event_txtBuscarProductosKeyTyped

    private void txtnombrePromocionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtnombrePromocionKeyTyped
        Validacion.letrasNumeros(evt);
    }//GEN-LAST:event_txtnombrePromocionKeyTyped

    private void txtnoseriePromocionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtnoseriePromocionKeyTyped
        Validacion.letrasNumeros(evt);
    }//GEN-LAST:event_txtnoseriePromocionKeyTyped

    private void txtdesdePromocionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtdesdePromocionKeyTyped
        Validacion.soloNumerosEnteros(evt);
    }//GEN-LAST:event_txtdesdePromocionKeyTyped

    private void txthastaPromocionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txthastaPromocionKeyTyped
        Validacion.soloNumerosEnteros(evt);
    }//GEN-LAST:event_txthastaPromocionKeyTyped

    private void txtprecioPromocionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtprecioPromocionKeyTyped
        Validacion.decimales(evt, txtbuscarem);
    }//GEN-LAST:event_txtprecioPromocionKeyTyped

    private void txtBuscarCategoriaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarCategoriaKeyTyped
        Validacion.letrasNumeros(evt);
    }//GEN-LAST:event_txtBuscarCategoriaKeyTyped

    private void cancelarTicket(int ROW_SELECTED) {
        String idtiket = dtClieCredito.getValueAt(ROW_SELECTED, 0).toString();
        controller.ControllerTicket.cancelarTicket(idtiket);
        vaciarListaCreditos();
        lblSaldoActualCliente.setText(MyFormater.formato(new ControllerClient().saldoActualCliente(String.valueOf(IDCLIENTE_ELIMINAR_TIKET))));
        cargarMeses(IDCLIENTE_ELIMINAR_TIKET); //IDCLIENTE_ELIMINAR_TIKET
        FrmMain.mostrar(""); //PARA ACTUALIZAR SALDO ACTUAL EN EL LISTADO DE CLIENTES
        if (lblSaldoActualCliente.getText().equals(lblLimiteCreditoCliente.getText())) {
            btnAbonarCliente.setEnabled(false);
            btnLiquidarAdeudo.setEnabled(false);
        } else {
            btnAbonarCliente.setEnabled(true);
            btnLiquidarAdeudo.setEnabled(true);
        }
    }
    private void dtClieCreditoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dtClieCreditoMouseClicked
        int row_selected = dtClieCredito.getSelectedRow();
        String idticket = dtClieCredito.getValueAt(row_selected, 0).toString();


    }//GEN-LAST:event_dtClieCreditoMouseClicked

    private void btnCobrarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCobrarVentaActionPerformed
        abrirCobrarTicket();
        txtEnterProducto.requestFocus();
    }//GEN-LAST:event_btnCobrarVentaActionPerformed

    private void btnImprimirCorteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirCorteActionPerformed
        if (IMPRIMIRCORTE) {
            SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat FORMAT2 = new SimpleDateFormat("dd-MM-yyyy");
            java.util.Date fecha = new java.util.Date();
            fecha = (java.util.Date) dateCorte.getDate();
            String p_fecha = FORMAT.format(fecha);
            String fecha_corte = FORMAT2.format(fecha);

            Double[] datosCorteCaja = new ControllerCorte().corte(p_fecha, cbxCajero.getSelectedItem().toString(), cbxCajero.getSelectedIndex());
            extras.Impresora.imprimirCorte(fecha_corte, cbxCajero.getSelectedItem().toString(), datosCorteCaja);
        } else {
            JOptionPane.showMessageDialog(null, "Para poder imprimir el corte es necesario que se realice primero, por favor haga clic en el botón \"Hacer corte\". Gracias");
        }
    }//GEN-LAST:event_btnImprimirCorteActionPerformed

    private void btnMostrarOpcionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMostrarOpcionesActionPerformed
        activarC("inicio");
        guardarInfoConfiEmpresa();
    }//GEN-LAST:event_btnMostrarOpcionesActionPerformed

    private void btnExportarClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportarClientesActionPerformed
        exportarTabla(dtClieListado);
    }//GEN-LAST:event_btnExportarClientesActionPerformed

    private void btneliminarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btneliminarClienteActionPerformed
        eliminarCliente();
    }//GEN-LAST:event_btneliminarClienteActionPerformed

    private void btnImprimirListaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirListaActionPerformed
        //imprimir(dtClieListado, "LISTA DE CLIENTES");
        extras.Impresora.imprimirListadoClientes(txtbuscarClientes.getText());
    }//GEN-LAST:event_btnImprimirListaActionPerformed

    private void btnnuevoClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnnuevoClienteActionPerformed
        nuevoCliente();
    }//GEN-LAST:event_btnnuevoClienteActionPerformed

    private void btnguardarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnguardarClienteActionPerformed
        guardarCliente();
    }//GEN-LAST:event_btnguardarClienteActionPerformed

    private void btncancelarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncancelarClienteActionPerformed
        txtnombreCliente.setText("");
        txtdireccionCliente.setText("");
        txttelefonoCliente.setText("");
        txtcreditoCliente.setText("");

        btnguardarCliente.setEnabled(false);
        btncancelarCliente.setEnabled(false);
    }//GEN-LAST:event_btncancelarClienteActionPerformed

    private void btnAbonarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbonarClienteActionPerformed
        abrirAbonar();
    }//GEN-LAST:event_btnAbonarClienteActionPerformed

    private void btnLiquidarAdeudoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLiquidarAdeudoActionPerformed
        liquidarDeuda();
        cargarMeses("" + Idcliente);
    }//GEN-LAST:event_btnLiquidarAdeudoActionPerformed

    private void liquidarDeuda() {
        int opcion = JOptionPane.showConfirmDialog(null, "¿Seguro que deseas liquidar el adeudo de este cliente?", "Confirmar", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opcion == 0) {
            ModelPaymet dts = new ModelPaymet();
            ControllerPayment func = new ControllerPayment();

            //LIQUIDACIÓN DE CUENTA
            try {
                extras.Impresora.imprimirLiquidacionCuentaCliente(confiEncabezadoTicket, getNombreCliente(Idcliente), String.valueOf(Idcliente));

            } catch (PrinterException ex) {
                ex.printStackTrace();
            }

            dts.setIdcliente(Idcliente);
            new ControllerTicket().liquidarTicketCliente(Idcliente);
            if (func.eliminar(dts)) {
                cargarMeses("" + Idcliente);
            }
            cargarMeses("" + Idcliente);
            lblSaldoActualCliente.setText(MyFormater.formato(new ControllerClient().saldoActualCliente(String.valueOf(Idcliente))));
            vaciarListaCreditos();
            mostrar("");
            btnAbonarCliente.setEnabled(false);
            btnLiquidarAdeudo.setEnabled(false);
        }
    }


    private void btnDetAbonoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDetAbonoActionPerformed
        abrirDetalleAbono();
    }//GEN-LAST:event_btnDetAbonoActionPerformed

    private void btnImprimirEdoCuentaCompletoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirEdoCuentaCompletoActionPerformed
        //ESTADO DE CUENTA
        try {
            extras.Impresora.imprimirEstadoCuentaCliente(confiEncabezadoTicket, getNombreCliente(Idcliente), String.valueOf(Idcliente));

        } catch (PrinterException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btnImprimirEdoCuentaCompletoActionPerformed

    private void btnEliminarTicketClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarTicketClienteActionPerformed
        int ROW_SELECTED = dtClieCredito.getSelectedRow();
        if (ROW_SELECTED == -1) {
            JOptionPane.showMessageDialog(null, "Selecciona un ticket de la izquierda.");
        } else {
            int opcion = JOptionPane.showConfirmDialog(null, "¿Seguro que desea eliminar este ticket?", "Confirmar", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (opcion == 0) {
                cancelarTicket(ROW_SELECTED);
                cargarMeses(IDCLIENTE_ELIMINAR_TIKET); //IDCLIENTE_ELIMINAR_TIKET
            }

        }
    }//GEN-LAST:event_btnEliminarTicketClienteActionPerformed

    private void btnImprimirTicketTabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirTicketTabActionPerformed
        //imprimir(dtClieCredito, "LISTA DE TICKET");
        try {

            String idticketCRE = "";
            String totalTicketCRE = "";
            String idclienteCRE = "";
            String idempleadoCRE = "";

            int filaSe = dtClieCredito.getSelectedRow();
            if (filaSe == -1) {
                JOptionPane.showMessageDialog(null, "Por favor elija un ticket de la izquierda...");
            } else {
                idticketCRE = dtClieCredito.getValueAt(filaSe, 0).toString();
                totalTicketCRE = String.valueOf(controller.ControllerTicket.total);
                idclienteCRE = dtClieCredito.getValueAt(filaSe, 5).toString();;
                idempleadoCRE = dtClieCredito.getValueAt(filaSe, 6).toString();
                int idem = Integer.parseInt(idempleadoCRE);
                extras.Impresora.imprimirCopiaTicketCredito(getNombreEmpleado(idem),
                        confiEncabezadoTicket,
                        confiMensajeAgradecieminto,
                        nombreFolio,
                        idticketCRE,
                        totalTicketCRE,
                        IMPUESTO,
                        getNombreCliente(Integer.parseInt(idclienteCRE))
                );
                beep = false;
                notificar("Ticket impreso");

            }
        } catch (PrinterException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btnImprimirTicketTabActionPerformed

    public static String getNombreCliente(int idcliente) {
        String sSQL = "SELECT\n"
                + "nombre\n"
                + "FROM cliente\n"
                + "WHERE idcliente = " + idcliente;
        String NOMBRE_CLIENTE = "";
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            while (rs.next()) {
                NOMBRE_CLIENTE = rs.getString("nombre");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error getNombreCliente: " + e);
        }

        return NOMBRE_CLIENTE;
    }

    private void btnEliminarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarProductoActionPerformed
        eliminarProducto();
    }//GEN-LAST:event_btnEliminarProductoActionPerformed

    private void btnImportarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportarProductoActionPerformed
        try {
            FrmImportarProducto importar = new FrmImportarProducto(this, true);
            importar.setVisible(true);
        } catch (Exception er) {
            JOptionPane.showMessageDialog(null, "Error: " + er.getMessage());
        }
    }//GEN-LAST:event_btnImportarProductoActionPerformed

    private void btnnuevoProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnnuevoProductoActionPerformed
        nuevoProducto();
    }//GEN-LAST:event_btnnuevoProductoActionPerformed

    private void btnguardarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnguardarProductoActionPerformed
        guardarProducto();
    }//GEN-LAST:event_btnguardarProductoActionPerformed

    private void btnProCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProCancelarActionPerformed
        txtNoSeriePro.setEnabled(false);
        txtdescripcionPro.setEnabled(false);
        txtprecioCostoPro.setEnabled(false);
        txtprecioVentaPro.setEnabled(false);
        txtprecioMayoreoPro.setEnabled(false);
        cbocategoriaPro.setEnabled(false);
        txtcantidadActualPro.setEnabled(false);
        txtcantidadMinimoPro.setEnabled(false);

        btnProCancelar.setEnabled(false);
        // btnnuevoCliente.setEnabled(false);
        btnguardarProducto.setEnabled(false);
    }//GEN-LAST:event_btnProCancelarActionPerformed

    private void btnExportarVentasPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportarVentasPedidoActionPerformed
        exportarTabla(dtProVentasPedido);
    }//GEN-LAST:event_btnExportarVentasPedidoActionPerformed

    private void btnImprimirProPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirProPedidoActionPerformed
        extras.Impresora.imprimirArticulosVendidos(getNombreEmpleado(IdempleadoU), fecha_desde, fecha_hasta);
    }//GEN-LAST:event_btnImprimirProPedidoActionPerformed

    private void btnEliminarPromocionProActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarPromocionProActionPerformed
        eliminarPromocion();
    }//GEN-LAST:event_btnEliminarPromocionProActionPerformed

    private void eliminarPromocion() {
        int fila = dtProPromocion.getSelectedRow();
        String idpromocion = dtProPromocion.getValueAt(fila, 0).toString();
        int confirmacion = JOptionPane.showConfirmDialog(rootPane, "Estas seguro de eliminar la promoción", "Confirmacion", 0);

        if (confirmacion == 0) {
            ControllerPromocion func = new ControllerPromocion();
            ModelPromocion dts = new ModelPromocion();
            dts.setIdpromocion(Integer.parseInt(idpromocion));
            func.eliminar(dts);
            mostrarPromocion("");
        }
    }
    private void btnGuardarPromocionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarPromocionActionPerformed
        guardarPromocion();
    }//GEN-LAST:event_btnGuardarPromocionActionPerformed

    private void btnnuevoCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnnuevoCategoriaActionPerformed
        nuevaCategoria();
    }//GEN-LAST:event_btnnuevoCategoriaActionPerformed

    private void btnguardarCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnguardarCategoriaActionPerformed
        guardarCategoria();
    }//GEN-LAST:event_btnguardarCategoriaActionPerformed

    private void btncancelarCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncancelarCategoriaActionPerformed
        txtnombreCategoria.setEnabled(false);
        btnguardarCategoria.setEnabled(false);
        btncancelarCategoria.setEnabled(false);
    }//GEN-LAST:event_btncancelarCategoriaActionPerformed

    private void btneliminarCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btneliminarCategoriaActionPerformed
        eliminarCategoria();
    }//GEN-LAST:event_btneliminarCategoriaActionPerformed

    private void btnImprimirMovimientoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirMovimientoActionPerformed
        //imprimir(dtInvHistorialMov, "HISTORIA DE TODOS LOS MOVIMIENTOS");

        int filaSeleccionada = dtInvHistorialMov.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "No hay registro seleccionado.");
        } else {
            String idinventario = dtInvHistorialMov.getValueAt(filaSeleccionada, 0).toString();

            try {
                extras.Impresora.imprimirDatosMovimiento(Integer.parseInt(idinventario));

            } catch (PrinterException ex) {
                ex.printStackTrace();
            }
        }

    }//GEN-LAST:event_btnImprimirMovimientoActionPerformed

    private void btnImprimirMovimiento1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirMovimiento1ActionPerformed
        try {
            SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date fecha = new java.util.Date();
            fecha = (java.util.Date) jDateChooser4.getDate();
            String p_fecha = FORMAT.format(fecha);
            String param2 = btnBuscar2.getText();
            int combo = jComboBox5.getSelectedIndex();
            //jLabel123.setText("FECHA: "+p_fecha+" PARAM2: "+param2+" INDEX: "+combo);
            extras.Impresora.imprimirMovimientosTodos(p_fecha, param2, combo);

        } catch (PrinterException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btnImprimirMovimiento1ActionPerformed

    private void btninputinventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btninputinventarioActionPerformed
        //agregarInv();
    }//GEN-LAST:event_btninputinventarioActionPerformed

    private void btnajusteinventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnajusteinventarioActionPerformed
        ajustarInventario();
    }//GEN-LAST:event_btnajusteinventarioActionPerformed

    private void btnExportarExcelBajosInventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportarExcelBajosInventarioActionPerformed
        exportarTabla(dtInvBajos);
    }//GEN-LAST:event_btnExportarExcelBajosInventarioActionPerformed

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        //imprimir(dtInvReporte, "INVENTARIO");
        //Impresora.imprimirReporteInventario();
        String strCategoria = cboCategoriaIn.getSelectedItem().toString();
        extras.Impresora.imprimirReporteInventario(strCategoria);
    }//GEN-LAST:event_btnImprimirActionPerformed


    private void btnExportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportarActionPerformed
        exportarTabla(dtInvReporte);
    }//GEN-LAST:event_btnExportarActionPerformed

    private void btnModificarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarProductoActionPerformed
        abrirModificarProductor();
    }//GEN-LAST:event_btnModificarProductoActionPerformed

    private void btnAgregarInventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarInventarioActionPerformed
        agregarInventario();
    }//GEN-LAST:event_btnAgregarInventarioActionPerformed

    private void btnHacerCorteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHacerCorteActionPerformed
        hacerCorte();
    }//GEN-LAST:event_btnHacerCorteActionPerformed

    private void btnExaminarLogotipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExaminarLogotipoActionPerformed
        examinarLogotipo();
    }//GEN-LAST:event_btnExaminarLogotipoActionPerformed

    private void btneliminaremActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btneliminaremActionPerformed
        eliminarEmpleado();
    }//GEN-LAST:event_btneliminaremActionPerformed

    private void dtEmListadoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dtEmListadoMouseClicked
        // TODO add your handling code here:
        btnguardarem.setText("Guardar Cambios");
        habilitarEm();
        btneliminarem.setEnabled(true);
        accion = "editar";

        int fila = dtEmListado.getSelectedRow();

        txtidempleado.setText(dtEmListado.getValueAt(fila, 0).toString());
        txtnombreem.setText(dtEmListado.getValueAt(fila, 1).toString());
        txtusuarioem.setText(dtEmListado.getValueAt(fila, 2).toString());
        txtpasswordem.setText(dtEmListado.getValueAt(fila, 3).toString());
        txtEmail.setText(dtEmListado.getValueAt(fila, 4).toString());
        String idem = dtEmListado.getValueAt(fila, 5).toString();
        int id = Integer.parseInt(idem);
        cboprivilegioem.setSelectedIndex(id);
    }//GEN-LAST:event_dtEmListadoMouseClicked

    private void btncancelaremActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncancelaremActionPerformed
        inhabilitarEm();
    }//GEN-LAST:event_btncancelaremActionPerformed

    private void btnguardaremActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnguardaremActionPerformed
        guardarEmpleado();
    }//GEN-LAST:event_btnguardaremActionPerformed

    private void btnnuevoemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnnuevoemActionPerformed
        nuevoEmpleado();
    }//GEN-LAST:event_btnnuevoemActionPerformed

    private void txtnombreemKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtnombreemKeyTyped
        Validacion.soloLetras(evt);
    }//GEN-LAST:event_txtnombreemKeyTyped

    private void txtbuscaremKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtbuscaremKeyReleased
        // TODO add your handling code here:
        mostrarEm(txtbuscarem.getText());
    }//GEN-LAST:event_txtbuscaremKeyReleased

    private void btnHacerCorte2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHacerCorte2ActionPerformed
        lblTotalInfo.setText("-");
        lblPagoConInfo.setText("-");
        lblCambioInfo.setText("-");
    }//GEN-LAST:event_btnHacerCorte2ActionPerformed

    private void btnReimprimirUltimoTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReimprimirUltimoTicketActionPerformed
        //reimprimirUltimoTicket();
        extras.Impresora.imprimirUltimoTicket(getNombreEmpleado(IdempleadoU),
                confiEncabezadoTicket,
                confiMensajeAgradecieminto,
                nombreFolio
        );
    }//GEN-LAST:event_btnReimprimirUltimoTicketActionPerformed

    private void btnVentasDevolucionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVentasDevolucionesActionPerformed
        abrirVentasDevoluciones();
    }//GEN-LAST:event_btnVentasDevolucionesActionPerformed

    private void btnAgregarProdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarProdActionPerformed
        String no_serie_pro = txtEnterProducto.getText();
        enterProducto(no_serie_pro, "1");
        txtEnterProducto.requestFocus();
    }//GEN-LAST:event_btnAgregarProdActionPerformed

    private void jTabbedPane4PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTabbedPane4PropertyChange
        int index = jTabbedPane4.getSelectedIndex();
        if (dobleClicked == true) {

        } else {
            lblNombreCliente.setText("-");
            lblSaldoActualCliente.setText("-");
            lblLimiteCreditoCliente.setText("-");
        }
    }//GEN-LAST:event_jTabbedPane4PropertyChange

    private void txtnombreClienteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtnombreClienteKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            guardarCliente();
        }
    }//GEN-LAST:event_txtnombreClienteKeyPressed

    private void txtdireccionClienteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtdireccionClienteKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            guardarCliente();
        }
    }//GEN-LAST:event_txtdireccionClienteKeyPressed

    private void txttelefonoClienteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txttelefonoClienteKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            guardarCliente();
        }
    }//GEN-LAST:event_txttelefonoClienteKeyPressed

    private void txtcreditoClienteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtcreditoClienteKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            guardarCliente();
        }
    }//GEN-LAST:event_txtcreditoClienteKeyPressed

    private void btnBuscar2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnBuscar2KeyReleased
        generarReporteMov();
    }//GEN-LAST:event_btnBuscar2KeyReleased

    private void btnHacerCorteHoyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHacerCorteHoyActionPerformed
        hacerCorteHoy();
    }//GEN-LAST:event_btnHacerCorteHoyActionPerformed

    private void rbImpuestoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbImpuestoActionPerformed
        if (rbImpuesto.isSelected()) {
            txtImpuesto.setVisible(true);
        } else {
            txtImpuesto.setVisible(false);
        }
    }//GEN-LAST:event_rbImpuestoActionPerformed

    private void lblLogotipoMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLogotipoMouseDragged
        //setLocation(getLocation().x + evt.getX() - x, getLocation().y + evt.getY() - y);
    }//GEN-LAST:event_lblLogotipoMouseDragged

    private void lblLogotipoMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLogotipoMousePressed
        x = evt.getX();
        y = evt.getY();
    }//GEN-LAST:event_lblLogotipoMousePressed

    private void formWindowDeiconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowDeiconified
        setExtendedState(getExtendedState() | FrmMain.MAXIMIZED_BOTH);
    }//GEN-LAST:event_formWindowDeiconified

    private void guardarInfoConfiEmpresa() {
        String nombreE = "";
        String calleE = "";
        String localE = "";
        String sucursalE = "";
        String telefonoE = "";
        if (txtEmpresaNombre.getText().equals("")) {
            nombreE = "-";
        } else {
            nombreE = txtEmpresaNombre.getText();
        }
        if (txtEmpresaCalle.getText().equals("")) {
            calleE = "-";
        } else {
            calleE = txtEmpresaCalle.getText();
        }
        if (txtEmpresaLocal.getText().equals("")) {
            localE = "-";
        } else {
            localE = txtEmpresaLocal.getText();
        }
        if (txtEmpresaSucursal.getText().equals("")) {
            sucursalE = "-";
        } else {
            sucursalE = txtEmpresaSucursal.getText();
        }
        if (txtEmpresaTelefono.getText().equals("")) {
            telefonoE = "-";
        } else {
            telefonoE = txtEmpresaTelefono.getText();
        }
        confiEncabezadoTicket
                = nombreE + "/"
                + calleE + "/"
                + localE + "/"
                + sucursalE + "/"
                + telefonoE;

        confiMensajeAgradecieminto = txtEmpresaAgradecimiento.getText();
        nombreFolio = txtFolioConfiguracion.getText();
        //rutaLogo = txtRutaLogotipo.getText();
        //activvadoImpuestos = "";
        if (rbImpuesto.isSelected()) {
            activvadoImpuestos = "TRUE/" + "0" + txtImpuesto.getText();
        } else {
            activvadoImpuestos = "FALSE/" + "0";
        }
        nombreImpresora = comboImpresora.getSelectedItem().toString();

        String strTicket = confiEncabezadoTicket + "/" + confiMensajeAgradecieminto;
        String strImpuesto = activvadoImpuestos;
        guardarconfi2(
                nombreFolio,
                strTicket,
                strImpuesto,
                nombreImpresora
        );
        recuperarConfi();
    }

    public void guardarconfi2(
            String nombreFolio,
            String strTicket,
            String strImpuesto,
            String nombreImpresora) {
        String sSQL = "update configuracion set folio = ?, str_ticket = ?, impuesto = ?, nombre_impresora  = ? where idconfiguracion  = 1";

        try {
            PreparedStatement pst = cn.prepareStatement(sSQL);
            pst.setString(1, nombreFolio);
            pst.setString(2, strTicket);
            pst.setString(3, strImpuesto);
            pst.setString(4, nombreImpresora);
            int n = pst.executeUpdate();
            if (n > 0) {
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al guardar confi 2: " + ex);
        }

    }

    private void eliminarCategoria() {
        if (!txtidcategoria.getText().equals("")) {
            int confirmacion = JOptionPane.showConfirmDialog(null, "Estas seguro de eliminar la categoría", "Confirmacion", 2);

            if (confirmacion == 0) {
                ControllerCategory func = new ControllerCategory();
                ModelCategory dts = new ModelCategory();
                dts.setIdcategoria(Integer.parseInt(txtidcategoria.getText()));
                func.eliminar(dts);
                mostrarCat("");
                inhabilitarCat();
            }
        }
    }

    public static void imprimir(JTable tabla, String header) {
        try {
            MessageFormat headerFormat = new MessageFormat(header);
            MessageFormat footerFormat = new MessageFormat("Page {0,number,integer}");
            tabla.print(JTable.PrintMode.FIT_WIDTH, headerFormat, footerFormat);

        } catch (PrinterException ex) {
            ex.printStackTrace();
        }
    }

    public static String seleccionarEmpleadoLogin(int idempleado) {
        String sSQL = "SELECT nombre FROM empleado WHERE idempleado = " + idempleado;
        String nombreEmpleado = "";
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            while (rs.next()) {
                nombreEmpleado = rs.getString("nombre");
            }
            return nombreEmpleado;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            return "";
        }
    }
    //ImageIcon bntC = new ImageIcon("src/Imagenes/btnCursor.png");
    //ImageIcon bntNormal = new ImageIcon("src/Imagenes/btnNormal.png");

    public boolean agregarExistencia(int cantidadIncrementar, int idproducto) {
        String sSQL = "UPDATE producto SET existencia = existencia + ?"
                + " WHERE idProducto = ?";

        try {
            PreparedStatement pst = cn.prepareStatement(sSQL);
            pst.setInt(1, cantidadIncrementar);
            pst.setInt(2, idproducto);
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

    public static void enterProducto(String codigo, String cantidad) {
        beep = true;
        if (!codigo.equals("")) {
            if (consultarProducto(codigo) != true) {
                notificarNotFounf("Producto no encotrado");
            } else {
                if (verificarExistencia(codigo) < Integer.parseInt(cantidad)) {
                    notificar("Inventario insuficiente. Hay: " + verificarExistencia(codigo) + " disponibles");
                } else {
                    // agregarProductoLista(codigo, cantidad);
                }

            }
            //MOSTRAR TOTAL IMPORTE
            totalImporte();

        } else {
            JOptionPane.showMessageDialog(null, "Ingrese el codigo del producto para agregar a la venta.");
        }
    }

    

    public static void totalImporte() {
        int row = modelo.getRowCount();
        String subT = "";
        String cant = "";
        Double subTotal = 0d;
        Double iva = 0d;
        Double totalTi = 0d;
        int cantidad = 0;

        for (int i = 0; i < row; i++) {
            subT = modelo.getValueAt(i, 5).toString();
            cant = modelo.getValueAt(i, 4).toString();

            subTotal += Double.parseDouble(subT);
            cantidad += Integer.parseInt(cant);
        }

        iva = (subTotal * IMPUESTO / 100);
        //jLabel35.setText("SUBTOTAL: "+subTotal+" IMPUESTO: "+IMPUESTO+" IVA AGREGADO: "+iva);
        totalTi = subTotal + iva;
        System.out.println(subTotal + " - " + iva);
        lblTotalCantProd.setText("" + cantidad);
        lblTotalImporte.setText(new MyFormater().formato(totalTi));
        lblSubTotalImporte.setText("Subtotal: " + new MyFormater().formato(subTotal));

    }

    public static String[] calcular(String noserie, String cant) {
        String pre = "";
        String can = "";
        String imp = "";
        double precio = 0d, precio_venta = 0d, precio_promocion = 0d, importe = 0d;
        int cantidad = 0;
        String valores[] = new String[3];
        //SELECCIONAR FILA PARA OBTENER DATOS
        for (int i = 0; i < dtTicket.getRowCount(); i++) {
            if (dtTicket.getValueAt(i, 1).equals(noserie)) {
                dtTicket.changeSelection(i, 1, false, false);
                pre = dtTicket.getValueAt(i, 3).toString();
                can = dtTicket.getValueAt(i, 4).toString();
                imp = dtTicket.getValueAt(i, 5).toString();
                valores[0] = "" + i; //Filla seleccionada
                break;
            }
        }
        if (cant.equals("1")) {
            cantidad = Integer.parseInt(can) + 1;
        } else {
            cantidad = Integer.parseInt(can) + Integer.parseInt(cant);

        }

        valores[1] = "" + cantidad;
        precio_venta = Double.parseDouble(pre);
        precio_promocion = aplicablePromocion(noserie, valores[1], Integer.parseInt(valores[0]));

        precio = (precio_promocion == 0) ? precio_venta : precio_promocion;
        importe = precio * cantidad;
        valores[2] = "" + importe;
        return valores;
    }

    public static int consultarRegistros(String codigoserie) {
        int resp = 0;

        //for (int i = 0; i < dtListadoVentas.getSelectedRow() ; i++) {
        for (int i = 0; i < dtTicket.getRowCount(); i++) {
            if (dtTicket.getValueAt(i, 1).equals(codigoserie)) {
                dtTicket.changeSelection(i, 1, false, false);
                resp++;
                //JOptionPane.showMessageDialog(null, "registro encotrado");
                break;
            }

        }
        return resp;
    }

    public static boolean consultarProducto(String noserie) {
        boolean resp = false;
        String cod = "";
        try {
            String sql1 = "SELECT noserie FROM producto WHERE noserie= '" + noserie + "' AND inactivo <> 1";
            //String sql2 = "SELECT CASE WHEN EXISTS ("+ sql1 +") THEN TRUE ELSE FALSE END";

            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql1);
            while (rs.next()) {
                cod = rs.getString("noserie");
            }
            if (!cod.equals("")) {
                resp = true;
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return resp;
    }

    public static Integer verificarExistencia(String noserie) {
        int existencia = 0;
        try {
            String sql1 = "SELECT existencia FROM producto WHERE noserie= '" + noserie + "'";

            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql1);
            while (rs.next()) {
                existencia = rs.getInt("existencia");
            }

            existencia = existencia - cantidad(noserie);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return existencia;
    }

    public static Integer cantidad(String noserie) {
        String cant = "";
        int cantidad = 0;
        for (int i = 0; i < dtTicket.getRowCount(); i++) {
            if (dtTicket.getValueAt(i, 1).equals(noserie)) {
                cant = (String) dtTicket.getValueAt(i, 4);
                cantidad = Integer.parseInt(cant);
                break;
            }
        }
        return cantidad;
    }

    public static Double aplicablePromocion(String noserie, String cantidad, int fila) {
        int cant = Integer.parseInt(cantidad);
        int desde = 0, hasta = 0;
        double precio_promocion = 0d, nuevo_precio = 0d, precioVenta = 0d;

        try {
            String sql1 = "SELECT pm.desde,pm.hasta,pro.precioVenta,pm.preciopromocion\n"
                    + "FROM promocion pm\n"
                    + "INNER JOIN producto pro\n"
                    + "ON pro.idproducto = pm.idproducto\n"
                    + "WHERE pro.noserie = '" + noserie + "'";

            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql1);
            while (rs.next()) {
                desde = rs.getInt("desde");
                hasta = rs.getInt("hasta");
                nuevo_precio = rs.getDouble("preciopromocion");
                precioVenta = rs.getDouble("precioVenta");

            }
            if (desde > 0 && hasta > 0) {
                if ((cant >= desde) && (cant <= hasta)) {
                    dtTicket.setValueAt(nuevo_precio, fila, 3);
                    precio_promocion = nuevo_precio;
                } else {
                    dtTicket.setValueAt(precioVenta, fila, 3);
                    precio_promocion = precioVenta;
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return precio_promocion;
    }

    private String cambiarLogo() {
        String respuesta = "";
        ControllerConfiguration fc = new ControllerConfiguration();

        ImageIcon image = new ImageIcon(fc.ruta_guardar + fc.getNombreImagen());
        //lblLogotipo.setText("");
        if (image.getIconHeight() > 72 || image.getIconWidth() > 200) {
            ImageIcon imageScalada = new ImageIcon(image.getImage().getScaledInstance(200, 72, 72));
            lblLogotipo.setIcon(imageScalada); //
            respuesta = "\n Logotipo ha sido cambiado exitosamente .";
            txtRutaLogotipo.setText(fc.getNombreImagen()); //fc.ruta_guardar + fc.getNombreImagen()
        } else {
            lblLogotipo.setIcon(image);
            respuesta = "\n Logotipo ha sido cambiado exitosamente .";
            txtRutaLogotipo.setText(fc.getNombreImagen());
        }
        return respuesta;
    }

    @Override
    public void run() {
        while (tiempo != null) {
            try {
                Thread.sleep(3000);
                tiempo = null;
                //pa.setVisible(false);
                //pa = null;
                msgticket = null;
                //this.dispose();
                //new Principal().setVisible(true);

            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

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
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
            //javax.swing.UIManager.setLookAndFeel("com.jtattoo.plaf.aero.AeroLookAndFeel");

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmMain().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ClientesPanel;
    private javax.swing.JPanel ConfiguracionPanel;
    private javax.swing.JPanel ContenidoCortePanel;
    private javax.swing.JPanel CortePanel;
    private javax.swing.JPanel DineroCajaPanel;
    private javax.swing.JPanel EntradaEfectivoPanel;
    private javax.swing.JPanel InventarioPanel;
    private javax.swing.JPanel MainCortetPanel;
    private javax.swing.JPanel PagosContadoPanel;
    private javax.swing.JPanel ProductosPanel;
    private javax.swing.JPanel TituloVentasPanel;
    private javax.swing.JPanel VentasDepatamentoPanel;
    private javax.swing.JPanel VentasPanel;
    private javax.swing.JPanel VentasTotalesPanel;
    private javax.swing.JPanel agregarInvPanel;
    private javax.swing.JPanel ajusteInvPanel;
    private javax.swing.JPanel bajoInvPanel;
    private javax.swing.JPanel baseDatosPanel;
    public static principal.MaterialButton btnAbonarCliente;
    private javax.swing.JButton btnAgregarInv;
    private principal.MaterialButton btnAgregarInventario;
    private principal.MaterialButton btnAgregarProd;
    private javax.swing.JButton btnAjustesInv;
    private javax.swing.JButton btnBorrarArt;
    private javax.swing.JButton btnBuscar;
    private app.bolivia.swing.JCTextField btnBuscar2;
    private javax.swing.JButton btnCajeros;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnCerrarSesion;
    public static javax.swing.JButton btnClientesH;
    private principal.MaterialButtomRectangle btnCobrarVenta;
    public static javax.swing.JButton btnConfiguracionH;
    public static javax.swing.JButton btnCorte;
    private principal.MaterialButton btnDetAbono;
    private principal.MaterialButton btnEliminarProducto;
    private principal.MaterialButton btnEliminarPromocionPro;
    private javax.swing.JButton btnEliminarTicket;
    private principal.MaterialButton btnEliminarTicketCliente;
    private javax.swing.JButton btnEntradas;
    private principal.MaterialButton btnExaminarLogotipo;
    private principal.MaterialButton btnExportar;
    private principal.MaterialButton btnExportarClientes;
    private principal.MaterialButton btnExportarExcelBajosInventario;
    private principal.MaterialButton btnExportarVentasPedido;
    private principal.MaterialButton btnGuardarPromocion;
    private principal.MaterialButton btnHacerCorte;
    private principal.MaterialButton btnHacerCorte2;
    private principal.MaterialButton btnHacerCorteHoy;
    private javax.swing.JButton btnINSVarios;
    private principal.MaterialButton btnImportarProducto;
    private javax.swing.JButton btnImpresoraTicket;
    private principal.MaterialButton btnImprimir;
    private principal.MaterialButton btnImprimirCorte;
    private principal.MaterialButton btnImprimirEdoCuentaCompleto;
    private principal.MaterialButton btnImprimirLista;
    private principal.MaterialButton btnImprimirMovimiento;
    private principal.MaterialButton btnImprimirMovimiento1;
    private principal.MaterialButton btnImprimirProPedido;
    private principal.MaterialButton btnImprimirTicketTab;
    private javax.swing.JButton btnImpuestos;
    public static javax.swing.JButton btnInventarioH;
    public static principal.MaterialButton btnLiquidarAdeudo;
    private javax.swing.JButton btnLogotipoPrograma;
    private javax.swing.JButton btnMayoreo;
    private javax.swing.JButton btnModificarFolio;
    private principal.MaterialButton btnModificarProducto;
    private principal.MaterialButton btnMostrarOpciones;
    private javax.swing.JButton btnNuevoTicket;
    private principal.MaterialButton btnProCancelar;
    private javax.swing.JButton btnProdInventarioBajos;
    public static javax.swing.JButton btnProductosH;
    private principal.MaterialButton btnReimprimirUltimoTicket;
    private javax.swing.JButton btnReporteInv;
    private javax.swing.JButton btnReporteMovInv;
    private javax.swing.JButton btnSalidas;
    private javax.swing.JButton btnTicket;
    public static javax.swing.JButton btnUsuario;
    private principal.MaterialButton btnVentasDevoluciones;
    public static javax.swing.JButton btnVentasH;
    private principal.MaterialButton btnajusteinventario;
    private principal.MaterialButton btncancelarCategoria;
    private principal.MaterialButton btncancelarCliente;
    private principal.MaterialButton btncancelarem;
    private principal.MaterialButton btneliminarCategoria;
    private principal.MaterialButton btneliminarCliente;
    private principal.MaterialButton btneliminarem;
    private principal.MaterialButton btnguardarCategoria;
    private principal.MaterialButton btnguardarCliente;
    public static principal.MaterialButton btnguardarProducto;
    private principal.MaterialButton btnguardarem;
    private principal.MaterialButton btninputinventario;
    private principal.MaterialButton btnnuevoCategoria;
    private principal.MaterialButton btnnuevoCliente;
    private principal.MaterialButton btnnuevoProducto;
    private principal.MaterialButton btnnuevoem;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JPanel buttonsPanel1;
    private javax.swing.JPanel buttonsPanel10;
    private javax.swing.JPanel buttonsPanel11;
    private javax.swing.JPanel buttonsPanel12;
    private javax.swing.JPanel buttonsPanel13;
    private javax.swing.JPanel buttonsPanel2;
    private javax.swing.JPanel buttonsPanel3;
    private javax.swing.JPanel buttonsPanel4;
    private javax.swing.JPanel buttonsPanel8;
    private javax.swing.JPanel buttonsPanel9;
    private javax.swing.JPanel cajerosPanel;
    public static javax.swing.JComboBox<String> cboCategoriaIn;
    public static javax.swing.JComboBox<String> cbocategoriaPro;
    private javax.swing.JComboBox<String> cboprivilegioem;
    private javax.swing.JComboBox<String> cbxCajero;
    private javax.swing.JComboBox<String> cmbox;
    private org.jdesktop.swingx.JXComboBox comboImpresora;
    private com.toedter.calendar.JDateChooser dateCorte;
    private javax.swing.JPanel dispositivosFranjaPanel;
    public static javax.swing.JTable dtClieCredito;
    public static javax.swing.JTable dtClieListado;
    private javax.swing.JTable dtEmListado;
    public static javax.swing.JTable dtInvBajos;
    public static javax.swing.JTable dtInvHistorialMov;
    public static javax.swing.JTable dtInvReporte;
    public static javax.swing.JTable dtProCategoria;
    public static javax.swing.JTable dtProListado;
    private javax.swing.JTable dtProPromocion;
    private javax.swing.JTable dtProVentasPedido;
    public static javax.swing.JTable dtTicket;
    public static javax.swing.JTable dtVentasCategoria;
    public static javax.swing.JDesktopPane escritorio;
    private com.toedter.calendar.JDateChooser fechachooser_desde;
    private com.toedter.calendar.JDateChooser fechachooser_hasta;
    private javax.swing.JPanel folioTicketPanel;
    private javax.swing.JPanel generalFranjaPanel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JPanel impresoraTicketPanel;
    private javax.swing.JPanel impuestosPanel;
    private javax.swing.JPanel inicioConfiguracionesPanel;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox<String> jComboBox5;
    private com.toedter.calendar.JDateChooser jDateChooser4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel107;
    private javax.swing.JLabel jLabel108;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel110;
    private javax.swing.JLabel jLabel112;
    private javax.swing.JLabel jLabel113;
    private javax.swing.JLabel jLabel114;
    private javax.swing.JLabel jLabel115;
    private javax.swing.JLabel jLabel116;
    private javax.swing.JLabel jLabel117;
    public static javax.swing.JLabel jLabel118;
    public static javax.swing.JLabel jLabel119;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel120;
    private javax.swing.JLabel jLabel121;
    private javax.swing.JLabel jLabel122;
    private javax.swing.JLabel jLabel123;
    private javax.swing.JLabel jLabel124;
    private javax.swing.JLabel jLabel125;
    private javax.swing.JLabel jLabel126;
    private javax.swing.JLabel jLabel127;
    private javax.swing.JLabel jLabel128;
    private javax.swing.JLabel jLabel129;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel130;
    private javax.swing.JLabel jLabel131;
    private javax.swing.JLabel jLabel132;
    private javax.swing.JLabel jLabel133;
    private javax.swing.JLabel jLabel134;
    private javax.swing.JLabel jLabel136;
    private javax.swing.JLabel jLabel137;
    private javax.swing.JLabel jLabel138;
    private javax.swing.JLabel jLabel139;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel140;
    private javax.swing.JLabel jLabel141;
    private javax.swing.JLabel jLabel142;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    public static javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPaneBajosInventario;
    private javax.swing.JScrollPane jScrollPaneCajero;
    private javax.swing.JScrollPane jScrollPaneCategoria;
    private javax.swing.JScrollPane jScrollPaneEstadoCuenta;
    private javax.swing.JScrollPane jScrollPaneHistorialMov;
    private javax.swing.JScrollPane jScrollPaneListadoCliente;
    private javax.swing.JScrollPane jScrollPaneListadoProducto;
    private javax.swing.JScrollPane jScrollPanePromocion;
    private javax.swing.JScrollPane jScrollPaneReporteInventario;
    private javax.swing.JScrollPane jScrollPaneVentas;
    private javax.swing.JScrollPane jScrollPaneVentasCategoria;
    private javax.swing.JScrollPane jScrollPaneVentasVendido;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane4;
    public static javax.swing.JTree jTree1;
    public static javax.swing.JLabel lblCambioInfo;
    private javax.swing.JLabel lblDescripcionPromocion;
    public static javax.swing.JLabel lblEntradas;
    public static javax.swing.JLabel lblEntradas2;
    private javax.swing.JLabel lblExistenciaAjuste;
    public static javax.swing.JLabel lblFechaCorte;
    public static javax.swing.JLabel lblGananciasDia;
    private javax.swing.JLabel lblIdProductoAjuste;
    public static javax.swing.JLabel lblImpuestos;
    private javax.swing.JLabel lblLimiteCreditoCliente;
    private javax.swing.JLabel lblLogotipo;
    private javax.swing.JLabel lblNombreCliente;
    private javax.swing.JLabel lblPagoClientes;
    public static javax.swing.JLabel lblPagoConInfo;
    public static javax.swing.JLabel lblPagosClientes;
    private javax.swing.JLabel lblPrecioCosto;
    private javax.swing.JLabel lblPrecioNormal;
    public static javax.swing.JLabel lblSaldoActualCliente;
    private javax.swing.JLabel lblSalidaEfectivo;
    public static javax.swing.JLabel lblSalidas;
    public static javax.swing.JLabel lblSalidas2;
    private javax.swing.JLabel lblSeleccionaUnCliente;
    public static javax.swing.JLabel lblSubTotalImporte;
    public static javax.swing.JLabel lblTotalCantProd;
    public static javax.swing.JLabel lblTotalDinero;
    public static javax.swing.JLabel lblTotalImporte;
    public static javax.swing.JLabel lblTotalImporteCredito;
    public static javax.swing.JLabel lblTotalInfo;
    public static javax.swing.JLabel lblTotalRegistros;
    public static javax.swing.JLabel lblTotalRegistros1;
    public static javax.swing.JLabel lblTotalRegistros2;
    public static javax.swing.JLabel lblVentasEfectivo;
    public static javax.swing.JLabel lblVentasEfectivo2;
    public static javax.swing.JLabel lblVentasTotales;
    private javax.swing.JLabel lblcantinventario;
    private javax.swing.JLabel lbldescripinventario;
    private javax.swing.JLabel lbldesinventario2;
    private javax.swing.JLabel lblusuario;
    private javax.swing.JPanel logotipoPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel opcionesHabilitadasPanel;
    private javax.swing.JPanel periodoFecha;
    private javax.swing.JPanel periodoPanel;
    private javax.swing.JPanel personalizaciónFranjaPanel;
    private javax.swing.JRadioButton rbImpuesto;
    private javax.swing.JPanel reporteInvPanel;
    private javax.swing.JPanel reporteMovimientosPanel;
    private javax.swing.JPanel ticketPanel;
    private javax.swing.JLabel titleSection;
    private javax.swing.JTextField txtBuscarCategoria;
    private javax.swing.JTextField txtBuscarProductos;
    private javax.swing.JTextField txtCodigoProAgregarInv;
    private javax.swing.JTextField txtCodigoProAjuste;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtEmpresaAgradecimiento;
    private javax.swing.JTextField txtEmpresaCalle;
    private javax.swing.JTextField txtEmpresaLocal;
    private javax.swing.JTextField txtEmpresaNombre;
    private javax.swing.JTextField txtEmpresaSucursal;
    private javax.swing.JTextField txtEmpresaTelefono;
    public static javax.swing.JTextField txtEnterProducto;
    private javax.swing.JTextField txtFolioConfiguracion;
    private javax.swing.JTextField txtImpuesto;
    public static javax.swing.JTextField txtNoSeriePro;
    private javax.swing.JTextField txtRutaLogotipo;
    private javax.swing.JTextField txtbuscarClientes;
    private javax.swing.JTextField txtbuscarem;
    public static javax.swing.JTextField txtcantidadActualPro;
    public static javax.swing.JTextField txtcantidadMinimoPro;
    private javax.swing.JTextField txtcreditoCliente;
    public static javax.swing.JTextField txtdescripcionPro;
    private javax.swing.JTextField txtdesdePromocion;
    private javax.swing.JTextField txtdireccionCliente;
    private javax.swing.JTextField txthastaPromocion;
    private javax.swing.JTextField txtidcategoria;
    private javax.swing.JTextField txtidcliente;
    private javax.swing.JTextField txtidempleado;
    public static javax.swing.JTextField txtidproductos;
    private javax.swing.JTextField txtinputinventario;
    private javax.swing.JTextField txtinputinventario2;
    private javax.swing.JTextField txtnombreCategoria;
    private javax.swing.JTextField txtnombreCliente;
    private javax.swing.JTextField txtnombrePromocion;
    private javax.swing.JTextField txtnombreem;
    private javax.swing.JTextField txtnoseriePromocion;
    private javax.swing.JTextField txtpasswordem;
    public static javax.swing.JTextField txtprecioCostoPro;
    public static javax.swing.JTextField txtprecioMayoreoPro;
    private javax.swing.JTextField txtprecioPromocion;
    public static javax.swing.JTextField txtprecioVentaPro;
    private javax.swing.JTextField txttelefonoCliente;
    private javax.swing.JTextField txtusuarioem;
    private javax.swing.JLayeredPane vistasConfiguraciones;
    private javax.swing.JLayeredPane vistasInventario;
    // End of variables declaration//GEN-END:variables
    private static Conexion mysql = new Conexion();
    private static Connection cn = mysql.getConnection();
}
