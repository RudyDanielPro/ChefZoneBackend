package ChefZoneBackend.Service;

import ChefZoneBackend.Entity.Like;
import ChefZoneBackend.Entity.Recipe;
import ChefZoneBackend.Entity.User;
import ChefZoneBackend.Repository.LikeRepository;
import ChefZoneBackend.Repository.RecipeRepository;
import ChefZoneBackend.Repository.UserRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Transactional
    public boolean toggleLike(Long userId, Long recipeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));

        // Verificar si ya existe el like
        var existingLike = likeRepository.findByUsuarioAndReceta(user, recipe);

        if (existingLike.isPresent()) {
            Like likeToRemove = existingLike.get();

            // 🟢 DESVINCULAR ANTES DE BORRAR (Para sincronizar caché de Hibernate)
            user.getLikes().remove(likeToRemove);
            recipe.getLikes().remove(likeToRemove);

            likeRepository.delete(likeToRemove);
            return false;
        } else {
            Like like = new Like(user, recipe);

            // 🟢 VINCULAR ANTES DE GUARDAR
            user.getLikes().add(like);
            recipe.getLikes().add(like);

            likeRepository.save(like);
            return true;
        }
    }

    public boolean hasUserLikedRecipe(Long userId, Long recipeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));

        return likeRepository.findByUsuarioAndReceta(user, recipe).isPresent();
    }

    public long getLikesCount(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));

        return likeRepository.countByReceta(recipe);
    }

    @Transactional
    public void deleteAllLikesByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Like> userLikes = likeRepository.findByUsuario(user);
        likeRepository.deleteAll(userLikes);
    }

    @Transactional
    public void deleteAllLikesByRecipe(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));

        List<Like> recipeLikes = likeRepository.findByReceta(recipe);
        likeRepository.deleteAll(recipeLikes);
    }
}