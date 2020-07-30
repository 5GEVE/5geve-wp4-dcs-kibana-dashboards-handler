package com.telcaria.dcs.nbi.wrapper;

import com.telcaria.dcs.storage.wrappers.KpiWrapper;
import com.telcaria.dcs.storage.wrappers.MetricWrapper;
import lombok.Data;

import java.util.Set;

@Data
public class ContextWrapper {

    // Only used if it is a metric.
    private String metricId;
    private String metricCollectionType;
    // Only used if it is a KPI.
    private String kpiId;
    // For both cases.
    private String graph;
    private String name;
    private String unit;
    private String interval;
}
