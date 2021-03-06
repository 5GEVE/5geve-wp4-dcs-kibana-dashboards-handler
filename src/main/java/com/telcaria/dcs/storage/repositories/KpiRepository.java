package com.telcaria.dcs.storage.repositories;

import com.telcaria.dcs.storage.entities.Kpi;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface KpiRepository extends JpaRepository<Kpi, String> {

  List<Kpi> findAllByExperiment_ExpId(String expId);

  Optional<Kpi> findByTopicAndExperiment_ExpId(String topic, String expId);

}
