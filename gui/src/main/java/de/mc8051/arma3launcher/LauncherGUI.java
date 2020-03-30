package de.mc8051.arma3launcher;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.sun.management.OperatingSystemMXBean;
import de.mc8051.arma3launcher.interfaces.Observer;
import de.mc8051.arma3launcher.model.JCheckBoxTree;
import de.mc8051.arma3launcher.model.ModListRenderer;
import de.mc8051.arma3launcher.model.MultiSelectModel;
import de.mc8051.arma3launcher.model.PresetListRenderer;
import de.mc8051.arma3launcher.model.PresetTableModel;
import de.mc8051.arma3launcher.model.RepositoryTreeNode;
import de.mc8051.arma3launcher.model.ServerTableModel;
import de.mc8051.arma3launcher.model.TabbedPaneUI;
import de.mc8051.arma3launcher.objects.AbstractMod;
import de.mc8051.arma3launcher.objects.Changelog;
import de.mc8051.arma3launcher.objects.Mod;
import de.mc8051.arma3launcher.objects.ModFile;
import de.mc8051.arma3launcher.objects.Modset;
import de.mc8051.arma3launcher.objects.Server;
import de.mc8051.arma3launcher.repo.DownloadStatus;
import de.mc8051.arma3launcher.repo.FileChecker;
import de.mc8051.arma3launcher.repo.RepositoryManger;
import de.mc8051.arma3launcher.repo.Updater;
import de.mc8051.arma3launcher.repo.Version;
import de.mc8051.arma3launcher.repo.sync.SyncList;
import de.mc8051.arma3launcher.repo.sync.Syncer;
import de.mc8051.arma3launcher.steam.SteamTimer;
import de.mc8051.arma3launcher.utils.ArmaUtils;
import de.mc8051.arma3launcher.utils.Callback;
import de.mc8051.arma3launcher.utils.Humanize;
import de.mc8051.arma3launcher.utils.ImageUtils;
import de.mc8051.arma3launcher.utils.LangUtils;
import de.mc8051.arma3launcher.utils.TaskBarUtils;
import org.json.JSONArray;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DefaultFormatter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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
    private JButton syncIntensiveCheckButton;
    public JProgressBar syncDownloadProgress;
    private JButton syncDownloadButton;
    private JButton syncDownloadAbortButton;
    private JButton syncPauseButton;
    private JComboBox syncPresetCombo;
    private JButton refreshRepoButton;
    private JPanel updateTreePanel;
    private JScrollPane updateTreeScrolPane;
    private JButton collapseAllButton;
    private JLabel syncCheckStatusLabel;
    private JLabel syncDeletedFilesLabel;
    private JLabel syncAddedFilesLabel;
    private JLabel syncChangedFilesLabel;
    public JLabel syncSizeLabel;
    private JLabel syncChangedFileSizeLabel;
    public JLabel syncFileCountLabel;
    public JLabel syncDownloadSpeedLabel;
    private JSplitPane splitView;
    public JLabel syncStatusLabel;
    private JLabel logo;
    private JLabel aboutLabel;
    private JButton changelogButton;
    private JPanel changelogTab;
    private JPanel aboutTab;
    private JTextArea changelogPane;
    private JScrollPane changelogScroll;
    private JLabel twitterIcon;
    private JLabel githubIcon;
    private JTextPane disclaimer;
    private JLabel aboutLogo;
    private JLabel aboutClient;
    private JLabel aboutProjectLabel;
    private JLabel aboutDeveloperLabel;
    private JLabel aboutCopyrightLabel;
    private JButton syncFastCheckButton;
    private JButton presetNoteButton;
    private JTextPane presetNoteTextPane;
    private JPanel presetNotePaneWrapper;
    private JPanel presetNotePane;
    private JLabel aboutUpdateLabel;
    private JButton updateButton;

    private JCheckBoxTree repoTree;
    private FileChecker fileChecker;
    private Syncer syncer;
    private SyncList lastSynclist = null;
    private Updater updater = new Updater();

    public LauncherGUI() {
        fileChecker = new FileChecker(syncCheckProgress);
        syncer = new Syncer(this);

        RepositoryManger.getInstance().addObserver(this);
        SteamTimer.addObserver(this);
        fileChecker.addObserver(this);
        syncer.addObserver(this);

        if (Parameters.ARMA_PATH.toStringParameter().getConfigValue() == null || Parameters.ARMA_PATH.toStringParameter().getConfigValue().isEmpty()) {
            final Path installationPath = ArmaUtils.getInstallationPath();
            if (installationPath != null) {
                Parameters.ARMA_PATH.toStringParameter().save(installationPath.toAbsolutePath().toString());
                Parameters.MOD_PATH.toStringParameter().save(Paths.get(
                        installationPath.toAbsolutePath().toString(), ArmA3Launcher.config.getString("name") + " Mods"
                ).toAbsolutePath().toString());
                techCheck();
            } else {
                SwingUtilities.invokeLater(() -> {
                    warnBox(LangUtils.getInstance().getString("arma_path_not_found_msg"), LangUtils.getInstance().getString("arma_path_not_found"));
                });
            }
        }

        new Thread(() -> {
            RepositoryManger.getInstance().refreshMeta();
            try {
                Thread.sleep(750);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            RepositoryManger.getInstance().refreshModset();
        }).start();

        switchTab(Tab.PLAY);

        updateTreePanel.removeAll();

        repoTree = new JCheckBoxTree();
        updateTreePanel.add(repoTree, BorderLayout.CENTER);

        DefaultTreeModel model = (DefaultTreeModel) repoTree.getModel();
        model.setRoot(new RepositoryTreeNode("Repository"));

        updateTreePanel.revalidate();
        updateTreePanel.repaint();

        tabbedPane1.setUI(new TabbedPaneUI());

        Insets x = new Insets(5, 15, 5, 0);
        settingsPanelButton.setMargin(x);
        updatePanelButton.setMargin(x);
        playPanelButton.setMargin(x);
        presetPanelButton.setMargin(x);
        changelogButton.setMargin(x);

        playPresetButton.setMargin(new Insets(10, 10, 10, 10));

        serverTable.setModel(new ServerTableModel());
        presetList.setModel(new PresetTableModel());

        presetList.setCellRenderer(new PresetListRenderer());
        modList.setCellRenderer(new ModListRenderer<String>());

        modList.setSelectionModel(new MultiSelectModel());

        subtitle.setText(
                ArmA3Launcher.config.getString("subtitle")
                        .replace("${name}", ArmA3Launcher.CLIENT_NAME)
                        .replace("${version}", ArmA3Launcher.VERSION));

        title.setText(
                ArmA3Launcher.config.getString("title")
                        .replace("${name}", ArmA3Launcher.CLIENT_NAME)
                        .replace("${version}", ArmA3Launcher.VERSION));

        initSettings();

        logo.setIcon(new ImageIcon(ImageUtils.getScaledImage(TaskBarUtils.IMAGE_LGO, 128, 128)));
        aboutLogo.setIcon(new ImageIcon(ImageUtils.getScaledImage(TaskBarUtils.IMAGE_LGO, 128, 128)));

        aboutClient.setText(ArmA3Launcher.config.getString("name") + " v" + ArmA3Launcher.VERSION);

        aboutDeveloperLabel.setText("<html><a href=''>https://gurkengewuerz.de</a></html>");
        aboutProjectLabel.setText("<html><a href=''>" + ArmA3Launcher.config.getString("social.github") + "</a></html>");

        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("disclaimer.html");
        if (resourceAsStream != null) {
            Scanner s = new Scanner(resourceAsStream).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";
            disclaimer.setText(result);
        }

        presetNoteTextPane.setHighlighter(null);
        presetNoteTextPane.getCaret().setVisible(false);
        presetNoteTextPane.setBackground(presetNotePaneWrapper.getBackground());
        presetNoteTextPane.setCaretColor(presetNoteTextPane.getBackground());

        presetNoteTextPane.setPreferredSize(new Dimension(-1, -1));

        aboutCopyrightLabel.setText(aboutCopyrightLabel.getText().replace("{year}", "" + Calendar.getInstance().get(Calendar.YEAR)));

        twitterIcon.setBorder(new EmptyBorder(2, 2, 2, 2));
        githubIcon.setBorder(new EmptyBorder(2, 2, 2, 2));

        settingScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        updateTreeScrolPane.getVerticalScrollBar().setUnitIncrement(16);
        splitView.setDividerLocation(-1);

        presetList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                PresetTableModel m = (PresetTableModel) presetList.getModel();
                Object elementAt = m.getElementAt(presetList.getSelectedIndex());
                Modset modset = (Modset) elementAt;

                if (modset.getType() == Modset.Type.SERVER || modset.getType() == Modset.Type.PLACEHOLDER) {
                    renamePresetButton.setEnabled(false);
                    removePresetButtom.setEnabled(false);
                } else {
                    renamePresetButton.setEnabled(true);
                    removePresetButtom.setEnabled(true);
                }
                clonePresetButton.setEnabled(modset.getType() != Modset.Type.PLACEHOLDER);

                updateModList(modset);
            }
        });

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


        collapseAllButton.addActionListener(e -> repoTree.collapseAllNodes());
        playPanelButton.addActionListener(e -> switchTab(Tab.PLAY));
        updatePanelButton.addActionListener(e -> switchTab(Tab.UPDATE));
        changelogButton.addActionListener(e -> switchTab(Tab.CHANGELOG));
        presetPanelButton.addActionListener(e -> switchTab(Tab.PRESET));
        settingsPanelButton.addActionListener(e -> switchTab(Tab.SETTING));

        refreshRepoButton.addActionListener(e -> RepositoryManger.getInstance().refreshModset());
        expandAllButton.addActionListener(e -> repoTree.expandAllNodes());
        syncDownloadAbortButton.addActionListener(e -> syncer.stop());
        syncCheckAbortButton.addActionListener(e -> fileChecker.stop());


        syncIntensiveCheckButton.addActionListener(e -> fileCheck(false));
        syncFastCheckButton.addActionListener(e -> fileCheck(true));

        syncDownloadButton.addActionListener(e -> {
            if (!fileChecker.isChecked()) return;
            if (lastSynclist == null) return;
            syncDownloadButton.setEnabled(false);
            syncDownloadAbortButton.setEnabled(true);
            syncPauseButton.setEnabled(true);
            syncIntensiveCheckButton.setEnabled(false);
            syncFastCheckButton.setEnabled(false);
            refreshRepoButton.setEnabled(false);
            new Thread(() -> syncer.sync(lastSynclist.clone())).start();
        });

        syncPauseButton.addActionListener(e -> {
            syncer.setPaused(!syncer.isPaused());
            syncPauseButton.setEnabled(false);
        });

        twitterIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                twitterIcon.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                twitterIcon.setBorder(new EmptyBorder(2, 2, 2, 2));
            }
        });

        githubIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                githubIcon.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                githubIcon.setBorder(new EmptyBorder(2, 2, 2, 2));
            }
        });

        aboutLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switchTab(Tab.ABOUT);
            }
        });

        twitterIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openURL(ArmA3Launcher.config.getString("social.twitter"));
            }
        });

        githubIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openURL(ArmA3Launcher.config.getString("social.github"));
            }
        });

        aboutDeveloperLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openURL("https://gurkengewuerz.de");
            }
        });

        aboutProjectLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openURL(ArmA3Launcher.config.getString("social.github"));
            }
        });

        modList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (presetList.getSelectedIndex() == -1) return;
                JList<?> list = (JList<?>) e.getSource();
                ListModel<?> model = list.getModel();

                ListSelectionModel listSelectionModel = list.getSelectionModel();

                int minSelectionIndex = listSelectionModel.getMinSelectionIndex();
                int maxSelectionIndex = listSelectionModel.getMaxSelectionIndex();

                List<String> selectedMods = new ArrayList<>();

                for (int i = minSelectionIndex; i <= maxSelectionIndex; i++) {
                    if (listSelectionModel.isSelectedIndex(i)) {
                        selectedMods.add(String.valueOf(model.getElementAt(i)));
                    }
                }

                PresetTableModel model1 = (PresetTableModel) presetList.getModel();
                if (presetList.getSelectedIndex() == -1) return;
                Object elementAt = model1.getElementAt(presetList.getSelectedIndex());
                Modset selectedModset = (Modset) elementAt;
                if (selectedModset.getType() == Modset.Type.PLACEHOLDER) return;
                selectedModset.getMods().clear();
                selectedModset.setMods(selectedMods);
                updateModsetList();
                selectedModset.save();
            }
        });

        newPresetButtom.addActionListener(e -> {
            String modname = JOptionPane.showInputDialog(null, "", LangUtils.getInstance().getString("new_modset_name"));
            if (modname.isEmpty()) return;
            if (Modset.MODSET_LIST.containsKey(modname)) {
                infoBox(LangUtils.getInstance().getString("modset_exists_msg"), LangUtils.getInstance().getString("modset_exists"));
                return;
            }

            Modset ms = new Modset(modname, new JSONArray(), Modset.Type.CLIENT);
            updateModsetList();
            ms.save();
        });

        presetNoteButton.addActionListener(e -> clonePresetButton.doClick());
        clonePresetButton.addActionListener(e -> {
            if (presetList.getSelectedIndex() == -1) return;
            String newName = JOptionPane.showInputDialog(null, "", LangUtils.getInstance().getString("new_modset_name"));
            if (newName.isEmpty()) return;
            if (Modset.MODSET_LIST.containsKey(newName)) {
                infoBox(LangUtils.getInstance().getString("modset_exists_msg"), LangUtils.getInstance().getString("modset_exists"));
                return;
            }

            PresetTableModel model1 = (PresetTableModel) presetList.getModel();
            Modset selectedModset = ((Modset) model1.getElementAt(presetList.getSelectedIndex()));
            Modset newModset = selectedModset.clone(newName, Modset.Type.CLIENT);
            updateModsetList();
            newModset.save();
        });

        removePresetButtom.addActionListener(e -> {
            if (presetList.getSelectedIndex() == -1) return;
            modList.setModel(new DefaultListModel<>());
            PresetTableModel model1 = (PresetTableModel) presetList.getModel();
            ((Modset) model1.getElementAt(presetList.getSelectedIndex())).removeFromConfig();
            updateModsetList();
        });

        renamePresetButton.addActionListener(e -> {
            if (presetList.getSelectedIndex() == -1) return;
            PresetTableModel model1 = (PresetTableModel) presetList.getModel();
            Modset selectedModset = ((Modset) model1.getElementAt(presetList.getSelectedIndex()));

            Object newNameO = JOptionPane.showInputDialog(null, "",
                    LangUtils.getInstance().getString("new_modset_name"), JOptionPane.QUESTION_MESSAGE, null, null, selectedModset.getName());
            if (newNameO == null) return;
            String newName = (String) newNameO;
            if (newName.isEmpty()) return;
            if (Modset.MODSET_LIST.containsKey(newName)) {
                infoBox(LangUtils.getInstance().getString("modset_exists_msg"), LangUtils.getInstance().getString("modset_exists"));
                return;
            }

            Modset newModset = selectedModset.clone(newName, Modset.Type.CLIENT);
            updateModsetList();
            selectedModset.removeFromConfig();
            newModset.save();
        });

        syncPresetCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    DefaultComboBoxModel<Modset> model = (DefaultComboBoxModel<Modset>) syncPresetCombo.getModel();
                    Modset elementAt = model.getElementAt(((JComboBox) e.getItemSelectable()).getSelectedIndex());
                    repoTree.setCheckboxesChecked(false);
                    if (elementAt.getType() == Modset.Type.PLACEHOLDER) return;

                    List<String> collect = elementAt.getMods().stream().map(Mod::getName).collect(Collectors.toList());

                    DefaultTreeModel repoModel = (DefaultTreeModel) repoTree.getModel();
                    RepositoryTreeNode root = (RepositoryTreeNode) repoModel.getRoot();

                    for (int i = 0; i < root.getChildCount(); i++) {
                        TreeNode childAt = root.getChildAt(i);
                        if (!collect.contains(childAt.toString())) continue;
                        final TreePath treePath = new TreePath(new TreeNode[]{root, childAt});
                        repoTree.checkSubTree(treePath, true);
                        repoTree.updatePredecessorsWithCheckMode(treePath, true);
                    }
                    repoTree.revalidate();
                    repoTree.repaint();
                    updateDownloadLabel();
                }
            }
        });

        updater.needUpdate((needUpdate, newestVersion) -> {
            if (needUpdate) {
                SwingUtilities.invokeLater(() -> warnBox(LangUtils.getInstance().getString("please_update"), LangUtils.getInstance().getString("client_outdated")));
            }
        });

        updateButton.addActionListener((e) -> {
            updater.needUpdate((needUpdate, newestVersion) -> {
                if (!needUpdate) return;
                try {
                    updater.update();
                    JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
                    frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                } catch (IOException ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
                    SwingUtilities.invokeLater(() -> aboutUpdateLabel.setText("UPDATE FAILED " + ex.getMessage()));
                }
            });
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
        boolean pathSet = Parameters.ARMA_PATH.toStringParameter().getConfigValue() != null && Parameters.ARMA_PATH.toStringParameter().getConfigValue() != null;
        if (SteamTimer.arma_running) {
            playButton.setEnabled(false);
            playPresetButton.setEnabled(false);
            syncIntensiveCheckButton.setEnabled(false);
            syncFastCheckButton.setEnabled(false);
            refreshRepoButton.setEnabled(false);
            syncDownloadButton.setEnabled(false);

            playButton.setToolTipText(LangUtils.getInstance().getString("arma_running"));
            playPresetButton.setToolTipText(LangUtils.getInstance().getString("arma_running"));
            syncIntensiveCheckButton.setToolTipText(LangUtils.getInstance().getString("arma_running"));
            syncFastCheckButton.setToolTipText(LangUtils.getInstance().getString("arma_running"));
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
                syncIntensiveCheckButton.setEnabled(true);
                syncFastCheckButton.setEnabled(true);
                refreshRepoButton.setEnabled(true);

                syncDownloadButton.setEnabled(fileChecker.isChecked());

                syncIntensiveCheckButton.setToolTipText(null);
                refreshRepoButton.setToolTipText(null);
            } else {
                syncIntensiveCheckButton.setEnabled(false);
                syncFastCheckButton.setEnabled(false);
                refreshRepoButton.setEnabled(false);

                syncDownloadButton.setEnabled(false);

                syncIntensiveCheckButton.setToolTipText(LangUtils.getInstance().getString("path_not_set"));
                syncFastCheckButton.setToolTipText(LangUtils.getInstance().getString("path_not_set"));
                refreshRepoButton.setToolTipText(LangUtils.getInstance().getString("path_not_set"));
            }
        }
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


        initFolderChooser(settingsArmaPathText, settingsArmaPathBtn, Parameters.ARMA_PATH.toStringParameter(), new Callback.JFileSelectCallback() {
            @Override
            public boolean allowSelection(File path) {
                String sPath = path.getAbsolutePath();
                if (!ArmaUtils.checkArmaPath(path.toPath())) {
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

        initFolderChooser(settingsModsPathText, settingsModsPathBtn, Parameters.MOD_PATH.toStringParameter(), new Callback.JFileSelectCallback() {
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

        initComboBox(settingsLanguageCombo, Parameters.LANGUAGE.toStringParameter());
        initComboBox(settingsBehaviorStartCombo, Parameters.BEHAVIOUR_AFTER_START.toStringParameter());

        initComboBox(settingsProfileCombo, Parameters.PROFILE.toStringParameter(directories));
        initComboBox(settingsExThreadsCombo, Parameters.EXTRA_THREADS.toStringParameter());
        initComboBox(settingsMallocCombo, Parameters.MALLOC.toStringParameter());


        // -------------------------------- CHECK BOXES --------------------------------

        initCheckBox(settingsShowParameterBox, Parameters.SHOW_START_PARAMETER.toBooolParameter());
        settingsShowParameterBox.addItemListener(e -> parameterText.setVisible(e.getStateChange() == ItemEvent.SELECTED));
        initCheckBox(settingsCheckModsBox, Parameters.CHECK_MODSET.toBooolParameter());

        initCheckBox(settingsUseWorkshopBox, Parameters.USE_WORKSHOP.toBooolParameter());
        settingsUseWorkshopBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                SwingUtilities.invokeLater(() -> warnBox(LangUtils.getInstance().getString("warning_workshop"), LangUtils.getInstance().getString("warning")));
            }
        });

        initCheckBox(settingsUseSixtyFourBitBox, Parameters.USE_64_BIT_CLIENT.toBooolParameter());
        initCheckBox(settingsNoSplashBox, Parameters.NO_SPLASH.toBooolParameter());
        initCheckBox(settingsSkipIntroBox, Parameters.SKIP_INTRO.toBooolParameter());
        initCheckBox(settingsNoCBBox, Parameters.NO_CB.toBooolParameter());
        initCheckBox(settingsNoLogsBox, Parameters.NO_LOGS.toBooolParameter());
        initCheckBox(settingsEnableHTBox, Parameters.ENABLE_HT.toBooolParameter());
        initCheckBox(settingsHugeoagesBox, Parameters.HUGEPAGES.toBooolParameter());
        initCheckBox(settingsNoPauseBox, Parameters.NO_PAUSE.toBooolParameter());
        initCheckBox(settingsShowScriptErrorsBox, Parameters.SHOW_SCRIPT_ERRORS.toBooolParameter());
        initCheckBox(settingsFilePatchingBox, Parameters.FILE_PATCHING.toBooolParameter());
        initCheckBox(settingsCrashDiagBox, Parameters.CRASH_DIAG.toBooolParameter());
        initCheckBox(settingsWindowBox, Parameters.WINDOW.toBooolParameter());


        // -------------------------------- SPINNER --------------------------------

        OperatingSystemMXBean mxbean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        int memorySize = (int) (mxbean.getTotalPhysicalMemorySize() / 1024);

        initSpinner(settingsMaxMemSpinner, Parameters.MAX_MEM.toStringParameter(), -1, memorySize);
        initSpinner(settingsMaxVRamSpinner, Parameters.MAX_VRAM.toStringParameter(), -1, 99999);
        initSpinner(settingsCpuCountSpinner, Parameters.CPU_COUNT.toStringParameter(), 0, Runtime.getRuntime().availableProcessors());
        initSpinner(settingsPosXSpinner, Parameters.POS_X.toStringParameter(), -1, 99999);
        initSpinner(settingsPosYSpinner, Parameters.POS_Y.toStringParameter(), -1, 99999);

        // -------------------------------- -------------------------------- --------------------------------
    }

    private void initCheckBox(JCheckBox cb, Parameter<Boolean> paraObj) {
        cb.setSelected(paraObj.getValue());
        cb.addItemListener(new SettingsHandler.CheckBoxListener(paraObj));
    }

    private void initComboBox(JComboBox<String> cb, Parameter<String> paraObj) {
        cb.setSelectedIndex(paraObj.getIndex());
        if (cb.getItemListeners().length == 1) cb.addItemListener(new SettingsHandler.ComboBoxListener(paraObj));
    }

    private void initFolderChooser(JTextField showText, JButton actionButton, Parameter<String> paraObj, Callback.JFileSelectCallback check) {
        showText.setText(paraObj.getValue());
        if (actionButton.getActionListeners().length == 0)
            actionButton.addActionListener(new SettingsHandler.Fileistener(mainPanel, paraObj, check));
    }

    public void initSpinner(JSpinner spinner, Parameter<String> paraObj, int min, int max) {
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
            for (int i = (path.length > 2 ? 2 : 1); i < path.length; i++) {
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

    public void updateModList(final Modset modset) {
        if (modset == null) return;
        DefaultListModel<String> listModel = new DefaultListModel<>();

        if (modset.getType() == Modset.Type.PLACEHOLDER) return;
        int[] select = new int[modset.getMods().size()];

        AtomicInteger selectCounter = new AtomicInteger(0);
        RepositoryManger.MOD_LIST.stream()
                .filter((am) -> am instanceof Mod)
                .sorted()
                .forEach((abstractMod) -> {
                    final int i = listModel.getSize();
                    listModel.add(i, abstractMod.getName());
                    for (Mod mod : modset.getMods()) {
                        if (mod.getName().equals(abstractMod.getName())) {
                            select[selectCounter.getAndIncrement()] = i;
                            break;
                        }
                    }
                });

        modList.setModel(listModel);
        modList.setSelectedIndices(select);
        modList.setEnabled(modset.getType() != Modset.Type.SERVER);
        presetNotePane.setVisible(modset.getType() == Modset.Type.SERVER);
        modList.revalidate();
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

                    for (int i = 0; i < path.size() - 1; i++) {
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
                syncPresetCombo.setSelectedIndex(0);

                updateDownloadLabel();
            }
        });

        expandAllButton.setEnabled(true);
        collapseAllButton.setEnabled(true);
    }

    public void updateDownloadLabel() {
        lastSynclist = getSyncList();
        if (lastSynclist.getSize() != 0)
            syncSizeLabel.setText("0.0 B/" + Humanize.binaryPrefix(lastSynclist.getSize()));
        else syncSizeLabel.setText("0.0 B/0.0 B");
        if (lastSynclist.getCount() != 0) {
            syncDownloadButton.setEnabled(true);
            syncFileCountLabel.setText("0/" + lastSynclist.getCount());
        } else {
            syncDownloadButton.setEnabled(false);
            syncFileCountLabel.setText("");
        }
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
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Observer received: " + s);
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

                    updateModsetList();
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
        } else if (s.equals(RepositoryManger.Type.CHANGELOG.toString())) {
            if (RepositoryManger.getInstance().getStatus(RepositoryManger.Type.CHANGELOG) == DownloadStatus.FINNISHED) {
                SwingUtilities.invokeLater(() -> {
                    changelogPane.setText(Changelog.get());
                    changelogPane.setCaretPosition(0);
                    changelogPane.setLineWrap(true);
                    changelogPane.setWrapStyleWord(true);
                    changelogPane.revalidate();
                    changelogPane.repaint();
                });
            }
        } else if (s.equals("fileChecker")) {
            syncIntensiveCheckButton.setEnabled(true);
            syncFastCheckButton.setEnabled(true);
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

            refreshRepoButton.setEnabled(true);

            syncChangedFileSizeLabel.setText(Humanize.binaryPrefix(fileChecker.getSize()));

            lastSynclist = null;
        } else if (s.equals("fileCheckerStopped")) {
            syncIntensiveCheckButton.setEnabled(true);
            syncFastCheckButton.setEnabled(true);
            syncCheckAbortButton.setEnabled(false);
            syncCheckProgress.setValue(0);
            syncCheckStatusLabel.setText("Failed!");
            repoTree.setCheckboxesEnabled(false);

            syncDownloadAbortButton.setEnabled(false);
            syncDownloadButton.setEnabled(false);
            syncPauseButton.setEnabled(false);

            repoTree.setCheckboxesChecked(false);
            refreshRepoButton.setEnabled(true);

            syncAddedFilesLabel.setText("" + 0);
            syncChangedFilesLabel.setText("" + 0);
            syncDeletedFilesLabel.setText("" + 0);

            syncChangedFileSizeLabel.setText("0.0 B");

            lastSynclist = null;
        } else if (s.equals("syncStopped")) {
            new Thread(() -> fileChecker.check(true)).start();
            SwingUtilities.invokeLater(() -> {
                syncDownloadButton.setEnabled(false);
                syncDownloadAbortButton.setEnabled(false);
                syncPauseButton.setEnabled(false);

                syncStatusLabel.setText("Sync stopped");
                TaskBarUtils.getInstance().setValue(0);
                TaskBarUtils.getInstance().off();
            });
        } else if (s.equals("syncComplete")) {
            new Thread(() -> fileChecker.check(true)).start();
            SwingUtilities.invokeLater(() -> {
                syncDownloadButton.setEnabled(false);
                syncDownloadAbortButton.setEnabled(false);
                syncPauseButton.setEnabled(false);

                syncStatusLabel.setText("Sync finished");
                TaskBarUtils.getInstance().setValue(0);
                TaskBarUtils.getInstance().off();
                TaskBarUtils.getInstance().attention();
                TaskBarUtils.getInstance().notification("Sync complete", "", TrayIcon.MessageType.INFO);
            });
        } else if (s.equals("syncContinue")) {
            SwingUtilities.invokeLater(() -> {
                syncDownloadAbortButton.setEnabled(true);
                syncPauseButton.setEnabled(true);
                syncPauseButton.setText(LangUtils.getInstance().getString("pause"));
                syncDownloadButton.setEnabled(false);
                TaskBarUtils.getInstance().normal();
            });
        } else if (s.equals("syncPaused")) {
            SwingUtilities.invokeLater(() -> {
                syncDownloadAbortButton.setEnabled(true);
                syncPauseButton.setEnabled(true);
                syncPauseButton.setText(LangUtils.getInstance().getString("resume"));
                syncDownloadButton.setEnabled(false);
                TaskBarUtils.getInstance().paused();
            });
        }
    }

    private void updateModsetList() {
        SwingUtilities.invokeLater(() -> {
            if (((DefaultComboBoxModel<Modset>) syncPresetCombo.getModel()).getSize() > 0) {
                syncPresetCombo.setSelectedIndex(0);
            }
            PresetTableModel model = (PresetTableModel) presetList.getModel();
            model.clear();

            model.add(new Modset("--Server", Modset.Type.PLACEHOLDER, null, false));
            Modset.MODSET_LIST.values().stream().filter((ms) -> ms.getType() == Modset.Type.SERVER).sorted().forEach(model::add);

            model.add(new Modset("--User", Modset.Type.PLACEHOLDER, null, false));
            Modset.MODSET_LIST.values().stream().filter((ms) -> ms.getType() == Modset.Type.CLIENT).sorted().forEach(model::add);

            DefaultComboBoxModel<Modset> presetModel = new DefaultComboBoxModel<>();
            presetModel.addElement(new Modset("", Modset.Type.PLACEHOLDER, null, false));
            Modset.MODSET_LIST.values().stream().filter((ms) -> ms.getType() != Modset.Type.PLACEHOLDER).sorted().forEach(presetModel::addElement);

            syncPresetCombo.setModel(presetModel);
        });
    }

    public void fileCheck(boolean fastscan) {
        syncIntensiveCheckButton.setEnabled(false);
        syncFastCheckButton.setEnabled(false);
        syncCheckAbortButton.setEnabled(true);
        syncCheckStatusLabel.setText("Running!");
        new Thread(() -> fileChecker.check(fastscan)).start();

        refreshRepoButton.setEnabled(false);

        repoTree.setCheckboxesEnabled(false);
        repoTree.setCheckboxesChecked(false);
    }

    public void exit() {
        fileChecker.stop();
        syncer.stop();
    }

    public void openURL(String url) {
        try {
            Desktop.getDesktop().browse(new URL(url).toURI());
        } catch (Exception ignored) {
        }
    }

    public void switchTab(Tab tab) {
        Color focusBackgroundColor = UIManager.getColor("Button.default.focusColor");
        Color backgroundColor = UIManager.getColor("Button.background");

        playPanelButton.setBackground(backgroundColor);
        updatePanelButton.setBackground(backgroundColor);
        changelogButton.setBackground(backgroundColor);
        presetPanelButton.setBackground(backgroundColor);
        settingsPanelButton.setBackground(backgroundColor);

        switch (tab) {
            case PLAY:
                playPanelButton.setBackground(focusBackgroundColor);
                break;

            case UPDATE:
                updatePanelButton.setBackground(focusBackgroundColor);
                break;

            case CHANGELOG:
                changelogButton.setBackground(focusBackgroundColor);
                Changelog.refresh();
                break;

            case PRESET:
                presetPanelButton.setBackground(focusBackgroundColor);
                break;

            case SETTING:
                settingsPanelButton.setBackground(focusBackgroundColor);
                break;

            case ABOUT:
                updater.needUpdate(new Callback.NeedUpdateCallback() {
                    @Override
                    public void response(boolean needUpdate, Version newestVersion) {
                        if (needUpdate) {
                            SwingUtilities.invokeLater(() -> {
                                aboutUpdateLabel.setText(LangUtils.getInstance().getString("client_outdated") + ": v" + newestVersion.get());
                                updateButton.setVisible(true);
                                aboutUpdateLabel.setForeground(Color.ORANGE);
                            });
                        } else {
                            SwingUtilities.invokeLater(() -> {
                                aboutUpdateLabel.setText(LangUtils.getInstance().getString("client_up_to_date"));
                                updateButton.setVisible(false);
                                aboutUpdateLabel.setForeground(UIManager.getColor("Label.foreground"));
                            });
                        }
                    }
                });
                break;
        }

        tabbedPane1.setSelectedIndex(tab.getIndex());
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.setMinimumSize(new Dimension(765, 550));
        splitView = new JSplitPane();
        splitView.setContinuousLayout(false);
        splitView.setDividerLocation(200);
        splitView.setDividerSize(10);
        splitView.setEnabled(false);
        splitView.setOrientation(1);
        mainPanel.add(splitView, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(14, 2, new Insets(0, 8, 7, 8), -1, -1));
        panel1.setBackground(new Color(-14210516));
        splitView.setLeftComponent(panel1);
        settingsPanelButton = new JButton();
        settingsPanelButton.setAlignmentY(0.0f);
        settingsPanelButton.setBorderPainted(true);
        settingsPanelButton.setFocusCycleRoot(false);
        settingsPanelButton.setFocusPainted(false);
        settingsPanelButton.setFocusable(false);
        Font settingsPanelButtonFont = this.$$$getFont$$$(null, -1, 16, settingsPanelButton.getFont());
        if (settingsPanelButtonFont != null) settingsPanelButton.setFont(settingsPanelButtonFont);
        settingsPanelButton.setHorizontalAlignment(2);
        settingsPanelButton.setIcon(new ImageIcon(getClass().getResource("/icons/settings_16.png")));
        settingsPanelButton.setIconTextGap(10);
        settingsPanelButton.setInheritsPopupMenu(true);
        settingsPanelButton.setMargin(new Insets(0, 0, 0, 0));
        this.$$$loadButtonText$$$(settingsPanelButton, ResourceBundle.getBundle("lang").getString("settings"));
        settingsPanelButton.putClientProperty("hideActionText", Boolean.FALSE);
        panel1.add(settingsPanelButton, new GridConstraints(12, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(6, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        updatePanelButton = new JButton();
        updatePanelButton.setFocusPainted(false);
        updatePanelButton.setFocusable(false);
        Font updatePanelButtonFont = this.$$$getFont$$$(null, -1, 16, updatePanelButton.getFont());
        if (updatePanelButtonFont != null) updatePanelButton.setFont(updatePanelButtonFont);
        updatePanelButton.setHorizontalAlignment(2);
        updatePanelButton.setIcon(new ImageIcon(getClass().getResource("/icons/download_16.png")));
        updatePanelButton.setIconTextGap(10);
        this.$$$loadButtonText$$$(updatePanelButton, ResourceBundle.getBundle("lang").getString("update"));
        panel1.add(updatePanelButton, new GridConstraints(9, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        playPanelButton = new JButton();
        playPanelButton.setFocusPainted(false);
        playPanelButton.setFocusable(false);
        Font playPanelButtonFont = this.$$$getFont$$$(null, -1, 16, playPanelButton.getFont());
        if (playPanelButtonFont != null) playPanelButton.setFont(playPanelButtonFont);
        playPanelButton.setHorizontalAlignment(2);
        playPanelButton.setIcon(new ImageIcon(getClass().getResource("/icons/play_16.png")));
        playPanelButton.setIconTextGap(10);
        playPanelButton.setMargin(new Insets(0, 0, 0, 0));
        this.$$$loadButtonText$$$(playPanelButton, ResourceBundle.getBundle("lang").getString("play"));
        panel1.add(playPanelButton, new GridConstraints(8, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.setBackground(new Color(-12828863));
        panel2.setOpaque(false);
        panel1.add(panel2, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(128, 128), null, null, 0, false));
        logo = new JLabel();
        logo.setText("");
        panel2.add(logo, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        title = new JLabel();
        Font titleFont = this.$$$getFont$$$(null, Font.BOLD, 14, title.getFont());
        if (titleFont != null) title.setFont(titleFont);
        title.setOpaque(false);
        title.setText("Welcome :)!");
        title.setVerticalAlignment(0);
        title.setVerticalTextPosition(0);
        panel1.add(title, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        subtitle = new JLabel();
        Font subtitleFont = this.$$$getFont$$$("Monospaced", Font.ITALIC, 12, subtitle.getFont());
        if (subtitleFont != null) subtitle.setFont(subtitleFont);
        subtitle.setText("Town-Client v1.1.153");
        panel1.add(subtitle, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Steam");
        panel1.add(label1, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        steamStatus = new JLabel();
        steamStatus.setText("");
        panel1.add(steamStatus, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_SOUTHEAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("ArmA 3");
        panel1.add(label2, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        armaStatus = new JLabel();
        armaStatus.setText("");
        panel1.add(armaStatus, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_NORTHEAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        presetPanelButton = new JButton();
        presetPanelButton.setFocusPainted(false);
        presetPanelButton.setFocusable(false);
        Font presetPanelButtonFont = this.$$$getFont$$$(null, -1, 16, presetPanelButton.getFont());
        if (presetPanelButtonFont != null) presetPanelButton.setFont(presetPanelButtonFont);
        presetPanelButton.setHorizontalAlignment(2);
        presetPanelButton.setIcon(new ImageIcon(getClass().getResource("/icons/preset_16.png")));
        presetPanelButton.setIconTextGap(10);
        presetPanelButton.setMargin(new Insets(0, 0, 0, 0));
        this.$$$loadButtonText$$$(presetPanelButton, ResourceBundle.getBundle("lang").getString("presets"));
        panel1.add(presetPanelButton, new GridConstraints(11, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        changelogButton = new JButton();
        changelogButton.setFocusPainted(false);
        changelogButton.setFocusable(false);
        Font changelogButtonFont = this.$$$getFont$$$(null, -1, 16, changelogButton.getFont());
        if (changelogButtonFont != null) changelogButton.setFont(changelogButtonFont);
        changelogButton.setHorizontalAlignment(2);
        changelogButton.setIcon(new ImageIcon(getClass().getResource("/icons/changelog_16.png")));
        changelogButton.setIconTextGap(10);
        this.$$$loadButtonText$$$(changelogButton, ResourceBundle.getBundle("lang").getString("changelog"));
        panel1.add(changelogButton, new GridConstraints(10, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.setOpaque(false);
        panel1.add(panel3, new GridConstraints(7, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        twitterIcon = new JLabel();
        twitterIcon.setIcon(new ImageIcon(getClass().getResource("/icons/twitter_32.png")));
        twitterIcon.setText("");
        twitterIcon.setToolTipText(ResourceBundle.getBundle("lang").getString("follow_on_twitter"));
        panel3.add(twitterIcon, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        githubIcon = new JLabel();
        githubIcon.setIcon(new ImageIcon(getClass().getResource("/icons/github_32.png")));
        githubIcon.setText("");
        githubIcon.setToolTipText(ResourceBundle.getBundle("lang").getString("star_on_github"));
        panel3.add(githubIcon, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        aboutLabel = new JLabel();
        Font aboutLabelFont = this.$$$getFont$$$(null, -1, 11, aboutLabel.getFont());
        if (aboutLabelFont != null) aboutLabel.setFont(aboutLabelFont);
        aboutLabel.setForeground(new Color(-7500403));
        this.$$$loadLabelText$$$(aboutLabel, ResourceBundle.getBundle("lang").getString("about"));
        panel1.add(aboutLabel, new GridConstraints(13, 1, 1, 1, GridConstraints.ANCHOR_SOUTHEAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tabbedPane1 = new JTabbedPane();
        tabbedPane1.setEnabled(true);
        tabbedPane1.setInheritsPopupMenu(false);
        tabbedPane1.setVisible(true);
        tabbedPane1.putClientProperty("html.disable", Boolean.FALSE);
        splitView.setRightComponent(tabbedPane1);
        playTab = new JPanel();
        playTab.setLayout(new BorderLayout(0, 0));
        tabbedPane1.addTab("Play", playTab);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(3, 1, new Insets(50, 50, 50, 50), -1, -1));
        playTab.add(panel4, BorderLayout.CENTER);
        final JScrollPane scrollPane1 = new JScrollPane();
        panel4.add(scrollPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        serverTable = new JTable();
        scrollPane1.setViewportView(serverTable);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 1, new Insets(2, 5, 3, 0), -1, -1));
        panel5.setBackground(new Color(-14736860));
        panel5.setEnabled(false);
        panel4.add(panel5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        Font label3Font = this.$$$getFont$$$(null, Font.BOLD, 16, label3.getFont());
        if (label3Font != null) label3.setFont(label3Font);
        this.$$$loadLabelText$$$(label3, ResourceBundle.getBundle("lang").getString("select_server"));
        panel5.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(2, 1, new Insets(50, 25, 0, 25), -1, -1));
        Font panel6Font = this.$$$getFont$$$(null, -1, -1, panel6.getFont());
        if (panel6Font != null) panel6.setFont(panel6Font);
        panel4.add(panel6, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        playButton = new JButton();
        playButton.setEnabled(false);
        Font playButtonFont = this.$$$getFont$$$(null, Font.BOLD, 18, playButton.getFont());
        if (playButtonFont != null) playButton.setFont(playButtonFont);
        playButton.setMargin(new Insets(15, 0, 15, 0));
        this.$$$loadButtonText$$$(playButton, ResourceBundle.getBundle("lang").getString("play_now"));
        panel6.add(playButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        parameterText = new JTextField();
        parameterText.setEditable(false);
        parameterText.setText("-noInit -noLogs");
        panel6.add(parameterText, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        updateTab = new JPanel();
        updateTab.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 5, 0), -1, -1));
        tabbedPane1.addTab("Update", updateTab);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(3, 1, new Insets(10, 0, 0, 0), -1, -1));
        panel7.setOpaque(true);
        updateTab.add(panel7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel7.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel8.setOpaque(true);
        panel7.add(panel8, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new BorderLayout(0, 0));
        panel9.setOpaque(true);
        panel8.add(panel9, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(1, 1, new Insets(2, 5, 3, 0), -1, -1));
        panel10.setBackground(new Color(-14736860));
        panel10.setEnabled(false);
        panel9.add(panel10, BorderLayout.NORTH);
        final JLabel label4 = new JLabel();
        Font label4Font = this.$$$getFont$$$(null, Font.BOLD, 16, label4.getFont());
        if (label4Font != null) label4.setFont(label4Font);
        this.$$$loadLabelText$$$(label4, ResourceBundle.getBundle("lang").getString("check"));
        panel10.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(6, 4, new Insets(0, 3, 0, 0), -1, -1));
        panel11.setOpaque(true);
        panel9.add(panel11, BorderLayout.CENTER);
        final JLabel label5 = new JLabel();
        this.$$$loadLabelText$$$(label5, ResourceBundle.getBundle("lang").getString("check_local_addons"));
        panel11.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel11.add(spacer3, new GridConstraints(0, 2, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        syncCheckStatusLabel = new JLabel();
        Font syncCheckStatusLabelFont = this.$$$getFont$$$(null, Font.ITALIC, -1, syncCheckStatusLabel.getFont());
        if (syncCheckStatusLabelFont != null) syncCheckStatusLabel.setFont(syncCheckStatusLabelFont);
        syncCheckStatusLabel.setText("");
        panel11.add(syncCheckStatusLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        syncCheckProgress = new JProgressBar();
        syncCheckProgress.setString("");
        syncCheckProgress.setStringPainted(true);
        syncCheckProgress.setValue(0);
        panel11.add(syncCheckProgress, new GridConstraints(1, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel11.add(panel12, new GridConstraints(2, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        syncCheckAbortButton = new JButton();
        syncCheckAbortButton.setEnabled(false);
        this.$$$loadButtonText$$$(syncCheckAbortButton, ResourceBundle.getBundle("lang").getString("abort"));
        panel12.add(syncCheckAbortButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        syncIntensiveCheckButton = new JButton();
        syncIntensiveCheckButton.setEnabled(false);
        this.$$$loadButtonText$$$(syncIntensiveCheckButton, ResourceBundle.getBundle("lang").getString("intensive_check"));
        panel12.add(syncIntensiveCheckButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        syncFastCheckButton = new JButton();
        syncFastCheckButton.setEnabled(false);
        this.$$$loadButtonText$$$(syncFastCheckButton, ResourceBundle.getBundle("lang").getString("fast_check"));
        panel12.add(syncFastCheckButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        this.$$$loadLabelText$$$(label6, ResourceBundle.getBundle("lang").getString("changed_files"));
        panel11.add(label6, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        this.$$$loadLabelText$$$(label7, ResourceBundle.getBundle("lang").getString("added_files"));
        panel11.add(label7, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        this.$$$loadLabelText$$$(label8, ResourceBundle.getBundle("lang").getString("deleted_files"));
        panel11.add(label8, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        syncChangedFilesLabel = new JLabel();
        syncChangedFilesLabel.setText("0");
        panel11.add(syncChangedFilesLabel, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        syncAddedFilesLabel = new JLabel();
        syncAddedFilesLabel.setText("0");
        panel11.add(syncAddedFilesLabel, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        syncDeletedFilesLabel = new JLabel();
        syncDeletedFilesLabel.setText("0");
        panel11.add(syncDeletedFilesLabel, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        this.$$$loadLabelText$$$(label9, ResourceBundle.getBundle("lang").getString("changed_filesize"));
        panel11.add(label9, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        syncChangedFileSizeLabel = new JLabel();
        syncChangedFileSizeLabel.setText("0.0 B");
        syncChangedFileSizeLabel.setToolTipText(ResourceBundle.getBundle("lang").getString("changed_filesize_tooltip"));
        panel11.add(syncChangedFileSizeLabel, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new BorderLayout(0, 0));
        panel13.setOpaque(true);
        panel7.add(panel13, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridLayoutManager(1, 1, new Insets(2, 5, 3, 0), -1, -1));
        panel14.setBackground(new Color(-14736860));
        panel14.setEnabled(false);
        panel13.add(panel14, BorderLayout.NORTH);
        final JLabel label10 = new JLabel();
        Font label10Font = this.$$$getFont$$$(null, Font.BOLD, 16, label10.getFont());
        if (label10Font != null) label10.setFont(label10Font);
        this.$$$loadLabelText$$$(label10, ResourceBundle.getBundle("lang").getString("progress"));
        panel14.add(label10, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel15 = new JPanel();
        panel15.setLayout(new FormLayout("fill:d:grow,left:4dlu:noGrow,fill:d:grow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        panel13.add(panel15, BorderLayout.CENTER);
        final JLabel label11 = new JLabel();
        this.$$$loadLabelText$$$(label11, ResourceBundle.getBundle("lang").getString("speed"));
        CellConstraints cc = new CellConstraints();
        panel15.add(label11, cc.xy(1, 5));
        final JLabel label12 = new JLabel();
        this.$$$loadLabelText$$$(label12, ResourceBundle.getBundle("lang").getString("remaining_time"));
        panel15.add(label12, cc.xy(1, 7));
        syncFileCountLabel = new JLabel();
        syncFileCountLabel.setText("");
        panel15.add(syncFileCountLabel, cc.xy(3, 3));
        final JLabel label13 = new JLabel();
        this.$$$loadLabelText$$$(label13, ResourceBundle.getBundle("lang").getString("file_count"));
        panel15.add(label13, cc.xy(1, 3));
        final JLabel label14 = new JLabel();
        this.$$$loadLabelText$$$(label14, ResourceBundle.getBundle("lang").getString("total_file_size"));
        panel15.add(label14, cc.xy(1, 1));
        syncSizeLabel = new JLabel();
        syncSizeLabel.setText("0.0 B/0.0 B");
        panel15.add(syncSizeLabel, cc.xy(3, 1));
        syncDownloadSpeedLabel = new JLabel();
        syncDownloadSpeedLabel.setText("");
        panel15.add(syncDownloadSpeedLabel, cc.xy(3, 5));
        final JLabel label15 = new JLabel();
        label15.setText("");
        panel15.add(label15, cc.xy(3, 7));
        final JPanel panel16 = new JPanel();
        panel16.setLayout(new BorderLayout(0, 0));
        panel16.setOpaque(true);
        panel7.add(panel16, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel17 = new JPanel();
        panel17.setLayout(new GridLayoutManager(1, 1, new Insets(2, 5, 3, 0), -1, -1));
        panel17.setBackground(new Color(-14736860));
        panel17.setEnabled(false);
        panel16.add(panel17, BorderLayout.NORTH);
        final JLabel label16 = new JLabel();
        Font label16Font = this.$$$getFont$$$(null, Font.BOLD, 16, label16.getFont());
        if (label16Font != null) label16.setFont(label16Font);
        this.$$$loadLabelText$$$(label16, ResourceBundle.getBundle("lang").getString("download"));
        panel17.add(label16, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel18 = new JPanel();
        panel18.setLayout(new FormLayout("fill:d:grow", "center:d:grow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        panel16.add(panel18, BorderLayout.CENTER);
        final JPanel panel19 = new JPanel();
        panel19.setLayout(new FormLayout("fill:d:grow", "center:d:noGrow"));
        panel18.add(panel19, cc.xy(1, 1));
        final JPanel panel20 = new JPanel();
        panel20.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel19.add(panel20, cc.xy(1, 1));
        syncDownloadProgress = new JProgressBar();
        Font syncDownloadProgressFont = this.$$$getFont$$$(null, Font.BOLD, 14, syncDownloadProgress.getFont());
        if (syncDownloadProgressFont != null) syncDownloadProgress.setFont(syncDownloadProgressFont);
        syncDownloadProgress.setString("");
        syncDownloadProgress.setStringPainted(true);
        syncDownloadProgress.setValue(0);
        panel20.add(syncDownloadProgress, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel21 = new JPanel();
        panel21.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel18.add(panel21, cc.xywh(1, 2, 1, 8));
        syncDownloadButton = new JButton();
        syncDownloadButton.setEnabled(false);
        this.$$$loadButtonText$$$(syncDownloadButton, ResourceBundle.getBundle("lang").getString("download"));
        panel21.add(syncDownloadButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        syncDownloadAbortButton = new JButton();
        syncDownloadAbortButton.setEnabled(false);
        this.$$$loadButtonText$$$(syncDownloadAbortButton, ResourceBundle.getBundle("lang").getString("abort"));
        panel21.add(syncDownloadAbortButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        syncPauseButton = new JButton();
        syncPauseButton.setEnabled(false);
        this.$$$loadButtonText$$$(syncPauseButton, ResourceBundle.getBundle("lang").getString("pause"));
        panel21.add(syncPauseButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        syncStatusLabel = new JLabel();
        syncStatusLabel.setText("");
        updateTab.add(syncStatusLabel, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel22 = new JPanel();
        panel22.setLayout(new GridLayoutManager(1, 1, new Insets(10, 0, 0, 5), -1, -1));
        updateTab.add(panel22, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel23 = new JPanel();
        panel23.setLayout(new BorderLayout(0, 0));
        panel22.add(panel23, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel24 = new JPanel();
        panel24.setLayout(new GridLayoutManager(1, 1, new Insets(2, 5, 3, 0), -1, -1));
        panel24.setBackground(new Color(-14736860));
        panel24.setEnabled(false);
        panel23.add(panel24, BorderLayout.NORTH);
        final JLabel label17 = new JLabel();
        Font label17Font = this.$$$getFont$$$(null, Font.BOLD, 16, label17.getFont());
        if (label17Font != null) label17.setFont(label17Font);
        this.$$$loadLabelText$$$(label17, ResourceBundle.getBundle("lang").getString("repository_content"));
        panel24.add(label17, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel25 = new JPanel();
        panel25.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel23.add(panel25, BorderLayout.SOUTH);
        expandAllButton = new JButton();
        expandAllButton.setEnabled(false);
        this.$$$loadButtonText$$$(expandAllButton, ResourceBundle.getBundle("lang").getString("expand_all"));
        panel25.add(expandAllButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        syncPresetCombo = new JComboBox();
        panel25.add(syncPresetCombo, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label18 = new JLabel();
        this.$$$loadLabelText$$$(label18, ResourceBundle.getBundle("lang").getString("preset"));
        panel25.add(label18, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        refreshRepoButton = new JButton();
        refreshRepoButton.setEnabled(false);
        this.$$$loadButtonText$$$(refreshRepoButton, ResourceBundle.getBundle("lang").getString("update_repository"));
        panel25.add(refreshRepoButton, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        collapseAllButton = new JButton();
        collapseAllButton.setEnabled(false);
        this.$$$loadButtonText$$$(collapseAllButton, ResourceBundle.getBundle("lang").getString("collapse_all"));
        panel25.add(collapseAllButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        updateTreeScrolPane = new JScrollPane();
        panel23.add(updateTreeScrolPane, BorderLayout.CENTER);
        updateTreePanel = new JPanel();
        updateTreePanel.setLayout(new BorderLayout(0, 0));
        updateTreeScrolPane.setViewportView(updateTreePanel);
        tree1 = new JTree();
        updateTreePanel.add(tree1, BorderLayout.CENTER);
        changelogTab = new JPanel();
        changelogTab.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Changelog", changelogTab);
        changelogScroll = new JScrollPane();
        changelogScroll.setAutoscrolls(false);
        changelogScroll.setOpaque(true);
        changelogScroll.setVisible(true);
        changelogTab.add(changelogScroll, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        changelogScroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        changelogPane = new JTextArea();
        changelogPane.setAutoscrolls(false);
        changelogPane.setEditable(false);
        Font changelogPaneFont = this.$$$getFont$$$("Monospaced", -1, 15, changelogPane.getFont());
        if (changelogPaneFont != null) changelogPane.setFont(changelogPaneFont);
        changelogPane.setLineWrap(true);
        changelogPane.setText("No Changelog Availaible");
        changelogPane.setVisible(true);
        changelogScroll.setViewportView(changelogPane);
        presetsTab = new JPanel();
        presetsTab.setLayout(new BorderLayout(0, 0));
        presetsTab.setBackground(new Color(-7103841));
        tabbedPane1.addTab("Presets", presetsTab);
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setDividerSize(15);
        splitPane1.setDoubleBuffered(false);
        splitPane1.setEnabled(false);
        presetsTab.add(splitPane1, BorderLayout.CENTER);
        final JPanel panel26 = new JPanel();
        panel26.setLayout(new BorderLayout(0, 0));
        splitPane1.setRightComponent(panel26);
        final JPanel panel27 = new JPanel();
        panel27.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 5, 5), -1, -1));
        panel26.add(panel27, BorderLayout.SOUTH);
        final Spacer spacer4 = new Spacer();
        panel27.add(spacer4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        playPresetButton = new JButton();
        playPresetButton.setEnabled(false);
        this.$$$loadButtonText$$$(playPresetButton, ResourceBundle.getBundle("lang").getString("play"));
        panel27.add(playPresetButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel28 = new JPanel();
        panel28.setLayout(new GridLayoutManager(2, 1, new Insets(10, 0, 5, 5), -1, -1));
        panel26.add(panel28, BorderLayout.CENTER);
        final JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setHorizontalScrollBarPolicy(31);
        scrollPane2.setVerticalScrollBarPolicy(20);
        panel28.add(scrollPane2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        final JPanel panel29 = new JPanel();
        panel29.setLayout(new GridLayoutManager(1, 1, new Insets(100, 70, 100, 70), -1, -1));
        scrollPane2.setViewportView(panel29);
        presetNotePane = new JPanel();
        presetNotePane.setLayout(new FormLayout("fill:d:grow", "center:d:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        presetNotePane.setVisible(false);
        panel29.add(presetNotePane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label19 = new JLabel();
        Font label19Font = this.$$$getFont$$$(null, Font.BOLD, 14, label19.getFont());
        if (label19Font != null) label19.setFont(label19Font);
        this.$$$loadLabelText$$$(label19, ResourceBundle.getBundle("lang").getString("note"));
        presetNotePane.add(label19, cc.xy(1, 1));
        presetNoteButton = new JButton();
        this.$$$loadButtonText$$$(presetNoteButton, ResourceBundle.getBundle("lang").getString("clone_preset"));
        presetNotePane.add(presetNoteButton, cc.xy(1, 5));
        presetNotePaneWrapper = new JPanel();
        presetNotePaneWrapper.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        presetNotePane.add(presetNotePaneWrapper, cc.xy(1, 3));
        presetNoteTextPane = new JTextPane();
        presetNoteTextPane.setEnabled(true);
        presetNoteTextPane.setText(ResourceBundle.getBundle("lang").getString("presets_note"));
        presetNotePaneWrapper.add(presetNoteTextPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(200, 50), null, 0, false));
        final JLabel label20 = new JLabel();
        Font label20Font = this.$$$getFont$$$(null, Font.BOLD, 16, label20.getFont());
        if (label20Font != null) label20.setFont(label20Font);
        this.$$$loadLabelText$$$(label20, ResourceBundle.getBundle("lang").getString("preset_settings"));
        panel28.add(label20, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel30 = new JPanel();
        panel30.setLayout(new BorderLayout(0, 0));
        splitPane1.setLeftComponent(panel30);
        final JPanel panel31 = new JPanel();
        panel31.setLayout(new GridLayoutManager(2, 2, new Insets(0, 5, 5, 0), -1, -1));
        panel30.add(panel31, BorderLayout.SOUTH);
        clonePresetButton = new JButton();
        clonePresetButton.setEnabled(false);
        this.$$$loadButtonText$$$(clonePresetButton, ResourceBundle.getBundle("lang").getString("clone"));
        panel31.add(clonePresetButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        newPresetButtom = new JButton();
        this.$$$loadButtonText$$$(newPresetButtom, ResourceBundle.getBundle("lang").getString("new"));
        panel31.add(newPresetButtom, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        removePresetButtom = new JButton();
        removePresetButtom.setEnabled(false);
        this.$$$loadButtonText$$$(removePresetButtom, ResourceBundle.getBundle("lang").getString("remove"));
        panel31.add(removePresetButtom, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        renamePresetButton = new JButton();
        renamePresetButton.setEnabled(false);
        this.$$$loadButtonText$$$(renamePresetButton, ResourceBundle.getBundle("lang").getString("rename"));
        panel31.add(renamePresetButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel32 = new JPanel();
        panel32.setLayout(new GridLayoutManager(1, 1, new Insets(0, 5, 15, 0), -1, -1));
        panel30.add(panel32, BorderLayout.CENTER);
        final JPanel panel33 = new JPanel();
        panel33.setLayout(new GridLayoutManager(2, 3, new Insets(10, 0, 5, 0), -1, -1));
        panel32.add(panel33, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        presetList = new JList();
        presetList.setSelectionMode(1);
        panel33.add(presetList, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        modList = new JList();
        panel33.add(modList, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        final JLabel label21 = new JLabel();
        Font label21Font = this.$$$getFont$$$(null, Font.BOLD, 16, label21.getFont());
        if (label21Font != null) label21.setFont(label21Font);
        this.$$$loadLabelText$$$(label21, ResourceBundle.getBundle("lang").getString("presets"));
        panel33.add(label21, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label22 = new JLabel();
        Font label22Font = this.$$$getFont$$$(null, Font.BOLD, 16, label22.getFont());
        if (label22Font != null) label22.setFont(label22Font);
        this.$$$loadLabelText$$$(label22, ResourceBundle.getBundle("lang").getString("select_mods"));
        panel33.add(label22, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel33.add(spacer5, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        settingsTab = new JPanel();
        settingsTab.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Settings", settingsTab);
        final JPanel panel34 = new JPanel();
        panel34.setLayout(new GridLayoutManager(1, 1, new Insets(10, 0, 0, 0), -1, -1));
        panel34.setOpaque(false);
        settingsTab.add(panel34, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel34.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        settingScrollPane = new JScrollPane();
        settingScrollPane.setOpaque(false);
        panel34.add(settingScrollPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        settingScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        final JPanel panel35 = new JPanel();
        panel35.setLayout(new GridLayoutManager(39, 4, new Insets(0, 0, 0, 5), -1, -1));
        panel35.setOpaque(false);
        settingScrollPane.setViewportView(panel35);
        final JLabel label23 = new JLabel();
        this.$$$loadLabelText$$$(label23, ResourceBundle.getBundle("lang").getString("arma3_installpath"));
        panel35.add(label23, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        panel35.add(spacer6, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        settingsArmaPathText = new JTextField();
        settingsArmaPathText.setEditable(false);
        panel35.add(settingsArmaPathText, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label24 = new JLabel();
        this.$$$loadLabelText$$$(label24, ResourceBundle.getBundle("lang").getString("modset_folder"));
        panel35.add(label24, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label25 = new JLabel();
        this.$$$loadLabelText$$$(label25, ResourceBundle.getBundle("lang").getString("show_startparameter"));
        panel35.add(label25, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label26 = new JLabel();
        this.$$$loadLabelText$$$(label26, ResourceBundle.getBundle("lang").getString("check_modset"));
        panel35.add(label26, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label27 = new JLabel();
        this.$$$loadLabelText$$$(label27, ResourceBundle.getBundle("lang").getString("behaviour_aafter_start"));
        panel35.add(label27, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsCheckModsBox = new JCheckBox();
        settingsCheckModsBox.setText("");
        panel35.add(settingsCheckModsBox, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsShowParameterBox = new JCheckBox();
        settingsShowParameterBox.setText("");
        panel35.add(settingsShowParameterBox, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsArmaPathBtn = new JButton();
        settingsArmaPathBtn.setText("...");
        panel35.add(settingsArmaPathBtn, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsModsPathText = new JTextField();
        settingsModsPathText.setEditable(false);
        panel35.add(settingsModsPathText, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        settingsModsPathBtn = new JButton();
        settingsModsPathBtn.setText("...");
        panel35.add(settingsModsPathBtn, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsBehaviorStartCombo = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Nothing");
        defaultComboBoxModel1.addElement("Minimize");
        defaultComboBoxModel1.addElement("Exit");
        settingsBehaviorStartCombo.setModel(defaultComboBoxModel1);
        panel35.add(settingsBehaviorStartCombo, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label28 = new JLabel();
        this.$$$loadLabelText$$$(label28, ResourceBundle.getBundle("lang").getString("backend_url"));
        panel35.add(label28, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsBackendText = new JTextField();
        settingsBackendText.setEditable(false);
        settingsBackendText.setEnabled(true);
        Font settingsBackendTextFont = this.$$$getFont$$$(null, Font.ITALIC, -1, settingsBackendText.getFont());
        if (settingsBackendTextFont != null) settingsBackendText.setFont(settingsBackendTextFont);
        panel35.add(settingsBackendText, new GridConstraints(8, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel36 = new JPanel();
        panel36.setLayout(new GridLayoutManager(1, 1, new Insets(2, 5, 3, 0), -1, -1));
        panel36.setBackground(new Color(-14736860));
        panel36.setEnabled(false);
        panel35.add(panel36, new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label29 = new JLabel();
        Font label29Font = this.$$$getFont$$$(null, Font.BOLD, 16, label29.getFont());
        if (label29Font != null) label29.setFont(label29Font);
        this.$$$loadLabelText$$$(label29, ResourceBundle.getBundle("lang").getString("client_settings"));
        panel36.add(label29, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel37 = new JPanel();
        panel37.setLayout(new GridLayoutManager(1, 1, new Insets(2, 5, 3, 0), -1, -1));
        panel37.setBackground(new Color(-14736860));
        panel37.setEnabled(false);
        panel35.add(panel37, new GridConstraints(10, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label30 = new JLabel();
        Font label30Font = this.$$$getFont$$$(null, Font.BOLD, 16, label30.getFont());
        if (label30Font != null) label30.setFont(label30Font);
        this.$$$loadLabelText$$$(label30, ResourceBundle.getBundle("lang").getString("arm33_parameter"));
        panel37.add(label30, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel38 = new JPanel();
        panel38.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 25, 0), -1, -1));
        panel35.add(panel38, new GridConstraints(9, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label31 = new JLabel();
        label31.setText("Profile");
        label31.setToolTipText(ResourceBundle.getBundle("lang").getString("profile_desc"));
        panel35.add(label31, new GridConstraints(11, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsProfileCombo = new JComboBox();
        panel35.add(settingsProfileCombo, new GridConstraints(11, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label32 = new JLabel();
        label32.setText("Use64BitClient");
        label32.setToolTipText(ResourceBundle.getBundle("lang").getString("use64bitclient_desc"));
        panel35.add(label32, new GridConstraints(12, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label33 = new JLabel();
        label33.setText("NoSplash");
        label33.setToolTipText(ResourceBundle.getBundle("lang").getString("nosplash_desc"));
        panel35.add(label33, new GridConstraints(14, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label34 = new JLabel();
        label34.setText("SkipIntro");
        label34.setToolTipText(ResourceBundle.getBundle("lang").getString("skipintro_desc"));
        panel35.add(label34, new GridConstraints(15, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label35 = new JLabel();
        label35.setText("World");
        label35.setToolTipText(ResourceBundle.getBundle("lang").getString("world_desc"));
        panel35.add(label35, new GridConstraints(16, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsWorldText = new JTextField();
        panel35.add(settingsWorldText, new GridConstraints(16, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label36 = new JLabel();
        label36.setText("MaxMem");
        label36.setToolTipText(ResourceBundle.getBundle("lang").getString("maxmem_desc"));
        panel35.add(label36, new GridConstraints(18, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label37 = new JLabel();
        label37.setText("MaxVRAM");
        label37.setToolTipText(ResourceBundle.getBundle("lang").getString("maxvram_desc"));
        panel35.add(label37, new GridConstraints(19, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label38 = new JLabel();
        label38.setText("NoCB");
        label38.setToolTipText(ResourceBundle.getBundle("lang").getString("nocb_desc"));
        label38.setVerifyInputWhenFocusTarget(false);
        panel35.add(label38, new GridConstraints(20, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label39 = new JLabel();
        label39.setText("CpuCount");
        label39.setToolTipText(ResourceBundle.getBundle("lang").getString("cpucount_desc"));
        panel35.add(label39, new GridConstraints(21, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label40 = new JLabel();
        label40.setText("ExThreads");
        label40.setToolTipText(ResourceBundle.getBundle("lang").getString("exthreads_desc"));
        panel35.add(label40, new GridConstraints(22, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label41 = new JLabel();
        label41.setText("Malloc");
        label41.setToolTipText(ResourceBundle.getBundle("lang").getString("malloc_desc"));
        panel35.add(label41, new GridConstraints(23, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label42 = new JLabel();
        label42.setText("NoLogs");
        label42.setToolTipText(ResourceBundle.getBundle("lang").getString("nologs_desc"));
        panel35.add(label42, new GridConstraints(24, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label43 = new JLabel();
        label43.setText("EnableHT");
        label43.setToolTipText(ResourceBundle.getBundle("lang").getString("enableht_desc"));
        panel35.add(label43, new GridConstraints(25, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label44 = new JLabel();
        label44.setText("Hugepages");
        label44.setToolTipText(ResourceBundle.getBundle("lang").getString("hugepages_desc"));
        panel35.add(label44, new GridConstraints(26, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel39 = new JPanel();
        panel39.setLayout(new GridLayoutManager(1, 1, new Insets(5, 0, 5, 0), -1, -1));
        panel35.add(panel39, new GridConstraints(17, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel40 = new JPanel();
        panel40.setLayout(new GridLayoutManager(1, 1, new Insets(2, 6, 5, 0), -1, -1));
        panel40.setBackground(new Color(-14210516));
        panel39.add(panel40, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label45 = new JLabel();
        Font label45Font = this.$$$getFont$$$(null, Font.BOLD | Font.ITALIC, 14, label45.getFont());
        if (label45Font != null) label45.setFont(label45Font);
        this.$$$loadLabelText$$$(label45, ResourceBundle.getBundle("lang").getString("performance"));
        panel40.add(label45, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel41 = new JPanel();
        panel41.setLayout(new GridLayoutManager(1, 1, new Insets(5, 0, 5, 0), -1, -1));
        panel35.add(panel41, new GridConstraints(13, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel42 = new JPanel();
        panel42.setLayout(new GridLayoutManager(1, 1, new Insets(2, 6, 5, 0), -1, -1));
        panel42.setBackground(new Color(-14210516));
        panel41.add(panel42, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label46 = new JLabel();
        Font label46Font = this.$$$getFont$$$(null, Font.BOLD | Font.ITALIC, 14, label46.getFont());
        if (label46Font != null) label46.setFont(label46Font);
        this.$$$loadLabelText$$$(label46, ResourceBundle.getBundle("lang").getString("speed_up_game"));
        panel42.add(label46, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel43 = new JPanel();
        panel43.setLayout(new GridLayoutManager(1, 1, new Insets(5, 0, 5, 0), -1, -1));
        panel35.add(panel43, new GridConstraints(27, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel44 = new JPanel();
        panel44.setLayout(new GridLayoutManager(1, 1, new Insets(2, 6, 5, 0), -1, -1));
        panel44.setBackground(new Color(-14210516));
        panel43.add(panel44, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label47 = new JLabel();
        Font label47Font = this.$$$getFont$$$(null, Font.BOLD | Font.ITALIC, 14, label47.getFont());
        if (label47Font != null) label47.setFont(label47Font);
        this.$$$loadLabelText$$$(label47, ResourceBundle.getBundle("lang").getString("developer_settings"));
        panel44.add(label47, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label48 = new JLabel();
        label48.setText("NoPause");
        label48.setToolTipText(ResourceBundle.getBundle("lang").getString("nopause_desc"));
        panel35.add(label48, new GridConstraints(28, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label49 = new JLabel();
        label49.setText("ShowScriptErrors");
        label49.setToolTipText(ResourceBundle.getBundle("lang").getString("showscripterrors_desc"));
        panel35.add(label49, new GridConstraints(29, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label50 = new JLabel();
        label50.setText("FilePatching");
        label50.setToolTipText(ResourceBundle.getBundle("lang").getString("filepatching_desc"));
        panel35.add(label50, new GridConstraints(30, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label51 = new JLabel();
        label51.setText("Init");
        label51.setToolTipText(ResourceBundle.getBundle("lang").getString("init_desc"));
        panel35.add(label51, new GridConstraints(31, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label52 = new JLabel();
        label52.setText("Beta");
        label52.setToolTipText(ResourceBundle.getBundle("lang").getString("beta_desc"));
        panel35.add(label52, new GridConstraints(32, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label53 = new JLabel();
        label53.setText("CrashDiag");
        label53.setToolTipText(ResourceBundle.getBundle("lang").getString("crashdiag_desc"));
        panel35.add(label53, new GridConstraints(33, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label54 = new JLabel();
        label54.setText("Window");
        label54.setToolTipText(ResourceBundle.getBundle("lang").getString("window_desc"));
        panel35.add(label54, new GridConstraints(35, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel45 = new JPanel();
        panel45.setLayout(new GridLayoutManager(1, 1, new Insets(5, 0, 5, 0), -1, -1));
        panel35.add(panel45, new GridConstraints(34, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel46 = new JPanel();
        panel46.setLayout(new GridLayoutManager(1, 1, new Insets(2, 6, 5, 0), -1, -1));
        panel46.setBackground(new Color(-14210516));
        panel45.add(panel46, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label55 = new JLabel();
        Font label55Font = this.$$$getFont$$$(null, Font.BOLD | Font.ITALIC, 14, label55.getFont());
        if (label55Font != null) label55.setFont(label55Font);
        this.$$$loadLabelText$$$(label55, ResourceBundle.getBundle("lang").getString("display_settings"));
        panel46.add(label55, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label56 = new JLabel();
        label56.setText("PosX");
        label56.setToolTipText(ResourceBundle.getBundle("lang").getString("posx_desc"));
        panel35.add(label56, new GridConstraints(36, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label57 = new JLabel();
        label57.setText("PosY");
        label57.setToolTipText(ResourceBundle.getBundle("lang").getString("posy_desc"));
        panel35.add(label57, new GridConstraints(37, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsNoCBBox = new JCheckBox();
        settingsNoCBBox.setText("");
        panel35.add(settingsNoCBBox, new GridConstraints(20, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsMallocCombo = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("");
        defaultComboBoxModel2.addElement("tbb4malloc_bi");
        defaultComboBoxModel2.addElement("jemalloc_bi");
        defaultComboBoxModel2.addElement("system");
        settingsMallocCombo.setModel(defaultComboBoxModel2);
        panel35.add(settingsMallocCombo, new GridConstraints(23, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsNoLogsBox = new JCheckBox();
        settingsNoLogsBox.setText("");
        panel35.add(settingsNoLogsBox, new GridConstraints(24, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsEnableHTBox = new JCheckBox();
        settingsEnableHTBox.setText("");
        panel35.add(settingsEnableHTBox, new GridConstraints(25, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsHugeoagesBox = new JCheckBox();
        settingsHugeoagesBox.setText("");
        panel35.add(settingsHugeoagesBox, new GridConstraints(26, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsNoPauseBox = new JCheckBox();
        settingsNoPauseBox.setText("");
        panel35.add(settingsNoPauseBox, new GridConstraints(28, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsShowScriptErrorsBox = new JCheckBox();
        settingsShowScriptErrorsBox.setText("");
        panel35.add(settingsShowScriptErrorsBox, new GridConstraints(29, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsFilePatchingBox = new JCheckBox();
        settingsFilePatchingBox.setText("");
        panel35.add(settingsFilePatchingBox, new GridConstraints(30, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsCrashDiagBox = new JCheckBox();
        settingsCrashDiagBox.setText("");
        panel35.add(settingsCrashDiagBox, new GridConstraints(33, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsWindowBox = new JCheckBox();
        settingsWindowBox.setText("");
        panel35.add(settingsWindowBox, new GridConstraints(35, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsMaxMemSpinner = new JSpinner();
        panel35.add(settingsMaxMemSpinner, new GridConstraints(18, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsMaxVRamSpinner = new JSpinner();
        panel35.add(settingsMaxVRamSpinner, new GridConstraints(19, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsCpuCountSpinner = new JSpinner();
        panel35.add(settingsCpuCountSpinner, new GridConstraints(21, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsPosXSpinner = new JSpinner();
        panel35.add(settingsPosXSpinner, new GridConstraints(36, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsPosYSpinner = new JSpinner();
        panel35.add(settingsPosYSpinner, new GridConstraints(37, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsInitText = new JTextField();
        panel35.add(settingsInitText, new GridConstraints(31, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        settingsExThreadsCombo = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel3 = new DefaultComboBoxModel();
        defaultComboBoxModel3.addElement("");
        defaultComboBoxModel3.addElement("3");
        defaultComboBoxModel3.addElement("7");
        settingsExThreadsCombo.setModel(defaultComboBoxModel3);
        panel35.add(settingsExThreadsCombo, new GridConstraints(22, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsSkipIntroBox = new JCheckBox();
        settingsSkipIntroBox.setText("");
        panel35.add(settingsSkipIntroBox, new GridConstraints(15, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsNoSplashBox = new JCheckBox();
        settingsNoSplashBox.setText("");
        panel35.add(settingsNoSplashBox, new GridConstraints(14, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsUseSixtyFourBitBox = new JCheckBox();
        settingsUseSixtyFourBitBox.setText("");
        panel35.add(settingsUseSixtyFourBitBox, new GridConstraints(12, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label58 = new JLabel();
        this.$$$loadLabelText$$$(label58, ResourceBundle.getBundle("lang").getString("language"));
        panel35.add(label58, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsLanguageCombo = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel4 = new DefaultComboBoxModel();
        defaultComboBoxModel4.addElement("System");
        defaultComboBoxModel4.addElement("English");
        defaultComboBoxModel4.addElement("Deutsch");
        settingsLanguageCombo.setModel(defaultComboBoxModel4);
        panel35.add(settingsLanguageCombo, new GridConstraints(7, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsBetaText = new JTextField();
        panel35.add(settingsBetaText, new GridConstraints(32, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel47 = new JPanel();
        panel47.setLayout(new GridLayoutManager(1, 1, new Insets(25, 0, 5, 0), -1, -1));
        panel35.add(panel47, new GridConstraints(38, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        settingsResetDefault = new JButton();
        Font settingsResetDefaultFont = this.$$$getFont$$$(null, Font.BOLD, 14, settingsResetDefault.getFont());
        if (settingsResetDefaultFont != null) settingsResetDefault.setFont(settingsResetDefaultFont);
        this.$$$loadButtonText$$$(settingsResetDefault, ResourceBundle.getBundle("lang").getString("reset_default"));
        panel47.add(settingsResetDefault, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label59 = new JLabel();
        this.$$$loadLabelText$$$(label59, ResourceBundle.getBundle("lang").getString("use_workshop"));
        label59.setToolTipText(ResourceBundle.getBundle("lang").getString("use_workshop_desc"));
        panel35.add(label59, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsUseWorkshopBox = new JCheckBox();
        settingsUseWorkshopBox.setText("");
        panel35.add(settingsUseWorkshopBox, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label60 = new JLabel();
        label60.setText("MB");
        panel35.add(label60, new GridConstraints(18, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label61 = new JLabel();
        label61.setText("MB");
        panel35.add(label61, new GridConstraints(19, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        aboutTab = new JPanel();
        aboutTab.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        aboutTab.setOpaque(true);
        tabbedPane1.addTab("About", aboutTab);
        final JPanel panel48 = new JPanel();
        panel48.setLayout(new GridLayoutManager(1, 1, new Insets(10, 150, 5, 150), -1, -1));
        aboutTab.add(panel48, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JSplitPane splitPane2 = new JSplitPane();
        splitPane2.setEnabled(false);
        splitPane2.setOrientation(0);
        panel48.add(splitPane2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel49 = new JPanel();
        panel49.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        splitPane2.setLeftComponent(panel49);
        final JPanel panel50 = new JPanel();
        panel50.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel49.add(panel50, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        aboutClient = new JLabel();
        aboutClient.setEnabled(true);
        Font aboutClientFont = this.$$$getFont$$$(null, Font.BOLD, 18, aboutClient.getFont());
        if (aboutClientFont != null) aboutClient.setFont(aboutClientFont);
        aboutClient.setText("Client v1.0.1000");
        panel50.add(aboutClient, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        aboutUpdateLabel = new JLabel();
        aboutUpdateLabel.setRequestFocusEnabled(true);
        this.$$$loadLabelText$$$(aboutUpdateLabel, ResourceBundle.getBundle("lang").getString("client_up_to_date"));
        panel50.add(aboutUpdateLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel51 = new JPanel();
        panel51.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel50.add(panel51, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label62 = new JLabel();
        this.$$$loadLabelText$$$(label62, ResourceBundle.getBundle("lang").getString("developer_page"));
        panel51.add(label62, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        aboutDeveloperLabel = new JLabel();
        aboutDeveloperLabel.setRequestFocusEnabled(true);
        aboutDeveloperLabel.setText("github.com");
        panel51.add(aboutDeveloperLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label63 = new JLabel();
        this.$$$loadLabelText$$$(label63, ResourceBundle.getBundle("lang").getString("project_page"));
        panel51.add(label63, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        aboutProjectLabel = new JLabel();
        aboutProjectLabel.setText("gurkengewuerz.de");
        panel51.add(aboutProjectLabel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        updateButton = new JButton();
        this.$$$loadButtonText$$$(updateButton, ResourceBundle.getBundle("lang").getString("update_now"));
        panel50.add(updateButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel52 = new JPanel();
        panel52.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel49.add(panel52, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        aboutLogo = new JLabel();
        aboutLogo.setText("");
        panel52.add(aboutLogo, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        aboutCopyrightLabel = new JLabel();
        aboutCopyrightLabel.setText("Copyright (c) 2020-{year} Niklas Schütrumpf (Gurkengewuerz)");
        panel49.add(aboutCopyrightLabel, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel53 = new JPanel();
        panel53.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane2.setRightComponent(panel53);
        final JScrollPane scrollPane3 = new JScrollPane();
        panel53.add(scrollPane3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        disclaimer = new JTextPane();
        disclaimer.setContentType("text/html");
        disclaimer.setEditable(false);
        disclaimer.setEnabled(true);
        disclaimer.setText("<html>\n  <head>\n    \n  </head>\n  <body>\n  </body>\n</html>\n");
        scrollPane3.setViewportView(disclaimer);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadLabelText$$$(JLabel component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setDisplayedMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadButtonText$$$(AbstractButton component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

    private enum Tab {
        PLAY(0),
        UPDATE(1),
        CHANGELOG(2),
        PRESET(3),
        SETTING(4),
        ABOUT(5);

        private int index;

        Tab(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }
}
