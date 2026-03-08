package ChefZoneBackend.Service;

import ChefZoneBackend.Entity.Category;
import ChefZoneBackend.Repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
    }
    
    public Category getCategoryByNombre(String nombre) {
        return categoryRepository.findByNombre(nombre)
            .orElseThrow(() -> new RuntimeException("Categoría no encontrada: " + nombre));
    }
    
    public Category createCategory(String nombre, String descripcion) {
        // Verificar si ya existe
        if (categoryRepository.findByNombre(nombre).isPresent()) {
            throw new RuntimeException("Ya existe una categoría con ese nombre");
        }
        
        Category category = new Category(nombre, descripcion);
        return categoryRepository.save(category);
    }
    
    public Category updateCategory(Long id, String nombre, String descripcion) {
        Category category = getCategoryById(id);
        
        if (nombre != null && !nombre.equals(category.getNombre())) {
            // Verificar que el nuevo nombre no esté en uso
            if (categoryRepository.findByNombre(nombre).isPresent()) {
                throw new RuntimeException("Ya existe una categoría con ese nombre");
            }
            category.setNombre(nombre);
        }
        
        if (descripcion != null) {
            category.setDescripcion(descripcion);
        }
        
        return categoryRepository.save(category);
    }
    
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        
        // Verificar si tiene recetas asociadas
        if (!category.getRecetas().isEmpty()) {
            throw new RuntimeException("No se puede eliminar la categoría porque tiene " + 
                                     category.getRecetas().size() + " recetas asociadas");
        }
        
        categoryRepository.delete(category);
    }
}