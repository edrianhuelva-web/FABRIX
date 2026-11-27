package fabrix;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/** Base class for every screen in the main window. */
public abstract class Page extends JPanel {
    protected final InventoryService svc;
    protected final DashboardFrame app;

    protected Page(InventoryService svc, DashboardFrame app) {
        super(new BorderLayout(0, 24));
        this.svc = svc;
        this.app = app;
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(32, 32, 28, 32));
    }

    /** Re-reads data from the service and redraws. */
    public abstract void refresh();
}
