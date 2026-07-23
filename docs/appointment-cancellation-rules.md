# Quy tắc hủy lịch hẹn (ABC-151)

## Quyền thực hiện

- Chỉ người dùng có vai trò `ADMIN` hoặc `RECEPTIONIST` được hủy lịch.
- `DOCTOR` chỉ được xem lịch hẹn; các vai trò khác không có quyền truy cập thao tác hủy.

## Điều kiện được hủy

- Lịch hẹn phải tồn tại.
- Trạng thái hiện tại phải là `SCHEDULED`.
- Thời điểm hủy phải trước thời gian bắt đầu lịch hẹn.
- Lý do hủy là bắt buộc; sau khi loại bỏ khoảng trắng đầu/cuối phải dài từ 1 đến 500 ký tự.

Không được hủy lịch ở các trạng thái `CHECKED_IN`, `CALLED`, `COMPLETED`, `CANCELLED`
hoặc `NO_SHOW`. Lịch đã đến hay quá giờ hẹn phải được xử lý theo quy trình tiếp nhận hoặc
đánh dấu không đến.

## Kết quả

- Hủy lịch là thay đổi trạng thái, không xóa bản ghi.
- Hệ thống chuyển trạng thái từ `SCHEDULED` sang `CANCELLED`, lưu lý do đã chuẩn hóa và
  cập nhật `updatedAt`.
- Lịch đã hủy không xuất hiện trong hàng đợi và khung giờ của bác sĩ được giải phóng.
- Hai yêu cầu hủy đồng thời được tuần tự hóa; yêu cầu đến sau sẽ nhận lỗi xung đột.

## Hợp đồng API

`PATCH /api/v1/appointments/{id}/cancel`

```json
{
  "reason": "Bệnh nhân đề nghị đổi ngày khám"
}
```

- `200 OK`: hủy thành công, trả về lịch hẹn có trạng thái `CANCELLED`.
- `400 Bad Request`: mã định danh hoặc lý do không hợp lệ.
- `401 Unauthorized`: chưa đăng nhập.
- `403 Forbidden`: không có quyền hủy lịch.
- `404 Not Found`: không tìm thấy lịch hẹn.
- `409 Conflict`: lịch không còn ở trạng thái có thể hủy hoặc đã tới giờ hẹn.
