
package modelo;

public class Pago {
    private double monto; 
    private String tipo_pago; 
    
    
    public Pago(){
        
    }
    
    public Pago(double monto, String tipo_pago){
        this.monto = monto;
        this.tipo_pago = tipo_pago;
        
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getTipo_pago() {
        return tipo_pago;
    }

    public void setTipo_pago(String tipo_pago) {
        this.tipo_pago = tipo_pago;
    }
    
    
}
