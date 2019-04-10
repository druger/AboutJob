package com.druger.aboutwork.model

import java.math.BigDecimal

/**
 * Created by druger on 10.08.2016.
 */
class MarkCompany {

    var userId: String? = null
    var companyId: String? = null
    var salary: Float = 0.toFloat()
    var chief: Float = 0.toFloat()
    var workplace: Float = 0.toFloat()
    var career: Float = 0.toFloat()
    var collective: Float = 0.toFloat()
    var socialPackage: Float = 0.toFloat()

    val averageMark: Float
        get() {
            if (salary != 0f && chief != 0f && workplace != 0f
                    && career != 0f && collective != 0f && socialPackage != 0f) {
                val rating = (salary + chief + workplace + career + collective + socialPackage) / 6
                return roundMark(rating)
            }
            return 0f
        }

    constructor() {}

    constructor(userId: String, companyId: String) {
        this.userId = userId
        this.companyId = companyId
    }

    companion object {

        fun roundMark(rating: Float): Float {
            return BigDecimal.valueOf(rating.toDouble()).setScale(2, BigDecimal.ROUND_HALF_UP).toFloat()
        }
    }
}
