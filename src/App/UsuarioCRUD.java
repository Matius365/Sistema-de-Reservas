package App;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioCRUD {

    // CREATE
//    public int insertar(Usuario u) {
//        String sql = "INSERT INTO usuario (correo_electronico, contrasena, nombre, fecha_nacimiento, tipo_usuario) VALUES (?,?,?,?,?)";
//
//        String sqlNormal = "INSERT INTO usuarionormal (id_usuario) VALUES (?)";
//
//        try (Connection con = ConexionDB.getConnection();
//             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//
//            ps.setString(1, u.getCorreo().trim());
//            ps.setString(2, u.getContrasena());
//            ps.setString(3, u.getNombre());
//            ps.setDate(4, new java.sql.Date(u.getFechaNacimiento().getTime()));
//            ps.setString(5, u.getTipo());
//
//            ps.executeUpdate();
//
//            ResultSet rs = ps.getGeneratedKeys();
//            if (rs.next()) return rs.getInt(1);
//
//        } catch (SQLException e) {
//
//            String msg = e.getMessage().toLowerCase();
//
//            if (msg.contains("duplicate")) {
//                throw new RuntimeException("EMAIL_DUPLICADO");
//            }
//
//            e.printStackTrace();
//        }
//
//        return -1;
//    }

    public int insertar(Usuario u) {

        String sqlUsuario =
                "INSERT INTO usuario (correo_electronico, contrasena, nombre, fecha_nacimiento, tipo_usuario) VALUES (?,?,?,?,?)";

        String sqlNormal =
                "INSERT INTO usuarionormal (id_usuario) VALUES (?)";

        try (Connection con = ConexionDB.getConnection()) {

            // 🔒 IMPORTANTE: transacción
            con.setAutoCommit(false);

            int idGenerado = -1;

            try (PreparedStatement ps = con.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, u.getCorreo().trim());
                ps.setString(2, u.getContrasena());
                ps.setString(3, u.getNombre());
                ps.setDate(4, new java.sql.Date(u.getFechaNacimiento().getTime()));
                ps.setString(5, u.getTipo());

                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    idGenerado = rs.getInt(1);
                }
            }

            // 👇 SOLO si es usuario normal
            if ("Normal".equalsIgnoreCase(u.getTipo())) {

                try (PreparedStatement ps2 = con.prepareStatement(sqlNormal)) {
                    ps2.setInt(1, idGenerado);
                    ps2.executeUpdate();
                }
            }

            con.commit();
            return idGenerado;

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
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
    public Usuario obtenerPorId(int id) {

        String sql = "SELECT * FROM usuario WHERE id_usuario=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("correo_electronico"),
                        rs.getString("contrasena"),
                        rs.getString("nombre"),
                        rs.getDate("fecha_nacimiento"),
                        rs.getString("tipo_usuario") // 👈 aquí está la clave
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}