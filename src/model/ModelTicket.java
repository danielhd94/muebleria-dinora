package model;

public class ModelTicket {
    private int idTicket;
    private int idEmpleado;
    private int idCliente;
    private int iva;
    private Double pagoCon;    
    private int estado;

    public int getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(int idTicket) {
        this.idTicket = idTicket;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIva() {
        return iva;
    }

    public void setIva(int iva) {
        this.iva = iva;
    }
    
    public Double getPagoCon() {
        return pagoCon;
    }

    public void setPagoCon(Double pagoCon) {
        this.pagoCon = pagoCon;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
    
}
