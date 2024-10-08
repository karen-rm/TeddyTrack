package controlador;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import modelo.Producto;
import vista.InicioInventario;
import vista.productoLista;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import modelo.Conexion;
import modelo.Inventario;
import vista.datosProducto;

public class Control_Producto implements ActionListener {

    private productoLista vistaProductos;
    private InicioInventario vistaInventario;
    private String categoriaSeleccionada;
    private Producto producto;
    private Inventario inventario;
    private datosProducto infoProducto;

    public Control_Producto(productoLista vistaProductos, InicioInventario vistaInventario, String categoriaSeleccionada) {
        this.vistaProductos = vistaProductos;
        this.vistaInventario = vistaInventario;
        this.categoriaSeleccionada = categoriaSeleccionada;

        this.producto = new Producto();
        this.inventario = new Inventario();

        vistaProductos.btnRegresar.addActionListener(this);
        vistaProductos.btnAgregar.addActionListener(this);
        this.vistaProductos.jComboBoxEliminaProducto.addActionListener(this);
        this.vistaProductos.jComboBoxModificaProducto.addActionListener(this);

        this.vistaProductos.titulo.setText(categoriaSeleccionada);
        //mostrarVistaProductos(); 

        vistaProductos.btnRegresar.setBackground(Color.WHITE);
        vistaProductos.btnRegresar.setBorderPainted(false);

        vistaProductos.btnAgregar.setBackground(Color.WHITE);
        vistaProductos.setLocationRelativeTo(null);
        mostrarVistaProductos();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        // Cerrar la vista de productos y mostrar la vista del inventario
        if (source == vistaProductos.btnRegresar) {

            vistaProductos.dispose();
            vistaInventario.setVisible(true);

        } else if (source == vistaProductos.jComboBoxEliminaProducto) {

            String nombreProducto = (String) vistaProductos.jComboBoxEliminaProducto.getSelectedItem();
            System.out.println(nombreProducto);
            //String categroia = controlProducto.getCategoriaSeleccionada(); 
            // Obtener el id del producto seleccionado
            int idProducto = obtenerIdProducto(nombreProducto);
            System.out.println(idProducto);
            // Verificar si se pudo obtener el id del producto
            if (idProducto != -1) {
                // Eliminar el producto
                System.out.println("eliminar producto");
                producto.eliminarProducto(idProducto, nombreProducto);
                List<Producto> productosActualizados = producto.obtenerProductos();
                // Actualizar la tabla con los productos actualizados
                agregarProductosATabla(productosActualizados);// Actualizar la tabla con los nuevos productos
                cargarProductosDeCategoriaEnComboBox(vistaProductos.jComboBoxModificaProducto, producto.obtenerIdCategoria(categoriaSeleccionada));
                cargarProductosDeCategoriaEnComboBox(vistaProductos.jComboBoxEliminaProducto, producto.obtenerIdCategoria(categoriaSeleccionada));

            } else {
                UIManager.put("OptionPane.okButtonText", "Aceptar");
                JOptionPane.showMessageDialog(null, "Error: No se pudo obtener el id del producto seleccionado");

            }
        } else if (source == vistaProductos.btnAgregar) {

            infoProducto = new datosProducto();
            infoProducto.setVisible(true);
            infoProducto.setLocationRelativeTo(null);

            // Agregar ActionListener a los botones de datosProducto
            infoProducto.btnOk.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        String nombreProducto = infoProducto.nombreProducto.getText();
                        double precio = Double.parseDouble(infoProducto.precio.getText());
                        int stock = (int) infoProducto.stock.getValue();
                        String descripcion = infoProducto.descripcion.getText();
                        int idCategoria = obtenerIdCategoria(categoriaSeleccionada);
                        String categoriaNombre = obtenerNombreCategoria(idCategoria);
                        infoProducto.categoria.setText(categoriaNombre);

                        if (nombreProducto.isEmpty() || descripcion.isEmpty()) {
                            UIManager.put("OptionPane.okButtonText", "Aceptar");
                            JOptionPane.showMessageDialog(null, "Es necesario rellenar todos los campos.", "Formulario incompleto", JOptionPane.ERROR_MESSAGE);
                        } else if (precio <= 0) {
                            UIManager.put("OptionPane.okButtonText", "Aceptar");
                            JOptionPane.showMessageDialog(null, "El precio debe ser mayor a cero.", "Datos inválidos", JOptionPane.ERROR_MESSAGE);
                        } else {

                            if (confimarAgregarProducto(nombreProducto, descripcion, precio, stock)) {

                                producto.agregarProducto(nombreProducto, idCategoria, precio, stock, descripcion, vistaProductos);
                                System.out.println("Se agrego el producto ");
                                infoProducto.dispose();

                                // Limpiar la tabla de productos en vistaProductos
                                DefaultTableModel model = (DefaultTableModel) vistaProductos.Tabla.getModel();
                                model.setRowCount(0);
                                System.out.println("limpiamos la tabla ");
                                // Limpiar ComboBox en vistaProductos
                                vistaProductos.jComboBoxEliminaProducto.setModel(new DefaultComboBoxModel<>());
                                vistaProductos.jComboBoxModificaProducto.setModel(new DefaultComboBoxModel<>());
                                System.out.println("limpiamos los comboBox");
                                //vistaProductos.setVisible(false); 
                                mostrarVistaProductos();
                                infoProducto = null;
                            } else {

                            }
                        }

                    } catch (NumberFormatException error) {
                        // Manejo de errores si el texto ingresado no puede ser convertido a double
                        UIManager.put("OptionPane.okButtonText", "Aceptar");
                        JOptionPane.showMessageDialog(null, "El precio ingresado no es válido. Por favor ingrese un número válido.", "Error de formato", JOptionPane.ERROR_MESSAGE);
                    }

                }
            });

            infoProducto.btnCancelar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    UIManager.put("OptionPane.okButtonText", "Aceptar");
                    JOptionPane.showMessageDialog(null, "Operación cancelada", "Cancelación", JOptionPane.INFORMATION_MESSAGE);
                    infoProducto.dispose();
                    infoProducto = null;
                }
            });

        } else if (source == vistaProductos.jComboBoxModificaProducto) {
            infoProducto = new datosProducto();

            // Establecer el texto del nombre del producto
            String nombreProductoSeleccionado = (String) vistaProductos.jComboBoxModificaProducto.getSelectedItem();

            String descripcionProducto = obtenerDescripcionProducto(nombreProductoSeleccionado);
            double precioProducto = obtenerPrecioProducto(nombreProductoSeleccionado);
            int stockProducto = obtenerStockProducto(nombreProductoSeleccionado);
            int categoriaProducto = obtenerCategoriaProducto(nombreProductoSeleccionado);
            String categoriaNombre = obtenerNombreCategoria(categoriaProducto);

            infoProducto.nombreProducto.setText(nombreProductoSeleccionado);
            infoProducto.descripcion.setText(descripcionProducto);
            infoProducto.precio.setText(String.valueOf(precioProducto));
            infoProducto.stock.setValue(stockProducto);
            infoProducto.categoria.setText(categoriaNombre);

            infoProducto.setVisible(true);
            infoProducto.setLocationRelativeTo(null);

            // Agregar ActionListener para el botón "Ok"
            infoProducto.btnOk.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    
                    try {
                        String n = infoProducto.nombreProducto.getText();
                        double p = Double.parseDouble(infoProducto.precio.getText());
                        int s = (int) infoProducto.stock.getValue();
                        String d = infoProducto.descripcion.getText();
                        String c = infoProducto.categoria.getText();
                        int id_c = obtenerIdCategoria(c);
                        
                        if (n.isEmpty() || d.isEmpty()) {
                            UIManager.put("OptionPane.okButtonText", "Aceptar");
                            JOptionPane.showMessageDialog(null, "Es necesario rellenar todos los campos.", "Formulario incompleto", JOptionPane.ERROR_MESSAGE);
                        } else if (p <= 0) {
                            UIManager.put("OptionPane.okButtonText", "Aceptar");
                            JOptionPane.showMessageDialog(null, "El precio debe ser mayor a cero.", "Datos inválidos", JOptionPane.ERROR_MESSAGE);
                        } else{
                            
                            if (confimarAgregarProducto(n, d, p, s)) {
                                producto.modificarProducto(nombreProductoSeleccionado, n, id_c, p, s, d);
                                System.out.println("Se modifico el producto. ");
                                infoProducto.dispose();

                                // Limpiar la tabla de productos en vistaProductos
                                DefaultTableModel model = (DefaultTableModel) vistaProductos.Tabla.getModel();
                                model.setRowCount(0);

                                // Limpiar ComboBox en vistaProductos
                                vistaProductos.jComboBoxEliminaProducto.setModel(new DefaultComboBoxModel<>());
                                vistaProductos.jComboBoxModificaProducto.setModel(new DefaultComboBoxModel<>());

                                mostrarVistaProductos();
                            }
                        }

                    } catch (NumberFormatException error) {
                        // Manejo de errores si el texto ingresado no puede ser convertido a double
                        UIManager.put("OptionPane.okButtonText", "Aceptar");
                        JOptionPane.showMessageDialog(null, "El precio ingresado no es válido. Por favor ingrese un número válido.", "Error de formato", JOptionPane.ERROR_MESSAGE);
                    }

                }
            });

            // Agregar ActionListener para el botón "Cancelar"
            infoProducto.btnCancelar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    infoProducto.dispose();
                    UIManager.put("OptionPane.okButtonText", "Aceptar");
                    JOptionPane.showMessageDialog(null, "Operación cancelada", "Cancelación", JOptionPane.INFORMATION_MESSAGE);
                }
            });

            //mostrarVistaProductos();
        }
    }

    public void mostrarVistaProductos() {
        vistaProductos.titulo.setText(categoriaSeleccionada);
        // Obtener la lista de productos según la categoría seleccionada
        List<Producto> productos = producto.obtenerProductos();

        // Configurar la vista de productos y mostrarla
        //vistaProductos.titulo.setText(categoriaSeleccionada); 
        vistaProductos.setVisible(true);

        //vistaProductos.toFront();
        vistaInventario.setVisible(false);

        // Agregar productos al JTable
        agregarProductosATabla(productos);
        ajustarAnchoColumnas(vistaProductos.Tabla);
        //agregar prdofuctos a los comboBox 
        //int idCategoria = obtenerIdCategoria(categoriaSeleccionada); 
        cargarProductosDeCategoriaEnComboBox(vistaProductos.jComboBoxModificaProducto, producto.obtenerIdCategoria(categoriaSeleccionada));
        cargarProductosDeCategoriaEnComboBox(vistaProductos.jComboBoxEliminaProducto, producto.obtenerIdCategoria(categoriaSeleccionada));
    }

    public void mostrarDetallesProducto(String nombreProducto, datosProducto infoProducto) {
        if (nombreProducto != null && !nombreProducto.isEmpty()) {
            Conexion conexion = new Conexion();
            String selectQuery = "SELECT id_categoria, precio_por_unidad, stock, descripcion FROM Producto WHERE nombre = ?";
            try (Connection conn = conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(selectQuery)) {
                pstmt.setString(1, nombreProducto);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int idCategoria = rs.getInt("id_categoria");
                        double precioPorUnidad = rs.getDouble("precio_por_unidad");
                        int stock = rs.getInt("stock");
                        String descripcion = rs.getString("descripcion");

                        // Aquí muestras los detalles del producto en tu interfaz gráfica
                        // Por ejemplo, si tienes campos de texto en tu interfaz, podrías hacer algo como:
                        infoProducto.nombreProducto.setText(nombreProducto);

                        infoProducto.precio.setText(String.valueOf(precioPorUnidad));
                        infoProducto.stock.setValue(stock);
                        infoProducto.descripcion.setText(descripcion);
                    } else {
                        UIManager.put("OptionPane.okButtonText", "Aceptar");
                        JOptionPane.showMessageDialog(null, "No se encontró el producto en la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException ex) {
                UIManager.put("OptionPane.okButtonText", "Aceptar");
                JOptionPane.showMessageDialog(null, "Error al obtener detalles del producto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        } else {
            UIManager.put("OptionPane.okButtonText", "Aceptar");
            JOptionPane.showMessageDialog(null, "El nombre del producto no puede estar vacío", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para agregar productos al JTable
    public void agregarProductosATabla(List<Producto> productos) {
        System.out.println("ento a agregarProductosTabla");
        DefaultTableModel model = (DefaultTableModel) vistaProductos.Tabla.getModel();

        // Limpiar la tabla antes de agregar nuevos datos
        model.setRowCount(0);

        int idCategoriaSeleccionada = obtenerIdCategoria(vistaProductos.titulo.getText());

        for (Producto producto : productos) {
            // Verificar si el producto pertenece a la categoría seleccionada
            if (producto.getIdCategoria() == obtenerIdCategoria(categoriaSeleccionada)) {
                Object[] fila = {
                    producto.getIdProducto(),
                    //producto.getIdCategoria(), // No se muestra el idCategoria en la tabla
                    producto.getNombre(),
                    producto.getDescripcion(),
                    producto.getPrecioPorUnidad(),
                    producto.getStock()
                };
                model.addRow(fila);
            }
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

    private int obtenerIdCategoria(String categoriaSeleccionada) {
        Conexion conexion = new Conexion();
        int idCategoria = -1; // Valor por defecto si no se encuentra la categoría

        try (Connection conn = conexion.getConnection()) {
            String sql = "SELECT id_categoria FROM Categoria WHERE nombre_categoria = ?";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, categoriaSeleccionada); // Establecer el nombre de la categoría como parámetro
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        idCategoria = resultSet.getInt("id_categoria");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return idCategoria;
    }

    public String getCategoriaSeleccionada() {
        return categoriaSeleccionada;
    }

    public void setCategoriaSeleccionada(String categoriaSeleccionada) {
        this.categoriaSeleccionada = categoriaSeleccionada;
        System.out.println("Categoría seleccionada: " + categoriaSeleccionada);
    }

    public void cargarProductosDeCategoriaEnComboBox(JComboBox<String> comboBox, int idCategoria) {
        Conexion conexion = new Conexion();
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();

        try (Connection conn = conexion.getConnection()) {
            String select = "SELECT nombre FROM Producto WHERE id_categoria = ?";
            try (PreparedStatement stmt = conn.prepareStatement(select)) {
                stmt.setInt(1, idCategoria);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String nombreProducto = rs.getString("nombre");
                        model.addElement(nombreProducto);
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error al conectarse a la base de datos: " + ex.getMessage());
            ex.printStackTrace();
        }

        comboBox.setModel(model);
    }

    private int obtenerIdProducto(String nombreProducto) {
        Conexion conexion = new Conexion();
        int idProducto = -1; // Valor por defecto si no se encuentra el producto

        try (Connection conn = conexion.getConnection()) {
            String sql = "SELECT id_producto FROM Producto WHERE nombre = ?";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, nombreProducto); // Establecer el nombre del producto como parámetro
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        idProducto = resultSet.getInt("id_producto");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return idProducto;
    }

    public void cargarCategoriasEnComboBox(JComboBox<String> comboBox) {
        Conexion conexion = new Conexion();
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();

        try (Connection conn = conexion.getConnection()) {
            String select = "SELECT nombre_categoria FROM Categoria;";
            try (PreparedStatement stmt = conn.prepareStatement(select); ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String nombreCategoria = rs.getString("nombre_categoria");
                    model.addElement(nombreCategoria);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error al conectarse a la base de datos: " + ex.getMessage());
            ex.printStackTrace();
        }

        comboBox.setModel(model);
    }

    public void regresarAInicioInventario() {
        /* // Limpiar la tabla de productos en vistaProductos
        DefaultTableModel model = (DefaultTableModel) vistaProductos.Tabla.getModel();
        model.setRowCount(0);

        // Limpiar ComboBox en vistaProductos
        vistaProductos.jComboBoxEliminaProducto.setModel(new DefaultComboBoxModel<>());
        vistaProductos.jComboBoxModificaProducto.setModel(new DefaultComboBoxModel<>());*/

        // Volver a mostrar InicioInventario y limpiar datos obsoletos
        vistaProductos.dispose();
        vistaInventario.setVisible(true);
    }

    private String obtenerDescripcionProducto(String nombreProducto) {
        Conexion conexion = new Conexion();
        String descripcionProducto = null;

        try (Connection conn = conexion.getConnection()) {
            String sql = "SELECT descripcion FROM Producto WHERE nombre = ?";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, nombreProducto);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        descripcionProducto = resultSet.getString("descripcion");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return descripcionProducto;
    }

    private double obtenerPrecioProducto(String nombreProducto) {
        Conexion conexion = new Conexion();
        double precioProducto = 0.0;

        try (Connection conn = conexion.getConnection()) {
            String sql = "SELECT precio_por_unidad FROM Producto WHERE nombre = ?";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, nombreProducto);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        precioProducto = resultSet.getDouble("precio_por_unidad");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return precioProducto;
    }

    private int obtenerStockProducto(String nombreProducto) {
        Conexion conexion = new Conexion();
        int stockProducto = 0;

        try (Connection conn = conexion.getConnection()) {
            String sql = "SELECT stock FROM Producto WHERE nombre = ?";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, nombreProducto);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        stockProducto = resultSet.getInt("stock");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stockProducto;
    }

    private int obtenerCategoriaProducto(String nombreProducto) {
        Conexion conexion = new Conexion();
        int categoriaProducto = 0; // Valor por defecto si no se encuentra la categoría

        try (Connection conn = conexion.getConnection()) {
            String sql = "SELECT id_categoria FROM Producto WHERE nombre = ?";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, nombreProducto);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        categoriaProducto = resultSet.getInt("id_categoria");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categoriaProducto;
    }

    private String obtenerNombreCategoria(int idCategoria) {
        Conexion conexion = new Conexion();
        String nombreCategoria = null;

        try (Connection conn = conexion.getConnection()) {
            String sql = "SELECT nombre_categoria FROM Categoria WHERE id_categoria = ?";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, idCategoria);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        nombreCategoria = resultSet.getString("nombre_categoria");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nombreCategoria;
    }

    public boolean confimarAgregarProducto(String nombre, String descripcion, Double precio, int stock) {
        JOptionPane optionPane = new JOptionPane(
                "Por favor, verifique los datos del formulario:\n"
                + "Producto nuevo: " + nombre + "\n"
                + "Descripción: " + descripcion + "\n"
                + "Precio por unidad: " + precio + "\n"
                + "Stock: " + stock + "\n",
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
            return true;
        } else if (selectedValue.equals("Cancelar")) {
            optionPane.setValue(JOptionPane.CLOSED_OPTION);
            return false;
        } else {
            optionPane.setValue(JOptionPane.CLOSED_OPTION);
            return false;
        }
    }

}
