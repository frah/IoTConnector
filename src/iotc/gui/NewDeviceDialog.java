package iotc.gui;

import iotc.db.*;
import iotc.event.DBEventListener;
import iotc.event.DBEventListenerManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.TableColumn;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.itolab.morihit.clinkx.UPnPRemoteDevice;
import org.itolab.morihit.clinkx.UPnPRemoteService;
import org.itolab.morihit.clinkx.UPnPRemoteStateVariable;

/**
 * 新しいデバイスを追加するためのダイアログ
 * @author atsushi-o
 */
public class NewDeviceDialog extends javax.swing.JDialog implements DBEventListener {
    private UPnPRemoteDevice upprd;

    /**
     * Creates new form NewDeviceDialog
     */
    public NewDeviceDialog(java.awt.Frame parent, boolean modal, UPnPRemoteDevice device) {
        super(parent, modal);
        this.upprd = device;
        initComponents();
        updateTable();
        DBEventListenerManager.getInstance().addListener(this, "Room|SensorType");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        udnField = new javax.swing.JTextField();
        nameField = new javax.swing.JTextField();
        roomCombo = new javax.swing.JComboBox();
        addRoomButton = new javax.swing.JButton();
        typeCombo = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        explanationTextArea = new javax.swing.JTextArea();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        comTable = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        varTable = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("New Device - IoTConnector");
        setResizable(false);

        jLabel1.setText("UDN");

        jLabel2.setText("Name");

        jLabel3.setText("Room");

        jLabel4.setText("Type");

        udnField.setEditable(false);
        if (upprd != null) {
            udnField.setText(upprd.getUDN());
        }

        if (upprd != null) {
            nameField.setText(upprd.getFriendlyName());
        }

        addRoomButton.setText("Add");
        addRoomButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addRoomButtonActionPerformed(evt);
            }
        });

        for (DeviceType t : DeviceType.values()) {
            typeCombo.addItem(t);
        }

        jLabel5.setText("Explanation");

        explanationTextArea.setColumns(20);
        explanationTextArea.setRows(5);
        jScrollPane1.setViewportView(explanationTextArea);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        comTable.setModel(new UPnPActionTableModel());
        comTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        comTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                comTableMouseReleased(evt);
            }
        });
        comTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                comTableKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(comTable);

        jLabel6.setText("Commands");

        varTable.setModel(new UPnPVariableTableModel());
        varTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        JComboBox combo = new JComboBox();
        combo.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                comboBoxValueChanged(e);
            }
        });
        combo.setBorder(BorderFactory.createEmptyBorder());
        varTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(combo));
        updateSensorTypeCombo();
        varTable.getColumnModel().getColumn(0).setMinWidth(1);
        varTable.getColumnModel().getColumn(0).setMaxWidth(15);
        varTable.getColumnModel().getColumn(0).setPreferredWidth(15);
        jScrollPane3.setViewportView(varTable);

        jLabel7.setText("Variables");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 629, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(nameField, javax.swing.GroupLayout.DEFAULT_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(udnField, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(typeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(roomCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(addRoomButton, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jLabel5)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 629, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 629, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(udnField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(roomCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addRoomButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(typeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        updateRoomList();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void addRoomButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addRoomButtonActionPerformed
        NewRoomDialog nr = new NewRoomDialog((javax.swing.JFrame)this.getParent(), true);
        nr.setVisible(true);
    }//GEN-LAST:event_addRoomButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        boolean isSuccess = false;

        if (nameField.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Device name is required", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Device device = new Device();
        device.setUdn(udnField.getText());
        device.setName(nameField.getText());
        device.setRoom((Room)roomCombo.getSelectedItem());
        device.setType(((DeviceType)typeCombo.getSelectedItem()).getId());
        device.setExplanation(explanationTextArea.getText());

        UPnPActionTableModel actionModel = (UPnPActionTableModel)comTable.getModel();
        UPnPVariableTableModel varModel = (UPnPVariableTableModel)varTable.getModel();

        Session s = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction t = s.beginTransaction();

        if (device.getType() != DeviceType.NonUPnP.getId()) {
            Query q = s.getNamedQuery("Device.findFromUDN");
            q.setString("udn", device.getUdn());
            if (q.uniqueResult() != null) {
                s.getTransaction().commit();
                JOptionPane.showMessageDialog(this, "This device is already added", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try {
            Serializable id = s.save(device);
            Device d = (Device)s.load(Device.class, id);

            for (int i = 0; i < actionModel.getRowCount() - 1; i++) {
                Command c = actionModel.getRowAt(i);
                if (c != null) {
                    c.setDevice(d);
                    s.save(c);
                }
            }
            for (int i = 0; i < varModel.getRowCount(); i++) {
                Sensor sens = varModel.getRowAt(i);
                if (sens != null) {
                    sens.setDevice(d);
                    s.save(sens);
                }
            }

            t.commit();
            isSuccess = true;
        } catch (HibernateException ex) {
            t.rollback();
            JOptionPane.showMessageDialog(this, ex.getLocalizedMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }

        if (isSuccess) {
            this.dispose();
        }
    }//GEN-LAST:event_okButtonActionPerformed

    private void comTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_comTableMouseReleased
        if (upprd == null) return;
        if (comTable.rowAtPoint(evt.getPoint()) != comTable.getRowCount() -1) return;
        if (evt.getClickCount() != 2) return;

        NewCommandDialog ncd = new NewCommandDialog((javax.swing.JFrame)this.getParent(), true, this.upprd);
        ncd.setVisible(true);
        if (ncd.isCancelled()) return;

        Command c = ncd.getNewCommand();
        if (c == null) return;
        UPnPActionTableModel actionModel = (UPnPActionTableModel)comTable.getModel();
        actionModel.addCommand(c);
    }//GEN-LAST:event_comTableMouseReleased

    private void comTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_comTableKeyReleased
        if (evt.getKeyCode() != KeyEvent.VK_DELETE) return;

        int r = comTable.getSelectedRow();
        if (r < 0 || r == comTable.getRowCount() - 1) return;
        UPnPActionTableModel actionModel = (UPnPActionTableModel)comTable.getModel();
        actionModel.removeCommand(r);
    }//GEN-LAST:event_comTableKeyReleased

    /**
     * 部屋一覧を更新する
     */
    private void updateRoomList() {
        Session s = HibernateUtil.getSessionFactory().getCurrentSession();
        s.beginTransaction();
        Query q = s.getNamedQuery("Room.findAll");

        roomCombo.removeAllItems();;
        for (Room r : (List<Room>)q.list()) {
            roomCombo.addItem(r);
        }
        s.getTransaction().commit();
    }

    // <editor-fold defaultstate="collapsed" desc="Table functions">
    /**
     * センサタイプのコンボボックスを更新する
     */
    private void updateSensorTypeCombo() {
        TableColumn tc = varTable.getColumnModel().getColumn(2);
        JComboBox combo = (JComboBox)((DefaultCellEditor)tc.getCellEditor()).getComponent();
        combo.removeAllItems();
        ActionListener[] als = combo.getActionListeners();
        for (ActionListener al : als) {
            combo.removeActionListener(al);
        }

        Session s = HibernateUtil.getSessionFactory().getCurrentSession();
        s.beginTransaction();
        Query q = s.getNamedQuery("SensorType.findAll");
        for (SensorType st : (List<SensorType>)q.list()) {
            combo.addItem(st);
        }
        combo.addItem("Add new type...");
        s.getTransaction().commit();

        for (ActionListener al : als) {
            combo.addActionListener(al);
        }
    }

    /**
     * センサタイプのコンボボックスの値が変更された時のイベントリスナ
     * @param evt
     */
    private void comboBoxValueChanged(ActionEvent evt) {
        JComboBox c = (JComboBox)evt.getSource();
        if (c.getSelectedIndex() == c.getItemCount()-1) {
            NewSensorTypeDialog nstd = new NewSensorTypeDialog((javax.swing.JFrame)this.getParent(), true);
            nstd.setVisible(true);
            if (c.getItemCount() > 1) c.setSelectedIndex(0);
        }
    }

    /**
     * テーブルの内容を更新する
     */
    private void updateTable() {
        if (upprd == null) return;
        UPnPVariableTableModel varModel = (UPnPVariableTableModel)varTable.getModel();

        for (UPnPRemoteService upprs : this.upprd.getRemoteServiceList()) {
            for (UPnPRemoteStateVariable var : upprs.getRemoteStateVariableList()) {
                varModel.addStateVariable(var);
            }
        }
    }// </editor-fold>

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addRoomButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JTable comTable;
    private javax.swing.JTextArea explanationTextArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField nameField;
    private javax.swing.JButton okButton;
    private javax.swing.JComboBox roomCombo;
    private javax.swing.JComboBox typeCombo;
    private javax.swing.JTextField udnField;
    private javax.swing.JTable varTable;
    // End of variables declaration//GEN-END:variables

    @Override
    public void onCreate(String entityName, Object entity) {
        switch (entityName) {
            case "Room":
                updateRoomList();
                break;
            case "SensorType":
                updateSensorTypeCombo();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDelete(String entityName, Object entity) {
        switch (entityName) {
            case "Room":
                updateRoomList();
                break;
            case "SensorType":
                updateSensorTypeCombo();
                break;
            default:
                break;
        }
    }

    @Override
    public void onUpdate(String entityName, Object entity) {
        switch (entityName) {
            case "Room":
                updateRoomList();
                break;
            case "SensorType":
                updateSensorTypeCombo();
                break;
            default:
                break;
        }
    }
}
