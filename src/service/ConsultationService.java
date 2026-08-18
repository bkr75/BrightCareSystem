package service;

import dao.ConsultationDAO;
import model.Consultation;
import security.Validation;
import shared.Response;

public class ConsultationService {

    private final ConsultationDAO consultationDAO;

    public ConsultationService() {
        this.consultationDAO = new ConsultationDAO();
    }

    public Response addConsultation(Consultation consultation) {

        if (consultation == null) {
            return new Response(false,
                    "Consultation data is null.",
                    null);
        }

        if (consultation.getAppointmentId() <= 0) {
            return new Response(false,
                    "A valid appointment ID is required.",
                    null);
        }

        if (Validation.isEmpty(consultation.getConsultationNotes())) {
            return new Response(false,
                    "Consultation notes cannot be empty.",
                    null);
        }

        boolean success = consultationDAO.addConsultation(consultation);

        if (success) {
            return new Response(true,
                    "Consultation created successfully.",
                    consultation);
        }

        return new Response(false,
                "Failed to create consultation.",
                null);
    }

    public Response updateDiagnosis(Consultation consultation) {

        if (consultation == null) {
            return new Response(false,
                    "Consultation data is null.",
                    null);
        }

        if (Validation.isEmpty(consultation.getConsultationNotes())) {
            return new Response(false,
                    "Consultation notes cannot be empty.",
                    null);
        }

        boolean success
                = consultationDAO.updateConsultation(consultation);

        if (success) {
            return new Response(true,
                    "Diagnosis updated successfully.",
                    consultation);
        }

        // updateConsultation only fails to match a row when the ID does not
        // exist, or when VERSION no longer matches (someone else saved first).
        // Re-read the current row to tell these two cases apart for the caller.
        Consultation current
                = consultationDAO.getConsultationById(consultation.getConsultationId());

        if (current == null) {
            return new Response(false,
                    "Consultation not found.",
                    null);
        }

        return new Response(false,
                "Conflict: this consultation was already modified by someone else "
                + "(current version is " + current.getVersion()
                + "). Reload the latest notes and try again.",
                current);
    }

    public Response getConsultation(int consultationId) {

        Consultation consultation
                = consultationDAO.getConsultationById(consultationId);

        if (consultation != null) {
            return new Response(true,
                    "Consultation found.",
                    consultation);
        }

        return new Response(false,
                "Consultation not found.",
                null);
    }
}
