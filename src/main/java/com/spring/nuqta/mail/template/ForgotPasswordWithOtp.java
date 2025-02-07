package com.spring.nuqta.mail.template;

import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.usermanagement.Entity.UserEntity;

public class ForgotPasswordWithOtp extends AbstractEmailContext {

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
        setTemplateLocation("mailForgotPassword");
        setSubject("Password Reset");
        setFrom("no-reply@Nuqta.com");

    }

    public void buildVerificationOtp(final String otp) {
        put("otp", otp);
    }
}
