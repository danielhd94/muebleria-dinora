/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import extras.MyColor;
import extras.MyScrollbarUI;
import extras.EstiloTablaHeader;
import extras.EstiloTablaRenderer;
import controller.ControllerClient;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class FrmCredito extends javax.swing.JInternalFrame {

    
    public FrmCredito() {
        initComponents();
        ((javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI()).setNorthPane(null);

        jScrollPane.getViewport().setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane.getVerticalScrollBar().setUI(new MyScrollbarUI());
        jScrollPane.getHorizontalScrollBar().setUI(new MyScrollbarUI());

        dtClientes.getTableHeader().setDefaultRenderer(new EstiloTablaHeader());
        dtClientes.setDefaultRenderer(Object.class, new EstiloTablaRenderer());
        
       
    }
    
    public void mostrarClientes(String buscar) {
        try {
            DefaultTableModel modelo;
            ControllerClient func = new ControllerClient();
            modelo = func.mostrar(buscar);

            dtClientes.setModel(modelo);
            ocultar_columnas();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    void ocultar_columnas() {
        for (int i = 0; i < dtClientes.getColumnCount(); i++) {
            if (i != 1) {
                dtClientes.getColumnModel().getColumn(i).setMaxWidth(0);
                dtClientes.getColumnModel().getColumn(i).setMinWidth(0);
                dtClientes.getColumnModel().getColumn(i).setPreferredWidth(0);
            }
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

        panelCredito1 = new javax.swing.JPanel();
        txtbuscarClientes = new javax.swing.JTextField();
        jScrollPane = new javax.swing.JScrollPane();
        dtClientes = new javax.swing.JTable();

        setBorder(null);

        panelCredito1.setBackground(new java.awt.Color(255, 255, 255));
        panelCredito1.setPreferredSize(new java.awt.Dimension(482, 211));

        txtbuscarClientes.setForeground(new MyColor().COLOR_FOCUS_TEXTFIELD);
        txtbuscarClientes.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtbuscarClientesKeyReleased(evt);
            }
        });

        dtClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Title 1"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        dtClientes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        dtClientes.setShowHorizontalLines(false);
        dtClientes.setShowVerticalLines(false);
        jScrollPane.setViewportView(dtClientes);

        javax.swing.GroupLayout panelCredito1Layout = new javax.swing.GroupLayout(panelCredito1);
        panelCredito1.setLayout(panelCredito1Layout);
        panelCredito1Layout.setHorizontalGroup(
            panelCredito1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCredito1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCredito1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                    .addComponent(txtbuscarClientes))
                .addContainerGap())
        );
        panelCredito1Layout.setVerticalGroup(
            panelCredito1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCredito1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtbuscarClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelCredito1, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelCredito1, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtbuscarClientesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtbuscarClientesKeyReleased
        mostrarClientes(txtbuscarClientes.getText());
    }//GEN-LAST:event_txtbuscarClientesKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JTable dtClientes;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JPanel panelCredito1;
    public static javax.swing.JTextField txtbuscarClientes;
    // End of variables declaration//GEN-END:variables
}
