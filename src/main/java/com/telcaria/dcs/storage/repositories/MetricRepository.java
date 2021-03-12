package com.telcaria.dcs.storage.repositories;


import com.telcaria.dcs.storage.entities.Metric;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface MetricRepository extends JpaRepository<Metric, String> {

  List<Metric> findAllByExperiment_ExpId(String expId);
  Optional<Metric> findByTopicAndExperiment_ExpId(String topic, String expId);
}
