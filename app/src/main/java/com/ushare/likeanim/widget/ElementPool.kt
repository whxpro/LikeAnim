package com.ushare.likeanim.widget

import androidx.core.util.Pools

class ElementPool : Pools.SimplePool<Element>(50) {

    override fun acquire(): Element {
        var e = super.acquire()
        if (e == null) {
            e = EruptionElement()
        }
        return e
    }

    override fun release(instance: Element): Boolean {
        instance.reset()
        return super.release(instance)
    }
}