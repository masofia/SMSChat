package mobile.sms.chat;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import mobile.sms.model.Conversation;

public class MainViewModel extends ViewModel {

    List<Conversation> allConversations = new ArrayList<>();

    public List<Conversation> getAllConversations() {
        return allConversations;
    }

    public boolean addNewConversation(Conversation convo) {
        return allConversations.add(convo);
    }
}
