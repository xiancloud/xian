package info.xiancloud.core.test.output_test.stream;

import info.xiancloud.core.message.SyncXian;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.file.FileUtil;
import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.SyncXian;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.test.TestGroup;
import info.xiancloud.core.util.file.FileUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Function;

/**
 * This unit is for test only.
 *
 * @author happyyangyuan
 */
public class StreamUnitResponseDemo {

    public static class StreamUnitResponseTest implements Unit {

        @Override
        public String getName() {
            return "streamUnitResponseTest";
        }

        @Override
        public UnitMeta getMeta() {
            return UnitMeta.create().setPublic(false);
        }

        @Override
        public Input getInput() {
            return null;
        }

        @Override
        public UnitResponse execute(UnitRequest msg) {
            try {
                return UnitResponse.success(new FileInputStream("/Users/happyyangyuan/Downloads/zz.txt"));
            } catch (FileNotFoundException e) {
                return UnitResponse.exception(e);
            }
        }

        @Override
        public Group getGroup() {
            return TestGroup.singleton;
        }
    }

    public static void main(String[] args) {
        //将流按行处理
        UnitResponse unitResponseObject = SyncXian.call("test", "streamUnitResponseTest");
        unitResponseObject.processStreamLineByLine(new Function<String, Object>() {
            @Override
            public Object apply(String line) {
                System.out.println(line);
                return null;
            }
        });


        //将流分段处理
        UnitResponse unitResponseObject1 = SyncXian.call("test", "streamUnitResponseTest");
        unitResponseObject1.processStreamPartByPart("[{]", new Function<String, Object>() {
            @Override
            public Object apply(String part) {
                System.out.println(part);
                return null;
            }
        });


        //将流写入到本地新文件内
        UnitResponse unitResponseObject2 = SyncXian.call("test", "streamUnitResponseTest");
        try {
            FileUtil.copyFile(unitResponseObject2.dataToStream(), "/path/to/your/new/file");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
