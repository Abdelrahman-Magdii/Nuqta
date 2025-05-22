package com.spring.nuqta.mail.template;

import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

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


    public void buildVerificationUrl(final UserEntity user, final Long donationId, final String baseUrl) {
        put("Name", user.getUsername());
        put("location", user.getDonation().getConservatism() + " " + user.getDonation().getCity());
        put("gender", user.getGender());

        try {
            // Encode sensitive data using Base64 for URL safety
            String encodedName = Base64.getUrlEncoder().encodeToString(
                    user.getUsername().getBytes(StandardCharsets.UTF_8));
            String encodedPhone = Base64.getUrlEncoder().encodeToString(
                    user.getPhoneNumber().getBytes(StandardCharsets.UTF_8));
            String encodedBloodType = Base64.getUrlEncoder().encodeToString(
                    user.getDonation().getBloodType().getBytes(StandardCharsets.UTF_8));

            final String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .path("/api/auth/accept")
                    .queryParam("accept", true)
                    .queryParam("donationId", donationId)
                    .queryParam("name", encodedName)
                    .queryParam("phoneNumber", encodedPhone)
                    .queryParam("bloodType", encodedBloodType)
                    .queryParam("encoded", true)
                    .encode()
                    .build()
                    .toUriString();

            put("verificationURL", url);
        } catch (Exception e) {
            // Fallback to regular encoding if Base64 fails
            final String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .path("/api/auth/accept")
                    .queryParam("accept", true)
                    .queryParam("donationId", donationId)
                    .queryParam("name", user.getUsername())
                    .queryParam("phoneNumber", user.getPhoneNumber())
                    .queryParam("bloodType", user.getDonation().getBloodType())
                    .encode()
                    .build()
                    .toUriString();
            put("verificationURL", url);
        }
    }
}
