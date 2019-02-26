package com.wul.hlt.ui.orderdetails;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.wul.hlt.R;
import com.wul.hlt.entity.OrderDetails;
import com.wul.hlt.mvp.MVPBaseActivity;
import com.wul.hlt.util.PhoneUtils;
import com.wul.hlt.util.TimeUtils;
import com.wul.hlt.widget.AlertDialog;
import com.wul.hlt.widget.lgrecycleadapter.LGRecycleViewAdapter;
import com.wul.hlt.widget.lgrecycleadapter.LGViewHolder;

import java.util.Timer;
import java.util.TimerTask;


/**
 * MVPPlugin
 * 订单详情页面
 */

public class OrderDetailsActivity extends MVPBaseActivity<OrderDetailsContract.View, OrderDetailsPresenter>
        implements OrderDetailsContract.View, View.OnClickListener {

    ImageView back;    //返回按钮
    TextView orderTaking;   //接单
    TextView allPriceButtom;    //底部总订单金额显示
    TextView allPrice;       //待接单时 订单总金额显示
    CircleProgressBar downTime;    //倒计时
    TextView timeText;
    LinearLayout unorderTitle;      //倒计时布局
    TextView orderId;           //订单编号
    TextView orderType;         //订单类型
    TextView payType;         //是否支付
    TextView takeGoodsPerson;    //收货人
    TextView takeShopName;       //收货店名
    TextView takeAddress;        //收货地址
    TextView customerPhone;       //客户电话
    TextView takeTime;        //   配送时间
    TextView orderTime;        //下单时间
    RecyclerView goodList;      //采购清单
    TextView shopId;        //门店编号
    RelativeLayout allPriceLayout;   //底部订单布局

    private int strOrderId;
    private int statusId;

    private OrderDetails orderDetails;
    Timer timer;

    @Override
    protected int getLayout() {
        return R.layout.act_order_details;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        strOrderId = bundle.getInt("id");
        statusId = bundle.getInt("statusId");

        initView();
        initMassage();
        mPresenter.getOrderDetails(strOrderId + "");
    }

    /**
     * 初始化布局
     */
    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        orderTaking = (TextView) findViewById(R.id.order_taking);
        allPriceButtom = (TextView) findViewById(R.id.all_price_buttom);
        allPrice = (TextView) findViewById(R.id.all_price);
        downTime = (CircleProgressBar) findViewById(R.id.order_progress);
        timeText = (TextView) findViewById(R.id.time_text);
        unorderTitle = (LinearLayout) findViewById(R.id.unorder_title);
        orderId = (TextView) findViewById(R.id.order_id);
        orderType = (TextView) findViewById(R.id.order_type);
        payType = (TextView) findViewById(R.id.pay_type);
        takeGoodsPerson = (TextView) findViewById(R.id.take_goods_person);
        takeShopName = (TextView) findViewById(R.id.take_shop_name);
        takeAddress = (TextView) findViewById(R.id.take_address);
        customerPhone = (TextView) findViewById(R.id.customer_phone);
        shopId = (TextView) findViewById(R.id.shop_id);
        takeTime = (TextView) findViewById(R.id.take_time);
        orderTime = (TextView) findViewById(R.id.order_time);
        goodList = (RecyclerView) findViewById(R.id.good_list);
        allPriceLayout = (RelativeLayout) findViewById(R.id.all_price_layout);
        downTime.setProgressFormatter(null);
    }

    /**
     * 初始化Recycle
     */
    private void initMassage() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        goodList.setLayoutManager(manager);
        goodList.setNestedScrollingEnabled(false);
        back.setOnClickListener(this);
        orderTaking.setOnClickListener(this);
        customerPhone.setOnClickListener(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public void onRequestError(String msg) {
        showToast(msg);
    }

    @Override
    public void onRequestEnd() {

    }

    @Override
    public void getOrderDetails(OrderDetails orderDetails) {
        this.orderDetails = orderDetails;
        orderId.setText("订单编号：" + orderDetails.getId());
        takeGoodsPerson.setText("收货人：" + orderDetails.getConsigneeName());
        takeShopName.setText("收货店名：" + orderDetails.getDeliverRestName());
        takeAddress.setText("收货地址：" + orderDetails.getDeliverAddress());
        customerPhone.setText(orderDetails.getConsigneePhone());
        orderTime.setText(TimeUtils.millis2String(orderDetails.getCreateDate(), "yyyy年MM月dd HH:mm:ss"));
        allPrice.setText("¥ " + orderDetails.getAmount());
        allPriceButtom.setText("¥ " + orderDetails.getAmount());
        switch (orderDetails.getPayStatus()) {
            case 0:
                payType.setText("未支付");
                payType.setTextColor(Color.parseColor("#f5142f"));
                break;
            case 1:
                payType.setText("已支付");
                payType.setTextColor(ContextCompat.getColor(OrderDetailsActivity.this, R.color.zhu_color));
                break;
        }
        unorderTitle.setVisibility(View.GONE);
        allPriceLayout.setVisibility(View.VISIBLE);
        orderType.setTextColor(ContextCompat.getColor(OrderDetailsActivity.this, R.color.zhu_color));
        switch (orderDetails.getStatusId()) {
            case 0:  //待接单
                orderType.setText("待接单");
                unorderTitle.setVisibility(View.VISIBLE);
                orderTaking.setVisibility(View.VISIBLE);
                allPriceLayout.setVisibility(View.GONE);
                orderType.setTextColor(Color.parseColor("#f5142f"));
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(0x11);
                    }
                }, 0, 1000);
                break;
            case 1:  //已接单
                orderType.setText("已接单");
                break;
            case 2:   //已完成
                orderType.setText("已完成");
                break;
            case 3:    //已终止
                orderType.setText("已终止");
                break;
        }
        setAdapter();
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            long allTime = orderDetails.getEndTime() - orderDetails.getCreateDate();    //倒计时总时间
            long resdulTime = orderDetails.getEndTime() - System.currentTimeMillis();   //剩余时间
            if (resdulTime > 0) {    //当前订单结束
                double progress = (double) resdulTime / allTime * 100;
                downTime.setProgress((int) progress);
                long minture = resdulTime / 1000 / 60;    //分钟
                long scond = resdulTime / 1000 % 60;
                if (minture < 1) {
                    timeText.setText("00:" + (scond < 10 ? "0" + scond : scond));
                } else if (minture < 10) {
                    timeText.setText("0" + minture + ":" + (scond < 10 ? "0" + scond : scond));
                } else {
                    timeText.setText(minture + ":" + (scond < 10 ? "0" + scond : scond));
                }
            }
        }
    };


    @Override
    public void takingOrderSuress() {
        showToast("接单成功！");
        mPresenter.getOrderDetails(strOrderId + "");
    }


    /**
     * 设置适配器
     */
    private void setAdapter() {
        LGRecycleViewAdapter<OrderDetails.ProductDetailListBean> adapter =
                new LGRecycleViewAdapter<OrderDetails.ProductDetailListBean>(orderDetails.getProductDetailList()) {
                    @Override
                    public int getLayoutId(int viewType) {
                        return R.layout.item_good;
                    }

                    @Override
                    public void convert(LGViewHolder holder, OrderDetails.ProductDetailListBean productDetailListBean, int position) {
                        holder.setImageUrl(OrderDetailsActivity.this, R.id.good_img, productDetailListBean.getImg());
                        holder.setText(R.id.good_name, productDetailListBean.getProductName());
                        holder.setText(R.id.good_price, "¥ " + productDetailListBean.getPrice1() + "/" +
                                productDetailListBean.getPrice1MeasureUnit());
                        holder.setText(R.id.goods_num, "数量：" + productDetailListBean.getQuantity() +
                                productDetailListBean.getPrice1MeasureUnit());
                        holder.setText(R.id.goods_all_price, "¥ " + productDetailListBean.getAmountOfMoney());
                    }
                };
        goodList.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.order_taking:
                mPresenter.takingOrder(strOrderId + "");
                break;
            case R.id.customer_phone:
                new AlertDialog(this).builder().setGone().setMsg(customerPhone.getText().toString().trim())
                        .setNegativeButton("取消", null)
                        .setPositiveButton("拨打", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                PhoneUtils.callPhone(customerPhone.getText().toString().trim());
                            }
                        }).show();

                break;
        }
    }
}
