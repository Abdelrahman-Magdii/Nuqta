package com.spring.nuqta.verificationToken.Services;

import com.spring.nuqta.verificationToken.Entity.VerificationToken;
import com.spring.nuqta.verificationToken.Repo.VerificationTokenRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class VerificationTokenService {

    private static BytesKeyGenerator DEFAULT_TOKEN_GENERATOR = KeyGenerators.secureRandom(12);
    private final VerificationTokenRepo verificationTokenRepo;

    @Value("${token.validity.in.seconds}")
    private int tokenValidityInSeconds;

    @Autowired
    public VerificationTokenService(VerificationTokenRepo verificationTokenRepo) {
        this.verificationTokenRepo = verificationTokenRepo;
    }


    public VerificationToken createToken() {
        String tokenValue = new String(Base64.encodeBase64URLSafeString(DEFAULT_TOKEN_GENERATOR.generateKey()));
        VerificationToken token = new VerificationToken();
        token.setToken(tokenValue);
        token.setExpiredAt(LocalDateTime.now().plusSeconds(tokenValidityInSeconds));
        return token;
    }

    public void saveToken(VerificationToken secureToken) {
        verificationTokenRepo.save(secureToken);
    }

    public VerificationToken findByToken(String token) {
        return verificationTokenRepo.findByToken(token);
    }

    public void removeToken(VerificationToken token) {
        verificationTokenRepo.delete(token);
    }
}
