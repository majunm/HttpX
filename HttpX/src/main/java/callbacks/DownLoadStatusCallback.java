package callbacks;

/**
 * 作者：马俊
 * 时间：2018/4/25 下午2:00
 * 邮箱：747673016@qq.com
 */

public interface DownLoadStatusCallback<T> {
    public void onStart(T bean);

    public void onError(T bean);

    public void onSuccess(T bean);

    public void onFinished(T bean);

    public void onStop(T bean);

    public void onPause(T bean);

    public void onCancel(T bean);
    /**
     * @param currentSize
     */
    public void onProgress(T bean, int currentSize);

    public void onPrepare(T bean);
}

