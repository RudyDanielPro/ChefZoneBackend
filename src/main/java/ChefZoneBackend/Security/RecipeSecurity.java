package ChefZoneBackend.Security;

import ChefZoneBackend.Entity.Recipe;
import ChefZoneBackend.Entity.User;
import ChefZoneBackend.Repository.RecipeRepository;
import ChefZoneBackend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("recipeSecurity")
public class RecipeSecurity {
    
    @Autowired
    private RecipeRepository recipeRepository;
    
    @Autowired
    private UserService userService;
    
    public boolean isOwner(Long recipeId, String userEmail) {
        Recipe recipe = recipeRepository.findById(recipeId)
            .orElseThrow(() -> new RuntimeException("Receta no encontrada"));
        
        User user = userService.findByEmail(userEmail);
        
        return recipe.getUsuario().getId().equals(user.getId());
    }
}