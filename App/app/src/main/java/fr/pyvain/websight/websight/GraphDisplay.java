package fr.pyvain.websight.websight;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import fr.pyvain.websight.websight.SweetGraphs.GraphView;

/**
 * <p>Activity where the graph is displayed</p>
 *
 * <p>
 * @author Vincent LEVALLOIS
 * </p>
 */
public class GraphDisplay extends AppCompatActivity implements ResponseReceiver.Receiver {

    /**
     * Button used to get a graph. Will be animated to show that a request is being treated
     */
    private ImageButton getGraph = null;

    /**
     * Animation used on the button
     */
    private Animation animation = null;

    /**
     * This boolean tracks whether a Graph is being requested or not
     */
    private boolean isRequestRunning = false;



    private String userID;

    /**
     * Object used to get the data from the service used to retrieve the graph
     */
    private ResponseReceiver mReceiver;

    /**
     * Displays a graph in the graphView
     *
     * @param graphJSon String containing a JSon graph
     */
    private void displayGraph(String graphJSon) {
        TextView tuto = (TextView) findViewById(R.id.noGraphText);
        tuto.setVisibility(View.GONE);
        GraphView gView = (GraphView) findViewById(R.id.graphView);
        gView.setGraph(graphJSon);
        gView.setVisibility(View.VISIBLE);
        ImageButton changeLabels = (ImageButton) findViewById(R.id.changeLabels);
        changeLabels.setVisibility(View.VISIBLE);
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
            GraphDisplay.this.finish();
        }
    }

    /**
     * This method takes the token and data from files and use them to send an asynchronous
     * request to the server to get a graph
     */
    private void getGraph () {
        String token = null;
        try {
            String tokenID = FileIO.readFile(this, getResources().getString(R.string.tokenFile));
            token = tokenID.split("\t")[0];
        } catch (IOException e) {
            e.printStackTrace();
        }

        String rawKeywords = "";

        // Keywords are needed to generate a graph
        File file = getBaseContext().getFileStreamPath(userID + getString(R.string.dataFile));
        if(file.exists()) {
            isRequestRunning = true;
            animateButton();
            try {
                rawKeywords = FileIO.readFile(this, userID + getString(R.string.dataFile));
            } catch (IOException e) {
                Toast.makeText(GraphDisplay.this, getString(R.string.fileExceptionMessage), Toast.LENGTH_SHORT).show();
            } finally {
                if (!(rawKeywords.equals(""))) {
                    // De-concatenation of the keywords to put them in an array
                    String keywords[] = rawKeywords.split("\t");

                    // Pass parameters to the service
                    Intent i = new Intent(this, GraphRequestService.class);
                    i.putExtra("token", token);
                    i.putExtra("keywords", keywords);
                    i.putExtra("receiver", mReceiver);
                    startService(i);
                } else
                    Toast.makeText(GraphDisplay.this, getString(R.string.fillDataMessage), Toast.LENGTH_SHORT).show();
            }
        } else
            Toast.makeText(GraphDisplay.this, getString(R.string.fillDataMessage), Toast.LENGTH_SHORT).show();
    }

    /**
     * Starts the animation for the graph request button
     */
    private void animateButton () {
        getGraph.startAnimation(animation);
        getGraph.setClickable(false);
    }


    /**
     * The action performed when the service's task is completed
     *
     * @param resultCode error code : 0 if something went wrong, 1 otherwise
     * @param resultData either a short description of te error or a String representing a graph (JSon)
     */
    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        isRequestRunning = false;
        getGraph.clearAnimation();
        getGraph.setClickable(true);
        if (resultCode != 0) {
            String graph = new String(resultData.getCharArray("graph"));
            try {
                FileIO.writeInFile(this, userID + getResources().getString(R.string.graphFile), graph);
            } catch (IOException e) {
                Toast.makeText(GraphDisplay.this, getString(R.string.fillDataMessage), Toast.LENGTH_SHORT).show();
            }
            displayGraph(graph);
        } else {
            String error = new String(resultData.getCharArray("error"));
            Toast.makeText(GraphDisplay.this, error, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method saves the state of the activity in order to rebuild if changes occur
     *
     * @param savedState bundle where data to restore will be saved
     */
    protected void onSaveInstanceState(Bundle savedState) {
        savedState.putParcelable("receiver", mReceiver);
        savedState.putBoolean("requesting", isRequestRunning);
        super.onSaveInstanceState(savedState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_display);

        //Get saved data back from the previous instance is there was one
        if (savedInstanceState != null) {
            isRequestRunning = savedInstanceState.getBoolean("requesting");
            mReceiver = savedInstanceState.getParcelable("receiver");
        } else {
            mReceiver = new ResponseReceiver(new Handler());
        }
        mReceiver.setReceiver(this);

        try {
            String tokenID = FileIO.readFile(this, getString(R.string.tokenFile));
            userID = tokenID.split("\t")[1];
        } catch (IOException e) {
            Toast.makeText(GraphDisplay.this, getString(R.string.fileExceptionMessage), Toast.LENGTH_SHORT).show();
        }

        File file = getBaseContext().getFileStreamPath(userID + getString(R.string.graphFile));
        if(file.exists()) {
            String graph = "";
            try {
                graph = FileIO.readFile(this, userID + getString(R.string.graphFile));
            } catch (IOException e) {
                new CustomToast(GraphDisplay.this, getString(R.string.fileExceptionMessage), 90, 16.0/9).show();
            }
            displayGraph(graph);
        }


        // Click on the goToParameters -> go to activity Parameters
        ImageButton goToParameters = (ImageButton) findViewById(R.id.goToParameters);
        goToParameters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToParameters = new Intent(GraphDisplay.this, Parameters.class);
                startActivity(goToParameters);
            }
        });

        // Click on the changeLabels -> change labels
        ImageButton changeLabels = (ImageButton) findViewById(R.id.changeLabels);
        changeLabels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphView gView = (GraphView) findViewById(R.id.graphView);
                gView.changeLabels();
            }
        });

        animation = AnimationUtils.loadAnimation(GraphDisplay.this.getBaseContext(), R.anim.rotation);

        // Click on getGraph -> start GraphRequestService and animate the button to notify that
        // a search is being performed
        getGraph = (ImageButton) findViewById(R.id.getGraph);
        if (isRequestRunning) {
            animateButton();
        }
        getGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getGraph();
//                TextView tuto = (TextView) findViewById(R.id.noGraphText);
//                tuto.setVisibility(View.GONE);
//                GraphView gView = (GraphView) findViewById(R.id.graphView);
//                gView.setVisibility(View.VISIBLE);
//                ImageButton changeLabels = (ImageButton) findViewById(R.id.changeLabels);
//                changeLabels.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        File file = getBaseContext().getFileStreamPath(getString(R.string.tokenFile));
        if(!file.exists()) {
            GraphDisplay.this.finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        GraphView gView = (GraphView) findViewById(R.id.graphView);
        gView.pause();
    }


    @Override
    public void onBackPressed()
    {
        logOut();
    }
}
