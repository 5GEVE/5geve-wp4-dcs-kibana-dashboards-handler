package com.telcaria.dcs.service;

import com.telcaria.dcs.nbi.wrapper.ContextWrapper;
import com.telcaria.dcs.nbi.wrapper.ValueWrapper;
import com.telcaria.dcs.storage.entities.Metric;
import com.telcaria.dcs.storage.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;


@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@Slf4j
class DcsApplicationServiceTests {

  @Autowired
  private DcsService dcsService;

  @Autowired
  private StorageService storageService;

  @Test
  void createDashboardTestSimple2() {
    log.info("ADD Metric Dashboard Test");
    ValueWrapper valueWrapper = new ValueWrapper();
    valueWrapper.setTopic("uc.4.france_nice.infrastructure_metric.expb_metricid");
    valueWrapper.setAction("");
    valueWrapper.setExpId("experimentid1");

    ContextWrapper contextWrapper = new ContextWrapper();
    contextWrapper.setName("name");
    contextWrapper.setInterval("5s");
    contextWrapper.setMetricId("metricid1");
    contextWrapper.setMetricCollectionType("CUMULATIVE");
    contextWrapper.setGraph("LINE");
    contextWrapper.setUnit("m");

    valueWrapper.setContext(contextWrapper);

    assert dcsService.createDashboardRequest(valueWrapper).equals("OK") : "ERROR";

    assert dcsService.deleteDashboardRequest(valueWrapper).equals("OK") : "ERROR 1";
  }

  @Test
  @Order(1)
  void createDashboardTestSimple() {
    log.info("Create Dashboard Test");
    dcsService.disableKibana();
    ValueWrapper valueWrapper = new ValueWrapper();
    valueWrapper.setTopic("EXPID.USECASE.SITEFACILITY.infrastructure.DATA_ID1");
    valueWrapper.setAction("");
    valueWrapper.setExpId("experimentid1");

    ContextWrapper contextWrapper = new ContextWrapper();
    contextWrapper.setName("name");
    contextWrapper.setInterval("5s");
    contextWrapper.setKpiId("kpiid1");
    contextWrapper.setGraph("GAUGE");
    contextWrapper.setUnit("m");

    valueWrapper.setContext(contextWrapper);

    assert dcsService.createDashboardRequest(valueWrapper).equals("OK") : "ERROR";

  }

  @Test
  @Order(2)
  void addKpiToDashboard() {
    log.info("ADD KPI Dashboard Test");
    ValueWrapper valueWrapper = new ValueWrapper();
    valueWrapper.setTopic("EXPID.USECASE.SITEFACILITY.infrastructure.DATA_ID2");
    valueWrapper.setAction("");
    valueWrapper.setExpId("experimentid1");

    ContextWrapper contextWrapper = new ContextWrapper();
    contextWrapper.setName("name");
    contextWrapper.setInterval("5s");
    contextWrapper.setKpiId("kpiid2");
    contextWrapper.setGraph("GAUGE");
    contextWrapper.setUnit("m");

    valueWrapper.setContext(contextWrapper);

    assert dcsService.createDashboardRequest(valueWrapper).equals("OK") : "ERROR";;

  }

  @Test
  @Order(3)
  void addMetricToDashboard() {
    log.info("ADD Metric Dashboard Test");
    ValueWrapper valueWrapper = new ValueWrapper();
    valueWrapper.setTopic("EXPID.USECASE.SITEFACILITY.infrastructure.DATA_ID3");
    valueWrapper.setAction("");
    valueWrapper.setExpId("experimentid1");

    ContextWrapper contextWrapper = new ContextWrapper();
    contextWrapper.setName("name");
    contextWrapper.setInterval("5s");
    contextWrapper.setMetricId("metricid1");
    contextWrapper.setMetricCollectionType("CUMULATIVE");
    contextWrapper.setGraph("GAUGE");
    contextWrapper.setUnit("m");

    valueWrapper.setContext(contextWrapper);

    assert dcsService.createDashboardRequest(valueWrapper).equals("OK") : "ERROR";;

  }

  @Test
  @Order(4)
  void removeMetricFromDashboard() {
    log.info("Remove Metric from Dashboard Test");
    ValueWrapper valueWrapper = new ValueWrapper();
    valueWrapper.setTopic("EXPID.USECASE.SITEFACILITY.infrastructure.DATA_ID3");
    valueWrapper.setAction("");
    valueWrapper.setExpId("experimentid1");

    ContextWrapper contextWrapper = new ContextWrapper();
    contextWrapper.setName("name");
    contextWrapper.setInterval("5s");
    contextWrapper.setMetricId("metricid1");
    contextWrapper.setMetricCollectionType("CUMULATIVE");
    contextWrapper.setGraph("GAUGE");
    contextWrapper.setUnit("m");

    valueWrapper.setContext(contextWrapper);

    assert dcsService.deleteDashboardRequest(valueWrapper).equals("OK") : "ERROR 1";
    assert !storageService.getMetric("EXPID.USECASE.SITEFACILITY.infrastructure.DATA_ID3").isPresent() : "ERROR 2";
    assert storageService.getExperiment("experimentid1").isPresent() : "ERROR 3";
  }

  @Test
  @Order(5)
  void removeKpiToDashboard() {
    log.info("Remove KPI from Dashboard Test");
    ValueWrapper valueWrapper = new ValueWrapper();
    valueWrapper.setTopic("EXPID.USECASE.SITEFACILITY.infrastructure.DATA_ID2");
    valueWrapper.setAction("");
    valueWrapper.setExpId("experimentid1");

    ContextWrapper contextWrapper = new ContextWrapper();
    contextWrapper.setName("name");
    contextWrapper.setInterval("5s");
    contextWrapper.setKpiId("kpiid2");
    contextWrapper.setGraph("GAUGE");
    contextWrapper.setUnit("m");

    valueWrapper.setContext(contextWrapper);

    assert dcsService.deleteDashboardRequest(valueWrapper).equals("OK") : "ERROR 1";
    assert !storageService.getKpi("EXPID.USECASE.SITEFACILITY.infrastructure.DATA_ID2").isPresent() : "ERROR 2";
  }

  @Test
  @Order(6)
  void removeLastKpiDashboard() {
    log.info("Remove last kpi from Dashboard");
    dcsService.disableKibana();
    ValueWrapper valueWrapper = new ValueWrapper();
    valueWrapper.setTopic("EXPID.USECASE.SITEFACILITY.infrastructure.DATA_ID1");
    valueWrapper.setAction("");
    valueWrapper.setExpId("experimentid1");

    ContextWrapper contextWrapper = new ContextWrapper();
    contextWrapper.setName("name");
    contextWrapper.setInterval("5s");
    contextWrapper.setKpiId("kpiid1");
    contextWrapper.setGraph("GAUGE");
    contextWrapper.setUnit("m");

    valueWrapper.setContext(contextWrapper);

    assert dcsService.deleteDashboardRequest(valueWrapper).equals("OK") : "ERROR 1";
    assert !storageService.getKpi("EXPID.USECASE.SITEFACILITY.infrastructure.DATA_ID1").isPresent() : "ERROR 2";
    assert !storageService.getExperiment("experimentid1").isPresent() : "ERROR 3";

  }


}

