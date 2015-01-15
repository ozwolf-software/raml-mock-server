package net.ozwolf.mockserver.raml;

import org.junit.Test;

import static org.junit.Assert.*;

public class ResponseObeyModeTest {
    @Test
    public void shouldBlockAnyAttemptedResponseCode(){
        assertFalse(ResponseObeyMode.STRICT.isStatusCodeAllowed(200));
        assertFalse(ResponseObeyMode.STRICT.isStatusCodeAllowed(201));
        assertFalse(ResponseObeyMode.STRICT.isStatusCodeAllowed(401));
        assertFalse(ResponseObeyMode.STRICT.isStatusCodeAllowed(403));
        assertFalse(ResponseObeyMode.STRICT.isStatusCodeAllowed(404));
        assertFalse(ResponseObeyMode.STRICT.isStatusCodeAllowed(405));
        assertFalse(ResponseObeyMode.STRICT.isStatusCodeAllowed(415));
        assertFalse(ResponseObeyMode.STRICT.isStatusCodeAllowed(500));
        assertFalse(ResponseObeyMode.STRICT.isStatusCodeAllowed(503));
    }

    @Test
    public void shouldOnlyBlockOkResponsesAndDefinableErrors(){
        assertFalse(ResponseObeyMode.SAFE_ERRORS.isStatusCodeAllowed(200));
        assertFalse(ResponseObeyMode.SAFE_ERRORS.isStatusCodeAllowed(201));
        assertFalse(ResponseObeyMode.SAFE_ERRORS.isStatusCodeAllowed(401));
        assertFalse(ResponseObeyMode.SAFE_ERRORS.isStatusCodeAllowed(403));
        assertTrue(ResponseObeyMode.SAFE_ERRORS.isStatusCodeAllowed(404));
        assertTrue(ResponseObeyMode.SAFE_ERRORS.isStatusCodeAllowed(405));
        assertTrue(ResponseObeyMode.SAFE_ERRORS.isStatusCodeAllowed(415));
        assertTrue(ResponseObeyMode.SAFE_ERRORS.isStatusCodeAllowed(500));
        assertTrue(ResponseObeyMode.SAFE_ERRORS.isStatusCodeAllowed(503));
    }

    @Test
    public void shouldOnlyBlockOkResponsesAndAllowAllErrors(){
        assertFalse(ResponseObeyMode.ALL_ERRORS.isStatusCodeAllowed(200));
        assertFalse(ResponseObeyMode.ALL_ERRORS.isStatusCodeAllowed(201));
        assertTrue(ResponseObeyMode.ALL_ERRORS.isStatusCodeAllowed(401));
        assertTrue(ResponseObeyMode.ALL_ERRORS.isStatusCodeAllowed(403));
        assertTrue(ResponseObeyMode.ALL_ERRORS.isStatusCodeAllowed(404));
        assertTrue(ResponseObeyMode.ALL_ERRORS.isStatusCodeAllowed(405));
        assertTrue(ResponseObeyMode.ALL_ERRORS.isStatusCodeAllowed(415));
        assertTrue(ResponseObeyMode.ALL_ERRORS.isStatusCodeAllowed(500));
        assertTrue(ResponseObeyMode.ALL_ERRORS.isStatusCodeAllowed(503));
    }
}