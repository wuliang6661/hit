package com.wul.hlt.ui.main;


import com.wul.hlt.entity.HistoryOrderBo;
import com.wul.hlt.entity.UnOrderBo;
import com.wul.hlt.mvp.BasePresenter;
import com.wul.hlt.mvp.BaseRequestView;

/**
 * MVPPlugin
 * �wuliang
 * <p>
 * 主页的业务接口
 */

public class MainContract {
    interface View extends BaseRequestView {

        void getHistoryOrderList(HistoryOrderBo historyOrderBo);

        void getUnOrderList(UnOrderBo unOrderBo);

    }

    interface Presenter extends BasePresenter<View> {


        void getUnOrderList();

        void getHistoryOrderList();
    }
}
