package dao;

import entities.Reservation;
import util.DatabaseConnector;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    // Convert LocalDate to java.sql.Date
    private java.sql.Date toSqlDate(LocalDate date) {
        return java.sql.Date.valueOf(date);
    }

    // Convert java.sql.Date to LocalDate
    private LocalDate toLocalDate(java.sql.Date date) {
        return date.toLocalDate();
    }

    // Create a new reservation record in the database
    public void addReservation(Reservation reservation) {
        String query = "INSERT INTO Reservations (guest_id, room_id, check_in_date, check_out_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, reservation.getGuestId());
            stmt.setInt(2, reservation.getRoomId());
            stmt.setDate(3, toSqlDate(reservation.getCheckInDate()));
            stmt.setDate(4, toSqlDate(reservation.getCheckOutDate()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Retrieve all reservations from the database
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM Reservations";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Reservation reservation = new Reservation(
                        rs.getInt("id"),
                        rs.getInt("guest_id"),
                        rs.getInt("room_id"),
                        toLocalDate(rs.getDate("check_in_date")),
                        toLocalDate(rs.getDate("check_out_date"))
                );
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    // Retrieve a reservation by its ID
    public Reservation getReservationById(int id) {
        Reservation reservation = null;
        String query = "SELECT * FROM Reservations WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    reservation = new Reservation(
                            rs.getInt("id"),
                            rs.getInt("guest_id"),
                            rs.getInt("room_id"),
                            toLocalDate(rs.getDate("check_in_date")),
                            toLocalDate(rs.getDate("check_out_date"))
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservation;
    }

    // Update reservation information
    public void updateReservation(Reservation reservation) {
        String query = "UPDATE Reservations SET guest_id = ?, room_id = ?, check_in_date = ?, check_out_date = ? WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, reservation.getGuestId());
            stmt.setInt(2, reservation.getRoomId());
            stmt.setDate(3, toSqlDate(reservation.getCheckInDate()));
            stmt.setDate(4, toSqlDate(reservation.getCheckOutDate()));
            stmt.setInt(5, reservation.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete a reservation record from the database
    public void deleteReservation(int id) {
        String query = "DELETE FROM Reservations WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Check room availability for specific dates
    public boolean isRoomAvailable(int roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        String query = "SELECT COUNT(*) FROM Reservations WHERE room_id = ? AND check_in_date < ? AND check_out_date > ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, roomId);
            stmt.setDate(2, toSqlDate(checkInDate));
            stmt.setDate(3, toSqlDate(checkOutDate));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
