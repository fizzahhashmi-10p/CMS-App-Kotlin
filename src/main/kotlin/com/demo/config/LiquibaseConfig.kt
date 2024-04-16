package com.demo.config

import liquibase.integration.spring.SpringLiquibase

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class LiquibaseConfig {

    @Bean
    fun liquibase(dataSource: DataSource): SpringLiquibase {
        val liquibase = SpringLiquibase()
        liquibase.changeLog = "db/changelog/db.changelog-master.yaml"
        liquibase.dataSource = dataSource
        return liquibase
    }
}
