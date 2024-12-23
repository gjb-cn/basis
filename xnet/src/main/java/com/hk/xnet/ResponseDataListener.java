package com.hk.xnet;


import com.hk.xnet.task.TaskGen;

public interface ResponseDataListener<T> {
    // 连接超时时长
    long CONNECTION_TIME = 5000l;
    // 读取超时时长
    long READ_TIME = 5000l;
    // 写入超时时长
    long WRITE_TIME = 5000l;
    // 任务超时时长
    long TASK_TIMEOUT = 10 * 1000l;
    // 重试次数 (默认不重试)
    int RETRY_COUNT = 0;

    /**
     * 请求成功
     * @param data
     */
    void success(T data);

    /**
     * 请求失败（用于处理界面UI）
     * @param throwable
     */
    void fail(Throwable throwable);

    /**
     * 请求失败， 公共异常处理方法（根据aPublicMethodIsRequired返回值确定）
     */
    void aPublicFail(Throwable throwable);

    /**
     * 是否走公共异常方法
     * @return
     */
    default boolean aPublicMethodIsRequired() {
        return true;
    }

    /**
     * 大部分接口不需要用到监听进度的方法。 下载文件需要用到
     *
     * @param currentSize currentSize
     * @param totalSize totalSize
     * @param progress progress
     * @param networkSpeed networkSpeed
     */
    default void upProgress(long currentSize, long totalSize, float progress, long networkSpeed){}

    /**
     * 特殊的错误码处理
     */
    void specialError(XNetSystemErrorCode code);

    /**
     * 当前数据处理模板
     * @return
     */
    default Template getTemplate() {
        // 当你请求模板变化时请重写该方法...
        return new Template();
    }

    /**
     * timeoutPeriod 超时时长设置
     * @return
     */
    default long[] getTimeoutPeriod() {
        return new long[]{CONNECTION_TIME, READ_TIME, WRITE_TIME};
    }

    /**
     * 重试次数
     * @return
     */
    default int getRetryCount() {
        return RETRY_COUNT;
    }

    /**
     * 是否开启计时任务 默认不开启
     * @return
     */
    default boolean timedTasks() {
        return false;
    }

    /**
     * 任务超时时间
     * @return
     */
    default long taskTimeout() {
        return TASK_TIMEOUT;
    }

    /**
     * 计时任务超时回调
     * @param taskGen
     */
    default void taskTimeFail(TaskGen taskGen){
    }
}
