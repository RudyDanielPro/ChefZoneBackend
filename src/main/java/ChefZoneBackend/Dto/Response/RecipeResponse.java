package ChefZoneBackend.Dto.Response;

public class RecipeResponse {
    private Long id;
    private String titulo;
    private String descripcion;
    private String instrucciones;
    private String ingredientes;
    private String categoriaNombre;
    private String autorNombre;
    private String autorFoto; // 🟢 NUEVO: Campo añadido para la foto del autor
    private String imagenUrl;
    private int cantidadLikes;
    private boolean likedByCurrentUser;

    public RecipeResponse() {
    }

    // Constructor actualizado
    public RecipeResponse(Long id, String titulo, String descripcion, String instrucciones, String ingredientes,
            String categoriaNombre, String autorNombre, String autorFoto, String imagenUrl, int cantidadLikes,
            boolean likedByCurrentUser) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.instrucciones = instrucciones;
        this.ingredientes = ingredientes;
        this.categoriaNombre = categoriaNombre;
        this.autorNombre = autorNombre;
        this.autorFoto = autorFoto; // 🟢
        this.imagenUrl = imagenUrl;
        this.cantidadLikes = cantidadLikes;
        this.likedByCurrentUser = likedByCurrentUser;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getInstrucciones() {
        return instrucciones;
    }

    public void setInstrucciones(String instrucciones) {
        this.instrucciones = instrucciones;
    }

    public String getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(String ingredientes) {
        this.ingredientes = ingredientes;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public void setCategoriaNombre(String categoriaNombre) {
        this.categoriaNombre = categoriaNombre;
    }

    public String getAutorNombre() {
        return autorNombre;
    }

    public void setAutorNombre(String autorNombre) {
        this.autorNombre = autorNombre;
    }

    public String getAutorFoto() {
        return autorFoto;
    }

    public void setAutorFoto(String autorFoto) {
        this.autorFoto = autorFoto;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public int getCantidadLikes() {
        return cantidadLikes;
    }

    public void setCantidadLikes(int cantidadLikes) {
        this.cantidadLikes = cantidadLikes;
    }

    public boolean isLikedByCurrentUser() {
        return likedByCurrentUser;
    }

    public void setLikedByCurrentUser(boolean likedByCurrentUser) {
        this.likedByCurrentUser = likedByCurrentUser;
    }

    
}