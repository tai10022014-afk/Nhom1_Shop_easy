package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.repository

import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.Resource
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.UserDto
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.network.ApiService
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class UserRepository(
    private val api: ApiService = RetrofitClient.api
) {

    suspend fun fetchUsers(): Resource<List<UserDto>> = withContext(Dispatchers.IO) {
        try {
            Resource.Success(api.getUsers().users)
        } catch (e: HttpException) {
            val message = when (e.code()) {
                400 -> "Lỗi 400: Yêu cầu không hợp lệ"
                401 -> "Lỗi 401: Chưa được xác thực"
                500 -> "Lỗi 500: Lỗi máy chủ"
                else -> "Lỗi HTTP ${e.code()}"
            }
            Resource.Error(message)
        } catch (e: IOException) {
            Resource.Error("Không có kết nối mạng. Vui lòng kiểm tra Wi-Fi.")
        } catch (e: Exception) {
            Resource.Error("Đã xảy ra lỗi: ${e.message}")
        }
    }
}