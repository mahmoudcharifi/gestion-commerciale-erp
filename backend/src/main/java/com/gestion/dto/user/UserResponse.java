
package com.gestion.dto.user;

import java.util.List;

public record UserResponse(
        Long id,
        String nom,
        String prenom,
        String email,
        boolean actif,
        List<String> roles
) {}
