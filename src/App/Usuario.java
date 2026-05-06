package App;

import java.util.Date;

public class Usuario {

        private int id;

        private String correo;

        private String contrasena;

        private String nombre;

        private Date fechaNacimiento;

        private String tipo;

        // Constructor
        public Usuario(int id, String correo, String contrasena, String nombre, Date fechaNacimiento, String tipo) {

            this.id = id;

            this.correo = correo;

            this.contrasena = contrasena;

            this.nombre = nombre;

            this.fechaNacimiento = fechaNacimiento;

            this.tipo = tipo;

        }

        // Getters y setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

}
