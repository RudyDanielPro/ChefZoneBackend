package ChefZoneBackend.Controller;

import ChefZoneBackend.Entity.Recipe;
import ChefZoneBackend.Entity.User;
import ChefZoneBackend.Dto.Request.RecipeRequest;
import ChefZoneBackend.Dto.Response.RecipeResponse;
import ChefZoneBackend.Dto.Response.RecipeSummaryResponse;
import ChefZoneBackend.Service.LikeService;
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
@RequestMapping("/api/recetas")
@CrossOrigin(origins = "*")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private UserService userService;

    // ✅ PÚBLICO - Obtener todas las recetas
    @GetMapping
    public ResponseEntity<List<RecipeSummaryResponse>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }

    // ✅ PÚBLICO - Obtener receta por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getRecipeById(@PathVariable Long id) {
        try {
            RecipeResponse recipe = recipeService.getRecipeById(id);
            return ResponseEntity.ok(recipe);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ CUALQUIERA AUTENTICADO - Crear nueva receta
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createRecipe(@RequestBody RecipeRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            User user = userService.findByEmail(email);
            
            RecipeResponse recipe = recipeService.createRecipe(request, user.getId());
            return ResponseEntity.ok(recipe);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ SOLO DUEÑO O ADMIN - Actualizar receta
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @recipeSecurity.isOwner(#id, authentication.principal.username)")
    public ResponseEntity<?> updateRecipe(
            @PathVariable Long id,
            @RequestBody RecipeRequest request) {
        try {
            RecipeResponse recipe = recipeService.updateRecipe(id, request);
            return ResponseEntity.ok(recipe);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ SOLO DUEÑO O ADMIN - Eliminar receta
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @recipeSecurity.isOwner(#id, authentication.principal.username)")
    public ResponseEntity<?> deleteRecipe(@PathVariable Long id) {
        try {
            recipeService.deleteRecipe(id);
            return ResponseEntity.ok(Map.of("message", "Receta eliminada correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ CUALQUIERA AUTENTICADO - Subir foto de receta
    @PostMapping("/{id}/foto")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadRecipePhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            Recipe recipe = recipeService.updateRecipePhoto(id, file);
            RecipeResponse response = recipeService.getRecipeById(recipe.getId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ SOLO DUEÑO O ADMIN - Eliminar foto de receta
    @DeleteMapping("/{id}/foto")
    @PreAuthorize("hasRole('ADMIN') or @recipeSecurity.isOwner(#id, authentication.principal.username)")
    public ResponseEntity<?> deleteRecipePhoto(@PathVariable Long id) {
        try {
            recipeService.deleteRecipePhoto(id);
            return ResponseEntity.ok(Map.of("message", "Foto eliminada correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ PÚBLICO - Buscar recetas por categoría
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<RecipeSummaryResponse>> getRecipesByCategory(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(recipeService.getRecipesByCategory(categoriaId));
    }

    // ✅ PÚBLICO - Buscar recetas por ingrediente
    @GetMapping("/buscar/ingrediente")
    public ResponseEntity<List<RecipeSummaryResponse>> searchByIngredient(
            @RequestParam("q") String ingredient) {
        return ResponseEntity.ok(recipeService.searchByIngredient(ingredient));
    }

    // ✅ PÚBLICO - Buscar recetas por título
    @GetMapping("/buscar/titulo")
    public ResponseEntity<List<RecipeSummaryResponse>> searchByTitle(
            @RequestParam("q") String title) {
        return ResponseEntity.ok(recipeService.searchByTitle(title));
    }

    // ✅ CUALQUIERA AUTENTICADO - Dar o quitar like
    @PostMapping("/{id}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> toggleLike(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            User user = userService.findByEmail(email);
            
            boolean liked = likeService.toggleLike(user.getId(), id);
            
            return ResponseEntity.ok(Map.of(
                "liked", liked,
                "likesCount", likeService.getLikesCount(id)
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ CUALQUIERA AUTENTICADO - Verificar si dio like
    @GetMapping("/{id}/like/estado")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getLikeStatus(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            User user = userService.findByEmail(email);
            
            boolean hasLiked = likeService.hasUserLikedRecipe(user.getId(), id);
            long likesCount = likeService.getLikesCount(id);
            
            return ResponseEntity.ok(Map.of(
                "hasLiked", hasLiked,
                "likesCount", likesCount
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}