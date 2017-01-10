package fr.pyvain.websight.websight;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * <p>This activity allows the user to login into his account.
 * It is also the starting activity.</p>
 *
 * <p>
 * @author Etienne THIERY  etienne.thiery@wanadoo.fr
 * @author Vincent LEVALLOIS
 * </p>
 */
public class SignIn extends AppCompatActivity {

    /**
     * This activity makes an asynchronous sign in request to the server
     *
     * @author Etienne THIERY
     * @version 0.1
     */
    private class SignInRequestTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String ...params) {
            String email = params[0];
            String password = params[1];
            String token;
            Resources resources = getResources();
            try {
                token = new Request(SignIn.this).getAccessToken(email, password);
            } catch (IOException e) {
                return resources.getString(R.string.ioExceptionMessage);
            } catch (InvalidCredentialsException e) {
                return resources.getString(R.string.invalidCredentialsExceptionMessage);
            } catch (ServerException e) {
                return resources.getString(R.string.serverExceptionMessage);
            } catch (NoConnexionException e) {
                return resources.getString(R.string.NoConnexionMessage);
            }

            try {
                writeInFile(getString(R.string.tokenFile), token + "\t" + email.hashCode());
                System.out.println("Token : " + token);
            } catch (FileNotFoundException e) {
                return resources.getString(R.string.fileExceptionMessage);
            } catch (IOException e) {
                return resources.getString(R.string.fileExceptionMessage);
            }
            return null;
        }

        // onPostExecute starts the GraphDisplay activity if the token has been provided
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Toast.makeText(SignIn.this, result, Toast.LENGTH_SHORT).show();
            } else {
                Intent goToGraphDisplay = new Intent(SignIn.this, GraphDisplay.class);
                startActivity(goToGraphDisplay);
            }
        }
    }


    /**
     * Writes a String into a specified file
     *
     * @param file name of the file where the String will be written
     * @param data String that will be written
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void writeInFile (String file, String data) throws IOException{
        FileOutputStream output;

        output = openFileOutput(file, MODE_PRIVATE);
        output.write(data.getBytes());
        System.out.println(data);
        output.close();
    }

    /**
     * Sends a new SignIn request to the server
     * with the values input by the user
     */
    private void signIn() {
        EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
        String email = emailEditText.getText().toString();
        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        String password = passwordEditText.getText().toString();
        new SignInRequestTask(/*this*/).execute(email, password);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Allows to sign in by an IME_ACTION_SEND action on the password
        // edit text (i.e by ending password by Enter)
        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    SignIn.this.signIn();
                    handled = true;
                }
                return handled;
            }
        });

        // Click on signInButton -> signin()
        Button signInButton = (Button) findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignIn.this.signIn();
            }
        });

        // Click on signUpLink -> go to activity SignUp
        TextView signUpLink = (TextView) findViewById(R.id.signUpLink);
        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSignUp = new Intent(SignIn.this, SignUp.class);
//                Intent goToSignUp = new Intent(SignIn.this, GraphDisplay2.class);
                startActivity(goToSignUp);
            }
        });

        // Click on forgottenPassword -> open browser
        TextView forgottenPassword = (TextView) findViewById(R.id.lostPasswordLink);
        forgottenPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Resources res = getResources();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(res.getString(R.string.serverUrl) +
                                  res.getString(R.string.lostPasswordPath)));
                startActivity(browserIntent);
            }
        });

        // Click on qwant -> open qwant in browser
        View qwant = findViewById(R.id.qwant);
        qwant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Resources res = getResources();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(res.getString(R.string.qwant_adress)));
                startActivity(browserIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        File file = getBaseContext().getFileStreamPath(getString(R.string.tokenFile));
        if(file.exists()) {
            Intent goToGraphDisplay = new Intent(SignIn.this, GraphDisplay.class);
            startActivity(goToGraphDisplay);
        }
    }
}
