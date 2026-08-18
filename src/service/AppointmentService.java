package service;

import dao.AppointmentDAO;
import dao.DoctorScheduleDAO;
import model.Appointment;
import model.DoctorSchedule;
import shared.Response;
import dao.DoctorDAO;
import model.Doctor;

import java.util.List;

public class AppointmentService {

    private final AppointmentDAO appointmentDAO;
    private final DoctorScheduleDAO doctorScheduleDAO;
    private final DoctorDAO doctorDAO;

    public AppointmentService() {
        this.appointmentDAO = new AppointmentDAO();
        this.doctorScheduleDAO = new DoctorScheduleDAO();
        this.doctorDAO = new DoctorDAO();
    }

    public Response bookAppointment(Appointment appointment) {

        if (appointment == null) {
            return new Response(false, "Appointment data is null.", null);
        }

        if (appointment.getPatientId() <= 0) {
            return new Response(false, "A valid patient ID is required.", null);
        }

        if (appointment.getDoctorId() <= 0) {
            return new Response(false, "A valid doctor ID is required.", null);
        }

        if (appointment.getAppointmentDate() == null) {
            return new Response(false, "Appointment date is required.", null);
        }

        boolean success = appointmentDAO.bookAppointment(appointment);

        if (success) {

            // Close the slot so it stops showing up as available and can't
            // be double-booked by another patient.
            doctorScheduleDAO.updateScheduleStatus(
                    appointment.getScheduleId(), "BOOKED");

            return new Response(
                    true,
                    "Appointment booked successfully.",
                    appointment);
        }

        return new Response(
                false,
                "Failed to book appointment.",
                null);
    }

    public Response cancelAppointment(int appointmentId) {

        if (appointmentId <= 0) {
            return new Response(false, "A valid appointment ID is required.", null);
        }

        // Look up the appointment first so we know which schedule slot to
        // free up again once the cancellation succeeds.
        Appointment appointment = appointmentDAO.getAppointmentById(appointmentId);

        boolean success = appointmentDAO.cancelAppointment(appointmentId);

        if (success) {

            if (appointment != null) {
                doctorScheduleDAO.updateScheduleStatus(
                        appointment.getScheduleId(), "AVAILABLE");
            }

            return new Response(true, "Appointment cancelled successfully.", null);
        }

        return new Response(false, "Failed to cancel appointment.", null);
    }

    public Response completeAppointment(int appointmentId) {

        if (appointmentId <= 0) {
            return new Response(false, "A valid appointment ID is required.", null);
        }

        boolean success = appointmentDAO.completeAppointment(appointmentId);

        if (success) {
            return new Response(true, "Appointment marked as completed.", null);
        }

        return new Response(false, "Failed to complete appointment.", null);
    }

    public Response viewAppointmentHistory(int patientId) {

        if (patientId <= 0) {
            return new Response(false, "A valid patient ID is required.", null);
        }

        List<Appointment> appointments = appointmentDAO.getAppointmentsByPatient(patientId);

        for (Appointment a : appointments) {

            Doctor doctor = doctorDAO.getDoctorById(a.getDoctorId());

            if (doctor != null) {
                a.setDoctorName(doctor.getDoctorName());
            }
        }

        return new Response(true, "Appointment history retrieved.", appointments);
    }

    public Response checkDoctorAvailability(int doctorId) {

        if (doctorId <= 0) {
            return new Response(false, "A valid doctor ID is required.", null);
        }

        List<DoctorSchedule> schedules = doctorScheduleDAO.getAvailableSchedules(doctorId);

        return new Response(true, "Doctor availability retrieved.", schedules);
    }
}
