package tedc.alian.data.remote.dto.abstraksi

import tedc.alian.data.remote.api.karya.KomentarDto
import tedc.alian.data.remote.api.karya.StatusKarya
import tedc.alian.data.remote.api.siswa.SiswaDto

abstract class BaseKaryaCitraDto {
    abstract val caption: String
    abstract val createdAt: String
    abstract val excerpt: String
    abstract val id: String
    abstract val idSiswa: String
    abstract val jumlahLike: String
    abstract val karya: String
    abstract val kategoriKaryaCitraId: String
    abstract val namaKarya: String
    abstract val slug: String
    abstract val status: String
    abstract val updatedAt: String
    abstract val format: String
    abstract val komentar: List<KomentarDto>?
    abstract val siswa: SiswaDto
    abstract val namaKategori: String
}