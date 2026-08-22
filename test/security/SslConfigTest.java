package security;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class SslConfigTest {

    // Run each test with a clean slate - a previous test (or a previous
    // JVM run) should never leave stale system properties behind that
    // make a broken configureServer()/configureClient() look like it worked.
    @Before
    public void clearSslSystemProperties() {
        System.clearProperty("javax.net.ssl.keyStore");
        System.clearProperty("javax.net.ssl.keyStorePassword");
        System.clearProperty("javax.net.ssl.trustStore");
        System.clearProperty("javax.net.ssl.trustStorePassword");
    }

    // Server-side configuration 

    @Test
    public void testConfigureServerSetsKeystoreSystemProperty() {

        SslConfig.configureServer();

        assertEquals("certs/server.keystore",
                System.getProperty("javax.net.ssl.keyStore"));
    }

    @Test
    public void testConfigureServerSetsKeystorePassword() {

        SslConfig.configureServer();

        assertNotNull(System.getProperty("javax.net.ssl.keyStorePassword"));
        assertFalse(System.getProperty("javax.net.ssl.keyStorePassword").isEmpty());
    }

    // Client-side configuration

    @Test
    public void testConfigureClientSetsTruststoreSystemProperty() {

        SslConfig.configureClient();

        assertEquals("certs/client.truststore",
                System.getProperty("javax.net.ssl.trustStore"));
    }

    @Test
    public void testConfigureClientSetsTruststorePassword() {

        SslConfig.configureClient();

        assertNotNull(System.getProperty("javax.net.ssl.trustStorePassword"));
        assertFalse(System.getProperty("javax.net.ssl.trustStorePassword").isEmpty());
    }
}