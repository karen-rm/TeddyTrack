
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
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import modelo.Conexion;
import modelo.Pago;
import modelo.Venta;
import vista.HistorialVentas;
import vista.Inicio;

public class Control_HistorialVentas implements ActionListener{
    
    private HistorialVentas vistahistorialVentas;
    private Inicio vistaInicio;
    private Venta venta;
    private Pago pago; 
    
    Control_HistorialVentas(Inicio vistaInicio, HistorialVentas vistaHistorialVentas){
        this.vistaInicio = vistaInicio;
        this.vistahistorialVentas = vistaHistorialVentas; 
        
        vistahistorialVentas.btnRegresar.addActionListener(this);
        vistahistorialVentas.btnConsultar.addActionListener(this);
        vistahistorialVentas.btnLimpiar.addActionListener(this);
        vistahistorialVentas.btnImprimir.addActionListener(this);
        
        
        vistahistorialVentas.btnRegresar.setBorderPainted(false);
        vistahistorialVentas.btnRegresar.setBackground(Color.WHITE);
        
        
        vistahistorialVentas.btnConsultar.setBackground(Color.WHITE);
        
        
        vistahistorialVentas.btnLimpiar.setBackground(Color.WHITE);
        
        
        vistahistorialVentas.btnImprimir.setBackground(Color.WHITE);
        vistahistorialVentas.btnImprimir.setEnabled(false);
        
        // Agregar un ListSelectionListener a la tabla
        vistahistorialVentas.tablaVenta.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Verificar si la selección cambió y no está ajustándose
                if (!e.getValueIsAdjusting()) {
                    // Obtener el índice de la fila seleccionada
                    int selectedRow = vistahistorialVentas.tablaVenta.getSelectedRow();

                    // Verificar si se seleccionó una fila válida
                    if (selectedRow != -1) {
                        // Obtener el valor de la celda en la columna que contiene el ID de la venta
                        int idVenta = (int) vistahistorialVentas.tablaVenta.getValueAt(selectedRow, 0);

                        // Llamar al método para mostrar el detalle de la venta
                        mostrarDetalleVenta(idVenta);
                    }
                }
            }
        });

        // Limpiar la tabla
        DefaultTableModel model = (DefaultTableModel) vistahistorialVentas.tablaVenta.getModel();
        model.setRowCount(0);
        
        vistahistorialVentas.jLabelGananciasTitulo.setVisible(false);
        vistahistorialVentas.jLabelGanancias.setVisible(false); 
        vistahistorialVentas.setLocationRelativeTo(null);
        
        
    
    }

    public Control_HistorialVentas() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        System.out.println("Action performed: " + e.getActionCommand());
       
        if (e.getSource() == vistahistorialVentas.btnRegresar) {
            System.out.println("regresando...");
            vistahistorialVentas.dispose();
            vistaInicio.setVisible(true);
        }else if(e.getSource() == vistahistorialVentas.btnConsultar){
            System.out.println("consultando...");
        java.util.Date fechaInicial = vistahistorialVentas.jDateChooser1.getDate();
        java.util.Date fechaFinal = vistahistorialVentas.jDateChooser2.getDate();
        
        // Verificar si se ha seleccionado una fecha
        if (fechaInicial != null && fechaFinal != null) {
            
            // Comprobar si la fecha inicial es mayor que la fecha final
            if (fechaInicial.after(fechaFinal)) {
                JOptionPane optionPane = new JOptionPane("La fecha inicial no puede ser mayor que la fecha final.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
                optionPane.setOptions(new Object[]{"Aceptar"});
                JDialog dialog = optionPane.createDialog(vistahistorialVentas, "Error");
                dialog.setVisible(true);
            } else {
                // Las fechas son válidas
                System.out.println("Rango de fechas válido: " + fechaInicial + " - " + fechaFinal);
                // Convertir las fechas de java.util.Date a java.sql.Date
                java.sql.Date fechaSqlInicial = new java.sql.Date(fechaInicial.getTime());
                java.sql.Date fechaSqlFinal = new java.sql.Date(fechaFinal.getTime());
                llenarTablaVentas(vistahistorialVentas.tablaVenta , fechaSqlInicial, fechaSqlFinal);
                calcularTotalVentas(fechaSqlInicial, fechaSqlFinal);
                vistahistorialVentas.jLabelGananciasTitulo.setVisible(true);
                vistahistorialVentas.jLabelGanancias.setVisible(true); 
                vistahistorialVentas.btnImprimir.setEnabled(true);
            }
        } else {
            // No se ha seleccionado ninguna fecha
            JOptionPane optionPane = new JOptionPane("No se ha seleccionado ninguna fecha.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
            optionPane.setOptions(new Object[]{"Aceptar"});
            JDialog dialog = optionPane.createDialog(vistahistorialVentas, "Error");
            dialog.setVisible(true);
        }

            
        }else if(e.getSource() ==  vistahistorialVentas.btnImprimir){
            System.out.println("imprimiendo...");
           java.util.Date fechaInicialUtil = vistahistorialVentas.jDateChooser1.getDate();
            java.util.Date fechaFinalUtil = vistahistorialVentas.jDateChooser2.getDate();

            // Convertir java.util.Date a java.sql.Date
            java.sql.Date fechaInicialSql = new java.sql.Date(fechaInicialUtil.getTime());
            java.sql.Date fechaFinalSql = new java.sql.Date(fechaFinalUtil.getTime());

            // Llamar a la función de impresión pasando las fechas convertidas
            imprimirReporteVentas(vistahistorialVentas.tablaVenta, Double.parseDouble(vistahistorialVentas.jLabelGanancias.getText()), fechaInicialSql, fechaFinalSql);

        }else if(e.getSource() == vistahistorialVentas.btnLimpiar){
            limpiar();
        }
           
        
    }
    
    public void llenarTablaVentas(JTable tablaVentas, Date fechaInicial, Date fechaFinal) {
        DefaultTableModel model = (DefaultTableModel) tablaVentas.getModel();
        model.setRowCount(0); // Limpiar la tabla antes de agregar nuevos datos
    
        // Consulta SQL para obtener las ventas en el rango de fechas especificado
        String queryVentas = "SELECT id_venta, fecha, total FROM venta " +
                             "WHERE fecha BETWEEN ? AND ? AND es_reserva = false";

        // Consulta SQL para obtener los tipos de pago asociados a cada venta
        String queryTiposPago = "SELECT tipo_pago FROM pago WHERE id_venta = ?";

    Conexion conexion = new Conexion();
    try (Connection conn = conexion.getConnection();
         PreparedStatement statementVentas = conn.prepareStatement(queryVentas);
         PreparedStatement statementTiposPago = conn.prepareStatement(queryTiposPago)) {
        
        // Establecer los parámetros de la consulta de ventas
        statementVentas.setDate(1, fechaInicial);
        statementVentas.setDate(2, fechaFinal);
        
        // Ejecutar la consulta de ventas
        ResultSet resultSetVentas = statementVentas.executeQuery();

        // Iterar sobre los resultados de ventas
        while (resultSetVentas.next()) {
            int idVenta = resultSetVentas.getInt("id_venta");
            Date fechaVenta = resultSetVentas.getDate("fecha");
            double totalVenta = resultSetVentas.getDouble("total");
            
            // Establecer el ID de venta en la consulta de tipos de pago
            statementTiposPago.setInt(1, idVenta);
            ResultSet resultSetTiposPago = statementTiposPago.executeQuery();
            
            // Construir una cadena con los tipos de pago asociados a la venta
            StringBuilder tiposPagoBuilder = new StringBuilder();
            while (resultSetTiposPago.next()) {
                tiposPagoBuilder.append(resultSetTiposPago.getString("tipo_pago")).append(" ");
            }
            String tiposPago = tiposPagoBuilder.toString();
            
            // Agregar la fila con los datos de la venta a la tabla
            model.addRow(new Object[]{idVenta, fechaVenta, totalVenta, tiposPago});
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane optionPane = new JOptionPane( "Error al cargar las ventas.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
        optionPane.setOptions(new Object[]{"Aceptar"});
        JDialog dialog = optionPane.createDialog(vistahistorialVentas, "Error");
        dialog.setVisible(true);
    }
}
    
    public void calcularTotalVentas(Date fechaInicial, Date fechaFinal) {
    // Consulta SQL para obtener el total de todas las ventas (excluyendo las reservas) en el periodo especificado
    String query = "SELECT SUM(total) AS total_ventas FROM venta WHERE fecha BETWEEN ? AND ? AND es_reserva = false";
    
    Conexion conexion = new Conexion();
    try (Connection conn = conexion.getConnection();
         PreparedStatement statement = conn.prepareStatement(query)) {
        // Establecer los parámetros de la consulta
        statement.setDate(1, fechaInicial);
        statement.setDate(2, fechaFinal);
        
        // Ejecutar la consulta
        ResultSet resultSet = statement.executeQuery();

        // Verificar si se encontraron resultados
        if (resultSet.next()) {
            // Obtener el total de ventas
            double totalVentas = resultSet.getDouble("total_ventas");
            
            // Asignar el total de ventas al jLabelGanancias
            vistahistorialVentas.jLabelGanancias.setText(String.valueOf(totalVentas));
        } else {
            // No se encontraron resultados
            vistahistorialVentas.jLabelGanancias.setText("0.0");
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane optionPane = new JOptionPane( "Error al calcular el total de ventas.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
        optionPane.setOptions(new Object[]{"Aceptar"});
        JDialog dialog = optionPane.createDialog(vistahistorialVentas, "Error");
        dialog.setVisible(true);
    }
}

    public List<String> obtenerDetalleVenta(int idVenta) {
        List<String> detalles = new ArrayList<>();
        // Consulta SQL para obtener el detalle de venta
String query = "SELECT p.nombre, dv.cantidad_vendida, dv.precio_unitario " +
               "FROM detalleVenta dv " +
               "INNER JOIN Producto p ON dv.id_producto = p.id_producto " +
               "WHERE dv.id_venta = ?";

Conexion conexion = new Conexion();
try (Connection conn = conexion.getConnection();
     PreparedStatement statement = conn.prepareStatement(query)) {
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

return detalles;
    }

    public void mostrarDetalleVenta(int idVenta) {
    // Consulta SQL para obtener el detalle de la venta
    String query = "SELECT p.nombre, dv.cantidad_vendida, dv.precio_unitario " +
                   "FROM detalleVenta dv " +
                   "INNER JOIN producto p ON dv.id_producto = p.id_producto " +
                   "WHERE dv.id_venta = ?";

    Conexion conexion = new Conexion();
    try (Connection conn = conexion.getConnection();
         PreparedStatement statement = conn.prepareStatement(query)) {
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
        vistahistorialVentas.jTextAreaDetalleVenta.setText(detalleVentaTexto.toString());
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane optionPane = new JOptionPane( "Error al mostrar el detalle de la venta.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
        optionPane.setOptions(new Object[]{"Aceptar"});
        JDialog dialog = optionPane.createDialog(vistahistorialVentas, "Error");
        dialog.setVisible(true);
    }
}
    
    public void imprimirReporteVentas(JTable tablaVentas, double totalGanancias,  Date fechaInicial, Date fechaFinal) {
        try {
        
        // Formatear las fechas para incluirlas en el nombre del archivo
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String fechaInicialStr = dateFormat.format(fechaInicial);
        String fechaFinalStr = dateFormat.format(fechaFinal);
        
        // Ruta y nombre de archivo para guardar el reporte PDF
        String fileName = "C:/Users/akram/Documents/NetBeansProjects/teddyTrack/reportes/reporte_ventas_" + fechaInicialStr + "_a_" + fechaFinalStr + ".pdf";

        // Inicializar un nuevo documento PDF
        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);

        // Agregar título al documento
        PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
        PdfFont font1 = PdfFontFactory.createFont(FontConstants.HELVETICA);
        float tamañoFuenteTitulo = 20;
        document.add(new Paragraph("Reporte de Ventas").setFont(font).setFontSize(tamañoFuenteTitulo)
                .setTextAlignment(TextAlignment.CENTER));
        
        Paragraph espacioEnBlanco = new Paragraph("\n");
                    document.add(espacioEnBlanco);
        
        //Agregar rango de fechas:
        document.add(new Paragraph("Periodo: " + fechaInicialStr + " a " + fechaFinalStr)
                .setFont(font).setTextAlignment(TextAlignment.RIGHT).setMultipliedLeading(0.5f) );
        
        // Agregar total de ganancias al final del documento
        document.add(new Paragraph("Total de Ganancias: $" + totalGanancias)
                .setFont(font).setTextAlignment(TextAlignment.RIGHT).setMultipliedLeading(0.5f) );
        
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
            int idVenta = (int) tablaVentas.getValueAt(i, 0);
            List<String> detallesVenta = obtenerDetalleVenta(idVenta);
            document.add(new Paragraph("Detalle de Venta #" + idVenta)
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
        JOptionPane optionPane = new JOptionPane( "Error al imprimir el reporte de ventas.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
        optionPane.setOptions(new Object[]{"Aceptar"});
        JDialog dialog = optionPane.createDialog(vistahistorialVentas, "Error");
        dialog.setVisible(true);
    }
}
    
    public void limpiar() {
    // Limpiar la selección de fechas
    vistahistorialVentas.jDateChooser1.setDate(null);
    vistahistorialVentas.jDateChooser2.setDate(null);
    
    // Limpiar la tabla
    DefaultTableModel model = (DefaultTableModel) vistahistorialVentas.tablaVenta.getModel();
    model.setRowCount(0);
    
    // Limpiar el JTextArea
    vistahistorialVentas.jTextAreaDetalleVenta.setText("");
    
    // Deshabilitar el botón de imprimir
    vistahistorialVentas.btnImprimir.setEnabled(false);
    vistahistorialVentas.jLabelGananciasTitulo.setVisible(false);
        vistahistorialVentas.jLabelGanancias.setVisible(false); 
}



}
