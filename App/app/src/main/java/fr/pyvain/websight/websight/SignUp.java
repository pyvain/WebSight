package fr.pyvain.websight.websight;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

/**
 * <p>This activity allows the user to create an account.</p>
 *
 * <p>
 * @author Etienne THIERY  etienne.thiery@wanadoo.fr
 * @author Vincent LEVALLOIS
 * </p>
 */
public class SignUp extends AppCompatActivity {

    /**
     * This activity makes an asynchronous sign up request to the server
     *
     * @author Etienne THIERY
     * @version 0.1
     */
    private class SignUpRequestTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String ...params) {
            String email = params[0];
            String password = params[1];
            Resources resources = getResources();
            try {
                Request req = new Request(SignUp.this);
                req.signUp(email, password);
            } catch (IOException e) {
                return resources.getString(R.string.ioExceptionMessage);
            } catch (EmailAlreadyUsedException e) {
                return resources.getString(R.string.emailAlreadyUsedExceptionMessage);
            } catch (InvalidCredentialsException e) {
                // This should definitely not happened, as credentials must
                // be checked before making a request
                return resources.getString(R.string.invalidCredentialsExceptionMessage);
            } catch (ServerException e) {
                return resources.getString(R.string.serverExceptionMessage);
            } catch (NoConnexionException e) {
                return resources.getString(R.string.NoConnexionMessage);
            }
            return resources.getString(R.string.successfulSignUpMessage);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(SignUp.this, result, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Checks if the email input by the user is valid
     * @return True if the input email is valid, False else
     */
    private boolean isValidEmail() {
        EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
        String email = emailEditText.getText().toString();
        return !TextUtils.isEmpty(email) &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Checks if the password input by the user is valid, ie :
     * - is at least 8 character long
     * - contains at least one lower-case character
     * - contains at least one upper-case character
     * - contains at least one digit
     * - contains at least one symbol
     * @return True if the input password is valid, False else
     */
    private boolean isValidPassword() {
        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        String password = passwordEditText.getText().toString();
        return  password.length() >= 8 &&
                password.matches(".*[0-9]+.*") &&
                password.matches(".*[a-z]+.*") &&
                password.matches(".*[A-Z]+.*") &&
                password.matches(".*[^[a-zA-Z0-9]]+.*");
    }

    /**
     * Checks if the rePassword input by the user is valid, ie
     * if it matches with the password input by the user
     * @return True if the input rePassword is valid, False else
     */
    private boolean isValidRePassword() {
        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        String password = passwordEditText.getText().toString();
        EditText rePasswordEditText = (EditText) findViewById(R.id.rePasswordEditText);
        String rePassword = rePasswordEditText.getText().toString();
        return  rePassword.equals(password);
    }

    /**
     * Checks if user inputs are valid, and if so,
     * sends a new SignUp request to the server
     * with these values
     */
    private void signUp() {
        if (isValidEmail() && isValidPassword() && isValidRePassword()) {
            EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
            String email = emailEditText.getText().toString();
            EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
            String password = passwordEditText.getText().toString();
            new SignUpRequestTask().execute(email, password);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // On input, check if email address in valid


        // Allow to sign up by an IME_ACTION_SEND action on the password
        // confirmation edit text (i.e by ending password by Enter)
        EditText rePasswordEditText = (EditText) findViewById(R.id.rePasswordEditText);
        rePasswordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    SignUp.this.signUp();
                    handled = true;
                }
                return handled;
            }
        });

        Button signUpButton = (Button) findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUp.this.signUp();
            }
        });

        TextView signInLink = (TextView) findViewById(R.id.signInLink);
        signInLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUp.this.finish();
            }
        });
    }
}
