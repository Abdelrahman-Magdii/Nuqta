package com.spring.nuqta.verificationToken.Services;

import com.spring.nuqta.donation.Entity.DonEntity;
import com.spring.nuqta.enums.*;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.request.Entity.ReqEntity;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import com.spring.nuqta.verificationToken.Entity.VerificationToken;
import com.spring.nuqta.verificationToken.Repo.VerificationTokenRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationTokenServiceDiffblueTest {

    @InjectMocks
    private VerificationTokenService verificationTokenService;

    @Mock
    private VerificationTokenRepo verificationTokenRepo;

    /**
     * Test {@link VerificationTokenService#createToken()}.
     * <p>
     * Method under test: {@link VerificationTokenService#createToken()}
     */
    @Test
    @DisplayName("Test createToken()")
    @Tag("MaintainedByDiffblue")
    void testCreateToken() {
        // Arrange and Act
        VerificationToken actualCreateTokenResult = verificationTokenService.createToken();

        // Assert
        assertNull(actualCreateTokenResult.getOrganization());
        assertNull(actualCreateTokenResult.getUser());
        assertNull(actualCreateTokenResult.getId());
    }

    /**
     * Test {@link VerificationTokenService#saveToken(VerificationToken)}.
     * <p>
     * Method under test: {@link VerificationTokenService#saveToken(VerificationToken)}
     */
    @Test
    @DisplayName("Test saveToken(VerificationToken)")
    @Tag("MaintainedByDiffblue")
    void testSaveToken() {
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
        when(verificationTokenRepo.save(Mockito.<VerificationToken>any())).thenReturn(verificationToken);

        OrgEntity organization3 = new OrgEntity();
        organization3.setCreatedDate(LocalDate.of(1970, 1, 1));
        organization3.setCreatedUser("Jan 1, 2020 8:00am GMT+0100");
        organization3.setEmail("jane.doe@example.org");
        organization3.setEnabled(true);
        organization3.setFcmToken("ABC123");
        organization3.setId(1L);
        organization3.setLicenseNumber("42");
        CoordinateArraySequence points3 = new CoordinateArraySequence(3);
        organization3.setLocation(new LineString(points3, new GeometryFactory()));
        organization3.setModifiedDate(LocalDate.of(1970, 1, 1));
        organization3.setModifiedUser("Jan 1, 2020 9:00am GMT+0100");
        organization3.setOrgName("Org Name");
        organization3.setPassword("iloveyou");
        organization3.setPhoneNumber("6625550144");
        organization3.setScope(Scope.USER);

        ReqEntity request2 = new ReqEntity();
        request2.setAddress("42 Main St");
        request2.setAmount(10.0d);
        request2.setBloodTypeNeeded("Blood Type Needed");
        request2.setCreatedDate(LocalDate.of(1970, 1, 1));
        request2.setCreatedUser("Jan 1, 2020 8:00am GMT+0100");
        request2.setId(1L);
        request2.setLocation(mock(Geometry.class));
        request2.setModifiedDate(LocalDate.of(1970, 1, 1));
        request2.setModifiedUser("Jan 1, 2020 9:00am GMT+0100");
        request2.setOrganization(new OrgEntity());
        request2.setPaymentAvailable(true);
        request2.setRequestDate(LocalDate.of(1970, 1, 1));
        request2.setStatus(Status.OPEN);
        request2.setUrgencyLevel(Level.LOW);
        request2.setUser(new UserEntity());

        UserEntity user4 = new UserEntity();
        user4.setBirthDate(LocalDate.of(1970, 1, 1));
        user4.setCreatedDate(LocalDate.of(1970, 1, 1));
        user4.setCreatedUser("Jan 1, 2020 8:00am GMT+0100");
        user4.setDonation(new DonEntity());
        user4.setEmail("jane.doe@example.org");
        user4.setEnabled(true);
        user4.setFcmToken("ABC123");
        user4.setGender(Gender.MALE);
        user4.setId(1L);
        user4.setModifiedDate(LocalDate.of(1970, 1, 1));
        user4.setModifiedUser("Jan 1, 2020 9:00am GMT+0100");
        user4.setPassword("iloveyou");
        user4.setPhoneNumber("6625550144");
        user4.setScope(Scope.USER);
        user4.setUsername("janedoe");

        DonEntity donation3 = new DonEntity();
        donation3.setAmount(10.0d);
        donation3.setBloodType("Blood Type");
        donation3.setCreatedDate(LocalDate.of(1970, 1, 1));
        donation3.setCreatedUser("Jan 1, 2020 8:00am GMT+0100");
        donation3.setDonationDate(LocalDate.of(1970, 1, 1));
        donation3.setId(1L);
        donation3.setLastDonation(LocalDate.of(1970, 1, 1));
        donation3.setLocation(new LineString(null, new GeometryFactory()));
        donation3.setModifiedDate(LocalDate.of(1970, 1, 1));
        donation3.setModifiedUser("Jan 1, 2020 9:00am GMT+0100");
        donation3.setPaymentOffered(true);
        donation3.setRequest(request2);
        donation3.setStatus(DonStatus.PENDING);
        donation3.setUser(user4);
        donation3.setWeight(10.0d);

        UserEntity user5 = new UserEntity();
        user5.setBirthDate(LocalDate.of(1970, 1, 1));
        user5.setCreatedDate(LocalDate.of(1970, 1, 1));
        user5.setCreatedUser("Jan 1, 2020 8:00am GMT+0100");
        user5.setDonation(donation3);
        user5.setEmail("jane.doe@example.org");
        user5.setEnabled(true);
        user5.setFcmToken("ABC123");
        user5.setGender(Gender.MALE);
        user5.setId(1L);
        user5.setModifiedDate(LocalDate.of(1970, 1, 1));
        user5.setModifiedUser("Jan 1, 2020 9:00am GMT+0100");
        user5.setPassword("iloveyou");
        user5.setPhoneNumber("6625550144");
        user5.setScope(Scope.USER);
        user5.setUsername("janedoe");

        VerificationToken secureToken = new VerificationToken();
        secureToken.setExpiredAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        secureToken.setId(1L);
        secureToken.setOrganization(organization3);
        secureToken.setToken("ABC123");
        secureToken.setUser(user5);

        // Act
        verificationTokenService.saveToken(secureToken);

        // Assert
        verify(verificationTokenRepo).save(isA(VerificationToken.class));
    }

    /**
     * Test {@link VerificationTokenService#findByToken(String)}.
     * <p>
     * Method under test: {@link VerificationTokenService#findByToken(String)}
     */
    @Test
    @DisplayName("Test findByToken(String)")
    @Tag("MaintainedByDiffblue")
    void testFindByToken() {
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
        when(verificationTokenRepo.findByToken(Mockito.<String>any())).thenReturn(verificationToken);

        // Act
        VerificationToken actualFindByTokenResult = verificationTokenService.findByToken("ABC123");

        // Assert
        verify(verificationTokenRepo).findByToken(eq("ABC123"));
        assertSame(verificationToken, actualFindByTokenResult);
    }

    /**
     * Test {@link VerificationTokenService#removeToken(VerificationToken)}.
     * <p>
     * Method under test: {@link VerificationTokenService#removeToken(VerificationToken)}
     */
    @Test
    @DisplayName("Test removeToken(VerificationToken)")
    @Tag("MaintainedByDiffblue")
    void testRemoveToken() {
        // Arrange
        doNothing().when(verificationTokenRepo).delete(Mockito.<VerificationToken>any());

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

        ReqEntity request = new ReqEntity();
        request.setAddress("42 Main St");
        request.setAmount(10.0d);
        request.setBloodTypeNeeded("Blood Type Needed");
        request.setCreatedDate(LocalDate.of(1970, 1, 1));
        request.setCreatedUser("Jan 1, 2020 8:00am GMT+0100");
        request.setId(1L);
        request.setLocation(mock(Geometry.class));
        request.setModifiedDate(LocalDate.of(1970, 1, 1));
        request.setModifiedUser("Jan 1, 2020 9:00am GMT+0100");
        request.setOrganization(new OrgEntity());
        request.setPaymentAvailable(true);
        request.setRequestDate(LocalDate.of(1970, 1, 1));
        request.setStatus(Status.OPEN);
        request.setUrgencyLevel(Level.LOW);
        request.setUser(new UserEntity());

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

        DonEntity donation = new DonEntity();
        donation.setAmount(10.0d);
        donation.setBloodType("Blood Type");
        donation.setCreatedDate(LocalDate.of(1970, 1, 1));
        donation.setCreatedUser("Jan 1, 2020 8:00am GMT+0100");
        donation.setDonationDate(LocalDate.of(1970, 1, 1));
        donation.setId(1L);
        donation.setLastDonation(LocalDate.of(1970, 1, 1));
        donation.setLocation(new LineString(null, new GeometryFactory()));
        donation.setModifiedDate(LocalDate.of(1970, 1, 1));
        donation.setModifiedUser("Jan 1, 2020 9:00am GMT+0100");
        donation.setPaymentOffered(true);
        donation.setRequest(request);
        donation.setStatus(DonStatus.PENDING);
        donation.setUser(user);
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

        VerificationToken token = new VerificationToken();
        token.setExpiredAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        token.setId(1L);
        token.setOrganization(organization);
        token.setToken("ABC123");
        token.setUser(user2);

        // Act
        verificationTokenService.removeToken(token);

        // Assert
        verify(verificationTokenRepo).delete(isA(VerificationToken.class));
    }
}
