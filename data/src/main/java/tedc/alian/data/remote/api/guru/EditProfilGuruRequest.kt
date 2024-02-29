package tedc.alian.data.remote.api.guru

import com.google.gson.annotations.SerializedName
import tedc.alian.data.remote.dto.abstraksi.EditProfilRequest
import java.io.File

data class EditProfilGuruRequest(
    @SerializedName("nuptk")
    val nuptk: String,
    @SerializedName("jabatan")
    val jabatan: String,
    @SerializedName("gelar")
    val gelar: String,
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
    override val agama: String,
) : EditProfilRequest()
