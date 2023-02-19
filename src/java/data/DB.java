package data;


import java.sql.Connection;
import java.sql.DriverManager;

public class DB {

    private static DB instance;
    private String kon = "jdbc:derby://localhost:1527/blogdb"; 
    
    private Connection con;


    private DB() { 

        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            con = DriverManager.getConnection(kon, "root", "admin123");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DB getInstance() {
        if (instance == null) {
            instance = new DB();
        }
            return instance;
    }

    public synchronized Connection getConnection() {
        return con;
    }
    
}