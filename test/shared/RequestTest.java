package shared;

import java.io.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class RequestTest {

    //  Construction 

    @Test
    public void testTwoArgConstructorSetsOperationAndData() {

        Request request = new Request("LOGIN", "somePayload");

        assertEquals("LOGIN", request.getOperation());
        assertEquals("somePayload", request.getData());
    }

    @Test
    public void testThreeArgConstructorAlsoSetsUsername() {

        Request request = new Request("BOOK_APPOINTMENT", 5, "patient");

        assertEquals("BOOK_APPOINTMENT", request.getOperation());
        assertEquals(5, request.getData());
        assertEquals("patient", request.getUsername());
    }

    @Test
    public void testTwoArgConstructorLeavesUsernameNull() {

        // Confirms the RMI dispatcher (ClinicRemoteImpl) has no username to
        // authorize against unless the 3-arg constructor is used.
        Request request = new Request("TEST_CONNECTION", null);

        assertNull(request.getUsername());
    }

    //  Setters 

    @Test
    public void testSettersUpdateValues() {

        Request request = new Request();
        request.setOperation("CANCEL_APPOINTMENT");
        request.setData(42);
        request.setUsername("doctor");

        assertEquals("CANCEL_APPOINTMENT", request.getOperation());
        assertEquals(42, request.getData());
        assertEquals("doctor", request.getUsername());
    }

    //  Serialization 
    // RMI sends Request objects over the network by serializing them to
    // bytes on the client and reconstructing them on the server. If this
    // round-trip fails, no request could ever reach ClinicRemoteImpl.

    @Test
    public void testRequestSurvivesSerializationRoundTrip() throws Exception {

        Request original = new Request("UPDATE_DIAGNOSIS", "some notes", "doctor");

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteStream);
        out.writeObject(original);
        out.flush();

        ByteArrayInputStream inStream = new ByteArrayInputStream(byteStream.toByteArray());
        ObjectInputStream in = new ObjectInputStream(inStream);
        Request reconstructed = (Request) in.readObject();

        assertEquals(original.getOperation(), reconstructed.getOperation());
        assertEquals(original.getData(), reconstructed.getData());
        assertEquals(original.getUsername(), reconstructed.getUsername());
    }
}