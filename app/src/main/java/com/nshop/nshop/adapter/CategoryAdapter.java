package com.nshop.nshop.adapter;

import android.content.Context;

import java.util.List;

import com.nshop.nshop.R;
import com.nshop.nshop.bean.Category;

/**
 */
public class CategoryAdapter extends SimpleAdapter<Category> {


    public CategoryAdapter(Context context, List<Category> datas) {
        super(context, R.layout.template_single_text, datas);
    }

    @Override
    protected void convert(BaseViewHolder viewHoder, Category item) {


        viewHoder.getTextView(R.id.textView).setText(item.getName());

    }
}
