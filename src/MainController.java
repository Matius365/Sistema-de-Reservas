
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Date;

public class MainController {

    // ===== USUARIOS =====
    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, Integer> colId;
    @FXML private TableColumn<Usuario, String> colNombre, colCorreo, colTipo;

    @FXML private TextField txtId,txtNombre, txtCorreo, txtPass, txtTipo;

    private UsuarioCRUD usuarioDAO = new UsuarioCRUD();

    // ===== RESERVAS =====
    @FXML private TableView<Reserva> tablaReservas;
    @FXML private TableColumn<Reserva, Integer> colRecurso, colReserva, colUsuarioR;
    @FXML private TableColumn<Reserva, String> colHoraIni, colHoraFin;

    @FXML private TextField txtRecurso, txtReserva, txtUsuarioR, txtHoraIni, txtHoraFin;

    private ReservaCRUD reservaDAO = new ReservaCRUD();

    @FXML
    public void initialize() {

        // USUARIOS
        colId.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getId()).asObject());
        colNombre.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getNombre()));
        colCorreo.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getCorreo()));
        colTipo.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getTipo()));

        // RESERVAS
        colRecurso.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getIdRecurso()).asObject());
        colReserva.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getIdReservaLocal()).asObject());
        colUsuarioR.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getIdUsuario()).asObject());
        colHoraIni.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getHoraInicio()));
        colHoraFin.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getHoraFin()));

        cargarUsuarios();
        cargarReservas();

        tablaUsuarios.setOnMouseClicked(e -> seleccionarUsuario());
        tablaReservas.setOnMouseClicked(e -> seleccionarReserva());
    }

    // ===== CRUD USUARIOS =====
    @FXML
    public void cargarUsuarios() {
        tablaUsuarios.setItems(FXCollections.observableArrayList(usuarioDAO.obtenerTodos()));
    }

    @FXML
    public void insertarUsuario() {
        try {
            Usuario u = new Usuario(
                    0,
                    txtCorreo.getText(),
                    txtPass.getText(),
                    txtNombre.getText(),
                    new Date(),
                    txtTipo.getText()
            );

            int idGenerado = usuarioDAO.insertar(u);

            if (idGenerado != -1) {
                txtId.setText(String.valueOf(idGenerado)); // 👈 mostrar ID
            }

            cargarUsuarios();
            limpiarCamposUsuario();
            txtNombre.requestFocus();

        } catch (RuntimeException e) {

            if ("EMAIL_DUPLICADO".equals(e.getMessage())) {
                warning("El correo ya está registrado");
            } else {
                error("Error al insertar usuario");
            }
        }
    }

    @FXML
    public void actualizarUsuario() {
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            warning("Selecciona un usuario");
            return;
        }

        try {
            Usuario u = new Usuario(
                    seleccionado.getId(), // 👈 importante
                    txtCorreo.getText(),
                    txtPass.getText(),
                    txtNombre.getText(),
                    new Date(),
                    txtTipo.getText()
            );

            usuarioDAO.actualizar(u);
            cargarUsuarios();

        } catch (Exception e) {
            error("Error al actualizar usuario");
        }
    }

    @FXML
    public void eliminarUsuario() {
        Usuario u = tablaUsuarios.getSelectionModel().getSelectedItem();

        if (u != null) {
            usuarioDAO.eliminar(u.getId());
            cargarUsuarios();
        } else {
            warning("Selecciona un usuario");
        }
    }

    private void seleccionarUsuario() {
        Usuario u = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (u != null) {
            //txtId.setText(String.valueOf(u.getId()));
            txtNombre.setText(u.getNombre());
            txtCorreo.setText(u.getCorreo());
            txtTipo.setText(u.getTipo());
        }
    }

    private void limpiarCamposUsuario() {
        txtNombre.clear();
        txtCorreo.clear();
        txtPass.clear();
        txtTipo.clear();
    }

    //Alertas y Warnings
    private void warning(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setContentText(msg);
        a.show();
    }


    // ===== CRUD RESERVAS =====
    @FXML
    public void cargarReservas() {
        tablaReservas.setItems(FXCollections.observableArrayList(reservaDAO.obtenerTodas()));
    }

    @FXML
    public void insertarReserva() {
        try {
            Reserva r = new Reserva(
                    Integer.parseInt(txtRecurso.getText()),
                    Integer.parseInt(txtReserva.getText()),
                    Integer.parseInt(txtUsuarioR.getText()),
                    new Date(),
                    txtHoraIni.getText(),
                    txtHoraFin.getText(),
                    0,0,"",""
            );
            reservaDAO.insertar(r);
            cargarReservas();
        } catch (Exception e) {
            error("Error al insertar reserva");
        }
    }

    @FXML
    public void actualizarReserva() {
        try {
            Reserva r = new Reserva(
                    Integer.parseInt(txtRecurso.getText()),
                    Integer.parseInt(txtReserva.getText()),
                    Integer.parseInt(txtUsuarioR.getText()),
                    new Date(),
                    txtHoraIni.getText(),
                    txtHoraFin.getText(),
                    0,0,"",""
            );
            reservaDAO.actualizar(r);
            cargarReservas();
        } catch (Exception e) {
            error("Error al actualizar reserva");
        }
    }

    @FXML
    public void eliminarReserva() {
        reservaDAO.eliminar(
                Integer.parseInt(txtRecurso.getText()),
                Integer.parseInt(txtReserva.getText())
        );
        cargarReservas();
    }

    private void seleccionarReserva() {
        Reserva r = tablaReservas.getSelectionModel().getSelectedItem();
        if (r != null) {
            txtRecurso.setText(String.valueOf(r.getIdRecurso()));
            txtReserva.setText(String.valueOf(r.getIdReservaLocal()));
            txtUsuarioR.setText(String.valueOf(r.getIdUsuario()));
            txtHoraIni.setText(r.getHoraInicio());
            txtHoraFin.setText(r.getHoraFin());
        }
    }

    private void error(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setContentText(msg);
        a.show();
    }
}