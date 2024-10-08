package controlador;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import vista.detalleVenta;
import modelo.DetalleVentas;
import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import modelo.Cliente;
import modelo.Conexion;
import modelo.Pago;
import modelo.Reserva;
import modelo.Venta;
import vista.Inicio;
import vista.PagarConEfectivo;
import vista.PagarConTarjeta;

public class Control_detalleVenta implements ActionListener {

    private detalleVenta vista;
    private List<JPanel> listaProductos;
    private Inicio vistaInicio;
    private Venta venta;
    private Pago pago;
    private PagarConEfectivo viewPago;
    private PagarConTarjeta viewPagoTarjeta;
    private Reserva reserva;
    List<DetalleVentas> detalleVentaLista = new ArrayList<>();
    private boolean registro_Cliente;
    private Control_Inicio controlInicio;

    public Control_detalleVenta() {

    }

    public Control_detalleVenta(Control_Inicio controlInicio, Inicio vistaInicio, detalleVenta vista, List<JPanel> listaProductos, Venta venta) {
        this.vistaInicio = vistaInicio;
        this.vista = vista;
        this.venta = venta;
        this.pago = new Pago();
        this.viewPago = new PagarConEfectivo();
        this.controlInicio = controlInicio;
        this.viewPagoTarjeta = new PagarConTarjeta();
        this.registro_Cliente = false;
        this.listaProductos = listaProductos;
        this.vista.btnRegresar.addActionListener(this);
        this.vista.btnAceptar.addActionListener(this);

        this.vista.btnContinuar.addActionListener(this);
        this.vista.btnTarjeta.addActionListener(this);

        // Mostrar los productos en jPanelDetalleVenta
        mostrarProductos();

        LocalDate fecha = venta.getFecha();
        String fechaString = fecha.toString();
        vista.jLabel10.setText(fechaString);

        this.vista.btnRegresar.setBorderPainted(false);
        this.vista.btnRegresar.setBackground(Color.WHITE);

    }

    public void mostrarProductos() {
        JPanel jPanelDetalleVenta = vista.jPanelDetalleVenta;
        jPanelDetalleVenta.removeAll(); // Limpiar el panel antes de agregar nuevos componentes

        // Establecer un layout para el panel
        jPanelDetalleVenta.setLayout(new BoxLayout(jPanelDetalleVenta, BoxLayout.Y_AXIS));

        // Lista para almacenar los objetos detalleVenta
        //List<DetalleVentas> detalleVentaLista = new ArrayList<>();
        double totalVenta = 0.0;

        // Agregar cada producto al panel y a la lista de detalleVenta
        for (JPanel panelProducto : listaProductos) {
            // Obtener el nombre del producto
            JLabel nombreLabel = (JLabel) panelProducto.getComponent(0);
            String nombre = nombreLabel.getText();

            // Obtener el precio del producto
            JLabel precioLabel = (JLabel) panelProducto.getComponent(1);
            String precioStr = precioLabel.getText();
            double precio = Double.parseDouble(precioStr.substring(precioStr.indexOf(":") + 1).trim());

            // Obtener la cantidad del producto del JSpinner
            JSpinner cantidadSpinner = (JSpinner) panelProducto.getComponent(2);
            int cantidad = (int) cantidadSpinner.getValue();

            // Calcular el total para este producto
            double totalProducto = precio * cantidad;
            totalVenta += totalProducto; // Agregar al total de la venta

            // Crear un nuevo objeto detalleVenta con los datos del producto
            DetalleVentas producto = new DetalleVentas(nombre, precio, cantidad);
            detalleVentaLista.add(producto);

            // Crear un nuevo JLabel para mostrar el detalle del producto
            String detalleStr = producto.mostrarDetalle();
            JLabel detalleLabel = new JLabel(detalleStr);

            // Crear un nuevo JPanel para cada producto y agregar el JLabel
            JPanel productoPanel = new JPanel();
            productoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            productoPanel.add(detalleLabel);

            // Agregar el panel del producto al panel principal
            jPanelDetalleVenta.add(productoPanel);
            jPanelDetalleVenta.add(Box.createVerticalStrut(10)); // Espacio entre productos 

        }

        // Crear un objeto DecimalFormat para formatear el total a dos decimales
        DecimalFormat df = new DecimalFormat("#.00");

        // Formatear el total con dos decimales
        String totalFormateado = df.format(totalVenta);

        // Mostrar el total de la venta
        JLabel totalLabel = new JLabel("Total: $" + totalFormateado);
        jPanelDetalleVenta.add(totalLabel);

        //pago.setMonto(totalVenta);
        venta.setTotal(totalVenta);
        // Hacer visible el panel
        jPanelDetalleVenta.revalidate();
        jPanelDetalleVenta.repaint();

        // Imprimir los detalles de venta en la consola (puedes quitar esto si no es necesario)
        for (DetalleVentas detalle : detalleVentaLista) {
            String nombre = detalle.getNombre();
            double precioUnitario = detalle.getPrecio();
            int cantidad = detalle.getCantidad();

            System.out.println("- " + nombre + " -> " + precioUnitario + " -> " + cantidad);
        }

    }

    int idCliente = -1;

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();

        if (source == vista.btnRegresar) {
            vista.dispose();
            vistaInicio.setVisible(true);

        } else if (source == vista.btnAceptar) {

            String nombre = vista.nombreCliente.getText();
            String apellido = vista.apellidoCliente.getText();
            String telefono = vista.telefonoCliente.getText();
            java.util.Date fechaUtil = vista.jDateChooser1.getDate();

            if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() || fechaUtil == null) {
                JOptionPane optionPane = new JOptionPane("Debe ingresar todos los datos del cliente para proceder con la reserva.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
                optionPane.setOptions(new Object[]{"Aceptar"});
                JDialog dialog = optionPane.createDialog("Error");
                dialog.setVisible(true);

            } else if (telefono.length() != 10 || !telefono.matches("\\d{10}")) {
                UIManager.put("OptionPane.okButtonText", "Aceptar");
                JOptionPane.showMessageDialog(null, "El número de teléfono debe tener exactamente 10 dígitos.", "Formato inválido", JOptionPane.ERROR_MESSAGE);
            } else {
                Cliente cliente = new Cliente(nombre, apellido, telefono);
                this.reserva = new Reserva();
                double ventaTotal = venta.getTotal();
                reserva.setTotal(ventaTotal);
                double anticipo = ventaTotal / 2;
                reserva.setAnticipo(anticipo);
                pago.setMonto(anticipo);

                java.sql.Date fechaSql = new java.sql.Date(fechaUtil.getTime());

                JOptionPane optionPane = new JOptionPane(
                        "Por favor, verifique los datos del cliente:\n"
                        + "Nombre: " + nombre + "\n"
                        + "Apellido: " + apellido + "\n"
                        + "Telefono: " + telefono + "\n"
                        + "Fecha entrega: " + fechaSql,
                        JOptionPane.INFORMATION_MESSAGE,
                        JOptionPane.OK_CANCEL_OPTION,
                        null,
                        null,
                        null);

                // Cambiar el nombre de los botones a español
                optionPane.setOptions(new Object[]{"Aceptar", "Cancelar"});
                JDialog dialog = optionPane.createDialog("Verificación");
                dialog.setVisible(true);

                Object selectedValue = optionPane.getValue();
                if (selectedValue.equals("Aceptar")) {
                    idCliente = registrarCliente(cliente);
                    reserva.setFechaEntrega(fechaSql);
                    vista.nombreCliente.setEnabled(false);
                    vista.apellidoCliente.setEnabled(false);
                    vista.telefonoCliente.setEnabled(false);
                    vista.jDateChooser1.setEnabled(false);
                } else if (selectedValue.equals("Cancelar")) {
                    optionPane.setValue(JOptionPane.CLOSED_OPTION);
                } else {
                    optionPane.setValue(JOptionPane.CLOSED_OPTION);
                }
            }

        } else if (source == vista.btnContinuar) {

            if (vistaInicio.jRadioButtonC.isSelected()) {
                viewPago.setVisible(true);
                Control_PagarConEfectivo ctrlPagarVenta = new Control_PagarConEfectivo(controlInicio, viewPago, venta, pago, vista, detalleVentaLista, vistaInicio);

            } else if (vistaInicio.jRadioButtonR.isSelected()) {
                if (this.registro_Cliente == true) {
                    viewPago.setVisible(true);
                    Control_PagarConEfectivo ctrlPagarReserva = new Control_PagarConEfectivo(controlInicio, viewPago, venta, pago, reserva, vista, idCliente, detalleVentaLista, vistaInicio);
                } else {
                    JOptionPane optionPane = new JOptionPane("Primero debes registrar a un cliente.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
                    optionPane.setOptions(new Object[]{"Aceptar"});
                    JDialog dialog = optionPane.createDialog("Error");
                    dialog.setVisible(true);
                }
            }

        } else if (source == vista.btnTarjeta) {

            if (vistaInicio.jRadioButtonC.isSelected()) {
                viewPagoTarjeta.setVisible(true);
                Control_PagarConTarjeta ctrlPagarVenta = new Control_PagarConTarjeta(controlInicio, viewPagoTarjeta, venta, pago, vista, detalleVentaLista, vistaInicio);

            } else if (vistaInicio.jRadioButtonR.isSelected()) {
                if (this.registro_Cliente == true) {
                    viewPagoTarjeta.setVisible(true);
                    Control_PagarConTarjeta ctrlPagarReserva = new Control_PagarConTarjeta(controlInicio, viewPagoTarjeta, venta, pago, reserva, vista, idCliente, detalleVentaLista, vistaInicio);
                } else {
                    JOptionPane optionPane = new JOptionPane("Primero debes registrar a un cliente.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
                    optionPane.setOptions(new Object[]{"Aceptar"});
                    JDialog dialog = optionPane.createDialog("Error");
                    dialog.setVisible(true);
                }
            }

        }

    }

    //!nombre.isEmpty()
    public int registrarCliente(Cliente cliente) {
        Conexion conexion = new Conexion();
        String insertQuery = "INSERT INTO Cliente (Nombre, Apellido, Telefono) VALUES (?, ?, ?)";
        int idCliente = -1; // Inicializar el ID del cliente como -1 (valor predeterminado)

        try (Connection conn = conexion.getConnection(); PreparedStatement statement = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {

            // Establecer los valores de los parámetros utilizando los métodos getter de la clase Cliente
            statement.setString(1, cliente.getNombre());
            statement.setString(2, cliente.getApellido());
            statement.setString(3, cliente.getTelefono());

            // Ejecutar la consulta SQL
            int filasInsertadas = statement.executeUpdate();

            // Verificar si se insertó correctamente
            if (filasInsertadas > 0) {
                // Obtener el ID generado por la base de datos
                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    idCliente = rs.getInt(1); // Obtener el ID de la primera columna generada
                }

                JOptionPane optionPane = new JOptionPane("Cliente registrado correctamente.", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
                optionPane.setOptions(new Object[]{"Aceptar"});
                JDialog dialog = optionPane.createDialog("Registro exitoso");
                dialog.setVisible(true);
                this.registro_Cliente = true;
            } else {
                JOptionPane optionPane = new JOptionPane("Error al registrar cliente.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
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

        return idCliente; // Devolver el ID del cliente
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

    
}