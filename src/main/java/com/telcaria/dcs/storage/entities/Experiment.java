package com.telcaria.dcs.storage.entities;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "experiment")
@Data
public class Experiment {

  @Id
  private String expId;
  private String useCase;
  private String siteFacility;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn
  private List<Metric> metrics;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn
  private List<Kpi> kpis;


  public void addKpi(Kpi kpi) {
    kpis.add(kpi);
    kpi.setExperiment(this);
  }

  public void removeKpi(Kpi kpi) {
    kpis.remove(kpi);
    kpi.setExperiment(null);
  }

  public void addMetric(Metric metric) {
    metrics.add(metric);
    metric.setExperiment(this);
  }

  public void removeMetric(Metric metric) {
    metrics.remove(metric);
    metric.setExperiment(null);
  }
}
