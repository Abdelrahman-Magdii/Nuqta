package com.spring.nuqta.mail.template;

import com.spring.nuqta.usermanagement.Entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriComponentsBuilder;

public class SendMailToDoner extends AbstractEmailContext {

    @Value("${token.base.url}")
    private String baseUrl;

    @Override
    public <T> void init(T context) {
        T entity = (T) context;
        if (entity instanceof UserEntity user) {
            put("User", user.getUsername());
            setTo(user.getEmail());
        }
        setTemplateLocation("requestAccepted");
        setSubject("Request Accepted ✅");
        setFrom("Nuqta.help@gmail.com");
    }


    public void buildVerificationUrl(final UserEntity user, final Long donationId) {
        put("Name", user.getUsername());
        put("blood", user.getDonation().getBloodType());
        put("location", user.getDonation().getConservatism() + " " + user.getDonation().getCity());
        put("gender", user.getGender());
        put("phoneNumber", user.getPhoneNumber());
        put("completionDate", user.getModifiedDate());

        final String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("api/auth/accept")
                .queryParam("accept", true)
                .queryParam("donationId", donationId)
                .toUriString();

        put("verificationURL", url);
    }
}
