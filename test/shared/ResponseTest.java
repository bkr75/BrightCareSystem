package shared;

import java.io.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class ResponseTest {

    // Construction

    @Test
    public void testConstructorSetsAllFieldsOnSuccess() {

        Response response = new Response(true, "Login successful.", "DOCTOR");

        assertTrue(response.isSuccess());
        assertEquals("Login successful.", response.getMessage());
        assertEquals("DOCTOR", response.getData());
    }

    @Test
    public void testConstructorSetsAllFieldsOnFailure() {

        Response response = new Response(false, "Invalid username or password.", null);

        assertFalse(response.isSuccess());
        assertEquals("Invalid username or password.", response.getMessage());
        assertNull(response.getData());
    }

    // Setters 

    @Test
    public void testSettersUpdateValues() {

        Response response = new Response();
        response.setSuccess(true);
        response.setMessage("Appointment booked successfully.");
        response.setData(101);

        assertTrue(response.isSuccess());
        assertEquals("Appointment booked successfully.", response.getMessage());
        assertEquals(101, response.getData());
    }

    // Serialization 
    // The server sends Response objects back to the client the same way -
    // serialized over the RMI connection. This confirms the round trip
    // preserves every field, including the success flag other code branches
    // on (e.g. deciding whether to show the portal after login).

    @Test
    public void testResponseSurvivesSerializationRoundTrip() throws Exception {

        Response original = new Response(true, "Consultation created successfully.", 7);

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteStream);
        out.writeObject(original);
        out.flush();

        ByteArrayInputStream inStream = new ByteArrayInputStream(byteStream.toByteArray());
        ObjectInputStream in = new ObjectInputStream(inStream);
        Response reconstructed = (Response) in.readObject();

        assertEquals(original.isSuccess(), reconstructed.isSuccess());
        assertEquals(original.getMessage(), reconstructed.getMessage());
        assertEquals(original.getData(), reconstructed.getData());
    }
}