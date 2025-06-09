package bcu.cmp5332.bookingsystem.gui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public final class DesignConstants {

    public static final Color PRIMARY_BLUE = new Color(34, 107, 172);
    public static final Color LIGHT_GRAY_BG = new Color(245, 245, 245);
    public static final Color TEXT_DARK = new Color(44, 62, 80);
    public static final Color TABLE_GRID_COLOR = new Color(220, 220, 220);
    public static final Color TABLE_SELECTION_BACKGROUND = new Color(230, 240, 255);
    public static final Color BUTTON_BLUE = new Color(52, 152, 219);
    public static final Color BUTTON_HOVER_BLUE = new Color(41, 128, 185);
    public static final Color BUTTON_TEXT_COLOR = Color.BLACK;
    public static final Color TEXT_MUTED = Color.GRAY;
    public static final Color ACCENT_COLOR = new Color(230, 126, 34);
    public static final Color TEXT_HIGHLIGHT = new Color(39, 174, 96);
    public static final Color STATUS_ACTIVE_GREEN = new Color(46, 204, 113);
    public static final Color STATUS_CANCELLED_RED = new Color(231, 76, 60);
    // Added STATUS_DEPARTED_ORANGE here
    public static final Color STATUS_DEPARTED_ORANGE = new Color(255, 140, 0); // A bright orange for departed flights
    public static final Color REBOOK_BUTTON_ORANGE = new Color(255, 165, 0);
    public static final Color REBOOK_BUTTON_HOVER_ORANGE = new Color(204, 133, 0);
    public static final Color INFO_PANEL_TITLE_COLOR = new Color(50, 50, 150);
    public static final Color CLOSE_BUTTON_BG = new Color(100, 150, 200);
    public static final Color CLOSE_BUTTON_HOVER = new Color(80, 120, 160);
    public static final Color TABLE_HEADER_BG_COLOR = new Color(230, 230, 240);
    public static final Color TABLE_HEADER_FOREGROUND_COLOR = new Color(40, 40, 40);
    public static final Color REMOVE_BUTTON_RED = new Color(200, 70, 70);
    public static final Color REMOVE_BUTTON_HOVER_RED = new Color(160, 56, 56);
    public static final Color DISABLED_BUTTON_BG = new Color(180, 180, 180);

    // Changed AVAILABLE_SEATS_FULL to a darker green
    public static final Color AVAILABLE_SEATS_FULL = new Color(0, 100, 0); // Original: (0, 120, 0)
    public static final Color AVAILABLE_SEATS_LOW_BG = new Color(255, 240, 220);
    public static final Color AVAILABLE_SEATS_LOW_FG = new Color(200, 100, 0);
    public static final Color AVAILABLE_SEATS_NONE_BG = new Color(255, 220, 220);
    public static final Color AVAILABLE_SEATS_NONE_FG = new Color(180, 0, 0);
    public static final Color TABLE_CELL_DEFAULT_BG = new Color(250, 250, 250);

    public static final Color CANCEL_BUTTON_HOVER_RED = STATUS_CANCELLED_RED.darker();
    public static final Color SECONDARY_BUTTON_BG = Color.LIGHT_GRAY;
    public static final Color SECONDARY_BUTTON_HOVER_BG = Color.LIGHT_GRAY.darker();
    public static final Color SAVE_BUTTON_HOVER_GREEN = STATUS_ACTIVE_GREEN.darker();

    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font TABLE_ROW_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font TABLE_EMPTY_MESSAGE_FONT = new Font("Segoe UI", Font.ITALIC, 14);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font SMALL_ITALIC_FONT = new Font("Segoe UI", Font.ITALIC, 10);
    public static final Font BOLD_PRICE_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font DETAILS_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font DETAILS_LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font DETAILS_VALUE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font STATUS_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font TITLED_BORDER_FONT = new Font("SansSerif", Font.BOLD, 14);
    public static final Font INFO_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font INFO_VALUE_FONT = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font BUDGET_INFO_FONT = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font MESSAGE_LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public static final Border MAIN_PANEL_BORDER = new EmptyBorder(15, 15, 15, 15);
    public static final Border HEADER_PANEL_BORDER = BorderFactory.createEmptyBorder(10, 0, 10, 0);
    public static final Border SCROLL_PANE_BORDER = BorderFactory.createLineBorder(PRIMARY_BLUE.darker(), 1);
    public static final Border BUTTON_PADDING_BORDER = BorderFactory.createEmptyBorder(8, 15, 8, 15);
    public static final Border TITLED_BORDER_STYLE = BorderFactory.createLineBorder(ACCENT_COLOR, 1);
    public static final Border DETAILS_PANEL_INNER_BORDER = new EmptyBorder(15, 15, 15, 15);
    public static final Border DETAILS_PANEL_OUTER_BORDER = BorderFactory.createLineBorder(PRIMARY_BLUE.darker(), 1);
    public static final Border DETAILS_PANEL_COMPOUND_BORDER = BorderFactory.createCompoundBorder(
        DETAILS_PANEL_OUTER_BORDER,
        DETAILS_PANEL_INNER_BORDER
    );
    public static final Border ETCHED_BORDER = BorderFactory.createEtchedBorder();
    public static final Border RAISED_BEVEL_BORDER = BorderFactory.createRaisedBevelBorder();
    public static final Border BUTTON_BEVEL_PADDING_BORDER = BorderFactory.createCompoundBorder(
        RAISED_BEVEL_BORDER,
        new EmptyBorder(5, 15, 5, 15)
    );

    public static final Border FORM_PANEL_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(PRIMARY_BLUE.darker(), 1),
        new EmptyBorder(15, 15, 15, 15)
    );
    public static final Border TOP_BOTTOM_HEADER_BORDER = BorderFactory.createEmptyBorder(10, 0, 10, 0);

    public static final int MAIN_PANEL_H_GAP = 10;
    public static final int MAIN_PANEL_V_GAP = 10;
    public static final int BUTTON_PANEL_H_GAP = 15;
    public static final int BUTTON_PANEL_V_GAP = 15;
    public static final int BUTTON_PANEL_BOTTOM_PADDING = 5;
    public static final int DETAILS_INSET_GAP = 5;
    public static final int INFO_PANEL_INSET_GAP_H = 8;
    public static final int INFO_PANEL_INSET_GAP_V = 8;
    public static final int FORM_INSET_GAP = 8;

    public static final int TABLE_ROW_HEIGHT = 25;

    public static final int COMPONENT_HEIGHT = 25;

    private DesignConstants() {
    }
}