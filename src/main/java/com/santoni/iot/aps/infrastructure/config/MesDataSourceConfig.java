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
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(
        basePackages = "com.santoni.iot.aps.infrastructure.database.mes",
        sqlSessionFactoryRef = "mesSqlSessionFactory"
)
public class MesDataSourceConfig {

    @Bean(name = "mesDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.mes")
    public DataSource mesDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "mesSqlSessionFactory")
    public SqlSessionFactory mesSqlSessionFactory(@Qualifier("mesDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath:mapper/mes/*.xml"));
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        factoryBean.setPlugins(interceptor);
        return factoryBean.getObject();
    }

    @Bean(name = "mesTransactionManager")
    public PlatformTransactionManager mesTransactionManager(@Qualifier("mesDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
