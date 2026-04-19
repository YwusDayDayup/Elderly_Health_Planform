/*
user.ts
 此文件是负责用户相关接口封装，包括登录、注册、退出登录、个人信息与管理端用户接口。
*/

import { http, type Result } from './http'

export interface LoginReq {
  username: string
  password: string
}

export interface RegisterReq {
  username: string
  password: string
  role?: string
  nickname: string
  email?: string | null
  phone?: string | null
  gender?: number | null
  birthday?: string | null
  bio?: string | null
  signature?: string | null
  region?: string | null
  city?: string | null
}

export interface LoginResp {
  token: string
}

export interface ProfileVO {
  id: number
  username: string
  nickname: string
  avatarUrl?: string | null
  email?: string | null
  phone?: string | null
  gender?: number | null
  birthday?: string | null
  bio?: string | null
  signature?: string | null
  region?: string | null
  city?: string | null
}

export interface UpdateProfileReq {
  nickname: string
  avatarUrl?: string | null
  email?: string | null
  phone?: string | null
  gender?: number | null
  birthday?: string | null
  bio?: string | null
  signature?: string | null
  region?: string | null
  city?: string | null
}

export interface UpdatePasswordReq {
  oldPassword: string
  newPassword: string
}

export interface RolesVO {
  role: string
}

export interface AdminUserVO {
  id: number
  username: string
  nickname: string
  avatarUrl?: string | null
  email?: string | null
  phone?: string | null
  gender?: number | null
  region?: string | null
  city?: string | null
  status: number
}

export interface PageResult<T> {
  total: number
  list: T[]
}

export interface AdminPageReq {
  pageNo: number
  pageSize: number
  keyword?: string
  status?: number
  role?: string
}

export interface AdminCreateReq {
  username: string
  password?: string
  nickname: string
  email?: string | null
  phone?: string | null
  gender?: number | null
  birthday?: string | null
  bio?: string | null
  signature?: string | null
  region?: string | null
  city?: string | null
  status: number
  role: string
}

export interface AdminUserDetailVO {
  id: number
  username: string
  nickname: string
  avatarUrl?: string | null
  email?: string | null
  phone?: string | null
  gender?: number | null
  birthday?: string | null
  bio?: string | null
  signature?: string | null
  region?: string | null
  city?: string | null
  status: number
  role: string
}

export interface AdminUpdateReq {
  nickname?: string
  avatarUrl?: string | null
  email?: string | null
  phone?: string | null
  gender?: number | null
  birthday?: string | null
  bio?: string | null
  signature?: string | null
  region?: string | null
  city?: string | null
  status?: number
  role?: string
}

export function login(data: LoginReq) {
  return http.post<any, Result<LoginResp>>('/api/public/login', data)
}

export function register(data: RegisterReq) {
  return http.post<any, Result<LoginResp>>('/api/public/register', data)
}

export function logout() {
  return http.post<any, Result<null>>('/api/public/logout')
}

export function getProfile() {
  return http.get<any, Result<ProfileVO>>('/api/me/profile')
}

export function updateProfile(data: UpdateProfileReq) {
  return http.put<any, Result<null>>('/api/me/profile', data)
}

export function updatePassword(data: UpdatePasswordReq) {
  return http.put<any, Result<null>>('/api/me/password', data)
}

export function getMyRoles() {
  return http.get<any, Result<RolesVO>>('/api/me/roles')
}

export function adminUserPage(data: AdminPageReq) {
  return http.post<any, Result<PageResult<AdminUserVO>>>('/api/admin/user/page', data)
}

export function adminUserCreate(data: AdminCreateReq) {
  return http.post<any, Result<null>>('/api/admin/user', data)
}

export function adminUserDetail(id: number) {
  return http.get<any, Result<AdminUserDetailVO>>(`/api/admin/user/${id}`)
}

export function adminUserUpdate(id: number, data: AdminUpdateReq) {
  return http.put<any, Result<null>>(`/api/admin/user/${id}`, data)
}

export function adminUserDelete(id: number) {
  return http.delete<any, Result<null>>(`/api/admin/user/${id}`)
}
