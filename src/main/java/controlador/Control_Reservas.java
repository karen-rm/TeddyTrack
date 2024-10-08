
package controlador;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import modelo.Cliente;
import modelo.Conexion;
import modelo.DetalleVentas;
import modelo.Pago;
import modelo.Reserva;
import modelo.Venta;
import vista.GestionReservas;
import vista.Inicio;
import vista.PagarConEfectivo;
import vista.PagarConTarjeta;

public class Control_Reservas implements ActionListener {
    private Reserva reserva; 
    private GestionReservas vistaReservas;
    private Inicio vistaInicio; 
    private Venta venta; 
    private Cliente cliente; 
    private DetalleVentas detalleventa; 
    private PagarConEfectivo vistaPagoEfectivo;
    private PagarConTarjeta vistaPagoTarjeta; 
    private Pago pago; 
     
    
    
    Control_Reservas(){

    }
    Control_Reservas(Inicio vistaInicio, GestionReservas vistaReservas){
    this.vistaInicio = vistaInicio; 
    this.vistaReservas = vistaReservas; 
    this.venta = new Venta("Restriccion");
    this.cliente = new Cliente();
    this.detalleventa = new DetalleVentas(); 
    vistaReservas.btnRegresar.addActionListener(this);
    
    vistaReservas.btnRegresar.setBackground(Color.WHITE);
    vistaReservas.btnRegresar.setBorderPainted(false);
    
    // Obtener la lista de reservas
    List<Reserva> reservas = obtenerReservas();
    
    // Iterar sobre cada reserva e imprimir su información en la consola
    for (Reserva reserva : reservas) {
        System.out.println("ID Reserva: " + reserva.getId_reserva());
        System.out.println("Fecha de entrega: " + reserva.getFechaEntrega());
        System.out.println("Anticipo: " + reserva.getAnticipo());
        System.out.println("Estado: " + reserva.getEstado());
        System.out.println("-------------------------------------------");
    }

    // Obtener el JScrollPane de la interfaz GestionReservas
    JScrollPane scrollPaneReservas = vistaReservas.jScrollPaneReservas;

    // Configurar el panel que contendrá las reservas
    JPanel panelReservas = new JPanel(new GridLayout(reservas.size(), 1)); // Una fila por reserva
    panelReservas.setBackground(Color.WHITE); // Color de fondo opcional

    // Iterar sobre las reservas y agregarlas al panel
for (Reserva reserva : reservas) {
    JPanel reservaPanel = new JPanel(new BorderLayout()); // Utilizamos BorderLayout para el panel de reserva
    reservaPanel.setBackground(Color.WHITE); // Fondo blanco

    // Aplicar un borde vacío al panel de reserva para crear espacio entre las reservas
    reservaPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Panel para la información de la reserva
    JPanel infoPanel = new JPanel(new GridLayout(10, 2)); // Tres filas: Fecha de entrega, Anticipo, Estado
    infoPanel.setBackground(Color.WHITE); // Fondo blanco

    // Crear etiquetas para la información de la reserva
    JLabel labelIdReservaTitle = new JLabel("Id reserva: ");
    JLabel labelIdReserva = new JLabel(String.valueOf(reserva.getId_reserva()));
    JLabel labelFechaInicioTitle = new JLabel("Fecha de reserva: ");
    JLabel labelFechaInicio = new JLabel(String.valueOf(venta.getFecha()));
    JLabel labelFechaEntregaTitle = new JLabel("Fecha de entrega:");
    JLabel labelFechaEntrega = new JLabel(reserva.getFechaEntrega() != null ? reserva.getFechaEntrega().toString() : "");
    JLabel labelAnticipoTitle = new JLabel("Anticipo:");
    JLabel labelAnticipo = new JLabel(String.valueOf(reserva.getAnticipo()));
    JLabel labelTotalTitle = new JLabel("Total: ");
    JLabel labelTotal = new JLabel(String.valueOf(venta.getTotal()));
    JLabel labelEstadoTitle = new JLabel("Estado:");
    JLabel labelEstado = new JLabel(reserva.getEstado());
    JLabel labelClienteNombreTitle = new JLabel("Cliente:");
    JLabel labelCliente = new JLabel(String.valueOf(cliente.getNombre()) + " " + String.valueOf(cliente.getApellido()) );
    JLabel labelClienteTelefonoTitulo = new JLabel("Telefono:");
    JLabel labelClienteTelefono = new JLabel(String.valueOf(cliente.getTelefono()));
    

    // Configurar la alineación de las etiquetas al centro
    labelIdReservaTitle.setHorizontalAlignment(SwingConstants.CENTER);
    labelIdReserva.setHorizontalAlignment(SwingConstants.CENTER);
    labelFechaInicioTitle.setHorizontalAlignment(SwingConstants.CENTER);
    labelFechaInicio.setHorizontalAlignment(SwingConstants.CENTER);
    labelFechaEntregaTitle.setHorizontalAlignment(SwingConstants.CENTER);
    labelFechaEntrega.setHorizontalAlignment(SwingConstants.CENTER);
    labelAnticipoTitle.setHorizontalAlignment(SwingConstants.CENTER);
    labelAnticipo.setHorizontalAlignment(SwingConstants.CENTER);
    labelTotalTitle.setHorizontalAlignment(SwingConstants.CENTER);
    labelTotal.setHorizontalAlignment(SwingConstants.CENTER);
    labelEstadoTitle.setHorizontalAlignment(SwingConstants.CENTER);
    labelEstado.setHorizontalAlignment(SwingConstants.CENTER);
    labelClienteNombreTitle.setHorizontalAlignment(SwingConstants.CENTER);
    labelCliente.setHorizontalAlignment(SwingConstants.CENTER);
    labelClienteTelefonoTitulo.setHorizontalAlignment(SwingConstants.CENTER);
    labelClienteTelefono.setHorizontalAlignment(SwingConstants.CENTER);
   
    

    // Agregar las etiquetas al panel de información
    infoPanel.add(labelIdReservaTitle); 
    infoPanel.add(labelIdReserva);
    infoPanel.add(labelFechaInicioTitle);
    infoPanel.add(labelFechaInicio);
    infoPanel.add(labelFechaEntregaTitle);
    infoPanel.add(labelFechaEntrega);
    infoPanel.add(labelAnticipoTitle);
    infoPanel.add(labelAnticipo);
    infoPanel.add(labelTotalTitle);
    infoPanel.add(labelTotal);
    infoPanel.add(labelEstadoTitle);
    infoPanel.add(labelEstado);
    infoPanel.add(labelClienteNombreTitle);
    infoPanel.add(labelCliente);
    infoPanel.add(labelClienteTelefonoTitulo);
    infoPanel.add(labelClienteTelefono);
   
            

    
    //En vertical
    // Crear un nuevo panel de botones con BoxLayout vertical
    JPanel botonesPanel = new JPanel();
    botonesPanel.setLayout(new BoxLayout(botonesPanel, BoxLayout.Y_AXIS));
    botonesPanel.setBackground(Color.WHITE); // Fondo blanco
    
    // Obtener la información de la venta correspondiente a esta reserva
    int idVenta = obtenerIdVenta(reserva);
    venta = obtenerInfoVenta(idVenta);
    if (venta != null) {
        // Actualizar las etiquetas de la información de la venta en el panel de reserva
        labelTotal.setText(String.valueOf(venta.getTotal()));
        labelFechaInicio.setText(String.valueOf(venta.getFecha()));
        
    } else {
        System.out.println("idVenta no encontrado");
    }

    // Crear botones
    JButton botonEntregar = new JButton(" Entregar ");
    botonEntregar.setActionCommand("Entregar_" + String.valueOf(reserva.getId_reserva()) + "_" + String.valueOf(venta.getIdVenta()));
    
    JButton botonCancelar = new JButton(" Cancelar ");
    botonCancelar.setActionCommand("Cancelar_" + String.valueOf(reserva.getId_reserva()) + "_" + String.valueOf(venta.getIdVenta()));
    
    JButton botonDetalles = new JButton("Visualizar");
    botonDetalles.setActionCommand("Visualizar_" + String.valueOf(reserva.getId_reserva()) + "_" + String.valueOf(venta.getIdVenta()));
    
    // Establecer el color de fondo de los botones a azul claro
    Color azulClaro = new Color(173, 216, 230); // Color azul claro predefinido en Java
    botonEntregar.setBackground(azulClaro);
    botonCancelar.setBackground(azulClaro);
    botonDetalles.setBackground(azulClaro);
    
    // Aplicar márgenes a los botones
    botonEntregar.setMargin(new Insets(5, 10, 5, 10)); // Márgenes del botón (arriba, izquierda, abajo, derecha)
    botonCancelar.setMargin(new Insets(5, 10, 5, 10)); // Márgenes del botón (arriba, izquierda, abajo, derecha)
    botonDetalles.setMargin(new Insets(5, 10, 5, 10));
    
    
    // Agregar los botones al panel de botones con espaciado entre ellos
    botonesPanel.add(Box.createVerticalStrut(10)); // Espaciado entre botones
    botonesPanel.add(botonEntregar);
    botonesPanel.add(Box.createVerticalStrut(10)); // Espaciado entre botones
    botonesPanel.add(botonCancelar);
    botonesPanel.add(Box.createVerticalStrut(10)); // Espaciado entre botones
    botonesPanel.add(botonDetalles);
  
    // Aplicar un borde alrededor del panel de botones
    //botonesPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    
    
    // Agregar el ActionListener a los botones
    botonEntregar.addActionListener(this);
    botonCancelar.addActionListener(this);
    botonDetalles.addActionListener(this);
    
    botonesPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0)); // Agregar un espacio inferior al panel de botones
   

// Agregar un separador entre cada panel de reserva
    reservaPanel.add(new JSeparator(), BorderLayout.SOUTH);

    // Agregar el panel de información al centro del panel de reserva
    reservaPanel.add(infoPanel, BorderLayout.CENTER);

    // Agregar el panel de botones a la derecha del panel de reserva
    reservaPanel.add(botonesPanel, BorderLayout.LINE_END);

    // Agregar el panel de reserva al panel principal
    panelReservas.add(reservaPanel);
    
    
    int idCliente = obtenerIdCliente(reserva); 
    cliente = obtenerInfoCliente(idCliente);
    if (cliente != null) {
        labelCliente.setText((String.valueOf(cliente.getNombre()) + " " + String.valueOf(cliente.getApellido()) ));
        labelClienteTelefono.setText((String.valueOf(cliente.getTelefono())));
        
    } else {
        System.out.println("idVenta no encontrado");
    }

    
    
}


    // Establecer el panel de reservas como la vista del JScrollPane
    scrollPaneReservas.setViewportView(panelReservas);

    // Refrescar la vista de la interfaz
    vistaReservas.revalidate();
    

}

    


    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
       
        if (e.getSource() == vistaReservas.btnRegresar) {
            vistaReservas.dispose();
            vistaInicio.setVisible(true);
        }// Verificar si el evento proviene del botón "Entregar"
        else if (e.getActionCommand().startsWith("Entregar")) {
            
            // Obtener el ID de reserva y el ID de venta del identificador único del botón
            String[] parts1 = e.getActionCommand().split("_");
            if (parts1.length == 3) {
                String id_reserva = parts1[1];
                String id_venta = parts1[2];
                int idVenta = Integer.parseInt(id_venta);
                int idReserva = Integer.parseInt(id_reserva);
                System.out.println("Visualizando reserva con ID: " + id_reserva);
                System.out.println("ID de venta asociado: " + id_venta);
                LocalDate fechaInicial = obtenerFechaDeVenta(idVenta);
                System.out.println(fechaInicial);
                // Array de strings para los nombres de los botones en español
                String[] opciones = {"Efectivo", "Tarjeta"};

                // Mostrar un diálogo de confirmación al usuario con botones en español
                int confirmacion = JOptionPane.showOptionDialog(
                    null,
                    "Elija el método para completar el pago de la reserva " + id_reserva ,
                    "Entregar Reserva",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[1] // Botón predeterminado (en este caso, "No")
                );
                if (confirmacion == JOptionPane.YES_OPTION) {
                    vistaPagoEfectivo = new PagarConEfectivo(); 
                    pago = new Pago(); 
                    vistaPagoEfectivo.setVisible(true);
                    
                    Control_PagarConEfectivo ctrlCompletarPagoEfectivo = new Control_PagarConEfectivo(vistaPagoEfectivo, idVenta, idReserva,  pago, this, fechaInicial);             
                }else if(confirmacion == JOptionPane.NO_OPTION){
                    vistaPagoTarjeta = new PagarConTarjeta(); 
                    pago = new Pago(); 
                    vistaPagoTarjeta.setVisible(true);
                    
                    Control_PagarConTarjeta ctrlCompletarPagoTarjeta = new Control_PagarConTarjeta(vistaPagoTarjeta, idVenta, idReserva, pago,  this, fechaInicial); 
                }
        }
        }
        // Verificar si el evento proviene del botón "Cancelar"
        else if (e.getActionCommand().startsWith("Cancelar")) {
            // Obtener el ID de reserva y el ID de venta del identificador único del botón
            String[] parts2 = e.getActionCommand().split("_");
            if (parts2.length == 3) {
                String id_reserva = parts2[1];
                String id_venta = parts2[2];
                System.out.println("Cancelando reserva con ID: " + id_reserva);
                System.out.println("ID de venta asociado: " + id_venta);
                
                // Array de strings para los nombres de los botones en español
                String[] opciones = {"Sí", "No"};

                // Mostrar un diálogo de confirmación al usuario con botones en español
                int confirmacion = JOptionPane.showOptionDialog(
                    null,
                    "¿Estás seguro de que deseas cancelar la reserva " + id_reserva + " ?",
                    "Confirmar Cancelación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[1] // Botón predeterminado (en este caso, "No")
                );
                
                // Si el usuario confirma la cancelación (presiona "Sí")
                if (confirmacion == JOptionPane.YES_OPTION) {
                
                    List<DetalleVentas> detallesVenta = obtenerInfoDetalleVenta(Integer.parseInt(id_venta));
                    // Recorrer la lista de detalles de venta y actualizar el stock de cada producto
                    for (DetalleVentas detalleVenta : detallesVenta) {
                        //int idProducto = detalleVenta.getIdProducto();
                        String nombreProducto = detalleVenta.getNombre();
                        int cantidadVendida = detalleVenta.getCantidad();

                        // Actualizar el stock del producto en la base de datos
                        actualizarStockProducto(nombreProducto, cantidadVendida);

                        reserva = obtenerReservaPorId(Integer.parseInt(id_reserva));
                        actualizarEstadoReservaCancelada(reserva); 
                        actualizarPanelReservas(); 
                    }
                }else{
                    
                }
                
            }
           
        }else if(e.getActionCommand().startsWith("Visualizar")){
            
            // Obtener el ID de reserva y el ID de venta del identificador único del botón
            String[] parts = e.getActionCommand().split("_");
            if (parts.length == 3) {
                String id_reserva = parts[1];
                String id_venta = parts[2];
                System.out.println("Visualizando reserva con ID: " + id_reserva);
                System.out.println("ID de venta asociado: " + id_venta);
                
                // Llamar al método obtenerInfoDetalleVenta para obtener los detalles de la venta
                List<DetalleVentas> detallesVenta = obtenerInfoDetalleVenta(Integer.parseInt(id_venta));

                // Verificar si se obtuvieron los detalles de la venta
                if (!detallesVenta.isEmpty()) {
                    // Construir el mensaje con los detalles de cada producto vendido
                    StringBuilder mensaje = new StringBuilder();
                    mensaje.append("Detalle venta reserva con ID: ").append(id_reserva).append("\n");
                    mensaje.append("ID de venta asociado: ").append(id_venta).append("\n");
                    mensaje.append("Detalle de la venta:\n");

                    for (DetalleVentas detalleVenta : detallesVenta) {
                        mensaje.append("- Nombre del producto: ").append(detalleVenta.getNombre()).append("\n");
                        mensaje.append("  Cantidad vendida: ").append(detalleVenta.getCantidad()).append("\n");
                        mensaje.append("  Precio unitario: ").append(detalleVenta.getPrecio()).append("\n");
                    }

                    // Mostrar los detalles de la venta en un cuadro de diálogo
                    JOptionPane optionPane = new JOptionPane(mensaje.toString(), JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
                    optionPane.setOptions(new Object[]{"Aceptar"});
                    JDialog dialog = optionPane.createDialog(vistaReservas, "Detalle de la Venta");
                    dialog.setVisible(true);
                } else {
                    // Mostrar un mensaje si no se pudieron obtener los detalles de la venta
                    JOptionPane optionPane = new JOptionPane("No se pudieron obtener los detalles de la venta.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
                    optionPane.setOptions(new Object[]{"Aceptar"});
                    JDialog dialog = optionPane.createDialog(vistaReservas, "Error");
                    dialog.setVisible(true);
                }

            }
        }
    }
    
    public void actualizarPanelReservas() {
    // Obtener la lista de reservas actualizada
    List<Reserva> reservas = obtenerReservas();

    // Obtener el JScrollPane de la interfaz GestionReservas
    JScrollPane scrollPaneReservas = vistaReservas.jScrollPaneReservas;

    // Configurar el panel que contendrá las reservas
    JPanel panelReservas = new JPanel(new GridLayout(reservas.size(), 1)); // Una fila por reserva
    panelReservas.setBackground(Color.WHITE); // Color de fondo opcional

    // Iterar sobre las reservas y agregarlas al panel
    for (Reserva reserva : reservas) {
        JPanel reservaPanel = new JPanel(new BorderLayout()); // Utilizamos BorderLayout para el panel de reserva
        reservaPanel.setBackground(Color.WHITE); // Fondo blanco

        // Aplicar un borde vacío al panel de reserva para crear espacio entre las reservas
        reservaPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel para la información de la reserva
        JPanel infoPanel = new JPanel(new GridLayout(10, 2)); // Tres filas: Fecha de entrega, Anticipo, Estado
        infoPanel.setBackground(Color.WHITE); // Fondo blanco

        // Crear etiquetas para la información de la reserva
    JLabel labelIdReservaTitle = new JLabel("Id reserva: ");
    JLabel labelIdReserva = new JLabel(String.valueOf(reserva.getId_reserva()));
    JLabel labelFechaInicioTitle = new JLabel("Fecha de reserva: ");
    JLabel labelFechaInicio = new JLabel(String.valueOf(venta.getFecha()));
    JLabel labelFechaEntregaTitle = new JLabel("Fecha de entrega:");
    JLabel labelFechaEntrega = new JLabel(reserva.getFechaEntrega() != null ? reserva.getFechaEntrega().toString() : "");
    JLabel labelAnticipoTitle = new JLabel("Anticipo:");
    JLabel labelAnticipo = new JLabel(String.valueOf(reserva.getAnticipo()));
    JLabel labelTotalTitle = new JLabel("Total: ");
    JLabel labelTotal = new JLabel(String.valueOf(venta.getTotal()));
    JLabel labelEstadoTitle = new JLabel("Estado:");
    JLabel labelEstado = new JLabel(reserva.getEstado());
    JLabel labelClienteNombreTitle = new JLabel("Cliente:");
    JLabel labelCliente = new JLabel(String.valueOf(cliente.getNombre()) + " " + String.valueOf(cliente.getApellido()) );
    JLabel labelClienteTelefonoTitulo = new JLabel("Telefono:");
    JLabel labelClienteTelefono = new JLabel(String.valueOf(cliente.getTelefono()));
    

    // Configurar la alineación de las etiquetas al centro
    labelIdReservaTitle.setHorizontalAlignment(SwingConstants.CENTER);
    labelIdReserva.setHorizontalAlignment(SwingConstants.CENTER);
    labelFechaInicioTitle.setHorizontalAlignment(SwingConstants.CENTER);
    labelFechaInicio.setHorizontalAlignment(SwingConstants.CENTER);
    labelFechaEntregaTitle.setHorizontalAlignment(SwingConstants.CENTER);
    labelFechaEntrega.setHorizontalAlignment(SwingConstants.CENTER);
    labelAnticipoTitle.setHorizontalAlignment(SwingConstants.CENTER);
    labelAnticipo.setHorizontalAlignment(SwingConstants.CENTER);
    labelTotalTitle.setHorizontalAlignment(SwingConstants.CENTER);
    labelTotal.setHorizontalAlignment(SwingConstants.CENTER);
    labelEstadoTitle.setHorizontalAlignment(SwingConstants.CENTER);
    labelEstado.setHorizontalAlignment(SwingConstants.CENTER);
    labelClienteNombreTitle.setHorizontalAlignment(SwingConstants.CENTER);
    labelCliente.setHorizontalAlignment(SwingConstants.CENTER);
    labelClienteTelefonoTitulo.setHorizontalAlignment(SwingConstants.CENTER);
    labelClienteTelefono.setHorizontalAlignment(SwingConstants.CENTER);
   
    

    // Agregar las etiquetas al panel de información
    infoPanel.add(labelIdReservaTitle); 
    infoPanel.add(labelIdReserva);
    infoPanel.add(labelFechaInicioTitle);
    infoPanel.add(labelFechaInicio);
    infoPanel.add(labelFechaEntregaTitle);
    infoPanel.add(labelFechaEntrega);
    infoPanel.add(labelAnticipoTitle);
    infoPanel.add(labelAnticipo);
    infoPanel.add(labelTotalTitle);
    infoPanel.add(labelTotal);
    infoPanel.add(labelEstadoTitle);
    infoPanel.add(labelEstado);
    infoPanel.add(labelClienteNombreTitle);
    infoPanel.add(labelCliente);
    infoPanel.add(labelClienteTelefonoTitulo);
    infoPanel.add(labelClienteTelefono);

        // Panel para los botones
        JPanel botonesPanel = new JPanel();
        botonesPanel.setLayout(new BoxLayout(botonesPanel, BoxLayout.Y_AXIS));
        botonesPanel.setBackground(Color.WHITE); // Fondo blanco
        
        
        // Obtener la información de la venta correspondiente a esta reserva
    int idVenta = obtenerIdVenta(reserva);
    venta = obtenerInfoVenta(idVenta);
    if (venta != null) {
        // Actualizar las etiquetas de la información de la venta en el panel de reserva
        labelTotal.setText(String.valueOf(venta.getTotal()));
        labelFechaInicio.setText(String.valueOf(venta.getFecha()));
        
    } else {
        System.out.println("idVenta no encontrado");
    }

    // Crear botones
    JButton botonEntregar = new JButton(" Entregar ");
    botonEntregar.setActionCommand("Entregar_" + String.valueOf(reserva.getId_reserva()) + "_" + String.valueOf(venta.getIdVenta()));
    
    JButton botonCancelar = new JButton(" Cancelar ");
    JButton botonDetalles = new JButton("Visualizar");
    botonDetalles.setActionCommand("Visualizar_" + String.valueOf(reserva.getId_reserva()) + "_" + String.valueOf(venta.getIdVenta()));
    
    // Establecer el color de fondo de los botones a azul claro
    Color azulClaro = new Color(173, 216, 230); // Color azul claro predefinido en Java
    botonEntregar.setBackground(azulClaro);
    botonCancelar.setBackground(azulClaro);
    botonDetalles.setBackground(azulClaro);
    
    // Aplicar márgenes a los botones
    botonEntregar.setMargin(new Insets(5, 10, 5, 10)); // Márgenes del botón (arriba, izquierda, abajo, derecha)
    botonCancelar.setMargin(new Insets(5, 10, 5, 10)); // Márgenes del botón (arriba, izquierda, abajo, derecha)
    botonDetalles.setMargin(new Insets(5, 10, 5, 10));

        // Agregar los botones al panel de botones con espaciado entre ellos
        botonesPanel.add(Box.createVerticalStrut(10)); // Espaciado entre botones
        botonesPanel.add(botonEntregar);
        botonesPanel.add(Box.createVerticalStrut(10)); // Espaciado entre botones
        botonesPanel.add(botonCancelar);
        botonesPanel.add(Box.createVerticalStrut(10)); // Espaciado entre botones
        botonesPanel.add(botonDetalles);
        
        botonEntregar.addActionListener(this);
    botonCancelar.addActionListener(this);
    botonDetalles.addActionListener(this);
    
    botonesPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0)); // Agregar un espacio inferior al panel de botones
   


        // Agregar un separador entre cada panel de reserva
        reservaPanel.add(new JSeparator(), BorderLayout.SOUTH);

        // Agregar el panel de información al centro del panel de reserva
        reservaPanel.add(infoPanel, BorderLayout.CENTER);

        // Agregar el panel de botones a la derecha del panel de reserva
        reservaPanel.add(botonesPanel, BorderLayout.LINE_END);

        // Agregar el panel de reserva al panel principal
        panelReservas.add(reservaPanel);
        
        int idCliente = obtenerIdCliente(reserva); 
        cliente = obtenerInfoCliente(idCliente);
        if (cliente != null) {
            labelCliente.setText((String.valueOf(cliente.getNombre()) + " " + String.valueOf(cliente.getApellido()) ));
            labelClienteTelefono.setText((String.valueOf(cliente.getTelefono())));

        } else {
            System.out.println("idVenta no encontrado");
        }
    }

    // Establecer el panel de reservas como la vista del JScrollPane
    scrollPaneReservas.setViewportView(panelReservas);

    // Refrescar la vista de la interfaz
    vistaReservas.revalidate();
}
    
    
    
    
    // Método para obtener la información de las reservas desde la base de datos
public List<Reserva> obtenerReservas() {
    List<Reserva> reservas = new ArrayList<>();

    // Establecer la conexión a la base de datos y ejecutar la consulta
    Conexion conexion = new Conexion();
    String query = "SELECT id_reserva, Fecha_entrega, Anticipo, Estado FROM Reserva WHERE Estado = 'En proceso'";
    try (Connection conn = conexion.getConnection();
         PreparedStatement statement = conn.prepareStatement(query)) {

        ResultSet resultSet = statement.executeQuery();

        // Iterar sobre los resultados y agregar cada reserva a la lista
        while (resultSet.next()) {
            reserva = new Reserva();
            reserva.setId_reserva(resultSet.getInt("id_reserva")); 
            reserva.setFechaEntrega(resultSet.getDate("Fecha_entrega"));
            reserva.setAnticipo(resultSet.getDouble("Anticipo"));
            reserva.setEstado(resultSet.getString("Estado")); 
            
            
            /*int idVenta = obtenerIdVenta(reserva);
            System.out.println("idVneta:" + idVenta);
            venta = obtenerInfoVenta(idVenta);
            if (venta != null) {
                venta.imprimirInfoVenta(); 
            } else {
                System.out.println("idVenta no encontrado");
            }*/
            
            
            reservas.add(reserva);
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    return reservas;
}

public int obtenerIdVenta(Reserva reserva) {
    int idVenta = -1; // Valor predeterminado en caso de no encontrar ningún ID de venta

    // Establecer la conexión a la base de datos y ejecutar la consulta
    Conexion conexion = new Conexion();
    String query = "SELECT id_venta FROM Reserva WHERE id_reserva = ?";
    try (Connection conn = conexion.getConnection();
         PreparedStatement statement = conn.prepareStatement(query)) {
        statement.setInt(1, reserva.getId_reserva());
        ResultSet resultSet = statement.executeQuery();

        // Si se encuentra un ID de venta, asignarlo y salir del bucle
        if (resultSet.next()) {
            idVenta = resultSet.getInt("id_venta");
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    return idVenta;
}

public int obtenerIdCliente(Reserva reserva) {
    int idCliente = -1; // Valor predeterminado en caso de no encontrar ningún ID de cliente

    // Establecer la conexión a la base de datos y ejecutar la consulta
    Conexion conexion = new Conexion();
    String query = "SELECT id_cliente FROM Reserva WHERE id_reserva = ?";
    try (Connection conn = conexion.getConnection();
         PreparedStatement statement = conn.prepareStatement(query)) {
        statement.setInt(1, reserva.getId_reserva());
        ResultSet resultSet = statement.executeQuery();

        // Si se encuentra un ID de cliente, asignarlo y salir del bucle
        if (resultSet.next()) {
            idCliente = resultSet.getInt("id_cliente");
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    return idCliente;
}


public Cliente obtenerInfoCliente(int idCliente) {
    // Establecer la conexión a la base de datos y ejecutar la consulta
    Conexion conexion = new Conexion();
    String query = "SELECT Nombre, Apellido, Telefono FROM Cliente WHERE id_cliente = ?";
    try (Connection conn = conexion.getConnection();
        PreparedStatement statement = conn.prepareStatement(query)) {
        statement.setInt(1, idCliente);
        ResultSet resultSet = statement.executeQuery();

        // Si se encuentra la información del cliente, asignarla a la reserva
        if (resultSet.next()) {
            this.cliente = new Cliente();
            cliente.setNombre(resultSet.getString("Nombre"));
            cliente.setApellido(resultSet.getString("Apellido"));
            cliente.setTelefono(resultSet.getString("Telefono"));
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
     return cliente;
}

public Venta obtenerInfoVenta(int idVenta) {
    
    // Establecer la conexión a la base de datos y ejecutar la consulta
    Conexion conexion = new Conexion();
    String query = "SELECT fecha, total FROM Venta WHERE id_venta = ?";
    try (Connection conn = conexion.getConnection();
        PreparedStatement statement = conn.prepareStatement(query)) {
        statement.setInt(1, idVenta);
        ResultSet resultSet = statement.executeQuery();

        // Si se encuentra la información de la venta, asignarla al objeto Venta
        if (resultSet.next()) {
            venta = new Venta("Restriccion");
            venta.setIdVenta(idVenta);
            
            // Convertir la fecha de SQL Date a LocalDate
            Date fechaSQL = resultSet.getDate("fecha");
            LocalDate fecha = fechaSQL.toLocalDate();
            venta.setFecha(fecha);
            venta.setTotal(resultSet.getDouble("total"));
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    return venta;
}

public LocalDate obtenerFechaDeVenta(int idVenta) {
    LocalDate fecha = null; // Declarar la variable fuera del bloque try para que sea accesible

    // Establecer la conexión a la base de datos y ejecutar la consulta
    Conexion conexion = new Conexion();
    String query = "SELECT fecha FROM Venta WHERE id_venta = ?";
    try (Connection conn = conexion.getConnection();
         PreparedStatement statement = conn.prepareStatement(query)) {
        statement.setInt(1, idVenta);
        ResultSet resultSet = statement.executeQuery();

        // Si se encuentra la información de la venta, asignarla al objeto Venta
        if (resultSet.next()) {
            // Convertir la fecha de SQL Date a LocalDate
            Date fechaSQL = resultSet.getDate("fecha");
            fecha = fechaSQL.toLocalDate();
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    return fecha;
}


public List<DetalleVentas> obtenerInfoDetalleVenta(int idVenta) {
    List<DetalleVentas> detallesVenta = new ArrayList<>();

    // Establecer la conexión a la base de datos y ejecutar la consulta
    Conexion conexion = new Conexion();
    String query = "SELECT cantidad_vendida, precio_unitario, id_producto " +
                   "FROM detalleVenta " +
                   "WHERE id_venta = ?";
    try (Connection conn = conexion.getConnection();
         PreparedStatement statement = conn.prepareStatement(query)) {
        statement.setInt(1, idVenta);
        ResultSet resultSet = statement.executeQuery();

        // Iterar sobre los resultados y agregar cada detalle de venta a la lista
        while (resultSet.next()) {
            DetalleVentas detalleVenta = new DetalleVentas();
            detalleVenta.setCantidad(resultSet.getInt("cantidad_vendida"));
            detalleVenta.setPrecio(resultSet.getDouble("precio_unitario"));
            int idProducto = resultSet.getInt("id_producto");
            String nombreProducto = obtenerNombreProducto(idProducto);
            detalleVenta.setNombre(nombreProducto);
            //detalleVenta.setIdProducto(resultSet.getInt("id_producto"));
            detallesVenta.add(detalleVenta);
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    return detallesVenta;
}

public String obtenerNombreProducto(int idProducto) {
    String nombreProducto = null;

    // Establecer la conexión a la base de datos y ejecutar la consulta
    Conexion conexion = new Conexion();
    String query = "SELECT nombre FROM Producto WHERE id_producto = ?";
    try (Connection conn = conexion.getConnection();
         PreparedStatement statement = conn.prepareStatement(query)) {
        statement.setInt(1, idProducto);
        ResultSet resultSet = statement.executeQuery();

        // Si se encuentra el nombre del producto, asignarlo a la variable correspondiente
        if (resultSet.next()) {
            nombreProducto = resultSet.getString("nombre");
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    return nombreProducto;
}

public void actualizarStockProducto(String nombreProducto, int cantidadVendida) {
    // 1. Find product ID based on nombreProducto
    int idProducto = buscarIdProductoPorNombre(nombreProducto);

    // Check if product ID was found
    if (idProducto > 0) {
        // 2. Update stock using the retrieved ID
        Conexion conexion = new Conexion();
        String query = "UPDATE producto " +
                       "SET stock = stock + ? " +
                       "WHERE id_producto = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, cantidadVendida);
            statement.setInt(2, idProducto);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    } else {
        // Handle case where product was not found by nombreProducto (optional)
        System.out.println("Producto con nombre " + nombreProducto + " no encontrado.");
    }
}

public int buscarIdProductoPorNombre(String nombreProducto) {
    int idProducto = -1; // Valor predeterminado en caso de no encontrar ningún ID de producto

    // Establecer la conexión a la base de datos y ejecutar la consulta
    Conexion conexion = new Conexion();
    String query = "SELECT id_producto FROM producto WHERE nombre = ?";
    try (Connection conn = conexion.getConnection();
         PreparedStatement statement = conn.prepareStatement(query)) {
        statement.setString(1, nombreProducto);
        ResultSet resultSet = statement.executeQuery();

        // Si se encuentra un ID de producto, asignarlo y salir del bucle
        if (resultSet.next()) {
            idProducto = resultSet.getInt("id_producto");
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    return idProducto;
}



public Reserva obtenerReservaPorId(int idReserva) {
    Reserva reserva = null; // Valor predeterminado en caso de no encontrar ninguna reserva

    // Establecer la conexión a la base de datos y ejecutar la consulta
    Conexion conexion = new Conexion();
    String query = "SELECT * FROM Reserva WHERE id_reserva = ?";
    try (Connection conn = conexion.getConnection();
         PreparedStatement statement = conn.prepareStatement(query)) {
        statement.setInt(1, idReserva);
        ResultSet resultSet = statement.executeQuery();

        // Si se encuentra la reserva, asignarla al objeto Reserva
        if (resultSet.next()) {
            reserva = new Reserva();
            reserva.setId_reserva(resultSet.getInt("id_reserva"));
            reserva.setFechaEntrega(resultSet.getDate("Fecha_entrega"));
            reserva.setAnticipo(resultSet.getDouble("Anticipo"));
            reserva.setEstado(resultSet.getString("Estado"));
            // Puedes agregar más campos si es necesario
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    return reserva;
}

public void actualizarEstadoReservaCancelada(Reserva reserva) {
    // Establecer la conexión a la base de datos y ejecutar la actualización
    Conexion conexion = new Conexion();
    String query = "UPDATE Reserva SET Estado = ? WHERE id_reserva = ?";
    try (Connection conn = conexion.getConnection();
         PreparedStatement statement = conn.prepareStatement(query)) {
        statement.setString(1, "Cancelada");
        statement.setInt(2, reserva.getId_reserva());
        int rowsUpdated = statement.executeUpdate();

        // Verificar si la actualización fue exitosa
        if (rowsUpdated > 0) {
            System.out.println("El estado de la reserva se ha actualizado a 'Entregado' correctamente.");
        } else {
            System.out.println("No se pudo actualizar el estado de la reserva.");
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
}






    
    
}
