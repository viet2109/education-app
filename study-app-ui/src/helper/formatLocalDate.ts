// Hàm helper để định dạng LocalDateTime thành ngày, tháng, năm
export const formatLocalDate = (localDateTimeString: string) => {
  const localDateTime = new Date(localDateTimeString);

  const options = {
    year: "numeric" as const,
    month: "numeric" as const,
    day: "numeric" as const,
  };

  return localDateTime.toLocaleDateString("vi-VN", options);
};

export function formatLocalDateTime(localDateTime: string) {
  // Chuyển chuỗi LocalDateTime thành đối tượng Date
  const date = new Date(localDateTime);

  // Lấy các thành phần của ngày và giờ
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0"); // Tháng bắt đầu từ 0
  const day = String(date.getDate()).padStart(2, "0");
  const hours = String(date.getHours()).padStart(2, "0");
  const minutes = String(date.getMinutes()).padStart(2, "0");
  const seconds = String(date.getSeconds()).padStart(2, "0");

  // Format theo ý muốn
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

export function getLocalDateTime(date: Date) {
  // Lấy ngày tháng theo kiểu cục bộ và đảm bảo mỗi phần có 2 chữ số
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  const hour = String(date.getHours()).padStart(2, "0");
  const minute = String(date.getMinutes()).padStart(2, "0");
  const second = String(date.getSeconds()).padStart(2, "0");

  // Trả về thời gian theo định dạng ISO nhưng chỉ bao gồm ngày và giờ cục bộ
  return `${year}-${month}-${day}T${hour}:${minute}:${second}`;
}
