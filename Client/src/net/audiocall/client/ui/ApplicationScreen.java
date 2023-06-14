package net.audiocall.client.ui;

import net.audiocall.Constants;
import net.audiocall.client.User;
import net.audiocall.client.Application;
import net.audiocall.client.util.SwingUtil;
import net.audiocall.client.util.ObservableList;
import net.audiocall.client.util.ObservableListModel;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

public class ApplicationScreen {

    private final Application owner;
    private final ResourceBundle resourceBundle;
    private final ObservableList<User> users;

    private JPanel rootPanel;
    private JTabbedPane tabbedPane;
    private JPanel homePanel;
    private JPanel connectPanel;
    private JButton connectButton;
    private JLabel connectStatusLabel;
    private JSplitPane homeSplitPane;
    private JScrollPane usersScrollPane;
    private JList<User> usersList;
    private JPanel callPanel;
    private JTextArea userTextArea;
    private JButton callButton;
    private JPanel settingsPanel;
    private JTextField addressField;

    public ApplicationScreen(Application owner, ResourceBundle resourceBundle, ObservableList<User> users) {
        this.owner = owner;
        this.resourceBundle = resourceBundle;
        this.users = users;
        createRootPanel();
    }

    public Container getRoot() {
        return rootPanel;
    }

    public void packTabs() {
        int tabCount = tabbedPane.getTabCount();
        JLabel[] labels = new JLabel[tabCount];
        for (int i = 0; i < tabCount; i++) {
            labels[i] = new JLabel(tabbedPane.getTitleAt(i));
            labels[i].setHorizontalAlignment(SwingConstants.CENTER);
            labels[i].setFocusable(false);
        }
        Dimension tabSize = new Dimension(
                tabbedPane.getWidth() / tabCount,
                Constants.CLIENT_TAB_HEIGHT
        );
        for (int i = 0; i < tabCount; i++) {
            labels[i].setPreferredSize(tabSize);
            tabbedPane.setTabComponentAt(i, labels[i]);
        }
        tabSize.width--;
        int indexToResize = 0;
        while (tabbedPane.getUI().getTabRunCount(tabbedPane) > 1) {
            if (indexToResize >= tabCount) {
                tabSize.width--;
                indexToResize = 0;
            }
            labels[indexToResize].setPreferredSize(tabSize);
            tabbedPane.revalidate();
            indexToResize++;
        }
    }

    public void dispose() {
        ((ObservableListModel<User>) usersList.getModel()).destroy();
    }

    public void onConnect(String username) {
        SwingUtil.runInUIThread(() -> {
            connectButton.setText(resourceBundle.getString("ApplicationScreen.HomeTab.connectButton.disconnect"));
            connectStatusLabel.setText(String.format(resourceBundle.getString("ApplicationScreen.HomeTab.connectStatusLabel.connected"), addressField.getText()));
            owner.setTitle(String.format("%s [%s]", Constants.CLIENT_TITLE, username));
        });
    }

    public void onDisconnect() {
        SwingUtil.runInUIThread(() -> {
            connectButton.setText(resourceBundle.getString("ApplicationScreen.HomeTab.connectButton.connect"));
            connectStatusLabel.setText(resourceBundle.getString("ApplicationScreen.HomeTab.connectStatusLabel.disconnected"));
            owner.setTitle(String.format("%s", Constants.CLIENT_TITLE));
        });
    }

    public void onCall(String with) {
        SwingUtil.runInUIThread(() -> {
            callButton.setText(resourceBundle.getString("ApplicationScreen.HomeTab.callButton.hangup"));
            connectStatusLabel.setText(String.format(resourceBundle.getString("ApplicationScreen.HomeTab.connectStatusLabel.conversation"), with));
        });
    }

    public void onHangup() {
        SwingUtil.runInUIThread(() -> {
            callButton.setText(resourceBundle.getString("ApplicationScreen.HomeTab.callButton.call"));
            connectStatusLabel.setText(String.format(resourceBundle.getString("ApplicationScreen.HomeTab.connectStatusLabel.connected"), addressField.getText()));
        });
    }

    private void createRootPanel() {
        rootPanel = new JPanel(new BorderLayout());
        rootPanel.setPreferredSize(new Dimension(400, 600));
        createTabbedPane();
        rootPanel.add(tabbedPane, BorderLayout.CENTER);
    }

    private void createTabbedPane() {
        tabbedPane = new JTabbedPane(SwingConstants.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
        createHomePanel();
        createSettingsPanel();
        tabbedPane.addTab(
                resourceBundle.getString("ApplicationScreen.HomeTab.title"),
                homePanel
        );
        tabbedPane.addTab(
                resourceBundle.getString("ApplicationScreen.SettingsTab.title"),
                settingsPanel
        );
    }

    private void createHomePanel() {
        homePanel = new JPanel(new BorderLayout(0, 3));
        createConnectPanel();
        createHomeSplitPane();
        homePanel.add(connectPanel, BorderLayout.NORTH);
        homePanel.add(homeSplitPane, BorderLayout.CENTER);
    }

    private void createConnectPanel() {
        connectPanel = new JPanel(new BorderLayout(25, 0));
        createConnectButton();
        createConnectStatusLabel();
        connectPanel.add(connectButton, BorderLayout.WEST);
        connectPanel.add(connectStatusLabel, BorderLayout.CENTER);
    }

    private void createConnectButton() {
        connectButton = new JButton(resourceBundle.getString("ApplicationScreen.HomeTab.connectButton.connect"));
        connectButton.setPreferredSize(new Dimension(140, 25));
        connectButton.addActionListener(e -> {
            if (owner.isConnected()) {
                owner.disconnect();
            } else {
                new ConnectDialog(owner, resourceBundle, s -> owner.connectToServer(addressField.getText(), Constants.COMMON_TCP_PORT, s));
            }
        });
    }

    private void createConnectStatusLabel() {
        connectStatusLabel = new JLabel(resourceBundle.getString("ApplicationScreen.HomeTab.connectStatusLabel.disconnected"));
    }

    private void createHomeSplitPane() {
        createUsersScrollPane();
        createCallPanel();
        homeSplitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                usersScrollPane,
                callPanel
        );
        homeSplitPane.setDividerLocation(140);
    }

    private void createUsersScrollPane() {
        createUsersList();
        usersScrollPane = new JScrollPane(usersList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }

    private void createUsersList() {
        usersList = new JList<>(new ObservableListModel<>(users));
        usersList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersList.setCellRenderer(new UserListCellRenderer());
        User selected = usersList.getSelectedValue();
        usersList.addListSelectionListener(e -> userTextArea.setText(selected != null ? selected.toString() : ""));
    }

    private void createCallPanel() {
        callPanel = new JPanel(new BorderLayout());
        createUserLabel();
        createCallButton();
        callPanel.add(userTextArea, BorderLayout.CENTER);
        callPanel.add(callButton, BorderLayout.SOUTH);
    }

    private void createUserLabel() {
        userTextArea = new JTextArea();
        userTextArea.setFont(userTextArea.getFont().deriveFont(Constants.CLIENT_USER_INFO_FONT_SIZE));
        userTextArea.setLineWrap(true);
        userTextArea.setWrapStyleWord(true);
        userTextArea.setEditable(false);
    }

    private void createCallButton() {
        callButton = new JButton(resourceBundle.getString("ApplicationScreen.HomeTab.callButton.call"));
        callButton.addActionListener(e -> {
            int selected = usersList.getSelectedIndex();
            String username = null;
            if(selected > -1) {
                username = usersList.getModel().getElementAt(selected).getName();
            }
            owner.callOrHangup(username);
        });
    }

    private void createSettingsPanel() {
        settingsPanel = new JPanel(new BorderLayout());
        createAddressField();
        settingsPanel.add(addressField, BorderLayout.NORTH);
    }

    private void createAddressField() {
        try {
            addressField = new JTextField(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            addressField = new JTextField("");
        }
    }
}
