package dao;

import database.DBConnection;
import model.Appointment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class AppointmentDAO {

    // CREATE
    // Book appointment
    public boolean bookAppointment(Appointment appointment) {

        String sql
                = "INSERT INTO APPOINTMENT "
                + "(PATIENT_ID, DOCTOR_ID, SCHEDULE_ID, APPOINTMENT_DATE, STATUS) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps
                = conn.prepareStatement(sql,
                        Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, appointment.getPatientId());

            ps.setInt(2, appointment.getDoctorId());

            ps.setInt(3, appointment.getScheduleId());

            ps.setDate(4, appointment.getAppointmentDate());

            ps.setString(5, appointment.getStatus());

            ps.executeUpdate();

            ResultSet rs
                    = ps.getGeneratedKeys();

            if (rs.next()) {

                appointment.setAppointmentId(
                        rs.getInt(1)
                );

            }

            return true;

        } catch (Exception e) {

            e.printStackTrace();

            return false;

        }

    }

    // READ
    // Retrieve appointment
    public Appointment getAppointmentById(int appointmentId) {

        String sql
                = "SELECT * FROM APPOINTMENT "
                + "WHERE APPOINTMENT_ID=?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps
                = conn.prepareStatement(sql)) {

            ps.setInt(1, appointmentId);

            ResultSet rs
                    = ps.executeQuery();

            if (rs.next()) {

                return new Appointment(
                        rs.getInt("APPOINTMENT_ID"),
                        rs.getInt("PATIENT_ID"),
                        rs.getInt("DOCTOR_ID"),
                        rs.getInt("SCHEDULE_ID"),
                        rs.getDate("APPOINTMENT_DATE"),
                        rs.getString("STATUS")
                );

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return null;

    }

    // UPDATE
    // Cancel appointment
    public boolean cancelAppointment(int appointmentId) {

        String sql
                = "UPDATE APPOINTMENT "
                + "SET STATUS='CANCELLED' "
                + "WHERE APPOINTMENT_ID=?";

        try (Connection conn
                = DBConnection.getConnection(); PreparedStatement ps
                = conn.prepareStatement(sql)) {

            ps.setInt(1, appointmentId);

            int rows
                    = ps.executeUpdate();

            return rows > 0;

        } catch (Exception e) {

            e.printStackTrace();

            return false;

        }

    }

    // UPDATE
    // Doctor marks an appointment as completed after the consultation
    public boolean completeAppointment(int appointmentId) {

        String sql
                = "UPDATE APPOINTMENT "
                + "SET STATUS='COMPLETED' "
                + "WHERE APPOINTMENT_ID=?";

        try (Connection conn
                = DBConnection.getConnection(); PreparedStatement ps
                = conn.prepareStatement(sql)) {

            ps.setInt(1, appointmentId);

            int rows
                    = ps.executeUpdate();

            return rows > 0;

        } catch (Exception e) {

            e.printStackTrace();

            return false;

        }

    }

// Retrieve all appointments for a patient
    public java.util.List<Appointment> getAppointmentsByPatient(int patientId) {

        java.util.List<Appointment> appointments
                = new java.util.ArrayList<>();

        String sql
                = "SELECT * FROM APPOINTMENT "
                + "WHERE PATIENT_ID=?";

        try (Connection conn
                = DBConnection.getConnection(); PreparedStatement ps
                = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);

            ResultSet rs
                    = ps.executeQuery();

            while (rs.next()) {

                Appointment appointment
                        = new Appointment(
                                rs.getInt("APPOINTMENT_ID"),
                                rs.getInt("PATIENT_ID"),
                                rs.getInt("DOCTOR_ID"),
                                rs.getInt("SCHEDULE_ID"),
                                rs.getDate("APPOINTMENT_DATE"),
                                rs.getString("STATUS")
                        );

                appointments.add(appointment);

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return appointments;
    }

// Retrieve all appointments for a doctor
    public java.util.List<Appointment> getAppointmentsByDoctor(int doctorId) {

        java.util.List<Appointment> appointments
                = new java.util.ArrayList<>();

        String sql
                = "SELECT * FROM APPOINTMENT "
                + "WHERE DOCTOR_ID=?";

        try (Connection conn
                = DBConnection.getConnection(); PreparedStatement ps
                = conn.prepareStatement(sql)) {

            ps.setInt(1, doctorId);

            ResultSet rs
                    = ps.executeQuery();

            while (rs.next()) {

                Appointment appointment
                        = new Appointment(
                                rs.getInt("APPOINTMENT_ID"),
                                rs.getInt("PATIENT_ID"),
                                rs.getInt("DOCTOR_ID"),
                                rs.getInt("SCHEDULE_ID"),
                                rs.getDate("APPOINTMENT_DATE"),
                                rs.getString("STATUS")
                        );

                appointments.add(appointment);

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return appointments;

    }

    // Retrieve all appointments (used for reporting)
    public java.util.List<Appointment> getAllAppointments() {

        java.util.List<Appointment> appointments
                = new java.util.ArrayList<>();

        String sql = "SELECT * FROM APPOINTMENT";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                appointments.add(new Appointment(
                        rs.getInt("APPOINTMENT_ID"),
                        rs.getInt("PATIENT_ID"),
                        rs.getInt("DOCTOR_ID"),
                        rs.getInt("SCHEDULE_ID"),
                        rs.getDate("APPOINTMENT_DATE"),
                        rs.getString("STATUS")
                ));
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return appointments;
    }

    // Retrieve appointments that fall within a given month/year (used for the
    // monthly appointment summary report)
    public java.util.List<Appointment> getAppointmentsByMonth(int month, int year) {

        java.util.List<Appointment> appointments
                = new java.util.ArrayList<>();

        String sql
                = "SELECT * FROM APPOINTMENT "
                + "WHERE APPOINTMENT_DATE >= ? AND APPOINTMENT_DATE < ?";

        // Compute the first day of the given month and the first day of
        // the following month, since Derby has no reliable MONTH()/YEAR().
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.clear();
        cal.set(year, month - 1, 1);
        java.sql.Date startDate = new java.sql.Date(cal.getTimeInMillis());

        cal.add(java.util.Calendar.MONTH, 1);
        java.sql.Date endDate = new java.sql.Date(cal.getTimeInMillis());

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, startDate);
            ps.setDate(2, endDate);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                appointments.add(new Appointment(
                        rs.getInt("APPOINTMENT_ID"),
                        rs.getInt("PATIENT_ID"),
                        rs.getInt("DOCTOR_ID"),
                        rs.getInt("SCHEDULE_ID"),
                        rs.getDate("APPOINTMENT_DATE"),
                        rs.getString("STATUS")
                ));
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return appointments;
    }

}
