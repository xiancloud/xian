package info.xiancloud.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Serialization based on java.
 */
public class SerializeUtil {

    private static final String UTF8 = "UTF-8";

    public static String serialize(Object object) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return new String(bytes, UTF8);
        } catch (Exception e) {
            LOG.error("Serialization failure.", e);
        }
        return null;
    }

    public static Object deserialize(String str) {
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(str.getBytes(UTF8));
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*public static class Test implements Serializable{
        public String abs;
    }

    public static void main(String[] args){
        Test t = new Test();
        t.abs = "123";
        System.out.println(SerializeUtil.serialize(""));
        System.out.println(SerializeUtil.deserialize(SerializeUtil.serialize("123")));
    }*/
}
