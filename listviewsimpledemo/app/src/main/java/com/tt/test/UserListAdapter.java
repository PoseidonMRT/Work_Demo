package com.tt.test;

import android.content.Context;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by TuoZhaoBing on 2016/4/6 0006.
 */
public class UserListAdapter extends BaseAdapter {
    public static final String TAG = "UserListAdapter";
    private LayoutInflater inflater;
    private Context mContext;
    List<String> mUsers;

    public UserListAdapter(Context context, List<String> ItemInfoList) {

        this.mUsers = ItemInfoList;
        this.mContext = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    static class ViewHolder {
        TextView name;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mUsers.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;

        if (holder == null) {
            convertView = inflater.inflate(R.layout.item, null);
            holder = new ViewHolder();

            holder.name = (TextView) convertView
                    .findViewById(R.id.textview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String item = (String) mUsers.get(position);
        holder.name.setText(item);
        return convertView;
    }

}
