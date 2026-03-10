package ChefZoneBackend.Dto.Request;

public class RegisterRequest {
    private String nombre;
    private String apellido;
    private String email;
    private String usuario;
    private String password;
    private String rol;

    // Constructor vacío (Necesario para que Jackson serialice el JSON)
    public RegisterRequest() {
    }

    // Constructor completo actualizado con el campo 'rol'
    public RegisterRequest(String nombre, String apellido, String email, String usuario, String password, String rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.usuario = usuario;
        this.password = password;
        this.rol = rol;
    }

    // --- Getters y Setters ---

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}