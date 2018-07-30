package delta.games.lotro.gui.character.storage;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import delta.common.ui.swing.GuiFactory;
import delta.common.ui.swing.windows.DefaultWindowController;
import delta.common.utils.collections.filters.Filter;
import delta.games.lotro.character.storage.AccountServerStorage;
import delta.games.lotro.character.storage.CharacterStorage;
import delta.games.lotro.character.storage.Chest;
import delta.games.lotro.character.storage.ItemsContainer;
import delta.games.lotro.character.storage.StoredItem;
import delta.games.lotro.character.storage.Vault;
import delta.games.lotro.character.storage.Wallet;
import delta.games.lotro.gui.items.CountedItemsTableController;
import delta.games.lotro.gui.items.chooser.ItemFilterConfiguration;
import delta.games.lotro.gui.items.chooser.ItemFilterController;
import delta.games.lotro.lore.items.Item;
import delta.games.lotro.plugins.StorageLoader;

/**
 * Test class to show the storage for a single character.
 * @author DAM
 */
public class MainTestShowCharacterStorage
{
  private void doIt()
  {
    String account="glorfindel666";
    String server="Landroval";
    String toon="Meva";
    boolean showShared=true;
    StorageLoader loader=new StorageLoader();
    AccountServerStorage storage=loader.loadStorage(account,server);
    if (storage!=null)
    {
      // Own bags
      {
        List<StoredItem> storedItems=getAllItems(toon,storage,true);
        show("Bags",storedItems);
      }
      // Own vault
      {
        List<StoredItem> storedItems=getAllItems(toon,storage,false);
        show("Vault",storedItems);
      }
      // Own wallet
      {
        CharacterStorage characterStorage=storage.getStorage(toon,true);
        Wallet ownWallet=characterStorage.getWallet();
        List<StoredItem> storedItems=ownWallet.getAllItemsByName();
        show("Wallet",storedItems);
      }
      if (showShared)
      {
        // Shared wallet
        {
          ItemsContainer container=storage.getSharedWallet();
          List<StoredItem> storedItems=container.getAllItemsByName();
          show("Shared wallet",storedItems);
        }
        // Shared vault
        {
          Vault sharedVault=storage.getSharedVault();
          List<StoredItem> storedItems=getAllItems(sharedVault);
          show("Shared vault",storedItems);
        }
      }
    }
  }

  private void show(String title, List<StoredItem> storedItems)
  {
    ItemFilterConfiguration cfg=new ItemFilterConfiguration();
    cfg.forStashFilter();
    ItemFilterController filterController=new ItemFilterController(cfg,null,null);
    Filter<Item> filter=filterController.getFilter();
    final CountedItemsTableController tableController=new CountedItemsTableController(null,storedItems,filter);
    DefaultWindowController c=new DefaultWindowController()
    {
      @Override
      protected JComponent buildContents()
      {
        // Table
        JTable table=tableController.getTable();
        JScrollPane scroll=GuiFactory.buildScrollPane(table);
        return scroll;
      }
    };
    c.setTitle(title);
    c.getWindow().pack();
    c.show();
  }

  private List<StoredItem> getAllItems(String character, AccountServerStorage storage, boolean bags)
  {
    CharacterStorage characterStorage=storage.getStorage(character,true);
    Vault container=(bags?characterStorage.getBags():characterStorage.getOwnVault());
    return getAllItems(container);
  }

  private List<StoredItem> getAllItems(Vault container)
  {
    List<StoredItem> items=new ArrayList<StoredItem>();
    int chests=container.getChestCount();
    //int itemsCount=0;
    for(int i=0;i<chests;i++)
    {
      Chest chest=container.getChest(i);
      if (chest!=null)
      {
        List<StoredItem> chestItems=chest.getAllItemsByName();
        //itemsCount+=chestItems.size();
        items.addAll(chestItems);
      }
    }
    //System.out.println(itemsCount);
    return items;
  }

  /**
   * Main method for this test.
   * @param args Not used.
   */
  public static void main(String[] args)
  {
    new MainTestShowCharacterStorage().doIt();
  }
}
