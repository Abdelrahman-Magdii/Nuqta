package com.spring.nuqta.mail.template;

import com.spring.nuqta.usermanagement.Entity.UserEntity;

public class SendMailToDonerRejected extends AbstractEmailContext {

    @Override
    public <T> void init(T context) {
        T entity = (T) context;
        if (entity instanceof UserEntity user) {
            put("User", user.getUsername());
            setTo(user.getEmail());
        }
        setTemplateLocation("requestRejected");
        setSubject("Request Rejected ❌");
        setFrom("Nuqta.help@gmail.com");
    }


    public void buildVerificationUrl(final UserEntity user) {
        put("Name", user.getUsername());
    }
}
