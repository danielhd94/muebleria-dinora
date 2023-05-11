package model;

public class ModelMovClient {
    private int idMovimientoCliente;
    private int idcliente;
    private int tipo;
    private Double monto;

    /**
     * @return the idMovimientoCliente
     */
    public int getIdMovimientoCliente() {
        return idMovimientoCliente;
    }

    /**
     * @param idMovimientoCliente the idMovimientoCliente to set
     */
    public void setIdMovimientoCliente(int idMovimientoCliente) {
        this.idMovimientoCliente = idMovimientoCliente;
    }

    /**
     * @return the idcliente
     */
    public int getIdcliente() {
        return idcliente;
    }

    /**
     * @param idcliente the idcliente to set
     */
    public void setIdcliente(int idcliente) {
        this.idcliente = idcliente;
    }

    /**
     * @return the tipo
     */
    public int getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the monto
     */
    public Double getMonto() {
        return monto;
    }

    /**
     * @param monto the monto to set
     */
    public void setMonto(Double monto) {
        this.monto = monto;
    }
    
}
