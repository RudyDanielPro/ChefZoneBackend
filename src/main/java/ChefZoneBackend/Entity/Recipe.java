package ChefZoneBackend.Entity;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Recetas")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "instrucciones", nullable = false)
    private String instrucciones;

    @Column(name = "ingredientes", nullable = false)
    private String ingredientes;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Category categoria;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private User usuario;

    @OneToMany(mappedBy = "receta", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Like> likes = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "foto_id")
    private RecipeFoto foto;

    public Recipe(String titulo, String descripcion, String instrucciones, String ingredientes, Category categoria,
            User usuario) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.instrucciones = instrucciones;
        this.ingredientes = ingredientes;
        this.categoria = categoria;
        this.usuario = usuario;
        this.likes = new ArrayList<>();
    }

    public Recipe() {
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

    public Category getCategoria() {
        return categoria;
    }

    public void setCategoria(Category categoria) {
        this.categoria = categoria;
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    public RecipeFoto getFoto() {
        return foto;
    }

    public void setFoto(RecipeFoto foto) {
        this.foto = foto;
    }

    
}
