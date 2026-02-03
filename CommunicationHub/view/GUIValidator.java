package view;

import model.PhoneNumber;

import javax.swing.JLabel;

/**
 * Centralized input validation helpers for the GUI.
 * Use these static methods instead of duplicating validation logic
 * across panels.
 */
public class GUIValidator {

    /**
     * Validates phone number format and displays an error if invalid.
     *
     * @param phone      the phone string to validate (XXX-XXX-XXXX format)
     * @param errorLabel label to display error message
     * @return true if valid, false otherwise
     */
    public static boolean validatePhone(String phone, JLabel errorLabel) {
        if (phone == null || phone.trim().isEmpty()) {
            errorLabel.setText("Phone number required");
            return false;
        }
        if (!PhoneNumber.isValid(phone)) {
            errorLabel.setText("Invalid format (use XXX-XXX-XXXX)");
            return false;
        }
        return true;
    }

    /**
     * Validates that a text field is not empty.
     *
     * @param text       the text to validate
     * @param fieldName  logical field name for error message
     * @param errorLabel label to display error message
     * @return true if not empty, false otherwise
     */
    public static boolean validateNotEmpty(String text, String fieldName, JLabel errorLabel) {
        if (text == null || text.trim().isEmpty()) {
            errorLabel.setText(fieldName + " cannot be empty");
            return false;
        }
        return true;
    }

    /**
     * Validates a human name (letters, spaces, hyphens, apostrophes).
     */
    public static boolean validateName(String name, JLabel errorLabel) {
        if (name == null || name.trim().isEmpty()) {
            errorLabel.setText("Name cannot be empty");
            return false;
        }
        if (!name.matches("^[a-zA-Z\\s'-]+$")) {
            errorLabel.setText("Name can only contain letters, spaces, hyphens, and apostrophes");
            return false;
        }
        return true;
    }

    /**
     * Validates that a string parses to an integer.
     */
    public static boolean validateNumber(String value, String fieldName, JLabel errorLabel) {
        try {
            Integer.parseInt(value.trim());
            return true;
        } catch (NumberFormatException e) {
            errorLabel.setText(fieldName + " must be a number");
            return false;
        }
    }

    /**
     * Validates that a string parses to a positive integer.
     */
    public static boolean validatePositiveNumber(String value, String fieldName, JLabel errorLabel) {
        try {
            int num = Integer.parseInt(value.trim());
            if (num <= 0) {
                errorLabel.setText(fieldName + " must be positive");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            errorLabel.setText(fieldName + " must be a positive number");
            return false;
        }
    }

    /**
     * Clears an error label after successful validation.
     */
    public static void clearError(JLabel errorLabel) {
        if (errorLabel != null) {
            errorLabel.setText("");
        }
    }
}
