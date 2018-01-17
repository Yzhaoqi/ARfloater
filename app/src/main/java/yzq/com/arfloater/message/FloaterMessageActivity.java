package yzq.com.arfloater.message;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;

import yzq.com.arfloater.R;
import yzq.com.arfloater.been.Floater;
import yzq.com.arfloater.been.FloaterLabel;
import yzq.com.arfloater.server.FloaterServer;

public class FloaterMessageActivity extends AppCompatActivity {
    private static final int EDIT_MODE = 0;
    private static final int MESSAGE_MODE = 1;

    private LocationHelper locationHelper;

    private FloaterLabel floaterLabel;
    private FloaterServer floaterServer;

    private int mode;
    private LinearLayout editLayout, messageLayout;
    private TextView title, locationText, message;
    private EditText editText;
    //TODO add Leave Words;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floater_message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        floaterLabel = new FloaterLabel();
        floaterLabel.setTitle(getIntent().getStringExtra("title"));
        floaterServer = FloaterServer.getInstance();
        locationHelper = LocationHelper.getInstance(this);

        editLayout = (LinearLayout)findViewById(R.id.edit_layout);
        messageLayout = (LinearLayout)findViewById(R.id.message_layout);
        title = (TextView)findViewById(R.id.floater_title);
        locationText = (TextView)findViewById(R.id.floater_location);
        message = (TextView)findViewById(R.id.floater_message);

        locationHelper.registerListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.floater_send, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.floater_send:
                // TODO
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onResume() {
        new FloaterGettingTask(this, floaterLabel, floaterServer).execute();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private class FloaterGettingTask extends AsyncTask<Void, Void, Floater> {
        private FloaterServer floaterServer;
        private Context context;
        private ProgressDialog pd;
        private FloaterLabel floaterLabel;

        public FloaterGettingTask(Context context, FloaterLabel floaterLabel, FloaterServer server) {
            this.floaterServer = FloaterServer.getInstance();
            this.context = context;
            this.floaterLabel = floaterLabel;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(context, "Floater", "Checking...");
        }

        @Override
        protected Floater doInBackground(Void... voids) {
            while (locationHelper.getLocation() == null);
            BDLocation location = locationHelper.getLocation();
            Log.i("BDLocation", String.valueOf(location.getLatitude()) + " " + String.valueOf(location.getLongitude()));
            floaterLabel.setLatitude(location.getLatitude());
            floaterLabel.setLongitude(location.getLongitude());
            return null;
        }

        @Override
        protected void onPostExecute(Floater floater) {
            pd.cancel();
            BDLocation location = locationHelper.getLocation();
            Toast.makeText(FloaterMessageActivity.this, String.valueOf(location.getLatitude()) + " " + String.valueOf(location.getLongitude()), Toast.LENGTH_SHORT).show();
            //title.setText("Title: "+ floater.getTitle());
            if (floater == null) {
                mode = EDIT_MODE;
                editLayout.setVisibility(View.VISIBLE);
            } else {
                mode = MESSAGE_MODE;
                messageLayout.setVisibility(View.VISIBLE);
                message.setText(floater.getText());
            }
        }
    }

    private class SubmitTask extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog pd;
        private Context context;
        private Floater floater;

        public SubmitTask(Context context, Floater floater, int mode) {
            this.context = context;
            this.floater = floater;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(context, "Floater", "Checking...");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            //return FloaterServer.getInstance().submit(floater);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);
            if (isSuccess) {
                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }
            pd.dismiss();
        }
    }
}
