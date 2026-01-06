package com.easymeeting.websocket.netty;

import com.easymeeting.entity.config.AppConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

@Component
@Slf4j
public class NettyWebSocketStarter implements Runnable{

    @Resource
    private HandlerWebSocket handlerWebSocket;


    private EventLoopGroup boosGroup = new NioEventLoopGroup();

    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    @Resource
    private HandlerTokenValidation handlerTokenValidation;

    @Resource
    private AppConfig appConfig;
    @Override
    public void run() {
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boosGroup,workerGroup);
            serverBootstrap.channel(NioServerSocketChannel.class).handler(new LoggingHandler(LogLevel.DEBUG)).
                    childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            ChannelPipeline pipeline =channel.pipeline();
                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new HttpObjectAggregator(64*1024));
                            pipeline.addLast(new IdleStateHandler(Integer.MAX_VALUE,0,0));
                            pipeline.addLast(new HandlerHearBeat());
                            pipeline.addLast(handlerTokenValidation);
                            pipeline.addLast(new WebSocketServerProtocolHandler("/ws",null,true,6553,true,true));
                            pipeline.addLast(handlerWebSocket);
                        }
                    });

            Channel channel = serverBootstrap.bind(appConfig.getWsPort()).sync().channel();
            log.info("Netty启动成功端口:{}",appConfig.getWsPort());
            channel.closeFuture().sync();
        }catch (Exception e){
            log.error("netty启动失败",e);
        }finally {
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @PreDestroy
    public void close(){
        boosGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}

