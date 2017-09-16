package delta.games.lotro.gui.stats.crafting;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import delta.common.ui.swing.GuiFactory;
import delta.common.ui.swing.combobox.ComboBoxController;
import delta.common.ui.swing.combobox.ItemSelectionListener;
import delta.common.ui.swing.windows.DefaultFormDialogController;
import delta.common.ui.swing.windows.WindowController;
import delta.games.lotro.character.CharacterFile;
import delta.games.lotro.character.crafting.CraftingStatus;
import delta.games.lotro.character.crafting.ProfessionStatus;
import delta.games.lotro.crafting.Profession;
import delta.games.lotro.crafting.Vocation;
import delta.games.lotro.crafting.Vocations;

/**
 * Controller for a "crafting stats" window.
 * @author DAM
 */
public class CraftingWindowController extends DefaultFormDialogController<CraftingStatus>
{
  private CharacterFile _toon;
  private CraftingStatus _stats;
  private ComboBoxController<Vocation> _vocation;
  private JPanel _professionsPanel;
  private HashMap<Profession,ProfessionPanelController> _panels;

  /**
   * Constructor.
   * @param parentController Parent controller.
   * @param toon Managed toon.
   */
  public CraftingWindowController(WindowController parentController, CharacterFile toon)
  {
    super(parentController,toon.getCraftingStatus());
    _toon=toon;
    _stats=toon.getCraftingStatus();
    _panels=new HashMap<Profession,ProfessionPanelController>();
  }

  @Override
  protected JPanel buildFormPanel()
  {
    JPanel panel=GuiFactory.buildPanel(new GridBagLayout());
    // Vocation panel
    // - combo
    _vocation=buildVocationCombo();
    Vocation vocation=_stats.getVocation();
    JPanel vocationPanel=GuiFactory.buildPanel(new FlowLayout());
    JLabel vocationLabel=GuiFactory.buildLabel("Vocation:");
    vocationPanel.add(vocationLabel);
    vocationPanel.add(_vocation.getComboBox());

    // Professions panel
    _professionsPanel=GuiFactory.buildPanel(new BorderLayout());
    GridBagConstraints c=new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(5,10,0,10),0,0);
    panel.add(vocationPanel,c);
    GridBagConstraints c2=new GridBagConstraints(0,1,1,1,1.0,1.0,GridBagConstraints.WEST,GridBagConstraints.BOTH,new Insets(5,10,5,10),0,0);
    panel.add(_professionsPanel,c2);

    ItemSelectionListener<Vocation> vocationListener=new ItemSelectionListener<Vocation>()
    {
      public void itemSelected(Vocation selectedVocation)
      {
        initProfessionPanel(selectedVocation);
      }
    };
    _vocation.addListener(vocationListener);
    _vocation.selectItem(vocation);

    return panel;
  }

  private void initProfessionPanel(Vocation selectedVocation)
  {
    JComponent centerComponent=null;
    boolean wasEmpty=(_professionsPanel.getComponentCount()==0);
    _professionsPanel.removeAll();
    Vocation currentVocation=_stats.getVocation();
    if (currentVocation!=selectedVocation)
    {
      long now=System.currentTimeMillis();
      _stats.resetVocation(selectedVocation,now);
      disposeProfessionPanels();
    }
    Profession[] professions=(selectedVocation!=null)?selectedVocation.getProfessions():null;
    if ((professions!=null) && (professions.length>0))
    {
      JTabbedPane tabbedPane=GuiFactory.buildTabbedPane();
      for(Profession profession : professions)
      {
        ProfessionStatus stats=_stats.getProfessionStatus(profession,true);
        ProfessionPanelController craftingPanelController=new ProfessionPanelController(stats);
        JPanel craftingPanel=craftingPanelController.getPanel();
        tabbedPane.add(profession.getLabel(),craftingPanel);
        _panels.put(profession,craftingPanelController);
      }
      centerComponent=tabbedPane;
    }
    else
    {
      JLabel centerLabel=new JLabel("No vocation!");
      centerComponent=centerLabel;
    }
    _professionsPanel.add(centerComponent,BorderLayout.CENTER);
    _professionsPanel.revalidate();
    _professionsPanel.repaint();
    if (!wasEmpty)
    {
      getWindow().pack();
    }
  }

  @Override
  protected JDialog build()
  {
    JDialog dialog=super.build();
    // Title
    String name=_toon.getName();
    String serverName=_toon.getServerName();
    String title="Crafting history editor for "+name+" @ "+serverName;
    dialog.setTitle(title);
    // Minimum size
    dialog.setMinimumSize(new Dimension(500,380));
    return dialog;
  }

  @Override
  protected void okImpl()
  {
    for(ProfessionPanelController controller: _panels.values())
    {
      controller.updateDataFromUi();
    }
    _toon.saveCrafting();
  }

  @Override
  protected void cancelImpl()
  {
    _toon.revertCrafting();
  }

  private ComboBoxController<Vocation> buildVocationCombo()
  {
    ComboBoxController<Vocation> ret=new ComboBoxController<Vocation>();
    ret.addEmptyItem("");
    List<Vocation> vocations=Vocations.getInstance().getAll();
    for(Vocation vocation : vocations)
    {
      ret.addItem(vocation,vocation.getName());
    }
    return ret;
  }

  private void disposeProfessionPanels()
  {
    for(ProfessionPanelController controller : _panels.values())
    {
      controller.dispose();
    }
    _panels.clear();
  }

  /**
   * Release all managed resources.
   */
  @Override
  public void dispose()
  {
    super.dispose();
    if (_panels!=null)
    {
      disposeProfessionPanels();
      _panels=null;
    }
    _stats=null;
    _toon=null;
  }
}
