package com.duke.screenmatch.ui

import java.awt.Component
import javax.swing.DefaultListCellRenderer
import javax.swing.JList

class HeaderItemRender : DefaultListCellRenderer() {

        override fun getListCellRendererComponent(list: JList<*>,
                                              value: Any,
                                              index: Int,
                                              isSelected: Boolean,
                                              cellHasFocus: Boolean): Component {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
        when (value) {
            is Header -> overrideHeader(list, value, index, isSelected, cellHasFocus)
            is Item -> overrideItem(list, value, index, isSelected, cellHasFocus)
        }
        return this
    }

    private fun overrideHeader(list: JList<*>,
                               value: Header,
                               index: Int,
                               isSelected: Boolean,
                               cellHasFocus: Boolean) {
        icon = value.icon
        text = value.text
        isEnabled = false
        background = null
        foreground = null
        border = null
    }

    private fun overrideItem(list: JList<*>,
                             value: Item,
                             index: Int,
                             isSelected: Boolean,
                             cellHasFocus: Boolean) {
        icon = value.icon
        text = value.text
    }
}