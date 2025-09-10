package com.materiel.client;

import com.materiel.client.config.AppConfig;
import com.materiel.client.config.DataMode;
import com.materiel.client.view.MainFrame;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Vérifie la présence des menus et le branchement basique des panels.
 */
public class UiWiringSmokeTest {
    @Test
    void menusShouldShowPanels() {
        System.setProperty("java.awt.headless", "true");
        AppConfig.getInstance().setDataMode(DataMode.MOCK_JSON);
        MainFrame frame = new MainFrame();
        JMenuBar bar = frame.getJMenuBar();
        assertNotNull(findMenu(bar, "Ventes"));
        assertNotNull(findMenu(bar, "Debug"));

        frame.navigateToPanel("COMMANDES");
        assertTrue(hasTableWithRows(frame));
        frame.navigateToPanel("BONS_LIVRAISON");
        assertTrue(hasTableWithRows(frame));
        frame.navigateToPanel("FACTURES");
        assertTrue(hasTableWithRows(frame));
        frame.dispose();
    }

    private JMenu findMenu(JMenuBar bar, String name) {
        for (int i = 0; i < bar.getMenuCount(); i++) {
            JMenu m = bar.getMenu(i);
            if (m != null && name.equals(m.getText())) return m;
        }
        return null;
    }

    private boolean hasTableWithRows(Container c) {
        if (c instanceof JTable) {
            return ((JTable)c).getRowCount() > 0;
        }
        for (Component comp : c.getComponents()) {
            if (comp instanceof Container && hasTableWithRows((Container) comp)) {
                return true;
            }
        }
        return false;
    }
}
