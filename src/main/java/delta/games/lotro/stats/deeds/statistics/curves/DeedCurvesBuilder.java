package delta.games.lotro.stats.deeds.statistics.curves;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import delta.games.lotro.character.CharacterFile;
import delta.games.lotro.gui.stats.curves.DatedCurveProvider;
import delta.games.lotro.lore.deeds.DeedDescription;
import delta.games.lotro.lore.deeds.DeedsManager;
import delta.games.lotro.stats.deeds.DeedStatus;
import delta.games.lotro.stats.deeds.DeedStatusDateComparator;
import delta.games.lotro.stats.deeds.DeedsStatusManager;
import delta.games.lotro.stats.deeds.io.DeedsStatusIo;
import delta.games.lotro.utils.charts.DatedCurve;

/**
 * Builds dated curves from deeds statistics.
 * @author DAM
 */
public class DeedCurvesBuilder implements DatedCurveProvider<CharacterFile>
{
  @Override
  public DatedCurve<?> getCurve(CharacterFile toon)
  {
    String name=toon.getName();
    DatedCurve<Integer> curve=new DatedCurve<Integer>(name);
    DeedsStatusManager deedsStatus=DeedsStatusIo.load(toon);
    List<DeedStatus> datedStatuses=getDeedsStatus(deedsStatus);
    DeedsManager deeds=DeedsManager.getInstance();
    int totalValue=0;
    Long currentDate=null;
    for(DeedStatus datedStatus : datedStatuses)
    {
      String deedKey=datedStatus.getDeedKey();
      DeedDescription deed=deeds.getDeed(deedKey);
      if (deed!=null)
      {
        int value=deed.getRewards().getLotroPoints();
        /*
        int value=0;
        Rewards rewards=deed.getRewards();
        ObjectsSet objects=rewards.getObjects();
        int nbItems=objects.getNbObjectItems();
        for(int i=0;i<nbItems;i++)
        {
          ObjectItem itemReward=objects.getItem(i);
          int itemId=itemReward.getItemId();
          int itemsCount=objects.getQuantity(i);
          // Marks
          if (itemId==1879224343)
          {
            value+=itemsCount;
          }
          // Medallions
          if (itemId==1879224344)
          {
            value+=itemsCount;
          }
        }
        */
        if (value!=0)
        {
          totalValue+=value;
          Long deedDate=datedStatus.getCompletionDate();
          if (!deedDate.equals(currentDate))
          {
            curve.addValue(deedDate.longValue(),Integer.valueOf(totalValue));
          }
        }
      }
    }
    return curve;
  }

  private List<DeedStatus> getDeedsStatus(DeedsStatusManager deedsStatus)
  {
    List<DeedStatus> all=deedsStatus.getAll();
    List<DeedStatus> datedStatuses=new ArrayList<DeedStatus>();
    for(DeedStatus status : all)
    {
      if (status.getCompletionDate()!=null)
      {
        datedStatuses.add(status);
      }
    }
    Collections.sort(datedStatuses,new DeedStatusDateComparator());
    return datedStatuses;
  }
}