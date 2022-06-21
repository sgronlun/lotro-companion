package delta.games.lotro.gui.lore.billingGroups.form;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import delta.common.ui.swing.navigator.NavigatorWindowController;
import delta.common.ui.swing.navigator.PageIdentifier;
import delta.games.lotro.gui.common.navigation.ReferenceConstants;
import delta.games.lotro.lore.quests.Achievable;
import delta.games.lotro.lore.quests.QuestDescription;
import delta.games.lotro.lore.xrefs.billingGroups.BillingGroupReference;
import delta.games.lotro.lore.xrefs.billingGroups.BillingGroupReferencesBuilder;
import delta.games.lotro.utils.gui.HtmlUtils;

/**
 * Controller to display references to a billing group.
 * @author DAM
 */
public class BillingGroupReferencesDisplayController
{
  // Controllers
  private NavigatorWindowController _parent;
  // UI
  private JEditorPane _display;

  /**
   * Constructor.
   * @param parent Parent window (a navigator).
   * @param billingGroupID Identifier of the billing group to show.
   */
  public BillingGroupReferencesDisplayController(NavigatorWindowController parent, int billingGroupID)
  {
    _parent=parent;
    _display=buildDetailsPane(billingGroupID);
  }

  /**
   * Get the managed component.
   * @return the managed component.
   */
  public JEditorPane getComponent()
  {
    return _display;
  }

  private JEditorPane buildDetailsPane(int billingGroupID)
  {
    List<BillingGroupReference<?>> references=getReferences(billingGroupID);
    if (references.size()==0)
    {
      return null;
    }
    String html=getHtml(references);
    JEditorPane editor=buildEditor();
    editor.setText(html);
    editor.setCaretPosition(0);
    return editor;
  }

  private JEditorPane buildEditor()
  {
    JEditorPane editor=new JEditorPane("text/html","");
    editor.setEditable(false);
    editor.setPreferredSize(new Dimension(500,300));
    editor.setOpaque(false);
    HyperlinkListener l=new HyperlinkListener()
    {
      @Override
      public void hyperlinkUpdate(HyperlinkEvent e)
      {
        if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
        {
          String reference=e.getDescription();
          PageIdentifier pageId=PageIdentifier.fromString(reference);
          _parent.navigateTo(pageId);
        }
      }
    };
    editor.addHyperlinkListener(l);
    return editor;
  }

  private List<BillingGroupReference<?>> getReferences(int billingGroupID)
  {
    BillingGroupReferencesBuilder builder=new BillingGroupReferencesBuilder();
    List<BillingGroupReference<?>> references=builder.inspectBillingGroup(billingGroupID);
    return references;
  }

  private String getHtml(List<BillingGroupReference<?>> references)
  {
    StringBuilder sb=new StringBuilder();
    sb.append("<html><body>");
    buildHtmlForQuestsAndDeeds(sb,references);
    sb.append("</body></html>");
    return sb.toString();
  }

  @SuppressWarnings("unchecked")
  private <T> List<BillingGroupReference<T>> getReferences(List<BillingGroupReference<?>> references, Class<T> clazz)
  {
    List<BillingGroupReference<T>> ret=new ArrayList<BillingGroupReference<T>>();
    for(BillingGroupReference<?> reference : references)
    {
      Object source=reference.getSource();
      if (clazz.isAssignableFrom(source.getClass()))
      {
        ret.add((BillingGroupReference<T>)reference);
      }
    }
    return ret;
  }

  private void buildHtmlForQuestsAndDeeds(StringBuilder sb, List<BillingGroupReference<?>> references)
  {
    List<BillingGroupReference<Achievable>> achievableReferences=getReferences(references,Achievable.class);
    if (achievableReferences.size()>0)
    {
      sb.append("<h1>Quests and deeds</h1>");
      for(BillingGroupReference<Achievable> achievableReference : achievableReferences)
      {
        buildHtmlForAchievableReference(sb,achievableReference.getSource());
      }
    }
  }

  private void buildHtmlForAchievableReference(StringBuilder sb, Achievable achievable)
  {
    sb.append("<p>Reward for ");
    boolean isQuest=(achievable instanceof QuestDescription);
    String type=isQuest?"quest ":"deed ";
    sb.append(type);
    sb.append("<b>");
    PageIdentifier to=ReferenceConstants.getAchievableReference(achievable);
    HtmlUtils.printLink(sb,to.getFullAddress(),achievable.getName());
    sb.append("</b></p>");
  }

  /**
   * Release all managed resources.
   */
  public void dispose()
  {
    _parent=null;
    _display=null;
  }
}