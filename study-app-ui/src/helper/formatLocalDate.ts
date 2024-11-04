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
