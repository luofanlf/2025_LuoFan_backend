package com.oracle;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import com.oracle.api.CoinResource;
import com.oracle.service.CoinService;

public class OracleTechChallengeApplication extends Application<OracleTechChallengeConfiguration> {

    public static void main(final String[] args) throws Exception {
        new OracleTechChallengeApplication().run(args);
    }

    @Override
    public String getName() {
        return "oracle-tech-challenge";
    }

    @Override
    public void initialize(final Bootstrap<OracleTechChallengeConfiguration> bootstrap) {
        // 纯后端API服务，无需配置静态文件
    }

    @Override
    public void run(final OracleTechChallengeConfiguration configuration,
                    final Environment environment) {
        // 创建 Service 实例
        final CoinService coinService = new CoinService();
        
        // 创建 Resource 实例并注入依赖
        final CoinResource coinResource = new CoinResource(coinService);
        
        // 注册 Resource 到 Jersey
        environment.jersey().register(coinResource);
    }

}
