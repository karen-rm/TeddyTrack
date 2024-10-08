package modelo;

import java.awt.Color;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class Inventario {

    public List<JButton> obtenerBotonesCategorias() {
        Conexion conexion = new Conexion();
        List<JButton> botonesCategorias = new ArrayList<>();
        try (Connection conn = conexion.getConnection()) {
            String select = "SELECT nombre_categoria FROM Categoria;";
            try (PreparedStatement stmt = conn.prepareStatement(select, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String nombreCategoria = rs.getString("nombre_categoria");
                    JButton botonCategoria = new JButton(nombreCategoria);
                    botonCategoria.setBackground(Color.WHITE);
                    botonesCategorias.add(botonCategoria);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error al conectarse a la base de datos: " + ex.getMessage());
            ex.printStackTrace();
        }
        return botonesCategorias;
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

    public void agregarCategoria(String nombreCategoria) {
        if (nombreCategoria != null && !nombreCategoria.isEmpty()) {
            Conexion conexion = new Conexion();

            // Verificar si ya existe una categoría con el mismo nombre
            if (categoriaExiste(nombreCategoria)) {
                UIManager.put("OptionPane.okButtonText", "Aceptar");
                JOptionPane.showMessageDialog(null, "Ya existe una categoría con el nombre ingresado");
                return; // Salir del método si la categoría ya existe
            }

            String insertQuery = "INSERT INTO Categoria (nombre_categoria) VALUES (?)";

            try (Connection conn = conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                pstmt.setString(1, nombreCategoria);
                pstmt.executeUpdate();
                UIManager.put("OptionPane.okButtonText", "Aceptar");
                JOptionPane.showMessageDialog(null, "Categoría agregada correctamente");
            } catch (SQLException ex) {
                UIManager.put("OptionPane.okButtonText", "Aceptar");
                JOptionPane.showMessageDialog(null, "Error al conectarse a la base de datos: " + ex.getMessage());
                ex.printStackTrace();
            }
        } else {
            UIManager.put("OptionPane.okButtonText", "Aceptar");
            JOptionPane.showMessageDialog(null, "El nombre de la categoría no puede estar vacío");
        }
    }

// Método para verificar si ya existe una categoría con el mismo nombre
    boolean categoriaExiste(String nombreCategoria) {
        Conexion conexion = new Conexion();
        String selectQuery = "SELECT nombre_categoria FROM Categoria WHERE nombre_categoria = ?";

        try (Connection conn = conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(selectQuery)) {
            pstmt.setString(1, nombreCategoria);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Devuelve true si encuentra una coincidencia, false de lo contrario
            }
        } catch (SQLException ex) {
            UIManager.put("OptionPane.okButtonText", "Aceptar");
            JOptionPane.showMessageDialog(null, "Error al verificar si existe la categoría: " + ex.getMessage());
            ex.printStackTrace();
        }
        return false; // En caso de error, se asume que la categoría no existe para evitar problemas
    }

    public void eliminarCategoria(String nombreCategoria) {
        if (nombreCategoria != null) {
            // Establecer el texto de los botones en español
            UIManager.put("OptionPane.yesButtonText", "Sí");
            UIManager.put("OptionPane.noButtonText", "No");

            int confirmacion = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que deseas eliminar la categoría: " + nombreCategoria + " ?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                Conexion conexion = new Conexion();
                String deleteDetalleVentaQuery = "DELETE FROM detalleventa WHERE id_producto IN (SELECT id_producto FROM producto WHERE id_categoria IN (SELECT id_categoria FROM categoria WHERE nombre_categoria = ?))";
                String deleteCategoriaQuery = "DELETE FROM Categoria WHERE nombre_categoria = ?";
                try (Connection conn = conexion.getConnection(); PreparedStatement pstmtDetalleVenta = conn.prepareStatement(deleteDetalleVentaQuery); PreparedStatement pstmtCategoria = conn.prepareStatement(deleteCategoriaQuery)) {
                    // Eliminar registros dependientes de detalleventa
                    pstmtDetalleVenta.setString(1, nombreCategoria);
                    pstmtDetalleVenta.executeUpdate();
                    // Luego, eliminar la categoría
                    pstmtCategoria.setString(1, nombreCategoria);
                    pstmtCategoria.executeUpdate();
                    UIManager.put("OptionPane.okButtonText", "Aceptar");
                    JOptionPane.showMessageDialog(null, "Categoría eliminada correctamente");
                } catch (SQLException ex) {
                    UIManager.put("OptionPane.okButtonText", "Aceptar");
                    JOptionPane.showMessageDialog(null, "Error al conectarse a la base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            } else {
                UIManager.put("OptionPane.okButtonText", "Aceptar");
                JOptionPane.showMessageDialog(null, "Operación cancelada", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

  public void modificarNombreCategoria(String nombreCategoriaActual, String nuevoNombreCategoria) {
    if (nombreCategoriaActual != null && !nuevoNombreCategoria.isEmpty()) {
        if (!categoriaExiste(nuevoNombreCategoria)) {
            // Establecer el texto de los botones en español
            UIManager.put("OptionPane.yesButtonText", "Sí");
            UIManager.put("OptionPane.noButtonText", "No");
            
            int confirmacion = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que deseas modificar el nombre de la categoría: " + nombreCategoriaActual + " a " + nuevoNombreCategoria + " ?", "Confirmar Modificación", JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                Conexion conexion = new Conexion();
                String updateQuery = "UPDATE Categoria SET nombre_categoria = ? WHERE nombre_categoria = ?";
                try (Connection conn = conexion.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                    pstmt.setString(1, nuevoNombreCategoria);
                    pstmt.setString(2, nombreCategoriaActual);
                    int rowsUpdated = pstmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        UIManager.put("OptionPane.okButtonText", "Aceptar");
                        JOptionPane.showMessageDialog(null, "Nombre de categoría modificado correctamente");
                    } else {
                        UIManager.put("OptionPane.okButtonText", "Aceptar");
                        JOptionPane.showMessageDialog(null, "No se encontró ninguna categoría con el nombre proporcionado");
                    }
                } catch (SQLException ex) {
                    UIManager.put("OptionPane.okButtonText", "Aceptar");
                    JOptionPane.showMessageDialog(null, "Error al conectarse a la base de datos: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                JOptionPane optionPane = new JOptionPane("Operación cancelada", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{"Aceptar"}, null);
                JDialog dialog = optionPane.createDialog("Operación cancelada");
                dialog.setVisible(true);
            }
        } else {
            JOptionPane optionPane = new JOptionPane("Ya existe una categoría con ese nombre.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{"Aceptar"}, null);
            JDialog dialog = optionPane.createDialog("Error");
            dialog.setVisible(true);
        }
    } else {
        JOptionPane optionPane = new JOptionPane("El nuevo nombre de categoría no puede estar vacío", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{"Aceptar"}, null);
        JDialog dialog = optionPane.createDialog("Error");
        dialog.setVisible(true);
    }
}

}
