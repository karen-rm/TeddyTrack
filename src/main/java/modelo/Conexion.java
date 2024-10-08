
package modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    String hostname = "localhost";
    String username = "root";
    String password = "13mysql22";
    String database = "teddytrack";
    String port = "3306";
    
    public Connection getConnection(){
        Connection conn = null;
        String jdbcUrl = "jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database;
        System.out.println(jdbcUrl);
        
        try {
            conn = DriverManager.getConnection(jdbcUrl, this.username, this.password);
            System.out.println("Successfully connected");
        } catch(SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } 
        return conn;
    }
}
