package mobile.sms.model;

import java.util.ArrayList;
import java.util.List;

public class Conversation {

    private Contact contact;
    private List<Message> messages;

    public Conversation(Contact contact) {
        this.contact = contact;
        messages = new ArrayList<>();
    }

    public Conversation(Contact contact, List<Message> messages) {
        this.contact = contact;
        this.messages = messages;
    }

    public boolean addMessage(String text) {
        return messages.add(new Message(text));
    }

    public void setContact(String number) {
        contact = new Contact(number);
    }

    public Contact getContact() {
        return contact;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
