package com.wul.hlt.api;

import com.wul.hlt.base.MyApplication;
import com.wul.hlt.entity.GetVersionRequest;
import com.wul.hlt.entity.HistoryOrderBo;
import com.wul.hlt.entity.OrderDetails;
import com.wul.hlt.entity.UnOrderBo;
import com.wul.hlt.entity.UserBo;
import com.wul.hlt.entity.VersionBo;
import com.wul.hlt.util.rx.RxResultHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Observable;

/**
 * Created by wuliang on 2017/4/19.
 * <p>
 * 所有网络请求方法
 */

public class HttpServiceIml {

    private static HttpService service;

    private static HttpService downLoadService;

    /**
     * 获取代理对象
     *
     * @return
     */
    public static HttpService getService() {
        if (service == null)
            service = ApiManager.getInstance().configRetrofit(HttpService.class, HttpService.URL);
        return service;
    }

    /**
     * 获取代理对象
     *
     * @return
     */
    public static HttpService getDownLoadService(DownloadResponseBody.DownloadListener listener) {
        downLoadService = ApiManager.getInstance().downloadConfigRetrofit(HttpService.class, HttpService.URL, listener);
        return downLoadService;

    }


    /**
     * 登录接口
     */
    public static Observable<UserBo> login(String number, String phone, String password) {
        JSONObject object = new JSONObject();
        try {
            object.put("number", number);
            object.put("password", password);
            object.put("phone", phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
        return getService().login(body).compose(RxResultHelper.<UserBo>httpRusult());
    }


    /**
     * 获取待接单订单列表
     */
    public static Observable<UnOrderBo> getUnOrderList() {
        JSONObject object = new JSONObject();
        try {
            object.put("token", MyApplication.spUtils.getString("token"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
        return getService().getUnOrderList(body).compose(RxResultHelper.<UnOrderBo>httpRusult());
    }


    /**
     * 获取历史订单列表
     */
    public static Observable<HistoryOrderBo> getHistoryList() {
        JSONObject object = new JSONObject();
        try {
            object.put("token", MyApplication.spUtils.getString("token"));
            object.put("pageNum", 1);
            object.put("pageSize", 1000);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
        return getService().getHistoryList(body).compose(RxResultHelper.<HistoryOrderBo>httpRusult());
    }


    /**
     * 接单
     */
    public static Observable<String> orderTaking(String orderId) {
        JSONObject object = new JSONObject();
        try {
            object.put("token", MyApplication.spUtils.getString("token"));
            object.put("id", orderId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
        return getService().orderTaking(body).compose(RxResultHelper.<String>httpRusult());
    }


    /**
     * 获取订单详情
     */
    public static Observable<OrderDetails> getOrderDetails(String orderId) {
        JSONObject object = new JSONObject();
        try {
            object.put("token", MyApplication.spUtils.getString("token"));
            object.put("id", orderId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
        return getService().getGreengrocerOrder(body).compose(RxResultHelper.<OrderDetails>httpRusult());
    }


    /**
     * 获取版本信息
     */
    public static Observable<VersionBo> getVersionInfo() {
        GetVersionRequest request = new GetVersionRequest();
        request.type = 1;
        request.token = MyApplication.spUtils.getString("token");
        return getService().getVersionName(request).compose(RxResultHelper.httpRusult());
    }


    /**
     * 下载
     */
    public static Observable<ResponseBody> downLoad(String url, DownloadResponseBody.DownloadListener
            downloadListener, File file) {
        //url = "http://172.18.100.26:8080/manager/images/kkk.7z";
        return getDownLoadService(downloadListener).download(url).compose(RxResultHelper.downRequest(file));
    }


}
