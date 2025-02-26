package com.spring.nuqta.notifications.Config;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FirebaseConfigTest {

    private FirebaseConfig firebaseConfig;

    @BeforeEach
    void setUp() {
        firebaseConfig = new FirebaseConfig();
        FirebaseApp.getApps().forEach(FirebaseApp::delete);
    }

    /**
     * ✅ Test Case 1: `initFirebase()` Throws Exception When File is Missing
     **/

    @Test
    void testInitFirebase_ThrowsException_WhenFileNotFound() throws IOException {
        // Create a spy instance of FirebaseConfig
        FirebaseConfig firebaseConfig = Mockito.spy(new FirebaseConfig());

        // Mock `loadFirebaseServiceAccount` to return null (simulate missing file)
        doReturn(null).when(firebaseConfig).loadFirebaseServiceAccount();

        // Assert that IOException is thrown when file is missing
        assertThrows(IOException.class, firebaseConfig::initFirebase);
    }

    /**
     * ✅ Test Case 2: `initFirebase()` Initializes Firebase Successfully
     **/
    @Test
    void testInitFirebase_SuccessfulInitialization(@TempDir Path tempDir) throws IOException {
        // Create a fake Firebase service account JSON file
        File serviceAccountFile = tempDir.resolve("firebase-service-account.json").toFile();
        try (FileWriter writer = new FileWriter(serviceAccountFile)) {
            writer.write("{\"type\": \"service_account\"}");
        }

        // Mock FirebaseApp.getApps() to return an empty list before initialization
        try (MockedStatic<FirebaseApp> mockedFirebase = Mockito.mockStatic(FirebaseApp.class)) {
            mockedFirebase.when(FirebaseApp::getApps).thenReturn(Collections.emptyList());

            FirebaseApp mockApp = mock(FirebaseApp.class);
            mockedFirebase.when(() -> FirebaseApp.initializeApp(any(FirebaseOptions.class))).thenReturn(mockApp);

            // Call the actual method
            firebaseConfig.initFirebase();

            // Verify Firebase initialized
            mockedFirebase.verify(() -> FirebaseApp.initializeApp(any(FirebaseOptions.class)), times(1));
        }
    }


    /**
     * ✅ Test Case 3: `firebaseApp()` Initializes Firebase When No Existing Apps
     **/
    @Test
    void testFirebaseApp_WhenNoExistingApps() throws IOException {
        try (MockedStatic<FirebaseApp> mockedFirebase = Mockito.mockStatic(FirebaseApp.class)) {
            mockedFirebase.when(FirebaseApp::getApps).thenReturn(Collections.emptyList());

            FirebaseApp mockApp = mock(FirebaseApp.class);
            mockedFirebase.when(() -> FirebaseApp.initializeApp(any(FirebaseOptions.class))).thenReturn(mockApp);

            FirebaseApp result = firebaseConfig.firebaseApp();
            assertNotNull(result);
        }
    }

    /**
     * ✅ Test Case 4: `firebaseApp()` Returns Existing FirebaseApp If Initialized
     **/
    @Test
    void testFirebaseApp_WhenExistingApps() throws IOException {
        FirebaseApp mockApp = mock(FirebaseApp.class);

        try (MockedStatic<FirebaseApp> mockedFirebase = Mockito.mockStatic(FirebaseApp.class)) {
            mockedFirebase.when(FirebaseApp::getApps).thenReturn(Collections.singletonList(mockApp));
            mockedFirebase.when(FirebaseApp::getInstance).thenReturn(mockApp);

            FirebaseApp result = firebaseConfig.firebaseApp();
            assertEquals(mockApp, result);
        }
    }

    /**
     * ✅ Test Case 5: `firebaseMessaging()` Returns FirebaseMessaging Instance
     **/
    @Test
    void testFirebaseMessaging() {
        FirebaseApp mockApp = mock(FirebaseApp.class);
        FirebaseMessaging result = firebaseConfig.firebaseMessaging(mockApp);
        assertNotNull(result);
    }
}
