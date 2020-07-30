package com.telcaria.dcs.nbi.wrapper;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class DcsRequestWrapper {

    @Valid
    private List<RecordWrapper> records;
}
