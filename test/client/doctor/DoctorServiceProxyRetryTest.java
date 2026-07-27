package client.doctor;

import org.junit.Test;
import shared.Operation;
import shared.Request;
import shared.Response;

import static org.junit.Assert.*;

public class DoctorServiceProxyRetryTest {

    @Test
    public void testSend_retriesWithBackoffThenFails() {

        // Nothing listens on this port, so every attempt is guaranteed to fail.
        DoctorServiceProxy proxy = new DoctorServiceProxy("localhost", 19999);

        long start = System.currentTimeMillis();
        Response response = proxy.send(new Request(Operation.TEST_CONNECTION, null));
        long elapsed = System.currentTimeMillis() - start;

        assertFalse(response.isSuccess());
        assertTrue("Message should report the number of attempts made",
                response.getMessage().contains("3 attempts"));

        // 2 retries with 1s then 2s backoff = at least ~3s before giving up.
        assertTrue("Expected at least ~3s of backoff delay, took " + elapsed + "ms",
                elapsed >= 3000);
    }

    @Test
    public void testSend_retryListenerFiresOnEachRetry() {

        DoctorServiceProxy proxy = new DoctorServiceProxy("localhost", 19998);

        int[] retryCount = {0};
        proxy.setRetryListener((attempt, max, backoff) -> retryCount[0]++);

        proxy.send(new Request(Operation.TEST_CONNECTION, null));

        // 3 total attempts means exactly 2 retries happen before giving up.
        assertEquals(2, retryCount[0]);
    }
}
