
package modelo;

import java.util.Date;


public class DetalleVentas {
    private String nombre;
    private double precio;
    private int cantidad;
   

    public DetalleVentas(String nombre, double precio, int cantidad) {
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
       
    }
    
    public DetalleVentas(){
        
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    
    
    
    public String mostrarDetalle() {
        return nombre + "........ " + precio + " ........ " + "( " + cantidad + " )";
    }

}

