package de.mc8051.arma3launcher.model;

import javax.swing.*;
import java.awt.*;

/**
 * Created by gurkengewuerz.de on 25.03.2020.
 */
public class ModListRenderer extends JCheckBox implements ListCellRenderer {

    public Component getListCellRendererComponent(JList list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {

        setComponentOrientation(list.getComponentOrientation());
        setFont(list.getFont());
        setBackground(list.getBackground());
        setForeground(list.getForeground());
        setSelected(isSelected);
        setEnabled(list.isEnabled());

        setText(value == null ? "" : value.toString());

        return this;
    }
}
