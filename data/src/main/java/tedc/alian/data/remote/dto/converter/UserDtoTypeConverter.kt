package tedc.alian.data.remote.dto.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import tedc.alian.data.remote.api.auth.UserDto

class UserDtoTypeConverter {
    @TypeConverter
    fun fromUserDto(userDto: UserDto): String {
        return Gson().toJson(userDto)
    }

    @TypeConverter
    fun toUserDto(userDtoJson: String): UserDto {
        return Gson().fromJson(userDtoJson, UserDto::class.java)
    }
}
