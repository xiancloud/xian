/*
 * Copyright 2013-2014, ApiFest project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apifest.oauth20;

import info.xiancloud.core.thread_pool.ThreadPoolManager;
import info.xiancloud.core.util.LOG;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Class responsible for ApiFest OAuth 2.0 Server.
 * 单例模式
 *
 * @author Rossitsa Borissova
 */
public final class OAuthServer {
    public static final OAuthServer singletonServer = new OAuthServer();
    public static final int PORT = 9223;
    private final Object LOCK = new Object();
    private final AtomicBoolean started = new AtomicBoolean(false);
    private Channel parentChannel;

    public void startServer() {
        synchronized (LOCK) {
            if (started.get()) {
                LOG.warn("已启动，不允许重复启动");
                return;
            }
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer() {
                            protected void initChannel(Channel ch) throws Exception {
                                ch.pipeline()
                                        .addLast("encoder", new HttpResponseEncoder())
                                        .addLast("decoder", new HttpRequestDecoder())
                                        .addLast("aggregator", new HttpObjectAggregator(4096))
                                        .addLast("handler", new OAuth20Handler());
                            }
                        })
                        .option(ChannelOption.SO_BACKLOG, 10240)
                        .option(ChannelOption.SO_REUSEADDR, true)
                ;
                LOG.info(String.format("[OAuth] oauth server is about to start on port {%s} ", PORT));
                parentChannel = b.bind(PORT).sync().channel();
                LOG.info(String.format("[OAuth] oauth server started on port {%s} ", PORT));
                ThreadPoolManager.execute(() -> {
                    try {
                        LOG.debug("[OAuth] Wait until the server socket is closed.");
                        parentChannel.closeFuture().sync();
                    } catch (Throwable ee) {
                        LOG.error(ee);
                    } finally {
                        LOG.info("[OAuth] 准备shutdown oauth server");
                        bossGroup.shutdownGracefully();
                        workerGroup.shutdownGracefully();
                        LOG.info("[OAuth] oauth server shutdown完毕!");
                    }
                });
                started.set(true);
            } catch (Throwable e) {
                LOG.error("[OAuth] oauth server 启动失败", e);
                started.set(false);
            }
        }
    }

    public void stopServer() {
        synchronized (LOCK) {
            if (!started.get()) {
                LOG.warn("从未启动过，停什么停？");
                return;
            }
            parentChannel.close();
        }
    }

}