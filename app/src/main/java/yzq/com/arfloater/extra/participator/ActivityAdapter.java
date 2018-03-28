package yzq.com.arfloater.extra.participator;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import yzq.com.arfloater.R;
import yzq.com.arfloater.been.Feature;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.MyViewHolder>{

    private List<Feature> mFeatures;
    private Context context;
    public ActivityAdapter(List<Feature> mFeatures, Context context) {
        this.context = context;
        this.mFeatures = mFeatures;
    }

    @Override
    public ActivityAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ActivityAdapter.MyViewHolder holder, final int position) {
        Feature f = mFeatures.get(position);

        holder.getTitle().setText("特征物："+f.getTitle());
        holder.getHint().setText("提示："+f.getHint());
        holder.getBtn_check().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((OrienteeringParticipatorActivity)context).checkIsSame(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFeatures.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title, hint;
        private Button btn_check;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.part_title);
            hint = itemView.findViewById(R.id.part_hint);
            btn_check = itemView.findViewById(R.id.btn_check);
        }

        public TextView getHint() {
            return hint;
        }

        public TextView getTitle() {
            return title;
        }

        public Button getBtn_check() {
            return btn_check;
        }
    }
}
