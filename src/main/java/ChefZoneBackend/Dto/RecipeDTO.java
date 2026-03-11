package ChefZoneBackend.Dto;

import java.util.List;

public class RecipeDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private String instrucciones;
    private String ingredientes;
    private String categoriaNombre;
    private int likesCount;
    private boolean likedByCurrentUser;

    public RecipeDTO(Long id, String titulo, String descripcion, String instrucciones, String ingredientes,
            String categoriaNombre, int likesCount, boolean likedByCurrentUser) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.instrucciones = instrucciones;
        this.ingredientes = ingredientes;
        this.categoriaNombre = categoriaNombre;
        this.likesCount = likesCount;
        this.likedByCurrentUser = likedByCurrentUser;
    }

    public RecipeDTO() {
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

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public boolean isLikedByCurrentUser() {
        return likedByCurrentUser;
    }

    public void setLikedByCurrentUser(boolean likedByCurrentUser) {
        this.likedByCurrentUser = likedByCurrentUser;
    }

}