package ChefZoneBackend.Dto.Request;

public class RecipeRequest {
    private String titulo;
    private String descripcion;
    private String instrucciones;
    private String ingredientes;
    private Long categoriaId;

    public RecipeRequest() {
    }

    public RecipeRequest(String titulo, String descripcion, String instrucciones, String ingredientes, Long categoriaId) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.instrucciones = instrucciones;
        this.ingredientes = ingredientes;
        this.categoriaId = categoriaId;
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

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }
}