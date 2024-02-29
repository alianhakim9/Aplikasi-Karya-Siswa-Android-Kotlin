package tedc.alian.data.remote.dto.abstraksi

import java.io.File

abstract class EditProfilRequest {
    abstract val id: String
    abstract val namaLengkap: String
    abstract val fotoProfil: File?
    abstract val jenisKelamin: String
    abstract val alamat: String
    abstract val ttl: String
    abstract val agama: String
}