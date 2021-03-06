package com.telcaria.dcs.storage.controllers;


import com.telcaria.dcs.storage.service.StorageService;
import com.telcaria.dcs.storage.wrappers.ExperimentWrapper;
import com.telcaria.dcs.storage.wrappers.KpiWrapper;
import com.telcaria.dcs.storage.wrappers.MetricWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/storage")
public class StorageController {

  private StorageService storageService;

  @Autowired
  public StorageController(StorageService storageService) {
    this.storageService = storageService;
  }

  @GetMapping(value = "/experiment/{experimentId}")
  public @ResponseBody ExperimentWrapper getExperiment(@PathVariable String experimentId) {
    return storageService.experimentToWrapper(experimentId);
  }

  @GetMapping(value = "/kpi/{kpiId}")
  public @ResponseBody KpiWrapper getKpi(@PathVariable String kpiId) {
    return storageService.kpiToWrapper(kpiId);
  }

  @GetMapping(value = "/metric/{metricId}")
  public @ResponseBody MetricWrapper getMetric(@PathVariable String metricId) {
    return storageService.metricToWrapper(metricId);
  }

  @PostMapping(value = "/experiment")
  public @ResponseBody String createExperiment(@RequestBody ExperimentWrapper experimentWrapper) {
    storageService.createExperiment(experimentWrapper);
    return "OK";
  }

  @PostMapping(value = "/kpi")
  public @ResponseBody String createKpi(@RequestBody KpiWrapper kpiWrapper) {
    storageService.createKpi(kpiWrapper);
    return "OK";
  }

  @PostMapping(value = "/metric")
  public @ResponseBody String createMetric(@RequestBody MetricWrapper metricWrapper) {
    storageService.createMetric(metricWrapper);
    return "OK";
  }

  @DeleteMapping(value = "/experiment/{experimentId}")
  public @ResponseBody String deleteExperiment(@PathVariable String experimentId) {
    storageService.removeExperiment(experimentId);
    return "OK";
  }

  @DeleteMapping(value = "/kpi/{kpiId}")
  public @ResponseBody String deleteKpi(@PathVariable String kpiId) {
    storageService.removeKpi(kpiId);
    return "OK";
  }

  @DeleteMapping(value = "/metric/{metricId}")
  public @ResponseBody String deleteMetric(@PathVariable String metricId) {
    storageService.removeMetric(metricId);
    return "OK";
  }

}
