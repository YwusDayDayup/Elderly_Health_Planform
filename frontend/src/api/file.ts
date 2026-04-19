/*
file.ts
 此文件是负责文件上传相关接口封装，包括通用上传与头像上传。
*/

import { http, type Result } from './http'

export interface UploadResp {
  id: number
  url: string
  originalName: string
  size: number
}

export function uploadFile(file: File, bizType?: string) {
  const form = new FormData()
  form.append('file', file)
  if (bizType) {
    form.append('bizType', bizType)
  }
  return http.post<any, Result<UploadResp>>('/api/me/files', form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

export function uploadAvatar(file: File) {
  return uploadFile(file, 'avatar')
}
