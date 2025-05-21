package com.spring.nuqta.mail.template;

import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.usermanagement.Entity.UserEntity;

public class SendMailToDoner extends AbstractEmailContext {

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
        setTemplateLocation("requestAccepted");
        setSubject("Request Accepted ✅");
        setFrom("Nuqta.help@gmail.com");
    }


    public void buildVerificationUrl(final UserEntity user) {
        put("Name", user.getUsername());
        put("blood", user.getDonation().getBloodType());
        put("location", user.getDonation().getConservatism() + " " + user.getDonation().getCity());
        put("gender", user.getGender());
        put("phoneNumber", user.getPhoneNumber());
        put("completionDate", user.getModifiedDate());
    }
}
