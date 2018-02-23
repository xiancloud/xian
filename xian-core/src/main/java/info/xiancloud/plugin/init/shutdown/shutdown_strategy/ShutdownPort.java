package info.xiancloud.plugin.init.shutdown.shutdown_strategy;

import info.xiancloud.plugin.conf.EnvConfig;
import info.xiancloud.plugin.util.LOG;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 监听固定端口实现本地和远程shutdown机制。
 *
 * @author happyyangyuan
 */
public class ShutdownPort extends ShutdownStrategy {

    private static final String IP_ADDR = "localhost";// 服务器地址
    private static final String SHUTDOWN_ACK = "shutdown over";
    private static Integer SHUT_DOWN_PORT;//懒加载,可以防止本地执行main方法时总是报找不到配置文件的错误

    public static void shutdown() {
        try (Socket socket = new Socket(IP_ADDR, getShutDownPort());
             PrintWriter writer = new PrintWriter(socket.getOutputStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            writer.println(SHUTDOWN);
            writer.flush();
            LOG.info("shutdown服务器命令已发送...");
            String line;
            do {
                line = reader.readLine();
                LOG.info("server返回:" + line);
            } while (!SHUTDOWN_ACK.endsWith(line));
            LOG.info("服务器shutdown完毕:" + line);
            LOG.debug("等待较短时间，以便服务端java虚拟机退出.");
            Thread.sleep(500);
        } catch (Throwable t) {
            LOG.error("", t);
        }
    }

    @Override
    public void prepare() {
        listenForShutdownCmd();
    }


    private static int getShutDownPort() {
        if (SHUT_DOWN_PORT == null) {
            try {
                SHUT_DOWN_PORT = EnvConfig.getIntValue(SHUTDOWN);// shutdown port, not supported any more.
            } catch (Throwable e) {
                throw new RuntimeException("读取配置文件\"conf/server.xml\"出现了错误", e);
            }
        }
        return SHUT_DOWN_PORT;
    }

    private static void listenForShutdownCmd() {
        Thread thread = new Thread(() -> {
            LOG.info("开始启动shutdown监听程序,shutdown端口=" + getShutDownPort());
            String cmd;
            ServerSocket server;
            try {
                server = new ServerSocket(getShutDownPort());
            } catch (IOException e) {
                LOG.error("", e);
                return;
            }
            while (true) {
                try {
                    final Socket socket = server.accept();//Blocks until a connection is made.
                    LOG.info(String.format("接到来自hostname=%s,ip=%s的连接", socket.getInetAddress().getHostName(), socket.getInetAddress().getHostAddress()));
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
                    final PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));
                    if ("127.0.0.1".equals(socket.getInetAddress().getHostAddress()) //ipv4
                            || "0:0:0:0:0:0:0:1".equals(socket.getInetAddress().getHostAddress())//ipv6
                            ) {
                        cmd = reader.readLine();
                        writer.println("Cmd received:  " + cmd);
                        writer.flush();
                        LOG.info("socket监听收到命令: " + cmd);
                        if (SHUTDOWN.equals(cmd)) {
                            LOG.info("接收到shutdown请求,System.exit(n)");
                            addHookToTheEnd(() -> {
                                try {
                                    LOG.info("所有shutdown hook执行完毕后才发送shutdownAck: 'shutdown ok'");
                                    writer.println(SHUTDOWN_ACK);
                                    writer.flush();
                                    reader.close();
                                    writer.close();
                                    socket.close();
                                } catch (Throwable ioException) {
                                    LOG.error("", ioException);
                                }
                            });
                            break;
                        }
                    } else {
                        writer.println("Illegal connection, force to close...");
                        writer.flush();
                        reader.close();
                        writer.close();
                        socket.close();
                    }
                    //然后继续监听下一个socket连接
                } catch (Throwable e) {
                    LOG.error("", e);
                }
            }
            System.exit(0);
        });
        thread.setDaemon(true);
        thread.start();
    }
}
