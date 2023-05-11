package model;

public class ModelIOCash {
    private int idEntradaSalida;
    private int idEmpleado; 
    private Double monto;
    private String descripcion;
    private int tipo;   

    public int getIdEntradaSalida() {
        return idEntradaSalida;
    }

    public void setIdEntradaSalida(int idEntradaSalida) {
        this.idEntradaSalida = idEntradaSalida;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

}
