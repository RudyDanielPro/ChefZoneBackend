package ChefZoneBackend.Service;

import ChefZoneBackend.Entity.User;
import ChefZoneBackend.Entity.UserFoto;
import ChefZoneBackend.Dto.Response.UserProfileResponse;
import ChefZoneBackend.Repository.UserRepository;
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

    // Buscar usuario por email
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
    }

    // Obtener perfil de usuario
    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return convertToProfileResponse(user);
    }

    // ✅ NUEVO: Obtener todos los usuarios (para ADMIN)
    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToProfileResponse)
                .collect(Collectors.toList());
    }

    // Actualizar usuario
    public User updateUser(Long userId, User userDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (userDetails.getNombre() != null && !userDetails.getNombre().trim().isEmpty()) {
            user.setNombre(userDetails.getNombre());
        }
        if (userDetails.getApellido() != null && !userDetails.getApellido().trim().isEmpty()) {
            user.setApellido(userDetails.getApellido());
        }
        if (userDetails.getEmail() != null && !userDetails.getEmail().trim().isEmpty()) {
            // Verificar que el email no esté en uso por otro usuario
            if (!user.getEmail().equals(userDetails.getEmail())) {
                if (userRepository.findByEmail(userDetails.getEmail()).isPresent()) {
                    throw new RuntimeException("El email ya está en uso");
                }
            }
            user.setEmail(userDetails.getEmail());
        }
        if (userDetails.getUsuario() != null && !userDetails.getUsuario().trim().isEmpty()) {
            // Verificar que el usuario no esté en uso por otro usuario
            if (!user.getUsuario().equals(userDetails.getUsuario())) {
                if (userRepository.findByUsuario(userDetails.getUsuario()).isPresent()) {
                    throw new RuntimeException("El nombre de usuario ya está en uso");
                }
            }
            user.setUsuario(userDetails.getUsuario());
        }

        return userRepository.save(user);
    }

    // ✅ NUEVO: Eliminar usuario (para ADMIN)
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Eliminar foto de Cloudinary si existe
        if (user.getFoto() != null && user.getFoto().getRuta() != null) {
            try {
                String publicId = cloudinaryService.extractPublicIdFromUrl(user.getFoto().getRuta());
                cloudinaryService.deleteFile(publicId);
            } catch (Exception e) {
                System.err.println("Error al eliminar foto de Cloudinary: " + e.getMessage());
                // No lanzamos excepción, continuamos con la eliminación del usuario
            }
        }

        // Nota: Las recetas y likes se eliminarán en cascada si tienes las relaciones
        // configuradas
        userRepository.delete(user);
    }

    // Actualizar foto de perfil
    public User updateProfilePhoto(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        try {
            // Subir nueva foto a Cloudinary
            String fotoUrl = cloudinaryService.uploadFile(file, "users");

            // Eliminar foto anterior si existe
            if (user.getFoto() != null && user.getFoto().getRuta() != null) {
                try {
                    String publicId = cloudinaryService.extractPublicIdFromUrl(user.getFoto().getRuta());
                    cloudinaryService.deleteFile(publicId);
                } catch (Exception e) {
                    System.err.println("Error al eliminar foto anterior: " + e.getMessage());
                }
            }

            // Actualizar usuario con nueva foto
            if (user.getFoto() == null) {
                UserFoto foto = new UserFoto();
                foto.setRuta(fotoUrl);
                foto.setNombreArchivo(file.getOriginalFilename());
                user.setFoto(foto);
            } else {
                user.getFoto().setRuta(fotoUrl);
                user.getFoto().setNombreArchivo(file.getOriginalFilename());
            }

            return userRepository.save(user); // ✅ Devolver el usuario actualizado

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar foto de perfil: " + e.getMessage());
        }
    }

    // Eliminar foto de perfil
    public void deleteProfilePhoto(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (user.getFoto() != null) {
            try {
                // Eliminar de Cloudinary
                String publicId = cloudinaryService.extractPublicIdFromUrl(user.getFoto().getRuta());
                cloudinaryService.deleteFile(publicId);

                // Eliminar de BD
                user.setFoto(null);
                userRepository.save(user);

            } catch (IOException e) {
                throw new RuntimeException("Error al eliminar foto: " + e.getMessage());
            }
        }
    }

    // Convertir entidad User a UserProfileResponse
    private UserProfileResponse convertToProfileResponse(User user) {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setNombre(user.getNombre());
        response.setApellido(user.getApellido());
        response.setEmail(user.getEmail());
        response.setUsuario(user.getUsuario());
        if (user.getFoto() != null) {
            response.setFotoPerfil(user.getFoto().getRuta());
        }
        return response;
    }
}