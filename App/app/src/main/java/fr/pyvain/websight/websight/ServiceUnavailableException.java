package fr.pyvain.websight.websight;

/**
 * <p>Exception raised :<ul>
 * <li>on sign in when the email & password input are not valid</li>
 * <li>on token check when the token is not valid</li></ul></p>
 *
 * <p>
 * @author Etienne THIERY
 * </p>
 */
class ServiceUnavailableException extends Exception {
    public ServiceUnavailableException() {
        super();
    }
}