package burp;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.*;
import burp.api.montoya.http.message.params.HttpParameter;
import burp.api.montoya.http.message.params.HttpParameterType;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;

public class RequestShortener implements BurpExtension {

    private MontoyaApi api;
    private MyTableModel tableModel;

    @Override
    public void initialize(MontoyaApi api) {
        this.api = api;

        tableModel = new MyTableModel();

        api.extension().setName("Request shortener");
        
        api.userInterface().registerSuiteTab("Request shortener", constructSettingsTab(tableModel));

        api.userInterface().registerHttpRequestEditorProvider(new MyHttpRequestEditorProvider(api, tableModel));       
    
        Logging logging = api.logging();        

        logging.logToOutput("Request shortener is loaded. Configure the extension in the eponymous tab.");
    }

    private Component constructSettingsTab(MyTableModel tableModel) {
        // Popup "Add parameter" panel
        JPanel popupPanel = new JPanel(new GridLayout(2, 2, 0, 5));
        JTextField parameterNameField = new JTextField(10);
        popupPanel.add(new JLabel("Name:"));
        popupPanel.add(parameterNameField);
        // popupPanel.add(Box.createVerticalStrut(15));
        JComboBox<HttpParameterType> parameterTypeBox = new JComboBox<>();
        parameterTypeBox.addItem(HttpParameterType.URL);
        parameterTypeBox.addItem(HttpParameterType.BODY);
        parameterTypeBox.addItem(HttpParameterType.COOKIE);
        // TODO JSON or all
        // JComboBox<HttpParameterType> parameterTypeBox = new JComboBox<>(HttpParameterType.values());
        popupPanel.add(new JLabel("Type:"));
        popupPanel.add(parameterTypeBox);
        // TODO enabled
        // popupPanel.add(new JLabel("Enabled:"));
        // JCheckBox parameterEnabledBox = new JCheckBox("", true);
        // popupPanel.add(parameterEnabledBox);        

        // Parameters panel
        JPanel settingsPanel = new JPanel();
        settingsPanel.setBorder(new TitledBorder("Settings"));
        settingsPanel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        // https://stackoverflow.com/questions/17874717/providing-white-space-in-a-swing-gui
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;   
        constraints.insets = new Insets(0, 0, 5, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        // Add button
        constraints.gridx = 0;
        constraints.gridy = 1;
        JButton addButton = new JButton("Add");
        settingsPanel.add(addButton, constraints);

        // Remove button
        constraints.gridx = 0;
        constraints.gridy = 2;
        JButton removeButton = new JButton("Remove");
        settingsPanel.add(removeButton, constraints);

        // Table
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridheight = 2;
        constraints.gridwidth = 2;
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        settingsPanel.add(scrollPane, constraints);
    
        addButton.addActionListener(e -> {
            int popupResult = JOptionPane.showConfirmDialog(null, popupPanel, 
            "Add parameter", JOptionPane.OK_CANCEL_OPTION);

            if (popupResult == JOptionPane.OK_OPTION) {
                String parameterName = parameterNameField.getText();
                HttpParameterType parameterType = (HttpParameterType)parameterTypeBox.getSelectedItem();
                // Boolean parameterEnabled = parameterEnabledBox.isSelected();
                if (!parameterName.isEmpty()) {
                    tableModel.add(HttpParameter.parameter(parameterName,"", parameterType));
                    parameterNameField.setText("");
                }
            }
        });

        removeButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.remove(selectedRow);
            }
        });

        return settingsPanel;
    }
}