/*
statistics.ts
 此文件是负责统计相关接口封装，包括概览与趋势数据。
*/

import { http, type Result } from './http'

export interface NameCountRow {
  name: string
  count: number
}

export interface SystemVO {
  systemCpuLoad?: number | null
  jvmUsedMemory: number
  jvmMaxMemory: number
  uptimeMs: number
  dbActive?: number | null
  dbIdle?: number | null
  dbTotal?: number | null
  dbThreadsAwaiting?: number | null
  redisOk?: boolean | null
}

export interface OverviewVO {
  totalUsers: number
  todayNewUsers: number
  roleDistribution: NameCountRow[]
  totalFiles: number
  totalFileSize: number
  fileTypeDistribution: NameCountRow[]
  totalMsgs: number
  totalUnreadMsgs: number
  totalOpLogs: number
  todayOpLogs: number
  todayOpLogFail: number
  system: SystemVO
}

export interface TrendDayVO {
  day: string
  userCount: number
  fileCount: number
  msgCount: number
  opLogCount: number
}

export interface TrendVO {
  days: number
  list: TrendDayVO[]
}

export function statisticsOverview() {
  return http.get<any, Result<OverviewVO>>('/api/statistics/overview')
}

export function statisticsTrend(days = 7) {
  return http.get<any, Result<TrendVO>>('/api/statistics/trend', { params: { days } })
}
