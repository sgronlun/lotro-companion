package delta.games.lotro.gui.character;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import delta.games.lotro.character.CharacterData;
import delta.games.lotro.character.events.CharacterEvent;
import delta.games.lotro.character.events.CharacterEventType;
import delta.games.lotro.character.events.CharacterEventsManager;
import delta.games.lotro.character.io.xml.CharacterDataIO;
import delta.games.lotro.character.stats.virtues.VirtuesSet;
import delta.games.lotro.gui.character.buffs.BuffEditionPanelController;
import delta.games.lotro.gui.character.tomes.TomesEditionPanelController;
import delta.games.lotro.gui.character.virtues.VirtuesDisplayPanelController;
import delta.games.lotro.gui.character.virtues.VirtuesEditionDialogController;
import delta.games.lotro.gui.utils.GuiFactory;
import delta.games.lotro.gui.utils.OKCancelPanelController;
import delta.games.lotro.utils.gui.DefaultWindowController;
import delta.games.lotro.utils.gui.WindowsManager;

/**
 * Controller for a "character data" window.
 * @author DAM
 */
public class CharacterDataWindowController extends DefaultWindowController
{
  private CharacterMainAttrsEditionPanelController _attrsController;
  private CharacterStatsSummaryPanelController _statsController;
  private EquipmentPanelController _equipmentController;
  private VirtuesDisplayPanelController _virtuesController;
  private BuffEditionPanelController _buffsController;
  private TomesEditionPanelController _tomesController;
  private OKCancelPanelController _okCancelController;
  private CharacterData _toon;
  private WindowsManager _windowsManager;

  /**
   * Constructor.
   * @param data Managed toon.
   */
  public CharacterDataWindowController(CharacterData data)
  {
    _toon=data;
    _windowsManager=new WindowsManager();
    _attrsController=new CharacterMainAttrsEditionPanelController(data);
    _attrsController.set();
    _statsController=new CharacterStatsSummaryPanelController(this,data);
    _equipmentController=new EquipmentPanelController(this,data);
    _virtuesController=new VirtuesDisplayPanelController();
    _virtuesController.setVirtues(data.getVirtues());
    _buffsController=new BuffEditionPanelController(data);
    _tomesController=new TomesEditionPanelController(data);
    _okCancelController=new OKCancelPanelController();
  }

  /**
   * Get the window identifier for a given toon.
   * @param serverName Server name.
   * @param toonName Toon name.
   * @return A window identifier.
   */
  public static String getIdentifier(String serverName, String toonName)
  {
    String id="DATA#"+serverName+"#"+toonName;
    id=id.toUpperCase();
    return id;
  }

  @Override
  protected JComponent buildContents()
  {
    JPanel panel=GuiFactory.buildPanel(new BorderLayout());
    JPanel editionPanel=buildEditionPanel();
    panel.add(editionPanel,BorderLayout.CENTER);
    JPanel okCancelPanel=_okCancelController.getPanel();
    panel.add(okCancelPanel,BorderLayout.SOUTH);
    _okCancelController.getOKButton().setText("Save");

    ActionListener al=new ActionListener()
    {
      public void actionPerformed(ActionEvent event)
      {
        String action=event.getActionCommand();
        if (OKCancelPanelController.OK_COMMAND.equals(action))
        {
          ok();
        }
        else if (OKCancelPanelController.CANCEL_COMMAND.equals(action))
        {
          cancel();
        }
      }
    };
    _okCancelController.getOKButton().addActionListener(al);
    _okCancelController.getCancelButton().addActionListener(al);

    return panel;
  }

  private JPanel buildEditionPanel()
  {
    JPanel attrsPanel=_attrsController.getPanel();
    // Stats panel
    JPanel statsPanel=_statsController.getPanel();
    // Equipment panel
    JPanel equipmentPanel=_equipmentController.getPanel();

    // Center panel
    JPanel panel=GuiFactory.buildBackgroundPanel(new GridBagLayout());
    GridBagConstraints c=new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0);
    panel.add(equipmentPanel,c);
    c=new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets(0,0,0,0),0,0);
    panel.add(statsPanel,c);

    // Bottom panel
    JPanel bottomPanel=GuiFactory.buildPanel(new GridBagLayout());
    // - virtues
    {
      JPanel virtuesPanel=buildVirtuesPanel();
      TitledBorder border=GuiFactory.buildTitledBorder("Virtues");
      virtuesPanel.setBorder(border);
      c=new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets(0,0,0,0),0,0);
      bottomPanel.add(virtuesPanel,c);
    }
    // - tomes
    {
      JPanel tomesPanel=_tomesController.getPanel();
      TitledBorder border=GuiFactory.buildTitledBorder("Tomes");
      tomesPanel.setBorder(border);
      c=new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets(0,0,0,0),0,0);
      bottomPanel.add(tomesPanel,c);
    }
    // - buffs
    {
      JPanel buffsPanel=_buffsController.getPanel();
      TitledBorder border=GuiFactory.buildTitledBorder("Buffs");
      buffsPanel.setBorder(border);
      c=new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets(0,0,0,0),0,0);
      bottomPanel.add(buffsPanel,c);
    }

    JPanel fullPanel=GuiFactory.buildBackgroundPanel(new BorderLayout());
    fullPanel.add(attrsPanel,BorderLayout.NORTH);
    fullPanel.add(panel,BorderLayout.CENTER);
    fullPanel.add(bottomPanel,BorderLayout.SOUTH);
    return fullPanel;
  }

  private JPanel buildVirtuesPanel()
  {
    JPanel panel=GuiFactory.buildPanel(new FlowLayout());
    JPanel virtuesPanel=_virtuesController.getPanel();
    panel.add(virtuesPanel);
    JButton button=GuiFactory.buildButton("Edit...");
    panel.add(button);
    ActionListener al=new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        VirtuesSet virtues=VirtuesEditionDialogController.editVirtues(CharacterDataWindowController.this,_toon.getVirtues());
        if (virtues!=null)
        {
          _toon.getVirtues().copyFrom(virtues);
          _virtuesController.setVirtues(virtues);
          // Broadcast virtues update event...
          CharacterEvent event=new CharacterEvent(null,_toon);
          CharacterEventsManager.invokeEvent(CharacterEventType.CHARACTER_DATA_UPDATED,event);
        }
      }
    };
    button.addActionListener(al);
    return panel;
  }

  @Override
  protected JFrame build()
  {
    JFrame frame=super.build();
    // Title
    String name=_toon.getName();
    String serverName=_toon.getServer();
    String title="Character: "+name+" @ "+serverName;
    frame.setTitle(title);
    // Size
    frame.pack();
    frame.setResizable(false);
    // Set values
    setValues();
    return frame;
  }

  private void setValues()
  {
    _statsController.update();
  }

  @Override
  public String getWindowIdentifier()
  {
    String serverName=_toon.getServer();
    String toonName=_toon.getName();
    String id=getIdentifier(serverName,toonName);
    return id;
  }

  private void ok()
  {
    boolean ok=CharacterDataIO.saveInfo(_toon.getFile(),_toon);
    if (!ok)
    {
      // TODO warn
    }
  }

  private void cancel()
  {
    _toon.revert();
    dispose();
  }

  @Override
  protected void doWindowClosing()
  {
    cancel();
  }

  /**
   * Release all managed resources.
   */
  @Override
  public void dispose()
  {
    if (_windowsManager!=null)
    {
      _windowsManager.disposeAll();
      _windowsManager=null;
    }
    super.dispose();
    if (_statsController!=null)
    {
      _statsController.dispose();
      _statsController=null;
    }
    if (_equipmentController!=null)
    {
      _equipmentController.dispose();
      _equipmentController=null;
    }
    if (_okCancelController!=null)
    {
      _okCancelController.dispose();
      _okCancelController=null;
    }
    if (_buffsController!=null)
    {
      _buffsController.dispose();
      _buffsController=null;
    }
    if (_tomesController!=null)
    {
      _tomesController.dispose();
      _tomesController=null;
    }
    _toon=null;
  }
}