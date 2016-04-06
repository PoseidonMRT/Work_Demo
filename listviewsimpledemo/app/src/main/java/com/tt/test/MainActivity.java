package com.tt.test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText mEditText;
    private Button mAddButton;
    private ListView mUserNameListView;
    private List<String> mUsers;
    private UserListAdapter userListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditText = (EditText)findViewById(R.id.username_edittext);
        mAddButton = (Button)findViewById(R.id.add);
        mUserNameListView = (ListView)findViewById(R.id.list_view);
        initData();
    }

    public void initData(){
        mUsers  = new ArrayList<>();
        userListAdapter = new UserListAdapter(this,mUsers);
        mUserNameListView.setAdapter(userListAdapter);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = mEditText.getText().toString();
                mUsers.add(str);
                userListAdapter.notifyDataSetChanged();
            }
        });
        mUserNameListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteDialog(position);
                return true;
            }
        });
        mUserNameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("点击的事"+mUsers.get(position));
                builder.setTitle("提示");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }

    public void showDeleteDialog(final int pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确认删除该条姓名记录吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mUsers.remove(pos);
                userListAdapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
