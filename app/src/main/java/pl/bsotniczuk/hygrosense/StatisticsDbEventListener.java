package pl.bsotniczuk.hygrosense;

import java.util.List;

import pl.bsotniczuk.hygrosense.data.model.StatisticsItem;

public interface StatisticsDbEventListener {
    void statisticsDataChanged(List<StatisticsItem> statisticsItems);
}
