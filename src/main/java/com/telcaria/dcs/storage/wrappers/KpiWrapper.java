package com.telcaria.dcs.storage.wrappers;

import com.telcaria.dcs.storage.enums.GraphType;
import lombok.Data;

@Data
public class KpiWrapper {

  private String topic;
  private String kpiId;
  private String name;
  private String unit;
  double interval;
  private GraphType graph;
  private String dashboardUrl = "";
  private String dashboardId;
  private String user;
  private String expId;
}
