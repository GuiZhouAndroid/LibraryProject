package zsdev.work.libraryproject;

import zsdev.work.lib.common.network.NetworkHelper;
import zsdev.work.libraryproject.service.APIService;


/**
 * Created: by 2023-09-26 18:59
 * Description:
 * Author: 张松
 */
public class ServiceBuildHelper {

    public static final String API_BASE_URL = "https://www.zsdev.work/";
    /**
     * 用户服务
     */
    private static APIService apiService;

    /**
     * 通过RetrofitHelper创建返回Service
     *
     * @return
     */
    public static synchronized APIService getApiService() {
        if (apiService == null) {
            synchronized (ServiceBuildHelper.class) {
//                if (apiService == null) {
//                    apiService = NetworkHelper.getApiServiceClass(App.getContext(), API_BASE_URL, true, APIService.class);
//                }
                if (apiService == null) {
                    apiService = NetworkHelper.getApiServiceClass(App.getContext(), API_BASE_URL, true, true, false, APIService.class, new NetWorkConfig());
                }
            }
        }
        return apiService;
    }
}
