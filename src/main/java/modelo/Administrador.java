
package modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Administrador {
    private String usuario; 
    private String contrasenia; 
    
    public Administrador(String usuario, String contrasenia){
        this.usuario = usuario; 
        this.contrasenia = contrasenia; 
    }
    
     public Administrador (){
     }
    
    public boolean verificarCredenciales(String usuario, String contrasenia) {
        Conexion conexion = new Conexion();
        try (Connection conn = conexion.getConnection()){

            String sql = "SELECT COUNT(*) FROM Administrador WHERE usuario = ? AND contrasenia = ?";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, usuario); // Usar el parámetro correo
                statement.setString(2, contrasenia); // Usar el parámetro contrasenia
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        return count == 1;
                    }
                }
            }
            
            try {
                if (conexion != null) {
                    conn.close(); // Cerrar la conexión
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } 
        
        return false;
    }
    
}
