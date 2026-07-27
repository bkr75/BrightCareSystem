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

        boolean success = appointmentDAO.bookAppointment(appointment);

        if (success) {
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

    boolean success = appointmentDAO.cancelAppointment(appointmentId);

    if (success) {
        return new Response(true, "Appointment cancelled successfully.", null);
    }

    return new Response(false, "Failed to cancel appointment.", null);
}

public Response viewAppointmentHistory(int patientId) {

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

    List<DoctorSchedule> schedules = doctorScheduleDAO.getAvailableSchedules(doctorId);

    return new Response(true, "Doctor availability retrieved.", schedules);
}
}