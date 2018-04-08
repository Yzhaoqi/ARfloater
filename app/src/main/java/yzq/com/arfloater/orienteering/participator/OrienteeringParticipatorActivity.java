package yzq.com.arfloater.orienteering.participator;

import android.content.DialogInterface;
import android.content.Intent;
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

import java.text.DecimalFormat;
import java.util.List;

import yzq.com.arfloater.R;
import yzq.com.arfloater.been.Feature;
import yzq.com.arfloater.camera.DetectorActivity;
import yzq.com.arfloater.location.LocationHelper;
import yzq.com.arfloater.server.OrienteeringServer;

public class OrienteeringParticipatorActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private List<Feature> mFeatures;
    private boolean[] checkList;
    private int position;
    private FloatingActionButton btnCheck;
    private LocationHelper locationHelper;
    private RecyclerView rv;
    private ActivityAdapter activityAdapter;

    private static DecimalFormat df = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orienteering_participator);

        mFeatures = OrienteeringServer.getInstance().getFeatureList();
        checkList = new boolean[mFeatures.size()];
        locationHelper = LocationHelper.getInstance(this);
        locationHelper.registerListener();

        rv = findViewById(R.id.rv_activity);
        rv.setLayoutManager(new LinearLayoutManager(this));
        activityAdapter = new ActivityAdapter(mFeatures, this);
        rv.setAdapter(activityAdapter);

        for (int i = 0; i < mFeatures.size(); i++)
            checkList[i] = false;
    }

    public void checkIsSame(View view, int i) {
        BDLocation bdLocation = locationHelper.getLocation();
        double la = bdLocation.getLatitude();
        la = Double.parseDouble(df.format(la));
        double lo = bdLocation.getLongitude();
        lo = Double.parseDouble(df.format(lo));
        Feature f = mFeatures.get(i);
        if (la != f.getLatitude() && lo != f.getLongitude()) {
            Toast.makeText(this, "位置错误", Toast.LENGTH_SHORT).show();
            return;
        }
        position = i;
        btnCheck = (FloatingActionButton) view;
        Intent intent = new Intent(OrienteeringParticipatorActivity.this, DetectorActivity.class);
        intent.putExtra("REQUEST_CODE", REQUEST_CODE);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "图像识别出错，请重试", Toast.LENGTH_SHORT).show();
        } else {
            String title = data.getStringExtra("title");
            if (title.equals(mFeatures.get(position).getTitle())) {
                if (mFeatures.get(position).getHas_question()) {
                    showQuestionDialog();
                } else {
                    Toast.makeText(this, "验证成功", Toast.LENGTH_SHORT).show();
                    btnCheck.setBackgroundResource(R.mipmap.icon_float_button_checked);
                    btnCheck.setClickable(false);
                    checkList[position] = true;
                }
            } else {
                Toast.makeText(this, "验证失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showQuestionDialog() {
        final Feature feature = mFeatures.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_feature, null);
        final EditText editAnswer = view.findViewById(R.id.dialog_answer);
        TextView textView = view.findViewById(R.id.dialog_question);

        textView.setText(feature.getQuestion());

        builder.setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (editAnswer.getText().toString().equals(feature.getAnswer())) {
                            Toast.makeText(OrienteeringParticipatorActivity.this, "回答正确", Toast.LENGTH_SHORT).show();
                            btnCheck.setBackgroundResource(R.mipmap.icon_float_button_checked);
                            btnCheck.setClickable(false);
                            checkList[position] = true;
                        } else {
                            Toast.makeText(OrienteeringParticipatorActivity.this, "回答错误", Toast.LENGTH_SHORT).show();
                        }
                        dialogInterface.dismiss();
                    }
                })
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
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
                boolean allComplete = true;
                for (int i = 0; i < mFeatures.size(); i++)
                    allComplete = allComplete & checkList[i];
                if (allComplete) Toast.makeText(this, "恭喜完成所有项目", Toast.LENGTH_SHORT).show();
                else Toast.makeText(this, "还有未完成的项目", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
}
