package fr.pyvain.websight.websight;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;


/**
 * <p>This activity is where parameters such as data to fill, log out,
 * delete account and general advices can be found</p>
 *
 * <p>
 * @author Vincent LEVALLOIS
 * </p>
 */

public class Parameters extends AppCompatActivity implements ResponseReceiver.Receiver {

    /**
     * Object used to get the signal from the data form when data have been validated
     */
    private ResponseReceiver mReceiver;

    private String userID;

    /**
     * Receive the result from DataForm activity. finishes this instance when data are
     * validated so we come back directly to the GraphDisplay
     *
     * @param resultCode not used here
     * @param resultData not used here
     */
    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        Parameters.this.finish();
    }

    /**
     * This activity makes an asynchronous request to the server to delete one's account
     *
     * @author Vincent LEVALLOIS
     * @version 0.1
     */
    private class DeleteAccountTask extends AsyncTask<String, Void, String> {

        /**
         * Constructor
         */
        public DeleteAccountTask() {
            super();
        }

        @Override
        protected String doInBackground(String ...params) {
            String token = params[0];
            String password = params[1];

            Resources resources = getResources();
            try {
                new Request(Parameters.this).deleteAccount(token, password);
            } catch (IOException e) {
                return resources.getString(R.string.ioExceptionMessage);
            } catch (InvalidCredentialsException e) {
                return resources.getString(R.string.invalidCredentialsExceptionMessage);
            } catch (ServerException e) {
                return resources.getString(R.string.serverExceptionMessage);
            } catch (NoConnexionException e) {
                return resources.getString(R.string.NoConnexionMessage);
            }

            deleteFiles();
            logOut();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Toast.makeText(Parameters.this, result, Toast.LENGTH_SHORT).show();
            } else {
                Intent goToGraphDisplay = new Intent(Parameters.this, GraphDisplay.class);
                startActivity(goToGraphDisplay);
            }
        }
    }

    /**
     * This method deletes the token file and terminate this activity
     */
    private void logOut () {
        File file = getBaseContext().getFileStreamPath(getString(R.string.tokenFile));
        if(file.exists()) {
            if (!file.delete()) {
                Log.e("Websight", "Cannot delete file: " + file);
            }

            Parameters.this.finish();
        }
    }

    /**
     * This method removes every file created by the app from the user's device
     */
    private void deleteFiles () {
        File file = getBaseContext().getFileStreamPath(userID + getString(R.string.dataFile));
        if(file.exists()) {
            if (!file.delete()) {
                Log.e("Websight", "Cannot delete file: " + file);
            }
        }
        file = getBaseContext().getFileStreamPath(userID + getString(R.string.graphFile));
        if(file.exists()) {
            if (!file.delete()) {
                Log.e("Websight", "Cannot delete file: " + file);
            }
        }
    }

    /**
     * Retrieve the authentication token and password to send a delete account request to the server
     */
    private void deleteAccount () {
        String token = null;
        try {
            String tokenID = FileIO.readFile(this, getResources().getString(R.string.tokenFile));
            token = tokenID.split("\t")[0];
        } catch (IOException e) {
            e.printStackTrace();
        }

        EditText editText = (EditText) findViewById(R.id.passwordEditText);
        String password = editText.getText().toString();
        new DeleteAccountTask().execute(token, password);
    }

    /**
     * This method saves the state of the activity in order to rebuild if changes occur
     *
     * @param savedState bundle where data to restore will be saved
     */
    protected void onSaveInstanceState(Bundle savedState) {
        savedState.putParcelable("receiver", mReceiver);
        super.onSaveInstanceState(savedState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters);

        if (savedInstanceState != null) {
            mReceiver = savedInstanceState.getParcelable("receiver");
        } else {
            mReceiver = new ResponseReceiver(new Handler());
        }
        mReceiver.setReceiver(this);

        try {
            String tokenID = FileIO.readFile(this, getString(R.string.tokenFile));
            userID = tokenID.split("\t")[1];
        } catch (IOException e) {
            Toast.makeText(Parameters.this, getString(R.string.fileExceptionMessage), Toast.LENGTH_SHORT).show();
        }

        // Click on fillDataButton -> go to DataForm activity
        Button fillDataButton = (Button) findViewById(R.id.fillDataButton);
        fillDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToFillData = new Intent(Parameters.this, DataForm.class);
                goToFillData.putExtra("receiver", mReceiver);
                startActivity(goToFillData);
            }
        });

        // Click on generalAdviceButton -> go to GeneralAdvice activity
        Button generalAdviceButton = (Button) findViewById(R.id.generalAdviceButton);
        generalAdviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToFillData = new Intent(Parameters.this, GeneralAdvice.class);
                startActivity(goToFillData);
            }
        });

        // Click on logOutButton -> logOut
        Button logOutButton = (Button) findViewById(R.id.logOutButton);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        // Click on deleteAccountButton -> Displays small text, password confirmation and validation button
        Button deleteAccountButton = (Button) findViewById(R.id.deleteAccountButton);
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout deleteAccountDetails = (LinearLayout) findViewById(R.id.deleteAccountDetails);
                if (deleteAccountDetails.getVisibility() == View.GONE)
                    deleteAccountDetails.setVisibility(View.VISIBLE);
                else
                    deleteAccountDetails.setVisibility(View.GONE);
            }
        });

        // Click on valdateDeletionButton -> deleteAccount
        Button validateDeletionButton = (Button) findViewById(R.id.validateDeletionButton);
        validateDeletionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });
    }
}
