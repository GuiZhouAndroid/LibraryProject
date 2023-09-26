package zsdev.work.network.exception;

/**
 * Created: by 2023-09-09 01:24
 * Description: 响应异常
 * Author: 张松
 */
public class ResponseThrowable extends RuntimeException {

    /**
     * 异常码
     */
    public int code;

    /**
     * 异常原因
     */
    public String msg;

    public ResponseThrowable(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
    }

    /**
     * RxJava装载异常创建对象后，在Subscriber订阅基类中的onError()方法中匹配异常约定常量后返回
     *
     * @param throwable 异常
     * @param code      异常码
     */
    public ResponseThrowable(Throwable throwable, int code, String msg) {
        super(throwable);
        this.code = code;
        this.msg = msg;
    }
}


