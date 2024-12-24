package com.hk.xnet.task;

import com.hk.xnet.ResponseDataListener;
import com.lzy.okgo.utils.OkLogger;

import java.util.Timer;
import java.util.TimerTask;

// UnknownHostException 找不到主机异常（DNS无法找到对应的IP地址抛出该异常，移动网路有概率出现）
// SocketTimeoutException 连接超时异常（已经建立连接，但是服务器响应慢才会走超时异常）；
// 为了解决UnknownHostException DNS长时间找不到IP的问题， 采用请求任务机制， 将超时的的任务找出并提示用户当前网路不可用
public class TaskGen {
    private long taskId; // 任务ID
    private String path; // 请求地址
    private volatile boolean isStop; // 是否停止运行
    private ResponseDataListener mResponseDataListener;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private long mTimeoutPeriod;
    private long mCurrentCount;
    private volatile boolean mCompleteTheRequest;

    public TaskGen(long taskId, String path, boolean isStop, ResponseDataListener responseDataListener) {
        this.taskId = taskId;
        this.path = path;
        this.isStop = isStop;
        this.mResponseDataListener = responseDataListener;
        mTimeoutPeriod = responseDataListener.taskTimeout(); // 取出任务超时时长
    }

    public void setCompleteTheRequest(boolean mCompleteTheRequest) {
        this.mCompleteTheRequest = mCompleteTheRequest;
    }

    private TaskGen() {
    }

    /**
     * 开启任务
     */
    public void startTask() {
        OkLogger.d( "TaskGen 任务开始：" + path);
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (isStop) {
                    OkLogger.d( "TaskGen 任务停止：超时停止的任务" +path);
                    stopTask(); // 停止计时
                    return;
                }

                if (mCompleteTheRequest) {
                    OkLogger.d( "TaskGen 任务停止：该请求完成停止的任务" + path);
                    stopTask();
                    return;
                }
                mCurrentCount = mCurrentCount + 1;
                long currentTime = mCurrentCount * 1000; // 得到当前使用时长
                if (currentTime >= mTimeoutPeriod && !mCompleteTheRequest) {
                    OkLogger.d( "TaskGen 超时：" + path);
                    isStop = true;
                    // 发送
                    TaskGen.this.mResponseDataListener.taskTimeFail(TaskGen.this);
                }
            }
        };
        mTimer.schedule(mTimerTask, 0 , 1000);
    }


    public void stopTask() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimerTask.cancel();
            mTimer = null;
            mTimerTask = null;
        }
    }


    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }
}
