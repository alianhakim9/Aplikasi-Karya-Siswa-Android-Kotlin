package tedc.alian.data.remote.api.karya

abstract class KaryaCitraRequest {
    abstract val namaKarya: String?
    abstract val caption: String?
    abstract val kategoriKaryaCitraId: String?
}