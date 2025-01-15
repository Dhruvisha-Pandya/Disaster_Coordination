import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class DisasterAgencyApp extends JFrame {
    private JTextField disasterIdField, dateField, locationField, disasterTypeField, severityField;
    private JTextField agencyIdField, agencyNameField, agencyTypeField, roleField;
    private JTable dataTable;
    private Connection connection;

    public DisasterAgencyApp() {
        setTitle("Disaster and Agency Management");
        setSize(1000, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Establish database connection
        connectToDatabase();

        // Layout configuration
        setLayout(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(1, 2));
        add(formPanel, BorderLayout.NORTH);

        // Disaster Panel
        JPanel disasterPanel = new JPanel(new GridLayout(6, 2));
        disasterPanel.setBorder(BorderFactory.createTitledBorder("Disaster Details"));
        disasterIdField = new JTextField();
        dateField = new JTextField();
        locationField = new JTextField();
        disasterTypeField = new JTextField();
        severityField = new JTextField();

        disasterPanel.add(new JLabel("Disaster ID:"));
        disasterPanel.add(disasterIdField);
        disasterPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        disasterPanel.add(dateField);
        disasterPanel.add(new JLabel("Location:"));
        disasterPanel.add(locationField);
        disasterPanel.add(new JLabel("Type:"));
        disasterPanel.add(disasterTypeField);
        disasterPanel.add(new JLabel("Severity (1-5):"));
        disasterPanel.add(severityField);

        // Agency Panel
        JPanel agencyPanel = new JPanel(new GridLayout(5, 2));
        agencyPanel.setBorder(BorderFactory.createTitledBorder("Agency Details"));
        agencyIdField = new JTextField();
        agencyNameField = new JTextField();
        agencyTypeField = new JTextField();
        roleField = new JTextField();

        agencyPanel.add(new JLabel("Agency ID:"));
        agencyPanel.add(agencyIdField);
        agencyPanel.add(new JLabel("Agency Name:"));
        agencyPanel.add(agencyNameField);
        agencyPanel.add(new JLabel("Type:"));
        agencyPanel.add(agencyTypeField);
        agencyPanel.add(new JLabel("Role:"));
        agencyPanel.add(roleField);

        formPanel.add(disasterPanel);
        formPanel.add(agencyPanel);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(3, 4));
        JButton addDisasterBtn = new JButton("Add Disaster");
        JButton updateDisasterBtn = new JButton("Update Disaster");
        JButton deleteDisasterBtn = new JButton("Delete Disaster");
        JButton showDisastersBtn = new JButton("Show Disasters");

        JButton addAgencyBtn = new JButton("Add Agency");
        JButton updateAgencyBtn = new JButton("Update Agency");
        JButton deleteAgencyBtn = new JButton("Delete Agency");
        JButton showAgenciesBtn = new JButton("Show Agencies");

        JButton addAgencyForDisasterBtn = new JButton("Add Agency for Disaster");
        JButton deleteAgencyForDisasterBtn = new JButton("Delete Agency for Disaster");
        JButton showAgenciesForDisasterBtn = new JButton("Show Agencies for Disaster");
        JButton showDisastersForAgencyBtn = new JButton("Show Disasters for Agency");

        addDisasterBtn.addActionListener(e -> addDisaster());
        updateDisasterBtn.addActionListener(e -> updateDisaster());
        deleteDisasterBtn.addActionListener(e -> deleteDisaster());
        addAgencyBtn.addActionListener(e -> addAgency());
        updateAgencyBtn.addActionListener(e -> updateAgency());
        deleteAgencyBtn.addActionListener(e -> deleteAgency());
        showDisastersBtn.addActionListener(e -> showDisasters());
        showAgenciesBtn.addActionListener(e -> showAgencies());
        showAgenciesForDisasterBtn.addActionListener(e -> showAgenciesForDisaster());
        showDisastersForAgencyBtn.addActionListener(e -> showDisastersForAgency());
        addAgencyForDisasterBtn.addActionListener(e -> addAgencyForDisaster());
        deleteAgencyForDisasterBtn.addActionListener(e -> deleteAgencyForDisaster());

        buttonPanel.add(addDisasterBtn);
        buttonPanel.add(updateDisasterBtn);
        buttonPanel.add(deleteDisasterBtn);
        buttonPanel.add(addAgencyBtn);
        buttonPanel.add(updateAgencyBtn);
        buttonPanel.add(deleteAgencyBtn);
        buttonPanel.add(showDisastersBtn);
        buttonPanel.add(showAgenciesBtn);
        buttonPanel.add(showAgenciesForDisasterBtn);
        buttonPanel.add(showDisastersForAgencyBtn);
        buttonPanel.add(addAgencyForDisasterBtn);
        buttonPanel.add(deleteAgencyForDisasterBtn);
        add(buttonPanel, BorderLayout.CENTER);

        // Data Table
        dataTable = new JTable();
        add(new JScrollPane(dataTable), BorderLayout.SOUTH);

        // to make everything is visible and packed
        pack();
        setVisible(true);
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/disaster_agency", "postgres",
                    "PASSWORD");
            System.out.println("Connected to database.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error connecting to database: " + e.getMessage());
        }
    }

    // helper function to clear out all input fields

    private void clearInputFields() {
        // Clear all disaster-related fields
        disasterIdField.setText("");
        dateField.setText("");
        locationField.setText("");
        disasterTypeField.setText("");
        severityField.setText("");

        // Clear all agency-related fields
        agencyIdField.setText("");
        agencyNameField.setText("");
        agencyTypeField.setText("");
        roleField.setText("");
    }

    // CRUD methods for Disaster and Agency
    private void addDisaster() {
        try {
            String sql = "INSERT INTO Disaster (disaster_id, date, location, type, severity) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(disasterIdField.getText()));
            statement.setDate(2, java.sql.Date.valueOf(dateField.getText()));
            statement.setString(3, locationField.getText());
            statement.setString(4, disasterTypeField.getText());
            statement.setInt(5, Integer.parseInt(severityField.getText()));
            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Disaster added successfully.");

            clearInputFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding disaster: " + e.getMessage());
        }
    }

    private void updateDisaster() {
        try {
            int disasterId = Integer.parseInt(disasterIdField.getText());

            // Check if the disaster exists
            String checkDisasterSQL = "SELECT * FROM Disaster WHERE disaster_id = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkDisasterSQL);
            checkStatement.setInt(1, disasterId);
            ResultSet rs = checkStatement.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Disaster ID does not exist!");
                return;
            }

            // Update the Disaster details
            String updateDisasterSQL = "UPDATE Disaster SET date = ?, location = ?, type = ?, severity = ? WHERE disaster_id = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateDisasterSQL);
            updateStatement.setDate(1, Date.valueOf(dateField.getText()));
            updateStatement.setString(2, locationField.getText());
            updateStatement.setString(3, disasterTypeField.getText()); // Corrected field name
            updateStatement.setInt(4, Integer.parseInt(severityField.getText()));
            updateStatement.setInt(5, disasterId);
            updateStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Disaster updated successfully!");

            clearInputFields();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating disaster: " + e.getMessage());
        }
    }

    private void deleteDisaster() {
        try {
            int disasterId = Integer.parseInt(disasterIdField.getText());

            // Check if the disaster exists
            String checkDisasterSQL = "SELECT * FROM Disaster WHERE disaster_id = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkDisasterSQL);
            checkStatement.setInt(1, disasterId);
            ResultSet rs = checkStatement.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Disaster ID does not exist!");
                return;
            }

            // Delete from junction table first to maintain referential integrity
            String deleteJunctionSQL = "DELETE FROM disaster_agencies WHERE disaster_id = ?";
            PreparedStatement deleteJunctionStatement = connection.prepareStatement(deleteJunctionSQL);
            deleteJunctionStatement.setInt(1, disasterId);
            deleteJunctionStatement.executeUpdate();

            // Now delete from Disaster table
            String deleteDisasterSQL = "DELETE FROM Disaster WHERE disaster_id = ?";
            PreparedStatement deleteDisasterStatement = connection.prepareStatement(deleteDisasterSQL);
            deleteDisasterStatement.setInt(1, disasterId);
            deleteDisasterStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Disaster deleted successfully!");

            clearInputFields();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting disaster: " + e.getMessage());
        }
    }

    private void addAgency() {
        try {
            String sql = "INSERT INTO Agency (agency_id, name, type, role) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(agencyIdField.getText()));
            statement.setString(2, agencyNameField.getText());
            statement.setString(3, agencyTypeField.getText());
            statement.setString(4, roleField.getText());
            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Agency added successfully.");

            clearInputFields();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding agency: " + e.getMessage());
        }
    }

    private void updateAgency() {
        try {
            int agencyId = Integer.parseInt(agencyIdField.getText());

            // Check if the agency exists
            String checkAgencySQL = "SELECT * FROM Agency WHERE agency_id = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkAgencySQL);
            checkStatement.setInt(1, agencyId);
            ResultSet rs = checkStatement.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Agency ID does not exist!");
                return;
            }

            // Update the Agency details
            String updateAgencySQL = "UPDATE Agency SET name = ?, type = ?, role = ? WHERE agency_id = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateAgencySQL);
            updateStatement.setString(1, agencyNameField.getText());
            updateStatement.setString(2, agencyTypeField.getText());
            updateStatement.setString(3, roleField.getText());
            updateStatement.setInt(4, agencyId);
            updateStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Agency updated successfully!");

            clearInputFields();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating agency: " + e.getMessage());
        }
    }

    private void deleteAgency() {
        try {
            int agencyId = Integer.parseInt(agencyIdField.getText());

            // Check if the agency exists
            String checkAgencySQL = "SELECT * FROM Agency WHERE agency_id = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkAgencySQL);
            checkStatement.setInt(1, agencyId);
            ResultSet rs = checkStatement.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Agency ID does not exist!");
                return;
            }

            // Delete from junction table first
            String deleteJunctionSQL = "DELETE FROM disaster_agencies WHERE agency_id = ?";
            PreparedStatement deleteJunctionStatement = connection.prepareStatement(deleteJunctionSQL);
            deleteJunctionStatement.setInt(1, agencyId);
            deleteJunctionStatement.executeUpdate();

            // Now delete from Agency table
            String deleteAgencySQL = "DELETE FROM Agency WHERE agency_id = ?";
            PreparedStatement deleteAgencyStatement = connection.prepareStatement(deleteAgencySQL);
            deleteAgencyStatement.setInt(1, agencyId);
            deleteAgencyStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Agency deleted successfully!");

            clearInputFields();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting agency: " + e.getMessage());
        }
    }

    private void showDisasters() {
        try {
            String sql = "SELECT * FROM Disaster";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();

            Vector<String> columnNames = new Vector<>();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                columnNames.add(rsmd.getColumnName(i));
            }

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    row.add(rs.getObject(i));
                }
                data.add(row);
            }

            dataTable.setModel(new DefaultTableModel(data, columnNames));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error showing disasters: " + e.getMessage());
        }
    }

    private void showAgencies() {
        try {
            String sql = "SELECT * FROM Agency";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();

            Vector<String> columnNames = new Vector<>();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                columnNames.add(rsmd.getColumnName(i));
            }

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    row.add(rs.getObject(i));
                }
                data.add(row);
            }

            dataTable.setModel(new DefaultTableModel(data, columnNames));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error showing agencies: " + e.getMessage());
        }
    }

    // join queries methods

    private void showAgenciesForDisaster() {
        try {
            String sql = "SELECT d.disaster_id, d.date, d.location, d.type AS disaster_type, d.severity, " +
                    "a.agency_id, a.name, a.type AS agency_type, a.role " +
                    "FROM Disaster d " +
                    "JOIN disaster_agencies da ON d.disaster_id = da.disaster_id " +
                    "JOIN Agency a ON a.agency_id = da.agency_id " +
                    "WHERE da.disaster_id=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(disasterIdField.getText()));
            ResultSet resultSet = statement.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Disaster ID");
            model.addColumn("Date");
            model.addColumn("Location");
            model.addColumn("Disaster Type");
            model.addColumn("Severity");
            model.addColumn("Agency ID");
            model.addColumn("Agency Name");
            model.addColumn("Agency Type");
            model.addColumn("Agency Role");

            while (resultSet.next()) {
                Vector<Object> row = new Vector<>();
                row.add(resultSet.getInt("disaster_id"));
                row.add(resultSet.getDate("date"));
                row.add(resultSet.getString("location"));
                row.add(resultSet.getString("disaster_type")); 
                row.add(resultSet.getInt("severity"));
                row.add(resultSet.getInt("agency_id"));
                row.add(resultSet.getString("name"));
                row.add(resultSet.getString("agency_type")); 
                row.add(resultSet.getString("role"));
                model.addRow(row);
            }
            dataTable.setModel(model);
            clearInputFields();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching agencies for disaster: " + e.getMessage());
        }
    }

    private void showDisastersForAgency() {
        try {
            String sql = "SELECT a.agency_id, a.name, a.type AS agency_type, a.role, " +
                    "d.disaster_id, d.date, d.location, d.type AS disaster_type, d.severity " +
                    "FROM Agency a " +
                    "JOIN disaster_agencies da ON a.agency_id = da.agency_id " +
                    "JOIN Disaster d ON d.disaster_id = da.disaster_id " +
                    "WHERE da.agency_id=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(agencyIdField.getText()));
            ResultSet resultSet = statement.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Agency ID");
            model.addColumn("Agency Name");
            model.addColumn("Agency Type");
            model.addColumn("Agency Role");
            model.addColumn("Disaster ID");
            model.addColumn("Date");
            model.addColumn("Location");
            model.addColumn("Disaster Type");
            model.addColumn("Severity");

            while (resultSet.next()) {
                Vector<Object> row = new Vector<>();
                row.add(resultSet.getInt("agency_id"));
                row.add(resultSet.getString("name"));
                row.add(resultSet.getString("agency_type")); // Renamed column
                row.add(resultSet.getString("role"));
                row.add(resultSet.getInt("disaster_id"));
                row.add(resultSet.getDate("date"));
                row.add(resultSet.getString("location"));
                row.add(resultSet.getString("disaster_type")); // Renamed column
                row.add(resultSet.getInt("severity"));
                model.addRow(row);
            }
            dataTable.setModel(model);
            clearInputFields();
        } 
        catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching disasters for agency: " + e.getMessage());
        }
    }

    private void addAgencyForDisaster() {
        try {
            String sql = "INSERT INTO disaster_agencies (disaster_id, agency_id) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(disasterIdField.getText()));
            statement.setInt(2, Integer.parseInt(agencyIdField.getText()));
            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Agency added for disaster.");
            clearInputFields();
        }
         catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding agency for disaster: " + e.getMessage());
        }
    }

    private void deleteAgencyForDisaster() {
        try {
            // Check if the combination of disaster_id and agency_id exists in the junction table first
            String checkSql = "SELECT * FROM disaster_agencies WHERE disaster_id = ? AND agency_id = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkSql);
            checkStatement.setInt(1, Integer.parseInt(disasterIdField.getText()));
            checkStatement.setInt(2, Integer.parseInt(agencyIdField.getText()));
            ResultSet rs = checkStatement.executeQuery();

            if (!rs.next()) {
                // If no matching record is found, show an error message
                JOptionPane.showMessageDialog(this,
                        "The specified combination of Disaster ID and Agency ID does not exist.");
                return; 
            }

        // If the combination exists, proceed with the delete operation
            String deleteSql = "DELETE FROM disaster_agencies WHERE disaster_id = ? AND agency_id = ?";
            PreparedStatement deleteStatement = connection.prepareStatement(deleteSql);
            deleteStatement.setInt(1, Integer.parseInt(disasterIdField.getText()));
            deleteStatement.setInt(2, Integer.parseInt(agencyIdField.getText()));
            int rowsAffected = deleteStatement.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Agency successfully deleted from disaster.");
            } else {
                JOptionPane.showMessageDialog(this, "Error: The agency could not be deleted.");
            }
            clearInputFields();
        } 
        catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting agency from disaster: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DisasterAgencyApp::new);
    }
}
