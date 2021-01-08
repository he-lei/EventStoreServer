import com.EventStoreServer.entity.RoughEventInfo;
import com.EventStoreServer.service.impl.GetServiceImpl;
import com.EventStoreServer.service.impl.SaveServiceImpl;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class SaveAndGetServiceTest {
    private static SaveServiceImpl saveService = new SaveServiceImpl();
    private static GetServiceImpl getService = new GetServiceImpl();

    public static void main(String[] args) {
//        RoughEventInfo roughEventInfo = new RoughEventInfo();
//        long startTime = System.currentTimeMillis();
//        for (int i = 1; i < 100000; i++) {
//            int x = ThreadLocalRandom.current().nextInt(0, 10);
//            String s = "业务对象" + x;
//            roughEventInfo.setBusinessObjectName(s);
//            int y = ThreadLocalRandom.current().nextInt(0, 100);
//            roughEventInfo.setBusinessObjectUUID(String.valueOf(y));
////            roughEventInfo.setCompleteDataAddress(0);
//            roughEventInfo.setEventNumber(0);
//            roughEventInfo.setEventType();
//            roughEventInfo.setEventTime(x);
//            saveService.saveRoughEventInfo(roughEventInfo);
//        }
//        long endTime = System.currentTimeMillis();
//        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
//        String s = getService.getRoughEventInfo("第383次插入", "20");
//        System.out.println(s);
//        for (int i = 0; i < 100; i++) {
//            String s = "第" + i + "次插入";
//            roughEventInfo.setBusinessObjectName(s);
//            saveService.saveRoughEventInfo(roughEventInfo);
//        }
        // 测试getAll
//        long startTime = System.currentTimeMillis();
//        ArrayList<String> s = getService.getAll("第10000次插入", "310");
//        long endTime = System.currentTimeMillis();
//        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
//        System.out.println(s);
    }
}
