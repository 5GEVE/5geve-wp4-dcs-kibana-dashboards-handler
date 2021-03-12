/*
 * Copyright 2020-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.telcaria.dcs.service;

import com.telcaria.dcs.authentication.service.KeycloakAuthenticationService;
import com.telcaria.dcs.elasticsearch.service.ElasticsearchService;
import com.telcaria.dcs.kibana.service.KeycloakService;
import com.telcaria.dcs.kibana.service.KibanaService;
import com.telcaria.dcs.nbi.wrapper.ContextWrapper;
import com.telcaria.dcs.nbi.wrapper.DashboardWrapper;
import com.telcaria.dcs.nbi.wrapper.TopicWrapper;
import com.telcaria.dcs.nbi.wrapper.TopicsWrapper;
import com.telcaria.dcs.nbi.wrapper.UrlWrapper;
import com.telcaria.dcs.nbi.wrapper.ValueWrapper;
import com.telcaria.dcs.nbi.wrapper.elasticsearch.ElasticsearchDcsResponseWrapper;
import com.telcaria.dcs.nbi.wrapper.elasticsearch.ElasticsearchParametersWrapper;
import com.telcaria.dcs.elasticsearch.wrapper.ElasticsearchResponseWrapper;
import com.telcaria.dcs.storage.entities.Experiment;
import com.telcaria.dcs.storage.entities.Kpi;
import com.telcaria.dcs.storage.entities.Metric;
import com.telcaria.dcs.storage.enums.GraphType;
import com.telcaria.dcs.storage.enums.MetricType;
import com.telcaria.dcs.storage.service.StorageService;
import com.telcaria.dcs.storage.wrappers.ExperimentWrapper;
import com.telcaria.dcs.storage.wrappers.KpiWrapper;
import com.telcaria.dcs.storage.wrappers.MetricWrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class DcsServiceImpl implements DcsService {

  private StorageService storageService;
  private KibanaService kibanaService;
  private KeycloakService keycloakService;
  private KeycloakAuthenticationService keycloakAuthenticationService;
  private ElasticsearchService elasticsearchService;

  @Value("${kibana.enabled}")
  private boolean isKibanaEnabled = true;

  @Autowired
  public DcsServiceImpl(StorageService storageService,
                        KibanaService kibanaService,
                        KeycloakService keycloakService,
                        KeycloakAuthenticationService keycloakAuthenticationService,
                        ElasticsearchService elasticsearchService) {
    this.storageService = storageService;
    this.kibanaService = kibanaService;
    this.keycloakService = keycloakService;
    this.keycloakAuthenticationService = keycloakAuthenticationService;
    this.elasticsearchService = elasticsearchService;
  }

  @Override
  public String createDashboardRequest(ValueWrapper valueWrapper) {
    // Create experiment in a synchronous way.
    try {
      waitFunction();
    } catch (Exception e) {
      log.error("Exception due to waitFunction for {}", valueWrapper);
    }
    return createDashboard(valueWrapper);
  }

  @Override
  public DashboardWrapper getDashboardRequest(String experimentId) {
    Optional<Experiment> experimentOp = storageService.getExperiment(experimentId);
    List<UrlWrapper> urlWrappers = new ArrayList<>();
    if (experimentOp.isPresent()) {
      List<Kpi> kpis = storageService.findAllKpisFromExperiment(experimentId);
      List<Metric> metrics = storageService.findAllMetricsFromExperiment(experimentId);
      for (Kpi kpi : kpis) {
        urlWrappers.add(new UrlWrapper(kpi.getDashboardUrl()));
        log.info("Added URL {}", kpi.getDashboardUrl());
      }
      for (Metric metric : metrics) {
        urlWrappers.add(new UrlWrapper(metric.getDashboardUrl()));
        log.info("Added URL {}", metric.getDashboardUrl());
      }

      if (urlWrappers.size() > 0) {
        // If there are URLs to show, check Keycloak token, and then continue.
        log.info("Check Keycloak token for nginx authentication");
        checkKeycloakToken();
      }

      return new DashboardWrapper(urlWrappers);
    }

    return new DashboardWrapper(urlWrappers);
  }

  @Override
  public String deleteDashboardRequest(ValueWrapper valueWrapper) {
    String experimentId = valueWrapper.getExpId();
    if (storageService.getExperiment(valueWrapper.getExpId()).isPresent()) {
      ContextWrapper contextWrapper = valueWrapper.getContext();
      if (contextWrapper.getKpiId() != null) { // If Kpi
        Optional<Kpi> kpiOp = storageService.getKpi(valueWrapper.getTopic());
        if (kpiOp.isPresent()) {
          Kpi kpi = kpiOp.get();
          // Remove Dashboard from Kibana
          if (isKibanaEnabled)
            kibanaService.deleteKibanaDashboard(kpi);
          // Remove from database
          storageService.removeKpiFromExperiment(valueWrapper.getTopic(), experimentId);
          storageService.removeKpi(valueWrapper.getTopic());
        } else {
          log.error("KPI {} does not exist", contextWrapper.getKpiId());
          return "KPI " + contextWrapper.getKpiId() + " does not exist";
        }
      } else if (contextWrapper.getMetricId() != null) { // Else if Metric
        Optional<Metric> metricOp = storageService.getMetric(valueWrapper.getTopic());
        if (metricOp.isPresent()) {
          Metric metric = metricOp.get();
          // Remove Dashboard from Kibana
          if (isKibanaEnabled)
            kibanaService.deleteKibanaDashboard(metric);
          // Remove from database
          storageService.removeMetricFromExperiment(valueWrapper.getTopic(), experimentId);
          storageService.removeMetric(valueWrapper.getTopic());
        } else {
          log.error("Metric {} does not exist", contextWrapper.getMetricId());
          return "Metric " + contextWrapper.getMetricId() + " does not exist";
        }
      } else {
          log.error("Data not matching Metric or KPI");
          return "Data not matching Metric or KPI";
      }

      // Clean experiment once all Kpis and metris removed
      if (storageService.isExperimentEmpty(experimentId)){
        storageService.removeExperiment(experimentId);
      }
      return "OK";

    }
    log.error("Experiment Id does not exist");
    return "Experiment Id does not exist";
  }

  @Override
  public TopicsWrapper getTopics(String experimentId, String token) {
    Optional<Experiment> experimentOp = storageService.getExperiment(experimentId);
    List<TopicWrapper> topicWrappers = new ArrayList<>();
    if (experimentOp.isPresent()) {
      List<Kpi> kpis = storageService.findAllKpisFromExperiment(experimentId);
      List<Metric> metrics = storageService.findAllMetricsFromExperiment(experimentId);

      // Only returning KPIs and Metrics to which the user is allowed to access, according to the token
      for (Kpi kpi : kpis) {
        if (checkIfAnyUserExistsInKeycloakToken(kpi.getKibanaUser().split(","), token)) {
          topicWrappers.add(new TopicWrapper(kpi.getTopic()));
          log.info("Added topic {}", kpi.getTopic());
        } else {
          log.info("Topic {} not added because users are not included in token", kpi.getTopic());
        }
      }
      for (Metric metric : metrics) {
        if (checkIfAnyUserExistsInKeycloakToken(metric.getKibanaUser().split(","), token)) {
          topicWrappers.add(new TopicWrapper(metric.getTopic()));
          log.info("Added topic {}", metric.getTopic());
        } else {
          log.info("Topic {} not added because users are not included in token", metric.getTopic());
        }
      }
      return new TopicsWrapper(topicWrappers);
    }
    return new TopicsWrapper(topicWrappers);
  }

  @Override
  public ElasticsearchDcsResponseWrapper getDataFromTopic(String experimentId, String topic,
                                                          String token,
                                                          ElasticsearchParametersWrapper elasticsearchParametersWrapper) {

    ElasticsearchDcsResponseWrapper response = new ElasticsearchDcsResponseWrapper();

    if (topic.contains(".kpi.")) {
      Optional<Kpi> kpiOp = storageService.getKpiFromExperimentAndTopic(experimentId, topic);
      if (kpiOp.isPresent()) {
        if (checkIfAnyUserExistsInKeycloakToken(kpiOp.get().getKibanaUser().split(","), token)) {
          response = elasticsearchService.searchData(topic.toLowerCase(Locale.ROOT), elasticsearchParametersWrapper);
        } else {
          log.warn("User not allowed to see data of topic {}", topic);
        }
      } else {
        log.warn("Topic not present in DB {}", topic);
      }
    } else if (topic.contains("_metric.")) {
      Optional<Metric> metricOp = storageService.getMetricFromExperimentAndTopic(experimentId, topic);
      if (metricOp.isPresent()) {
        if (checkIfAnyUserExistsInKeycloakToken(metricOp.get().getKibanaUser().split(","), token)) {
          response = elasticsearchService.searchData(topic.toLowerCase(Locale.ROOT), elasticsearchParametersWrapper);
        } else {
          log.warn("User not allowed to see data of topic {}", topic);
        }
      } else {
        log.warn("Topic not present in DB {}", topic);
      }
    } else {
      log.warn("Incorrect format for topic name {}", topic);
    }

    return response;
  }

  @Override
  public void disableKibana() {
    this.isKibanaEnabled = false;
  }

  private void waitFunction () throws InterruptedException {
    int upper = 1000;
    int lower = 1;
    int r = (int) (Math.random() * (upper - lower)) + lower;
    Thread.sleep(r);
  }

  private synchronized String createDashboard(ValueWrapper valueWrapper) {
    if (!storageService.getExperiment(valueWrapper.getExpId()).isPresent()){
      // Create Experiment Wrapper
      log.info("Creating experiment {} in DB", valueWrapper.getExpId());
      ExperimentWrapper experimentWrapper = new ExperimentWrapper();
      experimentWrapper.setExpId(valueWrapper.getExpId());
      String topic = valueWrapper.getTopic();
      experimentWrapper.setSiteFacility(getSiteFacilityFromTopic(topic));
      experimentWrapper.setUseCase(getUseCaseFromTopic(topic));

      // Persist Experiment
      try {
        storageService.createExperiment(experimentWrapper);
      }
      catch (Exception e) {
        log.warn("Trying to save experiment {} that is already in DB - concurrency problems", valueWrapper.getExpId());
      }
    } else {
      log.info("Experiment {} is present in DB", valueWrapper.getExpId());
    }

    String experimentId = valueWrapper.getExpId();
    ContextWrapper contextWrapper = valueWrapper.getContext();
    if (contextWrapper.getKpiId() != null) { // If Kpi
      KpiWrapper kpiWrapper = kpiWrapperFromValueWrapper(valueWrapper);

      log.info("Creating KPI {} in DB", kpiWrapper.getKpiId());
      // Persist Kpi
      String kpiId = storageService.createKpi(kpiWrapper);

      if (storageService.getKpi(kpiId).isPresent()){
        Kpi kpi = storageService.getKpi(kpiId).get();
        storageService.addKpiToExperiment(kpi, experimentId);
        // Create Dashboard in Kibana
        if (isKibanaEnabled){
          String dashboardUrl = kibanaService.createKibanaDashboard(kpi);
          if (dashboardUrl != null){
            log.info("Dashboard Visualization for Kpi {} Created", kpiWrapper.getKpiId());
            // Extract dashboard URL
            kpiWrapper.setDashboardUrl(dashboardUrl);
            // Update DB entities with Dashboard data
            storageService.updateKpi(kpiWrapper);
            return "OK";
          } else {
            log.error("Dashboard Visualization Error for Kpi {}", kpiWrapper.getKpiId());
            return "Dashboard Visualization Error for Kpi " + kpiWrapper.getKpiId();
          }
        } else {
          return "OK";
        }

      } else {
        log.error("KPI {} was not retrieved correctly", kpiWrapper.getKpiId());
        return "Dashboard Visualization Error for Kpi " + kpiWrapper.getKpiId();
      }

    } else if (contextWrapper.getMetricId() != null) { // Elif Metric
      MetricWrapper metricWrapper = metricWrapperFromValueWrapper(valueWrapper);
      log.info("Creating Metric {} in DB", metricWrapper.getMetricId());

      // Persist Metric
      String metricId = storageService.createMetric(metricWrapper);

      if (storageService.getMetric(metricId).isPresent()){
        Metric metric = storageService.getMetric(metricId).get();
        storageService.addMetricToExperiment(metric, experimentId);
        // Create Dashboard in Kibana
        if (isKibanaEnabled){
          String dashboardUrl = kibanaService.createKibanaDashboard(metric);
          if (dashboardUrl != null){
            log.info("Dashboard Visualization for Metric {} Created", metricWrapper.getMetricId());
            // Extract dashboard URL
            metricWrapper.setDashboardUrl(dashboardUrl);
            // Update DB entities with Dashboard data
            storageService.updateMetric(metricWrapper);
            return "OK";
          } else {
            log.error("Dashboard Visualization Error for Metric {}", metricWrapper.getMetricId());
            return "Dashboard Visualization Error for Metric " + metricWrapper.getMetricId();
          }
        } else {
          return "OK";
        }
      } else {
        log.error("Metric {} was not retrieved correctly", metricWrapper.getMetricId());
        return "Metric " + metricWrapper.getMetricId() + " was not retrieved correctly";
      }
    } else {
      log.error("Data not matching Metric or KPI");
      return "Data not matching Metric or KPI";
    }
  }

  private MetricWrapper metricWrapperFromValueWrapper(ValueWrapper valueWrapper){
    ContextWrapper contextWrapper = valueWrapper.getContext();
    MetricWrapper metricWrapper = new MetricWrapper();
    metricWrapper.setMetricId(contextWrapper.getMetricId());
    metricWrapper.setTopic(valueWrapper.getTopic());
    metricWrapper.setName(contextWrapper.getName());
    String topic = valueWrapper.getTopic();
    metricWrapper.setType(MetricType.valueOf(getMetricTypeFromTopic(topic)));
    metricWrapper.setUnit(contextWrapper.getUnit());
    metricWrapper.setInterval(getIntervalFromString(contextWrapper.getInterval()));
    metricWrapper.setMetricCollectionType(contextWrapper.getMetricCollectionType());
    metricWrapper.setDashboardId(generateUUID());
    metricWrapper.setUser(keycloakService.getUsersByUseCase(getUseCaseFromTopic(valueWrapper.getTopic())));
    try {
      metricWrapper.setGraph(GraphType.valueOf(contextWrapper.getGraph()));
    } catch (Exception e){
      if (metricWrapper.getMetricCollectionType().equals("CUMULATIVE")){
        metricWrapper.setGraph(GraphType.LINE);
      } else if (metricWrapper.getMetricCollectionType().equals("GAUGE")) {
        metricWrapper.setGraph(GraphType.GAUGE);
      }
    }
    return metricWrapper;
  }

  private KpiWrapper kpiWrapperFromValueWrapper(ValueWrapper valueWrapper) {
    ContextWrapper contextWrapper = valueWrapper.getContext();
    KpiWrapper kpiWrapper = new KpiWrapper();
    kpiWrapper.setKpiId(contextWrapper.getKpiId());
    kpiWrapper.setTopic(valueWrapper.getTopic());
    kpiWrapper.setName(contextWrapper.getName());
    kpiWrapper.setUnit(contextWrapper.getUnit());
    kpiWrapper.setInterval(getIntervalFromString(contextWrapper.getInterval()));
    kpiWrapper.setUser(keycloakService.getUsersByUseCase(getUseCaseFromTopic(valueWrapper.getTopic())));
    kpiWrapper.setDashboardId(generateUUID());
    try {
      kpiWrapper.setGraph(GraphType.valueOf(contextWrapper.getGraph()));
    } catch (Exception e){
      kpiWrapper.setGraph(GraphType.LINE);
    }
    return kpiWrapper;
  }
  private String generateUUID(){
    return UUID.randomUUID().toString();
  }

  private String getUseCaseFromTopic(String topic) {
    String[] topicParsed = topic.split("\\.");
    return topicParsed[0];
  }

  private String getSiteFacilityFromTopic(String topic) {
    String[] topicParsed = topic.split("\\.");
    return topicParsed[2];
  }

  private String getMetricTypeFromTopic(String topic) {
    String[] topicParsed = topic.split("\\.");
    return topicParsed[3].toUpperCase();
  }

  private double getIntervalFromString(String interval) {
    if (interval.contains("ms")) {
      return Integer.parseInt(interval.replaceAll("\\D+",""))/1000;
    } else if (interval.contains("s")) {
      return (double) Integer.parseInt(interval.replaceAll("\\D+",""));
    }
    return -1;
  }

  private void checkKeycloakToken() {
    // Read token from file
    String token = keycloakAuthenticationService.extractTokenFromFile();

    // Check if the token is valid
    if(!keycloakAuthenticationService.checkIfTokenIsActive(token)) {
      log.info("Token not valid, generating a new one");
      // Change token
      String newToken = keycloakAuthenticationService.requestToken();
      keycloakAuthenticationService.updateTokenInFile(token, newToken);
    } else {
      log.info("Token valid");
    }
  }

  private boolean checkIfAnyUserExistsInKeycloakToken(String[] users, String token) {

    boolean exists = false;
    int i = 0;
    String userFromToken = keycloakAuthenticationService.obtainUserFromToken(token);

    while (!exists && i < users.length) {
      if (users[i].equals(userFromToken)) {
        exists = true;
      } else {
        i++;
      }
    }

    return exists;
  }
}
