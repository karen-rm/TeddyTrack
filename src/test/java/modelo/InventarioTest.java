/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package modelo;

import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
public class InventarioTest {

    private Inventario inventario;
    public InventarioTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {

    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        inventario = new Inventario();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of obtenerBotonesCategorias method, of class Inventario.
     */
    @Test
    public void testObtenerBotonesCategorias() {
        System.out.println("obtenerBotonesCategorias");
        Inventario instance = new Inventario();
        List<JButton> expResult = null;
        List<JButton> result = instance.obtenerBotonesCategorias();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of cargarCategoriasEnComboBox method, of class Inventario.
     */
    @Test
    public void testCargarCategoriasEnComboBox() {
        System.out.println("cargarCategoriasEnComboBox");
        JComboBox<String> comboBox = null;
        Inventario instance = new Inventario();
        instance.cargarCategoriasEnComboBox(comboBox);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of agregarCategoria method, of class Inventario.
     */
    @Test
    public void testAgregarCategoria() {
        // Agregar una nueva categoría y luego verificar si se agrega correctamente
        String nuevaCategoria = "NuevaCategoría";
        inventario.agregarCategoria(nuevaCategoria);
        // Verificar si la categoría se agrega correctamente, por ejemplo, verificando si está en la lista de categorías
        assertTrue(inventario.obtenerBotonesCategorias().stream().anyMatch(boton -> boton.getText().equals(nuevaCategoria)));
    }

    /**
     * Test of categoriaExiste method, of class Inventario.
     */
    @Test
    public void testCategoriaExiste() {
        System.out.println("categoriaExiste");
        String nombreCategoria = "";
        Inventario instance = new Inventario();
        boolean expResult = false;
        boolean result = instance.categoriaExiste(nombreCategoria);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of eliminarCategoria method, of class Inventario.
     */
    @Test
    public void testEliminarCategoria() {
        System.out.println("eliminarCategoria");
        String nombreCategoria = "";
        Inventario instance = new Inventario();
        instance.eliminarCategoria(nombreCategoria);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of modificarNombreCategoria method, of class Inventario.
     */
    @Test
    public void testModificarNombreCategoria() {
        // Agregar una nueva categoría y luego modificar su nombre
        String categoriaExistente = "CategoríaExistente";
        String nuevoNombre = "NuevoNombreCategoria";
        inventario.agregarCategoria(categoriaExistente);
        inventario.modificarNombreCategoria(categoriaExistente, nuevoNombre);
        // Verificar que el nombre de la categoría se modifica correctamente, por ejemplo, verificando si ya no está en la lista de categorías con el nombre antiguo
        assertFalse(inventario.obtenerBotonesCategorias().stream().anyMatch(boton -> boton.getText().equals(categoriaExistente)));
        // Verificar que el nombre modificado está en la lista de categorías
        assertTrue(inventario.obtenerBotonesCategorias().stream().anyMatch(boton -> boton.getText().equals(nuevoNombre)));
    }
    
}
