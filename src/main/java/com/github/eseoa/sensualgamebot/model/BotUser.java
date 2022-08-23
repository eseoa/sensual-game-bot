package com.github.eseoa.sensualgamebot.model;

import com.github.eseoa.sensualgamebot.command.Command;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class BotUser {
    @Id
    private long id;
    @Enumerated(EnumType.STRING)
    @Column (nullable = false)
    private Command command;
    @Column (nullable = false)
    private boolean addCommandCheck;

    @OneToMany(mappedBy = "botUser")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<BotUserGroupChat> botUserGroupChats;

    @ManyToMany
    @JoinTable(name = "user_group_chat",
            joinColumns = @JoinColumn (name = "user_id"),
            inverseJoinColumns = @JoinColumn (name = "group_chat_id"))
    private List<GroupChat> groupChats;

    @ManyToMany
    @JoinTable(name = "user_preference",
            joinColumns = @JoinColumn (name = "user_id"),
            inverseJoinColumns = @JoinColumn (name = "preference_id"))
    private List<Preference> preferences;

    @OneToMany(mappedBy = "botUser")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<BotUserPreference> botUserPreferences;

    public BotUser(long id, Command command, boolean addCommandCheck) {
        this.id = id;
        this.command = command;
        this.addCommandCheck = addCommandCheck;
    }
}
