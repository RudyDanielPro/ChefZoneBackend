package ChefZoneBackend.Controller;

import ChefZoneBackend.Entity.User;
import ChefZoneBackend.Dto.Request.LoginRequest;
import ChefZoneBackend.Dto.Request.RegisterRequest;
import ChefZoneBackend.Dto.Response.UserProfileResponse;
import ChefZoneBackend.Security.JwtUtils;
import ChefZoneBackend.Service.AuthService;
import ChefZoneBackend.Service.UserService; // ✅ Inyectamos el servicio de usuario
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService; // ✅ Nueva inyección necesaria

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = authService.register(request);

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateToken((UserDetails) authentication.getPrincipal());

            UserProfileResponse userResponse = userService.getProfile(user.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("user", userResponse);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateToken((UserDetails) authentication.getPrincipal());

            User user = authService.authenticate(request);

            // ✅ CORRECCIÓN: Usamos el servicio para obtener el perfil con sus estadísticas reales
            UserProfileResponse userResponse = userService.getProfile(user.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("user", userResponse);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}