package controlador;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import modelo.Conexion;
import modelo.Pago;
import modelo.Reserva;
import modelo.Venta;
import vista.HistorialReservas;
import vista.Inicio;

public class Control_HistorialReservas implements ActionListener {

    private HistorialReservas vistahistorialReservas;
    private Inicio vistaInicio;
    private Venta venta;
    private Pago pago;

    Control_HistorialReservas(Inicio vistaInicio, HistorialReservas vistahistorialReservas) {
        this.vistaInicio = vistaInicio;
        this.vistahistorialReservas = vistahistorialReservas;

        vistahistorialReservas.btnRegresar.addActionListener(this);
        vistahistorialReservas.btnConsultar.addActionListener(this);
        vistahistorialReservas.btnLimpiar.addActionListener(this);
        vistahistorialReservas.btnImprimir.addActionListener(this);

        vistahistorialReservas.btnRegresar.setBorderPainted(false);
        vistahistorialReservas.btnRegresar.setBackground(Color.WHITE);
        vistahistorialReservas.btnConsultar.setBackground(Color.WHITE);
        vistahistorialReservas.btnLimpiar.setBackground(Color.WHITE);
        vistahistorialReservas.btnImprimir.setBackground(Color.WHITE);
        vistahistorialReservas.btnImprimir.setEnabled(false);

        // Agregar un ListSelectionListener a la tabla
        vistahistorialReservas.tablaReserva.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Verificar si la selección cambió y no está ajustándose
                if (!e.getValueIsAdjusting()) {
                    // Obtener el índice de la fila seleccionada
                    int selectedRow = vistahistorialReservas.tablaReserva.getSelectedRow();
                    // Verificar si se seleccionó una fila válida
                    if (selectedRow != -1) {
                        // Obtener el valor de la celda en la columna que contiene el ID de la reserva
                        int idReserva = (int) vistahistorialReservas.tablaReserva.getValueAt(selectedRow, 0);
                        // Crear una instancia de Reserva con el ID obtenido
                        Reserva reserva = new Reserva();
                        reserva.setId_reserva(idReserva);
                        // Obtener el ID de venta asociado a la reserva
                        int idVenta = obtenerIdVenta(reserva);
                        // Mostrar el detalle de la venta correspondiente al ID de venta
                        mostrarDetalleVenta(idVenta);
                    }
                }
            }
        });

        // Limpiar la tabla
        DefaultTableModel model = (DefaultTableModel) vistahistorialReservas.tablaReserva.getModel();
        model.setRowCount(0);
        vistahistorialReservas.jLabelGananciasTitulo.setVisible(false);
        vistahistorialReservas.jLabelGanancias.setVisible(false);
        vistahistorialReservas.jLabelCompensasionesTitulo.setVisible(false);
        vistahistorialReservas.jLabelCompensasiones.setVisible(false);
        vistahistorialReservas.setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if (e.getSource() == vistahistorialReservas.btnRegresar) {
            vistahistorialReservas.dispose();
            vistaInicio.setVisible(true);
        } else if (e.getSource() == vistahistorialReservas.btnConsultar) {
            java.util.Date fechaInicial = vistahistorialReservas.jDateChooser1.getDate();
            java.util.Date fechaFinal = vistahistorialReservas.jDateChooser2.getDate();
            String estadoReserva = (String) vistahistorialReservas.jComboBox1.getSelectedItem();

            // Verificar si se ha seleccionado una fecha
            if (fechaInicial != null && fechaFinal != null) {
                // Comprobar si la fecha inicial es mayor que la fecha final
                if (fechaInicial.after(fechaFinal)) {
                    UIManager.put("OptionPane.okButtonText", "Aceptar");
                    JOptionPane.showMessageDialog(null, "La fecha inicial no puede ser mayor que la fecha final.", "Formato inválido", JOptionPane.ERROR_MESSAGE);
                } else {
                    // Las fechas son válidas
                    System.out.println("Rango de fechas válido: " + fechaInicial + " - " + fechaFinal);
                    // Convertir las fechas de java.util.Date a java.sql.Date
                    java.sql.Date fechaSqlInicial = new java.sql.Date(fechaInicial.getTime());
                    java.sql.Date fechaSqlFinal = new java.sql.Date(fechaFinal.getTime());
                    llenarTablaReservas(vistahistorialReservas.tablaReserva, estadoReserva, fechaSqlInicial, fechaSqlFinal);

                    // Calcular el total de la columna "total"
                    double total = calcularTotalColumna(vistahistorialReservas.tablaReserva, 3);
                    // Formatear el total a dos decimales

                    if ("Entregado".equals(estadoReserva)) {
                        String totalFormateado = String.format("%.2f", total);
                        // Establecer el texto del JLabel con el total formateado
                        vistahistorialReservas.jLabelGanancias.setText(totalFormateado);
                        vistahistorialReservas.jLabelGananciasTitulo.setVisible(true);
                        vistahistorialReservas.jLabelGanancias.setVisible(true);
                        vistahistorialReservas.jLabelCompensasionesTitulo.setVisible(false);
                        vistahistorialReservas.jLabelCompensasiones.setVisible(false);
                    } else if ("Cancelada".equals(estadoReserva)) {
                        double compensasion = total / 2;
                        String compensasionFormateada = String.format("%.2f", compensasion);
                        vistahistorialReservas.jLabelCompensasiones.setText(compensasionFormateada);
                        vistahistorialReservas.jLabelCompensasionesTitulo.setVisible(true);
                        vistahistorialReservas.jLabelCompensasiones.setVisible(true);
                        vistahistorialReservas.jLabelGananciasTitulo.setVisible(false);
                        vistahistorialReservas.jLabelGanancias.setVisible(false);
                    }

                    vistahistorialReservas.btnImprimir.setEnabled(true);
                }
            } else {
                // No se ha seleccionado ninguna fecha
                JOptionPane optionPane = new JOptionPane("No se ha seleccionado ninguna fecha.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
                optionPane.setOptions(new Object[]{"Aceptar"});
                JDialog dialog = optionPane.createDialog(vistahistorialReservas, "Error");
                dialog.setVisible(true);
            }
        } else if (e.getSource() == vistahistorialReservas.btnLimpiar) {
            limpiar();
        } else if (e.getSource() == vistahistorialReservas.btnImprimir) {
            System.out.println("imprimiendo...");
            java.util.Date fechaInicialUtil = vistahistorialReservas.jDateChooser1.getDate();
            java.util.Date fechaFinalUtil = vistahistorialReservas.jDateChooser2.getDate();

            // Convertir java.util.Date a java.sql.Date
            java.sql.Date fechaInicialSql = new java.sql.Date(fechaInicialUtil.getTime());
            java.sql.Date fechaFinalSql = new java.sql.Date(fechaFinalUtil.getTime());
            String estadoReserva = (String) vistahistorialReservas.jComboBox1.getSelectedItem();
            // Llamar a la función de impresión pasando las fechas convertidas
            if ("Entregado".equals(estadoReserva)) {
                imprimirReporteReservas(vistahistorialReservas.tablaReserva, Double.parseDouble(vistahistorialReservas.jLabelGanancias.getText()), estadoReserva, fechaInicialSql, fechaFinalSql);
            } else if ("Cancelada".equals(estadoReserva)) {
                imprimirReporteReservas(vistahistorialReservas.tablaReserva, Double.parseDouble(vistahistorialReservas.jLabelCompensasiones.getText()), estadoReserva, fechaInicialSql, fechaFinalSql);
            }

        }
    }

    public void llenarTablaReservas(JTable tablaReservas, String estado, Date fechaInicial, Date fechaFinal) {
        DefaultTableModel model = (DefaultTableModel) tablaReservas.getModel();
        model.setRowCount(0); // Limpiar la tabla antes de agregar nuevos datos

        // Consulta SQL para obtener los datos de reserva según el estado y el periodo de tiempo proporcionados
        String queryReservas = "SELECT r.id_reserva, r.Fecha_entrega, r.id_venta, v.fecha, v.total, c.Nombre, c.Apellido "
                + "FROM Reserva r "
                + "INNER JOIN Venta v ON r.id_venta = v.id_venta "
                + "INNER JOIN Cliente c ON r.id_cliente = c.id_cliente "
                + "WHERE r.Estado = ? AND r.Fecha_entrega BETWEEN ? AND ?";

        // Consulta SQL para obtener los datos de pago asociados a cada reserva
        String queryPagos = "SELECT tipo_pago "
                + "FROM Pago "
                + "WHERE id_venta = ?";

        Conexion conexion = new Conexion();
        try (Connection conn = conexion.getConnection(); PreparedStatement statementReservas = conn.prepareStatement(queryReservas); PreparedStatement statementPagos = conn.prepareStatement(queryPagos)) {

            // Establecer los parámetros de la consulta de reservas
            statementReservas.setString(1, estado);
            statementReservas.setDate(2, fechaInicial);
            statementReservas.setDate(3, fechaFinal);

            // Ejecutar la consulta de reservas
            ResultSet resultSetReservas = statementReservas.executeQuery();

            // Iterar sobre los resultados de reservas
            while (resultSetReservas.next()) {
                int idReserva = resultSetReservas.getInt("id_reserva");
                Date fechaEntrega = resultSetReservas.getDate("Fecha_entrega");
                int idVenta = resultSetReservas.getInt("id_venta");
                Date fechaVenta = resultSetReservas.getDate("fecha");
                double totalVenta = resultSetReservas.getDouble("total");
                String nombreCliente = resultSetReservas.getString("Nombre");
                String apellidoCliente = resultSetReservas.getString("Apellido");

                // Establecer el parámetro de la consulta de pagos
                statementPagos.setInt(1, idVenta);

                // Ejecutar la consulta de pagos
                ResultSet resultSetPagos = statementPagos.executeQuery();

                // Inicializar las variables para almacenar los tipos de pago
                String tipoPago1 = "";
                String tipoPago2 = "";

                // Iterar sobre los resultados de pagos
                int i = 1;
                while (resultSetPagos.next() && i <= 2) { // Solo tomamos hasta dos pagos
                    String tipoPago = resultSetPagos.getString("tipo_pago");
                    if (i == 1) {
                        tipoPago1 = tipoPago;
                    } else {
                        tipoPago2 = tipoPago;
                    }
                    i++;
                }

                // Agregar la fila con los datos de la reserva y la venta a la tabla
                model.addRow(new Object[]{idReserva, fechaVenta, fechaEntrega, totalVenta, tipoPago1, tipoPago2, nombreCliente + " " + apellidoCliente});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane optionPane = new JOptionPane("Error al cargar las reservas.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
            optionPane.setOptions(new Object[]{"Aceptar"});
            JDialog dialog = optionPane.createDialog(vistahistorialReservas, "Error");
            dialog.setVisible(true);
        }
    }

    public double calcularTotalColumna(JTable tabla, int columna) {
        double total = 0.0;

        // Obtener el modelo de la tabla
        DefaultTableModel model = (DefaultTableModel) tabla.getModel();

        // Iterar sobre cada fila de la tabla y sumar los valores de la columna especificada
        for (int i = 0; i < model.getRowCount(); i++) {
            // Obtener el valor de la celda en la fila i y columna especificada
            Object value = model.getValueAt(i, columna);
            // Convertir el valor a tipo double y sumarlo al total
            total += Double.parseDouble(value.toString());
        }

        return total;
    }

    public void limpiar() {
        // Limpiar la selección de fechas
        vistahistorialReservas.jDateChooser1.setDate(null);
        vistahistorialReservas.jDateChooser2.setDate(null);

        // Limpiar la tabla
        DefaultTableModel model = (DefaultTableModel) vistahistorialReservas.tablaReserva.getModel();
        model.setRowCount(0);

        // Limpiar el JTextArea
        vistahistorialReservas.jTextAreaDetalleVenta.setText("");

        // Deshabilitar el botón de imprimir
        vistahistorialReservas.btnImprimir.setEnabled(false);
        vistahistorialReservas.jLabelGananciasTitulo.setVisible(false);
        vistahistorialReservas.jLabelGanancias.setVisible(false);
        vistahistorialReservas.jLabelCompensasionesTitulo.setVisible(false);
        vistahistorialReservas.jLabelCompensasiones.setVisible(false);
    }

    public int obtenerIdVenta(Reserva reserva) {
        int idVenta = -1; // Valor predeterminado en caso de no encontrar ningún ID de venta

        // Establecer la conexión a la base de datos y ejecutar la consulta
        Conexion conexion = new Conexion();
        String query = "SELECT id_venta FROM Reserva WHERE id_reserva = ?";
        try (Connection conn = conexion.getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, reserva.getId_reserva());
            ResultSet resultSet = statement.executeQuery();

            // Si se encuentra un ID de venta, asignarlo y salir del bucle
            if (resultSet.next()) {
                idVenta = resultSet.getInt("id_venta");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return idVenta;
    }

    public List<String> obtenerDetalleVenta(int idVenta) {
        List<String> detalles = new ArrayList<>();
        // Consulta SQL para obtener el detalle de venta
        String query = "SELECT p.nombre, dv.cantidad_vendida, dv.precio_unitario "
                + "FROM detalleVenta dv "
                + "INNER JOIN Producto p ON dv.id_producto = p.id_producto "
                + "WHERE dv.id_venta = ?";

        Conexion conexion = new Conexion();
        try (Connection conn = conexion.getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
            // Establecer el parámetro de la consulta
            statement.setInt(1, idVenta);

            // Ejecutar la consulta
            ResultSet resultSet = statement.executeQuery();

            // Iterar sobre los resultados y agregar los detalles al listado
            while (resultSet.next()) {
                String nombreProducto = resultSet.getString("nombre");
                int cantidadVendida = resultSet.getInt("cantidad_vendida");
                double precioUnitario = resultSet.getDouble("precio_unitario");

                String detalle = String.format("Producto: %s - Cantidad: %d - Precio Unitario: %.2f", nombreProducto, cantidadVendida, precioUnitario);
                detalles.add(detalle);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al obtener el detalle de la venta.", "Error", JOptionPane.ERROR_MESSAGE);
        }
// Mensaje de depuración
        System.out.println("Detalles obtenidos para la venta #" + idVenta + ": " + detalles);
        return detalles;
    }

    public void mostrarDetalleVenta(int idVenta) {
        // Consulta SQL para obtener el detalle de la venta
        String query = "SELECT p.nombre, dv.cantidad_vendida, dv.precio_unitario "
                + "FROM detalleVenta dv "
                + "INNER JOIN producto p ON dv.id_producto = p.id_producto "
                + "WHERE dv.id_venta = ?";

        Conexion conexion = new Conexion();
        try (Connection conn = conexion.getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
            // Establecer el parámetro de la consulta
            statement.setInt(1, idVenta);

            // Ejecutar la consulta
            ResultSet resultSet = statement.executeQuery();

            // Crear un StringBuilder para construir el texto con formato
            StringBuilder detalleVentaTexto = new StringBuilder();

            // Iterar sobre los resultados y agregar cada línea al StringBuilder
            while (resultSet.next()) {
                String nombreProducto = resultSet.getString("nombre");
                int cantidadVendida = resultSet.getInt("cantidad_vendida");
                double precioUnitario = resultSet.getDouble("precio_unitario");

                detalleVentaTexto.append(" - Producto: ").append(nombreProducto).append("\n")
                        .append("      Cantidad: ").append(cantidadVendida).append("\n")
                        .append("      Precio Unitario: ").append(String.format("%.2f", precioUnitario)).append("\n\n");
            }

            // Mostrar el detalle de la venta en el JTextArea
            vistahistorialReservas.jTextAreaDetalleVenta.setText(detalleVentaTexto.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane optionPane = new JOptionPane("Error al mostrar el detalle de la venta.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
            optionPane.setOptions(new Object[]{"Aceptar"});
            JDialog dialog = optionPane.createDialog(vistahistorialReservas, "Error");
            dialog.setVisible(true);
        }
    }

    public void imprimirReporteReservas(JTable tablaVentas, double totalGanancias, String estadoReservas, Date fechaInicial, Date fechaFinal) {
        try {

            // Formatear las fechas para incluirlas en el nombre del archivo
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String fechaInicialStr = dateFormat.format(fechaInicial);
            String fechaFinalStr = dateFormat.format(fechaFinal);

            // Ruta y nombre de archivo para guardar el reporte PDF
            String fileName = "C:/Users/akram/Documents/NetBeansProjects/teddyTrack/reportes/reporte_reservas_" + estadoReservas + "_" + fechaInicialStr + "_a_" + fechaFinalStr + ".pdf";

            // Inicializar un nuevo documento PDF
            PdfWriter writer = new PdfWriter(fileName);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);

            // Agregar título al documento
            PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
            PdfFont font1 = PdfFontFactory.createFont(FontConstants.HELVETICA);
            float tamañoFuenteTitulo = 20;
            document.add(new Paragraph("Reporte de Reservas").setFont(font).setFontSize(tamañoFuenteTitulo)
                    .setTextAlignment(TextAlignment.CENTER));

            Paragraph espacioEnBlanco = new Paragraph("\n");
            document.add(espacioEnBlanco);

            //Agregar rango de fechas:
            document.add(new Paragraph("Periodo: " + fechaInicialStr + " a " + fechaFinalStr)
                    .setFont(font).setTextAlignment(TextAlignment.RIGHT).setMultipliedLeading(0.5f));

            // Agregar estado reservas 
            document.add(new Paragraph("Estado Reservas: " + estadoReservas)
                    .setFont(font).setTextAlignment(TextAlignment.RIGHT).setMultipliedLeading(0.5f));

            if ("Entregado".equals(estadoReservas)) {
                // Agregar total de ganancias al final del documento
                document.add(new Paragraph("Total de Ganancias: $" + totalGanancias)
                        .setFont(font).setTextAlignment(TextAlignment.RIGHT).setMultipliedLeading(0.5f));
            } else if ("Cancelada".equals(estadoReservas)) {
                // Agregar total de ganancias al final del documento
                document.add(new Paragraph("Total de Compensasiones: $" + totalGanancias)
                        .setFont(font).setTextAlignment(TextAlignment.RIGHT).setMultipliedLeading(0.5f));
            }

            document.add(espacioEnBlanco);

            // Agregar tabla con datos de ventas
            Table table = new Table(tablaVentas.getColumnCount());
            for (int i = 0; i < tablaVentas.getColumnCount(); i++) {
                table.addHeaderCell(tablaVentas.getColumnName(i));
            }
            for (int i = 0; i < tablaVentas.getRowCount(); i++) {
                for (int j = 0; j < tablaVentas.getColumnCount(); j++) {
                    table.addCell(String.valueOf(tablaVentas.getValueAt(i, j)));
                }
            }
            document.add(table);
            document.add(espacioEnBlanco);
            document.add(new Paragraph("Detalle de ventas").setFont(font).setFontSize(tamañoFuenteTitulo)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(espacioEnBlanco);

            // Agregar detalle de cada venta
            for (int i = 0; i < tablaVentas.getRowCount(); i++) {
                // Obtener el ID de la reserva desde la tabla
                int idReserva = (int) tablaVentas.getValueAt(i, 0);

                // Crear una instancia de Reserva con el ID obtenido
                Reserva reserva = new Reserva();
                reserva.setId_reserva(idReserva);

                // Obtener el ID de venta asociado a la reserva
                int idVenta = obtenerIdVenta(reserva);

                // Obtener los detalles de la venta utilizando el ID de venta obtenido
                List<String> detallesVenta = obtenerDetalleVenta(idVenta);

                // Agregar el detalle de la venta al documento PDF
                document.add(new Paragraph("Detalle de Venta de Reserva #" + idReserva)
                        .setFont(font).setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(0.5f));
                for (String detalle : detallesVenta) {
                    document.add(new Paragraph(detalle)
                            .setFont(font1).setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(0.5f));
                }
                document.add(espacioEnBlanco);
            }

            // Cerrar el documento
            document.close();

            // Abrir el archivo PDF generado
            Desktop.getDesktop().open(new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane optionPane = new JOptionPane("Error al imprimir el reporte de reservas.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
            optionPane.setOptions(new Object[]{"Aceptar"});
            JDialog dialog = optionPane.createDialog(vistahistorialReservas, "Error");
            dialog.setVisible(true);
        }
    }
}
