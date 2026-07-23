# Quy tắc lịch quá hạn và đánh dấu không đến

Tài liệu này là tiêu chí nghiệm thu cho ABC-155, ABC-156 và ABC-157.

## Ngưỡng không đến

- Khoảng chờ cố định là **15 phút sau giờ hẹn**.
- Một lịch được xem là quá hạn khi còn ở trạng thái `SCHEDULED` và thời gian máy chủ
  thỏa `now >= appointmentAt + 15 phút`.
- Đúng tại mốc 15 phút đã đủ điều kiện. Ví dụ lịch 09:00 trở thành quá hạn lúc 09:15.
- `CHECKED_IN`, `CALLED`, `COMPLETED`, `CANCELLED` và `NO_SHOW` không được xem là quá hạn.
- Quá hạn là trạng thái suy ra để hiển thị, không phải trạng thái lưu thêm trong cơ sở dữ liệu.

## Đánh dấu không đến

- API: `PATCH /appointments/{id}/no-show`.
- Chỉ `ADMIN` và `RECEPTIONIST` được thực hiện.
- Thao tác là thủ công; hệ thống không tự động chuyển lịch quá hạn thành `NO_SHOW`.
- Bệnh nhân đến muộn vẫn có thể check-in khi lịch còn `SCHEDULED`.
- Thành công trả về lịch đã cập nhật với `status = NO_SHOW` và cập nhật `updatedAt`.
- Không tìm thấy lịch trả `404 Not Found`.
- Chưa đủ 15 phút hoặc lịch không còn `SCHEDULED` trả `409 Conflict`.
- Bản ghi được khóa khi đổi trạng thái để thao tác check-in, hủy và đánh dấu không đến
  không thể cùng ghi đè lẫn nhau.

## Hiển thị

- Giao diện hiển thị nhãn `Quá hạn` và số phút đã qua kể từ giờ hẹn.
- Có thể lọc riêng các lịch quá hạn.
- Đồng hồ giao diện cập nhật theo phút; máy chủ vẫn là nguồn quyết định cuối cùng khi
  người dùng xác nhận đánh dấu không đến.
- Sau khi đánh dấu thành công, lịch đổi thành `Không đến` và không còn trong bộ lọc quá hạn.
