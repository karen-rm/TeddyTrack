/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.time.LocalDate;

public class Venta {
    private int idVenta; 
    private LocalDate fecha; 
    private boolean es_reserva; 
    private double total; 
    
    public Venta(){
        fecha = LocalDate.now();  
    }
    
    public Venta(String restriccion){
         
    }
    
    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public boolean getEs_reserva() {
        return es_reserva;
    }

    public void setEs_reserva(boolean es_reserva) {
        this.es_reserva = es_reserva;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    
    public void imprimirInfoVenta(){
        System.out.println("Id venta: " + idVenta + "\n");
        System.out.println("Total: " + total + "\n");
        System.out.println("Fecha: " + fecha + "\n");
    }
    
}
