package mobile.sms.chat;

import androidx.lifecycle.ViewModel;

import mobile.sms.model.Contact;
import mobile.sms.model.Conversation;

public class ConversationViewModel extends ViewModel {

    private Conversation conversation;

    public Conversation getConversation() {
        return conversation;
    }

    public boolean addMessage(String text) {
        return conversation.addMessage(text);
    }

    public void setContact(String number) {
        conversation = new Conversation(new Contact(number));
    }

}
