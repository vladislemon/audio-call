package net.audiocall.client.ui;

import net.audiocall.client.User;

import javax.swing.*;
import java.awt.*;

public class UserListCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        setText(((User) value).getName());
        return component;
    }
}
