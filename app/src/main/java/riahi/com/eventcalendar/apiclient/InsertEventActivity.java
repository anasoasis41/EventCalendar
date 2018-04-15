package riahi.com.eventcalendar.apiclient;

import android.app.DatePickerDialog;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.xdty.preference.colorpicker.ColorPickerDialog;
import org.xdty.preference.colorpicker.ColorPickerSwatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import riahi.com.eventcalendar.R;

public class InsertEventActivity extends AppCompatActivity {

    private int mSelectedColor;

    TextView tvColor;
    int[] mColors;
    private EditText nameET,startET,endET;
    private TextView dateET;

    private Button btnInsert;
    TextView colorEvent;

    String hexColor;

    public static final String ROOT_URL = "http://192.168.2.16/";

    Calendar myCalendar;

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_event);

        tvColor = (TextView) findViewById(R.id.tvColor);

        mSelectedColor = ContextCompat.getColor(this, R.color.flamingo);

        mColors = getResources().getIntArray(R.array.default_rainbow);

        nameET = (EditText) findViewById(R.id.tvName);
        dateET = (TextView) findViewById(R.id.tvDate);
        startET = (EditText) findViewById(R.id.tvDebut);
        endET = (EditText) findViewById(R.id.tvFin);
        colorEvent = (TextView) findViewById(R.id.tvColor);

        btnInsert = (Button) findViewById(R.id.btnInsert);

        myCalendar = Calendar.getInstance();

        dateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog dpd = new DatePickerDialog(InsertEventActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date d = null;
                try {
                    String currentDate = sdf.format(new Date());
                    d = sdf.parse(currentDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dpd.getDatePicker().setMinDate(d.getTime());
                dpd.show();

            }
        });

        tvColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
                        mColors,
                        mSelectedColor,
                        5, // Number of columns
                        ColorPickerDialog.SIZE_SMALL);
                dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {

                    @Override
                    public void onColorSelected(int color) {
                        mSelectedColor = color;
                        tvColor.setBackgroundColor(mSelectedColor);

                        hexColor = String.format("#%06X", (0xFFFFFF & mSelectedColor));

                        //insertNewEvent(hexColor);
                        //Toast.makeText(InsertEventActivity.this, "color is:"+hexColor, Toast.LENGTH_SHORT).show();

                    }

                });

                dialog.show(getFragmentManager(), "color_dialog_test");
            }
        });



        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                insertNewEvent(hexColor);
            }
        });

    }

    private void insertNewEvent(String colorHx){

        if (colorHx == null){
            colorHx = "#007FFF";
        } else {
            colorHx = hexColor;
        }

        String dateEvnt = dateET.getText().toString();

        //Toast.makeText(InsertEventActivity.this, "color is:"+colorHx, Toast.LENGTH_SHORT).show();

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(ROOT_URL) //Setting the Root URL
                .build(); //Finally building the adapter

        //Creating object for our interface
        JsonService api = adapter.create(JsonService.class);

        //Defining the method insertEvent of our interface
        api.insertEvent(
                //Passing the values by getting it from editTexts
                nameET.getText().toString(),
                dateEvnt,
                startET.getText().toString(),
                endET.getText().toString(),
                colorHx,

                //Creating an anonymous callback
                new Callback<Response>() {
                    @Override
                    public void success(Response result, Response response) {
                        //On success we will read the server's output using bufferedreader
                        //Creating a bufferedreader object
                        BufferedReader reader = null;

                        //An string to store output from the server
                        String output = "";

                        try {
                            //Initializing buffered reader
                            reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                            //Reading the output in the string
                            output = reader.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //Displaying the output as a toast
                        Toast.makeText(InsertEventActivity.this, output, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        //If any error occured displaying the error as toast
                        Toast.makeText(InsertEventActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void updateLabel() {

        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);

        String date = sdf.format(myCalendar.getTime());
        dateET.setText(date);
        //Common.mDate = date;

    }
}
