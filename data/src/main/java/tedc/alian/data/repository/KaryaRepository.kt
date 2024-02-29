package tedc.alian.data.repository

import android.content.Context
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import tedc.alian.data.local.dao.KaryaTulisDao
import tedc.alian.data.local.model.KaryaTulis
import tedc.alian.data.local.repository.MySharedPref
import tedc.alian.data.remote.api.karya.KaryaApi
import tedc.alian.data.remote.api.karya.KaryaCitraDto
import tedc.alian.data.remote.api.karya.KaryaTulisDto
import tedc.alian.data.remote.api.karya.KategoriKaryaDto
import tedc.alian.data.remote.api.karya.NotifikasiResponse
import tedc.alian.data.remote.api.karya.TambahKaryaCitraRequest
import tedc.alian.data.remote.api.karya.UbahKaryaCitraRequestRequest
import tedc.alian.data.remote.api.karya.UpdateKaryaTulisRequest
import tedc.alian.data.remote.api.karya.UploadKaryaTulisRequest
import tedc.alian.data.remote.dto.CountKaryaDto
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.apiCall
import tedc.alian.utils.helper.getFileSizeInMb
import javax.inject.Inject
import kotlin.math.roundToInt

class KaryaRepository @Inject constructor(
    private val api: KaryaApi,
    private val context: Context,
    private val preferences: MySharedPref,
    private val karyaTulisDao: KaryaTulisDao
) {

    suspend fun getAllKaryaCitra(): Resource<List<KaryaCitraDto>> {
        return apiCall(
            context
        ) {
            val response = api.getAllKarya()
            if (response.isSuccessful) {
                Resource.success(response.body()?.data!!)
            } else {
                tampilErrorGetData()
            }
        }
    }

    suspend fun tambahKaryaCitra(request: TambahKaryaCitraRequest): Resource<KaryaCitraDto?> {
        return if (request.namaKarya.isNullOrEmpty() || request.caption.isNullOrEmpty() || request.kategoriKaryaCitraId.isNullOrEmpty()) {
            tampilErrorFieldKosong()
        } else if (request.karya == null) {
            tampilError("Harap pilih karya terlebih dahulu")
        } else {
            val sizeInMb = getFileSizeInMb(request.karya)
            val fileSize = (sizeInMb * 10.0).roundToInt() / 10.0
            if (fileSize <= 100) {
                apiCall(context) {
                    val requestFile =
                        request.karya.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val karya =
                        MultipartBody.Part.createFormData("karya", request.karya.name, requestFile)
                    val namaKarya =
                        request.namaKarya.toRequestBody("text/plain".toMediaTypeOrNull())
                    val caption = request.caption.toRequestBody("text/plain".toMediaTypeOrNull())
                    val kategoriKaryaCitraId =
                        request.kategoriKaryaCitraId.toRequestBody("text/plain".toMediaTypeOrNull())
                    val response = api.tambahKaryaCitra(
                        karya = karya,
                        namaKarya = namaKarya,
                        kategoriKaryaCitraId = kategoriKaryaCitraId,
                        caption = caption
                    )
                    if (response.isSuccessful) {
                        Resource.success(response.body()?.data)
                    } else {
                        when (response.code()) {
                            422 -> tampilError("Nama karya sudah ada")
                            500 -> tampilError("Terjadi kesalahan server")
                            else -> tampilError("Upload karya visual gagal, coba lagi nanti")
                        }
                    }
                }
            } else {
                tampilError("Ukuran file karya terlalu besar")
            }
        }
    }

    suspend fun tambahKaryaTulis(request: UploadKaryaTulisRequest): Resource<KaryaTulisDto?> {
        return if (request.judulKarya.isEmpty() || request.kontenKarya.isEmpty() || request.kategoriKaryaTulisId.isEmpty()) {
            tampilErrorFieldKosong()
        } else {
            apiCall(context) {
                val response = api.tambahKaryaTulis(request)
                if (response.isSuccessful) {
                    Resource.success(response.body()?.data)
                } else {
                    tampilError("Upload karya tulis gagal, coba lagi nanti")
                }
            }
        }
    }

    suspend fun getKategoriKaryaTulis(): Resource<List<KategoriKaryaDto?>> {
        return apiCall(context) {
            val response = api.getKategoriKaryaTulis()
            if (response.isSuccessful) {
                if (response.body() != null) {
                    Resource.success(response.body()!!.data)
                } else {
                    tampilErrorGetData()
                }
            } else {
                tampilErrorGetData()
            }
        }
    }

    suspend fun getKategoriKaryaCitra(): Resource<List<KategoriKaryaDto>> {
        Resource.loading("Sedang mengambil data, harap tunggu")
        return apiCall(context) {
            val response = api.getKategoriKaryaCitra()
            if (response.isSuccessful) {
                if (response.body() != null) {
                    Resource.success(response.body()!!.data)
                } else {
                    tampilErrorGetData()
                }
            } else {
                tampilErrorGetData()
            }
        }
    }

    suspend fun getKaryaTulisKu(): Resource<List<KaryaTulisDto>> {
        return apiCall(context) {
            val response = api.getKaryaTulisKu(preferences.getSiswaId().toString())
            if (response.isSuccessful) {
                if (response.body() != null) {
                    Resource.success(response.body()!!.data)
                } else {
                    tampilErrorGetData()
                }
            } else {
                tampilErrorGetData()
            }
        }
    }

    suspend fun getAllKaryaTulis(): Resource<List<KaryaTulisDto>> {
        return apiCall(context) {
            val response = api.getAllKaryaTulis(1)
            if (response.isSuccessful) {
                if (response.body() != null) {
                    Resource.success(response.body()!!.data)
                } else {
                    tampilError("Terjadi kesalahan saat mengambil data")
                }
            } else {
                tampilErrorGetData()
            }
        }
    }


    suspend fun getKaryaCitraKu(): Resource<List<KaryaCitraDto>> {
        return apiCall(context) {
            val response = api.getKaryaCitraKu(preferences.getSiswaId().toString())
            if (response.isSuccessful) {
                Resource.success(response.body()!!.data)
            } else {
                tampilErrorGetData()
            }
        }
    }

    suspend fun getKaryaYangBelumDiValidasi(): Resource<List<KaryaCitraDto>> {
        return apiCall(context) {
            val response = api.getKaryaYangBelumDiValidasi()
            if (response.isSuccessful) {
                val data = response.body()?.data
                if (data != null) {
                    Resource.success(data)
                } else {
                    tampilErrorGetData()
                }
            } else {
                tampilErrorGetData()
            }
        }
    }

    suspend fun terimaKaryaCitra(karyaCitraId: String, isWatermarked: Boolean): Resource<String> {
        return apiCall(context) {
            val response = api.terimaKarya(karyaCitraId = karyaCitraId, isWatermarked.toString())
            if (response.isSuccessful) {
                Resource.success("Karya visual, berhasil divalidasi")
            } else {
                tampilError("Karya visual, gagal divalidasi")
            }
        }
    }

    suspend fun tolakKarya(
        karyaCitraId: String,
        keterangan: String
    ): Resource<String> {
        return apiCall(context) {
            val response = api.tolakKarya(
                karyaCitraId = karyaCitraId,
                keterangan = keterangan
            )
            if (response.isSuccessful) {
                Resource.success("Karya berhasil ditolak")
            } else {
                tampilError("Karya visual, gagal ditolak")
            }
        }
    }

    suspend fun hapusKaryaCitra(karyaCitraId: String): Resource<String> {
        return apiCall(context) {
            val response = api.hapusKaryaCitra(
                karyaCitraId = karyaCitraId
            )
            if (response.isSuccessful) {
                Resource.success("Karya visual, berhasil dihapus")
            } else {
                tampilError("Karya visual, gagal dihapus")
            }
        }
    }

    suspend fun ubahKaryaCitra(
        karyaCitraId: String,
        request: UbahKaryaCitraRequestRequest
    ): Resource<String> {
        return if (request.karya == null || request.namaKarya.isNullOrEmpty() || request.caption.isNullOrEmpty()) {
            tampilErrorFieldKosong()
        } else {
            apiCall(context) {
                val requestFile =
                    request.karya.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val karya =
                    MultipartBody.Part.createFormData("karya", request.karya.name, requestFile)
                val namaKarya = request.namaKarya.toRequestBody("text/plain".toMediaTypeOrNull())
                val caption = request.caption.toRequestBody("text/plain".toMediaTypeOrNull())
                val kategoriKaryaCitraId =
                    request.kategoriKaryaCitraId?.toRequestBody("text/plain".toMediaTypeOrNull())
                val response = api.ubahKaryaCitra(
                    karyaCitraId,
                    karya = karya,
                    namaKarya = namaKarya,
                    kategoriKaryaCitraId = kategoriKaryaCitraId!!,
                    caption = caption
                )
                if (response.isSuccessful) {
                    Resource.success("Karya visual berhasil diubah")
                } else {
                    tampilError("Karya visual gagal diubah")
                }
            }
        }
    }

    suspend fun ubahKaryaCitraTanpaKarya(
        karyaCitraId: String,
        request: UbahKaryaCitraRequestRequest
    ): Resource<String> {
        return if (request.namaKarya.isNullOrEmpty() || request.caption.isNullOrEmpty() || request.kategoriKaryaCitraId.isNullOrEmpty()) {
            tampilErrorFieldKosong()
        } else {
            apiCall(context) {
                val response = api.ubahKaryaCitraTanpaKarya(
                    karyaCitraId = karyaCitraId,
                    namaKarya = request.namaKarya,
                    caption = request.caption,
                    kategoriKaryaCitraId = request.kategoriKaryaCitraId
                )
                if (response.isSuccessful) {
                    Resource.success("Karya visual berhasil diubah")
                } else {
                    tampilError("Gagal mengubah karya visual")
                }
            }
        }
    }

    suspend fun likeKaryaCitra(karyaCitraId: String): Resource<String> {
        return apiCall(context) {
            val response = api.likeKaryaCitra(karyaCitraId)
            if (response.isSuccessful) {
                Resource.success("Like berhasil diberikan")
            } else {
                when (response.code()) {
                    409 -> tampilError("Like sudah diberikan")
                    500 -> tampilError("Gagal memberikan like")
                    else -> tampilError("Terjadi kesalahan server")
                }
            }
        }
    }

    suspend fun getDetailKaryaCitra(karyaCitraId: String): Resource<KaryaCitraDto?> {
        return apiCall(context) {
            val response = api.getDetailKaryaCitra(karyaCitraId)
            if (response.isSuccessful) {
                Resource.success(response.body())
            } else {
                tampilError("Gagal mendapatkan data")
            }
        }
    }

    suspend fun hapusKaryaTulis(karyaTulisId: String): Resource<String> {
        return apiCall(context) {
            val response = api.hapusKaryaTulis(
                karyaTulisId = karyaTulisId
            )
            if (response.isSuccessful) {
                Resource.success("Karya tulis berhasil dihapus")
            } else {
                tampilError("Karya tulis, gagal dihapus")
            }
        }
    }

    suspend fun ubahKaryaTulis(
        idKarya: String,
        request: UpdateKaryaTulisRequest
    ): Resource<String> {
        return if (request.judulKarya.isEmpty() || request.kontenKarya.isEmpty()) {
            tampilErrorFieldKosong()
        } else {
            apiCall(
                context
            ) {
                val response = api.ubahKaryaTulis(
                    karyaCitraId = idKarya,
                    request = request
                )
                if (response.isSuccessful) {
                    Resource.success("Karya tulis berhasil diubah")
                } else {
                    tampilError("Karya tulis gagal diubah")
                }
            }
        }
    }

    suspend fun tambahKomentarKaryaTulis(
        komentar: String,
        idKaryaTulis: String
    ): Resource<String?> {
        Resource.loading("Sedang menambahkan komentar...")
        return if (komentar.isNotEmpty()) {
            apiCall(context) {
                val response = api.tambahKomentarTulis(
                    idKaryaTulis, komentar
                )
                if (response.isSuccessful) {
                    Resource.success("Komentar berhasil ditambahkan")
                } else {
                    tampilError("Gagal menambahkan komentar")
                }
            }
        } else {
            tampilError("Tidak boleh kosong")
        }
    }

    suspend fun tambahKomentarKaryaCitra(
        komentar: String,
        idKaryaCitra: String
    ): Resource<String?> {
        Resource.loading("Sedang menambahkan komentar...")
        return if (komentar.isNotEmpty()) {
            apiCall(context) {
                val response = api.tambahKomentarCitra(idKaryaCitra, komentar)
                if (response.isSuccessful) {
                    Resource.success("Komentar berhasil ditambahkan")
                } else {
                    tampilError("Gagal menambahkan komentar")
                }
            }
        } else {
            tampilError("Tidak boleh kosong")
        }
    }

    suspend fun likeKaryaTulis(karyaTulisId: String): Resource<String> {
        return apiCall(context) {
            val response = api.likeKaryaTulis(karyaTulisId)
            if (response.isSuccessful) {
                Resource.success("Like berhasil diberikan")
            } else {
                when (response.code()) {
                    409 -> tampilError("Like sudah diberikan")
                    500 -> tampilError("Gagal memberikan like")
                    else -> tampilError("Terjadi kesalahan server")
                }
            }
        }
    }

    suspend fun countKarya(): Resource<CountKaryaDto> {
        return apiCall(context) {
            val id = preferences.getSiswaId().toString()
            val response = api.countKaryaKu(id)
            if (response.isSuccessful) {
                Resource.success(response.body()!!)
            } else {
                tampilError("Gagal mendapatkan data")
            }
        }
    }

    suspend fun getDetailKaryaTulis(karyaTulisId: String): Resource<KaryaTulisDto?> {
        return apiCall(context) {
            val response = api.getDetailKaryaTulis(karyaTulisId)
            if (response.isSuccessful) {
                Resource.success(response.body())
            } else {
                tampilError("Gagal mendapatkan data")
            }
        }
    }

    suspend fun getNotifikasi(): Resource<NotifikasiResponse?> {
        return apiCall(context) {
            val response = api.getNotifikasi()
            if (response.isSuccessful) {
                Resource.success(response.body())
            } else {
                tampilError("Gagal mendapatkan data notifikasi")
            }
        }
    }

    suspend fun getNotifikasiSiswa(): Resource<NotifikasiResponse?> {
        return apiCall(context) {
            val response = api.getNotifikasiSiswa()
            if (response.isSuccessful) {
                Resource.success(response.body())
            } else {
                tampilError("Gagal mendapatkan data notifikasi")
            }
        }
    }

    suspend fun updateNotifikasi(
        idKarya: String,
        idNotifikasi: String
    ): Resource<String?> {
        return apiCall(context) {
            val response = api.updateNotifikasi(idKarya, idNotifikasi)
            if (response.isSuccessful) {
                Resource.success("Berhasil update")
            } else {
                tampilError("Gagal update")
            }
        }
    }

    suspend fun hapusNotifikasiGuru(idNotifikasi: String): Resource<String?> {
        return apiCall(context) {
            val response = api.hapusNotifikasiGuru(idNotifikasi)
            if (response.isSuccessful) {
                Resource.Success("Notifikasi berhasil dihapus")
            } else {
                tampilError("Gagal menghapus data notifikasi")
            }
        }
    }

    suspend fun hapusNotifikasiSiswa(): Resource<String?> {
        return apiCall(context) {
            val response = api.hapusNotifikasiSiswa(preferences.getSiswaId().toString())
            if (response.isSuccessful) {
                Resource.success("Notifikasi berhasil dihapus")
            } else {
                tampilError("Gagal menghapus notifikasi")
            }
        }
    }

    private fun tampilError(msg: String): Resource<Nothing> {
        return Resource.error(Throwable(msg))
    }

    private fun tampilErrorFieldKosong(): Resource<Nothing> {
        return Resource.error(Throwable("Tidak boleh ada input yang kosong"))
    }

    private fun tampilErrorGetData(): Resource<Nothing> {
        return Resource.error(Throwable("Terjadi kesalahan, saat mengambil data"))
    }

    suspend fun saveKaryaTulisDraft(karyaTulis: KaryaTulis) {
        karyaTulisDao.insert(karyaTulis)
    }

    suspend fun getDraftKaryaTulis(): KaryaTulis? {
        return karyaTulisDao.getKaryaTulis()
    }

    suspend fun deleteDraftKaryaTulis() {
        karyaTulisDao.deleteDraftKaryaTulis()
    }

    suspend fun getDraftKaryaTulisUpdate(judulKarya: String): KaryaTulis? {
        return karyaTulisDao.getKaryaTulisDraftUpdate(judulKarya)
    }

    suspend fun deleteDraftKaryaTulisId(judulKarya: String) {
        return karyaTulisDao.deleteDraftKaryaTulisUpdate(judulKarya)
    }
}