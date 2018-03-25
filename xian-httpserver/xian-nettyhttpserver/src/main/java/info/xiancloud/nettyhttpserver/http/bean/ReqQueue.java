package info.xiancloud.nettyhttpserver.http.bean;

import info.xiancloud.core.util.LOG;
import io.netty.util.AttributeKey;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * http长连接请求队列,确保http响应的顺序与收到的请求顺序一致,每个http连接的都持有唯一一个ReqQueue对象
 *
 * @author happyyangyuan
 */
public class ReqQueue {

    private final Queue<Request> reqQueue = new ConcurrentLinkedQueue<>();
    private final Map<String, ResponseWrapper> msgId_response = new HashMap<>();

    public static final int RES_COUNT_THRESHOLD = 500;//防内存泄露
    public static final int TIMEOUT_IN_MILLIS = Integer.MAX_VALUE /*原超时时间为15*1000ms，现改为无限大，即不超时*/;
    public static final AttributeKey<ReqQueue> REQ_QUEUE = AttributeKey.valueOf("REQ_QUEUE_NAME");

    //增加并发安全机制,日后如遇到长连接处理速度极限可以考虑去掉synchronized关键字,但需要仔细验证并发安全问题
    public void writeAndFlush(ResponseWrapper... responses) {
        synchronized (this) {
            for (ResponseWrapper response : responses) {
                msgId_response.put(response.getRequest().getMsgId(), response);
            }
            toContinue();
            if (isTooMany()) {
                processTooMany();
            }
        }
    }

    private void toContinue() {
        Request peekedRequest = reqQueue.peek();
        if (peekedRequest != null && msgId_response.containsKey(peekedRequest.getMsgId())) {
            LOG.debug("当前" + peekedRequest.getMsgId() + "对应的请求已经收到响应!");
            peekedRequest.getChannelHandlerContext().writeAndFlush(msgId_response.remove(peekedRequest.getMsgId()));
            reqQueue.poll();
            toContinue();
        }
    }

    public void offer(Request request) {
        synchronized (this) {
            reqQueue.offer(request);
        }
    }

    public Request poll() {
        synchronized (this) {
            return reqQueue.poll();
        }
    }

    /**
     * 删除并返回超时未处理的request列表
     */
    public List<Request> removeTimeout() {
        synchronized (this) {
            List<Request> timeoutList = new ArrayList<>();
            Iterator<Request> iterator = reqQueue.iterator();
            while (iterator.hasNext()) {
                Request request = iterator.next();
                if (System.currentTimeMillis() - request.getReqReceivedTimeInMillis() > TIMEOUT_IN_MILLIS) {
                    timeoutList.add(request);
                    iterator.remove();
                }
            }
            return timeoutList;
        }
    }

    private boolean isTooMany() {
        return msgId_response.size() > RES_COUNT_THRESHOLD;
    }

    private void processTooMany() {
        LOG.error("response消息堆积过多:" + msgId_response.size(), new Exception());
    }


}
