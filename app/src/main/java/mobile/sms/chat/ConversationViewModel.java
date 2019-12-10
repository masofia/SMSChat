package mobile.sms.chat;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import mobile.sms.model.Contact;
import mobile.sms.model.Conversation;
import mobile.sms.model.Message;

public class ConversationViewModel extends ViewModel {
    private Contact contact;
    private Conversation conversation;
    private String privateKey;
    private String iv;

    public Conversation getConversation() {
        Log.i("Conversation View Model", "get conversation ***********");
        return conversation;
    }

    public boolean addMessage(String text) {
        Log.i("Conversation View Model", "add message: " + text  + "; " + this.toString() + " ***********");
        return conversation.addMessage("sent: " + text);
    }

    public String addReceivedMessage(String text, String number) {
        Message message = new Message(text);
        message.decryptText(privateKey, iv);
        String decoratedMsg = message.decoratedReceivedMessage(number);
        conversation.addMessage(decoratedMsg);

        return decoratedMsg;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
        conversation = new Conversation(contact);
    }

    public void setPrivateKey(String key) {
        this.privateKey = key;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }
}
