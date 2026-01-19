package org.gso.profiles.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrentUserDto {
    private String userId;
    private String username;
    private String email;
    private List<String> roles;

    public static CurrentUserDto fromJwt(Jwt jwt) {
        List<String> roles = (List<String>) jwt.getClaim("realm_access");
        if (roles == null) {
            roles = List.of();
        }

        return CurrentUserDto.builder()
                .userId(jwt.getSubject())
                .username(jwt.getClaimAsString("preferred_username"))
                .email(jwt.getClaimAsString("email"))
                .roles(roles)
                .build();
    }
}
