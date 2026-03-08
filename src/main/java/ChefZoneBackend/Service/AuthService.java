package ChefZoneBackend.Service;

import ChefZoneBackend.Dto.Request.LoginRequest;
import ChefZoneBackend.Dto.Request.RegisterRequest;
import ChefZoneBackend.Entity.User;

import ChefZoneBackend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User register(RegisterRequest request) {
        // Verificar si ya existe el email o usuario
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }
        if (userRepository.findByUsuario(request.getUsuario()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }
        
        // Crear nuevo usuario
        User user = new User();
        user.setNombre(request.getNombre());
        user.setApellido(request.getApellido());
        user.setEmail(request.getEmail());
        user.setUsuario(request.getUsuario());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRol("USER");
        
        return userRepository.save(user);
    }
    
    public User authenticate(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }
        
        return user;
    }
}