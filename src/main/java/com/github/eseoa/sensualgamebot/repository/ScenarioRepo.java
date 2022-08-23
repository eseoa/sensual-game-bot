package com.github.eseoa.sensualgamebot.repository;

import com.github.eseoa.sensualgamebot.model.Scenario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScenarioRepo extends JpaRepository<Scenario, Long> {
}
