package fr.pyvain.websight.websight;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * <p>This class provides methods to request the server.</p>
 *
 * <p>
 * @author Etienne THIERY
 * </p>
 */
class Request {

    private final Context context;
    private HttpURLConnection urlConnection;

    /**
     * Constuctor Request
     *
     * @param context
     *          Must be the context of the activity, used to fetch resources
     */
    public Request(Context context) {
        this.context = context;
        this.urlConnection = null;
    }

    /**
     * Checks if there is an internet connexion available
     *
     * @return true if there is a connexion
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    /**
     * Sets up a HttpURLConnection for a post request on server whose
     * url is given
     *
     * @param url
     *          The server's url
     */
    private void setPOST(String url) throws IOException, NoConnexionException {
        // Opens connection and sets it for POST request
        URL urlToRequest = new URL(url);
        // openConnection() may throw IOException


        int retries = 0;
        if (isNetworkConnected()) {
            Resources res = context.getResources();
            while ((retries < res.getInteger(R.integer.max_connexion_retries)) && (urlConnection == null)) {
                try {
                    urlConnection = (HttpURLConnection) urlToRequest.openConnection();
                } catch (IOException e) {
                    ++retries;
                }
            }
            if ((urlConnection == null))
                throw new IOException();
        }
        else
            throw new NoConnexionException();
        // setDoOuput() and setChunkedStreamingMode() may throw IllegalStateException
        // if already connected (not possible here)
        urlConnection.setDoOutput(true);
        urlConnection.setChunkedStreamingMode(0);
    }

    /**
     * Sets up a HttpURLConnection for a post request on server whose
     * url is and add an identification token to the header
     *
     * @param url The server's url
     * @param token token used to identify a user
     */
    private void setPOST(String url, String token) throws IOException, NoConnexionException {
        setPOST(url);
        urlConnection.addRequestProperty("Authorization", token);
    }

    /**
     * Sends a POST query to the server
     * A HttpUrlConnection must already have been set up with
     * a call to setPOST()
     *
     * @param query
     *          HTTP Query to send
     * @throws IOException
     */
    private void sendPOST(String query) throws IOException {
        if (urlConnection != null) {

            // getOutputStream() may throw IOException
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));

            // write() and close() may throw IOException
            writer.write(query);
            writer.close();
        }
    }

    /**
     * Returns the response code returned by the server
     * Can be called only after sending a query with sendPOST
     * (else the server may wait for data and
     * a call to responseCode() may block)
     *
     * @return The response code
     *
     * @see Request#sendPOST
     */
    private int responseCode() throws IOException {
        if (urlConnection != null) {
            // getResponseCode() may throw IOException
            return urlConnection.getResponseCode();
        } else {
            return -1;
        }
    }

    /**
     * Returns the response returned by the server
     * Can be called only after sending a query with sendPOST
     * (else the server may wait for data and
     * a call to response() may block)
     *
     * @return The response code
     *
     * @see Request#sendPOST
     */
    private String response() throws IOException {
        if (urlConnection != null) {
            String response = "";
            // getInputStream() may throw IOException
            InputStream is = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            // Reads whole stream
            String line;
            // readLine() may throw IOException
            while ((line = reader.readLine()) != null) {
                response += line;
            }
            return response;
        } else {
            return "";
        }
    }

    private String errorResponse() throws IOException {
        if (urlConnection != null) {
            String response = "";
            // getInputStream() may throw IOException
            InputStream is = urlConnection.getErrorStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            // Reads whole stream
            String line;
            // readLine() may throw IOException
            while ((line = reader.readLine()) != null) {
                response += line;
            }
            return response;
        } else {
            return "";
        }
    }

    /**
     * Asks the server for an access token and returns it
     *
     * @param email
     *          Email of the user concerned
     * @param password
     *          Password of the user concerned
     * @return The response code
     *
     * @throws IOException
     * @throws InvalidCredentialsException
     * @throws ServerException
     */
    public String getAccessToken(String email, String password)
            throws IOException, InvalidCredentialsException, ServerException, NoConnexionException {

        // Sets up connection
        Resources resources = this.context.getResources();
        String url = resources.getString(R.string.serverUrl);   
        String signInPath = resources.getString(R.string.signInPath);
        // setPost() may throw a IOException
        this.setPOST(url + signInPath);

        // Sends request
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("email", email)
                .appendQueryParameter("password", password);
        this.sendPOST(builder.build().getEncodedQuery());

        System.out.println("Return code : " + this.responseCode());

        // Analyses response
        switch (this.responseCode()) {
            // Extracts token and returns it
            case HttpURLConnection.HTTP_OK:
                String response = this.response();
                try {
                    // JSONObject() and getString() may throw JSONException
                    // if the response is ill-formed
                    JSONObject json = new JSONObject(response);
                    return json.getString("jwt");
                } catch (JSONException e) {
                    System.out.println("Received : " + response);
                    throw new ServerException();
                }
            // Handles error responses
            case HttpURLConnection.HTTP_BAD_REQUEST:
                System.out.println(this.errorResponse());

                throw new InvalidCredentialsException();
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                System.out.println(this.errorResponse());

                throw new InvalidCredentialsException();
            default:
                System.out.println(this.errorResponse());

                throw new ServerException();
        }
    }

    public void signUp(String email, String password)
            throws IOException, EmailAlreadyUsedException,
            InvalidCredentialsException, ServerException, NoConnexionException {

        // Sets up connection
        Resources resources = this.context.getResources();
        String url = resources.getString(R.string.serverUrl);
        String signUpPath = resources.getString(R.string.signUpPath);
        // setPost() may throw a IOException
        this.setPOST(url + signUpPath);

        // Sends request
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("email", email)
                .appendQueryParameter("password", password);
        this.sendPOST(builder.build().getEncodedQuery());

        System.out.println("Return code : " + this.responseCode());

        // Analyses response
        switch (this.responseCode()) {
            // Exits method successfully
            case HttpURLConnection.HTTP_OK:
                break;
            // Handles error responses
            case HttpURLConnection.HTTP_BAD_REQUEST:
                System.out.println(this.errorResponse());
                String response = this.response();
                try {
                    // JSONObject() and getString() may throw JSONException
                    // if the response is ill-formed
                    JSONObject json = new JSONObject(response);
                    if (json.getString("error").equals("Address is already used")) {
                        System.out.println(this.errorResponse());

                        throw new EmailAlreadyUsedException();
                    } else {
                        System.out.println(this.errorResponse());

                        throw new InvalidCredentialsException();
                    }
                } catch (JSONException e) {
                    System.out.println(this.errorResponse());

                    throw new ServerException();
                }
            default:
                System.out.println(this.errorResponse());

                throw new ServerException();
        }
    }

    /**
     * Asks the server for a graph and returns it
     *
     * @param token
     *          token to identify a user
     * @param keywords
     *          keywords used to build the graph
     * @return The JSon of a graph
     *
     * @throws IOException
     * @throws InvalidCredentialsException
     * @throws ServerException
     */
    public String getGraph(String token, String[] keywords)
            throws IOException, InvalidCredentialsException, ServerException, ServiceUnavailableException, NoConnexionException {

        // Sets up connection
        Resources resources = this.context.getResources();
        String url = resources.getString(R.string.serverUrl);
        String getGraphPath = resources.getString(R.string.getGraphPath);
        // setPost() may throw a IOException
        this.setPOST(url + getGraphPath, token);

        // Sends request
        Uri.Builder builder = new Uri.Builder();
        for (String keyword : keywords)
            builder.appendQueryParameter("keywords[]", keyword);
        this.sendPOST(builder.build().getEncodedQuery());

        System.out.println("Token : " + token);
        System.out.println("Return code : " + this.responseCode());

        // Analyses response
        switch (this.responseCode()) {
            // Extracts token and returns it
            case HttpURLConnection.HTTP_OK: {
                return this.response();
            }
            // Handles error responses
            case HttpURLConnection.HTTP_BAD_REQUEST:
                System.out.println(this.errorResponse());

                throw new InvalidCredentialsException();
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                System.out.println(this.errorResponse());

                throw new InvalidCredentialsException();
            case HttpURLConnection.HTTP_UNAVAILABLE:
                System.out.println(this.errorResponse());

                throw new ServiceUnavailableException();
            default:
                System.out.println(this.errorResponse());

                throw new ServerException();
        }
    }

    /**
     * Asks the server for a graph and returns it
     *
     * @param token
     *          token to identify a user
     * @param password
     *          user password
     *
     * @throws IOException
     * @throws InvalidCredentialsException
     * @throws ServerException
     */
    public void deleteAccount(String token, String password)
            throws IOException, InvalidCredentialsException, ServerException, NoConnexionException {

        // Sets up connection
        Resources resources = this.context.getResources();
        String url = resources.getString(R.string.serverUrl);
        String deleteAccountPath = resources.getString(R.string.deleteAccuntPath);
        // setPost() may throw a IOException
        this.setPOST(url + deleteAccountPath, token);

        // Sends request
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("password", password);
        this.sendPOST(builder.build().getEncodedQuery());

        System.out.println("Return code : " + this.responseCode());

        // Analyses response
        switch (this.responseCode()) {
            case HttpURLConnection.HTTP_OK:
                break;
            // Handles error responses
            case HttpURLConnection.HTTP_BAD_REQUEST:
                System.out.println(this.errorResponse());

                throw new InvalidCredentialsException();
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                System.out.println(this.errorResponse());

                throw new InvalidCredentialsException();
            default:
                System.out.println(this.errorResponse());

                throw new ServerException();
        }
    }

    /**
     * Asks the server for an advice concerning an URL and keywords if relevant
     *
     * @param token
     *          token to identify a user
     * @param adviceUrl
     *          URL on which an advice is required
     * @param keywords
     *          keywords used to build the graph
     * @return The JSon of a graph
     *
     * @throws IOException
     * @throws InvalidCredentialsException
     * @throws ServerException
     */
    public String getAdvice(String token, String adviceUrl, String[] keywords)
            throws IOException, InvalidCredentialsException, ServerException, NoConnexionException {

        // Sets up connection
        Resources resources = this.context.getResources();
        String url = resources.getString(R.string.serverUrl);
        String getGraphPath = resources.getString(R.string.getAdvicePath);
        // setPost() may throw a IOException
        this.setPOST(url + getGraphPath, token);

        // Sends request
        Uri.Builder builder = new Uri.Builder();
        builder.appendQueryParameter("url", adviceUrl);
        System.out.println("url : " + adviceUrl);
        if (keywords != null) {
            for (String keyword : keywords) {
                System.out.println("mot " + keyword);
                builder.appendQueryParameter("keywords[]", keyword);
            }
        }
        this.sendPOST(builder.build().getEncodedQuery());

        System.out.println("Token : " + token);
        System.out.println("Return code : " + this.responseCode());

        // Analyses response
        switch (this.responseCode()) {
            // Extracts token and returns it
            case HttpURLConnection.HTTP_OK: {
                return this.response();
            }
            // Handles error responses
            case HttpURLConnection.HTTP_BAD_REQUEST:
                System.out.println(this.errorResponse());

                throw new InvalidCredentialsException();
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                System.out.println(this.errorResponse());

                throw new InvalidCredentialsException();
            default:
                System.out.println(this.errorResponse());

                throw new ServerException();
        }
    }


    /**
     * Asks the server to send an email with model letters to ask for information removal
     *
     * @param token
     *          token to identify a user
     *
     * @throws IOException
     * @throws InvalidCredentialsException
     * @throws ServerException
     */
    public void sendModels(String token)
            throws IOException, InvalidCredentialsException, ServerException, NoConnexionException {

        // Sets up connection
        Resources resources = this.context.getResources();
        String url = resources.getString(R.string.serverUrl);
        String getGraphPath = resources.getString(R.string.sendModelPath);
        // setPost() may throw a IOException
        this.setPOST(url + getGraphPath, token);

        // Sends request
        Uri.Builder builder = new Uri.Builder().appendQueryParameter("void", null);
        this.sendPOST(builder.build().getEncodedQuery());

        System.out.println("Token : " + token);
        System.out.println("Return code : " + this.responseCode());

        // Analyses response
        switch (this.responseCode()) {
            // Extracts token and returns it
            case HttpURLConnection.HTTP_OK:
                break;
            // Handles error responses
            case HttpURLConnection.HTTP_BAD_REQUEST:
                System.out.println(this.errorResponse());

                throw new InvalidCredentialsException();
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                System.out.println(this.errorResponse());

                throw new InvalidCredentialsException();
            default:
                System.out.println(this.errorResponse());

                throw new ServerException();
        }
    }
}
