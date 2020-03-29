package de.mc8051.arma3launcher.model;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by gurkengewuerz.de on 26.03.2020.
 */
public class RepositoryTreeNode extends DefaultMutableTreeNode {

    private Color labelColor = null;

    public RepositoryTreeNode(String userObject, boolean allowChildren) {
        super(userObject, allowChildren);
    }

    public RepositoryTreeNode(String userObject, Color labelColor, boolean allowChildren) {
        super(userObject, allowChildren);
        this.labelColor = labelColor;
    }

    public RepositoryTreeNode(String userObject, Color labelColor) {
        super(userObject);
        this.labelColor = labelColor;
    }

    public RepositoryTreeNode(String userObject) {
        super(userObject);
    }

    public Color getLabelColor() {
        return labelColor;
    }

    public void setLabelColor(Color labelColor) {
        this.labelColor = labelColor;
    }

    public Set<TreeNode> getAllLeafNodes() {
        Set<TreeNode> leafNodes = new HashSet<>();
        if (this.children == null) {
            leafNodes.add(this);
        } else {
            for (Object child : this.children) {
                if (child instanceof RepositoryTreeNode)
                    leafNodes.addAll(((RepositoryTreeNode) child).getAllLeafNodes());
            }
        }
        return leafNodes;
    }
}
