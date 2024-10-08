package controlador;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import modelo.Conexion;
import modelo.Producto;
import modelo.Venta;
import vista.GestionReservas;
import vista.HistorialReservas;
import vista.HistorialVentas;
import vista.Inicio;
import vista.Login;
import vista.detalleVenta;
import vista.solicitaContraseña;

public class Control_Inicio implements ActionListener {

    private Inicio vista;
    private List<JPanel> listaProductos;
    private Login login;

    public Control_Inicio(Login login) {
        this.vista = new Inicio();
        this.listaProductos = new ArrayList<>();

        // Establecer los comandos de acción para los elementos del menú
        this.vista.accederInventario.setActionCommand("accederInventario");
        this.vista.opcInventario.setActionCommand("Inventario");
        this.vista.opcHistorialVenta.setActionCommand("opcHistorialVenta");
        this.vista.opcHistorialReservas.setActionCommand("opcHistorialReservas");

        this.vista.verReservas.setActionCommand("verReservas");
        this.login = login;
        // Agregar ActionListener a los elementos del menú
        this.vista.accederInventario.addActionListener(this);
        this.vista.opcInventario.addActionListener(this);
        this.vista.verReservas.addActionListener(this);
        this.vista.opcHistorialVenta.addActionListener(this);
        this.vista.opcHistorialReservas.addActionListener(this);

        //Agregar los ActionListener a los elementos de la interfaz 
        this.vista.barraBusqueda.addActionListener(this);
        this.vista.btnBuscar.addActionListener(this);
        this.vista.btnBuscar.setActionCommand("Buscar"); // Agregar comando de acción al botón btnBuscar 
        this.vista.btnAceptar.addActionListener(this);
        this.vista.btnAceptar.setActionCommand("Aceptar");
        this.vista.btnLimpiar.addActionListener(this);
        this.vista.btnLimpiar.setActionCommand("Limpiar");
        this.vista.btnSalir.addActionListener(this);
        this.vista.btnSalir.setActionCommand("Salir");

        // Agregar el ListSelectionListener a la tablaInicio para detectar selecciones de fila
        this.vista.tablaInicio.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    // Seleccionar la fila seleccionada
                    int selectedRow = vista.tablaInicio.getSelectedRow();
                    if (selectedRow != -1) {
                        // Obtener los datos de la fila seleccionada
                        String nombre = (String) vista.tablaInicio.getValueAt(selectedRow, 1); // Columna del nombre
                        double precio = (double) vista.tablaInicio.getValueAt(selectedRow, 3); // Columna del precio

                        // Obtener el stock del producto seleccionado
                        int stockProducto = obtenerStockProducto(nombre);

                        // Verificar si hay suficiente stock
                        if (stockProducto > 0) {
                            // Crear los componentes para mostrar el nombre y el precio
                            JLabel nombreLabel = new JLabel(nombre + "  ");
                            JLabel precioLabel = new JLabel("Precio: " + precio + "  ");
                            SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, stockProducto, 1);

                            // Crear el JSpinner con el SpinnerNumberModel
                            JSpinner cantidadSpinner = new JSpinner(spinnerModel);
                            // Establecer un tamaño preferido para el JSpinner
                            Dimension spinnerSize = cantidadSpinner.getPreferredSize();
                            spinnerSize.width = 40; // Establecer el ancho deseado
                            cantidadSpinner.setPreferredSize(spinnerSize);
                            cantidadSpinner.addChangeListener(new ChangeListener() {
                                @Override
                                public void stateChanged(ChangeEvent e) {
                                    // Recalcular el total cada vez que cambie el valor del JSpinner
                                    calcularTotal(vista.jScrollPane2, vista.total);
                                }
                            });
                            JButton eliminarButton = new JButton("Eliminar");

                            // Configurar el ActionListener para el botón de eliminación
                            eliminarButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    // Eliminar el producto del JScrollPane
                                    eliminarProducto(nombreLabel, precioLabel, cantidadSpinner, eliminarButton);
                                }
                            });

                            // Crear un JPanel para contener los componentes
                            JPanel panelProducto = new JPanel(new FlowLayout(FlowLayout.LEFT));
                            panelProducto.add(nombreLabel);
                            panelProducto.add(precioLabel);
                            panelProducto.add(cantidadSpinner);
                            panelProducto.add(eliminarButton);

                            // Agregar el JPanel a la lista de productos y al JScrollPane
                            listaProductos.add(panelProducto);
                            actualizarPanelScroll();
                        } else {
                            // Mostrar mensaje de error si no hay stock disponible
                            JOptionPane optionPane = new JOptionPane("No hay stock disponible de ese producto", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
                            optionPane.setOptions(new Object[]{"Aceptar"});
                            JDialog dialog = optionPane.createDialog(vista, "Error");
                            dialog.setVisible(true);
                        }
                    }
                }
            }
        });

        //Agrupar radio buttons 
        ButtonGroup group = new ButtonGroup();
        group.add(vista.jRadioButtonC);
        group.add(vista.jRadioButtonR);

        //cargarPeluches en tabla 
        cargarInicialmenteProductosEnTabla();

        //vista.btnAceptar.setBorderPainted(false);
        vista.btnBuscar.setBackground(Color.WHITE);
        vista.btnLimpiar.setBackground(Color.WHITE);
        vista.btnSalir.setBackground(Color.WHITE);
        vista.setVisible(true);
        vista.setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "accederInventario":
                verificarIdentidad();

                break;
            case "Buscar":
                buscarProductos();
                ajustarAnchoColumnas(vista.tablaInicio);
                break;
            case "Aceptar":

                // Verificar si el panel de venta está vacío
                if (listaProductos.isEmpty()) {
                    JOptionPane optionPane = new JOptionPane("Debe añadir productos a la venta.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
                    optionPane.setOptions(new Object[]{"Aceptar"});
                    JDialog dialog = optionPane.createDialog(vista, "Error");
                    dialog.setVisible(true);
                    break;
                }

                Venta ventaEnCurso = new Venta();
                // Verificar si se ha seleccionado el radio button correcto

                if (vista.jRadioButtonC.isSelected()) {
                    // Mostrar la interfaz DetalleVenta y pasar los productos seleccionados
                    ventaEnCurso.setEs_reserva(false);
                    mostrarDetalleVenta(ventaEnCurso);

                } else if (vista.jRadioButtonR.isSelected()) {

                    ventaEnCurso.setEs_reserva(true);
                    mostrarRegistroCliente(ventaEnCurso);
                } else {

                    JOptionPane optionPane = new JOptionPane("Por favor seleccione una opción.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
                    optionPane.setOptions(new Object[]{"Aceptar"});
                    JDialog dialog = optionPane.createDialog(vista, "Error");
                    dialog.setVisible(true);
                }
                break;
            case "Limpiar":
                limpiarVista();
                break;
            case "verReservas":
                System.out.println("modulo reservas ");
                GestionReservas viewReservas = new GestionReservas();
                vista.setVisible(false);
                viewReservas.setVisible(true);
                viewReservas.setLocationRelativeTo(null);
                Control_Reservas ctrlReservas = new Control_Reservas(vista, viewReservas);
                break;
            case "opcHistorialVenta":
                System.out.println("historial venta :)");
                HistorialVentas viewHistorialVentas = new HistorialVentas();
                vista.setVisible(false);
                viewHistorialVentas.setVisible(true);
                Control_HistorialVentas ctrlHisVentas = new Control_HistorialVentas(vista, viewHistorialVentas);
                break;
            case "opcHistorialReservas":
                System.out.println("historial reserva :)");
                HistorialReservas vistaHistorialReservas = new HistorialReservas();
                vista.setVisible(false);
                vistaHistorialReservas.setVisible(true);
                Control_HistorialReservas ctrlHisReservas = new Control_HistorialReservas(vista, vistaHistorialReservas);
                break;
            case "Salir":
                vista.dispose();
                login.setVisible(true);
                break;
        }

    }

    public void verificarIdentidad() {
        solicitaContraseña dialog = new solicitaContraseña((JFrame) vista, true);
        dialog.setLocationRelativeTo((JFrame) vista);
        Control_Dialogo DialogCtrl = new Control_Dialogo(dialog, vista);
    }

    public void buscarProductos() {
        // Obtener el texto ingresado en la barra de búsqueda
        String parametroBusqueda = vista.barraBusqueda.getText().trim();

        // Verificar si se ingresó un parámetro de búsqueda válido
        if (parametroBusqueda.isEmpty()) {
            JOptionPane optionPane = new JOptionPane("Por favor ingrese un término de búsqueda válido.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
            optionPane.setOptions(new Object[]{"Aceptar"});
            JDialog dialog = optionPane.createDialog(vista, "Error");
            dialog.setVisible(true);
            return;
        }

        Conexion conexion = new Conexion();
        // Consulta SQL para buscar productos por nombre o categoría
        String query = "SELECT p.id_producto, p.nombre, p.descripcion, p.precio_por_unidad, p.stock "
                + "FROM Producto p "
                + "INNER JOIN Categoria c ON p.id_categoria = c.id_categoria "
                + "WHERE p.nombre LIKE ? OR c.nombre_categoria LIKE ?";
        try (Connection conn = conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            // Establecer los parámetros de la consulta preparada
            ps.setString(1, "%" + parametroBusqueda + "%");
            ps.setString(2, "%" + parametroBusqueda + "%");
            // Ejecutar la consulta
            ResultSet rs = ps.executeQuery();
            // Crear un modelo de tabla para mostrar los resultados
            DefaultTableModel model = (DefaultTableModel) vista.tablaInicio.getModel();
            // Limpiar la tabla antes de agregar nuevos datos
            model.setRowCount(0);
            // Llenar el modelo con los resultados de la consulta
            while (rs.next()) {
                Object[] fila = new Object[5];
                fila[0] = rs.getInt("id_producto");
                fila[1] = rs.getString("nombre");
                fila[2] = rs.getString("descripcion");
                fila[3] = rs.getDouble("precio_por_unidad");
                fila[4] = rs.getInt("stock");
                model.addRow(fila);
            }
        } catch (SQLException ex) {
            UIManager.put("OptionPane.okButtonText", "Aceptar");
            JOptionPane.showMessageDialog(vista, "Error al buscar productos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ajustarAnchoColumnas(JTable table) {
        JTableHeader header = table.getTableHeader();
        TableColumnModel columnModel = table.getColumnModel();
        TableCellRenderer headerRenderer = header.getDefaultRenderer();

        for (int column = 0; column < table.getColumnCount(); column++) {
            if (column != 2) { // Excluir la tercera columna (índice 2)
                TableColumn tableColumn = columnModel.getColumn(column);
                Component headerComp = headerRenderer.getTableCellRendererComponent(table, tableColumn.getHeaderValue(), false, false, -1, column);
                int headerWidth = headerComp.getPreferredSize().width + 2; // Sumar 2 para agregar un pequeño espacio adicional
                // Obtener el ancho máximo de las celdas en la columna
                int maxWidth = headerWidth;
                for (int row = 0; row < table.getRowCount(); row++) {
                    TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
                    Component cellComp = table.prepareRenderer(cellRenderer, row, column);
                    int cellWidth = cellComp.getPreferredSize().width + table.getIntercellSpacing().width;
                    maxWidth = Math.max(maxWidth, cellWidth);
                }
                // Establecer el ancho máximo como ancho preferido de la columna
                tableColumn.setPreferredWidth(maxWidth);
            }
        }
    }

    public void limpiarVista() {
        // Limpia la selección de la tablaInicio
        vista.tablaInicio.clearSelection();
        // Limpia los radio buttons usando el ButtonGroup
        ButtonGroup group = new ButtonGroup();
        group.add(vista.jRadioButtonC);
        group.add(vista.jRadioButtonR);
        group.clearSelection();
        // Limpia el campo de búsqueda y la barra de búsqueda
        vista.barraBusqueda.setText("");
        // Limpia los productos seleccionados y actualiza el JScrollPane
        listaProductos.clear();
        actualizarPanelScroll();
        cargarInicialmenteProductosEnTabla();
    }

    // Método para eliminar el producto del JScrollPane
    private void eliminarProducto(JLabel nombreLabel, JLabel precioLabel, JSpinner cantidadSpinner, JButton eliminarButton) {
        Container parent = nombreLabel.getParent();
        parent.remove(nombreLabel);
        parent.remove(precioLabel);
        parent.remove(cantidadSpinner);
        parent.remove(eliminarButton);
        listaProductos.remove(parent);
        actualizarPanelScroll();
    }

    // Método para actualizar el JScrollPane con la lista de productos
    private void actualizarPanelScroll() {
        JPanel panelProductos = new JPanel();
        panelProductos.setBackground(Color.WHITE);
        panelProductos.setLayout(new BoxLayout(panelProductos, BoxLayout.Y_AXIS));
        for (JPanel panelProducto : listaProductos) {
            panelProductos.add(panelProducto);
            panelProductos.add(Box.createVerticalStrut(10)); // Espacio entre productos
        }
        vista.jScrollPane2.setViewportView(panelProductos);
        vista.jScrollPane2.setBackground(Color.WHITE);
        vista.jScrollPane2.revalidate();
        vista.jScrollPane2.repaint();
        // Calcular y actualizar el total
        calcularTotal(vista.jScrollPane2, vista.total);
    }

    public void calcularTotal(JScrollPane scrollPane, JLabel totalLabel) {
        double total = 0;
        // Obtener la lista de productos desde la listaProductos
        for (JPanel panelProducto : listaProductos) {
            // Obtener el valor del precio del JLabel en el panel
            JLabel precioLabel = (JLabel) panelProducto.getComponent(1);
            String precioTexto = precioLabel.getText();
            double precio = Double.parseDouble(precioTexto.split(":")[1].trim());
            // Obtener la cantidad del JSpinner en el panel
            JSpinner cantidadSpinner = (JSpinner) panelProducto.getComponent(2);
            int cantidad = (int) cantidadSpinner.getValue();
            // Sumar al total el precio multiplicado por la cantidad
            total += precio * cantidad;
        }

        // Crear un objeto DecimalFormat para formatear el total a dos decimales
        DecimalFormat df = new DecimalFormat("#.00");
        // Formatear el total con dos decimales
        String totalFormateado = df.format(total);
        // Mostrar el total en el JLabel
        totalLabel.setText("$" + totalFormateado);
    }

    public void mostrarDetalleVenta(Venta venta) {
        detalleVenta detalleVenta = new detalleVenta();
        // Configurar el tamaño de la ventana
        detalleVenta.setSize(857, 600); // Cambia 'ancho' y 'alto' según tus preferencias
        // Centrar la ventana en la pantalla
        detalleVenta.setLocationRelativeTo(null);
        // Crear el controlador para la ventana DetalleVenta
        Control_detalleVenta controlDetalleVenta = new Control_detalleVenta(this, vista, detalleVenta, listaProductos, venta);
        vista.setVisible(false);
        detalleVenta.setVisible(true);
        detalleVenta.panelCliente.setVisible(false);
        detalleVenta.panelTituloCliente.setVisible(false);
    }

    public void mostrarRegistroCliente(Venta venta) {
        detalleVenta detalleVenta = new detalleVenta();
        detalleVenta.setLocationRelativeTo(vista);
        // Crear el controlador para la ventana DetalleVenta
        Control_detalleVenta controlDetalleVenta = new Control_detalleVenta(this, vista, detalleVenta, listaProductos, venta);
        vista.setVisible(false);
        detalleVenta.setVisible(true);
        detalleVenta.panelCliente.setVisible(true);
        detalleVenta.panelTituloCliente.setVisible(true);
    }

    // Método para obtener el stock del producto por su nombre
    public int obtenerStockProducto(String nombreProducto) {
        Conexion conexion = new Conexion();
        String query = "SELECT stock FROM Producto WHERE nombre = ?";
        int stock = 0;
        try (Connection conn = conexion.getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, nombreProducto);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                stock = rs.getInt("stock");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return stock;
    }

    public List<Producto> obtenerProductosDesdeBaseDeDatos() {
        List<Producto> productos = new ArrayList<>();
        // Establecer la conexión a la base de datos y ejecutar la consulta
        Conexion conexion = new Conexion();
        String query = "SELECT id_producto, nombre, descripcion, precio_por_unidad, stock FROM Producto";
        try (Connection conn = conexion.getConnection(); Statement statement = conn.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
            // Iterar sobre los resultados y crear objetos Producto
            while (resultSet.next()) {
                int id = resultSet.getInt("id_producto");
                String nombre = resultSet.getString("nombre");
                String descripcion = resultSet.getString("descripcion");
                double precio = resultSet.getDouble("precio_por_unidad");
                int stock = resultSet.getInt("stock");

                // Crear un nuevo objeto Producto y agregarlo a la lista
                Producto producto = new Producto(id, nombre, descripcion, precio, stock);
                productos.add(producto);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return productos;
    }

    private void cargarInicialmenteProductosEnTabla() {
        // Obtener la lista de productos desde la base de datos
        List<Producto> productos = obtenerProductosDesdeBaseDeDatos();
        // Crear un modelo de tabla para mostrar los resultados
        DefaultTableModel model = (DefaultTableModel) vista.tablaInicio.getModel();
        // Limpiar la tabla antes de agregar nuevos datos
        model.setRowCount(0);
        // Agregar cada producto al modelo de la tabla
        for (Producto producto : productos) {
            Object[] fila = {producto.getIdProducto(), producto.getNombre(), producto.getDescripcion(), producto.getPrecioPorUnidad(), producto.getStock()};
            model.addRow(fila);
        }
    }

}
