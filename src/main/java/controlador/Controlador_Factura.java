package controlador;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import modelo.DetalleVentas;
import vista.Factura;
import vista.Inicio;
import vista.Ticket;

public class Controlador_Factura implements ActionListener {

    private Factura factura;
    private double total;
    private List<DetalleVentas> detalleVentaLista;
    int contador = 0;
    // Variable para almacenar el último número de factura generado
    private int ultimoNumeroFactura;
    private Ticket recibo;
    private String tipoPago; 
    private Date fechaE;
    private String modalidad; 
    private LocalDate fechaInicial; 

    //Controlador Factura para una venta normal 
    public Controlador_Factura(Factura factura, List<DetalleVentas> detalleVentaLista, double total, String tipoPago, String modalidad) {
        this.factura = factura;
        this.total = total;
        this.tipoPago = tipoPago; 
        this.modalidad = modalidad; 
        factura.setLocationRelativeTo(null);
        this.detalleVentaLista = detalleVentaLista;

        /*factura.nombre.setEnabled(true);
        factura.dir.setEnabled(true);
        factura.cp.setEnabled(true);
        factura.correo.setEnabled(true);
        factura.rfc.setEnabled(true);*/
        this.factura.btnContinuar.addActionListener(this);
        factura.btnRegresar.addActionListener(this);
    
        factura.btnRegresar.setBorderPainted(false);
        factura.btnRegresar.setBackground(java.awt.Color.WHITE);

        factura.setVisible(true);
        SwingUtilities.invokeLater(() -> {
            factura.toFront();
        });

    }
    
    //Controlador Factura para una reserva 
    public Controlador_Factura(Factura factura, List<DetalleVentas> detalleVentaLista, double total, String tipoPago, String modalidad, Date fechaEntrega) {
        this.factura = factura;
        this.total = total;
        this.tipoPago = tipoPago; 
        this.fechaE = fechaEntrega; 
        this.modalidad = modalidad; 
        factura.setLocationRelativeTo(null);
        this.detalleVentaLista = detalleVentaLista;

        /*factura.nombre.setEnabled(true);
        factura.dir.setEnabled(true);
        factura.cp.setEnabled(true);
        factura.correo.setEnabled(true);
        factura.rfc.setEnabled(true);*/
        this.factura.btnContinuar.addActionListener(this);
        factura.btnRegresar.addActionListener(this);
    
        factura.btnRegresar.setBorderPainted(false);
        factura.btnRegresar.setBackground(java.awt.Color.WHITE);

        factura.setVisible(true);
        SwingUtilities.invokeLater(() -> {
            factura.toFront();
        });

    }
    
    //Controlador factura para un pago de reserva completado
    public Controlador_Factura(Factura factura, List<DetalleVentas> detalleVentaLista, double total, String tipoPago, String modalidad, LocalDate fechaInicial) {
        this.factura = factura;
        this.total = total;
        this.tipoPago = tipoPago; 
        this.fechaInicial = fechaInicial; 
        this.modalidad = modalidad; 
        factura.setLocationRelativeTo(null);
        this.detalleVentaLista = detalleVentaLista;

        
        this.factura.btnContinuar.addActionListener(this);
        factura.btnRegresar.addActionListener(this);
    
        factura.btnRegresar.setBorderPainted(false);
        factura.btnRegresar.setBackground(java.awt.Color.WHITE);

        factura.setVisible(true);
        SwingUtilities.invokeLater(() -> {
            factura.toFront();
        });

    }
    
    

    public void crearFactura(List<DetalleVentas> detalleVentaLista, double total) {
        String nombreCliente = factura.nombre.getText();
        String direccionCliente = factura.dir.getText();
        String codigoPostal = factura.cp.getText();
        String correo = factura.correo.getText();
        String rfc = factura.rfc.getText();
        String regimenFiscal = (String) factura.jComboBox1.getSelectedItem();
        String cfdi = (String) factura.jComboBox2.getSelectedItem();
        // Incrementar el contador para obtener el nuevo número de factura
        contador = ++ultimoNumeroFactura;

        if (nombreCliente.isEmpty() || direccionCliente.isEmpty() || codigoPostal.isEmpty()
                || correo.isEmpty() || rfc.isEmpty() || regimenFiscal.isEmpty() || cfdi.isEmpty()) {
            JOptionPane optionPane = new JOptionPane("Por favor ingrese todos los datos del formulario", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
            optionPane.setOptions(new Object[]{"Aceptar"});
            JDialog dialog = optionPane.createDialog(factura, "Error");
            dialog.setVisible(true);
        } else if (!validarCampos(codigoPostal, rfc)) {
            JOptionPane optionPane = new JOptionPane("El código postal debe tener 5 dígitos y el RFC debe tener 13 caracteres.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
            optionPane.setOptions(new Object[]{"Aceptar"});
            JDialog dialog = optionPane.createDialog(factura, "Error");
            dialog.setVisible(true);
        } else if (!validarCorreo(correo)) {
            JOptionPane optionPane = new JOptionPane("El correo ingresado no tiene un formato válido.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
            optionPane.setOptions(new Object[]{"Aceptar"});
            JDialog dialog = optionPane.createDialog(factura, "Error");
            dialog.setVisible(true);
        } else {
            if (confimarFormularioFactura(nombreCliente, direccionCliente, codigoPostal, correo, rfc, regimenFiscal, cfdi)) {
                // Nombre del archivo PDF
                String nombreArchivo = "Factura_" + rfc + ".pdf";

                try {
                    // Crear un nuevo documento PDF
                    PdfWriter writer = new PdfWriter(nombreArchivo);
                    PdfDocument pdf = new PdfDocument(writer);
                    Document document = new Document(pdf, PageSize.A4);

                    // Agregar contenido al documento PDF
                    document.setMargins(50, 50, 50, 50);

                    // Agregar factura
                    Paragraph factura = new Paragraph("FACTURA")
                            .setTextAlignment(TextAlignment.RIGHT)
                            .setMultipliedLeading(0.5f) // Reducir el interlineado al 80% del valor predeterminado
                            .setBold();
                    document.add(factura);

                    // Agregar fecha
                    String fecha = "Fecha: " + obtenerFechaActual();
                    Paragraph fechaParagraph = new Paragraph(fecha)
                            .setTextAlignment(TextAlignment.RIGHT)
                            .setMultipliedLeading(0.5f) // Reducir el interlineado al 80% del valor predeterminado
                            .setBold();
                    document.add(fechaParagraph);

                    // Agregar número de factura
                    String numeroFactura = "N° factura: " + (++contador);
                    ultimoNumeroFactura = contador;
                    Paragraph numeroFacturaParagraph = new Paragraph(numeroFactura)
                            .setTextAlignment(TextAlignment.RIGHT)
                            .setMultipliedLeading(0.5f) // Reducir el interlineado al 80% del valor predeterminado
                            .setBold();
                    document.add(numeroFacturaParagraph);

                    dibujarlinea(pdf, PageSize.A4.getHeight() - 115);
                    Paragraph espacioEnBlanco = new Paragraph("\n");
                    document.add(espacioEnBlanco);

                    // Agregar empresa emisora y cliente en formato de tabla de 2 columnas
                    float[] columnWidths = {200f, 200f}; // Ancho de las dos columnas
                    Table table = new Table(columnWidths);
                    table.setWidth(UnitValue.createPercentValue(100));

                    // Datos de la empresa
                    Cell empresaCell = new Cell().add(new Paragraph("Datos de la empresa"))
                            .setBold();
                    empresaCell.setBorder(Border.NO_BORDER);
                    table.addCell(empresaCell);

                    Cell clienteCell = new Cell().add(new Paragraph("Datos del cliente"))
                            .setBold();
                    clienteCell.setBorder(Border.NO_BORDER);
                    table.addCell(clienteCell);

                    // Datos de la empresa
                    String[] empresaData = {
                        "Empresa Emisora: TeddyTrack",
                        "NIF: B98334964",
                        "Plaza del Centro,5",
                        "08004-PUEBLA(MÉXICO)",
                        "Teléfono: 2224095278",
                        "e-Mail: teddyTrackcompany@gmail.com"
                    };

                    // Datos del cliente
                    String[] clienteData = {
                        "Nombre: " + nombreCliente,
                        "RFC: " + rfc,
                        "Dirección: " + direccionCliente,
                        "Código Postal: " + codigoPostal,
                        "Correo: " + correo,
                        "Regimen Fiscal: " + regimenFiscal,
                        "CFDI: " + cfdi
                    };

                    for (int i = 0; i < Math.max(empresaData.length, clienteData.length); i++) {
                        Paragraph empresaInfo = i < empresaData.length ? new Paragraph(empresaData[i]) : new Paragraph("");
                        Paragraph clienteInfo = i < clienteData.length ? new Paragraph(clienteData[i]) : new Paragraph("");

                        empresaInfo.setBorder(Border.NO_BORDER);
                        clienteInfo.setBorder(Border.NO_BORDER);

                        table.addCell(empresaInfo);
                        table.addCell(clienteInfo);
                    }
                    document.add(table);

                    dibujarlinea(pdf, PageSize.A4.getHeight() - 350);
                    document.add(espacioEnBlanco);
                    document.add(espacioEnBlanco);
                    // Agregar detalles de la compra
                    Paragraph detallesCompraTitulo = new Paragraph("DETALLE DE VENTA")
                            .setBold();
                    document.add(detallesCompraTitulo);

                    // Agregar tabla con los detalles de la compra
                    Table tablaDetallesCompra = new Table(new float[]{2, 2, 2, 2}).setWidthPercent(100);
                    tablaDetallesCompra.addCell(new Cell().add(new Paragraph("Producto")));
                    tablaDetallesCompra.addCell(new Cell().add(new Paragraph("Precio")));
                    tablaDetallesCompra.addCell(new Cell().add(new Paragraph("Cantidad")));
                    tablaDetallesCompra.addCell(new Cell().add(new Paragraph("Total")));

                    for (DetalleVentas detalle : detalleVentaLista) {
                        tablaDetallesCompra.addCell(detalle.getNombre());
                        tablaDetallesCompra.addCell(String.valueOf(detalle.getPrecio()));
                        tablaDetallesCompra.addCell(String.valueOf(detalle.getCantidad()));
                        tablaDetallesCompra.addCell(String.valueOf(detalle.getPrecio() * detalle.getCantidad()));
                    }

                    document.add(tablaDetallesCompra);

                    // Dibujar línea horizontal después de la tabla de detalles de la compra
                    dibujarlinea(pdf, PageSize.A4.getHeight() - 800);
                    document.add(espacioEnBlanco);

                    // Agregar forma de pago y total
                    Paragraph formaPago = new Paragraph("Forma de pago: " + tipoPago)
                            .setMultipliedLeading(0.5f);
                    document.add(formaPago);
                    Paragraph sumaTotal = new Paragraph("TOTAL: " + total)
                            .setMultipliedLeading(0.5f);
                    document.add(sumaTotal);
                    
                    if("Venta".equals(modalidad)){
                        Paragraph importeParrafo = new Paragraph("Importe: " + total)
                                .setMultipliedLeading(0.5f)
                                .setBold();
                        document.add(importeParrafo);
                    }else if("Reserva".equals(modalidad)){
                        String fechapago = "Fecha primer importe: " + obtenerFechaActual();
                        Paragraph fechaParrafo = new Paragraph(fechapago)
                                .setMultipliedLeading(0.5f);
                        document.add(fechaParrafo);
                        
                        SimpleDateFormat formatoDeseado = new SimpleDateFormat("dd/MM/yyyy");
                        // Formatear la fecha al nuevo formato
                        String fechaFormateada = formatoDeseado.format(fechaE);
                        Paragraph fechaEntrega = new Paragraph("Fecha liquidación: " + fechaFormateada)
                                .setMultipliedLeading(0.5f);
                        document.add(fechaEntrega);
                        
                        Paragraph importeParrafo = new Paragraph("Importe pagado: " + (total/2))
                                .setMultipliedLeading(0.5f)
                                .setBold();
                        document.add(importeParrafo);
                    }else if("Reserva2".equals(modalidad)){
                        // Formatear la fecha al nuevo formato directamente desde LocalDate
                        String fechaFormateada = fechaInicial.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        Paragraph fechaEntrega = new Paragraph("Fecha primer importe: " + fechaFormateada)
                            .setMultipliedLeading(0.5f);
                        document.add(fechaEntrega);
                        
                        Paragraph importe1Parrafo = new Paragraph("Pimer importe: " + (total/2))
                                .setMultipliedLeading(0.5f);
                        document.add(importe1Parrafo);
                        
                        String fechapago = "Fecha segundo importe: " + obtenerFechaActual();
                        Paragraph fechaParrafo = new Paragraph(fechapago)
                                .setMultipliedLeading(0.5f);
                        document.add(fechaParrafo);
                        
                        Paragraph importe2Parrafo = new Paragraph("Segundo importe: " + (total/2))
                                .setMultipliedLeading(0.5f)
                                .setBold();
                        document.add(importe2Parrafo);
                        
                        Paragraph estadoReserva = new Paragraph("Pago liquidado")
                                .setMultipliedLeading(0.5f)
                                .setBold();
                        document.add(estadoReserva);
                    }
  
                    // Cerrar el documento
                    document.close();

                    // Mensaje de éxito
                    System.out.println("Factura generada correctamente: " + nombreArchivo);

                    this.factura.dispose();
                    
                    JOptionPane optionPane1 = new JOptionPane("La factura se generó, exitosamente.", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
                    optionPane1.setOptions(new Object[]{"Aceptar"});
                    JDialog dialog1 = optionPane1.createDialog("Fcatura Generada");
                    dialog1.setVisible(true);
                    
                    imprimirTicket();

                } catch (IOException e) {
                    // Manejar cualquier excepción de E/S
                    JOptionPane optionPane2 = new JOptionPane("Error al hacer el pdf ", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
                    optionPane2.setOptions(new Object[]{"Aceptar"});
                    JDialog dialog2 = optionPane2.createDialog("Error");
                    dialog2.setVisible(true);
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean validarCampos(String codigoPostal, String rfc) {
        // Verificar la longitud del código postal y RFC
        boolean codigoPostalValido = codigoPostal.length() == 5;
        boolean rfcValido = rfc.length() == 13;

        // Devolver true si ambos campos son válidos, de lo contrario, false
        return codigoPostalValido && rfcValido;
    }

    public boolean validarCorreo(String correo) {
        // Expresión regular para verificar el formato de un correo electrónico
        String regex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return correo.matches(regex);
    }

    private String obtenerFechaActual() {
        // Aquí puedes obtener la fecha actual y darle el formato que desees
        // por ejemplo:
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        return sdf.format(date);
    }

    public void dibujarlinea(PdfDocument pdf, float yPos) {
        // Dibujar una línea horizontal continua
        PdfCanvas canvas = new PdfCanvas(pdf.getFirstPage());
        Color colorLinea = new DeviceRgb(0, 0, 0); // Color negro
        canvas.setStrokeColor(colorLinea);
        canvas.moveTo(50, yPos) // Coordenadas de inicio
                .lineTo(PageSize.A4.getWidth() - 50, yPos) // Coordenadas de fin
                .closePathStroke();
    }

    public boolean confimarFormularioFactura(String nombre, String direccion, String cp, String correo, String rfc, String rf, String cfdi) {
        JOptionPane optionPane = new JOptionPane(
                "Por favor, verifique los datos del formulario:\n"
                + "Nombre del receptor: " + nombre + "\n"
                + "Dirección: " + direccion + "\n"
                + "Código Postal: " + cp + "\n"
                + "Correo: " + correo + "\n"
                + "RFC: " + rfc + "\n"
                + "Regimen fiscal: " + rf + "\n"
                + "Uso de CFDI: " + cfdi,
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

    public void imprimirTicket() {
        // Crear un cuadro de diálogo de opción con botones "Sí" y "No"
        JOptionPane optionPane = new JOptionPane(
                "¿Desea imprimir el ticket?",
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION
        );

        // Cambiar el texto de los botones a "Sí" y "No"
        optionPane.setOptions(new Object[]{"Sí", "No"});

        // Crear el diálogo con el cuadro de diálogo de opción
        JDialog dialog = optionPane.createDialog("Confirmación");

        // Mostrar el diálogo
        dialog.setVisible(true);

        // Obtener la opción seleccionada por el usuario
        Object selectedValue = optionPane.getValue();

        // Verificar la opción seleccionada
        if (selectedValue.equals("Sí")) {
            this.recibo = new Ticket();
            
            
            if("Venta".equals(modalidad) || "Reserva2".equals(modalidad)){
                
                Control_Recibo ctrlRecibo = new Control_Recibo(recibo, detalleVentaLista, modalidad); 
            }else if("Reserva".equals(modalidad)){
                 
                Control_Recibo ctrlRecibo = new Control_Recibo(recibo, detalleVentaLista, modalidad, fechaE); 
            }
            
            
        } else if (selectedValue.equals("No")) {
            // El usuario seleccionó "No", hacer algo más o simplemente cerrar el diálogo
            JOptionPane optionPane1 = new JOptionPane("Venta finalizada exitosamente.", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
            optionPane1.setOptions(new Object[]{"Aceptar"});
            JDialog dialog1 = optionPane1.createDialog("Venta finalizada");
            dialog1.setVisible(true);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (e.getSource() == factura.btnContinuar) {
            crearFactura(detalleVentaLista, total);
        }else if(e.getSource() == factura.btnRegresar){
            factura.dispose();   
            imprimirTicket(); 
        }

    }

}
