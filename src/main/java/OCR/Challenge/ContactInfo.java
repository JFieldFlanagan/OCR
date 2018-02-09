package OCR;

public class ContactInfo {
    final private String name;
    final private String phoneNumber;
    final private String email;

    public ContactInfo() {
        name = null;
        phoneNumber = null;
        email = null;
    }
    
    public ContactInfo(final String name, final String phoneNumber, final String email) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }
    
    public String getName() {
        return name;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public String getEmailAddress() {
        return email;
    }
}
