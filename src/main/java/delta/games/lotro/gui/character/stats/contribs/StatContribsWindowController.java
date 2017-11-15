package delta.games.lotro.gui.character.stats.contribs;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

import delta.common.ui.swing.windows.DefaultDialogController;
import delta.common.ui.swing.windows.WindowController;
import delta.games.lotro.character.CharacterData;

/**
 * Controller for a "detailed character stats" window.
 * @author DAM
 */
public class StatContribsWindowController extends DefaultDialogController
{
  /**
   * Window identifier.
   */
  public static final String IDENTIFIER="CONTRIBS";

  private StatContribsPanelController _contribsPanelController;
  private CharacterData _toon;

  /**
   * Constructor.
   * @param parent Parent controller.
   * @param toon Managed toon.
   */
  public StatContribsWindowController(WindowController parent, CharacterData toon)
  {
    super(parent);
    _contribsPanelController=new StatContribsPanelController();
    _toon=toon;
  }

  @Override
  protected JComponent buildContents()
  {
    JPanel panel = _contribsPanelController.getPanel();
    return panel;
  }

  @Override
  protected JDialog build()
  {
    JDialog dialog=super.build();
    // Title
    String name=_toon.getName();
    String serverName=_toon.getServer();
    String title="Stat contributions for: "+name+" @ "+serverName;
    dialog.setTitle(title);
    dialog.pack();
    dialog.setResizable(true);
    return dialog;
  }

  @Override
  public String getWindowIdentifier()
  {
    return IDENTIFIER;
  }

  /**
   * Update values.
   */
  public void update()
  {
    _contribsPanelController.update(_toon);
  }

  /**
   * Release all managed resources.
   */
  @Override
  public void dispose()
  {
    if (_contribsPanelController!=null)
    {
      _contribsPanelController.dispose();
      _contribsPanelController=null;
    }
    _toon=null;
    super.dispose();
  }
}
