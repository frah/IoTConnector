package iotc.gui;

import iotc.db.Command;
import iotc.db.CommandType;
import java.util.LinkedList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import org.itolab.morihit.clinkx.UPnPRemoteAction;
import org.itolab.morihit.clinkx.UPnPRemoteDevice;

/**
 *
 * @author atsushi-o
 */
public class UPnPActionTableModel extends DefaultTableModel {
    private final String[] columnName = {"", "Name", "Type", "Power", "Command", "Alias"};
    private final List<UPnPRemoteAction> list;

    public UPnPActionTableModel() {
        super(0, 6);
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
                return true;
            case 2:
                return true;
            case 3:
                return true;
            case 4:
                return false;
            case 5:
                return true;
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
                return CommandType.class;
            case 3:
                return Integer.class;
            case 4:
                return String.class;
            case 5:
                return String.class;
            default:
                return String.class;
        }
    }

    public void addUPnPAction(UPnPRemoteAction action) {
        synchronized (list) {
            if (list.contains(action)) return;
            insertRow(list.size(), new Object[]{
                Boolean.FALSE,
                action.getName(),
                CommandType.UPnPAction,
                0,
                action.getName(),
                ""
            });
            list.add(action);
        }
    }

    public UPnPRemoteAction removeUPnPAction(int row) {
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
                    removeUPnPAction(i-num_removed);
                    num_removed++;
                }
            }
        }
    }

    public Command getRowAt(int row) {
        Command c = null;

        if ((Boolean)getValueAt(row, 0)) {
            c = new Command();
            c.setName((String)getValueAt(row, 1));
            c.setType(((CommandType)getValueAt(row, 2)).getId());
            c.setPower((Integer)getValueAt(row, 3));
            c.setCommand((String)getValueAt(row, 4));
            c.setAliasName((String)getValueAt(row, 5));
        }

        return c;
    }
}
