package ChefZoneBackend.Dto.Response;

public class UserProfileResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String usuario;
    private String rol;
    private String fotoPerfil;
    
    // ✅ Nuevos campos para las estadísticas
    private int recetasCount;
    private int likesCount;

    // Constructor vacío
    public UserProfileResponse() {
    }

    // Constructor completo (Actualizado)
    public UserProfileResponse(Long id, String nombre, String apellido, String email, String usuario, String rol, String fotoPerfil, int recetasCount, int likesCount) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.usuario = usuario;
        this.rol = rol;
        this.fotoPerfil = fotoPerfil;
        this.recetasCount = recetasCount;
        this.likesCount = likesCount;
    }

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    // ✅ Nuevos Getters y Setters para estadísticas
    
    public int getRecetasCount() {
        return recetasCount;
    }

    public void setRecetasCount(int recetasCount) {
        this.recetasCount = recetasCount;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }
}