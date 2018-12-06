package delta.games.lotro.gui.character.essences;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import delta.common.ui.swing.GuiFactory;
import delta.games.lotro.character.CharacterData;
import delta.games.lotro.character.stats.BasicStatsSet;
import delta.games.lotro.character.stats.STAT;
import delta.games.lotro.character.stats.base.DerivedStatsContributionsMgr;
import delta.games.lotro.character.stats.base.io.DerivedStatContributionsIO;
import delta.games.lotro.common.CharacterClass;
import delta.games.lotro.gui.character.essences.EssencesSummary.EssenceCount;
import delta.games.lotro.gui.character.stats.StatLabels;
import delta.games.lotro.gui.items.ItemUiTools;
import delta.games.lotro.lore.items.Item;
import delta.games.lotro.utils.FixedDecimalsInteger;

/**
 * Controller for a panel to show a summary of all the essences of a character.
 * @author DAM
 */
public class EssencesSummaryPanelController
{
  // Data
  private CharacterData _toon;
  private EssencesSummary _summary;
  // UI
  private JPanel _panel;
  private JLabel _total;
  private JPanel _countsPanel;
  private JPanel _rawStatsPanel;
  private JPanel _cumulatedStatsPanel;

  /**
   * Constructor.
   * @param toon Managed toon.
   */
  public EssencesSummaryPanelController(CharacterData toon)
  {
    _toon=toon;
    _summary=new EssencesSummary(toon);
    _panel=buildPanel();
    update();
  }

  private JPanel buildPanel()
  {
    JPanel panel=GuiFactory.buildBackgroundPanel(new GridBagLayout());
    // Total
    GridBagConstraints c=new GridBagConstraints(0,0,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(0,0,0,0),0,0);
    _total=GuiFactory.buildLabel("");
    panel.add(_total,c);
    // Counts
    _countsPanel=GuiFactory.buildPanel(new GridBagLayout());
    TitledBorder countsBorder=GuiFactory.buildTitledBorder("Essence usage");
    _countsPanel.setBorder(countsBorder);
    c=new GridBagConstraints(0,1,1,1,0.0,0.0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE,new Insets(0,0,0,0),0,0);
    panel.add(_countsPanel,c);
    // Stats
    // - raw
    _rawStatsPanel=GuiFactory.buildPanel(new GridBagLayout());
    TitledBorder rawBorder=GuiFactory.buildTitledBorder("Raw stats");
    _rawStatsPanel.setBorder(rawBorder);
    c=new GridBagConstraints(1,1,1,1,0.0,0.0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE,new Insets(0,0,0,0),0,0);
    panel.add(_rawStatsPanel,c);
    // - cumulated
    _cumulatedStatsPanel=GuiFactory.buildPanel(new GridBagLayout());
    TitledBorder cumulatedBorder=GuiFactory.buildTitledBorder("Cumulated stats");
    _cumulatedStatsPanel.setBorder(cumulatedBorder);
    c=new GridBagConstraints(2,1,1,1,0.0,0.0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE,new Insets(0,0,0,0),0,0);
    panel.add(_cumulatedStatsPanel,c);
    return panel;
  }

  /**
   * Update display.
   */
  public void update()
  {
    _summary.update();
    // Totals
    int essencesCount=_summary.getEssencesCount();
    int slotsCount=_summary.getSlotsCount();
    String label="Essences: " + essencesCount + " / " + slotsCount;
    _total.setText(label);
    // Essence counts
    updateEssenceCountsPanel();
    // Stats
    updateStatsPanel();
  }

  private void updateEssenceCountsPanel()
  {
    _countsPanel.removeAll();
    List<EssenceCount> counts=_summary.getCounts();
    int rowIndex=0;
    GridBagConstraints strutConstraints=new GridBagConstraints(0,rowIndex,3,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(0,5,0,0),0,0);
    _countsPanel.add(Box.createHorizontalStrut(100),strutConstraints);
    rowIndex++;
    for(EssenceCount count : counts)
    {
      SingleEssenceCountController controller=new SingleEssenceCountController(count);
      // Count label
      JLabel countLabel=controller.getCount();
      GridBagConstraints c=new GridBagConstraints(0,rowIndex,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(0,5,0,0),0,0);
      _countsPanel.add(countLabel,c);
      // Icon
      JLabel icon=controller.getIcon();
      c=new GridBagConstraints(1,rowIndex,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,5,2,0),0,0);
      _countsPanel.add(icon,c);
      // Name
      JLabel name=controller.getEssenceName();
      c=new GridBagConstraints(2,rowIndex,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,5),0,0);
      _countsPanel.add(name,c);
      rowIndex++;
    }
  }

  private void updateStatsPanel()
  {
    // Raw
    BasicStatsSet raw=_summary.getStats();
    updateStatsPanel(_rawStatsPanel,raw);
    // Cumulated
    DerivedStatsContributionsMgr derivedStatsMgr=DerivedStatContributionsIO.load();
    CharacterClass characterClass=_toon.getCharacterClass();
    BasicStatsSet derivated=derivedStatsMgr.getContribution(characterClass,raw);
    BasicStatsSet cumulated=new BasicStatsSet();
    cumulated.addStats(raw);
    cumulated.addStats(derivated);
    updateStatsPanel(_cumulatedStatsPanel,cumulated);
  }

  private void updateStatsPanel(JPanel panel, BasicStatsSet stats)
  {
    panel.removeAll();

    int rowIndex=0;
    GridBagConstraints strutConstraints=new GridBagConstraints(0,rowIndex,3,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(0,5,0,0),0,0);
    panel.add(Box.createHorizontalStrut(100),strutConstraints);
    rowIndex++;

    int statsCount=stats.getStatsCount();
    if (statsCount>0)
    {
      // Grab toon stats
      BasicStatsSet toonStats=_toon.getStats();

      // Build display
      for(STAT stat : STAT.values())
      {
        FixedDecimalsInteger value=stats.getStat(stat);
        if (value!=null)
        {
          // Value label
          JLabel valueLabel=GuiFactory.buildLabel(value.toString());
          GridBagConstraints c=new GridBagConstraints(0,rowIndex,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(0,5,0,0),0,0);
          panel.add(valueLabel,c);
          // Name label
          String name=StatLabels.getStatLabel(stat);
          JLabel statLabel=GuiFactory.buildLabel(name);
          c=new GridBagConstraints(1,rowIndex,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,0),0,0);
          panel.add(statLabel,c);
          // Percentage
          FixedDecimalsInteger toonStat=toonStats.getStat(stat);
          String percentageStr="";
          if (toonStat!=null)
          {
            float percentage=100*(value.floatValue()/toonStat.floatValue());
            percentageStr=String.format("%.1f%%",Float.valueOf(percentage));
          }
          JLabel percentageLabel=GuiFactory.buildLabel(percentageStr);
          c=new GridBagConstraints(2,rowIndex,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(0,5,0,5),0,0);
          panel.add(percentageLabel,c);
          rowIndex++;
        }
      }
    }
  }

  /**
   * Get the managed panel.
   * @return the managed panel.
   */
  public JPanel getPanel()
  {
    return _panel;
  }

  /**
   * Release all managed resources.
   */
  public void dispose()
  {
    // Data
    _toon=null;
    _summary=null;
    // UI
    if (_panel!=null)
    {
      _panel.removeAll();
      _panel=null;
    }
    _total=null;
    _countsPanel=null;
    _rawStatsPanel=null;
    _cumulatedStatsPanel=null;
  }

  private static class SingleEssenceCountController
  {
    private JLabel _count;
    private JLabel _icon;
    private JLabel _essenceName;

    public SingleEssenceCountController(EssenceCount essenceCount)
    {
      // Count
      int count=essenceCount.getCount();
      String label=count+" x ";
      _count=GuiFactory.buildLabel(label);
      // Icon
      Item essence=essenceCount.getEssence();
      Icon icon=ItemUiTools.buildItemIcon(essence);
      _icon=GuiFactory.buildIconLabel(icon);
      // Name
      _essenceName=GuiFactory.buildLabel(essence.getName());
    }

    /**
     * Get the label for the display of the essence count.
     * @return a label.
     */
    public JLabel getCount()
    {
      return _count;
    }

    /**
     * Get the label for the display of the essence icon.
     * @return a label.
     */
    public JLabel getIcon()
    {
      return _icon;
    }

    /**
     * Get the label for the display of the essence name.
     * @return a label.
     */
    public JLabel getEssenceName()
    {
      return _essenceName;
    }
  }
}
