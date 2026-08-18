package service;

import dao.PatientDAO;
import model.Patient;
import security.Validation;
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

        if (Validation.isEmpty(patient.getFirstName())) {
            return new Response(false, "First name is required.", null);
        }

        if (Validation.isEmpty(patient.getLastName())) {
            return new Response(false, "Last name is required.", null);
        }

        if (Validation.isEmpty(patient.getIcPassport())) {
            return new Response(false, "IC/Passport number is required.", null);
        }

        if (Validation.isEmpty(patient.getContactNumber())) {
            return new Response(false, "Contact number is required.", null);
        }

        if (Validation.isEmpty(patient.getMedicalRecordId())) {
            return new Response(false, "Medical Record ID is required.", null);
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

    public Response updatePatientInfo(Patient patient) {

        if (patient == null) {
            return new Response(false, "Patient data is null.", null);
        }

        if (Validation.isEmpty(patient.getContactNumber())) {
            return new Response(false, "Contact number is required.", null);
        }

        boolean success = patientDAO.updatePatient(patient);

        if (success) {
            return new Response(true, "Patient information updated successfully.", patient);
        }

        return new Response(false, "Failed to update patient information.", null);
    }
}
