
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.Date;

public class MainController {

    // ===== USUARIOS =====
    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, Integer> colId;
    @FXML private TableColumn<Usuario, String> colNombre, colCorreo, colTipo;
    @FXML private TableColumn<Usuario, Date> colFechaNacimiento;
    @FXML private DatePicker dateNacimiento;
    @FXML private ComboBox<String> cbTipo;
    @FXML private TextField txtId,txtNombre, txtCorreo, txtPass;

    private UsuarioCRUD usuarioDAO = new UsuarioCRUD();

    // ===== RESERVAS =====
    @FXML private TableView<Reserva> tablaReservas;
    @FXML private TableColumn<Reserva, Integer> colRecurso, colReserva, colUsuarioR;
    @FXML private TableColumn<Reserva, String> colHoraIni, colHoraFin;
    @FXML private TableColumn<Reserva, Date> colFecha;
    @FXML private TableColumn<Reserva, Double> colCoste;
    @FXML private TableColumn<Reserva, Integer> colPlazas;
    @FXML private TableColumn<Reserva, String> colMotivo;
    @FXML private TableColumn<Reserva, String> colObs;
    @FXML private TextField txtRecurso, txtReserva, txtUsuarioR;
    @FXML private DatePicker dateReserva;
    @FXML private TextField txtCoste, txtPlazas, txtMotivo, txtObservaciones;
    @FXML private Button btnInsertar;
    @FXML private Spinner<Integer> spHoraIni, spMinIni;
    @FXML private Spinner<Integer> spHoraFin, spMinFin;


    private ReservaCRUD reservaDAO = new ReservaCRUD();

    @FXML
    public void initialize() {

        // USUARIOS
        colId.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getId()).asObject());
        colNombre.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getNombre()));
        colCorreo.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getCorreo()));
        colFechaNacimiento.setCellValueFactory(
                d -> new javafx.beans.property.SimpleObjectProperty<>(d.getValue().getFechaNacimiento())
        );
        colTipo.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getTipo()));
        cbTipo.setItems(FXCollections.observableArrayList("Administrador", "Normal"));

        // RESERVAS
        colRecurso.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getIdRecurso()).asObject());
        colReserva.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getIdReservaLocal()).asObject());
        colUsuarioR.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getIdUsuario()).asObject());
        colHoraIni.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getHoraInicio()));
        colHoraFin.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getHoraFin()));
        colFecha.setCellValueFactory(d -> new javafx.beans.property.SimpleObjectProperty<>(d.getValue().getFecha()));
        colCoste.setCellValueFactory(d -> new javafx.beans.property.SimpleObjectProperty<>(d.getValue().getCoste()));
        colPlazas.setCellValueFactory(d -> new javafx.beans.property.SimpleObjectProperty<>(d.getValue().getNumeroPlazas()));
        colMotivo.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getMotivo()));
        colObs.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getObservaciones()));

        cargarUsuarios();
        cargarReservas();

        tablaUsuarios.setOnMouseClicked(e -> seleccionarUsuario());
        tablaUsuarios.setOnMouseClicked(e -> {
            seleccionarUsuario();
            filtrarReservasPorUsuario();
        });
//        tablaReservas.setOnMouseClicked(e -> seleccionarReserva());
        tablaReservas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                seleccionarReserva();
            }
        });

        BooleanBinding camposVacios = txtCorreo.textProperty().isEmpty()
//                .or(txtPass.textProperty().isEmpty())
                .or(txtNombre.textProperty().isEmpty());
//                .or(txtTipo.textProperty().isEmpty());
        btnInsertar.disableProperty().bind(camposVacios);

        spHoraIni.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12));
        spMinIni.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));

        spHoraFin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 13));
        spMinFin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));

        //asi podemos escribir en los spinners y utilizar las flechas tambien
        spHoraIni.setEditable(true);
        spMinIni.setEditable(true);
        spHoraFin.setEditable(true);
        spMinFin.setEditable(true);

        //Validacion spinners hora
        spHoraIni.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                spHoraIni.getEditor().setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        txtRecurso.textProperty().addListener((obs, oldVal, newVal) -> {
            prepararSiguienteId();
        });
        txtReserva.setDisable(true);
    }

    // ===== CRUD USUARIOS =====
    @FXML
    public void cargarUsuarios() {
        tablaUsuarios.setItems(FXCollections.observableArrayList(usuarioDAO.obtenerTodos()));
    }

    @FXML
    public void insertarUsuario() {
//        if (txtCorreo.getText().trim().isEmpty() ||
//                txtPass.getText().trim().isEmpty() ||
//                txtNombre.getText().trim().isEmpty() ||
//                txtTipo.getText().trim().isEmpty()) {
//            warning("Rellena todos los campos obligatorios");
//            return;
//        }
        if (cbTipo.getValue() == null) {
            warning("Selecciona tipo de usuario");
            return;
        }

        try {
            java.sql.Date sqlDate = null;
            if (dateNacimiento.getValue() != null) {
                LocalDate fecha = dateNacimiento.getValue();
                sqlDate = java.sql.Date.valueOf(fecha);
            }
            Usuario u = new Usuario(
                    0,
                    txtCorreo.getText(),
                    txtPass.getText(),
                    txtNombre.getText(),
//                    new Date(),
//                    fecha,
                    sqlDate,
                    cbTipo.getValue()
//                    txtTipo.getText()
            );

            int idGenerado = usuarioDAO.insertar(u);

//            if (idGenerado != -1) {
//                txtId.setText(String.valueOf(idGenerado)); // mostrar ID
//            }

            txtId.clear();
            cargarUsuarios();
            limpiarCamposUsuario();
            txtNombre.requestFocus();

        } catch (IllegalArgumentException e) {
            warning("Formato de fecha incorrecto. Usa aaaa-mm-dd");

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
        if (cbTipo.getValue() == null) {
            warning("Selecciona tipo de usuario");
            return;
        }

        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            warning("Selecciona un usuario");
            return;
        }

        try {
//            java.sql.Date fecha = java.sql.Date.valueOf(txtFechaNacimiento.getText());
            java.sql.Date sqlDate = null;
            if (dateNacimiento.getValue() != null) {
                LocalDate fecha = dateNacimiento.getValue();
                sqlDate = java.sql.Date.valueOf(fecha);
            }
            Usuario u = new Usuario(
                    seleccionado.getId(), // 👈 importante
                    txtCorreo.getText(),
                    txtPass.getText(),
                    txtNombre.getText(),
//                    new Date(),
//                    fecha,
                    sqlDate,
                    cbTipo.getValue()
//                    txtTipo.getText()
            );

            usuarioDAO.actualizar(u);
            cargarUsuarios();

        } catch (Exception e) {
            error("Error al actualizar usuario");
        }
    }

//    @FXML
//    public void eliminarUsuario() {
//        Usuario u = tablaUsuarios.getSelectionModel().getSelectedItem();
//
//        if (u != null) {
//            usuarioDAO.eliminar(u.getId());
//            cargarUsuarios();
//        } else {
//            warning("Selecciona un usuario");
//        }
//    }

    @FXML
    public void eliminarUsuario() {

        Usuario u = tablaUsuarios.getSelectionModel().getSelectedItem();

        if (u == null) {
            warning("Selecciona un usuario");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("Eliminar usuario");
        confirmacion.setContentText("¿Seguro que quieres eliminar a " + u.getNombre() + "?");

        ButtonType botonSi = new ButtonType("Sí");
        ButtonType botonNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmacion.getButtonTypes().setAll(botonSi, botonNo);

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == botonSi) {
                usuarioDAO.eliminar(u.getId());
                cargarUsuarios();
                limpiarCamposUsuario();
            }
        });
    }

    private void seleccionarUsuario() {
        Usuario u = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (u != null) {
            //txtId.setText(String.valueOf(u.getId()));
            txtNombre.setText(u.getNombre());
            txtCorreo.setText(u.getCorreo());
            cbTipo.setValue(u.getTipo());
            //txtTipo.setText(u.getTipo());
        }
    }

//    @FXML
//    public void refrescarUsuarios() {
//
//        // 1. Recargar tabla desde BD
//        cargarUsuarios();
//
//        // 2. Limpiar campos del formulario
//        limpiarCamposUsuario();
//
//        // 3. Limpiar selección de la tabla
//        tablaUsuarios.getSelectionModel().clearSelection();
//
//        // 4. Reset de campos extra (si tienes ComboBox)
//        cbTipo.setValue(null);
//
//        // 5. Enfocar el primer campo para empezar rápido
//        txtNombre.requestFocus();
//    }

    private void limpiarCamposUsuario() {
        txtNombre.clear();
        txtCorreo.clear();
        txtPass.clear();
        cbTipo.getValue();
//        txtTipo.clear();
    }

    //Alertas y Warnings
    private void warning(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setContentText(msg);
        a.show();
    }

    @FXML
    public void resetFormularioUsuarios() {
        cargarUsuarios();
        limpiarCamposUsuario();
        tablaUsuarios.getSelectionModel().clearSelection();
        cbTipo.setValue(null);
        dateNacimiento.setValue(null);
        tablaReservas.setItems(FXCollections.observableArrayList());
        txtId.clear();
        txtNombre.requestFocus();
    }



    // ===== CRUD RESERVAS =====
    @FXML
    public void cargarReservas() {
        tablaReservas.setItems(FXCollections.observableArrayList(reservaDAO.obtenerTodas()));
    }

    private void filtrarReservasPorUsuario() {
        Usuario u = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (u == null) return;
        tablaReservas.setItems(
                FXCollections.observableArrayList(
                        reservaDAO.obtenerPorUsuario(u.getId())
                )
        );
    }

    @FXML
    public void insertarReserva() {

        int idRecurso;

        // 1. Validar recurso
        try {
            idRecurso = Integer.parseInt(txtRecurso.getText());
        } catch (NumberFormatException e) {
            warning("Introduce un recurso válido (1 al 5)");
            return;
        }

        if (idRecurso < 1 || idRecurso > 5) {
            warning("Introduce un recurso del 1 al 5");
            return;
        }

        try {
            // 2. generar ID
            int idReservaLocal = reservaDAO.siguienteIdReserva(idRecurso);

            int idUsuario = Integer.parseInt(txtUsuarioR.getText());

            Usuario u = usuarioDAO.obtenerPorId(idUsuario);

            if (u == null) {
                warning("El usuario no existe");
                return;
            }

            if ("Administrador".equalsIgnoreCase(u.getTipo())) {
                warning("Los administradores no pueden realizar reservas");
                return;
            }

            if (dateReserva.getValue() == null) {
                warning("Selecciona una fecha");
                return;
            }

            java.sql.Date fecha = java.sql.Date.valueOf(dateReserva.getValue());

            String horaInicio = String.format("%02d:%02d",
                    spHoraIni.getValue(),
                    spMinIni.getValue()
            );

            String horaFin = String.format("%02d:%02d",
                    spHoraFin.getValue(),
                    spMinFin.getValue()
            );

            Reserva r = new Reserva(
                    idRecurso,
                    idReservaLocal,
                    idUsuario,
                    fecha,
                    horaInicio,
                    horaFin,
                    Double.parseDouble(txtCoste.getText()),
                    Integer.parseInt(txtPlazas.getText()),
                    txtMotivo.getText(),
                    txtObservaciones.getText()
            );

            // 3. Insertar
            reservaDAO.insertar(r);

            // 4. Recargar + limpiar
            cargarReservas();
            limpiarCamposReserva();
            txtRecurso.requestFocus();

            // 5. Preparar siguiente ID automáticamente
//            txtRecurso.setText(String.valueOf(idRecurso)); // mantiene el recurso
            prepararSiguienteId();

        } catch (NumberFormatException e) {
            warning("Inserta coste y plazas");
        } catch (Exception e) {
            error("Error al insertar reserva");
            e.printStackTrace();
        }
    }

    @FXML
    public void actualizarReserva() {
        try {

            if (dateReserva.getValue() == null) {
                warning("Selecciona una fecha");
                return;
            }

            String horaInicio = String.format("%02d:%02d",
                    spHoraIni.getValue(),
                    spMinIni.getValue()
            );

            String horaFin = String.format("%02d:%02d",
                    spHoraFin.getValue(),
                    spMinFin.getValue()
            );

            Reserva r = new Reserva(
                    Integer.parseInt(txtRecurso.getText()),
                    Integer.parseInt(txtReserva.getText()),
                    Integer.parseInt(txtUsuarioR.getText()),
                    java.sql.Date.valueOf(dateReserva.getValue()),
                    horaInicio,
                    horaFin,
                    Double.parseDouble(txtCoste.getText()),
                    Integer.parseInt(txtPlazas.getText()),
                    txtMotivo.getText(),
                    txtObservaciones.getText()
            );

            reservaDAO.actualizar(r);
            cargarReservas();

        } catch (NumberFormatException e) {
            warning("Revisa los campos numéricos");
        } catch (Exception e) {
            error("Error al actualizar reserva");
            e.printStackTrace();
        }
    }

//    @FXML
//    public void eliminarReserva() {
//        reservaDAO.eliminar(
//                Integer.parseInt(txtRecurso.getText()),
//                Integer.parseInt(txtReserva.getText())
//        );
//        cargarReservas();
//    }

    @FXML
    public void eliminarReserva() {

        Reserva r = tablaReservas.getSelectionModel().getSelectedItem();

        if (r == null) {
            warning("Selecciona una reserva");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("Eliminar reserva");
        confirmacion.setContentText(
                "¿Seguro que quieres eliminar la reserva " +
                        r.getIdReservaLocal() + " del recurso " + r.getIdRecurso() + "?"
        );

        ButtonType botonSi = new ButtonType("Sí");
        ButtonType botonNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmacion.getButtonTypes().setAll(botonSi, botonNo);

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == botonSi) {

                reservaDAO.eliminar(r.getIdRecurso(), r.getIdReservaLocal());

                cargarReservas();
                limpiarCamposReserva();

                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setContentText("Reserva eliminada correctamente");
                a.show();
            }
        });
    }


    private void limpiarCamposReserva() {

        txtRecurso.clear();
        txtReserva.clear();
        txtUsuarioR.clear();
//        txtHoraIni.clear();
//        txtHoraFin.clear();
        spHoraIni.getValueFactory().setValue(12);
        spMinIni.getValueFactory().setValue(0);
        spHoraFin.getValueFactory().setValue(13);
        spMinFin.getValueFactory().setValue(0);
        txtCoste.clear();
        txtPlazas.clear();
        txtMotivo.clear();
        txtObservaciones.clear();

        dateReserva.setValue(null);

        tablaReservas.getSelectionModel().clearSelection();
    }

    private void prepararSiguienteId() {
        try {
            if (!txtRecurso.getText().isEmpty()) {
                int idRecurso = Integer.parseInt(txtRecurso.getText());
                int siguiente = reservaDAO.siguienteIdReserva(idRecurso);
                txtReserva.setText(String.valueOf(siguiente));
            }
        } catch (Exception e) {
            txtReserva.clear();
        }
    }

    private void seleccionarReserva() {
        Reserva r = tablaReservas.getSelectionModel().getSelectedItem();

        if (r == null) return;

        txtRecurso.setText(String.valueOf(r.getIdRecurso()));
        txtReserva.setText(String.valueOf(r.getIdReservaLocal()));
        txtUsuarioR.setText(String.valueOf(r.getIdUsuario()));

        // Fecha
        if (r.getFecha() != null) {
            dateReserva.setValue(
                    new java.sql.Date(r.getFecha().getTime()).toLocalDate()
            );
        }

        // Horas (si vienen en formato "HH:mm")
        if (r.getHoraInicio() != null) {
            String[] hIni = r.getHoraInicio().split(":");
            spHoraIni.getValueFactory().setValue(Integer.parseInt(hIni[0]));
            spMinIni.getValueFactory().setValue(Integer.parseInt(hIni[1]));
        }

        if (r.getHoraFin() != null) {
            String[] hFin = r.getHoraFin().split(":");
            spHoraFin.getValueFactory().setValue(Integer.parseInt(hFin[0]));
            spMinFin.getValueFactory().setValue(Integer.parseInt(hFin[1]));
        }

        // Resto de campos
        txtCoste.setText(String.valueOf(r.getCoste()));
        txtPlazas.setText(String.valueOf(r.getNumeroPlazas()));
        txtMotivo.setText(r.getMotivo());
        txtObservaciones.setText(r.getObservaciones());
    }

    private void error(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setContentText(msg);
        a.show();
    }

    @FXML
    public void resetReservas() {
        cargarReservas();
        limpiarCamposReserva();
        tablaReservas.getSelectionModel().clearSelection();
        tablaUsuarios.getSelectionModel().clearSelection();
        txtRecurso.requestFocus();
    }
}