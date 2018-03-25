package info.xiancloud.qcloudcos.api.server;

import com.alibaba.fastjson.JSON;
import info.xiancloud.core.util.EnvUtil;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.qcloudcos.api.QCloudCosClient;
import info.xiancloud.qcloudcos.api.QCloudCosConfig;
import info.xiancloud.qcloudcos.api.request.GetObjectRequest;
import info.xiancloud.qcloudcos.api.request.PutObjectRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.Map;

public class HttpFileHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client connect to server ....");
        super.channelRegistered(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

        LOG.info("qcloud-xml-api netty服务接收请求 : " + request.uri());

        Map<String, String> map = RequestParser.urlParams(request);
        if (map.isEmpty()) {
            writeResponse(ctx, ResponseEntity.build(Code.ParamEmpty));
            return;
        }
        LOG.info("qcloud-xml-api netty服务获取到请求参数 : " + JSON.toJSONString(map));

        String op = map.get("op");
        String bucketName = map.get("bucketName");
        String cosPath = map.get("cosPath");
        if (StringUtil.isEmpty(op)) {
            writeResponse(ctx, ResponseEntity.build(Code.NeedOp));
            return;
        }

        if (StringUtil.isEmpty(bucketName)) {
            writeResponse(ctx, ResponseEntity.build(Code.NeedBuctName));
            return;
        }

        if (StringUtil.isEmpty(cosPath)) {
            writeResponse(ctx, ResponseEntity.build(Code.NeedCosPath));
            return;
        }

        if (!QCloudCosConfig.validBucket(bucketName)) {
            writeResponse(ctx, ResponseEntity.build(Code.BuctNameConfig));
            return;
        }

        ResponseEntity response = null;
        if (op.equals(Op.PUT.lowerName())) {
            response = putObject(request, bucketName, cosPath);
        } else if (op.equals(Op.GET.lowerName())) { // getObject
            byte[] bytes = getObject(request, bucketName, cosPath);
            writeResponse(ctx, bytes);
            return;// 直接返回
        } else {
            response = ResponseEntity.build(Code.NOOP);
        }
        writeResponse(ctx, response);
    }

    /**
     * 上传文件
     *
     * @param request
     * @param bucketName
     * @param cosPath
     * @return
     */
    private ResponseEntity putObject(FullHttpRequest request, String bucketName, String cosPath) {
        ByteBuf buf = request.content();
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        // XXX 在存储路劲前面添加当前环境前缀，用来区分是测试环境还是生产环境，以免出现文件覆盖情况
        cosPath = String.format("/%s%s", EnvUtil.getEnv(), cosPath);

        QCloudCosConfig config = QCloudCosConfig.build();
        PutObjectRequest putRequest = new PutObjectRequest(bucketName, cosPath, config, bytes);

        String result = QCloudCosClient.putObject(putRequest);
        if (StringUtil.isEmpty(result)) {
            // 返回文件相对访问路径 host+cosPath 目前不返回前路径，由业务自己控制 host
            // String filePath = config.getUrlPre() +
            // QCloudCosConfig.bucketHost(bucketName) + cosPath;
            return ResponseEntity.buildSuc(cosPath);
        } else {
            return ResponseEntity.buildFail(result);
        }

    }

    /**
     * 下载文件
     *
     * @param request
     * @param bucketName
     * @param cosPath
     * @return
     */
    private byte[] getObject(FullHttpRequest request, String bucketName, String cosPath) {
        // XXX 在存储路劲前面添加当前环境前缀，用来区分是测试环境还是生产环境，以免出现文件覆盖情况
        // cosPath = String.format("/%s%s", EnvironmentUtil.getEnv(), cosPath);

        QCloudCosConfig config = QCloudCosConfig.build();
        GetObjectRequest getRequest = new GetObjectRequest(bucketName, cosPath, config);

        byte[] bytes = QCloudCosClient.getObject(getRequest);
        return bytes;
    }

    private void writeResponse(ChannelHandlerContext ctx, ResponseEntity entity) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);

        // response.headers().set(HttpHeaderNames.CONNECTION,
        // HttpHeaderValues.CLOSE);

        ByteBuf contentBuf = Unpooled.copiedBuffer(JSON.toJSONString(entity), CharsetUtil.UTF_8);
        response.content().writeBytes(contentBuf);
        contentBuf.release();
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void writeResponse(ChannelHandlerContext ctx, byte[] bytes) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);

        ByteBuf contentBuf = Unpooled.copiedBuffer(bytes);
        response.content().writeBytes(contentBuf);
        contentBuf.release();
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        LOG.error("qcloud-xml-api netty服务出错了", cause);
        cause.printStackTrace();
    }
}
