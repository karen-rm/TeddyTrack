
package controlador;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import modelo.DetalleVentas;
import vista.Ticket;


public class Control_Recibo {
    private Ticket recibo;
    private String modalidad; 
    private Date fecha; 
    
    public Control_Recibo(Ticket recibo, List<DetalleVentas> detalleVentaLista, String modalidad){
        this.recibo = recibo; 
         this.modalidad = modalidad; 
        mostrarDetalleVenta(detalleVentaLista);
        recibo.setVisible(true); 
        recibo.setLocationRelativeTo(null); 
       
        // Configura el comportamiento de cierre de la ventana
        recibo.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Mantener la ventana siempre en la parte superior
        recibo.setAlwaysOnTop(true);
        System.out.println("La modalidad essss: " + modalidad);
    }
    
    public Control_Recibo(Ticket recibo, List<DetalleVentas> detalleVentaLista, String modalidad, Date fechaE){
        this.recibo = recibo; 
         this.modalidad = modalidad; 
         this.fecha = fechaE;
        mostrarDetalleVenta(detalleVentaLista);
        recibo.setVisible(true); 
        recibo.setLocationRelativeTo(null); 
       
        // Configura el comportamiento de cierre de la ventana
        recibo.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Mantener la ventana siempre en la parte superior
        recibo.setAlwaysOnTop(true);
        System.out.println("La modalidad essss: " + modalidad);
    }
    
    
    
    public void mostrarDetalleVenta(List<DetalleVentas> detalleVentaLista) {
        
        // Limpia el panel antes de mostrar el nuevo detalle de venta
        recibo.panelDetalleVenta.removeAll();

        // Ajusta el layout del panel
        recibo.panelDetalleVenta.setLayout(new GridBagLayout());
        
        // Configura las restricciones para la primera columna
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets.left = 10; // Ajusta el margen izquierdo
        
        // Añade las etiquetas para mostrar el encabezado del detalle de venta
        recibo.panelDetalleVenta.add(new JLabel("CANT"), gbc);
        gbc.gridx++;
        recibo.panelDetalleVenta.add(new JLabel("PRODUCTO"), gbc);
        gbc.gridx++;
        recibo.panelDetalleVenta.add(new JLabel("TOTAL"), gbc);

        // Recorre la lista de detalles de venta y muestra cada elemento
        DecimalFormat df = new DecimalFormat("#.##"); // Formato para dos decimales
        double totalVenta = 0.0; // Variable para almacenar el total de la venta
        for (int i = 0; i < detalleVentaLista.size(); i++) {
            DetalleVentas detalle = detalleVentaLista.get(i);
            gbc.gridy++;
            gbc.gridx = 0;
            JLabel lblCantidad = new JLabel(String.valueOf(detalle.getCantidad()));
            recibo.panelDetalleVenta.add(lblCantidad, gbc);
            gbc.gridx++;
            JLabel lblProducto = new JLabel(detalle.getNombre() + "...$" + df.format(detalle.getPrecio()));
            recibo.panelDetalleVenta.add(lblProducto, gbc);
            gbc.gridx++;
            JLabel lblTotal = new JLabel("$" + df.format(detalle.getPrecio() * detalle.getCantidad()));
            recibo.panelDetalleVenta.add(lblTotal, gbc);
            // Suma el total de este detalle al total de la venta
            totalVenta += detalle.getPrecio() * detalle.getCantidad();
        }

        // Añade una etiqueta con el total de la venta al final
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 3; // Ocupa todas las columnas disponibles
        JLabel lblTotalVenta = new JLabel("TOTAL: $" + df.format(totalVenta));
        recibo.panelDetalleVenta.add(lblTotalVenta, gbc);
        
        if("Reserva".equals(modalidad)){
            System.out.println("pase por aqui modalidadddd");
            //Añadir importe
            gbc.gridy++;
            gbc.gridx = 0;
            gbc.gridwidth = 3; // Ocupa todas las columnas disponibles
            JLabel lblImporte = new JLabel("Importe pagado: $" + df.format((totalVenta)/2));
            recibo.panelDetalleVenta.add(lblImporte, gbc);
            
            SimpleDateFormat formatoDeseado = new SimpleDateFormat("dd/MM/yyyy");
            String fechaFormateada = formatoDeseado.format(fecha);
            //Añadir fecha entrega
            gbc.gridy++;
            gbc.gridx = 0;
            gbc.gridwidth = 3; // Ocupa todas las columnas disponibles
            JLabel lblfecha = new JLabel("Fecha de entrega pedido: " + fechaFormateada);
            recibo.panelDetalleVenta.add(lblfecha, gbc);
        }
        
        if("Reserva2".equals(modalidad)){
            System.out.println("pase por aqui modalidadddd");
            //Añadir importe
            gbc.gridy++;
            gbc.gridx = 0;
            gbc.gridwidth = 3; // Ocupa todas las columnas disponibles
            JLabel lblImporte = new JLabel("Importe pagado: $" + df.format((totalVenta)/2));
            recibo.panelDetalleVenta.add(lblImporte, gbc);
            
            //Añadir Pago liquidado
            gbc.gridy++;
            gbc.gridx = 0;
            gbc.gridwidth = 3; // Ocupa todas las columnas disponibles
            JLabel lblPago = new JLabel("Pago liquidado.");
            recibo.panelDetalleVenta.add(lblPago, gbc);
        }
        

        // Refresca el panel para que se muestren los cambios
        recibo.panelDetalleVenta.revalidate();
        recibo.panelDetalleVenta.repaint();
    }

}
