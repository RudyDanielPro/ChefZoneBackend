package ChefZoneBackend.Service;

import ChefZoneBackend.Entity.Category;
import ChefZoneBackend.Entity.Recipe;
import ChefZoneBackend.Entity.RecipeFoto;
import ChefZoneBackend.Entity.User;
import ChefZoneBackend.Dto.Request.RecipeRequest;
import ChefZoneBackend.Dto.Response.RecipeResponse;
import ChefZoneBackend.Dto.Response.RecipeSummaryResponse;
import ChefZoneBackend.Repository.CategoryRepository;
import ChefZoneBackend.Repository.LikeRepository;
import ChefZoneBackend.Repository.RecipeRepository;
import ChefZoneBackend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        
        Recipe savedRecipe = recipeRepository.save(recipe);
        return convertToResponse(savedRecipe);
    }
    
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
    
    public List<RecipeSummaryResponse> getRecipesByUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        return user.getRecetas().stream()
            .map(this::convertToSummaryResponse)
            .collect(Collectors.toList());
    }
    
    public Recipe updateRecipePhoto(Long recipeId, MultipartFile file) {
        Recipe recipe = recipeRepository.findById(recipeId)
            .orElseThrow(() -> new RuntimeException("Receta no encontrada"));
        
        try {
            // Subir nueva foto a Cloudinary
            String fotoUrl = cloudinaryService.uploadFile(file, "recipes");
            
            // Eliminar foto anterior si existe
            if (recipe.getFoto() != null && recipe.getFoto().getRuta() != null) {
                try {
                    String publicId = cloudinaryService.extractPublicIdFromUrl(recipe.getFoto().getRuta());
                    cloudinaryService.deleteFile(publicId);
                } catch (Exception e) {
                    System.err.println("Error al eliminar foto anterior: " + e.getMessage());
                }
            }
            
            // Actualizar receta con nueva foto
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
            throw new RuntimeException("Error al actualizar foto de receta: " + e.getMessage());
        }
    }
    
    public void deleteRecipePhoto(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
            .orElseThrow(() -> new RuntimeException("Receta no encontrada"));
        
        if (recipe.getFoto() != null) {
            try {
                // Eliminar de Cloudinary
                String publicId = cloudinaryService.extractPublicIdFromUrl(recipe.getFoto().getRuta());
                cloudinaryService.deleteFile(publicId);
                
                // Eliminar de BD
                recipe.setFoto(null);
                recipeRepository.save(recipe);
                
            } catch (Exception e) {
                throw new RuntimeException("Error al eliminar foto: " + e.getMessage());
            }
        }
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
        return convertToResponse(updatedRecipe);
    }
    
    public void deleteRecipe(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
            .orElseThrow(() -> new RuntimeException("Receta no encontrada"));
        
        // Eliminar foto de Cloudinary si existe
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
    
    private RecipeResponse convertToResponse(Recipe recipe) {
        RecipeResponse response = new RecipeResponse();
        response.setId(recipe.getId());
        response.setTitulo(recipe.getTitulo());
        response.setDescripcion(recipe.getDescripcion());
        response.setInstrucciones(recipe.getInstrucciones());
        response.setIngredientes(recipe.getIngredientes());
        response.setCategoriaNombre(recipe.getCategoria().getNombre());
        response.setAutorNombre(recipe.getUsuario().getNombre() + " " + recipe.getUsuario().getApellido());
        
        if (recipe.getFoto() != null) {
            response.setImagenUrl(recipe.getFoto().getRuta());
        }
        
        long likesCount = likeRepository.countByReceta(recipe);
        response.setCantidadLikes((int) likesCount);
        
        return response;
    }
    
    private RecipeSummaryResponse convertToSummaryResponse(Recipe recipe) {
        RecipeSummaryResponse response = new RecipeSummaryResponse();
        response.setId(recipe.getId());
        response.setTitulo(recipe.getTitulo());
        
        String descCorta = recipe.getDescripcion();
        if (descCorta != null && descCorta.length() > 100) {
            descCorta = descCorta.substring(0, 100) + "...";
        }
        response.setDescripcionCorta(descCorta);
        
        response.setCategoriaNombre(recipe.getCategoria().getNombre());
        response.setAutorNombre(recipe.getUsuario().getNombre() + " " + recipe.getUsuario().getApellido());
        
        if (recipe.getFoto() != null) {
            response.setImagenUrl(recipe.getFoto().getRuta());
        }
        
        long likesCount = likeRepository.countByReceta(recipe);
        response.setCantidadLikes((int) likesCount);
        
        return response;
    }
}