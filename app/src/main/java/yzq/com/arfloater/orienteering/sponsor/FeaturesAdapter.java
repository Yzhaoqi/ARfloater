package yzq.com.arfloater.orienteering.sponsor;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import yzq.com.arfloater.R;
import yzq.com.arfloater.been.Feature;

/**
 * Created by YZQ on 2018/1/17.
 */

public class FeaturesAdapter extends RecyclerView.Adapter<FeaturesAdapter.MyViewHolder> implements View.OnClickListener {

    private List<Feature> mFeatures;
    private OnItemClickListener mOnItemClickListenter = null;
    public FeaturesAdapter(List<Feature> mFeatures) {
        this.mFeatures = mFeatures;
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListenter != null) {
            mOnItemClickListenter.onItemClick(view, (int)view.getTag());
        }
    }

    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListenter = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feature, parent, false);
        itemView.setOnClickListener(this);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Feature f = mFeatures.get(position);

        holder.itemView.setTag(position);
        holder.getTitle().setText("特征物："+f.getTitle());
        holder.getHint().setText("提示："+f.getHint());
        if (f.getHas_question()) {
            holder.getQuestion().setVisibility(View.VISIBLE);
            holder.getAnswer().setVisibility(View.VISIBLE);
            holder.getQuestion().setText("问题："+f.getQuestion());
            holder.getAnswer().setText("答案："+f.getAnswer());
        } else {
            holder.getQuestion().setVisibility(View.GONE);
            holder.getAnswer().setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mFeatures.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title, hint, question, answer;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.sponsor_title);
            hint = itemView.findViewById(R.id.sponsor_hint);
            question = itemView.findViewById(R.id.sponsor_question);
            answer = itemView.findViewById(R.id.sponsor_answer);
        }

        public TextView getHint() {
            return hint;
        }

        public TextView getTitle() {
            return title;
        }

        public TextView getQuestion() {
            return question;
        }

        public TextView getAnswer() {
            return answer;
        }
    }
}
