package info.xiancloud.core.test.output_test.stream;

import info.xiancloud.core.*;
import info.xiancloud.core.message.SingleRxXian;
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
        public void execute(UnitRequest request, Handler<UnitResponse> handler) {
            try {
                handler.handle(UnitResponse.createSuccess(new FileInputStream("/Users/happyyangyuan/Downloads/zz.txt")));
            } catch (FileNotFoundException e) {
                handler.handle(UnitResponse.createException(e));
            }
        }

        @Override
        public Group getGroup() {
            return TestGroup.singleton;
        }
    }

    public static void main(String[] args) {
        //将流按行处理
        SingleRxXian
                .call("test", "streamUnitResponseTest")
                .subscribe(unitResponse -> {
                    unitResponse.processStreamLineByLine(new Function<String, Object>() {
                        @Override
                        public Object apply(String line) {
                            System.out.println(line);
                            return null;
                        }
                    });
                })
        ;

        //将流分段处理
        SingleRxXian
                .call("test", "streamUnitResponseTest")
                .subscribe(unitResponse -> {
                    unitResponse.processStreamPartByPart("[{]", part -> {
                        System.out.println(part);
                        return null;
                    });
                })
        ;


        //将流写入到本地新文件内
        SingleRxXian
                .call("test", "streamUnitResponseTest")
                .subscribe(unitResponse -> {
                    try {
                        FileUtil.copyFile(unitResponse.dataToStream(), "/path/to/your/new/file");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });


    }
}
