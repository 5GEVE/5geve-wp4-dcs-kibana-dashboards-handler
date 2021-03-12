package com.telcaria.dcs.kibana.service;

import com.telcaria.dcs.storage.entities.Kpi;
import com.telcaria.dcs.storage.entities.Metric;

public interface KibanaService {

/*
    String createKibanaVisualization(Kpi kpi);

    String createKibanaVisualization(Metric metric);
*/

    String createKibanaDashboard(Kpi kpi);

    String createKibanaDashboard(Metric metric);

    boolean deleteKibanaDashboard(Kpi kpi);

    boolean deleteKibanaDashboard(Metric metric);
}
