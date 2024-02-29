package tedc.alian.data.repository

import android.content.Context
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import tedc.alian.data.local.dao.GuruDao
import tedc.alian.data.local.repository.MySharedPref
import tedc.alian.data.remote.api.guru.EditProfilGuruRequest
import tedc.alian.data.remote.api.guru.GuruApi
import tedc.alian.data.remote.api.guru.GuruDto
import tedc.alian.data.remote.api.karya.KategoriKaryaDto
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.apiCall
import tedc.alian.utils.helper.isNetworkConnected
import javax.inject.Inject

class GuruRepository @Inject constructor(
    private val context: Context,
    private val guruDao: GuruDao,
    private val preferences: MySharedPref,
    private val api: GuruApi
) {


    suspend fun getProfil(): Resource<GuruDto?> {
        val guru = guruDao.getGuru(preferences.getUserId().toString())
        return if (guru != null) {
            Resource.success(guru)
        } else {
            apiCall(context) {
                if (context.isNetworkConnected()) {
                    val response =
                        api.getProfilGuru()
                    if (response.isSuccessful) {
                        val profil = response.body()?.data
                        guruDao.insert(profil!!)
                        preferences.setGuruId(profil.id)
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
                } else {
                    tampilError("Tidak ada koneksi internet")
                }
            }
        }
    }

    suspend fun editProfilGuru(request: EditProfilGuruRequest): Resource<String?> {
        return apiCall(context) {
            val requestFile =
                request.fotoProfil?.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val fotoProfil =
                MultipartBody.Part.createFormData(
                    "foto_profil",
                    request.fotoProfil?.name,
                    requestFile!!
                )
            val namaLengkap = request.namaLengkap
                .toRequestBody("text/plain".toMediaTypeOrNull())
            val nuptk = request.nuptk
                .toRequestBody("text/plain".toMediaTypeOrNull())
            val agama =
                request.agama.toRequestBody("text/plain".toMediaTypeOrNull())
            val jenisKelamin =
                request.jenisKelamin.toRequestBody("text/plain".toMediaTypeOrNull())
            val ttl =
                request.ttl.toRequestBody("text/plain".toMediaTypeOrNull())
            val alamat =
                request.alamat.toRequestBody("text/plain".toMediaTypeOrNull())
            val jabatan =
                request.jabatan.toRequestBody("text/plain".toMediaTypeOrNull())
            val gelar =
                request.gelar.toRequestBody("text/plain".toMediaTypeOrNull())
            val response = api.editProfilGuru(
                id = request.id,
                fotoProfil = fotoProfil,
                namaLengkap = namaLengkap,
                nuptk = nuptk,
                jenisKelamin = jenisKelamin,
                alamat = alamat,
                ttl = ttl,
                agama = agama,
                jabatan = jabatan,
                gelar = gelar
            )
            if (response.isSuccessful) {
                val newUser = response.body()?.data
                if (newUser != null) {
                    guruDao.insert(newUser)
                }
                Resource.success("Profil berhasil disimpan")
            } else {
                tampilError("Gagal mengubah data profil")
            }
        }
    }

    suspend fun editProfilGuruTanpaFoto(request: EditProfilGuruRequest): Resource<String?> {
        return apiCall(context) {
            if (request.namaLengkap.isEmpty() ||
                request.agama.isEmpty() ||
                request.alamat.isEmpty() ||
                request.jenisKelamin.isEmpty() ||
                request.nuptk.isEmpty() ||
                request.ttl.isEmpty() ||
                request.jabatan.isEmpty()
            ) {
                tampilError("Tidak boleh kosong")
            } else {
                val response = api.editProfilGuruTanpaFoto(
                    idGuru = preferences.getGuruId().toString(),
                    namaLengkap = request.namaLengkap,
                    nuptk = request.nuptk,
                    jenisKelamin = request.jenisKelamin,
                    agama = request.agama,
                    ttl = request.ttl,
                    jabatan = request.jabatan,
                    gelar = request.gelar,
                    alamat = request.alamat
                )
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    if (data != null) {
                        guruDao.insert(data)
                    }
                    Resource.success("Profil berhasil disimpan")
                } else {
                    tampilError("Gagal memperbarui data profil")
                }
            }
        }
    }

    suspend fun tambahKategoriKaryaCitra(namaKategori: String): Resource<String?> {
        return if (namaKategori.isNotEmpty()) {
            apiCall(context) {
                val response = api.tambahKategoriKaryaCitra(
                    namaKategori
                )
                if (response.isSuccessful) {
                    Resource.success("Kategori berhasil ditambahkan")
                } else {
                    tampilError("Gagal menambahkan data kategori")
                }
            }
        } else {
            tampilError("Data kosong tidak diizinkan")
        }
    }

    suspend fun listKategoriKaryaCitra(): Resource<List<KategoriKaryaDto>?> {
        return apiCall(context) {
            val response = api.listKategoriKaryaCitra()
            if (response.isSuccessful) {
                Resource.success(response.body()?.data)
            } else {
                tampilError("Gagal mendapatkan data")
            }
        }
    }

    suspend fun hapusKategoriKaryaCitra(id: String): Resource<String?> {
        return apiCall(context) {
            val response = api.hapusKategorikaryaCitra(id)
            if (response.isSuccessful) {
                Resource.success("Kategori berhasil dihapus")
            } else {
                tampilError("Gagal menghapus kategori")
            }
        }
    }

    suspend fun tambahKategoriKaryaTulis(namaKategori: String): Resource<String?> {
        return if (namaKategori.isNotEmpty()) {
            apiCall(context) {
                val response = api.tambahKategoriKaryaTulis(
                    namaKategori
                )
                if (response.isSuccessful) {
                    Resource.success("Kategori berhasil ditambahkan")
                } else {
                    tampilError("Gagal menambahkan data kategori")
                }
            }
        } else {
            tampilError("Data kosong tidak diizinkan")
        }
    }

    suspend fun listKategoriKaryaTulis(): Resource<List<KategoriKaryaDto>?> {
        return apiCall(context) {
            val response = api.listKategoriKaryaTulis()
            if (response.isSuccessful) {
                Resource.success(response.body()?.data)
            } else {
                tampilError("Gagal mendapatkan data")
            }
        }
    }

    suspend fun hapusKategoriKaryaTulis(id: String): Resource<String?> {
        return apiCall(context) {
            val response = api.hapusKategoriKaryaTulis(id)
            if (response.isSuccessful) {
                Resource.success("Kategori berhasil dihapus")
            } else {
                tampilError("Gagal menghapus kategori")
            }
        }
    }

    private fun tampilError(msg: String): Resource<Nothing> =
        Resource.error(throwable = Throwable(msg))
}