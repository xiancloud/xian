package info.xiancloud.plugin.dao.mongodb;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.init.shutdown.ShutdownHook;
import info.xiancloud.core.util.Reflection;
import info.xiancloud.core.util.StringUtil;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class Mongo implements ShutdownHook {
    private static volatile MongoDatabase DEFAULT_DATABASE;
    private static volatile MongoClient DEFAULT_CLIENT;
    private static final Object LOCK = new Object();

    public static <T> MongoCollection<T> getCollection(String collectionName, Class<T> tClass) {
        //没时间做验证了，我怕它不是并发安全的，所以这里还是改回来非单例吧。
        return getOrInitDefaultDatabase().getCollection(collectionName, tClass);
    }

    @Override
    public boolean shutdown() {
        if (DEFAULT_DATABASE != null) {
            DEFAULT_CLIENT.close();
        }
        return true;
    }

    /**
     * Get default mongodb database reference or initiate it if not initialized.
     *
     * @return the default mongodb database reference
     */
    public static MongoDatabase getOrInitDefaultDatabase() {
        String connectionString = XianConfig.get("mongodb_connection_string");
        String database = XianConfig.get("mongodb_database");
        return getOrInitDefaultDatabase(connectionString, database);
    }

    /**
     * Get default mongodb database reference or initiate it if not initialized.
     *
     * @param connectionString MongoDB standard connection string
     * @param database         mongodb database name
     * @return MongoDB mongodb client database reference.
     */
    public static MongoDatabase getOrInitDefaultDatabase(String connectionString, String database) {
        if (DEFAULT_DATABASE == null) {
            synchronized (LOCK) {
                if (DEFAULT_DATABASE == null) {
                    if (!StringUtil.isEmpty(connectionString)) {
                        DEFAULT_CLIENT = MongoClients.create(connectionString);
                        CodecRegistry pojoCodecRegistry = fromRegistries(
                                /*fromCodecs(new StringCodecExt()),*/
                                MongoClientSettings.getDefaultCodecRegistry(),
                                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
                        DEFAULT_DATABASE = DEFAULT_CLIENT.getDatabase(database).withCodecRegistry(pojoCodecRegistry);
                    } else {
                        throw new RuntimeException("No datasource configuration found for mongodb.");
                    }
                }
            }
        }
        return DEFAULT_DATABASE;
    }

    public static <T> Page<T> findPageByPageNumber(MongoCollection<T> collection, Bson filter, int pageNumber, int pageSize) {
        long total = collection.countDocuments(filter);
        Page<T> page = new Page<>();
        page.setPageSize(pageSize);
        int totalPage = new Double(Math.ceil((double) total / pageSize)).intValue();
        page.setTotalPage(totalPage);
        if(totalPage<pageNumber && totalPage !=0){
            pageNumber = totalPage;
        }
        page.setPageNumber(pageNumber);
        page.setTotal(total);
        int skip = (pageNumber - 1) * pageSize;
        collection.find(filter).skip(skip).limit(pageSize).forEach((Consumer<T>) page.getList()::add);
        return page;
    }

    public static <T> Page<T> findPageByPageNumber(MongoCollection<T> collection, Bson filter, Bson sort, int pageNumber, int pageSize) {
        long total = collection.countDocuments(filter);

        Page<T> page = new Page<>();
        page.setPageSize(pageSize);
        int totalPage = new Double(Math.ceil((double) total / pageSize)).intValue();
        page.setTotalPage(totalPage);
        if(totalPage<pageNumber && totalPage !=0){
            pageNumber = totalPage;
        }
        page.setPageNumber(pageNumber);
        page.setTotal(total);
        int skip = (pageNumber - 1) * pageSize;
        collection.find(filter).sort(sort).skip(skip).limit(pageSize).forEach((Consumer<T>) page.getList()::add);
        return page;
    }

    public static <T> Page<T> findPageBySkip(MongoCollection<T> collection, Bson filter, long skip, long limit) {
        long total = collection.countDocuments(filter);
        Page<T> page = new Page<>();
        page.setPageSize(new Long(limit).intValue());
        page.setTotalPage(Long.valueOf(total / limit).intValue());
        page.setPageNumber(Long.valueOf(skip / limit + 1).intValue());
        page.setTotal(total);
        collection.find(filter).forEach((Consumer<T>) page.getList()::add);
        return page;
    }

    public static <T> Page<T> findPageBySkip(MongoCollection<T> collection, Bson filter, Bson sort, long skip, long limit) {
        long total = collection.countDocuments(filter);
        Page<T> page = new Page<>();
        page.setPageSize(new Long(limit).intValue());
        page.setTotalPage(Long.valueOf(total / limit).intValue());
        page.setPageNumber(Long.valueOf(skip / limit + 1).intValue());
        page.setTotal(total);
        collection.find(filter).sort(sort).forEach((Consumer<T>) page.getList()::add);
        return page;
    }


    public static class Page<T> {

        /**
         * list result of this page
         */
        private List<T> list = new ArrayList<>();
        /**
         * page number
         */
        private int pageNumber=1;
        /**
         * result amount of this page
         */
        private int pageSize=10;
        /**
         * total page
         */
        private int totalPage;
        /**
         * total
         */
        private long total;

        public Page() {
        }

        public Page(int pageNumber, int pageSize) {
            this.pageNumber = pageNumber;
            this.pageSize = pageSize;
        }

        public List<T> getList() {
            return list;
        }

        public void setList(List<T> list) {
            this.list = list;
        }

        public int getPageNumber() {
            return pageNumber;
        }

        public void setPageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        @Override
        public String toString() {
            //serialize to json string
            return Reflection.toType(this, String.class);
        }
    }

}
