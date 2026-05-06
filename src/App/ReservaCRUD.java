package App;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaCRUD {

    // CREATE
    public void insertar(Reserva r) {
        String sql = "INSERT INTO reserva VALUES (?,?,?,?,?,?,?,?,?,?)";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, r.getIdRecurso());
            ps.setInt(2, r.getIdReservaLocal());
            ps.setInt(3, r.getIdUsuario());
            ps.setDate(4, new java.sql.Date(r.getFecha().getTime()));
            ps.setString(5, r.getHoraInicio());
            ps.setString(6, r.getHoraFin());
            ps.setDouble(7, r.getCoste());
            ps.setInt(8, r.getNumeroPlazas());
            ps.setString(9, r.getMotivo());
            ps.setString(10, r.getObservaciones());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // READ
    public List<Reserva> obtenerTodas() {
        List<Reserva> lista = new ArrayList<>();
        String sql = "SELECT * FROM  reserva ORDER BY fecha ASC";

        try (Connection con = ConexionDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Reserva(
                        rs.getInt("id_recurso"),
                        rs.getInt("id_reserva_local"),
                        rs.getInt("id_usuario"),
                        rs.getDate("fecha"),
                        rs.getString("hora_inicio"),
                        rs.getString("hora_fin"),
                        rs.getDouble("coste"),
                        rs.getInt("numero_plazas"),
                        rs.getString("motivo"),
                        rs.getString("observaciones")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // UPDATE
    public void actualizar(Reserva r) {
        String sql = "UPDATE reserva SET id_usuario=?, fecha=?, hora_inicio=?, hora_fin=?, coste=?, numero_plazas=?, motivo=?, observaciones=? WHERE id_recurso=? AND id_reserva_local=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, r.getIdUsuario());
            ps.setDate(2, new java.sql.Date(r.getFecha().getTime()));
            ps.setString(3, r.getHoraInicio());
            ps.setString(4, r.getHoraFin());
            ps.setDouble(5, r.getCoste());
            ps.setInt(6, r.getNumeroPlazas());
            ps.setString(7, r.getMotivo());
            ps.setString(8, r.getObservaciones());
            ps.setInt(9, r.getIdRecurso());
            ps.setInt(10, r.getIdReservaLocal());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // DELETE
    public void eliminar(int idRecurso, int idReservaLocal) {
        String sql = "DELETE FROM reserva WHERE id_recurso=? AND id_reserva_local=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idRecurso);
            ps.setInt(2, idReservaLocal);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Reserva> obtenerPorUsuario(int idUsuario) {

        List<Reserva> lista = new ArrayList<>();
        String sql = "SELECT * FROM reserva WHERE id_usuario=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(new Reserva(
                        rs.getInt("id_recurso"),
                        rs.getInt("id_reserva_local"),
                        rs.getInt("id_usuario"),
                        rs.getDate("fecha"),
                        rs.getString("hora_inicio"),
                        rs.getString("hora_fin"),
                        rs.getDouble("coste"),
                        rs.getInt("numero_plazas"),
                        rs.getString("motivo"),
                        rs.getString("observaciones")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public int siguienteIdReserva(int idRecurso) {

        String sql = "SELECT IFNULL(MAX(id_reserva_local),0)+1 FROM reserva WHERE id_recurso=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idRecurso);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 1; // fallback
    }
}