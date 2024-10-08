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
import vista.Inicio;
import vista.InicioInventario;
import vista.solicitaContraseña;

public class Control_Dialogo implements ActionListener {

    private solicitaContraseña dialogo;
    private Inicio inicio;

    public Control_Dialogo(solicitaContraseña dialog, Inicio inicio) {
        this.dialogo = dialog;
        this.inicio = inicio;

        this.dialogo.btnVerificar.addActionListener(this);
        this.dialogo.btnCancelar.addActionListener(this);

        dialog.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == dialogo.btnVerificar) {
            String usuario = dialogo.getUsuario();
            String contrasenia = dialogo.getContrasenia();

            boolean accesoAutorizado = verificarCredenciales(usuario, contrasenia);

            if (accesoAutorizado) {
                UIManager.put("OptionPane.okButtonText", "Aceptar");
                JOptionPane.showMessageDialog(null, "Acceso autorizado");

                dialogo.dispose();
                InicioInventario inicioInventario = new InicioInventario();
                inicioInventario.setVisible(true);
                inicioInventario.setLocationRelativeTo(null);
                inicio.setVisible(false);

                Control_InicioInventario Ctrlinventario = new Control_InicioInventario(inicioInventario, inicio);

            } else {
                UIManager.put("OptionPane.okButtonText", "Aceptar");
                JOptionPane.showMessageDialog(null, "Las credenciales son incorrectas");
            }

        } else if (source == dialogo.btnCancelar) {
            dialogo.dispose();
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
