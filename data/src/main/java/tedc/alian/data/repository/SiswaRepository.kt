package tedc.alian.data.repository

import android.content.Context
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import tedc.alian.data.local.dao.SiswaDao
import tedc.alian.data.local.repository.MySharedPref
import tedc.alian.data.remote.api.siswa.EditProfilSiswaRequest
import tedc.alian.data.remote.api.siswa.SiswaApi
import tedc.alian.data.remote.api.siswa.SiswaDto
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.apiCall
import javax.inject.Inject

class SiswaRepository @Inject constructor(
    private val api: SiswaApi,
    private val context: Context,
    private val siswaDao: SiswaDao,
    private val preferences: MySharedPref
) {

    suspend fun getProfilSiswa(): Resource<SiswaDto?> {
        val siswa = siswaDao.getSiswa(preferences.getUserId().toString())
        return if (siswa != null) {
            Resource.success(siswa)
        } else {
            apiCall(context) {
                val response = api.getProfilSiswa()
                if (response.isSuccessful) {
                    val profil = response.body()?.data
                    siswaDao.insert(profil!!)
                    preferences.setSiswaId(profil.id)
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

    suspend fun editProfilSiswa(editProfilSiswaRequest: EditProfilSiswaRequest): Resource<String?> {
        return apiCall(context) {
            val requestFile =
                editProfilSiswaRequest.fotoProfil?.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val fotoProfil =
                MultipartBody.Part.createFormData(
                    "foto_profil",
                    editProfilSiswaRequest.fotoProfil?.name,
                    requestFile!!
                )
            val namaLengkap = editProfilSiswaRequest.namaLengkap
                .toRequestBody("text/plain".toMediaTypeOrNull())
            val nisn = editProfilSiswaRequest.nisn
                .toRequestBody("text/plain".toMediaTypeOrNull())
            val agama =
                editProfilSiswaRequest.agama.toRequestBody("text/plain".toMediaTypeOrNull())
            val jenisKelamin =
                editProfilSiswaRequest.jenisKelamin.toRequestBody("text/plain".toMediaTypeOrNull())
            val ttl =
                editProfilSiswaRequest.ttl.toRequestBody("text/plain".toMediaTypeOrNull())
            val alamat =
                editProfilSiswaRequest.alamat.toRequestBody("text/plain".toMediaTypeOrNull())
            val response = api.editProfilSiswa(
                id = editProfilSiswaRequest.id,
                fotoProfil = fotoProfil,
                namaLengkap = namaLengkap,
                nisn = nisn,
                jenisKelamin = jenisKelamin,
                alamat = alamat,
                ttl = ttl,
                agama = agama
            )
            if (response.isSuccessful) {
                val newUser = response.body()?.data
                if (newUser != null) {
                    siswaDao.insert(newUser)
                }
                Resource.success("Data berhasil disimpan")
            } else {
                tampilError("Gagal mengubah data profil")
            }
        }
    }

    suspend fun editProfilSiswaTanpaFoto(editProfilSiswaRequest: EditProfilSiswaRequest): Resource<String?> {
        return apiCall(context) {
            if (editProfilSiswaRequest.namaLengkap.isEmpty() ||
                editProfilSiswaRequest.agama.isEmpty() ||
                editProfilSiswaRequest.alamat.isEmpty() ||
                editProfilSiswaRequest.jenisKelamin.isEmpty() ||
                editProfilSiswaRequest.nisn.isEmpty() ||
                editProfilSiswaRequest.ttl.isEmpty()
            ) {
                tampilError("Tidak boleh kosong")
            } else {
                val response = api.editProfilTanpaFoto(
                    idSiswa = preferences.getSiswaId().toString(),
                    namaLengkap = editProfilSiswaRequest.namaLengkap,
                    nisn = editProfilSiswaRequest.nisn,
                    jenisKelamin = editProfilSiswaRequest.jenisKelamin,
                    alamat = editProfilSiswaRequest.alamat,
                    ttl = editProfilSiswaRequest.ttl,
                    agama = editProfilSiswaRequest.agama
                )
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    if (data != null) {
                        siswaDao.insert(data)
                    }
                    Resource.success("Profil berhasil disimpan")
                } else {
                    tampilError("Gagal memperbarui data profil")
                }
            }
        }
    }

    private fun tampilError(error: String): Resource<Nothing> {
        return Resource.error(throwable = Throwable(error))
    }
}