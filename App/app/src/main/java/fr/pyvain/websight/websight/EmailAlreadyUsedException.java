package fr.pyvain.websight.websight;

/**
 * <p>Exception raised on sign up when the email input by the user
 * is already used</p>
 *
 * <p>
 * @author Etienne THIERY, etienne.thiery@wanadoo.fr
 * </p>
 */
class EmailAlreadyUsedException extends Exception {
    public EmailAlreadyUsedException() {
        super();
    }
}
