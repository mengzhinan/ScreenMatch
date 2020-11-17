package com.duke.screenmatch.ui

import com.intellij.openapi.vfs.VirtualFile
import java.util.*
import javax.swing.AbstractListModel
import javax.swing.Icon


sealed class HeaderItem {
    abstract val text: String
    abstract val icon: Icon?
}

data class Header @JvmOverloads constructor(
        override val text: String,
        override val icon: Icon? = null): HeaderItem()


data class Item @JvmOverloads constructor(
        val file: VirtualFile,
        override val text: String,
        override val icon: Icon? = null): HeaderItem()

class HeaderListModel : AbstractListModel<HeaderItem>() {

    private val myItemList: MutableList<HeaderItem> = ArrayList()

    override fun getSize(): Int {
        return myItemList.size
    }

    override fun getElementAt(index: Int): HeaderItem {
        return myItemList[index]
    }

    fun addHeader(header: Header) {
        val index = myItemList.size
        myItemList.add(header)
        fireIntervalAdded(this, index, index)
    }

    fun removeHeader(header: HeaderItem): Boolean {
        val index = myItemList.indexOf(header)
        val rv = myItemList.remove(header)
        if (index >= 0) {
            fireIntervalRemoved(this, index, index)
        }
        return rv
    }

    fun addItem(item: Item) {
        val index = myItemList.size
        myItemList.add(item)
        fireIntervalAdded(this, index, index)
    }

    fun removeAllElements() {
        val index1 = myItemList.size - 1
        myItemList.clear()
        if (index1 >= 0) {
            fireIntervalRemoved(this, 0, index1)
        }
    }
}