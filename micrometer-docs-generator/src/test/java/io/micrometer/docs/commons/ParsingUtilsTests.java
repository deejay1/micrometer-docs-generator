/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.micrometer.docs.commons;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster._shade.org.eclipse.jdt.core.dom.MethodDeclaration;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ParsingUtils}.
 *
 * @author Tadaya Tsuyukubo
 */
class ParsingUtilsTests {

    @ParameterizedTest
    @org.junit.jupiter.params.provider.MethodSource
    void readStringReturnValue(MethodDeclaration methodDeclaration, String expected) {
        String result = ParsingUtils.readStringReturnValue(methodDeclaration);
        assertThat(result).isEqualTo(expected);
    }

    static Stream<Arguments> readStringReturnValue() throws Exception {
        String filename = ParsingUtilsTests.class.getCanonicalName().replace(".", "/") + ".java";
        Path path = Paths.get("src/test/java", filename);
        JavaClassSource classSource = Roaster.parse(JavaClassSource.class, path.toFile());
        JavaClassSource returnValueClass = (JavaClassSource) classSource.getNestedType(ReturnValueClass.class.getSimpleName());
        MethodSource<?> stringLiteralSource = returnValueClass.getMethod("stringLiteral");
        MethodSource<?> booleanLiteralSource = returnValueClass.getMethod("booleanLiteral");
        MethodDeclaration stringLiteralDeclaration = (MethodDeclaration) stringLiteralSource.getInternal();
        MethodDeclaration booleanPrimitiveDeclaration = (MethodDeclaration) booleanLiteralSource.getInternal();

        return Stream.of(
                Arguments.of(Named.of("stringLiteral", stringLiteralDeclaration), "my-string"),
                Arguments.of(Named.of("booleanLiteral", booleanPrimitiveDeclaration), "true")
        );
    }

    static class ReturnValueClass {
        String stringLiteral() {
            return "my-string";
        }

        // note: object Boolean is not supported

        boolean booleanLiteral() {
            return true;
        }
    }

}
