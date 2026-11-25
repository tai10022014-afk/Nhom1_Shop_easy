# ShopEasy — Đồ án Lập trình ứng dụng di động (71ITSE30603)

Ứng dụng mua sắm Android (Kotlin, View Binding). Kiến trúc **MVVM**, dữ liệu **offline-first**: UI chỉ đọc từ Room, API/Firebase chỉ làm mới Room.

## Kiến trúc

```
UI (Activity/Fragment/Adapter)  →  ViewModel (LiveData)  →  Repository  →  Room (nguồn dữ liệu duy nhất)
                                                                      ↘  Retrofit (FakeStore API) / Firebase Auth + Firestore
```

| Lớp | Thư mục | Ghi chú |
|---|---|---|
| UI | `ui/`, `adapter/` | Không gọi trực tiếp Firebase/Room |
| ViewModel | `viewmodel/` | `LoginViewModel`, `ShopViewModel`, `ProfileViewModel`, `UserListViewModel` |
| Repository | `repository/` | `AuthRepository`, `ShopRepository`, `UserRepository` |
| Room | `database/`, `model/` | `users`, `products`, `cart_items`, `favorites`, `orders` (DB version 4) |
| API | `network/` | `ShopApi` (FakeStore: `GET /products`, `GET /products/categories`), `ApiService` (DummyJSON) |
| Bảo mật | `data/SessionManager.kt` | EncryptedSharedPreferences (AES-256); R8 bật ở bản Release |

## Phân công theo chiều dọc (Vertical Slicing) — *điền tên/MSSV từng thành viên*

| Thành viên | Cụm tính năng End-to-End | UI → ViewModel → Room → API/Firebase |
|---|---|---|
| … | Đăng nhập / Đăng ký / Quên mật khẩu | `LoginActivity` → `LoginViewModel` → `SessionManager` → Firebase Auth + Firestore |
| … | Danh sách sản phẩm, tìm kiếm, lọc, sắp xếp | `HomeFragment` → `ShopViewModel` → `products` → FakeStore API |
| … | Giỏ hàng + Đặt hàng | `CartFragment` → `ShopViewModel` → `cart_items`, `orders` |
| … | Yêu thích + Chi tiết + Cá nhân | `FavoriteFragment`/`DetailActivity`/`ProfileFragment` → `favorites` |

## Build

1. Mở bằng Android Studio, để Gradle sync (cần `google-services.json` trong `app/`).
2. Bản Release ký số: sao chép `keystore.properties.example` → `keystore.properties`, điền thông tin, tạo keystore theo hướng dẫn trong file, rồi `Build > Generate Signed App Bundle / APK`.
3. Chạy kiểm thử đơn vị: `./gradlew testDebugUnitTest`.
4. Trước khi nén nộp: **Build > Clean Project**, xóa `/build` và `/.idea`; đặt tên `Nhom01_ShopEasy.zip`.
