package iotc.gui;

import java.util.List;
import iotc.db.Command;
import iotc.db.CommandType;
import javax.swing.JOptionPane;
import org.itolab.morihit.clinkx.*;
import javax.swing.table.DefaultTableModel;

/**
 * 新しいコマンドを登録するダイアログ
 * @author atsushi-o
 */
public class NewCommandDialog extends javax.swing.JDialog {
    private UPnPRemoteDevice upprd;
    private Command ret;
    private boolean isCancelled = true;

    /**
     * Creates new form NewCommandDialog
     */
    public NewCommandDialog(java.awt.Frame parent, boolean modal, UPnPRemoteDevice device) {
        super(parent, modal);
        this.upprd = device;
        initComponents();
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
        actionCombo = new javax.swing.JComboBox<UPnPRemoteAction>();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        argTable = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        powerCombo = new javax.swing.JComboBox<Integer>();
        aliasTextField = new javax.swing.JTextField();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("New Command - IoTConnector");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setText("Action");

        actionCombo.setRenderer(new javax.swing.plaf.basic.BasicComboBoxRenderer(){
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof UPnPRemoteAction) {
                    setText(((UPnPRemoteAction)value).getName());
                }
                return this;
            }
        });
        this.updateActionCombo();
        actionCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionComboActionPerformed(evt);
            }
        });

        jLabel2.setText("Arguments");

        argTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Type", "Value", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        argTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(argTable);
        argTable.getColumnModel().getColumn(1).setResizable(false);
        argTable.getColumnModel().getColumn(3).setResizable(false);
        argTable.getColumnModel().getColumn(3).setPreferredWidth(15);
        this.actionComboActionPerformed(null);

        jLabel3.setText("Name");

        jLabel4.setText("Power");

        jLabel5.setText("Alias");

        for (int i = 0; i <= 5; i++) {
            powerCombo.addItem(i);
        }

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(actionCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(powerCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(nameTextField)
                            .addComponent(aliasTextField)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(actionCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(powerCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(aliasTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void updateActionCombo() {
        for (UPnPRemoteService s : this.upprd.getRemoteServiceList()) {
            for (UPnPRemoteAction a : s.getRemoteActionList()) {
                actionCombo.addItem(a);
            }
        }
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.isCancelled = true;
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void actionComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actionComboActionPerformed
        DefaultTableModel tm = (DefaultTableModel)argTable.getModel();
        for (int i = tm.getRowCount() - 1; i >= 0 ; i--) {
            tm.removeRow(i);
        }

        UPnPRemoteAction a = actionCombo.getItemAt(actionCombo.getSelectedIndex());
        for (UPnPRemoteActionArgument arg : a.getRemoteActionInputArgumentList()) {
            tm.addRow(new Object[]{
                arg.getName(),
                arg.getDataType(),
                "",
                false
            });
        }
    }//GEN-LAST:event_actionComboActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        if (nameTextField.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Command name is required", "error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.ret = new Command();
        ret.setName(nameTextField.getText());
        ret.setType(CommandType.UPnPAction.getId());
        ret.setPower(powerCombo.getItemAt(powerCombo.getSelectedIndex()));
        ret.setAliasName(aliasTextField.getText());

        /* generate command string */
        DefaultTableModel tm = (DefaultTableModel)argTable.getModel();
        UPnPRemoteAction a = actionCombo.getItemAt(actionCombo.getSelectedIndex());
        StringBuilder sb = new StringBuilder();
        sb.append(a.getName());
        for (int i = 0; i < tm.getRowCount(); i++) {
            sb.append("::");
            sb.append(tm.getValueAt(i, 0)).append("(");
            if ((Boolean)tm.getValueAt(i, 3)) {
                sb.append(":variable:");
            } else {
                sb.append(tm.getValueAt(i, 2));
            }
            sb.append(")");
        }
        ret.setCommand(sb.toString());

        this.isCancelled = false;
        this.setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        this.isCancelled = true;
    }//GEN-LAST:event_formWindowClosing

    public Command getNewCommand() {
        return this.ret;
    }

    public boolean isCancelled() {
        return this.isCancelled;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<UPnPRemoteAction> actionCombo;
    private javax.swing.JTextField aliasTextField;
    private javax.swing.JTable argTable;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton okButton;
    private javax.swing.JComboBox<Integer> powerCombo;
    // End of variables declaration//GEN-END:variables
}
