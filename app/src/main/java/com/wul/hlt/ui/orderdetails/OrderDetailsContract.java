package com.wul.hlt.ui.orderdetails;

import com.wul.hlt.entity.OrderDetails;
import com.wul.hlt.mvp.BasePresenter;
import com.wul.hlt.mvp.BaseRequestView;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class OrderDetailsContract {
    interface View extends BaseRequestView {

        void getOrderDetails(OrderDetails orderDetails);

        void takingOrderSuress();
    }

    interface Presenter extends BasePresenter<View> {


        void getOrderDetails(String orderId);

        void takingOrder(String orderId);

    }
}
