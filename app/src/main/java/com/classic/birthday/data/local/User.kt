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
    /**
     * 预置头像编码（稳定值，见 [Avatar]）。
     * 注意：不要再存储 R.drawable.xxx 资源 ID，资源 ID 不是跨构建稳定的。
     */
    var photoResId: Int = Avatar.DEFAULT
    /** 备注 */
    var remark: String = ""

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as User
        if (id != other.id) return false
        if (birthday != other.birthday) return false
        if (name != other.name) return false
        if (photo != other.photo) return false
        return true
    }
    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + birthday.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + photo.hashCode()
        return result
    }

}
