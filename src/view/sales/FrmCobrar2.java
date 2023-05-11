package view.sales;

import model.ModelMovClient;
import model.ModelTicket;
import model.ModelProduct;
import controller.ControllerMovClient;
import extras.MyFormater;
import extras.MyColor;
import extras.Conexion;
import controller.ControllerTicket;
import controller.ControllerClient;
import static view.FrmEfectivo.txtPagoCon;
import static view.FrmCredito.txtbuscarClientes;
import static view.FrmMain.mostrarBajosInventario;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import view.FrmCredito;
import view.FrmEfectivo;
import view.FrmMain;


public class FrmCobrar2 extends javax.swing.JDialog {

    private static Conexion mysql = new Conexion();
    private static Connection cn = mysql.getConnection();

    DefaultTableModel modelo;
    String cambio;
    double importe = 0d;
    public static double importeM = 0d;
    public static double subTotal = 0d;
    public int IdEmpleado = 0;
    int IdCliente = 0;
    int IdProducto = 0;
    int cantidadM = 0;
    double precioVenta = 0d;
    List<String> idproductos; //id
    List<String> noserie; //noserie
    List<String> desproductos; //des
    List<Double> prevproductos; //prev
    List<String> cantproductos; //cant
    List<Double> impproductos; //imp
    List<String> existproductos; //exit

    //1 - Al contado;
    //0 - A crédito
    int tipoPago = 1;

    FrmEfectivo efectivo = null;
    FrmCredito credito = null;

    public FrmCobrar2(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);
        tipoPago = 1;

        this.idproductos = new ArrayList<>();
        this.noserie = new ArrayList<>();
        this.desproductos = new ArrayList<>();
        this.prevproductos = new ArrayList<>();
        this.cantproductos = new ArrayList<>();
        this.impproductos = new ArrayList<>();
        this.existproductos = new ArrayList<>();

        btnEfectivo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/ECM.png")));
        btnEfectivo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnEfectivo.setVerticalAlignment(javax.swing.SwingConstants.CENTER);

        btnCredito.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/CSM.png")));
        btnCredito.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnCredito.setVerticalAlignment(javax.swing.SwingConstants.CENTER);

        efectivo = new FrmEfectivo();
        credito = new FrmCredito();
        frmEfectivo();

    }

    public void recibirModelo(DefaultTableModel modelo) {
        this.modelo = modelo;
        importeM = 0d;
        subTotal = 0d;
        cantidadM = 0;
        int fila = modelo.getRowCount();
        String cant = "";
        String imp = "";

        for (int i = 0; i < fila; i++) {
            idproductos.add(modelo.getValueAt(i, 0).toString());
            noserie.add(modelo.getValueAt(i, 1).toString());
            desproductos.add(modelo.getValueAt(i, 2).toString());
            prevproductos.add(Double.parseDouble(modelo.getValueAt(i, 3).toString())); //TIPO DOUBLE
            cantproductos.add(modelo.getValueAt(i, 4).toString());
            impproductos.add(Double.parseDouble(modelo.getValueAt(i, 5).toString())); //TIPO DOUBLE
            existproductos.add(modelo.getValueAt(i, 6).toString());

            cant = modelo.getValueAt(i, 4).toString(); //cantidad
            imp = modelo.getValueAt(i, 5).toString(); //importe
            cantidadM += Integer.parseInt(cant);
            //importeM += Double.parseDouble(imp);
            subTotal += Double.parseDouble(imp);
            
        }
        importeM = subTotal + (subTotal * FrmMain.IMPUESTO/100);
        //jLabel3.setText("IMPUESTO: "+FrmMain.IMPUESTO);
        lblTotalCobrar.setText(new MyFormater().formato(importeM));
        lblTotalArticulos.setText("" + cantidadM);
        if (btnEfectivo.isSelected()) {
            txtPagoCon.setText(new MyFormater().formato(importeM));
        }
        //importeM = 0d;
    }

    public static void ventana(JFrame obj) {
        int width = jDesktopPane1.getWidth();
        int Height = jDesktopPane1.getHeight();
        obj.setSize(width, Height);
        jDesktopPane1.add(obj);
        obj.show();
    }

    private void changeEnabled(boolean valor) {
        btnEfectivo.setSelected(valor);
        btnCredito.setSelected(!valor);

        if (valor) {
            btnEfectivo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/ECM.png")));
            btnEfectivo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            btnEfectivo.setVerticalAlignment(javax.swing.SwingConstants.CENTER);

            btnCredito.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/CSM.png")));
            btnCredito.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            btnCredito.setVerticalAlignment(javax.swing.SwingConstants.CENTER);

            tipoPago = 1; //1 - Efectivo
        } else {
            //panelsTipoPago.moveToBack(panelEfectivo);
            //panelsTipoPago.moveToFront(panelCredito);

            btnEfectivo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/ESM.png")));
            btnEfectivo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            btnEfectivo.setVerticalAlignment(javax.swing.SwingConstants.CENTER);

            btnCredito.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/CCM.png")));
            btnCredito.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            btnCredito.setVerticalAlignment(javax.swing.SwingConstants.CENTER);

            tipoPago = 0; // 0 - A crédito
        }
    }
    
    private Integer getExistencia(int idproducto){
        int existencia = 0;
        try {
            String sql = "SELECT existencia FROM producto WHERE idproducto= '" + idproducto + "'";

            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                existencia = rs.getInt("existencia");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return existencia;
    }

    private void realizarCobro() {
        ModelTicket dts = new ModelTicket();
        ModelProduct dtsp = new ModelProduct();
        ControllerTicket func = new ControllerTicket();
        ModelMovClient dtMovC = new ModelMovClient();
        ControllerMovClient funcMovC = new ControllerMovClient();
        

        Double pagocon = FrmEfectivo.pagoCon();

        if (tipoPago == 1) { // 1 - Al contado
            if (FrmEfectivo.cobrar) {
                int fila = modelo.getRowCount();
                for (int i = 0; i < fila; i++) {

                    //DATOS PARA REGISTRAR UN TICKET
                    dts.setIdEmpleado(IdEmpleado);
                    dts.setIdCliente(1); //1 - Cliente por defecto para al contado
                    dts.setIva(FrmMain.IMPUESTO); //Valor de impuesto por defecto
                    dts.setPagoCon(pagocon);
                    //DATOS PARA REGISTRAR UN PRODUCTO
                    dtsp.setIdproducto(Integer.parseInt(idproductos.get(i)));
                    dtsp.setPrecioVenta(prevproductos.get(i));

                    //REGISTRAR TICKET DE VENTA
                    func.insertar(dts, dtsp, Integer.parseInt(cantproductos.get(i)));
                    
                    
                    
                    //REGISTRAR MOVIMIENTO DE INVENTARIO
                    //1 ENTRADAS
                    //2 SALIDAS
                    //3 AJUSTES
                    //4 DEVOLUCIONES
                    FrmMain.registrarMovimiento(
                                                  Integer.parseInt(idproductos.get(i)), //IDPRODUCTO
                                                  IdEmpleado, //CAJERO
                                                  getExistencia(Integer.parseInt(idproductos.get(i))), //HABIA 
                                                  2, //TIPO_MOVIMIENTO = SALIDA
                                                  Integer.parseInt(cantproductos.get(i)) //CANTIDAD SALIDA
                                                  );
                    //RESTAR A LA EXISTENCIA CANTIDAD VENDIDA
                    disminuirExistencia(Integer.parseInt(cantproductos.get(i)), Integer.parseInt(idproductos.get(i)));
                    
                    //ACTUALIZAR REPORTE DE BAJOS EN INVENTARIO
                    mostrarBajosInventario();
                    
                }
                controller.ControllerTicket.nuevaVenta = true;
                this.dispose();
                //Presentacion.FrmMain.mostrar("");
                view.FrmMain.vaciarListaProductos();
                view.FrmMain.lblTotalInfo.setText(MyFormater.formato(importeM));
                view.FrmMain.lblPagoConInfo.setText(MyFormater.formato(pagocon));
                view.FrmMain.lblCambioInfo.setText(FrmEfectivo.lblCambio.getText());

            } else {
                JOptionPane.showMessageDialog(null, "El pago no debe ser menor que el total a cobrar.");
            }
        } else if (tipoPago == 0) { //0 - A crédito
            if (FrmCredito.dtClientes.getSelectedRowCount() < 1) {
                JOptionPane.showMessageDialog(null, "Selecciona un cliente.");
            } else {
                int fila = FrmCredito.dtClientes.getSelectedRow();
                IdCliente = Integer.parseInt(FrmCredito.dtClientes.getValueAt(fila, 0).toString());
                Double SALDO_ACTUAL_USUARIO = new ControllerClient().saldoActualCliente(String.valueOf(IdCliente));

                if (SALDO_ACTUAL_USUARIO < importeM) {
                    JOptionPane.showMessageDialog(null, "El cliente ha llegado a su límite de crédito.\n"
                            + "Saldo Disponible $" + SALDO_ACTUAL_USUARIO);
                } else {
                    int filaCount = modelo.getRowCount();
                    for (int i = 0; i < filaCount; i++) {
                        //int fila = dtClientes.getSelectedRow();
                        //IdCliente = Integer.parseInt(dtClientes.getValueAt(fila, 0).toString());
                        dts.setIdEmpleado(IdEmpleado);
                        dts.setIdCliente(IdCliente);
                        dts.setIva(16);
                        dts.setPagoCon(0d);
                        dts.setEstado(0);

                        //JOptionPane.showMessageDialog(null,""+idproductos.get(i));
                        dtsp.setIdproducto(Integer.parseInt(idproductos.get(i)));
                        dtsp.setPrecioVenta(prevproductos.get(i));

                        func.insertar(dts, dtsp, Integer.parseInt(cantproductos.get(i)));
                        
                        //REGISTRAR MOVIMIETO CLIENTE
                        dtMovC.setIdcliente(IdCliente);
                        dtMovC.setTipo(0); //El valor es 0 cuando es COMPRA DE PRODUCTO
                        dtMovC.setMonto(importeM);
                        funcMovC.insertar(dtMovC);
                    
                        //DISMINUIR EXISTENCIA
                        disminuirExistencia(Integer.parseInt(cantproductos.get(i)), Integer.parseInt(idproductos.get(i)));
                        mostrarBajosInventario();
                    }
                    //Presentacion.FrmMain.gastado();
                    view.FrmMain.mostrar("");
                    controller.ControllerTicket.nuevaVenta = true;
                    this.dispose();
                    view.FrmMain.vaciarListaProductos();
                    view.FrmMain.lblTotalInfo.setText(MyFormater.formato(importeM));
                    view.FrmMain.lblPagoConInfo.setText(MyFormater.formato(pagocon));
                    view.FrmMain.lblCambioInfo.setText(FrmEfectivo.lblCambio.getText());
                }
            }
        }

    }

    public boolean disminuirExistencia(int cantidadVendida, int idproducto) {
        String sSQL = "UPDATE producto SET existencia = existencia - " + cantidadVendida
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

    public void frmEfectivo() {
        changeEnabled(true);
        int width = jDesktopPane1.getWidth();
        int Height = jDesktopPane1.getHeight();
        efectivo.setSize(width, Height);
        jDesktopPane1.add(efectivo, 0);
        jDesktopPane1.moveToFront(efectivo);
        efectivo.setVisible(true);
        credito.dispose();
        txtPagoCon.requestFocus();
    }

    public void frmCredito() {
        changeEnabled(false);
        int width = jDesktopPane1.getWidth();
        int Height = jDesktopPane1.getHeight();
        credito.mostrarClientes("");
        credito.setSize(width, Height);
        jDesktopPane1.add(credito, 1);
        jDesktopPane1.moveToFront(credito);
        credito.setVisible(true);
        efectivo.dispose();
        txtbuscarClientes.requestFocus();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblTotalCobrar = new javax.swing.JLabel();
        btnEfectivo = new javax.swing.JButton();
        btnCredito = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        lblTotalArticulos = new javax.swing.JLabel();
        btneliminarCliente = new principal.MaterialButton();
        btnCobrarRegVenta = new principal.MaterialButton();
        btnCobrarRegVenta1 = new principal.MaterialButton();
        jDesktopPane1 = new javax.swing.JDesktopPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel1.setText("COBRAR");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Total a cobrar");

        jLabel4.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Efectivo");

        jLabel7.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Crédito");

        lblTotalCobrar.setFont(new java.awt.Font("Tahoma", 0, 48)); // NOI18N
        lblTotalCobrar.setForeground(new MyColor().COLOR_JLABEL_PRIMARY);
        lblTotalCobrar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalCobrar.setText("$20.00");

        btnEfectivo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/ESM.png"))); // NOI18N
        btnEfectivo.setToolTipText("El cliente está pagando con efectivo");
        btnEfectivo.setBorderPainted(false);
        btnEfectivo.setContentAreaFilled(false);
        btnEfectivo.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnEfectivo.setFocusPainted(false);
        btnEfectivo.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/ECM.png"))); // NOI18N
        btnEfectivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEfectivoActionPerformed(evt);
            }
        });

        btnCredito.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/CSM.png"))); // NOI18N
        btnCredito.setToolTipText("Asignar esta compra como crédito a un cliente");
        btnCredito.setBorderPainted(false);
        btnCredito.setContentAreaFilled(false);
        btnCredito.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnCredito.setFocusPainted(false);
        btnCredito.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/CCM.png"))); // NOI18N
        btnCredito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreditoActionPerformed(evt);
            }
        });

        jPanel3.setBackground(MyColor.COLOR_BACKGROUND_BLACK);

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Total de artículos");

        lblTotalArticulos.setFont(new java.awt.Font("Tahoma", 0, 48)); // NOI18N
        lblTotalArticulos.setForeground(new java.awt.Color(255, 255, 255));
        lblTotalArticulos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalArticulos.setText("1");

        btneliminarCliente.setBackground(new MyColor().COLOR_BUTTON);
        btneliminarCliente.setForeground(new java.awt.Color(255, 255, 255));
        btneliminarCliente.setText("ESC - Cancelar");
        btneliminarCliente.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btneliminarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btneliminarClienteActionPerformed(evt);
            }
        });

        btnCobrarRegVenta.setBackground(new MyColor().COLOR_BUTTON);
        btnCobrarRegVenta.setForeground(new java.awt.Color(255, 255, 255));
        btnCobrarRegVenta.setText("F2 - Cobrar sólo registrando la venta");
        btnCobrarRegVenta.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnCobrarRegVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCobrarRegVentaActionPerformed(evt);
            }
        });

        btnCobrarRegVenta1.setBackground(new MyColor().COLOR_BUTTON);
        btnCobrarRegVenta1.setForeground(new java.awt.Color(255, 255, 255));
        btnCobrarRegVenta1.setText("F1 - Cobrar e imprimir Ticket");
        btnCobrarRegVenta1.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnCobrarRegVenta1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCobrarRegVenta1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotalArticulos, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btneliminarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(btnCobrarRegVenta, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                        .addComponent(btnCobrarRegVenta1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCobrarRegVenta1, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCobrarRegVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(btneliminarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTotalArticulos)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jDesktopPane1.setBackground(new java.awt.Color(204, 204, 204));

        javax.swing.GroupLayout jDesktopPane1Layout = new javax.swing.GroupLayout(jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setHorizontalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 395, Short.MAX_VALUE)
        );
        jDesktopPane1Layout.setVerticalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 175, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotalCobrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnEfectivo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnCredito, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jDesktopPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 2, Short.MAX_VALUE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel1)
                .addGap(26, 26, 26)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTotalCobrar)
                .addGap(31, 31, 31)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnEfectivo, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCredito, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDesktopPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btneliminarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btneliminarClienteActionPerformed
        this.dispose();
        view.FrmMain.displayMessage("Cobro de venta cancelado...");
    }//GEN-LAST:event_btneliminarClienteActionPerformed

    private void btnCobrarRegVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCobrarRegVentaActionPerformed
        realizarCobro();
    }//GEN-LAST:event_btnCobrarRegVentaActionPerformed

    private void btnCobrarRegVenta1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCobrarRegVenta1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCobrarRegVenta1ActionPerformed


    private void btnEfectivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEfectivoActionPerformed
        frmEfectivo();
    }//GEN-LAST:event_btnEfectivoActionPerformed

    private void btnCreditoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreditoActionPerformed
        frmCredito();
    }//GEN-LAST:event_btnCreditoActionPerformed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.dispose();
        }
    }//GEN-LAST:event_formKeyPressed

    
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
                FrmCobrar2 dialog = new FrmCobrar2(new javax.swing.JFrame(), true);
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
    private principal.MaterialButton btnCobrarRegVenta;
    private principal.MaterialButton btnCobrarRegVenta1;
    private javax.swing.JButton btnCredito;
    private javax.swing.JButton btnEfectivo;
    private principal.MaterialButton btneliminarCliente;
    public static javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel lblTotalArticulos;
    private javax.swing.JLabel lblTotalCobrar;
    // End of variables declaration//GEN-END:variables
}
