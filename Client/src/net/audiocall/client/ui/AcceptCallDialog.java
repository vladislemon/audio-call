package net.audiocall.client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class AcceptCallDialog extends JDialog {

    private final Consumer<Boolean> acceptConsumer;

    public AcceptCallDialog(Frame owner, ResourceBundle resourceBundle, String caller, Consumer<Boolean> acceptConsumer) {
        super(owner, resourceBundle.getString("AcceptCallDialog.title"), true);
        this.acceptConsumer = acceptConsumer;
        JPanel rootPanel = new JPanel(new BorderLayout());
        JPanel labelPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(String.format(resourceBundle.getString("AcceptCallDialog.label.text"), caller), SwingConstants.CENTER);
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 5));
        JPanel acceptButtonPanel = new JPanel(new BorderLayout());
        JPanel dismissButtonPanel = new JPanel(new BorderLayout());
        JButton acceptButton = new JButton(resourceBundle.getString("AcceptCallDialog.acceptButton.text"));
        acceptButton.addActionListener(this::onAcceptButtonPressed);
        JButton dismissButton = new JButton(resourceBundle.getString("AcceptCallDialog.dismissButton.text"));
        dismissButton.addActionListener(this::onDismissButtonPressed);
        labelPanel.add(label, BorderLayout.CENTER);
        acceptButtonPanel.add(acceptButton, BorderLayout.CENTER);
        dismissButtonPanel.add(dismissButton, BorderLayout.CENTER);
        buttonsPanel.add(acceptButtonPanel);
        buttonsPanel.add(dismissButtonPanel);
        rootPanel.add(labelPanel, BorderLayout.CENTER);
        rootPanel.add(buttonsPanel, BorderLayout.SOUTH);
        add(rootPanel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                AcceptCallDialog.this.onDismissButtonPressed(null);
            }
        });

        setSize(300, 120);
        setResizable(false);
        setLocationRelativeTo(owner);
        setVisible(false);
    }

    private void onAcceptButtonPressed(ActionEvent e) {
        acceptConsumer.accept(true);
        dispose();
    }

    private void onDismissButtonPressed(ActionEvent e) {
        acceptConsumer.accept(false);
        dispose();
    }
}
