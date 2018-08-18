package callbacks;

import android.app.Activity;

import com.vise.log.Logger;

import http.ApiException;

/**
 */

public abstract class ResultCallbackAdapt<T> extends ResultCallback<T> {
    public final String TAG = getClass().getSimpleName();
    public ResultCallbackAdapt(Activity mAct) {
        this.mActivity = mAct;
    }

    public Activity mActivity;


    public ResultCallbackAdapt() {

    }

    @Override
    public void onError(ApiException ex) {
        doCommonTask();
        Logger.e(TAG, ex);
        if (mActivity != null && mActivity.isFinishing()) {
            Logger.e(TAG, "#activity已经死亡#");
            return;
        }
        doOnError(ex);
    }

    private void doCommonTask() {
        if (mActivity != null && mActivity.isFinishing()) {
            Logger.e(TAG, "#activity已经死亡#");
            return;
        }
    }


    @Override
    public void onResponse(T response) {
        doCommonTask();
        if (mActivity != null && mActivity.isFinishing()) {
            Logger.e(TAG, "#activity已经死亡#");
            return;
        }
        doOnResponse(response);
        Logger.e(TAG, response);
    }

    /**
     * 成功回调,包裹
     *
     * @param response
     */
    public abstract void doOnResponse(T response);

    /**
     * 失败回调,包裹
     *
     * @param ex
     */
    public abstract void doOnError(ApiException ex);

}
