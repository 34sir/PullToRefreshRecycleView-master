package com.example.admin.pulltorefreshrecycleview_master.item;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.pulltorefreshrecycleview_master.R;
import com.example.admin.pulltorefreshrecycleview_master.model.DemoModel;
import com.example.pulltorefreshrecycleview.impl.AdapterItem;

/**
 * Created by admin on 2017/4/12.
 */

public class TextItem implements AdapterItem<DemoModel> {

    @Override
    public int getLayoutResId() {
        return R.layout.demo_item_text;
    }

    TextView textView;
    private int mPosition;


    @Override
    public void bindViews(View root) {
        textView = (TextView) root.findViewById(R.id.textView);
    }

    @Override
    public void setViews() {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), mPosition+"", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void handleData(DemoModel model, int position) {
        mPosition=position;
        textView.setText(model.content + " pos=" + position);
    }

}
