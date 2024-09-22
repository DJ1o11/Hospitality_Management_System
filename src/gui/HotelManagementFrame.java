package gui;

import dao.HotelDAO;
import entities.Hotel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class HotelManagementFrame extends JFrame {
    private HotelDAO hotelDAO;
    private JTextField nameField;
    private JTextField locationField;
    private JTextField amenitiesField;
    private JTable hotelTable;
    private DefaultTableModel tableModel;
    private int selectedHotelId = -1;

    public HotelManagementFrame() {
        hotelDAO = new HotelDAO();
        setTitle("Hotel Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Panel for hotel input
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2));

        inputPanel.add(new JLabel("Hotel Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Location:"));
        locationField = new JTextField();
        inputPanel.add(locationField);

        inputPanel.add(new JLabel("Amenities:"));
        amenitiesField = new JTextField();
        inputPanel.add(amenitiesField);

        JButton addButton = new JButton("Add Hotel");
        addButton.addActionListener(new AddHotelAction());
        inputPanel.add(addButton);

        JButton updateButton = new JButton("Update Hotel");
        updateButton.addActionListener(new UpdateHotelAction());
        inputPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete Hotel");
        deleteButton.addActionListener(new DeleteHotelAction());
        inputPanel.add(deleteButton);

        add(inputPanel, BorderLayout.NORTH);

        // Table for displaying hotels
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Location", "Amenities"}, 0);
        hotelTable = new JTable(tableModel);
        hotelTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedHotel();
            }
        });
        JScrollPane scrollPane = new JScrollPane(hotelTable);
        add(scrollPane, BorderLayout.CENTER);

        loadHotels();
        setVisible(true);
    }

    private void loadHotels() {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Load hotels from the database
        List<Hotel> hotels = hotelDAO.getAllHotels();
        for (Hotel hotel : hotels) {
            tableModel.addRow(new Object[]{
                    hotel.getId(),
                    hotel.getName(),
                    hotel.getLocation(),
                    hotel.getAmenities()
            });
        }
    }

    private void loadSelectedHotel() {
        int selectedRow = hotelTable.getSelectedRow();
        if (selectedRow != -1) {
            selectedHotelId = (int) tableModel.getValueAt(selectedRow, 0);
            nameField.setText((String) tableModel.getValueAt(selectedRow, 1));
            locationField.setText((String) tableModel.getValueAt(selectedRow, 2));
            amenitiesField.setText((String) tableModel.getValueAt(selectedRow, 3));
        }
    }

    private class AddHotelAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText();
            String location = locationField.getText();
            String amenities = amenitiesField.getText();

            if (!name.isEmpty() && !location.isEmpty() && !amenities.isEmpty()) {
                Hotel newHotel = new Hotel(0, name, location, amenities); // ID will be auto-generated
                hotelDAO.addHotel(newHotel);
                loadHotels(); // Refresh the table
                clearFields();
            } else {
                JOptionPane.showMessageDialog(HotelManagementFrame.this, "Please fill all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class UpdateHotelAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedHotelId != -1) {
                String name = nameField.getText();
                String location = locationField.getText();
                String amenities = amenitiesField.getText();

                if (!name.isEmpty() && !location.isEmpty() && !amenities.isEmpty()) {
                    Hotel updatedHotel = new Hotel(selectedHotelId, name, location, amenities);
                    hotelDAO.updateHotel(updatedHotel);
                    loadHotels(); // Refresh the table
                    clearFields();
                    selectedHotelId = -1; // Reset selection
                } else {
                    JOptionPane.showMessageDialog(HotelManagementFrame.this, "Please fill all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(HotelManagementFrame.this, "Select a hotel to update.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class DeleteHotelAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedHotelId != -1) {
                hotelDAO.deleteHotel(selectedHotelId);
                loadHotels(); // Refresh the table
                clearFields();
                selectedHotelId = -1; // Reset selection
            } else {
                JOptionPane.showMessageDialog(HotelManagementFrame.this, "Select a hotel to delete.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearFields() {
        nameField.setText("");
        locationField.setText("");
        amenitiesField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HotelManagementFrame::new);
    }
}
