package com.telcaria.dcs.nbi.wrapper;

import lombok.Data;

@Data
public class ValueWrapper {

    private String topic;
    private String expId;
    private String action;
    private ContextWrapper context;
}
