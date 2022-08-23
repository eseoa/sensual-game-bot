package com.github.eseoa.sensualgamebot.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "user_group_chat")
@Data
@NoArgsConstructor
public class BotUserGroupChat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long  id;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private BotUser botUser;
    @ManyToOne
    @JoinColumn(name = "group_chat_id", referencedColumnName = "id")
    private GroupChat groupChat;

    public BotUserGroupChat(BotUser botUser, GroupChat groupChat) {
        this.botUser = botUser;
        this.groupChat = groupChat;
    }

}
