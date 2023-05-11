/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

public class ModelTicketDetail {
    private int iddetalleticket;
    private int idticket;
    private int idproducto;
    private int cantidad;
    private double precioVenta;
    private int devuelto;
    private int cantidadDevo;

    public int getIddetalleticket() {
        return iddetalleticket;
    }

    public void setIddetalleticket(int iddetalleticket) {
        this.iddetalleticket = iddetalleticket;
    }

    public int getIdticket() {
        return idticket;
    }

    public void setIdticket(int idticket) {
        this.idticket = idticket;
    }

    public int getIdproducto() {
        return idproducto;
    }

    public void setIdproducto(int idproducto) {
        this.idproducto = idproducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public int getDevuelto() {
        return devuelto;
    }

    public void setDevuelto(int devuelto) {
        this.devuelto = devuelto;
    }

    public int getCantidadDevo() {
        return cantidadDevo;
    }

    public void setCantidadDevo(int cantidadDevo) {
        this.cantidadDevo = cantidadDevo;
    }
}
