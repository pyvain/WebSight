package fr.pyvain.websight.websight;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * <p>This class allows communication between Services and other activities</p>
 *
 * <p>
 * @author Vincent LEVALLOIS
 * </p>
 */

public class ResponseReceiver extends ResultReceiver {

    private Receiver mReceiver;

    public ResponseReceiver(Handler handler) {
        super(handler);
    }

    /**
     * Interface implemented in the activity to allow it to receive data from a service
     */
    public interface Receiver {
        void onReceiveResult(int resultCode, Bundle resultData);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }


    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }

}