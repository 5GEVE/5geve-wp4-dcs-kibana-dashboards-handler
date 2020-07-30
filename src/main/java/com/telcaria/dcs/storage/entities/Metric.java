package com.telcaria.dcs.storage.entities;

import com.telcaria.dcs.storage.enums.GraphType;
import com.telcaria.dcs.storage.enums.MetricType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "metric")
@Data
public class Metric {

  @Id
  private String topic;
  private String metricId;
  @Enumerated(EnumType.STRING)
  private MetricType type;
  private String name;
  private String metricCollectionType;
  private String unit;
  double interval;
  @Enumerated(EnumType.STRING)
  private GraphType graph;
  private String dashboardUrl="";
  private String dashboardId;
  private String kibanaUser;
  @ManyToOne
  private Experiment experiment;
}
