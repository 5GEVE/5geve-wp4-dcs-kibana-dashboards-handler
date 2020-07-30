package com.telcaria.dcs.storage.service;

import com.telcaria.dcs.storage.entities.Experiment;
import com.telcaria.dcs.storage.entities.Kpi;
import com.telcaria.dcs.storage.entities.Metric;
import com.telcaria.dcs.storage.wrappers.ExperimentWrapper;
import com.telcaria.dcs.storage.wrappers.KpiWrapper;
import com.telcaria.dcs.storage.wrappers.MetricWrapper;
import java.util.List;
import java.util.Optional;

public interface StorageService {

  /* - - - - - - - - - - - - - - - - - Experiment - - - - - - - - - - - - - - - - - */
  String createExperiment(ExperimentWrapper experimentWrapper);

  void removeExperiment(String experimentId);

  Optional<Experiment> getExperiment(String experimentId);

  ExperimentWrapper experimentToWrapper(String experimentId);

  Experiment findExperimentFromKpi(String topic);

  Experiment findExperimentFromMetric(String topic);

  Boolean isExperimentEmpty(String experimentId);

  /* - - - - - - - - - - - - - - - - - Kpi - - - - - - - - - - - - - - - - - */

  String createKpi(KpiWrapper kpiWrapper);

  void removeKpi(String kpiId);

  Optional<Kpi> getKpi(String kpiId);

  void addKpiToExperiment(Kpi kpi, String experimentId);

  void removeKpiFromExperiment(String kpiId, String experimentId);

  KpiWrapper kpiToWrapper(String kpiId);

  List<Kpi> findAllKpisFromExperiment(String experimentId);

  void updateKpi(KpiWrapper kpiWrapper);

  /* - - - - - - - - - - - - - - - - - Metric - - - - - - - - - - - - - - - - - */

  String createMetric(MetricWrapper metricWrapper);

  void removeMetric(String metricId);

  Optional<Metric> getMetric(String metricId);

  void addMetricToExperiment(Metric metric, String experimentId);

  void removeMetricFromExperiment(String metricId, String experimentId);

  MetricWrapper metricToWrapper(String metricId);

  List<Metric> findAllMetricsFromExperiment(String experimentId);

  void updateMetric(MetricWrapper metricWrapper);

}
