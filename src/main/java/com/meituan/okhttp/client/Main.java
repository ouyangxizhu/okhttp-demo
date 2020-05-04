package com.meituan.okhttp.client;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
//    private static Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        log.info("start");
        OkHttpRequest okHttpRequest = new OkHttpRequest();
        okHttpRequest.syncGet();
        okHttpRequest.aSyncGet();

        log.info("all");

    }
}
