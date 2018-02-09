package OCR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BusinessCardParser {

    /**
     * This is the main parser method. It will take in OCR text and return an
     * object with the parsed results.
     * @param document contains the OCR text with either windows or linux
     * end lines.
     * @return A ContactInfo object containing the name, phoneNumber, and
     * email address from the input document. If any of the values cannot be
     * parsed they are returned as null.
     */
    public ContactInfo getContactInfo(String document) {

        // If the document is null return an empty ContactInfo object.
        if ((document == null) || (document.matches("\\s"))) {
            return new ContactInfo();
        }

        // Initialize all of the return values to null.
        String name = null;
        String phoneNumber = null;
        String emailAddress = null;

        // Split the document into lines
        final List<String> lines = splitDocument(document);

        // Evaluate each line for email address.
        final List<String> potentialEmails = simpleEmailEvaluator(lines);
        if(potentialEmails.size() == 1) {
            emailAddress = potentialEmails.get(0).trim();
        }

        // If the simple parser did not return any email addresses or returned
        // more than one address implement a secondary email parser.
        
        // Once the email address has been identified remove it from the
        // remaining lines.
        lines.remove(emailAddress);

        // This method will use the email address to try to match a name.
        final List<String> potentialNames = nameClueInEmail(emailAddress, lines);
        if(potentialNames.size() == 1) {
            name = potentialNames.get(0);
        }

        // If the nameClueInEmail did not return any names or returned
        // more than one name implement a secondary name parser.
        
        // One the name has been identified remove it from the remaining lines.
        lines.remove(name);

        // The simple phone parser 
        final List<String> potentialPhone = simplePhoneEvaluator(lines);
        if(potentialPhone.size() == 1) {
            phoneNumber = potentialPhone.get(0).replaceAll("\\D", "");
        }

        // The simple phone parser 
        if(potentialPhone.size() > 1) {
            final List<String> secondaryPhone = secondaryPhoneEvaluator(potentialPhone);
            if(secondaryPhone.size() == 1) {
                phoneNumber = secondaryPhone.get(0).replaceAll("\\D", "");
            }
        }

        return new ContactInfo(name, phoneNumber, emailAddress);
    }

    protected static List<String> splitDocument(final String document) {
        return new ArrayList<String>(Arrays.asList(document.split("\\r?\\n")));
    }

    protected static List<String> simpleEmailEvaluator(final List<String> lines) {

        final List<String> potentialEmails = new ArrayList<String>();

        for(String line : lines) {
            if (line.matches(".*@.*\\..*")) {
                potentialEmails.add(line);
            }
        }
        return potentialEmails;
    }

    protected static List<String> nameClueInEmail(final String email, final List<String> lines) {
        final String namePortion = Arrays.asList(email.split("@")).get(0);

        // If a person is using their name in the email address it is usually
        // some combination of their name and initials.
        final StringBuilder namePatternBuilder = new StringBuilder(".*");
        for(int i = 0; i < namePortion.length(); i++) {
            final Character character = namePortion.charAt(i);
            if(Character.isLetter(character)) {
                namePatternBuilder.append("[");
                namePatternBuilder.append(Character.toLowerCase(character));
                namePatternBuilder.append(Character.toUpperCase(character));
                namePatternBuilder.append("].*");
            }
        }

        final List<String> potentialNameMatches = new ArrayList<String>();
        for(String line : lines) {
            if(line.matches(namePatternBuilder.toString())) {
                potentialNameMatches.add(line);
            }
        }
        
        return potentialNameMatches;
    }

    protected static List<String> simplePhoneEvaluator(final List<String> lines) {
        final List<String> potentialPhoneMatches = new ArrayList<String>();
        for(String line : lines) {
            if (line.replaceAll("\\D", "").length() >= 10 ) {
                potentialPhoneMatches.add(line);
            }
        }
        return potentialPhoneMatches;
    }

    protected static List<String> secondaryPhoneEvaluator(final List<String> lines) {
        final List<String> potentialPhoneMatches = new ArrayList<String>();
        for(String line : lines) {
            // This catches Phone or Office or Cell
            if(line.contains("o") || line.contains("O")) {
                potentialPhoneMatches.add(line);
            }

            // This catches M or Cell or C 
            if (potentialPhoneMatches.isEmpty() &&
                    (line.contains("m") || line.contains("M") ||
                     line.contains("c") || line.contains("C"))) {
                potentialPhoneMatches.add(line);
            }
            
         // This catches Tel or Tel
            if (potentialPhoneMatches.isEmpty() &&
                    (line.contains("t") || line.contains("T"))) {
                potentialPhoneMatches.add(line);
            }
        }
        return potentialPhoneMatches;
    }
}