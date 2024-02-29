package tedc.alian.data.remote.dto.abstraksi

import androidx.room.Ignore
import tedc.alian.data.remote.api.auth.UserDto

abstract class BaseGuruSiswaDto {
    abstract var agama: String
    abstract var alamat: String
    abstract var fotoProfil: String
    abstract var jenisKelamin: String
    abstract var namaLengkap: String
    abstract var ttl: String
    abstract var userId: String

    @get:Ignore
    abstract var user: UserDto
}