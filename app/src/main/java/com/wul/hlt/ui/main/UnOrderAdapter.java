package com.wul.hlt.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.wul.hlt.R;
import com.wul.hlt.entity.UnOrderBo;
import com.wul.hlt.ui.orderdetails.OrderDetailsActivity;

import java.util.List;

/**
 * Created by wuliang on 2018/12/13.
 * <p>
 * 待接单的订单适配器
 */

public class UnOrderAdapter extends RecyclerView.Adapter<UnOrderAdapter.ViewHolder> {

    private Context context;
    private List<UnOrderBo.GreengrocerUnOrderListBean> datas;


    public UnOrderAdapter(Context context, List<UnOrderBo.GreengrocerUnOrderListBean> datas) {
        this.context = context;
        this.datas = datas;
    }


    public void setDatas(List<UnOrderBo.GreengrocerUnOrderListBean> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.shopName.setText(datas.get(position).getDeliverRestName());
        holder.shopAddress.setText(datas.get(position).getDeliverAddress());
        holder.orderPrice.setText("¥ " + datas.get(position).getAmount());
        holder.itemLayout.setTag(datas.get(position));
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UnOrderBo.GreengrocerUnOrderListBean data = (UnOrderBo.GreengrocerUnOrderListBean) v.getTag();
                Bundle bundle = new Bundle();
                bundle.putInt("id", data.getId());
                bundle.putInt("statusId", 0);
                Intent intent = new Intent(context, OrderDetailsActivity.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        UnOrderBo.GreengrocerUnOrderListBean item = datas.get(position);
        long allTime = item.getEndTime() - item.getCreateDate();    //倒计时总时间
        long resdulTime = item.getEndTime() - System.currentTimeMillis();   //剩余时间
        if (resdulTime <= 0) {    //当前订单结束
            if (ondetele != null) {
                ondetele.delete(position);
            }
        } else {
            double progress = (double) resdulTime / allTime * 100;
            holder.circleProgressBar.setProgress((int) progress);
            long minture = resdulTime / 1000 / 60;    //分钟
            long scond = resdulTime / 1000 % 60;
            if (minture < 1) {
                holder.timeText.setText("00:" + (scond < 10 ? "0" + scond : scond));
            } else if (minture < 10) {
                holder.timeText.setText("0" + minture + ":" + (scond < 10 ? "0" + scond : scond));
            } else {
                holder.timeText.setText(minture + ":" + (scond < 10 ? "0" + scond : scond));
            }
        }
    }


    @Override
    public int getItemCount() {
        return datas.size();
    }

    private onDetele ondetele;

    public void setOndetele(onDetele detele) {
        this.ondetele = detele;
    }

    public interface onDetele {

        void delete(int position);

    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView shopName;
        private TextView shopAddress;
        private TextView orderPrice;
        private TextView orderMore;
        private CircleProgressBar circleProgressBar;
        private TextView timeText;
        private RelativeLayout itemLayout;


        ViewHolder(View itemView) {
            super(itemView);
            shopName = (TextView) itemView.findViewById(R.id.shop_name);
            shopAddress = (TextView) itemView.findViewById(R.id.shop_address);
            orderPrice = (TextView) itemView.findViewById(R.id.order_price);
            orderMore = (TextView) itemView.findViewById(R.id.order_more);
            circleProgressBar = (CircleProgressBar) itemView.findViewById(R.id.order_progress);
            timeText = (TextView) itemView.findViewById(R.id.time_text);
            itemLayout = (RelativeLayout) itemView.findViewById(R.id.item_layout);
            circleProgressBar.setProgressFormatter(null);
            circleProgressBar.setMax(100);
        }
    }

}
