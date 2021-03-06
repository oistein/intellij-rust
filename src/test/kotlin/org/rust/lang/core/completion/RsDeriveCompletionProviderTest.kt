/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.lang.core.completion

import com.intellij.testFramework.LightProjectDescriptor
import org.rust.lang.core.resolve.StdDerivableTrait
import org.rust.lang.core.resolve.withDependencies

class RsDeriveCompletionProviderTest : RsCompletionTestBase() {

    override fun getProjectDescriptor(): LightProjectDescriptor = WithStdlibRustProjectDescriptor

    fun testCompleteOnStruct() = doSingleCompletion("""
        #[derive(Debu/*caret*/)]
        struct Test {
            foo: u8
        }
    """, """
        #[derive(Debug/*caret*/)]
        struct Test {
            foo: u8
        }
    """)

    fun testCompleteOnEnum() = doSingleCompletion("""
        #[derive(Debu/*caret*/)]
        enum Test {
            Something
        }
    """, """
        #[derive(Debug/*caret*/)]
        enum Test {
            Something
        }
    """)

    fun `test complete with dependencies`() {
        StdDerivableTrait.values()
            .filter { it.dependencies.isNotEmpty() }
            .forEach {
                checkContainsCompletion(it.withDependencies.joinToString(", "), """
                    #[derive(${it.name.dropLast(1)}/*caret*/)]
                    struct Foo;
                """)
            }
    }

    fun `test complete with partially implemented dependencies`() = checkContainsCompletion("Ord, Eq, PartialEq", """
        #[derive(PartialOrd, Or/*caret*/)]
        struct Foo;
    """)

    fun `test complete with manually implemented dependencies`() = doSingleCompletion("""
        #[derive(Cop/*caret*/)]
        enum Foo { Something }

        impl Clone for Foo {
            fn clone(&self) -> Foo { Foo::Something }
        }
    """, """
        #[derive(Copy/*caret*/)]
        enum Foo { Something }

        impl Clone for Foo {
            fn clone(&self) -> Foo { Foo::Something }
        }
    """)

    fun testDoesntCompleteOnFn() = checkNoCompletion("""
        #[foo(PartialE/*caret*/)]
        fn foo() { }
    """)

    fun testDoesntCompleteOnMod() = checkNoCompletion("""
        #[foo(PartialE/*caret*/)]
        mod foo { }
    """)

    fun testDoesntCompleteNonDeriveAttr() = checkNoCompletion("""
        #[foo(PartialE/*caret*/)]
        enum Test { Something }
    """)

    fun testDoesntCompleteInnerAttr() = checkNoCompletion("""
        mod bar {
            #![derive(PartialE/*caret*/)]
        }
    """)

    fun testDoesntCompleteAlreadyDerived() = checkNoCompletion("""
        #[derive(Debug, Debu/*caret*/)]
        enum Test { Something }
    """)

    fun `test doesn't complete already derived`() = checkNoCompletion("""
        #[derive(Clon/*caret*/)]
        enum Foo { Something }

        impl Clone for Foo {
            fn clone(&self) -> Foo { Foo::Something }
        }
    """)
}
