package tedc.alian.data.remote.api.siswa

import com.google.gson.annotations.SerializedName
import tedc.alian.data.remote.dto.abstraksi.EditProfilRequest
import java.io.File

data class EditProfilSiswaRequest(
    @SerializedName("nisn")
    val nisn: String,
    @SerializedName("id")
    override val id: String,
    @SerializedName("nama_lengkap")
    override val namaLengkap: String,
    @SerializedName("foto_profil")
    override val fotoProfil: File?,
    @SerializedName("jenis_kelamin")
    override val jenisKelamin: String,
    @SerializedName("alamat")
    override val alamat: String,
    @SerializedName("ttl")
    override val ttl: String,
    @SerializedName("agama")
    override val agama: String
) : EditProfilRequest()
