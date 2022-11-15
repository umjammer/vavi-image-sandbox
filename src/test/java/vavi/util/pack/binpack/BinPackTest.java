package vavi.util.pack.binpack;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Disabled;


@Disabled("not implemented yet")
class BinPackerTest {

    public static void main(String[] args) {
        StringBuilder outbuf = new StringBuilder();

        List<BinPacker.Dim> rects = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter(",");
        int rotation_flag = scanner.nextInt();
        int textureSize = scanner.nextInt();
        while (scanner.hasNextInt()) {
            rects.add(new BinPacker.Dim(scanner.nextInt(), scanner.nextInt()));
        }
        scanner.close();

        BinPacker packer = new BinPacker();
        List<List<BinPacker.Rect>> packs = packer.pack(rects, textureSize, rotation_flag == 1);

        outbuf.append("[");
        for (List<BinPacker.Rect> pack : packs) {
            outbuf.append("[");
            for (BinPacker.Rect rect : pack) {
                outbuf.append("{\"id\":");
                outbuf.append(rect.id);
                outbuf.append(",\"x\":");
                outbuf.append(rect.x);
                outbuf.append(",\"y\":");
                outbuf.append(rect.y);
                outbuf.append(",\"rot\":");
                outbuf.append(rect.rotated);
                outbuf.append("},");
            }
            outbuf.setLength(outbuf.length() - 1);
            outbuf.append("],");
        }
        outbuf.setLength(outbuf.length() - 1);
        outbuf.append("]");
        System.out.println(outbuf.toString());
    }
}
