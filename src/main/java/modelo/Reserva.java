/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.sql.Date;


public class Reserva extends Venta{
    
    private int id_reserva; 
    private Date fechaEntrega; 
    private double anticipo;
    private String estado;
    
    public Reserva(){
        super();
        this.estado = "En proceso"; 
    }

    public void setId_reserva(int id_reserva) {
        this.id_reserva = id_reserva;
    }
    
    
    public int getId_reserva() {
        return id_reserva;
    }
    
    public Date getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public double getAnticipo() {
        return anticipo;
    }

    public void setAnticipo(double anticipo) {
        this.anticipo = anticipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
}
