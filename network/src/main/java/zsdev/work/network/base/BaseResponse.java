package zsdev.work.network.base;

import androidx.annotation.NonNull;

/**
 * Created: by 2023-09-08 00:13
 * Description: 网络请求之后响应结果泛型基类
 * Author: 张松
 */
public class BaseResponse<T> {
    private int code;
    private String msg;
    private T data;

    public boolean isSuccess() {
        return code == 200;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @NonNull
    @Override
    public String toString() {
        return "BaseResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
