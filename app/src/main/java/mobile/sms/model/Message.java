package mobile.sms.model;

import java.util.Date;

public class Message {

    private Contact author;
    private Date sentAt;
    private String text;
    private String encryptedText;

    public Message(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
