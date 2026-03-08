package ChefZoneBackend.Repository;

import ChefZoneBackend.Entity.Like;
import ChefZoneBackend.Entity.User;
import ChefZoneBackend.Entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUsuarioAndReceta(User usuario, Recipe receta);

    long countByReceta(Recipe receta);

    List<Like> findByUsuario(User usuario);

    List<Like> findByReceta(Recipe receta);
}