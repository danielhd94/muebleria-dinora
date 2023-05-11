package model;

public class ModelPaymet {
    private int idpago;
    private int idcliente;
    private int idempleado; 
    private double monto;

    public int getIdpago() {
        return idpago;
    }

    public void setIdpago(int idpagos) {
        this.idpago = idpagos;
    }

    public int getIdcliente() {
        return idcliente;
    }

    public void setIdcliente(int idcliente) {
        this.idcliente = idcliente;
    }

    public int getIdempleado() {
        return idempleado;
    }

    public void setIdempleado(int idempleado) {
        this.idempleado = idempleado;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }
    
    
}
