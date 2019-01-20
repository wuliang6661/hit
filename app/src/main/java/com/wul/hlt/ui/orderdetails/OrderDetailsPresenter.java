package com.wul.hlt.ui.orderdetails;

import com.wul.hlt.api.HttpServiceIml;
import com.wul.hlt.entity.OrderDetails;
import com.wul.hlt.mvp.BasePresenterImpl;

import rx.Subscriber;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class OrderDetailsPresenter extends BasePresenterImpl<OrderDetailsContract.View>
        implements OrderDetailsContract.Presenter {

    @Override
    public void getOrderDetails(String orderId) {
        HttpServiceIml.getOrderDetails(orderId).subscribe(new Subscriber<OrderDetails>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if (mView != null) {
                    mView.onRequestError(e.getMessage());
                }
            }

            @Override
            public void onNext(OrderDetails s) {
                if (mView != null) {
                    mView.getOrderDetails(s);
                }
            }
        });
    }

    @Override
    public void takingOrder(final String orderId) {
        HttpServiceIml.orderTaking(orderId).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if (mView != null) {
                    getOrderDetails(orderId);
                    mView.onRequestError(e.getMessage());
                }
            }

            @Override
            public void onNext(String s) {
                if (mView != null) {
                    mView.takingOrderSuress();
                }
            }
        });
    }
}
