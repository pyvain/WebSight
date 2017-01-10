package fr.pyvain.websight.websight;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

/**
 * <p>This very simple activity only displays general advices about data privacy </p>
 *
 * <p>
 * @author Vincent LEVALLOIS
 * </p>
 */
public class GeneralAdvice extends AppCompatActivity {

    private Context context;

    /**
     * This activity makes an asynchronous advice request to the server
     *
     * @author Vincent LEVALLOIS
     * @version 0.1
     */
    private class SendMailRequestTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String ...params) {
            String token = params[0];

            Resources resources = getResources();

            try {
                new Request(GeneralAdvice.this).sendModels(token);
            } catch (IOException e) {
                return resources.getString(R.string.ioExceptionMessage);
            } catch (InvalidCredentialsException e) {
                return resources.getString(R.string.invalidCredentialsExceptionMessage);
            } catch (ServerException e) {
                return resources.getString(R.string.serverExceptionMessage);
            } catch (NoConnexionException e) {
                return resources.getString(R.string.NoConnexionMessage);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Resources resources = getResources();

            if (result != null) {
                Toast.makeText(GeneralAdvice.this, result, Toast.LENGTH_SHORT).show();

            } else {
                Toast toast = Toast.makeText(GeneralAdvice.this, resources.getString(R.string.emailSent), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_advice);

        context = getApplicationContext();

        Resources res = getResources();
        WebView webView = (WebView) findViewById(R.id.webviewGeneral);

        webView.loadDataWithBaseURL(null, res.getString(R.string.general_advice), "text/html", "utf-8", null);
        webView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBg));

        Button sendMailButton = (Button) findViewById(R.id.sendMailButton);
        sendMailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String tokenID = FileIO.readFile(context, getString(R.string.tokenFile));
                    String token = tokenID.split("\t")[0];
                            new SendMailRequestTask().execute(token);
                } catch (IOException e) {
                    Toast.makeText(GeneralAdvice.this, getString(R.string.fileExceptionMessage), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
