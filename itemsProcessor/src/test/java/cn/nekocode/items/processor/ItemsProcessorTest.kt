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

import cn.nekocode.items.ItemAdapter
import cn.nekocode.items.annotation.Adapter
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
                import ${Adapter::class.java.canonicalName};

                @${Adapter::class.java.simpleName}
                public interface TestAdapter {}
            """.trimIndent()
        )

        Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(arrayListOf(javaFile))
            .processedWith(ItemsProcessor())
            .failsToCompile()
            .withErrorContaining("The @${Adapter::class.java.simpleName} " +
                    "should not annotates to interface class:")
    }

    @Test
    fun notExtendsAdapter() {
        val javaFile = JavaFileObjects.forSourceString(
            "com.test.TestAdapter",
            """
                package com.test;
                import ${Adapter::class.java.canonicalName};

                @${Adapter::class.java.simpleName}
                public class TestAdapter {}
            """.trimIndent()
        )

        Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(arrayListOf(javaFile))
            .processedWith(ItemsProcessor())
            .failsToCompile()
            .withErrorContaining("The adapter class should extends " +
                    "${ItemAdapter::class.java.simpleName}:")
    }
}