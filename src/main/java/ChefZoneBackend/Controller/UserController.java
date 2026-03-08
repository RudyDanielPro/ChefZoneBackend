package ChefZoneBackend.Controller;

import ChefZoneBackend.Entity.User;
import ChefZoneBackend.Dto.Response.RecipeSummaryResponse;
import ChefZoneBackend.Dto.Response.UserProfileResponse;
import ChefZoneBackend.Service.RecipeService;
import ChefZoneBackend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RecipeService recipeService;

    // ✅ SOLO ADMIN - Listar todos los usuarios
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserProfileResponse> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ CUALQUIERA AUTENTICADO - Obtener perfil del usuario actual
    @GetMapping("/perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUserProfile() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            User user = userService.findByEmail(email);
            UserProfileResponse profile = userService.getProfile(user.getId());

            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ PÚBLICO - Obtener perfil de un usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long id) {
        try {
            UserProfileResponse profile = userService.getProfile(id);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ SOLO EL MISMO USUARIO O ADMIN - Actualizar usuario
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isSameUser(#id, authentication.principal.username)")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            UserProfileResponse profile = userService.getProfile(updatedUser.getId());
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ SOLO ADMIN - Eliminar usuario
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "Usuario eliminado correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ SOLO EL MISMO USUARIO O ADMIN - Subir foto de perfil
    @PostMapping("/{id}/foto")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isSameUser(#id, authentication.principal.username)")
    public ResponseEntity<?> uploadProfilePhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            User user = userService.updateProfilePhoto(id, file);
            UserProfileResponse profile = userService.getProfile(user.getId());
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ SOLO EL MISMO USUARIO O ADMIN - Eliminar foto de perfil
    @DeleteMapping("/{id}/foto")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isSameUser(#id, authentication.principal.username)")
    public ResponseEntity<?> deleteProfilePhoto(@PathVariable Long id) {
        try {
            userService.deleteProfilePhoto(id);
            return ResponseEntity.ok(Map.of("message", "Foto eliminada correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ PÚBLICO - Obtener recetas de un usuario
    @GetMapping("/{id}/recetas")
    public ResponseEntity<?> getUserRecipes(@PathVariable Long id) {
        try {
            List<RecipeSummaryResponse> recipes = recipeService.getRecipesByUser(id);
            return ResponseEntity.ok(recipes);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}