package net.audiocall.client.ui;

import net.audiocall.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class ConnectDialog extends JDialog {

    private final Consumer<String> nameConsumer;
    private final JTextField textField;

    public ConnectDialog(Frame owner, ResourceBundle resourceBundle, Consumer<String> nameConsumer) {
        super(owner, resourceBundle.getString("ConnectDialog.title"), true);
        this.nameConsumer = nameConsumer;
        JPanel rootPanel = new JPanel(new BorderLayout());
        this.textField = new JTextField();
        this.textField.addActionListener(this::onButtonPressed);
        JButton button = new JButton(resourceBundle.getString("ConnectDialog.button.text"));
        button.addActionListener(this::onButtonPressed);
        rootPanel.add(textField, BorderLayout.CENTER);
        rootPanel.add(button, BorderLayout.SOUTH);
        add(rootPanel);
        setSize(300, 90);
        setResizable(false);
        setLocationRelativeTo(owner);
        setVisible(true);
    }

    private void onButtonPressed(ActionEvent e) {
        String name = textField.getText();
        if(name != null && !name.isBlank() && name.length() <= Constants.CLIENT_NAME_MAX_LENGTH) {
            nameConsumer.accept(name);
            this.dispose();
        } else {
            textField.setText("");
            textField.requestFocus();
        }
    }
}
