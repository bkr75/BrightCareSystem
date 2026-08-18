package service;

import dao.AppointmentDAO;
import dao.DoctorScheduleDAO;
import dao.MedicalRecordDAO;
import model.Appointment;
import model.DoctorSchedule;
import model.MedicalRecord;
import shared.Response;

import java.util.List;

public class DoctorService {

    private final AppointmentDAO appointmentDAO;
    private final MedicalRecordDAO medicalRecordDAO;
    private final DoctorScheduleDAO doctorScheduleDAO;

    public DoctorService() {
        this.appointmentDAO = new AppointmentDAO();
        this.medicalRecordDAO = new MedicalRecordDAO();
        this.doctorScheduleDAO = new DoctorScheduleDAO();
    }

    public Response getAppointmentList(int doctorId) {

        List<Appointment> appointments
                = appointmentDAO.getAppointmentsByDoctor(doctorId);

        return new Response(true,
                "Appointment list retrieved.",
                appointments);
    }

    public Response getPatientHistory(int patientId) {

        List<MedicalRecord> records
                = medicalRecordDAO.getMedicalRecordsByPatientId(patientId);

        return new Response(true,
                "Patient history retrieved.",
                records);
    }

    public Response getSchedule(int doctorId) {

        // The doctor needs to see their FULL schedule (available AND
        // booked slots), not just the available ones - otherwise a slot
        // disappears from their own view the moment it gets booked.
        List<DoctorSchedule> schedule
                = doctorScheduleDAO.getAllSchedulesByDoctor(doctorId);

        return new Response(true,
                "Schedule retrieved.",
                schedule);
    }

    public Response updateSchedule(DoctorSchedule schedule) {

        if (schedule == null) {
            return new Response(false,
                    "Schedule data is null.",
                    null);
        }

        boolean success
                = doctorScheduleDAO.updateScheduleStatus(
                        schedule.getScheduleId(),
                        schedule.getStatus());

        if (success) {
            return new Response(true,
                    "Schedule updated successfully.",
                    schedule);
        }

        return new Response(false,
                "Failed to update schedule.",
                null);
    }
}
