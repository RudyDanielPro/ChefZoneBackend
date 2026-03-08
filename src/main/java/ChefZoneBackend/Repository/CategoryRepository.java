package ChefZoneBackend.Repository;

import ChefZoneBackend.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByNombre(String nombre);
}