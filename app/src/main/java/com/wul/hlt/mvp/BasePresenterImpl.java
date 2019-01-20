package com.wul.hlt.mvp;

/**
 * MVPPlugin
 * �wuliang
 * <p>
 * 作为事务绑定机制
 */

public class BasePresenterImpl<V extends BaseView> implements BasePresenter<V> {
    protected V mView;

    @Override
    public void attachView(V view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
    }
}
