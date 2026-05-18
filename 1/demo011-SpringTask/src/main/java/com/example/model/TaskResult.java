package com.example.model;

/**
 * 任务执行结果模型类
 *
 * 【设计目的】
 * 用于封装异步任务的执行结果
 * 演示如何获取异步任务的返回值
 *
 * 【泛型说明】
 * T: 表示任务返回值的类型
 * 使用泛型可以支持不同类型的任务结果
 *
 * 【使用场景】
 * 1. 异步任务需要返回执行结果时使用
 * 2. 需要在主线程中获取异步任务的执行状态
 * 3. 需要获取异步任务的返回数据
 *
 * 【示例】
 * TaskResult<String> result = asyncService.downloadFile();
 * if (result.isSuccess()) {
 *     String data = result.getData();
 * }
 */
public class TaskResult<T> {

    /**
     * 任务是否执行成功
     */
    private boolean success;

    /**
     * 任务返回的数据
     */
    private T data;

    /**
     * 错误信息（如果任务失败）
     */
    private String errorMessage;

    /**
     * 任务开始执行的时间戳
     */
    private long startTime;

    /**
     * 任务执行完成的时间戳
     */
    private long endTime;

    /**
     * 默认构造函数
     */
    public TaskResult() {
    }

    /**
     * 带参数的构造函数
     *
     * @param success 是否成功
     * @param data 返回数据
     */
    public TaskResult(boolean success, T data) {
        this.success = success;
        this.data = data;
        this.startTime = System.currentTimeMillis();
        this.endTime = this.startTime;
    }

    /**
     * 创建成功结果
     *
     * @param data 返回数据
     * @param <T> 数据类型
     * @return 成功的结果对象
     */
    public static <T> TaskResult<T> success(T data) {
        TaskResult<T> result = new TaskResult<>();
        result.setSuccess(true);
        result.setData(data);
        result.setEndTime(System.currentTimeMillis());
        return result;
    }

    /**
     * 创建失败结果
     *
     * @param errorMessage 错误信息
     * @param <T> 数据类型
     * @return 失败的结果对象
     */
    public static <T> TaskResult<T> failure(String errorMessage) {
        TaskResult<T> result = new TaskResult<>();
        result.setSuccess(false);
        result.setErrorMessage(errorMessage);
        result.setEndTime(System.currentTimeMillis());
        return result;
    }

    /**
     * 判断任务是否执行成功
     *
     * @return true表示成功，false表示失败
     */
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    /**
     * 获取任务执行耗时（毫秒）
     *
     * @return 执行耗时
     */
    public long getExecutionTime() {
        return endTime - startTime;
    }

    @Override
    public String toString() {
        return "TaskResult{" +
                "success=" + success +
                ", data=" + data +
                ", errorMessage='" + errorMessage + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", executionTime=" + getExecutionTime() + "ms" +
                '}';
    }
}
