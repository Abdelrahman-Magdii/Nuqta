package com.spring.nuqta.verificationToken.Entity;

import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

@ContextConfiguration(classes = {VerificationToken.class})
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class VerificationTokenTest {
    @Autowired
    private VerificationToken verificationToken;

    /**
     * Test {@link VerificationToken#isExpired()}.
     * <p>
     * Method under test: {@link VerificationToken#isExpired()}
     */
    @Test
    @DisplayName("Test isExpired()")
    @Disabled("TODO: Complete this test")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"boolean com.spring.nuqta.verificationToken.Entity.VerificationToken.isExpired()"})
    void testIsExpired() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Method may be time-sensitive.
        //   Diffblue Cover was only able to write tests that are time-sensitive.
        //   The assertions don't pass when run at an alternate date, time, and
        //   timezone. Try refactoring the method to take a 'java.time.Clock' instance so
        //   that the time can be parameterized during testing.
        //   See Working with code R031 (https://diff.blue/R031) for details.

        // Arrange
        VerificationToken verificationToken2 = new VerificationToken();
        verificationToken2.setExpiredAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        // Act
        verificationToken2.isExpired();
    }

    /**
     * Test {@link VerificationToken#isExpired()}.
     * <ul>
     *   <li>Given {@link VerificationToken} (default constructor).</li>
     * </ul>
     * <p>
     * Method under test: {@link VerificationToken#isExpired()}
     */
    @Test
    @DisplayName("Test isExpired(); given VerificationToken (default constructor)")
    @Disabled("TODO: Complete this test")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"boolean com.spring.nuqta.verificationToken.Entity.VerificationToken.isExpired()"})
    void testIsExpired_givenVerificationToken() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "java.time.LocalDateTime.isBefore(java.time.chrono.ChronoLocalDateTime)" because the return value of "com.spring.nuqta.verificationToken.Entity.VerificationToken.getExpiredAt()" is null
        //       at com.spring.nuqta.verificationToken.Entity.VerificationToken.isExpired(VerificationToken.java:37)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange and Act
        (new VerificationToken()).isExpired();
    }
}
