package com.example.consumer.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "scheduler.enabled",havingValue = "true", matchIfMissing = true) // 기본적으로는 스케쥴러가 작동하도록 설정, 싫으면 scheduler.enabled 에 false라고 하면됨
public class SchedulerConfig {
}
