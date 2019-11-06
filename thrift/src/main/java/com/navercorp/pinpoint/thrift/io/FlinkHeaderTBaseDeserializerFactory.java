/*
 * Copyright 2018 NAVER Corp.
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
package com.navercorp.pinpoint.thrift.io;

import com.navercorp.pinpoint.io.util.TypeLocator;
import org.apache.thrift.TBase;

/**
 * @author minwoo.jung
 */
public class FlinkHeaderTBaseDeserializerFactory implements DeserializerFactory<HeaderTBaseDeserializer> {
    private final HeaderTBaseDeserializerFactory headerTBaseDeserializerFactory;

    public FlinkHeaderTBaseDeserializerFactory(TypeLocator<TBase<?, ?>> flinkTBaseLocator) {
        if (flinkTBaseLocator == null) {
            throw new NullPointerException("flinkTBaseLocator");
        }

        this.headerTBaseDeserializerFactory = new HeaderTBaseDeserializerFactory(flinkTBaseLocator);
    }

    @Override
    public HeaderTBaseDeserializer createDeserializer() {
        return headerTBaseDeserializerFactory.createDeserializer();
    }
}
