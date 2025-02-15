/*
 * Copyright 2022 NAVER Corp.
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

package com.pinpoint.test.plugin;

import com.pinpoint.test.common.view.ApiLinkPage;
import com.pinpoint.test.common.view.HrefTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
public class MariaDBPluginController {
    static final String STATEMENT_QUERY = "SELECT count(1) FROM playground";
    static final String PREPARED_STATEMENT_QUERY = "SELECT * FROM playground where id = ?";
    static final String PROCEDURE_NAME = "getPlaygroundByName";
    static final String CALLABLE_STATEMENT_QUERY = "{ CALL " + PROCEDURE_NAME + "(?, ?) }";
    static final String CALLABLE_STATEMENT_INPUT_PARAM = "TWO";
    static final int CALLABLE_STATMENT_OUTPUT_PARAM_TYPE = Types.INTEGER;

    private final RequestMappingHandlerMapping handlerMapping;

    @Autowired
    public MariaDBPluginController(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @GetMapping("/")
    String welcome() {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = this.handlerMapping.getHandlerMethods();
        List<HrefTag> list = new ArrayList<>();
        for (RequestMappingInfo info : handlerMethods.keySet()) {
            for (String path : info.getDirectPaths()) {
                list.add(HrefTag.of(path));
            }
        }
        list.sort(Comparator.comparing(HrefTag::getPath));
        return new ApiLinkPage("mariadb-jdbc-plugin-testweb")
                .addHrefTag(list)
                .build();
    }


    @RequestMapping(value = "/mariadb/execute1")
    public String execute1() throws Exception {
        executeStatement();
        return "OK";
    }

    @RequestMapping(value = "/mariadb/execute2")
    public String execute2() throws Exception {
        executePreparedStatement();
        return "OK";
    }

    @RequestMapping(value = "/mariadb/execute3")
    public String execute3() throws Exception {
        executeCallableStatement();
        return "OK";
    }

    private int executeStatement() throws Exception {
        int resultCount = 0;
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(STATEMENT_QUERY)) {
            while (rs.next()) {
                ++resultCount;
            }
        }
        return resultCount;
    }

    private int executePreparedStatement() throws Exception {
        int resultCount = 0;

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(PREPARED_STATEMENT_QUERY)) {
            ps.setInt(1, 3);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ++resultCount;
                }
            }
        }
        return resultCount;
    }

    private int executeCallableStatement() throws Exception {
        final String outputParamCountName = "outputParamCount";
        int resultCount = 0;
        try (Connection conn = getConnection();
            CallableStatement cs = conn.prepareCall(CALLABLE_STATEMENT_QUERY)) {
            cs.setString(1, CALLABLE_STATEMENT_INPUT_PARAM);
            cs.registerOutParameter(2, CALLABLE_STATMENT_OUTPUT_PARAM_TYPE);
            try (ResultSet rs = cs.executeQuery() ) {
                while (rs.next()) {
                    ++resultCount;
                }
            }
            final int totalCount = cs.getInt(outputParamCountName);
        }
        return resultCount;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(MariaDBServer.getUri(), "root", "");
    }
}
