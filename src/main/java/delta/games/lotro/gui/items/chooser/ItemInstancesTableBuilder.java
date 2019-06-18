package delta.games.lotro.gui.items.chooser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import delta.common.ui.swing.tables.CellDataProvider;
import delta.common.ui.swing.tables.DataProvider;
import delta.common.ui.swing.tables.DefaultTableColumnController;
import delta.common.ui.swing.tables.GenericTableController;
import delta.common.ui.swing.tables.GenericTableController.DateRenderer;
import delta.common.ui.swing.tables.ListDataProvider;
import delta.common.ui.swing.tables.ProxiedTableColumnController;
import delta.common.ui.swing.tables.TableColumnController;
import delta.common.ui.swing.tables.TableColumnsManager;
import delta.games.lotro.common.colors.ColorDescription;
import delta.games.lotro.common.id.AccountId;
import delta.games.lotro.common.id.EntityId;
import delta.games.lotro.common.id.ItemInstanceId;
import delta.games.lotro.common.money.Money;
import delta.games.lotro.gui.items.ItemColumnIds;
import delta.games.lotro.gui.items.ItemInstanceColumnIds;
import delta.games.lotro.lore.items.Item;
import delta.games.lotro.lore.items.ItemInstance;
import delta.games.lotro.lore.items.ItemPropertyNames;
import delta.games.lotro.utils.Formats;

/**
 * Builder for a table that shows items.
 * @author DAM
 */
public class ItemInstancesTableBuilder
{
  /**
   * Build a table to show item instances.
   * @param items Items to show.
   * @return A new table controller.
   */
  public static GenericTableController<ItemInstance<? extends Item>> buildTable(List<ItemInstance<? extends Item>> items)
  {
    DataProvider<ItemInstance<? extends Item>> provider=new ListDataProvider<ItemInstance<? extends Item>>(items);
    GenericTableController<ItemInstance<? extends Item>> table=new GenericTableController<ItemInstance<? extends Item>>(provider);
    List<TableColumnController<ItemInstance<? extends Item>,?>> columns=initColumns();

    TableColumnsManager<ItemInstance<? extends Item>> columnsManager=table.getColumnsManager();
    for(TableColumnController<ItemInstance<? extends Item>,?> column : columns)
    {
      columnsManager.addColumnController(column,false);
    }
    List<String> columnsIds=getDefaultColumnIds();
    columnsManager.setColumns(columnsIds);
    // Adjust table row height for icons (32 pixels)
    JTable swingTable=table.getTable();
    swingTable.setRowHeight(32);
    return table;
  }

  private static List<String> getDefaultColumnIds()
  {
    List<String> columnsIds=new ArrayList<String>();
    columnsIds.add(ItemColumnIds.ICON.name());
    columnsIds.add(ItemColumnIds.NAME.name());
    columnsIds.add(ItemInstanceColumnIds.TIME.name());
    columnsIds.add(ItemInstanceColumnIds.BIRTH_NAME.name());
    columnsIds.add(ItemInstanceColumnIds.INSTANCE_ID.name());
    columnsIds.add(ItemInstanceColumnIds.INSTANCE_ITEM_LEVEL.name());
    return columnsIds;
  }

  /**
   * Build a list of all managed columns.
   * @return A list of column controllers.
   */
  public static List<TableColumnController<ItemInstance<? extends Item>,?>> initColumns()
  {
    List<TableColumnController<ItemInstance<? extends Item>,?>> columns=new ArrayList<TableColumnController<ItemInstance<? extends Item>,?>>();
    columns.addAll(initInstanceColumns());

    CellDataProvider<ItemInstance<? extends Item>,Item> dataProvider=new CellDataProvider<ItemInstance<? extends Item>,Item>()
    {
      @Override
      public Item getData(ItemInstance<? extends Item> p)
      {
        return p.getReference();
      }
    };

    List<DefaultTableColumnController<Item,?>> itemColumns=ItemsTableBuilder.initColumns();
    for(TableColumnController<Item,?> itemColumn : itemColumns)
    {
      String columnId=itemColumn.getId();
      if (acceptItemColumn(columnId))
      {
        @SuppressWarnings("unchecked")
        TableColumnController<Item,Object> c=(TableColumnController<Item,Object>)itemColumn;
        ProxiedTableColumnController<ItemInstance<? extends Item>,Item,Object> column=new ProxiedTableColumnController<ItemInstance<? extends Item>,Item,Object>(c,dataProvider);
        columns.add(column);
      }
    }
    return columns;
  }

  private static boolean acceptItemColumn(String id)
  {
    if (id.equals(ItemColumnIds.ICON.name())) return true;
    if (id.equals(ItemColumnIds.NAME.name())) return true;
    return false;
  }

  /**
   * Build a list of all managed columns.
   * @return A list of column controllers.
   */
  public static List<DefaultTableColumnController<ItemInstance<? extends Item>,?>> initInstanceColumns()
  {
    List<DefaultTableColumnController<ItemInstance<? extends Item>,?>> columns=new ArrayList<DefaultTableColumnController<ItemInstance<? extends Item>,?>>();

    // Instance ID
    {
      CellDataProvider<ItemInstance<? extends Item>,ItemInstanceId> cell=new CellDataProvider<ItemInstance<? extends Item>,ItemInstanceId>()
      {
        @Override
        public ItemInstanceId getData(ItemInstance<? extends Item> item)
        {
          ItemInstanceId instanceId=item.getInstanceId();
          return instanceId;
        }
      };
      DefaultTableColumnController<ItemInstance<? extends Item>,ItemInstanceId> column=new DefaultTableColumnController<ItemInstance<? extends Item>,ItemInstanceId>(ItemInstanceColumnIds.INSTANCE_ID.name(),"Instance ID",ItemInstanceId.class,cell);
      column.setWidthSpecs(150,150,150);
      column.setSortable(false);
      column.setCellRenderer(new InstanceIdRenderer());
      columns.add(column);
    }
    // Time column
    {
      CellDataProvider<ItemInstance<? extends Item>,Date> cell=new CellDataProvider<ItemInstance<? extends Item>,Date>()
      {
        @Override
        public Date getData(ItemInstance<? extends Item> item)
        {
          Long time=item.getTime();
          return (time!=null)?new Date(time.longValue()):null;
        }
      };
      DefaultTableColumnController<ItemInstance<? extends Item>,Date> column=new DefaultTableColumnController<ItemInstance<? extends Item>,Date>(ItemInstanceColumnIds.TIME.name(),"Time",Date.class,cell);
      column.setWidthSpecs(110,110,110);
      column.setCellRenderer(new DateRenderer(Formats.DATE_TIME_PATTERN));
      columns.add(column);
    }
    // Birth name column
    {
      CellDataProvider<ItemInstance<? extends Item>,String> cell=new CellDataProvider<ItemInstance<? extends Item>,String>()
      {
        @Override
        public String getData(ItemInstance<? extends Item> item)
        {
          return item.getBirthName();
        }
      };
      DefaultTableColumnController<ItemInstance<? extends Item>,String> column=new DefaultTableColumnController<ItemInstance<? extends Item>,String>(ItemInstanceColumnIds.BIRTH_NAME.name(),"Birth name",String.class,cell);
      column.setWidthSpecs(90,200,90);
      columns.add(column);
    }
    // Crafter name column
    {
      CellDataProvider<ItemInstance<? extends Item>,String> cell=new CellDataProvider<ItemInstance<? extends Item>,String>()
      {
        @Override
        public String getData(ItemInstance<? extends Item> item)
        {
          return item.getCrafterName();
        }
      };
      DefaultTableColumnController<ItemInstance<? extends Item>,String> column=new DefaultTableColumnController<ItemInstance<? extends Item>,String>(ItemInstanceColumnIds.CRAFTER_NAME.name(),"Crafter",String.class,cell);
      column.setWidthSpecs(90,90,50);
      columns.add(column);
    }
    // Durability column
    {
      CellDataProvider<ItemInstance<? extends Item>,Integer> cell=new CellDataProvider<ItemInstance<? extends Item>,Integer>()
      {
        @Override
        public Integer getData(ItemInstance<? extends Item> item)
        {
          return item.getDurability();
        }
      };
      DefaultTableColumnController<ItemInstance<? extends Item>,Integer> column=new DefaultTableColumnController<ItemInstance<? extends Item>,Integer>(ItemInstanceColumnIds.DURABILITY.name(),"Durability",Integer.class,cell);
      column.setWidthSpecs(90,90,50);
      columns.add(column);
    }
    // Item level column
    {
      CellDataProvider<ItemInstance<? extends Item>,Integer> cell=new CellDataProvider<ItemInstance<? extends Item>,Integer>()
      {
        @Override
        public Integer getData(ItemInstance<? extends Item> item)
        {
          return item.getItemLevel();
        }
      };
      DefaultTableColumnController<ItemInstance<? extends Item>,Integer> column=new DefaultTableColumnController<ItemInstance<? extends Item>,Integer>(ItemInstanceColumnIds.INSTANCE_ITEM_LEVEL.name(),"Item Level",Integer.class,cell);
      column.setWidthSpecs(55,55,50);
      columns.add(column);
    }
    // Required level column
    {
      CellDataProvider<ItemInstance<? extends Item>,Integer> cell=new CellDataProvider<ItemInstance<? extends Item>,Integer>()
      {
        @Override
        public Integer getData(ItemInstance<? extends Item> item)
        {
          return item.getMinLevel();
        }
      };
      DefaultTableColumnController<ItemInstance<? extends Item>,Integer> column=new DefaultTableColumnController<ItemInstance<? extends Item>,Integer>(ItemInstanceColumnIds.INSTANCE_REQUIRED_LEVEL.name(),"Req. Level",Integer.class,cell);
      column.setWidthSpecs(55,55,50);
      columns.add(column);
    }
    // Value column
    {
      CellDataProvider<ItemInstance<? extends Item>,Money> cell=new CellDataProvider<ItemInstance<? extends Item>,Money>()
      {
        @Override
        public Money getData(ItemInstance<? extends Item> item)
        {
          return item.getValue();
        }
      };
      DefaultTableColumnController<ItemInstance<? extends Item>,Money> column=new DefaultTableColumnController<ItemInstance<? extends Item>,Money>(ItemInstanceColumnIds.INSTANCE_VALUE.name(),"Value",Money.class,cell);
      column.setWidthSpecs(90,90,50);
      column.setCellRenderer(new MoneyRenderer());
      columns.add(column);
    }
    // Color column
    {
      CellDataProvider<ItemInstance<? extends Item>,ColorDescription> cell=new CellDataProvider<ItemInstance<? extends Item>,ColorDescription>()
      {
        @Override
        public ColorDescription getData(ItemInstance<? extends Item> item)
        {
          return item.getColor();
        }
      };
      DefaultTableColumnController<ItemInstance<? extends Item>,ColorDescription> column=new DefaultTableColumnController<ItemInstance<? extends Item>,ColorDescription>(ItemInstanceColumnIds.COLOR.name(),"Color",ColorDescription.class,cell);
      column.setWidthSpecs(90,90,50);
      column.setCellRenderer(new ColorRenderer());
      columns.add(column);
    }
    // Bound to column
    {
      CellDataProvider<ItemInstance<? extends Item>,EntityId> cell=new CellDataProvider<ItemInstance<? extends Item>,EntityId>()
      {
        @Override
        public EntityId getData(ItemInstance<? extends Item> item)
        {
          return item.getBoundTo();
        }
      };
      DefaultTableColumnController<ItemInstance<? extends Item>,EntityId> column=new DefaultTableColumnController<ItemInstance<? extends Item>,EntityId>(ItemInstanceColumnIds.BOUND_TO.name(),"Bound to",EntityId.class,cell);
      column.setWidthSpecs(150,150,150);
      column.setCellRenderer(new EntityIdRenderer());
      columns.add(column);
    }
    // Essences column
    // User comments column
    {
      CellDataProvider<ItemInstance<? extends Item>,String> cell=new CellDataProvider<ItemInstance<? extends Item>,String>()
      {
        @Override
        public String getData(ItemInstance<? extends Item> item)
        {
          return item.getProperty(ItemPropertyNames.USER_COMMENT);
        }
      };
      DefaultTableColumnController<ItemInstance<? extends Item>,String> column=new DefaultTableColumnController<ItemInstance<? extends Item>,String>(ItemInstanceColumnIds.USER_COMMENTS.name(),"Comment",String.class,cell);
      column.setWidthSpecs(90,-1,90);
      columns.add(column);
    }
    // Stash ID column
    {
      CellDataProvider<ItemInstance<? extends Item>,Integer> cell=new CellDataProvider<ItemInstance<? extends Item>,Integer>()
      {
        @Override
        public Integer getData(ItemInstance<? extends Item> item)
        {
          return item.getStashIdentifier();
        }
      };
      DefaultTableColumnController<ItemInstance<? extends Item>,Integer> column=new DefaultTableColumnController<ItemInstance<? extends Item>,Integer>(ItemInstanceColumnIds.STASH_ID.name(),"Stash ID",Integer.class,cell);
      column.setWidthSpecs(90,90,50);
      columns.add(column);
    }
    return columns;
  }

  /**
   * Color renderer.
   * @author DAM
   */
  public static class ColorRenderer extends DefaultTableCellRenderer
  {
    @Override
    public void setValue(Object value)
    {
      ColorDescription color=(ColorDescription)value;
      String text=(color!=null)?color.getName():"";
      setText(text);
    }
  }

  /**
   * Instance ID renderer.
   * @author DAM
   */
  public static class InstanceIdRenderer extends DefaultTableCellRenderer
  {
    @Override
    public void setValue(Object value)
    {
      ItemInstanceId id=(ItemInstanceId)value;
      String text=(id!=null)?id.asString():"";
      setText(text);
    }
  }

  /**
   * Entity ID renderer.
   * @author DAM
   */
  public static class EntityIdRenderer extends DefaultTableCellRenderer
  {
    @Override
    public void setValue(Object value)
    {
      EntityId id=(EntityId)value;
      String text="";
      if (id!=null)
      {
        // TODO Resolve account/character IDs
        String prefix="";
        if (id instanceof AccountId)
        {
          prefix="Account:";
        }
        text=prefix+id.getId1()+"/"+id.getId2();
      }
      setText(text);
    }
  }

  /**
   * Instance ID renderer.
   * @author DAM
   */
  public static class MoneyRenderer extends DefaultTableCellRenderer
  {
    @Override
    public void setValue(Object value)
    {
      Money money=(Money)value;
      String text=(money!=null)?money.getShortLabel():"";
      setText(text);
    }
  }
}