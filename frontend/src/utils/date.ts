import dayjs from 'dayjs'

/**
 * 将日期对象或日期字符串格式化为后端统一的日期时间字符串
 * @param date 日期对象或日期字符串
 * @param format 格式字符串，默认为 'YYYY-MM-DD HH:mm:ss'
 * @returns 格式化后的日期时间字符串
 */
export function formatDateTime(date: dayjs.ConfigType, format: string = 'YYYY-MM-DD HH:mm:ss'): string {
  if (!date) {
    return ''
  }
  return dayjs(date).format(format)
}

/**
 * 将日期对象或日期字符串格式化为后端统一的日期字符串 (无时间)
 * @param date 日期对象或日期字符串
 * @param format 格式字符串，默认为 'YYYY-MM-DD'
 * @returns 格式化后的日期字符串
 */
export function formatDate(date: dayjs.ConfigType, format: string = 'YYYY-MM-DD'): string {
  if (!date) {
    return ''
  }
  return dayjs(date).format(format)
}

/**
 * 将日期对象或日期字符串格式化为后端统一的时间字符串 (无日期)
 * @param date 日期对象或日期字符串
 * @param format 格式字符串，默认为 'HH:mm:ss'
 * @returns 格式化后的时间字符串
 */
export function formatTime(date: dayjs.ConfigType, format: string = 'HH:mm:ss'): string {
  if (!date) {
    return ''
  }
  return dayjs(date).format(format)
}
