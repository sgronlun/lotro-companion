package delta.games.lotro.gui.character.stats.curves;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import delta.common.ui.swing.GuiFactory;
import delta.games.lotro.character.stats.BasicStatsSet;
import delta.games.lotro.character.stats.ratings.RatingCurve;
import delta.games.lotro.common.stats.StatDescription;
import delta.games.lotro.gui.character.stats.StatDisplayUtils;
import delta.games.lotro.utils.FixedDecimalsInteger;

/**
 * Controller for a panel to display stat values.
 * @author DAM
 */
public class StatValuesPanelController
{
  // UI
  private JPanel _panel;
  // Data
  private StatCurvesChartConfiguration _config;
  private List<FixedDecimalsInteger> _deltaValues;
  private BasicStatsSet _values;

  /**
   * Constructor.
   * @param config Curves configuration.
   */
  public StatValuesPanelController(StatCurvesChartConfiguration config)
  {
    _config=config;
    _panel=GuiFactory.buildPanel(new GridBagLayout());
    _deltaValues=new ArrayList<FixedDecimalsInteger>();
    _values=new BasicStatsSet();
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
   * Update this panel with new stats.
   * @param values Stats to display.
   */
  public void update(BasicStatsSet values)
  {
    _values=new BasicStatsSet(values);
    _deltaValues.clear();
    update();
  }

  /**
   * Update this panel.
   */
  public void update()
  {
    buildContents();
    _panel.revalidate();
    _panel.repaint();
  }

  private void buildContents()
  {
    // Clear
    _panel.removeAll();

    GridBagConstraints c=new GridBagConstraints(0,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,5,2,5),0,0);
    // First line: Base stat
    // - First cell blank
    c.gridx++;
    // - Stat name
    StatDescription baseStat=_config.getBaseStat();
    JLabel baseStatNameLabel=GuiFactory.buildLabel(baseStat.getName());
    _panel.add(baseStatNameLabel,c);
    // - Stat value
    c.gridx++;
    FixedDecimalsInteger baseStatValue=_values.getStat(baseStat);
    JLabel baseStatValueLabel=GuiFactory.buildLabel(StatDisplayUtils.getStatDisplay(baseStatValue,baseStat.isPercentage()));
    _panel.add(baseStatValueLabel,c);
    c.gridy++;

    boolean doComputeDelta=(_deltaValues.size()==0);
    // Stat lines
    int level=_config.getLevel();
    List<SingleStatCurveConfiguration> curveConfigs=_config.getCurveConfigurations();
    int nbCurves=curveConfigs.size();
    int index=0;
    for(int curveIndex=0;curveIndex<nbCurves;curveIndex++)
    {
      SingleStatCurveConfiguration curveConfig=curveConfigs.get(curveIndex);
      Color color=StatCurveChartPanelController.LINE_COLORS[curveIndex];
      List<StatDescription> stats=curveConfig.getStats();
      for(StatDescription stat : stats)
      {
        c.gridx=0;
        c.fill=GridBagConstraints.NONE;
        c.weightx=0.0;
        // Add color
        JLabel coloredLabel=buildColoredLabel(color);
        _panel.add(coloredLabel,c);
        c.gridx++;
        // Stat name
        JLabel statNameLabel=GuiFactory.buildLabel(stat.getName());
        _panel.add(statNameLabel,c);
        c.gridx++;
        // Stat value
        FixedDecimalsInteger statValueFromCurve=getStatValue(curveConfig,level,baseStatValue);
        FixedDecimalsInteger statValue=_values.getStat(stat);
        String statValueFromCurveStr=StatDisplayUtils.getStatDisplay(statValueFromCurve,stat.isPercentage());
        JLabel statValueFromCurveLabel=GuiFactory.buildLabel(statValueFromCurveStr);
        _panel.add(statValueFromCurveLabel,c);
        c.gridx++;
        // Bonus
        FixedDecimalsInteger deltaValue=null;
        if (doComputeDelta)
        {
          deltaValue=getDeltaValue(statValueFromCurve,statValue);
          _deltaValues.add(deltaValue);
        }
        else
        {
          deltaValue=_deltaValues.get(index);
        }
        if (deltaValue!=null)
        {
          String deltaValueStr=StatDisplayUtils.getStatDisplay(deltaValue,stat.isPercentage());
          if (deltaValue.getInternalValue()>0) deltaValueStr="+"+deltaValueStr;
          FixedDecimalsInteger totalValue=new FixedDecimalsInteger(statValueFromCurve);
          totalValue.add(deltaValue);
          String totalValueStr=StatDisplayUtils.getStatDisplay(totalValue,stat.isPercentage());
          String bonusStr=deltaValueStr+" = "+totalValueStr;
          JLabel bonusLabel=GuiFactory.buildLabel(bonusStr);
          _panel.add(bonusLabel,c);
          c.gridx++;
        }
        // Add 'buffer' empty column
        {
          c.fill=GridBagConstraints.BOTH;
          c.weightx=1.0;
          Component glue=Box.createGlue();
          _panel.add(glue,c);
        }
        c.gridx++;
        c.gridy++;
        index++;
      }
    }
  }

  private FixedDecimalsInteger getDeltaValue(FixedDecimalsInteger statValueFromCurve, FixedDecimalsInteger statValue)
  {
    FixedDecimalsInteger deltaValue=null;
    if ((statValueFromCurve!=null) && (statValue!=null))
    {
      int delta=statValue.getInternalValue()-statValueFromCurve.getInternalValue();
      if (delta!=0)
      {
        deltaValue=new FixedDecimalsInteger();
        deltaValue.setRawValue(delta);
      }
    }
    return deltaValue;
  }

  private FixedDecimalsInteger getStatValue(SingleStatCurveConfiguration config, int level, FixedDecimalsInteger baseStatValue)
  {
    RatingCurve curve=config.getCurve();
    double value=(baseStatValue!=null)?baseStatValue.doubleValue():0.0;
    Double result=curve.getPercentage(value,level);
    FixedDecimalsInteger ret=(result!=null)?new FixedDecimalsInteger(result.floatValue()):null;
    return ret;
  }

  private JLabel buildColoredLabel(Color color)
  {
    JLabel label=GuiFactory.buildLabel("     ");
    label.setBackground(color);
    label.setOpaque(true);
    return label;
  }

  /**
   * Release all managed resources.
   */
  public void dispose()
  {
    if (_panel!=null)
    {
      _panel.removeAll();
      _panel=null;
    }
    _config=null;
  }
}
