package com.github.eseoa.sensualgamebot.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "user_preference")
@Data
@NoArgsConstructor
public class BotUserPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private BotUser botUser;

    @ManyToOne
    @JoinColumn(name = "preference_id", referencedColumnName = "id")
    private Preference preference;

    @Column(name = "is_ready", nullable = false)
    private boolean isReady;

    public BotUserPreference(BotUser botUser, Preference preference, boolean isReady) {
        this.botUser = botUser;
        this.preference = preference;
        this.isReady = isReady;
    }
}
