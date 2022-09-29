/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.chunjun.ddl.convent.mysql.parse;

import com.google.common.collect.ImmutableList;
import org.apache.calcite.sql.SqlDrop;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlSpecialOperator;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.parser.SqlParserPos;

import javax.annotation.Nonnull;

import java.util.List;

/** Parse tree for {@code DROP VIEW} statement. */
public class SqlDropTable extends SqlDrop {
    private static final SqlOperator OPERATOR =
            new SqlSpecialOperator("DROP TABLE", SqlKind.DROP_VIEW);

    public boolean isTemporary;
    public SqlNodeList name;
    private final Boolean isCascade;

    public SqlDropTable(
            SqlParserPos pos,
            boolean isTemporary,
            boolean ifExists,
            SqlNodeList name,
            Boolean isCascade) {
        super(OPERATOR, pos, ifExists);
        this.isTemporary = isTemporary;
        this.name = name;
        this.isCascade = isCascade;
    }

    @Override
    @Nonnull
    public List<SqlNode> getOperandList() {
        return ImmutableList.of(this.name);
    }

    public void unparse(SqlWriter writer, int leftPrec, int rightPrec) {
        writer.keyword("DROP");
        if (isTemporary) {
            writer.keyword("TEMPORARY");
        }
        writer.keyword("TABLE");
        if (this.ifExists) {
            writer.keyword("IF EXISTS");
        }

        if (name.size() > 1) {
            SqlWriter.Frame frame =
                    writer.startList(SqlWriter.FrameTypeEnum.create("sds"), "(", ")");
            for (SqlNode viewName : name) {
                printIndent(writer);
                viewName.unparse(writer, leftPrec, rightPrec);
            }
            writer.newlineAndIndent();
            writer.endList(frame);
        } else {
            name.get(0).unparse(writer, leftPrec, rightPrec);
        }

        if (isCascade != null) {
            if (isCascade) {
                writer.keyword("CASCADE");
            } else {
                writer.keyword("RESTRICT");
            }
        }
    }

    protected void printIndent(SqlWriter writer) {
        writer.sep(",", false);
        writer.newlineAndIndent();
        writer.print("  ");
    }
}
