package extras;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Conexion {

    private String db = "dbdinora";
    private String url = "jdbc:mysql://127.0.0.1:3306/" + db;
    private String userName = "root";
    private String password = "N0m3l0s3.1";

    public Conexion() {
    }

    public Connection getConnection() {
        Connection link = null;
        try {
            // Class.forName("org.gjt.mm.mysql.Driver");
            Class.forName("com.mysql.cj.jdbc.Driver"); //nuevo driver
            link = DriverManager.getConnection(this.url, this.userName, this.password);
            if (link.isClosed()) {
                JOptionPane.showMessageDialog(null, "Click en Ok, para reestablecer la conexión. Gracias!");
                // reiniciarServidor();
            }
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Ha ocurrido un error inesperado con el servidor.\n Por favor reinicia el servidor. \nError: " + e);
            System.out.println(e);
            System.exit(1);
        }
        return link;

    }

    public void reiniciarServidor() {
        String fileLocal = new String("XAMPP Control Panel.xlsx");
        try {
            Runtime.getRuntime().exec("cmd /c start " + fileLocal);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
