package de.mc8051.arma3launcher;

import de.mc8051.arma3launcher.interfaces.Observer;
import de.mc8051.arma3launcher.model.JCheckBoxTree;
import de.mc8051.arma3launcher.model.ModListRenderer;
import de.mc8051.arma3launcher.model.PresetListRenderer;
import de.mc8051.arma3launcher.model.PresetTableModel;
import de.mc8051.arma3launcher.model.ServerTableModel;
import de.mc8051.arma3launcher.objects.AbstractMod;
import de.mc8051.arma3launcher.objects.Mod;
import de.mc8051.arma3launcher.objects.ModFile;
import de.mc8051.arma3launcher.objects.Modset;
import de.mc8051.arma3launcher.objects.Server;
import de.mc8051.arma3launcher.repo.FileChecker;
import de.mc8051.arma3launcher.repo.RepositoryManger;
import de.mc8051.arma3launcher.steam.SteamTimer;
import de.mc8051.arma3launcher.utils.Callback;
import de.mc8051.arma3launcher.utils.LangUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.text.DefaultFormatter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
    private JButton expandAllButton;
    private JProgressBar syncCheckProgress;
    private JButton syncCheckAbortButton;
    private JButton syncCheckButton;
    private JProgressBar progressBar2;
    private JProgressBar progressBar3;
    private JButton syncDownloadButton;
    private JButton syncDownloadAbortButton;
    private JButton syncPauseButton;
    private JComboBox comboBox1;
    private JButton refreshRepoButton;
    private JPanel updateTreePanel;
    private JScrollPane updateTreeScrolPane;
    private JButton collapseAllButton;
    private JLabel syncCheckStatusLabel;
    private JLabel syncDeletedFilesLabel;
    private JLabel syncAddedFilesLabel;
    private JLabel syncChangedFilesLabel;
    private JLabel syncSizeLabel;

    private JCheckBoxTree repoTree;
    private FileChecker fileChecker;

    // TODO: Updater
    /*
        Prüfung
        In eine Liste hinzufügen wenn Datei in modset.json (Neu runterladen), nicht in modset.json (zum Löschen) oder die Größe unterschiedlich ist (Geändert)
        Checkboxen beim Syncronisieren deaktivieren
     */

    public LauncherGUI() {
        fileChecker = new FileChecker(syncCheckProgress);

        RepositoryManger.getInstance().addObserver(this);
        SteamTimer.addObserver(this);
        fileChecker.addObserver(this);

        updateTreePanel.remove(tree1);

        repoTree = new JCheckBoxTree();
        updateTreePanel.add(repoTree, BorderLayout.CENTER);

        updateTreePanel.revalidate();
        updateTreePanel.repaint();

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

                    if (modset.getType() == Modset.Type.SERVER) {
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
        updateTreeScrolPane.getVerticalScrollBar().setUnitIncrement(16);

        refreshRepoButton.addActionListener(e -> RepositoryManger.getInstance().refreshModset());
        expandAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repoTree.expandAllNodes();
            }
        });
        collapseAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repoTree.collapseAllNodes();
            }
        });

        new Thread(() -> {
            RepositoryManger.getInstance().refreshMeta();
            RepositoryManger.getInstance().refreshModset();
        }).start();

        syncCheckButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                syncCheckButton.setEnabled(false);
                syncCheckAbortButton.setEnabled(true);
                syncCheckStatusLabel.setText("Running!");
                new Thread(() -> fileChecker.check()).start();

                // TODO: disable JTree Checkboxes
            }
        });

        syncCheckAbortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChecker.stop();
            }
        });
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
        boolean pathSet = ArmA3Launcher.user_config.get("client").containsKey("armaPath") && ArmA3Launcher.user_config.get("client").containsKey("modPath");
        if (SteamTimer.arma_running) {
            playButton.setEnabled(false);
            playPresetButton.setEnabled(false);
            syncCheckButton.setEnabled(false);
            refreshRepoButton.setEnabled(false);

            playButton.setToolTipText(LangUtils.getInstance().getString("arma_running"));
            playPresetButton.setToolTipText(LangUtils.getInstance().getString("arma_running"));
            syncCheckButton.setToolTipText(LangUtils.getInstance().getString("arma_running"));
        } else {
            if (SteamTimer.steam_running) {
                if (pathSet) {
                    playButton.setEnabled(true);
                    playPresetButton.setEnabled(true);

                    playButton.setToolTipText(null);
                    playPresetButton.setToolTipText(null);
                } else {
                    playButton.setEnabled(false);
                    playPresetButton.setEnabled(false);

                    playButton.setToolTipText(LangUtils.getInstance().getString("path_not_set"));
                    playPresetButton.setToolTipText(LangUtils.getInstance().getString("path_not_set"));
                }
            } else {
                playButton.setEnabled(false);
                playPresetButton.setEnabled(false);

                playButton.setToolTipText(LangUtils.getInstance().getString("steam_not_running"));
                playPresetButton.setToolTipText(LangUtils.getInstance().getString("steam_not_running"));
            }

            if (pathSet) {
                syncCheckButton.setEnabled(true);
                refreshRepoButton.setEnabled(true);

                syncCheckButton.setToolTipText(null);
                refreshRepoButton.setToolTipText(null);
            } else {
                syncCheckButton.setEnabled(true);
                refreshRepoButton.setEnabled(true);

                syncCheckButton.setToolTipText(LangUtils.getInstance().getString("path_not_set"));
                refreshRepoButton.setToolTipText(LangUtils.getInstance().getString("path_not_set"));
            }
        }
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

                String modPath = ArmA3Launcher.user_config.get("client", "modPath");
                if(sPath.equalsIgnoreCase(modPath)) {
                    SwingUtilities.invokeLater(() -> errorBox(LangUtils.getInstance().getString("same_mod_arma_dir_msg"), LangUtils.getInstance().getString("same_mod_arma_dir")));
                    return false;
                }

                settingsArmaPathText.setText(sPath);
                techCheck();
                return true;
            }
        });

        initFolderChooser(settingsModsPathText, settingsModsPathBtn, "modPath", Parameter.ParameterType.CLIENT, new Callback.JFileSelectCallback() {
            @Override
            public boolean allowSelection(File path) {
                String sPath = path.getAbsolutePath();

                String armaPath = ArmA3Launcher.user_config.get("client", "armaPath");
                if(sPath.equalsIgnoreCase(armaPath)) {
                    SwingUtilities.invokeLater(() -> errorBox(LangUtils.getInstance().getString("same_mod_arma_dir_msg"), LangUtils.getInstance().getString("same_mod_arma_dir")));
                    return false;
                }

                settingsModsPathText.setText(sPath);
                RepositoryManger.getInstance().refreshModset();
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

    public ArrayList<AbstractMod> getSyncList() {
        ArrayList<AbstractMod> modList = new ArrayList<>();

        HashMap<String, ArrayList<String>> tempMap = new HashMap<>();
        for (TreePath checkedPath : repoTree.getCheckedPaths()) {
            DefaultMutableTreeNode tn = (DefaultMutableTreeNode)checkedPath.getLastPathComponent();

            if(tn.getChildCount() > 0) continue;
            Object[] path = checkedPath.getPath();
            DefaultMutableTreeNode[] modifiedArray = Arrays.stream(Arrays.copyOfRange(path, 1, path.length)).toArray(DefaultMutableTreeNode[]::new);

            ArrayList<String> strings = new ArrayList<>();
            if(tempMap.containsKey(String.valueOf(modifiedArray[0].getUserObject()))) {
                strings = tempMap.get(String.valueOf(modifiedArray[0].getUserObject()));
            }

            String modPath = "";
            for (int i = 1; i < modifiedArray.length; i++) {
                modPath += String.valueOf(modifiedArray[i].getUserObject()) + "/";
            }
            modPath = modPath.isEmpty() ? "" : modPath.substring(0, modPath.length() - 1);
            strings.add(modPath);

            tempMap.put((String) modifiedArray[0].getUserObject(), strings);
        }

        for (Map.Entry<String, ArrayList<String>> entry : tempMap.entrySet()) {
            String modS = entry.getKey();
            ArrayList<String> modlistS = entry.getValue();

            if(modlistS.isEmpty()) {
                for (AbstractMod abstractMod : RepositoryManger.MOD_LIST) {
                    if (abstractMod.getName().equals(modS)) {
                        modList.add(abstractMod);
                        break;
                    }
                }
            } else {
                for (AbstractMod abstractMod : RepositoryManger.MOD_LIST) {
                    if (abstractMod.getName().equals(modS)) {
                        if(!(abstractMod instanceof Mod)) continue;
                        Mod m = ((Mod) abstractMod).clone();

                        for (int i = 0; i < m.getFiles().size(); i++) {
                            boolean found = false;
                            for (String pathS : modlistS) {
                                if(m.getFiles().get(i).getModfileString().equals(pathS)) {
                                    found = true;
                                }
                            }

                            if(!found) {
                                m.getFiles().remove(i);
                            }
                        }

                        modList.add(m);
                    }
                }
            }
        }

        return modList;
    }

    public void updateModList(Modset modset) {
        ListModel<String> model = (ListModel) modList.getModel();
        // TODO: Show All Mods (keyname)
        // TODO: Show not installed Mods with red font
        // TODO: Select Mod if in modset.Mods
        // TODO: Custom Checkbox Render
        // TODO: Wenn modset.type == Server alle Checkboxen deaktivieren!
    }

    public void updateRepoTree() {
        expandAllButton.setEnabled(false);
        collapseAllButton.setEnabled(false);

        DefaultTreeModel model = (DefaultTreeModel) repoTree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.setUserObject("Repository");
        root.removeAllChildren();

        for (AbstractMod abstractMod : RepositoryManger.MOD_LIST) {
            if (abstractMod instanceof Mod) {
                // Whole Folder
                // TODO: Recursives Ordner Parsen und einzelne Treenodes erstellen
                Mod m = (Mod) abstractMod;
                DefaultMutableTreeNode modFolder = new DefaultMutableTreeNode(m.getName(), true);
                model.insertNodeInto(modFolder, root, root.getChildCount());

                for (ModFile modfile : m.getFiles()) {

                    DefaultMutableTreeNode lastNode = modFolder;
                    ArrayList<String> path = modfile.getPath();

                    for (int i = 0; i < path.size(); i++) {
                        boolean found = false;

                        for (int j = 0; j < lastNode.getChildCount(); j++) {
                            DefaultMutableTreeNode temp = (DefaultMutableTreeNode) lastNode.getChildAt(j);
                            if (temp.getUserObject().equals(path.get(i))) {
                                found = true;
                                lastNode = temp;
                                break;
                            }
                        }

                        if (!found) {
                            DefaultMutableTreeNode temp = new DefaultMutableTreeNode(path.get(i));
                            model.insertNodeInto(temp, lastNode, lastNode.getChildCount());
                            lastNode = temp;
                        }
                    }

                    model.insertNodeInto(new DefaultMutableTreeNode(modfile.getName()), lastNode, lastNode.getChildCount());
                }
                sort(modFolder);
            } else if (abstractMod instanceof ModFile) {
                // Just a Single FIle
                ModFile m = (ModFile) abstractMod;
                model.insertNodeInto(new DefaultMutableTreeNode(m.getName(), false), root, root.getChildCount());
            }
        }

        sort(root);

        repoTree.clearCheckChangeEventListeners();

        repoTree.resetCheckingState();

        SwingUtilities.invokeLater(() -> {
            model.nodeChanged(root);
            model.reload();
            repoTree.revalidate();
            repoTree.repaint();
            updateTreePanel.revalidate();
            updateTreePanel.repaint();
        });

        expandAllButton.setEnabled(true);
        collapseAllButton.setEnabled(true);
    }

    public DefaultMutableTreeNode sort(DefaultMutableTreeNode node) {

        //sort alphabetically
        for(int i = 0; i < node.getChildCount() - 1; i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
            String nt = child.getUserObject().toString();

            for(int j = i + 1; j <= node.getChildCount() - 1; j++) {
                DefaultMutableTreeNode prevNode = (DefaultMutableTreeNode) node.getChildAt(j);
                String np = prevNode.getUserObject().toString();

                if(nt.compareToIgnoreCase(np) > 0) {
                    node.insert(child, j);
                    node.insert(prevNode, i);
                }
            }
            if(child.getChildCount() > 0) {
                sort(child);
            }
        }

        //put folders first - normal on Windows and some flavors of Linux but not on Mac OS X.
        for(int i = 0; i < node.getChildCount() - 1; i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
            for(int j = i + 1; j <= node.getChildCount() - 1; j++) {
                DefaultMutableTreeNode prevNode = (DefaultMutableTreeNode) node.getChildAt(j);

                if(!prevNode.isLeaf() && child.isLeaf()) {
                    node.insert(child, j);
                    node.insert(prevNode, i);
                }
            }
        }

        return node;

    }


    @Override
    public void update(String s) {
        System.out.println(s);
        if (s.equals(RepositoryManger.Type.METADATA.toString())) {
            switch (RepositoryManger.getInstance().getStatus(RepositoryManger.Type.METADATA)) {
                case ERROR:
                    errorBox("Metadata download failed. Is the server availaible? Do you have an active internet connection?", "Download failed");
                    System.exit(1);
                    break;

                case FINNISHED:
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
                    break;
            }
        } else if (s.equals("steamtimer")) {
            SwingUtilities.invokeLater(() -> {
                updateLabels(SteamTimer.steam_running, SteamTimer.arma_running);
                techCheck();
            });
        } else if (s.equals(RepositoryManger.Type.MODSET.toString())) {
            switch (RepositoryManger.getInstance().getStatus(RepositoryManger.Type.METADATA)) {
                case FINNISHED:
                    refreshRepoButton.setEnabled(true);
                    updateRepoTree();
                    break;

                case RUNNING:
                    refreshRepoButton.setEnabled(false);
                    break;
            }
        } else if(s.equals("fileChecker")) {
            syncCheckButton.setEnabled(true);
            syncCheckAbortButton.setEnabled(false);
            syncCheckStatusLabel.setText("Finished!");
            updateRepoTree();
            // TODO: Label einfärben
            // TODO: Enable Tree Checkboxes
            syncDownloadButton.setEnabled(true);
            syncAddedFilesLabel.setText(String.valueOf(fileChecker.getAddedCount()));
            syncChangedFilesLabel.setText(String.valueOf(fileChecker.getChangedCount()));
            syncDeletedFilesLabel.setText(String.valueOf(fileChecker.getDeletedCount()));

            syncSizeLabel.setText(String.valueOf(fileChecker.getSize())); // TODO: Make Humanreadable
        } else if (s.equals("fileCheckerStopped")) {
            syncCheckButton.setEnabled(true);
            syncCheckAbortButton.setEnabled(false);
            syncCheckProgress.setValue(0);
            syncCheckStatusLabel.setText("Failed!");

            syncAddedFilesLabel.setText("" + 0);
            syncChangedFilesLabel.setText("" + 0);
            syncDeletedFilesLabel.setText("" + 0);

            syncSizeLabel.setText("0.0 B");
        }
    }
}
