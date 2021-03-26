package delta.games.lotro.gui.clientImport;

import javax.swing.JComponent;
import javax.swing.JLabel;

import delta.common.utils.text.EndOfLine;

/**
 * Controller for the client import HOW TO panel.
 * @author DAM
 */
public class ClientImportHowToPanelController
{
  private JComponent _howTo;

  /**
   * Constructor.
   */
  public ClientImportHowToPanelController()
  {
    _howTo=buildHowToGadget();
  }

  /**
   * Get the managed gadget.
   * @return the managed gadget.
   */
  public JComponent getHowToGadget()
  {
    return _howTo;
  }

  private JLabel buildHowToGadget()
  {
    JLabel editor=new JLabel();
    String html=getHowToHtml();
    editor.setText(html);
    return editor;
  }

  private String getHowToHtml()
  {
    StringBuilder sb=new StringBuilder();
    sb.append("<html><body>");
    String text=getHowToText();
    text=text.trim();
    text=text.replace("\n","<br>");
    sb.append(text);
    sb.append("</body></html>");
    return sb.toString();
  }

  private String getHowToText()
  {
    StringBuilder sb=new StringBuilder();
    sb.append("1) Start your 32-bit or 64-bit LOTRO client for Windows.");
    sb.append(EndOfLine.UNIX);
    sb.append("2) Click on the Start Button when:");
    sb.append(EndOfLine.UNIX);
    sb.append("a) the client is in the character choice panel.");
    sb.append(EndOfLine.UNIX);
    sb.append("  Then it will import summary data, reputation status, virtues status and crafting status.");
    sb.append(EndOfLine.UNIX);
    sb.append("  Current gear and deeds status are not available at this stage.");
    sb.append(EndOfLine.UNIX);
    sb.append("OR");
    sb.append(EndOfLine.UNIX);
    sb.append("b) the selected character has entered world. Then it will additionaly import current gear and deeds status.");
    sb.append(EndOfLine.UNIX);
    sb.append(EndOfLine.UNIX);
    sb.append("Option a) is fastest to get an overview: select a character then import, select another one, then import...");
    sb.append(EndOfLine.UNIX);
    sb.append("Option b) is slower but it will get all the possible data for the chosen character.");
    sb.append(EndOfLine.UNIX);
    return sb.toString();
  }
}