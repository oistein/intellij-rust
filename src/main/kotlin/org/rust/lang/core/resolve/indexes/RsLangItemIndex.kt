/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.lang.core.resolve.indexes

import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.AbstractStubIndex
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubIndexKey
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import org.rust.lang.core.psi.RsTraitItem
import org.rust.lang.core.psi.ext.langAttribute
import org.rust.lang.core.resolve.StdDerivableTrait
import org.rust.lang.core.stubs.RsFileStub
import org.rust.lang.core.stubs.RsTraitItemStub
import org.rust.openapiext.getElements

class RsLangItemIndex : AbstractStubIndex<String, RsTraitItem>() {
    override fun getVersion(): Int = RsFileStub.Type.stubVersion
    override fun getKey(): StubIndexKey<String, RsTraitItem> = KEY
    override fun getKeyDescriptor(): KeyDescriptor<String> = EnumeratorStringDescriptor.INSTANCE

    companion object {
        fun findLangItem(project: Project, langAttribute: String): RsTraitItem? {
            return getElements(KEY, langAttribute, project, GlobalSearchScope.allScope(project)).firstOrNull()
        }

        fun index(stub: RsTraitItemStub, sink: IndexSink) {
            val key = stub.psi.langAttribute
            if (key != null) {
                sink.occurrence(KEY, key)
            }
        }

        private val KEY: StubIndexKey<String, RsTraitItem> =
            StubIndexKey.createIndexKey("org.rust.lang.core.resolve.indexes.RsLangItemIndex")
    }
}
