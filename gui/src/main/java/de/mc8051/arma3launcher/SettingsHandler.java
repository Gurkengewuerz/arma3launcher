package de.mc8051.arma3launcher;

import de.mc8051.arma3launcher.utils.Callback;
import de.mc8051.arma3launcher.utils.LangUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

/**
 * Created by gurkengewuerz.de on 24.03.2020.
 */
public class SettingsHandler {

    public static class CheckBoxListener implements ItemListener {
        private Parameter parameter;

        public CheckBoxListener(Parameter parameter) {
            this.parameter = parameter;
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            parameter.save(e.getStateChange() == ItemEvent.SELECTED);
        }
    }

    public static class ComboBoxListener implements ItemListener {

        private Parameter parameter;

        public ComboBoxListener(Parameter parameter) {
            this.parameter = parameter;
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                parameter.save(((JComboBox) e.getItemSelectable()).getSelectedIndex());
            }
        }
    }


    public static class Fileistener implements ActionListener {

        private JPanel parent;
        private Parameter parameter;
        private Callback.JFileSelectCallback check;

        public Fileistener(JPanel parent, Parameter parameter, Callback.JFileSelectCallback check) {
            this.parent = parent;
            this.parameter = parameter;
            this.check = check;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(() -> {
                JFileChooser chooser = new JFileChooser();

                chooser.setCurrentDirectory(new File("."));
                chooser.setDialogTitle(LangUtils.getInstance().getString("select_folder"));
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);

                if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
                    File path = chooser.getSelectedFile();
                    if(check.allowSelection(path)) {
                        parameter.save(path.getAbsolutePath());
                    }
                }
            });
        }
    }

    public static class SpinnerListener implements ChangeListener {

        private static long lastChange = -1;
        private Parameter parameter;

        public SpinnerListener(Parameter parameter) {
            this.parameter = parameter;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            long time = System.currentTimeMillis();
            if(lastChange == -1 || time - lastChange > 500) {
                lastChange = time;
                parameter.save(String.valueOf(((JSpinner) e.getSource()).getValue()));
            }
        }

    }


}
