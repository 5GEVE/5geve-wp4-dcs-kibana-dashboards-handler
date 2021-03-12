package com.telcaria.dcs.elasticsearch.client;

import com.telcaria.dcs.nbi.wrapper.elasticsearch.ElasticsearchParametersWrapper;
import com.telcaria.dcs.elasticsearch.wrapper.ElasticsearchResponseWrapper;

public interface ElasticsearchClient {

  ElasticsearchResponseWrapper searchData(String index, ElasticsearchParametersWrapper parameters);
}