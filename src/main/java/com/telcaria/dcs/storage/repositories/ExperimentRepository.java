package com.telcaria.dcs.storage.repositories;

import com.telcaria.dcs.storage.entities.Experiment;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ExperimentRepository extends JpaRepository<Experiment, String> {

  Experiment findByMetrics_Topic(String topic);

  Experiment findByKpis_Topic(String topic);

}
