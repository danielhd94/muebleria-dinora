package model;

public class ModelProduct {
    private int idproducto;
    private int idcategoria;
    private String noserie;
    private String descripcion;
    private Double precioCosto;
    private Double precioVenta;
    private Double precioMayoreo;
    private int existencia;
    private int cantidadMinima;
    private int inactivo;

    public int getIdproducto() {
        return idproducto;
    }

    public void setIdproducto(int idproducto) {
        this.idproducto = idproducto;
    }

    public int getIdcategoria() {
        return idcategoria;
    }

    public void setIdcategoria(int idcategoria) {
        this.idcategoria = idcategoria;
    }

    public String getNoserie() {
        return noserie;
    }

    public void setNoserie(String noserie) {
        this.noserie = noserie;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecioCosto() {
        return precioCosto;
    }

    public void setPrecioCosto(Double precioCosto) {
        this.precioCosto = precioCosto;
    }

    public Double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(Double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public Double getPrecioMayoreo() {
        return precioMayoreo;
    }

    public void setPrecioMayoreo(Double precioMayoreo) {
        this.precioMayoreo = precioMayoreo;
    }

    public int getExistencia() {
        return existencia;
    }

    public void setExistencia(int existencia) {
        this.existencia = existencia;
    }

    public int getCantidadMinima() {
        return cantidadMinima;
    }

    public void setCantidadMinima(int cantidadMinima) {
        this.cantidadMinima = cantidadMinima;
    }

    public int getInactivo() {
        return inactivo;
    }

    public void setInactivo(int inactivo) {
        this.inactivo = inactivo;
    }
    
}
