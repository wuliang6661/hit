package com.wul.hlt.ui.main;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wul.hlt.R;
import com.wul.hlt.entity.HistoryOrderBo;
import com.wul.hlt.entity.UnOrderBo;
import com.wul.hlt.mvp.MVPBaseActivity;
import com.wul.hlt.ui.orderdetails.OrderDetailsActivity;
import com.wul.hlt.util.AppManager;
import com.wul.hlt.util.TimeUtils;
import com.wul.hlt.widget.MediaListener;
import com.wul.hlt.widget.lgrecycleadapter.LGRecycleViewAdapter;
import com.wul.hlt.widget.lgrecycleadapter.LGViewHolder;

import java.util.Timer;
import java.util.TimerTask;


/**
 * MVPPlugin
 */

public class MainActivity extends MVPBaseActivity<MainContract.View, MainPresenter> implements MainContract.View {

    private static final long updateTime = 5000;   //5秒请求一次

    ImageView gonggaoImg;
    TextView gonggaoText;
    LinearLayout gonggaoLayout;
    RecyclerView orderList;
    TextView allPrice;
    RecyclerView lishiOrder;
    View lineLishi;
    LinearLayout lishiOrderTitle;
    LinearLayout noOrderLayout;
//    NestedScrollView scrollView;

    private HistoryOrderBo historyOrderBo;

    private int lastOrderId;

    private UnOrderAdapter adapter;
    private Timer timer;
    boolean isFirst = true;   //是否第一次获取待接单

    private MediaPlayer mediaPlayer;
    private boolean unOrderBoIsNull = false;   //上次列表为空

    @Override
    protected int getLayout() {
        return R.layout.act_main;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        mediaPlayer = MediaPlayer.create(this, R.raw.order_music);

        mPresenter.getUnOrderList();
        handler.sendEmptyMessageDelayed(0, updateTime);

        if (ActivityCompat.checkSelfPermission(AppManager.getAppManager().curremtActivity(), Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AppManager.getAppManager().curremtActivity(),
                    new String[]{
                            Manifest.permission.CALL_PHONE
                    }, 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.getHistoryOrderList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        // mediaPlayer.stop();
        //  mediaPlayer.release();
        if (timer != null) {
            timer.cancel();
        }
    }

    /**
     * 初始化布局
     */
    private void initView() {
        gonggaoImg = (ImageView) findViewById(R.id.gonggao_img);
        gonggaoText = (TextView) findViewById(R.id.gonggao_text);
        gonggaoLayout = (LinearLayout) findViewById(R.id.gonggao_layout);
        orderList = (RecyclerView) findViewById(R.id.order_list);
        allPrice = (TextView) findViewById(R.id.all_price);
        lishiOrder = (RecyclerView) findViewById(R.id.lishi_order);
        lishiOrderTitle = (LinearLayout) findViewById(R.id.lishi_order_title);
        lineLishi = findViewById(R.id.line_lishi);
        noOrderLayout = (LinearLayout) findViewById(R.id.no_order_layout);
//        scrollView = (NestedScrollView) findViewById(R.id.scrollView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        orderList.setLayoutManager(manager);
        orderList.setNestedScrollingEnabled(false);
        LinearLayoutManager manager1 = new LinearLayoutManager(this);
        manager1.setOrientation(LinearLayoutManager.VERTICAL);
        lishiOrder.setLayoutManager(manager1);
//        lishiOrder.setNestedScrollingEnabled(false);
    }


    @Override
    public void onRequestError(String msg) {
        showToast(msg);
    }

    @Override
    public void onRequestEnd() {

    }

    @Override
    public void getHistoryOrderList(HistoryOrderBo historyOrderBo) {
        this.historyOrderBo = historyOrderBo;
        if (historyOrderBo.getGreengrocerHistoryOrderList() == null || historyOrderBo.getGreengrocerHistoryOrderList().size() == 0) {
            lishiOrder.setVisibility(View.GONE);
            lineLishi.setVisibility(View.GONE);
            lishiOrderTitle.setVisibility(View.GONE);
        } else {
            lishiOrder.setVisibility(View.VISIBLE);
            lineLishi.setVisibility(View.VISIBLE);
            lishiOrderTitle.setVisibility(View.VISIBLE);
            allPrice.setText("¥ " + historyOrderBo.getACountMoneyTotal());
            setHistoryAdapter();
        }
    }

    @Override
    public void getUnOrderList(final UnOrderBo unOrderBo) {
        mPresenter.getHistoryOrderList();
        if (unOrderBo.getGreengrocerUnOrderList() == null || unOrderBo.getGreengrocerUnOrderList().size() == 0) {
            if (!unOrderBoIsNull) {      //如果上次列表不为空
                noOrderLayout.setVisibility(View.VISIBLE);
                orderList.setVisibility(View.GONE);
                gonggaoLayout.setVisibility(View.VISIBLE);
                gonggaoImg.setImageResource(R.drawable.gonggao_blue);
                gonggaoText.setTextColor(Color.parseColor("#6EC71E"));
                gonggaoText.setText("暂无新订单，请留意！");
                unOrderBoIsNull = true;   //为空了
                isFirst = false;
                lastOrderId = 0;
                if (timer != null) {
                    timer.cancel();
                }
            }
        } else {
            noOrderLayout.setVisibility(View.GONE);
            orderList.setVisibility(View.VISIBLE);
            int size = unOrderBo.getGreengrocerUnOrderList().size();
            if (isFirst) {   //第一次获取数据
                lastOrderId = unOrderBo.getGreengrocerUnOrderList().get(size - 1).getId();
            } else {
                int id = unOrderBo.getGreengrocerUnOrderList().get(size - 1).getId();
                if (id > lastOrderId) {   //有新的订单，播放声音
                    gonggaoLayout.setVisibility(View.VISIBLE);
                    lastOrderId = id;
                    mediaPlayer.start();
                    mediaPlayer.setOnCompletionListener(new MediaListener());
                    gonggaoImg.setImageResource(R.drawable.gonggao_red);
                    gonggaoText.setTextColor(Color.parseColor("#f5142f"));
                    gonggaoText.setText("您有一笔新的订单啦，请赶紧查看！");
                }
            }
            adapter = new UnOrderAdapter(this, unOrderBo.getGreengrocerUnOrderList());
            adapter.setOndetele(new UnOrderAdapter.onDetele() {
                @SuppressLint("HandlerLeak")
                @Override
                public void delete(int position) {
                    showDialog();
                }
            });
            orderList.setAdapter(adapter);
            if (unOrderBoIsNull && timer != null) {
                startTimer();
            }
            if (timer == null) {
                startTimer();
            }
            unOrderBoIsNull = false;    //上次列表不为空
        }
        isFirst = false;
    }

    /**
     * 启动倒计时
     */
    private void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler1.sendEmptyMessage(0x11);
            }
        }, 0, 1000);
    }


    AlertDialog dialog;

    /**
     * 显示弹窗
     */
    private void showDialog() {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_message, null);
        dialog = new AlertDialog.Builder(this).setView(view).create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mPresenter.getUnOrderList();
            }
        });
        dialog.show();
        handler1.sendEmptyMessageDelayed(0x22, 3000);
    }


    @SuppressLint("HandlerLeak")
    Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x11:
                    adapter.notifyDataSetChanged();
                    break;
                case 0x22:
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    break;
            }
        }
    };


    /**
     * 设置历史订单适配器
     */
    private void setHistoryAdapter() {
        LGRecycleViewAdapter<HistoryOrderBo.GreengrocerHistoryOrderListBean> adapter =
                new LGRecycleViewAdapter<HistoryOrderBo.GreengrocerHistoryOrderListBean>(historyOrderBo.getGreengrocerHistoryOrderList()) {
                    @Override
                    public int getLayoutId(int viewType) {
                        return R.layout.item_order_lishi;
                    }

                    @Override
                    public void convert(LGViewHolder holder, HistoryOrderBo.GreengrocerHistoryOrderListBean orderBO, int position) {
                        switch (orderBO.getStatusId()) {
                            case 0:  //待接单
                                holder.setText(R.id.order_type, "待接单");
                                holder.setText(R.id.stop_time, "终止时间：" + TimeUtils.millis2String(orderBO.getUpdateDate(),
                                        "yyyy年MM月dd HH:mm:ss"));
                                break;
                            case 1:  //已接单
                                holder.setText(R.id.order_type, "已接单");
                                holder.setText(R.id.stop_time, "接单时间：" + TimeUtils.millis2String(orderBO.getUpdateDate(),
                                        "yyyy年MM月dd HH:mm:ss"));
                                break;
                            case 2:   //已完成
                                holder.setText(R.id.order_type, "已完成");
                                holder.setText(R.id.stop_time, "完成时间：" + TimeUtils.millis2String(orderBO.getUpdateDate(),
                                        "yyyy年MM月dd HH:mm:ss"));
                                break;
                            case 3:    //已终止
                                holder.setText(R.id.order_type, "已终止");
                                holder.setText(R.id.stop_time, "终止时间：" + TimeUtils.millis2String(orderBO.getUpdateDate(),
                                        "yyyy年MM月dd HH:mm:ss"));
                                break;
                        }
                        holder.setText(R.id.order_price, "¥：" + orderBO.getAmount());
                        if (orderBO.getProductDetailList().size() >= 1) {
                            holder.setImageUrl(MainActivity.this, R.id.shop_img1, orderBO.getProductDetailList().get(0).getImg());
                        }
                        if (orderBO.getProductDetailList().size() >= 2) {
                            holder.setImageUrl(MainActivity.this, R.id.shop_img2, orderBO.getProductDetailList().get(1).getImg());
                        }
                        if (orderBO.getProductDetailList().size() >= 3) {
                            holder.setImageUrl(MainActivity.this, R.id.shop_img3, orderBO.getProductDetailList().get(2).getImg());
                        }
                        if (orderBO.getProductDetailList().size() >= 4) {
                            holder.setImageUrl(MainActivity.this, R.id.shop_img4, orderBO.getProductDetailList().get(3).getImg());
                        }
                    }
                };
        adapter.setOnItemClickListener(R.id.item_layout, new LGRecycleViewAdapter.ItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putInt("id", historyOrderBo.getGreengrocerHistoryOrderList().get(position).getId());
                bundle.putInt("statusId", historyOrderBo.getGreengrocerHistoryOrderList().get(position).getStatusId());
                gotoActivity(OrderDetailsActivity.class, bundle, false);
            }
        });
        lishiOrder.setAdapter(adapter);
    }


    /**
     * 循环请求订单
     */
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            mPresenter.getUnOrderList();
            sendEmptyMessageDelayed(0, updateTime);
        }
    };


    //记录用户首次点击返回键的时间
    private long firstTime = 0;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {
                    showToast("再按一次退出程序");
                    firstTime = secondTime;
                    return true;
                } else {
                    AppManager.getAppManager().finishAllActivity();
                    System.exit(0);
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

}
