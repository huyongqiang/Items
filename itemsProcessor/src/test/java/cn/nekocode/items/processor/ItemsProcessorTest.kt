/*
 * Copyright 2018. nekocode (nekocode.cn@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.nekocode.items.processor

import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourcesSubjectFactory
import org.junit.Test
import com.google.common.truth.Truth

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
class ItemsProcessorTest {

    @Test
    fun annotateInterface() {
        val javaFile = JavaFileObjects.forSourceString(
            "com.test.TestAdapter",
            """
                package com.test;

                @${Names.ADAPTER}
                public interface TestAdapter {}
            """.trimIndent()
        )

        Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(arrayListOf(javaFile))
            .processedWith(ItemsProcessor())
            .failsToCompile()
            .withErrorContaining("The @${Names.ADAPTER} " +
                    "should not annotates to interface:")
    }

    @Test
    fun notExtendsAdapter() {
        val javaFile = JavaFileObjects.forSourceString(
            "com.test.TestAdapter",
            """
                package com.test;

                @${Names.ADAPTER}
                public class TestAdapter {}
            """.trimIndent()
        )

        Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(arrayListOf(javaFile))
            .processedWith(ItemsProcessor())
            .failsToCompile()
            .withErrorContaining("The adapter class should extends class " +
                    "${Names.ITEM_ADAPTER}:")
    }

    @Test
    fun notOverrideMethods() {
        fun javaFile(body: String = "") = JavaFileObjects.forSourceString(
            "com.test.TestAdapter",
            """
                package com.test;

                @${Names.ADAPTER}
                public class TestAdapter extends ${Names.ITEM_ADAPTER} {${body}}
            """.trimIndent()
        )

        Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(arrayListOf(javaFile()))
            .processedWith(ItemsProcessor())
            .failsToCompile()
            .withErrorContaining("The adapter class should override method")

        Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(arrayListOf(javaFile(
                "@Override public <T> T getData(int position) { return (T) list.get(position); }")))
            .processedWith(ItemsProcessor())
            .failsToCompile()
            .withErrorContaining("The adapter class should override method ${Names.GET_ITEM_COUNT}")

        Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(arrayListOf(javaFile(
                "@Override public int getItemCount() { return list.size(); }")))
            .processedWith(ItemsProcessor())
            .failsToCompile()
            .withErrorContaining("The adapter class should override method ${Names.GET_DATA}")

        // TODO case of overriding two methods
    }
}