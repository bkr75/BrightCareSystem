package service;

import dao.PatientDAO;
import model.Patient;
import shared.Response;

public class PatientService {

    private final PatientDAO patientDAO;

    public PatientService() {
        this.patientDAO = new PatientDAO();
    }

    public Response registerPatient(Patient patient) {

        if (patient == null) {
            return new Response(false,
                    "Patient data is null.",
                    null);
        }

        boolean success = patientDAO.registerPatient(patient);

        if (success) {
            return new Response(true,
                    "Patient registered successfully.",
                    patient);
        }

        return new Response(false,
                "Failed to register patient.",
                null);
    }

    public Response viewPatient(int patientId) {

        Patient patient = patientDAO.getPatientById(patientId);

        if (patient != null) {
            return new Response(true,
                    "Patient found.",
                    patient);
        }

        return new Response(false,
                "Patient not found.",
                null);
    }
}