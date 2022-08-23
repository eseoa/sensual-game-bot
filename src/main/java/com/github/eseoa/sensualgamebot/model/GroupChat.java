package com.github.eseoa.sensualgamebot.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "group_chats")
@Data
@NoArgsConstructor
public class GroupChat {
    @Id
    private long id;

    @OneToMany(mappedBy = "groupChat")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<BotUserGroupChat> botUserGroupChats;

    @ManyToMany(mappedBy = "groupChats")
    private List<BotUser> botUsers;

    public GroupChat(long id) {
        this.id = id;
    }
}
