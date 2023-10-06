package zsdev.work.lib.common.network.exception;

import androidx.annotation.NonNull;

/**
 * Created: by 2023-09-09 01:24
 * Description: 响应异常
 * Author: 张松
 */
public class ResponseThrowable extends Exception {

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

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @NonNull
    @Override
    public String toString() {
        return "ResponseThrowable{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}


