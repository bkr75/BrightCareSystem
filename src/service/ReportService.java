package service;

import dao.AppointmentDAO;
import dao.DoctorDAO;
import dao.PatientDAO;
import model.Appointment;
import model.AppointmentSummaryReport;
import model.Doctor;
import model.DoctorActivityReport;
import model.PatientAnalyticsReport;
import shared.Response;

import java.util.ArrayList;
import java.util.List;

public class ReportService {

    private final AppointmentDAO appointmentDAO;
    private final DoctorDAO doctorDAO;
    private final PatientDAO patientDAO;

    public ReportService() {
        this.appointmentDAO = new AppointmentDAO();
        this.doctorDAO = new DoctorDAO();
        this.patientDAO = new PatientDAO();
    }

    // Monthly appointment summary: how many appointments were booked in a
    // given month, broken down by status.
    public Response generateAppointmentSummaryReport(int month, int year) {

        if (month < 1 || month > 12) {
            return new Response(false, "Invalid month.", null);
        }

        List<Appointment> appointments
                = appointmentDAO.getAppointmentsByMonth(month, year);

        int completed = 0;
        int cancelled = 0;
        int pending = 0;

        for (Appointment appointment : appointments) {

            String status = appointment.getStatus();

            if (status == null) {
                continue;
            }

            switch (status.toUpperCase()) {
                case "COMPLETED":
                    completed++;
                    break;
                case "CANCELLED":
                    cancelled++;
                    break;
                default:
                    pending++;
            }
        }

        AppointmentSummaryReport report = new AppointmentSummaryReport(
                month,
                year,
                appointments.size(),
                completed,
                cancelled,
                pending);

        return new Response(true, "Appointment summary report generated.", report);
    }

    // Doctor activity report: total appointments handled per doctor.
    public Response generateDoctorActivityReport() {

        List<Doctor> doctors = doctorDAO.getAllDoctors();
        List<Appointment> appointments = appointmentDAO.getAllAppointments();

        List<DoctorActivityReport> report = new ArrayList<>();

        for (Doctor doctor : doctors) {

            int count = 0;

            for (Appointment appointment : appointments) {
                if (appointment.getDoctorId() == doctor.getDoctorId()) {
                    count++;
                }
            }

            report.add(new DoctorActivityReport(
                    doctor.getDoctorId(),
                    doctor.getDoctorName(),
                    doctor.getSpecialization(),
                    count));
        }

        return new Response(true, "Doctor activity report generated.", report);
    }

    // Patient analytics report: overall patient/appointment volume.
    public Response generatePatientAnalyticsReport() {

        int totalPatients = patientDAO.getPatientCount();
        int totalAppointments = appointmentDAO.getAllAppointments().size();

        double average = (totalPatients == 0)
                ? 0.0
                : (double) totalAppointments / totalPatients;

        PatientAnalyticsReport report = new PatientAnalyticsReport(
                totalPatients,
                totalAppointments,
                average);

        return new Response(true, "Patient analytics report generated.", report);
    }
}
