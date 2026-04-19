/*
msg.ts
 此文件是负责站内消息相关接口封装，包括我的消息列表、未读数、标记已读与管理端发送。
*/

import { http, type Result } from './http'

export interface MsgVO {
  id: number
  msgType: string
  title: string
  content: string
  bizId?: string | null
  readFlag: number
  readTime?: string | null
  createTime: string
}

export interface UnreadCountVO {
  count: number
}

export interface MarkReadReq {
  msgId: number
}

export interface SendReq {
  receiverId: number
  msgType: string
  title: string
  content: string
  bizId?: string
}

export function myMsgList(readFlag?: number) {
  return http.get<any, Result<MsgVO[]>>('/api/me/messages', { params: { readFlag } })
}

export function myMsgDetail(id: number) {
  return http.get<any, Result<MsgVO>>(`/api/me/messages/${id}`)
}

export function unreadCount() {
  return http.get<any, Result<UnreadCountVO>>('/api/me/messages/unread-count')
}

export function markRead(id: number) {
  return http.patch<any, Result<null>>(`/api/me/messages/${id}/read`)
}

export function markReadAll() {
  return http.post<any, Result<null>>('/api/me/message-read-batches')
}

export function adminSend(data: SendReq) {
  return http.post<any, Result<null>>('/api/admin/messages', data)
}

export interface BroadcastReq {
  msgType: string
  title: string
  content: string
  bizId?: string
}

export interface AdminPageReq {
  pageNo: number
  pageSize: number
  receiverId?: number
  msgType?: string
  readFlag?: number
  startTime?: string
  endTime?: string
}

export interface PageResult<T> {
  total: number
  list: T[]
}

export interface AdminMsgVO {
  id: number
  senderId?: number | null
  receiverId?: number | null
  msgType: string
  title: string
  content: string
  bizId?: string | null
  readFlag: number
  readTime?: string | null
  createTime: string
}

export function adminBroadcast(data: BroadcastReq) {
  return http.post<any, Result<null>>('/api/admin/messages/broadcast', data)
}

export function adminMsgPage(data: { pageNo: number; pageSize: number }) {
  return http.get<any, Result<PageResult<AdminMsgVO>>>('/api/admin/messages', { params: data })
}

export function adminMsgDelete(id: number) {
  return http.delete<any, Result<null>>(`/api/admin/messages/${id}`)
}
