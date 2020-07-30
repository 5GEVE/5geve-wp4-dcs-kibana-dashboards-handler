package com.telcaria.dcs.kibana.service;

import com.telcaria.dcs.storage.entities.Kpi;
import com.telcaria.dcs.storage.entities.Metric;
import com.telcaria.dcs.storage.enums.GraphType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class KibanaServiceImplTest {

    @Autowired
    KibanaService kibanaService;

/*    @Test
    void createKibanaVisualizationMetric() {

        Metric metric1 = new Metric();
        metric1.setTopic("uc.4.france_nice.infrastructure_metric.expb_metricid");
        metric1.setName("uc.4.france_nice.infrastructure_metric.expb_metricid");
        metric1.setGraph(GraphType.LINE);

        String url1 = kibanaService.createKibanaVisualization(metric1);
        log.info(url1);

*//*        Metric metric2 = new Metric();
        metric2.setTopic("uc.4.france_nice.infrastructure_metric.expb_metricid_pie");
        metric2.setName("uc.4.france_nice.infrastructure_metric.expb_metricid_pie");
        metric2.setGraph(GraphType.PIE);

        String url2 = kibanaService.createKibanaVisualization(metric2);
        log.info(url2);*//*


    }*/

    @Test
    void createKibanaDashboardMetric() {

        Metric metric1 = new Metric();
        metric1.setDashboardId("74c757a4-785f-482e-b920-cba0edaa7584");
        metric1.setKibanaUser("user1");
        metric1.setMetricId("expb_metricid");
        metric1.setUnit("milliseconds");
        metric1.setTopic("uc.4.france_nice.infrastructure_metric.expb_metricid");
        metric1.setName("name");
        metric1.setGraph(GraphType.LINE);

        String url1 = kibanaService.createKibanaDashboard(metric1);
        log.info(url1);
    }

/*
    @Test
    void createKibanaDashboardKpi() {

        Kpi metric1 = new Kpi();
        metric1.setDashboardId("74c757a4-785f-482e-b920-cba0edaa7584");
        metric1.setKibanaUser("user1");
        metric1.setKpiId("expb_metricid");
        metric1.setUnit("milliseconds");
        metric1.setTopic("uc.4.france_nice.infrastructure_metric.expb_metricid");
        metric1.setName("name");
        metric1.setGraph(GraphType.PIE);

        String url1 = kibanaService.createKibanaDashboard(metric1);
        log.info(url1);
    }
*/

/*
    @Test
    void createKibanaVisualizationKpi() {
    }

    @Test
    void deleteKibanaVisualizationKpi() {
    }
*/

    @Test
    void deleteKibanaVisualizationMetric() {

        Metric metric1 = new Metric();
        metric1.setDashboardId("74c757a4-785f-482e-b920-cba0edaa7584");
        metric1.setKibanaUser("user1");
        metric1.setMetricId("expb_metricid");
        metric1.setUnit("milliseconds");
        metric1.setTopic("uc.4.france_nice.infrastructure_metric.expb_metricid");
        metric1.setName("name");
        metric1.setGraph(GraphType.PIE);

        kibanaService.deleteKibanaDashboard(metric1);
    }
}