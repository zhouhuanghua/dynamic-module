package cn.zhh.module.conf;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;

@MapperScan(
        basePackages = "cn.zhh.module.mapper",
        annotationClass = Mapper.class,
        sqlSessionFactoryRef = "moduleSqlSessionFactory",
        sqlSessionTemplateRef = "moduleSqlSessionTemplate"
)
@Configuration
public class MybatisConfig {

    @Bean
    public SqlSessionFactory moduleSqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath*:mapper/*.xml"));
        return sqlSessionFactoryBean.getObject();
    }

    @Bean
    public SqlSessionTemplate moduleSqlSessionTemplate(SqlSessionFactory moduleSqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(moduleSqlSessionFactory);
    }

}