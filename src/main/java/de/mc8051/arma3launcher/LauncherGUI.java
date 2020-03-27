package de.mc8051.arma3launcher;

import de.mc8051.arma3launcher.interfaces.Observer;
import de.mc8051.arma3launcher.model.JCheckBoxTree;
import de.mc8051.arma3launcher.model.ModListRenderer;
import de.mc8051.arma3launcher.model.PresetListRenderer;
import de.mc8051.arma3launcher.model.PresetTableModel;
import de.mc8051.arma3launcher.model.RepositoryTreeNode;
import de.mc8051.arma3launcher.model.ServerTableModel;
import de.mc8051.arma3launcher.objects.AbstractMod;
import de.mc8051.arma3launcher.objects.Mod;
import de.mc8051.arma3launcher.objects.ModFile;
import de.mc8051.arma3launcher.objects.Modset;
import de.mc8051.arma3launcher.objects.Server;
import de.mc8051.arma3launcher.repo.FileChecker;
import de.mc8051.arma3launcher.repo.RepositoryManger;
import de.mc8051.arma3launcher.repo.SyncList;
import de.mc8051.arma3launcher.repo.Syncer;
import de.mc8051.arma3launcher.steam.SteamTimer;
import de.mc8051.arma3launcher.utils.Callback;
import de.mc8051.arma3launcher.utils.Humanize;
import de.mc8051.arma3launcher.utils.LangUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.text.DefaultFormatter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
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
    public JProgressBar syncDownloadProgress;
    public JProgressBar syncFileProgress;
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
    private JLabel syncChangedFileSizeLabel;
    private JLabel syncFileCountLabel;
    public JLabel syncDownloadedLabel;
    public JLabel syncDownloadSpeedLabel;
    private JSplitPane splitView;

    private JCheckBoxTree repoTree;
    private FileChecker fileChecker;
    private Syncer syncer;
    private SyncList lastSynclist;

    public LauncherGUI() {
        fileChecker = new FileChecker(syncCheckProgress);
        syncer = new Syncer(this);

        RepositoryManger.getInstance().addObserver(this);
        SteamTimer.addObserver(this);
        fileChecker.addObserver(this);
        syncer.addObserver(this);

        updateTreePanel.remove(tree1);

        repoTree = new JCheckBoxTree();
        updateTreePanel.add(repoTree, BorderLayout.CENTER);

        DefaultTreeModel model = (DefaultTreeModel) repoTree.getModel();
        model.setRoot(new RepositoryTreeNode("Repository"));

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
            try {
                Thread.sleep(750);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            RepositoryManger.getInstance().refreshModset();
        }).start();

        syncCheckButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                syncCheckButton.setEnabled(false);
                syncCheckAbortButton.setEnabled(true);
                syncCheckStatusLabel.setText("Running!");
                new Thread(() -> fileChecker.check()).start();

                repoTree.setCheckboxesEnabled(false);
                repoTree.setCheckboxesChecked(false);
            }
        });

        syncCheckAbortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChecker.stop();
            }
        });

        syncDownloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(lastSynclist == null) return;
                new Thread(() -> syncer.sync(lastSynclist.clone())).start();
            }
        });

        syncDownloadAbortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                syncer.stop();
            }
        });

        syncPauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                syncer.setPaused(!syncer.isPaused());
                syncPauseButton.setEnabled(false);
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
        splitView.setDividerLocation(-1);
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
                if (sPath.equalsIgnoreCase(modPath)) {
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
                if (sPath.equalsIgnoreCase(armaPath)) {
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

    public SyncList getSyncList() {
        SyncList synclist = new SyncList();

        DefaultTreeModel model = (DefaultTreeModel) repoTree.getModel();
        RepositoryTreeNode root = (RepositoryTreeNode) model.getRoot();
        for (TreeNode leaf : root.getAllLeafNodes()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) leaf;
            TreeNode[] path = node.getPath();
            boolean isSelected = repoTree.isSelected(new TreePath(path));
            if (!isSelected) continue;

            ArrayList<String> treePathList = new ArrayList<>();
            for (int i = 2; i < path.length; i++) {
                treePathList.add(String.valueOf(((DefaultMutableTreeNode) path[i]).getUserObject()));
            }
            String treePath = String.join("/", treePathList);
            String modname = String.valueOf(((DefaultMutableTreeNode) path[1]).getUserObject());

            if (fileChecker.getChanged().containsKey(modname)) {
                for (ModFile modFile : fileChecker.getChanged().get(modname)) {
                    if (String.join("/", modFile.getPath()).equals(treePath)) {
                        synclist.add(modFile);
                        break;
                    }
                }
            }

            if (fileChecker.getAdded().containsKey(modname)) {
                for (ModFile modFile : fileChecker.getAdded().get(modname)) {
                    if (String.join("/", modFile.getPath()).equals(treePath)) {
                        synclist.add(modFile);
                        break;
                    }
                }
            }
        }
        synclist.setDeleted(fileChecker.getDeleted());

        return synclist;
    }

    public void updateModList(Modset modset) {
        ListModel<String> model = (ListModel) modList.getModel();
        // TODO: Show All Mods (keyname)
        //  Show not installed Mods with red font
        //  Select Mod if in modset.Mods
        //  Custom Checkbox Render
        //  Wenn modset.type == Server alle Checkboxen deaktivieren!
        //  Show hint that server modsets cant be edited
    }

    public void updateRepoTree() {
        expandAllButton.setEnabled(false);
        collapseAllButton.setEnabled(false);

        DefaultTreeModel model = (DefaultTreeModel) repoTree.getModel();
        RepositoryTreeNode root = (RepositoryTreeNode) model.getRoot();
        root.removeAllChildren();

        for (AbstractMod abstractMod : RepositoryManger.MOD_LIST) {
            if (abstractMod instanceof Mod) {
                // Whole Folder
                Mod m = (Mod) abstractMod;
                RepositoryTreeNode modFolder = new RepositoryTreeNode(m.getName(), true);
                model.insertNodeInto(modFolder, root, root.getChildCount());

                for (ModFile modfile : m.getFiles()) {

                    RepositoryTreeNode lastNode = modFolder;
                    ArrayList<String> path = modfile.getPath();

                    for (int i = 0; i < path.size() -1; i++) {
                        boolean found = false;

                        for (int j = 0; j < lastNode.getChildCount(); j++) {
                            RepositoryTreeNode temp = (RepositoryTreeNode) lastNode.getChildAt(j);
                            if (temp.getUserObject().equals(path.get(i))) {
                                found = true;
                                lastNode = temp;
                                break;
                            }
                        }

                        if (!found) {
                            RepositoryTreeNode temp = new RepositoryTreeNode(path.get(i));
                            model.insertNodeInto(temp, lastNode, lastNode.getChildCount());
                            lastNode = temp;
                        }
                    }
                    model.insertNodeInto(new RepositoryTreeNode(modfile.getName(), getNodeColor(m.getName(), modfile)), lastNode, lastNode.getChildCount());
                }
                sort(modFolder);
            } else if (abstractMod instanceof ModFile) {
                // Just a Single FIle
                ModFile m = (ModFile) abstractMod;
                model.insertNodeInto(new RepositoryTreeNode(m.getName(), getNodeColor(m.getName(), m), false), root, root.getChildCount());
            }
        }

        sort(root);
        setParentColor(root);

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

        repoTree.addCheckChangeEventListener(new JCheckBoxTree.CheckChangeEventListener() {
            @Override
            public void checkStateChanged(JCheckBoxTree.CheckChangeEvent event) {
                lastSynclist = getSyncList();
                if (lastSynclist.getSize() != 0)
                    syncSizeLabel.setText(Humanize.binaryPrefix(lastSynclist.getSize()));
                else syncSizeLabel.setText("0.0 B");
                if (lastSynclist.getCount() != 0) {
                    syncDownloadButton.setEnabled(true);
                    syncFileCountLabel.setText("" + lastSynclist.getCount());
                } else {
                    syncDownloadButton.setEnabled(false);
                    syncFileCountLabel.setText("");
                }
            }
        });

        expandAllButton.setEnabled(true);
        collapseAllButton.setEnabled(true);
    }

    public Color getNodeColor(String mod, ModFile mf) {
        if (fileChecker.getAdded().containsKey(mod)) {
            ArrayList<ModFile> mfList = fileChecker.getAdded().get(mod);
            for (ModFile modFile : mfList) {
                if (modFile.getLocaleFile().getPath().equals(mf.getLocaleFile().getPath())) return Color.RED;
            }
        }

        if (fileChecker.getChanged().containsKey(mod)) {
            ArrayList<ModFile> mfList = fileChecker.getChanged().get(mod);
            for (ModFile modFile : mfList) {
                if (modFile.getLocaleFile().getPath().equals(mf.getLocaleFile().getPath())) return Color.ORANGE;
            }
        }

        return null;
    }

    public void setParentColor(RepositoryTreeNode node) {
        for (TreeNode leaf : node.getAllLeafNodes()) {
            if (!(leaf instanceof RepositoryTreeNode)) continue;
            RepositoryTreeNode mLeaf = (RepositoryTreeNode) leaf;
            TreeNode[] path = mLeaf.getPath();

            if (mLeaf.getLabelColor() == null) continue;
            for (int i = 0; i < path.length - 1; i++) {
                if (!(path[i] instanceof RepositoryTreeNode)) continue;
                RepositoryTreeNode parent = (RepositoryTreeNode) path[i];
                if (parent.getLabelColor() == mLeaf.getLabelColor()) continue;
                if (parent.getLabelColor() == Color.RED) continue;
                parent.setLabelColor(mLeaf.getLabelColor());
            }
        }
    }

    public RepositoryTreeNode sort(RepositoryTreeNode node) {

        //sort alphabetically
        for (int i = 0; i < node.getChildCount() - 1; i++) {
            RepositoryTreeNode child = (RepositoryTreeNode) node.getChildAt(i);
            String nt = child.getUserObject().toString();

            for (int j = i + 1; j <= node.getChildCount() - 1; j++) {
                RepositoryTreeNode prevNode = (RepositoryTreeNode) node.getChildAt(j);
                String np = prevNode.getUserObject().toString();

                if (nt.compareToIgnoreCase(np) > 0) {
                    node.insert(child, j);
                    node.insert(prevNode, i);
                }
            }
            if (child.getChildCount() > 0) {
                sort(child);
            }
        }

        //put folders first - normal on Windows and some flavors of Linux but not on Mac OS X.
        for (int i = 0; i < node.getChildCount() - 1; i++) {
            RepositoryTreeNode child = (RepositoryTreeNode) node.getChildAt(i);
            for (int j = i + 1; j <= node.getChildCount() - 1; j++) {
                RepositoryTreeNode prevNode = (RepositoryTreeNode) node.getChildAt(j);

                if (!prevNode.isLeaf() && child.isLeaf()) {
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
        } else if (s.equals("fileChecker")) {
            syncCheckButton.setEnabled(true);
            syncCheckAbortButton.setEnabled(false);
            syncCheckStatusLabel.setText("Finished!");
            updateRepoTree();

            repoTree.setCheckboxesEnabled(true);
            syncDownloadButton.setEnabled(true);
            syncAddedFilesLabel.setText(String.valueOf(fileChecker.getAddedCount()));
            syncChangedFilesLabel.setText(String.valueOf(fileChecker.getChangedCount()));
            syncDeletedFilesLabel.setText(String.valueOf(fileChecker.getDeletedCount()));

            syncDownloadAbortButton.setEnabled(false);
            syncDownloadButton.setEnabled(true);
            syncPauseButton.setEnabled(false);

            syncChangedFileSizeLabel.setText(Humanize.binaryPrefix(fileChecker.getSize()));
        } else if (s.equals("fileCheckerStopped")) {
            syncCheckButton.setEnabled(true);
            syncCheckAbortButton.setEnabled(false);
            syncCheckProgress.setValue(0);
            syncCheckStatusLabel.setText("Failed!");
            repoTree.setCheckboxesEnabled(false);

            syncDownloadAbortButton.setEnabled(false);
            syncDownloadButton.setEnabled(false);
            syncPauseButton.setEnabled(false);

            repoTree.setCheckboxesChecked(false);

            syncAddedFilesLabel.setText("" + 0);
            syncChangedFilesLabel.setText("" + 0);
            syncDeletedFilesLabel.setText("" + 0);

            syncChangedFileSizeLabel.setText("0.0 B");
        } else if (s.equals("syncStopped")) {
            new Thread(() -> fileChecker.check()).start();
        } else if (s.equals("syncComplete")) {
            new Thread(() -> fileChecker.check()).start();
        } else if (s.equals("syncContinue")) {
            syncDownloadAbortButton.setEnabled(true);
            syncPauseButton.setEnabled(true);
            syncPauseButton.setText(LangUtils.getInstance().getString("pause"));
            syncDownloadButton.setEnabled(false);
        } else if (s.equals("syncPaused")) {
            syncDownloadAbortButton.setEnabled(true);
            syncPauseButton.setEnabled(true);
            syncPauseButton.setText(LangUtils.getInstance().getString("resume"));
            syncDownloadButton.setEnabled(false);
        }
    }
}
