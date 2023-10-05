package zsdev.work.lib.common.utils.cpu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import zsdev.work.lib.common.utils.FileUtil;
import zsdev.work.lib.common.utils.LogUtil;


/**
 * Created: by 2023-09-26 10:12
 * Description: CPU工具类
 * Author: 张松
 */
public class CpuUtil {
    private static final String TAG = CpuUtil.class.getSimpleName();
    private static final String CPU_INFO_PATH = "/proc/cpuinfo";
    private static final String CPU_FREQ_NULL = "N/A";
    private static final String CMD_CAT = "/system/bin/cat";
    private static final String CPU_FREQ_CUR_PATH = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq";
    private static final String CPU_FREQ_MAX_PATH = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq";
    private static final String CPU_FREQ_MIN_PATH = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq";

    private static String CPU_NAME;
    private static int CPU_CORES = 0;
    private static long CPU_MAX_FREQENCY = 0;
    private static long CPU_MIN_FREQENCY = 0;

    /**
     * 打印cpu信息
     */
    public static String printCpuInfo() {
        String info = FileUtil.getFileOutputString(CPU_INFO_PATH);
        LogUtil.i("_______  CPU :   \n" + info);
        return info;
    }

    /**
     * 获取可用的处理器
     */
    public static int getProcessorsCount() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * 获取此设备中所有处理器的可用内核数
     * 需要：能够在“/sys/devices/system/cpu”上仔细阅读文件系统
     *
     * @return 如果没有得到结果，核心数或可用处理器数
     */
    public static int getCoresNumbers() {
        if (CPU_CORES != 0) {
            return CPU_CORES;
        }
        //Private类，只显示目录列表中的CPU设备
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                //检查文件名是否为cpu，后跟一个位数
                if (Pattern.matches("cpu[0-9]+", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }

        try {
            //获取包含CPU信息的目录
            File dir = new File("/sys/devices/system/cpu/");
            //筛选以仅列出我们关心的设备
            File[] files = dir.listFiles(new CpuFilter());
            //返回内核数（虚拟CPU设备）
            CPU_CORES = files.length;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (CPU_CORES < 1) {
            CPU_CORES = Runtime.getRuntime().availableProcessors();
        }
        if (CPU_CORES < 1) {
            CPU_CORES = 1;
        }
        return CPU_CORES;
    }

    /**
     * 获取CPU名称
     */
    public static String getCpuName() {
        if (!Check.isEmpty(CPU_NAME)) {
            return CPU_NAME;
        }
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(CPU_INFO_PATH), 8192);
            String line = bufferedReader.readLine();
            bufferedReader.close();
            String[] array = line.split(":\\s+", 2);
            if (array.length > 1) {
                LogUtil.i(array[1]);
                CPU_NAME = array[1];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return CPU_NAME;
    }

    /**
     * 获取当前CPU频率
     */
    public static long getCurrentFreqency() {
        try {
            return Long.parseLong(FileUtil.getFileOutputString(CPU_FREQ_CUR_PATH).trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取最大CPU频率
     */
    public static long getMaxFreqency() {
        if (CPU_MAX_FREQENCY > 0) {
            return CPU_MAX_FREQENCY;
        }
        try {
            CPU_MAX_FREQENCY = Long.parseLong(getCMDOutputString(new String[]{CMD_CAT, CPU_FREQ_MAX_PATH}).trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CPU_MAX_FREQENCY;
    }

    /**
     * 获取最小CPU频率
     */
    public static long getMinFreqency() {
        if (CPU_MIN_FREQENCY > 0) {
            return CPU_MIN_FREQENCY;
        }
        try {
            CPU_MIN_FREQENCY = Long.parseLong(getCMDOutputString(new String[]{CMD_CAT, CPU_FREQ_MIN_PATH}).trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CPU_MIN_FREQENCY;
    }

    /**
     * 获取命令输出字符串
     */
    public static String getCMDOutputString(String[] args) {
        try {
            ProcessBuilder cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            StringBuilder sb = new StringBuilder();
            byte[] re = new byte[64];
            int len;
            while ((len = in.read(re)) != -1) {
                sb.append(new String(re, 0, len));
            }
            in.close();
            process.destroy();
            LogUtil.i("CMD: " + sb.toString());
            return sb.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
