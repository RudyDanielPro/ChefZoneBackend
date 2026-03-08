package ChefZoneBackend.Security;

import ChefZoneBackend.Entity.User;
import ChefZoneBackend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {
    
    @Autowired
    private UserService userService;
    
    public boolean isSameUser(Long userId, String userEmail) {
        User user = userService.findByEmail(userEmail);
        return user.getId().equals(userId);
    }
}