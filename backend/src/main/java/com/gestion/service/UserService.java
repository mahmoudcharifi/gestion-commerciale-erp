
package com.gestion.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestion.dto.user.CreateUserRequest;
import com.gestion.dto.user.UpdateUserRequest;
import com.gestion.dto.user.UserResponse;
import com.gestion.entity.Role;
import com.gestion.entity.Utilisateur;
import com.gestion.repository.RoleRepository;
import com.gestion.repository.UtilisateurRepository;

@Service
@Transactional
public class UserService {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UtilisateurRepository utilisateurRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.utilisateurRepository = utilisateurRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // CREATE USER
    public UserResponse createUser(CreateUserRequest request) {

        String email = request.email().trim().toLowerCase();

        if (utilisateurRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Cette adresse email est déjà utilisée."
            );
        }

        Utilisateur utilisateur = new Utilisateur();

        utilisateur.setNom(request.nom().trim());
        utilisateur.setPrenom(request.prenom().trim());
        utilisateur.setEmail(email);

        utilisateur.setMotDePasse(
                passwordEncoder.encode(request.password())
        );

        utilisateur.setActif(true);
        utilisateur.setRoles(getRoles(request.roles()));

        Utilisateur savedUser =
                utilisateurRepository.save(utilisateur);

        return mapToResponse(savedUser);
    }

    // GET ALL USERS
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {

        return utilisateurRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // GET USER BY ID
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {

        Utilisateur utilisateur = findUserById(id);

        return mapToResponse(utilisateur);
    }

    // UPDATE USER
    public UserResponse updateUser(
            Long id,
            UpdateUserRequest request
    ) {

        Utilisateur utilisateur = findUserById(id);

        String email = request.email().trim().toLowerCase();

        utilisateurRepository.findByEmail(email)
                .filter(existing ->
                        !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Cette adresse email est déjà utilisée."
                    );
                });

        utilisateur.setNom(request.nom().trim());
        utilisateur.setPrenom(request.prenom().trim());
        utilisateur.setEmail(email);

        if (request.roles() != null) {
            utilisateur.setRoles(getRoles(request.roles()));
        }

        return mapToResponse(
                utilisateurRepository.save(utilisateur)
        );
    }

    // ACTIVATE / DEACTIVATE USER
    public UserResponse updateUserStatus(Long id, boolean actif) {

        Utilisateur utilisateur = findUserById(id);

        if (!actif && utilisateur.getActif()
                && utilisateur.getRoles().stream()
                        .anyMatch(role -> role.getNom().equals("ADMIN"))) {

            long activeAdmins =
                    utilisateurRepository.countByRoles_NomAndActifTrue("ADMIN");

            if (activeAdmins <= 1) {
                throw new IllegalArgumentException(
                        "Impossible de désactiver le dernier administrateur actif."
                );
            }
        }

        utilisateur.setActif(actif);

        return mapToResponse(
                utilisateurRepository.save(utilisateur)
        );
    }

    public void deleteUser(Long id) {

        Utilisateur utilisateur = findUserById(id);

        boolean isAdmin = utilisateur.getRoles()
                .stream()
                .anyMatch(role -> role.getNom().equals("ADMIN"));

        if (Boolean.TRUE.equals(utilisateur.getActif()) && isAdmin) {

            long activeAdmins =
                    utilisateurRepository.countByRoles_NomAndActifTrue("ADMIN");

            if (activeAdmins <= 1) {
                throw new IllegalArgumentException(
                        "Impossible de supprimer le dernier administrateur actif."
                );
            }
        }

        utilisateurRepository.delete(utilisateur);
    }

    // FIND USER
    private Utilisateur findUserById(Long id) {

        return utilisateurRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Utilisateur introuvable : " + id
                        )
                );
    }

    // GET ROLES
    private Set<Role> getRoles(List<String> roleNames) {

        if (roleNames == null || roleNames.isEmpty()) {
            throw new IllegalArgumentException(
                    "Au moins un rôle est obligatoire."
            );
        }

        return roleNames.stream()
                .map(name -> name.trim().toUpperCase())
                .map(name -> roleRepository.findByNom(name)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Rôle introuvable : " + name
                                )
                        )
                )
                .collect(Collectors.toSet());
    }

    // ENTITY TO DTO
    private UserResponse mapToResponse(Utilisateur utilisateur) {

        List<String> roles = utilisateur.getRoles()
                .stream()
                .map(Role::getNom)
                .sorted()
                .toList();

        return new UserResponse(
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                Boolean.TRUE.equals(utilisateur.getActif()),
                roles
        );
    }
}
