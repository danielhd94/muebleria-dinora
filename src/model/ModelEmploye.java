package model;


public class ModelEmploye {
    private int idempleado;
    private String nombre;
    private String usuario;
    private String password;
    private String email;

   
    private int privilegio;
    private int inactivo;

    public ModelEmploye(int idempleado, String nombre, String usuario, String password, int privilegio) {
        this.idempleado = idempleado;
        this.nombre = nombre;
        this.usuario = usuario;
        this.password = password;
        this.privilegio = privilegio;
    }

    public ModelEmploye() {
    }

    public int getIdempleado() {
        return idempleado;
    }

    public void setIdempleado(int idempleado) {
        this.idempleado = idempleado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
     public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPrivilegio() {
        return privilegio;
    }

    public void setPrivilegio(int privilegio) {
        this.privilegio = privilegio;
    }
    
    public int getInactivo() {
        return inactivo;
    }

    public void setInactivo(int inactivo) {
        this.inactivo = inactivo;
    }
    
}
