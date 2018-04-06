package info.xiancloud.core;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.Reflection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link Unit}'s input parameter descriptions，see {@link Unit#getInput()}
 *
 * @author ads、happyyangyuan 2017-03-10 增加xhash、sequential属性
 */
public class Input {
    private List<Obj> objList = new ArrayList<>();

    public static class Obj {

        //this default constructor is used for UnitBean deserialization, please do not delete!
        Obj() {
        }

        Obj(String name, Class clazz, String description) {
            this.name = name;
            this.clazz = clazz;
            this.description = description;
        }

        Obj(String name, Class clazz, String description, boolean required) {
            this.name = name;
            this.clazz = clazz;
            this.description = description;
            this.required = required;
        }

        private String name;
        private Class clazz;
        private String description;
        private boolean required = false;// defaults to not required.
        private boolean xhash = false;//whether or not to use the consistent hash algorithm while sending messages.
        private boolean sequential = false;//whether or not to honor the sequence while dealing with received messages.

        public String getName() {
            return name;
        }

        public Obj setName(String name) {
            this.name = name;
            return this;
        }

        public Class getClazz() {
            return clazz;
        }

        public Obj setClazz(String clazzName) {
            try {
                this.clazz = Reflection.forName(clazzName);
            } catch (ClassNotFoundException e) {
                LOG.warn("无法识别的参数类型：" + clazzName);
                this.clazz = String.class;
            }
            return this;
        }

        public String getDescription() {
            return description;
        }

        public Obj setDescription(String description) {
            this.description = description;
            return this;
        }

        public Obj setRequired(boolean required) {
            this.required = required;
            return this;
        }

        public boolean isRequired() {
            return required;
        }

        @Override
        final public String toString() {
            return JSONObject.toJSONString(this);
        }

        public boolean isXhash() {
            return xhash;
        }

        public Obj setXhash(boolean xhash) {
            this.xhash = xhash;
            return this;
        }

        public boolean isSequential() {
            return sequential;
        }

        public Obj setSequential(boolean sequential) {
            this.sequential = sequential;
            return this;
        }
    }

    /**
     * Create a new Input instance.
     * Both use the default constructor method or this builder method is OK.
     */
    public static Input create() {
        return new Input();
    }

    /**
     * generate an input object from the bean.
     *
     * @param requestBean the request bean
     * @param <Request>   the request bean type
     * @return a newly created input definition.
     */
    public static <Request> Input create(Request requestBean) {
        if (requestBean == null)
            throw new IllegalArgumentException("request bean should not be null!");
        Class requestClass = requestBean.getClass();
        return create(requestClass);
    }

    /**
     * @param beanClass the bean class
     * @param <Request> the bean type
     * @return the newly created input definition.
     */
    public static <Request> Input create(Class<? extends Request> beanClass) {
        if (beanClass == null) throw new IllegalArgumentException("bean class should not be null");
        Input input = Input.create();
        List<Field> allFields = Reflection.getAllFields(beanClass);
        for (Field field : allFields) {
            //TODO read from annotations.
            input.add(field.getName(), field.getType(), null);
        }
        return input;
    }

    public Input add(String name, Class clazz, String description) {
        objList.add(new Obj(name, clazz, description));
        return this;
    }

    /**
     * 这种入参设计人为出错的可能性太大，请使用{@link #add(String, Class, String, RequiredOrNot) 可读性更好的枚举值参数}
     * 因此这里改为私有方法
     */
    private Input add(String name, Class clazz, String description, boolean required) {
        objList.add(new Obj(name, clazz, description, required));
        return this;
    }

    public Input add(String name, Class clazz, String description, RequiredOrNot requiredOrNot) {
        boolean required = (requiredOrNot == RequiredOrNot.REQUIRED);
        return add(name, clazz, description, required);
    }

    public Input add(String name, Class clazz, String description, RequiredOrNot requiredOrNot, XhashOrNot xhashOrNot) {
        boolean required = (requiredOrNot == RequiredOrNot.REQUIRED),
                xhash = (xhashOrNot == XhashOrNot.XHASH);
        objList.add(new Obj(name, clazz, description, required).setXhash(xhash));
        return this;
    }

    public Input add(String name, Class clazz, String description, RequiredOrNot requiredOrNot, XhashOrNot xhashOrNot, SequentialOrNot sequentialOrNot) {
        boolean required = (requiredOrNot == RequiredOrNot.REQUIRED),
                xhash = (xhashOrNot == XhashOrNot.XHASH),
                sequential = (sequentialOrNot == SequentialOrNot.SEQUENTIAL);
        objList.add(new Obj(name, clazz, description, required).setXhash(xhash).setSequential(sequential));
        return this;
    }

    public Input add(Obj obj) {
        objList.add(obj);
        return this;
    }

    public List<Obj> getList() {
        return objList;
    }

    public Input setList(List<Obj> objs) {
        objList = objs;
        return this;
    }

    public Input add(Class<? extends Bean> beanClass) {
        Field[] fields = beanClass.getDeclaredFields();
        if (fields != null && fields.length > 0) {
            InputBind inputBind;
            for (Field field : fields) {
                if (field.getName().startsWith("this$")) {
                    continue;
                }
                inputBind = field.getAnnotation(InputBind.class);
                if (inputBind == null) {
                    add(field.getName(), field.getType(), "");
                } else if (!inputBind.disable()) {
                    add(field.getName(), field.getType(), inputBind.description(), inputBind.required());
                }
            }
        }
        return this;
    }

    public Input addAll(Input input) {
        getList().addAll(input.getList());
        return this;
    }


    /**
     * sequential属性视图；
     * 注册服务时，不会序列化此属性，与inputObjs内的属性重复了，保留较全的inputObjs属性
     */
    @JSONField(serialize = false)
    public boolean isSequential() {
        if (getList() != null) {
            for (Input.Obj obj : getList()) {
                if (obj.isSequential()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * sequential属性视图；
     * 注册服务时，不要序列化此属性，与inputObjs内的属性重复了，保留较全的inputObjs属性.
     */
    @JSONField(serialize = false)
    public List<Obj> getSequential() {
        List<Obj> sequential = new ArrayList<>();
        if (getList() != null) {
            for (Obj obj : getList()) {
                if (obj.isSequential()) {
                    sequential.add(obj);
                }
            }
        }
        return sequential;
    }

    /**
     * sequential属性名视图；
     * 注册服务时，不要序列化此属性，与inputObjs内的属性重复了，保留较全的inputObjs属性.
     */
    @JSONField(serialize = false)
    public String[] getSequentialNames() {
        List<Obj> sequential = getSequential();
        String[] sequentialNames = new String[sequential.size()];
        for (int i = 0; i < sequential.size(); i++) {
            sequentialNames[i] = sequential.get(i).getName();
        }
        return sequentialNames;
    }


    /**
     * xhash属性视图
     */
    @JSONField(serialize = false)
    public boolean isXhash() {
        if (getList() != null) {
            for (Obj obj : getList()) {
                if (obj.isXhash()) return true;
            }
        }

        return false;
    }

    /**
     * xhash属性视图，如果不存在xhash属性，那么返回空列表 而不是null
     */
    @JSONField(serialize = false)
    public List<Obj> getXhash() {
        List<Obj> xhash = new ArrayList<>();
        if (getList() != null) {
            for (Obj obj : getList()) {
                if (obj.isXhash()) {
                    xhash.add(obj);
                }
            }
        }
        return xhash;
    }

    /**
     * xhash属性名视图，如果不存在xhash属性，那么返回空数组，而不是null
     */
    @JSONField(serialize = false)
    public String[] getXhashNames() {
        List<Obj> xhash = getXhash();
        String[] xhashNames = new String[xhash.size()];
        for (int i = 0; i < xhash.size(); i++) {
            xhashNames[i] = xhash.get(i).getName();
        }
        return xhashNames;
    }

    /**
     * required参数视图，如果不存在毕传参数，那么返回空数组，而不是返回null
     */
    @JSONField(serialize = false)
    public List<Obj> getRequired() {
        List<Obj> requred = new ArrayList<>();
        if (getList() != null) {
            for (Obj obj : getList()) {
                if (obj.isRequired()) {
                    requred.add(obj);
                }
            }
        }
        return requred;
    }

    /**
     * @return required参数名视图，如果不存在毕传参数，那么返回空数组，而不是返回null
     */
    @JSONField(serialize = false)
    public String[] getRequiredNames() {
        List<Obj> required = getRequired();
        String[] requiredNames = new String[required.size()];
        for (int i = 0; i < required.size(); i++) {
            requiredNames[i] = required.get(i).getName();
        }
        return requiredNames;
    }

}
