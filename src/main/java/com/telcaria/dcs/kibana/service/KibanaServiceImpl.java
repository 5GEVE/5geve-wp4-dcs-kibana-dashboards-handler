package com.telcaria.dcs.kibana.service;

//import com.telcaria.dcs.kibana.client.KibanaConnector;
import com.telcaria.dcs.kibana.client.KibanaConnectorService;
import com.telcaria.dcs.kibana.client.KibanaProperties;
import com.telcaria.dcs.storage.entities.Kpi;
import com.telcaria.dcs.storage.entities.Metric;
import com.telcaria.dcs.storage.enums.GraphType;
import com.telcaria.kibana.dashboards.Generator;
import com.telcaria.kibana.dashboards.model.KibanaDashboardDescription;
import com.telcaria.kibana.dashboards.model.KibanaDashboardVisualization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
public class KibanaServiceImpl implements KibanaService{

    @Autowired
    private KibanaProperties kibanaProperties;
    @Autowired
    private KibanaConnectorService kibanaConnectorService;
    private Generator generator = new Generator();

    @Override
    public String createKibanaVisualization(Kpi kpi){
        KibanaDashboardVisualization kibanaVisualization = new KibanaDashboardVisualization();
        kibanaVisualization.setTitle(kpi.getKpiId());
        kibanaVisualization.setId(kpi.getTopic().toLowerCase());
        kibanaVisualization.setVisualizationType(kpi.getGraph().toString());
        kibanaVisualization.setIndex(kpi.getTopic().toLowerCase());

        //TODO: plot several graphs differentiating by "device_id" attribute?
        kibanaVisualization.setMetricNameX("device_timestamp");
        kibanaVisualization.setMetricNameY("kpi_value");
        kibanaVisualization.setAggregationType("avg");

        String kibanaObject = generator.createVisualizationObject(kibanaVisualization);
        if(kibanaConnectorService.postKibanaObject(kibanaObject)){
            return getUrl(kibanaVisualization.getId());
        } else {
            return null;
        }
    }

    @Override
    public String createKibanaVisualization(Metric metric){
        KibanaDashboardVisualization kibanaVisualization = new KibanaDashboardVisualization();
        kibanaVisualization.setTitle(metric.getMetricId());
        kibanaVisualization.setId(metric.getTopic().toLowerCase());
        kibanaVisualization.setVisualizationType(metric.getGraph().toString());
        kibanaVisualization.setIndex(metric.getTopic().toLowerCase());

        if(metric.getGraph().equals(GraphType.LINE)) {
            //line
            kibanaVisualization.setMetricNameX("device_timestamp");
            kibanaVisualization.setMetricNameY("metric_value");
            kibanaVisualization.setAggregationType("avg");
        } else if(metric.getGraph().equals(GraphType.PIE)){
            //pie
            //TODO: plot several graphs differentiating by "device_id" attribute?
            kibanaVisualization.setMetricNameX("metric_value");
            kibanaVisualization.setAggregationType("count");

        }else if(metric.getGraph().equals(GraphType.COUNTER)){ // visualizationType==metric
            //type count, do nothing
        }

        String jsonKibanaObject = generator.createVisualizationObject(kibanaVisualization);
        if(kibanaConnectorService.postKibanaObject(jsonKibanaObject)){
            return getUrl(kibanaVisualization.getId());
        } else {
            return null;
        }
    }

    @Override
    public String createKibanaDashboard(Kpi kpi) {

        KibanaDashboardDescription kibanaDashboardDescription = new KibanaDashboardDescription();
        kibanaDashboardDescription.setDashboardId(kpi.getDashboardId());
        kibanaDashboardDescription.setIndex(kpi.getTopic().toLowerCase());
        kibanaDashboardDescription.setDashboardTitle(kpi.getKpiId());

        KibanaDashboardVisualization kibanaVisualization = new KibanaDashboardVisualization();
        kibanaVisualization.setTitle(kpi.getKpiId());
        kibanaVisualization.setId(kpi.getTopic().toLowerCase());
        kibanaVisualization.setVisualizationType(kpi.getGraph().toString());
        kibanaVisualization.setIndex(kpi.getTopic().toLowerCase());
        kibanaVisualization.setYlabel(kpi.getName() + " (" + kpi.getUnit() + ")");


        if(kpi.getGraph().equals(GraphType.LINE)) {
            //line
            kibanaVisualization.setMetricNameX("device_timestamp");
            kibanaVisualization.setMetricNameY("kpi_value");
            kibanaVisualization.setXlabel("device_timestamp");
            kibanaVisualization.setAggregationType("avg");

        } else if(kpi.getGraph().equals(GraphType.PIE)){
            //pie
            //TODO: plot several graphs differentiating by "device_id" attribute?
            kibanaVisualization.setMetricNameX("kpi_value");
            kibanaVisualization.setAggregationType("count");

        }else if(kpi.getGraph().equals(GraphType.COUNTER)){ // visualizationType==metric
            //type count, do nothing
        }


        kibanaDashboardDescription.addVisualizationsItem(kibanaVisualization);
        List<String> kibanaObjects = generator.translate(kibanaDashboardDescription);
        //String jsonKibanaObject = kibanaObjects.get(0);

        for (String jsonKibanaObject : kibanaObjects) {
            if(!kibanaConnectorService.postKibanaObject(jsonKibanaObject)){
                return null;
            }
        }

        if (kibanaProperties.isDashboardOwnerEnabled()){
            if(!kibanaConnectorService.setOwner(kpi.getDashboardId(), kpi.getKibanaUser())){
                return null;
            }
        }
        return getUrl(kibanaDashboardDescription.getDashboardId());
    }

    @Override
    public String createKibanaDashboard(Metric metric) {

        KibanaDashboardDescription kibanaDashboardDescription = new KibanaDashboardDescription();
        kibanaDashboardDescription.setDashboardId(metric.getDashboardId());
        kibanaDashboardDescription.setIndex(metric.getTopic().toLowerCase());
        kibanaDashboardDescription.setDashboardTitle(metric.getMetricId());

        KibanaDashboardVisualization kibanaVisualization = new KibanaDashboardVisualization();
        kibanaVisualization.setTitle(metric.getMetricId());
        kibanaVisualization.setId(metric.getTopic().toLowerCase());
        kibanaVisualization.setVisualizationType(metric.getGraph().toString());
        kibanaVisualization.setIndex(metric.getTopic().toLowerCase());
        kibanaVisualization.setYlabel(metric.getName() + " (" + metric.getUnit()+ ")");


        if(metric.getGraph().equals(GraphType.LINE)) {
            //line
            kibanaVisualization.setMetricNameX("device_timestamp");
            kibanaVisualization.setMetricNameY("metric_value");
            kibanaVisualization.setXlabel("device_timestamp");
            kibanaVisualization.setAggregationType("avg");
        } else if(metric.getGraph().equals(GraphType.PIE)){
            //pie
            //TODO: plot several graphs differentiating by "device_id" attribute?
            kibanaVisualization.setMetricNameX("metric_value");
            kibanaVisualization.setAggregationType("count");

        }else if(metric.getGraph().equals(GraphType.COUNTER)){ // visualizationType==metric
            //type count, do nothing
        }

        kibanaDashboardDescription.addVisualizationsItem(kibanaVisualization);
        List<String> kibanaObjects = generator.translate(kibanaDashboardDescription);
        //String jsonKibanaObject = kibanaObjects.get(0);

        for (String jsonKibanaObject : kibanaObjects) {
            if(!kibanaConnectorService.postKibanaObject(jsonKibanaObject)){
                return null;
            }
        }
        if (kibanaProperties.isDashboardOwnerEnabled()) {
            if (!kibanaConnectorService.setOwner(metric.getDashboardId(), metric.getKibanaUser())) {
                return null;
            }
        }
        return getUrl(kibanaDashboardDescription.getDashboardId());
    }


    @Override
    public boolean deleteKibanaDashboard(Kpi kpi) {
        return kibanaConnectorService.removeKibanaObject("visualization_" + kpi.getTopic().toLowerCase(), "visualization") &&
                kibanaConnectorService.removeKibanaObject(kpi.getDashboardId(), "dashboard") &&
                kibanaConnectorService.removeKibanaObject("index_" + kpi.getTopic().toLowerCase(), "index-pattern");
    }

    @Override
    public boolean deleteKibanaDashboard(Metric metric) {
        return kibanaConnectorService.removeKibanaObject("visualization_" + metric.getTopic().toLowerCase(), "visualization") &&
                kibanaConnectorService.removeKibanaObject(metric.getDashboardId(), "dashboard") &&
                kibanaConnectorService.removeKibanaObject("index_" + metric.getTopic().toLowerCase(), "index-pattern");
    }
    

    private String getUrl(String id) {
        return kibanaProperties.getBaseUrl() + "/app/kibana#/dashboard/" + id + "?embed=true&_g=()";
    }

}
