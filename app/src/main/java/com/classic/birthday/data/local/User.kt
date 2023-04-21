@file:Suppress("unused")

package com.classic.birthday.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * 用户信息
 *
 * @author Classic
 * @version 2023/4/21 15:07
 */
@Entity(tableName = "t_user")
class User : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
    /** 生日 */
    var birthday: Long = System.currentTimeMillis()
    /** 名称 */
    var name: String = ""
    /** 照片 */
    var photo: String = ""
    // /** 备注 */
    // var remark: String = ""

}
