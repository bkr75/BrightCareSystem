package service;

import dao.ConsultationDAO;
import model.Consultation;
import shared.Response;

public class ConsultationService {

    private final ConsultationDAO consultationDAO;

    public ConsultationService() {
        this.consultationDAO = new ConsultationDAO();
    }

    public Response updateDiagnosis(Consultation consultation) {

        if (consultation == null) {
            return new Response(false,
                    "Consultation data is null.",
                    null);
        }

        boolean success =
                consultationDAO.updateConsultation(consultation);

        if (success) {
            return new Response(true,
                    "Diagnosis updated successfully.",
                    consultation);
        }

        return new Response(false,
                "Failed to update diagnosis.",
                null);
    }
}