package iotc.gui;

import java.awt.event.MouseEvent;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * ToolTip付きTree
 * http://www.java2s.com/Code/Java/Swing-Components/ToolTipTreeExample.htm
 * @author atsushi-o
 */
public class ToolTipTree extends JTree {
    public ToolTipTree() {
        super(new javax.swing.tree.DefaultTreeModel(new ToolTipTreeNode("Root")));
        super.setToolTipText("");
    }

    @Override
    public String getToolTipText(MouseEvent evt) {
        if (getRowForLocation(evt.getX(), evt.getY()) == -1) {
            return null;
        }
        TreePath curPath = getPathForLocation(evt.getX(), evt.getY());
        return ((ToolTipTreeNode)curPath.getLastPathComponent()).getToolTipText();
    }

    /**
    * ToolTip付きTreeNode
    * @author atsushi-o
    */
    public static class ToolTipTreeNode extends DefaultMutableTreeNode {
        private String toolTipText;

        public ToolTipTreeNode() {
            this("", "");
        }
        public ToolTipTreeNode(Object userObject) {
            this(userObject, "");
        }
        public ToolTipTreeNode(Object userObject, String toolTipText) {
            super(userObject);
            this.toolTipText = toolTipText;
        }

        public String getToolTipText() {
            return this.toolTipText;
        }
        public void setToolTipText(String text) {
            this.toolTipText = text;
        }
    }
}
