package com.liella.liellagateway.Fileter;

import com.liella.liellaclientsdk.utils.SignUtils;
import com.liella.liellacommon.model.entity.InterfaceInfo;
import com.liella.liellacommon.model.entity.User;
import com.liella.liellacommon.service.InnerInterfaceInfoService;
import com.liella.liellacommon.service.InnerUserInterfaceInfoService;
import com.liella.liellacommon.service.InnerUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component //扫描
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference
    private InnerUserService innerUserService;
    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;
    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;





    private static  final List<String> IP_WHILE_LIST = Arrays.asList("127.0.0.1");
    private static final String INTERFACE_HOST="http://localhost:8123";
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //TODO 全局请求拦截

        /**
         * 1.用户发送请求到 API
         * http://localhost:8123/api/name/get?name=ll 前缀匹配
         * 网关
         * 请求日志 （黑白名单）
         * 用户鉴权（判断 ak、sk 是否合法）
         * 请求的模拟接口是否存在？
         * 请求转发，调用模拟接口
         * 响应日志
         * 调用成功，接口调用次数 + 1 调用失败，返回一个规范的错误码
         */
        //请求日志
        //获取请求对象
        ServerHttpRequest request = exchange.getRequest();
        //获取路径
        String path = INTERFACE_HOST+request.getPath().value();
        //获取方法
        String method = request.getMethod().toString();
        log.info("请求的唯一标识"+request.getId());
        log.info("请求的路劲"+request.getPath().value());
        log.info("请求的唯一方法"+request.getMethod());
        log.info("请求的参数"+request.getQueryParams());
        String sourceAddress = request.getLocalAddress().getHostString();
        log.info("请求的来源地址"+request.getLocalAddress().getHostString());
        log.info("请求的来源地址"+request.getRemoteAddress());
        //获取响应对象
        ServerHttpResponse response = exchange.getResponse();
        //访问控制 - 黑白名单
        if (!IP_WHILE_LIST.contains(sourceAddress)){
            handleNoAuth(response);
        }

        //用户鉴权 （判断 ak sk是否合法）
        HttpHeaders headers = request.getHeaders();
        String accessKey =   headers.getFirst("accessKey");
        String nonce =   headers.getFirst("nonce");
        String timestamp =   headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body =   headers.getFirst("body");
        //TODO 从数据库中获取sk,ak
        User invokeUser =null;
        try {
           invokeUser = innerUserService.getInvokeUser(accessKey);
        }catch (Exception e){
            log.error("as获取错误",e);
        }
        //判断是否为空
        if (invokeUser==null){
            return handleNoAuth(response);
        }

        //        if (!accessKey.equals("ll")){
        //            return handleNoAuth(response);
        //        }

        if (Long.parseLong(nonce)>10000){
          return   handleNoAuth(response) ;
        }

        //TODO 时间和当前时间不能超过5min
        long currentTime = System.currentTimeMillis() / 1000;
        long FIVE_MINUTES=60*5L;
        if ((currentTime- Long.parseLong(timestamp))>=FIVE_MINUTES){
            return handleNoAuth(response);
        }

        //实际情况是从数据库中查询sk
        String secretKey = invokeUser.getSecretKey();
        String gennedSign = SignUtils.genSign(body, secretKey);
        if (sign==null || !sign.equals(gennedSign)){
            throw new RuntimeException("sign错误");
        }
        //请求模拟接口是否存在
        // todo 从数据库查询接口是否存在 以及请求方法是否匹配 校验请求参数
        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method);;
        }catch (Exception e){
            log.error("接口错误",e);
        }

        if (interfaceInfo==null){
            return handleNoAuth(response);
        }

        // todo 是否有调用次数
        int invokeCount = 0;
        try {

        invokeCount = innerInterfaceInfoService.getInvokeCount(interfaceInfo.getId(),invokeUser.getId());
        }catch (Exception e){
            log.error("接口错误",e);
            return handleNoAuth(response);
        }

        if (invokeCount<=0){
            return handleNoAuth(response);
        }


        return handdleResponse(exchange,chain,interfaceInfo.getId(),invokeUser.getId());
    }

    @Override
    public int getOrder() {
        return -1;
    }


    /**
     * 处理响应
     *
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handdleResponse(ServerWebExchange exchange, GatewayFilterChain chain,long interfaceInfoId,long userId){
        try {
            //获取响应对象
            ServerHttpResponse originalResponse = exchange.getResponse();
            //缓存数据
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            //响应码
            HttpStatus statusCode = originalResponse.getStatusCode();

            if(statusCode == HttpStatus.OK){
                //ServerHttpResponseDecorator 增强类 装饰response 增强其能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {

                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        //log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            //writeWith 是一个异步的方法，这里先缓存数据，等到数据写入之后，再修改响应数据
                            //往返回结果中写入数据
                            return super.writeWith(fluxBody.map(dataBuffer -> {
                                // 7. 调用成功，接口调用次数 + 1 invokeCount 总调用次数+1
                                try {
                                    innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
                                    innerInterfaceInfoService.addInvokeCoint(interfaceInfoId);
                                } catch (Exception e) {
                                    log.error("invokeCount error", e);
                                }

                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                DataBufferUtils.release(dataBuffer);//释放掉内存
                                // 构建日志
                                StringBuilder sb2 = new StringBuilder(200);
                                sb2.append("<--- {} {} \n");
                                List<Object> rspArgs = new ArrayList<>();
                                rspArgs.add(originalResponse.getStatusCode());
                                //rspArgs.add(requestUrl);
                                String data = new String(content, StandardCharsets.UTF_8);//data


                                sb2.append(data);
                                log.info(sb2.toString(), rspArgs.toArray());//log.info("<-- {} {}\n", originalResponse.getStatusCode(), data);
                                return bufferFactory.wrap(content);
                            }));
                        } else {
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange);//降级处理返回数据
        }catch (Exception e){
            log.error("网关处理异常请联系管理员\n" + e);
            return chain.filter(exchange);
        }

    }
    public Mono<Void> handleNoAuth(ServerHttpResponse response){
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    public Mono<Void> handleInvokeError(ServerHttpResponse response){
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }
}

