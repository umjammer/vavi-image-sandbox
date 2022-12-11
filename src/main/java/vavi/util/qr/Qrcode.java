/*
 * Copyright (c) 2003 by Y.Swetake, All rights reserved.
 *
 * Modified by Naohide Sano
 */

package vavi.util.qr;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.TreeBidiMap;


/**
 * QRcode class library.
 * This version supports QRcode model2 version 1-40.
 * Some functions are not supported.<BR>
 *
 * @author Y.Swetake
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.50b8 original version <br>
 *          0.60 040903 nsano refactor <br>
 */
public class Qrcode {

    /** */
    private static final String basePath = "resources";
    /** */
    public static final int VERSION_AUTO = 0;
    /** */
    public static final char ERROR_CORRECTION_LEVEL_L = 'L';
    /** */
    public static final char ERROR_CORRECTION_LEVEL_M = 'M';
    /** */
    public static final char ERROR_CORRECTION_LEVEL_Q = 'Q';
    /** */
    public static final char ERROR_CORRECTION_LEVEL_H = 'H';
    /** */
    private char errorCorrectionLevel;
    /** */
    private static final BidiMap levels = new TreeBidiMap();
    /* */
    static {
        levels.put(0, ERROR_CORRECTION_LEVEL_M);
        levels.put(1, ERROR_CORRECTION_LEVEL_L);
        levels.put(3, ERROR_CORRECTION_LEVEL_Q);
        levels.put(2, ERROR_CORRECTION_LEVEL_H);
    }
    /** */
    public static final char ENCODING_NUMERIC = 'N';
    /** */
    public static final char ENCODING_ASCII = 'A';
    /** */
    public static final char ENCODING_BYTE = 'B';
    /** */
    private char encoding;
    /** */
    private int version;
    /** */
    private int structureAppendN;
    /** */
    private int structureAppendM;
    /** */
    private int structureAppendParity;
    /** */
    @SuppressWarnings("unused")
    private String structureAppendOriginalData;

    /**
     * デフォルト値で QR コードジェネレータを作成します。
     * <li>エラー訂正レベル ... 'M'
     * <li>エンコーディング ... バイトモード
     * <li>バージョン ... 自動設定
     */
    public Qrcode() {
        errorCorrectionLevel = ERROR_CORRECTION_LEVEL_M;
        encoding = ENCODING_BYTE;
        version = VERSION_AUTO;

        structureAppendN = 0;
        structureAppendM = 0;
        structureAppendParity = 0;
        structureAppendOriginalData = "";
    }

    /**
     * エラー訂正レベルを設定します。
     * @param errorCorrectionLevel エラー訂正レベル('L', 'M', 'Q', 'H')
     */
    public void setErrorCorrectionLevel(char errorCorrectionLevel) {
        this.errorCorrectionLevel = errorCorrectionLevel;
    }

    /**
     * エラー訂正レベルを取得します。
     * @return エラー訂正レベル('L', 'M', 'Q', 'H')
     */
    public char getErrorCorrectionLevel() {
        return errorCorrectionLevel;
    }

    /**
     * バージョンを取得します。
     * @return バージョン 0 から 40 の整数、0 の場合は自動設定
     */
    public int getVersion() {
        return version;
    }

    /**
     * バージョンを設定します。
     * @param version  0 から 40 の整数、 0 を設定すると自動設定
     */
    public void setVersion(int version) {
        if (version >= 0 && version <= 40) {
            this.version = version;
        }
    }

    /**
     * エンコードモードを設定します。
     * 'N': 数字モード  'A': 英数字モード　その他: byte モード
     * @param encoding エンコードモード('N', 'A' or other)
     */
    public void setEncoding(char encoding) {
        this.encoding = encoding;
    }

    /**
     * エンコードモードを取得します。
     * @return エンコードモード ('N', 'A' or other)
     */
    public char getEncoding() {
        return encoding;
    }

    /**
     * 連結関連のメソッドです。
     * (試験導入)
     */
    public void setStructureAppend(int m, int n, int p) {
        if (n > 1 && n <= 16 && m > 0 && m <= 16 && p >= 0 && p <= 255) {
            structureAppendM = m;
            structureAppendN = n;
            structureAppendParity = p;
        }
    }

    /**
     * 連結に用いるパリティを算出します。
     * (試験導入)
     */
    public int calculateStructureAppendParity(byte[] originalData) {
        int originalDataLength;
        int i = 0;
        int structureAppendParity = 0;

        originalDataLength = originalData.length;

        if (originalDataLength > 1) {
            structureAppendParity = 0;
            while (i < originalDataLength) {
                structureAppendParity = (structureAppendParity ^ originalData[i]);
                i++;
            }
        } else {
            structureAppendParity = -1;
        }
        return structureAppendParity;
    }

    /** 英数字モード */
    private static final int[] codeWordNumPlusA = {
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2,
        2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4, 4, 4, 4, 4,
        4, 4, 4, 4, 4, 4, 4, 4, 4
    };

    /** 数字モード */
    private static final int[] codeWordNumPlusN = {
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2,
        2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4, 4, 4, 4, 4,
        4, 4, 4, 4, 4, 4, 4, 4, 4
    };

    /** バイトモード */
    private static final int[] codeWordNumPlusDefault = {
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 8, 8, 8, 8, 8,
        8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
        8, 8, 8, 8, 8, 8, 8, 8, 8
    };

    /** 空値 */
    private static final boolean[][] nullResult = {
        { false }
    };

    /** */
    private static final byte[] formatInformationX1 = {
        0, 1, 2, 3, 4, 5, 7, 8, 8, 8, 8, 8, 8,
        8, 8
    };

    /** */
    private static final byte[] formatInformationY1 = {
        8, 8, 8, 8, 8, 8, 8, 8, 7, 5, 4, 3, 2,
        1, 0
    };

    /** */
    private static final int[] matrixRemainBit = {
        0, 0, 7, 7, 7, 7, 7, 0, 0, 0, 0, 0, 0, 0, 3,
        3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 3, 3,
        3, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0
    };

    /** */
    private static final int[] maxCodeWordsArray = {
        0, 26, 44, 70, 100, 134, 172, 196, 242,
        292, 346, 404, 466, 532, 581, 655, 733,
        815, 901, 991, 1085, 1156, 1258, 1364,
        1474, 1588, 1706, 1828, 1921, 2051, 2185,
        2323, 2465, 2611, 2761, 2876, 3034, 3196,
        3362, 3532, 3706
    };

    /** */
    private static final int[][] maxDataBitsArray = { {
        0, 128, 224, 352, 512, 688, 864, 992,
        1232, 1456, 1728, 2032, 2320, 2672,
        2920, 3320, 3624, 4056, 4504, 5016,
        5352, 5712, 6256, 6880, 7312, 8000,
        8496, 9024, 9544, 10136, 10984, 11640,
        12328, 13048, 13800, 14496, 15312,
        15936, 16816, 17728, 18672
    }, {
        0, 152, 272, 440, 640, 864, 1088,
        1248, 1552, 1856, 2192, 2592, 2960,
        3424, 3688, 4184, 4712, 5176, 5768,
        6360, 6888, 7456, 8048, 8752, 9392,
        10208, 10960, 11744, 12248, 13048,
        13880, 14744, 15640, 16568, 17528,
        18448, 19472, 20528, 21616, 22496,
        23648
    }, {
        0, 72, 128, 208, 288, 368, 480, 528,
        688, 800, 976, 1120, 1264, 1440, 1576,
        1784, 2024, 2264, 2504, 2728, 3080,
        3248, 3536, 3712, 4112, 4304, 4768,
        5024, 5288, 5608, 5960, 6344, 6760,
        7208, 7688, 7888, 8432, 8768, 9136,
        9776, 10208
    }, {
        0, 104, 176, 272, 384, 496, 608, 704,
        880, 1056, 1232, 1440, 1648, 1952,
        2088, 2360, 2600, 2936, 3176, 3560,
        3880, 4096, 4544, 4912, 5312, 5744,
        6032, 6464, 6968, 7288, 7880, 8264,
        8920, 9368, 9848, 10288, 10832, 11408,
        12016, 12656, 13328
    } };

    /** */
    private static final String[] formatInformationArray = {
        "101010000010010",
        "101000100100101",
        "101111001111100",
        "101101101001011",
        "100010111111001",
        "100000011001110",
        "100111110010111",
        "100101010100000",
        "111011111000100",
        "111001011110011",
        "111110110101010",
        "111100010011101",
        "110011000101111",
        "110001100011000",
        "110110001000001",
        "110100101110110",
        "001011010001001",
        "001001110111110",
        "001110011100111",
        "001100111010000",
        "000011101100010",
        "000001001010101",
        "000110100001100",
        "000100000111011",
        "011010101011111",
        "011000001101000",
        "011111100110001",
        "011101000000110",
        "010010010110100",
        "010000110000011",
        "010111011011010",
        "010101111101101"
    };

    /**
     * 与えられたデータ列からQR コードエンコードデータを
     * boolean 二次元配列で返します。
     * @param qrcodeData エンコードするデータ
     * @return QR コードエンコードデータ
     */
    public boolean[][] toQrcode(byte[] qrcodeData) {
        int dataLength;
        int dataCounter = 0;

        dataLength = qrcodeData.length;

        int[] dataValue = new int[dataLength + 32];
        byte[] dataBits = new byte[dataLength + 32];

        if (dataLength <= 0) {
            return nullResult;
        }

        if (structureAppendN > 1) {
            dataValue[0] = 3;
            dataBits[0] = 4;

            dataValue[1] = structureAppendM - 1;
            dataBits[1] = 4;

            dataValue[2] = structureAppendN - 1;
            dataBits[2] = 4;

            dataValue[3] = structureAppendParity;
            dataBits[3] = 8;

            dataCounter = 4;
        }
        dataBits[dataCounter] = 4;

        // determine encode mode
        int[] codewordNumPlus;
        int codewordNumCounterValue;

        switch (encoding) {
        // alphanumeric mode
        case 'A':
            codewordNumPlus = codeWordNumPlusA;
            dataValue[dataCounter] = 2;
            dataCounter++;
            dataValue[dataCounter] = dataLength;
            dataBits[dataCounter] = 9;
            codewordNumCounterValue = dataCounter;

            dataCounter++;
            for (int i = 0; i < dataLength; i++) {
                char chr = (char) qrcodeData[i];
                byte chrValue = 0;
                if ((chr >= 48) && (chr < 58)) {
                    chrValue = (byte) (chr - 48);
                } else {
                    if ((chr >= 65) && (chr < 91)) {
                        chrValue = (byte) (chr - 55);
                    } else {
                        if (chr == 32) {
                            chrValue = 36;
                        }
                        if (chr == 36) {
                            chrValue = 37;
                        }
                        if (chr == 37) {
                            chrValue = 38;
                        }
                        if (chr == 42) {
                            chrValue = 39;
                        }
                        if (chr == 43) {
                            chrValue = 40;
                        }
                        if (chr == 45) {
                            chrValue = 41;
                        }
                        if (chr == 46) {
                            chrValue = 42;
                        }
                        if (chr == 47) {
                            chrValue = 43;
                        }
                        if (chr == 58) {
                            chrValue = 44;
                        }
                    }
                }
                if ((i % 2) == 0) {
                    dataValue[dataCounter] = chrValue;
                    dataBits[dataCounter] = 6;
                } else {
                    dataValue[dataCounter] = (dataValue[dataCounter] * 45) +
                                             chrValue;
                    dataBits[dataCounter] = 11;
                    if (i < (dataLength - 1)) {
                        dataCounter++;
                    }
                }
            }
            dataCounter++;
            break;

        // numeric mode
        case 'N':
            codewordNumPlus = codeWordNumPlusN;
            dataValue[dataCounter] = 1;
            dataCounter++;
            dataValue[dataCounter] = dataLength;

            dataBits[dataCounter] = 10; // #version 1-9
            codewordNumCounterValue = dataCounter;

            dataCounter++;
            for (int i = 0; i < dataLength; i++) {
                if ((i % 3) == 0) {
                    dataValue[dataCounter] = qrcodeData[i] - 0x30;
                    dataBits[dataCounter] = 4;
                } else {
                    dataValue[dataCounter] = (dataValue[dataCounter] * 10) +
                                             (qrcodeData[i] - 0x30);

                    if ((i % 3) == 1) {
                        dataBits[dataCounter] = 7;
                    } else {
                        dataBits[dataCounter] = 10;
                        if (i < (dataLength - 1)) {
                            dataCounter++;
                        }
                    }
                }
            }
            dataCounter++;
            break;

        // byte
        default:
            codewordNumPlus = codeWordNumPlusDefault;
            dataValue[dataCounter] = 4;
            dataCounter++;
            dataValue[dataCounter] = dataLength;
            dataBits[dataCounter] = 8; // #version 1-9
            codewordNumCounterValue = dataCounter;

            dataCounter++;

            for (int i = 0; i < dataLength; i++) {
                dataValue[i + dataCounter] = (qrcodeData[i] & 0xff);
                dataBits[i + dataCounter] = 8;
            }
            dataCounter += dataLength;

            break;
        }

        int totalDataBits = 0;
        for (int i = 0; i < dataCounter; i++) {
            totalDataBits += dataBits[i];
        }

        int ec;
        switch (errorCorrectionLevel) {
        case ERROR_CORRECTION_LEVEL_L:
            ec = 1;
            break;
        case ERROR_CORRECTION_LEVEL_Q:
            ec = 3;
            break;
        case ERROR_CORRECTION_LEVEL_H:
            ec = 2;
            break;
        default:
            ec = 0;
        }

        int maxDataBits = 0;

        if (version == 0) {
            // auto version select
            version = 1;
            for (int i = 1; i <= 40; i++) {
                if ((maxDataBitsArray[ec][i]) >= (totalDataBits +
                    codewordNumPlus[version])) {
                    maxDataBits = maxDataBitsArray[ec][i];
                    break;
                }
                version++;
            }
        } else {
            maxDataBits = maxDataBitsArray[ec][version];
        }
        totalDataBits += codewordNumPlus[version];
        dataBits[codewordNumCounterValue] = (byte) (dataBits[codewordNumCounterValue] + codewordNumPlus[version]);

        int maxCodewords = maxCodeWordsArray[version];
//    int maxModules1side = 17 + (version << 2);

        // read version ECC data file
        int byte_num = matrixRemainBit[version] + (maxCodewords << 3);

        byte[] matrixX = new byte[byte_num];
        byte[] matrixY = new byte[byte_num];
        byte[] maskArray = new byte[byte_num];
        byte[] formatInformationX2 = new byte[15];
        byte[] formatInformationY2 = new byte[15];
        byte[] rsEccCodewords = new byte[1];
        byte[] rsBlockOrderTemp = new byte[128];

        try {
            String filename = basePath + "/qrv" + version + "_" + ec + ".dat";
            InputStream is = new BufferedInputStream(Qrcode.class.getResourceAsStream(filename));
            is.read(matrixX);
            is.read(matrixY);
            is.read(maskArray);
            is.read(formatInformationX2);
            is.read(formatInformationY2);
            is.read(rsEccCodewords);
            is.read(rsBlockOrderTemp);
            is.close();
            is.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        byte rsBlockOrderLength = 1;
        for (byte i = 1; i < 128; i++) {
            if (rsBlockOrderTemp[i] == 0) {
                rsBlockOrderLength = i;
                break;
            }
        }

        byte[] rsBlockOrder = new byte[rsBlockOrderLength];
        System.arraycopy(rsBlockOrderTemp, 0, rsBlockOrder, 0,
                         rsBlockOrderLength);

        int maxDataCodewords = maxDataBits >> 3;

        // read frame data
        int modules1Side = (4 * version) + 17;
        int matrixTotalBits = modules1Side * modules1Side;
        byte[] frameData = new byte[matrixTotalBits + modules1Side];

        try {
            String filename = basePath + "/qrvfr" + version + ".dat";
            InputStream is = new BufferedInputStream(Qrcode.class.getResourceAsStream(filename));
            is.read(frameData);
            is.close();
            is.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        // set terminator
        if (totalDataBits <= (maxDataBits - 4)) {
            dataValue[dataCounter] = 0;
            dataBits[dataCounter] = 4;
        } else {
            if (totalDataBits < maxDataBits) {
                dataValue[dataCounter] = 0;
                dataBits[dataCounter] = (byte) (maxDataBits - totalDataBits);
            } else {
                if (totalDataBits > maxDataBits) {
                    System.out.println("overflow");
                }
            }
        }

        byte[] dataCodewords = divideDataBy8Bits(dataValue, dataBits,
                                                 maxDataCodewords);
        byte[] codewords = calculateRSECC(dataCodewords, rsEccCodewords[0],
                                          rsBlockOrder, maxDataCodewords,
                                          maxCodewords);

        // flash matrix
        byte[][] matrixContent = new byte[modules1Side][modules1Side];

        for (int i = 0; i < modules1Side; i++) {
            for (int j = 0; j < modules1Side; j++) {
                matrixContent[j][i] = 0;
            }
        }

        // attach data
        for (int i = 0; i < maxCodewords; i++) {
            byte codeword_i = codewords[i];
            for (int j = 7; j >= 0; j--) {
                int codewordBitsNumber = (i * 8) + j;

                matrixContent[matrixX[codewordBitsNumber] & 0xff][matrixY[codewordBitsNumber] &
                0xff] = (byte) ((255 * (codeword_i & 1)) ^
                        maskArray[codewordBitsNumber]);

                codeword_i = (byte) ((codeword_i & 0xff) >>> 1);
            }
        }

        for (int matrixRemain = matrixRemainBit[version];
             matrixRemain > 0; matrixRemain--) {
            int remainBitTemp = (matrixRemain + (maxCodewords * 8)) - 1;
            matrixContent[matrixX[remainBitTemp] & 0xff][matrixY[remainBitTemp] &
            0xff] = (byte) (255 ^ maskArray[remainBitTemp]);
        }

        // mask select
        byte maskNumber = selectMask(matrixContent,
                                     matrixRemainBit[version] +
                                     (maxCodewords * 8));
        byte maskContent = (byte) (1 << maskNumber);

        // format information
        byte formatInformationValue = (byte) ((ec << 3) | maskNumber);

        for (int i = 0; i < 15; i++) {
            byte content = Byte.parseByte(formatInformationArray[formatInformationValue].substring(i, i + 1));

            matrixContent[formatInformationX1[i] & 0xff][formatInformationY1[i] & 0xff] = (byte) (content * 255);
            matrixContent[formatInformationX2[i] & 0xff][formatInformationY2[i] & 0xff] = (byte) (content * 255);
        }

        boolean[][] out = new boolean[modules1Side][modules1Side];

        int c = 0;
        for (int i = 0; i < modules1Side; i++) {
            for (int j = 0; j < modules1Side; j++) {
                if (((matrixContent[j][i] & maskContent) != 0) ||
                    (frameData[c] == (char) 49)) {
                    out[j][i] = true;
                } else {
                    out[j][i] = false;
                }
                c++;
            }
            c++;
        }

        return out;
    }

    /**
     *
     * @param data
     * @param bits
     * @param maxDataCodeWords
     * @return byte[]
     */
    private static byte[] divideDataBy8Bits(int[] data, byte[] bits, int maxDataCodeWords) {
        /* divide Data By 8bit and add padding char */
        int l1 = bits.length;
        int l2;
        int codeWordsCounter = 0;
        int remainingBits = 8;
        int max = 0;
        int buffer;
        int bufferBits;
        boolean flag;

        if (l1 != data.length) {
        }
        for (byte bit : bits) {
            max += bit;
        }
        l2 = ((max - 1) / 8) + 1;

        byte[] codeWords = new byte[maxDataCodeWords];
        for (int i = 0; i < l2; i++) {
            codeWords[i] = 0;
        }
        for (int i = 0; i < l1; i++) {
            buffer = data[i];
            bufferBits = bits[i];
            flag = true;

            if (bufferBits == 0) {
                break;
            }
            while (flag) {
                if (remainingBits > bufferBits) {
                    codeWords[codeWordsCounter] = (byte) ((codeWords[codeWordsCounter] << bufferBits) |
                                                  buffer);
                    remainingBits -= bufferBits;
                    flag = false;
                } else {
                    bufferBits -= remainingBits;
                    codeWords[codeWordsCounter] = (byte) ((codeWords[codeWordsCounter] << remainingBits) |
                                                  (buffer >> bufferBits));

                    if (bufferBits == 0) {
                        flag = false;
                    } else {
                        buffer = (buffer & ((1 << bufferBits) - 1));
                        flag = true;
                    }
                    codeWordsCounter++;
                    remainingBits = 8;
                }
            }
        }
        if (remainingBits != 8) {
            codeWords[codeWordsCounter] = (byte) (codeWords[codeWordsCounter] << remainingBits);
        } else {
            codeWordsCounter--;
        }
        if (codeWordsCounter < (maxDataCodeWords - 1)) {
            flag = true;
            while (codeWordsCounter < (maxDataCodeWords - 1)) {
                codeWordsCounter++;
                if (flag) {
                    codeWords[codeWordsCounter] = -20;
                } else {
                    codeWords[codeWordsCounter] = 17;
                }
                flag = !(flag);
            }
        }
        return codeWords;
    }

    /**
     *
     * @param codeWords
     * @param rsEccCodeWords
     * @param rsBlockOrder
     * @param maxDataCodeWords
     * @param maxCodeWords
     * @return byte[]
     */
    private static byte[] calculateRSECC(byte[] codeWords,
                                         byte rsEccCodeWords,
                                         byte[] rsBlockOrder,
                                         int maxDataCodeWords,
                                         int maxCodeWords) {
        byte[][] rsCalTableArray = new byte[256][rsEccCodeWords];
        try {
            String filename = basePath + "/rsc" +
                    rsEccCodeWords + ".dat";
            InputStream is = new BufferedInputStream(Qrcode.class.getResourceAsStream(filename));
            for (int i = 0; i < 256; i++) {
                is.read(rsCalTableArray[i]);
            }
            is.close();
            is.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        // RS-ECC prepare
        int i = 0;
        int j = 0;
        int rsBlockNumber = 0;

        byte[][] rsTemp = new byte[rsBlockOrder.length][];
        byte[] res = new byte[maxCodeWords];
        System.arraycopy(codeWords, 0, res, 0, codeWords.length);

        i = 0;
        while (i < rsBlockOrder.length) {
            rsTemp[i] = new byte[(rsBlockOrder[i] & 0xff) - rsEccCodeWords];
            i++;
        }
        i = 0;
        while (i < maxDataCodeWords) {
            rsTemp[rsBlockNumber][j] = codeWords[i];
            j++;
            if (j >= ((rsBlockOrder[rsBlockNumber] & 0xff) - rsEccCodeWords)) {
                j = 0;
                rsBlockNumber++;
            }
            i++;
        }

        // RS-ECC main
        rsBlockNumber = 0;
        while (rsBlockNumber < rsBlockOrder.length) {
            byte[] rsTempData = rsTemp[rsBlockNumber].clone();

            int rsCodewords = (rsBlockOrder[rsBlockNumber] & 0xff);
            int rsDataCodewords = rsCodewords - rsEccCodeWords;

            j = rsDataCodewords;
            while (j > 0) {
                byte first = rsTempData[0];
                if (first != 0) {
                    byte[] leftChr = new byte[rsTempData.length - 1];
                    System.arraycopy(rsTempData, 1, leftChr, 0, rsTempData.length - 1);

                    byte[] cal = rsCalTableArray[(first & 0xff)];
                    rsTempData = calculateByteArrayBits(leftChr, cal, "xor");
                } else {
                    if (rsEccCodeWords < rsTempData.length) {
                        byte[] rsTempNew = new byte[rsTempData.length - 1];
                        System.arraycopy(rsTempData, 1, rsTempNew, 0, rsTempData.length - 1);
                        rsTempData = rsTempNew.clone();
                    } else {
                        byte[] rsTempNew = new byte[rsEccCodeWords];
                        System.arraycopy(rsTempData, 1, rsTempNew, 0, rsTempData.length - 1);
                        rsTempNew[rsEccCodeWords - 1] = 0;
                        rsTempData = rsTempNew.clone();
                    }
                }
                j--;
            }

            System.arraycopy(rsTempData, 0, res, codeWords.length + (rsBlockNumber * rsEccCodeWords), rsEccCodeWords);
            rsBlockNumber++;
        }
        return res;
    }

    /**
     *
     * @param xa
     * @param xb
     * @param ind
     * @return byte[]
     */
    private static byte[] calculateByteArrayBits(byte[] xa, byte[] xb, String ind) {
        int ll;
        int ls;
        byte[] res;
        byte[] xl;
        byte[] xs;

        if (xa.length > xb.length) {
            xl = xa.clone();
            xs = xb.clone();
        } else {
            xl = xb.clone();
            xs = xa.clone();
        }
        ll = xl.length;
        ls = xs.length;
        res = new byte[ll];

        for (int i = 0; i < ll; i++) {
            if (i < ls) {
                if (ind == "xor") {
                    res[i] = (byte) (xl[i] ^ xs[i]);
                } else {
                    res[i] = (byte) (xl[i] | xs[i]);
                }
            } else {
                res[i] = xl[i];
            }
        }
        return res;
    }

    /** */
    private static final int[] d4Value = {
        90, 80, 70, 60, 50, 40, 30, 20, 10, 0, 0, 10, 20, 30,
        40, 50, 60, 70, 80, 90, 90
    };

    /**
     *
     * @param matrixContent
     * @param maxCodeWordsBitWithRemain
     * @return byte
     */
    private static byte selectMask(byte[][] matrixContent,
                                   int maxCodeWordsBitWithRemain) {
        int l = matrixContent.length;
        int[] d1 = { 0, 0, 0, 0, 0, 0, 0, 0 };
        int[] d2 = { 0, 0, 0, 0, 0, 0, 0, 0 };
        int[] d3 = { 0, 0, 0, 0, 0, 0, 0, 0 };
        int[] d4 = { 0, 0, 0, 0, 0, 0, 0, 0 };

        int d2And = 0;
        int d2Or = 0;
        int[] d4Counter = { 0, 0, 0, 0, 0, 0, 0, 0 };

        for (int y = 0; y < l; y++) {
            int[] xData = { 0, 0, 0, 0, 0, 0, 0, 0 };
            int[] yData = { 0, 0, 0, 0, 0, 0, 0, 0 };
            boolean[] xD1Flag = {
                    false, false, false, false, false, false,
                    false, false
            };
            boolean[] yD1Flag = {
                    false, false, false, false, false, false,
                    false, false
            };

            for (int x = 0; x < l; x++) {
                if ((x > 0) && (y > 0)) {
                    d2And = matrixContent[x][y] & matrixContent[x - 1][y] &
                            matrixContent[x][y - 1] &
                            matrixContent[x - 1][y - 1] & 0xff;

                    d2Or = (matrixContent[x][y] & 0xff) |
                           (matrixContent[x - 1][y] & 0xff) |
                           (matrixContent[x][y - 1] & 0xff) |
                           (matrixContent[x - 1][y - 1] & 0xff);
                }

                for (int maskNumber = 0; maskNumber < 8; maskNumber++) {
                    xData[maskNumber] = ((xData[maskNumber] & 63) << 1) | (((matrixContent[x][y] & 0xff) >>> maskNumber) & 1);

                    yData[maskNumber] = ((yData[maskNumber] & 63) << 1) | (((matrixContent[y][x] & 0xff) >>> maskNumber) & 1);

                    if ((matrixContent[x][y] & (1 << maskNumber)) != 0) {
                        d4Counter[maskNumber]++;
                    }

                    if (xData[maskNumber] == 93) {
                        d3[maskNumber] += 40;
                    }

                    if (yData[maskNumber] == 93) {
                        d3[maskNumber] += 40;
                    }

                    if ((x > 0) && (y > 0)) {
                        if (((d2And & 1) != 0) || ((d2Or & 1) == 0)) {
                            d2[maskNumber] += 3;
                        }

                        d2And = d2And >> 1;
                        d2Or = d2Or >> 1;
                    }

                    if (((xData[maskNumber] & 0x1f) == 0) ||
                        ((xData[maskNumber] & 0x1f) == 0x1f)) {
                        if (x > 3) {
                            if (xD1Flag[maskNumber]) {
                                d1[maskNumber]++;
                            } else {
                                d1[maskNumber] += 3;
                                xD1Flag[maskNumber] = true;
                            }
                        }
                    } else {
                        xD1Flag[maskNumber] = false;
                    }
                    if (((yData[maskNumber] & 0x1f) == 0) ||
                        ((yData[maskNumber] & 0x1f) == 0x1f)) {
                        if (x > 3) {
                            if (yD1Flag[maskNumber]) {
                                d1[maskNumber]++;
                            } else {
                                d1[maskNumber] += 3;
                                yD1Flag[maskNumber] = true;
                            }
                        }
                    } else {
                        yD1Flag[maskNumber] = false;
                    }
                }
            }
        }

        int minValue = 0;
        byte res = 0;
        for (int maskNumber = 0; maskNumber < 8; maskNumber++) {
            d4[maskNumber] = d4Value[((20 * d4Counter[maskNumber]) / maxCodeWordsBitWithRemain)];

            int demerit = d1[maskNumber] + d2[maskNumber] + d3[maskNumber] + d4[maskNumber];

            if ((demerit < minValue) || (maskNumber == 0)) {
                res = (byte) maskNumber;
                minValue = demerit;
            }
        }
        return res;
    }
}

/* */
