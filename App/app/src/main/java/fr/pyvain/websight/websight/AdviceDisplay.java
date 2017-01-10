package fr.pyvain.websight.websight;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

/**
 * <p>Activity where the graph is displayed</p>
 *
 * <p>
 * @author Vincent LEVALLOIS
 * </p>
 */
public class AdviceDisplay extends AppCompatActivity {

    /**
     * This activity makes an asynchronous advice request to the server
     *
     * @author Vincent LEVALLOIS
     * @version 0.1
     */
    private class GetAdviceRequestTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String ...params) {
            String token = params[0];
            String url = params[1];
            String keywords[] = new String[params.length - 2];

            System.out.println(params.length - 2);

            System.arraycopy(params, 2, keywords, 0, params.length - 2);

            String advice;
            Resources resources = getResources();

            try {
                advice = new Request(AdviceDisplay.this).getAdvice(token, url, keywords);
            } catch (IOException e) {
                return resources.getString(R.string.ioExceptionMessage);
            } catch (InvalidCredentialsException e) {
                return resources.getString(R.string.invalidCredentialsExceptionMessage);
            } catch (ServerException e) {
                return resources.getString(R.string.serverExceptionMessage);
            } catch (NoConnexionException e) {
                return resources.getString(R.string.NoConnexionMessage);
            }

            return advice;
        }

        @Override
        protected void onPostExecute(String result) {
            Resources resources = getResources();

            if (result.equals(resources.getString(R.string.ioExceptionMessage))
                    || result.equals(resources.getString(R.string.invalidCredentialsExceptionMessage))
                    || result.equals(resources.getString(R.string.serverExceptionMessage))) {
                Toast.makeText(AdviceDisplay.this, result, Toast.LENGTH_SHORT).show();
            } else {
                displayAdvice(result);
            }

            if (adviceNumber > 0) {
                previousAdvice.setVisibility(View.VISIBLE);
                previousAdvice.setClickable(true);
            }
            if (adviceNumber == 0)
                previousAdvice.setVisibility(View.GONE);

            if (adviceNumber < urls.length - 1) {
                nextAdvice.setVisibility(View.VISIBLE);
                nextAdvice.setClickable(true);
            }
            if (adviceNumber == urls.length - 1)
                nextAdvice.setVisibility(View.GONE);


        }
    }

    /**
     * The advice is displayed here
     */
    private WebView adviceView;

    /**
     * Index of the currently displayed advice
     */
    private int adviceNumber = 0;

    /**
     * Authentication token
     */
    private String token = null;

    /**
     * keywords linked to the advice
     */
    private String keywords[];

    /**
     * URLS on which advices can be requested
     */
    private String urls[];

    /**
     * Buttons to change advices
     */
    private ImageButton previousAdvice, nextAdvice;

    /**
     * Update the WebViw to display an HTML-formated advice
     *
     * @param advice HTML string to display
     */
    private void displayAdvice (String advice) {
        System.out.println("Advice  : " + advice);
        adviceView.loadDataWithBaseURL(null, advice, "text/html", "utf-8", null);
        adviceView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBg));
    }


    /**
     * Displays an advice on the screen
     */
    private void changeAdvice () {
        previousAdvice.setClickable(false);
        nextAdvice.setClickable(false);

        if (urls.length == 0) {
            Resources res = getResources();
            displayAdvice(res.getString(R.string.htmlHead) + keywords[0] + res.getString(R.string.htmlTail));
        } else {
            TextView progress = (TextView) findViewById(R.id.progress);
            progress.setText((adviceNumber + 1) + "/" + urls.length);
            if (keywords == null)
                new GetAdviceRequestTask().execute(token, urls[adviceNumber]);
            else if (keywords.length == 1)
                new GetAdviceRequestTask().execute(token, urls[adviceNumber], keywords[0]);
            else if (keywords.length == 2)
                new GetAdviceRequestTask().execute(token, urls[adviceNumber], keywords[0], keywords[1]);
        }
    }

    /**
     * Receive the intent containing the URL and potential keywords on which advices should be retrieved
     *
     * @param savedInstanceState Not used here but mandatory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advice_display);
        adviceView = (WebView)findViewById(R.id.webview);

        Intent intent = getIntent();
        // Get the token from file to be identified

        try {
            String tokenID = FileIO.readFile(AdviceDisplay.this, getString(R.string.tokenFile));
            token = tokenID.split("\t")[0];
        } catch (IOException e) {
            Toast.makeText(AdviceDisplay.this, getString(R.string.fileExceptionMessage), Toast.LENGTH_SHORT).show();
        }

        urls = intent.getStringArrayExtra("urls");
        keywords = intent.getStringArrayExtra("keywords");

        //Arrows appear and disappear depending on the URLs provided
        previousAdvice = (ImageButton) findViewById(R.id.previousAdvice);
        nextAdvice = (ImageButton) findViewById(R.id.nextAdvice);
        previousAdvice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adviceNumber > 0)
                    --adviceNumber;
                changeAdvice();
            }
        });
        nextAdvice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adviceNumber < urls.length - 1)
                    ++adviceNumber;
                changeAdvice();
            }
        });
        if (urls.length > 1)
            nextAdvice.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Display the fist advice, if any
        changeAdvice();
    }
}
