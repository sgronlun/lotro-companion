package delta.games.lotro.gui.stats.deeds.statistics;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;

import delta.common.ui.swing.GuiFactory;
import delta.common.ui.swing.tables.TableColumnsChooserController;
import delta.common.ui.swing.windows.WindowController;
import delta.games.lotro.gui.items.CountedItemsTableController;
import delta.games.lotro.lore.items.CountedItem;
import delta.games.lotro.stats.deeds.DeedsStatistics;

/**
 * Controller for the items display panel.
 * @author DAM
 */
public class ItemsDisplayPanelController
{
  // Data
  private DeedsStatistics _stats;
  private List<CountedItem> _items;
  // GUI
  private JPanel _panel;
  private JLabel _statsLabel;
  // Controllers
  private CountedItemsTableController _tableController;
  private WindowController _parent;

  /**
   * Constructor.
   * @param parent Parent window.
   * @param stats Stats to show.
   */
  public ItemsDisplayPanelController(WindowController parent, DeedsStatistics stats)
  {
    _parent=parent;
    _stats=stats;
    _items=new ArrayList<CountedItem>();
    _tableController=new CountedItemsTableController(null,_items,null);
  }

  /**
   * Get the managed panel.
   * @return the managed panel.
   */
  public JPanel getPanel()
  {
    if (_panel==null)
    {
      _panel=build();
    }
    return _panel;
  }

  private JPanel build()
  {
    JPanel panel=GuiFactory.buildBackgroundPanel(new BorderLayout());
    TitledBorder border=GuiFactory.buildTitledBorder("Items");
    panel.setBorder(border);

    // Table
    JTable table=_tableController.getTable();
    JScrollPane scroll=GuiFactory.buildScrollPane(table);
    panel.add(scroll,BorderLayout.CENTER);
    // Stats
    JPanel statsPanel=GuiFactory.buildPanel(new FlowLayout(FlowLayout.LEFT));
    _statsLabel=GuiFactory.buildLabel("-");
    statsPanel.add(_statsLabel);
    JButton choose=GuiFactory.buildButton("Choose columns...");
    ActionListener al=new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        TableColumnsChooserController<CountedItem> chooser=new TableColumnsChooserController<CountedItem>(_parent,_tableController.getTableController());
        chooser.editModal();
      }
    };
    choose.addActionListener(al);
    statsPanel.add(choose);
    panel.add(statsPanel,BorderLayout.NORTH);
    return panel;
  }

  /**
   * Update display.
   */
  public void update()
  {
    updateStatsLabel();
    _tableController.update();
  }

  private void updateStatsLabel()
  {
    _items.clear();
    _items.addAll(_stats.getItems());
    int nbItems=_items.size();
    String label="Item(s): "+nbItems;
    _statsLabel.setText(label);
  }

  /**
   * Release all managed resources.
   */
  public void dispose()
  {
    // Data
    _stats=null;
    // GUI
    if (_panel!=null)
    {
      _panel.removeAll();
      _panel=null;
    }
    _statsLabel=null;
    // Controllers
    _tableController=null;
    _parent=null;
  }
}
