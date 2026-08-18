package client.admin;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

// Shared look-and-feel for the Admin module, matching the Doctor/Receptionist
// and Patient portals so all four client modules present a consistent theme.
public final class AdminTheme {

    public static final Color PRIMARY_COLOR = new Color(15, 118, 110);    // Medical Teal
    public static final Color BG_COLOR = new Color(241, 245, 249);         // Slate Gray
    public static final Color CARD_BG = Color.WHITE;
    public static final Color TEXT_DARK = new Color(30, 41, 59);
    public static final Color SUCCESS_COLOR = new Color(22, 101, 52);
    public static final Color ERROR_COLOR = new Color(153, 27, 27);

    public static final Font FONT_HEADER = new Font("SansSerif", Font.BOLD, 18);
    public static final Font FONT_SUBHEADER = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font FONT_LABEL = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_INPUT = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_BTN = new Font("SansSerif", Font.BOLD, 13);

    private AdminTheme() {
    }

    public static void applySystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }

    public static JPanel createHeaderPanel(String subtitle, JLabel serverStatusLabel) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("BrightCare Medical Center");
        title.setFont(FONT_HEADER);
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel(subtitle);
        sub.setFont(FONT_SUBHEADER);
        sub.setForeground(new Color(204, 251, 241));

        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 2));
        titles.setOpaque(false);
        titles.add(title);
        titles.add(sub);

        serverStatusLabel.setFont(new Font("SansSerif", Font.BOLD, 12));

        header.add(titles, BorderLayout.WEST);
        header.add(serverStatusLabel, BorderLayout.EAST);
        return header;
    }

    public static void setServerStatus(JLabel serverStatusLabel, boolean connected) {
        if (connected) {
            serverStatusLabel.setText("● Server Connected");
            serverStatusLabel.setForeground(new Color(187, 247, 208));
        } else {
            serverStatusLabel.setText("● Offline");
            serverStatusLabel.setForeground(new Color(254, 202, 202));
        }
    }

    public static JTextField createStyledTextField() {
        JTextField field = new JTextField(18);
        field.setFont(FONT_INPUT);
        field.setBorder(new CompoundBorder(
                new LineBorder(new Color(203, 213, 225), 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return field;
    }

    public static JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(18);
        field.setFont(FONT_INPUT);
        field.setBorder(new CompoundBorder(
                new LineBorder(new Color(203, 213, 225), 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return field;
    }

    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BTN);
        btn.setBackground(PRIMARY_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BTN);
        btn.setBackground(new Color(226, 232, 240));
        btn.setForeground(TEXT_DARK);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        return btn;
    }

    public static JLabel createStatusLabel() {
        JLabel label = new JLabel(" ", SwingConstants.LEFT);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return label;
    }

    public static void setSuccessStatus(JLabel statusLabel, String message) {
        statusLabel.setForeground(SUCCESS_COLOR);
        statusLabel.setText("✔  " + message);
    }

    public static void setErrorStatus(JLabel statusLabel, String message) {
        statusLabel.setForeground(ERROR_COLOR);
        statusLabel.setText("✖  " + message);
    }

    public static JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(FONT_INPUT);
        table.setRowHeight(28);
        table.setGridColor(new Color(226, 232, 240));
        table.getTableHeader().setFont(FONT_LABEL);
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.getTableHeader().setForeground(TEXT_DARK);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        return table;
    }

    public static GridBagConstraints defaultGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    public static void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel label = new JLabel(labelText);
        label.setFont(FONT_LABEL);
        label.setForeground(TEXT_DARK);
        panel.add(label, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    // fillSpace = true lets the card's content (e.g. a table) grow to fill the tab;
    // false keeps the card compact at the top, like a plain form.
    public static JPanel createCardPanel(String titleText, JComponent contentComponent, boolean fillSpace) {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(20, 25, 20, 25)
        ));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(PRIMARY_COLOR);
        title.setBorder(new EmptyBorder(0, 0, 10, 0));

        card.add(title, BorderLayout.NORTH);
        card.add(contentComponent, BorderLayout.CENTER);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG_COLOR);
        outer.setBorder(new EmptyBorder(10, 10, 10, 10));

        if (fillSpace) {
            outer.add(card, BorderLayout.CENTER);
        } else {
            outer.add(card, BorderLayout.NORTH);
        }

        return outer;
    }
}
