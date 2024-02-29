package tedc.alian.data.remote.api.karya

abstract class KaryaTulisRequest {
    abstract val kontenKarya: String
    abstract val judulKarya: String
    abstract val kategoriKaryaTulisId: String
    abstract val sumber: String
}