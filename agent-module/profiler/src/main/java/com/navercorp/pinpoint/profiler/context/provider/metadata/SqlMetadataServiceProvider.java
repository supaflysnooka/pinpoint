/*
 * Copyright 2019 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.profiler.context.provider.metadata;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;
import com.navercorp.pinpoint.common.profiler.message.EnhancedDataSender;
import com.navercorp.pinpoint.profiler.cache.SimpleCache;
import com.navercorp.pinpoint.profiler.cache.UidCache;
import com.navercorp.pinpoint.profiler.cache.UidGenerator;
import com.navercorp.pinpoint.profiler.context.module.MetadataDataSender;
import com.navercorp.pinpoint.profiler.context.monitor.config.MonitorConfig;
import com.navercorp.pinpoint.profiler.metadata.DefaultSqlMetaDataService;
import com.navercorp.pinpoint.profiler.metadata.MetaDataType;
import com.navercorp.pinpoint.profiler.metadata.SqlCacheService;
import com.navercorp.pinpoint.profiler.metadata.SqlMetaDataService;
import com.navercorp.pinpoint.profiler.metadata.SqlUidMetaDataService;

import java.util.Objects;

/**
 * @author Woonduk Kang(emeroad)
 */
public class SqlMetadataServiceProvider implements Provider<SqlMetaDataService> {
    private final ProfilerConfig profilerConfig;
    private final MonitorConfig monitorConfig;
    private final EnhancedDataSender<MetaDataType> enhancedDataSender;
    private final SimpleCacheFactory simpleCacheFactory;

    @Inject
    public SqlMetadataServiceProvider(ProfilerConfig profilerConfig,
                                      MonitorConfig monitorConfig,
                                      @MetadataDataSender EnhancedDataSender<MetaDataType> enhancedDataSender,
                                      SimpleCacheFactory simpleCacheFactory) {
        this.profilerConfig = Objects.requireNonNull(profilerConfig, "profilerConfig");
        this.monitorConfig = Objects.requireNonNull(monitorConfig, "monitorConfig");
        this.enhancedDataSender = Objects.requireNonNull(enhancedDataSender, "enhancedDataSender");
        this.simpleCacheFactory = Objects.requireNonNull(simpleCacheFactory, "simpleCacheFactory");
    }

    @Override
    public SqlMetaDataService get() {
        final int jdbcSqlCacheSize = profilerConfig.getJdbcSqlCacheSize();
        final int maxSqlLength = profilerConfig.getMaxSqlLength();

        if (monitorConfig.isSqlStatEnable()) {
            final int maxSqlCacheLength = profilerConfig.getMaxSqlCacheLength();

            UidCache sqlCache = new UidCache(jdbcSqlCacheSize, new UidGenerator.Murmur(), maxSqlCacheLength);
            SqlCacheService<byte[]> sqlCacheService = new SqlCacheService<>(enhancedDataSender, sqlCache, maxSqlLength);
            return new SqlUidMetaDataService(sqlCacheService);
        } else {
            SimpleCache<String, Integer> sqlCache = simpleCacheFactory.newSimpleCache(jdbcSqlCacheSize);
            SqlCacheService<Integer> sqlCacheService = new SqlCacheService<>(enhancedDataSender, sqlCache, maxSqlLength);
            return new DefaultSqlMetaDataService(sqlCacheService);
        }
    }
}
