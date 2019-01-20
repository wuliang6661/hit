package com.wul.hlt.ui.main;


import com.wul.hlt.api.HttpServiceIml;
import com.wul.hlt.entity.HistoryOrderBo;
import com.wul.hlt.entity.UnOrderBo;
import com.wul.hlt.mvp.BasePresenterImpl;

import rx.Subscriber;

/**
 * MVPPlugin
 */

public class MainPresenter extends BasePresenterImpl<MainContract.View> implements MainContract.Presenter {

    @Override
    public void getUnOrderList() {
        HttpServiceIml.getUnOrderList().subscribe(new Subscriber<UnOrderBo>() {
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
            public void onNext(UnOrderBo s) {
                if(mView != null){
                    mView.getUnOrderList(s);
                }
            }
        });
    }

    @Override
    public void getHistoryOrderList() {
        HttpServiceIml.getHistoryList().subscribe(new Subscriber<HistoryOrderBo>() {
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
            public void onNext(HistoryOrderBo s) {
                if (mView != null) {
                    mView.getHistoryOrderList(s);
                }
            }
        });
    }
}
