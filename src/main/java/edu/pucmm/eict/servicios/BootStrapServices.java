package edu.pucmm.eict.servicios;

import org.h2.tools.Server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class BootStrapServices {

    private static BootStrapServices instancia;

    private static final int TCP_PORT = 9092;
    private static final int WEB_PORT = 8082;

    private Server tcpServer;
    private Server webServer;

    private BootStrapServices() {
    }

    public static BootStrapServices getInstancia() {
        if (instancia == null) {
            instancia = new BootStrapServices();
        }
        return instancia;
    }


    public void startDb() {
        try {
            tcpServer = Server.createTcpServer(
                    "-tcpPort", String.valueOf(TCP_PORT),
                    "-tcpAllowOthers",
                    "-tcpDaemon",
                    "-ifNotExists").start();
            webServer = Server.createWebServer("-webPort", String.valueOf(WEB_PORT)).start();
            System.out.println("Servidor H2 TCP iniciado en localhost:" + TCP_PORT);
            System.out.println("Consola web H2: " + webServer.getURL());
        } catch (SQLException ex) {
            System.out.println("Problema con la base de datos: " + ex.getMessage());
        }
    }

    public void stopDb() {
        if (webServer != null && webServer.isRunning(false)) {
            webServer.stop();
        }
        if (tcpServer != null && tcpServer.isRunning(false)) {
            tcpServer.stop();
            System.out.println("Servidor H2 detenido.");
        }
    }


    public void crearTablas() {
        String sql = "CREATE TABLE IF NOT EXISTS ESTUDIANTE ("
                + "  MATRICULA INTEGER PRIMARY KEY NOT NULL,"
                + "  NOMBRE    VARCHAR(100) NOT NULL,"
                + "  CARRERA   VARCHAR(50)  NOT NULL"
                + ")";
        try (Connection con = DataBaseServices.getInstancia().getConexion();
             Statement st = con.createStatement()) {
            st.execute(sql);
            System.out.println("Tabla ESTUDIANTE verificada/creada.");
        } catch (SQLException ex) {
            System.out.println("Error creando la tabla ESTUDIANTE: " + ex.getMessage());
        }
    }


    public void init() {
        startDb();
        crearTablas();
        cargarDatosBase();
    }


    private void cargarDatosBase() {
        String count  = "SELECT COUNT(*) FROM ESTUDIANTE";
        String insert = "INSERT INTO ESTUDIANTE (MATRICULA, NOMBRE, CARRERA) VALUES (20011136, 'Carlos Camacho', 'ITT')";
        try (Connection con = DataBaseServices.getInstancia().getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(count)) {
            if (rs.next() && rs.getInt(1) == 0) {
                st.executeUpdate(insert);
                System.out.println("Datos base creados (estudiante demo).");
            }
        } catch (SQLException ex) {
            System.out.println("Error cargando datos base: " + ex.getMessage());
        }
    }
}
