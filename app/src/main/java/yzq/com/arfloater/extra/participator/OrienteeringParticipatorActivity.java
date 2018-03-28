package yzq.com.arfloater.extra.participator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;

import java.text.DecimalFormat;
import java.util.List;

import yzq.com.arfloater.R;
import yzq.com.arfloater.been.Feature;
import yzq.com.arfloater.camera.DetectorActivity;
import yzq.com.arfloater.message.LocationHelper;
import yzq.com.arfloater.server.OrienteeringServer;

public class OrienteeringParticipatorActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private List<Feature> mFeatures;
    private boolean[] checkList;
    private int location;
    private Button btnCheck;
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
        location = i;
        btnCheck = (Button) view;
        Intent intent = new Intent(OrienteeringParticipatorActivity.this, DetectorActivity.class);
        intent.putExtra("REQUEST_CODE", REQUEST_CODE);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "Fail to get place", Toast.LENGTH_SHORT).show();
        } else {
            String title = data.getStringExtra("title");
            if (title.equals(mFeatures.get(location).getTitle())) {
                Toast.makeText(this, "验证成功", Toast.LENGTH_SHORT).show();
                btnCheck.setVisibility(View.GONE);
                checkList[location] = true;
            } else {
                Toast.makeText(this, "验证失败", Toast.LENGTH_SHORT).show();
            }
        }
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
