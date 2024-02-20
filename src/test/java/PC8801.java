/*
 * https://github.com/PARA-DISO/pc88like_image/blob/main/src/img_processor_core.rs
 */

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.file.Paths;
import java.util.stream.IntStream;
import javax.imageio.ImageIO;


/**
 * PC8801.
 *
 * @version 0.00 2023-08-03 nsano initial version <br>
 */
public class PC8801 {

    public static void main(String[] args) throws Exception {
        // コマンドライン引数を受け取る
        float gamma_sat = Float.parseFloat(args[2]);
        float gamma_light = Float.parseFloat(args[3]);
        // 画像読み込み
        BufferedImage image_data = ImageIO.read(Paths.get(args[0]).toFile());
        PC8801 app = new PC8801();
        image_data = app.pc88_like(image_data, new float[] {gamma_sat, gamma_light});
        // 画像の出力
        ImageIO.write(image_data, "png", Paths.get(args[1]).toFile());
    }

    // PC8801風の画像に変換する関数
    BufferedImage pc88_like(BufferedImage img_data, float[] gamma) {
        if (img_data.getType() != BufferedImage.TYPE_4BYTE_ABGR) {
            throw new IllegalArgumentException("should be BufferedImage#TYPE_4BYTE_ABGR");
        }
        // 固定サイズ(横)
        final int WIDTH = 640;
        // 実縮小サイズ(横)
        final int HALF_SCALE = 320;
        // 画像データ
        int width = img_data.getWidth();
        int height = img_data.getHeight();
        // 画像縮小サイズの決定
        float scale = WIDTH / (2f * width);
        int scaled_height = (int) (scale * height);
        scaled_height = (scaled_height & 1) == 1 ? scaled_height - 1 : scaled_height;
        // 横方向の縮小画像バッファ
        byte[] hrzn = new byte[HALF_SCALE * 3 * height];
        int i = 0;
        // 1pxに対応する元画像の画素数
        final int K_MAX = (int) (width / (float) HALF_SCALE);
        int[] k_max = IntStream.range(0, 8).map(x -> (int) (K_MAX + 0.125f * x)).toArray();
        //int[] k_max = new int[(int) (k_max + 0.5f)];
        // 横方向の縮小
        byte[] data = ((DataBufferByte) img_data.getRaster().getDataBuffer()).getData();
        while (i < height) {
            int j = 0;
            int k = 0;
            while (j < HALF_SCALE) {
                float sum_r = 0f;
                float sum_g = 0f;
                float sum_b = 0f;
                float s = 0f;
                int end = k + k_max[j & 7] * 4;
                // 対象範囲における色ごとの総和
                while (k < end && k < (width * 4)) {
                    sum_r += data[i * width * 4 + k];
                    sum_g += data[i * width * 4 + k + 1];
                    sum_b += data[i * width * 4 + k + 2];
                    k += 4;
                    s += 1f;
                }
                // 各色の平均
                hrzn[i * HALF_SCALE * 3 + j * 3] = (byte) (sum_r / s);
                hrzn[i * HALF_SCALE * 3 + j * 3 + 1] = (byte) (sum_g / s);
                hrzn[i * HALF_SCALE * 3 + j * 3 + 2] = (byte) (sum_b / s);
                j += 1;
            }
            i += 1;
        }
        // 縦方向に縮小した画像バッファ
        byte[] vrtcl = new byte[scaled_height * HALF_SCALE * 4];
        i = 0;
        // 1pxに対応する画素数
        k_max = IntStream.range(0, 8).map(x -> (int) (1f / scale + 0.125f * x)).toArray();
        //k_max = new int[(int) (1f / scale + 0.5f), (int) (1f / scale)];
        // 高さ方向の縮小
        int k = 0;
        while (i < scaled_height) {
            int end = k + k_max[i & 7];
            int k_tmp = k;
            int j = 0;
            while (j < HALF_SCALE) {
                float sum_r = 0f;
                float sum_g = 0f;
                float sum_b = 0f;
                float s = 0f;
                k = k_tmp;
                // 対象範囲における色ごとの総和
                while (k < end && k < height) {
                    sum_r += hrzn[k * HALF_SCALE * 3 + j * 3];
                    sum_g += hrzn[k * HALF_SCALE * 3 + j * 3 + 1];
                    sum_b += hrzn[k * HALF_SCALE * 3 + j * 3 + 2];
                    s += 1f;
                    k += 1;
                }
                // 各色の平均を求める
                vrtcl[i * HALF_SCALE * 4 + j * 4] = (byte) (sum_r / s);
                vrtcl[i * HALF_SCALE * 4 + j * 4 + 1] = (byte) (sum_g / s);
                vrtcl[i * HALF_SCALE * 4 + j * 4 + 2] = (byte) (sum_b / s);
                j += 1;
            }
            i += 1;
        }
        // HSL変換
        float[] hsl_data = rgba2hsla(vrtcl);
        // 画像バッファ(高さ1/2)
        byte[] replaced_data = new byte[WIDTH * scaled_height * 4];
        i = 0;
        int j = 0;
        int usize = 0;
        // カラーパレット
        final byte[][] COLOR_PALLET = {
                {(byte) 255, 0, 0},   // red
                {(byte) 255, (byte) 255, 0},   // yellow
                {0, (byte) 255, 0},   // green
                {0, (byte) 255, (byte) 255}, // light blue
                {0, 0, (byte) 255}, // blue
                {(byte) 255, 0, (byte) 255}, // purple
                {0, 0, 0},   // black
                {(byte) 255, (byte) 255, (byte) 255}  // white
        };
        // 画素データの決定
        while (i < hsl_data.length) {
            float h = hsl_data[i];
            // 彩度を正規化
            float s_ = hsl_data[i + 1] / 100f;
            // 彩度を0,1に変換
            byte s = (byte) (Math.pow(s_, gamma[0]) + 0.5f);
            // 明度を正規化
            float l = hsl_data[i + 2] / 100f;
            // 明度を0,1,2,3,4or5に変換
            byte l_quartile = (byte) (Math.pow(l, gamma[1]) * 5f);
            // 色を2色決定
            int[] cm_cs = calc_color(h);
            // 明度に応じて出力する色を決定
            int[] main_color_sub_color;
            switch (l_quartile) {
            case 0:
                main_color_sub_color = new int[] {6, 6};
                break;
            case 1: {
                // 暗いほうの色を採用
                if ((cm_cs[0] & 1) == 0) {
                    main_color_sub_color = new int[] {cm_cs[0], 6};
                } else {
                    main_color_sub_color = new int[] {cm_cs[1], 6};
                }
                break;
            }
            case 2: {
                if (s == 0) {
                    main_color_sub_color = new int[] {6, 7};
                } else {
                    main_color_sub_color = new int[] {cm_cs[0], cm_cs[1]};
                }
                break;
            }
            case 3: {
                // 明るいほうの色を採用
                if ((cm_cs[0] & 1) == 1) {
                    main_color_sub_color = new int[] {cm_cs[0], 7};
                } else {
                    main_color_sub_color = new int[] {cm_cs[1], 7};
                }
                break;
            }
            default:
                main_color_sub_color = new int[] {7, 7};
            }
            // 色の配置場所を奇数、偶数行目で変える
            if (((i / (WIDTH * 4)) & 1) == 0) {
                main_color_sub_color = new int[] {main_color_sub_color[0], main_color_sub_color[1]};
            } else {
                main_color_sub_color = new int[] {main_color_sub_color[1], main_color_sub_color[0]};
            }
            // 対応するRGBデータを2px分代入
            replaced_data[j] = COLOR_PALLET[main_color_sub_color[0]][0];
            replaced_data[j + 1] = COLOR_PALLET[main_color_sub_color[0]][1];
            replaced_data[j + 2] = COLOR_PALLET[main_color_sub_color[0]][2];
            j += 4;
            replaced_data[j] = COLOR_PALLET[main_color_sub_color[1]][0];
            replaced_data[j + 1] = COLOR_PALLET[main_color_sub_color[1]][1];
            replaced_data[j + 2] = COLOR_PALLET[main_color_sub_color[1]][2];
            i += 4;
            j += 4;
        }
        // 高さ方向を倍に拡大
        int display_height = scaled_height * 2;
        byte[] dest = new byte[display_height * WIDTH * 4];
        i = 0;
        while (i < scaled_height) {
            j = 0;
            while (j < 4 * WIDTH) {
                dest[8 * i * WIDTH + j] = replaced_data[i * 4 * WIDTH + j];
                dest[8 * i * WIDTH + j + 1] = replaced_data[i * 4 * WIDTH + j + 1];
                dest[8 * i * WIDTH + j + 2] = replaced_data[i * 4 * WIDTH + j + 2];

                dest[(2 * i + 1) * 4 * WIDTH + j] = replaced_data[i * 4 * WIDTH + j];
                dest[(2 * i + 1) * 4 * WIDTH + j + 1] = replaced_data[i * 4 * WIDTH + j + 1];
                dest[(2 * i + 1) * 4 * WIDTH + j + 2] = replaced_data[i * 4 * WIDTH + j + 2];
                j += 4;
            }
            i += 1;
        }
        BufferedImage result = new BufferedImage(WIDTH, display_height, BufferedImage.TYPE_4BYTE_ABGR);
        result.getRaster().setDataElements(0, 0, dest);
        return result;
    }

    // 色の決定関数
    int[] calc_color(float hue) {
        // 12色表現
        int h_dt = (int) (hue / 30f);
        // 6色表現
        int h = (int) (hue / 60f);
        int sub_color = h_dt - h;
        // 色の決定
        if (sub_color > 5) {
            return new int[] {h, sub_color - 6};
        } else {
            return new int[] {h, sub_color};
        }
    }

    // 色空間、フォーマット変換
    public float[] rgba2hsva(byte[] src) {
        int len = src.length;
        float[] dest = new float[len];
        int i = 0;
        while (i < len) {
            float r = src[i];
            float g = src[i + 1];
            float b = src[i + 2];
            float a = src[i + 3];
            // 最小値
            float min = r < g ? r : g;
            min = min < b ? min : b;
            // 最大値
            float max = r > g ? r : g;
            max = max > b ? max : b;
            float sat = (max - min) / max;
            float sub_max_min = max - min;
            float hue = min == max ? 0f
                    : min == b ? 60f * (g - r) / sub_max_min + 60f
                    : min == r ? 60f * (b - g) / sub_max_min + 180f
                    : min == g ? 60f * (r - b) / sub_max_min + 300f
                    : 0f;
            dest[i] = hue < 0f ? hue + 360f : hue > 360f ? hue - 360f : hue;
            dest[i + 1] = 100f * sat;
            dest[i + 2] = 100f * max / 255f;
            dest[i + 3] = a;
            i += 4;
        }
        return dest;
    }

    public byte[] hsva2rgba(float[] src) {
        int len = src.length;
        byte[] dest = new byte[len];
        int i = 0;
        while (i < len) {
            float h = src[i] / 60f;
            float s = src[i + 1] / 100f;
            float v = src[i + 2] / 100f;
            byte a = (byte) src[i + 3];
            float c = v * s;
            float constant_num = v - c;
            float r = constant_num;
            float g = constant_num;
            float b = constant_num;
            int hp = (int) h;
            float hmod = h - 2f * ((int) (h * 0.5f));
            float hsub1 = hmod - 1f;
            hsub1 = hsub1 < 0f ? -1f * hsub1 : hsub1;
            float x = c * (1f - hsub1);
            if (s > 0f) {
                switch (hp) {
                case 0: {
                    r += c;
                    g += x;
                    break;
                }
                case 1: {
                    r += x;
                    g += c;
                    break;
                }
                case 2: {
                    g += c;
                    b += x;
                    break;
                }
                case 3: {
                    g += x;
                    b += c;
                    break;
                }
                case 4: {
                    r += x;
                    b += c;
                    break;
                }
                case 5:
                case 6: {
                    r += c;
                    b += x;
                    break;
                }
                default:
                    break;
                }
            }
            dest[i] = (byte) (r * 255f);
            dest[i + 1] = (byte) (g * 255f);
            dest[i + 2] = (byte) (b * 255f);
            dest[i + 3] = a;
            i += 4;
        }
        return dest;
    }

    public float[] hsva2hsla(float[] src) {
        int len = src.length;
        int i = 0;
        while (i < len) {
            float h = src[i];
            float s = src[i + 1] / 100f;
            float max = src[i + 2] / 100f;
            float a = src[i + 3];
            float l = max * (2f - s) / 2f;
            src[i] = h;
            src[i + 1] = s * max * 100f;
            src[i + 2] = l * 100f;
            src[i + 3] = a;
            i += 4;
        }
        return src;
    }

    public float[] hsla2hsva(float[] src) {
        int len = src.length;
        int i = 0;
        while (i < len) {
            float h = src[i];
            float s = src[i + 1] / 100f;
            float l = src[i + 2] / 100f;
            float a = src[i + 3];
            float v = (s + 2f * l) * 50f;
            src[i] = h;
            src[i + 1] = s / v * 100f;
            src[i + 2] = v;
            src[i + 3] = a;
            i += 4;
        }
        return src;
    }

    public byte[] rgba2rgb(byte[] src) {
        byte[] dest = new byte[(src.length >> 2) * 3];
        int i = 0;
        int j = 0;
        while (i < src.length) {
            dest[j] = src[i];
            dest[j + 1] = src[i + 1];
            dest[j + 2] = src[i + 2];
            i += 4;
            j += 3;
        }
        return dest;
    }

    public byte[] rgb2rgba(byte[] src) {
        byte[] dest = new byte[(src.length / 3) << 2];
        int i = 0;
        int j = 0;
        while (j < src.length) {
            dest[i] = src[j];
            dest[i + 1] = src[j + 1];
            dest[i + 2] = src[j + 2];
            i += 4;
            j += 3;
        }
        return dest;
    }

    public float[] rgba2hsla(byte[] src) {
        return hsva2hsla(rgba2hsva(src));
    }

    public byte[] hsla2rgba(float[] src) {
        return hsva2rgba(hsla2hsva(src));
    }
}
