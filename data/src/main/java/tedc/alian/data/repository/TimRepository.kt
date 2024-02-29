package tedc.alian.data.repository

import android.content.Context
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import tedc.alian.data.local.dao.TimDao
import tedc.alian.data.local.repository.MySharedPref
import tedc.alian.data.remote.api.tim.TambahPromosiRequest
import tedc.alian.data.remote.api.tim.TimApi
import tedc.alian.data.remote.api.tim.UbahPromosiRequest
import tedc.alian.data.remote.dto.PromosiDto
import tedc.alian.data.remote.dto.TimDto
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.apiCall

class TimRepository constructor(
    private val api: TimApi,
    private val context: Context,
    private val timDao: TimDao,
    private val preferences: MySharedPref
) {

    suspend fun getProfil(): Resource<TimDto?> {
        val tim = timDao.getTim(preferences.getUserId().toString())
        return if (tim != null) {
            Resource.success(tim)
        } else {
            apiCall(context) {
                // && isRefreshing == false
                val response = api.getProfil()
                if (response.isSuccessful) {
                    val profil = response.body()?.data
                    timDao.insert(profil!!)
                    preferences.setTimId(profil.id)
                    Resource.success(data = response.body()?.data)
                } else {
                    when (response.code()) {
                        404 -> {
                            tampilError("Data tidak ada")
                        }
                        else -> {
                            tampilError("Terjadi kesalahan server")
                        }
                    }
                }
            }
        }
    }

    suspend fun editProfil(
        namaLengkap: String,
        jabatan: String
    ): Resource<String> {
        return apiCall(context) {
            if (namaLengkap.isEmpty() ||
                jabatan.isEmpty()
            ) {
                tampilError("Tidak boleh kosong")
            } else {
                val response = api.editProfil(
                    timId = preferences.getTimId().toString(),
                    namaLengkap = namaLengkap,
                    jabatan
                )
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    if (data != null) {
                        timDao.insert(data)
                    }
                    Resource.success("Data berhasil disimpan")
                } else {
                    tampilError("Gagal memperbarui data profil")
                }
            }
        }
    }

    suspend fun tambahPromosi(request: TambahPromosiRequest): Resource<String> {
        return if (request.namaPromosi.isEmpty() || request.keterangan.isEmpty() || request.tanggalPromosi.isEmpty() || request.status.isEmpty()) {
            tampilError("Harap lengkapi data")
        } else if (request.gambar == null) {
            tampilError("Harap pilih gambar/foto promosi terlebih dahulu")
        } else {
            apiCall(context) {
                val requestFile =
                    request.gambar.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val gambar =
                    MultipartBody.Part.createFormData("gambar", request.gambar.name, requestFile)
                val namaPromosi =
                    request.namaPromosi.toRequestBody("text/plain".toMediaTypeOrNull())
                val keterangan = request.keterangan.toRequestBody("text/plain".toMediaTypeOrNull())
                val tanggalPromosi =
                    request.tanggalPromosi.toRequestBody("text/plain".toMediaTypeOrNull())
                val status =
                    request.status.toRequestBody("text/plain".toMediaTypeOrNull())
                val response = api.tambahPromosi(
                    gambar = gambar,
                    namaPromosi, tanggalPromosi, keterangan, status
                )
                if (response.isSuccessful) {
                    Resource.success("Data promosi berhasil ditambahkan")
                } else {
                    tampilError("Upload karya visual gagal, coba lagi nanti")
                }
            }
        }
    }

    suspend fun getListPromosi(): Resource<List<PromosiDto>?> {
        return apiCall(context) {
            val response = api.getListPromosi()
            if (response.isSuccessful) {
                Resource.success(response.body()?.data)
            } else {
                tampilError("Gagal mendapatkan data")
            }
        }
    }

    suspend fun getListPromosiByTim(): Resource<List<PromosiDto>?> {
        return apiCall(context) {
            val response = api.getListPromosiByTim(
                preferences.getTimId().toString()
            )
            if (response.isSuccessful) {
                Resource.success(response.body()?.data)
            } else {
                tampilError("Gagal mendapatkan data")
            }
        }
    }

    suspend fun hapusDataPromosi(id: String): Resource<String?> {
        return apiCall(context) {
            val response = api.hapusPromosi(id)
            if (response.isSuccessful) {
                Resource.success("Promosi berhasil dihapus")
            } else {
                tampilError("Gagal menghapus data promosi")
            }
        }
    }

    suspend fun ubahPromosi(
        id: String,
        ubahPromosiRequest: UbahPromosiRequest
    ): Resource<String> {
        return if (
            ubahPromosiRequest.gambar == null || ubahPromosiRequest.namaPromosi.isEmpty() || ubahPromosiRequest.keterangan.isEmpty() || ubahPromosiRequest.status.isEmpty() || ubahPromosiRequest.tanggalPromosi.isEmpty()
        ) {
            tampilError("Data kosong tidak diizinkan")
        } else {
            apiCall(context) {
                val gambarPromosi =
                    ubahPromosiRequest.gambar.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val newGambarPromo =
                    MultipartBody.Part.createFormData(
                        "gambar",
                        ubahPromosiRequest.gambar.name,
                        gambarPromosi
                    )
                val namaPromosi = ubahPromosiRequest.namaPromosi
                    .toRequestBody("text/plain".toMediaTypeOrNull())
                val keterangan = ubahPromosiRequest.keterangan
                    .toRequestBody("text/plain".toMediaTypeOrNull())
                val status =
                    ubahPromosiRequest.status.toRequestBody("text/plain".toMediaTypeOrNull())
                val tanggalPromosi =
                    ubahPromosiRequest.tanggalPromosi.toRequestBody("text/plain".toMediaTypeOrNull())
                val response = api.ubahPromosi(
                    id, newGambarPromo, namaPromosi, keterangan, status, tanggalPromosi
                )
                if (response.isSuccessful) {
                    Resource.success("Data promosi berhasil disimpan")
                } else {
                    tampilError("Gagal mengubah data profil")
                }
            }
        }
    }

    suspend fun ubahPromosiTanpaGambar(
        id: String,
        ubahPromosiRequest: UbahPromosiRequest
    ): Resource<String?> {
        return if (ubahPromosiRequest.namaPromosi.isEmpty() || ubahPromosiRequest.keterangan.isEmpty() || ubahPromosiRequest.status.isEmpty() || ubahPromosiRequest.tanggalPromosi.isEmpty()
        ) {
            tampilError("Data kosong tidak diizinkan")
        } else {
            apiCall(context) {
                val response = api.ubahPromosiTanpaGambar(
                    id,
                    ubahPromosiRequest.namaPromosi,
                    ubahPromosiRequest.keterangan,
                    ubahPromosiRequest.status,
                    ubahPromosiRequest.tanggalPromosi
                )
                if (response.isSuccessful) {
                    Resource.success("Data promosi berhasil disimpan")
                } else {
                    tampilError("Gagal menyimpan data promosi")
                }
            }
        }
    }

    suspend fun getActivePromosi(): Resource<PromosiDto?> {
        return apiCall(context) {
            val response = api.getActivePromosi()
            if (response.isSuccessful) {
                Resource.Success(response.body()?.data)
            } else {
                tampilError("Gagal mendapatkan data")
            }
        }
    }

    private fun tampilError(error: String): Resource<Nothing> {
        return Resource.error(throwable = Throwable(error))
    }
}