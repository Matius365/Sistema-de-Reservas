import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioCRUD {

    // CREATE
    public void insertar(Usuario u) {
        String sql = "INSERT INTO usuario VALUES (?,?,?,?,?,?)";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, u.getId());
            ps.setString(2, u.getCorreo());
            ps.setString(3, u.getContrasena());
            ps.setString(4, u.getNombre());
            ps.setDate(5, new java.sql.Date(u.getFechaNacimiento().getTime()));
            ps.setString(6, u.getTipo());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // READ
    public List<Usuario> obtenerTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario";

        try (Connection con = ConexionDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("correo_electronico"),
                        rs.getString("contrasena"),
                        rs.getString("nombre"),
                        rs.getDate("fecha_nacimiento"),
                        rs.getString("tipo_usuario")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // UPDATE
    public void actualizar(Usuario u) {
        String sql = "UPDATE usuario SET correo_electronico=?, contrasena=?, nombre=?, fecha_nacimiento=?, tipo_usuario=? WHERE id_usuario=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getCorreo());
            ps.setString(2, u.getContrasena());
            ps.setString(3, u.getNombre());
            ps.setDate(4, new java.sql.Date(u.getFechaNacimiento().getTime()));
            ps.setString(5, u.getTipo());
            ps.setInt(6, u.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // DELETE
    public void eliminar(int id) {
        String sql = "DELETE FROM usuario WHERE id_usuario=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}