package OCR;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class BusinessCardParserTest extends BusinessCardParser {

    static final String exampleDocumentOne =
            "ASYMMETRIK LTD\n" +
            "Mike Smith\n" +
            "Senior Software Engineer\n" +
            "(410)555-1234\n" +
            "msmith@asymmetrik.com\n";

    static final String nameOne = "Mike Smith";
    static final String phoneOne = "(410)555-1234";
    static final String phoneOneFormatted = "4105551234";
    static final String emailOne = "msmith@asymmetrik.com";

    static final String exampleDocumentTwo =
            "Foobar Technologies\n" +
            "Analytic Developer\n" +
            "Lisa Haung\n" +
            "1234 Sentry Road\n" +
            "Columbia, MD 12345\n" +
            "Phone: 410-555-1234\n" +
            "Fax: 410-555-4321\n" +
            "lisa.haung@foobartech.com\n";

    static final String nameTwo = "Lisa Haung";
    static final String phoneTwo = "Phone: 410-555-1234";
    static final String phoneTwoFormatted = "4105551234";
    static final String emailTwo = "lisa.haung@foobartech.com";

    static final String exampleDocumentThree =
            "Arthur Wilson\n" +
            "Software Engineer\n" +
            "Decision & Security Technologies\n" +
            "ABC Technologies\n" +
            "123 North 11th Street\n" +
            "Suite 229\n" +
            "Arlington, VA 22209\n" +
            "Tel: +1 (703) 555-1259\n" +
            "Fax: +1 (703) 555-1200\n" +
            "awilson@abctech.com\n";

    static final String nameThree = "Arthur Wilson";
    static final String phoneThree = "Tel: +1 (703) 555-1259";
    static final String phoneThreeFormatted = "17035551259";
    static final String emailThree = "awilson@abctech.com";

    static final List<String> documentList = new ArrayList<String>(
            Arrays.asList(
                    exampleDocumentOne,
                    exampleDocumentTwo,
                    exampleDocumentThree));

    static final List<String> nameList = new ArrayList<String> (
            Arrays.asList(nameOne, nameTwo, nameThree));

    static final List<String> phoneList = new ArrayList<String> (
            Arrays.asList(phoneOne, phoneTwo, phoneThree));

    static final List<String> phoneListFormatted = new ArrayList<String> (
            Arrays.asList(
                    phoneOneFormatted,
                    phoneTwoFormatted,
                    phoneThreeFormatted));

    static final List<String> emailList = new ArrayList<String> (
            Arrays.asList(emailOne, emailTwo, emailThree));

    @Test
    public void testBuildingContactInfoNull() {
        final BusinessCardParser businessCardParser = new BusinessCardParser();
        final ContactInfo contactInfo = businessCardParser.getContactInfo(null);
        assertNull(contactInfo.getName());
        assertNull(contactInfo.getPhoneNumber());
        assertNull(contactInfo.getEmailAddress());
    }

    @Test
    public void testBuildingContactInfoExamples() {
        for(int i = 0; i < documentList.size(); i++) {
            final BusinessCardParser businessCardParser = new BusinessCardParser();
            final ContactInfo contactInfo = businessCardParser.getContactInfo(documentList.get(i));

            assertEquals(nameList.get(i), contactInfo.getName());
            assertEquals(phoneListFormatted.get(i), contactInfo.getPhoneNumber());
            assertEquals(emailList.get(i), contactInfo.getEmailAddress());
        }
    }

    @Test
    public void testSplitLines() {
        final List<String> exampleListOne =
                BusinessCardParser.splitDocument(exampleDocumentOne);
        assertEquals(5, exampleListOne.size());

        final List<String> exampleListTwo =
                BusinessCardParser.splitDocument(exampleDocumentTwo);
        assertEquals(8, exampleListTwo.size());
 
        final List<String> exampleListThree = 
                BusinessCardParser.splitDocument(exampleDocumentThree);
        assertEquals(10, exampleListThree.size());
    }

    @Test
    public void testSimpleEmailEvaluator() {
        for(int i = 0; i < documentList.size(); i++) {
            final List<String> examples =
                    BusinessCardParser.splitDocument(documentList.get(i));
            final List<String> potentialEmails = 
                    BusinessCardParser.simpleEmailEvaluator(examples);

            assertEquals(1, potentialEmails.size());
            assertTrue(potentialEmails.contains(emailList.get(i)));
        }
    }

    @Test
    public void testNameFromEmail() {
        for (int i = 0; i < documentList.size(); i++) {
            final List<String> exampleList =
                    BusinessCardParser.splitDocument(documentList.get(i));

            // Remove the found email address from the lines.
            exampleList.remove(emailList.get(i));

            final List<String> potentialNames =
                    BusinessCardParser.nameClueInEmail(emailList.get(i), exampleList);

            assertEquals(1, potentialNames.size());
            assertTrue(potentialNames.contains(nameList.get(i)));
        }
    }

    @Test
    public void testSimplePhoneEvaluator() {
        for (int i = 0; i < documentList.size(); i++) {
            final List<String> exampleList =
                    BusinessCardParser.splitDocument(documentList.get(i));

            final List<String> potentialPhoneMatches = 
                    BusinessCardParser.simplePhoneEvaluator(exampleList);

            assertTrue(potentialPhoneMatches.contains(phoneList.get(i)));
        }
    }

    @Test
    public void testSecondaryPhoneEvaluator() {
        for (int i = 0; i < documentList.size(); i++) {
            final List<String> exampleList =
                    BusinessCardParser.splitDocument(documentList.get(i));

            final List<String> potentialPhoneMatches = 
                    BusinessCardParser.simplePhoneEvaluator(exampleList);

            if (potentialPhoneMatches.size() == 1) {
                assertEquals(1, potentialPhoneMatches.size());
                assertTrue(potentialPhoneMatches.contains(phoneList.get(i)));
                return;
            }

            final List<String> secondaryPhoneMatches = 
                    BusinessCardParser.secondaryPhoneEvaluator(potentialPhoneMatches);
            assertEquals(1, secondaryPhoneMatches.size());
            assertTrue(secondaryPhoneMatches.contains(phoneList.get(i)));
        }
    }
}
