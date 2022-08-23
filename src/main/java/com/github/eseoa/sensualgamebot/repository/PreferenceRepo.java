package com.github.eseoa.sensualgamebot.repository;

import com.github.eseoa.sensualgamebot.model.Preference;
import com.github.eseoa.sensualgamebot.model.enums.PreferenceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PreferenceRepo extends JpaRepository<Preference, Long> {
    List<Preference> findByModeratedAndPreferenceType (boolean moderated, PreferenceType preferenceType);
    List<Preference> findByPreferenceType (PreferenceType preferenceType);
    Optional<Preference> findByPreferenceTypeAndName (PreferenceType preferenceType, String name);
}
