package ChefZoneBackend.Dto.Response;

public class RecipeSummaryResponse {
    private Long id;
    private String titulo;
    private String descripcionCorta;
    private String categoriaNombre;
    private String autorNombre;
    private String imagenUrl;
    private int cantidadLikes;
    private boolean likedByCurrentUser; // 🟢 Nuevo campo

    

    public RecipeSummaryResponse(String titulo, String descripcionCorta, String categoriaNombre, String autorNombre,
            String imagenUrl, int cantidadLikes, boolean likedByCurrentUser) {
        this.titulo = titulo;
        this.descripcionCorta = descripcionCorta;
        this.categoriaNombre = categoriaNombre;
        this.autorNombre = autorNombre;
        this.imagenUrl = imagenUrl;
        this.cantidadLikes = cantidadLikes;
        this.likedByCurrentUser = likedByCurrentUser;
    }

    public RecipeSummaryResponse() {
    }

    // Getters y Setters
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

    public String getDescripcionCorta() {
        return descripcionCorta;
    }

    public void setDescripcionCorta(String descripcionCorta) {
        this.descripcionCorta = descripcionCorta;
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