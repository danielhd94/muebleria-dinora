package view;

import model.ModelCorreo;
import static controller.ControllerCorreo.enviarConGMail;
import extras.MyColor;
import extras.Conexion;
import static com.microsoft.schemas.office.x2006.encryption.STCipherAlgorithm.AES;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.mail.MessagingException;
import javax.swing.JOptionPane;

public class frmEnviarCorreo extends javax.swing.JDialog {

    /**
     * Creates new form frmEnviarCorreo
     */
    ModelCorreo c = new ModelCorreo();
    private Conexion mysql = new Conexion();
    private Connection cn = mysql.getConnection();
    private String sSQL = "";

    public frmEnviarCorreo(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);
    }

    public void enviarCorreo() throws MessagingException {
        /*c.setPassword("ynrmlpszsmaaiudx"); //ynrm lpsz smaa iudx
        c.setUsuarioCorreo("muebleriadinorapuntodeventa@gmail.com");
        c.setAsunto(txtAsunto.getText());
        c.setMensaje(txtMensaje.getText());
        c.setDestino(txtDestino.getText());
        
        ControllerCorreo co = new ControllerCorreo();
        if(co.enviarCorreo(c)){
            JOptionPane.showMessageDialog(null,"Se ha enviado al correo correctamente.");
        }else{
            JOptionPane.showMessageDialog(null,"Se ha generado un error inesperado.");
        }*/
        String destinatario = "";//txtDestino.getText(); //A quien le quieres escribir.
        String asunto = "";//txtAsunto.getText();
        String cuerpo = "";

        enviarConGMail(destinatario, asunto, cuerpo);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtEmail = new org.jdesktop.swingx.JXTextField();
        jLabel1 = new javax.swing.JLabel();
        btnRecuperarPass = new principal.MaterialButton();
        btnCancelar = new principal.MaterialButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("RECUPERACIÓN CONTRASEÑA");
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setForeground(new java.awt.Color(135, 65, 155));
        jLabel2.setText("INGRESE SU CORREO:");

        txtEmail.setForeground(new java.awt.Color(135, 65, 155));
        txtEmail.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmailActionPerformed(evt);
            }
        });
        txtEmail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtEmailKeyPressed(evt);
            }
        });

        jLabel1.setForeground(new java.awt.Color(135, 65, 155));
        jLabel1.setText("Se enviará un mensaje con su contraseña nueva al correo registrado.");

        btnRecuperarPass.setBackground(new MyColor().COLOR_BUTTON);
        btnRecuperarPass.setForeground(new java.awt.Color(255, 255, 255));
        btnRecuperarPass.setText("RECUPERAR");
        btnRecuperarPass.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnRecuperarPass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRecuperarPassActionPerformed(evt);
            }
        });

        btnCancelar.setBackground(new MyColor().COLOR_BUTTON);
        btnCancelar.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelar.setText("CANCELAR");
        btnCancelar.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(btnRecuperarPass, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel2)
                        .addComponent(txtEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRecuperarPass, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRecuperarPassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRecuperarPassActionPerformed
        if (verificarCorreo(txtEmail.getText())) {
            generarNuevoPassword(txtEmail.getText());
        } else {
            JOptionPane.showMessageDialog(null, "No se registró alguna cuenta con ese email");
        }
    }//GEN-LAST:event_btnRecuperarPassActionPerformed

    private void generarNuevoPassword(String email) {
        int nuevoPassword = (int) (Math.random() * 100000) + 999999;
        sSQL = "UPDATE empleado SET password = ? WHERE email = ? ";

        try {
            PreparedStatement pst = cn.prepareStatement(sSQL);
            pst.setInt(1, nuevoPassword);
            pst.setString(2, email);
            int n = pst.executeUpdate();
            if (n != 0) {
                System.out.println("La nueva contraseña para " + email + "es:\n " + nuevoPassword);
                String destinatario = email;
                String asunto = "RECUPERACIÓN DE LA CUENTA - MUEBLERÍA DINORA PUNTO DE VENTA";
                String cuerpo = "LOS NUEVOS DATOS PARA " + email + "\nes: USUARIO: "+ nameUser(email)+"\nNUEVA CONTRASEÑA: "+ nuevoPassword;
                
                enviarConGMail(destinatario, asunto, cuerpo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String nameUser(String email) {
        String respuesta = "";
        sSQL = "SELECT usuario FROM empleado WHERE email = '" + email + "'";

        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            while (rs.next()) {
                respuesta = rs.getString("usuario");
            }
        } catch (Exception e) {
            
        }
        return respuesta;
    }
    
    private boolean verificarCorreo(String email) {
        boolean respuesta = false;
        sSQL = "SELECT email FROM empleado WHERE email = '" + email + "' LIMIT 1";

        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            while (rs.next()) {
                if (rs.getString("email").equals("")) {

                } else {
                    respuesta = true;
                }
            }
        } catch (Exception e) {
            respuesta = false;
        }
        return respuesta;
    }

    private void txtEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailActionPerformed

    }//GEN-LAST:event_txtEmailActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void txtEmailKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEmailKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (verificarCorreo(txtEmail.getText())) {
                generarNuevoPassword(txtEmail.getText());
            } else {
                JOptionPane.showMessageDialog(null, "No se registró alguna cuenta con ese email");
            }
        }
    }//GEN-LAST:event_txtEmailKeyPressed

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
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                frmEnviarCorreo dialog = new frmEnviarCorreo(new javax.swing.JFrame(), true);
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
    private principal.MaterialButton btnCancelar;
    private principal.MaterialButton btnRecuperarPass;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private org.jdesktop.swingx.JXTextField txtEmail;
    // End of variables declaration//GEN-END:variables
}
