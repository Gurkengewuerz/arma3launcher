package de.mc8051.arma3launcher;

import de.mc8051.arma3launcher.interfaces.Observer;
import de.mc8051.arma3launcher.model.ModListRenderer;
import de.mc8051.arma3launcher.model.PresetListRenderer;
import de.mc8051.arma3launcher.model.PresetTableModel;
import de.mc8051.arma3launcher.model.ServerTableModel;
import de.mc8051.arma3launcher.objects.Modset;
import de.mc8051.arma3launcher.objects.Server;
import de.mc8051.arma3launcher.repo.RepositoryManger;
import de.mc8051.arma3launcher.utils.Callback;
import de.mc8051.arma3launcher.utils.LangUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.text.DefaultFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Created by gurkengewuerz.de on 23.03.2020.
 */
public class LauncherGUI implements Observer {
    public JPanel mainPanel;
    private JButton settingsPanelButton;
    private JButton updatePanelButton;
    private JButton playPanelButton;
    private JLabel subtitle;
    private JLabel title;
    private JTabbedPane tabbedPane1;
    private JLabel steamStatus;
    private JLabel armaStatus;
    private JButton presetPanelButton;
    private JPanel logo;
    private JPanel presetsTab;
    private JButton playPresetButton;
    private JButton clonePresetButton;
    private JButton newPresetButtom;
    private JButton removePresetButtom;
    private JButton renamePresetButton;
    private JList presetList;
    private JList modList;
    private JPanel playTab;
    private JTable serverTable;
    private JButton playButton;
    private JPanel updateTab;
    private JPanel settingsTab;
    private JTextField settingsArmaPathText;
    private JButton settingsArmaPathBtn;
    private JComboBox settingsBehaviorStartCombo;
    private JComboBox settingsProfileCombo;
    private JComboBox settingsMallocCombo;
    private JSpinner settingsMaxMemSpinner;
    private JTextField parameterText;
    private JButton settingsModsPathBtn;
    private JTextField settingsModsPathText;
    private JCheckBox settingsShowParameterBox;
    private JCheckBox settingsCheckModsBox;
    private JComboBox settingsLanguageCombo;
    private JTextField settingsBackendText;
    private JComboBox settingsExThreadsCombo;
    private JTextField settingsWorldText;
    private JTextField settingsInitText;
    private JTextField settingsBetaText;
    private JCheckBox settingsUseSixtyFourBitBox;
    private JCheckBox settingsNoSplashBox;
    private JCheckBox settingsSkipIntroBox;
    private JCheckBox settingsNoCBBox;
    private JCheckBox settingsNoLogsBox;
    private JCheckBox settingsEnableHTBox;
    private JCheckBox settingsHugeoagesBox;
    private JCheckBox settingsNoPauseBox;
    private JCheckBox settingsShowScriptErrorsBox;
    private JCheckBox settingsFilePatchingBox;
    private JCheckBox settingsCrashDiagBox;
    private JCheckBox settingsWindowBox;
    private JSpinner settingsMaxVRamSpinner;
    private JSpinner settingsCpuCountSpinner;
    private JSpinner settingsPosXSpinner;
    private JSpinner settingsPosYSpinner;
    private JButton settingsResetDefault;
    private JScrollPane settingScrollPane;
    private JCheckBox settingsUseWorkshopBox;
    private JTree tree1;
    private JButton allesAuswählenButton;
    private JButton allesAusklappenButton;
    private JProgressBar progressBar1;
    private JButton abbrechenButton;
    private JButton überprüfenButton;
    private JProgressBar progressBar2;
    private JProgressBar progressBar3;
    private JButton downloadButton;
    private JButton abbrechenButton1;
    private JButton pauseButton;

    // TODO: Updater
    /*
        Prüfung
        In eine Liste hinzufügen wenn Datei in modset.json (Neu runterladen), nicht in modset.json (zum Löschen) oder die Größe unterschiedlich ist (Geändert)
     */

    public LauncherGUI() {
        RepositoryManger.getInstance().addObserver(this);

        tabbedPane1.setUI(new BasicTabbedPaneUI() {
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
        });

        Insets x = new Insets(5, 5, 5, 5);
        settingsPanelButton.setMargin(x);
        updatePanelButton.setMargin(x);
        playPanelButton.setMargin(x);
        presetPanelButton.setMargin(x);

        playPresetButton.setMargin(new Insets(10, 10, 10, 10));

        playPanelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabbedPane1.setSelectedIndex(0);
            }
        });

        updatePanelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabbedPane1.setSelectedIndex(1);
            }
        });

        presetPanelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabbedPane1.setSelectedIndex(2);
            }
        });

        settingsPanelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabbedPane1.setSelectedIndex(3);
            }
        });

        serverTable.setModel(new ServerTableModel());

        presetList.setModel(new PresetTableModel());
        presetList.setCellRenderer(new PresetListRenderer());
        presetList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    PresetTableModel m = (PresetTableModel) presetList.getModel();
                    Modset modset = (Modset) m.getElementAt(presetList.getSelectedIndex());
                    System.out.println(modset.getName());

                    if(modset.getType() == Modset.Type.SERVER) {
                        renamePresetButton.setEnabled(false);
                        removePresetButtom.setEnabled(false);
                    } else {
                        renamePresetButton.setEnabled(true);
                        removePresetButtom.setEnabled(true);
                    }
                    clonePresetButton.setEnabled(true);

                    updateModList(modset);
                }
            }
        });

        modList.setCellRenderer(new ModListRenderer());

        subtitle.setText(
                ArmA3Launcher.config.getString("subtitle")
                        .replace("${name}", ArmA3Launcher.CLIENT_NAME)
                        .replace("${version}", ArmA3Launcher.VERSION));

        title.setText(
                ArmA3Launcher.config.getString("title")
                        .replace("${name}", ArmA3Launcher.CLIENT_NAME)
                        .replace("${version}", ArmA3Launcher.VERSION));

        initSettings();

        settingsResetDefault.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ArmA3Launcher.user_config.remove("arma");
                    ArmA3Launcher.user_config.store();
                    initSettings();
                } catch (IOException ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
                }
            }
        });

        settingScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        RepositoryManger.getInstance().refreshMeta();
    }

    public static void infoBox(String infoMessage, String titleBar) {
        JOptionPane.showMessageDialog(null, infoMessage, "INFO: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void warnBox(String infoMessage, String titleBar) {
        JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.WARNING_MESSAGE);
    }

    public static void errorBox(String errorMessage, String titleBar) {
        JOptionPane.showMessageDialog(null, errorMessage, "ERROR: " + titleBar, JOptionPane.ERROR_MESSAGE);
    }

    public void updateLabels(boolean steamRunning, boolean armaRunning) {
        if (steamRunning) {
            steamStatus.setText(LangUtils.getInstance().getString("signed_in"));
            steamStatus.setForeground(new Color(82, 137, 74));
        } else {
            steamStatus.setText(LangUtils.getInstance().getString("closed"));
            steamStatus.setForeground(Color.RED);
        }

        if (armaRunning) {
            armaStatus.setText(LangUtils.getInstance().getString("running"));
            armaStatus.setForeground(new Color(82, 137, 74));
        } else {
            armaStatus.setText(LangUtils.getInstance().getString("closed"));
            armaStatus.setForeground(Color.red);
        }
    }

    public void techCheck() {
        // Arma Path set
        // Steam running
        // Arma not running
    }

    public boolean checkArmaPath(String path) {
        if (settingsArmaPathText.getText().isEmpty()) return false;
        File dir = new File(settingsArmaPathText.getText());

        ArrayList<String> search = new ArrayList<String>(Arrays.asList("arma3.exe", "steam.dll"));
        File[] listOfFiles = dir.listFiles();

        try {
            for (File file : listOfFiles) {
                if (search.isEmpty()) return true;
                if (file.isFile()) {
                    search.remove(file.getName().toLowerCase());
                }
            }
        } catch (NullPointerException ex) {
            return false;
        }
        return false;
    }

    public void initSettings() {

        settingsBackendText.setText(ArmA3Launcher.config.getString("sync.url"));


        // -------------------------------- PROFILE --------------------------------

        File file = new File((new JFileChooser().getFileSystemView().getDefaultDirectory().toString()) + File.separator + "Arma 3 - Other Profiles");
        String[] directories = file.list((current, name) -> new File(current, name).isDirectory());

        directories = Stream.concat(Arrays.stream(new String[]{""}), Arrays.stream(directories == null ? new String[]{} : directories)).toArray(String[]::new);

        String[] readableDirectories = new String[directories.length];
        for (int i = 0; i < directories.length; i++) {
            try {
                readableDirectories[i] = URLDecoder.decode(directories[i], StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                readableDirectories[i] = directories[i];
            }
        }

        ((JComboBox<String>) settingsProfileCombo).setModel(new DefaultComboBoxModel<>(readableDirectories));


        initFolderChooser(settingsArmaPathText, settingsArmaPathBtn, "armaPath", Parameter.ParameterType.CLIENT, new Callback.JFileSelectCallback() {
            @Override
            public boolean allowSelection(File path) {
                String sPath = path.getAbsolutePath();
                if (!checkArmaPath(sPath)) {
                    SwingUtilities.invokeLater(() -> warnBox(LangUtils.getInstance().getString("not_arma_dir_msg"), LangUtils.getInstance().getString("not_arma_dir")));
                    return false;
                }
                settingsArmaPathText.setText(sPath);
                return true;
            }
        });

        initFolderChooser(settingsModsPathText, settingsModsPathBtn, "modPath", Parameter.ParameterType.CLIENT, new Callback.JFileSelectCallback() {
            @Override
            public boolean allowSelection(File path) {
                settingsModsPathText.setText(path.getAbsolutePath());
                return true;
            }
        });

        // -------------------------------- COMBO BOXES --------------------------------

        initComboBox(settingsLanguageCombo, "language", Parameter.ParameterType.CLIENT, new String[]{"system", "en_US", "de_DE"});
        initComboBox(settingsBehaviorStartCombo, "behaviourAfterStart", Parameter.ParameterType.CLIENT, new String[]{"nothing", "minimize", "exit"});

        initComboBox(settingsProfileCombo, "Profile", Parameter.ParameterType.ARMA, directories);
        initComboBox(settingsExThreadsCombo, "ExThreads", Parameter.ParameterType.ARMA, new String[]{"", "3", "7"});
        initComboBox(settingsMallocCombo, "Malloc", Parameter.ParameterType.ARMA, new String[]{"", "tbb4malloc_bi", "jemalloc_bi", "system"});


        // -------------------------------- CHECK BOXES --------------------------------

        initCheckBox(settingsShowParameterBox, "ShowStartParameter", Parameter.ParameterType.CLIENT);
        settingsShowParameterBox.addItemListener(e -> parameterText.setVisible(e.getStateChange() == ItemEvent.SELECTED));
        initCheckBox(settingsCheckModsBox, "CheckModset", Parameter.ParameterType.CLIENT);

        initCheckBox(settingsUseWorkshopBox, "UseWorkshop", Parameter.ParameterType.CLIENT);
        settingsUseWorkshopBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                SwingUtilities.invokeLater(() -> warnBox(LangUtils.getInstance().getString("warning_workshop"), LangUtils.getInstance().getString("warning")));
            }
        });

        initCheckBox(settingsUseSixtyFourBitBox, "Use64BitClient", Parameter.ParameterType.ARMA);
        initCheckBox(settingsNoSplashBox, "NoSplash", Parameter.ParameterType.ARMA);
        initCheckBox(settingsSkipIntroBox, "SkipIntro", Parameter.ParameterType.ARMA);
        initCheckBox(settingsNoCBBox, "NoCB", Parameter.ParameterType.ARMA);
        initCheckBox(settingsNoLogsBox, "NoLogs", Parameter.ParameterType.ARMA);
        initCheckBox(settingsEnableHTBox, "EnableHT", Parameter.ParameterType.ARMA);
        initCheckBox(settingsHugeoagesBox, "Hugepages", Parameter.ParameterType.ARMA);
        initCheckBox(settingsNoPauseBox, "NoPause", Parameter.ParameterType.ARMA);
        initCheckBox(settingsShowScriptErrorsBox, "ShowScriptErrors", Parameter.ParameterType.ARMA);
        initCheckBox(settingsFilePatchingBox, "FilePatching", Parameter.ParameterType.ARMA);
        initCheckBox(settingsCrashDiagBox, "CrashDiag", Parameter.ParameterType.ARMA);
        initCheckBox(settingsWindowBox, "Window", Parameter.ParameterType.ARMA);


        // -------------------------------- SPINNER --------------------------------

        com.sun.management.OperatingSystemMXBean mxbean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        int memorySize = (int) (mxbean.getTotalPhysicalMemorySize() / 1024);

        initSpinner(settingsMaxMemSpinner, "MaxMem", Parameter.ParameterType.ARMA, -1, memorySize);
        initSpinner(settingsMaxVRamSpinner, "MaxVRAM", Parameter.ParameterType.ARMA, -1, 99999);
        initSpinner(settingsCpuCountSpinner, "CpuCount", Parameter.ParameterType.ARMA, 0, Runtime.getRuntime().availableProcessors());
        initSpinner(settingsPosXSpinner, "PosX", Parameter.ParameterType.ARMA, -1, 99999);
        initSpinner(settingsPosYSpinner, "PosY", Parameter.ParameterType.ARMA, -1, 99999);

        // -------------------------------- -------------------------------- --------------------------------
    }

    private void initCheckBox(JCheckBox cb, String parameter, Parameter.ParameterType pType) {
        Parameter<Boolean> paraObj = new Parameter<>(parameter, pType, Boolean.class);
        cb.setSelected(paraObj.getValue());
        cb.addItemListener(new SettingsHandler.CheckBoxListener(paraObj));
    }

    private void initComboBox(JComboBox<String> cb, String parameter, Parameter.ParameterType pType, String[] values) {
        Parameter<String> paraObj = new Parameter<>(parameter, pType, String.class, values);
        cb.setSelectedIndex(paraObj.getIndex());
        if (cb.getItemListeners().length == 0) cb.addItemListener(new SettingsHandler.ComboBoxListener(paraObj));
    }

    private void initFolderChooser(JTextField showText, JButton actionButton, String parameter, Parameter.ParameterType pType, Callback.JFileSelectCallback check) {
        Parameter<String> paraObj = new Parameter<>(parameter, pType, String.class);
        showText.setText(paraObj.getValue());
        if (actionButton.getActionListeners().length == 0)
            actionButton.addActionListener(new SettingsHandler.Fileistener(mainPanel, paraObj, check));
    }

    public void initSpinner(JSpinner spinner, String parameter, Parameter.ParameterType pType, int min, int max) {
        Parameter<String> paraObj = new Parameter<>(parameter, pType, String.class);

        SpinnerNumberModel RAMModel = new SpinnerNumberModel(Integer.parseInt(paraObj.getValue()), min, max, 1);
        spinner.setModel(RAMModel);
        JComponent comp = spinner.getEditor();
        JFormattedTextField field = (JFormattedTextField) comp.getComponent(0);
        DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
        formatter.setCommitsOnValidEdit(false);
        spinner.addChangeListener(new SettingsHandler.SpinnerListener(paraObj));
    }

    public void updateModList(Modset modset) {
        ListModel<String> model = (ListModel)modList.getModel();
        // TODO: RepositoryManger.downloadModlist
        // TODO: Show All Mods (keyname)
        // TODO: Show not installed Mods with red font
        // TODO: Select Mod if in modset.Mods
        // TODO: Custom Checkbox Render
        // TODO: Wenn modset.type == Server alle Checkboxen deaktivieren!
    }

    @Override
    public void update(Object o) {
        String s = String.valueOf(o);

        if (s.equals("refreshMeta")) {
            SwingUtilities.invokeLater(() -> {
                ServerTableModel model = (ServerTableModel) serverTable.getModel();

                Server.SERVER_LIST.forEach((name, server) -> model.add(server));
            });

            SwingUtilities.invokeLater(() -> {
                PresetTableModel model = (PresetTableModel) presetList.getModel();
                model.clear();

                model.add(new Modset("--Server", Modset.Type.CLIENT, null, false));

                Modset.MODSET_LIST.forEach((name, set) -> {
                    model.add(set);
                });
            });
        }
    }
}
