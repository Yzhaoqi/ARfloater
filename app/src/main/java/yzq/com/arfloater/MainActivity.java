package yzq.com.arfloater;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import yzq.com.arfloater.been.Feature;
import yzq.com.arfloater.camera.DetectorActivity;
import yzq.com.arfloater.extra.participator.OrienteeringParticipatorActivity;
import yzq.com.arfloater.extra.sponsor.OrienteeringSponsorActivity;
import yzq.com.arfloater.server.OrienteeringServer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private Button camera, btn_extra, btn_next;
    private LinearLayout layout_extra;
    private TextInputLayout layout_par;
    private EditText edit_id;
    private RadioGroup rg_extra;
    private boolean is_sponsor = true;


    private static final int PERMISSIONS_REQUEST = 1;
    private static final String PERMISSION_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String PERMISSION_PHONE = Manifest.permission.READ_PHONE_STATE;
    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    private static final String PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera = findViewById(R.id.open_camera);
        btn_extra = findViewById(R.id.btn_extra);
        btn_next = findViewById(R.id.btn_next);
        layout_extra = findViewById(R.id.layout_extra);
        layout_par = findViewById(R.id.layout_par);
        edit_id = findViewById(R.id.edit_id);
        rg_extra = findViewById(R.id.rg_extra);

        if (!hasPermission()) {
            requestPermission();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        camera.setOnClickListener(this);
        btn_extra.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        rg_extra.setOnCheckedChangeListener(this);
    }

    @Override
    public void onRequestPermissionsResult(
            final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                    && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
            } else {
                requestPermission();
            }
        }
    }

    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(PERMISSION_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(PERMISSION_PHONE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(PERMISSION_STORAGE) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(PERMISSION_COARSE_LOCATION) ||
                    shouldShowRequestPermissionRationale(PERMISSION_PHONE) ||
                    shouldShowRequestPermissionRationale(PERMISSION_CAMERA) ||
                    shouldShowRequestPermissionRationale(PERMISSION_STORAGE)) {
            }
            requestPermissions(new String[] {PERMISSION_COARSE_LOCATION, PERMISSION_PHONE, PERMISSION_CAMERA, PERMISSION_STORAGE, }, PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_camera:
                Intent intent = new Intent(MainActivity.this, DetectorActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_extra:
                if (layout_extra.getVisibility() == View.GONE) {
                    layout_extra.setVisibility(View.VISIBLE);
                } else {
                    layout_extra.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_next:
                if (is_sponsor) new OrienteeringDealTask(OrienteeringDealTask.GET_ID, this).execute();
                else new OrienteeringDealTask(OrienteeringDealTask.GET_ACTIVITY, this).execute();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.rbtn_sponsor) {
            layout_par.setVisibility(View.GONE);
            is_sponsor = true;
        } else if (checkedId == R.id.rbtn_participator) {
            layout_par.setVisibility(View.VISIBLE);
            is_sponsor = false;
        }
    }

    private class OrienteeringDealTask extends AsyncTask<Void, Void, Void> {
        public static final int GET_ID = 0;
        public static final int GET_ACTIVITY = 1;

        private Context context;
        private ProgressDialog pd;
        private int mode;
        private String id;
        private boolean is_success;

        OrienteeringDealTask(int mode, Context context) {
            this.mode = mode;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mode == GET_ID) {
                pd = ProgressDialog.show(context, "获取id中", "请稍后...");
            } else {
                pd = ProgressDialog.show(context, "获取活动中", "请稍后...");
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (mode == GET_ID) {
                id =  OrienteeringServer.getInstance().getId();
            } else {
                id = edit_id.getText().toString();
                is_success = OrienteeringServer.getInstance().getActivity(id);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            pd.cancel();
            if (mode == GET_ID) {
                if (id != OrienteeringServer.ERROR) {
                    Intent intent = new Intent(MainActivity.this, OrienteeringSponsorActivity.class);
                    intent.putExtra("ID", id);
                    startActivity(intent);
                } else {
                    Toast.makeText(context, "Fail to get id", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (is_success) {
                    Intent intent = new Intent(MainActivity.this, OrienteeringParticipatorActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(context, "Fail to get Activity", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
