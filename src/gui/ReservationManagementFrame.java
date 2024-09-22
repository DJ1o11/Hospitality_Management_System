package gui;

import dao.ReservationDAO;
import entities.Reservation;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

public class ReservationManagementFrame extends JFrame {
    private ReservationDAO reservationDAO;
    private JTextField guestIdField;
    private JTextField roomIdField;
    private JTextField checkInField;
    private JTextField checkOutField;
    private JTable reservationTable;
    private DefaultTableModel tableModel;
    private int selectedReservationId = -1;

    public ReservationManagementFrame() {
        reservationDAO = new ReservationDAO();
        setTitle("Reservation Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Panel for reservation input
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2));

        inputPanel.add(new JLabel("Guest ID:"));
        guestIdField = new JTextField();
        inputPanel.add(guestIdField);

        inputPanel.add(new JLabel("Room ID:"));
        roomIdField = new JTextField();
        inputPanel.add(roomIdField);

        inputPanel.add(new JLabel("Check-In Date (yyyy-mm-dd):"));
        checkInField = new JTextField();
        inputPanel.add(checkInField);

        inputPanel.add(new JLabel("Check-Out Date (yyyy-mm-dd):"));
        checkOutField = new JTextField();
        inputPanel.add(checkOutField);

        JButton addButton = new JButton("Add Reservation");
        addButton.addActionListener(new AddReservationAction());
        inputPanel.add(addButton);

        JButton updateButton = new JButton("Update Reservation");
        updateButton.addActionListener(new UpdateReservationAction());
        inputPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete Reservation");
        deleteButton.addActionListener(new DeleteReservationAction());
        inputPanel.add(deleteButton);

        add(inputPanel, BorderLayout.NORTH);

        // Table for displaying reservations
        tableModel = new DefaultTableModel(new String[]{"ID", "Guest ID", "Room ID", "Check-In", "Check-Out"}, 0);
        reservationTable = new JTable(tableModel);
        reservationTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedReservation();
            }
        });
        JScrollPane scrollPane = new JScrollPane(reservationTable);
        add(scrollPane, BorderLayout.CENTER);

        loadReservations();
        setVisible(true);
    }

    private void loadReservations() {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Load reservations from the database
        List<Reservation> reservations = reservationDAO.getAllReservations();
        for (Reservation reservation : reservations) {
            tableModel.addRow(new Object[]{
                    reservation.getId(),
                    reservation.getGuestId(),
                    reservation.getRoomId(),
                    reservation.getCheckInDate(),
                    reservation.getCheckOutDate()
            });
        }
    }

    private void loadSelectedReservation() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow != -1) {
            selectedReservationId = (int) tableModel.getValueAt(selectedRow, 0);
            guestIdField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 1)));
            roomIdField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 2)));
            checkInField.setText(tableModel.getValueAt(selectedRow, 3).toString());
            checkOutField.setText(tableModel.getValueAt(selectedRow, 4).toString());
        }
    }

    private class AddReservationAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int guestId = Integer.parseInt(guestIdField.getText());
                int roomId = Integer.parseInt(roomIdField.getText());
                LocalDate checkInDate = LocalDate.parse(checkInField.getText());
                LocalDate checkOutDate = LocalDate.parse(checkOutField.getText());

                if (reservationDAO.isRoomAvailable(roomId, checkInDate, checkOutDate)) {
                    Reservation newReservation = new Reservation(0, guestId, roomId, checkInDate, checkOutDate);
                    reservationDAO.addReservation(newReservation);
                    loadReservations(); // Refresh the table
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(ReservationManagementFrame.this, "Room is not available for the selected dates.", "Availability Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ReservationManagementFrame.this, "Invalid input. Please check your entries.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class UpdateReservationAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedReservationId != -1) {
                try {
                    int guestId = Integer.parseInt(guestIdField.getText());
                    int roomId = Integer.parseInt(roomIdField.getText());
                    LocalDate checkInDate = LocalDate.parse(checkInField.getText());
                    LocalDate checkOutDate = LocalDate.parse(checkOutField.getText());

                    Reservation updatedReservation = new Reservation(selectedReservationId, guestId, roomId, checkInDate, checkOutDate);
                    reservationDAO.updateReservation(updatedReservation);
                    loadReservations(); // Refresh the table
                    clearFields();
                    selectedReservationId = -1; // Reset selection
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ReservationManagementFrame.this, "Invalid input. Please check your entries.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(ReservationManagementFrame.this, "Select a reservation to update.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class DeleteReservationAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedReservationId != -1) {
                reservationDAO.deleteReservation(selectedReservationId);
                loadReservations(); // Refresh the table
                clearFields();
                selectedReservationId = -1; // Reset selection
            } else {
                JOptionPane.showMessageDialog(ReservationManagementFrame.this, "Select a reservation to delete.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearFields() {
        guestIdField.setText("");
        roomIdField.setText("");
        checkInField.setText("");
        checkOutField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ReservationManagementFrame::new);
    }
}
