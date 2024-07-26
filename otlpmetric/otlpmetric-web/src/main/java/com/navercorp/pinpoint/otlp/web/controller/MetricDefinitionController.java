/*
 * Copyright 2024 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.otlp.web.controller;

import com.navercorp.pinpoint.common.server.response.Response;
import com.navercorp.pinpoint.common.server.response.SimpleResponse;
import com.navercorp.pinpoint.otlp.common.defined.AppMetricDefinition;
import com.navercorp.pinpoint.otlp.common.definition.property.MetricDefinitionProperty;
import com.navercorp.pinpoint.otlp.web.service.AppMetricDefinitionService;
import com.navercorp.pinpoint.otlp.web.service.MetricMetadataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author minwoo-jung
 */
@RestController
@RequestMapping(value = "/api/otlp")
public class MetricDefinitionController {

    private final MetricMetadataService metricMetadataService;
    private final AppMetricDefinitionService appMetricDefinitionService;

    public MetricDefinitionController(MetricMetadataService metricMetadataService, AppMetricDefinitionService appMetricDefinitionService) {
        this.appMetricDefinitionService = appMetricDefinitionService;
        this.metricMetadataService = metricMetadataService;
    }

    @GetMapping("/metricDef/property")
    public MetricDefinitionProperty getMetricDefinitionProperty(@RequestParam("applicationName") String applicationName) {
        return metricMetadataService.getMetricDefinitionInfo(applicationName);
    }

    @PostMapping("/metricDef/userDefined")
    public Response addUserDefinedMetric(@RequestBody AppMetricDefinition appMetricDefinition) {
        appMetricDefinitionService.addUserDefinedMetric(appMetricDefinition);
        return SimpleResponse.ok();
    }

    @PatchMapping(value = "/metricDef/userDefined")
    public Response updateUserDefinedMetric(@RequestBody List<AppMetricDefinition> appMetricDefinitionList) {
        appMetricDefinitionService.updateUserDefinedMetric(appMetricDefinitionList);
        return SimpleResponse.ok();
    }
}
