package controlador;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import modelo.Inventario;
import modelo.Producto;
import vista.Inicio;
import vista.InicioInventario;
import vista.productoLista;
import vista.solicitaContraseña;

public class Control_InicioInventario implements ActionListener {

    private InicioInventario vistaInventario;
    private Inicio inicio;
    private Inventario inventario;
    private productoLista vistaProductos;
    private Producto producto;
    private solicitaContraseña dialogo;
    private Control_Producto ctrlProductoLista; // Variable para almacenar la instancia única de Control_Producto

    public Control_InicioInventario() {

    }

    public Control_InicioInventario(InicioInventario vista, Inicio vistaInicio) {
        this.vistaInventario = vista;
        this.inventario = new Inventario();
        this.vistaProductos = new productoLista();
        this.inicio = vistaInicio;

        this.vistaInventario.jComboBoxEliminaCat.addActionListener(this);
        this.vistaInventario.jComboBoxModificaCat.addActionListener(this);
        this.vistaInventario.btnAgregarCat.addActionListener(this);
        this.vistaInventario.btnRegresar.addActionListener(this);
        mostrarCategorias();
        inventario.cargarCategoriasEnComboBox(vistaInventario.jComboBoxModificaCat);
        inventario.cargarCategoriasEnComboBox(vistaInventario.jComboBoxEliminaCat);

        vistaInventario.btnRegresar.setBorderPainted(false);
        vistaInventario.btnRegresar.setBackground(Color.WHITE);
        vistaInventario.btnAgregarCat.setBackground(Color.WHITE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        System.out.println("Evento detectado en: " + source.getClass().getName());

        if (source == vistaInventario.btnAgregarCat) {
            System.out.println("Se presionó el botón Agregar Categoría.");
            UIManager.put("OptionPane.okButtonText", "Aceptar");
            UIManager.put("OptionPane.cancelButtonText", "Cancelar");

            JPanel panel = new JPanel();
            JTextField textField = new JTextField(10); // Crear un campo de texto
            panel.add(new JLabel("Nombre de la nueva categoría:"));
            panel.add(textField);

            // Mostrar un cuadro de diálogo con el campo de texto y los botones "Agregar" y "Cancelar"
            int resultado = JOptionPane.showConfirmDialog(null, panel, "Nueva Categoría", JOptionPane.OK_CANCEL_OPTION);
            // Verificar qué botón se presionó
            if (resultado == JOptionPane.OK_OPTION) {
                // Si se presionó el botón "Agregar"
                String nombreCategoria = textField.getText(); // Obtener el texto ingresado
                inventario.agregarCategoria(nombreCategoria);
                mostrarCategorias();
                inventario.cargarCategoriasEnComboBox(vistaInventario.jComboBoxModificaCat);
                inventario.cargarCategoriasEnComboBox(vistaInventario.jComboBoxEliminaCat);
            } else {
                // Si se presionó el botón "Cancelar" o se cerró el cuadro de diálogo
                JOptionPane optionPane = new JOptionPane("Se canceló la operación", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
                optionPane.setOptions(new Object[]{"Aceptar"});
                JDialog dialog = optionPane.createDialog("Error");
                dialog.setVisible(true);
            }

        } else if (source == vistaInventario.jComboBoxEliminaCat) {
            String nombreCategoria = (String) vistaInventario.jComboBoxEliminaCat.getSelectedItem();
            inventario.eliminarCategoria(nombreCategoria);
            mostrarCategorias();
            inventario.cargarCategoriasEnComboBox(vistaInventario.jComboBoxModificaCat);
            inventario.cargarCategoriasEnComboBox(vistaInventario.jComboBoxEliminaCat);
        } else if (source == vistaInventario.jComboBoxModificaCat) {
            UIManager.put("OptionPane.okButtonText", "Aceptar");
            UIManager.put("OptionPane.cancelButtonText", "Cancelar");
            String nombreCategoria = (String) vistaInventario.jComboBoxModificaCat.getSelectedItem();
            JPanel panel = new JPanel();
            JTextField textField = new JTextField(10); // Crear un campo de texto
            panel.add(new JLabel("Modifique nombre de la categoría: "));
            panel.add(textField);
            textField.setText(nombreCategoria);
            // Mostrar un cuadro de diálogo con el campo de texto y los botones "Agregar" y "Cancelar"
            int resultado = JOptionPane.showConfirmDialog(null, panel, "Modificar Categoría", JOptionPane.OK_CANCEL_OPTION);
            // Verificar qué botón se presionó
            if (resultado == JOptionPane.OK_OPTION) {
                String nombreNuevoCategoria = textField.getText(); // Obtener el texto ingresado
                inventario.modificarNombreCategoria(nombreCategoria, nombreNuevoCategoria);
                mostrarCategorias();
                inventario.cargarCategoriasEnComboBox(vistaInventario.jComboBoxModificaCat);
                inventario.cargarCategoriasEnComboBox(vistaInventario.jComboBoxEliminaCat);
                System.out.println("Dato ingresado: " + nombreCategoria);
            } else {
                UIManager.put("OptionPane.okButtonText", "Aceptar");
                JOptionPane.showMessageDialog(null, "Se canceló la operación");
            }
        } else if (source == vistaInventario.btnRegresar) {
            vistaInventario.dispose();
            inicio.setVisible(true);
        } else {
            System.out.println("Evento no manejado: " + source);
        }
    }

    public void mostrarCategorias() {
        // Limpiar el panel de categorías antes de agregar nuevos botones
        vistaInventario.panelCategorias.removeAll();
        List<JButton> listaCategorias = inventario.obtenerBotonesCategorias();
        if (listaCategorias != null) {
            int numRows = (int) Math.ceil((double) listaCategorias.size() / 3); // Establecer el número de filas en función del número de botones
            // Establecer un layout de GridLayout con el número de filas calculado y 3 columnas
            vistaInventario.panelCategorias.setLayout(new GridLayout(numRows, 3, 10, 10)); // 10 píxeles de espacio horizontal y vertical entre componentes
            // Establecer un borde vacío alrededor del panel
            vistaInventario.panelCategorias.setBorder(new EmptyBorder(10, 10, 10, 10)); // 10 píxeles de espacio en todos los lados
            for (JButton botonCategoria : listaCategorias) {
                // Eliminar todos los action listeners existentes del botón
                ActionListener[] actionListeners = botonCategoria.getActionListeners();
                for (ActionListener listener : actionListeners) {
                    botonCategoria.removeActionListener(listener);
                }

                // Agregar un nuevo action listener al botón
                botonCategoria.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JButton clickedBoton = (JButton) e.getSource();
                        String categoriaSeleccionada = clickedBoton.getText();
                        System.out.println("Se presionó un botón de categoría: " + categoriaSeleccionada);
                        System.out.println("Presionó un botón.");

                        // Verificar si ya existe una instancia de Control_Producto
                        if (ctrlProductoLista == null) {
                            // Si no existe, crear una nueva instancia
                            ctrlProductoLista = new Control_Producto(vistaProductos, vistaInventario, categoriaSeleccionada);
                        } else {
                            // Si ya existe, simplemente actualizar la categoría seleccionada
                            ctrlProductoLista.setCategoriaSeleccionada(categoriaSeleccionada);
                        }
                        ctrlProductoLista.mostrarVistaProductos();
                    }
                });

                // Agregar el botón al panel
                vistaInventario.panelCategorias.add(botonCategoria);
            }

            // Actualizar y repintar el panel después de agregar los botones
            //configurarBotonesCategorias(); 
            vistaInventario.panelCategorias.revalidate();
            vistaInventario.panelCategorias.repaint();
        }
        //configurarBotonesCategorias();
    }

    public void configurarBotonesCategorias() {
        List<JButton> botonesCategorias = inventario.obtenerBotonesCategorias();
        if (botonesCategorias != null) {
            // Limpiar el panel antes de agregar los botones actualizados
            vistaInventario.panelCategorias.removeAll();
            int numRows = (int) Math.ceil((double) botonesCategorias.size() / 3); // Establecer el número de filas en función del número de botones

            // Establecer un layout de GridLayout con el número de filas calculado y 3 columnas
            vistaInventario.panelCategorias.setLayout(new GridLayout(numRows, 3, 10, 10)); // 10 píxeles de espacio horizontal y vertical entre componentes
            // Establecer un borde vacío alrededor del panel
            vistaInventario.panelCategorias.setBorder(new EmptyBorder(10, 10, 10, 10)); // 10 píxeles de espacio en todos los lados

            vistaInventario.panelCategorias.setBackground(Color.WHITE);

            for (JButton botonCategoria : botonesCategorias) {
                for (ActionListener al : botonCategoria.getActionListeners()) {
                    botonCategoria.removeActionListener(al);
                }
                botonCategoria.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Object source = e.getSource();
                        // Aquí va el código que se ejecutará cuando se presione un botón de categoría
                        System.out.println("seleccionaste la categoria: " + source);
                        JButton clickedBoton = (JButton) e.getSource();
                        String categoriaSeleccionada = clickedBoton.getText();
                        System.out.println(categoriaSeleccionada);

                        // Mostrar la vista de productos desde el controlador de productos
                        Control_Producto ctrlProductoLista = new Control_Producto(vistaProductos, vistaInventario, categoriaSeleccionada);
                        ctrlProductoLista.mostrarVistaProductos();

                    }
                });
                // Agregar el botón al panelCategorias
                vistaInventario.panelCategorias.add(botonCategoria);
            }

            // Actualizar y repintar el panel después de agregar los botones
            vistaInventario.panelCategorias.revalidate();
            vistaInventario.panelCategorias.repaint();
        }
    }

}
