package de.mc8051.arma3launcher.model;

import de.mc8051.arma3launcher.objects.Modset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Created by gurkengewuerz.de on 25.03.2020.
 */
public class PresetListRenderer extends JLabel implements ListCellRenderer {
    JPanel separator;

    public PresetListRenderer() {
        setOpaque(true);
        setBorder(new EmptyBorder(0, 0, 0, 0));

        MatteBorder mb = new MatteBorder(1, 0, 0, 0, Color.BLACK);
        TitledBorder tb = new TitledBorder(mb, "", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION);
        separator = new JPanel();
        separator.setBorder(tb);
        separator.setPreferredSize(new Dimension(-1, 20));
    }

    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        Modset m = value == null ? new Modset("", Modset.Type.CLIENT,null, false) : (Modset) value;
        if (m.getName().startsWith("--")) {
            ((TitledBorder)separator.getBorder()).setTitle(m.getName().substring(2));
            return separator;
        }
        if (isSelected) {
            setBackground(list.getSelectionBackground());
        } else {
            setBackground(list.getBackground());
        }
        setFont(list.getFont());
        setText(m.getName());
        return this;
    }
}