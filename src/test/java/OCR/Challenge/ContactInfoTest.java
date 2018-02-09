package OCR;

import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ContactInfoTest {

    @Test
    public void testBuildingContactInfo() {
        ContactInfo contactInfo = new ContactInfo(null, null, null);
        assertNull(contactInfo.getName());
        assertNull(contactInfo.getPhoneNumber());
        assertNull(contactInfo.getEmailAddress());
    }
}
