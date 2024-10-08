
package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import modelo.Conexion;
import vista.Login;

public class Control_Login implements ActionListener{
    
    private Login login;
 
    public Control_Login(){
        this.login = new Login();
        this.login.btnEntrar.addActionListener(this);
        login.setVisible(true); 
        login.setLocationRelativeTo(null);
    }        

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
    
        if(source == login.btnEntrar){
            
            String usuario = login.usuarioIngresado.getText();
            String contrasenia = login.contraseñaIngresada.getText();
            boolean accesoAutorizado = verificarCredenciales(usuario, contrasenia);
            
            if (accesoAutorizado) {
                UIManager.put("OptionPane.okButtonText", "Aceptar");
                JOptionPane.showMessageDialog(null, "Acceso autorizado");
                login.setVisible(false); 
                login.usuarioIngresado.setText("");
                login.contraseñaIngresada.setText(""); 
                Control_Inicio controlInicio = new Control_Inicio(login);
            } else {
                UIManager.put("OptionPane.okButtonText", "Aceptar");
                JOptionPane.showMessageDialog(null, "Las credenciales son incorrectas");
            }
        }
    }
    
    public boolean verificarCredenciales(String usuario, String contrasenia) {
        Conexion conexion = new Conexion();
        try (Connection conn = conexion.getConnection()) {
            String sql = "SELECT COUNT(*) FROM Administrador WHERE usuario = ? AND contrasenia = ?";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, usuario);
                statement.setString(2, contrasenia);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        return count == 1;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
            
}
