package com.example.pizzaapp.services;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

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
     * Ensure all existing users are enabled (for migration purposes)
     */
    public void enableAllExistingUsers() {
        List<User> allUsers = userRepository.findAll();
        boolean needsSave = false;

        for (User user : allUsers) {
            if (!user.isEnabled()) {
                user.setEnabled(true);
                needsSave = true;
            }
        }

        if (needsSave) {
            userRepository.saveAll(allUsers);
        }
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
     */
    public void toggleUserStatus(UUID userId) {
        toggleUserAccess(userId);
    }

    /**
     * Check if user has admin role
     */
    public boolean isAdmin(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName() == Role.RoleName.ROLE_ADMIN);
    }

    /**
     * Block/Unblock a user by toggling their enabled status
     * Only allows blocking of non-admin users
     */
    public void toggleUserAccess(UUID userId) {
        User user = getUserById(userId);

        // Prevent blocking of admin users
        if (isAdmin(user)) {
            throw new RuntimeException("Cannot block admin users");
        }

        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    /**
     * Block a user (set enabled to false)
     */
    public void blockUser(UUID userId) {
        User user = getUserById(userId);

        // Prevent blocking of admin users
        if (isAdmin(user)) {
            throw new RuntimeException("Cannot block admin users");
        }

        user.setEnabled(false);
        userRepository.save(user);
    }

    /**
     * Unblock a user (set enabled to true)
     */
    public void unblockUser(UUID userId) {
        User user = getUserById(userId);
        user.setEnabled(true);
        userRepository.save(user);
    }

    /**
     * Check if user is blocked
     */
    public boolean isUserBlocked(UUID userId) {
        User user = getUserById(userId);
        return !user.isEnabled();
    }

    /**
     * Change user password
     */
    public void changeUserPassword(UUID userId, String newPassword) {
        User user = getUserById(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
