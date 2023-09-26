package zsdev.work.network.enums;

/**
 * Created: by 2023-09-11 15:11
 * Description: 网络类型枚举类
 * Author: 张松
 */
public enum NetworkMode {
    None(1),
    Mobile(2),
    Wifi(4),
    Other(8);

    NetworkMode(int value) {
        this.value = value;
    }

    public int value;
}
