package com.telcaria.dcs.storage.wrappers;

import com.telcaria.dcs.storage.enums.GraphType;
import com.telcaria.dcs.storage.enums.MetricType;
import lombok.Data;

@Data
public class MetricWrapper {

  private String topic;
  private String metricId;
  private MetricType type;
  private String name;
  private String metricCollectionType;
  private String unit;
  double interval;
  private GraphType graph;
  private String dashboardUrl = "";
  private String dashboardId;
  private String user;
  private String expId;
}
