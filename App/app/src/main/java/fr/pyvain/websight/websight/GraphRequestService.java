package fr.pyvain.websight.websight;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.io.IOException;

/**
 * <p> Service used </p></p>
 *
 * <p>
 * @author Vincent LEVALLOIS
 * </p>
 */
public class GraphRequestService extends IntentService {

    public GraphRequestService() {
        super("GraphService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("Début request");
        String token = intent.getStringExtra("token");
        String[] keywords = intent.getStringArrayExtra("keywords");
        ResultReceiver receiver = intent.getParcelableExtra("receiver");

        Bundle b = new Bundle();
        int code = 0;

        Resources resources = getResources();
//        b.putCharArray("graph", Graph.niceGraph().toCharArray());
//        code = 1;
        String graph;
        try {
            graph = new Request(this.getApplicationContext()).getGraph(token, keywords);
            b.putCharArray("graph", graph.toCharArray());
            code = 1;
        } catch (IOException e) {
            b.putCharArray("error", resources.getString(R.string.ioExceptionMessage).toCharArray());
        } catch (InvalidCredentialsException e) {
            b.putCharArray("error", resources.getString(R.string.invalidCredentialsExceptionMessage).toCharArray());
        } catch (ServerException e) {
            b.putCharArray("error", resources.getString(R.string.serverExceptionMessage).toCharArray());
        } catch (ServiceUnavailableException e) {
            b.putCharArray("error", resources.getString(R.string.ServiceUnvailableMessage).toCharArray());
        } catch (NoConnexionException e) {
            b.putCharArray("error", resources.getString(R.string.NoConnexionMessage).toCharArray());
        }
        System.out.println("fin request");
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icon)
                        .setVibrate(new long[] {0, 500})
                        .setSound(notificationSound)
                        .setContentTitle("Search complete")
                        .setContentText("You can now visualize the results.");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, GraphDisplay.class);
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(GraphDisplay.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        int mId = 0;
        // mId allows you to update the notification later on.
        mNotificationManager.notify(mId, mBuilder.build());
        receiver.send(code, b);
    }
}
