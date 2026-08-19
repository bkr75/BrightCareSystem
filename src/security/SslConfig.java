package security;

import java.io.File;

public final class SslConfig {

    private static final String KEYSTORE_PATH = "certs/server.keystore";
    private static final String TRUSTSTORE_PATH = "certs/client.truststore";
    private static final String STORE_PASSWORD = "brightcare123";

    private SslConfig() {
    }

    public static void configureServer() {

        File keystoreFile = new File(KEYSTORE_PATH);

        if (!keystoreFile.exists()) {
            throw new IllegalStateException(
                    "SSL keystore not found at '" + keystoreFile.getAbsolutePath() + "'.");
        }

        System.setProperty("javax.net.ssl.keyStore", KEYSTORE_PATH);
        System.setProperty("javax.net.ssl.keyStorePassword", STORE_PASSWORD);
    }

    public static void configureClient() {

        File truststoreFile = new File(TRUSTSTORE_PATH);

        if (!truststoreFile.exists()) {
            throw new IllegalStateException(
                    "SSL truststore not found at '" + truststoreFile.getAbsolutePath() + "'.");
        }

        System.setProperty("javax.net.ssl.trustStore", TRUSTSTORE_PATH);
        System.setProperty("javax.net.ssl.trustStorePassword", STORE_PASSWORD);
    }
}
