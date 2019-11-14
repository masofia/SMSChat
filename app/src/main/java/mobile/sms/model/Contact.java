package mobile.sms.model;

public class Contact {

    private String name; // currently not in use
    private String number;

    public Contact(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }
}
