package info.xiancloud.plugin.stream;

import info.xiancloud.plugin.message.UnitResponse;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author happyyangyuan
 */
public class StreamUnitResponseObjectTest {

    @Test
    public void printResponseStreamLineByLine() throws FileNotFoundException {
        UnitResponse unitResponseObject = UnitResponse.success(new FileInputStream("/Users/happyyangyuan/Downloads/zz.txt"));
        unitResponseObject.processStreamLineByLine(line -> {
            System.out.println(line);
            return null;
        });
    }

    @Test
    public void printResponseStreamPartByPart() throws FileNotFoundException {
        UnitResponse unitResponseObject = UnitResponse.success(new FileInputStream("/Users/happyyangyuan/Downloads/zz.txt"));
        unitResponseObject.processStreamPartByPart("[{]", part -> {
            System.out.println(part);
            return null;
        });
    }
}
