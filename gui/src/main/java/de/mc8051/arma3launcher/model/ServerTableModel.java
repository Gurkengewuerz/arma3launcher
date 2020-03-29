package de.mc8051.arma3launcher.model;

import de.mc8051.arma3launcher.objects.Server;
import de.mc8051.arma3launcher.utils.LangUtils;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gurkengewuerz.de on 24.03.2020.
 */
public class ServerTableModel extends AbstractTableModel {

    private String[] columnNames = {
            LangUtils.getInstance().getString("description"),
            LangUtils.getInstance().getString("ip_address"),
            LangUtils.getInstance().getString("port"),
            LangUtils.getInstance().getString("preset")
    };
    private List<Server> data = new ArrayList<>();

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public void add(Server s) {
        data.add(s);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) return data.get(rowIndex).getName();
        else if (columnIndex == 1) return data.get(rowIndex).getIp();
        else if (columnIndex == 2) return data.get(rowIndex).getPort();
        else if (columnIndex == 3) return data.get(rowIndex).getPreset().getName();
        return null;
    }
}