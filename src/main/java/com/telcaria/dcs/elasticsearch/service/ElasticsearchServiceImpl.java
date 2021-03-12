/*
 * Copyright 2021-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.telcaria.dcs.elasticsearch.service;

import com.telcaria.dcs.elasticsearch.client.ElasticsearchClient;
import com.telcaria.dcs.elasticsearch.wrapper.HitWrapper;
import com.telcaria.dcs.elasticsearch.wrapper.SourceWrapper;
import com.telcaria.dcs.nbi.wrapper.elasticsearch.ElasticsearchDataWrapper;
import com.telcaria.dcs.nbi.wrapper.elasticsearch.ElasticsearchDcsResponseWrapper;
import com.telcaria.dcs.nbi.wrapper.elasticsearch.ElasticsearchParametersWrapper;
import com.telcaria.dcs.elasticsearch.wrapper.ElasticsearchResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional
public class ElasticsearchServiceImpl implements ElasticsearchService {

  private ElasticsearchClient elasticsearchClient;

  @Autowired
  public ElasticsearchServiceImpl(ElasticsearchClient elasticsearchClient) {
    this.elasticsearchClient = elasticsearchClient;
  }

  @Override
  public ElasticsearchDcsResponseWrapper searchData(String index, ElasticsearchParametersWrapper parameters) {

    ElasticsearchResponseWrapper response = elasticsearchClient.searchData(index, parameters);

    return response.equals(new ElasticsearchResponseWrapper()) ? new ElasticsearchDcsResponseWrapper() : transformResponse(index, response);
  }

  private ElasticsearchDcsResponseWrapper transformResponse(String index, ElasticsearchResponseWrapper responseWrapper) {

    ElasticsearchDcsResponseWrapper response = new ElasticsearchDcsResponseWrapper();
    List<ElasticsearchDataWrapper> listData = new ArrayList<>();

    for (HitWrapper hitWrapper : responseWrapper.getHits().getHits()) {
      SourceWrapper sourceWrapper = hitWrapper.getSource();
      ElasticsearchDataWrapper data = new ElasticsearchDataWrapper();

      data.setUnit(sourceWrapper.getUnit());
      data.setContext(sourceWrapper.getContext());
      data.setDevice_id(sourceWrapper.getDevice_id());
      data.setTimestamp(sourceWrapper.getTimestamp());
      if (index.contains(".kpi.")) {
        data.setValue(sourceWrapper.getKpi_value());
      } else {
        data.setValue(sourceWrapper.getMetric_value());
      }
      data.setDevice_timestamp(sourceWrapper.getDevice_timestamp());

      listData.add(data);
    }

    response.setData(listData);

    return response;
  }
}
