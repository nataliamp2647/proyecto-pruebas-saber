package co.edu.unicauca.liswiit2g9.security;

public class PasswordValidator {

    public boolean isValid(String password) {

        if (password == null || password.length() < 6) {
            return false;
        }

        boolean hasDigit = false;
        boolean hasUppercase = false;
        boolean hasSpecialCharacter = false;

        for (char character : password.toCharArray()) {

            if (Character.isDigit(character)) {
                hasDigit = true;
            }

            if (Character.isUpperCase(character)) {
                hasUppercase = true;
            }

            if (!Character.isLetterOrDigit(character)) {
                hasSpecialCharacter = true;
            }
        }

        return hasDigit && hasUppercase && hasSpecialCharacter;
    }
}