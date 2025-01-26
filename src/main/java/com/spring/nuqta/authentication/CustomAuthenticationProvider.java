package com.spring.nuqta.authentication;

import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.organization.Repo.OrgRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final OrgRepo organizationRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        String email = authentication.getName(); // true
        String password = authentication.getCredentials().toString();
        Optional<OrgEntity> organization =
                organizationRepo.findByEmail(email);

        if (organization.isPresent()) {
            if (passwordEncoder.matches(password, organization.get().getPassword())) {

                return new UsernamePasswordAuthenticationToken(email, password,
                        grantedAuthorities(organization.get()));
            }
        }
        return null;
    }


    private List<GrantedAuthority> grantedAuthorities(OrgEntity organization) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Map the Scope to roles
        switch (organization.getScope()) {
            case ORGANIZATION:
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                break;
            case USER:
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                break;
            default:
                throw new IllegalArgumentException("Unknown scope: " + organization.getScope());
        }

        return authorities;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
