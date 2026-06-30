package edu.pucmm.eict.servicios;

import edu.pucmm.eict.encapsulaciones.Estudiante;
import edu.pucmm.eict.util.NoExisteEstudianteException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class EstudianteServices {

    private static EstudianteServices instancia;

    private EstudianteServices() {
    }

    public static EstudianteServices getInstancia() {
        if (instancia == null) {
            instancia = new EstudianteServices();
        }
        return instancia;
    }

    public List<Estudiante> listarEstudiante() {
        String sql = "SELECT MATRICULA, NOMBRE, CARRERA FROM ESTUDIANTE ORDER BY MATRICULA";
        List<Estudiante> lista = new ArrayList<>();
        try (Connection con = DataBaseServices.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error listando estudiantes: " + ex.getMessage(), ex);
        }
        return lista;
    }

    public Estudiante getEstudiantePorMatricula(int matricula) {
        String sql = "SELECT MATRICULA, NOMBRE, CARRERA FROM ESTUDIANTE WHERE MATRICULA = ?";
        try (Connection con = DataBaseServices.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, matricula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error buscando estudiante " + matricula + ": " + ex.getMessage(), ex);
        }
        return null;
    }

    public Estudiante crearEstudiante(Estudiante estudiante) {
        if (getEstudiantePorMatricula(estudiante.getMatricula()) != null) {
            System.out.println("Estudiante ya registrado: " + estudiante.getMatricula());
            return null;
        }
        String sql = "INSERT INTO ESTUDIANTE (MATRICULA, NOMBRE, CARRERA) VALUES (?, ?, ?)";
        try (Connection con = DataBaseServices.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, estudiante.getMatricula());
            ps.setString(2, estudiante.getNombre());
            ps.setString(3, estudiante.getCarrera());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Error creando estudiante: " + ex.getMessage(), ex);
        }
        return estudiante;
    }

    public Estudiante actualizarEstudiante(Estudiante estudiante) {
        String sql = "UPDATE ESTUDIANTE SET NOMBRE = ?, CARRERA = ? WHERE MATRICULA = ?";
        try (Connection con = DataBaseServices.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estudiante.getNombre());
            ps.setString(2, estudiante.getCarrera());
            ps.setInt(3, estudiante.getMatricula());
            int filas = ps.executeUpdate();
            if (filas == 0) { // no existe, no se puede actualizar
                throw new NoExisteEstudianteException("No Existe el estudiante: " + estudiante.getMatricula());
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error actualizando estudiante: " + ex.getMessage(), ex);
        }
        return estudiante;
    }

    public boolean eliminandoEstudiante(int matricula) {
        String sql = "DELETE FROM ESTUDIANTE WHERE MATRICULA = ?";
        try (Connection con = DataBaseServices.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, matricula);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Error eliminando estudiante " + matricula + ": " + ex.getMessage(), ex);
        }
    }

    private Estudiante mapear(ResultSet rs) throws SQLException {
        return new Estudiante(
                rs.getInt("MATRICULA"),
                rs.getString("NOMBRE"),
                rs.getString("CARRERA"));
    }
}
