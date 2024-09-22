package gui;

import dao.RoomDAO;
import entities.Room;

import javax.swing.*;
        import javax.swing.table.DefaultTableModel;
import java.awt.*;
        import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class RoomManagementFrame extends JFrame {
    private RoomDAO roomDAO;
    private JTextField hotelIdField;
    private JTextField roomNumberField;
    private JTextField typeField;
    private JTextField priceField;
    private JTextField statusField;
    private JTable roomTable;
    private DefaultTableModel tableModel;
    private int selectedRoomId = -1;

    public RoomManagementFrame() {
        roomDAO = new RoomDAO();
        setTitle("Room Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Panel for room input
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 2));

        inputPanel.add(new JLabel("Hotel ID:"));
        hotelIdField = new JTextField();
        inputPanel.add(hotelIdField);

        inputPanel.add(new JLabel("Room Number:"));
        roomNumberField = new JTextField();
        inputPanel.add(roomNumberField);

        inputPanel.add(new JLabel("Type:"));
        typeField = new JTextField();
        inputPanel.add(typeField);

        inputPanel.add(new JLabel("Price:"));
        priceField = new JTextField();
        inputPanel.add(priceField);

        inputPanel.add(new JLabel("Status:"));
        statusField = new JTextField();
        inputPanel.add(statusField);

        JButton addButton = new JButton("Add Room");
        addButton.addActionListener(new AddRoomAction());
        inputPanel.add(addButton);

        JButton updateButton = new JButton("Update Room");
        updateButton.addActionListener(new UpdateRoomAction());
        inputPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete Room");
        deleteButton.addActionListener(new DeleteRoomAction());
        inputPanel.add(deleteButton);

        add(inputPanel, BorderLayout.NORTH);

        // Table for displaying rooms
        tableModel = new DefaultTableModel(new String[]{"ID", "Hotel ID", "Room Number", "Type", "Price", "Status"}, 0);
        roomTable = new JTable(tableModel);
        roomTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedRoom();
            }
        });
        JScrollPane scrollPane = new JScrollPane(roomTable);
        add(scrollPane, BorderLayout.CENTER);

        loadRooms();
        setVisible(true);
    }

    private void loadRooms() {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Load rooms from the database
        List<Room> rooms = roomDAO.getAllRooms();
        for (Room room : rooms) {
            tableModel.addRow(new Object[]{
                    room.getId(),
                    room.getHotelId(),
                    room.getRoomNumber(),
                    room.getType(),
                    room.getPrice(),
                    room.getStatus()
            });
        }
    }

    private void loadSelectedRoom() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow != -1) {
            selectedRoomId = (int) tableModel.getValueAt(selectedRow, 0);
            hotelIdField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 1)));
            roomNumberField.setText((String) tableModel.getValueAt(selectedRow, 2));
            typeField.setText((String) tableModel.getValueAt(selectedRow, 3));
            priceField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 4)));
            statusField.setText((String) tableModel.getValueAt(selectedRow, 5));
        }
    }

    private class AddRoomAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int hotelId = Integer.parseInt(hotelIdField.getText());
            String roomNumber = roomNumberField.getText();
            String type = typeField.getText();
            double price = Double.parseDouble(priceField.getText());
            String status = statusField.getText();

            if (!roomNumber.isEmpty() && !type.isEmpty() && price > 0 && !status.isEmpty()) {
                Room newRoom = new Room(0, hotelId, roomNumber, type, price, status); // ID will be auto-generated
                roomDAO.addRoom(newRoom);
                loadRooms(); // Refresh the table
                clearFields();
            } else {
                JOptionPane.showMessageDialog(RoomManagementFrame.this, "Please fill all fields correctly.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class UpdateRoomAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedRoomId != -1) {
                int hotelId = Integer.parseInt(hotelIdField.getText());
                String roomNumber = roomNumberField.getText();
                String type = typeField.getText();
                double price = Double.parseDouble(priceField.getText());
                String status = statusField.getText();

                if (!roomNumber.isEmpty() && !type.isEmpty() && price > 0 && !status.isEmpty()) {
                    Room updatedRoom = new Room(selectedRoomId, hotelId, roomNumber, type, price, status);
                    roomDAO.updateRoom(updatedRoom);
                    loadRooms(); // Refresh the table
                    clearFields();
                    selectedRoomId = -1; // Reset selection
                } else {
                    JOptionPane.showMessageDialog(RoomManagementFrame.this, "Please fill all fields correctly.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(RoomManagementFrame.this, "Select a room to update.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class DeleteRoomAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedRoomId != -1) {
                roomDAO.deleteRoom(selectedRoomId);
                loadRooms(); // Refresh the table
                clearFields();
                selectedRoomId = -1; // Reset selection
            } else {
                JOptionPane.showMessageDialog(RoomManagementFrame.this, "Select a room to delete.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearFields() {
        hotelIdField.setText("");
        roomNumberField.setText("");
        typeField.setText("");
        priceField.setText("");
        statusField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RoomManagementFrame::new);
    }
}
