package uk.ac.cam.cal56.graphics;

import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class TabbedDisplayPanel extends JTabbedPane {

    private DisplayPanel activePanel;

    public void frameUpdate() {
        if(activePanel==null) return;
        updateActivePanel();
        activePanel.frameUpdate();
    }

    public void drawPlotsAndLabels() {
        updateActivePanel();
        activePanel.drawPlotsAndLabels();
    }

    private void updateActivePanel() {
        DisplayPanel currentPanel = (DisplayPanel) getSelectedComponent();
        if (currentPanel != activePanel) {
            if (activePanel != null)
                activePanel.setEnabled(false);
            activePanel = currentPanel;
            activePanel.setEnabled(true);
        }
    }
}
