package yzq.com.arfloater;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
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

import yzq.com.arfloater.camera.DetectorActivity;
import yzq.com.arfloater.orienteering.participator.OrienteeringParticipatorActivity;
import yzq.com.arfloater.orienteering.sponsor.OrienteeringSponsorActivity;
import yzq.com.arfloater.server.OrienteeringServer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private Button camera, btn_extra, btn_next;
    private LinearLayout layout_extra;
    private TextInputLayout layout_par,layout_sor_new, layout_sor_update_id, layout_sor_update_pwd;
    private EditText edit_id, edit_pwd, edit_update_id, edit_update_pwd;
    private RadioGroup rg_extra, rg_sponsor_choose;
    private boolean is_sponsor = true;
    private boolean is_new = true;


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
        layout_sor_new = findViewById(R.id.layout_sor_new);
        layout_sor_update_id = findViewById(R.id.layout_sor_update_id);
        layout_sor_update_pwd = findViewById(R.id.layout_sor_update_pwd);
        edit_id = findViewById(R.id.edit_id);
        edit_pwd = findViewById(R.id.edit_pwd);
        edit_update_id = findViewById(R.id.edit_update_id);
        edit_update_pwd = findViewById(R.id.edit_update_pwd);
        rg_extra = findViewById(R.id.rg_extra);
        rg_sponsor_choose = findViewById(R.id.rg_sponsor_choose);

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
        rg_sponsor_choose.setOnCheckedChangeListener(this);
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
                if (is_sponsor) {
                    if (is_new) {
                        String pwd = edit_pwd.getText().toString();
                        if (pwd.equals("")) {
                            Toast.makeText(this, "请输入活动密码", Toast.LENGTH_SHORT).show();
                        } else {
                            new GetNewActivityTask(this, pwd).execute();
                        }
                    } else {
                        new CheckPasswordTask(this).execute();
                    }
                } else {
                    new GetActivityTask(false, this).execute();
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.rbtn_sponsor) {
            layout_par.setVisibility(View.GONE);
            rg_sponsor_choose.setVisibility(View.VISIBLE);
            if (is_new) {
                layout_sor_new.setVisibility(View.VISIBLE);
                layout_sor_update_id.setVisibility(View.GONE);
                layout_sor_update_pwd.setVisibility(View.GONE);
            } else {
                layout_sor_new.setVisibility(View.GONE);
                layout_sor_update_id.setVisibility(View.VISIBLE);
                layout_sor_update_pwd.setVisibility(View.VISIBLE);
            }
            is_sponsor = true;
        } else if (checkedId == R.id.rbtn_participator) {
            layout_par.setVisibility(View.VISIBLE);
            layout_sor_new.setVisibility(View.GONE);
            rg_sponsor_choose.setVisibility(View.GONE);
            layout_sor_update_id.setVisibility(View.GONE);
            layout_sor_update_pwd.setVisibility(View.GONE);
            is_sponsor = false;
        } else if (checkedId == R.id.rbtn_new) {
            is_new = true;
            layout_sor_new.setVisibility(View.VISIBLE);
            layout_sor_update_id.setVisibility(View.GONE);
            layout_sor_update_pwd.setVisibility(View.GONE);
        } else if (checkedId == R.id.rbtn_update) {
            is_new = false;
            layout_sor_new.setVisibility(View.GONE);
            layout_sor_update_id.setVisibility(View.VISIBLE);
            layout_sor_update_pwd.setVisibility(View.VISIBLE);
        }
    }

    private class GetActivityTask extends AsyncTask<Void, Void, Void> {
        private Context context;
        private ProgressDialog pd;
        private boolean isSponsor;
        private String id;
        private boolean is_success;

        GetActivityTask(boolean isSponsor, Context context) {
            this.isSponsor = isSponsor;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(context, "获取活动中", "请稍后...");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (isSponsor) id = edit_update_id.getText().toString();
            else id = edit_id.getText().toString();
            is_success = OrienteeringServer.getInstance().getActivity(id);
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            pd.cancel();
            if (is_success) {
                if (isSponsor) {
                    Intent intent = new Intent(MainActivity.this, OrienteeringSponsorActivity.class);
                    intent.putExtra("IS_NEW", false);
                    intent.putExtra("ID", id);
                    intent.putExtra("PASSWORD", edit_update_pwd.getText().toString());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, OrienteeringParticipatorActivity.class);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(context, "Fail to get Activity", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class CheckPasswordTask extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private String id, pwd;

        CheckPasswordTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            id = edit_update_id.getText().toString();
            pwd = edit_update_pwd.getText().toString();
            return OrienteeringServer.getInstance().checkPassword(id, pwd);
        }

        @Override
        protected void onPostExecute(Boolean is_pair) {
            if (is_pair) {
                new GetActivityTask(true, context).execute();
            } else {
                Toast.makeText(context, "获取活动失败，请检查您的活动id或者密码是否输入正确", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetNewActivityTask extends AsyncTask<Void, Void, Void> {

        private Context context;
        private ProgressDialog pd;
        private String id, pwd;

        public GetNewActivityTask(Context context, String pwd) {
            this.context = context;
            this.pwd = pwd;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(context, "获取id中", "请稍后...");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            id =  OrienteeringServer.getInstance().getId();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pd.cancel();
            if (!id.equals(OrienteeringServer.ERROR)) {
                Intent intent = new Intent(MainActivity.this, OrienteeringSponsorActivity.class);
                intent.putExtra("IS_NEW", true);
                intent.putExtra("ID", id);
                intent.putExtra("PASSWORD", pwd);
                startActivity(intent);
            } else {
                Toast.makeText(context, "获取活动失败，请检查网络并重试", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
