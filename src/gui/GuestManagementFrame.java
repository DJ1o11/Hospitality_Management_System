package gui;

import dao.GuestDAO;
import entities.Guest;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class GuestManagementFrame extends JFrame {
    private GuestDAO guestDAO;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneNumberField;
    private JTable guestTable;
    private DefaultTableModel tableModel;
    private int selectedGuestId = -1;

    public GuestManagementFrame() {
        guestDAO = new GuestDAO();
        setTitle("Guest Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Panel for guest input
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2));

        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        inputPanel.add(emailField);

        inputPanel.add(new JLabel("Phone Number:"));
        phoneNumberField = new JTextField();
        inputPanel.add(phoneNumberField);

        JButton addButton = new JButton("Add Guest");
        addButton.addActionListener(new AddGuestAction());
        inputPanel.add(addButton);

        JButton updateButton = new JButton("Update Guest");
        updateButton.addActionListener(new UpdateGuestAction());
        inputPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete Guest");
        deleteButton.addActionListener(new DeleteGuestAction());
        inputPanel.add(deleteButton);

        add(inputPanel, BorderLayout.NORTH);

        // Table for displaying guests
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Phone Number"}, 0);
        guestTable = new JTable(tableModel);
        guestTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedGuest();
            }
        });
        JScrollPane scrollPane = new JScrollPane(guestTable);
        add(scrollPane, BorderLayout.CENTER);

        loadGuests();
        setVisible(true);
    }

    private void loadGuests() {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Load guests from the database
        List<Guest> guests = guestDAO.getAllGuests();
        for (Guest guest : guests) {
            tableModel.addRow(new Object[]{
                    guest.getId(),
                    guest.getName(),
                    guest.getEmail(),
                    guest.getPhoneNumber()
            });
        }
    }

    private void loadSelectedGuest() {
        int selectedRow = guestTable.getSelectedRow();
        if (selectedRow != -1) {
            selectedGuestId = (int) tableModel.getValueAt(selectedRow, 0);
            nameField.setText((String) tableModel.getValueAt(selectedRow, 1));
            emailField.setText((String) tableModel.getValueAt(selectedRow, 2));
            phoneNumberField.setText((String) tableModel.getValueAt(selectedRow, 3));
        }
    }

    private class AddGuestAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText();
            String email = emailField.getText();
            String phoneNumber = phoneNumberField.getText();

            if (!name.isEmpty() && !email.isEmpty() && !phoneNumber.isEmpty()) {
                Guest newGuest = new Guest(0, name, email, phoneNumber); // ID will be auto-generated
                guestDAO.addGuest(newGuest);
                loadGuests(); // Refresh the table
                clearFields();
            } else {
                JOptionPane.showMessageDialog(GuestManagementFrame.this, "Please fill all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class UpdateGuestAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedGuestId != -1) {
                String name = nameField.getText();
                String email = emailField.getText();
                String phoneNumber = phoneNumberField.getText();

                if (!name.isEmpty() && !email.isEmpty() && !phoneNumber.isEmpty()) {
                    Guest updatedGuest = new Guest(selectedGuestId, name, email, phoneNumber);
                    guestDAO.updateGuest(updatedGuest);
                    loadGuests(); // Refresh the table
                    clearFields();
                    selectedGuestId = -1; // Reset selection
                } else {
                    JOptionPane.showMessageDialog(GuestManagementFrame.this, "Please fill all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(GuestManagementFrame.this, "Select a guest to update.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class DeleteGuestAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedGuestId != -1) {
                guestDAO.deleteGuest(selectedGuestId);
                loadGuests(); // Refresh the table
                clearFields();
                selectedGuestId = -1; // Reset selection
            } else {
                JOptionPane.showMessageDialog(GuestManagementFrame.this, "Select a guest to delete.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearFields() {
        nameField.setText("");
        emailField.setText("");
        phoneNumberField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GuestManagementFrame::new);
    }
}
