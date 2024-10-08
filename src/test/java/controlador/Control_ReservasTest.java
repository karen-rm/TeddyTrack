/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package controlador;

import java.awt.event.ActionEvent;
import java.util.List;

import modelo.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author akram
 */
public class Control_ReservasTest {

    private Control_Reservas controlReservas;
    private Conexion conexion;

    public Control_ReservasTest() {
    }

    @BeforeClass
    public static void setUpClass() {

    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before


    @After
    public void tearDown() {
    }

    /**
     * Test of actionPerformed method, of class Control_Reservas.
     */
    @Test
    public void testActionPerformed() {
        System.out.println("actionPerformed");
        ActionEvent e = null;
        Control_Reservas instance = null;
        instance.actionPerformed(e);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of actualizarPanelReservas method, of class Control_Reservas.
     */
    @Test
    public void testActualizarPanelReservas() {
        System.out.println("actualizarPanelReservas");
        Control_Reservas instance = null;
        instance.actualizarPanelReservas();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerReservas method, of class Control_Reservas.
     */
    @Test
    public void testObtenerReservas() {
        System.out.println("obtenerReservas");
        Control_Reservas instance = null;
        List<Reserva> expResult = null;
        List<Reserva> result = instance.obtenerReservas();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerIdVenta method, of class Control_Reservas.
     */
    @Test
    public void testObtenerIdVenta() {
        System.out.println("obtenerIdVenta");
        Reserva reserva = null;
        Control_Reservas instance = null;
        int expResult = 0;
        int result = instance.obtenerIdVenta(reserva);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerIdCliente method, of class Control_Reservas.
     */
    @Test
    public void testObtenerIdCliente() {
        System.out.println("obtenerIdCliente");
        Reserva reserva = null;
        Control_Reservas instance = null;
        int expResult = 0;
        int result = instance.obtenerIdCliente(reserva);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerInfoCliente method, of class Control_Reservas.
     */
    @Test
    public void testObtenerInfoCliente() {
        System.out.println("obtenerInfoCliente");
        int idCliente = 0;
        Control_Reservas instance = null;
        Cliente expResult = null;
        Cliente result = instance.obtenerInfoCliente(idCliente);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerInfoVenta method, of class Control_Reservas.
     */
    @Test
    public void testObtenerInfoVenta() {
        System.out.println("obtenerInfoVenta");
        int idVenta = 0;
        Control_Reservas instance = null;
        Venta expResult = null;
        Venta result = instance.obtenerInfoVenta(idVenta);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerInfoDetalleVenta method, of class Control_Reservas.
     */
    @Test
    public void testObtenerInfoDetalleVenta(){
        System.out.println("obtenerInfoDetalleVenta");
        Control_Reservas instance = new Control_Reservas(); // Crear instancia del controlador
        List<DetalleVentas> detallesVenta = instance.obtenerInfoDetalleVenta(4);

        // Verificar que se obtenga el resultado esperado

        DetalleVentas detalleVenta = new DetalleVentas("Pikachu de peluche grande",25.0,1);
        assertEquals(1, detalleVenta.getCantidad());
        assertEquals(25.0, detalleVenta.getPrecio(), 0.01);
        assertEquals("Pikachu de peluche grande", detalleVenta.getNombre());
    }

    /**
     * Test of obtenerNombreProducto method, of class Control_Reservas.
     */
    @Test
    public void testObtenerNombreProducto() {
        System.out.println("obtenerNombreProducto");
        int idProducto = 0;
        Control_Reservas instance = null;
        String expResult = "";
        String result = instance.obtenerNombreProducto(idProducto);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of actualizarStockProducto method, of class Control_Reservas.
     */
    @Test
    public void testActualizarStockProducto() {

    }

    /**
     * Test of buscarIdProductoPorNombre method, of class Control_Reservas.
     */
    @Test
    public void testBuscarIdProductoPorNombre() {
        System.out.println("buscarIdProductoPorNombre");
        String nombreProducto = "";
        Control_Reservas instance = null;
        int expResult = 0;
        int result = instance.buscarIdProductoPorNombre(nombreProducto);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerReservaPorId method, of class Control_Reservas.
     */
    @Test
    public void testObtenerReservaPorId() {
        System.out.println("obtenerReservaPorId");
        int idReserva = 0;
        Control_Reservas instance = null;
        Reserva expResult = null;
        Reserva result = instance.obtenerReservaPorId(idReserva);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of actualizarEstadoReservaCancelada method, of class Control_Reservas.
     */
    @Test
    public void testActualizarEstadoReservaCancelada() {
        System.out.println("actualizarEstadoReservaCancelada");
        Reserva reserva = null;
        Control_Reservas instance = null;
        instance.actualizarEstadoReservaCancelada(reserva);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
