package iotc.gui;

import iotc.db.Command;
import java.util.LinkedList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * コマンドを表示するテーブルモデル
 * @author atsushi-o
 */
public class UPnPActionTableModel extends DefaultTableModel {
    private final String[] columnName = {"Name", "Power", "Command", "Alias"};
    private final List<Command> list;

    public UPnPActionTableModel() {
        super(0, 4);
        list = new LinkedList();
        this.addRow(new Object[]{
            "Add new command...", null, null, null
        });
    }

    @Override
    public String getColumnName(int c) {
        return columnName[c];
    }
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0:
                return String.class;
            case 1:
                return Integer.class;
            case 2:
                return String.class;
            case 3:
                return String.class;
            default:
                return String.class;
        }
    }

    public void addCommand(Command command) {
        synchronized (list) {
            if (list.contains(command)) return;
            insertRow(list.size(), new Object[]{
                command.getName(),
                command.getPower(),
                command.getCommand(),
                command.getAliasName()
            });
            list.add(command);
        }
    }

    public Command removeCommand(int row) {
        synchronized (list) {
            if (row < 0 || row > list.size()) return null;
            removeRow(row);
            return list.remove(row);
        }
    }

    public Command getRowAt(int row) {
        return list.get(row);
    }
}
