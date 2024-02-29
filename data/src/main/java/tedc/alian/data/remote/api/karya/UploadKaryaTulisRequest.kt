package tedc.alian.data.remote.api.karya

import com.google.gson.annotations.SerializedName

data class UploadKaryaTulisRequest(
    @SerializedName("konten_karya")
    override val kontenKarya: String,
    @SerializedName("judul_karya")
    override val judulKarya: String,
    @SerializedName("kategori_karya_tulis_id")
    override val kategoriKaryaTulisId: String,
    @SerializedName("sumber")
    override val sumber: String
) : KaryaTulisRequest()