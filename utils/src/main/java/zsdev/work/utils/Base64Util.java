package zsdev.work.utils;

import java.io.UnsupportedEncodingException;

/**
 * Created: by 2023-08-08 00:15
 * Description: Base64工具类
 * Author: 张松
 */
public class Base64Util {
    /**
     * 编码器/解码器标志的默认值。
     */
    public static final int DEFAULT = 0;

    /**
     * 编码器标志位，用于省略末尾的填充“=”字符
     * <p>
     * 输出（如果有的话）。
     */
    public static final int NO_PADDING = 1;

    /**
     * 编码器标志位，用于省略所有行终止符（即输出
     * 将在一条长线上）。
     */
    public static final int NO_WRAP = 2;

    /**
     * 编码器标志位，用于指示线路应以
     * CRLF对，而不仅仅是一个LF。如果{@code无效
     * NO_WRAP｝也被指定。
     */
    public static final int CRLF = 4;

    /**
     * 编码器/解码器标志位使用“URL和
     * Base64的“文件名安全”变体（参见RFC 3548第4节），其中
     * 使用{@code-}和{@code_}来代替{@code+}和
     * ｛@code/｝。
     */
    public static final int URL_SAFE = 8;

    /**
     * 要传递给｛@link android.util.Base64OutputStream｝的标志，以指示
     * 不应关闭正在包装的输出流
     * 其本身是封闭的。
     */
    public static final int NO_CLOSE = 16;

    //  --------------------------------------------------------
    //  shared code
    //  --------------------------------------------------------

    /* package */ static abstract class Coder {
        public byte[] output;
        public int op;

        /**
         * 对另一个输入数据块进行编码/解码。这个输出是
         * 由调用者提供，并且必须足够大才能容纳所有
         * 编码数据。在退出时，this.op将设置为长度
         * 编码数据的。
         *
         * @param finish true如果这是要处理的最后一个调用
         *               此对象。将最终确定编码器状态并
         *               在输出中包括任何最终字节。
         *               如果到目前为止输入良好，@return true；false如果有的
         *               在输入流中检测到错误。。
         */
        public abstract boolean process(byte[] input, int offset, int len, boolean finish);

        /**
         * @返回调用process（）的最大字节数 可以为给定数量的输入字节生成。这可能
         * 被高估了。
         */
        public abstract int maxOutputSize(int len);
    }

    //  --------------------------------------------------------
    //  decoding
    //  --------------------------------------------------------

    /**
     * 解码输入中的Base64编码数据，并返回中的数据
     * 一个新的字节数组。
     *
     * <p>末尾的填充“=”字符被认为是可选的，但是
     * 如果有，必须有正确的数量。
     *
     * @param str 要解码的输入字符串，转换为
     *            使用默认字符集的字节
     * @param标志控制解码输出的某些功能。 传递｛@code DEFAULT｝以解码标准Base64。
     * 如果输入包含
     * 填充不正确
     */
    public static byte[] decode(String str, int flags) {
        return decode(str.getBytes(), flags);
    }

    /**
     * 解码输入中的Base64编码数据，并返回中的数据
     * 一个新的字节数组。
     *
     * <p>末尾的填充“=”字符被认为是可选的，但是
     * 如果有，必须有正确的数量。
     *
     * @param输入要解码的输入数组
     * @param标志控制解码输出的某些功能。 传递｛@code DEFAULT｝以解码标准Base64。
     * 如果输入包含
     * 填充不正确
     */
    public static byte[] decode(byte[] input, int flags) {
        return decode(input, 0, input.length, flags);
    }

    /**
     * 解码输入中的Base64编码数据，并返回中的数据
     * 一个新的字节数组。
     *
     * <p>末尾的填充“=”字符被认为是可选的，但是
     * 如果有，必须有正确的数量。
     *
     * @param len 要解码的输入字节数
     * @param输入要解码的数据
     * @param偏移输入数组中开始的位置
     * @param标志控制解码输出的某些功能。 传递｛@code DEFAULT｝以解码标准Base64。
     * 如果输入包含
     * 填充不正确
     */
    public static byte[] decode(byte[] input, int offset, int len, int flags) {
        //为输入可以表示的大多数数据分配空间
        // （如果包含空格等，则可以包含更少的空格）
        Decoder decoder = new Decoder(flags, new byte[len * 3 / 4]);

        if (!decoder.process(input, offset, len, true)) {
            throw new IllegalArgumentException("bad base-64");
        }

        // 也许我们运气好，分配了足够的输出空间。
        if (decoder.op == decoder.output.length) {
            return decoder.output;
        }

        //需要缩短阵列，因此分配一个新的
        // 合适的尺寸和复制。
        byte[] temp = new byte[decoder.op];
        System.arraycopy(decoder.output, 0, temp, 0, decoder.op);
        return temp;
    }

    /* package */ static class Decoder extends Coder {
        /**
         * 用于将字节转换为其在
         * Base64字母表。
         */
        private static final int DECODE[] = {
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63,
                52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -2, -1, -1,
                -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
                15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1,
                -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
                41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        };

        /**
         * 解码“网络安全”变体的查找表（RFC 3548
         * 第4节），其中-和_替换+和/。
         */
        private static final int DECODE_WEBSAFE[] = {
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1,
                52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -2, -1, -1,
                -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
                15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63,
                -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
                41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        };

        /**
         * DECODE数组中的非数据值。
         */
        private static final int SKIP = -1;
        private static final int EQUALS = -2;

        /**
         * 状态0-3正在读取下一个输入元组。
         * 状态4已读取一个“=”，并且正期待
         * 再来一次。
         * 状态5不需要更多的数据或填充字符
         * 在输入中。
         * 状态6是错误状态；检测到错误
         * 在输入中，没有未来的输入可以“修复”它。
         */
        private int state;   //状态编号（0到6）
        private int value;

        final private int[] alphabet;

        public Decoder(int flags, byte[] output) {
            this.output = output;

            alphabet = ((flags & URL_SAFE) == 0) ? DECODE : DECODE_WEBSAFE;
            state = 0;
            value = 0;
        }

        /**
         * 返回对字节数的高估
         * len}个字节可以解码为。
         */
        public int maxOutputSize(int len) {
            return len * 3 / 4 + 10;
        }

        /**
         * 解码另一个输入数据块。
         * <p>
         * 如果状态机仍然正常，@return true。false if
         * 在输入流中检测到坏的base-64数据。
         */
        public boolean process(byte[] input, int offset, int len, boolean finish) {
            if (this.state == 6) return false;

            int p = offset;
            len += offset;

            //使用局部变量使解码器大约占12%
            // 比我们在中操作成员变量更快
            // 循环。（即使是字母表也能测量
            // 差异，这让我有些惊讶，因为
            // 成员变量为final。）
            int state = this.state;
            int value = this.value;
            int op = 0;
            final byte[] output = this.output;
            final int[] alphabet = this.alphabet;

            while (p < len) {
                //尝试快速路径：我们正在启动一个新的元组
                // 输入流的下四个字节都是数据
                // 字节。这对应于通过状态
                // 0-1-2-3-0。我们希望在大多数情况下使用这种方法
                // 数据。
                //
                // 如果接下来的四个输入字节中有任何一个是非数据
                // （空格等），则值将以负数结束。（全部
                // 解码中的非数据值为小负值
                // 数字，所以把它们中的任何一个向上或向上移动
                // 一起将产生一个设置了其最高位的值。）
                //
                // 您可以删除整个块，并且输出应该
                // 还是一样，只是慢一点。
                if (state == 0) {
                    while (p + 4 <= len &&
                            (value = ((alphabet[input[p] & 0xff] << 18) |
                                    (alphabet[input[p + 1] & 0xff] << 12) |
                                    (alphabet[input[p + 2] & 0xff] << 6) |
                                    (alphabet[input[p + 3] & 0xff]))) >= 0) {
                        output[op + 2] = (byte) value;
                        output[op + 1] = (byte) (value >> 8);
                        output[op] = (byte) (value >> 16);
                        op += 3;
                        p += 4;
                    }
                    if (p >= len) break;
                }

                //快速路径不可用--或者我们已经阅读了
                // 部分元组，或者后面的四个输入字节不是全部
                // 数据或其他什么。回退到较慢的状态
                // 机器实现。
                int d = alphabet[input[p++] & 0xff];

                switch (state) {
                    case 0:
                        if (d >= 0) {
                            value = d;
                            ++state;
                        } else if (d != SKIP) {
                            this.state = 6;
                            return false;
                        }
                        break;

                    case 1:
                        if (d >= 0) {
                            value = (value << 6) | d;
                            ++state;
                        } else if (d != SKIP) {
                            this.state = 6;
                            return false;
                        }
                        break;

                    case 2:
                        if (d >= 0) {
                            value = (value << 6) | d;
                            ++state;
                        } else if (d == EQUALS) {
                            //发出最后一个（部分）输出元组；
                            // 只需要多一个填充字符。
                            output[op++] = (byte) (value >> 4);
                            state = 4;
                        } else if (d != SKIP) {
                            this.state = 6;
                            return false;
                        }
                        break;

                    case 3:
                        if (d >= 0) {
                            // 发出三重输出并返回到状态0
                            value = (value << 6) | d;
                            output[op + 2] = (byte) value;
                            output[op + 1] = (byte) (value >> 8);
                            output[op] = (byte) (value >> 16);
                            op += 3;
                            state = 0;
                        } else if (d == EQUALS) {
                            // 发出最后一个（部分）输出元组
                            // 不需要进一步的数据或填充字符
                            output[op + 1] = (byte) (value >> 2);
                            output[op] = (byte) (value >> 10);
                            op += 2;
                            state = 5;
                        } else if (d != SKIP) {
                            this.state = 6;
                            return false;
                        }
                        break;

                    case 4:
                        if (d == EQUALS) {
                            ++state;
                        } else if (d != SKIP) {
                            this.state = 6;
                            return false;
                        }
                        break;

                    case 5:
                        if (d != SKIP) {
                            this.state = 6;
                            return false;
                        }
                        break;
                }
            }

            if (!finish) {
                // 我们没有投入，但未来的电话可能会提供
                // more.
                this.state = state;
                this.value = value;
                this.op = op;
                return true;
            }

            //完成读取输入。现在弄清楚我们在哪里
            // 状态机并结束。
            switch (state) {
                case 0:
                    // 输出长度是三的倍数。好的
                    break;
                case 1:
                    //读取一个额外的输入字节，这不足以
                    // 生成另一个输出字节。不合法的
                    this.state = 6;
                    return false;
                case 2:
                    //读取两个额外的输入字节，足以再发出1个
                    // 输出字节。好的
                    output[op++] = (byte) (value >> 4);
                    break;
                case 3:
                    //读取三个额外的输入字节，足以再发出2个
                    // 输出字节。好的
                    output[op++] = (byte) (value >> 10);
                    output[op++] = (byte) (value >> 2);
                    break;
                case 4:
                    // 当我们期望2时，读取一个填充“=”。不合法的
                    this.state = 6;
                    return false;
                case 5:
                    //请阅读我们所期望的所有填充项，不再赘述。
                    // 很好。
                    break;
            }

            this.state = state;
            this.op = op;
            return true;
        }
    }

    //  --------------------------------------------------------
    //  encoding
    //  --------------------------------------------------------

    /**
     * Base64对给定数据进行编码，并返回新分配的
     * 包含结果的字符串。
     *
     * @param输入要编码的数据
     * @param标志控制编码输出的某些功能。 传递｛@code DEFAULT｝会导致输出
     * 遵循RFC 2045。
     */
    public static String encodeToString(byte[] input, int flags) {
        try {
            return new String(encode(input, flags), "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            // US-ASCII保证可用。
            throw new AssertionError(e);
        }
    }

    /**
     * Base64对给定数据进行编码，并返回新分配的
     * 包含结果的字符串。
     *
     * @param len 要编码的输入字节数
     * @param输入要编码的数据
     * @param偏移输入数组中要 启动
     * @param标志控制编码输出的某些功能。 传递｛@code DEFAULT｝会导致输出
     * 遵循RFC 2045。
     */
    public static String encodeToString(byte[] input, int offset, int len, int flags) {
        try {
            return new String(encode(input, offset, len, flags), "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            // US-ASCII is guaranteed to be available.
            throw new AssertionError(e);
        }
    }

    /**
     * Base64对给定数据进行编码，并返回新分配的
     * 字节[]和结果。
     *
     * @param输入要编码的数据
     * @param标志控制编码输出的某些功能。 传递｛@code DEFAULT｝会导致输出
     * 遵循RFC 2045。
     */
    public static byte[] encode(byte[] input, int flags) {
        return encode(input, 0, input.length, flags);
    }

    /**
     * Base64对给定数据进行编码，并返回新分配的
     * 字节[]和结果。
     *
     * @param len 要编码的输入字节数
     * @param输入要编码的数据
     * @param偏移输入数组中要 启动
     * @param标志控制编码输出的某些功能。 传递｛@code DEFAULT｝会导致输出
     * 遵循RFC 2045。
     */
    public static byte[] encode(byte[] input, int offset, int len, int flags) {
        Encoder encoder = new Encoder(flags, null);

        //计算我们将生成的数组的确切长度
        int output_len = len / 3 * 4;

        // 说明数据的尾部和填充字节（如果有的话）
        if (encoder.do_padding) {
            if (len % 3 > 0) {
                output_len += 4;
            }
        } else {
            switch (len % 3) {
                case 0:
                    break;
                case 1:
                    output_len += 2;
                    break;
                case 2:
                    output_len += 3;
                    break;
            }
        }

        // 说明换行符（如果有的话）
        if (encoder.do_newline && len > 0) {
            output_len += (((len - 1) / (3 * Encoder.LINE_GROUPS)) + 1) *
                    (encoder.do_cr ? 2 : 1);
        }

        encoder.output = new byte[output_len];
        encoder.process(input, offset, len, true);

        assert encoder.op == output_len;

        return encoder.output;
    }

    /* package */ static class Encoder extends Coder {
        /**
         * 每隔这么多输出元组发出一行新行。相对应
         * 76个字符的行长度（根据
         * <a href=“http://www.ietf.org/rfc/rfc2045.txt“>RFC 2045</a>）。
         */
        public static final int LINE_GROUPS = 19;

        /**
         * 翻转Base64字母位置的查找表（6位）
         * 转换为输出字节。
         */
        private static final byte ENCODE[] = {
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
                'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
                'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
                'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/',
        };

        /**
         * 翻转Base64字母位置的查找表（6位）
         * 转换为输出字节。
         */
        private static final byte ENCODE_WEBSAFE[] = {
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
                'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
                'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
                'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_',
        };

        final private byte[] tail;
        /* package */ int tailLen;
        private int count;

        final public boolean do_padding;
        final public boolean do_newline;
        final public boolean do_cr;
        final private byte[] alphabet;

        public Encoder(int flags, byte[] output) {
            this.output = output;

            do_padding = (flags & NO_PADDING) == 0;
            do_newline = (flags & NO_WRAP) == 0;
            do_cr = (flags & CRLF) != 0;
            alphabet = ((flags & URL_SAFE) == 0) ? ENCODE : ENCODE_WEBSAFE;

            tail = new byte[2];
            tailLen = 0;

            count = do_newline ? LINE_GROUPS : -1;
        }

        /**
         * @返回对字节数的高估{@code len}个字节可以编码为。
         */
        public int maxOutputSize(int len) {
            return len * 8 / 5 + 10;
        }

        public boolean process(byte[] input, int offset, int len, boolean finish) {
            // 使用局部变量可使编码器的速度提高约9%
            final byte[] alphabet = this.alphabet;
            final byte[] output = this.output;
            int op = 0;
            int count = this.count;

            int p = offset;
            len += offset;
            int v = -1;

            //首先，我们需要连接上一个调用的尾部
            // 现在有任何可用的输入字节，看看我们是否可以清空
            // 尾巴。
            switch (tailLen) {
                case 0:
                    // 没有尾巴
                    break;

                case 1:
                    if (p + 2 <= len) {
                        //具有至少2个字节的1字节尾部
                        // 输入现在可用。
                        v = ((tail[0] & 0xff) << 16) |
                                ((input[p++] & 0xff) << 8) |
                                (input[p++] & 0xff);
                        tailLen = 0;
                    }
                    ;
                    break;

                case 2:
                    if (p + 1 <= len) {
                        //一个2字节的尾部，至少有1个字节的输入。
                        v = ((tail[0] & 0xff) << 16) |
                                ((tail[1] & 0xff) << 8) |
                                (input[p++] & 0xff);
                        tailLen = 0;
                    }
                    break;
            }

            if (v != -1) {
                output[op++] = alphabet[(v >> 18) & 0x3f];
                output[op++] = alphabet[(v >> 12) & 0x3f];
                output[op++] = alphabet[(v >> 6) & 0x3f];
                output[op++] = alphabet[v & 0x3f];
                if (--count == 0) {
                    if (do_cr) output[op++] = '\r';
                    output[op++] = '\n';
                    count = LINE_GROUPS;
                }
            }

            //在这一点上，要么没有尾巴，要么更少
            // 可用的输入超过3字节。

            // 主循环，将3个输入字节转换为4个输出字节
            // 每次迭代。
            while (p + 3 <= len) {
                v = ((input[p] & 0xff) << 16) |
                        ((input[p + 1] & 0xff) << 8) |
                        (input[p + 2] & 0xff);
                output[op] = alphabet[(v >> 18) & 0x3f];
                output[op + 1] = alphabet[(v >> 12) & 0x3f];
                output[op + 2] = alphabet[(v >> 6) & 0x3f];
                output[op + 3] = alphabet[v & 0x3f];
                p += 3;
                op += 4;
                if (--count == 0) {
                    if (do_cr) output[op++] = '\r';
                    output[op++] = '\n';
                    count = LINE_GROUPS;
                }
            }

            if (finish) {
                //完成输入的尾部。请注意，我们需要
                // 在任何字节之前消耗尾部中的任何字节
                // 保留输入；最多应该有两个字节
                // 总计。

                if (p - tailLen == len - 1) {
                    int t = 0;
                    v = ((tailLen > 0 ? tail[t++] : input[p++]) & 0xff) << 4;
                    tailLen -= t;
                    output[op++] = alphabet[(v >> 6) & 0x3f];
                    output[op++] = alphabet[v & 0x3f];
                    if (do_padding) {
                        output[op++] = '=';
                        output[op++] = '=';
                    }
                    if (do_newline) {
                        if (do_cr) output[op++] = '\r';
                        output[op++] = '\n';
                    }
                } else if (p - tailLen == len - 2) {
                    int t = 0;
                    v = (((tailLen > 1 ? tail[t++] : input[p++]) & 0xff) << 10) |
                            (((tailLen > 0 ? tail[t++] : input[p++]) & 0xff) << 2);
                    tailLen -= t;
                    output[op++] = alphabet[(v >> 12) & 0x3f];
                    output[op++] = alphabet[(v >> 6) & 0x3f];
                    output[op++] = alphabet[v & 0x3f];
                    if (do_padding) {
                        output[op++] = '=';
                    }
                    if (do_newline) {
                        if (do_cr) output[op++] = '\r';
                        output[op++] = '\n';
                    }
                } else if (do_newline && op > 0 && count != LINE_GROUPS) {
                    if (do_cr) output[op++] = '\r';
                    output[op++] = '\n';
                }

                assert tailLen == 0;
                assert p == len;
            } else {
                //把剩菜留到尾巴里，下次吃
                // 调用encodeInternal。

                if (p == len - 1) {
                    tail[tailLen++] = input[p];
                } else if (p == len - 2) {
                    tail[tailLen++] = input[p];
                    tail[tailLen++] = input[p + 1];
                }
            }

            this.op = op;
            this.count = count;

            return true;
        }
    }

    private Base64Util() {
    }   //不实例化
}