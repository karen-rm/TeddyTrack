package controlador;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import modelo.Conexion;
import modelo.DetalleVentas;
import modelo.Pago;
import modelo.Reserva;
import modelo.Venta;
import vista.Inicio;
import vista.PagarConEfectivo;
import vista.Ticket;
import vista.detalleVenta;
import java.text.DecimalFormat;
import java.util.ArrayList;
import vista.Factura;

public class Control_PagarConEfectivo implements ActionListener {

    private PagarConEfectivo viewPago;
    private Pago pago;
    private Venta venta;
    private Reserva reserva;
    private detalleVenta viewDetalleVenta;
    private int idCliente;
    private List<DetalleVentas> detalleVentaLista;
    private Inicio vistaInicio;
    private boolean bandera;
    private int id_venta;
    private int id_reserva;
    private Control_Reservas controlReservas;
    private Ticket recibo;
    private Control_Inicio controlInicio;
    private Factura factura;
    private LocalDate fechaInicial;

    Control_PagarConEfectivo(Control_Inicio controlInicio, PagarConEfectivo viewPago, Venta venta, Pago pago, Reserva reserva, detalleVenta viewDetalleVenta, int idCliente, List<DetalleVentas> detalleVentaLista, Inicio vistaInicio) {
        this.viewPago = viewPago;
        this.venta = venta;
        this.pago = pago;
        this.reserva = reserva;
        this.viewDetalleVenta = viewDetalleVenta;
        this.idCliente = idCliente;
        this.detalleVentaLista = detalleVentaLista;
        this.vistaInicio = vistaInicio;
        this.bandera = false;
        this.controlInicio = controlInicio;

        viewPago.btnPagar.addActionListener(this);
        viewPago.btnRegresar.addActionListener(this);

        viewPago.btnRegresar.setBorderPainted(false);
        viewPago.btnRegresar.setBackground(Color.WHITE);

        mostrarInfoPagoReserva(venta);
        pago.setTipo_pago("Efectivo");
        viewPago.setLocationRelativeTo(null);
        viewPago.jLabelCambio.setVisible(false);
        viewPago.cambio.setVisible(false);

        if (bandera == false) {
            if (vistaInicio.jRadioButtonC.isSelected()) {
                viewPago.jLabelImporte.setVisible(false);
            }
        }
    }

    Control_PagarConEfectivo(Control_Inicio controlInicio, PagarConEfectivo viewPago, Venta venta, Pago pago, detalleVenta viewDetalleVenta, List<DetalleVentas> detalleVentaLista, Inicio vistaInicio) {
        this.viewPago = viewPago;
        this.venta = venta;
        this.pago = pago;
        this.bandera = false;

        this.viewDetalleVenta = viewDetalleVenta;

        this.detalleVentaLista = detalleVentaLista;
        this.vistaInicio = vistaInicio;
        this.controlInicio = controlInicio;

        viewPago.btnRegresar.setBorderPainted(false);
        viewPago.btnRegresar.setBackground(Color.WHITE);

        viewPago.btnPagar.addActionListener(this);
        viewPago.btnRegresar.addActionListener(this);

        mostrarInfoPagoVenta(venta);
        pago.setTipo_pago("Efectivo");
        viewPago.setLocationRelativeTo(null);
        viewPago.jLabelCambio.setVisible(false);
        viewPago.cambio.setVisible(false);

        if (bandera == false) {
            if (vistaInicio.jRadioButtonC.isSelected()) {
                viewPago.jLabelImporte.setVisible(false);
            }
        }

    }

    Control_PagarConEfectivo(PagarConEfectivo vistaPago, int id_venta, int id_reserva, Pago pago, Control_Reservas controlReservas, LocalDate fechaInicial) {
        this.viewPago = vistaPago;

        this.id_venta = id_venta;

        this.venta = obtenerInfoVenta(id_venta);
        this.id_reserva = id_reserva;
        this.pago = pago;
        this.controlReservas = controlReservas;
        this.fechaInicial = fechaInicial;
        this.bandera = true;
        this.detalleVentaLista = obtenerInfoDetalleVenta(id_venta);

        viewPago.btnRegresar.setBorderPainted(false);
        viewPago.btnRegresar.setBackground(Color.WHITE);

        vistaPago.btnPagar.addActionListener(this);
        viewPago.btnRegresar.addActionListener(this);

        mostrarInfoPagoReserva(venta);
        pago.setTipo_pago("Efectivo");
        vistaPago.setLocationRelativeTo(null);
        viewPago.jLabelCambio.setVisible(false);
        viewPago.cambio.setVisible(false);

        if (bandera == false) {
            if (vistaInicio.jRadioButtonC.isSelected()) {
                viewPago.jLabelImporte.setVisible(false);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (e.getSource() == viewPago.btnRegresar) {
            viewPago.dispose();
        } else if (e.getSource() == viewPago.btnPagar) {

            viewPago.btnPagar.disable();

            // Crear y mostrar el diálogo de progreso
            JDialog progressDialog = new JDialog();
            progressDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            progressDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
            progressDialog.setTitle("Procesando");
            progressDialog.setSize(300, 150);
            progressDialog.setLayout(new BorderLayout());
            progressDialog.setLocationRelativeTo(null); // Centrar en la pantalla

            // Etiqueta con el mensaje informativo
            JLabel messageLabel = new JLabel("Espere por favor...");
            messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            progressDialog.add(messageLabel, BorderLayout.CENTER);

            // Barra de progreso
            JProgressBar progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); // Barra de progreso indeterminada
            progressDialog.add(progressBar, BorderLayout.SOUTH);

            // Temporizador para cerrar el diálogo después de 2 segundos
            Timer timer = new Timer(3000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    progressDialog.dispose();
                }
            });
            timer.setRepeats(false); // Para que el temporizador solo se ejecute una vez
            timer.start();

            // Mostrar el diálogo de progreso
            progressDialog.setVisible(true);

            //si es un pago inicial
            if (bandera == false) {
                //y es reserva 
                if (vistaInicio.jRadioButtonR.isSelected()) {
                    mostrarCambioReserva(venta);
                }
                //y es una venta normal
                if (vistaInicio.jRadioButtonC.isSelected()) {
                    mostrarCambioVenta(venta);
                }

                //si se va a completar el pago de una reserva
            } else {

                if (verificarCantidadRecibida(venta)) {
                    pago.setMonto(venta.getTotal() / 2);
                    insertarPago(pago, id_venta);

                    //buscar reserva perteneciente al id_reserva
                    reserva = obtenerReservaPorIdVenta(id_venta);
                    //actualizar bd del estado de reserva 
                    actualizarEstadoReservaEntregado(reserva);

                    generarFacturaPDF();
                    viewPago.dispose();
                    controlReservas.actualizarPanelReservas();
                    //que no se dupliquen vistas pagar 
                }
            }
        }
    }

    public void pagar() {
        int id_venta = insertarVenta(venta);
        insertarPago(pago, id_venta);
        if (vistaInicio.jRadioButtonR.isSelected()) {
            insertarReserva(reserva, id_venta, idCliente);
        }
        insertarDetallesVenta(id_venta, detalleVentaLista);

        for (DetalleVentas detalle : detalleVentaLista) {
            String nombreProducto = detalle.getNombre();
            int cantidadVendida = detalle.getCantidad();

            try {
                // Llamar al método para actualizar el stock del producto
                actualizarStockProducto(nombreProducto, cantidadVendida);

                // Continuar con la inserción del detalle de venta...
            } catch (SQLException ex) {
                Logger.getLogger(Control_detalleVenta.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        generarFacturaPDF();

        viewPago.dispose();
        viewDetalleVenta.dispose();

        controlInicio.limpiarVista();

        // Actualiza la vista para reflejar los cambios
        vistaInicio.revalidate();
        vistaInicio.repaint();

        vistaInicio.setVisible(true);
    }

    public void mostrarInfoPagoReserva(Venta venta) {
        double total = venta.getTotal();
        double importe = total / 2;
        viewPago.ventaTotal.setText(String.valueOf(total));
        viewPago.importe.setVisible(true);
        viewPago.importe.setText(String.valueOf(importe));
    }

    public void mostrarInfoPagoVenta(Venta venta) {
        double total = venta.getTotal();
        pago.setMonto(total);
        viewPago.ventaTotal.setText(String.valueOf(total));
        viewPago.importe.setVisible(false);
    }

    public boolean verificarCantidadRecibida(Venta venta) {
        double total = venta.getTotal();
        double importe = total / 2;

        String cantidadRecibida = viewPago.cantidadRecibida.getText();

        if (cantidadRecibida.isEmpty()) {
            JOptionPane optionPane = new JOptionPane("Ingrese una cantidad.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
            optionPane.setOptions(new Object[]{"Aceptar"});
            JDialog dialog = optionPane.createDialog(viewPago, "Error");
            dialog.setVisible(true);
            return false; // Salir del método si la cantidad recibida está vacía
        }

        double cantidad = Double.parseDouble(cantidadRecibida);
        double cambio = cantidad - importe;

        if (cantidad < importe) {
            // Si la cantidad recibida es insuficiente, mostrar un mensaje de error y retornar false
            JOptionPane optionPane = new JOptionPane("La cantidad no puede ser menor al importe.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
            optionPane.setOptions(new Object[]{"Aceptar"});
            JDialog dialog = optionPane.createDialog(viewPago, "Error");
            dialog.setVisible(true);
            return false;
        } else {
            // Si la cantidad recibida es suficiente, mostrar el cambio y retornar true
            viewPago.cambio.setText(String.valueOf(cambio));
            viewPago.jLabelCambio.setVisible(true);
            viewPago.cambio.setVisible(true);
            viewPago.cantidadRecibida.disable();
            return true;
        }
    }

    public void mostrarCambioReserva(Venta venta) {
        double total = venta.getTotal();
        double importe = total / 2;
        String cantidadRecibida = viewPago.cantidadRecibida.getText();

        if (cantidadRecibida.isEmpty()) {
            JOptionPane optionPane = new JOptionPane("Ingrese una cantidad.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
            optionPane.setOptions(new Object[]{"Aceptar"});
            JDialog dialog = optionPane.createDialog(viewPago, "Error");
            dialog.setVisible(true);
            return; // Salir del método si la cantidad recibida está vacía
        }

        double cantidad = Double.parseDouble(cantidadRecibida);
        double cambio = cantidad - importe;

        if (cantidad < importe) {
            JOptionPane optionPane = new JOptionPane("La cantidad no puede ser menor al importe.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
            optionPane.setOptions(new Object[]{"Aceptar"});
            JDialog dialog = optionPane.createDialog(viewPago, "Error");
            dialog.setVisible(true);
        } else {
            viewPago.cambio.setText(String.valueOf(cambio));
            viewPago.jLabelCambio.setVisible(true);
            viewPago.cambio.setVisible(true);
            viewPago.cantidadRecibida.disable();
            pagar();
        }
    }

    public void mostrarCambioVenta(Venta venta) {
        double total = venta.getTotal();
        String cantidadRecibida = viewPago.cantidadRecibida.getText();

        if (cantidadRecibida.isEmpty()) {
            JOptionPane optionPane = new JOptionPane("Ingrese una cantidad.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
            optionPane.setOptions(new Object[]{"Aceptar"});
            JDialog dialog = optionPane.createDialog(viewPago, "Error");
            dialog.setVisible(true);
            return; // Salir del método si la cantidad recibida está vacía
        }

        double cantidad = Double.parseDouble(cantidadRecibida);
        double cambio = cantidad - total;
        DecimalFormat df = new DecimalFormat("#.##"); // Patrón para mostrar solo dos decimales
        String cambioFormateado = df.format(cambio);

        if (cantidad < total) {
            JOptionPane optionPane = new JOptionPane("La cantidad no es válida.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
            optionPane.setOptions(new Object[]{"Aceptar"});
            JDialog dialog = optionPane.createDialog(viewPago, "Error");
            dialog.setVisible(true);
        } else {
            viewPago.cambio.setText(String.valueOf(cambioFormateado));
            viewPago.jLabelCambio.setVisible(true);
            viewPago.cambio.setVisible(true);
            viewPago.cantidadRecibida.disable();

            pagar();
        }
    }

    public int insertarVenta(Venta venta) {
        Conexion conexion = new Conexion();
        String insertQuery = "INSERT INTO Venta (fecha, es_reserva, total) VALUES (?, ?, ?)";

        int idVenta = -1;

        try (Connection conn = conexion.getConnection(); PreparedStatement statement = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {

            // Establecer los valores de los parámetros utilizando los métodos getter de la clase Venta
            statement.setDate(1, Date.valueOf(venta.getFecha()));
            statement.setBoolean(2, venta.getEs_reserva());
            statement.setDouble(3, venta.getTotal());

            // Ejecutar la consulta SQL
            int filasInsertadas = statement.executeUpdate();

            // Verificar si se insertó correctamente
            if (filasInsertadas > 0) {

                // Obtener el ID generado por la base de datos
                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    idVenta = rs.getInt(1); // Obtener el ID de la primera columna generada
                }

                JOptionPane optionPane = new JOptionPane("Venta registrada correctamente.", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
                optionPane.setOptions(new Object[]{"Aceptar"});
                JDialog dialog = optionPane.createDialog("Registro exitoso");
                dialog.setVisible(true);
            } else {
                JOptionPane optionPane = new JOptionPane("Error al registrar la venta.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
                optionPane.setOptions(new Object[]{"Aceptar"});
                JDialog dialog = optionPane.createDialog("Error");
                dialog.setVisible(true);
            }

        } catch (SQLException ex) {
            JOptionPane optionPane = new JOptionPane("Error al conectarse a la base de datos: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
            optionPane.setOptions(new Object[]{"Aceptar"});
            JDialog dialog = optionPane.createDialog("Error");
            dialog.setVisible(true);
            ex.printStackTrace();
        }

        return idVenta; // Devolver el ID de la venta
    }

    public int insertarPago(Pago pago, int idVenta) {
        Conexion conexion = new Conexion();
        String insertQuery = "INSERT INTO pago (monto, tipo_pago, id_venta) VALUES (?, ?, ?)";

        int idPago = -1; // Inicializar el ID de pago como -1 (valor predeterminado)

        try (Connection conn = conexion.getConnection(); PreparedStatement statement = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {

            // Establecer los valores de los parámetros utilizando los métodos getter de la clase Pago
            statement.setDouble(1, pago.getMonto());
            statement.setString(2, pago.getTipo_pago());
            statement.setInt(3, idVenta); // Vincular el pago con la venta usando el ID de venta proporcionado

            // Ejecutar la consulta SQL
            int filasInsertadas = statement.executeUpdate();

            // Verificar si se insertó correctamente
            if (filasInsertadas > 0) {
                // Obtener el ID generado por la base de datos
                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    idPago = rs.getInt(1); // Obtener el ID de la primera columna generada
                }

                JOptionPane optionPane = new JOptionPane("Pago registrado correctamente.", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
                optionPane.setOptions(new Object[]{"Aceptar"});
                JDialog dialog = optionPane.createDialog("Registro exitoso");
                dialog.setVisible(true);
            } else {
                JOptionPane optionPane = new JOptionPane("Error al registrar el pago.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
                optionPane.setOptions(new Object[]{"Aceptar"});
                JDialog dialog = optionPane.createDialog("Error");
                dialog.setVisible(true);
            }

        } catch (SQLException ex) {
            JOptionPane optionPane = new JOptionPane("Error al conectarse a la base de datos: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
            optionPane.setOptions(new Object[]{"Aceptar"});
            JDialog dialog = optionPane.createDialog("Error");
            dialog.setVisible(true);
            ex.printStackTrace();
        }

        return idPago; // Devolver el ID del pago
    }

    public int insertarReserva(Reserva reserva, int idVenta, int idCliente) {
        Conexion conexion = new Conexion();
        String insertQuery = "INSERT INTO Reserva (id_venta, Fecha_entrega, Anticipo, Estado, id_cliente) VALUES (?, ?, ?, ?, ?)";
        int idReserva = -1; // Inicializar el ID de la reserva como -1 (valor predeterminado)

        try (Connection conn = conexion.getConnection(); PreparedStatement statement = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {

            // Establecer los valores de los parámetros utilizando los métodos getter de la clase Reserva
            statement.setInt(1, idVenta);
            statement.setDate(2, reserva.getFechaEntrega());
            statement.setDouble(3, reserva.getAnticipo());
            statement.setString(4, reserva.getEstado());
            statement.setInt(5, idCliente);

            // Ejecutar la consulta SQL
            int filasInsertadas = statement.executeUpdate();

            // Verificar si se insertó correctamente
            if (filasInsertadas > 0) {
                // Obtener el ID generado por la base de datos
                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    idReserva = rs.getInt(1); // Obtener el ID de la primera columna generada
                }

                JOptionPane optionPane = new JOptionPane("Reserva registrada correctamente.", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
                optionPane.setOptions(new Object[]{"Aceptar"});
                JDialog dialog = optionPane.createDialog("Registro exitoso");
                dialog.setVisible(true);
            } else {
                JOptionPane optionPane = new JOptionPane("Error al registrar reserva.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
                optionPane.setOptions(new Object[]{"Aceptar"});
                JDialog dialog = optionPane.createDialog("Error");
                dialog.setVisible(true);
            }

        } catch (SQLException ex) {
            JOptionPane optionPane = new JOptionPane("Error al conectarse a la base de datos: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
            optionPane.setOptions(new Object[]{"Aceptar"});
            JDialog dialog = optionPane.createDialog("Error");
            dialog.setVisible(true);
            ex.printStackTrace();
        }

        return idReserva; // Devolver el ID de la reserva
    }

    public void insertarDetallesVenta(int idVenta, List<DetalleVentas> detalleVentaLista) {
        Conexion conexion = new Conexion();
        String insertQuery = "INSERT INTO DetalleVenta (id_venta, id_producto, cantidad_vendida, precio_unitario) VALUES (?, ?, ?, ?)";

        try (Connection conn = conexion.getConnection(); PreparedStatement statement = conn.prepareStatement(insertQuery)) {

            for (DetalleVentas detalle : detalleVentaLista) {
                String nombreProducto = detalle.getNombre();
                double precioUnitario = detalle.getPrecio();
                int cantidadVendida = detalle.getCantidad();

                // Consulta SQL para obtener el ID del producto
                String queryBuscarIdProducto = "SELECT id_producto FROM Producto WHERE nombre = ?";
                try (PreparedStatement statementIdProducto = conn.prepareStatement(queryBuscarIdProducto)) {
                    statementIdProducto.setString(1, nombreProducto);
                    ResultSet rs = statementIdProducto.executeQuery();
                    if (rs.next()) {
                        int idProducto = rs.getInt("id_producto");

                        // Establecer los valores de los parámetros
                        statement.setInt(1, idVenta);
                        statement.setInt(2, idProducto);
                        statement.setInt(3, cantidadVendida);
                        statement.setDouble(4, precioUnitario);

                        // Ejecutar la consulta SQL para insertar cada detalle de venta
                        statement.executeUpdate();
                    } else {
                        System.out.println("No se encontró el ID para el producto: " + nombreProducto);
                    }
                }
            }

            JOptionPane optionPane = new JOptionPane("Detalles de venta registrados correctamente.", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
            optionPane.setOptions(new Object[]{"Aceptar"});
            JDialog dialog = optionPane.createDialog("Registro exitoso");
            dialog.setVisible(true);

        } catch (SQLException ex) {
            JOptionPane optionPane = new JOptionPane("Error al conectarse a la base de datos: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
            optionPane.setOptions(new Object[]{"Aceptar"});
            JDialog dialog = optionPane.createDialog("Error");
            dialog.setVisible(true);
            ex.printStackTrace();
        }
    }

    public void actualizarStockProducto(String nombreProducto, int cantidadVendida) throws SQLException {
        Conexion conexion = new Conexion();
        // Consulta SQL para obtener el ID y el stock actual del producto
        String queryBuscarIdYStock = "SELECT id_producto, stock FROM Producto WHERE nombre = ?";

        try (Connection conn = conexion.getConnection(); PreparedStatement statementIdYStock = conn.prepareStatement(queryBuscarIdYStock)) {
            statementIdYStock.setString(1, nombreProducto);
            ResultSet rs = statementIdYStock.executeQuery();
            if (rs.next()) {
                int idProducto = rs.getInt("id_producto");
                int stockActual = rs.getInt("stock");

                // Calcular el nuevo stock después de la venta
                int nuevoStock = stockActual - cantidadVendida;

                // Actualizar el stock en la tabla Producto
                String queryActualizarStock = "UPDATE Producto SET stock = ? WHERE id_producto = ?";
                try (PreparedStatement statementActualizarStock = conn.prepareStatement(queryActualizarStock)) {
                    statementActualizarStock.setInt(1, nuevoStock);
                    statementActualizarStock.setInt(2, idProducto);
                    statementActualizarStock.executeUpdate();
                } catch (SQLException ex) {
                    System.out.println("Error al actualizar el stock del producto: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                System.out.println("No se encontró el producto en la base de datos: " + nombreProducto);
            }
        } catch (SQLException ex) {
            System.out.println("Error al buscar el ID y el stock del producto: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public Reserva obtenerReservaPorIdVenta(int idVenta) {
        Reserva reserva = null;

        // Establecer la conexión a la base de datos y ejecutar la consulta
        Conexion conexion = new Conexion();
        String query = "SELECT id_reserva, Fecha_entrega, Anticipo, Estado "
                + "FROM Reserva "
                + "WHERE id_venta = ?";
        try (Connection conn = conexion.getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, idVenta);
            ResultSet resultSet = statement.executeQuery();

            // Si se encuentra la reserva, asignar los datos a un objeto Reserva
            if (resultSet.next()) {
                reserva = new Reserva();
                reserva.setId_reserva(resultSet.getInt("id_reserva"));
                reserva.setFechaEntrega(resultSet.getDate("Fecha_entrega"));
                reserva.setAnticipo(resultSet.getDouble("Anticipo"));
                reserva.setEstado(resultSet.getString("Estado"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return reserva;
    }

    public void actualizarEstadoReservaEntregado(Reserva reserva) {
        // Establecer la conexión a la base de datos y ejecutar la actualización
        Conexion conexion = new Conexion();
        String query = "UPDATE Reserva SET Estado = ? WHERE id_reserva = ?";
        try (Connection conn = conexion.getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, "Entregado");
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

    public Venta obtenerInfoVenta(int idVenta) {

        // Establecer la conexión a la base de datos y ejecutar la consulta
        Conexion conexion = new Conexion();
        String query = "SELECT fecha, total FROM Venta WHERE id_venta = ?";
        try (Connection conn = conexion.getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
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

    public void imprimirTicket() {
        // Crear un cuadro de diálogo de opción con botones "Sí" y "No"
        JOptionPane optionPane = new JOptionPane(
                "¿Desea imprimir el ticket?",
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION
        );

        // Cambiar el texto de los botones a "Sí" y "No"
        optionPane.setOptions(new Object[]{"Sí", "No"});

        // Crear el diálogo con el cuadro de diálogo de opción
        JDialog dialog = optionPane.createDialog(viewPago, "Confirmación");

        // Mostrar el diálogo
        dialog.setVisible(true);

        // Obtener la opción seleccionada por el usuario
        Object selectedValue = optionPane.getValue();

        // Verificar la opción seleccionada
        if (selectedValue.equals("Sí")) {
            this.recibo = new Ticket();

            String modalidad = " ";

            if (bandera == false) {
                if (vistaInicio.jRadioButtonC.isSelected()) {
                    modalidad = "Venta";
                    Control_Recibo ctrlRecibo = new Control_Recibo(recibo, detalleVentaLista, modalidad);
                } else if (vistaInicio.jRadioButtonR.isSelected()) {
                    Date fechaEntrega = reserva.getFechaEntrega();
                    modalidad = "Reserva";
                    Control_Recibo ctrlRecibo = new Control_Recibo(recibo, detalleVentaLista, modalidad, fechaEntrega);
                }
            } else {
                modalidad = "Reserva2";
                Control_Recibo ctrlRecibo = new Control_Recibo(recibo, detalleVentaLista, modalidad);
            }

        } else if (selectedValue.equals("No")) {
            // El usuario seleccionó "No", hacer algo más o simplemente cerrar el diálogo
            JOptionPane optionPane1 = new JOptionPane("Venta finalizada exitosamente.", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
            optionPane1.setOptions(new Object[]{"Aceptar"});
            JDialog dialog1 = optionPane1.createDialog("Venta finalizada");
            dialog1.setVisible(true);

        }

    }

    public void generarFacturaPDF() {

        viewPago.btnPagar.disable();

        // Crear un cuadro de diálogo de opción con botones "Sí" y "No"
        JOptionPane optionPane = new JOptionPane(
                "¿Desea imprimir la factura?",
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION
        );

        // Cambiar el texto de los botones a "Sí" y "No"
        optionPane.setOptions(new Object[]{"Sí", "No"});

        // Crear el diálogo con el cuadro de diálogo de opción
        JDialog dialog = optionPane.createDialog(viewPago, "Confirmación");

        // Mostrar el diálogo
        dialog.setVisible(true);

        // Obtener la opción seleccionada por el usuario
        Object selectedValue = optionPane.getValue();

        // Verificar la opción seleccionada
        if (selectedValue.equals("Sí")) {
            this.factura = new Factura();
            double total = venta.getTotal();
            String tipoPago = pago.getTipo_pago();
            //Controlador_Factura ctrlFactura = new Controlador_Factura(factura, detalleVentaLista, total, tipoPago, vistaInicio); 

            String modalidadVenta = "Venta";
            String modalidadReserva = "Reserva";
            String modalidadCompletarPago = "Reserva2";
            if (bandera == false) {
                if (vistaInicio.jRadioButtonC.isSelected()) {
                    Controlador_Factura ctrlFactura = new Controlador_Factura(factura, detalleVentaLista, total, tipoPago, modalidadVenta);
                } else if (vistaInicio.jRadioButtonR.isSelected()) {
                    Date fechaEntrega = reserva.getFechaEntrega();
                    Controlador_Factura ctrlFactura = new Controlador_Factura(factura, detalleVentaLista, total, tipoPago, modalidadReserva, fechaEntrega);
                }
            } else {
                Controlador_Factura ctrlFactura = new Controlador_Factura(factura, detalleVentaLista, total, tipoPago, modalidadCompletarPago, fechaInicial);
            }

        } else if (selectedValue.equals("No")) {
            imprimirTicket();
        }
    }

    public List<DetalleVentas> obtenerInfoDetalleVenta(int idVenta) {
        List<DetalleVentas> detallesVenta = new ArrayList<>();

        // Establecer la conexión a la base de datos y ejecutar la consulta
        Conexion conexion = new Conexion();
        String query = "SELECT cantidad_vendida, precio_unitario, id_producto "
                + "FROM detalleVenta "
                + "WHERE id_venta = ?";
        try (Connection conn = conexion.getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
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
        try (Connection conn = conexion.getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
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

}
