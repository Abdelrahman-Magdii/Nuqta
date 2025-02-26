package com.spring.nuqta.verificationToken.Services;

import com.spring.nuqta.verificationToken.Entity.VerificationToken;
import com.spring.nuqta.verificationToken.Repo.VerificationTokenRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationTokenServiceTest {

    @Mock
    private VerificationTokenRepo verificationTokenRepo;

    @InjectMocks
    private VerificationTokenService verificationTokenService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(verificationTokenService, "tokenValidityInSeconds", 3600);
    }

    @Test
    void createToken_ShouldGenerateValidToken() {
        VerificationToken token = verificationTokenService.createToken();
        assertNotNull(token);
        assertNotNull(token.getToken());
        assertNotNull(token.getExpiredAt());
        assertTrue(token.getExpiredAt().isAfter(LocalDateTime.now()));
    }

    @Test
    void saveToken_ShouldCallRepoSave() {
        VerificationToken token = new VerificationToken();
        verificationTokenService.saveToken(token);
        verify(verificationTokenRepo, times(1)).save(token);
    }

    @Test
    void findByToken_ShouldReturnToken_WhenExists() {
        String tokenValue = "testToken";
        VerificationToken expectedToken = new VerificationToken();
        expectedToken.setToken(tokenValue);
        when(verificationTokenRepo.findByToken(tokenValue)).thenReturn(expectedToken);

        VerificationToken actualToken = verificationTokenService.findByToken(tokenValue);
        assertNotNull(actualToken);
        assertEquals(tokenValue, actualToken.getToken());
    }

    @Test
    void findByToken_ShouldReturnNull_WhenTokenDoesNotExist() {
        String tokenValue = "nonExistentToken";
        when(verificationTokenRepo.findByToken(tokenValue)).thenReturn(null);

        VerificationToken actualToken = verificationTokenService.findByToken(tokenValue);
        assertNull(actualToken);
    }

    @Test
    void removeToken_ShouldCallRepoDelete() {
        VerificationToken token = new VerificationToken();
        verificationTokenService.removeToken(token);
        verify(verificationTokenRepo, times(1)).delete(token);
    }
}
