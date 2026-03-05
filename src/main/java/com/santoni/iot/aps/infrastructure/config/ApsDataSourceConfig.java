package com.santoni.iot.aps.infrastructure.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

@Configuration
@MapperScan(
        basePackages = "com.santoni.iot.aps.infrastructure.database.aps",
        sqlSessionFactoryRef = "apsSqlSessionFactory"
)
public class ApsDataSourceConfig {

    @Bean(name = "apsDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.aps")
    @Primary // 标记为主数据源
    public DataSource apsDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "apsSqlSessionFactory")
    @Primary
    public SqlSessionFactory apsSqlSessionFactory(@Qualifier("apsDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath:mapper/aps/*.xml"));

        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        factoryBean.setPlugins(interceptor);
        return factoryBean.getObject();
    }

    @Bean(name = "apsTransactionManager")
    @Primary
    public PlatformTransactionManager apsTransactionManager(@Qualifier("apsDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    @Primary
    public TransactionTemplate transactionTemplate(@Qualifier("apsTransactionManager") PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }
}
