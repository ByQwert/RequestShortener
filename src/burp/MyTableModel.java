package burp;

import burp.api.montoya.http.message.params.HttpParameter;
import burp.api.montoya.http.message.params.HttpParameterType;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

// https://github.com/PortSwigger/burp-extensions-montoya-api-examples/blob/main/customlogger/src/main/java/example/customlogger/MyTableModel.java
public class MyTableModel extends AbstractTableModel {
    private String[] columnNames = {"Name", "Type"};

    public final List<HttpParameter> parameters;

    public MyTableModel() {
        this.parameters = new ArrayList<>();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public synchronized int getRowCount() {
        return parameters.size();
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        HttpParameter parameter = parameters.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> parameter.name();
            case 1 -> parameter.type();
            // case 2 -> true;
            default -> "";
        };
    }

    @Override 
    public Class getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return HttpParameterType.class;
            // case 2:
            //     return Boolean.class;
            default:
                return Boolean.class;
        }
    }

    public synchronized void add(HttpParameter parameter) {
        int index = parameters.size();
        parameters.add(parameter);
        fireTableRowsInserted(index, index);
    }

    public synchronized void remove(int index) {
        parameters.remove(index);
        fireTableRowsDeleted(index, index);
    }

    public synchronized Object get(int rowIndex) {
        return parameters.get(rowIndex);
    }
}