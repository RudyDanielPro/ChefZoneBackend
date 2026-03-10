package ChefZoneBackend.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import ChefZoneBackend.Entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsuario(String usuario);

    Optional<User> findByUsuarioOrEmail(String usuario, String email);

    boolean existsByUsuario(String usuario);

    List<User> findByNombre(String nombre);

    List<User> findByApellido(String apellido);

    List<User> findByNombreAndApellido(String nombre, String apellido);

    List<User> findByRol(String rol);

    boolean existsByEmail(String email);
}