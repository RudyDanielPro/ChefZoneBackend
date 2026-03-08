package ChefZoneBackend.Repository;

import ChefZoneBackend.Entity.Recipe;
import ChefZoneBackend.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findByCategoria(Category categoria);

    List<Recipe> findByTituloContainingIgnoreCase(String titulo);

    @Query("SELECT r FROM Recipe r WHERE LOWER(r.ingredientes) LIKE LOWER(CONCAT('%', :ingrediente, '%'))")
    List<Recipe> findByIngredienteContaining(@Param("ingrediente") String ingrediente);

    List<Recipe> findByCategoriaAndTituloContainingIgnoreCase(Category categoria, String titulo);
}