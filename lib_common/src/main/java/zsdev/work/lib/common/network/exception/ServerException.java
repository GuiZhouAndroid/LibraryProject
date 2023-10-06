package zsdev.work.lib.common.network.exception;

/**
 * Created: by 2023-09-09 01:25
 * Description: 服务器异常
 * Author: 张松
 */
public class ServerException extends RuntimeException {

    /**
     * 异常状态码
     */
    public int code;

    /**
     * 异常原因
     */
    public String msg;

    public ServerException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
