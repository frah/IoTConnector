package iotc.gui;

import iotc.common.UPnPException;
import iotc.db.*;
import iotc.event.DBEventListener;
import iotc.event.DBEventListenerManager;
import iotc.event.UPnPEventListener;
import iotc.gui.ToolTipTree.ToolTipTreeNode;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.hibernate.Query;
import org.hibernate.Session;
import org.itolab.morihit.clinkx.UPnPRemoteDevice;
import org.itolab.morihit.clinkx.UPnPRemoteStateVariable;

/**
 * メインウィンドウ
 * @author atsushi-o
 */
public class MainOverviewWindow extends javax.swing.JFrame implements UPnPEventListener, DBEventListener {
    private Device currentView;
    private Device lastRightClicked;

    private static final Logger LOG;
    static {
        LOG = Logger.getLogger(MainOverviewWindow.class.getName());
    }

    /**
     * Creates new form MainOverviewWindow
     */
    public MainOverviewWindow() {
        // <editor-fold defaultstate="collapsed" desc="Look and Feel settings">
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }// </editor-fold>

        initComponents();
        DBEventListenerManager.getInstance().addListener(this, "Room|Device");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        devicePopup = new javax.swing.JPopupMenu();
        aComMenuItem = new javax.swing.JMenuItem();
        dDelMenuItem = new javax.swing.JMenuItem();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        roomTree = new ToolTipTree();
        jSplitPane2 = new javax.swing.JSplitPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        comTable = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        varTable = new javax.swing.JTable();
        menuBar = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        quitMenuItem = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        addRoomMenuItem = new javax.swing.JMenuItem();
        addIRComMenuItem = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        userWMenuItem = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();

        aComMenuItem.setText("Add command");
        aComMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aComMenuItemActionPerformed(evt);
            }
        });
        devicePopup.add(aComMenuItem);

        dDelMenuItem.setText("Remove this");
        dDelMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dDelMenuItemActionPerformed(evt);
            }
        });
        devicePopup.add(dDelMenuItem);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("IoTConnector");

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setMinimumSize(new java.awt.Dimension(800, 500));
        jSplitPane1.setPreferredSize(new java.awt.Dimension(800, 500));

        jScrollPane1.setPreferredSize(new java.awt.Dimension(130, 275));

        roomTree.setRootVisible(false);
        roomTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                roomTreeMouseReleased(evt);
            }
        });
        roomTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                roomTreeValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(roomTree);
        updateTree();

        jSplitPane1.setLeftComponent(jScrollPane1);

        jSplitPane2.setDividerLocation(250);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        comTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name", "Type", "Power", "Alias", "Command"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(comTable);
        comTable.getColumnModel().getColumn(0).setResizable(false);
        comTable.getColumnModel().getColumn(0).setPreferredWidth(20);

        jSplitPane2.setTopComponent(jScrollPane2);

        varTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Type", "Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.String.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(varTable);
        varTable.getColumnModel().getColumn(0).setResizable(false);
        varTable.getColumnModel().getColumn(0).setPreferredWidth(20);

        jSplitPane2.setRightComponent(jScrollPane3);

        jSplitPane1.setRightComponent(jSplitPane2);

        jMenu1.setText("File");

        quitMenuItem.setText("Exit");
        quitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(quitMenuItem);

        menuBar.add(jMenu1);

        jMenu3.setText("Edit");

        addRoomMenuItem.setText("Add room");
        addRoomMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addRoomMenuItemActionPerformed(evt);
            }
        });
        jMenu3.add(addRoomMenuItem);

        addIRComMenuItem.setText("Add IR command");
        addIRComMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addIRComMenuItemActionPerformed(evt);
            }
        });
        jMenu3.add(addIRComMenuItem);

        menuBar.add(jMenu3);

        jMenu4.setText("Window");

        userWMenuItem.setText("User window");
        userWMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userWMenuItemActionPerformed(evt);
            }
        });
        jMenu4.add(userWMenuItem);

        menuBar.add(jMenu4);

        jMenu2.setText("Help");

        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(aboutMenuItem);

        menuBar.add(jMenu2);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addRoomMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addRoomMenuItemActionPerformed
        NewRoomDialog nr = new NewRoomDialog(this, true);
        nr.setVisible(true);
    }//GEN-LAST:event_addRoomMenuItemActionPerformed

    private void quitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitMenuItemActionPerformed
        this.dispose();
        System.exit(0);
    }//GEN-LAST:event_quitMenuItemActionPerformed

    private void roomTreeMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_roomTreeMouseReleased
        if (SwingUtilities.isRightMouseButton(evt)) {
            TreePath path = roomTree.getPathForLocation(evt.getX(), evt.getY());
            if (path == null) return;
            Object o = ((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject();
            if (!(o instanceof Device)) return;

            lastRightClicked = (Device)o;
            roomTree.setSelectionPath(path);
            devicePopup.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_roomTreeMouseReleased

    private void dDelMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dDelMenuItemActionPerformed
        if (lastRightClicked == null) return;

        if (JOptionPane.showConfirmDialog(this, "Delete anyway?", "IoTConnector", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.OK_OPTION) return;
        Session s = HibernateUtil.getSessionFactory().openSession();
        s.beginTransaction();
        try {
            s.delete(lastRightClicked);
            lastRightClicked = null;
            s.getTransaction().commit();
        } catch (org.hibernate.HibernateException ex) {
            s.getTransaction().rollback();
            JOptionPane.showMessageDialog(this, ex.getLocalizedMessage(), "error", JOptionPane.ERROR_MESSAGE);
        } finally {
            s.close();
        }
    }//GEN-LAST:event_dDelMenuItemActionPerformed

    private void aComMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aComMenuItemActionPerformed
        if (lastRightClicked == null) return;
        UPnPRemoteDevice d = iotc.UPnPDevices.getInstance().getAvailableUPnPDevice(lastRightClicked.getUdn());
        if (d == null) {
            JOptionPane.showMessageDialog(this, "This UPnP device is not available", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        NewCommandDialog ncd = new NewCommandDialog(this, true, d);

        while (true) {
            ncd.setVisible(true);

            Command c = ncd.getNewCommand();
            if (ncd.isCancelled() || c == null) break;
            c.setDevice(lastRightClicked);

            Session s = HibernateUtil.getSessionFactory().openSession();
            s.beginTransaction();
            try {
                s.save(c);
                s.getTransaction().commit();
                break;
            } catch (org.hibernate.HibernateException ex) {
                s.getTransaction().rollback();
                JOptionPane.showMessageDialog(this, ex.getLocalizedMessage(), "error", JOptionPane.ERROR_MESSAGE);
            } finally {
                s.close();
            }
        }
        ncd.dispose();

        updateTable(lastRightClicked);
        lastRightClicked = null;
    }//GEN-LAST:event_aComMenuItemActionPerformed

    private void roomTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_roomTreeValueChanged
        Object o = ((DefaultMutableTreeNode)evt.getPath().getLastPathComponent()).getUserObject();
        if (!(o instanceof Device)) return;
        updateTable((Device)o);
    }//GEN-LAST:event_roomTreeValueChanged

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        new AboutDialog(this, true).setVisible(true);
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void addIRComMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addIRComMenuItemActionPerformed
        new NewIRCommandDialog(this, true, null).setVisible(true);
    }//GEN-LAST:event_addIRComMenuItemActionPerformed

    private void userWMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userWMenuItemActionPerformed
        new UserWindow().setVisible(true);
    }//GEN-LAST:event_userWMenuItemActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aComMenuItem;
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenuItem addIRComMenuItem;
    private javax.swing.JMenuItem addRoomMenuItem;
    private javax.swing.JTable comTable;
    private javax.swing.JMenuItem dDelMenuItem;
    private javax.swing.JPopupMenu devicePopup;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem quitMenuItem;
    private javax.swing.JTree roomTree;
    private javax.swing.JMenuItem userWMenuItem;
    private javax.swing.JTable varTable;
    // End of variables declaration//GEN-END:variables

    @Override
    public void onDetectNewDevice(UPnPRemoteDevice device) {
        if (JOptionPane.showConfirmDialog(
                this,
                "New UPnP device detected. Add this to database?",
                "IoTConnector",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION) {
            NewDeviceDialog w = new NewDeviceDialog(this, true, device);
            w.setVisible(true);
        }
    }

    @Override
    public void onDetectKnownDevice(Device device) {
        synchronized (this) {
            DefaultTreeModel model = (DefaultTreeModel)roomTree.getModel();
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();

            Enumeration allNode = root.postorderEnumeration();
            while (allNode.hasMoreElements()) {
                DefaultMutableTreeNode n = (DefaultMutableTreeNode)allNode.nextElement();
                Object o = n.getUserObject();
                if (o instanceof Device) {
                    if (device.getId().equals(((Device)o).getId())) {
                        // already added device
                        return;
                    }
                }
            }

            Enumeration c = root.children();
            while (c.hasMoreElements()) {
                DefaultMutableTreeNode n = (DefaultMutableTreeNode)c.nextElement();
                if (((Room)n.getUserObject()).getId() == device.getRoom().getId()) {
                    n.add(new DefaultMutableTreeNode(device));
                    model.reload();
                    return;
                }
            }

            DefaultMutableTreeNode node = new DefaultMutableTreeNode(device.getRoom());
            node.add(new DefaultMutableTreeNode(device));
            root.add(node);
            model.reload();
        }
    }

    @Override
    public void onFailDevice(Device device) {
        synchronized (this) {
            DefaultTreeModel model = (DefaultTreeModel)roomTree.getModel();
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();

            Enumeration allNode = root.postorderEnumeration();
            while (allNode.hasMoreElements()) {
                DefaultMutableTreeNode n = (DefaultMutableTreeNode)allNode.nextElement();
                Object o = n.getUserObject();
                if (o instanceof Device) {
                    if (((Device)o).getId() == device.getId()) {
                        n.removeFromParent();
                        break;
                    }
                }
            }

            model.reload();
        }
    }

    @Override
    public void onUpdateValue(UPnPRemoteStateVariable upprsv) {
        if (currentView == null || !upprsv.getRemoteService().getRemoteDevice().getUDN().equals(currentView.getUdn())) return;

        DefaultTableModel vtm = (DefaultTableModel)varTable.getModel();
        for (int i = 0; i < vtm.getRowCount(); i++) {
            String varName = (String)vtm.getValueAt(i, 1);
            if (upprsv.getName().equals(varName)) {
                vtm.setValueAt(upprsv.getValue(), i, 2);
                return;
            }
        }
    }

    /**
     * 部屋のツリーを更新
     */
    private void updateTree() {
        synchronized (this) {
            DefaultTreeModel model = (DefaultTreeModel)roomTree.getModel();
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
            root.removeAllChildren();

            Session s = HibernateUtil.getSessionFactory().openSession();
            Query q = s.getNamedQuery("Room.findAll");
            //FIXME: stack over flow (loop error)
            for (Room r : (java.util.List<Room>)q.list()) {
                DefaultMutableTreeNode node = new ToolTipTreeNode(r);
                for (Device d : (java.util.Set<Device>)r.getDevices()) {
                    node.add(new ToolTipTreeNode(d, d.getUdn()));
                }

                root.add(node);
            }
            s.close();

            model.reload();

            for (int row = 0; row < roomTree.getRowCount(); row++) {
                roomTree.expandRow(row);
            }
        }
    }

    /**
     * デバイスの詳細テーブル（コマンド，センサ）を更新する
     * @param device
     */
    private void updateTable(Device device) {
        currentView = device;
        Session s = HibernateUtil.getSessionFactory().openSession();
        Device d = (Device)s.load(Device.class, device.getId());

        DefaultTableModel ctm = (DefaultTableModel)comTable.getModel();
        DefaultTableModel vtm = (DefaultTableModel)varTable.getModel();

        removeAllRow(ctm);
        removeAllRow(vtm);
        for (Command c : (Set<Command>)d.getCommands()) {
            ctm.addRow(new Object[]{
                c.getId(),
                c.getName(),
                CommandType.valueOf(c.getType()).toString(),
                c.getPower(),
                c.getAliasName(),
                c.getCommand()
            });
        }
        for (Sensor sens : (Set<Sensor>)d.getSensors()) {
            vtm.addRow(new Object[]{
                sens.getId(),
                sens.getName(),
                0.0
            });
        }

        s.close();
    }

    /**
     * 全ての行を削除する
     * @param dtm
     */
    private void removeAllRow(DefaultTableModel dtm) {
        for (int i = dtm.getRowCount() - 1; i >= 0 ; i--) {
            dtm.removeRow(i);
        }
    }

    @Override
    public void onCreate(String entityName, Object entity) {
        updateTree();
    }

    @Override
    public void onDelete(String entityName, Object entity) {
        updateTree();
    }

    @Override
    public void onUpdate(String entityName, Object entity) {
        updateTree();
    }
}
