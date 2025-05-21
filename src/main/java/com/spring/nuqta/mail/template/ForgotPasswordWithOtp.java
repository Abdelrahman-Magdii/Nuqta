package com.spring.nuqta.mail.template;

import com.spring.nuqta.organization.Projection.OrgAuthProjection;
import com.spring.nuqta.usermanagement.Projection.UserAuthProjection;

public class ForgotPasswordWithOtp extends AbstractEmailContext {

    @Override
    public <T> void init(T context) {
        T entity = (T) context;
        if (entity instanceof UserAuthProjection user) {
            put("User", user.username());
            setTo(user.email());
        } else if (entity instanceof OrgAuthProjection org) {
            put("User", org.orgName());
            setTo(org.email());
        }
        setTemplateLocation("mailForgotPassword");
        setSubject("Password Reset");
        setFrom("Nuqta.help@gmail.com");

    }

    public void buildVerificationOtp(final String otp) {
        put("otp", otp);
    }
}
