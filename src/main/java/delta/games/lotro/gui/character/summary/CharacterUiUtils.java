package delta.games.lotro.gui.character.summary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import delta.common.ui.swing.combobox.ComboBoxController;
import delta.common.utils.misc.TypedProperties;
import delta.games.lotro.Config;
import delta.games.lotro.account.Account;
import delta.games.lotro.account.AccountsManager;
import delta.games.lotro.common.CharacterClass;
import delta.games.lotro.common.CharacterSex;
import delta.games.lotro.common.Race;
import delta.games.lotro.gui.utils.SharedUiUtils;

/**
 * Utility methods for character UI.
 * @author DAM
 */
public class CharacterUiUtils
{
  /**
   * Build a character class combobox.
   * @param includeEmptyChoice Include an empty choice or not.
   * @return a character class combobox.
   */
  public static ComboBoxController<CharacterClass> buildClassCombo(boolean includeEmptyChoice)
  {
    ComboBoxController<CharacterClass> ctrl=new ComboBoxController<CharacterClass>();
    if (includeEmptyChoice)
    {
      ctrl.addEmptyItem("");
    }
    for(CharacterClass characterClass : CharacterClass.ALL_CLASSES)
    {
      ctrl.addItem(characterClass,characterClass.getLabel());
    }
    return ctrl;
  }

  /**
   * Build a character race combobox.
   * @param includeEmptyChoice Include an empty choice or not.
   * @return a character race combobox.
   */
  public static ComboBoxController<Race> buildRaceCombo(boolean includeEmptyChoice)
  {
    ComboBoxController<Race> ctrl=new ComboBoxController<Race>();
    if (includeEmptyChoice)
    {
      ctrl.addEmptyItem("");
    }
    for(Race race : Race.ALL_RACES)
    {
      ctrl.addItem(race,race.getLabel());
    }
    return ctrl;
  }

  /**
   * Build a character sex combobox.
   * @param includeEmptyChoice Include an empty choice or not.
   * @return a character sex combobox.
   */
  public static ComboBoxController<CharacterSex> buildSexCombo(boolean includeEmptyChoice)
  {
    ComboBoxController<CharacterSex> ctrl=new ComboBoxController<CharacterSex>();
    if (includeEmptyChoice)
    {
      ctrl.addEmptyItem("");
    }
    for(CharacterSex characterSex : CharacterSex.ALL)
    {
      ctrl.addItem(characterSex,characterSex.getLabel());
    }
    return ctrl;
  }

  /**
   * Build a server combobox.
   * @return a server combobox.
   */
  public static ComboBoxController<String> buildServerCombo()
  {
    ComboBoxController<String> ctrl=new ComboBoxController<String>();
    List<String> servers=Config.getInstance().getServerNames();
    for(String server : servers)
    {
      ctrl.addItem(server,server);
    }
    TypedProperties props=Config.getInstance().getParameters();
    String defaultServer=props.getStringProperty("default.server",null);
    ctrl.selectItem(defaultServer);
    return ctrl;
  }

  /**
   * Build an account combobox.
   * @return an account combobox.
   */
  public static ComboBoxController<String> buildAccountCombo()
  {
    List<Account> accounts=AccountsManager.getInstance().getAllAccounts();
    List<String> accountNames=new ArrayList<String>();
    for(Account account : accounts)
    {
      String accountName=account.getName();
      accountNames.add(accountName);
    }
    Collections.sort(accountNames);
    ComboBoxController<String> ctrl=new ComboBoxController<String>();
    ctrl.addEmptyItem("");
    for(String accountName : accountNames)
    {
      ctrl.addItem(accountName,accountName);
    }
    return ctrl;
  }

  /**
   * Build a level combobox.
   * @return a level combobox.
   */
  public static ComboBoxController<Integer> buildLevelCombo()
  {
    int maxLevel=Config.getInstance().getMaxCharacterLevel();
    List<Integer> levels=new ArrayList<Integer>();
    for(int i=1;i<=maxLevel;i++)
    {
      levels.add(Integer.valueOf(i));
    }
    ComboBoxController<Integer> ctrl=SharedUiUtils.buildIntegerCombo(levels,false);
    ctrl.selectItem(Integer.valueOf(maxLevel));
    return ctrl;
  }
}
