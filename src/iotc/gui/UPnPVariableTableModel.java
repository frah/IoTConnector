package iotc.gui;

import iotc.db.HibernateUtil;
import iotc.db.Sensor;
import iotc.db.SensorType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Query;
import org.hibernate.Session;
import org.itolab.morihit.clinkx.UPnPRemoteDevice;
import org.itolab.morihit.clinkx.UPnPRemoteStateVariable;

/**
 * UPnPVariable表示用テーブルモデル
 * @author atsushi-o
 */
public class UPnPVariableTableModel extends DefaultTableModel {
    private final String[] columnName = {"", "Name", "Type", "Value"};
    private final List<UPnPRemoteStateVariable> list;

    public UPnPVariableTableModel() {
        super(0, 4);
        list = new LinkedList();
    }

    @Override
    public String getColumnName(int c) {
        return columnName[c];
    }
    @Override
    public boolean isCellEditable(int row, int column) {
        switch (column) {
            case 0:
                return true;
            case 1:
                return false;
            case 2:
                return true;
            case 3:
                return false;
            default:
                return false;
        }
    }
    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0:
                return Boolean.class;
            case 1:
                return String.class;
            case 2:
                return SensorType.class;
            case 3:
                return Float.class;
            default:
                return String.class;
        }
    }

    public void addStateVariable(UPnPRemoteStateVariable var) {
        synchronized (list) {
            if (list.contains(var)) return;
            insertRow(list.size(), new Object[]{
                Boolean.FALSE,
                var.getName(),
                new SensorType(),
                var.getValue()
            });
            list.add(var);
        }
    }

    public void updateStateVariable(UPnPRemoteStateVariable var) {
        synchronized (list) {
            int row = list.indexOf(var);
            if (row == -1) return;
            setValueAt(var.getValue(), row, 2);
        }
    }

    public UPnPRemoteStateVariable removeStateVariable(int row) {
        synchronized (list) {
            if (row < 0 || row > list.size()) return null;
            removeRow(row);
            return list.remove(row);
        }
    }

    public void removeStateVariable(UPnPRemoteDevice device) {
        synchronized (list) {
            int num_removed = 0;
            for (int i=0, n=list.size(); i<n; i++) {
                if (list.get(i-num_removed).getRemoteService().getRemoteDevice() == device) {
                    removeStateVariable(i-num_removed);
                    num_removed++;
                }
            }
        }
    }



    public Sensor getRowAt(int row) {
        Sensor s = null;

        if ((Boolean)getValueAt(row, 0)) {
            s = new Sensor();
            s.setName((String)getValueAt(row, 1));
            s.setSensorType((SensorType)getValueAt(row, 2));
        }

        return s;
    }
}
