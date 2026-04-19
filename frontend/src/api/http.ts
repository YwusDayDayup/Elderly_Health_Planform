/*
http.ts
 此文件是负责前端HTTP请求封装，包括统一Result类型与请求实例导出。

注意：项目已有 `src/utils/request.ts` 作为统一 axios 实例（包含拦截器、错误提示、401跳转）。
为了避免两套请求封装不一致，这里直接复用该实例。
*/

import request from '@/utils/request'

export interface Result<T = any> {
  success: boolean
  code: string
  message: string
  data: T
}

export const http = request
