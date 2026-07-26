package service;

import dao.AppointmentDAO;
import model.Appointment;
import shared.Response;

public class AppointmentService {

    private final AppointmentDAO appointmentDAO;

    public AppointmentService() {
        this.appointmentDAO = new AppointmentDAO();
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
}