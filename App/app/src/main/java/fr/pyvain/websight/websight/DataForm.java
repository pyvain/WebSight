package fr.pyvain.websight.websight;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * <p>Activity where the user can provide his personal data</p>
 *
 * <p>
 * @author Vincent LEVALLOIS
 * </p>
 */
public class DataForm extends AppCompatActivity {

    /**
     * Activity context
     */
    private Context context;

    /**
     * The number of EditTexts can vary an each has to be accessible for reading
     */
    private final ArrayList<EditText> editTextList = new ArrayList<>();

    /**
     * RadialLayout in which views are added dynamically
     */
    private LinearLayout dynamicLayout;

    /**
     * This button is used to add a new EditText and should be hidden when the threshold is met
     */
    private Button addEditTextButton;

    private String userID;

    /**
     * Listener used to add editTexts dynamically
     */
    private final View.OnClickListener addEditListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText editText = newEditText();

            editTextList.add(editText);

            generateLayout();
        }
    };

    /**
     * Creates a new edit text with appropriate properties
     *
     * @return an empty default EditText
     */
    private EditText newEditText() {
        EditText editText = new EditText(getBaseContext());

        editText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

        editText.setHint(R.string.dataForm);

        editText.setHintTextColor(ContextCompat.getColor(this, R.color.colorHint));

        editText.setFreezesText(true);

        editText.setVisibility(View.VISIBLE);

        editText.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);

        return editText;
    }

    /**
     * Creates a new EditText with a pre-filled text
     *
     * @param text the EditText is filled with this
     * @return a pre-filled EditText
     */
    private EditText newEditText(String text) {
        EditText ret = newEditText();
        ret.setText(text);
        return ret;
    }

    /**
     * Refreshes the display of the layout to make sure the correct views are displayed
     */
    private void generateLayout() {
        dynamicLayout.removeAllViewsInLayout();

        for (EditText editText : editTextList) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            params.setMargins(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.interline));
            dynamicLayout.addView(editText, params);
        }
        if (editTextList.size() >= getResources().getInteger(R.integer.max_keywords))
            addEditTextButton.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_form);
        context = this;
        final Intent intent = getIntent();

        try {
            String tokenID = FileIO.readFile(this, getString(R.string.tokenFile));
            userID = tokenID.split("\t")[1];
        } catch (IOException e) {
            Toast.makeText(DataForm.this, getString(R.string.fileExceptionMessage), Toast.LENGTH_SHORT).show();
        }

        dynamicLayout = (LinearLayout) findViewById(R.id.data_form_layout);

        // Re-fills the data if some already has been input before
        File file = getBaseContext().getFileStreamPath(userID + getString(R.string.dataFile));
        if (file.exists()) {
            String data = null;
            try {
                data = FileIO.readFile(this, userID + getString(R.string.dataFile));
            } catch (IOException e) {
                Toast.makeText(DataForm.this, getString(R.string.fileExceptionMessage), Toast.LENGTH_SHORT).show();
            } finally {
                String separatedData[] = new String[0];

                if (data != null) {
                    separatedData = data.split("\t");
                }

                for (String aSeparatedData : separatedData) {
                    if (aSeparatedData.length() > 0)
                        editTextList.add(newEditText(aSeparatedData));
                }
            }
        }

        // Click on addEditTextButton -> add an EditText to the list
        addEditTextButton = (Button) findViewById(R.id.addEditTextButton);
        addEditTextButton.setOnClickListener(addEditListener);

        generateLayout();

        // Click on validateDataButton -> concatenate all keywords and save them then finish activity
        Button validateDataButton = (Button) findViewById(R.id.validateDataButton);
        validateDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = "";

                for (int i = 0; i < editTextList.size(); ++i) {
                    if (i > 0)
                        data += "\t";
                    data += editTextList.get(i).getText().toString();
                }

                try {
                    FileIO.writeInFile(context, userID + getString(R.string.dataFile), data);
                } catch (IOException e) {
                    Toast.makeText(DataForm.this, getString(R.string.fileExceptionMessage), Toast.LENGTH_SHORT).show();
                }

                // Send a signal to the previous activity so it can be terminated
                ResultReceiver receiver = intent.getParcelableExtra("receiver");
                receiver.send(0, null);
                DataForm.this.finish();
            }
        });
    }
}