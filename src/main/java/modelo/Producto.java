package modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import vista.productoLista;

public class Producto {

    private int idProducto;
    private int idCategoria;
    private String nombre;
    private double precioPorUnidad;
    private int stock;
    private String descripcion;

    public int getIdProducto() {
        return idProducto;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecioPorUnidad() {
        return precioPorUnidad;
    }

    public int getStock() {
        return stock;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Producto() {

    }

    public Producto(int idProducto, int idCategoria, String nombre, double precioPorUnidad, int stock, String descripcion) {
        this.idProducto = idProducto;
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.precioPorUnidad = precioPorUnidad;
        this.stock = stock;
        this.descripcion = descripcion;
    }

    public Producto(int idProducto, String nombre, String descripcion, double precio, int stock) {
        this.idProducto = idProducto;

        this.nombre = nombre;
        this.precioPorUnidad = precio;
        this.stock = stock;
        this.descripcion = descripcion;
    }

    public static List<Producto> obtenerProductos() {
        List<Producto> productos = new ArrayList<>();
        Conexion conexion = new Conexion();
        try (Connection conn = conexion.getConnection()) {
            String sql = "SELECT id_producto, id_categoria, nombre, precio_por_unidad, stock, descripcion FROM Producto";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int idProducto = resultSet.getInt("id_producto");
                        int idCategoria = resultSet.getInt("id_categoria");
                        String nombre = resultSet.getString("nombre");
                        double precioPorUnidad = resultSet.getDouble("precio_por_unidad");
                        int stock = resultSet.getInt("stock");
                        String descripcion = resultSet.getString("descripcion");

                        // Crear un nuevo objeto Producto con los valores obtenidos y agregarlo a la lista
                        Producto producto = new Producto(idProducto, idCategoria, nombre, precioPorUnidad, stock, descripcion);
                        productos.add(producto);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productos;
    }

    public void eliminarProducto(int idProducto, String nombreProducto) {
        System.out.println("entro a eliminarProducto");
        UIManager.put("OptionPane.yesButtonText", "Sí");
        UIManager.put("OptionPane.noButtonText", "No");
        UIManager.put("OptionPane.cancelButtonText", "Cancelar");
        UIManager.put("OptionPane.okButtonText", "Aceptar");
        if (nombreProducto != null) {
            int confirmacion = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que deseas eliminar el producto: " + nombreProducto + " ?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                Conexion conexion = new Conexion();
                String deleteQuery = "DELETE FROM Producto WHERE id_producto = ?";
                try (Connection conn = conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {

                    pstmt.setInt(1, idProducto);
                    pstmt.executeUpdate();

                    UIManager.put("OptionPane.okButtonText", "Aceptar");
                    JOptionPane.showMessageDialog(null, "Producto eliminado correctamente");

                } catch (SQLException ex) {
                    UIManager.put("OptionPane.okButtonText", "Aceptar");
                    JOptionPane.showMessageDialog(null, "Error al conectarse a la base de datos: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                UIManager.put("OptionPane.okButtonText", "Aceptar");
                JOptionPane.showMessageDialog(null, "Operación cancelada");
            }
        }
    }

    public void agregarProducto(String nombreProducto, int idCategoria, double precioPorUnidad, int stock, String descripcion, productoLista vistaProductos) {
        if (nombreProducto != null && !nombreProducto.isEmpty() && !descripcion.isEmpty()) {
            Conexion conexion = new Conexion();

            String insertQuery = "INSERT INTO Producto (id_categoria, nombre, precio_por_unidad, stock, descripcion) VALUES (?, ?, ?, ?, ?)";

            try (Connection conn = conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

                pstmt.setInt(1, idCategoria);
                pstmt.setString(2, nombreProducto);
                pstmt.setDouble(3, precioPorUnidad);
                pstmt.setInt(4, stock);
                pstmt.setString(5, descripcion);
                pstmt.executeUpdate();

                UIManager.put("OptionPane.okButtonText", "Aceptar");
                JOptionPane.showMessageDialog(vistaProductos, "Producto agregado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException ex) {
                UIManager.put("OptionPane.okButtonText", "Aceptar");
                JOptionPane.showMessageDialog(null, "Error al conectarse a la base de datos: " + ex.getMessage());
                ex.printStackTrace();
            }
        } else {
            UIManager.put("OptionPane.okButtonText", "Aceptar");
            JOptionPane.showMessageDialog(vistaProductos, "Ningun campo puede estar vacío", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void modificarProducto(String nombreProductoActual, String nuevoNombre, int idCategoria, double precio, int stock, String descripcion) {
        // Verificar que los valores proporcionados sean válidos
        if (nombreProductoActual != null && !nombreProductoActual.isEmpty() && nuevoNombre != null && !nuevoNombre.isEmpty() && idCategoria > 0 && precio >= 0 && stock >= 0 && descripcion != null) {
            try {
                Conexion conexion = new Conexion();
                Connection conn = conexion.getConnection();
                String sql = "UPDATE Producto SET nombre = ?, id_categoria = ?, precio_por_unidad = ?, stock = ?, descripcion = ? WHERE nombre = ?";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setString(1, nuevoNombre);
                statement.setInt(2, idCategoria);
                statement.setDouble(3, precio);
                statement.setInt(4, stock);
                statement.setString(5, descripcion);
                statement.setString(6, nombreProductoActual);

                // Ejecutar la actualización
                int filasAfectadas = statement.executeUpdate();
                if (filasAfectadas > 0) {
                    // La actualización se realizó con éxito

                    System.out.println("Producto actualizado correctamente");
                } else {
                    // No se pudo encontrar el producto con el nombre especificado
                    UIManager.put("OptionPane.okButtonText", "Aceptar");
                    JOptionPane.showMessageDialog(null, "No se encontró el producto con el nombre especificado", "Error", JOptionPane.ERROR_MESSAGE);
                }

                // Cerrar la conexión y liberar recursos
                statement.close();
                conn.close();
            } catch (SQLException e) {
                // Manejar cualquier excepción SQL
                e.printStackTrace();
                UIManager.put("OptionPane.okButtonText", "Aceptar");
                JOptionPane.showMessageDialog(null, "Error al actualizar el producto: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Informar al usuario si algún valor proporcionado es inválido
            UIManager.put("OptionPane.okButtonText", "Aceptar");
            JOptionPane.showMessageDialog(null, "Se deben proporcionar valores válidos para todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public int obtenerIdCategoria(String categoriaSeleccionada) {
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

}
