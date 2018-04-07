package yzq.com.arfloater.orienteering.sponsor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;

import java.util.ArrayList;
import java.util.List;

import yzq.com.arfloater.R;
import yzq.com.arfloater.been.Feature;
import yzq.com.arfloater.camera.DetectorActivity;
import yzq.com.arfloater.message.LocationHelper;
import yzq.com.arfloater.server.OrienteeringServer;

public class OrienteeringSponsorActivity extends AppCompatActivity implements View.OnClickListener, FeaturesAdapter.OnItemClickListener{

    private RecyclerView rv;
    private FloatingActionButton fab;
    private LocationHelper locationHelper;
    private List<Feature> mFeatureArray;
    private FeaturesAdapter featuresAdapter;
    private String id;
    private String pwd;
    private Boolean isNew;

    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getString("ID");
            pwd = bundle.getString("PASSWORD");
            isNew = bundle.getBoolean("IS_NEW");
        }
        setContentView(R.layout.activity_orienteering_sponsor);
        rv = findViewById(R.id.rv_feature);
        fab = findViewById(R.id.fab_add);

        if (!isNew) {
            mFeatureArray = OrienteeringServer.getInstance().getFeatureList();
        } else {
            mFeatureArray = new ArrayList<Feature>();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        fab.setOnClickListener(this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        featuresAdapter = new FeaturesAdapter(mFeatureArray);
        rv.setAdapter(featuresAdapter);
        featuresAdapter.setOnItemClickListener(this);
        locationHelper = LocationHelper.getInstance(this);
        locationHelper.registerListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.orienteering, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_submit:
                new SubmitTask(this).execute();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_add:
                Intent intent = new Intent(OrienteeringSponsorActivity.this, DetectorActivity.class);
                intent.putExtra("REQUEST_CODE", REQUEST_CODE);
                startActivityForResult(intent, REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "Fail to get place", Toast.LENGTH_SHORT).show();
        } else {
            String title = data.getStringExtra("title");
            BDLocation bdLocation = locationHelper.getLocation();
            Feature feature = new Feature();
            feature.setHint("");
            feature.setLatitude(bdLocation.getLatitude());
            feature.setLongitude(bdLocation.getLongitude());
            feature.setTitle(title);
            mFeatureArray.add(feature);
            featuresAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        showFeatureDialog(position);
    }

    private void showFeatureDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_feature, null);
        final EditText editText = view.findViewById(R.id.dialog_edit_hint);
        final EditText editQuestion = view.findViewById(R.id.dialog_edit_question);
        final EditText editAnswer = view.findViewById(R.id.dialog_edit_answer);
        TextView textView = view.findViewById(R.id.dialog_title);
        Switch dialog_switch = view.findViewById(R.id.dialog_switch);
        final TextInputLayout layoutQuestion = view.findViewById(R.id.dialog_layout_question);
        final TextInputLayout layoutAnswer = view.findViewById(R.id.dialog_layout_answer);
        editText.setText(mFeatureArray.get(position).getHint());
        textView.setText("特征物：" + mFeatureArray.get(position).getTitle());

        dialog_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mFeatureArray.get(position).setHas_question(true);
                    layoutQuestion.setVisibility(View.VISIBLE);
                    layoutAnswer.setVisibility(View.VISIBLE);
                } else {
                    mFeatureArray.get(position).setHas_question(false);
                    layoutQuestion.setVisibility(View.GONE);
                    layoutAnswer.setVisibility(View.GONE);
                }
            }
        });

        builder.setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mFeatureArray.get(position).setHint(editText.getText().toString());
                        if (mFeatureArray.get(position).getHas_question()) {
                            if (editQuestion.getText().toString().equals("") || editAnswer.getText().toString().equals("")) {
                                Toast.makeText(OrienteeringSponsorActivity.this, "请编辑问题和答案", Toast.LENGTH_SHORT).show();
                            } else {
                                mFeatureArray.get(position).setQuestion(editQuestion.getText().toString());
                                mFeatureArray.get(position).setAnswer(editAnswer.getText().toString());
                                featuresAdapter.notifyDataSetChanged();
                                dialogInterface.dismiss();
                            }
                        } else {
                            featuresAdapter.notifyDataSetChanged();
                            dialogInterface.dismiss();
                        }
                    }
                })
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mFeatureArray.remove(position);
                        featuresAdapter.notifyDataSetChanged();
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    private class SubmitTask extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private ProgressDialog pd;

        SubmitTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(context, "正在提交", "请稍后...");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (isNew) return OrienteeringServer.getInstance().submitActivity(mFeatureArray, id, pwd);
            else return OrienteeringServer.getInstance().updateActivity(mFeatureArray, id);
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            pd.cancel();
            if (isSuccess) {
                Toast.makeText(context, "提交成功", Toast.LENGTH_SHORT).show();
                OrienteeringSponsorActivity.this.finish();
            } else {
                Toast.makeText(context, "提交失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
