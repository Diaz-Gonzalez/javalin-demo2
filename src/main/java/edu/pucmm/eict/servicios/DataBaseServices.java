package edu.pucmm.eict.servicios;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseServices {
    private static DataBaseServices instancia;
    private static final String URL =
            "jdbc:h2:tcp://localhost:9092/./datos/estudiantes";
    private static final String USER = "sa";
    private static final String PASS = "";

    private DataBaseServices() {}
    public static DataBaseServices getInstancia() {
        if (instancia == null) {
            instancia = new DataBaseServices();
        }
        return instancia;
    }
    public Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
