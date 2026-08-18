package model;

import java.io.Serializable;

public class MedicalRecord implements Serializable {

    private int recordId;
    private int patientId;
    private String diagnosis;

    // Constructor for creating new record
    public MedicalRecord(int patientId,
            String diagnosis) {

        this.patientId = patientId;
        this.diagnosis = diagnosis;

    }

    // Constructor for retrieving record
    public MedicalRecord(int recordId,
            int patientId,
            String diagnosis) {

        this.recordId = recordId;
        this.patientId = patientId;
        this.diagnosis = diagnosis;

    }

    // Getters
    public int getRecordId() {

        return recordId;

    }

    public int getPatientId() {

        return patientId;

    }

    public String getDiagnosis() {

        return diagnosis;

    }

    // Setter
    public void setRecordId(int recordId) {

        this.recordId = recordId;

    }

    public void setDiagnosis(String diagnosis) {

        this.diagnosis = diagnosis;

    }

}
