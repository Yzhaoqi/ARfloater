package yzq.com.arfloater.message;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import yzq.com.arfloater.R;

/**
 * Created by YZQ on 2018/1/17.
 */

public class LeaveWordsAdapter extends RecyclerView.Adapter<LeaveWordsAdapter.MyViewHolder> {

    private List<String> mDatas;
    private LayoutInflater inflater;

    public LeaveWordsAdapter(List<String> datas) {
        this.mDatas = datas;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leave_word, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String word = mDatas.get(position);

        holder.getLabel().setText("#"+String.valueOf(position));
        holder.getWords().setText(word);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView label, words;

        public MyViewHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.word_label);
            words = (TextView) itemView.findViewById(R.id.word_content);
        }

        public TextView getLabel() {
            return label;
        }

        public TextView getWords() {
            return words;
        }
    }
}
