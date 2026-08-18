package dao;

import database.DBConnection;
import model.Doctor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DoctorDAO {

    // CREATE
    // Add doctor
    public boolean addDoctor(Doctor doctor) {

        String sql
                = "INSERT INTO DOCTOR "
                + "(DOCTOR_NAME, SPECIALIZATION) "
                + "VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps
                = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, doctor.getDoctorName());

            ps.setString(2, doctor.getSpecialization());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) {

                doctor.setDoctorId(
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
    // Find doctor by ID
    public Doctor getDoctorById(int doctorId) {

        String sql
                = "SELECT * FROM DOCTOR WHERE DOCTOR_ID=?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps
                = conn.prepareStatement(sql)) {

            ps.setInt(1, doctorId);

            ResultSet rs
                    = ps.executeQuery();

            if (rs.next()) {

                return new Doctor(
                        rs.getInt("DOCTOR_ID"),
                        rs.getString("DOCTOR_NAME"),
                        rs.getString("SPECIALIZATION")
                );

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return null;

    }

    // UPDATE
    // Update doctor specialization
    public boolean updateDoctor(Doctor doctor) {

        String sql
                = "UPDATE DOCTOR SET SPECIALIZATION=? "
                + "WHERE DOCTOR_ID=?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps
                = conn.prepareStatement(sql)) {

            ps.setString(1, doctor.getSpecialization());

            ps.setInt(2, doctor.getDoctorId());

            int rows
                    = ps.executeUpdate();

            return rows > 0;

        } catch (Exception e) {

            e.printStackTrace();

            return false;

        }

    }

    // READ
    // Retrieve all doctors (used for reporting)
    public java.util.List<Doctor> getAllDoctors() {

        java.util.List<Doctor> doctors = new java.util.ArrayList<>();

        String sql = "SELECT * FROM DOCTOR";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                doctors.add(new Doctor(
                        rs.getInt("DOCTOR_ID"),
                        rs.getString("DOCTOR_NAME"),
                        rs.getString("SPECIALIZATION")
                ));
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return doctors;
    }

}
