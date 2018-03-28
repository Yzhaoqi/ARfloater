package yzq.com.arfloater.extra.sponsor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.EditText;
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
    private List<Feature> mFeatureArray = new ArrayList<Feature>();
    private FeaturesAdapter featuresAdapter;
    private String id;

    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) id = bundle.getString("ID");
        setContentView(R.layout.activity_orienteering_sponsor);
        rv = findViewById(R.id.rv_feature);
        fab = findViewById(R.id.fab_add);

        rv.setLayoutManager(new LinearLayoutManager(this));
        featuresAdapter = new FeaturesAdapter(mFeatureArray);
        rv.setAdapter(featuresAdapter);
        featuresAdapter.setOnItemClickListener(this);
        locationHelper = LocationHelper.getInstance(this);
        locationHelper.registerListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        fab.setOnClickListener(this);
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
        final EditText editText = view.findViewById(R.id.dialog_edit);
        TextView textView = view.findViewById(R.id.dialog_title);
        editText.setText(mFeatureArray.get(position).getHint());
        textView.setText("特征物：" + mFeatureArray.get(position).getTitle());

        builder.setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mFeatureArray.get(position).setHint(editText.getText().toString());
                        featuresAdapter.notifyDataSetChanged();
                        dialogInterface.dismiss();
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
            return OrienteeringServer.getInstance().submitActivity(mFeatureArray, id);
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
