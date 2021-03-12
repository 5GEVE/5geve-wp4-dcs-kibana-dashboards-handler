package com.telcaria.dcs.elasticsearch.service;

import com.telcaria.dcs.nbi.wrapper.elasticsearch.ElasticsearchDcsResponseWrapper;
import com.telcaria.dcs.nbi.wrapper.elasticsearch.ElasticsearchParametersWrapper;
import com.telcaria.dcs.elasticsearch.wrapper.ElasticsearchResponseWrapper;

public interface ElasticsearchService {

    ElasticsearchDcsResponseWrapper searchData(String index, ElasticsearchParametersWrapper parameters);
}