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

    @GetMapping
    public ResponseEntity<List<RecipeSummaryResponse>> getAllRecipes() {
        List<RecipeSummaryResponse> recetas = recipeService.getAllRecipes();
        // 🟢 Aseguramos que cada resumen tenga sus likes actualizados
        recetas.forEach(r -> r.setCantidadLikes((int) likeService.getLikesCount(r.getId())));
        return ResponseEntity.ok(recetas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRecipeById(@PathVariable Long id) {
        try {
            RecipeResponse recipe = recipeService.getRecipeById(id);
            return ResponseEntity.ok(recipe);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

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

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<RecipeSummaryResponse>> getRecipesByCategory(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(recipeService.getRecipesByCategory(categoriaId));
    }

    @GetMapping("/buscar/ingrediente")
    public ResponseEntity<List<RecipeSummaryResponse>> searchByIngredient(
            @RequestParam("q") String ingredient) {
        return ResponseEntity.ok(recipeService.searchByIngredient(ingredient));
    }

    @GetMapping("/buscar/titulo")
    public ResponseEntity<List<RecipeSummaryResponse>> searchByTitle(
            @RequestParam("q") String title) {
        return ResponseEntity.ok(recipeService.searchByTitle(title));
    }

    @PostMapping("/{id}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> toggleLike(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            User user = userService.findByEmail(email);

            // 🟢 Ejecutamos el cambio en la BD
            boolean liked = likeService.toggleLike(user.getId(), id);

            // 🟢 Obtenemos el conteo real directamente del servicio
            long totalLikes = likeService.getLikesCount(id);

            return ResponseEntity.ok(Map.of(
                    "liked", liked,
                    "cantidadLikes", totalLikes // ✅ Ahora sí coincide con tu RecipeCard.tsx
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

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
                    "likesCount", likesCount));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}