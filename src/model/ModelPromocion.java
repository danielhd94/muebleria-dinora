package model;

public class ModelPromocion {
    private int idpromocion;
    private int idproducto;
    private String nombre;
    private int desde;
    private int hasta;
    private Double preciopromocion;

    public int getIdpromocion() {
        return idpromocion;
    }

    public void setIdpromocion(int idpromocion) {
        this.idpromocion = idpromocion;
    }

    public int getIdproducto() {
        return idproducto;
    }

    public void setIdproducto(int idproducto) {
        this.idproducto = idproducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getDesde() {
        return desde;
    }

    public void setDesde(int desde) {
        this.desde = desde;
    }

    public int getHasta() {
        return hasta;
    }

    public void setHasta(int hasta) {
        this.hasta = hasta;
    }

    public Double getPreciopromocion() {
        return preciopromocion;
    }

    public void setPreciopromocion(Double preciopromocion) {
        this.preciopromocion = preciopromocion;
    }

    
}
