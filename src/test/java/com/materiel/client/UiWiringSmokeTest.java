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
        assertTrue(hasWiredOk(frame));
        frame.navigateToPanel("BONS_LIVRAISON");
        assertTrue(hasWiredOk(frame));
        frame.navigateToPanel("FACTURES");
        assertTrue(hasWiredOk(frame));
        frame.dispose();
    }

    private JMenu findMenu(JMenuBar bar, String name) {
        for (int i = 0; i < bar.getMenuCount(); i++) {
            JMenu m = bar.getMenu(i);
            if (m != null && name.equals(m.getText())) return m;
        }
        return null;
    }

    private boolean hasWiredOk(Container c) {
        if (c instanceof JLabel) {
            return "WIRED_OK".equals(((JLabel)c).getText());
        }
        for (Component comp : c.getComponents()) {
            if (comp instanceof Container && hasWiredOk((Container) comp)) {
                return true;
            }
        }
        return false;
    }
}
