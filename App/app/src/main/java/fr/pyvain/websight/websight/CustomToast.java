package fr.pyvain.websight.websight;

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * <p>Custom Toast activity</p>
 *
 * <p>
 * @author Etienne THIERY, etienne.thiery@wanadoo.fr
 * </p>
 */
class CustomToast {

    private final Toast toast;

    public CustomToast(Activity activity, String text,
                       int percentWidth, double ratio) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(
                R.layout.custom_toast,
                (ViewGroup) activity.findViewById(R.id.custom_toast_root));

        // Sets text
        TextView textView = (TextView) layout.findViewById(R.id.text);
        textView.setText(text);
        // Sets maximum width and height
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = percentWidth *size.x/100;
        int height = (int) Math.floor(width/ratio);
        textView.setWidth(width);
        textView.setHeight(height);

        toast = new Toast(activity.getApplicationContext());
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
    }

    public void show() {
        toast.show();
    }
}
