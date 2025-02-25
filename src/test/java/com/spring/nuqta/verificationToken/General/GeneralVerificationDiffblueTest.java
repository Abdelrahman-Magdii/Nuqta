package com.spring.nuqta.verificationToken.General;

import com.diffblue.cover.annotations.MethodsUnderTest;
import com.spring.nuqta.donation.Entity.DonEntity;
import com.spring.nuqta.enums.*;
import com.spring.nuqta.mail.Services.EmailService;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.organization.Repo.OrgRepo;
import com.spring.nuqta.request.Entity.ReqEntity;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import com.spring.nuqta.usermanagement.Repo.UserRepo;
import com.spring.nuqta.verificationToken.Entity.VerificationToken;
import com.spring.nuqta.verificationToken.Services.VerificationTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {GeneralVerification.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class GeneralVerificationDiffblueTest {

    @Autowired
    private GeneralVerification generalVerification;

    @Mock
    private VerificationTokenService verificationTokenService;

    @Mock
    private EmailService emailService;

    @Mock
    private OrgRepo orgRepo;

    @Mock
    private UserRepo userRepo;

    /**
     * Test {@link GeneralVerification#sendOtpEmail(Object)}.
     * <p>
     * Method under test: {@link GeneralVerification#sendOtpEmail(Object)}
     */
    @Test
    @DisplayName("Test sendOtpEmail(Object)")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({
            "void com.spring.nuqta.verificationToken.General.GeneralVerification.sendOtpEmail(java.lang.Object)"})
    void testSendOtpEmail() {
        // Arrange
        OrgEntity organization = new OrgEntity();
        organization.setCreatedDate(LocalDate.of(1970, 1, 1));
        organization.setCreatedUser("Jan 1, 2020 8:00am GMT+0100");
        organization.setEmail("jane.doe@example.org");
        organization.setEnabled(true);
        organization.setFcmToken("ABC123");
        organization.setId(1L);
        organization.setLicenseNumber("42");
        CoordinateArraySequence points = new CoordinateArraySequence(3);
        organization.setLocation(new LineString(points, new GeometryFactory()));
        organization.setModifiedDate(LocalDate.of(1970, 1, 1));
        organization.setModifiedUser("Jan 1, 2020 9:00am GMT+0100");
        organization.setOrgName("Org Name");
        organization.setPassword("iloveyou");
        organization.setPhoneNumber("6625550144");
        organization.setScope(Scope.USER);

        OrgEntity organization2 = new OrgEntity();
        organization2.setCreatedDate(LocalDate.of(1970, 1, 1));
        organization2.setCreatedUser("Jan 1, 2020 8:00am GMT+0100");
        organization2.setEmail("jane.doe@example.org");
        organization2.setEnabled(true);
        organization2.setFcmToken("ABC123");
        organization2.setId(1L);
        organization2.setLicenseNumber("42");
        organization2.setLocation(mock(Geometry.class));
        organization2.setModifiedDate(LocalDate.of(1970, 1, 1));
        organization2.setModifiedUser("Jan 1, 2020 9:00am GMT+0100");
        organization2.setOrgName("Org Name");
        organization2.setPassword("iloveyou");
        organization2.setPhoneNumber("6625550144");
        organization2.setScope(Scope.USER);

        UserEntity user = new UserEntity();
        user.setBirthDate(LocalDate.of(1970, 1, 1));
        user.setCreatedDate(LocalDate.of(1970, 1, 1));
        user.setCreatedUser("Jan 1, 2020 8:00am GMT+0100");
        user.setDonation(new DonEntity());
        user.setEmail("jane.doe@example.org");
        user.setEnabled(true);
        user.setFcmToken("ABC123");
        user.setGender(Gender.MALE);
        user.setId(1L);
        user.setModifiedDate(LocalDate.of(1970, 1, 1));
        user.setModifiedUser("Jan 1, 2020 9:00am GMT+0100");
        user.setPassword("iloveyou");
        user.setPhoneNumber("6625550144");
        user.setScope(Scope.USER);
        user.setUsername("janedoe");

        ReqEntity request = new ReqEntity();
        request.setAddress("42 Main St");
        request.setAmount(10.0d);
        request.setBloodTypeNeeded("Blood Type Needed");
        request.setCreatedDate(LocalDate.of(1970, 1, 1));
        request.setCreatedUser("Jan 1, 2020 8:00am GMT+0100");
        request.setId(1L);
        request.setLocation(new LineString(null, new GeometryFactory()));
        request.setModifiedDate(LocalDate.of(1970, 1, 1));
        request.setModifiedUser("Jan 1, 2020 9:00am GMT+0100");
        request.setOrganization(organization2);
        request.setPaymentAvailable(true);
        request.setRequestDate(LocalDate.of(1970, 1, 1));
        request.setStatus(Status.OPEN);
        request.setUrgencyLevel(Level.LOW);
        request.setUser(user);

        DonEntity donation = new DonEntity();
        donation.setAmount(10.0d);
        donation.setBloodType("Blood Type");
        donation.setCreatedDate(LocalDate.of(1970, 1, 1));
        donation.setCreatedUser("Jan 1, 2020 8:00am GMT+0100");
        donation.setDonationDate(LocalDate.of(1970, 1, 1));
        donation.setId(1L);
        donation.setLastDonation(LocalDate.of(1970, 1, 1));
        donation.setLocation(mock(Geometry.class));
        donation.setModifiedDate(LocalDate.of(1970, 1, 1));
        donation.setModifiedUser("Jan 1, 2020 9:00am GMT+0100");
        donation.setPaymentOffered(true);
        donation.setRequest(new ReqEntity());
        donation.setStatus(DonStatus.PENDING);
        donation.setUser(new UserEntity());
        donation.setWeight(10.0d);

        UserEntity user2 = new UserEntity();
        user2.setBirthDate(LocalDate.of(1970, 1, 1));
        user2.setCreatedDate(LocalDate.of(1970, 1, 1));
        user2.setCreatedUser("Jan 1, 2020 8:00am GMT+0100");
        user2.setDonation(donation);
        user2.setEmail("jane.doe@example.org");
        user2.setEnabled(true);
        user2.setFcmToken("ABC123");
        user2.setGender(Gender.MALE);
        user2.setId(1L);
        user2.setModifiedDate(LocalDate.of(1970, 1, 1));
        user2.setModifiedUser("Jan 1, 2020 9:00am GMT+0100");
        user2.setPassword("iloveyou");
        user2.setPhoneNumber("6625550144");
        user2.setScope(Scope.USER);
        user2.setUsername("janedoe");

        DonEntity donation2 = new DonEntity();
        donation2.setAmount(10.0d);
        donation2.setBloodType("Blood Type");
        donation2.setCreatedDate(LocalDate.of(1970, 1, 1));
        donation2.setCreatedUser("Jan 1, 2020 8:00am GMT+0100");
        donation2.setDonationDate(LocalDate.of(1970, 1, 1));
        donation2.setId(1L);
        donation2.setLastDonation(LocalDate.of(1970, 1, 1));
        CoordinateArraySequence points2 = new CoordinateArraySequence(3);
        donation2.setLocation(new LineString(points2, new GeometryFactory()));
        donation2.setModifiedDate(LocalDate.of(1970, 1, 1));
        donation2.setModifiedUser("Jan 1, 2020 9:00am GMT+0100");
        donation2.setPaymentOffered(true);
        donation2.setRequest(request);
        donation2.setStatus(DonStatus.PENDING);
        donation2.setUser(user2);
        donation2.setWeight(10.0d);

        UserEntity user3 = new UserEntity();
        user3.setBirthDate(LocalDate.of(1970, 1, 1));
        user3.setCreatedDate(LocalDate.of(1970, 1, 1));
        user3.setCreatedUser("Jan 1, 2020 8:00am GMT+0100");
        user3.setDonation(donation2);
        user3.setEmail("jane.doe@example.org");
        user3.setEnabled(true);
        user3.setFcmToken("ABC123");
        user3.setGender(Gender.MALE);
        user3.setId(1L);
        user3.setModifiedDate(LocalDate.of(1970, 1, 1));
        user3.setModifiedUser("Jan 1, 2020 9:00am GMT+0100");
        user3.setPassword("iloveyou");
        user3.setPhoneNumber("6625550144");
        user3.setScope(Scope.USER);
        user3.setUsername("janedoe");

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setExpiredAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        verificationToken.setId(1L);
        verificationToken.setOrganization(organization);
        verificationToken.setToken("ABC123");
        verificationToken.setUser(user3);
        when(verificationTokenService.createToken()).thenReturn(verificationToken);

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> generalVerification.sendOtpEmail("Entity"));
        verify(verificationTokenService).createToken();
    }

    /**
     * Test {@link GeneralVerification#verifyRegistration(String, String)}.
     * <p>
     * Method under test: {@link GeneralVerification#verifyRegistration(String, String)}
     */
    @Test
    @DisplayName("Test verifyRegistration(String, String)")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({
            "boolean com.spring.nuqta.verificationToken.General.GeneralVerification.verifyRegistration(java.lang.String, java.lang.String)"})
    void testVerifyRegistration() {
        // Arrange
        OrgEntity organization = new OrgEntity();
        organization.setCreatedDate(LocalDate.of(1970, 1, 1));
        organization.setCreatedUser("Jan 1, 2020 8:00am GMT+0100");
        organization.setEmail("jane.doe@example.org");
        organization.setEnabled(true);
        organization.setFcmToken("ABC123");
        organization.setId(1L);
        organization.setLicenseNumber("42");
        CoordinateArraySequence points = new CoordinateArraySequence(3);
        organization.setLocation(new LineString(points, new GeometryFactory()));
        organization.setModifiedDate(LocalDate.of(1970, 1, 1));
        organization.setModifiedUser("Jan 1, 2020 9:00am GMT+0100");
        organization.setOrgName("Org Name");
        organization.setPassword("iloveyou");
        organization.setPhoneNumber("6625550144");
        organization.setScope(Scope.USER);

        OrgEntity organization2 = new OrgEntity();
        organization2.setCreatedDate(LocalDate.of(1970, 1, 1));
        organization2.setCreatedUser("Jan 1, 2020 8:00am GMT+0100");
        organization2.setEmail("jane.doe@example.org");
        organization2.setEnabled(true);
        organization2.setFcmToken("ABC123");
        organization2.setId(1L);
        organization2.setLicenseNumber("42");
        organization2.setLocation(mock(Geometry.class));
        organization2.setModifiedDate(LocalDate.of(1970, 1, 1));
        organization2.setModifiedUser("Jan 1, 2020 9:00am GMT+0100");
        organization2.setOrgName("Org Name");
        organization2.setPassword("iloveyou");
        organization2.setPhoneNumber("6625550144");
        organization2.setScope(Scope.USER);

        UserEntity user = new UserEntity();
        user.setBirthDate(LocalDate.of(1970, 1, 1));
        user.setCreatedDate(LocalDate.of(1970, 1, 1));
        user.setCreatedUser("Jan 1, 2020 8:00am GMT+0100");
        user.setDonation(new DonEntity());
        user.setEmail("jane.doe@example.org");
        user.setEnabled(true);
        user.setFcmToken("ABC123");
        user.setGender(Gender.MALE);
        user.setId(1L);
        user.setModifiedDate(LocalDate.of(1970, 1, 1));
        user.setModifiedUser("Jan 1, 2020 9:00am GMT+0100");
        user.setPassword("iloveyou");
        user.setPhoneNumber("6625550144");
        user.setScope(Scope.USER);
        user.setUsername("janedoe");

        ReqEntity request = new ReqEntity();
        request.setAddress("42 Main St");
        request.setAmount(10.0d);
        request.setBloodTypeNeeded("Blood Type Needed");
        request.setCreatedDate(LocalDate.of(1970, 1, 1));
        request.setCreatedUser("Jan 1, 2020 8:00am GMT+0100");
        request.setId(1L);
        request.setLocation(new LineString(null, new GeometryFactory()));
        request.setModifiedDate(LocalDate.of(1970, 1, 1));
        request.setModifiedUser("Jan 1, 2020 9:00am GMT+0100");
        request.setOrganization(organization2);
        request.setPaymentAvailable(true);
        request.setRequestDate(LocalDate.of(1970, 1, 1));
        request.setStatus(Status.OPEN);
        request.setUrgencyLevel(Level.LOW);
        request.setUser(user);

        DonEntity donation = new DonEntity();
        donation.setAmount(10.0d);
        donation.setBloodType("Blood Type");
        donation.setCreatedDate(LocalDate.of(1970, 1, 1));
        donation.setCreatedUser("Jan 1, 2020 8:00am GMT+0100");
        donation.setDonationDate(LocalDate.of(1970, 1, 1));
        donation.setId(1L);
        donation.setLastDonation(LocalDate.of(1970, 1, 1));
        donation.setLocation(mock(Geometry.class));
        donation.setModifiedDate(LocalDate.of(1970, 1, 1));
        donation.setModifiedUser("Jan 1, 2020 9:00am GMT+0100");
        donation.setPaymentOffered(true);
        donation.setRequest(new ReqEntity());
        donation.setStatus(DonStatus.PENDING);
        donation.setUser(new UserEntity());
        donation.setWeight(10.0d);

        UserEntity user2 = new UserEntity();
        user2.setBirthDate(LocalDate.of(1970, 1, 1));
        user2.setCreatedDate(LocalDate.of(1970, 1, 1));
        user2.setCreatedUser("Jan 1, 2020 8:00am GMT+0100");
        user2.setDonation(donation);
        user2.setEmail("jane.doe@example.org");
        user2.setEnabled(true);
        user2.setFcmToken("ABC123");
        user2.setGender(Gender.MALE);
        user2.setId(1L);
        user2.setModifiedDate(LocalDate.of(1970, 1, 1));
        user2.setModifiedUser("Jan 1, 2020 9:00am GMT+0100");
        user2.setPassword("iloveyou");
        user2.setPhoneNumber("6625550144");
        user2.setScope(Scope.USER);
        user2.setUsername("janedoe");

        DonEntity donation2 = new DonEntity();
        donation2.setAmount(10.0d);
        donation2.setBloodType("Blood Type");
        donation2.setCreatedDate(LocalDate.of(1970, 1, 1));
        donation2.setCreatedUser("Jan 1, 2020 8:00am GMT+0100");
        donation2.setDonationDate(LocalDate.of(1970, 1, 1));
        donation2.setId(1L);
        donation2.setLastDonation(LocalDate.of(1970, 1, 1));
        CoordinateArraySequence points2 = new CoordinateArraySequence(3);
        donation2.setLocation(new LineString(points2, new GeometryFactory()));
        donation2.setModifiedDate(LocalDate.of(1970, 1, 1));
        donation2.setModifiedUser("Jan 1, 2020 9:00am GMT+0100");
        donation2.setPaymentOffered(true);
        donation2.setRequest(request);
        donation2.setStatus(DonStatus.PENDING);
        donation2.setUser(user2);
        donation2.setWeight(10.0d);

        UserEntity user3 = new UserEntity();
        user3.setBirthDate(LocalDate.of(1970, 1, 1));
        user3.setCreatedDate(LocalDate.of(1970, 1, 1));
        user3.setCreatedUser("Jan 1, 2020 8:00am GMT+0100");
        user3.setDonation(donation2);
        user3.setEmail("jane.doe@example.org");
        user3.setEnabled(true);
        user3.setFcmToken("ABC123");
        user3.setGender(Gender.MALE);
        user3.setId(1L);
        user3.setModifiedDate(LocalDate.of(1970, 1, 1));
        user3.setModifiedUser("Jan 1, 2020 9:00am GMT+0100");
        user3.setPassword("iloveyou");
        user3.setPhoneNumber("6625550144");
        user3.setScope(Scope.USER);
        user3.setUsername("janedoe");

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setExpiredAt(LocalDate.of(2025, 6, 1).atStartOfDay());
        verificationToken.setId(1L);
        verificationToken.setOrganization(organization);
        verificationToken.setToken("ABC123");
        verificationToken.setUser(user3);
        when(verificationTokenService.findByToken(Mockito.<String>any())).thenReturn(verificationToken);

        // Act
        generalVerification.verifyRegistration("ABC123", "Mail");

        // Assert
        verify(verificationTokenService).findByToken(eq("ABC123"));
    }
}
