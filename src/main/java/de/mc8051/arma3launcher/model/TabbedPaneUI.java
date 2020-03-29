package de.mc8051.arma3launcher.model;

import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;

/**
 * Created by gurkengewuerz.de on 28.03.2020.
 */
public class TabbedPaneUI extends BasicTabbedPaneUI {

    private final Insets borderInsets = new Insets(0, 0, 0, 0);

    @Override
    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
    }

    @Override
    protected Insets getContentBorderInsets(int tabPlacement) {
        return borderInsets;
    }

    @Override
    protected int calculateTabAreaHeight(int tab_placement, int run_count, int max_tab_height) {
        return -5;
    }
}
