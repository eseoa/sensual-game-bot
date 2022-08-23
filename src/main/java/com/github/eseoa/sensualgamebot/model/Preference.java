package com.github.eseoa.sensualgamebot.model;

import com.github.eseoa.sensualgamebot.model.enums.PreferenceType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "preferences")
@Data
@NoArgsConstructor
public class Preference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column (nullable = false)
    private PreferenceType preferenceType;

    @Column (nullable = false)
    private boolean moderated;

    @Column (columnDefinition = "text")
    private String ozonUrl;

    @Column (columnDefinition = "text")
    private String pinkRabbitUrl;

    @Column (columnDefinition = "text")
    private String intimShopUrl;

    private String fileId;

    @OneToMany(mappedBy = "preference")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<BotUserPreference> botUserPreferences;

    @ManyToMany (mappedBy = "preferences")
    private List<BotUser> botUsers;


    public Preference(String name, String description, PreferenceType preferenceType, boolean moderated) {
        this.name = name;
        this.description = description;
        this.preferenceType = preferenceType;
        this.moderated = moderated;
    }

    public Preference(String name, String description, PreferenceType preferenceType, boolean moderated, String fileId) {
        this.name = name;
        this.description = description;
        this.preferenceType = preferenceType;
        this.moderated = moderated;
        this.fileId = fileId;
    }
}
