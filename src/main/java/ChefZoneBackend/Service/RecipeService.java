package ChefZoneBackend.Service;

import ChefZoneBackend.Entity.Category;
import ChefZoneBackend.Entity.Recipe;
import ChefZoneBackend.Entity.RecipeFoto;
import ChefZoneBackend.Entity.User;
import ChefZoneBackend.Dto.Response.RecipeResponse;
import ChefZoneBackend.Dto.Response.RecipeSummaryResponse;
import ChefZoneBackend.Dto.Request.RecipeRequest;
import ChefZoneBackend.Repository.CategoryRepository;
import ChefZoneBackend.Repository.LikeRepository;
import ChefZoneBackend.Repository.RecipeRepository;
import ChefZoneBackend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    // ============================================================
    // MÉTODOS DE CONSULTA
    // ============================================================

    public List<RecipeSummaryResponse> getAllRecipes() {
        return recipeRepository.findAll().stream()
                .map(this::convertToSummaryResponse)
                .collect(Collectors.toList());
    }

    public RecipeResponse getRecipeById(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));
        return convertToResponse(recipe);
    }

    public List<RecipeSummaryResponse> getRecipesByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        return recipeRepository.findByCategoria(category).stream()
                .map(this::convertToSummaryResponse)
                .collect(Collectors.toList());
    }

    public List<RecipeSummaryResponse> getRecipesByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return user.getRecetas().stream()
                .map(this::convertToSummaryResponse)
                .collect(Collectors.toList());
    }

    // ============================================================
    // MÉTODOS DE ACCIÓN (CREAR, ACTUALIZAR, FOTOS)
    // ============================================================

    public RecipeResponse createRecipe(RecipeRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Category category = categoryRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        Recipe recipe = new Recipe(
                request.getTitulo(),
                request.getDescripcion(),
                request.getInstrucciones(),
                request.getIngredientes(),
                category,
                user
        );

        return convertToResponse(recipeRepository.save(recipe));
    }

    public Recipe updateRecipePhoto(Long recipeId, MultipartFile file) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));

        try {
            String fotoUrl = cloudinaryService.uploadFile(file, "recipes");

            if (recipe.getFoto() == null) {
                RecipeFoto foto = new RecipeFoto();
                foto.setRuta(fotoUrl);
                foto.setNombreArchivo(file.getOriginalFilename());
                recipe.setFoto(foto);
            } else {
                recipe.getFoto().setRuta(fotoUrl);
                recipe.getFoto().setNombreArchivo(file.getOriginalFilename());
            }

            return recipeRepository.save(recipe);
        } catch (Exception e) {
            throw new RuntimeException("Error al subir imagen: " + e.getMessage());
        }
    }

    // ============================================================
    // MÉTODOS DE CONVERSIÓN (MAPEO A DTO)
    // ============================================================

    /**
     * Convierte una Receta al DTO de Resumen (usado en listas/cards)
     * 🟢 Incluye autorFoto para que se vea el avatar en el frontend
     */
    private RecipeSummaryResponse convertToSummaryResponse(Recipe recipe) {
        User currentUser = getCurrentUserEntity();
        RecipeSummaryResponse response = new RecipeSummaryResponse();

        response.setId(recipe.getId());
        response.setTitulo(recipe.getTitulo());

        // Recortar descripción para el resumen
        String desc = recipe.getDescripcion();
        if (desc != null && desc.length() > 100) {
            desc = desc.substring(0, 100) + "...";
        }
        response.setDescripcionCorta(desc);
        
        response.setCategoriaNombre(recipe.getCategoria().getNombre());
        
        // Datos del Autor
        User autor = recipe.getUsuario();
        response.setAutorNombre(autor.getNombre() + " " + autor.getApellido());
        
        // 🟢 CARGA DE FOTO DE PERFIL DEL AUTOR
        if (autor.getFoto() != null) {
            response.setAutorFoto(autor.getFoto().getRuta());
        }

        // Imagen de la receta
        if (recipe.getFoto() != null) {
            response.setImagenUrl(recipe.getFoto().getRuta());
        }

        // Lógica de Likes (Sincronizada con el frontend)
        response.setCantidadLikes((int) likeRepository.countByReceta(recipe));
        if (currentUser != null) {
            response.setLikedByCurrentUser(likeRepository.existsByRecetaAndUsuario(recipe, currentUser));
        }

        return response;
    }

    /**
     * Convierte una Receta al DTO detallado
     */
    private RecipeResponse convertToResponse(Recipe recipe) {
        User currentUser = getCurrentUserEntity();
        RecipeResponse response = new RecipeResponse();
        
        response.setId(recipe.getId());
        response.setTitulo(recipe.getTitulo());
        response.setDescripcion(recipe.getDescripcion());
        response.setInstrucciones(recipe.getInstrucciones());
        response.setIngredientes(recipe.getIngredientes());
        response.setCategoriaNombre(recipe.getCategoria().getNombre());
        response.setAutorNombre(recipe.getUsuario().getNombre() + " " + recipe.getUsuario().getApellido());

        // 🟢 Foto del autor también para el detalle
        if (recipe.getUsuario().getFoto() != null) {
            response.setAutorFoto(recipe.getUsuario().getFoto().getRuta());
        }

        if (recipe.getFoto() != null) {
            response.setImagenUrl(recipe.getFoto().getRuta());
        }

        response.setCantidadLikes((int) likeRepository.countByReceta(recipe));
        if (currentUser != null) {
            response.setLikedByCurrentUser(likeRepository.existsByRecetaAndUsuario(recipe, currentUser));
        }

        return response;
    }

    // ============================================================
    // UTILIDADES
    // ============================================================

    private User getCurrentUserEntity() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        return userRepository.findByEmail(auth.getName()).orElse(null);
    }

    public List<RecipeSummaryResponse> searchByIngredient(String ingredient) {
        return recipeRepository.findByIngredienteContaining(ingredient).stream()
                .map(this::convertToSummaryResponse)
                .collect(Collectors.toList());
    }

    public List<RecipeSummaryResponse> searchByTitle(String title) {
        return recipeRepository.findByTituloContainingIgnoreCase(title).stream()
                .map(this::convertToSummaryResponse)
                .collect(Collectors.toList());
    }

    public RecipeResponse updateRecipe(Long recipeId, RecipeRequest request) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));
        
        if (request.getTitulo() != null) recipe.setTitulo(request.getTitulo());
        if (request.getDescripcion() != null) recipe.setDescripcion(request.getDescripcion());
        if (request.getInstrucciones() != null) recipe.setInstrucciones(request.getInstrucciones());
        if (request.getIngredientes() != null) recipe.setIngredientes(request.getIngredientes());
        
        if (request.getCategoriaId() != null) {
            Category category = categoryRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            recipe.setCategoria(category);
        }
        
        Recipe updatedRecipe = recipeRepository.save(recipe);
        return convertToResponse(updatedRecipe); // 🟢 Aprovecha el mapeo que ya incluye la foto
    }

    public void deleteRecipe(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));
        
        // Limpiamos la foto de Cloudinary antes de borrar la receta
        if (recipe.getFoto() != null) {
            try {
                String publicId = cloudinaryService.extractPublicIdFromUrl(recipe.getFoto().getRuta());
                cloudinaryService.deleteFile(publicId);
            } catch (Exception e) {
                System.err.println("Error al eliminar foto de Cloudinary: " + e.getMessage());
            }
        }
        
        recipeRepository.delete(recipe);
    }

    public void deleteRecipePhoto(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));
        
        if (recipe.getFoto() != null) {
            try {
                String publicId = cloudinaryService.extractPublicIdFromUrl(recipe.getFoto().getRuta());
                cloudinaryService.deleteFile(publicId);
                recipe.setFoto(null);
                recipeRepository.save(recipe);
            } catch (Exception e) {
                throw new RuntimeException("Error al eliminar foto: " + e.getMessage());
            }
        }
    }
}