package delta.games.lotro.gui.items;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import delta.common.ui.swing.GuiFactory;
import delta.common.ui.swing.form.LabeledComponent;
import delta.common.ui.swing.text.dates.DateCodec;
import delta.common.ui.swing.windows.WindowController;
import delta.games.lotro.character.stats.BasicStatsSet;
import delta.games.lotro.common.colors.ColorDescription;
import delta.games.lotro.common.id.ItemInstanceId;
import delta.games.lotro.common.money.Money;
import delta.games.lotro.common.stats.WellKnownStat;
import delta.games.lotro.gui.LotroIconsManager;
import delta.games.lotro.gui.common.money.MoneyDisplayController;
import delta.games.lotro.lore.items.Armour;
import delta.games.lotro.lore.items.ArmourInstance;
import delta.games.lotro.lore.items.Item;
import delta.games.lotro.lore.items.ItemInstance;
import delta.games.lotro.lore.items.ItemPropertyNames;
import delta.games.lotro.lore.items.ItemSturdiness;
import delta.games.lotro.utils.DateFormat;
import delta.games.lotro.utils.FixedDecimalsInteger;
import delta.games.lotro.utils.gui.HtmlUtils;

/**
 * Controller for an item instance edition panel.
 * @author DAM
 */
public class ItemInstanceDisplayPanelController
{
  // Data
  private ItemInstance<? extends Item> _itemInstance;
  // GUI
  private JPanel _panel;
  private WindowController _parent;
  // - Item identification (icon+name)
  private JLabel _icon;
  private JLabel _name;
  // - Instance ID
  private LabeledComponent<JLabel> _instanceId;
  // - Validity date
  private LabeledComponent<JLabel> _date;
  // - Birth name
  private LabeledComponent<JLabel> _birthName;
  // - Crafter name
  private LabeledComponent<JLabel> _crafterName;
  // - Durability
  private JLabel _durability;
  // - Sturdiness
  private JLabel _sturdiness;
  // - Item level
  private JLabel _itemLevel;
  // - Min level
  private JLabel _minLevel;
  // - Value
  private MoneyDisplayController _value;
  // - Color => only for armours?
  private LabeledComponent<JLabel> _color;
  // - Bound to
  // TODO
  // - Description
  private JLabel _description;
  // - User comments
  private LabeledComponent<JLabel> _userComments;

  // Armour specifics
  private JPanel _armourPanel;
  private JLabel _armourValue;

  // Item model data:
  // - slot
  // - category?
  // - binding
  // - unique
  // - sturdiness
  // - max level
  // - requirements: class, and later: race, faction, glory rank, trait
  // - description
  // - value
  // - stack max
  // - quality

  /**
   * Constructor.
   * @param parent Parent window.
   * @param itemInstance Item instance.
   */
  public ItemInstanceDisplayPanelController(WindowController parent, ItemInstance<? extends Item> itemInstance)
  {
    _parent=parent;
    _itemInstance=itemInstance;
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

  private void initGadgets()
  {
    // Item identification (icon+name)
    // - icon
    _icon=GuiFactory.buildIconLabel(null);
    // - name
    _name=GuiFactory.buildLabel("");
    _name.setFont(_name.getFont().deriveFont(16f).deriveFont(Font.BOLD));
    // - Instance ID
    _instanceId=new LabeledComponent<JLabel>("ID:",GuiFactory.buildLabel(""));
    // Validity date
    _date=new LabeledComponent<JLabel>("Date:",GuiFactory.buildLabel(""));
    // Birth name
    _birthName=new LabeledComponent<JLabel>("Name:",GuiFactory.buildLabel(""));
    // Crafter name
    _crafterName=new LabeledComponent<JLabel>("Crafter:",GuiFactory.buildLabel(""));
    // - Durability
    _durability=GuiFactory.buildLabel("");
    // - Sturdiness
    _sturdiness=GuiFactory.buildLabel("");
    // - Item level
    _itemLevel=GuiFactory.buildLabel("");
    // - Min level
    _minLevel=GuiFactory.buildLabel("");
    // - Value
    _value=new MoneyDisplayController();
    // - Color
    _color=new LabeledComponent<JLabel>("Color:",GuiFactory.buildLabel(""));
    // - Bound to
    // TODO
    // - Description
    _description=GuiFactory.buildLabel("");
    // - User comments
    _userComments=new LabeledComponent<JLabel>("Comments:",GuiFactory.buildLabel(""));

    // Armour specifics
    if (_itemInstance instanceof ArmourInstance)
    {
      _armourPanel=GuiFactory.buildPanel(new FlowLayout(FlowLayout.LEFT));
      // Armour value
      _armourValue=GuiFactory.buildLabel("");
    }
  }

  private JPanel build()
  {
    initGadgets();
    return buildPanel();
  }

  private JPanel buildPanel()
  {
    JPanel panel=GuiFactory.buildPanel(new GridBagLayout());

    GridBagConstraints c=new GridBagConstraints(0,0,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0);
    // Main data line
    {
      JPanel panelLine=GuiFactory.buildPanel(new FlowLayout(FlowLayout.LEFT));
      panel.add(panelLine,c);
      c.gridy++;
      // Icon
      panelLine.add(_icon);
      // Name
      panelLine.add(_name);
    }
    // Identification line: ID and validity date
    {
      JPanel panelLine=GuiFactory.buildPanel(new FlowLayout(FlowLayout.LEFT));
      panel.add(panelLine,c);
      c.gridy++;
      // ID
      panelLine.add(_instanceId.getLabel());
      panelLine.add(_instanceId.getComponent());
      // Date
      panelLine.add(_date.getLabel());
      panelLine.add(_date.getComponent());
    }
    // Birth line: birth name and crafter name
    {
      JPanel panelLine=GuiFactory.buildPanel(new FlowLayout(FlowLayout.LEFT));
      panel.add(panelLine,c);
      c.gridy++;
      // Birth name
      panelLine.add(_birthName.getLabel());
      panelLine.add(_birthName.getComponent());
      // Crafter name
      panelLine.add(_crafterName.getLabel());
      panelLine.add(_crafterName.getComponent());
    }
    // Level line
    {
      JPanel panelLine=GuiFactory.buildPanel(new FlowLayout(FlowLayout.LEFT));
      panel.add(panelLine,c);
      c.gridy++;
      // Item level
      panelLine.add(GuiFactory.buildLabel("Item level:"));
      panelLine.add(_itemLevel);
      // Minimum level
      panelLine.add(GuiFactory.buildLabel("Required level:"));
      panelLine.add(_minLevel);
    }
    // Durability line
    {
      JPanel panelLine=GuiFactory.buildPanel(new FlowLayout(FlowLayout.LEFT));
      panel.add(panelLine,c);
      c.gridy++;
      // Durability
      panelLine.add(GuiFactory.buildLabel("Durability:"));
      panelLine.add(_durability);
      // Sturdiness
      panelLine.add(_sturdiness);
    }
    // Color and value line
    {
      JPanel panelLine=GuiFactory.buildPanel(new FlowLayout(FlowLayout.LEFT));
      panel.add(panelLine,c);
      c.gridy++;
      // Color
      panelLine.add(_color.getLabel());
      panelLine.add(_color.getComponent());
      // Value
      panelLine.add(GuiFactory.buildLabel("Value:"));
      panelLine.add(_value.getPanel());
    }
    // Armour specifics line
    if (_itemInstance instanceof ArmourInstance)
    {
      panel.add(_armourPanel,c);
      c.gridy++;
      // Armour value
      _armourPanel.add(GuiFactory.buildLabel("Armour:"));
      _armourPanel.add(_armourValue);
    }
    // Description
    {
      JPanel panelLine=GuiFactory.buildPanel(new FlowLayout(FlowLayout.LEFT));
      panel.add(panelLine,c);
      c.gridy++;
      panelLine.add(_description);
    }
    // User comments
    {
      JPanel panelLine=GuiFactory.buildPanel(new FlowLayout(FlowLayout.LEFT));
      panel.add(panelLine,c);
      c.gridy++;
      panelLine.add(_userComments.getLabel());
      panelLine.add(_userComments.getComponent());
    }

    JPanel ret=GuiFactory.buildPanel(new GridBagLayout());
    c=new GridBagConstraints(0,0,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0);
    ret.add(panel,c);

    _panel=ret;
    setItem();
    return ret;
  }

  /**
   * Set the item to display.
   */
  private void setItem()
  {
    Item item=_itemInstance.getReference();
    // Title
    String name=item.getName();
    if (_parent!=null)
    {
      _parent.setTitle(name);
    }
    // Item identification (icon+name)
    // - icon
    String iconPath=item.getIcon();
    ImageIcon icon=LotroIconsManager.getItemIcon(iconPath);
    _icon.setIcon(icon);
    // - name
    _name.setText(name);
    // - Instance ID
    ItemInstanceId instanceId=_itemInstance.getInstanceId();
    _instanceId.setVisible(instanceId!=null);
    if (instanceId!=null)
    {
      _instanceId.getComponent().setText(instanceId.asSingleHexString());
    }
    // Validity date
    Long time=_itemInstance.getTime();
    _date.setVisible(time!=null);
    if (time!=null)
    {
      DateCodec codec=DateFormat.getDateTimeCodec();
      String validityDateStr=codec.formatDate(time);
      _date.getComponent().setText(validityDateStr);
    }
    // Adjust visibility of the parent panel
    _date.getComponent().getParent().setVisible((instanceId!=null)||(time!=null));
    // Birth name
    String birthName=_itemInstance.getBirthName();
    boolean hasName=((birthName!=null) && (birthName.length()>0));
    _birthName.setVisible(hasName);
    if (hasName)
    {
      _birthName.getComponent().setText(birthName);
    }
    // Crafter name
    String crafterName=_itemInstance.getCrafterName();
    boolean hasCrafter=((crafterName!=null) && (crafterName.length()>0));
    _crafterName.setVisible(hasCrafter);
    if (hasCrafter)
    {
      _crafterName.getComponent().setText(crafterName);
    }
    // Adjust visibility of the parent panel
    _crafterName.getComponent().getParent().setVisible(hasName||hasCrafter);
    // - Own stats
    setStats(_itemInstance.getEffectiveOwnStats());
    // - Durability
    String durabilityStr=getDurabilityLabel();
    _durability.setText(durabilityStr);
    // - Sturdiness
    String sturdiness=getSturdinessLabel();
    _sturdiness.setText(sturdiness);
    // - Item level
    Integer itemLevel=_itemInstance.getEffectiveItemLevel();
    String itemLevelStr=(itemLevel!=null)?itemLevel.toString():"-";
    _itemLevel.setText(itemLevelStr);
    // - Min level
    Integer minLevel=_itemInstance.getEffectiveMinLevel();
    _minLevel.setText(minLevel!=null?minLevel.toString():"-");
    // - Value
    Money value=_itemInstance.getValue();
    if (value!=null)
    {
      _value.setMoney(value);
    }
    // - Color
    ColorDescription color=_itemInstance.getColor();
    _color.setVisible(color!=null);
    if (color!=null)
    {
      _color.getComponent().setText(color.getName());
    }
    // - Bound to
    // TODO
    // Description
    String description=item.getDescription();
    boolean hasDescription=(description.length()>0);
    _description.setVisible(hasDescription);
    // Adjust visibility of the parent panel
    _description.getParent().setVisible(hasDescription);
    if (hasDescription)
    {
      String html="<html>"+HtmlUtils.toHtml(description)+"</html>";
      _description.setText(html);
    }
    // - User comments
    String userComments=_itemInstance.getProperty(ItemPropertyNames.USER_COMMENT);
    boolean hasUserComments=(userComments!=null);
    _userComments.setVisible(hasUserComments);
    // Adjust visibility of the parent panel
    _userComments.getComponent().getParent().setVisible(hasUserComments);
    if (hasUserComments)
    {
      _userComments.getComponent().setText(userComments);
    }
  }

  private String getDurabilityLabel()
  {
    Integer instanceDurability=_itemInstance.getEffectiveDurability();
    Item reference=_itemInstance.getReference();
    Integer durability=(reference!=null)?reference.getDurability():null;
    String durabilityStr="-";
    if (durability!=null)
    {
      durabilityStr=durability.toString();
      if ((instanceDurability!=null) && (!instanceDurability.equals(durability)))
      {
        durabilityStr=instanceDurability.toString()+"/"+durabilityStr;
      }
    }
    return durabilityStr;
  }

  private String getSturdinessLabel()
  {
    String ret="";
    Item reference=_itemInstance.getReference();
    ItemSturdiness sturdiness=reference.getSturdiness();
    String label=(sturdiness!=null)?sturdiness.getLabel():null;
    if (label!=null)
    {
      ret="("+label+")";
    }
    return ret;
  }

  private void setStats(BasicStatsSet stats)
  {
    Item item=_itemInstance.getReference();
    if (item instanceof Armour)
    {
      FixedDecimalsInteger armourValue=stats.getStat(WellKnownStat.ARMOUR);
      if (armourValue!=null)
      {
        _armourValue.setText(armourValue.toString());
      }
    }
  }

  /**
   * Release all managed resources.
   */
  public void dispose()
  {
    // Data
    _itemInstance=null;
    // UI/controllers
    if (_panel!=null)
    {
      _panel.removeAll();
      _panel=null;
    }
    _parent=null;
    // - Item identification (icon+name)
    _icon=null;
    _name=null;
    // - Instance ID
    if (_instanceId!=null)
    {
      _instanceId.dispose();
      _instanceId=null;
    }
    // - Validity date
    if (_date!=null)
    {
      _date.dispose();
      _date=null;
    }
    // - Birth name
    if (_birthName!=null)
    {
      _birthName.dispose();
      _birthName=null;
    }
    // - Crafter name
    if (_crafterName!=null)
    {
      _crafterName.dispose();
      _crafterName=null;
    }
    // - Durability
    _durability=null;
    // - Sturdiness
    _sturdiness=null;
    // - Item level
    _itemLevel=null;
    // - Min level
    _minLevel=null;
    // - Value
    if (_value!=null)
    {
      _value.dispose();
      _value=null;
    }
    // - Color
    if (_color!=null)
    {
      _color.dispose();
      _color=null;
    }
    // - Bound to
    // TODO
    // - Description
    _description=null;
    // - User comments
    if (_userComments!=null)
    {
      _userComments.dispose();
      _userComments=null;
    }
    // Armour specifics
    _armourPanel=null;
    _armourValue=null;
  }
}
