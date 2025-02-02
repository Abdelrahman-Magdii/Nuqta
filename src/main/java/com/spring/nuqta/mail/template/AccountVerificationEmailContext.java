package com.spring.nuqta.mail.template;

import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import org.springframework.web.util.UriComponentsBuilder;

public class AccountVerificationEmailContext extends AbstractEmailContext {

    private String token;

    @Override
    public <T> void init(T context) {
        T entity = (T) context;
        if (entity instanceof UserEntity user) {
            put("User", user.getUsername());
            setTo(user.getEmail());
        } else if (entity instanceof OrgEntity org) {
            put("User", org.getOrgName());
            setTo(org.getEmail());
        }
        setTemplateLocation("mail");
        setSubject("Complete your registration");
        setFrom("no-reply@Nuqta.com");

    }

    public void setToken(String token) {
        this.token = token;
        put("token", token);
    }

    public void buildVerificationUrl(final String baseURL, final String token, final String email) {
        final String url = UriComponentsBuilder.fromHttpUrl(baseURL)
                .path("api/auth/verify").queryParam("token", token).queryParam("mail", email).toUriString();
        put("verificationURL", url);
    }
}
