package dao;

import model.Consultation;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConsultationDAOConflictTest {

    @Test
    public void testUpdateConsultation_versionIncrementsOnSuccess() {

        ConsultationDAO dao = new ConsultationDAO();

        Consultation consultation = new Consultation(1, "Initial notes for conflict test");
        dao.addConsultation(consultation);

        Consultation loaded = dao.getConsultationById(consultation.getConsultationId());
        assertNotNull(loaded);
        assertEquals(0, loaded.getVersion());

        loaded.setConsultationNotes("Updated once");
        boolean result = dao.updateConsultation(loaded);
        assertTrue(result);

        Consultation reloaded = dao.getConsultationById(consultation.getConsultationId());
        assertEquals(1, reloaded.getVersion());
        assertEquals("Updated once", reloaded.getConsultationNotes());
    }

    @Test
    public void testUpdateConsultation_staleVersionIsRejected() {

        ConsultationDAO dao = new ConsultationDAO();

        Consultation consultation = new Consultation(1, "Original notes");
        dao.addConsultation(consultation);

        // Two doctors "load" the same consultation at roughly the same time.
        Consultation firstCopy = dao.getConsultationById(consultation.getConsultationId());
        Consultation secondCopy = dao.getConsultationById(consultation.getConsultationId());

        // First doctor saves successfully - row version moves from 0 to 1.
        firstCopy.setConsultationNotes("Saved by first doctor");
        assertTrue(dao.updateConsultation(firstCopy));

        // Second doctor still holds version 0 - this save must be rejected.
        secondCopy.setConsultationNotes("Saved by second doctor (stale)");
        boolean secondSaveResult = dao.updateConsultation(secondCopy);

        assertFalse("A stale version must not be allowed to overwrite a newer save",
                secondSaveResult);

        // The row must still reflect the first doctor's save, not the second.
        Consultation finalState = dao.getConsultationById(consultation.getConsultationId());
        assertEquals("Saved by first doctor", finalState.getConsultationNotes());
    }
}
