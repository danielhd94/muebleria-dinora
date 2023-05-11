package controller;

import extras.LongTask;
import view.FrmLogin;
import model.ModelEmploye;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import view.FrmMain;

public class ControllerLogin implements ActionListener {

    private ModelEmploye employeModel;
    private FrmLogin frmLogin;
    private FrmMain frmMain;
    
    public final static int ONE_SECOND = 1000;
    private Timer timer;
    private LongTask task;
    
    private boolean listo = false;
    private int loop = 0;

    public ControllerLogin() {
        frmLogin = new FrmLogin();
        frmMain = new FrmMain();
        frmLogin.btnIngresarLogin.addActionListener(this);
        frmLogin.btnSalirLogin.addActionListener(this);
        task = new LongTask(200);
        //create a timer and a task
        timer = new Timer(ONE_SECOND, new TimerListener());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.print("Evento");
        if (e.getSource() == frmLogin.btnIngresarLogin) {
            login();
        }

        if (e.getSource() == frmLogin.btnSalirLogin) {
            System.exit(0);
        }
    }

    private void clearFieldLogin() {
        frmLogin.txtUsuario.setText(null);
        frmLogin.txtPassword.setText(null);
    }
    
    private void login() {
        if (frmLogin.txtUsuario.getText().equals("") && frmLogin.txtPassword.getText().equals("")) {
            //JOptionPane.showMessageDialog(null, "Ingrese contraseña", "Acceso al sistema", JOptionPane.ERROR_MESSAGE);
            frmLogin.lblCargando.setVisible(true);
            frmLogin.lblCargando.setText("No tiene acceso al sistema.");
            frmLogin.txtUsuario.requestFocus();
        } else {
            if (frmLogin.txtUsuario.getText().equals("")) {
                //JOptionPane.showMessageDialog(null, "Ingrese contraseña", "Acceso al sistema", JOptionPane.ERROR_MESSAGE);
                frmLogin.lblCargando.setVisible(true);
                frmLogin.lblCargando.setText("Ingrese usuario o email");
                frmLogin.txtUsuario.requestFocus();
            } else {
                if (frmLogin.txtPassword.getText().equals("")) {
                    //JOptionPane.showMessageDialog(null, "Ingrese contraseña", "Acceso al sistema", JOptionPane.ERROR_MESSAGE);
                    frmLogin.lblCargando.setVisible(true);
                    frmLogin.lblCargando.setText("Ingrese contraseña");
                    frmLogin.txtPassword.requestFocus();
                } else {
                    ingresar(frmLogin.txtUsuario.getText(), frmLogin.txtPassword.getText());
                }
            }

        }

    }
    private void instanciar() {
        frmMain.IdempleadoU = employeModel.getIdempleado();
        frmMain.btnUsuario.setText(employeModel.getNombre());
        frmMain.setVisible(true);
        //MENSAJE DE BIENVENIDA
        view.FrmMain.beep = false;
        view.FrmMain.notificar("Bienvenido " + employeModel.getNombre());

        if (employeModel.getPrivilegio() != 1) {
            frmMain.btnClientesH.setEnabled(false);
            frmMain.btnProductosH.setEnabled(false);
            frmMain.btnInventarioH.setEnabled(false);
            frmMain.btnConfiguracionH.setEnabled(false);
            frmMain.btnCorte.setEnabled(false);
        }
        timer.stop();
        frmLogin.setVisible(false);
    }

    private void ingresar(String user, String password) {
        try {
            ControllerEmploye func = new ControllerEmploye();
            boolean band = func.validar(user, password);

            if (band) {
                employeModel = func.getWorker(user, password);
                if (employeModel.getInactivo() == 1) {
                    JOptionPane.showMessageDialog(null, "El trabajador ya no pertenece a la empresa", "Información", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                frmLogin.lblCargando.setVisible(true);
                frmLogin.progressBar.setVisible(true);
                task.go();
                timer.start();
            } else {
                frmLogin.lblCargando.setVisible(true);
                boolean r = new ControllerEmploye().perteneceAdministrador(user);
                if (r) {
                    frmLogin.lblCargando.setText("La cuenta o la contraseña es incorrecta.\nSi no recuerdas la cuenta.");
                    frmLogin.lblRestablecerAhora.setVisible(true);
                } else {
                    frmLogin.lblRestablecerAhora.setVisible(false);
                    frmLogin.lblCargando.setText("La cuenta o la contraseña es incorrecta.");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }
    
    
    class TimerListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            frmLogin.progressBar.setValue(task.getCurrent());
            loop++;
            if (loop == 1) {
                frmLogin.lblCargando.setText("Cargando.");
            }
            if (loop == 2) {
                frmLogin.lblCargando.setText("Cargando..");
            }
            if (loop == 3) {
                frmLogin.lblCargando.setText("Cargando...");
                loop = 0;
            }

            if (task.done()) {
                //listo = true;
                frmLogin.lblCargando.setText("¡Listo!");
                instanciar();
            }
        }
    }

}
