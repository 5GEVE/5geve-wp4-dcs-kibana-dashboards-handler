package com.telcaria.dcs.nbi.controller;


import com.telcaria.dcs.nbi.wrapper.DashboardWrapper;
import com.telcaria.dcs.nbi.wrapper.DcsRequestWrapper;
import com.telcaria.dcs.nbi.wrapper.elasticsearch.ElasticsearchDcsResponseWrapper;
import com.telcaria.dcs.nbi.wrapper.elasticsearch.ElasticsearchParametersWrapper;
import com.telcaria.dcs.nbi.wrapper.RecordWrapper;
import com.telcaria.dcs.nbi.wrapper.TopicsWrapper;
import com.telcaria.dcs.elasticsearch.wrapper.ElasticsearchResponseWrapper;
import com.telcaria.dcs.service.DcsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.security.RolesAllowed;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping(value = "/portal/dcs")
public class DcsController {

  private DcsService dcsService;

  @Autowired
  public DcsController(DcsService dcsService) {
    this.dcsService = dcsService;
  }

  @PostMapping(value = "/dashboard")
  public @ResponseBody ResponseEntity createDashboard(@RequestBody DcsRequestWrapper dcsRequestWrapper) {
    //TODO: manage exceptions?
    for (RecordWrapper recordWrapper : dcsRequestWrapper.getRecords()) {
      log.info("Create dashboard for {}", recordWrapper.getValue().getTopic());
      dcsService.createDashboardRequest(recordWrapper.getValue());
    }
    return new ResponseEntity(HttpStatus.ACCEPTED);
  }

  @GetMapping(value = "/dashboard/{experimentId}")
  public @ResponseBody DashboardWrapper getDashboard(@PathVariable String experimentId) {
    //TODO: manage exceptions?
    log.info("Get dashboards for expId {}", experimentId);
    return dcsService.getDashboardRequest(experimentId);
  }

  @DeleteMapping(value = "/dashboard")
  public @ResponseBody ResponseEntity deleteDashboard(@RequestBody DcsRequestWrapper dcsRequestWrapper) {
    //TODO: manage exceptions?
    for (RecordWrapper recordWrapper : dcsRequestWrapper.getRecords()) {
      log.info("Delete dashboard for {}", recordWrapper.getValue().getTopic());
      dcsService.deleteDashboardRequest(recordWrapper.getValue());
    }
    return new ResponseEntity(HttpStatus.ACCEPTED);
  }

  @RolesAllowed("view-dashboards")
  @GetMapping(value = "/topics/{experimentId}")
  public @ResponseBody TopicsWrapper getTopics(@PathVariable String experimentId, @RequestHeader("Authorization") String token) {
    //TODO: manage exceptions?
    log.info("Get topics for expId {}", experimentId);

    return dcsService.getTopics(experimentId, token.split(" ")[1]);
  }

  @RolesAllowed("view-dashboards")
  @GetMapping(value = "/topics/{experimentId}/{topic}")
  public @ResponseBody ElasticsearchDcsResponseWrapper getDataFromTopic(@PathVariable("experimentId") String experimentId,
                                                   @PathVariable("topic") String topic,
                                                   @RequestHeader("Authorization") String token,
                                                   @RequestBody ElasticsearchParametersWrapper elasticsearchParametersWrapper) {
    //TODO: manage exceptions?
    log.info("Get data from for expId {} - topic {}", experimentId, topic);

    return dcsService.getDataFromTopic(experimentId, topic, token.split(" ")[1], elasticsearchParametersWrapper);
  }
}
