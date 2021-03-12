package com.telcaria.dcs.storage.service;

import com.telcaria.dcs.storage.entities.Experiment;
import com.telcaria.dcs.storage.entities.Kpi;
import com.telcaria.dcs.storage.entities.Metric;
import com.telcaria.dcs.storage.repositories.ExperimentRepository;
import com.telcaria.dcs.storage.repositories.KpiRepository;
import com.telcaria.dcs.storage.repositories.MetricRepository;
import com.telcaria.dcs.storage.wrappers.ExperimentWrapper;
import com.telcaria.dcs.storage.wrappers.KpiWrapper;
import com.telcaria.dcs.storage.wrappers.MetricWrapper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class StorageImpl implements StorageService {

  //TODO: use SQL database instead of internal repository.
  private ExperimentRepository experimentRepository;
  private KpiRepository kpiRepository;
  private MetricRepository metricRepository;

  @Autowired
  public StorageImpl(ExperimentRepository experimentRepository,
      KpiRepository kpiRepository,
      MetricRepository metricRepository) {
    this.experimentRepository = experimentRepository;
    this.kpiRepository = kpiRepository;
    this.metricRepository = metricRepository;
  }

  /* - - - - - - - - - - - - - - - - - Experiment - - - - - - - - - - - - - - - - - */

  @Override
  public String createExperiment(ExperimentWrapper experimentWrapper) {
    Experiment experiment = new Experiment();
    experiment.setExpId(experimentWrapper.getExpId());
    experiment.setSiteFacility(experimentWrapper.getSiteFacility());
    experiment.setUseCase(experimentWrapper.getUseCase());
    experiment.setKpis(new ArrayList<>());
    experiment.setMetrics(new ArrayList<>());

    experiment = experimentRepository.saveAndFlush(experiment);
    log.info("Experiment {} saved in database", experiment.getExpId());
    return experiment.getExpId();
  }

  @Override
  public void removeExperiment(String experimentId) {
    experimentRepository.deleteById(experimentId);
    log.info("Experiment {} deleted in database", experimentId);
  }

  @Override
  public Optional<Experiment> getExperiment(String experimentId) {
    return experimentRepository.findById(experimentId);
  }

  @Override
  public ExperimentWrapper experimentToWrapper(String experimentId) {
    Optional<Experiment> experimentOp = getExperiment(experimentId);
    if (experimentOp.isPresent()) {
      Experiment experiment = experimentOp.get();
      ExperimentWrapper experimentWrapper = new ExperimentWrapper();
      experimentWrapper.setExpId(experiment.getExpId());
      experimentWrapper.setSiteFacility(experiment.getSiteFacility());
      experimentWrapper.setUseCase(experiment.getUseCase());

      Set<KpiWrapper> kpis = new HashSet<>();
      for (Kpi kpi : experiment.getKpis()) {
        kpis.add(kpiToWrapper(kpi.getTopic()));
      }
      experimentWrapper.setKpis(kpis);

      Set<MetricWrapper> metrics = new HashSet<>();
      for (Metric metric : experiment.getMetrics()) {
        metrics.add(metricToWrapper(metric.getTopic()));
      }
      experimentWrapper.setMetrics(metrics);

      return experimentWrapper;
    }
    return null;
  }

  @Override
  public Experiment findExperimentFromKpi(String topic) {
    return experimentRepository.findByKpis_Topic(topic);
  }

  @Override
  public Experiment findExperimentFromMetric(String topic) {
    return experimentRepository.findByMetrics_Topic(topic);
  }

  @Override
  public Boolean isExperimentEmpty(String experimentId) {
    Optional<Experiment> experimentOp = getExperiment(experimentId);
    if (experimentOp.isPresent()) {
      List<Kpi> kpis = findAllKpisFromExperiment(experimentId);
      List<Metric> metrics = findAllMetricsFromExperiment(experimentId);
      if (kpis.isEmpty() && metrics.isEmpty()){
        return true;
      }

    }
    return false;
  }

  /* - - - - - - - - - - - - - - - - - Kpi - - - - - - - - - - - - - - - - - */

  @Override
  public String createKpi(KpiWrapper kpiWrapper) {
    Kpi kpi = new Kpi();
    kpi.setDashboardUrl(kpiWrapper.getDashboardUrl());
    kpi.setGraph(kpiWrapper.getGraph());
    kpi.setInterval(kpiWrapper.getInterval());
    kpi.setKpiId(kpiWrapper.getKpiId());
    kpi.setName(kpiWrapper.getName());
    kpi.setTopic(kpiWrapper.getTopic());
    kpi.setUnit(kpiWrapper.getUnit());
    kpi.setKibanaUser(kpiWrapper.getUser());
    kpi.setDashboardId(kpiWrapper.getDashboardId());
    kpi.setExperiment(null);
    kpi = kpiRepository.saveAndFlush(kpi);
    log.info("KPI {} saved in database", kpi.getKpiId());
    return kpi.getTopic();
  }


  @Override
  public void removeKpi(String kpiId) {
    kpiRepository.deleteById(kpiId);
    log.info("Kpi {} deleted in database", kpiId);
  }

  @Override
  public Optional<Kpi> getKpi(String kpiId) {
    return kpiRepository.findById(kpiId);
  }

  @Override
  public void addKpiToExperiment(Kpi kpi, String experimentId) {
    Optional<Experiment> experimentOp = getExperiment(experimentId);
    if (experimentOp.isPresent()) {
      Experiment experiment = experimentOp.get();
      experiment.addKpi(kpi);
      experimentRepository.saveAndFlush(experiment);
      return;
    }
    log.warn("No Experiment matching the id ", experimentId);
  }

  @Override
  public void removeKpiFromExperiment(String kpiId, String experimentId) {
    Optional<Experiment> experimentOp = experimentRepository.findById(experimentId);
    if (experimentOp.isPresent()) {
      Optional<Kpi> kpiOp = getKpi(kpiId);
      if (kpiOp.isPresent()) {
        Experiment experiment = experimentOp.get();
        Kpi kpi = kpiOp.get();
        experiment.removeKpi(kpi);
        experimentRepository.saveAndFlush(experiment);
        return;
      }
    }
    log.warn("No Experiment matching the id ", experimentId);
  }

  @Override
  public KpiWrapper kpiToWrapper(String kpiId) {
    Optional<Kpi> kpiOp = getKpi(kpiId);
    if (kpiOp.isPresent()) {
      Kpi kpi = kpiOp.get();
      KpiWrapper kpiWrapper = new KpiWrapper();
      kpiWrapper.setDashboardUrl(kpi.getDashboardUrl());
      kpiWrapper.setGraph(kpi.getGraph());
      kpiWrapper.setInterval(kpi.getInterval());
      kpiWrapper.setKpiId(kpi.getKpiId());
      kpiWrapper.setName(kpi.getName());
      kpiWrapper.setTopic(kpi.getTopic());
      kpiWrapper.setUnit(kpi.getUnit());
      kpiWrapper.setDashboardId(kpi.getKpiId());
      kpiWrapper.setUser(kpi.getKibanaUser());
      return kpiWrapper;
    }
    return null;
  }

  @Override
  public List<Kpi> findAllKpisFromExperiment(String experimentId) {
    return kpiRepository.findAllByExperiment_ExpId(experimentId);
  }

  @Override
  public Optional<Kpi> getKpiFromExperimentAndTopic(String experimentId, String topic) {
    return kpiRepository.findByTopicAndExperiment_ExpId(topic, experimentId);
  }

  private static boolean isNullOrEmpty(String str) {
    if(str != null && !str.isEmpty())
        return false;
    return true;
  }

  @Override
  public void updateKpi(KpiWrapper kpiWrapper) {
    Optional<Kpi> kpiOp = getKpi(kpiWrapper.getTopic());
    if (kpiOp.isPresent()) {
      Kpi kpi = kpiOp.get();
      kpi.setDashboardUrl(kpiWrapper.getDashboardUrl());
      kpi.setGraph(kpiWrapper.getGraph());
      kpi.setInterval(kpiWrapper.getInterval());
      kpi.setKpiId(kpiWrapper.getKpiId());
      kpi.setName(kpiWrapper.getName());
      kpi.setUnit(kpiWrapper.getUnit());
      if (!isNullOrEmpty(kpiWrapper.getDashboardId()))
        kpi.setDashboardId(kpiWrapper.getDashboardId());
      if (!isNullOrEmpty(kpiWrapper.getUser()))
        kpi.setKibanaUser(kpiWrapper.getUser());
      kpiRepository.saveAndFlush(kpi);
      log.info("KPI {} updated in database", kpi.getKpiId());
    }
  }

  /* - - - - - - - - - - - - - - - - - Metric - - - - - - - - - - - - - - - - - */

  @Override
  public String createMetric(MetricWrapper metricWrapper) {
    Metric metric = new Metric();
    metric.setDashboardUrl(metricWrapper.getDashboardUrl());
    metric.setGraph(metricWrapper.getGraph());
    metric.setInterval(metricWrapper.getInterval());
    metric.setMetricCollectionType(metricWrapper.getMetricCollectionType());
    metric.setMetricId(metricWrapper.getMetricId());
    metric.setName(metricWrapper.getName());
    metric.setTopic(metricWrapper.getTopic());
    metric.setType(metricWrapper.getType());
    metric.setUnit(metricWrapper.getUnit());
    metric.setExperiment(null);
    metric.setKibanaUser(metricWrapper.getUser());
    metric.setDashboardId(metricWrapper.getDashboardId());
    metric = metricRepository.saveAndFlush(metric);
    log.info("Metric {} saved in database", metric.getMetricId());
    return metric.getTopic();
  }

  @Override
  public void removeMetric(String metricId) {
    metricRepository.deleteById(metricId);
    log.info("Metric {} deleted in database", metricId);
  }

  @Override
  public Optional<Metric> getMetric(String metricId) {
    return metricRepository.findById(metricId);
  }

  @Override
  public void addMetricToExperiment(Metric metric, String experimentId) {
    Optional<Experiment> experimentOp = getExperiment(experimentId);
    if (experimentOp.isPresent()) {
      Experiment experiment = experimentOp.get();
      experiment.addMetric(metric);
      experimentRepository.saveAndFlush(experiment);
      return;
    }
    log.warn("No Experiment matching the id ", experimentId);
  }

  @Override
  public void removeMetricFromExperiment(String metricId, String experimentId) {
    Optional<Experiment> experimentOp = experimentRepository.findById(experimentId);
    if (experimentOp.isPresent()) {
      Optional<Metric> metricOp = getMetric(metricId);
      if (metricOp.isPresent()) {
        Experiment experiment = experimentOp.get();
        Metric metric = metricOp.get();
        experiment.removeMetric(metric);
        experimentRepository.saveAndFlush(experiment);
        return;
      }
    }
    log.warn("No Experiment matching the id ", experimentId);
  }

  @Override
  public MetricWrapper metricToWrapper(String metricId) {
    Optional<Metric> metricOp = metricRepository.findById(metricId);
    if (metricOp.isPresent()) {
      Metric metric = metricOp.get();
      MetricWrapper metricWrapper = new MetricWrapper();
      metricWrapper.setDashboardUrl(metric.getDashboardUrl());
      metricWrapper.setGraph(metric.getGraph());
      metricWrapper.setInterval(metric.getInterval());
      metricWrapper.setMetricId(metric.getMetricId());
      metricWrapper.setName(metric.getName());
      metricWrapper.setTopic(metric.getTopic());
      metricWrapper.setUnit(metric.getUnit());
      metricWrapper.setMetricCollectionType(metric.getMetricCollectionType());
      metricWrapper.setType(metric.getType());
      metricWrapper.setDashboardId(metric.getDashboardId());
      metricWrapper.setUser(metric.getKibanaUser());
      return metricWrapper;
    }
    return null;
  }

  @Override
  public List<Metric> findAllMetricsFromExperiment(String experimentId) {
    return metricRepository.findAllByExperiment_ExpId(experimentId);
  }

  @Override
  public Optional<Metric> getMetricFromExperimentAndTopic(String experimentId, String topic) {
    return metricRepository.findByTopicAndExperiment_ExpId(topic, experimentId);
  }

  @Override
  public void updateMetric(MetricWrapper metricWrapper) {
    Optional<Metric> metricOp = metricRepository.findById(metricWrapper.getTopic());
    if (metricOp.isPresent()) {
      Metric metric = metricOp.get();
      metric.setDashboardUrl(metricWrapper.getDashboardUrl());
      metric.setGraph(metricWrapper.getGraph());
      metric.setInterval(metricWrapper.getInterval());
      metric.setMetricCollectionType(metricWrapper.getMetricCollectionType());
      metric.setMetricId(metricWrapper.getMetricId());
      metric.setName(metricWrapper.getName());
      metric.setType(metricWrapper.getType());
      metric.setUnit(metricWrapper.getUnit());
      if (!isNullOrEmpty(metricWrapper.getUser()))
        metric.setKibanaUser(metricWrapper.getUser());
      if (!isNullOrEmpty(metricWrapper.getDashboardId()))
        metric.setDashboardId(metricWrapper.getDashboardId());
      metricRepository.saveAndFlush(metric);
      log.info("Metric {} updated in database", metric.getMetricId());
    }
  }
}
