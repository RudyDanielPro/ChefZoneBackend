package ChefZoneBackend.Service;

import ChefZoneBackend.Entity.User;
import ChefZoneBackend.Entity.UserFoto;
import ChefZoneBackend.Entity.Recipe;
import ChefZoneBackend.Dto.Response.UserProfileResponse;
import ChefZoneBackend.Repository.UserRepository;
import ChefZoneBackend.Repository.RecipeRepository;
import ChefZoneBackend.Repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    // ✅ Inyecciones agregadas para los contadores
    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private LikeRepository likeRepository;

    // Buscar usuario por email
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
    }

    // ✅ NUEVO: Buscar usuario por ID (Soluciona el error en el controller)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    // Obtener perfil de usuario
    public UserProfileResponse getProfile(Long userId) {
        User user = findById(userId);
        return convertToProfileResponse(user);
    }

    // Obtener todos los usuarios (para ADMIN)
    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToProfileResponse)
                .collect(Collectors.toList());
    }

    // Actualizar usuario
    public User updateUser(Long userId, User userDetails) {
        User user = findById(userId);

        if (userDetails.getNombre() != null && !userDetails.getNombre().trim().isEmpty()) {
            user.setNombre(userDetails.getNombre());
        }
        if (userDetails.getApellido() != null && !userDetails.getApellido().trim().isEmpty()) {
            user.setApellido(userDetails.getApellido());
        }
        if (userDetails.getEmail() != null && !userDetails.getEmail().trim().isEmpty()) {
            if (!user.getEmail().equals(userDetails.getEmail())) {
                if (userRepository.findByEmail(userDetails.getEmail()).isPresent()) {
                    throw new RuntimeException("El email ya está en uso");
                }
            }
            user.setEmail(userDetails.getEmail());
        }
        if (userDetails.getUsuario() != null && !userDetails.getUsuario().trim().isEmpty()) {
            if (!user.getUsuario().equals(userDetails.getUsuario())) {
                if (userRepository.findByUsuario(userDetails.getUsuario()).isPresent()) {
                    throw new RuntimeException("El nombre de usuario ya está en uso");
                }
            }
            user.setUsuario(userDetails.getUsuario());
        }

        return userRepository.save(user);
    }

    // Eliminar usuario (para ADMIN)
    @Transactional
    public void deleteUser(Long userId) {
        User user = findById(userId);

        if (user.getFoto() != null && user.getFoto().getRuta() != null) {
            try {
                String publicId = cloudinaryService.extractPublicIdFromUrl(user.getFoto().getRuta());
                cloudinaryService.deleteFile(publicId);
            } catch (Exception e) {
                System.err.println("Error al eliminar foto de Cloudinary: " + e.getMessage());
            }
        }

        userRepository.delete(user);
    }

    // Actualizar foto de perfil
    public User updateProfilePhoto(Long userId, MultipartFile file) {
        User user = findById(userId);

        try {
            String fotoUrl = cloudinaryService.uploadFile(file, "users");

            if (user.getFoto() != null && user.getFoto().getRuta() != null) {
                try {
                    String publicId = cloudinaryService.extractPublicIdFromUrl(user.getFoto().getRuta());
                    cloudinaryService.deleteFile(publicId);
                } catch (Exception e) {
                    System.err.println("Error al eliminar foto anterior: " + e.getMessage());
                }
            }

            if (user.getFoto() == null) {
                UserFoto foto = new UserFoto();
                foto.setRuta(fotoUrl);
                foto.setNombreArchivo(file.getOriginalFilename());
                user.setFoto(foto);
            } else {
                user.getFoto().setRuta(fotoUrl);
                user.getFoto().setNombreArchivo(file.getOriginalFilename());
            }

            return userRepository.save(user);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar foto de perfil: " + e.getMessage());
        }
    }

    // Eliminar foto de perfil
    public void deleteProfilePhoto(Long userId) {
        User user = findById(userId);

        if (user.getFoto() != null) {
            try {
                String publicId = cloudinaryService.extractPublicIdFromUrl(user.getFoto().getRuta());
                cloudinaryService.deleteFile(publicId);

                user.setFoto(null);
                userRepository.save(user);

            } catch (IOException e) {
                throw new RuntimeException("Error al eliminar foto: " + e.getMessage());
            }
        }
    }

    // ✅ ACTUALIZADO: Convertir entidad User a UserProfileResponse con los
    // contadores
    private UserProfileResponse convertToProfileResponse(User user) {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setNombre(user.getNombre());
        response.setApellido(user.getApellido());
        response.setEmail(user.getEmail());
        response.setUsuario(user.getUsuario());
        response.setRol(user.getRol());

        if (user.getFoto() != null) {
            response.setFotoPerfil(user.getFoto().getRuta());
        }

        // 🟢 MEJORA: Contar recetas directamente
        response.setRecetasCount(user.getRecetas().size());

        // 🟢 MEJORA: Sumar todos los likes que han recibido sus recetas
        // Esto cuenta cuántas veces a la gente "le han gustado" las cosas de este
        // usuario
        int totalLikesRecibidos = user.getRecetas().stream()
                .mapToInt(r -> r.getLikes().size())
                .sum();
        response.setLikesCount(totalLikesRecibidos);

        return response;
    }
}