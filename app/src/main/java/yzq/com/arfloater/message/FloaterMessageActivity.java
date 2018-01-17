package yzq.com.arfloater.message;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;

import yzq.com.arfloater.R;
import yzq.com.arfloater.been.Floater;
import yzq.com.arfloater.been.FloaterLabel;
import yzq.com.arfloater.server.FloaterServer;

public class FloaterMessageActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int EDIT_MODE = 0;
    private static final int MESSAGE_MODE = 1;

    private LocationHelper locationHelper;

    private FloaterLabel floaterLabel;
    private FloaterServer floaterServer;

    private int mode;
    private LinearLayout editLayout, messageLayout;
    private TextView title, locationText, message;
    private EditText editText, editWord;
    private RecyclerView leaveWordRecycler;
    private Button floaterSend, leaveWordSend;
    private Floater mFloater;
    private LeaveWordsAdapter leaveWordsAdapter;

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
        editText = (EditText)findViewById(R.id.edit_text);
        editWord = (EditText)findViewById(R.id.message_edit_words);
        floaterSend = (Button)findViewById(R.id.floater_send);
        leaveWordSend = (Button)findViewById(R.id.leave_word_send);
        leaveWordRecycler = (RecyclerView)findViewById(R.id.message_list);
        leaveWordRecycler.setLayoutManager(new LinearLayoutManager(this));

        locationHelper.registerListener();
        floaterSend.setOnClickListener(this);
        leaveWordSend.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floater_send:
            case R.id.leave_word_send:
                sendFloater();
                break;
            default:
                break;
        }
    }

    private void sendFloater() {
        if (mode == EDIT_MODE) {
            if (editText.getText().toString().isEmpty()) {
                Toast.makeText(this, "请编辑漂流瓶", Toast.LENGTH_SHORT).show();
                return;
            }
            String s = editText.getText().toString();
            Floater floater = new Floater(floaterLabel);
            floater.setText(s);
            new SubmitTask(this, floater, mode).execute();
        } else {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editWord.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            if (editWord.getText().toString().isEmpty()) {
                Toast.makeText(this, "留言不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            String s = editWord.getText().toString();
            mFloater.addLeaveWord(s);
            new SubmitTask(this, mFloater, mode).execute();
        }
    }

    @Override
    protected void onResume() {
        new FloaterGettingTask(this).execute();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private class FloaterGettingTask extends AsyncTask<Void, Void, Floater> {
        private Context context;
        private ProgressDialog pd;

        public FloaterGettingTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(context, "获取漂流瓶中", "请稍后...");
        }

        @Override
        protected Floater doInBackground(Void... voids) {
            while (locationHelper.getLocation() == null);
            BDLocation location = locationHelper.getLocation();
            Log.i("BDLocation", String.valueOf(location.getLatitude()) + " " + String.valueOf(location.getLongitude()));
            floaterLabel.setLatitude(location.getLatitude());
            floaterLabel.setLongitude(location.getLongitude());
            return floaterServer.getFloater(floaterLabel);
        }

        @Override
        protected void onPostExecute(Floater floater) {
            pd.cancel();
            title.setText(getString(R.string.title) + floaterLabel.getTitle());
            locationText.setText(getString(R.string.message_location)+ String.valueOf(floaterLabel.getLongitude()) + " "+ String.valueOf(floaterLabel.getLatitude()));
            if (floater == null) {
                FloaterMessageActivity.this.setTitle("Edit Floater");
                mode = EDIT_MODE;
                editLayout.setVisibility(View.VISIBLE);
            } else {
                FloaterMessageActivity.this.setTitle("Read Floater");
                mode = MESSAGE_MODE;
                messageLayout.setVisibility(View.VISIBLE);
                message.setText(floater.getText());
                leaveWordsAdapter = new LeaveWordsAdapter(floater.getLeaveWords());
                leaveWordRecycler.setAdapter(leaveWordsAdapter);
                mFloater = floater;
            }
        }
    }

    private class SubmitTask extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog pd;
        private Context context;
        private Floater floater;
        private int mode;

        public SubmitTask(Context context, Floater floater, int mode) {
            this.context = context;
            this.floater = floater;
            this.mode = mode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(context, "正在提交", "请稍后...");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (mode == EDIT_MODE) {
                return floaterServer.submit(floater);
            } else {
                return floaterServer.submitLeaveWords(floater);
            }
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);
            if (isSuccess) {
                Toast.makeText(context, "发送成功", Toast.LENGTH_SHORT).show();
                if (mode == EDIT_MODE) {
                    FloaterMessageActivity.this.finish();
                } else {
                    leaveWordsAdapter.notifyDataSetChanged();
                    editWord.setText("");
                    editWord.clearFocus();
                }
            } else {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }
            pd.dismiss();
        }
    }
}
