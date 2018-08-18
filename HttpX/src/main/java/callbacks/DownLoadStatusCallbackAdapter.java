package callbacks;

import com.vise.log.Logger;

/**
 * 作者：马俊
 * 时间：2018/4/25 下午2:01
 * 邮箱：747673016@qq.com
 */

public abstract class DownLoadStatusCallbackAdapter<T> implements
        DownLoadStatusCallback<T> {
    public static final String TAG = DownLoadStatusCallbackAdapter.class.getSimpleName();

    public DownLoadStatusCallbackAdapter() {
        super();
    }

    @Override
    public void onStart(T bean) {
        if (bean != null) {
            Logger.e(TAG, "#开始下载#" + bean);
        }
    }

    @Override
    public void onError(T bean) {
        if (bean != null) {
            Logger.e(TAG, "#下载失败#" + bean);
        }
    }

    @Override
    public void onSuccess(T bean) {
        if (bean != null) {
            Logger.e(TAG, "#下载成功#" + bean);
        }
    }

    @Override
    public void onFinished(T bean) {
        if (bean != null) {
            Logger.e(TAG, "#下载完成#" + bean);
        }
    }

    @Override
    public void onStop(T bean) {
        if (bean != null) {
            Logger.e(TAG, "#下载停止#" + bean);
        }
    }

    @Override
    public void onPause(T bean) {
        if (bean != null) {
            Logger.e(TAG, "#下载暂停#" + bean);
        }
    }

    @Override
    public void onCancel(T bean) {
        if (bean != null) {
            Logger.e(TAG, "#取消下载#" + bean);
        }
    }

    @Override
    public void onProgress(T bean, int currentSize) {
        if (bean != null) {
            Logger.e(TAG, "#下载进度#" + currentSize);
        }
    }

    @Override
    public void onPrepare(T bean) {
        if (bean != null) {
            Logger.e(TAG, "#下载准备#" + bean);
        }
    }

}

