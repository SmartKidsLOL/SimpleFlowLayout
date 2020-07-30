package com.wjr.simple_flow_layout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 模拟动态添加Tag的场景
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // 流式布局
    SimpleFlowLayout mFlowLayout;
    // 添加Tag的Item
    View addGoodsView;

    private Random mRandom = new Random();

    // 模拟Tag集合数据
    private List<SimpleTag> mTags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFlowLayout = findViewById(R.id.flow_layout);

        initFlowLayout();
    }

    /**
     * 初始化默认控件，添加默认item
     */
    private void initFlowLayout() {
        // 添加默认的一条
        addGoodsView = LayoutInflater.from(this).inflate(R.layout.item_add_goods, null, false);
        mFlowLayout.addView(addGoodsView);

        addGoodsView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == addGoodsView) {
            // 添加标签
            addItems();
        } else {
            // 删除标签
            delItems(v);
        }
    }

    // 删除标签
    private void delItems(View v) {
        SimpleTag tag = (SimpleTag) v.getTag();
        mTags.remove(tag);
        View parent = (View) v.getParent();
        // 从流式布局中移除此View
        if (parent != null && parent.getParent() != null) {
            mFlowLayout.removeView(parent);
        }
    }

    /**
     * 模拟添加随机Tag
     */
    private void addItems() {
        int addNum = mRandom.nextInt(5);        // 随机添加的item数量

        for (int i = 0; i < addNum; i++) {
            // 构建出一条Tag
            int tagCount = mRandom.nextInt(127);    // 模拟数量
            SimpleTag tag = new SimpleTag();
            tag.setContent("商品：" + (mTags.size() + 1));
            tag.setNum(tagCount);
            mTags.add(tag);
        }

        // 刷新布局
        refreshFlowLayout();
    }

    /**
     * 刷新流式布局
     */
    private void refreshFlowLayout() {
        int size = mTags.size();
        int childCount = mFlowLayout.getChildCount();
        if (size == 0) {
            // 所有子Tag被删除
            if (childCount != 1) {
                // 如果布局里的子View>1，则删除所有子view，留下默认的子Item
                mFlowLayout.removeViews(0, childCount - 1);
            }
        } else {
            // 进行刷新布局，先删除所有Item(Tag)
            mFlowLayout.removeViews(0, childCount - 1);

            // 创建自己的TagView
            int lastIndex = 0;
            for (int i = 0; i < size; i++) {
                // 构建View
                SimpleTag tag = mTags.get(i);
                View itemView = LayoutInflater.from(this).inflate(R.layout.item_exchange_goods, null, false);
                TextView goodNameTv = itemView.findViewById(R.id.goods_name_tv);
                goodNameTv.setText(tag.getContent());
                TextView goodNumTv = itemView.findViewById(R.id.goods_num_tv);
                goodNumTv.setText("x" + tag.getNum());
                ImageView delIv = itemView.findViewById(R.id.del_goods_iv);

                // 绑定删除的点击事件
                delIv.setOnClickListener(this);
                delIv.setTag(tag);

                // 加入到流式布局中，插入至默认Item前的一个下标位
                mFlowLayout.addView(itemView, lastIndex);
                lastIndex = i + 1;
            }
        }
    }
}
