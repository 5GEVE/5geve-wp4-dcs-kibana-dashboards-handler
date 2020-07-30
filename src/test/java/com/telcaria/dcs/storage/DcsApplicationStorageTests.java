package com.telcaria.dcs.storage;

import com.telcaria.dcs.storage.entities.Experiment;
import com.telcaria.dcs.storage.entities.Kpi;
import com.telcaria.dcs.storage.entities.Metric;
import com.telcaria.dcs.storage.enums.GraphType;
import com.telcaria.dcs.storage.enums.MetricType;
import com.telcaria.dcs.storage.service.StorageService;
import com.telcaria.dcs.storage.wrappers.ExperimentWrapper;
import com.telcaria.dcs.storage.wrappers.KpiWrapper;
import com.telcaria.dcs.storage.wrappers.MetricWrapper;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@Slf4j
class DcsApplicationStorageTests {

  @Autowired
  private StorageService storageService;

	@Test
  @Order(1)
  void loadExperiment() {
    log.info("loadExperiment");
    ExperimentWrapper experimentWrapper = new ExperimentWrapper();
    experimentWrapper.setUseCase("TestUsecase");
    experimentWrapper.setSiteFacility("TestSiteFacility");
    experimentWrapper.setExpId("testExpId");

    storageService.createExperiment(experimentWrapper);
  }

  @Test
  @Order(2)
  void retrieveExperiment() {
    log.info("retrieveExperiment");
    Optional<Experiment> experimentOp = storageService.getExperiment("testExpId");
    assert experimentOp.isPresent() : "Experiment does not exist";

    Experiment experiment = experimentOp.get();
    assert experiment.getSiteFacility().equals("TestSiteFacility") : "Experiment Site facility is wrong";
  }

  @Test
  @Order(3)
  void createKpi() {
    log.info("createKpi");
    KpiWrapper kpiWrapper = new KpiWrapper();
    kpiWrapper.setUnit("m");
    kpiWrapper.setTopic("1234");
    kpiWrapper.setName("testKPI");
    kpiWrapper.setKpiId("testkpiId");
    kpiWrapper.setInterval(1);
    kpiWrapper.setGraph(GraphType.LINE);
    kpiWrapper.setDashboardUrl("http://localhost");

    storageService.createKpi(kpiWrapper);
  }

  @Test
  @Order(4)
  void retrieveKpi() {
    log.info("retrieveKpi");
    Optional<Kpi> kpiOp = storageService.getKpi("1234");
    assert kpiOp.isPresent() : "Kpi does not exist";

    Kpi kpi = kpiOp.get();
    assert kpi.getName().equals("testKPI") : "Kpi name is wrong";
  }

  @Test
  @Order(5)
  void createMetric() {
    log.info("createMetric");
    MetricWrapper metricWrapper = new MetricWrapper();
    metricWrapper.setUnit("m");
    metricWrapper.setTopic("1234");
    metricWrapper.setName("testMetric");
    metricWrapper.setMetricId("testkpiId");
    metricWrapper.setInterval(1);
    metricWrapper.setGraph(GraphType.LINE);
    metricWrapper.setDashboardUrl("http://localhost");
    metricWrapper.setType(MetricType.APPLICATION_METRIC);
    metricWrapper.setMetricCollectionType("collection-type");

    storageService.createMetric(metricWrapper);
  }

  @Test
  @Order(6)
  void retrieveMetric() {
    log.info("retrieveMetric");
    Optional<Metric> metricOp = storageService.getMetric("1234");
    assert metricOp.isPresent() : "Metric does not exist";

    Metric metric = metricOp.get();
    assert metric.getName().equals("testMetric") : "Metric name is wrong";
  }

  @Test
  @Order(7)
  void addKpiToExperiment() {
    log.info("addKpiToExperiment");
    Optional<Kpi> kpiOp = storageService.getKpi("1234");
    assert kpiOp.isPresent() : "Kpi does not exist";

    Kpi kpi = kpiOp.get();
    storageService.addKpiToExperiment(kpi, "testExpId");
  }

  @Test
  @Order(8)
  void retrieveKpiFromExperiment() {
    log.info("retrieveKpiFromExperiment");
    List<Kpi> kpis = storageService.findAllKpisFromExperiment("testExpId");
    assert !kpis.isEmpty():"Empty Kpis";
    Kpi kpi = kpis.get(0);
    assert kpi.getTopic().equals("1234") : "Kpi not stored correctly";
  }


  @Test
  @Order(9)
  void addMetricToExperiment() {
    log.info("addMetricToExperiment");
    Optional<Metric> metricOp = storageService.getMetric("1234");
    assert metricOp.isPresent() : "Metric does not exist";

    Metric metric = metricOp.get();
    storageService.addMetricToExperiment(metric, "testExpId");
  }

  @Test
  @Order(10)
  void retrieveMetricFromExperiment() {
    log.info("retrieveMetricFromExperiment");
    List<Metric> metrics = storageService.findAllMetricsFromExperiment("testExpId");
    assert !metrics.isEmpty():"Empty Metrics";
    Metric metric = metrics.get(0);
    assert metric.getTopic().equals("1234") : "Metric not stored correctly";
  }

  @Test
  @Order(11)
  void retrieveExperimentFromKpi() {
    log.info("retrieveExperimentFromKpi");
    Experiment experiment = storageService.findExperimentFromKpi("1234");
    assert experiment != null:"Experiment does not exist";
    assert experiment.getSiteFacility().equals("TestSiteFacility") : "Experiment Site facility is wrong";
  }

  @Test
  @Order(12)
  void retrieveExperimentFromMetric() {
    log.info("retrieveExperimentFromMetric");
    Experiment experiment = storageService.findExperimentFromMetric("1234");
    assert experiment != null:"Experiment does not exist";
    assert experiment.getSiteFacility().equals("TestSiteFacility") : "Experiment Site facility is wrong";
  }

}
