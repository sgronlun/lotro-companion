package delta.games.lotro.gui.lore.items.legendary2;

import java.awt.Color;

import javax.swing.Icon;

import delta.common.ui.swing.icons.IconsManager;
import delta.common.ui.swing.windows.WindowController;
import delta.games.lotro.Config;
import delta.games.lotro.character.stats.BasicStatsSet;
import delta.games.lotro.common.stats.StatUtils;
import delta.games.lotro.gui.lore.items.ItemUiTools;
import delta.games.lotro.gui.lore.items.utils.IconNameStatsBundle;
import delta.games.lotro.lore.items.Item;
import delta.games.lotro.lore.items.legendary2.SocketEntryInstance;
import delta.games.lotro.lore.items.legendary2.Tracery;
import delta.games.lotro.utils.ContextPropertyNames;

/**
 * Controller for the UI items to display a single tracery.
 * @author DAM
 */
public class SingleTraceryDisplayController extends IconNameStatsBundle
{
  // Data
  private int _characterLevel;

  /**
   * Constructor.
   * @param parent Parent window.
   */
  public SingleTraceryDisplayController(WindowController parent)
  {
    super();
    Integer characterLevel=parent.getContextProperty(ContextPropertyNames.CHARACTER_LEVEL,Integer.class);
    _characterLevel=(characterLevel!=null)?characterLevel.intValue():Config.getInstance().getMaxCharacterLevel();
    // Initialize with nothing slotted
    setTracery(null,1);
  }

  /**
   * Set current essence.
   * @param traceryInstance Essence to set.
   * @param itemLevel Item level.
   */
  public void setTracery(SocketEntryInstance traceryInstance, int itemLevel)
  {
    // Set icon
    Icon icon=null;
    Item item=null;
    Tracery tracery=(traceryInstance!=null)?traceryInstance.getTracery():null;
    if (tracery!=null)
    {
      item=tracery.getItem();
      icon=ItemUiTools.buildItemIcon(item);
    }
    else
    {
      icon=IconsManager.getIcon(ITEM_WITH_NO_ICON);
    }
    _icon.setIcon(icon);
    // Color
    Color foreground=Color.BLACK;
    if (tracery!=null)
    {
      boolean ok=tracery.isApplicable(_characterLevel,itemLevel);
      foreground=ok?Color.BLACK:Color.RED;
    }
    // Text
    _name.setForegroundColor(foreground);
    String text="";
    if (item!=null)
    {
      text=item.getName();
    }
    _name.setText(text,1);
    // Stats
    if (tracery!=null)
    {
      _stats.setForegroundColor(foreground);
      // Stats
      BasicStatsSet stats=traceryInstance.getStats();
      String[] lines=StatUtils.getStatsDisplayLines(stats);
      _stats.setText(lines);
    }
    else
    {
      _stats.setText(new String[0]);
    }
  }
}
