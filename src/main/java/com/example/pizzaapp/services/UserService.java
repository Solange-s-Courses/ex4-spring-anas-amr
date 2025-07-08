package com.example.pizzaapp.services;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.pizzaapp.models.Role;
import com.example.pizzaapp.models.User;
import com.example.pizzaapp.repositories.RoleRepository;
import com.example.pizzaapp.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public User findUserByPrincipal(Principal principal) {
        if (principal == null) {
            return null;
        }
        return userRepository.findByEmail(principal.getName()).orElse(null);
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    // Admin methods for user management

    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get user by ID
     */
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    /**
     * Update user role
     */
    public void updateUserRole(UUID userId, String roleName) {
        User user = getUserById(userId);

        // Clear existing roles
        user.getRoles().clear();

        // Add new role
        Role.RoleName roleEnum;
        try {
            roleEnum = Role.RoleName.valueOf(roleName);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role: " + roleName);
        }

        Role role = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        user.getRoles().add(role);
        userRepository.save(user);
    }

    /**
     * Toggle user account status (enable/disable)
     * Note: This assumes you have an 'enabled' field in User model
     * If not, this method will need to be adjusted based on your User model
     */
    public void toggleUserStatus(UUID userId) {
        // Placeholder implementation - you can implement based on your needs
        // You may need to add an 'enabled' field to the User model

        // User user = getUserById(userId);
        // user.setEnabled(!user.isEnabled());
        // userRepository.save(user);

        throw new RuntimeException("User status toggle not yet implemented - please add 'enabled' field to User model");
    }

    /**
     * Check if user has admin role
     */
    public boolean isAdmin(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName() == Role.RoleName.ROLE_ADMIN);
    }
}
