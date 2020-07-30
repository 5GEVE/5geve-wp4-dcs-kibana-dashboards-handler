package com.telcaria.dcs.storage.wrappers;

import java.util.Set;
import lombok.Data;

@Data
public class ExperimentWrapper {

    private String expId;
    private String useCase;
    private String siteFacility;

    private Set<KpiWrapper> kpis;
    private Set<MetricWrapper> metrics;

}
